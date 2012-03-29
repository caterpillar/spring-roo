package org.springframework.roo.addon.tailor.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.roo.addon.tailor.CommandTransformation;
import org.springframework.roo.project.MavenOperationsImpl;

/**
 * Tests for {@link Focus}
 * 
 * @author Birgitta Boeckeler
 */
public class TestFocusModule {

    @Test
    public void testStandard() {
        Focus action = createTestActionObject();
        ActionConfig config = ActionConfigFactory.focusModuleAction("domain");
        CommandTransformation trafo = new CommandTransformation(
                "command not relevant for this test");
        action.execute(trafo, config);
        // Test data: "domain-it" module is first, "domain" second.
        // Expected that action will choose "domain-it" as the first positive
        // match
        Assert.assertTrue(trafo.getOutputCommands().contains(
                "module focus --moduleName domain-it"));
    }

    /**
     * Tests a list of match strings for the module name
     */
    @Test
    public void testList() {

        Focus action = createTestActionObject();
        ActionConfig config = ActionConfigFactory.focusModuleAction("data,it");
        CommandTransformation trafo = new CommandTransformation(
                "command not relevant for this test");
        action.execute(trafo, config);
        // Test data: "data" module is first, "data-it" second.
        // Expected that action will discard "data" and choose "data-it"
        Assert.assertTrue(trafo.getOutputCommands().contains(
                "module focus --moduleName data-it"));
    }

    @Test
    public void testListWithout() {
        Focus action = createTestActionObject();
        ActionConfig config = ActionConfigFactory
                .focusModuleAction("domain,/it");
        CommandTransformation trafo = new CommandTransformation(
                "command not relevant for this test");
        action.execute(trafo, config);
        // Test data: "domain-it" module is first, "domain" second.
        // Expected that action will discard "domain-it" because it contains
        // "it", and choose "domain"
        Assert.assertTrue(trafo.getOutputCommands().contains(
                "module focus --moduleName domain"));
    }

    @Test
    public void testModulesNotLoadedYet() {
        Focus action = new Focus();
        action.projectOperations = new MockProjectOperationsEmpty();

        ActionConfig config = ActionConfigFactory.focusModuleAction("domain");
        CommandTransformation trafo = new CommandTransformation(
                "command not relevant for this test");

        try {
            action.execute(trafo, config);
            Assert.fail("Should throw exception (is caught by DefaultTailorImpl, but this test goes directly to the action)");
        }
        catch (IllegalStateException e) {
            Assert.assertTrue(trafo.getOutputCommands().isEmpty());
        }

    }

    private Focus createTestActionObject() {
        Focus action = new Focus();
        action.projectOperations = new MockProjectOperations();
        return action;
    }

    /**
     * Mock ProjectOperations to return a list of test module names
     */
    private class MockProjectOperations extends MavenOperationsImpl {

        @Override
        public Collection<String> getModuleNames() {
            List<String> result = new ArrayList<String>();
            result.add("");
            result.add("domain-it");
            result.add("domain");
            result.add("data");
            result.add("data-it");
            return result;
        }

    }

    private class MockProjectOperationsEmpty extends MavenOperationsImpl {

        @Override
        public Collection<String> getModuleNames() {
            return Collections.emptyList();
        }

    }

}
