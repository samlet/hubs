package com.bluecc.prefabs.demo.camunda.springboot;

import com.bluecc.prefabs.demo.camunda.adapter.*;
import com.bluecc.prefabs.demo.camunda.springboot.builder.SagaBuilder;
import org.camunda.bpm.engine.ProcessEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;

@Component
//@Singleton
public class TripBookingSaga {

  @Autowired
  private ProcessEngine camunda;

  @PostConstruct
  public void defineSaga() {
    SagaBuilder saga = SagaBuilder.newSaga("trip") //
        .activity("Reserve car", ReserveCarAdapter.class) //
        .compensationActivity("Cancel car", CancelCarAdapter.class) //
        .activity("Book hotel", BookHotelAdapter.class) //
        .compensationActivity("Cancel hotel", CancelHotelAdapter.class) //
        .activity("Book flight", BookFlightAdapter.class) //
        .compensationActivity("Cancel flight", CancelFlightAdapter.class) //
        .end() //
        .triggerCompensationOnAnyError();

    camunda.getRepositoryService().createDeployment() //
        .addModelInstance("trip.bpmn", saga.getModel()) //
        .deploy();

//    File file = new File("result.bpmn");
//    Bpmn.writeModelToFile(file, saga.getModel());
  }
  
  public void bookTrip() {
    HashMap<String, Object> someVariables = new HashMap<>();
    // Could add some variables here - not used in simple demo
    camunda.getRuntimeService().startProcessInstanceByKey("trip", someVariables);
  }

}
