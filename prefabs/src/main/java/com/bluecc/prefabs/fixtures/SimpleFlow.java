package com.bluecc.prefabs.fixtures;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.bpmn.instance.Process;

import java.io.File;

public class SimpleFlow {
    public static void main(String[] args) {
        SimpleFlow flow=new SimpleFlow();
        flow.createEmptyModel();
        // flow.createProcessWithOneTask();
        flow.createProcessWithParallelGateway();

        // ready
        // BpmnModelInstance saga = flow.done();
        // optional: Write to file to be able to open it in Camunda Modeler
        Bpmn.writeModelToFile(new File("simple2.bpmn"), flow.modelInstance);
    }

    public void createProcessWithOneTask() {
        // create process
        Process process = createElement(definitions, "process-with-one-task", Process.class);

        // create elements
        StartEvent startEvent = createElement(process, "start", StartEvent.class);
        UserTask task1 = createElement(process, "task1", UserTask.class);
        EndEvent endEvent = createElement(process, "end", EndEvent.class);

        // create flows
        createSequenceFlow(process, startEvent, task1);
        createSequenceFlow(process, task1, endEvent);
    }

    public void createProcessWithParallelGateway() {
        // create process
        Process process = createElement(definitions, "process-with-parallel-gateway", Process.class);

        // create elements
        StartEvent startEvent = createElement(process, "start", StartEvent.class);
        ParallelGateway fork = createElement(process, "fork", ParallelGateway.class);
        UserTask task1 = createElement(process, "task1", UserTask.class);
        ServiceTask task2 = createElement(process, "task2", ServiceTask.class);
        ParallelGateway join = createElement(process, "join", ParallelGateway.class);
        EndEvent endEvent = createElement(process, "end", EndEvent.class);

        // create flows
        createSequenceFlow(process, startEvent, fork);
        createSequenceFlow(process, fork, task1);
        createSequenceFlow(process, fork, task2);
        createSequenceFlow(process, task1, join);
        createSequenceFlow(process, task2, join);
        createSequenceFlow(process, join, endEvent);
    }

    public BpmnModelInstance modelInstance;
    public Definitions definitions;
    public Process process;

    public void createEmptyModel() {
        modelInstance = Bpmn.createEmptyModel();
        definitions = modelInstance.newInstance(Definitions.class);
        definitions.setTargetNamespace("http://camunda.org/examples");
        modelInstance.setDefinitions(definitions);
    }

    protected <T extends BpmnModelElementInstance> T createElement(BpmnModelElementInstance parentElement, String id, Class<T> elementClass) {
        T element = modelInstance.newInstance(elementClass);
        element.setAttributeValue("id", id, true);
        parentElement.addChildElement(element);
        return element;
    }

    public SequenceFlow createSequenceFlow(Process process, FlowNode from, FlowNode to) {
        SequenceFlow sequenceFlow = createElement(process, from.getId() + "-" + to.getId(), SequenceFlow.class);
        process.addChildElement(sequenceFlow);
        sequenceFlow.setSource(from);
        from.getOutgoing().add(sequenceFlow);
        sequenceFlow.setTarget(to);
        to.getIncoming().add(sequenceFlow);
        return sequenceFlow;
    }
}
