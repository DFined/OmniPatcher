package dfined.omnipatcher.filesystem;

import dfined.omnipatcher.application.OmniPatcher;
import javafx.application.Platform;
import javavpk.core.Archive;
import net.lingala.zip4j.ZipFile;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class ValveResourceManager {
    private static final String STEAM_CMD_URL = "https://steamcdn-a.akamaihd.net/client/installer/steamcmd.zip";
    private static final File STEAM_CMD_ARCHIVE = new File(System.getProperty("user.dir"), "steamcmd.zip");
    private static final File STEAM_CMD_DIR = new File(System.getProperty("user.dir"), "steamcmd");
    private static final File STEAM_CMD_FILE = new File(STEAM_CMD_DIR, "steamcmd.exe");
    static Logger log = LogManager.getLogger(ValveResourceManager.class);
    private static final String VALVE_FORMAT_DECOMPILER_LOCATION = "valveFormatDecompiler.exe";
    private static final File DEFAULT_IMAGE = new File(System.getProperty("user.dir"), "defaultIcon.png");

    public static boolean validateDecompilerLocation() {
        File decomp = new File(VALVE_FORMAT_DECOMPILER_LOCATION);
        return decomp.exists();
    }

    public static File getResource(String name, String formatIn, String formatOut, boolean acceptCache) {
        if (!(FileManagerUtils.existsInLocal(name + formatOut) && acceptCache)) {
            try {
                FileManagerUtils.getFromLocal(name + formatIn);
                if (FileManagerUtils.existsInLocal(name + formatIn)) {
                    Runtime rt = Runtime.getRuntime();

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
                }
            } catch (IOException | InterruptedException e) {
                try {
                    FileUtils.copyFile(DEFAULT_IMAGE, new File(FileManagerUtils.getLocalPath(), name + formatOut));
                } catch (IOException ioException) {
                    JOptionPane.showMessageDialog(null, "Unable to load default icon", "Fatal Error", JOptionPane.ERROR_MESSAGE);
                    log.fatal(e);
                    Platform.exit();
                    System.exit(1);
                }
            }
        }

        if (FileManagerUtils.existsInLocal(name + formatIn)) {
            FileManagerUtils.deleteLocal(name + formatIn);
        }

        return new File(FileManagerUtils.getLocalPath(), name + formatOut);
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
        inserts.put("Mod\t\t\t\t\tdota", "Mod\t\tmods");
        File gameinfoCopy = new File(gameinfo.getPath() + "copy");
        File gameinfoClean = new File(gameinfo.getPath() + "_Clean");
        if (!gameinfoClean.exists()) {
            FileUtils.copyFile(gameinfo, gameinfoClean);
        }
        String[] lines = FileUtils.readFileToString(gameinfoClean, "UTF8").split("\n");
        StringBuilder builder = new StringBuilder("");
        for (String line : lines) {
            String lineClean = line.trim();
            //if (lineClean.startsWith("//")) continue;
            //if (lineClean.trim().isEmpty()) continue;
            if (inserts.containsKey(line.trim())) {
                builder.append(inserts.get(line.trim()) + "\n");
            }
            builder.append(line + "\n");
        }
        FileUtils.writeStringToFile(gameinfoCopy, builder.toString(), "UTF8");
        gameinfo.delete();
        gameinfoCopy.renameTo(gameinfo);
    }

//    public void prepareSteamCMDLocal() {
//        try {
//            if(!STEAM_CMD_FILE.exists()) {
//                if (!STEAM_CMD_ARCHIVE.exists()) {
//                    FileUtils.copyURLToFile(new URL(STEAM_CMD_URL), STEAM_CMD_ARCHIVE);
//                }
//                net.lingala.zip4j.ZipFile zipCmd = new ZipFile(STEAM_CMD_ARCHIVE);
//                if (!STEAM_CMD_DIR.exists()) {
//                    STEAM_CMD_DIR.mkdir();
//                }
//                zipCmd.extractAll(STEAM_CMD_DIR.getPath());
//            }
//            if(!STEAM_CMD_FILE.exists()){
//                log.fatal("Unable to acquire steamcmd.exe file");
//                Platform.exit();
//                System.exit(1);
//            }
//            Runtime rt = Runtime.getRuntime();
//
//            Process pr = rt.exec(STEAM_CMD_FILE.getPath());
//            BufferedReader stream = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
//            while(pr.isAlive())
//            String line = stream.readLine();
//            while (line != null) {
//                log.info(line);
//            }
//
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException ioException) {
//            ioException.printStackTrace();
//        }
//    }
}
