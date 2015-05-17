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
import org.spongepowered.api.world.extent.Extent;

public abstract class BlockRayFilter {

    public abstract void start(double x, double y, double z, Vector3d direction, Extent extent);

    public abstract boolean shouldContinue(double x, double y, double z);

    public BlockRayFilter and(final BlockRayFilter that) {

        final BlockRayFilter self = this;

        return new BlockRayFilter() {
            @Override
            public void start(double x, double y, double z, Vector3d direction, Extent extent) {
                self.start(x, y, z, direction, extent);
                that.start(x, y, z, direction, extent);
            }

            @Override
            public boolean shouldContinue(double x, double y, double z) {
                return self.shouldContinue(x, y, z) && that.shouldContinue(x, y, z);
            }
        };
    }

    public static final BlockRayFilter ONLY_AIR = blockType(BlockTypes.AIR);

    public static final BlockRayFilter ALL = new BlockRayFilter() {
        @Override
        public void start(double x, double y, double z, Vector3d direction, Extent extent) {}

        @Override
        public boolean shouldContinue(double x, double y, double z) {
            return true;
        }
    };

    public static final BlockRayFilter NONE = new BlockRayFilter() {
        @Override
        public void start(double x, double y, double z, Vector3d direction, Extent extent) {}

        @Override
        public boolean shouldContinue(double x, double y, double z) {
            return false;
        }
    };

    public static BlockRayFilter blockType(final BlockType type) {

        return new BlockRayFilter() {

            private Extent extent;

            @Override
            public void start(double x, double y, double z, Vector3d direction, Extent extent) {
                this.extent = extent;
            }

            @Override
            public boolean shouldContinue(double x, double y, double z) {
                return extent.getBlockType((int) x, (int) y, (int) z).equals(type);
            }

        };

    }

    public static BlockRayFilter maxDistance(double distance) {

        final double distanceSquared = distance * distance;

        return new BlockRayFilter() {

            double startX;
            double startY;
            double startZ;

            @Override
            public void start(double x, double y, double z, Vector3d direction, Extent extent) {
                this.startX = x;
                this.startY = y;
                this.startZ = z;
            }

            @Override
            public boolean shouldContinue(double x, double y, double z) {
                double deltaX = x - startX;
                double deltaY = y - startY;
                double deltaZ = z - startZ;
                return (deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) < distanceSquared;
            }
        };

    }

}
