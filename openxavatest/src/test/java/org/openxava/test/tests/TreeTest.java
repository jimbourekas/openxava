package org.openxava.test.tests;

import java.util.*;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.*;

public class TreeTest extends WebDriverTestBase{
	
	private WebDriver driver;
	private Map<String, String> nodesId;

	public void setUp() throws Exception {
		driver = createWebDriver();
	}

	public void testNavigation() throws Exception {
		goTo(driver, "http://localhost:8080/openxavatest/m/TreeItem");
		String rootIdValue = getValueInList(driver, "TreeItem", "0", "0");
		addTreeIdValues(rootIdValue);
		
		goTo(driver, "http://localhost:8080/openxavatest/m/TreeContainer");
		execute(driver, "TreeContainer", "List.viewDetail", "row=0");
		createNewNodeSelecting(driver);	

		goTo(driver, "http://localhost:8080/openxavatest/m/TreeItem");
		addNewNodeId(driver);
		
		goTo(driver, "http://localhost:8080/openxavatest/m/TreeContainer");
		verifyCreatedNodesAndCheck(driver);
		editNodeWithDoubleClick(driver);
		deleteSelectedNode(driver);
		cutNode(driver);
		execute(driver, "TreeContainer", "Mode.list");
		execute(driver, "TreeContainer", "List.viewDetail", "row=0");
		dragAndDrop(driver); 
		execute(driver, "TreeContainer", "Mode.list");
		execute(driver, "TreeContainer", "CRUD.deleteRow", "row=1");
	}

	public void tearDown() throws Exception {
		driver.quit();
	}
	
	// Wait until the element is available
	private WebElement findElement(WebDriver driver, By by) { 
		wait(driver, by);
		return driver.findElement(by);		
	}

	private void createNewNodeSelecting(WebDriver driver) throws Exception {
		WebElement childItem2CheckBox = findElement(driver, By.xpath("//a[@id='"+ nodesId.get("child2") +"_anchor']/i")); 
		childItem2CheckBox.click();
		execute(driver, "TreeContainer", "TreeView.new", "viewObject=xava_view_treeItems");
		insertValueToInput(driver, "ox_openxavatest_TreeContainer__description", "A", false);
		WebElement save = driver.findElement(By.id("ox_openxavatest_TreeContainer__TreeView___save"));
		save.click();
		wait(driver);

		WebElement createNewButtonElement = driver.findElement(By.xpath("//a[@data-application='openxavatest' and @data-module='TreeContainer' and @data-action='TreeView.new' and @data-argv='viewObject=xava_view_treeItems']"));
		((JavascriptExecutor) driver).executeScript("arguments[0].click();", createNewButtonElement); // must clicked manually because the element is not interactable
		Thread.sleep(500); //sometimes need
		acceptInDialogJS(driver);
		insertValueToInput(driver, "ox_openxavatest_TreeContainer__description","B", false);
		save = driver.findElement(By.id("ox_openxavatest_TreeContainer__TreeView___save"));
		save.click();
		wait(driver);
	}
	
	private void editNodeWithDoubleClick(WebDriver driver) throws Exception {
		Thread.sleep(500); //sometimes need
		WebElement aElement = driver.findElement(By.id(nodesId.get("a") + "_anchor"));
		Actions actions = new Actions(driver);
		actions.doubleClick(aElement).perform();
		wait(driver);
		
		insertValueToInput(driver, "ox_openxavatest_TreeContainer__description", "AA", true);
		WebElement save = driver.findElement(By.id("ox_openxavatest_TreeContainer__TreeView___save"));
		save.click();
		wait(driver);
		
		assertEquals("AA", findElement(driver, By.id(nodesId.get("a") + "_anchor")).getText()); 
	}
	
	private void verifyCreatedNodesAndCheck(WebDriver driver) throws InterruptedException {
		WebElement childItem2CheckBox = findElement(driver, By.xpath("//a[@id='"+ nodesId.get("child2") +"_anchor']/i")); 
		childItem2CheckBox.click();
		Thread.sleep(500); //sometimes need
		expandNode(driver, nodesId.get("child2"));
		WebElement newNodeB = driver.findElement(By.xpath("//a[@id='" + nodesId.get("b") + "_anchor']"));
		assertEquals("B", newNodeB.getText());
		WebElement newNodeACheckBox = driver.findElement(By.xpath("//a[@id='" + nodesId.get("a") + "_anchor']/i"));
		newNodeACheckBox.click();
	}
	
	private void deleteSelectedNode(WebDriver driver) throws Exception {
		List<WebElement> collectionButtons = driver.findElements(By.className("ox-collection-list-actions"));
		List<WebElement> firstCollectionButtons = collectionButtons.get(0).findElements(By.className("ox-button-bar-button"));
		WebElement removeButton = firstCollectionButtons.get(1).findElement(By.tagName("a"));
		removeButton.click();
		acceptInDialogJS(driver);
		wait(driver);

		boolean showMessages = !driver.findElements(By.cssSelector("td.ox-messages")).isEmpty();
		assertTrue(showMessages);
	}
	
