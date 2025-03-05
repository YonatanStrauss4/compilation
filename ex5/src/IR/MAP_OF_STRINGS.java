

package IR;

import java.util.*;

public class MAP_OF_STRINGS {
    private Map<String, String> map;

    public MAP_OF_STRINGS() {
        map = new HashMap<>();
    }

    // Puts a key-value pair into the map.
    public void put(String key, String value) {
        map.put(key, value);
    }

    // Retrieves the value associated with the given key.
    public String get(String key) {
        return map.get(key);
    }

    // Checks if the map contains the given key.
    public boolean containsKey(String key) {
        return map.containsKey(key);
    }

    // Removes the key-value pair associated with the given key.
    public void remove(String key) {
        map.remove(key);
    }

    // Returns the number of key-value pairs in the map.
    public int size() {
        return map.size();
    }

    // Checks if the map is empty.
    public boolean isEmpty() {
        return map.isEmpty();
    }

    // Clears all key-value pairs from the map.
    public void clear() {
        map.clear();
    }

    // Returns a string representation of the map.
    @Override
    public String toString() {
        return map.toString();
    }

    private static MAP_OF_STRINGS instance;

    // Returns the singleton instance of the MAP_OF_STRINGS class.
    public static MAP_OF_STRINGS getInstance() {
        if (instance == null) {
            instance = new MAP_OF_STRINGS();
        }
        return instance;
    }
}
