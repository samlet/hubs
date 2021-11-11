package com.bluecc.prefabs.builder;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.builder.AbstractActivityBuilder;
import org.camunda.bpm.model.bpmn.builder.AbstractFlowNodeBuilder;
import org.camunda.bpm.model.bpmn.builder.ProcessBuilder;

public class BlockBuilder {

  @SuppressWarnings("rawtypes")
  private AbstractFlowNodeBuilder saga;
  private BpmnModelInstance bpmnModelInstance;
  private String name;
  private ProcessBuilder process;

  public BlockBuilder(String name) {
    this.name = name;
  }

  public static BlockBuilder newSaga(String name) {
    BlockBuilder builder = new BlockBuilder(name);
    return builder.start();
  }
  
  public BpmnModelInstance getModel() {
    if (bpmnModelInstance==null) {
      bpmnModelInstance = saga.done();
    }
    return bpmnModelInstance;
  }

  public BlockBuilder start() {
    process = Bpmn.createExecutableProcess(name);
    saga = process.startEvent("Start-" + name);
    return this;
  }
  
  public BlockBuilder end() {
    saga = saga.endEvent("End-" + name);
    return this;
  }  

  @SuppressWarnings("rawtypes")
  public BlockBuilder activity(String name, Class adapterClass) {
    // this is very handy and could also be done inline above directly
    String id = "Activity-" + name.replace(" ", "-"); // risky thing ;-)
    saga = saga.serviceTask(id).name(name).camundaClass(adapterClass.getName());
    return this;
  }
  
  @SuppressWarnings("rawtypes")
  public BlockBuilder compensationActivity(String name, Class adapterClass) {
    if (!(saga instanceof AbstractActivityBuilder)) {
      throw new RuntimeException("Compensation activity can only be specified right after activity");
    }

    String id = "Activity-" + name.replace(" ", "-") + "-compensation"; // risky thing ;-)
    
    ((AbstractActivityBuilder)saga)
        .boundaryEvent()
        .compensateEventDefinition()
        .compensateEventDefinitionDone()
        .compensationStart()
            .serviceTask(id).name(name).camundaClass(adapterClass.getName())
        .compensationDone();   
    
    return this;
  }
  
  public BlockBuilder triggerCompensationOnAnyError() {
    process.eventSubProcess()
      .startEvent("ErrorCatched").error("java.lang.Throwable")
      .intermediateThrowEvent("ToBeCompensated").compensateEventDefinition().compensateEventDefinitionDone()
      .endEvent("ErrorHandled");
      
    return this;
  }
  
}
