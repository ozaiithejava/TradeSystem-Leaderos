package de.codingair.tradesystem.spigot.ozaii.credit.external;

import de.codingair.tradesystem.spigot.trade.gui.layout.types.TradeIcon;
import de.codingair.tradesystem.spigot.trade.gui.layout.types.impl.economy.ShowEconomyIcon;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ShowLeaderosIcon extends ShowEconomyIcon {
    public ShowLeaderosIcon(@NotNull ItemStack itemStack) {
        super(itemStack, "Credit");
    }

    @Override
    public @NotNull Class<? extends TradeIcon> getOriginClass() {
        return LeaderosIcon.class;
    }
}
