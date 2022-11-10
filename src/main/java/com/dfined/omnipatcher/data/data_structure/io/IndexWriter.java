package com.dfined.omnipatcher.data.data_structure.io;

import com.dfined.omnipatcher.data.data_structure.HashDataMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public abstract class IndexWriter {
    private static Logger log = LogManager.getLogger(IndexWriter.class);

    public static void writeObject(LineWriter writer, String name, HashDataMap dataMap) throws IOException {
        writeObject(writer, name, dataMap, "");
        writer.close();
    }

    private static void writeObject(LineWriter writer, String name, HashDataMap dataMap, String prefix) throws IOException {
        writer.writeLine(String.format("%s\"%s\"\n%s{\n", prefix, name, prefix));
        writeBody(writer, dataMap, prefix + "\t");
        writer.writeLine(String.format("%s}\n", prefix));

    }

    private static void writeBody(LineWriter writer, HashDataMap object, String prefix) throws IOException {
        for (String key : object.keySet()) {
            Object value = object.get(key);
            if (value instanceof HashDataMap) {
                writeObject(writer, ((HashDataMap) value).getDataObjectName(), (HashDataMap) value, prefix);
            } else {
                writer.writeLine(String.format("%s\"%s\"\t\t\t\t\"%s\"\n", prefix, key, value.toString()));
            }

        }
    }
}
