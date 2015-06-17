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

import com.flowpowered.math.GenericMath;
import com.flowpowered.math.imaginary.Quaterniond;
import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.extent.Extent;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A block ray which traces a line and returns all block boundaries intersected in order,
 * starting from the start location. This class implements the {@link Iterator} interface
 * with the exception of {@link Iterator#remove()}.
 *
 * Filters determine at what location a {@link BlockRay} should stop. A filter
 * is called at the boundary of each new location that a {@link BlockRay} passes through in order
 * to determine whether the ray cast should continue or terminate at that location.
 *
 * <p>Any one instance of a {@link Predicate} should only be run on one path.
 * It is not specified that {@link Predicate}s have to be stateless, pure functions.
 * They are allowed to keep state along an individual path, based on the assertion that a
 * single instance is only called on one path.</p>
 *
 * <p>Filters are most useful for limiting the target block a player is looking
 * at based on some metric, like transparency, block model, or even distance. The standard
 * Bukkit-like behavior for finding the target block can be achieved with using
 * {@link BlockRay#ONLY_AIR_FILTER}, optionally combined with
 * {@link BlockRay#maxDistanceFilter(Vector3d, double)} to limit the target block to be within some
 * distance.</p>
 *
 * @see BlockRayHit
 */
public class BlockRay implements Iterator<BlockRayHit> {

    /**
     * A block type filter that only permits air as a transparent block.
     *
     * <p>This is provided for convenience, as the default behavior in previous systems was to pass
     * through air blocks only until a non-air block was hit.</p>
     */
    public static final Predicate<BlockRayHit> ONLY_AIR_FILTER = blockTypeFilter(BlockTypes.AIR);

    /**
     * A filter that accepts all blocks. A {@link BlockRay} combined with no other filter than this
     * one could run endlessly.
     */
    public static final Predicate<BlockRayHit> ALL_FILTER = new Predicate<BlockRayHit>() {

        @Override
        public boolean apply(BlockRayHit lastHit) {
            return true;
        }

    };

    private static final Vector3d X_POSITIVE = Vector3d.UNIT_X;
    private static final Vector3d X_NEGATIVE = X_POSITIVE.negate();
    private static final Vector3d Y_POSITIVE = Vector3d.UNIT_Y;
    private static final Vector3d Y_NEGATIVE = Y_POSITIVE.negate();
    private static final Vector3d Z_POSITIVE = Vector3d.UNIT_Z;
    private static final Vector3d Z_NEGATIVE = Z_POSITIVE.negate();
    private static final int DEFAULT_BLOCK_LIMIT = 1000;
    // Ending test predicate
    private final Predicate<BlockRayHit> filter;
    // Extent to iterate in
    private final Extent extent;
    // Starting position
    private final Vector3d position;
    // Direction of the ray
    private final Vector3d direction;
    // The directions the faces are passed through
    private final Vector3d xNormal, yNormal, zNormal;
    // The directions the edges and corners are passed through, lazily computed
    private Vector3d xyzNormal, xyNormal, xzNormal, yzNormal;
    // The plane increments for the direction
    private final int xPlaneIncrement, yPlaneIncrement, zPlaneIncrement;
    // The current coordinates
    private double xCurrent, yCurrent, zCurrent;
    // The current passed face
    private Vector3d normalCurrent;
    // The next plane values
    private int xPlaneNext, yPlaneNext, zPlaneNext;
    // The solutions for the nearest plane intersections
    private double xPlaneT, yPlaneT, zPlaneT;
    // Limits to help prevent infinite iteration
    private int blockLimit = DEFAULT_BLOCK_LIMIT, blockCount;
    // Last block hit
    private BlockRayHit hit;
    // If hasNext() is called, we need to move ahead to check the next hit
    private boolean ahead = false;

    /**
     * Constructs a BlockRay that traces from a starting location to an ending one.
     * This ray ends when it either reaches the location, the filter returns false
     * or the optional block limit has been reached.
     *
     * @param filter The filter condition for ending the trace
     * @param from The starting point
     * @param to The end point
     * @throws IllegalArgumentException If the extents from both points differ
     * @throws NullPointerException is any of the arguments are null
     */
    public BlockRay(Predicate<BlockRayHit> filter, Location from, Location to) {
        this(Predicates.and(filter, new TargetBlockFilter(to.getBlockPosition())), from, to.getPosition().sub(from.getPosition()));
        Preconditions.checkArgument(from.getExtent().equals(to.getExtent()), "Cannot iterate between extents");
    }

    /**
     * Constructs a BlockRay that traces in a given direction starting from a point.
     * This ray ends when the filter returns false or the optional block limit has been reached.
     *
     * @param filter The filter condition for ending the trace
     * @param start The starting point
     * @param direction The direction in which to trace
     * @throws IllegalArgumentException If direction is the zero vector
     * @throws NullPointerException is any of the arguments are null
     */
    public BlockRay(Predicate<BlockRayHit> filter, Location start, Vector3d direction) {
        Preconditions.checkArgument(direction.lengthSquared() != 0, "Direction must be a non-zero vector ('from' and 'to' cannot be the same)");

        this.filter = filter;

        this.extent = start.getExtent();
        this.position = start.getPosition();
        this.direction = direction.normalize();

        // Figure out the direction of the ray for each axis
        if (this.direction.getX() >= 0) {
            this.xPlaneIncrement = 1;
            this.xNormal = X_NEGATIVE;
        } else {
            this.xPlaneIncrement = -1;
            this.xNormal = X_POSITIVE;
        }
        if (this.direction.getY() >= 0) {
            this.yPlaneIncrement = 1;
            this.yNormal = Y_NEGATIVE;
        } else {
            this.yPlaneIncrement = -1;
            this.yNormal = Y_POSITIVE;
        }
        if (this.direction.getZ() >= 0) {
            this.zPlaneIncrement = 1;
            this.zNormal = Z_NEGATIVE;
        } else {
            this.zPlaneIncrement = -1;
            this.zNormal = Z_POSITIVE;
        }

        reset();
    }

    /**
     * Sets the maximum number of blocks to intersect before stopping.
     * This is a safeguard to prevent infinite iteration.
     * Default value is 1000. Use a negative value to disable this.
     *
     * @param blockLimit The block limit
     */
    public void setBlockLimit(int blockLimit) {
        this.blockLimit = blockLimit;
    }

    /**
     * Resets the iterator; it will iterate from the starting location again.
     */
    public final void reset() {
        // Start at the position
        this.xCurrent = this.position.getX();
        this.yCurrent = this.position.getY();
        this.zCurrent = this.position.getZ();

        // First planes are for the block that contains the coordinates
        this.xPlaneNext = GenericMath.floor(this.xCurrent);
        // noinspection SuspiciousNameCombination
        this.yPlaneNext = GenericMath.floor(this.yCurrent);
        this.zPlaneNext = GenericMath.floor(this.zCurrent);

        // Correct the next planes for the direction when inside the block
        if (this.xCurrent - this.xPlaneNext != 0 && this.direction.getX() >= 0) {
            this.xPlaneNext++;
        }
        if (this.yCurrent - this.yPlaneNext != 0 && this.direction.getY() >= 0) {
            this.yPlaneNext++;
        }
        if (this.zCurrent - this.zPlaneNext != 0 && this.direction.getZ() >= 0) {
            this.zPlaneNext++;
        }

        // Compute the first intersection solutions for each plane
        this.xPlaneT = (this.xPlaneNext - this.position.getX()) / this.direction.getX();
        this.yPlaneT = (this.yPlaneNext - this.position.getY()) / this.direction.getY();
        this.zPlaneT = (this.zPlaneNext - this.position.getZ()) / this.direction.getZ();

        // We start in the block, no plane has been entered yet
        this.normalCurrent = Vector3d.ZERO;

        // Reset the block
        this.blockCount = 0;
        this.ahead = false;
        this.hit = null;
    }

    private void advance() {
        // Check if we're ahead because of hasNext(), if so we don't need to do anything
        if (this.ahead) {
            this.ahead = false;
            return;
        }

        // Check the block limit if in use
        if (this.blockLimit >= 0 && this.blockCount >= this.blockLimit) {
            throw new NoSuchElementException("Block limit reached");
        }

        /*
            The ray can be modeled using the following parametric equations:
                x = d_x * t + p_x
                y = d_y * t + p_y
                z = d_z * t + p_z
            Where d is the direction vector, p the starting point and t is in |R.

            The block boundary grid can be modeled as an infinity of perpendicular planes
            on the x, y and z axes, on integer coordinates, spaced 1 unit away.

            Such a plane has an equation:
                A = n
            Where A is the axis label and n is in |Z

            The solution of the intersection between the above ray and such a plane is:
                n = d_A * t_s + p_A
                t_s = (n - p_A) / d_A

                x_s = d_x * t_s + p_x
                y_s = d_y * t_s + p_y
                z_s = d_z * t_s + p_z

            Where t_s is the solution parameter and x_s, y_s, z_s are the intersection coordinates.
            A small optimization is that x_A = n, which also helps with rounding errors.

            The iterator solves these equations and provides the solutions in increasing order with respect to t_s.
        */

        if (this.xPlaneT == this.yPlaneT) {
            if (this.xPlaneT == this.zPlaneT) {
                // xPlaneT, yPlaneT and zPlaneT are equal
                xyzIntersect();
            } else {
                // xPlaneT and yPlaneT are equal
                xyIntersect();
            }
        } else if (this.xPlaneT == this.zPlaneT) {
            // xPlaneT and zPlaneT are equal
            xzIntersect();
        } else if (this.yPlaneT == this.zPlaneT) {
            // yPlaneT and zPlaneT are equal
            yzIntersect();
        } else if (this.xPlaneT < this.yPlaneT) {
            if (this.xPlaneT < this.zPlaneT) {
                // xPlaneT is smallest
                xIntersect();
            } else {
                // zPlaneT is smallest
                zIntersect();
            }
        } else if (this.yPlaneT < this.zPlaneT) {
            // yPlaneT is smallest
            yIntersect();
        } else {
            // zPlaneT is smallest
            zIntersect();
        }

        // Check the block filter
        final BlockRayHit hit = new BlockRayHit(this.extent, this.xCurrent, this.yCurrent, this.zCurrent, this.direction, this.normalCurrent);
        if (this.filter.apply(hit)) {
            this.hit = hit;
            this.blockCount++;
        } else {
            throw new NoSuchElementException("Filter limit reached");
        }
    }

    @Override
    public boolean hasNext() {
        try {
            advance();
            ahead = true;
        } catch (NoSuchElementException exception) {
            return false;
        }
        return true;
    }

    @Override
    public BlockRayHit next() {
        advance();
        return this.hit;
    }

    private void xyzIntersect() {
        this.xCurrent = this.xPlaneNext;
        this.yCurrent = this.yPlaneNext;
        this.zCurrent = this.zPlaneNext;
        this.normalCurrent = getXYZNormal();
        // Prepare next intersection
        this.xPlaneNext += this.xPlaneIncrement;
        this.yPlaneNext += this.yPlaneIncrement;
        this.zPlaneNext += this.zPlaneIncrement;
        this.xPlaneT = (this.xPlaneNext - this.position.getX()) / this.direction.getX();
        this.yPlaneT = (this.yPlaneNext - this.position.getY()) / this.direction.getY();
        this.zPlaneT = (this.zPlaneNext - this.position.getZ()) / this.direction.getZ();
    }

    private void xyIntersect() {
        this.xCurrent = this.xPlaneNext;
        this.yCurrent = this.yPlaneNext;
        this.zCurrent = this.direction.getZ() * this.xPlaneT + this.position.getZ();
        this.normalCurrent = getXYNormal();
        // Prepare next intersection
        this.xPlaneNext += this.xPlaneIncrement;
        this.yPlaneNext += this.yPlaneIncrement;
        this.xPlaneT = (this.xPlaneNext - this.position.getX()) / this.direction.getX();
        this.yPlaneT = (this.yPlaneNext - this.position.getY()) / this.direction.getY();
    }

    private void xzIntersect() {
        this.xCurrent = this.xPlaneNext;
        this.yCurrent = this.direction.getY() * this.xPlaneT + this.position.getY();
        this.zCurrent = this.zPlaneNext;
        this.normalCurrent = getXZNormal();
        // Prepare next intersection
        this.xPlaneNext += this.xPlaneIncrement;
        this.zPlaneNext += this.zPlaneIncrement;
        this.xPlaneT = (this.xPlaneNext - this.position.getX()) / this.direction.getX();
        this.zPlaneT = (this.zPlaneNext - this.position.getZ()) / this.direction.getZ();
    }

    private void yzIntersect() {
        this.xCurrent = this.direction.getX() * this.yPlaneT + this.position.getX();
        this.yCurrent = this.yPlaneNext;
        this.zCurrent = this.zPlaneNext;
        this.normalCurrent = getYZNormal();
        // Prepare next intersection
        this.yPlaneNext += this.yPlaneIncrement;
        this.zPlaneNext += this.zPlaneIncrement;
        this.yPlaneT = (this.yPlaneNext - this.position.getY()) / this.direction.getY();
        this.zPlaneT = (this.zPlaneNext - this.position.getZ()) / this.direction.getZ();
    }

    private void xIntersect() {
        this.xCurrent = this.xPlaneNext;
        this.yCurrent = this.direction.getY() * this.xPlaneT + this.position.getY();
        this.zCurrent = this.direction.getZ() * this.xPlaneT + this.position.getZ();
        this.normalCurrent = this.xNormal;
        // Prepare next intersection
        this.xPlaneNext += this.xPlaneIncrement;
        this.xPlaneT = (this.xPlaneNext - this.position.getX()) / this.direction.getX();
    }

    private void yIntersect() {
        this.xCurrent = this.direction.getX() * this.yPlaneT + this.position.getX();
        this.yCurrent = this.yPlaneNext;
        this.zCurrent = this.direction.getZ() * this.yPlaneT + this.position.getZ();
        this.normalCurrent = this.yNormal;
        // Prepare next intersection
        this.yPlaneNext += this.yPlaneIncrement;
        this.yPlaneT = (this.yPlaneNext - this.position.getY()) / this.direction.getY();
    }

    private void zIntersect() {
        this.xCurrent = this.direction.getX() * this.zPlaneT + this.position.getX();
        this.yCurrent = this.direction.getY() * this.zPlaneT + this.position.getY();
        this.zCurrent = this.zPlaneNext;
        this.normalCurrent = this.zNormal;
        // Prepare next intersection
        this.zPlaneNext += this.zPlaneIncrement;
        this.zPlaneT = (this.zPlaneNext - this.position.getZ()) / this.direction.getZ();
    }

    private Vector3d getXYZNormal() {
        if (this.xyzNormal == null) {
            this.xyzNormal = this.xNormal.add(this.yNormal).add(this.zNormal).normalize();
        }
        return this.xyzNormal;
    }

    private Vector3d getXYNormal() {
        if (this.xyNormal == null) {
            this.xyNormal = this.xNormal.add(this.yNormal).normalize();
        }
        return this.xyNormal;
    }

    private Vector3d getXZNormal() {
        if (this.xzNormal == null) {
            this.xzNormal = this.xNormal.add(this.zNormal).normalize();
        }
        return this.xzNormal;
    }

    private Vector3d getYZNormal() {
        if (this.yzNormal == null) {
            this.yzNormal = this.yNormal.add(this.zNormal).normalize();
        }
        return this.yzNormal;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Removal is not supported by this iterator");
    }

    // TODO: remove me once done
    public static void test(CommandSource source) {
        final Player player = (Player) source;
        for (BlockRayHit lastHit : from(player).filter(ONLY_AIR_FILTER)) {
            if (!lastHit.getLocation().hasBlock()) {
                break;
            }
            //noinspection ConstantConditions
            lastHit.getLocation().replaceWith(BlockTypes.DIAMOND_BLOCK);
        }
    }

    /**
     * Initializes a block ray builder, starting with the starting location.
     *
     * @param start The starting location
     * @return A new block ray builder
     * @see #BlockRay(Predicate, Location, Location)
     * @see #BlockRay(Predicate, Location, Vector3d)
     */
    public static BlockRayBuilder from(Location start) {
        Preconditions.checkArgument(start != null, "Start cannot be null");
        return new BlockRayBuilder(start);
    }

    /**
     * Initializes a block ray builder for the entity's eye.
     * This sets both the starting point and direction.
     *
     * @param entity The entity
     * @return A new block ray builder
     */
    public static BlockRayBuilder from(Entity entity) {
        final Vector3d rotation = entity.getRotation();
        final Vector3d direction = Quaterniond.fromAxesAnglesDeg(rotation.getY(), 360 - rotation.getX(), rotation.getZ()).getDirection();
        // TODO: this needs to use the eye location
        return from(entity.getLocation()).direction(direction);
    }

    /**
     * A builder for block ray, which also implements {@link Iterable} which makes it
     * useful for 'advanced for loops'. Use {@link #from(Location)} to get an instance.
     */
    public static class BlockRayBuilder implements Iterable<BlockRayHit> {

        private final Location start;
        private Predicate<BlockRayHit> filter = null;
        private Location end = null;
        private Vector3d direction = null;
        private int blockLimit = DEFAULT_BLOCK_LIMIT;

        private BlockRayBuilder(Location start) {
            this.start = start;
        }

        /**
         * Adds a filter to the block ray. This is optional and can only be done once.
         *
         * @param filter The filter to use
         * @return This for chained calls
         */
        public BlockRayBuilder filter(Predicate<BlockRayHit> filter) {
            Preconditions.checkArgument(this.filter == null, "Filter has already been set");
            Preconditions.checkArgument(filter != null, "Filter cannot be null");
            this.filter = filter;
            return this;
        }

        /**
         * Sets the ending location. This or setting the direction is required and can only be done once.
         *
         * @param end The ending location
         * @return This for chained calls
         */
        public BlockRayBuilder to(Location end) {
            Preconditions.checkArgument(this.end == null, "End point has already been set");
            Preconditions.checkArgument(this.direction == null, "End point and direction cannot be both set");
            Preconditions.checkArgument(end != null, "End cannot be null");
            this.end = end;
            return this;
        }

        /**
         * Sets the direction. This or setting the ending location is required and can only be done once.
         *
         * @param direction The direction
         * @return This for chained calls
         */
        public BlockRayBuilder direction(Vector3d direction) {
            Preconditions.checkArgument(this.direction == null, "Direction has already been set");
            Preconditions.checkArgument(this.end == null, "Direction and end point cannot be both set");
            Preconditions.checkArgument(direction != null, "Direction cannot be null");
            this.direction = direction;
            return this;
        }

        /**
         * Sets the maximum number of blocks to intersect before stopping.
         * This is a safeguard to prevent infinite iteration.
         * Default value is 1000. Use a negative value to disable this.
         *
         * @param blockLimit The block limit
         */
        public BlockRayBuilder blockLimit(int blockLimit) {
            this.blockLimit = blockLimit;
            return this;
        }

        /**
         * Returns a block ray build from the settings. An ending location or direction needs to have been set.
         *
         * @return A block ray
         */
        public BlockRay build() {
            Preconditions.checkState(this.end != null || this.direction != null, "Either end point or direction needs to be set");
            final Predicate<BlockRayHit> filter = this.filter == null ? ALL_FILTER : this.filter;
            final BlockRay blockRay = this.end == null ? new BlockRay(filter, this.start, this.direction)
                : new BlockRay(filter, this.start, this.end);
            blockRay.setBlockLimit(blockLimit);
            return blockRay;
        }

        @Override
        public Iterator<BlockRayHit> iterator() {
            return build();
        }
    }

    /**
     * A filter that only allows blocks of a certain block type.
     *
     * @param type The type of blocks to allow
     * @return The filter instance
     */
    public static Predicate<BlockRayHit> blockTypeFilter(final BlockType type) {

        return new Predicate<BlockRayHit>() {

            @Override
            public boolean apply(BlockRayHit lastHit) {
                return lastHit.getLocation().getType().equals(type);
            }

        };

    }

    /**
     * A filter that stops at a certain distance.
     *
     * <p>Note the behavior of a {@link BlockRay} under this filter: ray casting stops once the
     * distance is greater than the given distance, meaning that the ending location can at a
     * distance greater than the distance given. However, this filter still maintains that all
     * locations on the path are within the maximum distance.</p>
     *
     * @param start The starting point of the ray
     * @param distance The maximum distance allowed
     * @return The filter instance
     */
    public static Predicate<BlockRayHit> maxDistanceFilter(final Vector3d start, double distance) {

        final double distanceSquared = distance * distance;

        return new Predicate<BlockRayHit>() {

            @Override
            public boolean apply(BlockRayHit lastHit) {
                final double deltaX = lastHit.getX() - start.getX();
                final double deltaY = lastHit.getY() - start.getY();
                final double deltaZ = lastHit.getZ() - start.getZ();
                return deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ < distanceSquared;
            }
        };

    }

    private static class TargetBlockFilter implements Predicate<BlockRayHit> {

        private final Vector3i target;

        private TargetBlockFilter(Vector3i target) {
            this.target = target;
        }

        @Override
        public boolean apply(BlockRayHit lastHit) {
            return !lastHit.getBlockPosition().equals(this.target);
        }
    }
}
