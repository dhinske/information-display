package de.dave.display;

import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
	
	private Main() {}
	
	private final static Logger LOGGER = Logger.getLogger(Main.class.getName());

	private static final String COMMAND_ADD_COLUMN = "addColumn";
	private static final String COMMAND_REMOVE_COLUMN = "removeColumn";
	private static final String COMMAND_REMOVE_ROW = "removeRow";

	public static void main(String[] args) {

		String path = null;
		try {
			path = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
		} catch (URISyntaxException e) {
			LOGGER.severe("Could not resolve location of jar" + e);
		}
		if (path == null) {
			LOGGER.severe("Could not resolve location of jar");
			return;
		}
		path = path.replace("information-display.jar", "");
		LOGGER.log(Level.INFO, "Looking for index.html under %s", path);
		IndexHtml indexHtml = new IndexHtml(path + "index.html");
		
		// check parameter
		if (args.length < 2 || ("help").equals(args[0])) {
			LOGGER.warning("How to use:");
			LOGGER.warning("");
			LOGGER.warning("<Name> <Key> <Value>");
			LOGGER.warning(COMMAND_ADD_COLUMN + " <Name>");
			LOGGER.warning(COMMAND_REMOVE_COLUMN + " <Name>");
			LOGGER.warning(COMMAND_REMOVE_ROW + " <Name>");
			return;
		}

		// adapt changes
		switch (args[0]) {
		case COMMAND_ADD_COLUMN:
			indexHtml.addColumn(args[1]);
			break;
		case COMMAND_REMOVE_COLUMN:
			indexHtml.removeColumn(args[1]);
			break;
		case COMMAND_REMOVE_ROW:
			indexHtml.removeRow(args[1]);
			break;
		default:
			// change data
			indexHtml.addRow(args[0], args[1], args[2]);
			break;
		}
		
		// write index.html
		indexHtml.writeIndexHtml();
	}
}
