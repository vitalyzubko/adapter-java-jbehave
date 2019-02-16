package com.epam.jbehave.utils;

import com.epam.jira.core.TestResultProcessor;
import org.jbehave.core.model.Scenario;
import org.jbehave.core.reporters.NullStoryReporter;
import org.jbehave.core.steps.StepResult;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.util.concurrent.TimeUnit.*;

public class JIRAReporterCore extends NullStoryReporter {

    private static final String JIRA_KEY_NAME = "JIRATestKey";
    private final Map<String, StepResult.Type> unSuccessfulStepResults = new HashMap<>();

    private static boolean isJiraKeyPresentInStory = false;
    private static boolean isJiraKeyPresentInScenario;
    private static String jiraKey;
    private long scenarioStartTime;
    private String scenarioSummary;

    @Override
    public void afterStory(boolean b) {
        if (isJiraKeyPresentInStory) {
            TestResultProcessor.saveResults();
        }
    }

    @Override
    public void beforeScenario(Scenario scenario) {
        if (scenario.hasMeta()) {
            jiraKey = "";
            scenarioSummary = "";
            unSuccessfulStepResults.clear();
            isJiraKeyPresentInScenario = scenario.getMeta().hasProperty(JIRA_KEY_NAME);
            if (isJiraKeyPresentInScenario) {
                isJiraKeyPresentInStory = true;
                jiraKey = scenario.getMeta().getProperty(JIRA_KEY_NAME).trim();
                if (isNotEmpty(jiraKey)) {
                    scenarioStartTime = System.nanoTime();
                    TestResultProcessor.startJiraAnnotatedTest(jiraKey);
                } else {
                    System.out.println("@" + JIRA_KEY_NAME + " in scenario Meta is empty");
                }
            }
        }
    }

    @Override
    public void afterScenario() {
        if (isNotEmpty(jiraKey)) {
            this.generateScenarioSummary(unSuccessfulStepResults);
            TestResultProcessor.setStatus(getScenarioStatusType());
            TestResultProcessor.setTime(getScenarioDuration());
            if (isNotEmpty(scenarioSummary)) {
                TestResultProcessor.addToSummary("Wrong steps execution:");
                TestResultProcessor.addToSummary(scenarioSummary);
            }
        }
    }

    @Override
    public void ignorable(String step) {
        saveUnSuccessfulStepResult(() -> unSuccessfulStepResults.put(step, StepResult.Type.IGNORABLE));
    }

    @Override
    public void comment(String step) {
        saveUnSuccessfulStepResult(() -> unSuccessfulStepResults.put(step, StepResult.Type.COMMENT));
    }

    @Override
    public void pending(String step) {
        saveUnSuccessfulStepResult(() -> unSuccessfulStepResults.put(step, StepResult.Type.PENDING));
    }

    @Override
    public void notPerformed(String step) {
        saveUnSuccessfulStepResult(() -> unSuccessfulStepResults.put(step, StepResult.Type.NOT_PERFORMED));
    }

    @Override
    public void failed(String step, Throwable throwable) {
        saveUnSuccessfulStepResult(() -> unSuccessfulStepResults.put(step, StepResult.Type.FAILED));
        if (isNotEmpty(jiraKey)) {
            TestResultProcessor.addException(throwable);
        }
    }

    private void saveUnSuccessfulStepResult(Runnable runnable) {
        if (isNotEmpty(jiraKey)) {
            runnable.run();
        }
    }

    private String getScenarioStatusType() {
        return unSuccessfulStepResults.isEmpty() ? "Passed" : "Failed";
    }

    private void generateScenarioSummary(Map<String, StepResult.Type> stepResults) {
        stepResults.entrySet().stream()
                .filter(result -> result.getValue() != StepResult.Type.FAILED)
                .forEach(result -> scenarioSummary += result.getValue() + " step '" + result.getKey() + "'");
    }

    private String getScenarioDuration() {
        long duration = System.nanoTime() - scenarioStartTime;
        return MINUTES.convert(duration, NANOSECONDS) + "m " +
                SECONDS.convert(duration, NANOSECONDS) + "." +
                MILLISECONDS.convert(duration, NANOSECONDS) + "s";
    }

    public static void addParameter(String title, String value) {
        if (isJiraKeyPresentInScenario) {
            if (isNotEmpty(jiraKey)) {
                TestResultProcessor.addParameter(Objects.requireNonNull(title), Objects.requireNonNull(value));
            } else {
                System.out.println("Parameter '" + title + "' with '" + value + "' value cannot be written for the empty @" + JIRA_KEY_NAME + " in Meta scenario");
            }
        }
    }

    public static void addAttachment(File file) {
        if (isJiraKeyPresentInScenario) {
            if (isNotEmpty(jiraKey)) {
                TestResultProcessor.addAttachment(Objects.requireNonNull(file));
            } else {
                System.out.println("Attachment '" + file.getName() + "' cannot be written for the empty @" + JIRA_KEY_NAME + " in Meta scenario");
            }
        }
    }

    private static boolean isNotEmpty(String str) {
        return !str.trim().isEmpty();
    }
}