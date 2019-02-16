package com.epam.jbehave.steps;

import com.epam.jbehave.driver.DriverSingleton;
import com.epam.jbehave.utils.JIRAReporter;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.steps.Steps;
import org.junit.Assert;
import org.openqa.selenium.*;

import java.io.File;

public class Stepdefs extends Steps {

    private WebDriver driver = DriverSingleton.getDriver();

    @Given("I am om google main page")
    public void givenIAmOnGoogleMainPage() {
        driver.get("https://www.google.com/");
    }

    @When("I enter '$text'")
    public void whenIEnterText(String text) {
        WebElement searchInput = driver.findElement(By.name("q"));
        DriverSingleton.waitVisibilityOf(searchInput);
        searchInput.sendKeys(text);
    }

    @When("I press ENTER on keyboard")
    public void whenIClickOnSearchButton() {
        WebElement searchInput = driver.findElement(By.name("q"));
        DriverSingleton.waitVisibilityOf(searchInput);
        searchInput.sendKeys(Keys.ENTER);
    }

    @Then("first search result should contain '$text'")
    public void thenFirstResultShouldContainText(String text) {
        WebElement firstSearchResult = driver.findElement(By.xpath("(//h3)[1]"));
        DriverSingleton.waitVisibilityOf(firstSearchResult);

        JIRAReporter.addParameter("First result", firstSearchResult.getText());
        JIRAReporter.addParameter("Second title", "Second test value");

        System.out.println("[expected: '" + firstSearchResult.getText() + "' contains '" + text + "'][actual: '" + firstSearchResult.getText().contains(text) + "']");
        Assert.assertTrue(firstSearchResult.getText().contains(text));
    }

    @Then("first link in footer should contain '$text'")
    public void thenFirstLinkInFooterContainText(String text) {
        WebElement firstSearchResult = driver.findElement(By.xpath("(//*[@id='brs']//a)[1]"));
        DriverSingleton.waitVisibilityOf(firstSearchResult);

        JIRAReporter.addParameter("First result in footer", firstSearchResult.getText());
        JIRAReporter.addAttachment(((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE));

        System.out.println("[expected: '" + firstSearchResult.getText() + "' contains '" + text + "'][actual: '" + firstSearchResult.getText().contains(text) + "']");
        Assert.assertTrue(firstSearchResult.getText().contains(text));
    }
}