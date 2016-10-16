/*
 * This file is part of FoxCore, licensed under the MIT License (MIT).
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

import net.foxdenstudio.sponge.foxcore.plugin.wand.IWand;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableData;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.ValueFactory;
import org.spongepowered.api.data.value.immutable.ImmutableValue;

import java.util.Optional;

import static net.foxdenstudio.sponge.foxcore.plugin.wand.data.WandKeys.*;

public class ImmutableWandData extends AbstractImmutableData<ImmutableWandData, WandData> {

    public static final ValueFactory VALUEFACTORY = Sponge.getRegistry().getValueFactory();

    private String type;
    private Integer id;
    private IWand wand;

    ImmutableWandData(IWand wand, int id, String type){
        this.wand = wand;
        this.id = id;
        this.type = type;
        registerGetters();
    }

    public ImmutableValue<String> getWandType() {
        return VALUEFACTORY.createValue(TYPE, type).asImmutable();
    }

    public ImmutableValue<IWand> getWand() {
        return VALUEFACTORY.createValue(WAND, wand).asImmutable();
    }

    @Override
    protected void registerGetters() {
        registerFieldGetter(TYPE, () -> this.type);
        registerKeyValue(TYPE, this::getWandType);
        registerFieldGetter(WAND, () -> this.wand);
        registerKeyValue(WAND, this::getWand);
    }

    @Override
    public <E> Optional<ImmutableWandData> with(Key<? extends BaseValue<E>> key, E value) {
        if (this.supports(key)) {
            return Optional.of(asMutable().set(key, value).asImmutable());
        } else return Optional.empty();
    }

    @Override
    public WandData asMutable() {
        return new WandData(wand, id, type);
    }

    @Override
    public int compareTo(ImmutableWandData o) {
        return this.type.compareTo(o.type);
    }

    @Override
    public int getContentVersion() {
        return 1;
    }

    @Override
    public DataContainer toContainer() {
        return super.toContainer()
                .set(TYPE.getQuery(), this.type)
                .set(ID.getQuery(), this.id)
                .set(WAND.getQuery(), this.wand);
    }
}
