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

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wtf.casper.amethyst.paper.ryseinventory.util.TimeUtils;

import java.util.Arrays;

public enum TimeSetting {

    MILLISECONDS("ms"),
    SECONDS("s"),
    MINUTES("m"),
    ;

    private final String shortCut;

    @Contract(pure = true)
    TimeSetting(@NotNull String shortCut) {
        this.shortCut = shortCut;
    }

    public static @Nullable TimeSetting fromName(@NotNull String name) {
        return Arrays.stream(values())
                .filter(timeSetting ->
                        timeSetting.name().equalsIgnoreCase(name)
                                || timeSetting.shortCut.equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public static int clickDelay(int delay, TimeSetting setting) {
        return TimeUtils.buildTime(delay, setting);
    }
}
