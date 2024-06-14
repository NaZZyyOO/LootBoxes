package ink.anh.nazzyyoo.lootboxes.lootbox;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ink.anh.api.database.AbstractTable;
import ink.anh.api.LibraryManager;
import ink.anh.api.database.TableField;

import org.bukkit.Location;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LootBoxTable extends AbstractTable<LootBox> {

    private static final String TABLE_NAME = "LootBox";
    private final Gson gson = new Gson();


    public LootBoxTable(LibraryManager manager) {
        super(manager, TABLE_NAME);
    }

    @Override
    protected void initialize() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS " + dbName + " (" +
                "hash_code INTEGER PRIMARY KEY," +
                "location TEXT," +
                "lootTableName VARCHAR(255)," +
                "lootedPlayers TEXT," +
                "cooldown INTEGER" +
                ");";
        executeTransaction(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(createTableSQL)) {
                ps.execute();
            }
        }, "Failed to create LootBox table");
    }

    @Override
    public void insert(LootBox lootBox) {
        String insertSQL = "INSERT INTO " + dbName + " (hash_code, location, lootTableName, lootedPlayers, cooldown) VALUES (?, ?, ?, ?, ?);";
        executeTransaction(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(insertSQL)) {
                ps.setInt(1, lootBox.getLocation().hashCode());
                ps.setString(2, serializeLocation(lootBox.getLocation()));
                ps.setString(3, lootBox.getLootTableName());
                ps.setString(4, serializeLootedPlayers(lootBox.getLootedPlayers()));
                ps.setInt(5, lootBox.getCooldown());
                ps.executeUpdate();
                System.out.println("Successfully added LootBox with hashCode: " + lootBox.getLocation().hashCode());
            }
        }, "Failed to insert LootBox");
    }

    @Override
    public void update(LootBox lootBox) {
        String updateSQL = "UPDATE " + dbName + " SET location = ?, lootTableName = ?, lootedPlayers = ?, cooldown = ? WHERE hash_code = ?;";
        executeTransaction(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(updateSQL)) {
                ps.setString(1, serializeLocation(lootBox.getLocation()));
                ps.setString(2, lootBox.getLootTableName());
                ps.setString(3, serializeLootedPlayers(lootBox.getLootedPlayers()));
                ps.setInt(4, lootBox.getCooldown());
                ps.setInt(5, lootBox.getLocation().hashCode());
                ps.executeUpdate();
            }
        }, "Failed to update LootBox");
    }

    @Override
    public <K> void updateField(TableField<K> tableField) {
        String updateSQL = "UPDATE " + dbName + " SET " + tableField.getFieldName() + " = ? WHERE hash_code = ?;";
        executeTransaction(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(updateSQL)) {
                ps.setObject(1, tableField.getFieldValue());
                ps.setString(2, tableField.getKey().toString());
                ps.executeUpdate();
            }
        }, "Failed to update field: " + tableField.getFieldName());
    }

    @Override
    public void delete(LootBox lootBox) {
        String deleteSQL = "DELETE FROM " + dbName + " WHERE hash_code = ?;";
        executeTransaction(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(deleteSQL)) {
                ps.setInt(1, lootBox.getLocation().hashCode());
                int rowsAffected = ps.executeUpdate();
                if (rowsAffected == 0) {
                    System.err.println("Failed to delete LootBox with hashCode: " + lootBox.getLocation().hashCode() + ". No rows affected.");
                } else {
                    System.out.println("Successfully deleted LootBox with hashCode: " + lootBox.getLocation().hashCode());
                }
            }
        }, "Failed to delete LootBox");
    }

    public LootBox getLootBox(int hashCode) {
        String selectSQL = "SELECT * FROM " + dbName + " WHERE hash_code = ?;";
        LootBox[] lootBox = {null};

        executeTransaction(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(selectSQL)) {
                ps.setInt(1, hashCode);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Location location = deserializeLocation(rs.getString("location"));
                        String lootTableName = rs.getString("lootTableName");
                        String lootedPlayersStr = rs.getString("lootedPlayers");
                        int cooldown = rs.getInt("cooldown");
                        Map<UUID, Integer> lootedPlayers = deserializeLootedPlayers(lootedPlayersStr);
                        lootBox[0] = new LootBox(location, lootTableName, cooldown, lootedPlayers);
                    }
                }
            }
        }, "Failed to retrieve LootBox with hash_code: " + hashCode);

        return lootBox[0];
    }

    public Map<Integer, LootBox> getAllLootBoxes() {
        String selectSQL = "SELECT * FROM " + dbName + ";";
        Map<Integer, LootBox> lootBoxes = new HashMap<>();

        executeTransaction(conn -> {
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(selectSQL)) {
                 while (rs.next()) {
                    Location location = deserializeLocation(rs.getString("location"));
                    int hashCode = location.hashCode();
                    String lootTableName = rs.getString("lootTableName");
                    String lootedPlayersStr = rs.getString("lootedPlayers");
                    int cooldown = rs.getInt("cooldown");
                    Map<UUID, Integer> lootedPlayers = deserializeLootedPlayers(lootedPlayersStr);
                    LootBox lootBox = new LootBox(location, lootTableName, cooldown, lootedPlayers);
                    lootBoxes.put(hashCode, lootBox);
                }
            }
        }, "Failed to retrieve all LootBoxes");
        
        return lootBoxes;
    }

    private String serializeLootedPlayers(Map<UUID, Integer> lootedPlayers) {
        return gson.toJson(lootedPlayers);
    }

    private Map<UUID, Integer> deserializeLootedPlayers(String lootedPlayersStr) {
        Type mapType = new TypeToken<Map<UUID, Integer>>() {}.getType();
        return gson.fromJson(lootedPlayersStr, mapType);
    }

    private String serializeLocation(Location location) {
        if (location == null) {
            return null;
        }
        return gson.toJson(location.serialize());
    }

    private Location deserializeLocation(String locationString) {
        if (locationString == null || locationString.isEmpty()) {
            return null;
        }
        Type type = new TypeToken<Map<String, Object>>() {}.getType();
        Map<String, Object> locationMap = gson.fromJson(locationString, type);
        return Location.deserialize(locationMap);
    }
}
