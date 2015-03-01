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
package org.spongepowered.api.world.extent;

import com.google.common.base.Predicate;
import org.spongepowered.api.block.data.TileEntity;

import java.util.Collection;

/**
 * A volume containing {@link TileEntity}s.
 */
public interface TileVolume {

    /**
     * Return a collection of tile entities contained within this volume,
     * possibly only returning tile entities only in loaded areas.
     *
     * <p>For world implementations, only some parts of the world is usually
     * loaded, so this method will only return tile entities within those loaded
     * parts.</p>
     *
     * @return A collection of entities
     */
    Collection<TileEntity> getTileEntities();

    /**
     * Return a collection of tile entities contained within this volume,
     * possibly only returning tile entities only in loaded areas. The returned
     * tile entities are filtered by the given {@link com.google.common.base.Predicate} before being
     * returned.
     *
     * <p>For world implementations, only some parts of the world is usually
     * loaded, so this method will only return tile entities within those loaded
     * parts.</p>
     *
     * @param filter The filter to apply to the returned entities
     * @return A collection of filtered entities
     */
    Collection<TileEntity> getTileEntities(Predicate<TileEntity> filter);

    /**
     * Spawns a tile entity using the already set properties (extent, position, rotation).
     *
     * @param tileEntity The tile entity to spawn
     * @return True if successful, false otherwise
     */
    boolean spawnTileEntity(TileEntity tileEntity);

}
