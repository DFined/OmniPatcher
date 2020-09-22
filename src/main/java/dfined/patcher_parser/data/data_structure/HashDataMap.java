package dfined.patcher_parser.data.data_structure;

import java.util.LinkedHashMap;
import java.util.stream.Stream;

public class HashDataMap extends LinkedHashMap<String, Object> implements DataMap {
    String name;
    String path;

    public HashDataMap(String name, String path) {
        super();
        this.name = name;
        this.path = path;
    }

    @Override
    public Stream<Object> streamByRegex(String regex, boolean getKeys) {
        String[] paths = prepPath(regex);
        if (paths.length > 1) {
            return this.keySet().stream()
                    .filter(key -> key.matches(paths[0]))
                    .flatMap(key -> this.get(key) instanceof DataMap ? ((DataMap) this.get(key)).streamByRegex(paths[1], getKeys) : Stream.empty());
        }
        Stream keys = this.keySet().stream().filter(key -> key.matches(paths[0]));
        if(getKeys){
            return keys;
        }
        return keys.map(this::get);
    }

    @Override
    public String getDataObjectName() {
        return name;
    }

    @Override
    public String backingPath() {
        return path;
    }
}
