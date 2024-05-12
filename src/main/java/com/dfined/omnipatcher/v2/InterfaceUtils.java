package com.dfined.omnipatcher.v2;

import com.dfined.omnipatcher.v2.error.ExceptionInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import one.util.streamex.StreamEx;
import org.apache.commons.io.FileUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class InterfaceUtils {
    public static final ObjectMapper MAPPER = Globals.OBJECT_MAPPER;

    public static List<File> chooseFiles(boolean multi, List<String> extensions) {
        FileDialog dialog = new FileDialog((Frame) null, "Select item profile to install");
        dialog.setMode(FileDialog.LOAD);
        dialog.setFile("*." + String.join(";*.", extensions));
        dialog.setVisible(true);
        dialog.setMultipleMode(multi);
        return Arrays.asList(dialog.getFiles());
    }

    public static File chooseFile(String extension) {
        FileDialog dialog = new FileDialog((Frame) null, "Select item profile to install");
        dialog.setMode(FileDialog.SAVE);
        dialog.setVisible(true);
        dialog.setFile("*." + extension);
        var files = Arrays.asList(dialog.getFiles());
        if (files.size() == 1) {
            return files.get(0);
        }
        throw new IllegalStateException(new ExceptionInfo().toString());
    }

    public static <T> List<T> loadFiles(Class<T> type, List<String> extensions) {
        var files = chooseFiles(true, extensions);
        return StreamEx.of(files)
                .map(file -> mapFile(file, type))
                .toList();
    }

    public static <T> void saveFile(T obj, String extension) throws IOException {
        var file = chooseFile(extension);
        FileUtils.writeStringToFile(file, MAPPER.writeValueAsString(obj), "UTF-8");
    }

    private static <T> T mapFile(File file, Class<T> type) {
        try {
            return MAPPER.readValue(FileUtils.readFileToString(file, "UTF-8"), type);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load file " + file.getPath(), e);
        }
    }
}
