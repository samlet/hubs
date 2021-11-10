/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH
 * under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright
 * ownership. Camunda licenses this file to you under the Apache License,
 * Version 2.0; you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bluecc.prefabs.fixtures;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static com.bluecc.prefabs.fixtures.ReaderTest.dataSource;
import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Simple process test to validate the current implementation protoype.
 */
public class TaskAssigneeTest {

    @Test
    public void testTaskAssignee() throws IOException {
        // engine
        ProcessEngine camunda =
                new StandaloneInMemProcessEngineConfiguration()
                        .buildProcessEngine();
        ReaderTest.ProcessHelper helper = new ReaderTest.ProcessHelper(camunda);

        // deploy
        // BpmnModelInstance modelInstance = Bpmn.readModelFromStream(
        //         dataSource("TaskAssigneeTest.testTaskAssignee.bpmn20.xml"));
        // TaskAssigneeTest.xml
        BpmnModelInstance modelInstance = Bpmn.readModelFromStream(
                TaskAssigneeTest.class.getResourceAsStream("/bpmn/TaskAssigneeTest.testTaskAssignee.bpmn20.xml"));
        camunda.getRepositoryService().createDeployment() //
                .addModelInstance("taskAssigneeExampleProcess.bpmn", modelInstance) //
                .deploy();

        // Start process instance
        ProcessInstance processInstance = helper.runtimeService
                .startProcessInstanceByKey("taskAssigneeExampleProcess");

        // Get task list
        List<Task> tasks = helper.taskService
                .createTaskQuery()
                .taskAssignee("kermit")
                .list();
        assertEquals(1, tasks.size());
        Task myTask = tasks.get(0);
        assertEquals("Schedule meeting", myTask.getName());
        assertEquals("Schedule an engineering meeting for next week with the new hire.", myTask.getDescription());

        // Complete task. Process is now finished
        helper.taskService.complete(myTask.getId());
        // assert if the process instance completed
        helper.assertProcessEnded(processInstance.getId());
    }

}
