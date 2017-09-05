package org.seedstack.seed.rest.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.seedstack.seed.security.CRUDAction;
import org.seedstack.seed.security.spi.CRUDActionResolver;

public class RestCrudActionResolver implements CRUDActionResolver {

    private final Map<Class<? extends Annotation>, CRUDAction> annotationMap;

    public RestCrudActionResolver() {
        Map<Class<? extends Annotation>, CRUDAction> map = new HashMap<>();
        map.put(javax.ws.rs.DELETE.class, CRUDAction.DELETE);
        map.put(javax.ws.rs.GET.class, CRUDAction.READ);
        map.put(javax.ws.rs.HEAD.class, CRUDAction.READ);
        map.put(javax.ws.rs.OPTIONS.class, CRUDAction.READ);
        map.put(javax.ws.rs.POST.class, CRUDAction.CREATE);
        map.put(javax.ws.rs.PUT.class, CRUDAction.UPDATE);
        annotationMap = Collections.unmodifiableMap(map);
    }

    @Override
    public boolean canResolve(Method method) {

        // Check if any of the annotations is a JAXRS protocol specifier
        return Arrays.stream(method.getAnnotations())
                .map(Annotation::getClass)
                .anyMatch(annotationMap::containsKey);
    }

    @Override
    public Optional<CRUDAction> resolve(Method method) {
        return Arrays.stream(method.getAnnotations())
                .map(Annotation::getClass)
                .map(x -> annotationMap.getOrDefault(x, null))
                .filter(Objects::nonNull)
                .findFirst();
    }
}
