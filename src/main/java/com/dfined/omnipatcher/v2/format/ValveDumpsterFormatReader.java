package com.dfined.omnipatcher.v2.format;

import com.dfined.omnipatcher.dalt.ALTNode;
import com.dfined.omnipatcher.dalt.Tuple;
import com.dfined.omnipatcher.v2.io.BufferedLineReader;
import com.dfined.omnipatcher.v2.error.ExceptionInfo;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ValveDumpsterFormatReader {
    private final File file;
    private BufferedLineReader reader;

    public ALTNode parse() {
        reader = new BufferedLineReader(file);
        try {
            String outerObject = getNameAndR(nextLine()).getKey();
            return parseObject(outerObject, new ArrayList<>());
        } catch (IOException e) {
            throw new IllegalArgumentException(new ExceptionInfo().toString(), e);
        }
    }

    private ALTNode parseObject(String name, List<String> path) throws IOException {
        var line = nextLine();
        if (!line.startsWith("{")) {
            throw new IllegalStateException(new ExceptionInfo().toString());
        }
        ALTNode objectNode = new ALTNode(name, null, path);
        line = nextLine();

        var nPath = new ArrayList<>(path);
        nPath.add(name);
        while (!line.equals("}")) {
            var namedLine = getNameAndR(line);
            var node = getCurrentNode(nPath, namedLine);
            objectNode.addChild(node);
            line = nextLine();
        }
        return objectNode;
    }

    private ALTNode getCurrentNode(List<String> path, Tuple<String, String> namedLine) throws IOException {
        if (namedLine.getValue() == null) {
            //this is an object
            return parseObject(namedLine.getKey(), path);
        }
        //this is a single value
        return new ALTNode(namedLine.getKey(), namedLine.getValue(), path);
    }

    private String nextLine() throws IOException {
        String line = "";
        while (line.isEmpty() || line.startsWith("//")) {
            line = reader.readLine();
            line = line.trim();
        }
        if (line.equals("}") || line.equals("{")) {
            return line;
        }
        line = line.substring(line.indexOf("\"")+1, line.lastIndexOf("\""));
        return line;
    }

    private static Tuple<String, String> getNameAndR(String name) {
        String value = null;
        if (name.contains("\"")) {
            String[] values = name.split("\"[\t\n ]*\"");
            name = values[0].trim();
            if (values.length > 1) {
                value = values[1].trim();
            } else {
                value = "";
            }
        }
        return new Tuple<>(name, value);
    }
}
