package com.bluecc.prefabs.fixtures;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.GatewayDirection;
import org.camunda.bpm.model.bpmn.builder.ProcessBuilder;

import java.io.File;

public class ProcessFlow {
    public static void main(String[] args) {
        BpmnModelInstance modelInstance;
        ProcessBuilder flow = Bpmn.createExecutableProcess("invoice");
        modelInstance=flow
                .executable()
                .startEvent()
                .name("Invoice received")
                .camundaFormKey("embedded:app:forms/start-form.html")
                .userTask()
                .name("Assign Approver")
                .camundaFormKey("embedded:app:forms/assign-approver.html")
                .camundaAssignee("demo")
                .userTask("approveInvoice")
                .name("Approve Invoice")
                .camundaFormKey("embedded:app:forms/approve-invoice.html")
                .camundaAssignee("${approver}")
                .exclusiveGateway()
                .name("Invoice approved?")
                .gatewayDirection(GatewayDirection.Diverging)
                .condition("yes", "${approved}")
                    .userTask()
                    .name("Prepare Bank Transfer")
                    .camundaFormKey("embedded:app:forms/prepare-bank-transfer.html")
                    .camundaCandidateGroups("accounting")
                    .serviceTask()
                    .name("Archive Invoice")
                    .camundaClass("org.camunda.bpm.example.invoice.service.ArchiveInvoiceService")
                    .endEvent()
                    .name("Invoice processed")
                    .moveToLastGateway()
                .condition("no", "${!approved}")
                    .userTask()
                    .name("Review Invoice")
                    .camundaFormKey("embedded:app:forms/review-invoice.html")
                    .camundaAssignee("demo")
                    .exclusiveGateway()
                    .name("Review successful?")
                    .gatewayDirection(GatewayDirection.Diverging)
                        .condition("no", "${!clarified}")
                            .endEvent()
                            .name("Invoice not processed")
                            .moveToLastGateway()
                        .condition("yes", "${clarified}")
                            .connectTo("approveInvoice")
                .done();
        Bpmn.writeModelToFile(new File("invoice.bpmn"), modelInstance);
    }
}
