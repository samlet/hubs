package com.bluecc.prefabs.fixtures;

import com.google.common.io.Resources;
import org.camunda.bpm.engine.ManagementService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration;
import org.camunda.bpm.engine.impl.event.EventType;
import org.camunda.bpm.engine.impl.util.ClockUtil;
import org.camunda.bpm.engine.runtime.EventSubscription;
import org.camunda.bpm.engine.runtime.Job;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class ReaderTest {

    // protected ProvidedProcessEngineRule engineRule = new ProvidedProcessEngineRule();
    // protected ProcessEngineTestRule testRule = new ProcessEngineTestRule(engineRule);

    public static InputStream dataSource(String src) throws IOException {
        return Resources.getResource("bpmn/" + src).openStream();
    }

    public static class ProcessHelper{
        public RuntimeService runtimeService;
        public ProcessEngine processEngine;
        ManagementService managementService;
        public TaskService taskService;

        public ProcessHelper(ProcessEngine camunda){
            this.processEngine=camunda;
            this.runtimeService=camunda.getRuntimeService();
            managementService = camunda.getManagementService();
            taskService = processEngine.getTaskService();
        }

        private List<EventSubscription> getEventSubscriptionList() {
            return runtimeService.createEventSubscriptionQuery()
                    .eventType(EventType.MESSAGE.name()).list();
        }

        public void receiveOne(){
            EventSubscription subscription = getEventSubscriptionList().get(0);
            System.out.format("subscription: %s - %s\n", subscription.getEventName(),
                    subscription.getExecutionId());
            // then: we can trigger the event subscription
            runtimeService.messageEventReceived(subscription.getEventName(),
                    subscription.getExecutionId());
        }

        public void assertProcessEnded(String processInstanceId) {
            ProcessInstance processInstance = processEngine
                    .getRuntimeService()
                    .createProcessInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .singleResult();

            assertThat(processInstance).describedAs("Process instance with id " + processInstanceId + " is not finished").isNull();
        }

        public boolean areJobsAvailable() {
            List<Job> list = managementService.createJobQuery().list();
            for (Job job : list) {
                if (!job.isSuspended() && job.getRetries() > 0 && (job.getDuedate() == null || ClockUtil.getCurrentTime().after(job.getDuedate()))) {
                    return true;
                }
            }
            return false;
        }
    }

    @Test
    void reader() throws IOException {
        // Configure and startup (in memory) engine
        ProcessEngine camunda =
                new StandaloneInMemProcessEngineConfiguration()
                        .buildProcessEngine();
        ProcessHelper helper=new ProcessHelper(camunda);

        deployTests(camunda);

        ProcessInstance processInstance = camunda.getRuntimeService().startProcessInstanceByKey(
                "testProcess", Variables.putValue("name", "test"));
        List<EventSubscription> subscriptionList = helper.getEventSubscriptionList();

        assertEquals(1, subscriptionList.size());
        EventSubscription subscription = subscriptionList.get(0);

        helper.receiveOne();
        // expect: subscription is removed
        assertEquals(0, helper.getEventSubscriptionList().size());

        System.out.println(processInstance.getId());
        helper.assertProcessEnded(processInstance.getId());

        // System.out.println("suspend: "+processInstance.isSuspended());
        // System.out.println("done: "+processInstance.isEnded());
    }

    public static void deployTests(ProcessEngine camunda) throws IOException {
        BpmnModelInstance modelInstance = Bpmn.readModelFromStream(
                dataSource("ReceiveTaskTest.singleReceiveTask.bpmn20.xml"));
        camunda.getRepositoryService().createDeployment() //
                .addModelInstance("testProcess.bpmn", modelInstance) //
                .deploy();

        modelInstance = Bpmn.readModelFromStream(
                dataSource("SendTaskTest.testJavaDelegate.bpmn20.xml"));
        camunda.getRepositoryService().createDeployment() //
                .addModelInstance("sendTaskJavaDelegate.bpmn", modelInstance) //
                .deploy();
    }

    @Test
    void correlateMessage_single_2_ProcessInstance() throws IOException {
        ProcessEngine camunda =
                new StandaloneInMemProcessEngineConfiguration()
                        .buildProcessEngine();
        ProcessHelper helper=new ProcessHelper(camunda);
        deployTests(camunda);

        // given: a process instance with business key 23 waiting in the receive task
        ProcessInstance processInstance23 = helper.runtimeService.startProcessInstanceByKey("testProcess", "23");

        // given: a 2nd process instance with business key 42 waiting in the receive task
        ProcessInstance processInstance42 = helper.runtimeService.startProcessInstanceByKey("testProcess", "42");

        // expect: there is two message event subscriptions for the tasks
        List<EventSubscription> subscriptionList = helper.getEventSubscriptionList();
        assertEquals(2, subscriptionList.size());

        //+
        // ManagementService managementService = camunda.getManagementService();
        System.out.println("areJobsAvailable: "+helper.areJobsAvailable());
        //+

        // then: we can correlate the event subscription to one of the process instances
        helper.runtimeService.correlateMessage("newInvoiceMessage", "23");

        // expect: one subscription is removed
        assertEquals(1, helper.getEventSubscriptionList().size());

        // expect: this ends the process instance with business key 23
        helper.assertProcessEnded(processInstance23.getId());

        // expect: other process instance is still running
        assertEquals(1, helper.runtimeService.createProcessInstanceQuery().processInstanceId(processInstance42.getId()).count());

        // then: we can correlate the event subscription to the other process instance
        helper.runtimeService.correlateMessage("newInvoiceMessage", "42");

        // expect: subscription is removed
        assertEquals(0, helper.getEventSubscriptionList().size());

        // expect: this ends the process instance
        helper.assertProcessEnded(processInstance42.getId());
    }
}
