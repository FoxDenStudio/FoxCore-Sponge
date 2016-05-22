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
 *
 */

package net.foxdenstudio.sponge.foxcore.common.addons;

import net.foxdenstudio.sponge.foxcore.common.addons.annotations.Addon;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by d4rkfly3r on 5/22/2016.
 */
public final class ClassFinderTest {

    ClassFinder classFinder;

    @Before
    public void setUp() throws Exception {
        classFinder = new ClassFinder();
        classFinder.initialize();
    }

    @Test
    public void testGetClasses() throws Exception {
        final ArrayList<Class<?>> allClasses = classFinder.getAllClasses();
        Assert.assertNotNull(allClasses);
    }

    @Test
    public void testGetAllClasses() throws Exception {
        final List<Class<?>> classes = classFinder.getClasses(Addon.class);
        Assert.assertNotNull(classes);
    }
}