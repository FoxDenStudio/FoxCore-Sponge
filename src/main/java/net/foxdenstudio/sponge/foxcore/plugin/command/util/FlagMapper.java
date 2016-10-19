package net.foxdenstudio.sponge.foxcore.plugin.command.util;

import java.util.Map;
import java.util.function.Function;

/**
 * Created by Fox on 7/7/2016.
 */
public interface FlagMapper extends Function<Map<String, String>, Function<String, Function<String, Boolean>>> {
}
