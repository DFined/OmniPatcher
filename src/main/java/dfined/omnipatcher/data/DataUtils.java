package dfined.omnipatcher.data;

import java.util.HashMap;

public abstract class DataUtils {
    public static HashMap<String, Object> cloneMap(HashMap<String, Object> input){
        HashMap<String, Object> result = new HashMap();
        for(String key:result.keySet()){
            Object value = input.get(key);
            if(value instanceof HashMap){
                result.put(key, cloneMap((HashMap<String, Object>) value));
            }
            result.put(key, value);
        }
        return result;
    }
}
