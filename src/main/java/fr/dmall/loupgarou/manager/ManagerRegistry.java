package fr.dmall.loupgarou.manager;

import java.util.ArrayList;
import java.util.List;

public class ManagerRegistry {

    private final List<Manager> managers = new ArrayList<>();

    public void register(Manager manager) {
        managers.add(manager);
    }

    public <T extends Manager> T getManager(Class<T> clazz) {
        for (Manager manager : managers) {
            if (clazz.isInstance(manager)) {
                return clazz.cast(manager);
            }
        }
        return null;
    }

    public void enableAll() {
        managers.forEach(Manager::enable);
    }

    public void disableAll() {
        managers.forEach(Manager::disable);
    }
}