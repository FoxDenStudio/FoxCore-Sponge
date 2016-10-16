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
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.KeyFactory;
import org.spongepowered.api.data.value.mutable.Value;

public class WandKeys {

    public static final Key<Value<String>> TYPE = KeyFactory.makeSingleKey(
            String.class,
            Value.class,
            DataQuery.of("type")
    );

    public static final Key<Value<Integer>> ID = KeyFactory.makeSingleKey(
            Integer.class,
            Value.class,
            DataQuery.of("id")
    );

    public static final Key<Value<IWand>> WAND = KeyFactory.makeSingleKey(
            IWand.class,
            Value.class,
            DataQuery.of("wand")
    );

    // API 5 Code:
    /*
    public static final Key<Value<String>> TYPE = KeyFactory.makeSingleKey(
            TypeToken.of(String.class),
            new TypeToken<Value<String>>() {},
            DataQuery.of("type"),
            "foxcore:wandtype",
            "Wand Type");

    public static final Key<Value<Integer>> ID = KeyFactory.makeSingleKey(
            TypeToken.of(Integer.class),
            new TypeToken<Value<Integer>>() {},
            DataQuery.of("id"),
            "foxcore:wandID",
            "Wand ID"
    );

    public static final Key<Value<IWand>> WAND = KeyFactory.makeSingleKey(
            TypeToken.of(IWand.class),
            new TypeToken<Value<IWand>>() {},
            DataQuery.of("wand"),
            "foxcore:wanddata",
            "Wand Data"
    );
    */
}
