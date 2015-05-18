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
import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.extent.Extent;

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
 * {@link BlockRayFilter#maxDistance(int)} to limit the target block to be within some
 * distance.</p>
 *
 * <p>A {@link BlockRayFilter} is modeled as a predicate taking three doubles and a block face,
 * (in the {@link #shouldContinue(double, double, double, Direction)} method) instead of using a
 * regular {@link com.google.common.base.Predicate} taking a tuple. This is to save object
 * creation, which can become very expensive if an individual ray cast goes through hundreds or
 * thousands of locations.</p>
 *
 * <p>Many {@link BlockRayFilter}s are not concerned with the individual positions within a block.
 * For convenience, a {@link DiscreteBlockRayFilter} class is provided that wraps
 * {@link BlockRayFilter}s that only care about discrete block locations.</p>
 */
public abstract class BlockRayFilter {

    /**
     * Called at the beginning of a ray cast. An instance should perform any setup required
     * in this method.
     *
     * <p>This method can only be called once on any one {@link BlockRayFilter} instance, and is
     * called at the beginning of a ray cast.</p>
     *
     * @param location The starting location of the ray cast
     * @param direction The direction of the ray cast
     */
    public abstract void start(Location location, Vector3d direction);

    /**
     * Called along each step of a ray cast to determine whether the ray cast should continue. A
     * result value of {@code true} indicates that the ray cast should keep going, while a result
     * value of {@code false} indicates that the ray cast should stop at the current block
     * location.
     *
     * <p>Due to the specifics of the ray casting algorithm, at least one of the given x, y, and z
     * coordinates on a specific invocation of this method will be a round, integer number. The
     * coordinate that is an integer is the axis of the block face that was passed through when the
     * ray entered, but this isn't enough information to determine the specific block face that was
     * passed through. For that reason, a {@link Direction} is provided, or the normal vector of
     * the block face that was passed through.</p>
     *
     * <p>The fractional parts of the two coordinates that are still floating points represent the
     * position along the block face where the ray cast entered the block. This is useful for more
     * granular collision checking with the ray, like with block models.</p>
     *
     * @param x The x coordinate of the location
     * @param y The y coordinate of the location
     * @param z The z coordinate of the location
     * @param blockFace The normal vector of the block face that was passed through
     * @return True to continue ray casting, false to stop
     */
    public abstract boolean shouldContinue(double x, double y, double z, Direction blockFace);

    /**
     * Composes this instance with the given {@link BlockRayFilter}, and returns an instance which
     * first checks with this instance, and then the given instance. This is essentially like doing
     * an AND operation between the two checks.
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
            public boolean shouldContinue(double x, double y, double z, Direction blockFace) {
                return self.shouldContinue(x, y, z, blockFace) && that.shouldContinue(x, y, z, blockFace);
            }
        };
    }

    /**
     * Represents a {@link BlockRayFilter} that has no concerns for coordinates within a block,
     * only with block coordinates themselves. This class wraps the
     * {@link #shouldContinue(double, double, double, Direction)} method from
     * {@link BlockRayFilter} with {@link #shouldContinue(Extent, int, int, int, Direction)}, which
     * takes integers instead of doubles and provides an additional {@link Extent} reference.
     *
     * <p>Many {@link BlockRayFilter}s, like {@link #ONLY_AIR} or {@link #maxDistance(int)}, do
     * not operate on the coordinates within a block, incurring unnecessary overhead. Instead, they
     * extend the {@link DiscreteBlockRayFilter} class to limit their continue checks to integer
     * coordinates.</p>
     */
    public static abstract class DiscreteBlockRayFilter extends BlockRayFilter {

        /**
         * The extent in which the block ray is being casted. Stored for convenience.
         */
        private Extent extent;

        @Override
        public void start(Location location, Vector3d direction) {
            extent = location.getExtent();
        }

        /**
         * Called along each step of a ray cast to determine whether the ray cast should continue.
         * A result value of {@code true} indicates that the ray cast should keep going, while a
         * result value of {@code false} indicates that the ray cast should stop at the current
         * block location.
         *
         * <p>The integer coordinates of the location in a specific call are provided, along with
         * a {@link Direction} representing the normal of the face that was passed through for the
         * current block check.</p>
         *
         * @param x The x coordinate of the location
         * @param y The y coordinate of the location
         * @param z The z coordinate of the location
         * @param blockFace The normal vector of the block face that was passed through
         * @return True to continue ray casting, false to stop
         */
        public abstract boolean shouldContinue(Extent extent, int x, int y, int z, Direction blockFace);

        @Override
        public boolean shouldContinue(double x, double y, double z, Direction blockFace) {
            return shouldContinue(extent, (int) x, (int) y, (int) z, blockFace);
        }

    }

    /**
     * A block type filter that only permits air as a transparent block.
     *
     * <p>This is provided for convenience, as the default behavior in previous systems was to pass
     * through air blocks only until a non-air block was hit.</p>
     */
    public static final BlockRayFilter ONLY_AIR = blockType(BlockTypes.AIR);

    /**
     * A filter that accepts no blocks. A {@link BlockRay} combined with no other filter than this
     * one could run endlessly.
     */
    public static final BlockRayFilter ALL = new DiscreteBlockRayFilter() {

        @Override
        public boolean shouldContinue(Extent extent, int x, int y, int z, Direction blockFace) {
            return true;
        }

    };

    /**
     * A filter that accepts no blocks. A {@link BlockRay} that uses this filter would terminate
     * immediately.
     */
    public static final BlockRayFilter NONE = new DiscreteBlockRayFilter() {

        @Override
        public boolean shouldContinue(Extent extent, int x, int y, int z, Direction blockFace) {
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

        return new DiscreteBlockRayFilter() {

            @Override
            public boolean shouldContinue(Extent extent, int x, int y, int z, Direction blockFace) {
                return extent.getBlockType(x, y, z).equals(type);
            }

        };

    }

    /**
     * A filter that stops at a certain integer distance. Since this filter does not care about
     * sub-block coordinates, the distances only need to be integers.
     *
     * <p>Note the behavior of a {@link BlockRay} under this filter: ray casting stops once the
     * distance is greater than the given distance, meaning that the ending location can at a
     * distance greater than the distance given. However, this filter still maintains that all
     * locations on the path are within the maximum distance.</p>
     *
     * @param distance The maximum distance allowed
     * @return The filter instance
     */
    public static BlockRayFilter maxDistance(int distance) {

        final int distanceSquared = distance * distance;

        return new DiscreteBlockRayFilter() {

            private int startX;
            private int startY;
            private int startZ;

            @Override
            public void start(Location location, Vector3d direction) {
                Vector3i position = location.getBlockPosition();
                this.startX = position.getX();
                this.startY = position.getY();
                this.startZ = position.getZ();
            }

            @Override
            public boolean shouldContinue(Extent extent, int x, int y, int z, Direction blockFace) {
                int deltaX = x - startX;
                int deltaY = y - startY;
                int deltaZ = z - startZ;
                return (deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) < distanceSquared;
            }
        };

    }

}
