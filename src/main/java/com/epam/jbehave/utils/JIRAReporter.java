package com.epam.jbehave.utils;

import org.jbehave.core.model.Story;
import org.jbehave.core.reporters.NullStoryReporter;
import org.jbehave.core.reporters.StoryReporter;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class JIRAReporter extends NullStoryReporter {

    private static Method beforeStory;
    private static Method beforeScenario;
    private static Method afterScenario;
    private static Method ignorable;
    private static Method pending;
    private static Method notPerformed;
    private static Method failed;
    private static Method addParameter;
    private static Method addAttachment;

    static {
        try {
            beforeStory = JIRAReporterCore.class.getMethod("beforeStory", Story.class, Boolean.TYPE);
            beforeScenario = JIRAReporterCore.class.getMethod("beforeScenario", String.class);
            afterScenario = JIRAReporterCore.class.getMethod("afterScenario");
            ignorable = JIRAReporterCore.class.getMethod("ignorable", String.class);
            pending = JIRAReporterCore.class.getMethod("pending", String.class);
            notPerformed = JIRAReporterCore.class.getMethod("notPerformed", String.class);
            failed = JIRAReporterCore.class.getMethod("failed", String.class, Throwable.class);
            addParameter = JIRAReporterCore.class.getMethod("addParameter", String.class, String.class);
            addAttachment = JIRAReporterCore.class.getMethod("addAttachment", File.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private static final StoryReporter delegate = new JIRAReporterCore();
    private static final List<JIRAReporter.DelayedMethod> delayedMethods = Collections.synchronizedList(new ArrayList<>());
    private static final Map<String, String> stepParameters = Collections.synchronizedMap(new HashMap<>());
    private static final Map<Integer, Map<String, String>> scenarioParametersContainer = Collections.synchronizedMap(new HashMap<>());
    private static final List<File> stepAttachments = Collections.synchronizedList(new ArrayList<>());
    private static final Map<Integer, List<File>> scenarioAttachmentsContainer = Collections.synchronizedMap(new HashMap<>());
    private static volatile int scenarioIndex;

    @Override
    public synchronized void beforeStory(Story story, boolean givenStory) {
        scenarioIndex = 0;
        delayedMethods.add(new JIRAReporter.DelayedMethod(beforeStory, story, givenStory));
    }

    @Override
    public void afterStory(boolean b) {
        invokeDelayed();
    }

    @Override
    public void beforeScenario(String title) {
        delayedMethods.add(new JIRAReporter.DelayedMethod(beforeScenario, title));
    }

    @Override
    public void afterScenario() {
        delayedMethods.add(new JIRAReporter.DelayedMethod(afterScenario));
    }

    @Override
    public void ignorable(String step) {
        delayedMethods.add(new JIRAReporter.DelayedMethod(ignorable, step));
    }

    @Override
    public void pending(String step) {
        delayedMethods.add(new JIRAReporter.DelayedMethod(pending, step));
    }

    @Override
    public void notPerformed(String step) {
        delayedMethods.add(new JIRAReporter.DelayedMethod(notPerformed, step));
    }

    @Override
    public void failed(String step, Throwable throwable) {
        delayedMethods.add(new JIRAReporter.DelayedMethod(failed, step, throwable));
    }

    public static synchronized void saveResults() {
        JIRAReporterCore.saveResults();
    }

    public static synchronized void addParameter(String title, String value) {
        stepParameters.put(Objects.requireNonNull(title), Objects.requireNonNull(value));
    }

    private void handleParameter(String title, String value) {
        new JIRAReporter.DelayedMethod(addParameter, title, value).invoke(delegate);
    }

    public static synchronized void addAttachment(File file) {
        stepAttachments.add(Objects.requireNonNull(file));
    }

    private void handleAttachment(File file) {
        new JIRAReporter.DelayedMethod(addAttachment, file).invoke(delegate);
    }

    public static synchronized void collectData() {
        if (isNotEmpty(stepParameters)) {
            scenarioParametersContainer.put(scenarioIndex, new HashMap<>());
            stepParameters.forEach((key, value) -> scenarioParametersContainer.get(scenarioIndex).put(key, value));
            stepParameters.clear();
        }
        if (isNotEmpty(stepAttachments)) {
            scenarioAttachmentsContainer.put(scenarioIndex, new ArrayList<>());
            stepAttachments.forEach(attachment -> scenarioAttachmentsContainer.get(scenarioIndex).add(attachment));
            stepAttachments.clear();
        }
        scenarioIndex++;
    }

    private void invokeDelayed() {
        synchronized (delegate) {
            int scenarioCount = 0;
            for (JIRAReporter.DelayedMethod delayedMethod : delayedMethods) {
                delayedMethod.invoke(delegate);
                if (delayedMethod.getMethod().equals(afterScenario)) {
                    if (scenarioParametersContainer.containsKey(scenarioCount)) {
                        scenarioParametersContainer.get(scenarioCount).forEach(this::handleParameter);
                    }
                    if (scenarioAttachmentsContainer.containsKey(scenarioCount)) {
                        scenarioAttachmentsContainer.get(scenarioCount).forEach(this::handleAttachment);
                    }
                    scenarioCount++;
                }
            }
            delayedMethods.clear();
            scenarioParametersContainer.clear();
            scenarioAttachmentsContainer.clear();
        }
    }

    private static boolean isNotEmpty(Object object) {
        if (object instanceof Map) {
            return !((Map) object).isEmpty();
        } else if (object instanceof Collection) {
            return !((Collection) object).isEmpty();
        }
        return false;
    }

    private static class DelayedMethod {
        private Method method;
        private Object[] args;

        DelayedMethod(Method method, Object... args) {
            this.method = method;
            this.args = args;
        }

        void invoke(StoryReporter delegate) {
            try {
                method.invoke(delegate, args);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("" + method, e);
            }
        }

        Method getMethod() {
            return method;
        }
    }
}