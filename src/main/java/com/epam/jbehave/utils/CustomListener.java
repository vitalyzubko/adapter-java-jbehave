package com.epam.jbehave.utils;

import org.jbehave.core.model.*;
import org.jbehave.core.reporters.StoryReporter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CustomListener implements StoryReporter {
    public void storyNotAllowed(Story story, String s) {

    }

    public void storyCancelled(Story story, StoryDuration storyDuration) {

    }

    public void beforeStory(Story story, boolean b) {

    }

    public void afterStory(boolean b) {

    }

    public void narrative(Narrative narrative) {

    }

    public void lifecyle(Lifecycle lifecycle) {

    }

    public void scenarioNotAllowed(Scenario scenario, String s) {

    }

    public void beforeScenario(Scenario scenario) {
        System.out.println("!!!! Before scenario: " + scenario.getMeta().getPropertyNames() + "=" + scenario.getMeta().getProperty("JIRA"));
    }

    public void beforeScenario(String s) {

    }

    public void scenarioMeta(Meta meta) {

    }

    public void afterScenario() {

    }

    public void beforeGivenStories() {

    }

    public void givenStories(GivenStories givenStories) {

    }

    public void givenStories(List<String> list) {

    }

    public void afterGivenStories() {

    }

    public void beforeExamples(List<String> list, ExamplesTable examplesTable) {

    }

    public void example(Map<String, String> map) {

    }

    public void afterExamples() {

    }

    public void beforeStep(String s) {

    }

    public void successful(String s) {
        System.out.println("!!! Successful step: " + s);
    }

    public void ignorable(String s) {

    }

    public void comment(String s) {

    }

    public void pending(String s) {

    }

    public void notPerformed(String s) {

    }

    public void failed(String s, Throwable throwable) {
        System.out.println("!!!!! Failed: " + throwable.getCause());
        Arrays.stream(throwable.getStackTrace()).map(StackTraceElement::toString).forEach(System.out::println);
    }

    public void failedOutcomes(String s, OutcomesTable outcomesTable) {

    }

    public void restarted(String s, Throwable throwable) {

    }

    public void restartedStory(Story story, Throwable throwable) {

    }

    public void dryRun() {

    }

    public void pendingMethods(List<String> list) {

    }
}
