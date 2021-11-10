package com.bluecc.prefabs.fixtures;

import org.camunda.bpm.engine.runtime.EventSubscription;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class ReceiveTest {
    /*
    @Deployment(resources = "org/camunda/bpm/engine/test/bpmn/receivetask/ReceiveTaskTest.singleReceiveTask.bpmn20.xml")
    @Test
    public void testSupportsMessageEventReceivedOnSingleReceiveTask() {

        // given: a process instance waiting in the receive task
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("testProcess");

        // expect: there is a message event subscription for the task
        List<EventSubscription> subscriptionList = getEventSubscriptionList();
        assertEquals(1, subscriptionList.size());
        EventSubscription subscription = subscriptionList.get(0);

        // then: we can trigger the event subscription
        runtimeService.messageEventReceived(subscription.getEventName(), subscription.getExecutionId());

        // expect: subscription is removed
        assertEquals(0, getEventSubscriptionList().size());

        // expect: this ends the process instance
        testRule.assertProcessEnded(processInstance.getId());
    }

     */
}
