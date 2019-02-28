package com.epam.jbehave.steps;

import com.epam.jbehave.driver.DriverSingleton;
import com.epam.jbehave.utils.JIRAReporter;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.steps.Steps;
import org.junit.Assert;
import org.openqa.selenium.*;

public class Stepdefs extends Steps {

    private final WebDriver driver = DriverSingleton.getDriver();

    @Given("I am on page with url '$url'")
    public void givenIAmOnYouTubeMainPage(String url) {
        driver.navigate().to(url);
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

    @Then("I see block with '$expectedText' text on page")
    public void thenISeeBlockWithTextOnPage(String expectedText) {
        WebElement block = driver.findElement(By.xpath(("//div[@id='contentBox']//h2)[1]")));
        DriverSingleton.waitVisibilityOf(block);
        System.out.println("[expected: '" + expectedText + "'][actual: '" + block.getText() + "']");
        Assert.assertEquals(expectedText, block.getText());
    }


    @Then("I should see author name '$expectedAuthorName'")
    public void thenIShouldSeeAuthorName(String expectedAuthorName) {
        WebElement authorName = driver.findElement(By.xpath("//*[@id='owner-name'])[2]"));
        DriverSingleton.waitVisibilityOf(authorName);
        String actualAuthorName = authorName.getText();

        JIRAReporter.addParameter("Author", actualAuthorName);

        System.out.println("[expected: '" + expectedAuthorName + "'][actual: '" + actualAuthorName + "']");
        Assert.assertEquals(expectedAuthorName, actualAuthorName);
    }


    @Then("I should see '$text' in list video")
    public void IShouldSeeTitleInListVideo(String expectedVideoTitle) {
        DriverSingleton.waitVisibilityOf(driver.findElement(By.xpath("(//*[@id='logo-icon-container'])[1]")));
        WebElement videoTitle = driver.findElement(By.id("video-title"));
        String actualVideoTitle = videoTitle.getText();

        JIRAReporter.addParameter("Logo container title", actualVideoTitle);
        JIRAReporter.addAttachment(((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE));

        System.out.println("[expected: '" + expectedVideoTitle + "'][actual: '" + actualVideoTitle + "']");
        Assert.assertEquals(expectedVideoTitle, actualVideoTitle);
    }

    @Then("see '$expectedTitle' in title")
    public void ISeeTextInTitle(String expectedTitle) {
        WebElement onlinerLogo = driver.findElement(By.xpath("//*[@class='header__logo']"));
        DriverSingleton.waitVisibilityOf(onlinerLogo);
        String actualTitle = driver.getTitle();

        JIRAReporter.addParameter("epam.com title", actualTitle);
        JIRAReporter.addAttachment(((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE));

        System.out.println("[expected: '" + actualTitle + "' contains '" + expectedTitle + "'][actual: '" + actualTitle.contains(expectedTitle) + "']");
        Assert.assertTrue(actualTitle.contains(expectedTitle));
    }
}