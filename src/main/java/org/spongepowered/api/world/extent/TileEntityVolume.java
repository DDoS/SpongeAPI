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
package org.spongepowered.api.world.extent;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.world.extent.worker.MutableBlockVolumeWorker;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * A mutable block volume that also contains {@link TileEntity} instances.
 */
public interface TileEntityVolume extends MutableBlockVolume {

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
     * tile entities are filtered by the given {@link Predicate} before being
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
     * Gets the tile entity at the given position, if it exists.
     *
     * @param position The position
     * @return The tile entity, or {@link Optional#empty()}
     */
    default Optional<TileEntity> getTileEntity(Vector3i position) {
        return getTileEntity(position.getX(), position.getY(), position.getZ());
    }

    /**
     * Get the tile entity at the given position, if it exists.
     *
     * @param x The X position
     * @param y The Y position
     * @param z The Z position
     * @return The tile entity, or {@link Optional#empty()}
     */
    Optional<TileEntity> getTileEntity(int x, int y, int z);

    @Override
    MutableBlockVolumeWorker<? extends TileEntityVolume> getBlockWorker();

}
