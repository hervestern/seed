package org.seedstack.seed.undertow.internal.graalvm;

import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import io.undertow.servlet.spec.ServletPrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@TargetClass(className = "io.undertow.servlet.spec.ServletPrintWriterDelegate")
final class ServletPrintWriterDelegate {
    @Substitute
    public static io.undertow.servlet.spec.ServletPrintWriterDelegate newInstance(
            final ServletPrintWriter servletPrintWriter) {
        try {
            Constructor<io.undertow.servlet.spec.ServletPrintWriterDelegate> declaredConstructor =
                    io.undertow.servlet.spec.ServletPrintWriterDelegate.class
                            .getDeclaredConstructor();
            io.undertow.servlet.spec.ServletPrintWriterDelegate servletPrintWriterDelegate =
                    declaredConstructor.newInstance();
            servletPrintWriterDelegate.setServletPrintWriter(servletPrintWriter);
            return servletPrintWriterDelegate;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
