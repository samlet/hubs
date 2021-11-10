package com.bluecc.prefabs.fixtures;

import org.camunda.bpm.model.dmn.DmnModelInstance;
import org.camunda.bpm.model.dmn.instance.DmnElement;
import org.camunda.bpm.model.dmn.instance.DmnModelElementInstance;

public class DmnModelHelper {
    DmnModelInstance modelInstance;
    public DmnModelHelper(DmnModelInstance modelInstance){
        this.modelInstance=modelInstance;
    }

    public <E extends DmnModelElementInstance> E generateElement(Class<E> elementClass, Integer suffix) {
        E element = modelInstance.newInstance(elementClass);
        if (element instanceof DmnElement) {
            String identifier = elementClass.getSimpleName();
            if (suffix != null) {
                identifier += suffix.toString();
            }
            identifier = Character.toLowerCase(identifier.charAt(0)) + identifier.substring(1);
            ((DmnElement) element).setId(identifier);
        }
        return element;
    }
}
