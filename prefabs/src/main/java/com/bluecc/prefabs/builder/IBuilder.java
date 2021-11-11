package com.bluecc.prefabs.builder;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;

public interface IBuilder {
    ProcessEngine engine();

    void startProcess(ProcessEngine camunda, String processKey);

    BpmnModelInstance build(String targetFile);

    default void deploy(ProcessEngine camunda, String resourceName) {
        BpmnModelInstance saga = build(resourceName + ".bpmn");

        // finish Saga and deploy it to Camunda
        camunda.getRepositoryService().createDeployment() //
                .addModelInstance(resourceName + ".bpmn", saga) //
                .deploy();
    }
}
