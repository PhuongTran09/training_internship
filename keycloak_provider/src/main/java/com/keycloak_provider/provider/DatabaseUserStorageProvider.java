package com.keycloak_provider.provider;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserQueryProvider;
import org.keycloak.storage.user.UserRegistrationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.mindrot.jbcrypt.BCrypt;

import com.keycloak_provider.adapter.CustomUserAdapter;
import com.keycloak_provider.dto.UserInfo;

public class DatabaseUserStorageProvider implements UserStorageProvider,
        UserLookupProvider,
        UserQueryProvider,
        UserRegistrationProvider,
        CredentialInputValidator {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseUserStorageProvider.class);

    private final KeycloakSession session;
    private final ComponentModel model;

    private final String jdbcUrl;
    private final String dbUser;
    private final String dbPass;

    public DatabaseUserStorageProvider(KeycloakSession session, ComponentModel model) {
        this.session = session;
        this.model = model;
        this.jdbcUrl = model.get("dbUrl");
        this.dbUser = model.get("dbUser");
        this.dbPass = model.get("dbPassword");
    }



    @Override
    public UserModel getUserByUsername(RealmModel realm, String username) {
        try {
            UserInfo user = getUserInfo(
                    "SELECT username, email, first_name, last_name FROM users WHERE username = ?", username);
            if (user != null) {

                return new CustomUserAdapter(session, realm, model, user);
            } else {
                logger.warn("User '{}' not found in database", username);
            }
        } catch (SQLException e) {
            logger.error(" SQL error while fetching user '{}': {}", username, e.getMessage());
        } catch (Exception e) {
            logger.error(" Unexpected error: {}", e.getMessage());
        }
        return null;
    }

    @Override
    public UserModel getUserByEmail(RealmModel realm, String email) {
        try {
            UserInfo user = getUserInfo(
                    "SELECT username, email, first_name, last_name FROM users WHERE email = ?", email);
            if (user != null) {
                logger.info("Found user '{}'", email);
                return new CustomUserAdapter(session, realm, model, user);
            } else {
                logger.warn(" Email '{}' not found", email);
            }
        } catch (SQLException e) {
            logger.error("SQL error while fetching email '{}': {}", email, e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage());
        }
        return null;
    }

    @Override
    public UserModel getUserById(RealmModel realm, String id) {
        String username = StorageId.externalId(id);

        try {
            UserModel user = getUserByUsername(realm, username);
            if (user == null) {
                logger.warn("No user found with ID '{}'", id);
            }
            return user;
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage());
            return null;
        }
    }

    @Override

    public boolean isValid(RealmModel realm, UserModel user, CredentialInput input) {
        if (!supportsCredentialType(input.getType())) {

            return false;
        }
        try {
            String rawPassword = input.getChallengeResponse();
            boolean isValid = validatePassword(user.getUsername(), rawPassword);
            logger.info("Password validation for user {}: {}", user.getUsername(), isValid);
            return isValid;
        } catch (SQLException e) {
            logger.error("Error validating password: {}", e.getMessage());
            return false;
        }
    }

    private boolean validatePassword(String username, String rawPassword) throws SQLException {
        String sql = "SELECT password FROM users WHERE username = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String hashedPasswordFromDb = rs.getString("password");
                boolean result = BCrypt.checkpw(rawPassword, hashedPasswordFromDb);
                logger.info("Password match for user {}: {}", username, result);
                return result;
            } else {
                logger.warn("No password found for user: {}", username);
            }
        }
        return false;
    }

    private UserInfo getUserInfo(String query, String param) throws SQLException {
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, param);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new UserInfo(
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("first_name"),
                        rs.getString("last_name")
                );
            }
        }
        return null;
    }

    private Connection getConnection() throws SQLException {
        logger.info("Connecting to DB: {}", jdbcUrl);
        return DriverManager.getConnection(jdbcUrl, dbUser, dbPass);
    }

    @Override
    public boolean supportsCredentialType(String credentialType) {
        return PasswordCredentialModel.TYPE.equals(credentialType);
    }

    @Override
    public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
        return supportsCredentialType(credentialType);
    }

    @Override
    public void close() {
        logger.info("Closing user storage provider");
    }

    @Override
    public Stream<UserModel> searchForUserByUserAttributeStream(RealmModel realm, String attrName, String attrValue) {
        return Stream.empty();
    }

    @Override
    public Stream<UserModel> getGroupMembersStream(RealmModel realm, GroupModel group, Integer firstResult, Integer maxResults) {

        return Stream.empty();
    }

    @Override
    public Stream<UserModel> searchForUserStream(RealmModel realm, Map<String, String> params, Integer firstResult, Integer maxResults) {
        String search = params.getOrDefault("search", "");
        List<UserModel> users = new ArrayList<>();
        String sql = "SELECT username, email, first_name, last_name FROM users WHERE username LIKE ? OR email LIKE ? LIMIT ? OFFSET ?";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + search + "%");
            stmt.setString(2, "%" + search + "%");
            stmt.setInt(3, maxResults != null ? maxResults : Integer.MAX_VALUE);
            stmt.setInt(4, firstResult != null ? firstResult : 0);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                UserInfo userInfo = new UserInfo(
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("first_name"),
                        rs.getString("last_name")
                );
                users.add(new CustomUserAdapter(session, realm, model, userInfo));
            }
        } catch (Exception e) {
            logger.error("Error: {}", e.getMessage());
        }
        return users.stream();
    }

    @Override
    public UserModel addUser(RealmModel realm, String username) {

        String sql = "INSERT INTO users (username) VALUES (?)";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                UserInfo userInfo = new UserInfo(username, null, null, null);
                logger.info("User '{}' successfully added to DB", username);
                return new CustomUserAdapter(session, realm, model, userInfo);
            } else {
                logger.warn("Failed to insert user '{}' into DB", username);
            }

        } catch (SQLException e) {
            logger.error("SQL error while adding user '{}': {}", username, e.getMessage(), e);
        }
        return null;
    }

    @Override
    public boolean removeUser(RealmModel realmModel, UserModel userModel) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            long persistenceId;
            try {
                persistenceId = Long.parseLong(StorageId.externalId(userModel.getId()));
            } catch (NumberFormatException e) {
                logger.error("Invalid ID format: {}", userModel.getId(), e);
                return false;
            }

            stmt.setLong(1, persistenceId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("User successfully removed with ID: {}", userModel.getId());
                return true;
            } else {
                logger.warn("No user found with ID: {} to remove", userModel.getId());
            }
        } catch (SQLException e) {
            logger.error("Error removing user", e);
        }
        return false;
    }

    @Override
    public int getUsersCount(RealmModel realm) {
        String sql = "SELECT COUNT(*) FROM users";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            logger.error("Error: {}", e.getMessage(), e);
        }
        return 0;
    }
}
