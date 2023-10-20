package org.openxava.test.tests;

import java.util.*;

import javax.persistence.*;

import org.openqa.selenium.*;
import org.openxava.jpa.*;
import org.openxava.test.model.*;
import org.openxava.util.*;
import org.openxava.web.*;

/**
 * To test @DescriptionsList related issue with Selenium.
 *
 * @author Javier Paniza
 */

public class DescriptionsListTest extends WebDriverTestBase {
	
	private WebDriver driver;
	private String module; 

	public void setUp() throws Exception {
		XPersistence.reset(); 
		XPersistence.setPersistenceUnit("junit");
		setHeadless(true);
	    driver = createWebDriver();
	}
	
	public void tearDown() throws Exception {
		driver.quit();
	}
	
	public void testAutocomplete() throws Exception {
		setFamilyDescription(1, "SOFTWAR�"); // To test a bug with accents 
		createWarehouseWithQuote(); // To test a bug with quotes

		goModule("Product2");
		execute("CRUD.new");
		
		driver.findElement(By.id("ox_openxavatest_Product2__reference_editor_warehouse")); // Warehouse combo must be to test the "quotes" bug
		
		WebElement familyList = driver.findElement(By.id("ui-id-1"));
		assertFalse(familyList.isDisplayed());
		assertEquals(0, familyList.findElements(By.tagName("li")).size());
		WebElement familyEditor = driver.findElement(By.id("ox_openxavatest_Product2__reference_editor_family"));
		WebElement openFamilyListIcon = familyEditor.findElement(By.className("mdi-menu-down"));
		WebElement closeFamilyListIcon = familyEditor.findElement(By.className("mdi-menu-up"));
		assertTrue(openFamilyListIcon.isDisplayed());
		assertFalse(closeFamilyListIcon.isDisplayed()); 
		openFamilyListIcon.click();
		assertTrue(familyList.isDisplayed());
		assertEquals(3, familyList.findElements(By.tagName("li")).size());
		assertFalse(openFamilyListIcon.isDisplayed());
		assertTrue(closeFamilyListIcon.isDisplayed());	

		closeFamilyListIcon.click();
		assertFalse(familyList.isDisplayed());
		assertTrue(openFamilyListIcon.isDisplayed());
		assertFalse(closeFamilyListIcon.isDisplayed());	

		WebElement familyTextField = familyEditor.findElement(By.className("ui-autocomplete-input"));
		assertEquals("HARDWARE", familyTextField.getAttribute("value"));
		familyTextField.sendKeys(Keys.CONTROL + "a");
		familyTextField.sendKeys("\b");
		assertEquals("", familyTextField.getAttribute("value"));
		
		familyTextField.sendKeys("ware"); 
		assertEquals("ware", familyTextField.getAttribute("value"));
		Thread.sleep(700); 
		assertTrue(familyList.isDisplayed()); 
		assertFalse(openFamilyListIcon.isDisplayed());
		assertTrue(closeFamilyListIcon.isDisplayed());
		
		List<WebElement> familyListChildren = familyList.findElements(By.tagName("li")); 
		assertEquals(2, familyListChildren.size()); 
		assertEquals("SOFTWAR�", familyListChildren.get(0).getText()); 
		assertEquals("HARDWARE", familyListChildren.get(1).getText());
		
		familyListChildren.get(0).click(); // SOFTWARE
		wait(driver);
		WebElement subfamilyEditor = driver.findElement(By.id("ox_openxavatest_Product2__reference_editor_subfamily"));
		WebElement openSubfamilyListIcon = subfamilyEditor.findElement(By.className("mdi-menu-down"));
		openSubfamilyListIcon.click();
		WebElement subfamilyList = driver.findElement(By.id("ui-id-9"));
		assertTrue(subfamilyList.isDisplayed());
		List<WebElement> subfamilyListChildren = subfamilyList.findElements(By.tagName("li")); 
		assertEquals(3, subfamilyListChildren.size());
		assertEquals("DESARROLLO", subfamilyListChildren.get(0).getText()); 
		assertEquals("SISTEMA", subfamilyListChildren.get(2).getText());	
		
		subfamilyListChildren.get(0).click(); // DESARROLLO
		WebElement subfamilyTextField = subfamilyEditor.findElement(By.className("ui-autocomplete-input"));
		assertEquals("DESARROLLO", subfamilyTextField.getAttribute("value")); 

		setValue("number", "66");
		setValue("description", "JUNIT PRODUCT");
		setValue("unitPrice", "66");
		execute("CRUD.save");
		assertNoErrors(); 
		execute("Mode.list");
		execute("List.orderBy", "property=number");
		execute("List.orderBy", "property=number");
		assertValueInList(0, 0, "66"); 
		assertValueInList(0, 1, "JUNIT PRODUCT");
		assertValueInList(0, 2, "SOFTWAR�"); 
		assertValueInList(0, 3, "DESARROLLO");
		
		execute("List.viewDetail", "row=0");
		assertValue("number", "66");
		assertValue("family.number", "1");
		assertDescriptionValue("family.number", "SOFTWAR�");
		familyTextField =  getDescriptionsListTextField("family");
		assertEquals("SOFTWAR�", familyTextField.getAttribute("value")); 
		assertValue("subfamily.number", "1");
		assertDescriptionValue("subfamily.number", "DESARROLLO");
		subfamilyTextField = getDescriptionsListTextField("subfamily");
		assertEquals("DESARROLLO", subfamilyTextField.getAttribute("value")); 
		execute("CRUD.delete");
		assertNoErrors();

		
		execute("CRUD.new");
		familyTextField = getDescriptionsListTextField("family");
		familyTextField.sendKeys(Keys.CONTROL + "a");
		familyTextField.sendKeys("\b");		
		familyTextField.sendKeys("ware");
		assertEquals("ware", familyTextField.getAttribute("value"));
		familyTextField.sendKeys(Keys.TAB);
		assertEquals("", familyTextField.getAttribute("value")); 
		
		execute("CRUD.new");
		familyList = driver.findElement(By.id("ui-id-25")); 
		assertFalse(familyList.isDisplayed());
		assertEquals(0, familyList.findElements(By.tagName("li")).size());
		familyTextField = getDescriptionsListTextField("family");
		familyTextField.sendKeys(Keys.CONTROL + "a");
		familyTextField.sendKeys("\b");
		familyTextField.sendKeys(" \b");
		Thread.sleep(700); 
		assertTrue(familyList.isDisplayed()); 
		assertEquals(3, familyList.findElements(By.tagName("li")).size());
				
		familyEditor = driver.findElement(By.id("ox_openxavatest_Product2__reference_editor_family"));
		openFamilyListIcon = familyEditor.findElement(By.className("mdi-menu-down"));
		closeFamilyListIcon = familyEditor.findElement(By.className("mdi-menu-up"));		
		closeFamilyListIcon.click();
		openFamilyListIcon.click();
		assertTrue(familyList.isDisplayed());
		subfamilyEditor = driver.findElement(By.id("ox_openxavatest_Product2__reference_editor_subfamily"));
		openSubfamilyListIcon = subfamilyEditor.findElement(By.className("mdi-menu-down"));		
		openSubfamilyListIcon.click();
		assertFalse(familyList.isDisplayed());
		
		setFamilyDescription(1, "SOFTWARE"); 
		removeWarehouseWithQuote(); 
	}
	
