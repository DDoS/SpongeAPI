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
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.world.Location;

/**
 * Represents a filter that determines at what location a {@link BlockRay} should stop. This filter
 * is called at the boundary of each new location that a {@link BlockRay} passes through in order
 * to determine whether the ray cast should continue or terminate at that location.
 *
 * <p>Any one instance of a {@link BlockRayFilter} should only be run on one path, meaning that the
 * {@link #start(Location, Vector3d)} method should only be called once. It is not specified that
 * {@link BlockRayFilter}s have to be stateless, pure functions. They are allowed to keep state
 * along an individual path, based on the assertion that a single instance is only called on one
 * path.</p>
 *
 * <p>{@link BlockRayFilter}s are most useful for limiting the target block a player is looking
 * at based on some metric, like transparency, block model, or even distance. The standard
 * Bukkit-like behavior for finding the target block can be achieved with using
 * {@link BlockRayFilter#ONLY_AIR}, optionally combined with
 * {@link BlockRayFilter#maxDistance(double)} to limit the target block to be within some
 * distance.</p>
 *
 * <p>A {@link BlockRayFilter} is modeled as a predicate taking three doubles and a block face,
 * (in the {@link #shouldContinue(BlockRayHit)} method) instead of using a
 * regular {@link com.google.common.base.Predicate} taking a tuple. This is to save object
 * creation, which can become very expensive if an individual ray cast goes through hundreds or
 * thousands of locations.</p>
 */
public abstract class BlockRayFilter {

    /**
     * Called at the beginning of a ray cast. An instance should perform any setup required
     * in this method. The default implementation does nothing
     *
     * <p>This method can only be called once on any one {@link BlockRayFilter} instance, and is
     * called at the beginning of a ray cast.</p>
     *
     * @param location The starting location of the ray cast
     * @param direction The direction of the ray cast
     */
    public void start(Location location, Vector3d direction) {
    }

    /**
     * Called along each step of a ray cast to determine whether the ray cast should continue. A
     * result value of {@code true} indicates that the ray cast should keep going, while a result
     * value of {@code false} indicates that the ray cast should stop at the current block
     * location.
     *
     * <p>This method provides the instance of the last block hit, which contains information
     * about the exact intersection coordinates, the block face hit and the affected block.
     * If no blocks have been hit yet, this will be the starting position with normal of {@link Vector3d#ZERO}.</p>
     *
     * @param lastHit The last block hit
     * @return True to continue ray casting, false to stop
     * @see BlockRayHit
     */
    public abstract boolean shouldContinue(BlockRayHit lastHit);

    /**
     * Composes this instance with the given {@link BlockRayFilter}, and returns an instance which
     * first checks with this instance, and then the given instance. This is essentially an AND
     * operation between the two checks.
     *
     * @param that The other {@link BlockRayFilter} to compose with
     * @return The composed {@link BlockRayFilter}
     */
    public BlockRayFilter and(final BlockRayFilter that) {

        final BlockRayFilter self = this;

        return new BlockRayFilter() {

            @Override
            public void start(Location location, Vector3d direction) {
                self.start(location, direction);
                that.start(location, direction);
            }

            @Override
            public boolean shouldContinue(BlockRayHit lastHit) {
                return self.shouldContinue(lastHit) && that.shouldContinue(lastHit);
            }
        };
    }

    /**
     * Composes this instance with the given {@link BlockRayFilter}, and returns an instance which
     * checks with this instance or the given instance. This is essentially an OR operation between
     * the two checks.
     *
     * @param that The other {@link BlockRayFilter} to compose with
     * @return The composed {@link BlockRayFilter}
     */
    public BlockRayFilter or(final BlockRayFilter that) {

        final BlockRayFilter self = this;

        return new BlockRayFilter() {

            @Override
            public void start(Location location, Vector3d direction) {
                self.start(location, direction);
                that.start(location, direction);
            }

            @Override
            public boolean shouldContinue(BlockRayHit lastHit) {
                return self.shouldContinue(lastHit) || that.shouldContinue(lastHit);
            }
        };
    }

    /**
     * Inverts this instance with the given {@link BlockRayFilter}, and returns an instance which
     * first continues only when {@link BlockRayFilter#shouldContinue(BlockRayHit)}
     * returns false. This is essentially a NOT operation on the check.
     *
     * @return The inverted {@link BlockRayFilter}
     */
    public BlockRayFilter not() {

        final BlockRayFilter self = this;

        return new BlockRayFilter() {

            @Override
            public void start(Location location, Vector3d direction) {
                self.start(location, direction);
            }

            @Override
            public boolean shouldContinue(BlockRayHit lastHit) {
                return !self.shouldContinue(lastHit);
            }
        };
    }

    /**
     * A block type filter that only permits air as a transparent block.
     *
     * <p>This is provided for convenience, as the default behavior in previous systems was to pass
     * through air blocks only until a non-air block was hit.</p>
     */
    public static final BlockRayFilter ONLY_AIR = blockType(BlockTypes.AIR);

    /**
     * A filter that accepts all blocks. A {@link BlockRay} combined with no other filter than this
     * one could run endlessly.
     */
    public static final BlockRayFilter ALL = new BlockRayFilter() {

        @Override
        public boolean shouldContinue(BlockRayHit lastHit) {
            return true;
        }

    };

    /**
     * A filter that accepts no blocks. A {@link BlockRay} that uses this filter would terminate
     * immediately.
     */
    public static final BlockRayFilter NONE = new BlockRayFilter() {

        @Override
        public boolean shouldContinue(BlockRayHit lastHit) {
            return false;
        }

    };

    /**
     * A filter that only allows blocks of a certain block type.
     *
     * @param type The type of blocks to allow
     * @return The filter instance
     */
    public static BlockRayFilter blockType(final BlockType type) {

        return new BlockRayFilter() {

            @Override
            public boolean shouldContinue(BlockRayHit lastHit) {
                return lastHit.getExtent().getBlockType(lastHit.getBlockPosition()).equals(type);
            }

        };

    }

    /**
     * A filter that stops at a certain integer distance.
     *
     * <p>Note the behavior of a {@link BlockRay} under this filter: ray casting stops once the
     * distance is greater than the given distance, meaning that the ending location can at a
     * distance greater than the distance given. However, this filter still maintains that all
     * locations on the path are within the maximum distance.</p>
     *
     * @param distance The maximum distance allowed
     * @return The filter instance
     */
    public static BlockRayFilter maxDistance(double distance) {

        final double distanceSquared = distance * distance;

        return new BlockRayFilter() {

            private double startX;
            private double startY;
            private double startZ;

            @Override
            public void start(Location location, Vector3d direction) {
                final Vector3d position = location.getPosition();
                this.startX = position.getX();
                this.startY = position.getY();
                this.startZ = position.getZ();
            }

            @Override
            public boolean shouldContinue(BlockRayHit lastHit) {
                final double deltaX = lastHit.getX() - this.startX;
                final double deltaY = lastHit.getY() - this.startY;
                final double deltaZ = lastHit.getZ() - this.startZ;
                return (deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) < distanceSquared;
            }
        };

    }

}
