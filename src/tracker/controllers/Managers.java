package tracker.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Managers {
        public static TaskManager getDefault() {
            return new InMemoryTaskManager();
        }
        public static HistoryManager getDefaultHistory() {
            return new InMemoryHistoryManager();
        }
    private static final Gson gson = new GsonBuilder().create();

    public static Gson getGson() {
        return gson;
    }

}
