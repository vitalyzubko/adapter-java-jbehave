package com.epam.jbehave;

import com.epam.jbehave.steps.LifeCycleHooks;
import com.epam.jbehave.steps.Stepdefs;
import com.epam.jbehave.utils.JIRAReporter;
import com.github.valfirst.jbehave.junit.monitoring.JUnitReportingRunner;
import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.embedder.Embedder;
import org.jbehave.core.io.CodeLocations;
import org.jbehave.core.io.LoadFromClasspath;
import org.jbehave.core.io.StoryFinder;
import org.jbehave.core.junit.JUnitStories;
import org.jbehave.core.reporters.Format;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.steps.InjectableStepsFactory;
import org.jbehave.core.steps.InstanceStepsFactory;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

@RunWith(JUnitReportingRunner.class)
public class TestRunner extends JUnitStories {

    @Override
    public void run() {
        JUnitReportingRunner.recommendedControls(configuredEmbedder());
        Embedder embedder = this.configuredEmbedder();
        embedder.runStoriesAsPaths(storyPaths());
    }

    @Override
    protected List<String> storyPaths() {
        return new StoryFinder().
                findPaths(CodeLocations.codeLocationFromClass(this.getClass()),
                        Arrays.asList("**/*.story"),
                        Arrays.asList(""));
    }

    @Override
    public Configuration configuration() {
        return new MostUsefulConfiguration()
                .useStoryLoader(new LoadFromClasspath(this.getClass().getClassLoader()))
                .useStoryReporterBuilder(new StoryReporterBuilder()
                        .withDefaultFormats()
                        .withFormats(Format.TXT, Format.HTML)
                        .withRelativeDirectory("jbehave-report")
                        .withReporters(new JIRAReporter())
                );
    }

    @Override
    public InjectableStepsFactory stepsFactory() {
        return new InstanceStepsFactory(configuration(),
                Arrays.asList(new Stepdefs(), new LifeCycleHooks()));
    }
}