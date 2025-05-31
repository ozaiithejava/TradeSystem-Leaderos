package de.codingair.tradesystem.spigot.ozaii.credit.external;

import de.codingair.tradesystem.spigot.ozaii.credit.CreditSystemSync;
import de.codingair.tradesystem.spigot.trade.Trade;
import de.codingair.tradesystem.spigot.trade.gui.layout.types.feedback.FinishResult;
import de.codingair.tradesystem.spigot.trade.gui.layout.types.impl.economy.EconomyIcon;
import de.codingair.tradesystem.spigot.trade.gui.layout.utils.Perspective;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.sql.SQLException;

public class LeaderosIcon extends EconomyIcon<ShowLeaderosIcon> {

    public LeaderosIcon(@NotNull ItemStack itemStack) {
        super(itemStack, "Credit", "Credit", true);
    }

    public LeaderosIcon(@NotNull ItemStack itemStack, @NotNull String nameSingular, @NotNull String namePlural, boolean decimal) {
        super(itemStack, nameSingular, namePlural, decimal);
    }

    @Override
    public Class<ShowLeaderosIcon> getTargetClass() {
        return ShowLeaderosIcon.class;
    }

    @Override
    protected @NotNull BigDecimal getBalance(@NotNull Player player) {
        try {
            int credit = CreditSystemSync.getInstance().getCreditSync(player.getName());
            return BigDecimal.valueOf(credit);
        } catch (SQLException e) {
            e.printStackTrace();
            return BigDecimal.ZERO;
        }
    }

    @Override
    protected void withdraw(Player player, @NotNull BigDecimal value) {
        try {
            CreditSystemSync.getInstance().removeCreditSync(player.getName(), value.intValue());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void deposit(Player player, @NotNull BigDecimal value) {
        try {
            CreditSystemSync.getInstance().addCreditSync(player.getName(), value.intValue());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public @NotNull FinishResult tryFinish(@NotNull Trade trade, @NotNull Perspective perspective, @NotNull Player viewer, boolean initiationServer) {
        FinishResult result = super.tryFinish(trade, perspective, viewer, initiationServer);

        Player player = trade.getPlayer(perspective);
        if (player == null) {
            return result; // Proxy durum
        }

        double value = getOverallDifference(trade, perspective).doubleValue();

        try {
            int currentCredit = CreditSystemSync.getInstance().getCreditSync(player.getName());

            if (value < 0) {
                int withdrawAmount = (int) (-value);
                if (currentCredit < withdrawAmount) {
                    return FinishResult.ERROR_ECONOMY;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return FinishResult.ERROR_ECONOMY;
        }

        return result;
    }

    @Override
    protected @NotNull de.codingair.tradesystem.spigot.extras.external.TypeCap getMaxSupportedValue() {
        return de.codingair.tradesystem.spigot.extras.external.EconomySupportType.INTEGER;
    }
}
