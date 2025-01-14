package org.openxava.test.tests;

import java.util.*;

import org.openqa.selenium.*;

/**
 * To test charts related issues with Selenium.
 * 
 * @author Javier Paniza
 */
public class ChartsTest extends WebDriverTestBase {
	
	private WebDriver driver;
	private String module; 

	public void setUp() throws Exception {
		setHeadless(true); 
	    driver = createWebDriver();
	}
	
	public void tearDown() throws Exception {
		driver.quit();
	}
	
	public void testCharts() throws Exception {
		goModule("Invoice");
		assertOneBarByEachRow();
		
		goModule("Color");
		assertMax120Bars();
	}

	
	private void assertOneBarByEachRow() throws Exception {
		int rowCount = getListRowCount();
		execute("ListFormat.select", "editor=Charts");
		List<WebElement> rects = driver.findElements(By.cssSelector(".ox-chart-data svg .c3-chart rect")); 
		assertEquals(rowCount, rects.size());
	}
	
	private void assertMax120Bars() throws Exception {
		String listInfo = driver.findElement(By.cssSelector("tr.ox-list-info-detail")).getText();
		assertTrue(listInfo.contains("There are 2,124 records in list")); // No matter the exact count, but it should be more than 1000
		execute("ListFormat.select", "editor=Charts");
		WebElement selectColumn = driver.findElement(By.id("ox_openxavatest_Color__columns___0___name"));
		selectColumn.click();
		selectColumn.findElement(By.cssSelector("option[value='number']")).click();
		wait(driver);
		List<WebElement> rects = driver.findElements(By.cssSelector(".ox-chart-data svg .c3-chart rect"));
		assertEquals(120, rects.size());		
	}
	
	
	private void execute(String action, String arguments) throws Exception { // Duplicated with ListTest, refactoring pending 
		execute(driver, module, action, arguments);
	}

	
	private void goModule(String module) throws Exception{ // Duplicated with ListTest, refactoring pending 
		driver.get("http://localhost:8080/openxavatest/m/" + module);
		this.module = module;
		wait(driver);
	}
	
	private int getListRowCount() { // Duplicated with ListTest, refactoring pending
		return getCollectionRowCount("list") - 2;
	}

	private int getCollectionRowCount(String collection) { // Duplicated with ListTest, refactoring pending
		int rowCount = getTable(collection).findElements(By.tagName("tr")).size();
		return rowCount - 1;
	}
	
	private WebElement getTable(String collection) { // Duplicated with ListTest, refactoring pending
		return driver.findElement(By.id("ox_openxavatest_" + module + "__" + collection));
	}
		
}
