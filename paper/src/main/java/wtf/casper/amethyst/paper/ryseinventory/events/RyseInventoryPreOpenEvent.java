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
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import wtf.casper.amethyst.paper.ryseinventory.pagination.RyseInventory;

/**
 * @since 7/4/2022
 * @author Rysefoxx | Rysefoxx#6772
 * <br>
 * This class is called before the inventory is opened. With this you are able to e.g. change the inventory #setInventory or cancel it completely #setCancelled
 */
public class RyseInventoryPreOpenEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    private RyseInventory inventory;

    private boolean isCancelled;

    /**
     * @param player    The player who opened the inventory.
     * @param inventory The inventory
     */
    public RyseInventoryPreOpenEvent(@NotNull Player player, @NotNull RyseInventory inventory) {
        super(player);
        this.inventory = inventory;
        this.isCancelled = false;
    }

    @Contract(pure = true)
    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    /**
     * If true, the inventory will not be opened.
     */
    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.isCancelled = cancel;
    }

    /**
     * @return The inventory, which is opened to the player.
     */
    public @NotNull RyseInventory getInventory() {
        return this.inventory;
    }

    /**
     * @param inventory The new RyseInventory, which will be opened to the player.
     */
    public void setInventory(@NotNull RyseInventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }
}
