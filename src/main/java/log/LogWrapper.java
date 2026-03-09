package log;

import common.GlobalVariables;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import utils.FileUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LogWrapper {

	static {
		System.setProperty("log4j.configurationFile", GlobalVariables.CONFIG_FILE_LOG);
	}

	public static final Logger createLogger(String className) {
		return LogManager.getLogger(className);
	}

	public static void mergeLogFiles() {
		try {
			File file = new File(GlobalVariables.OUTPUT_LOG_FOLDER);
			File[] files = file.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return pathname.getName().endsWith(GlobalVariables.RUN_TEST_TIMESTAMP + GlobalVariables.EXT_LOG);
				}
			});

			List<Path> paths = new ArrayList<Path>();
			for (File f : files) {
				Path path = f.toPath();
				paths.add(path);
			}

			Collections.sort(paths);

			String logFileName = GlobalVariables.FILE_LOG_NAME + GlobalVariables.RUN_TEST_TIMESTAMP
					+ GlobalVariables.EXT_LOG;
			Path pathAll = FileSystems.getDefault().getPath(GlobalVariables.OUTPUT_LOG_FOLDER + "/" + logFileName);
			for (Path path : paths) {
				String separator = "\n--- " + path.getFileName() + " " + "-".repeat(60) + "\n";
				Files.write(pathAll, separator.getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
				Files.write(pathAll, Files.readAllBytes(path), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public static void closeAppenderWriters() {
		LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
		GroupedLoggingCustomAppender appender = ctx.getConfiguration().getAppender("MyCustomAppender");
		if (appender != null) {
			appender.closeAllWriters();
		}
	}

	public static void deleteIndividualThreadLog() {
		FileUtils.deleteFilesWithPrefix(GlobalVariables.OUTPUT_LOG_FOLDER, "thread_output_");
	}
}
