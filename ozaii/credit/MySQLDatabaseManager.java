package de.codingair.tradesystem.spigot.ozaii.credit;

import de.codingair.codingapi.files.ConfigFile;
import de.codingair.tradesystem.spigot.TradeSystem;
import de.codingair.tradesystem.spigot.utils.Supplier;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;

public class MySQLDatabaseManager {

    private static MySQLDatabaseManager instance;

    private final String url;
    private final String user;
    private final String password;

    private MySQLDatabaseManager() {
        ConfigFile file = TradeSystem.getInstance().getFileManager().getFile("Config");
        FileConfiguration config = file.getConfig();

        this.url = config.getString("ozaii.Database.MySQL.Connection_URL");
        this.user = config.getString("ozaii.Database.MySQL.User");

        String password = config.getString("ozaii.Database.MySQL.Password");
        if (password != null && password.equalsIgnoreCase("null")) password = null;

        this.password = password;
    }

    @NotNull
    public static MySQLDatabaseManager getInstance() {
        if (instance == null) instance = new MySQLDatabaseManager();
        return instance;
    }

    /**
     * Asenkron olarak bir bağlantı sağlayıcı döner.
     */
    @NotNull
    public static Supplier<Connection, SQLException> getConnection() {
        // Burada da loglama eklemek istersen:
        return () -> {
            try {
                return getInstance().buildConnection();
            } catch (SQLException ex) {
                TradeSystem.getInstance().getLogger().log(Level.SEVERE, "MySQL bağlantısı kurulamadı: " + ex.getMessage());
                throw ex;
            }
        };
    }

    /**
     * Veritabanına bağlanabiliyor mu test eder.
     */
    public static void checkConnection() {
        try (Connection conn = getConnection().get()) {
            // Bağlantı başarılı
            TradeSystem.getInstance().getLogger().info("MySQL veritabanına bağlantı başarılı.");
        } catch (SQLException e) {
            TradeSystem.getInstance().getLogger().log(Level.SEVERE, "MySQL veritabanına bağlantı sağlanamadı!", e);
        }
    }

    /**
     * Veritabanına bağlanabiliyor mu test eder ve boolean döner.
     */
    public static boolean checkConn() {
        try (Connection conn = getConnection().get()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            TradeSystem.getInstance().getLogger().log(Level.SEVERE, "MySQL veritabanına bağlantı sağlanamadı!", e);
            return false;
        }
    }


    /**
     * Gerçek bağlantıyı kurar.
     */
    @Nullable
    private Connection buildConnection() throws SQLException {
        if (url == null || user == null) {
            TradeSystem.getInstance().getLogger().severe("MySQL bağlantı bilgileri eksik (URL veya kullanıcı adı null).");
            return null;
        }

        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException ex) {
            TradeSystem.getInstance().getLogger().log(Level.SEVERE, "MySQL bağlantısı sırasında hata oluştu: " + ex.getMessage(), ex);
            throw ex;
        }
    }
}
