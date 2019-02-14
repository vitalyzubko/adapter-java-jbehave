package com.epam.jbehave;

import com.epam.jbehave.steps.LifeCycleHooks;
import com.epam.jbehave.steps.Stepdefs;
import com.epam.jbehave.utils.CustomListener;
import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.embedder.Embedder;
import org.jbehave.core.io.LoadFromClasspath;
import org.jbehave.core.junit.JUnitStory;
import org.jbehave.core.reporters.Format;
import org.jbehave.core.reporters.StoryReporterBuilder;

import java.util.Arrays;
import java.util.List;

public class TestRunner extends JUnitStory {

    private static List<String> storyPaths = Arrays.asList("epamSearch.story");

    @Override
    public void run() {
        Embedder embedder = this.configuredEmbedder();
        embedder.candidateSteps().add(new Stepdefs());
        embedder.candidateSteps().add(new LifeCycleHooks());
        embedder.runStoriesAsPaths(storyPaths);
    }

    @Override
    public Configuration configuration() {
        return new MostUsefulConfiguration()
                .useStoryLoader(new LoadFromClasspath(this.getClass().getClassLoader()))
                .useStoryReporterBuilder(new StoryReporterBuilder()
                        .withDefaultFormats()
                        .withFormats(Format.HTML,Format.CONSOLE)
                        .withRelativeDirectory("jbehave-report")
                        .withReporters(new CustomListener())
                );
    }
}