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

package net.foxdenstudio.sponge.foxcore.plugin.command.util;

import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.Optional;

public final class ProcessResult {

    private static final ProcessResult SUCCESS = of(true);
    private static final ProcessResult FAILURE = of(false);

    private final boolean success;
    private final @Nullable Text message;

    private ProcessResult(boolean success, @Nullable Text message) {
        this.success = success;
        this.message = message;
    }

    public static ProcessResult of(boolean success) {
        return new ProcessResult(success, null);
    }

    public static ProcessResult of(boolean success, Text message) {
        return new ProcessResult(success, message);
    }

    public static ProcessResult success() {
        return SUCCESS;
    }

    public static ProcessResult failure() {
        return FAILURE;
    }

    public boolean isSuccess() {
        return success;
    }

    public Optional<Text> getMessage() {
        return Optional.ofNullable(message);
    }
}
