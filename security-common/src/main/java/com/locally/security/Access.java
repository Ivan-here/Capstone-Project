package com.locally.security;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

/** Authorization uses the verified server principal, never a client role or user ID. */
public final class Access {
    private Access() {}
    public static String userId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return auth.getName();
    }
    public static boolean hasRole(String role) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }
    public static boolean privileged() { return hasRole("INTERNAL") || hasRole("ADMIN"); }
    public static boolean owns(String owner) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return owner != null && auth != null && auth.isAuthenticated() && owner.equals(auth.getName());
    }
    public static void owner(String owner) {
        if (!privileged() && !owns(owner)) denied();
    }
    public static void participant(String first, String second) {
        if (!privileged() && !owns(first) && !owns(second)) denied();
    }
    public static void admin() { if (!privileged()) denied(); }
    public static void internal() { if (!hasRole("INTERNAL")) denied(); }
    public static void role(String role) { if (!privileged() && !hasRole(role)) denied(); }
    public static void denied() { throw new ResponseStatusException(HttpStatus.FORBIDDEN); }
}
