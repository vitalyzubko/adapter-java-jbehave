package com.epam.jbehave.utils;

import com.epam.jira.core.TestResultProcessor;
import org.jbehave.core.model.Meta;
import org.jbehave.core.model.Story;
import org.jbehave.core.reporters.NullStoryReporter;
import org.jbehave.core.steps.StepResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.util.concurrent.TimeUnit.*;

class JIRAReporterCore extends NullStoryReporter {

    private static final String JIRA_KEY_NAME = "JIRATestKey";
    private static final Logger LOGGER = LoggerFactory.getLogger(JIRAReporterCore.class);

    private final Map<String, StepResult.Type> unSuccessfulStepResults = new HashMap<>();
    private final Map<Integer, Meta> scenarioMetas = new HashMap<>();

    private static boolean isJiraKeyPresentInStories = false;
    private static boolean isJiraKeyPresentInScenario;
    private static String jiraKey;
    private long scenarioStartTime;
    private String scenarioSummary;
    private int scenarioIndex;

    @Override
    public void beforeStory(Story story, boolean givenStory) {
        scenarioIndex = 0;
        story.getScenarios().forEach(scenario -> scenarioMetas.put(scenarioIndex++, scenario.getMeta()));
        scenarioIndex = 0;
    }

    @Override
    public void beforeScenario(String title) {
        jiraKey = "";
        scenarioSummary = "";
        isJiraKeyPresentInScenario = false;
        unSuccessfulStepResults.clear();
        if (isNotEmpty(scenarioMetas.get(scenarioIndex))) {
            isJiraKeyPresentInScenario = scenarioMetas.get(scenarioIndex).hasProperty(JIRA_KEY_NAME);
            if (isJiraKeyPresentInScenario) {
                isJiraKeyPresentInStories = true;
                jiraKey = scenarioMetas.get(scenarioIndex).getProperty(JIRA_KEY_NAME).trim();
                if (isNotEmpty(jiraKey)) {
                    scenarioStartTime = System.nanoTime();
                    TestResultProcessor.startJiraAnnotatedTest(jiraKey);
                } else {
                    LOGGER.info("@" + JIRA_KEY_NAME + " in '" + title + "' scenario is empty");
                }
            }
        }
        scenarioIndex++;
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

    public static void saveResults() {
        if (isJiraKeyPresentInStories) {
            TestResultProcessor.saveResults();
        }
    }

    public static void addParameter(String title, String value) {
        if (isJiraKeyPresentInScenario) {
            if (isNotEmpty(jiraKey)) {
                TestResultProcessor.addParameter(Objects.requireNonNull(title), Objects.requireNonNull(value));
            } else {
                LOGGER.info("Parameter '" + title +"' with '" + value + "' value cannot be written due to empty @" + JIRA_KEY_NAME);
            }
        }
    }

    public static void addAttachment(File file) {
        if (isJiraKeyPresentInScenario) {
            if (isNotEmpty(jiraKey)) {
                TestResultProcessor.addAttachment(Objects.requireNonNull(file));
            } else {
                LOGGER.info("Attachment '" + file.getName() + "' cannot be written due to empty @" + JIRA_KEY_NAME);
            }
        }
    }

    private static boolean isNotEmpty(Object object) {
        if (object instanceof Meta) {
            return !((Meta) object).isEmpty();
        } else if (object instanceof String) {
            return !((String) object).trim().isEmpty();
        }
        return false;
    }
}