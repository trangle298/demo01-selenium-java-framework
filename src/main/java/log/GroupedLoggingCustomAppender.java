package log;

import common.GlobalVariables;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Plugin(name = "MyCustomAppender", category = "Core", elementType = "appender", printObject = true)
public class GroupedLoggingCustomAppender extends AbstractAppender {

	private final ConcurrentHashMap<Long, BufferedWriter> tid2file = new ConcurrentHashMap<Long, BufferedWriter>();
	private volatile boolean closed = false;
	private static final String outputDir;

	static {
		String outdir = GlobalVariables.RUN_FOLDER;
		System.out.println("outdir = " + outdir);
		if (!outdir.endsWith("/"))
			outdir += "/";
		outputDir = outdir;
		new java.io.File(outputDir).mkdirs();
	}

	protected GroupedLoggingCustomAppender(String name, Filter filter, Layout<? extends Serializable> layout,
			final boolean ignoreExceptions) {
		super(name, filter, layout, ignoreExceptions, Property.EMPTY_ARRAY);
	}

	@PluginFactory
	public static GroupedLoggingCustomAppender createAppender(@PluginAttribute("name") String name,
			@PluginElement("Layout") Layout<? extends Serializable> layout,
			@PluginElement("Filter") final Filter filter, @PluginAttribute("otherAttribute") String otherAttribute) {
		if (name == null) {
			LOGGER.error("No name provided for MyCustomAppenderImp");
			return null;
		}
		if (layout == null) {
			layout = PatternLayout.createDefaultLayout();
		}
		return new GroupedLoggingCustomAppender(name, filter, layout, true);

	}

	@Override
	public void append(LogEvent event) {
		if (closed)
			return;

		try {
			long tid = Thread.currentThread().threadId();
			BufferedWriter fw = tid2file.get(tid);
			if (fw == null) {
				fw = new BufferedWriter(new FileWriter(getFileNameFromThreadID(tid)));
				tid2file.put(tid, fw);
			}
			String timestamp = ZonedDateTime.now().format(
					java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
			String fullLoggerName = event.getLoggerName();
			String simpleClassName = fullLoggerName.contains(".")
					? fullLoggerName.substring(fullLoggerName.lastIndexOf('.') + 1)
					: fullLoggerName;
			String level = String.format("%-5s", event.getLevel());
			fw.write(timestamp + " [" + level + "] [" + simpleClassName + ":" + event.getSource().getLineNumber() + "] "
					+ event.getMessage().getFormattedMessage());
			fw.write("\n");
			fw.flush();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public void closeAllWriters() {
		for (BufferedWriter bw : tid2file.values()) {
			try {
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		tid2file.clear();
		closed = true;
	}

	private String getFileNameFromThreadID(long tid) {
		return String.format("%sthread_output_%04d%s", outputDir, tid,
				"_" + GlobalVariables.RUN_TEST_TIMESTAMP + GlobalVariables.EXT_LOG);
	}
}