/*
 * This file is part of FoxGuard, licensed under the MIT License (MIT).
 *
 * Copyright (c) gravityfox - https://gravityfox.net/
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

package net.foxdenstudio.sponge.foxcore.plugin.wand.data;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.ValueFactory;
import org.spongepowered.api.data.value.mutable.Value;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;
import static net.foxdenstudio.sponge.foxcore.plugin.wand.data.WandKeys.WANDTYPE;

public class WandData extends AbstractData<WandData, ImmutableWandData> {

    public static final ValueFactory VALUEFACTORY = Sponge.getRegistry().getValueFactory();

    private WandType type;

    public WandData(WandType type) {
        this.type = type;
        registerGettersAndSetters();
    }

    public WandData() {
        this(WandType.POSITION);
    }

    public Value<WandType> getWandType() {
        return VALUEFACTORY.createValue(WANDTYPE, type);
    }

    public void setWandType(WandType type) {
        this.type = type;
    }

    @Override
    protected void registerGettersAndSetters() {
        registerFieldGetter(WANDTYPE, () -> this.type);
        registerFieldSetter(WANDTYPE, this::setWandType);
        registerKeyValue(WANDTYPE, this::getWandType);
    }

    @Override
    public Optional<WandData> fill(DataHolder dataHolder, MergeFunction overlap) {
        WandData wandData = checkNotNull(overlap).merge(copy(),
                from(dataHolder.toContainer()).orElse(null));
        return Optional.of(set(WANDTYPE, wandData.get(WANDTYPE).get()));
    }

    @Override
    public Optional<WandData> from(DataContainer container) {
        set(WANDTYPE, WandType.POSITION);
        if (container.contains(WANDTYPE.getQuery())) {
            set(WANDTYPE, WandType.valueOf((String) container.get(WANDTYPE.getQuery()).orElse(null)));
            return Optional.of(this);
        } else return Optional.empty();
    }

    @Override
    public WandData copy() {
        return new WandData(this.type);
    }

    @Override
    public ImmutableWandData asImmutable() {
        return new ImmutableWandData(this.type);
    }

    @Override
    public int compareTo(WandData o) {
        return this.type.compareTo(o.type);
    }

    @Override
    public int getContentVersion() {
        return 1;
    }

    @Override
    public DataContainer toContainer() {

        return super.toContainer().set(WANDTYPE.getQuery(), this.type.name());
    }
}
