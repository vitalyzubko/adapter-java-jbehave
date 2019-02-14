package com.epam.jbehave.steps;

import com.epam.jbehave.driver.DriverSingleton;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.steps.Steps;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class Stepdefs extends Steps {

    private WebDriver driver = DriverSingleton.getDriver();

    @Given("I am om google main page")
    public void givenIAmOnGoogleMainPage() {
        driver.get("https://www.google.com/");
    }

    @When("I enter '$text'")
    public void whenIEnterText (String text) {
        WebElement searchInput = driver.findElement(By.name("q"));
        DriverSingleton.waitVisibilityOf(searchInput);
        searchInput.sendKeys(text);
    }

    @When("I press ENTER on keyboard")
    public void whenIClickOnSearchButton() {
        WebElement searchButton = driver.findElement(By.name("q"));
        DriverSingleton.waitVisibilityOf(searchButton);
        searchButton.sendKeys(Keys.ENTER);
    }

    @Then("first search result should contain '$text'")
    public void thenFirstResultShouldContainText(String text) {
        WebElement firstSearchResult = driver.findElement(By.xpath("(//h3)[1]"));
        DriverSingleton.waitVisibilityOf(firstSearchResult);
        Assert.assertTrue(firstSearchResult.getText().contains(text));
    }
}
