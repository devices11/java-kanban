package main.service;

import main.util.Exception.ManagerSaveException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Managers {

    private Managers() {
    }

    public static TaskManager getDefault() {
        return getInMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getFileBackedTaskManager() {
        Path path = Paths.get("src/resources/storage.csv");
        File file = new File(path.toUri());
        if (!Files.exists(path)) {
            try {
                Files.createFile(path);
            } catch (IOException e) {
                throw new ManagerSaveException("Произошла ошибка при создании файла.");
            }
        }
        return FileBackedTaskManager.loadFromFile(getDefaultHistory(), file);
    }

    public static TaskManager getInMemoryTaskManager() {
        return new InMemoryTaskManager(getDefaultHistory());
    }


}
