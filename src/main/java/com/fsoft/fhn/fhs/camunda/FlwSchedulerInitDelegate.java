package com.fsoft.fhn.fhs.camunda;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class FlwSchedulerInitDelegate implements JavaDelegate {
    private static SimpleDateFormat sdf= new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long initDelay = (Long) execution.getVariable("delayTimeMinutes");
        if (initDelay == null || initDelay <= 0) {
            initDelay = 2L;
        }
        System.out.println("Loaded delay: " + initDelay + " minutes");
        LocalDateTime triggerDate = LocalDateTime.now();
        triggerDate = triggerDate.plusMinutes(initDelay);
        LocalDateTime endDate = triggerDate.plusMinutes(2L);
        Date dTriggerDate = Date.from(triggerDate.atZone(ZoneId.systemDefault()).toInstant());
        Date dEndDate = Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant());
        System.out.println("Trigger time: " + sdf.format(dTriggerDate));
        System.out.println("End time: " + sdf.format(dEndDate));
        execution.setVariable("triggerTime", dTriggerDate);
        execution.setVariable("endDate", dEndDate);
    }
}
