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

package wtf.casper.amethyst.paper.ryseinventory.animator;

import com.google.common.base.Preconditions;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.*;
import wtf.casper.amethyst.paper.ryseinventory.content.IntelligentItem;
import wtf.casper.amethyst.paper.ryseinventory.content.InventoryContents;
import wtf.casper.amethyst.paper.ryseinventory.enums.AnimatorDirection;
import wtf.casper.amethyst.paper.ryseinventory.enums.TimeSetting;
import wtf.casper.amethyst.paper.ryseinventory.pagination.RyseInventory;
import wtf.casper.amethyst.paper.ryseinventory.util.SlotUtils;
import wtf.casper.amethyst.paper.ryseinventory.util.StringConstants;
import wtf.casper.amethyst.paper.ryseinventory.util.TimeUtils;

import javax.annotation.Nonnegative;
import java.util.*;

/**
 * @author Rysefoxx | Rysefoxx#6772
 * @since 4/15/2022
 */
public class SlideAnimation {

    private static Plugin plugin;
    private final List<BukkitTask> task = new ArrayList<>();
    private final HashMap<Integer, Integer> timeHandler = new HashMap<>();
    private List<Integer> from = new ArrayList<>();
    private List<Integer> to = new ArrayList<>();
    private List<IntelligentItem> items = new ArrayList<>();
    private int period = 20;
    private int delay = 20;
    private AnimatorDirection direction;
    private Object identifier;
    private InventoryContents contents;
    private boolean blockClickEvent = false;

    @Contract("_ -> new")
    public static @NotNull Builder builder(@NotNull Plugin plugin) {
        SlideAnimation.plugin = plugin;
        return new Builder();
    }

    /**
     * This starts the animation for the inventory.
     *
     * @param contents The inventory contents to animate.
     * @throws IllegalArgumentException if invalid data was passed in the builder.
     */
    public void animate(@NotNull InventoryContents contents) throws IllegalArgumentException {
        this.contents = contents;
        RyseInventory inventory = contents.pagination().inventory();

        for (int i = 0; i < this.from.size(); i++) {
            int fromSlot = this.from.get(i);
            int toSlot = this.to.get(i);

            checkIfInvalid(fromSlot, toSlot, inventory);
        }

        animateByTyp();
    }

    /**
     * If the direction is horizontal, animate horizontally. If the direction is vertical, animate vertically. If the
     * direction is diagonal, animate diagonally
     */
    private void animateByTyp() {
        if (this.direction == AnimatorDirection.HORIZONTAL_LEFT_RIGHT || this.direction == AnimatorDirection.HORIZONTAL_RIGHT_LEFT) {
            animateHorizontal();
            return;
        }
        if (this.direction == AnimatorDirection.VERTICAL_UP_DOWN || this.direction == AnimatorDirection.VERTICAL_DOWN_UP) {
            animateVertical();
            return;
        }
        if (this.direction == AnimatorDirection.DIAGONAL_TOP_LEFT || this.direction == AnimatorDirection.DIAGONAL_BOTTOM_LEFT) {
            animateDiagonalTopBottomLeft();
            return;
        }
        if (this.direction == AnimatorDirection.DIAGONAL_TOP_RIGHT || this.direction == AnimatorDirection.DIAGONAL_BOTTOM_RIGHT) {
            animateDiagonalTopBottomRight();
        }
    }

