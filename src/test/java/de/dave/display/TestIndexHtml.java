package de.dave.display;
import java.util.List;

import org.jsoup.nodes.Node;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.dave.display.IndexHtml;

public class TestIndexHtml {
	
	private IndexHtml indexHtml;
	private IndexHtml indexNoDataHtml;
	
	@Before
	public void init() {
		indexHtml = new IndexHtml("src/test/resources/index.html");
		indexNoDataHtml = new IndexHtml("src/test/resources/index_no_data.html");
	}
	
	@Test
	public void testGetPrimaryKey() {
		Assert.assertEquals("Key1", indexHtml.getPrimaryKey());
		Assert.assertEquals(null, indexNoDataHtml.getPrimaryKey());
	}

	@Test
	public void testGetAllTableHeaders() {
		List<String> rows = indexNoDataHtml.getAllTableHeaders();
		Assert.assertEquals(0, rows.size());
		
		rows = indexHtml.getAllTableHeaders();
		Assert.assertEquals(3, rows.size());
		Assert.assertEquals(false, rows.contains("row1"));
		Assert.assertEquals(true, rows.contains("Key1"));
		Assert.assertEquals(true, rows.contains("Key2"));
		Assert.assertEquals(true, rows.contains("Key3"));
	}
	
	@Test
	public void testGetData() {
		Assert.assertEquals(null, indexHtml.getRow("test"));
		Node node = indexHtml.getRow("row1");

		Assert.assertNotEquals(null , node);
	}
	
	@Test
	public void testAddColumn() {
		Assert.assertEquals(0, indexNoDataHtml.getDocument().getElementsByTag("th").size());
		Assert.assertEquals(0, indexNoDataHtml.getDocument().getElementsByTag("td").size());
		indexNoDataHtml.addColumn("Test");
		Assert.assertEquals(1, indexNoDataHtml.getDocument().getElementsByTag("th").size());
		Assert.assertEquals(0, indexNoDataHtml.getDocument().getElementsByTag("td").size());
		
		Assert.assertEquals(3, indexHtml.getDocument().getElementsByTag("th").size());
		Assert.assertEquals(3, indexHtml.getDocument().getElementsByTag("td").size());
		indexHtml.addColumn("Test");
		Assert.assertEquals(4, indexHtml.getDocument().getElementsByTag("th").size());
		Assert.assertEquals(4, indexHtml.getDocument().getElementsByTag("td").size());
	}
	
	@Test
	public void testRemoveColumn() {
		List<String> rows = indexHtml.getAllTableHeaders();
		Assert.assertEquals(true, rows.contains("Key3"));
		Assert.assertEquals(3, rows.size());
		
		indexHtml.removeColumn("Key3");
		rows = indexHtml.getAllTableHeaders();
		Assert.assertEquals(2, rows.size());
		Assert.assertEquals(false, rows.contains("Key3"));
	}
	
	@Test
	public void testRows() {
//		Assert.assertNotEquals(null, indexHtml.getRow("row1"));
//		indexHtml.removeRow("row1");
//		Assert.assertEquals(null, indexHtml.getRow("row1"));
		
		Assert.assertEquals(null, indexHtml.getRow("row3"));
		indexHtml.addRow("row3", "Key2", "value3");
		//System.out.println(indexHtml.getDocument());
		Assert.assertNotEquals(null, indexHtml.getRow("row3"));
	}
}
