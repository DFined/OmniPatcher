package dfined.patcher_parser.data.data_structure;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataMap {
    HashMap<String, Object> map;

    public DataMap(HashMap<String, Object> map) {
        this.map = map;
    }

    public DataMap getByPath(String path){
        return getByPath(path, map);
    }

    public static DataMap getByPath(String path, HashMap map){
        DataMap pathMap = navigateTo(path, map);
        DataMap result = new DataMap(new HashMap<>());
        if(pathMap != null){
            result.map.putAll(pathMap.getMap());
        }
        return result;
    }

    public static Object mapf(Map.Entry entry, HashMap map){
        return map.put(entry.getKey(), entry.getValue());
    }

    public static DataMap navigateTo(String path, HashMap map){
        String[] elements = path.split("/");
        HashMap<String, Object> currentMap = map;
        for(String element: elements){
            Object value = currentMap.get(element);
            if(value != null){
                if(value instanceof HashMap){
                    currentMap = (HashMap<String, Object>) value;
                }else{
                    return null;
                }
            }else {
                return null;
            }
        }
        return new DataMap(currentMap);
    }

    public HashMap<String, Object> getMap() {
        return map;
    }
}