	private void cutNode(WebDriver driver) throws Exception {
		WebElement bCheckBox = driver.findElement(By.xpath("//a[@id='" + nodesId.get("b") + "_anchor']/i"));
		bCheckBox.click();
		execute(driver, "TreeContainer", "CollectionCopyPaste.cut", "viewObject=xava_view_treeItems");
		execute(driver, "TreeContainer", "Mode.list");
		execute(driver, "TreeContainer", "CRUD.new");
		insertValueToInput(driver, "ox_openxavatest_TreeContainer__description", "BB", false);
		execute(driver, "TreeContainer", "CollectionCopyPaste.paste", "viewObject=xava_view_treeItems");
		WebElement bElement = findElement(driver, By.id(nodesId.get("b") + "_anchor")); 
		assertTrue(bElement.getText().equals("B"));
	}
	
	private void dragAndDrop(WebDriver driver) throws Exception {
		executeDnd(driver, nodesId.get("child1sub2") + "_anchor", nodesId.get("child1sub1") + "_anchor");
		executeDndBetween(driver, nodesId.get("child1") + "_anchor", nodesId.get("root"));
		expandNode(driver, nodesId.get("child1sub1"));
		executeDnd(driver, nodesId.get("child3sub1") + "_anchor", nodesId.get("child1sub2") + "_anchor");
		expandNode(driver, nodesId.get("child1sub2"));
		executeDnd(driver, nodesId.get("child1") + "_anchor", nodesId.get("root") + "_anchor");
		driver.navigate().refresh();
		wait(driver);
		assertTrue(isElementInside(driver, nodesId.get("root"), nodesId.get("child1") + "_anchor"));
		assertTrue(isElementInside(driver, nodesId.get("child1"), nodesId.get("child1sub1") + "_anchor"));
		assertTrue(isElementInside(driver, nodesId.get("child1sub1"), nodesId.get("child1sub2") + "_anchor"));
		assertTrue(isElementInside(driver, nodesId.get("child1sub2"), nodesId.get("child3sub1") + "_anchor"));
		
		executeDnd(driver, nodesId.get("child3sub1") + "_anchor", nodesId.get("child3") + "_anchor");
		executeDndBetween(driver, nodesId.get("child1sub2") + "_anchor", nodesId.get("child1"));
	}
	
	private void executeDnd(WebDriver driver, String sourceElementId, String targetElementId) throws InterruptedException {
        WebElement sourceElement = findElement(driver, By.id(sourceElementId));
        WebElement targetElement = findElement(driver, By.id(targetElementId));		
        Actions actions = new Actions(driver);
        actions.dragAndDrop(sourceElement, targetElement).build().perform();
        Thread.sleep(500);// wait animation and html
	}
	
	private void executeDndBetween(WebDriver driver, String sourceElementId, String targetParentId) throws InterruptedException {
        Actions actions = new Actions(driver);
        actions.clickAndHold(driver.findElement(By.id(sourceElementId)))
               .build()
               .perform();
        actions.moveToElement(driver.findElement(By.id(targetParentId + "_anchor")))
               .release()
               .build()
               .perform();
        actions.moveToElement(driver.findElement(By.id(targetParentId)).findElement(By.tagName("ul")))
               .release()
               .build()
               .perform();
        Thread.sleep(500);// wait animation and html
	}
	
	private void expandNode(WebDriver driver, String id) throws InterruptedException {
		WebElement liElement = driver.findElement(By.id(id));
		String expanded = liElement.getAttribute("aria-expanded");
		if (expanded.equals("false")) {
			WebElement liIcon = liElement.findElement(By.cssSelector("i.jstree-icon.jstree-ocl"));
			liIcon.click();
		}
		Thread.sleep(500); // wait animation and fill html elements
	}
	
	private boolean isElementInside(WebDriver driver, String parentElementId, String childElementId) {
		WebElement parentElement = driver.findElement(By.id(parentElementId));
		WebElement childElement = parentElement.findElement(By.id(childElementId));
		return (childElement != null);
	}
	
	private String getValueInList(WebDriver driver, String model, String row, String column) {
		List<WebElement> elements = driver.findElements(By.cssSelector(".ox_openxavatest_" + model + "__tipable.ox_openxavatest_" + model + "__list_col" + column ));
		return elements.get(Integer.valueOf(row)).getText();
	}
	
	private void addTreeIdValues(String rootId) {
		nodesId = new HashMap<>();
		int root = Integer.valueOf(rootId);
		nodesId.put("root", rootId);
		nodesId.put("child1", String.valueOf(root+1));
		nodesId.put("child2", String.valueOf(root+2));
		nodesId.put("child3", String.valueOf(root+3));
		nodesId.put("child1sub1", String.valueOf(root+4));
		nodesId.put("child1sub2", String.valueOf(root+5));
		nodesId.put("child3sub1", String.valueOf(root+6));
	}
	
	private void addNewNodeId(WebDriver driver) throws Exception {
		nodesId.put("a", getValueInList(driver, "TreeItem", "7", "0"));
		nodesId.put("b", getValueInList(driver, "TreeItem", "8", "0"));
	}
	
	private void goTo(WebDriver driver, String url) throws Exception {
		driver.get(url);
		wait(driver);
		acceptInDialogJS(driver);
	}
	
}
