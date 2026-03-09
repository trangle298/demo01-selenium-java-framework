package common;

import utils.DateTimeUtils;

public class GlobalVariables {
    public static final String PROJECT_PATH = System.getProperty("user.dir");
    public static final String OUTPUT_LOG_FOLDER = PROJECT_PATH.concat("/test-output");

    public static String RUN_TEST_TIMESTAMP = DateTimeUtils.getCurrentTimeStamp("dd-MM-yyyy_HH-mm-ss");
    public static final String EXT_LOG = ".txt";
    public static final String CONFIG_FILE_LOG = "config/log/log4j2.xml";
    public static final String FILE_LOG_NAME = "AutomationLog_";
    public static final String RUN_FOLDER = String.format("%s/run-%s/", OUTPUT_LOG_FOLDER, RUN_TEST_TIMESTAMP);
}