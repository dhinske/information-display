package de.dave.display;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

/**
 * Representation of the index.html.
 * Interface to add/delete/manipulate data.
 * 
 * @author david.hinske
 */
public class IndexHtml {

	private final static Logger LOGGER = Logger.getLogger(IndexHtml.class.getName());

	private final static String TBODY = "tbody";
	private final static String TR = "tr";
	private final static String TD = "td";

	private String path;
	private Document document;

	public IndexHtml(String path) {
		this.path = path;
		// read index.html
		document = null;
		try {
			document = Jsoup.parse(new File(path), "UTF-8");
			LOGGER.info("Succesfully loaded index.html");
		} catch (IOException e) {
			LOGGER.severe("index.html could not be parsed: " + path + "\n" + e);
			return;
		}
	}

	public Document getDocument() {
		return document;
	}

	/**
	 * Add a new row. If the row already exists, the existing value will be overwritten.
	 * 
	 * @param name		name of the row which should be changed/created
	 * @param key		name of the column which should be changed
	 * @param value		value of the table-body
	 */
	void addRow(String name, String key, String value) {
		if (!getAllTableHeaders().contains(key)) {
			LOGGER.log(Level.SEVERE, "Didnt find column " + key + ", abort!");
			return;
		}

		LOGGER.info("Found primary key");
		Node data = getRow(name);
		if (data == null) {
			LOGGER.log(Level.INFO, "No data found for " + name + ", creating new row!");
			Element tbody = document.getElementsByTag(TBODY).first();
			Element tr = document.createElement(TR);
			for (String head : getAllTableHeaders()) {
				Element td = document.createElement(TD);
				if (getPrimaryKey().equals(head)) {
					td.text(name);
				}
				if (key.equals(head)) {
					td.text(value);
				}
				tr.appendChild(td);
			}
			tbody.appendChild(tr);

		} else {
			List<Node> dataChildren = data.childNodes();

			int nodeCount = 0;

			for (int i = 0; i < dataChildren.size(); i++) {
				if (dataChildren.get(i).nodeName().equals(TD)) {
					if (nodeCount == getTableHeaderPosition(key)) {
						LOGGER.log(Level.INFO, "Found element at position " + nodeCount);
						if (dataChildren.get(i) instanceof Element) {
							((Element) dataChildren.get(i)).text(value);
						}
						break;
					}
					nodeCount++;
				}
			}
		}
	}

	/**
	 * Returns the complete table-row (<tr>...</tr>) of a given key.
	 * 
	 * @param name	the value of the first table-data
	 * @return 		the row which first data equals the given name (<td>name</td>)
	 */
	Node getRow(String name) {
		Element tbody = document.getElementsByTag(TBODY).first();
		List<Node> trNodes = tbody.childNodes();
		for (int i = 0; i < trNodes.size(); i++) {
			if (trNodes.get(i).nodeName().equals(TR)) {
				List<Node> tdNodes = trNodes.get(i).childNodes();
				for (int j = 0; j < tdNodes.size(); j++) {
					if (tdNodes.get(j).nodeName().equals(TD) && !tdNodes.get(j).childNodes().isEmpty()
							&& name.equals(tdNodes.get(j).childNode(0).toString())) {
						return trNodes.get(i);
					}
				}
			}
		}
		return null;
	}

	/**
	 * Adds a new column to the table. All existing table-bodies will get table-row. 
	 * 
	 * @param name	name of the table
	 */
	void addColumn(String name) {
		LOGGER.log(Level.INFO, "Adding new column: " + name);
		Element thead = document.getElementsByTag("thead").first();

		// HEAD
		Element th = document.createElement("th");
		th.text(name);
		// does thead already has a tr-child?
		if (thead.childNodes().size() == 1) {
			LOGGER.info("Empty table, creating first column");
			Element tr = document.createElement(TR);

			tr.appendChild(th);
			thead.appendChild(tr);
		} else {
			LOGGER.info("Appending column");
			Element firstTr = document.getElementsByTag(TR).first();
			firstTr.appendChild(th);
		}

		// BODY
		Element tbody = document.getElementsByTag(TBODY).first();
		List<Node> trNodes = tbody.childNodes();
		// no rows yet, nothing to do
		if (trNodes.size() == 1) {
			LOGGER.info("No data-entries yet, leaving tbody alone");
			return;
		}

		for (int i = 0; i < trNodes.size(); i++) {
			if (trNodes.get(i).nodeName().equals(TR)) {
				((Element) trNodes.get(i)).appendChild(document.createElement(TD));
			}
		}
		tbody.appendChild(document.createElement(TR));
	}

	/**
	 * Remove an existing column (table-head with all columns in the table-bodies).
	 * 
	 * @param name	name of the column which will be removed
	 */
	void removeColumn(String name) {
		int position = getTableHeaderPosition(name);
		// remove head
		document.getElementsByTag("tr").first().child(position).remove();
		// remove body
		for (int i = 0; i < document.getElementsByTag(TBODY).first().getElementsByTag("tr").size(); i++) {
			document.getElementsByTag(TBODY).first().child(i).child(position).remove();
		}
	}

	/**
	 * Remove an existing row (table-body).
	 * 
	 * @param name	name of the row which will be removed
	 */
	void removeRow(String name) {
		for (int i = 0; i < document.getElementsByTag(TBODY).first().getElementsByTag("tr").size(); i++) {
			if (document.getElementsByTag(TBODY).first().child(i).child(0).text().equals(name)) {
				document.getElementsByTag(TBODY).first().child(i).remove();
			}
		}
	}

	/**
	 * 
	 * @param name	name of the table-header
	 * @return place of occurence of the table-header, starting at 0
	 */
	private int getTableHeaderPosition(String name) {
		List<String> keys = getAllTableHeaders();
		for (int i = 0; i < keys.size(); i++) {
			if (keys.get(i).equals(name)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * @return all table-headers as Strings in a list
	 */
	List<String> getAllTableHeaders() {
		Node firstTr = document.getElementsByTag(TR).first();
		if (firstTr == null) {
			return Collections.emptyList();
		}
		List<Node> thNodes = firstTr.childNodes();

		List<String> keys = new ArrayList<>();
		for (int i = 0; i < thNodes.size(); i++) {
			if (thNodes.get(i) instanceof Element) {
				keys.add(((Element) thNodes.get(i)).text());
			}
		}
		return keys;
	}

	/**
	 * Returns the first key of all table-headers
	 * 
	 * @return redname of first key, null if no table-headers exist
	 */
	String getPrimaryKey() {
		Node firstTr = document.getElementsByTag(TR).first();
		if (firstTr == null) {
			return null;
		}
		List<Node> thNodes = firstTr.childNodes();

		for (int i = 0; i < thNodes.size(); i++) {
			if (thNodes.get(i) instanceof Element) {
				return ((Element) thNodes.get(i)).text();
			}
		}
		return null;
	}

	/**
	 * writes the in-memory document to the index.html
	 */
	void writeIndexHtml() {
		try {
			PrintWriter writer = new PrintWriter(path, "UTF-8");
			writer.println(document.toString());
			writer.close();
		} catch (IOException e) {
			LOGGER.severe("Could not write to " + path + " " + e);
		}
	}
}
