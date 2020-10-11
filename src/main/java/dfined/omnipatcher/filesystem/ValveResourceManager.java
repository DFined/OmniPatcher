package dfined.omnipatcher.filesystem;

import javafx.application.Platform;
import javavpk.core.Archive;
import dfined.omnipatcher.application.OmniPatcher;
import javavpk.core.Entry;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ValveResourceManager {
    static Logger log = LogManager.getLogger(ValveResourceManager.class);
    private static final String VALVE_FORMAT_DECOMPILER_LOCATION = "valveFormatDecompiler.exe";

    public static boolean validateDecompilerLocation(){
        File decomp = new File(VALVE_FORMAT_DECOMPILER_LOCATION);
        return decomp.exists();
    }

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
                    JOptionPane.showMessageDialog(null, "Unable to load item icons", "Error Launching App", JOptionPane.ERROR_MESSAGE);
                    Platform.exit();
                    System.exit(1);
                    e.printStackTrace();
                }
            }
        }
        if (FileManagerUtils.existsInLocal(name + formatIn)) {
            FileManagerUtils.deleteLocal(name + formatIn);
        }
        return FileManagerUtils.getFromLocal(name + formatOut);
    }

    public static void installDirectoryToGame(File dir, File result) throws IOException {
        Archive archive = new Archive(result);
        HashMap<String, File> filesToSave = OmniPatcher.getFileManager().listFilesInRepoDir(dir, "");
        archive.writeV1SingleArchive(filesToSave, dir.getPath());
    }

    public static void convertGameinfo(File gameinfo) throws IOException {
        HashMap<String, String> inserts = new HashMap<>();
        inserts.put("Game_Language\t\tdota_*LANGUAGE*", "Game\t\tmods");
        inserts.put("Game\t\t\t\tdota", "Game\t\tmods");
        inserts.put("Mod\t\t\t\tdota", "Mod\t\tmods");
        File gameinfoCopy = new File(gameinfo.getPath() + "copy");
        File gameinfoClean = new File(gameinfo.getPath() + "_Clean");
        if (!gameinfoClean.exists()) {
            FileUtils.copyFile(gameinfo, gameinfoClean);
        }
        String[] lines = FileUtils.readFileToString(gameinfoClean, "UTF8").split("\n");
        StringBuilder builder = new StringBuilder("");
        for (String line : lines) {
            if (line.startsWith("//")) continue;
            if (line.trim().isEmpty()) continue;
            if (inserts.containsKey(line.trim())) {
                builder.append(inserts.get(line.trim()) + "\n");
            }
            builder.append(line + "\n");
        }
        FileUtils.writeStringToFile(gameinfoCopy, builder.toString(), "UTF8");
        gameinfo.delete();
        gameinfoCopy.renameTo(gameinfo);

    }
}
