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
import com.google.common.collect.UnmodifiableIterator;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.util.Collect;
import org.spongepowered.api.world.Location;

import java.util.ArrayList;
import java.util.List;

public class BlockRay {

    private final Location startLocation;
    private final Vector3d startDirection;
    private final BlockRayFilter filter;

    public BlockRay(Location startLocation, Vector3d startDirection, BlockRayFilter filter) {
        this.startLocation = startLocation;
        this.startDirection = startDirection;
        this.filter = filter;

        this.currentPosition = startLocation.getPosition();
        this.startDirectionNormal = startDirection.normalize();
    }

    private final Vector3d startDirectionNormal;

    private boolean finished = false;
    private Vector3d currentPosition;

    private Location endLocation;

    // traces this ray for one step
    private void trace() {

    }

    private List<Location> computedIntersecting = new ArrayList<Location>();

    private boolean traceUntilIntersecting() {
        int startSize = computedIntersecting.size();
        while (!finished && computedIntersecting.size() == startSize) {
            trace();
        }
        return computedIntersecting.size() > startSize;
    }

    public Iterable<Location> getIntersecting() {
        return Collect.iterableFromIterator(new UnmodifiableIterator<Location>() {

            @Override
            public boolean hasNext() {
                if (finished) {
                    return false;
                }
            }

            @Override
            public Location next() {
                return null;
            }
        });
    }

    private List<Location> computedDiscrete = new ArrayList<Location>();

    private boolean traceUntilDiscrete() {
        int startSize = computedDiscrete.size();
        while (!finished && computedDiscrete.size() == startSize) {
            trace();
        }
        return computedDiscrete.size() > startSize;
    }


    private void traceUntilEnd() {

    }

    public Location getStartLocation() {
        return startLocation;
    }

    public Location getEndLocation() {
        return null;
    }

    public Iterable<Location> getDiscrete() {
        return Collect.iterableFromIterator(new UnmodifiableIterator<Location>() {
            @Override
            public boolean hasNext() {
                return !finished;
            }

            @Override
            public Location next() {
                return null;
            }
        });
    }

    public static BlockRay fromEntityRotation(Entity entity, BlockRayFilter filter) {
        return new BlockRay(entity.getLocation(), entity.getRotation(), filter);
    }

    public static BlockRay fromEntityRotation(Entity entity) {
        return fromEntityRotation(entity, BlockRayFilter.ONLY_AIR);
    }

}
