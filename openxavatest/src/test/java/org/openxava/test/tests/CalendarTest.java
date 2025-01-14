package org.openxava.test.tests;

import java.text.*;
import java.util.*;

import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.interactions.*;
import org.openqa.selenium.support.ui.*;

public class CalendarTest extends WebDriverTestBase {

	private WebDriver driver;

	public void setUp() throws Exception {
		driver = createWebDriver();
	}

	public void testNavigation() throws Exception {
		forTestAddEventAndVerify();
		forTestConditionsAndFilter();
		forTestAnyNameAsDateProperty();
		forTestMultipleDateAndFirstDateAsEventStart();  
		forTestFilterPerformance();
		forTestMore();
		forTestCreateDateWithTimeInWeekAndDailyView_tooltip();
	}

	public void tearDown() throws Exception {
		driver.quit();
	}

	private void nextOnCalendar() throws Exception {
		WebElement next = driver.findElement(By.cssSelector(".fc-icon.fc-icon-chevron-right"));
		next.click();
		waitCalendarEvent(driver);
	}

	private void prevOnCalendar() throws Exception {
		WebElement prev = driver.findElement(By.cssSelector(".fc-icon.fc-icon-chevron-left"));
		prev.click();
		waitCalendarEvent(driver);
	}

	private List<Date> setDates() {
		List<Date> dates = new ArrayList<>();
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		Date currentMonth = calendar.getTime();
		calendar.add(Calendar.MONTH, -1);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		Date previousMonth = calendar.getTime();
		calendar.add(Calendar.MONTH, 2);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		Date nextMonth = calendar.getTime();
		dates.add(previousMonth);
		dates.add(currentMonth);
		dates.add(nextMonth);

		return dates;
	}

	private void createInvoiceEventPrevCurrentNextMonth() throws Exception {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		List<Date> dates = setDates();
		for (int i = 0; i < dates.size(); i++) {
			if (i == 2) {
				nextOnCalendar();
				waitCalendarEvent(driver);
				wait(driver);
			}
			String dateString = dateFormat.format(dates.get(i));
			WebElement day = driver
					.findElement(By.xpath("//div[contains(@class,'fc-daygrid-day-frame') and ancestor::td[@data-date='"
							+ dateString + "']]"));
			day.click();
			wait(driver);
			createInvoice(i);
		}
	}

	private void createInvoice(int invoiceNUmber) throws Exception {
		WebElement inputInvoiceNumber = driver.findElement(By.id("ox_openxavatest_Invoice__number"));
		int invoiceNumber = (10 + invoiceNUmber);
		inputInvoiceNumber.sendKeys(String.valueOf(invoiceNumber));
		WebElement inputCustomerNumber = driver.findElement(By.id("ox_openxavatest_Invoice__customer___number"));
		inputCustomerNumber.sendKeys("1");
		WebElement section2Child = driver
				.findElement(By.id("ox_openxavatest_Invoice__label_xava_view_section2_sectionName"));
		WebElement section2Parent = section2Child.findElement(By.xpath(".."));
		section2Parent.click();
		wait(driver);
		WebElement inputVAT = driver.findElement(By.id("ox_openxavatest_Invoice__vatPercentage"));
		inputVAT.sendKeys("3");
		execute(driver, "Invoice", "CRUD.save");
		execute(driver, "Invoice", "Mode.list");
		waitCalendarEvent(driver);
	}

	private void verifyPrevInvoiceEvent() throws Exception {
		WebElement currentMonthEvent = driver.findElement(By.cssSelector(
				"a.fc-event.fc-event-draggable.fc-event-resizable.fc-event-start.fc-event-end.fc-event-past.fc-daygrid-event.fc-daygrid-dot-event"));
		currentMonthEvent.click();
		wait(driver);
		WebElement invoiceNumber = driver.findElement(By.id("ox_openxavatest_Invoice__number"));
		assertEquals("10", invoiceNumber.getAttribute("value"));
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		WebElement invoiceYear = driver.findElement(By.id("ox_openxavatest_Invoice__year"));
		assertEquals(String.valueOf(year), invoiceYear.getAttribute("value"));
		execute(driver, "Invoice", "Mode.list");
		waitCalendarEvent(driver);
	}

