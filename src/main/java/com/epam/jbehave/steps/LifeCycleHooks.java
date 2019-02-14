package com.epam.jbehave.steps;

import com.epam.jbehave.driver.DriverSingleton;
import org.jbehave.core.annotations.AfterStories;
import org.jbehave.core.annotations.BeforeStories;
import org.jbehave.core.steps.Steps;

public class LifeCycleHooks extends Steps {

    @BeforeStories
    public void setUp() {

    }

    @AfterStories
    public void tearDown() {
        DriverSingleton.closeDriver();
    }
}
