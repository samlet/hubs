package com.bluecc.prefabs.builder;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration;

public abstract class BuilderBase implements IBuilder{

    // Configure and startup (in memory) engine
    public ProcessEngine engine() {
        return new StandaloneInMemProcessEngineConfiguration()
                .buildProcessEngine();
    }

}
