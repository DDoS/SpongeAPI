/*
 * This file is part of SpongeAPI, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
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
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.api.util;

import java.util.Iterator;

/**
 * This class contains utilities related to collections that are used by Sponge.
 */
public final class Collect {
    private Collect() {}

    /**
     * Constructs an iterable from an existing iterator.
     *
     * <p>This should only be used in special cases where an iterable is instantiated
     * where required, and not as a general method for creating an iterable from an
     * iterator.</p>
     *
     * @param iterator The iterator to use in the iterable
     * @param <T> The type of iterable and iterator
     * @return The iterable
     */
    public static <T> Iterable<T> iterableFromIterator(final Iterator<T> iterator) {
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return iterator;
            }
        };
    }

}
