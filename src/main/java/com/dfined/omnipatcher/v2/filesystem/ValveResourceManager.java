package com.dfined.omnipatcher.v2.filesystem;

import com.dfined.omnipatcher.v2.OmniPatcherV2;
import external.CRCHack;
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
    private static final String ICONS_OUTPUT_EXTENSION = ".png";
    private static final String HELLO_GABEN = "Hello, Gaben, this string ==>1234<== is defeating your validity check. Please unblock my dota custom files and eat a deek. F";
    private static final String STEAM_CMD_URL = "https://steamcdn-a.akamaihd.net/client/installer/steamcmd.zip";
    private static final File STEAM_CMD_ARCHIVE = new File(System.getProperty("user.dir"), "steamcmd.zip");
    private static final File STEAM_CMD_DIR = new File(System.getProperty("user.dir"), "steamcmd");
    private static final File STEAM_CMD_FILE = new File(STEAM_CMD_DIR, "steamcmd.exe");
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
                image = new Image(new FileInputStream(ValveResourceManager.getIcon(pathInVpk, ICONS_INPUT_EXTENSION, ICONS_OUTPUT_EXTENSION)));
            } catch (IOException e) {
                log.warn(String.format("Error loading icon '%s'", pathInVpk), e);
            }
        }
        return image;
    }

    public static File getIcon(String name, String formatIn, String formatOut) {
        var fileManager = OmniPatcherV2.FSM;
        var resultFile = fileManager.getInDir(name + formatOut, FileStore.PERSISTENT);
        var decompFile = fileManager.getInDir(name + formatOut, FileStore.PERSISTENT);

        if (!resultFile.exists()) {
            try {
                if (!decompFile.exists()) {
                    var file = fileManager.getFileForPath(name + formatIn, FileStore.PERSISTENT);
                    if (file != null && file.exists()) {
                        Runtime rt = Runtime.getRuntime();

                        Process pr = rt.exec(
                                String.format(
                                        "%s -i %s -o %s",
                                        VALVE_FORMAT_DECOMPILER_LOCATION,
                                        file.getAbsolutePath(),
                                        decompFile.getAbsolutePath()
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
                }
                if(!decompFile.getPath().equals(resultFile.getPath())){
                    FileUtils.copyFile(decompFile, resultFile);
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
            if (lineClean.startsWith("//")) continue;
            //if (lineClean.trim().isEmpty()) continue;
            if (inserts.containsKey(line.trim())) {
                builder.append(inserts.get(line.trim()) + "\n");
            }
            builder.append(line + "\n");
        }
        var realLength = gameinfoClean.length();
        var newLength = builder.toString().length();
        //How many symbols to append to a comment at the end of file. -2 because // at start of comment
        var appendCommentLength = (int) realLength - newLength - 2 - HELLO_GABEN.length();
        builder.append("//").append(HELLO_GABEN).append("u".repeat(appendCommentLength));
        FileUtils.writeStringToFile(gameinfoCopy, builder.toString(), "UTF8");
        CRCHack.modifyFileCrc32(gameinfoCopy, gameinfoClean, newLength + 31, true);
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
