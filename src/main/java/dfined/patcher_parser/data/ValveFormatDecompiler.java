package dfined.patcher_parser.data;

import dfined.patcher_parser.application.PatcherParser;
import dfined.patcher_parser.filesystem.FileManager;

import java.io.*;
import java.util.concurrent.TimeUnit;

public class ValveFormatDecompiler {
    private static final String VALVE_FORMAT_DECOMPILER_LOCATION = "valveFormatDecompiler.exe";

    public static File getResource(String name, String formatIn, String formatOut, boolean acceptCache) {
        FileManager manager = PatcherParser.getFileManager();
        if (!(manager.existsInLocal(name + formatOut) && acceptCache)) {
            manager.getFromLocal(name + formatIn);
            if (manager.existsInLocal(name + formatIn)) {
                Runtime rt = Runtime.getRuntime();
                try {
                    Process pr = rt.exec(
                            String.format(
                                    "%s -i %s -o %s",
                                    VALVE_FORMAT_DECOMPILER_LOCATION,
                                    manager.getLocalPath() + name + formatIn,
                                    manager.getLocalPath() + name + formatOut
                            )
                    );
                    pr.waitFor(1, TimeUnit.SECONDS);
                    BufferedReader stream = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
                    String line = stream.readLine();
                    while (line != null) {
                        System.out.println(line);
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        if (manager.existsInLocal(name + formatIn)) {
            manager.deleteLocal(name + formatIn);
        }
        return manager.getFromLocal(name + formatOut);
    }
}