	private void setInvoiceCondition(String module) throws InterruptedException {
		WebElement inputInvoiceNumber = driver.findElement(By.id("ox_openxavatest_" + module + "__conditionValue___1"));
		inputInvoiceNumber.sendKeys("12");
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].focus();", inputInvoiceNumber);
		WebElement selectInvoiceNumberCondition = driver
				.findElement(By.id("ox_openxavatest_" + module + "__conditionComparator___1"));
		Select select = new Select(selectInvoiceNumberCondition);
		select.selectByVisibleText("=");
		WebElement filterAction = driver.findElement(By.id("ox_openxavatest_" + module + "__List___filter"));
		filterAction.click();
	}

	private void verifyConditionEvents(String time, boolean isExist) {
		WebElement currentMonthEvent = null;
		try {
			currentMonthEvent = driver.findElement(By.cssSelector(
					"a.fc-event.fc-event-draggable.fc-event-resizable.fc-event-start.fc-event-end.fc-event-" + time
							+ ".fc-daygrid-event.fc-daygrid-dot-event"));
		} catch (NoSuchElementException e) {
		}
		assert isExist ? currentMonthEvent != null : currentMonthEvent == null;

	}

	private void deteleEvents() throws Exception {
		WebElement clearFilterAction = driver
				.findElement(By.cssSelector("td.ox-list-subheader a:has(i.mdi.mdi-eraser)"));
		clearFilterAction.click();
		wait(driver);
		for (int i = 0; i < 3; i++) {
			WebElement element = driver.findElement(
					By.xpath("//a[contains(@class, 'ox-image-link') and .//i[contains(@class, 'mdi-delete')]]"));
			element.click();
			acceptInDialogJS(driver);
		}
	}

	private void forTestAnyNameAsDateProperty() throws Exception {
		driver.get("http://localhost:8080/openxavatest/m/UserWithBirthday");
		wait(driver);
		acceptInDialogJS(driver);
		try {
			execute(driver, "UserWithBirthday", "Mode.list");
		} catch (NoSuchElementException e) {
		}
		moveToListView(driver);
		moveToCalendarView(driver);
		moveToListView(driver);
	}

	private void forTestAddEventAndVerify() throws Exception {
		driver.get("http://localhost:8080/openxavatest/m/Invoice");
		wait(driver);
		acceptInDialogJS(driver);
		moveToCalendarView(driver);
		assertFalse(isMonthWeekDayViewPresent(driver));
		prevOnCalendar();
		createInvoiceEventPrevCurrentNextMonth();
		prevOnCalendar();
		verifyPrevInvoiceEvent();
	}

	private void forTestConditionsAndFilter() throws Exception {
		driver.get("http://localhost:8080/openxavatest/m/InvoiceCalendar");
		wait(driver);
		moveToListView(driver);
		setInvoiceCondition("InvoiceCalendar");
		wait(driver);
		moveToCalendarView(driver);
		prevOnCalendar();
		verifyConditionEvents("past", false);
		nextOnCalendar();
		nextOnCalendar();
		verifyConditionEvents("future", true);
		moveToListView(driver);
		deteleEvents();
	}

	private void forTestMultipleDateAndFirstDateAsEventStart() throws Exception {
		driver.get("http://localhost:8080/openxavatest/m/Event");
		wait(driver);
		acceptInDialogJS(driver);
		moveToCalendarView(driver);
		List<Date> dates = setDates();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = dateFormat.format(dates.get(1));

		WebElement day = driver.findElement(By.xpath(
				"//div[contains(@class,'fc-daygrid-day-frame') and ancestor::td[@data-date='" + dateString + "']]"));
		day.click();
		wait(driver);
		
		WebElement startDate = driver.findElement(By.id("ox_openxavatest_Event__startDate"));
		blur(driver, startDate);
		//sleep needed after blur
		Thread.sleep(500);
		List<WebElement> iconElements = driver.findElements(By.cssSelector("i.mdi.mdi-calendar"));
		if (!iconElements.isEmpty()) {
			WebElement firstIconElement = iconElements.get(1);
			firstIconElement.click();
		}

		List<WebElement> spanElements = driver
			.findElements(By.xpath("//div[@class='dayContainer']//span[@class='flatpickr-day ' and text()='2']"));
		
		if (!spanElements.isEmpty()) {
			WebElement spanElement = spanElements.get(1); 
			spanElement.click(); 
		}

		wait(driver);
		execute(driver, "Event", "CRUD.save");
		execute(driver, "Event", "Mode.list");
		waitCalendarEvent(driver);

		List<WebElement> events = driver
				.findElements(By.xpath("//div[contains(@class,'fc-daygrid-event-harness') and ancestor::td[@data-date='"
						+ dateString + "']]"));
		assertTrue(!events.isEmpty());

		moveToListView(driver);
		List<WebElement> elements = driver.findElements(
				By.xpath("//a[contains(@class, 'ox-image-link') and .//i[contains(@class, 'mdi-delete')]]"));
		elements.get(1).click();
		acceptInDialogJS(driver);
	}

	private void forTestFilterPerformance() throws Exception {
		//testing default filter defined by user and month filter defined by calendar
		driver.get("http://localhost:8080/openxavatest/m/EventWithFilter");
		wait(driver);
		acceptInDialogJS(driver);
		moveToListView(driver);
		long ini = System.currentTimeMillis();
		moveToCalendarView(driver);
		long takes = System.currentTimeMillis() - ini;
		// with CompositeFilter it takes no more than 1500, without it takes more than
		// 4000
		assertTrue(takes < 3000);
		moveToListView(driver);
	}
	
	private void forTestMore() throws Exception {
		driver.get("http://localhost:8080/openxavatest/m/Hound");
		wait(driver);
		acceptInDialogJS(driver);
		execute(driver, "Hound", "CRUD.new");
		for (int i = 0; i < 6; i++) {
			insertValueToInput(driver, "ox_openxavatest_Hound__name", String.valueOf(i), true);
			execute(driver, "Hound", "CRUD.save");
		}
		execute(driver, "Hound", "Mode.list");
		waitCalendarEvent(driver);
		moveToCalendarView(driver);
		WebElement linkElement = driver.findElement(By.cssSelector("a.fc-daygrid-more-link.fc-more-link"));
		assertNotNull(linkElement);
		
		moveToListView(driver);

		for (int i = 0; i < 6; i++) {
			WebElement checkboxElement = driver.findElement(By.xpath("//input[@name='ox_openxavatest_Hound__xava_selected' and @value='selected:" + i + "']"));
			checkboxElement.click();
		}
		execute(driver, "Hound", "CRUD.deleteSelected");
	}

	private void forTestCreateDateWithTimeInWeekAndDailyView_tooltip() throws Exception {
		driver.get("http://localhost:8080/openxavatest/m/Appointment");
		wait(driver);
		acceptInDialogJS(driver);
		moveToCalendarView(driver);
		moveToTimeGridWeek(driver);
		
        WebElement dayTimeCell = driver.findElement(By.cssSelector("tr:nth-child(6) > .fc-timegrid-slot-lane"));
        dayTimeCell.click();
        wait(driver);
        
        WebElement dateTime = driver.findElement(By.id("ox_openxavatest_Appointment__time"));
        String dateTimeInput = dateTime.getAttribute("value");
        assertTrue(dateTimeInput.contains("2:30"));
        insertValueToInput(driver, "ox_openxavatest_Appointment__description", "A", false);
        execute(driver, "Appointment", "CRUD.save");
        execute(driver, "Appointment", "Mode.list");
        waitCalendarEvent(driver);

        //tooltip
		WebElement monthEvent = driver.findElement(By.cssSelector(".fc-event.fc-event-draggable.fc-event-resizable.fc-event-start.fc-event-end"));
		Actions builder = new Actions(driver);
        builder.moveToElement(monthEvent).perform();
        Thread.sleep(500);
		WebElement tooltip = driver.findElement(By.cssSelector(".fc-event-tooltip"));
		assertEquals("A", tooltip.getText());
        
        moveToTimeGridWeek(driver);
        WebElement event = driver.findElement(By.cssSelector(".fc-event.fc-event-draggable.fc-event-resizable.fc-event-start.fc-event-end"));
        event.click();
        wait(driver);
        WebElement dateTime2 = driver.findElement(By.id("ox_openxavatest_Appointment__time"));
        String dateTimeInput2 = dateTime2.getAttribute("value");
        assertTrue(dateTimeInput2.contains("2:30"));
        execute(driver, "Appointment", "CRUD.delete");
        execute(driver, "Appointment", "Mode.list");
        waitCalendarEvent(driver);
        // used to verify existence of daily view
		WebElement dayButton = driver.findElement(By.cssSelector("button.fc-timeGridDay-button"));
		dayButton.click();
		waitCalendarEvent(driver);
		moveToListView(driver);
		acceptInDialogJS(driver);
	}
	
	private boolean isMonthWeekDayViewPresent(WebDriver driver) {
		int monthButton = driver.findElements(By.className("fc-timeGridWeek-button")).size();
        int weekButton = driver.findElements(By.className("fc-timeGridDay-button")).size();
        int dayButton = driver.findElements(By.className("fc-dayGridMonth-button")).size();
        boolean b = (monthButton == 1 && weekButton == 1 && dayButton == 1) ? true : false;
        
        return b;
	}

	
}
