package dfined.patcher_parser.data.data_structure.io;

import com.sun.javaws.exceptions.InvalidArgumentException;
import dfined.patcher_parser.application.PatcherParser;
import dfined.patcher_parser.data.data_structure.NamedString;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;

public abstract class IndexMapper {
    private static final Logger log = LogManager.getLogger(PatcherParser.class.getSimpleName());

    private static Map.Entry<String, Object> parseEntry(LineReader reader, String line) throws IOException {
        NamedString nameAndR = getNameAndR(line);
        String fieldName = nameAndR.getName();
        String remainder = nameAndR.getValue();
        Object value;
        if (remainder != null) {
            //This is a single value
            return new AbstractMap.SimpleEntry<>(fieldName, remainder);
        }
        HashMap hashMap =  parseObjectInternal(reader);
        //This is an object
        return new AbstractMap.SimpleEntry<>(fieldName,hashMap);
    }

    public static Map.Entry<String, Object> parseEntry(LineReader reader) throws IOException {
        return parseEntry(reader, prepLine(reader));
    }

    public static HashMap<String, Object> parseObjectInternal(LineReader reader) throws IOException {
        String line = prepLine(reader);
        if (!line.equals("{")) {
            log.fatal(String.format("Malformed index file. Expected line '{' but found '%s'. Exiting.", line), new InvalidArgumentException(new String[]{line}));
            Platform.exit();
        }
        HashMap<String, Object> map = new LinkedHashMap<>();


        line = prepLine(reader);
        while (!line.equals("}")) {
            Map.Entry<String, Object> entry = parseEntry(reader, line);
            String key = entry.getKey();
            int count = 0;
            while(map.containsKey(key)){
                key = entry.getKey() + count++;
            }
            map.put(key, entry.getValue());
            line = prepLine(reader);
        }
        return map;
    }


    private static String prepLine(LineReader reader) throws IOException {
        String line = "";
        while (line.isEmpty() || line.startsWith("//")) {
            line = reader.readLine();
            line = line.trim();
        }
        if (line.equals("}") || line.equals("{")) {
            return line;
        }
        return line.substring(1, line.length() - 1);
    }

    public static NamedString getNameAndR(String name) throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        String value = null;
        if (name.contains("\"")) {
            //This is an atomic value
            String[] values = name.split("\"[ \n\t]*\"");
            name = values[0].trim();
            value = values[1].trim();
            lines.add(value);
        }
        return new NamedString(name, value);
    }
}
