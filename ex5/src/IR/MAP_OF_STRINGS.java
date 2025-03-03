

package IR;

import java.util.*;

public class MAP_OF_STRINGS {
    private Map<String, String> map;

    public MAP_OF_STRINGS() {
        map = new HashMap<>();
    }

    public void put(String key, String value) {
        map.put(key, value);
    }

    public String get(String key) {
        return map.get(key);
    }

    public boolean containsKey(String key) {
        return map.containsKey(key);
    }

    public void remove(String key) {
        map.remove(key);
    }

    public int size() {
        return map.size();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public void clear() {
        map.clear();
    }

    @Override
    public String toString() {
        return map.toString();
    }

    private static MAP_OF_STRINGS instance;

    public static MAP_OF_STRINGS getInstance() {
        if (instance == null) {
            instance = new MAP_OF_STRINGS();
        }
        return instance;
    }
}