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

package net.foxdenstudio.foxcore.command.util;

import org.spongepowered.api.text.Text;

import java.util.Optional;

public class ProcessResult {

    private static final ProcessResult SUCCESS = of(true);
    private static final ProcessResult FAILURE = of(false);

    private final boolean success;
    private final Optional<Text> message;

    private ProcessResult(boolean success, Optional<Text> message) {
        this.success = success;
        this.message = message;
    }

    public static ProcessResult of(boolean success) {
        return new ProcessResult(success, Optional.empty());
    }

    public static ProcessResult of(boolean success, Text message) {
        return new ProcessResult(success, Optional.of(message));
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
        return message;
    }
}
