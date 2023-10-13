/*
 * MIT License
 *
 * Copyright (c) 2022. Rysefoxx
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package wtf.casper.amethyst.paper.ryseinventory.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import wtf.casper.amethyst.paper.ryseinventory.pagination.RyseInventory;

/**
 * @author Rysefoxx | Rysefoxx#6772
 * <br>
 * This event is called after the inventory is opened. Accordingly, you can not prevent the event.
 * @since 7/4/2022
 */
public class RyseInventoryOpenEvent extends PlayerEvent {

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    private final RyseInventory inventory;

    /**
     * @param player    The player who opened the inventory.
     * @param inventory The inventory
     */
    public RyseInventoryOpenEvent(@NotNull Player player, @NotNull RyseInventory inventory) {
        super(player);
        this.inventory = inventory;
    }

    @Contract(pure = true)
    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    /**
     * @return The inventory, which is opened to the player.
     */
    public @NotNull RyseInventory getInventory() {
        return this.inventory;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }
}
