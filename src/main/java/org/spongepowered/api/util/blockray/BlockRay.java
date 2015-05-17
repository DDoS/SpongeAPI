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
package org.spongepowered.api.util.blockray;

import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.extent.Extent;

public class BlockRay {

    private final Vector3d startLocation;
    private final Vector3d startDirection;
    private final Extent extent;
    private final BlockRayFilter filter;

    public BlockRay(Vector3d startLocation, Vector3d startDirection, Extent extent, BlockRayFilter filter) {
        this.startLocation = startLocation;
        this.startDirection = startDirection;
        this.extent = extent;
        this.filter = filter;
    }

    // traces this ray fully
    public static BlockRay trace() {
        return null;
    }

    public static Location getEndingLocation() {
        return null;
    }

    public static Iterable<Location> getIntersectingBlocks() {
        return null;
    }

    public static Iterable<Location> asDiscretePath() {
        return null;
    }

    public static BlockRay fromEntityLooking(Entity entity, BlockRayFilter filter) {
        return new BlockRay(
                entity.getLocation().getPosition(),
                entity.getRotation(),
                entity.getLocation().getExtent(),
                filter
        );
    }

    public static BlockRay fromEntityLooking(Entity entity) {
        return new BlockRay(
                entity.getLocation().getPosition(),
                entity.getRotation(),
                entity.getLocation().getExtent(),
                BlockRayFilter.ONLY_AIR
        );
    }

}
