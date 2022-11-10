package com.dfined.omnipatcher.data.data_structure.io;

import com.dfined.omnipatcher.application.OmniPatcher;
import com.dfined.omnipatcher.data.data_structure.DataMap;
import com.dfined.omnipatcher.data.data_structure.HashDataMap;
import com.dfined.omnipatcher.data.data_structure.NamedString;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;

public abstract class IndexMapper {
    private static final Logger log = LogManager.getLogger(OmniPatcher.class.getSimpleName());

    private static Map.Entry<String, Object> parseEntry(LineReader reader, String line, String path) throws IOException {
        NamedString nameAndR = getNameAndR(line);
        String fieldName = nameAndR.getName();
        String remainder = nameAndR.getValue();
        if (remainder != null) {
            //This is a single value
            return new AbstractMap.SimpleEntry<>(fieldName, remainder);
        }
        DataMap dataMap = parseObjectInternal(reader, fieldName, String.format("%s/%s",path,fieldName));
        //This is an object
        return new AbstractMap.SimpleEntry<>(fieldName, dataMap);
    }

    public static DataMap parseEntry(LineReader reader) throws IOException {
        return (DataMap) parseEntry(reader, prepLine(reader), "").getValue();
    }

    public static DataMap parseObjectInternal(LineReader reader, String name, String path) throws IOException {
        String line = prepLine(reader);
        if (!line.equals("{")) {
            log.fatal(String.format("Malformed index file. Expected line '{' but found '%s'. Exiting.", line), new IllegalArgumentException());
            Platform.exit();
            System.exit(1);
        }
        HashDataMap map = new HashDataMap(name, path);


        line = prepLine(reader);
        while (!line.equals("}")) {
            Map.Entry<String, Object> entry = parseEntry(reader, line, path);
            String key = entry.getKey();
            int count = 0;
            while (map.containsKey(key)) {
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

    public static NamedString getNameAndR(String name) {
        String value = null;
        if (name.contains("\"")) {
            String[] values = name.split("\"[ \n\t]*\"");
            name = values[0].trim();
            value = values[1].trim();
        }
        return new NamedString(name, value);
    }
}
