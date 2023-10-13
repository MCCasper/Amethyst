package wtf.casper.amethyst.paper.utils;

import wtf.casper.amethyst.paper.ryseinventory.content.InventoryProvider;
import wtf.casper.amethyst.paper.ryseinventory.enums.InventoryOpenerType;
import wtf.casper.amethyst.paper.ryseinventory.pagination.RyseInventory;
import org.bukkit.plugin.java.JavaPlugin;
import wtf.casper.amethyst.paper.AmethystPaper;

public class MenuUtil {

    public static RyseInventory getInventory(JavaPlugin plugin, InventoryProvider provider, int size, String title, InventoryOpenerType type) {
        if (!plugin.isEnabled()) {
            throw new IllegalStateException("Plugin is not enabled");
        }

        return RyseInventory.builder()
                .provider(provider)
                .type(type)
                .size(size)
                .title(title)
                .build(plugin, AmethystPaper.getInventoryManager(plugin));
    }

    public static RyseInventory getInventory(JavaPlugin plugin, InventoryProvider provider, int size, String title) {
        if (!plugin.isEnabled()) {
            throw new IllegalStateException("Plugin is not enabled");
        }
        return RyseInventory.builder()
                .provider(provider)
                .size(size)
                .title(title)
                .build(plugin, AmethystPaper.getInventoryManager(plugin));
    }
}
