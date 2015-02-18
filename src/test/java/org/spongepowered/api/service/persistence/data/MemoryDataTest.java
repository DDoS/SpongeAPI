/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered.org <http://www.spongepowered.org>
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
package org.spongepowered.api.service.persistence.data;

import static org.junit.Assert.assertTrue;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.util.List;

public class MemoryDataTest {

    @Test
    public void testCreateDataView() {
        new MemoryDataContainer();
    }

    @Test
    public void testCreateView() {
        DataContainer container = new MemoryDataContainer();
        DataQuery tempQuery = new DataQuery("foo", "bar", "baz");
        container.createView(tempQuery);
        assertTrue(container.getView(tempQuery).isPresent());
    }

    @Test
    public void testSetData() {
        DataContainer container = new MemoryDataContainer();
        DataQuery testQuery = new DataQuery('.', "foo.bar");
        container.set(testQuery, 1);
        Optional<Integer> optional = container.getInt(testQuery);
        assertTrue(optional.isPresent());
    }

    @Test
    public void testIncorrectType() {
        DataContainer container = new MemoryDataContainer();
        DataQuery testQuery = new DataQuery("foo", "bar");
        container.set(testQuery, "foo");
        Optional<Integer> optional = container.getInt(testQuery);
        assertTrue(!optional.isPresent());
    }

    @Test
    public void testNumbers() {
        DataContainer container = new MemoryDataContainer();
        DataQuery testQuery = new DataQuery("foo", "bar");
        container.set(testQuery, 1.0D);
        Optional<Integer> integerOptional = container.getInt(testQuery);
        assertTrue(integerOptional.isPresent());
        assertTrue(integerOptional.get() == 1);
        Optional<Long> longOptional = container.getLong(testQuery);
        assertTrue(longOptional.isPresent());
        assertTrue(longOptional.get() == 1L);
        Optional<Double> doubleOptional = container.getDouble(testQuery);
        assertTrue(doubleOptional.isPresent());
        assertTrue(doubleOptional.get() == 1.0D);
    }

    @Test
    public void testAbsents() {
        DataContainer container = new MemoryDataContainer();
        DataQuery testQuery = new DataQuery("foo", "bar", "baz");
        assertTrue(!container.get(testQuery).isPresent());
        assertTrue(!container.getBoolean(testQuery).isPresent());
        assertTrue(!container.getBooleanList(testQuery).isPresent());
        assertTrue(!container.getByteList(testQuery).isPresent());
        assertTrue(!container.getCharacterList(testQuery).isPresent());
        assertTrue(!container.getDouble(testQuery).isPresent());
        assertTrue(!container.getDoubleList(testQuery).isPresent());
        assertTrue(!container.getFloatList(testQuery).isPresent());
        assertTrue(!container.getInt(testQuery).isPresent());
        assertTrue(!container.getIntegerList(testQuery).isPresent());
        assertTrue(!container.getList(testQuery).isPresent());
        assertTrue(!container.getLong(testQuery).isPresent());
        assertTrue(!container.getLongList(testQuery).isPresent());
        assertTrue(!container.getMapList(testQuery).isPresent());
        assertTrue(!container.getShortList(testQuery).isPresent());
        assertTrue(!container.getString(testQuery).isPresent());
        assertTrue(!container.getStringList(testQuery).isPresent());
        assertTrue(!container.getView(testQuery).isPresent());
    }

    @Test
    public void testNumberedLists() {
        DataContainer container = new MemoryDataContainer();
        DataQuery testQuery = new DataQuery("foo", "bar", "baz");
        List<Integer> intList = ImmutableList.of(1, 2, 3, 4);
        container.set(testQuery, intList);
        assertTrue(container.getIntegerList(testQuery).isPresent());
        assertTrue(container.getIntegerList(testQuery).get().equals(intList));

        List<Double> doubleList = ImmutableList.of(1.0D, 2.0D, 3.0D, 4.0D);
        container.set(testQuery, doubleList);
        assertTrue(container.getDoubleList(testQuery).isPresent());
        assertTrue(container.getDoubleList(testQuery).get().equals(doubleList));

        List<Short> shortList = ImmutableList.of((short) 1, (short) 2, (short) 3, (short) 4);
        container.set(testQuery, shortList);
        assertTrue(container.getShortList(testQuery).isPresent());
        assertTrue(container.getShortList(testQuery).get().equals(shortList));

        List<Byte> byteList = ImmutableList.of((byte) 1, (byte) 2, (byte) 3, (byte) 4);
        container.set(testQuery, byteList);
        assertTrue(container.getByteList(testQuery).isPresent());
        assertTrue(container.getByteList(testQuery).get().equals(byteList));

    }

}
