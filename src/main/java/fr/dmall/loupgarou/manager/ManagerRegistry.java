package fr.dmall.loupgarou.manager;

import java.util.ArrayList;
import java.util.List;

public class ManagerRegistry {

    private final List<Manager> managers = new ArrayList<>();

    public void register(Manager manager) {
        managers.add(manager);
    }

    public void enableAll() {
        managers.forEach(Manager::enable);
    }

    public void disableAll() {
        managers.forEach(Manager::disable);
    }
}