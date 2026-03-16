package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class FileUtils {

    private static final Logger LOG = LogManager.getLogger(FileUtils.class);

    public static void deleteFilesWithPrefix(final String folderPath, final String prefix) {
        try {
            File folder = new File(folderPath);
            for (File f : folder.listFiles()) {
                if (f.getName().startsWith(prefix)) {
                    f.delete();
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
    }
}