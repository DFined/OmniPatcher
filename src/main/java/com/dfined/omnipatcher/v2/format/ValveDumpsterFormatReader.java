package com.dfined.omnipatcher.v2.format;

import com.dfined.omnipatcher.v2.format.raw.NamedString;
import com.dfined.omnipatcher.v2.io.BufferedLineReader;
import com.dfined.omnipatcher.v2.error.ExceptionInfo;
import com.dfined.omnipatcher.v2.format.raw.VDFNode;
import com.dfined.omnipatcher.v2.format.raw.VDFObjectNode;
import com.dfined.omnipatcher.v2.format.raw.VDFValueNode;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;

@RequiredArgsConstructor
public class ValveDumpsterFormatReader {
    private final File file;
    private BufferedLineReader reader;
    private int line = 0;

    public VDFObjectNode parse() {
        reader = new BufferedLineReader(file);
        try {
            String outerObject = getNameAndR(nextLine()).getName();
            return parseObject(outerObject, outerObject);
        } catch (IOException e) {
            throw new IllegalArgumentException(new ExceptionInfo().toString(), e);
        }
    }

    private VDFObjectNode parseObject(String path, String name) throws IOException {
        var line = nextLine();
        if (!line.startsWith("{")) {
            throw new IllegalStateException(new ExceptionInfo().toString());
        }
        VDFObjectNode object = new VDFObjectNode(path, name);
        line = nextLine();
        while (!line.equals("}")) {
            var namedLine = getNameAndR(line);
            var node = getCurrentNode(path, namedLine);
            object.put(namedLine.getName(), node);
            line = nextLine();
        }
        return object;
    }

    private VDFNode getCurrentNode(String path, NamedString namedLine) throws IOException {
        String newPath = path + "/" + namedLine.getName();
        if (namedLine.getValue() == null) {
            //this is an object
            return parseObject(newPath, namedLine.getName());
        }
        //this is a single value
        return new VDFValueNode(namedLine.getName(), namedLine.getValue());
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

    private static NamedString getNameAndR(String name) {
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
        return new NamedString(name, value);
    }
}
