package com.example.keycloak_provider.provider;

import java.util.List;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.storage.UserStorageProviderFactory;

public class DatabaseUserStorageProviderFactory implements UserStorageProviderFactory<DatabaseUserStorageProvider> {

    public static final String PROVIDER_ID = "db-user-storage-provider";

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public DatabaseUserStorageProvider create(KeycloakSession session, ComponentModel model) {
        return new DatabaseUserStorageProvider(session, model);
    }

    @Override
    public void init(org.keycloak.Config.Scope config) {
        System.out.println("DatabaseUserStorageProviderFactory.init()");
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        System.out.println("DatabaseUserStorageProviderFactory.postInit()");
    }

    @Override
    public void close() {
        System.out.println("DatabaseUserStorageProviderFactory.closed()");
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        ProviderConfigProperty dbUrl = new ProviderConfigProperty();
        dbUrl.setName("dbUrl");
        dbUrl.setLabel("Database JDBC URL");
        dbUrl.setType(ProviderConfigProperty.STRING_TYPE);


        ProviderConfigProperty dbUser = new ProviderConfigProperty();
        dbUser.setName("dbUser");
        dbUser.setLabel("Database User");
        dbUser.setType(ProviderConfigProperty.STRING_TYPE);

        ProviderConfigProperty dbPass = new ProviderConfigProperty();
        dbPass.setName("dbPassword");
        dbPass.setLabel("Database Password");
        dbPass.setType(ProviderConfigProperty.PASSWORD);

        return List.of(dbUrl, dbUser, dbPass);
    }
}
