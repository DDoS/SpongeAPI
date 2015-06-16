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
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.extent.Extent;

import java.util.Iterator;

/**
 * A block ray which traces a line and returns all blocks intersected in order,
 * starting from the start location. A {@link BlockRayFilter} is used as a predicate
 * to know when the trace should be ended. This class implements the {@link Iterator}
 * interface with the exception of {@link Iterator#remove()}.
 */
public class BlockRay implements Iterator<Location> {

    // Ending test predicate
    private final BlockRayFilter filter;
    // Extent to iterate in
    private final Extent extent;
    // Starting position
    private final Vector3d position;
    // Direction of the ray
    private final Vector3d direction;
    // The directions the faces are passed through
    private final Direction xFace, yFace, zFace;
    // The plane increments for the direction
    private final int xPlaneIncrement, yPlaneIncrement, zPlaneIncrement;
    // The current coordinates
    private double xCurrent, yCurrent, zCurrent;
    // The current passed face
    private Direction faceCurrent;
    // The next plane values
    private int xPlaneNext, yPlaneNext, zPlaneNext;
    // The solutions for the nearest plane intersections
    private double xPlaneT, yPlaneT, zPlaneT;

    /**
     * Constructs a BlockRay that traces from a starting location to an ending one.
     * This ray ends when it either reaches the location of the filter returns false.
     *
     * @param filter The filter condition for ending the trace
     * @param from The starting point
     * @param to The end point
     * @throws IllegalArgumentException If the extents from both points differ
     * @throws NullPointerException is any of the arguments are null
     */
    public BlockRay(BlockRayFilter filter, Location from, Location to) {
        this(filter.and(new TargetBlockFilter(to.getBlockPosition())), from, to.getPosition().sub(from.getPosition()));
        Preconditions.checkArgument(from.getExtent().equals(to.getExtent()), "Cannot iterate between extents");
    }

    /**
     * Constructs a BlockRay that traces in a given direction starting from a point.
     * This ray ends when the filter returns false.
     *
     * @param filter The filter condition for ending the trace
     * @param start The starting point
     * @param direction The direction in which to trace
     * @throws IllegalArgumentException If direction is the zero vector
     * @throws NullPointerException is any of the arguments are null
     */
    public BlockRay(BlockRayFilter filter, Location start, Vector3d direction) {
        Preconditions.checkArgument(direction.lengthSquared() != 0, "Direction must be a non-zero vector ('from' and 'to' cannot be the same)");

        this.filter = filter;

        this.extent = start.getExtent();
        this.position = start.getPosition();
        this.direction = direction.normalize();

        // Figure out the direction of the ray for each axis
        if (this.direction.getX() >= 0) {
            this.xPlaneIncrement = 1;
            this.xFace = Direction.EAST;
        } else {
            this.xPlaneIncrement = -1;
            this.xFace = Direction.WEST;
        }
        if (this.direction.getY() >= 0) {
            this.yPlaneIncrement = 1;
            this.yFace = Direction.UP;
        } else {
            this.yPlaneIncrement = -1;
            this.yFace = Direction.DOWN;
        }
        if (this.direction.getZ() >= 0) {
            this.zPlaneIncrement = 1;
            this.zFace = Direction.SOUTH;
        } else {
            this.zPlaneIncrement = -1;
            this.zFace = Direction.NORTH;
        }

        reset();
    }

    /**
     * Resets the iterator; it will iterate from the start location again.
     */
    public final void reset() {
        // Start at the position
        this.xCurrent = this.position.getX();
        this.yCurrent = this.position.getY();
        this.zCurrent = this.position.getZ();

        // First plane is the block that contains the coordinate,
        // compute the intersection solution for the next plane
        this.xPlaneNext = GenericMath.floor(this.xCurrent) + this.xPlaneIncrement;
        this.xPlaneT = (this.xPlaneNext - this.position.getX()) / this.direction.getX();

        // noinspection SuspiciousNameCombination
        this.yPlaneNext = GenericMath.floor(this.yCurrent) + this.yPlaneIncrement;
        this.yPlaneT = (this.yPlaneNext - this.position.getY()) / this.direction.getY();

        this.zPlaneNext = GenericMath.floor(this.zCurrent) + this.zPlaneIncrement;
        this.zPlaneT = (this.zPlaneNext - this.position.getZ()) / this.direction.getZ();

        // We start in the block, no plane has been entered yet
        this.faceCurrent = Direction.NONE;
    }

    @Override
    public boolean hasNext() {
        return this.filter.shouldContinue(this.xCurrent, this.yCurrent, this.zCurrent, this.faceCurrent);
    }

