# JBehave java adapter for JIRA

JBehave java adapter (further - adapter) gathers scenarios execution results and than generates `jira-tm-report.xml` file which is used by [Test Management Plugin](https://github.com/at-lab-development/jenkins-test-management-plugin).

Adapter gathers the following scenario execution results:
- status (passed or failed);
- summary (reason of test failure, if so, and/or any user's info) - optional;
- execution time;
- attachments (error stacktrace if scenario is failed and/or any user's info) - optional;
- parameters which user is going to cast to JIRA issue - optional.

Adapter is thread-safety.

## Requirements

Adapter works with the following restrictions:
- JDK version 1.8 and above;
- JBehave version 4.0 and above.

## Installation

Add repository in `pom.xml` file:
```xml
<repositories>
    <repository>
        <id>JiraTestNG-mvn-repo</id>
        <url>https://raw.github.com/vitalyzubko/adapter-java-jbehave/mvn-repo</url>
        <snapshots>
            <enabled>true</enabled>
            <updatePolicy>always</updatePolicy>
        </snapshots>
    </repository>
....
</repositories>
```
And than add dependency:
```xml
<dependencies>
    <dependency>
        <groupId>com.epam.jira</groupId>
        <artifactId>adapter-java-jbehave</artifactId>
        <version>1.0</version>
     </dependency>
....
</dependencies>
```

## Usage

Add `JIRAReporter` in `StoryReporterBuilder` as on example below:

```java
@Override
public Configuration configuration() {
   return new MostUsefulConfiguration()
           ....
           .useStoryReporterBuilder(new StoryReporterBuilder()
           ....
           .withReporters(new JIRAReporter())
            );
}
```

Create JBehave `@AfterStrories` hook and invoke method `JIRAReporter.saveResults()` in it:
```java
@AfterStories
public void afterStoriesHook() {
   JIRAReporter.saveResults();
}
```
In JBehave story add `@JIRATestKey` meta with Jira ticket key via space (on example it is `@JIRATestKey EPMFARMATS-7894`).
```txt
Scenario: Passed epam.com open page
Meta:
@JIRATestKey EPMFARMATS-7894
Given I am on page with url 'https://www.epam.com'
Then see 'EPAM' in title
```

That is it! Now adapter is ready for usage. Just run your tests and adapter will gather all results in `jira-tm-report.xml` (see `target` folder to check).

Please, NOTE: If you want to cast `parameters` and/or `attachments` in Jira issue (both points are optional to use) you should create JBehave `@AfterScenario` hook and invoke `JIRAReporter.collectData()` in it.
```java
@AfterScenario
public void afterScenarioHook() {
  JIRAReporter.collectData();
}
```
Than in steps you can invoke methods `JIRAReporter.addParameter()` and/or `JIRAReporter.addAttachment()`.
```java
    @Then("I should see '$text' in list video")
    public void IShouldSeeTitleInListVideo(String expectedVideoTitle) {
        ....
        String actualVideoTitle = videoTitle.getText();

        JIRAReporter.addParameter("Logo container title", actualVideoTitle);
        JIRAReporter.addAttachment(((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE));

        Assert.assertEquals(expectedVideoTitle, actualVideoTitle);
    }
```

## Test results mapping
All kind of JBehave step error (failed, pending, ignorable, not performed) will be cast to Jira issue as failed test.