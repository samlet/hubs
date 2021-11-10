package com.bluecc.prefabs.fixtures;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.xml.ModelReferenceException;
import org.camunda.bpm.model.xml.ModelValidationException;

import java.io.File;

public class MessageFlow {
    public static void main(String[] args) {
        new MessageFlow().shouldAddMessageAndMessageEventDefinition();
    }

    public void shouldAddMessageAndMessageEventDefinition() {
        // create empty model
        BpmnModelInstance bpmnModelInstance = Bpmn.createEmptyModel();

        // add definitions to model
        Definitions definitions = bpmnModelInstance.newInstance(Definitions.class);
        definitions.setTargetNamespace("Examples");
        bpmnModelInstance.setDefinitions(definitions);

        // create and add message
        Message message = bpmnModelInstance.newInstance(Message.class);
        message.setId("start-message-id");
        definitions.getRootElements().add(message);

        // create and add message event definition
        MessageEventDefinition messageEventDefinition = bpmnModelInstance.newInstance(MessageEventDefinition.class);
        messageEventDefinition.setId("message-event-def-id");
        messageEventDefinition.setMessage(message);
        definitions.getRootElements().add(messageEventDefinition);

        // test if message was set correctly
        Message setMessage = messageEventDefinition.getMessage();
        // assertThat(setMessage).isEqualTo(message);

        // add process
        Process process = bpmnModelInstance.newInstance(Process.class);
        process.setId("messageEventDefinition");
        definitions.getRootElements().add(process);

        // add start event
        StartEvent startEvent = bpmnModelInstance.newInstance(StartEvent.class);
        startEvent.setId("theStart");
        process.getFlowElements().add(startEvent);

        // create and add message event definition to start event
        MessageEventDefinition startEventMessageEventDefinition = bpmnModelInstance.newInstance(MessageEventDefinition.class);
        startEventMessageEventDefinition.setMessage(message);
        startEvent.getEventDefinitions().add(startEventMessageEventDefinition);

        // create another message but do not add it
        Message anotherMessage = bpmnModelInstance.newInstance(Message.class);
        anotherMessage.setId("another-message-id");

        // create a message event definition and try to add last create message
        MessageEventDefinition anotherMessageEventDefinition = bpmnModelInstance.newInstance(MessageEventDefinition.class);
        try {
            anotherMessageEventDefinition.setMessage(anotherMessage);
            System.out.println("Message should not be added to message event definition, cause it is not part of the model");
            return;
        }
        catch(Exception e) {
            // assertThat(e).isInstanceOf(ModelReferenceException.class);
        }

        // first add message to model than to event definition
        definitions.getRootElements().add(anotherMessage);
        anotherMessageEventDefinition.setMessage(anotherMessage);
        startEvent.getEventDefinitions().add(anotherMessageEventDefinition);

        // message event definition and add message by id to it
        anotherMessageEventDefinition = bpmnModelInstance.newInstance(MessageEventDefinition.class);
        startEvent.getEventDefinitions().add(anotherMessageEventDefinition);

        // validate model
        try {
            Bpmn.validateModel(bpmnModelInstance);
            Bpmn.writeModelToFile(new File("message.bpmn"), bpmnModelInstance);
        }
        catch (ModelValidationException e) {
            e.printStackTrace();
        }
    }
}
