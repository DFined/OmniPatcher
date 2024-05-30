package com.dfined.omnipatcher.v2.filesystem;

import com.dfined.omnipatcher.v2.OmniPatcherV2;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javavpk.core.Archive;
import one.util.streamex.StreamEx;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.JOptionPane;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class ValveResourceManager {
    private static final String ICONS_INPUT_EXTENSION = "_png.vtex_c";
    static Logger log = LogManager.getLogger(ValveResourceManager.class);
    private static final String VALVE_FORMAT_DECOMPILER_LOCATION = "valveFormatDecompiler/Decompiler.exe";
    private static final File DEFAULT_IMAGE = new File(System.getProperty("user.dir"), "defaultIcon.png");

    public static boolean validateDecompilerLocation() {
        File decomp = new File(VALVE_FORMAT_DECOMPILER_LOCATION);
        return decomp.exists();
    }

    public static Image getIcon(String pathInVpk) {
        Image image = null;
        if (pathInVpk != null) {
            try {
                image = new Image(new FileInputStream(ValveResourceManager.getIcon(pathInVpk, ICONS_INPUT_EXTENSION)));
            } catch (IOException e) {
                log.warn(String.format("Error loading icon '%s'", pathInVpk), e);
            }
        }
        return image;
    }

    public static File getIcon(String name, String formatIn) {
        var fileManager = OmniPatcherV2.FSM;
        var fullname = name.contains(".vtex_c") ? name : name + formatIn;
        var file = fileManager.getFileForPath(fullname, FileStore.PERSISTENT);
        var resultFile = new File(file.getAbsolutePath().replace(".vtex_c", ".png"));

        if (!resultFile.exists()) {
            try {
                if (file != null && file.exists()) {
                    Runtime rt = Runtime.getRuntime();

                    Process pr = rt.exec(
                            String.format(
                                    "%s -i %s -o %s",
                                    VALVE_FORMAT_DECOMPILER_LOCATION,
                                    file.getAbsolutePath(),
                                    resultFile.getAbsolutePath()
                            )
                    );
                    pr.waitFor(1, TimeUnit.SECONDS);
                    BufferedReader stream = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
                    String line = stream.readLine();
                    while (line != null) {
                        log.info(line);
                        line = stream.readLine();
                    }
                }

            } catch (IOException | InterruptedException | IllegalStateException e) {
                try {
                    FileUtils.copyFile(DEFAULT_IMAGE, resultFile);
                } catch (IOException ioException) {
                    JOptionPane.showMessageDialog(null, "Unable to load default icon", "Fatal Error", JOptionPane.ERROR_MESSAGE);
                    log.fatal(e);
                    Platform.exit();
                    System.exit(1);
                }
            }
        }
        return resultFile;
    }

    public static void installDirectoryToGame(File dir, File result) throws IOException {
        Archive archive = new Archive(result);
        Map<String, File> filesToSave = StreamEx.of(FileUtils.listFiles(dir, null, true))
                .toMap(file -> pathRelativeToDir(file, dir), Function.identity());
        archive.writeV1SingleArchive(filesToSave, dir.getPath());
    }

    private static String pathRelativeToDir(File file, File dir) {
        return dir.toPath().relativize(file.toPath()).toString();
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
