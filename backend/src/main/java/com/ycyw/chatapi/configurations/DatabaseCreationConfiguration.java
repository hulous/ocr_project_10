package com.ycyw.chatapi.configurations;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration(proxyBeanMethods = false)
public class DatabaseCreationConfiguration {
  private static final Pattern POSTGRES_URL_PATTERN = Pattern.compile(
    "^jdbc:postgresql://([^/]+)/([^?]+)(?:\\?(.*))?$"
  );

  @Bean
  public DataSource dataSource(DataSourceProperties properties) {
    String url = properties.determineUrl();
    if (isPostgresUrl(url)) {
      createDatabaseIfMissing(url, properties.determineUsername(), properties.determinePassword());
    }

    return properties.initializeDataSourceBuilder().build();
  }

  private static boolean isPostgresUrl(String url) {
    return StringUtils.hasText(url) && url.startsWith("jdbc:postgresql:");
  }

  private static void createDatabaseIfMissing(String url, String username, String password) {
    if (!StringUtils.hasText(url)) {
      return;
    }

    String databaseName = extractDatabaseName(url);
    if (!StringUtils.hasText(databaseName) || "postgres".equalsIgnoreCase(databaseName) || "template1".equalsIgnoreCase(databaseName)) {
      return;
    }

    String adminUrl = createAdminUrl(url);
    try (Connection adminConnection = DriverManager.getConnection(adminUrl, username, password)) {
      if (!databaseExists(adminConnection, databaseName)) {
        createDatabase(adminConnection, databaseName);
      }
    } catch (SQLException ex) {
      throw new IllegalStateException("Unable to create PostgreSQL database '" + databaseName + "'.", ex);
    }
  }

  private static boolean databaseExists(Connection connection, String databaseName) throws SQLException {
    try (PreparedStatement stmt = connection.prepareStatement("SELECT 1 FROM pg_database WHERE datname = ?")) {
      stmt.setString(1, databaseName);

      try (ResultSet rs = stmt.executeQuery()) {
        return rs.next();
      }
    }
  }

  private static void createDatabase(Connection connection, String databaseName) throws SQLException {
    String quotedName = "\"" + databaseName.replace("\"", "\"\"") + "\"";

    try (PreparedStatement stmt = connection.prepareStatement("CREATE DATABASE " + quotedName)) {
      stmt.execute();
    }
  }

  private static String extractDatabaseName(String url) {
    Matcher matcher = POSTGRES_URL_PATTERN.matcher(url);
    if (!matcher.matches()) {
      return null;
    }

    return matcher.group(2);
  }

  private static String createAdminUrl(String url) {
    Matcher matcher = POSTGRES_URL_PATTERN.matcher(url);
    if (!matcher.matches()) {
      return url;
    }

    String hostAndPort = matcher.group(1);
    String query = matcher.group(3);

    return "jdbc:postgresql://" + hostAndPort + "/postgres" + (query == null ? "" : "?" + query);
  }
}