    @Override
    public Location next() {
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

        if (this.xPlaneT < this.yPlaneT) {
            if (this.xPlaneT < this.zPlaneT) {
                // xPlaneT is smallest
                this.xCurrent = this.xPlaneNext;
                this.yCurrent = this.direction.getY() * this.xPlaneT + this.position.getY();
                this.zCurrent = this.direction.getZ() * this.xPlaneT + this.position.getZ();
                this.faceCurrent = xFace;
                // Prepare next intersection
                this.xPlaneNext += this.xPlaneIncrement;
                this.xPlaneT = (this.xPlaneNext - this.position.getX()) / this.direction.getX();
            } else {
                // zPlaneT is smallest
                this.xCurrent = this.direction.getX() * this.zPlaneT + this.position.getX();
                this.yCurrent = this.direction.getY() * this.zPlaneT + this.position.getY();
                this.zCurrent = this.zPlaneNext;
                this.faceCurrent = this.zFace;
                // Prepare next intersection
                this.zPlaneNext += this.zPlaneIncrement;
                this.zPlaneT = (this.zPlaneNext - this.position.getZ()) / this.direction.getZ();
            }
        } else if (this.yPlaneT < this.zPlaneT) {
            // yPlaneT is smallest
            this.xCurrent = this.direction.getX() * this.yPlaneT + this.position.getX();
            this.yCurrent = this.yPlaneNext;
            this.zCurrent = this.direction.getZ() * this.yPlaneT + this.position.getZ();
            this.faceCurrent = this.yFace;
            // Prepare next intersection
            this.yPlaneNext += this.yPlaneIncrement;
            this.yPlaneT = (this.yPlaneNext - this.position.getY()) / this.direction.getY();
        } else {
            // zPlaneT is smallest
            this.xCurrent = this.direction.getX() * this.zPlaneT + this.position.getX();
            this.yCurrent = this.direction.getY() * this.zPlaneT + this.position.getY();
            this.zCurrent = this.zPlaneNext;
            this.faceCurrent = this.zFace;
            // Prepare next intersection
            this.zPlaneNext += this.zPlaneIncrement;
            this.zPlaneT = (this.zPlaneNext - this.position.getZ()) / this.direction.getZ();
        }

        return new Location(this.extent, this.xCurrent, this.yCurrent, this.zCurrent);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Removal is not supported by this iterator");
    }

    // TODO: remove me when done
    public static void test(CommandSource source) {
        final Player player = (Player) source;
        final Vector3d rotation = player.getRotation();
        for (Location location : from(player.getLocation()).direction(Quaterniond.fromAxesAnglesDeg(rotation.getY(), rotation.getX(), rotation.getZ())
            .getDirection())) {
            if (!location.hasBlock()) {
                break;
            }
            System.out.println(location.getPosition());
            location.replaceWith(BlockTypes.DIAMOND_BLOCK);
        }
    }

    /**
     * Initializes a block ray build, starting with the starting location.
     *
     * @param start The starting location
     * @return A new block ray builder
     */
    public static BlockRayBuilder from(Location start) {
        Preconditions.checkArgument(start != null, "Start cannot be null");
        return new BlockRayBuilder(start);
    }

    /**
     * A builder for block ray, which also implements {@link Iterable} which makes it
     * useful for 'advanced for loops'. Use {@link #from(Location)} to get an instance.
     */
    public static class BlockRayBuilder implements Iterable<Location> {

        private final Location start;
        private BlockRayFilter filter = null;
        private Location end = null;
        private Vector3d direction = null;

        private BlockRayBuilder(Location start) {
            this.start = start;
        }

        /**
         * Adds a filter to the block ray. This is optional and can only be done once.
         *
         * @param filter The filter to use
         * @return This for chained calls
         */
        public BlockRayBuilder filter(BlockRayFilter filter) {
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
         * Returns a block ray build from the settings. An ending location or direction needs to have been set.
         *
         * @return A block ray
         */
        public BlockRay build() {
            Preconditions.checkState(this.end != null || this.direction != null, "Either end point or direction needs to be set");
            final BlockRayFilter filter = this.filter == null ? BlockRayFilter.ALL : this.filter;
            return this.end == null ? new BlockRay(filter, this.start, this.direction) : new BlockRay(filter, this.start, this.end);
        }

        @Override
        public Iterator<Location> iterator() {
            return build();
        }
    }

    private static class TargetBlockFilter extends BlockRayFilter.DiscreteBlockRayFilter {

        private final Vector3i target;

        private TargetBlockFilter(Vector3i target) {
            this.target = target;
        }

        @Override
        public boolean shouldContinue(Extent extent, int x, int y, int z, Direction blockFace) {
            return this.target.getX() != x || this.target.getY() != y || this.target.getZ() != z;
        }
    }
}
