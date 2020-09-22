package dfined.patcher_parser.data.data_structure;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface DataMap {
    Stream<Object> streamByRegex(String regex, boolean getKeys);
    default List<Object> listByRegex(String regex, boolean getKeys){
        return streamByRegex(regex,getKeys).collect(Collectors.toList());
    };

    default Object getSingle(String path, boolean getKey){
        return streamByRegex(path, getKey).findFirst().orElse(null);
    }

    default <T> List<T> listTypeByRegex(String regex, Class<T> tClass){
        return listByRegex(regex,false).stream()
                .filter(object -> tClass.isAssignableFrom(object.getClass()))
                .map(obj -> (T)obj)
                .collect(Collectors.toList());
    }

    default <T>T getSingleType(String path, Class<T> tClass){
        return listTypeByRegex(path,tClass).stream().findFirst().orElse(null);
    }

    String getDataObjectName();

    String backingPath();

    default String[] prepPath(String regex){
        if(regex.startsWith("/")){
            return regex.substring(1).split("/",2);
        }
        return regex.split("/",2);
    }
}
