package fr.dmall.loupgarou.manager;

import java.util.LinkedHashMap;
import java.util.Map;

public class ManagerRegistry {

    private final Map<Class<? extends Manager>, Manager> managers = new LinkedHashMap<>();

    public void register(Manager manager) {
        managers.put(manager.getClass(), manager);
    }

    public <T extends Manager> T getManager(Class<T> clazz) {
        return clazz.cast(managers.get(clazz));
    }

    public void enableAll() {
        managers.values().forEach(Manager::enable);
    }

    public void disableAll() {
        managers.values().forEach(Manager::disable);
    }
}
