package com.bluecc.prefabs.fixtures;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.builder.ProcessBuilder;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnEdge;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnShape;
import org.camunda.bpm.model.bpmn.instance.dc.Bounds;
import org.camunda.bpm.model.bpmn.instance.di.Waypoint;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Iterator;

import static com.bluecc.prefabs.fixtures.BpmnTestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;

public class CoordinatesGenerationTest {

    private BpmnModelInstance instance;

    protected BpmnShape findBpmnShape(String id) {
        Collection<BpmnShape> allShapes = instance.getModelElementsByType(BpmnShape.class);

        Iterator<BpmnShape> iterator = allShapes.iterator();
        while (iterator.hasNext()) {
            BpmnShape shape = iterator.next();
            if (shape.getBpmnElement().getId().equals(id)) {
                return shape;
            }
        }
        return null;
    }

    protected BpmnEdge findBpmnEdge(String sequenceFlowId){
        Collection<BpmnEdge> allEdges = instance.getModelElementsByType(BpmnEdge.class);
        Iterator<BpmnEdge> iterator = allEdges.iterator();

        while (iterator.hasNext()) {
            BpmnEdge edge = iterator.next();
            if(edge.getBpmnElement().getId().equals(sequenceFlowId)) {
                return edge;
            }
        }
        return null;
    }

    protected void assertShapeCoordinates(Bounds bounds, double x, double y){
        assertThat(bounds.getX()).isEqualTo(x);
        assertThat(bounds.getY()).isEqualTo(y);
    }

    protected void assertWaypointCoordinates(Waypoint waypoint, double x, double y){
        assertThat(x).isEqualTo(waypoint.getX());
        assertThat(y).isEqualTo(waypoint.getY());
    }

    @Test
    public void shouldPlaceStartEvent() {

        ProcessBuilder builder = Bpmn.createExecutableProcess();

        instance = builder
                .startEvent(START_EVENT_ID)
                .done();

        Bounds startBounds = findBpmnShape(START_EVENT_ID).getBounds();
        assertShapeCoordinates(startBounds, 100, 100);
    }


    @Test
    public void shouldPlaceUserTask() {

        ProcessBuilder builder = Bpmn.createExecutableProcess();

        instance = builder
                .startEvent(START_EVENT_ID)
                .sequenceFlowId(SEQUENCE_FLOW_ID)
                .userTask(USER_TASK_ID)
                .done();

        Bounds userTaskBounds = findBpmnShape(USER_TASK_ID).getBounds();
        assertShapeCoordinates(userTaskBounds, 186, 78);

        Collection<Waypoint> sequenceFlowWaypoints = findBpmnEdge(SEQUENCE_FLOW_ID).getWaypoints();
        Iterator<Waypoint> iterator = sequenceFlowWaypoints.iterator();

        Waypoint waypoint = iterator.next();
        assertWaypointCoordinates(waypoint, 136, 118);

        while(iterator.hasNext()){
            waypoint = iterator.next();
        }

        assertWaypointCoordinates(waypoint, 186, 118);

    }

    @Test
    public void shouldPlaceSendTask() {

        ProcessBuilder builder = Bpmn.createExecutableProcess();

        instance = builder
                .startEvent(START_EVENT_ID)
                .sequenceFlowId(SEQUENCE_FLOW_ID)
                .sendTask(SEND_TASK_ID)
                .done();

        Bounds sendTaskBounds = findBpmnShape(SEND_TASK_ID).getBounds();
        assertShapeCoordinates(sendTaskBounds, 186, 78);

        Collection<Waypoint> sequenceFlowWaypoints = findBpmnEdge(SEQUENCE_FLOW_ID).getWaypoints();
        Iterator<Waypoint> iterator = sequenceFlowWaypoints.iterator();

        Waypoint waypoint = iterator.next();
        assertWaypointCoordinates(waypoint, 136, 118);

        while(iterator.hasNext()){
            waypoint = iterator.next();
        }

        assertWaypointCoordinates(waypoint, 186, 118);

    }
}