    /**
     * It animates the items from the top left to the bottom right
     */
    private void animateDiagonalTopBottomLeft() {
        for (int i = 0; i < this.items.size(); i++) {
            boolean moreDelay = i != 0 && Objects.equals(this.from.get(i), this.from.get(i - 1));

            final int[] wait = {this.timeHandler.getOrDefault(this.from.get(i), 0) + 2};

            if (moreDelay) {
                this.timeHandler.put(this.from.get(i), wait[0]);
            }

            int finalI = i;

            BukkitTask bukkitTask = new BukkitRunnable() {
                final int toIndex = to.get(finalI);
                final IntelligentItem item = items.get(finalI);
                final boolean isTopLeft = direction == AnimatorDirection.DIAGONAL_TOP_LEFT;
                int fromIndex = from.get(finalI);
                int previousIndex = fromIndex;

                @Override
                public void run() {
                    if (moreDelay && wait[0] > 0) {
                        wait[0]--;
                        return;
                    }

                    if (this.isTopLeft) {
                        if (this.fromIndex > this.toIndex) {
                            cancel();
                            return;
                        }
                    } else if (this.fromIndex < this.toIndex) {
                        cancel();
                        return;
                    }

                    if (this.fromIndex != from.get(finalI)) {
                        contents.removeItemWithConsumer(this.previousIndex);
                    }

                    contents.set(this.fromIndex, this.item);
                    contents.update(this.fromIndex, this.item);

                    this.previousIndex = this.fromIndex;
                    if (this.isTopLeft) {
                        this.fromIndex += 10;
                        return;
                    }
                    this.fromIndex -= 8;
                }
            }.runTaskTimer(plugin, this.delay, this.period);
            this.task.add(bukkitTask);
        }
    }

    /**
     * It animates the items from the top right
     */
    private void animateDiagonalTopBottomRight() {
        for (int i = 0; i < this.items.size(); i++) {
            boolean moreDelay = i != 0 && Objects.equals(this.from.get(i), this.from.get(i - 1));

            final int[] wait = {this.timeHandler.getOrDefault(this.from.get(i), 0) + 2};

            if (moreDelay) {
                this.timeHandler.put(this.from.get(i), wait[0]);
            }

            int finalI = i;

            BukkitTask bukkitTask = new BukkitRunnable() {
                final int toIndex = to.get(finalI);
                final IntelligentItem item = items.get(finalI);
                final boolean isTopRight = direction == AnimatorDirection.DIAGONAL_TOP_RIGHT;
                int fromIndex = from.get(finalI);
                int previousIndex = fromIndex;

                @Override
                public void run() {
                    if (moreDelay && wait[0] > 0) {
                        wait[0]--;
                        return;
                    }

                    if (this.isTopRight) {
                        if (this.fromIndex > this.toIndex) {
                            cancel();
                            return;
                        }
                    } else if (this.fromIndex < this.toIndex) {
                        cancel();
                        return;
                    }

                    if (this.fromIndex != from.get(finalI))
                        contents.removeItemWithConsumer(this.previousIndex);

                    contents.set(this.fromIndex, this.item);
                    contents.update(this.fromIndex, this.item);

                    this.previousIndex = this.fromIndex;
                    if (this.isTopRight) {
                        this.fromIndex += 8;
                        return;
                    }
                    this.fromIndex -= 10;
                }
            }.runTaskTimer(plugin, this.delay, this.period);
            this.task.add(bukkitTask);
        }
    }

    /**
     * It moves the items from one slot to another
     */
    private void animateHorizontal() {
        for (int i = 0; i < this.items.size(); i++) {
            boolean moreDelay = i != 0 && Objects.equals(this.from.get(i), this.from.get(i - 1));

            final int[] wait = {this.timeHandler.getOrDefault(this.from.get(i), 0) + 2};

            if (moreDelay) {
                this.timeHandler.put(this.from.get(i), wait[0]);
            }

            int finalI = i;
            BukkitTask bukkitTask = new BukkitRunnable() {
                final int toIndex = to.get(finalI);
                final IntelligentItem item = items.get(finalI);
                final boolean leftToRight = direction == AnimatorDirection.HORIZONTAL_LEFT_RIGHT;
                int fromIndex = from.get(finalI);
                int previousIndex = fromIndex;

                @Override
                public void run() {
                    if (moreDelay && wait[0] > 0) {
                        wait[0]--;
                        return;
                    }

                    if (this.leftToRight) {
                        if (this.fromIndex > this.toIndex) {
                            cancel();
                            return;
                        }
                    } else if (this.fromIndex < this.toIndex) {
                        cancel();
                        return;
                    }

                    if (this.fromIndex != from.get(finalI))
                        contents.removeItemWithConsumer(this.previousIndex);

                    contents.set(this.fromIndex, this.item);
                    contents.update(this.fromIndex, this.item);

                    this.previousIndex = this.fromIndex;
                    if (this.leftToRight) {
                        this.fromIndex++;
                        return;
                    }
                    this.fromIndex--;

                }
            }.runTaskTimer(plugin, this.delay, this.period);
            this.task.add(bukkitTask);
        }
    }