	private void setFamilyDescription(int number, String newDescription) { 
		Family2 software = XPersistence.getManager().find(Family2.class, number);
		software.setDescription(newDescription);
	}

	private void createWarehouseWithQuote() {
		Warehouse warehouse = new Warehouse();
		warehouse.setZoneNumber(10);
		warehouse.setNumber(11);
		warehouse.setName("ALMACEN \"EL REBOLLAR\"");
		XPersistence.getManager().persist(warehouse);
		XPersistence.commit();
	}

	private void removeWarehouseWithQuote() { 
 		Query query = XPersistence.getManager().createQuery("from Warehouse as o where o.zoneNumber = 10 and number = 11"); // We don't use Warehouse.findByZoneNumberNumber(10, 11) in order to work with XML/Hibernate version 
 		Warehouse warehouse = (Warehouse) query.getSingleResult();
		XPersistence.getManager().remove(warehouse);
		XPersistence.commit();
	}
	
	private WebElement getDescriptionsListTextField(String reference) {
		WebElement referenceEditor = driver.findElement(By.id("ox_openxavatest_" + module + "__reference_editor_" + reference));
		return referenceEditor.findElement(By.className("ui-autocomplete-input"));
	}
	
	private void assertDescriptionValue(String name, String value) { 
		assertEquals(XavaResources.getString("unexpected_value", name), value, getDescriptionValue(name));		
	}
	
	private String getDescriptionValue(String name) {		
		WebElement input = driver.findElement(By.name(Ids.decorate("openxavatest", module, name + "__DESCRIPTION__")));
		return input.getAttribute("value");
	}
	
	private void assertValue(String name, String value) { // tmr Duplicated with ListTest
		assertEquals(XavaResources.getString("unexpected_value", name), value, getValue(name));		
	}
	
	private String getValue(String name) { // tmr Duplicated with ListTest
		WebElement input = driver.findElement(By.id(Ids.decorate("openxavatest", module, name)));
		return input.getAttribute("value");
	}
	
	private void assertValueInList(int row, int column, String expectedValue) { // tmr Duplicado con ListTest 
		assertEquals(expectedValue, getValueInList(row, column));				
	}
	
	
	private String getValueInList(int row, int column) { // tmr Duplicado con DescriptionsListTest
		return getValueInCollection("list", row + 1, column);
	}
		
	private String getValueInCollection(String collection, int row, int column) { // tmr Duplicado con ListTest
		return getCell(collection, row + 1, column).getText().trim();
	}
	
	private WebElement getCell(String collection, int row, int column) { // tmr Duplicado con ListTest
		WebElement tableRow = getTable(collection).findElements(By.tagName("tr")).get(row);
		String cellType = row == 0?"th":"td";
		List<WebElement> cells = tableRow.findElements(By.tagName(cellType));		
		return cells.get(column + 2);
	}
	
	private WebElement getTable(String collection) { // tmr Duplicado con ListTest
		return driver.findElement(By.id("ox_openxavatest_" + module + "__" + collection));
	}
	
	private void execute(String action, String arguments) throws Exception { // tmr Duplicado con DescriptionsListTest
		execute(driver, module, action, arguments);
	}
	
	private void assertNoErrors() { // tmr Duplicado con DescriptionsListTest
		WebElement errors = driver.findElement(By.id("ox_openxavatest_" + module + "__errors"));
		assertEquals(XavaResources.getString("unexpected_messages", "Errors"), "", errors.getText());
	}

	
	private void setValue(String name, String value) { // tmr Duplicada con ListTest
		WebElement input = driver.findElement(By.id(Ids.decorate("openxavatest", module, name)));
		input.clear();
		input.sendKeys(value);	
	}
	
	private void execute(String action) throws Exception { // tmr Duplicado con ListTest
		execute(driver, module, action);
	}
	
	private void goModule(String module) throws Exception{ // tmr Duplicado con ListTest
		driver.get("http://localhost:8080/openxavatest/m/" + module);
		this.module = module;
		wait(driver);
	}
	
}