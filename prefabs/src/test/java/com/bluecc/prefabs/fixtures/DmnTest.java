package com.bluecc.prefabs.fixtures;

import org.camunda.bpm.model.dmn.Dmn;
import org.camunda.bpm.model.dmn.DmnModelInstance;
import org.camunda.bpm.model.dmn.instance.Decision;
import org.camunda.bpm.model.dmn.instance.DecisionTable;
import org.camunda.bpm.model.dmn.instance.InputEntry;
import org.camunda.bpm.model.dmn.instance.Rule;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DmnTest {
    @Test
    public void shouldReadJDK9StyleModel()
    {
        DmnModelInstance modelInstance =
                Dmn.readModelFromStream(DmnTest.class.getResourceAsStream("JDK9-style-CDATA.dmn"));

        Decision decision = (Decision) modelInstance.getDefinitions().getDrgElements().iterator().next();

        DecisionTable decisionTable = (DecisionTable) decision.getExpression();

        Rule rule = decisionTable.getRules().iterator().next();
        InputEntry inputEntry = rule.getInputEntries().iterator().next();
        String textContent = inputEntry.getText().getTextContent();
        assertThat(textContent).isEqualTo(">= 1000");
    }
}
