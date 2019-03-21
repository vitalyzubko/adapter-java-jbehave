package com.epam.jbehave.steps;

import com.epam.jbehave.driver.DriverSingleton;
import com.epam.jbehave.utils.JIRAReporter;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.steps.Steps;
import org.junit.Assert;
import org.openqa.selenium.*;

public class Stepdefs extends Steps {

    private final WebDriver driver = DriverSingleton.getDriver();

    @Given("I am on page with url '$url'")
    public void givenIAmOnYouTubeMainPage(String url) {
        driver.navigate().to(url);
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
        JIRAReporter.addParameter("Test title", "Test value");

        System.out.println("[expected: '" + actualTitle + "' contains '" + expectedTitle + "'][actual: '" + actualTitle.contains(expectedTitle) + "']");
        Assert.assertTrue(actualTitle.contains(expectedTitle));
    }
}