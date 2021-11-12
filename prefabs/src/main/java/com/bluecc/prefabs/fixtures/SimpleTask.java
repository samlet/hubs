package com.bluecc.prefabs.fixtures;

import com.bluecc.prefabs.builder.BlockBuilderMain;
import com.bluecc.prefabs.builder.BuilderBase;
import com.bluecc.prefabs.demo.camunda.adapter.*;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.builder.ProcessBuilder;

import java.io.File;

public class SimpleTask extends BuilderBase {
    public static void main(String[] args) {
        SimpleTask blocks = new SimpleTask();
        ProcessEngine camunda = blocks.engine();
        blocks.deploy(camunda, "trip");
        blocks.startProcess(camunda, "trip");
    }

    public void startProcess(ProcessEngine camunda, String processKey) {
        // now we can start running instances of our saga - its state will be persisted
        camunda.getRuntimeService().startProcessInstanceByKey(processKey,
                Variables.putValue("name", "trip1"));
    }

    public BpmnModelInstance build(String targetFile) {
        // define saga as BPMN process
        ProcessBuilder flow = Bpmn.createExecutableProcess("trip");

        // - flow of activities and compensating actions
        flow.startEvent()
                .serviceTask("car").name("Reserve car").camundaClass(ReserveNotifyAdapter.class)
                .serviceTask("hotel").name("Book hotel").camundaClass(BookHotelAdapter.class)
                .endEvent();

        // ready
        BpmnModelInstance saga = flow.done();
        // optional: Write to file to be able to open it in Camunda Modeler
        Bpmn.writeModelToFile(new File(targetFile), saga);
        return saga;
    }
}
