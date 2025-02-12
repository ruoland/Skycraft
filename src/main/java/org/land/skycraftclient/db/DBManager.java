package org.land.skycraftclient.db;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import org.land.skycraftclient.SkyPlayer;
import org.land.skycraftclient.skill.PlayerSkill;
import org.land.skycraftclient.skill.PlayerSkillManager;
import org.land.skycraftclient.skill.Skill;
import org.land.skycraftclient.skill.Skills;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.UUID;

public class DBManager {
    private MysqlConnectionPoolDataSource dataSource;

    public void initDB() throws SQLException, IOException {
        dataSource = new MysqlConnectionPoolDataSource();
        dataSource.setServerName("localhost");
        dataSource.setPort(3306 );
        dataSource.setDatabaseName("skycraft_client");
        dataSource.setUser("root");
        dataSource.setPassword("135790");

        testDataSource();
        executeSetupQueries();
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
        }
    }

    private void executeSetupQueries() throws IOException, SQLException {
        String setup = readSetupFile();
        String[] queries = setup.split(";");
        for (String query : queries) {
            if (query.isBlank()) continue;
            executeUpdate(query);
        }

    }

    private String readSetupFile() throws IOException {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("dbsetup.sql")) {
            return new String("CREATE TABLE IF NOT EXISTS players\n" +
                    "(\n" +
                    "\tuuid  CHAR(36) NOT NULL,\n" +
                    "    level INT DEFAULT 1, \n" +
                    "    experience BIGINT DEFAULT 0,\n" +
                    "\tcurrent_party_id INT DEFAULT 0,\n" +
                    "    PRIMARY KEY (uuid)\n" +
                    ");\n" +
                    "\n" +
                    "CREATE TABLE IF NOT EXISTS friends\n" +
                    "(\n" +
                    "\tid INT AUTO_INCREMENT PRIMARY KEY,\n" +
                    "\tplayer_uuid CHAR(36) NOT NULL,\n" +
                    "    friend_uuid CHAR(36) NOT NULL,\n" +
                    "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n" +
                    "    FOREIGN KEY (player_uuid) REFERENCES players(uuid),\n" +
                    "    FOREIGN KEY (friend_uuid) REFERENCES players(uuid),\n" +
                    "    UNIQUE KEY unique_friendship (player_uuid, friend_uuid)\n" +
                    ");\n" +
                    "\n" +
                    "CREATE TABLE IF NOT EXISTS friend_requests(\n" +
                    "\tid INT AUTO_INCREMENT PRIMARY KEY,\n" +
                    "    sender_uuid CHAR(36) NOT NULL,\n" +
                    "    receiver_uuid CHAR(36) NOT NULL,\n" +
                    "    status ENUM('PENDING', 'ACCEPTED', 'REJECTED') NOT NULL DEFAULT 'PENDING',\n" +
                    "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n" +
                    "    FOREIGN KEY (sender_uuid) REFERENCES players(uuid),\n" +
                    "    FOREIGN KEY (receiver_uuid) REFERENCES players(uuid),\n" +
                    "    UNIQUE KEY unique_request (sender_uuid, receiver_uuid)\n" +
                    ");\n" +

                    "CREATE TABLE IF NOT EXISTS player_skills(\n" +
                    "\tplayer_uuid CHAR(36) NOT NULL,\n" +
                    "    skill_id INT NOT NULL,\n" +
                    "    skill_level INT NOT NULL,\n" +
                    "    skill_exp INT DEFAULT 0,\n" +
                    "\tPRIMARY KEY (player_uuid, skill_id),\n" +
                    "\tFOREIGN KEY (player_uuid) REFERENCES players(uuid),\n" +
                    "\tFOREIGN KEY (skill_id) REFERENCES skills(id)\n" +
                    ");");
        } catch (IOException e) {

            throw e;
        }
    }

    public void insertPlayer(UUID uuid, int level, long experience, Integer currentPartyId) {
        String query = "INSERT INTO players (uuid, level, experience, current_party_id) VALUES (?, ?, ?, ?)";
        try {
            executeUpdate(query, uuid.toString(), level, experience, currentPartyId);
        } catch (SQLException e) {
        }
    }


    public void insertPlayerSkill(UUID uuid, PlayerSkill skill) {
        String query = "INSERT INTO player_skills (player_uuid, skill_id, skill_level, skill_exp) VALUES (?, ?, ?, ?)";
        try {
            executeUpdate(query, uuid.toString(), skill.getSkill().getId(), skill.getLevel(), skill.getExperience());
        } catch (SQLException e) {
        }
    }
    public void updatePlayerSkill(UUID uuid, PlayerSkill skill) {
        String query = "UPDATE player_skills SET skill_level = ?, skill_exp = ? WHERE player_uuid = ? AND skill_id = ?";
        try {

            executeUpdate(query, skill.getLevel(), skill.getExperience(), uuid.toString(), skill.getSkill().getId());
        } catch (SQLException e) {
        }
    }
    public SkyPlayer getPlayer(UUID uuid) {
        String query = "SELECT * FROM players WHERE uuid = ?";
        try {
            SkyPlayer player = executeQuery(query, rs -> {
                if (rs.next()) {
                    return new SkyPlayer(
                            rs.getString("uuid"),
                            rs.getInt("level"),
                            rs.getLong("experience"),
                            rs.getInt("current_party_id")
                    );
                }
                return null;
            }, uuid.toString());

            if (player != null) {
                player.setPlayerSkills(getPlayerSkills(uuid));
            }

            return player;
        } catch (SQLException e) {
            return null;
        }
    }
    public PlayerSkillManager getPlayerSkills(UUID uuid) {
        String query = "SELECT * FROM player_skills WHERE player_uuid = ?";
        try {
            return executeQuery(query, rs -> {
                PlayerSkillManager playerSkillManager = new PlayerSkillManager(uuid);
                while (rs.next()) {
                    int skillId = rs.getInt("skill_id");
                    Skill skill = Skills.getById(skillId);
                    if (skill != null) {
                        PlayerSkill playerSkill = new PlayerSkill(skill)
                                .setLevel(rs.getInt("skill_level"))
                                .setExperience(rs.getInt("skill_exp"));
                        playerSkillManager.addSkill(skill);
                        playerSkillManager.getSkills().put(skill.getName(), playerSkill);
                    }
                }
                return playerSkillManager;
            }, uuid.toString());
        } catch (SQLException e) {
//            logError("Error retrieving player skills", e);
            return new PlayerSkillManager(uuid);
        }
    }


    private void executeUpdate(String query, Object... params) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            for (int i = 0; i < params.length; i++) {
                if (params[i] == null) {
                    stmt.setNull(i + 1, Types.NULL);
                } else {
                    stmt.setObject(i + 1, params[i]);
                }
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
