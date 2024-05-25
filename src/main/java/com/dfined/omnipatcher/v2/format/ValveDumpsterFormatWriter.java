package com.dfined.omnipatcher.v2.format;

import com.dfined.omnipatcher.dalt.ALTNode;
import com.dfined.omnipatcher.v2.io.BufferedLineWriter;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ValveDumpsterFormatWriter {
    private static final String OFFSET = "\t";
    private final BufferedLineWriter writer;
    private int indentation = 0;
    private Map<List<String>, List<ALTNode>> overrides;

    private ValveDumpsterFormatWriter(File file, Map<List<String>, List<ALTNode>> overrides) {
        this.writer = new BufferedLineWriter(file);
        this.overrides = overrides;
    }

    /**
     * Write an ALT node as a VDF file, while applying a map of overrides. If a node has a path, which is present
     * as a key in the overrides map, then the associated value node will be written instead of it
     */
    private void write(ALTNode sourceNode) throws IOException {
        writeObjectNode(sourceNode);
        writer.close();
    }

    private void writeNode(ALTNode sourceNode) throws IOException {
        {
//            if(sourceNode.getName().equals("apply_when_equipped_in_ability_effects_slot")){
//                return;
//            }
            if (sourceNode.getValue() == null) {
                writeObjectNode(sourceNode);
            } else {
                var value = sourceNode.getValue();
                writer.writeLine(String.format("%s\"%s\"\t\t\"%s\"\n", indent(), sourceNode.getName(), value));
            }
        }
    }

    private void writeObjectNode(ALTNode sourceNode) throws IOException {
        writer.writeLine(String.format("%s\"%s\"\n%s{\n", indent(), sourceNode.getName(), indent()));
        indentation += 1;

        if (overrides.containsKey(sourceNode.getPathSegments())) {
            for (var override : overrides.get(sourceNode.getPathSegments())) {
                writeChildObjects(override);
            }
        } else {
            writeChildObjects(sourceNode);
        }

        indentation -= 1;
        writer.writeLine(String.format("%s}\n", OFFSET.repeat(indentation)));
    }

    private void writeChildObjects(ALTNode node) throws IOException {
        for (var childName : node.getChildNodes().keySet()) {
            for (var child : node.getChildNodes().get(childName)) {
                writeNode(child);
            }
        }
    }

    private String indent() {
        return OFFSET.repeat(indentation);
    }

    public static void write(File file, ALTNode rootNode, Map<List<String>, List<ALTNode>> Overrides) throws IOException {
        new ValveDumpsterFormatWriter(file, Overrides).write(rootNode);
    }
}
