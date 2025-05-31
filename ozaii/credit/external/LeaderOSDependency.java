package de.codingair.tradesystem.spigot.ozaii.credit.external;


import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.tradesystem.spigot.TradeSystem;
import de.codingair.tradesystem.spigot.events.TradeIconInitializeEvent;
import de.codingair.tradesystem.spigot.events.TradePatternRegistrationEvent;
import de.codingair.tradesystem.spigot.extras.external.PluginDependency;
import de.codingair.tradesystem.spigot.ozaii.credit.MySQLDatabaseManager;
import de.codingair.tradesystem.spigot.trade.gui.layout.registration.EditorInfo;
import de.codingair.tradesystem.spigot.trade.gui.layout.registration.TransitionTargetEditorInfo;
import de.codingair.tradesystem.spigot.trade.gui.layout.registration.Type;
import de.codingair.tradesystem.spigot.trade.gui.layout.registration.exceptions.TradeIconException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class LeaderOSDependency implements PluginDependency, Listener {

    @EventHandler
    public void onIconInitialize(TradeIconInitializeEvent e) {

        if (!MySQLDatabaseManager.checkConn()) {
            TradeSystem.getInstance().getLogger().warning("Leaderos için database bağlanıtısı başarısız bu yüzden leaderos kredisi kullanılamaz!");
            return;
        }
        try {
            e.registerIcon(
                    TradeSystem.getInstance(),
                    LeaderosIcon.class,
                    new EditorInfo("LeaderOS icon", Type.ECONOMY, (editor) -> new ItemBuilder(XMaterial.EMERALD), false, getPluginName())
            );
            e.registerIcon(
                    TradeSystem.getInstance(),
                    ShowLeaderosIcon.class,
                    new TransitionTargetEditorInfo("LeaderOS preview icon", LeaderosIcon.class)
            );
        } catch (TradeIconException ex) {
            throw new RuntimeException(ex);
        }
    }

    @EventHandler
    public void onPatternRegistration(TradePatternRegistrationEvent e) {
        e.addPattern(new DefaultLeaderosPattern());
    }

    @Override
    public @NotNull String getPluginName() {
        return "Vault";
    }
}
