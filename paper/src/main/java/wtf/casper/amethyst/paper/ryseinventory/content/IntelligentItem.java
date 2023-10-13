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

package wtf.casper.amethyst.paper.ryseinventory.content;

import lombok.Getter;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wtf.casper.amethyst.paper.ryseinventory.pagination.InventoryManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * @author Rysefoxx | Rysefoxx#6772
 * @since 2/18/2022
 */
@Getter
public class IntelligentItem {

    private final ItemStack itemStack;
    private final wtf.casper.amethyst.paper.ryseinventory.content.IntelligentItemError error;

    private Consumer<InventoryClickEvent> defaultConsumer;

    private boolean canClick = true;
    private boolean canSee = true;
    private boolean advanced = false;
    private int delay;

    private @Nullable Object id;
    private @Nullable BukkitTask delayTask;

    //For serialization
    @Contract(pure = true)
    private IntelligentItem(@NotNull ItemStack itemStack, @NotNull wtf.casper.amethyst.paper.ryseinventory.content.IntelligentItemError error) {
        this.itemStack = itemStack;
        this.error = error;
    }

    @Contract(pure = true)
    public IntelligentItem(@NotNull ItemStack itemStack, int delay, Consumer<InventoryClickEvent> eventConsumer, wtf.casper.amethyst.paper.ryseinventory.content.IntelligentItemError error) {
        this.itemStack = itemStack;
        this.defaultConsumer = eventConsumer;
        this.error = error;
        this.delay = delay;
    }

    /**
     * @param itemStack     The item stack that will be used to create the intelligent item.
     * @param error         The error that will be displayed if the player doesn't have the required permission.
     * @param eventConsumer The consumer that will be called when the item is clicked.
     * @return A new instance of IntelligentItem
     */
    @Contract(value = "_, _, _ -> new", pure = true)
    public static @NotNull IntelligentItem of(@NotNull ItemStack itemStack, @NotNull wtf.casper.amethyst.paper.ryseinventory.content.IntelligentItemError error, @NotNull Consumer<InventoryClickEvent> eventConsumer) {
        return new IntelligentItem(itemStack, 0, eventConsumer, error);
    }

    /**
     * @param itemStack     The item stack that will be used to create the intelligent item.
     * @param delayInTicks  The delay in ticks before the consumer is called. (1 Sec = 20 Ticks)
     * @param error         The error that will be displayed if the player doesn't have the required permission.
     * @param eventConsumer The consumer that will be called when the item is clicked.
     * @return A new instance of IntelligentItem
     */
    public static @NotNull IntelligentItem of(@NotNull ItemStack itemStack, int delayInTicks, @NotNull wtf.casper.amethyst.paper.ryseinventory.content.IntelligentItemError error, @NotNull Consumer<InventoryClickEvent> eventConsumer) {
        return new IntelligentItem(itemStack, delayInTicks, eventConsumer, error);
    }

    /**
     * @param itemStack     The item that will be displayed in the inventory.
     * @param eventConsumer The consumer that will be called when the item is clicked.
     * @return A new instance of IntelligentItem
     */
    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull IntelligentItem of(@NotNull ItemStack itemStack, @NotNull Consumer<InventoryClickEvent> eventConsumer) {
        return new IntelligentItem(itemStack, 0, eventConsumer, null);
    }

    /**
     * @param itemStack     The item that will be displayed in the inventory.
     * @param delayInTicks  The delay in ticks before the consumer is called. (1 Sec = 20 Ticks)
     * @param eventConsumer The consumer that will be called when the item is clicked.
     * @return A new instance of IntelligentItem
     */
    public static @NotNull IntelligentItem of(@NotNull ItemStack itemStack, int delayInTicks, @NotNull Consumer<InventoryClickEvent> eventConsumer) {
        return new IntelligentItem(itemStack, delayInTicks, eventConsumer, null);
    }

    /**
     * This function returns a new IntelligentItem with no actions.
     *
     * @param itemStack The ItemStack that will be used for the item.
     * @return A new IntelligentItem object.
     */
    @Contract(value = "_ -> new", pure = true)
    public static @NotNull IntelligentItem empty(@NotNull ItemStack itemStack) {
        return new IntelligentItem(itemStack, 0, event -> {
        }, null);
    }

