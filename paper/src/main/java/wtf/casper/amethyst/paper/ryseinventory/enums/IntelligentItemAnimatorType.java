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

package wtf.casper.amethyst.paper.ryseinventory.enums;

/**
 * @author Rysefoxx(Rysefoxx # 6772)
 * <p>
 * An enum that is used to determine how the text will be animated.
 * @since 4/12/2022
 */
public enum IntelligentItemAnimatorType {

    /**
     * The text will be animated from left to right.
     * <p>
     * Your Text: Hello World
     * <p>
     *  First H is animated then e then l then o and so on.
     */
    WORD_BY_WORD,
    /**
     * The whole text is animated.
     * <p>
     * Your Text: Hello World
     * <p>
     * First the whole text is animated e.g. in red then in aqua.
     * <p>
     * §4Hello World
     * <p>
     * §bHello World
     */
    FULL_WORD,
    /**
     * All the text is animated as Flash.
     */
    FLASH
}
