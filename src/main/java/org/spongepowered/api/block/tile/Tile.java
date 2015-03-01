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

package org.spongepowered.api.block.tile;

import org.spongepowered.api.block.BlockLoc;
import org.spongepowered.api.service.persistence.DataSerializable;
import org.spongepowered.api.world.World;

/**
 * Represents an abstract Tile Entity.
 */
public interface Tile extends DataSerializable {

    /**
     * Gets the world that this Tile is within.
     *
     * @return The world
     */
    World getWorld();

    /**
     * Gets the BlockLoc that this tile is in.
     *
     * @return The BlockLoc
     */
    BlockLoc getBlock();

    /**
     * Checks for whether the tile is currently valid or not.
     *
     * @return True if the tile is valid, false if
     */
    boolean isValid();

    /**
     * Changes the validation of this tile.
     *
     * <p>If the tile entity is invalid, no processing will be done on the tile entity
     * until it either becomes valid or is reset on the next tick.</p>
     *
     * <p>If the tile entity is valid, then processing can continue and the tile entity
     * will not be reset on the next tick.</p>
     *
     * @param valid True if the tile should be validated, or false if it should be invalidated
     */
    void setValid(boolean valid);
    
}
