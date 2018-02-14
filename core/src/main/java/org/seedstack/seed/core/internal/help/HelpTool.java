package org.seedstack.seed.core.internal.help;

import io.nuun.kernel.api.plugin.InitState;
import io.nuun.kernel.api.plugin.context.InitContext;
import io.nuun.kernel.api.plugin.request.ClasspathScanRequest;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.seedstack.seed.SeedException;
import org.seedstack.seed.cli.CliArgs;
import org.seedstack.seed.core.internal.AbstractSeedTool;
import org.seedstack.seed.core.internal.CoreErrorCode;
import org.seedstack.seed.spi.SeedTool;
import org.seedstack.shed.reflect.ClassPredicates;
import org.seedstack.shed.reflect.Classes;

public class HelpTool extends AbstractSeedTool {
    private final Map<String, SeedTool> tools = new HashMap<>();
    @CliArgs
    private String[] args;

    @Override
    public String toolName() {
        return "help";
    }

    @Override
    public String toolDescription() {
        return "Gives help on other tools";
    }

    @Override
    public Collection<ClasspathScanRequest> classpathScanRequests() {
        return classpathScanRequestBuilder()
                .subtypeOf(SeedTool.class)
                .build();
    }

    @Override
    protected InitState initialize(InitContext initContext) {
        initContext.scannedSubTypesByParentClass().get(SeedTool.class)
                .stream()
                .filter(SeedTool.class::isAssignableFrom)
                .filter(ClassPredicates.classModifierIs(Modifier.ABSTRACT).negate())
                .forEach(candidate -> {
                    SeedTool instance = Classes.instantiateDefault(candidate.asSubclass(SeedTool.class));
                    tools.put(instance.toolName(), instance);
                });
        return InitState.INITIALIZED;
    }

    @Override
    public Integer call() {
        if (args.length == 0) {
            HelpPrinter.printToolList(System.out, tools);
        } else {
            SeedTool tool = tools.get(args[0]);
            if (tool == null) {
                throw SeedException.createNew(CoreErrorCode.TOOL_NOT_FOUND)
                        .put("toolName", args[0]);
            }
            new HelpPrinter(tool).print(System.out);
        }
        return 0;
    }
}
