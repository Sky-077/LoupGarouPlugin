package fr.dmall.loupgarou.role;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class RoleFactory {

    private static final Map<String, Supplier<Role>> ROLES = new LinkedHashMap<>();

    private RoleFactory() {
    }

    public static Set<String> getRegisteredNames() {
        return Collections.unmodifiableSet(ROLES.keySet());
    }

    public static void register(String name, Supplier<Role> supplier) {
        ROLES.put(name.toLowerCase(), supplier);
    }

    public static boolean exists(String name) {
        return ROLES.containsKey(name.toLowerCase());
    }

    public static Role create(String name) {

        Supplier<Role> supplier = ROLES.get(name.toLowerCase());

        if (supplier == null) {
            throw new IllegalArgumentException("Rôle inconnu : " + name);
        }

        return supplier.get();
    }

}