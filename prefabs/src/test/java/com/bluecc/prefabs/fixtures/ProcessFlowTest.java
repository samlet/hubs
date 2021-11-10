package com.bluecc.prefabs.fixtures;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.xml.Model;
import org.camunda.bpm.model.xml.type.ModelElementType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ProcessFlowTest {
    private BpmnModelInstance modelInstance;
    private static ModelElementType taskType;
    private static ModelElementType gatewayType;
    private static ModelElementType eventType;
    private static ModelElementType processType;

    @BeforeAll
    public static void getElementTypes() {
        Model model = Bpmn.createEmptyModel().getModel();
        taskType = model.getType(Task.class);
        gatewayType = model.getType(Gateway.class);
        eventType = model.getType(Event.class);
        processType = model.getType(Process.class);
    }

    @Test
    public void testExtend() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .userTask()
                .id("task1")
                .serviceTask()
                .endEvent()
                .done();

        assertThat(modelInstance.getModelElementsByType(taskType))
                .hasSize(2);

        UserTask userTask = modelInstance.getModelElementById("task1");
        SequenceFlow outgoingSequenceFlow = userTask.getOutgoing().iterator().next();
        FlowNode serviceTask = outgoingSequenceFlow.getTarget();
        userTask.getOutgoing().remove(outgoingSequenceFlow);
        userTask.builder()
                .scriptTask()
                .userTask()
                .connectTo(serviceTask.getId());

        assertThat(modelInstance.getModelElementsByType(taskType))
                .hasSize(4);
    }


    @Test
    public void testCreateProcessWithBusinessRuleTask() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .businessRuleTask()
                .endEvent()
                .done();

        assertThat(modelInstance.getModelElementsByType(eventType))
                .hasSize(2);
        assertThat(modelInstance.getModelElementsByType(taskType))
                .hasSize(1);
    }

    @Test
    public void testCreateProcessWithScriptTask() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .scriptTask()
                .endEvent()
                .done();

        assertThat(modelInstance.getModelElementsByType(eventType))
                .hasSize(2);
        assertThat(modelInstance.getModelElementsByType(taskType))
                .hasSize(1);
    }

    @Test
    public void testCreateProcessWithReceiveTask() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .receiveTask()
                .endEvent()
                .done();

        assertThat(modelInstance.getModelElementsByType(eventType))
                .hasSize(2);
        assertThat(modelInstance.getModelElementsByType(taskType))
                .hasSize(1);
    }

}