package org.seedstack.seed.security.internal;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import javax.inject.Inject;
import org.seedstack.seed.security.Role;
import org.seedstack.seed.security.Scope;
import org.seedstack.seed.security.SecurityService;
import org.seedstack.seed.security.SecuritySupport;
import org.seedstack.seed.security.SimpleScope;
import org.seedstack.seed.security.principals.PrincipalProvider;
import org.seedstack.seed.security.principals.SimplePrincipalProvider;

public class SecuritySupportAdapter implements SecuritySupport {
    @Inject
    private SecurityService securityService;

    @Override
    public PrincipalProvider<?> getIdentityPrincipal() {
        return securityService.getIdentity();
    }

    @Override
    public Collection<PrincipalProvider<?>> getOtherPrincipals() {
        return securityService.getPrincipals();
    }

    @Override
    public <T extends Serializable> Collection<PrincipalProvider<T>> getPrincipalsByType(
            Class<T> principalClass) {
        return securityService.getPrincipalsByType(principalClass);
    }

    @Override
    public Collection<SimplePrincipalProvider> getSimplePrincipals() {
        return securityService.getSimplePrincipals();
    }

    @Override
    public SimplePrincipalProvider getSimplePrincipalByName(
            String principalName) {
        return securityService.getSimplePrincipalByName(principalName);
    }

    @Override
    public boolean isPermitted(String permission) {
        return securityService.hasPermission(permission);
    }

    @Override
    public boolean isPermitted(String permission, Scope... scopes) {
        return securityService.hasPermission(permission, scopes);
    }

    @Override
    public boolean isPermittedAll(String... permissions) {
        return securityService.isPermittedAll(permissions);
    }

    @Override
    public boolean isPermittedAny(String... permissions) {
        return securityService.isPermittedAny(permissions);
    }

    @Override
    public void checkPermission(String permission, Scope... scopes) {
        securityService.checkPermission(permission, scopes);
    }

    @Override
    public void checkPermission(String permission) {
        securityService.checkPermission(permission);
    }

    @Override
    public void checkPermissions(String... permissions) {
        securityService.checkPermissions(permissions);
    }

    @Override
    public boolean hasRole(String roleIdentifier, Scope... scopes) {
        return securityService.hasRole(roleIdentifier, scopes);
    }

    @Override
    public boolean hasRole(String roleIdentifier) {
        return securityService.hasRole(roleIdentifier);
    }

    @Override
    public boolean hasAllRoles(String... roleIdentifiers) {
        return securityService.hasAllRoles(roleIdentifiers);
    }

    @Override
    public boolean hasAnyRole(String... roleIdentifiers) {
        return securityService.hasAnyRole(roleIdentifiers);
    }

    @Override
    public void checkRole(String roleIdentifier) {
        securityService.checkRole(roleIdentifier);
    }

    @Override
    public void checkRoles(String... roleIdentifiers) {
        securityService.checkRoles(roleIdentifiers);
    }

    @Override
    public Set<Role> getRoles() {
        return securityService.getRoles();
    }

    @Override
    public Set<SimpleScope> getSimpleScopes() {
        return securityService.getSimpleScopes();
    }

    @Override
    public void logout() {
        securityService.logout();
    }

    @Override
    public boolean isAuthenticated() {
        return securityService.isAuthenticated();
    }

    @Override
    public boolean isRemembered() {
        return securityService.isRemembered();
    }

    @Override
    public String getHost() {
        return securityService.getHost();
    }
}
