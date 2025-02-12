package org.land.skycraftclient.db;

import org.land.skycraftclient.SkyPlayer;

import org.land.skycraftclient.skill.PlayerSkill;
import org.land.skycraftclient.skill.PlayerSkillManager;
import org.land.skycraftclient.skill.Skill;
import org.land.skycraftclient.skill.Skills;

import java.sql.*;
import java.util.UUID;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.util.logging.Logger;

public class DBManager {
    private static final Logger LOGGER = Logger.getLogger(DBManager.class.getName());
    private HikariDataSource dataSource;
    private final String dbHost;
    private final int dbPort;
    private final String dbName;
    private final String dbUser;
    private final String dbPassword;

    public DBManager(String dbHost, int dbPort, String dbName, String dbUser, String dbPassword) {
        this.dbHost = dbHost;
        this.dbPort = dbPort;
        this.dbName = dbName;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
    }

    public void initDB() throws SQLException {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(String.format("jdbc:mysql://%s:%d/%s", dbHost, dbPort, dbName));
        config.setUsername(dbUser);
        config.setPassword(dbPassword);
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");

        // HikariCP 설정 (최적화)
        config.setMaximumPoolSize(10); // 최대 커넥션 수
        config.setMinimumIdle(5); // 최소 유휴 커넥션 수
        config.setMaxLifetime(1800000); // 30분 (커넥션 최대 생존 시간)
        config.setConnectionTimeout(30000); // 30초 (커넥션 획득 타임아웃)
        config.setIdleTimeout(600000); // 10분 (유휴 커넥션 타임아웃)
        config.setPoolName("SkycraftClientPool");

        dataSource = new HikariDataSource(config);

        testDataSource();
        executeSetupQueries();
    }

    public void close() {
        if (dataSource != null) {
            dataSource.close();
            LOGGER.info("DataSource closed.");
        }
    }