    /**
     * This function returns a new IntelligentItem with no actions.
     *
     * @param itemStack    The ItemStack that will be used for the item.
     * @param delayInTicks The delay in ticks before the consumer is called. (1 Sec = 20 Ticks)
     * @return A new IntelligentItem object.
     */
    public static @NotNull IntelligentItem empty(@NotNull ItemStack itemStack, int delayInTicks) {
        return new IntelligentItem(itemStack, delayInTicks, event -> {
        }, null);
    }

    /**
     * This function returns an IntelligentItem that does nothing when clicked. And displays an error when the given condition does not apply.
     *
     * @param itemStack The ItemStack that will be used to create the IntelligentItem.
     * @param error     The error when the given condition does not apply.
     * @return A new instance of IntelligentItem
     */
    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull IntelligentItem empty(@NotNull ItemStack itemStack, @NotNull wtf.casper.amethyst.paper.ryseinventory.content.IntelligentItemError error) {
        return new IntelligentItem(itemStack, 0, event -> {
        }, error);
    }

    /**
     * This function returns an IntelligentItem that does nothing when clicked. And displays an error when the given condition does not apply.
     *
     * @param itemStack    The ItemStack that will be used to create the IntelligentItem.
     * @param delayInTicks The delay in ticks before the consumer is called. (1 Sec = 20 Ticks)
     * @param error        The error when the given condition does not apply.
     * @return A new instance of IntelligentItem
     */
    public static @NotNull IntelligentItem empty(@NotNull ItemStack itemStack, int delayInTicks, @NotNull wtf.casper.amethyst.paper.ryseinventory.content.IntelligentItemError error) {
        return new IntelligentItem(itemStack, delayInTicks, event -> {
        }, error);
    }

    /**
     * This function takes an ItemStack and returns an IntelligentItem that is ignored.
     * This allows the player who has the inventory open to take the item out.
     *
     * @param itemStack The item to be ignored.
     * @return A new IntelligentItem object.
     */
    @Contract(value = "_ -> new", pure = true)
    public static @NotNull IntelligentItem ignored(@NotNull ItemStack itemStack) {
        return new IntelligentItem(itemStack, 0, null, null);
    }

    /**
     * This function takes an ItemStack and returns an IntelligentItem that is ignored.
     * This allows the player who has the inventory open to take the item out.
     *
     * @param itemStack    The item to be ignored.
     * @param delayInTicks The delay in ticks before the consumer is called. (1 Sec = 20 Ticks)
     * @return A new IntelligentItem object.
     */
    public static @NotNull IntelligentItem ignored(@NotNull ItemStack itemStack, int delayInTicks) {
        return new IntelligentItem(itemStack, delayInTicks, null, null);
    }

    /**
     * This function takes an ItemStack and returns an IntelligentItem that is ignored.
     * This allows the player who has the inventory open to take the item out.
     *
     * @param itemStack The item stack that is being ignored.
     * @param error     The error when the given condition does not apply.
     * @return A new instance of the IntelligentItem class.
     */
    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull IntelligentItem ignored(@NotNull ItemStack itemStack, @NotNull wtf.casper.amethyst.paper.ryseinventory.content.IntelligentItemError error) {
        return new IntelligentItem(itemStack, 0, null, error);
    }

    /**
     * This function takes an ItemStack and returns an IntelligentItem that is ignored.
     * This allows the player who has the inventory open to take the item out.
     *
     * @param itemStack    The item stack that is being ignored.
     * @param delayInTicks The delay in ticks before the consumer is called. (1 Sec = 20 Ticks)
     * @param error        The error when the given condition does not apply.
     * @return A new instance of the IntelligentItem class.
     */
    public static @NotNull IntelligentItem ignored(@NotNull ItemStack itemStack, int delayInTicks, @NotNull wtf.casper.amethyst.paper.ryseinventory.content.IntelligentItemError error) {
        return new IntelligentItem(itemStack, delayInTicks, null, error);
    }

    /**
     * Removes the consumer from an IntelligentItem
     */
    public void clearConsumer() {
        this.defaultConsumer = event -> {
        };
    }

    /**
     * Sets the id of an IntelligentItem
     *
     * @param id      The id of the item
     * @param manager The manager that will be used to update the inventory.
     * @return The IntelligentItem object.
     */
    public @NotNull IntelligentItem identifier(@NotNull Object id, @NotNull InventoryManager manager) {
        this.id = id;
        manager.register(this);
        return this;
    }

