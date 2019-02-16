package com.epam.jbehave.steps;

import com.epam.jbehave.driver.DriverSingleton;
import com.epam.jbehave.utils.JIRAReporter;
import org.jbehave.core.annotations.AfterScenario;
import org.jbehave.core.annotations.AfterStories;
import org.jbehave.core.annotations.BeforeStories;
import org.jbehave.core.steps.Steps;

public class LifeCycleHooks extends Steps {

    @BeforeStories
    public void setUp() {

    }

    @AfterScenario
    public void afterScenarioHook() {
        JIRAReporter.collectData();
    }

    @AfterStories
    public void tearDown() {
        DriverSingleton.closeDriver();
    }
}