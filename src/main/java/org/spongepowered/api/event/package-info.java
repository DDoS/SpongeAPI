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

/**
 * The Sponge Event Structure is easy to understand if you think of base events as being
 * "sources".
 *
 * Their names are composed as Source|Target|Action.
 *
 * ie. BlockEntityInteractEvent is when a source (block) selects a target (entity) and interacts (verb).
 *
 * Source: {@link org.spongepowered.api.block.BlockState} or "Block"
 * Target: {@link org.spongepowered.api.entity.Entity} or "Entity"
 * Action: Interact or some verb (Use, Break, etc)
 *
 * There is one exception to this rule. Should an event have a target the same as its source, it
 * is composed as Source|Action|Target.
 *
 * ie. BlockInteractBlockEvent is when a source (block) interacts (verb) the same type of target (block)
 *
 * Source: {@link org.spongepowered.api.block.BlockState} or "Block"
 * Action: Interact or some verb (User, Break, etc)
 * Target: {@link org.spongepowered.api.block.BlockState} or "Block" (the ending type, same as the source)
 */
@org.spongepowered.api.util.annotation.NonnullByDefault package org.spongepowered.api.event;
