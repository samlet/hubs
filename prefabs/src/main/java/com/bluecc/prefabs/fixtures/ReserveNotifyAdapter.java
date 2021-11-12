package com.bluecc.prefabs.fixtures;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class ReserveNotifyAdapter extends NotifyAdapterBase implements JavaDelegate {

    @Override
    public void execute(DelegateExecution ctx) throws Exception {
        send("reserve car for '" + ctx.getVariable("name") + "'");
    }

}
