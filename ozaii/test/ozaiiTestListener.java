package de.codingair.tradesystem.spigot.ozaii.test;

import de.codingair.codingapi.files.ConfigFile;
import de.codingair.tradesystem.spigot.TradeSystem;
import de.codingair.tradesystem.spigot.ozaii.credit.CreditSystemSync;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.sql.SQLException;

public class ozaiiTestListener implements Listener {
    ConfigFile file = TradeSystem.getInstance().getFileManager().getFile("Config");
    FileConfiguration config = file.getConfig();

    boolean debug = config.getBoolean("ozaii.listener.debug");

    @EventHandler
    public void playerChatEvent(AsyncPlayerChatEvent event) {

        if (!debug) return;
        Player player = event.getPlayer();
        String message = event.getMessage();
        if (!message.startsWith("!")) return;
        if (!player.isOp()) return;

        String[] args = message.split(" ");
        String command = args[0].toLowerCase();

        try {
            switch (command) {
                case "!get":
                    int credit = CreditSystemSync.getInstance().getCreditSync(player.getName());
                    player.sendMessage(ChatColor.GREEN + "Kredin: " + credit);
                    break;

                case "!add":
                    if (args.length < 2 || !isNumeric(args[1])) {
                        player.sendMessage(ChatColor.RED + "Kullanım: !add <pozitif miktar>");
                        return;
                    }
                    int addAmount = Integer.parseInt(args[1]);
                    if (addAmount <= 0) {
                        player.sendMessage(ChatColor.RED + "Pozitif bir miktar girmeniz gerekiyor.");
                        return;
                    }
                    if (CreditSystemSync.getInstance().addCreditSync(player.getName(), addAmount)) {
                        player.sendMessage(ChatColor.YELLOW + "Krediye +" + addAmount + " eklendi.");
                    } else {
                        player.sendMessage(ChatColor.RED + "Hesabınız bulunamadı veya kredi eklenemedi.");
                    }
                    break;

                case "!remove":
                    if (args.length < 2 || !isNumeric(args[1])) {
                        player.sendMessage(ChatColor.RED + "Kullanım: !remove <pozitif miktar>");
                        return;
                    }
                    int removeAmount = Integer.parseInt(args[1]);
                    if (removeAmount <= 0) {
                        player.sendMessage(ChatColor.RED + "Pozitif bir miktar girmeniz gerekiyor.");
                        return;
                    }
                    int currentCredit = CreditSystemSync.getInstance().getCreditSync(player.getName());
                    if (currentCredit == 0) {
                        player.sendMessage(ChatColor.RED + "Hesabınız bulunamadı veya kredi 0.");
                        return;
                    }
                    if (currentCredit < removeAmount) {
                        player.sendMessage(ChatColor.RED + "Yeterli krediniz yok! (" + currentCredit + ")");
                        return;
                    }
                    if (CreditSystemSync.getInstance().removeCreditSync(player.getName(), removeAmount)) {
                        player.sendMessage(ChatColor.YELLOW + "Krediden -" + removeAmount + " silindi.");
                    } else {
                        player.sendMessage(ChatColor.RED + "Kredi silinemedi.");
                    }
                    break;

                case "!set":
                    if (args.length < 2 || !isNumeric(args[1])) {
                        player.sendMessage(ChatColor.RED + "Kullanım: !set <pozitif veya sıfır miktar>");
                        return;
                    }
                    int setAmount = Integer.parseInt(args[1]);
                    if (setAmount < 0) {
                        player.sendMessage(ChatColor.RED + "Negatif değer ayarlanamaz.");
                        return;
                    }
                    if (CreditSystemSync.getInstance().setCreditSync(player.getName(), setAmount)) {
                        player.sendMessage(ChatColor.YELLOW + "Kredin " + setAmount + " olarak ayarlandı.");
                    } else {
                        player.sendMessage(ChatColor.RED + "Hesabınız bulunamadı veya kredi ayarlanamadı.");
                    }
                    break;

                default:
                    player.sendMessage(ChatColor.RED + "Bilinmeyen komut.");
                    break;
            }
        } catch (SQLException e) {
            player.sendMessage(ChatColor.RED + "Bir hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }

        event.setCancelled(true); // sohbet mesajını engelle
    }

    private boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }
}