    /**
     * It animates the items in the inventory from one slot to another
     */
    private void animateVertical() {
        for (int i = 0; i < this.items.size(); i++) {
            boolean moreDelay = i != 0 && Objects.equals(this.from.get(i), this.from.get(i - 1));

            final int[] wait = {this.timeHandler.getOrDefault(this.from.get(i), 0) + 2};

            if (moreDelay) {
                this.timeHandler.put(this.from.get(i), wait[0]);
            }

            int finalI = i;

            BukkitTask bukkitTask = new BukkitRunnable() {
                final int toIndex = to.get(finalI);
                final IntelligentItem item = items.get(finalI);
                final boolean upToDown = direction == AnimatorDirection.VERTICAL_UP_DOWN;
                int fromIndex = from.get(finalI);
                int previousIndex = fromIndex;

                @Override
                public void run() {
                    if (moreDelay && wait[0] > 0) {
                        wait[0]--;
                        return;
                    }

                    if (this.upToDown) {
                        if (this.fromIndex > this.toIndex) {
                            cancel();
                            return;
                        }
                    } else if (this.fromIndex < this.toIndex) {
                        cancel();
                        return;
                    }

                    if (this.fromIndex != from.get(finalI)) {
                        contents.removeItemWithConsumer(this.previousIndex);
                    }

                    contents.set(this.fromIndex, this.item);
                    contents.update(this.fromIndex, this.item);

                    this.previousIndex = this.fromIndex;
                    if (this.upToDown) {
                        this.fromIndex += 9;
                        return;
                    }
                    this.fromIndex -= 9;

                }
            }.runTaskTimer(plugin, this.delay, this.period);
            this.task.add(bukkitTask);
        }
    }

    /**
     * It checks if the animation is valid
     *
     * @param from      The start slot of the animation.
     * @param to        The end slot of the animation.
     * @param inventory The inventory that the animation is being performed on.
     */
    private void checkIfInvalid(@Nonnegative int from,
                                @Nonnegative int to,
                                @NotNull RyseInventory inventory) {
        if (this.direction == AnimatorDirection.HORIZONTAL_LEFT_RIGHT || this.direction == AnimatorDirection.HORIZONTAL_RIGHT_LEFT) {
            if ((from - 1) / 9 != (to - 1) / 9 && from / 9 != to / 9) {
                throw new IllegalArgumentException("The start position " + from + " and the end position " + to + " are not on the same row.");
            }
            if (this.direction == AnimatorDirection.HORIZONTAL_LEFT_RIGHT) {
                if (from < to) return;
                throw new IllegalArgumentException("An animation from left to right requires that the values in to() are larger than the values in from()");
            } else {
                if (to < from) return;
                throw new IllegalArgumentException("An animation from right to left requires that the values in from() are larger than the values in to()");
            }
        } else if (this.direction == AnimatorDirection.VERTICAL_UP_DOWN || this.direction == AnimatorDirection.VERTICAL_DOWN_UP) {
            if (from % 9 != to % 9) {
                throw new IllegalArgumentException("The start position " + from + " and the end position " + to + " are not on the same column.");
            }
            if (this.direction == AnimatorDirection.VERTICAL_UP_DOWN) {
                if (from < to) return;
                throw new IllegalArgumentException("An animation from up to down requires that the values in to() are larger than the values in from()");
            } else {
                if (to < from) return;
                throw new IllegalArgumentException("An animation from down to up requires that the values in from() are larger than the values in to()");
            }
        }

        if (from == to)
            throw new IllegalArgumentException("The animation could not be started! from " + from + " and to " + to + " have the same values. from must be smaller than " + to + ".");

        if (from > inventory.size(contents))
            throw new IllegalArgumentException("The start slot must not be larger than the inventory size.");

        if (to > inventory.size(contents))
            throw new IllegalArgumentException("The end slot must not be larger than the inventory size.");

    }