    /**
     * Checks if the item can be clicked.
     *
     * @param supplier The supplier to check.
     * @return The IntelligentItem.
     */
    public @NotNull IntelligentItem canClick(@NotNull BooleanSupplier supplier) {
        this.canClick = supplier.getAsBoolean();
        return this;
    }

    /**
     * Checks if the item is visible to the player.
     *
     * @param supplier The supplier to check.
     * @return The IntelligentItem.
     */
    public @NotNull IntelligentItem canSee(@NotNull BooleanSupplier supplier) {
        this.canSee = supplier.getAsBoolean();
        return this;
    }

    /**
     * Changes the ItemStack of an existing ItemStack without changing the consumer.
     *
     * @param newItemStack The new ItemStack
     * @return The new intelligent ItemStack
     */
    public @NotNull IntelligentItem update(@NotNull ItemStack newItemStack) {
        return new IntelligentItem(newItemStack, this.delay, this.defaultConsumer, this.error);
    }

    /**
     * Changes the ItemStack of an existing ItemStack without changing the consumer.
     *
     * @param newItemStack The new ItemStack
     * @param delayInTicks The delay in ticks before the consumer is called. (1 Sec = 20 Ticks)
     * @return The new intelligent ItemStack
     */
    public @NotNull IntelligentItem update(@NotNull ItemStack newItemStack, int delayInTicks) {
        return new IntelligentItem(newItemStack, delayInTicks, this.defaultConsumer, this.error);
    }

    /**
     * Changes the ItemStack of an existing Intelligent with changing the consumer.
     *
     * @param newIntelligentItem The new IntelligentItem
     * @return The new intelligent ItemStack
     */
    public @NotNull IntelligentItem update(@NotNull IntelligentItem newIntelligentItem) {
        return new IntelligentItem(newIntelligentItem.getItemStack(), this.delay, newIntelligentItem.getDefaultConsumer(), this.error);
    }

    /**
     * Changes the ItemStack of an existing Intelligent with changing the consumer.
     *
     * @param newIntelligentItem The new IntelligentItem
     * @param delayInTicks       The delay in ticks before the consumer is called. (1 Sec = 20 Ticks)
     * @return The new intelligent ItemStack
     */
    public @NotNull IntelligentItem update(@NotNull IntelligentItem newIntelligentItem, int delayInTicks) {
        return new IntelligentItem(newIntelligentItem.getItemStack(), delayInTicks, newIntelligentItem.getDefaultConsumer(), this.error);
    }

    /**
     * Serializes the IntelligentItem to a map.
     *
     * @return The serialized map.
     * @see #deserialize(Map) to get back the IntelligentItem.
     */
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("item", this.itemStack);
        map.put("consumer", this.defaultConsumer);
        map.put("error", this.error);
        map.put("can-click", this.canClick);
        map.put("can-see", this.canSee);
        map.put("advanced", this.advanced);
        map.put("delay", this.delay);
        map.put("id", this.id);
        return map;
    }

    /**
     * Deserializes a map to an IntelligentItem.
     *
     * @param map The map to deserialize.
     * @return The deserialized IntelligentItem.
     */
    @SuppressWarnings("unchecked")
    public static @Nullable IntelligentItem deserialize(@NotNull Map<String, Object> map) {
        if (map.isEmpty()) return null;

        IntelligentItem intelligentItem = new IntelligentItem((ItemStack) map.get("item"), (IntelligentItemError) map.get("error"));
        intelligentItem.defaultConsumer = (Consumer<InventoryClickEvent>) map.get("consumer");
        intelligentItem.canClick = (boolean) map.get("can-click");
        intelligentItem.canSee = (boolean) map.get("can-see");
        intelligentItem.id = map.get("id");
        intelligentItem.advanced = (boolean) map.get("advanced");
        intelligentItem.delay = (int) map.get("delay");
        return intelligentItem;
    }

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IntelligentItem)) return false;
        IntelligentItem that = (IntelligentItem) o;
        return itemStack.isSimilar(that.itemStack)
                && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemStack, error, defaultConsumer, canClick, canSee, advanced, id);
    }

    @ApiStatus.Internal
    public void setDelayTask(@Nullable BukkitTask delayTask) {
        this.delayTask = delayTask;
    }
}
