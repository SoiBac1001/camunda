package com.fsoft.fhn.fhs.camunda;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.task.TaskQuery;
import org.camunda.bpm.engine.variable.VariableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Component(value = "FlwGenFirstRunDelegate")
public class FlwGenFirstRunDelegate implements JavaDelegate {
    private ProcessEngine processEngine;

    @Autowired
    public void setProcessEngine(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }
    private static final SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Calendar currentTime = Calendar.getInstance();
        TaskQuery taskQuery = processEngine.getTaskService().createTaskQuery().processDefinitionKey("prsInputMessage").active()
                .taskDefinitionKey("atvInputMessage");
        List<Task> currentBatch = null;
        int firstOfBatch = 0;
        // Paging list of Submitted Member BS for performance.
        do {
            currentBatch = taskQuery.listPage(firstOfBatch, 50);
            for (Task task : currentBatch) {
                Map<String, Object> variables = new HashMap<>();
                variables.put("messageType", "Not ok message type");
                variables.put("inputMessage", "The input message");
                processEngine.getTaskService().complete(task.getId(), variables);
            }
            firstOfBatch += currentBatch.size();
        } while (!CollectionUtils.isEmpty(currentBatch) && currentBatch.size() >= 50);
        System.out.println(String.format("Auto completed %s submitted member bs to Underwriting.", firstOfBatch + 1));


        currentTime.add(Calendar.MINUTE, 2);
        Date nextRun = currentTime.getTime();
        System.out.println("Next run: " + sdf.format(nextRun));
        execution.setVariable("nextRun", nextRun);
    }
}
