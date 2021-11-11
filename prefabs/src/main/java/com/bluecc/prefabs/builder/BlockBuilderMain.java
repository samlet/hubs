package com.bluecc.prefabs.builder;

import com.bluecc.prefabs.demo.camunda.adapter.*;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.builder.ProcessBuilder;

import java.io.File;

public class BlockBuilderMain extends BuilderBase{

    public static void main(String[] args) {
        BlockBuilderMain blocks = new BlockBuilderMain();
        ProcessEngine camunda = blocks.engine();
        blocks.deploy(camunda, "trip");
        blocks.startProcess(camunda, "trip");
    }

    public void startProcess(ProcessEngine camunda, String processKey) {
        // now we can start running instances of our saga - its state will be persisted
        camunda.getRuntimeService().startProcessInstanceByKey(processKey,
                Variables.putValue("name", "trip1"));
        camunda.getRuntimeService().startProcessInstanceByKey(processKey,
                Variables.putValue("name", "trip2"));
    }

    public BpmnModelInstance build(String targetFile) {
        // define saga as BPMN process
        ProcessBuilder flow = Bpmn.createExecutableProcess("trip");

        // - flow of activities and compensating actions
        flow.startEvent()
                .serviceTask("car").name("Reserve car").camundaClass(ReserveCarAdapter.class)
                .boundaryEvent().compensateEventDefinition().compensateEventDefinitionDone()
                .compensationStart().serviceTask("CancelCar").camundaClass(CancelCarAdapter.class).compensationDone()
                .serviceTask("hotel").name("Book hotel").camundaClass(BookHotelAdapter.class)
                .boundaryEvent().compensateEventDefinition().compensateEventDefinitionDone()
                .compensationStart().serviceTask("CancelHotel").camundaClass(CancelHotelAdapter.class).compensationDone()
                .serviceTask("flight").name("Book flight").camundaClass(BookFlightAdapter.class)
                .boundaryEvent().compensateEventDefinition().compensateEventDefinitionDone()
                .compensationStart().serviceTask("CancelFlight").camundaClass(CancelFlightAdapter.class).compensationDone()
                .endEvent();

        // - trigger compensation in case of any exception (other triggers are possible)
        flow.eventSubProcess()
                .startEvent().error("java.lang.Throwable")
                .intermediateThrowEvent().compensateEventDefinition().compensateEventDefinitionDone()
                .endEvent();

        // ready
        BpmnModelInstance saga = flow.done();
        // optional: Write to file to be able to open it in Camunda Modeler
        Bpmn.writeModelToFile(new File(targetFile), saga);
        return saga;
    }

}