    /**
     * Returns true if the click event is blocked.
     *
     * @return The boolean value of the blockClickEvent variable.
     */
    @ApiStatus.Internal
    public boolean isBlockClickEvent() {
        return this.blockClickEvent;
    }

    /**
     * It returns a list of tasks that are currently running
     *
     * @return A list of BukkitTasks
     * @throws UnsupportedOperationException If list gets modified
     */
    @ApiStatus.Internal
    @Unmodifiable
    public @NotNull List<BukkitTask> getTasks() throws UnsupportedOperationException {
        return Collections.unmodifiableList(this.task);
    }

    @ApiStatus.Internal
    public void clearTasks() {
        this.task.clear();
    }

    /**
     * Returns the identifier of this object, or null if it has none.
     *
     * @return The identifier of the object.
     */
    public @Nullable Object getIdentifier() {
        return this.identifier;
    }

    public static class Builder {
        private SlideAnimation preset;
        private List<Integer> from = new ArrayList<>();
        private List<Integer> to = new ArrayList<>();
        private List<IntelligentItem> items = new ArrayList<>();
        private int period = 20;
        private int delay = 20;

        private AnimatorDirection direction;
        private Object identifier;
        private boolean blockClickEvent = false;

        /**
         * This blocks the InventoryClickEvent until the animation is over.
         *
         * @return The Builder to perform further editing.
         */
        public Builder blockClickEvent() {
            this.blockClickEvent = true;
            return this;
        }

        /**
         * Takes over all properties of the passed animator.
         *
         * @param preset The animator to be copied.
         * @return The Builder to perform further editing.
         * <p>
         * When copying the animator, the identification is not copied if present!
         */
        public @NotNull Builder copy(@NotNull SlideAnimation preset) {
            this.preset = preset;
            return this;
        }

        /**
         * Defines how the item should be animated in the inventory.
         *
         * @param direction The direction of the animation.
         * @return The Builder to perform further editing.
         */
        public @NotNull Builder direction(@NotNull AnimatorDirection direction) {
            this.direction = direction;
            return this;
        }

        /**
         * Specifies the delay before the animation starts.
         *
         * @param time    The delay.
         * @param setting The time setting.
         * @return The Builder to perform further editing.
         */
        public @NotNull Builder delay(@Nonnegative int time, @NotNull TimeSetting setting) {
            this.delay = TimeUtils.buildTime(time, setting);
            return this;
        }

        /**
         * Sets the speed of the animation in the scheduler.
         *
         * @param time    The period.
         * @param setting The time setting.
         * @return The Builder to perform further editing.
         */
        public @NotNull Builder period(@Nonnegative int time, @NotNull TimeSetting setting) {
            this.period = TimeUtils.buildTime(time, setting);
            return this;
        }

        /**
         * Adds a single start position.
         *
         * @param slot The slot to start at.
         * @return The Builder to perform further editing.
         * @throws IllegalArgumentException if slot is greater than 53
         */
        public @NotNull Builder from(@Nonnegative int slot) throws IllegalArgumentException {
            if (slot > 53)
                throw new IllegalArgumentException(StringConstants.INVALID_SLOT);

            this.from.add(slot);
            return this;
        }

        /**
         * Adds a single start position.
         *
         * @param row    The row to start at.
         * @param column The column to start at.
         * @return The Builder to perform further editing.
         * @throws IllegalArgumentException if slot greater than 53
         */
        public @NotNull Builder from(@Nonnegative int row, @Nonnegative int column) throws IllegalArgumentException {
            return from(SlotUtils.toSlot(row, column));
        }