    private Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("DataSource is not initialized");
        }
        return dataSource.getConnection();
    }

    private void testDataSource() throws SQLException {
        try (Connection conn = getConnection()) {
            if (!conn.isValid(1)) {
                throw new SQLException("Could not establish database connection.");
            }
            LOGGER.info("Database connection test successful.");
        }
    }

    private void executeSetupQueries() throws SQLException {
        String setup = getSetupQueries();
        String[] queries = setup.split(";");
        for (String query : queries) {
            if (query.isBlank()) continue;
            try {
                executeUpdate(query);
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Failed to execute setup query: " + query, e);
                throw e; // 쿼리 실패 시 initDB()에서 롤백할 수 있도록 예외를 다시 던집니다.
            }
        }
        LOGGER.info("Database setup queries executed.");
    }

    private String getSetupQueries() {
        return "CREATE TABLE IF NOT EXISTS players (\n" +
                "    uuid CHAR(36) NOT NULL,\n" +
                "    level INT DEFAULT 1, \n" +
                "    experience BIGINT DEFAULT 0,\n" +
                "    current_party_id INT DEFAULT 0,\n" +
                "    PRIMARY KEY (uuid)\n" +
                ");\n" +
                "\n" +
                "CREATE TABLE IF NOT EXISTS friends (\n" +
                "    id INT AUTO_INCREMENT PRIMARY KEY,\n" +
                "    player_uuid CHAR(36) NOT NULL,\n" +
                "    friend_uuid CHAR(36) NOT NULL,\n" +
                "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n" +
                "    FOREIGN KEY (player_uuid) REFERENCES players(uuid),\n" +
                "    FOREIGN KEY (friend_uuid) REFERENCES players(uuid),\n" +
                "    UNIQUE KEY unique_friendship (player_uuid, friend_uuid)\n" +
                ");\n" +
                "\n" +
                "CREATE TABLE IF NOT EXISTS friend_requests (\n" +
                "    id INT AUTO_INCREMENT PRIMARY KEY,\n" +
                "    sender_uuid CHAR(36) NOT NULL,\n" +
                "    receiver_uuid CHAR(36) NOT NULL,\n" +
                "    status ENUM('PENDING', 'ACCEPTED', 'REJECTED') NOT NULL DEFAULT 'PENDING',\n" +
                "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n" +
                "    FOREIGN KEY (sender_uuid) REFERENCES players(uuid),\n" +
                "    FOREIGN KEY (receiver_uuid) REFERENCES players(uuid),\n" +
                "    UNIQUE KEY unique_request (sender_uuid, receiver_uuid)\n" +
                ");\n" +
                "\n" +
                "CREATE TABLE IF NOT EXISTS player_skills (\n" +
                "    player_uuid CHAR(36) NOT NULL,\n" +
                "    skill_type VARCHAR(50) NOT NULL,\n" +
                "    skill_level INT NOT NULL,\n" +
                "    skill_exp INT DEFAULT 0,\n" +
                "    PRIMARY KEY (player_uuid, skill_type),\n" +
                "    FOREIGN KEY (player_uuid) REFERENCES players(uuid)\n" +
                ");";
    }

    public void insertPlayer(UUID uuid, int level, long experience, Integer currentPartyId) {
        String query = "INSERT INTO players (uuid, level, experience, current_party_id) VALUES (?, ?, ?, ?)";
        try {
            executeUpdate(query, uuid.toString(), level, experience, currentPartyId);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Failed to insert player: " + uuid, e);
        }
    }

    public void insertPlayerSkill(UUID uuid, PlayerSkill skill) {
        String query = "INSERT INTO player_skills (player_uuid, skill_type, skill_level, skill_exp) VALUES (?, ?, ?, ?)";
        try {
            executeUpdate(query, uuid.toString(), skill.getSkill().getName(), skill.getLevel(), skill.getExperience());
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Failed to insert player skill for player: " + uuid + ", skill: " + skill.getSkill().getName(), e);
        }
    }

    public void updatePlayerSkill(UUID uuid, PlayerSkill skill) {
        String query = "UPDATE player_skills SET skill_level = ?, skill_exp = ? WHERE player_uuid = ? AND skill_type = ?";
        try {
            executeUpdate(query, skill.getLevel(), skill.getExperience(), uuid.toString(), skill.getSkill().getName());
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Failed to update player skill for player: " + uuid + ", skill: " + skill.getSkill().getName(), e);
        }
    }

    public SkyPlayer getPlayer(UUID uuid) {
        String query = "SELECT * FROM players WHERE uuid = ?";
        try {
            return executeQuery(query, rs -> {
                if (rs.next()) {
                    SkyPlayer player = new SkyPlayer(
                            rs.getString("uuid"),
                            rs.getInt("level"),
                            rs.getLong("experience"),
                            rs.getInt("current_party_id")
                    );
                    player.setPlayerSkills(getPlayerSkills(uuid));
                    return player;
                }
                return null;
            }, uuid.toString());
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Failed to get player: " + uuid, e);
            return null;
        }
    }

    public PlayerSkillManager getPlayerSkills(UUID uuid) {
        String query = "SELECT * FROM player_skills WHERE player_uuid = ?";
        try {
            return executeQuery(query, rs -> {
                PlayerSkillManager playerSkillManager = new PlayerSkillManager(uuid);
                while (rs.next()) {
                    String skillTypeStr = rs.getString("skill_type");
                    Skill skill = Skills.getByType(skillTypeStr); // Skills에서 Skill 객체 가져오기

                    if (skill != null) {
                        PlayerSkill playerSkill = new PlayerSkill(skill)
                                .setLevel(rs.getInt("skill_level"))
                                .setExperience(rs.getInt("skill_exp"));
                        playerSkillManager.addSkill(skill);
                    } else {
                        LOGGER.warning("Skill with type " + skillTypeStr + " not found.");
                    }
                }
                return playerSkillManager;
            }, uuid.toString());
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Failed to get player skills for player: " + uuid, e);
            return new PlayerSkillManager(uuid);
        }
    }

    private void executeUpdate(String query, Object... params) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            stmt.executeUpdate();
        }
    }

    private <T> T executeQuery(String query, ResultSetHandler<T> handler, Object... params) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                return handler.handle(rs);
            }
        }
    }

    @FunctionalInterface
    private interface ResultSetHandler<T> {
        T handle(ResultSet rs) throws SQLException;
    }
}
