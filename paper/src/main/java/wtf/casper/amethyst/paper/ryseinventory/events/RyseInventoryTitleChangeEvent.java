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

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * @author Rysefoxx | Rysefoxx#6772
 * @since 7/4/2022
 */
public class RyseInventoryTitleChangeEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.builder()
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build();

    private final Component oldTitle;
    private Component newTitle;

    private boolean isCancelled;

    /**
     * @param player   The player who changed the title.
     * @param newTitle The new title of the inventory.
     * @param oldTitle The old title of the inventory.
     *                 This event is called when the title of the inventory changes.
     */
    public RyseInventoryTitleChangeEvent(@NotNull Player player, @NotNull Component oldTitle, @NotNull Component newTitle) {
        super(player);
        this.isCancelled = false;
        this.oldTitle = oldTitle;
        this.newTitle = newTitle;
    }

    @Contract(pure = true)
    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    /**
     * If true, the title set via {@link #setNewTitle(String)} will be ignored.
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
     * @return The previous title of the inventory.
     */
    public @NotNull String getOldTitle() {
        return SERIALIZER.serialize(this.oldTitle);
    }

    /**
     * @return The previous title of the inventory.
     */
    public @NotNull Component oldTitle() {
        return this.oldTitle;
    }

    /**
     * @return The new title of the inventory.
     */
    public @NotNull String getNewTitle() {
        return SERIALIZER.serialize(this.newTitle);
    }

    /**
     * @return The new title of the inventory.
     */
    public @NotNull Component newTitle() {
        return this.newTitle;
    }

    /**
     * Gives the inventory a new title.
     *
     * @param newTitle The new title of the inventory.
     *                 <p>
     *                 If isCancelled is true, the title will not be set.
     */
    public void setNewTitle(@NotNull String newTitle) {
        if (this.isCancelled) return;

        this.newTitle = Component.text(newTitle);
    }

    /**
     * Gives the inventory a new title.
     *
     * @param newTitle The new title of the inventory.
     *                 <p>
     *                 If isCancelled is true, the title will not be set.
     */
    public void setNewTitle(@NotNull Component newTitle) {
        if (this.isCancelled) return;

        this.newTitle = newTitle;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }
}
