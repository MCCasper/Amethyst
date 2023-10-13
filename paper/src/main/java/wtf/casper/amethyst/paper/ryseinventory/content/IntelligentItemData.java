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
import lombok.Setter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnegative;

/**
 * @author Rysefoxx | Rysefoxx#6772
 * @since 6/14/2022
 */
@Setter
@Getter
public class IntelligentItemData {

    private final IntelligentItem item;
    private int amount;
    private int page;
    private int originalSlot;
    private int modifiedSlot;

    private boolean transfer;
    private boolean presetOnAllPages;

    @Contract(pure = true)
    public IntelligentItemData(@NotNull IntelligentItem item,
                               @Nonnegative int page,
                               int originalSlot,
                               boolean transfer,
                               boolean presetOnAllPages) {
        this.item = item;
        this.page = page;
        this.originalSlot = originalSlot;
        this.modifiedSlot = this.originalSlot;
        this.transfer = transfer;
        this.presetOnAllPages = presetOnAllPages;
        this.amount = item.getItemStack().getAmount();
    }
}
