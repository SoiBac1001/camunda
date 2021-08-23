package com.fsoft.fhn.fhs.camunda;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class DefaultFlowListener implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String activityId = execution.getCurrentActivityId();
        String transitionId = execution.getCurrentTransitionId();
        System.out.println("Executed flow " + transitionId + ", activityId: " + activityId);
    }
}
