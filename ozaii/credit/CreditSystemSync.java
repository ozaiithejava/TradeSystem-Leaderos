package de.codingair.tradesystem.spigot.ozaii.credit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CreditSystemSync {

    private static volatile CreditSystemSync instance;

    private CreditSystemSync() {}

    public static CreditSystemSync getInstance() {
        if (instance == null) {
            synchronized (CreditSystemSync.class) {
                if (instance == null) {
                    instance = new CreditSystemSync();
                }
            }
        }
        return instance;
    }

    public int getCreditSync(String playerName) throws SQLException {
        try (Connection conn = MySQLDatabaseManager.getConnection().get()) {
            PreparedStatement ps = conn.prepareStatement("SELECT credit FROM Accounts WHERE username = ?");
            ps.setString(1, playerName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("credit");
            } else {
                createAccountIfNotExists(conn, playerName);
                return 0;
            }
        }
    }

    public boolean setCreditSync(String playerName, int credit) throws SQLException {
        try (Connection conn = MySQLDatabaseManager.getConnection().get()) {
            createAccountIfNotExists(conn, playerName);
            PreparedStatement ps = conn.prepareStatement("UPDATE Accounts SET credit = ? WHERE username = ?");
            ps.setInt(1, Math.max(0, credit));
            ps.setString(2, playerName);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean addCreditSync(String playerName, int credit) throws SQLException {
        if (credit <= 0) return false;

        try (Connection conn = MySQLDatabaseManager.getConnection().get()) {
            createAccountIfNotExists(conn, playerName);
            PreparedStatement ps = conn.prepareStatement("UPDATE Accounts SET credit = credit + ? WHERE username = ?");
            ps.setInt(1, credit);
            ps.setString(2, playerName);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean removeCreditSync(String playerName, int credit) throws SQLException {
        if (credit <= 0) return false;

        try (Connection conn = MySQLDatabaseManager.getConnection().get()) {
            int current = getCreditSync(playerName);
            if (current < credit) return false;

            PreparedStatement ps = conn.prepareStatement("UPDATE Accounts SET credit = credit - ? WHERE username = ?");
            ps.setInt(1, credit);
            ps.setString(2, playerName);
            return ps.executeUpdate() > 0;
        }
    }

    private void createAccountIfNotExists(Connection conn, String playerName) throws SQLException {
        PreparedStatement check = conn.prepareStatement("SELECT username FROM Accounts WHERE username = ?");
        check.setString(1, playerName);
        ResultSet rs = check.executeQuery();

        if (!rs.next()) {
            PreparedStatement insert = conn.prepareStatement("INSERT INTO Accounts (username, credit) VALUES (?, 0)");
            insert.setString(1, playerName);
            insert.executeUpdate();
        }
    }
}
