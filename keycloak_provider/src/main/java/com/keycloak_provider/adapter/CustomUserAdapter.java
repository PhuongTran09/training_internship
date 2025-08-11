package com.keycloak_provider.adapter;

import org.keycloak.component.ComponentModel;
import org.keycloak.credential.LegacyUserCredentialManager;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.SubjectCredentialManager;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.adapter.AbstractUserAdapter;

import com.keycloak_provider.dto.UserInfo;

public class CustomUserAdapter extends AbstractUserAdapter {

    private final String username;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final String id;

    public CustomUserAdapter(KeycloakSession session, RealmModel realm, ComponentModel model, UserInfo userInfo) {
        super(session, realm, model);
        this.username = userInfo.getUsername();
        this.email = userInfo.getEmail();
        this.firstName = userInfo.getFirstName();
        this.lastName = userInfo.getLastName();
        this.id = StorageId.keycloakId(model, this.username);
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getEmail() {
        return this.email;
    }

    @Override
    public String getFirstName() {
        return this.firstName;
    }

    @Override
    public String getLastName() {
        return this.lastName;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public SubjectCredentialManager credentialManager() {
        return new LegacyUserCredentialManager(this.session, this.realm, this);
    }
}