        /**
         * Add multiple start positions.
         *
         * @param slots The slots to start at.
         * @return The Builder to perform further editing.
         * @throws IllegalArgumentException if slot is greater than 53
         */
        public @NotNull Builder from(Integer ... slots) throws IllegalArgumentException {
            for (Integer slot : slots)
                from(slot);

            return this;
        }

        /**
         * Add multiple start positions.
         *
         * @param rows    The rows to start at.
         * @param columns The columns to start at.
         * @return The Builder to perform further editing.
         * @throws IllegalArgumentException if row is greater than 5 or if column greater than 8
         */
        public @NotNull Builder from(Integer [] rows, Integer [] columns) throws IllegalArgumentException {
            Preconditions.checkArgument(rows.length == columns.length, StringConstants.INVALID_ROW_LENGTH);

            for (int i = 0; i < rows.length; i++)
                from(rows[i], columns[i]);

            return this;
        }

        /**
         * Add multiple start positions.
         *
         * @param slots The slots to start at.
         * @return The Builder to perform further editing.
         * @throws IllegalArgumentException if slot is greater than 53
         *                                  <p>
         *                                  We recommend passing the list from small to large. e.g .from(Arrays.asList(1, 1, 4)) NOT .from(Arrays.asList(4,1,1))
         */
        public @NotNull Builder from(@NotNull List<Integer> slots) throws IllegalArgumentException {
            slots.forEach(this::from);
            return this;
        }

        /**
         * Add multiple start positions.
         *
         * @param rows    The rows to start at.
         * @param columns The columns to start at.
         * @return The Builder to perform further editing.
         * @throws IllegalArgumentException if row is greater than 5 or if column greater than 8
         */
        public @NotNull Builder from(@NotNull List<Integer> rows, @NotNull List<Integer> columns) throws IllegalArgumentException {
            Preconditions.checkArgument(rows.size() == columns.size(), StringConstants.INVALID_ROW_LENGTH);

            for (int i = 0; i < rows.size(); i++)
                from(rows.get(i), columns.get(i));

            return this;
        }

        /**
         * Adds a single end position
         *
         * @param slot The slot to end at.
         * @return The Builder to perform further editing.
         * @throws IllegalArgumentException if slot is greater than 53
         */
        public @NotNull Builder to(@Nonnegative int slot) throws IllegalArgumentException {
            if (slot > 53)
                throw new IllegalArgumentException(StringConstants.INVALID_SLOT);

            this.to.add(slot);
            return this;
        }

        /**
         * Adds a single end position.
         *
         * @param row    The row to end at.
         * @param column The column to end at.
         * @return The Builder to perform further editing.
         * @throws IllegalArgumentException if slot is greater than 53
         */
        public @NotNull Builder to(@Nonnegative int row, @Nonnegative int column) throws IllegalArgumentException {
            return to(SlotUtils.toSlot(row, column));
        }

        /**
         * Add multiple end positions.
         *
         * @param slots The slots to end at.
         * @return The Builder to perform further editing.
         * @throws IllegalArgumentException if slot is greater than 53
         */
        public @NotNull Builder to(int ... slots) throws IllegalArgumentException {
            for (int slot : slots)
                to(slot);

            return this;
        }

        /**
         * Add multiple end positions.
         *
         * @param rows    The rows to end at.
         * @param columns The columns to end at.
         * @return The Builder to perform further editing.
         * @throws IllegalArgumentException if row is greater than 5 or if column greater than 8
         */
        public @NotNull Builder to(Integer [] rows, Integer [] columns) throws IllegalArgumentException {
            Preconditions.checkArgument(rows.length == columns.length, StringConstants.INVALID_ROW_LENGTH);

            for (int i = 0; i < rows.length; i++)
                to(rows[i], columns[i]);

            return this;
        }

        /**
         * Add multiple end positions.
         *
         * @param slots The slots to end at.
         * @return The Builder to perform further editing.
         * @throws IllegalArgumentException if slot is greater than 53
         */
        public @NotNull Builder to(@NotNull List<Integer> slots) throws IllegalArgumentException {
            slots.forEach(this::to);
            return this;
        }

