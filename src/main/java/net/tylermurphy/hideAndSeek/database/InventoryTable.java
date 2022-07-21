package net.tylermurphy.hideAndSeek.database;

import net.tylermurphy.hideAndSeek.Main;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.UUID;

public class InventoryTable {

    private final Database database;

    protected InventoryTable(Database database) {

        String sql = "CREATE TABLE IF NOT EXISTS hs_inventory (\n"
                + "	uuid BINARY(16) NOT NULL,\n"
                + "	inventory TEXT NOT NULL,\n"
                + "	PRIMARY KEY (uuid)\n"
                + ");";

        try(Connection connection = database.connect(); Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            Main.getInstance().getLogger().severe("SQL Error: " + e.getMessage());
            e.printStackTrace();
        }

        this.database = database;
    }

    @Nullable
    public ItemStack[] getInventory(@NotNull UUID uuid) {
        String sql = "SELECT * FROM hs_inventory WHERE uuid = ?;";
        try(Connection connection = database.connect(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBytes(1, database.encodeUUID(uuid));
            ResultSet rs  = statement.executeQuery();
            if (rs.next()) {
                String data = rs.getString("inventory");
                if(data == null) return null;
                return itemStackArrayFromBase64(data);
            }
            rs.close();
        } catch (SQLException e) {
            Main.getInstance().getLogger().severe("SQL Error: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Main.getInstance().getLogger().severe("IO Error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean saveInventory(@NotNull UUID uuid, @NotNull ItemStack[] itemArray) {
        String sql = "INSERT OR REPLACE INTO hs_inventory (uuid, inventory) VALUES (?,?)";
        String data = itemStackArrayToBase64(itemArray);
        try(Connection connection = database.connect(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBytes(1, database.encodeUUID(uuid));
            statement.setString(2, data);
            statement.execute();
            statement.close();
            return true;
        } catch (SQLException e) {
            Main.getInstance().getLogger().severe("SQL Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private String itemStackArrayToBase64(ItemStack[] itemArray) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            dataOutput.writeObject(itemArray);

            dataOutput.close();

            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Error whilst saving items, Please contact the developer", e);
        }
    }

    private ItemStack[] itemStackArrayFromBase64(String data) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);

            ItemStack[] itemArray = (ItemStack[]) dataInput.readObject();

            dataInput.close();
            return itemArray;
        } catch (ClassNotFoundException e) {
            throw new IOException("Error whilst loading items, Please contact the developer", e);
        }
    }

}
