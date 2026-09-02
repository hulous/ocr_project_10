package com.ycyw.chatapi.configurations;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;

@ExtendWith(MockitoExtension.class)
class DatabaseCreationConfigurationTest {

  @Mock private Connection adminConnection;

  @Mock private PreparedStatement selectStatement;

  @Mock private PreparedStatement createStatement;

  @Mock private ResultSet resultSet;

  @Test
  void dataSourceCreatesPostgresDatabaseWhenMissing() throws SQLException {
    DataSourceProperties properties = new DataSourceProperties();
    properties.setUrl("jdbc:postgresql://localhost:5432/testdb?ssl=false");
    properties.setUsername("dbuser");
    properties.setPassword("secret");

    when(adminConnection.prepareStatement("SELECT 1 FROM pg_database WHERE datname = ?"))
        .thenReturn(selectStatement);
    when(selectStatement.executeQuery()).thenReturn(resultSet);
    when(resultSet.next()).thenReturn(false);
    when(adminConnection.prepareStatement("CREATE DATABASE \"testdb\""))
        .thenReturn(createStatement);
    when(createStatement.execute()).thenReturn(true);

    try (MockedStatic<java.sql.DriverManager> driverManager =
        mockStatic(java.sql.DriverManager.class)) {
      driverManager
          .when(
              () ->
                  java.sql.DriverManager.getConnection(
                      "jdbc:postgresql://localhost:5432/postgres?ssl=false", "dbuser", "secret"))
          .thenReturn(adminConnection);

      DatabaseCreationConfiguration configuration = new DatabaseCreationConfiguration();
      DataSource dataSource = configuration.dataSource(properties);

      assertNotNull(dataSource);
      verify(selectStatement).setString(1, "testdb");
      verify(createStatement).execute();
    }
  }

  @Test
  void dataSourceDoesNotCreatePostgresDatabaseWhenAlreadyExists() throws SQLException {
    DataSourceProperties properties = new DataSourceProperties();
    properties.setUrl("jdbc:postgresql://localhost:5432/existingdb");
    properties.setUsername("dbuser");
    properties.setPassword("secret");

    when(adminConnection.prepareStatement("SELECT 1 FROM pg_database WHERE datname = ?"))
        .thenReturn(selectStatement);
    when(selectStatement.executeQuery()).thenReturn(resultSet);
    when(resultSet.next()).thenReturn(true);

    try (MockedStatic<java.sql.DriverManager> driverManager =
        mockStatic(java.sql.DriverManager.class)) {
      driverManager
          .when(
              () ->
                  java.sql.DriverManager.getConnection(
                      "jdbc:postgresql://localhost:5432/postgres", "dbuser", "secret"))
          .thenReturn(adminConnection);

      DatabaseCreationConfiguration configuration = new DatabaseCreationConfiguration();
      DataSource dataSource = configuration.dataSource(properties);

      assertNotNull(dataSource);
      verify(selectStatement).setString(1, "existingdb");
      verify(adminConnection, never()).prepareStatement("CREATE DATABASE \"existingdb\"");
    }
  }
}
