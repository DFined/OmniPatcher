package com.dfined.omnipatcher.v2.format;

import com.dfined.omnipatcher.v2.format.raw.*;
import com.dfined.omnipatcher.v2.io.BufferedLineWriter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ValveDumpsterFormatWriter {
    private final BufferedLineWriter writer;
    private final Map<String, ? extends InstallableItem<?>> overrides;
    int indent = 0;

    private void write(VDFNode nodeA) throws IOException {
        if (nodeA instanceof VDFObjectNode node) {
            enterObject(node.getName());
            writeObjectInternals(node, Map.of());
            exitObject();
        } else if (nodeA instanceof VDFSingleOrListNode node) {
            for (VDFNode subNode : node) {
                write(subNode);
            }
        } else if (nodeA instanceof VDFValueNode node) {
            writeLine(String.format("\"%s\"\t\t\"%s\"", node.getName(), node.getValue()));
        }
    }

    private void writeObjectInternals(VDFObjectNode node, Map<String, VDFNode> localOverrides) throws IOException {
        for (String childName : node.keySet()) {
            String path = node.getPath() + "/" + childName;
            if (overrides.containsKey(path)) {
                for(VDFNode subNode: node.get(childName)) {
                    if(subNode instanceof VDFObjectNode objNode) {
                        write(objNode, overrides.get(path));
                    }
                }
            } else {
                write(localOverrides.containsKey(childName) ? localOverrides.get(childName) : node.get(childName));
            }
        }
    }

    private void write(VDFObjectNode sourceNode, VDFMappedObject mappedObject) throws IOException {
        var defaults = mappedObject.getDefaults();

        HashMap<String, VDFNode> defMap = new HashMap<>();
        for (String def : defaults) {
            var keepValue = sourceNode.getOrDefault(def, null);
            if (keepValue != null) {
                defMap.put(def, keepValue);
            }
        }
        enterObject(sourceNode.getName());
        writeObjectInternals(mappedObject.getVdfObject(), defMap);
        exitObject();
    }

    private void writeLine(String line) throws IOException {
        writer.writeLine("\t".repeat(indent) + line + "\n");
    }

    private void enterObject(String name) throws IOException {
        writeLine(String.format("\"%s\"", name));
        writeLine("{");
        indent++;
    }

    private void exitObject() throws IOException {
        indent--;
        writeLine("}");
    }

    public static void write(File file, VDFObjectNode sourceNode, Map<String, ? extends InstallableItem<?>> overrides) throws IOException {
        var writer = new BufferedLineWriter(file);
        new ValveDumpsterFormatWriter(writer, overrides).write(sourceNode);
        writer.close();
    }
}
