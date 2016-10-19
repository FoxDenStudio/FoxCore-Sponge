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

import net.foxdenstudio.sponge.foxcore.plugin.wand.FCWandRegistry;
import net.foxdenstudio.sponge.foxcore.plugin.wand.IWand;
import net.foxdenstudio.sponge.foxcore.plugin.wand.IWandFactory;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.ValueFactory;
import org.spongepowered.api.data.value.mutable.Value;

import java.util.Optional;
import java.util.Random;

import static com.google.common.base.Preconditions.checkNotNull;
import static net.foxdenstudio.sponge.foxcore.plugin.wand.data.WandKeys.*;

public class WandData extends AbstractData<WandData, ImmutableWandData> {

    public static final ValueFactory VALUEFACTORY = Sponge.getRegistry().getValueFactory();
    public static final Random random = new Random();

    private IWand wand;
    private int id;
    private String type;

    public WandData(IWand wand) {
        this.wand = wand;
        if(wand == null) {
            type = "";
        } else {
            type = wand.type();
        }
        registerGettersAndSetters();
    }

    public WandData() {
        this(null);
    }

    WandData(IWand wand, int id, String type){
        this.wand = wand;
        this.id = id;
        this.type = type;
        registerGettersAndSetters();
    }

    public Value<String> getWandType() {
        return VALUEFACTORY.createValue(TYPE, type);
    }

    public Value<IWand> getWand() {
        return VALUEFACTORY.createValue(WAND, wand);
    }

    public void setWand(IWand wand) {
        this.type = wand.type();
        this.wand = wand;
    }

    @Override
    protected void registerGettersAndSetters() {
        registerFieldGetter(TYPE, () -> this.type);
        registerKeyValue(TYPE, this::getWandType);
        registerFieldGetter(WAND, () -> this.wand);
        registerFieldSetter(WAND, this::setWand);
        registerKeyValue(WAND, this::getWand);
    }

    @Override
    public Optional<WandData> fill(DataHolder dataHolder, MergeFunction overlap) {
        WandData wandData = checkNotNull(overlap).merge(copy(),
                from(dataHolder.toContainer()).orElse(null));
        set(TYPE, wandData.get(TYPE).orElse(null));
        set(ID, wandData.get(ID).orElse(random.nextInt()));
        set(WAND, wandData.get(WAND).orElse(null));
        return Optional.of(this);
    }

    @Override
    public Optional<WandData> from(DataContainer container) {
        return from((DataView) container);
    }

    public Optional<WandData> from(DataView view) {
        if (!view.contains(TYPE.getQuery())) {
            return Optional.empty();
        }
        String type = view.getString(TYPE.getQuery()).orElse(null);
        IWandFactory factory = FCWandRegistry.getInstance().getBuilder(type);
        if (factory == null) {
            return Optional.empty();
        }

        if (view.contains(ID.getQuery())) {

        }
        DataQuery wandQuery = WAND.getQuery();
        DataView wandData = view.getView(wandQuery).orElse(view.createView(wandQuery));
        IWand wand = factory.build(wandData);
        if (wand == null) {
            return Optional.empty();
        }

        this.wand = wand;
        this.type = type;
        return Optional.of(this);
    }

    @Override
    public WandData copy() {
        return new WandData(wand, id, type);
    }

    @Override
    public ImmutableWandData asImmutable() {
        return new ImmutableWandData(wand, id, type);
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
