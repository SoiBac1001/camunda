package com.fsoft.fhn.fhs.camunda;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.runtime.ProcessInstanceQuery;
import org.camunda.bpm.engine.runtime.ProcessInstanceWithVariables;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.task.TaskQuery;
import org.camunda.bpm.engine.variable.Variables;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "test-process")
public class ProcessController {


    private ProcessEngine getDefaultProcessEngine() {
        return ProcessEngines.getDefaultProcessEngine();
    }

    @RequestMapping(value = "trigger")
    public String triggerProcess() {
        ProcessEngine processEngine = getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();

        Map<String, Object> variables = new HashMap<>();
        ProcessInstanceWithVariables instance = runtimeService.createProcessInstanceByKey("prsInputMessage")
                .setVariables(variables)
                .executeWithVariablesInReturn();
        return "OK " + instance.getId();
    }

    @RequestMapping(value = "input-message")
    public String inputMessage(@RequestParam String instanceId, @RequestParam String message, @RequestParam String messageType) {
        ProcessEngine processEngine = getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        ProcessInstanceQuery instanceQuery = runtimeService.createProcessInstanceQuery().processInstanceId(instanceId);
        ProcessInstance pi = instanceQuery.singleResult();
        if (pi == null) {
            return "Process instance not found!";
        }
        TaskService taskService = processEngine.getTaskService();
        Task runningTask = taskService.createTaskQuery().processInstanceId(pi.getId()).active().taskDefinitionKey("atvInputMessage").singleResult();
        if (runningTask == null) {
            return "Task not found!";
        }
        taskService.complete(runningTask.getId(), Variables.putValue("inputMessage", message).putValue("messageType", messageType));
        HistoricVariableInstance variableInstance = processEngine.getHistoryService().createHistoricVariableInstanceQuery().processInstanceId(instanceId)
                .variableName("inputMessage")
                .singleResult();
        if (variableInstance != null) {
            String inputMessage = (String) variableInstance.getValue();
            System.out.println("History input message: " + inputMessage);
        }
//        String message2 = (String) runtimeService.getVariable(pi.getId(), "theMessage2");
//        Task inputMessageTask = taskService.createTaskQuery().processInstanceId(instanceId).taskId("atvInputMessage").singleResult();
//        inputMessageTask.
//        runtimeService.setVariable(pi.getId(), "inputMessage", message);
//        runtimeService.setVariable(pi.getId(), "messageType", messageType);
        System.out.println("Camunda instance " + instanceId + " completed.");
        return "OK " + instanceId;
    }

    @RequestMapping("kill-process")
    public String killProcess(@RequestParam String processInstanceId) {
        try {
            ProcessEngine camunda = getDefaultProcessEngine();
            List<ProcessInstance> lstInstances = camunda.getRuntimeService().createProcessInstanceQuery().superProcessInstanceId(processInstanceId).list();
            if (lstInstances != null && lstInstances.size() > 0) {
                System.out.println("Found " + lstInstances.size() + " sub-process.");
                for (ProcessInstance pi : lstInstances) {
                    System.out.println("Deleting sub process instance id: " + pi.getProcessInstanceId());
                    camunda.getRuntimeService().deleteProcessInstance(pi.getProcessInstanceId(), null);
                }
            }
            ProcessInstance runningPI = camunda.getRuntimeService().createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
            if (runningPI != null) {
                System.out.println("Deleting main process instance id: " + processInstanceId);
                camunda.getRuntimeService().deleteProcessInstance(processInstanceId, null);
            }
            return "Kill success";
        } catch (Exception ex) {
            ex.printStackTrace();
            return ex.getMessage();
        }
    }
}
