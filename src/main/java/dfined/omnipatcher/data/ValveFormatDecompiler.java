package dfined.omnipatcher.data;

import dfined.omnipatcher.filesystem.FileManagerUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

public class ValveFormatDecompiler {
    static Logger log = LogManager.getLogger(ValveFormatDecompiler.class);
    private static final String VALVE_FORMAT_DECOMPILER_LOCATION = "valveFormatDecompiler.exe";

    public static File getResource(String name, String formatIn, String formatOut, boolean acceptCache) throws IOException {
        if (!(FileManagerUtils.existsInLocal(name + formatOut) && acceptCache)) {
            FileManagerUtils.getFromLocal(name + formatIn);
            if (FileManagerUtils.existsInLocal(name + formatIn)) {
                Runtime rt = Runtime.getRuntime();
                try {
                    Process pr = rt.exec(
                            String.format(
                                    "%s -i %s -o %s",
                                    VALVE_FORMAT_DECOMPILER_LOCATION,
                                    FileManagerUtils.getLocalPath() + name + formatIn,
                                    FileManagerUtils.getLocalPath() + name + formatOut
                            )
                    );
                    pr.waitFor(1, TimeUnit.SECONDS);
                    BufferedReader stream = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
                    String line = stream.readLine();
                    while (line != null) {
                        log.info(line);
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        if (FileManagerUtils.existsInLocal(name + formatIn)) {
            FileManagerUtils.deleteLocal(name + formatIn);
        }
        return FileManagerUtils.getFromLocal(name + formatOut);
    }
}