        /**
         * Add multiple end positions.
         *
         * @param rows    The rows to end at.
         * @param columns The columns to end at.
         * @return The Builder to perform further editing.
         * @throws IllegalArgumentException if row is greater than 5 or if column greater than 8
         */
        public @NotNull Builder to(@NotNull List<Integer> rows, @NotNull List<Integer> columns) throws IllegalArgumentException {
            Preconditions.checkArgument(rows.size() == columns.size(), StringConstants.INVALID_ROW_LENGTH);

            for (int i = 0; i < rows.size(); i++)
                to(rows.get(i), columns.get(i));

            return this;
        }

        /**
         * Add an item, which will appear animated in the inventory.
         *
         * @param item The item to add.
         * @return The Builder to perform further editing.
         */
        public @NotNull Builder item(@NotNull IntelligentItem item) {
            this.items.add(item);
            return this;
        }

        /**
         * Add multiple items that will appear animated in the inventory.
         *
         * @param items The items to add.
         * @return The Builder to perform further editing.
         */
        public @NotNull Builder item(IntelligentItem ... items) {
            for (IntelligentItem item : items)
                item(item);

            return this;
        }

        /**
         * Add multiple items that will appear animated in the inventory.
         *
         * @param items The items to add.
         * @return The Builder to perform further editing.
         */
        public @NotNull Builder items(IntelligentItem ... items) {
            return item(items);
        }

        /**
         * Add multiple items that will appear animated in the inventory.
         *
         * @param items The items to add.
         * @return The Builder to perform further editing.
         */
        public @NotNull Builder item(@NotNull List<IntelligentItem> items) {
            items.forEach(this::item);
            return this;
        }

        /**
         * Add multiple items that will appear animated in the inventory.
         *
         * @param items The items to add.
         * @return The Builder to perform further editing.
         */
        public @NotNull Builder items(@NotNull List<IntelligentItem> items) {
            return item(items);
        }


        /**
         * Gives the Animation an identification
         *
         * @param identifier The ID through which you can get the animation
         * @return The Builder to perform further editing
         */
        public @NotNull Builder identifier(@NotNull Object identifier) {
            this.identifier = identifier;
            return this;
        }

        /**
         * This creates the animation class but does not start it yet! {@link SlideAnimation#animate(InventoryContents)}
         *
         * @return The animation class
         * @throws IllegalArgumentException if no start position was specified, if no end position was specified, if period is empty or if items is empty.
         * @throws IllegalStateException    if manager is null
         * @throws NullPointerException     if direction is null.
         */
        public SlideAnimation build() throws IllegalArgumentException, NullPointerException {
            if (this.preset != null) {
                this.to = this.preset.to;
                this.from = this.preset.from;
                this.items = this.preset.items;
                this.direction = this.preset.direction;
                this.period = this.preset.period;
                this.delay = this.preset.delay;
                this.blockClickEvent = this.preset.blockClickEvent;
            }

            if (this.to.isEmpty())
                throw new IllegalArgumentException("No start positions were found. Please add start positions to make the animation work.");

            if (this.from.isEmpty())
                throw new IllegalArgumentException("No end positions were found. Please add end positions to make the animation work.");

            if (this.items.isEmpty())
                throw new IllegalArgumentException("No items were found. Please add items to make the animation work.");

            if (this.direction == null)
                throw new NullPointerException("Direction is null. Please specify a direction for the animation.");

            Preconditions.checkArgument(this.from.size() == this.to.size(), "from and to must have the same size");
            Preconditions.checkArgument(this.from.size() == this.items.size(), "from and items must have the same size");

            SlideAnimation slideAnimation = new SlideAnimation();
            slideAnimation.to = this.to;
            slideAnimation.from = this.from;
            slideAnimation.items = this.items;
            slideAnimation.direction = this.direction;
            slideAnimation.period = this.period;
            slideAnimation.identifier = this.identifier;
            slideAnimation.delay = this.delay;
            slideAnimation.blockClickEvent = this.blockClickEvent;
            return slideAnimation;
        }
    }
}
