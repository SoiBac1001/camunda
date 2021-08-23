package com.fsoft.fhn.fhs.camunda;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class FlwSubmitDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String inputMessage = (String) execution.getVariable("inputMessage");
        String messageType = (String) execution.getVariable("messageType");
        System.out.println("The message: " + inputMessage + ", messageType: " + messageType);
        MessageType mappedMessageType = MessageType.findByString(messageType);
        execution.setVariable("mappedMessageType", mappedMessageType);
    }
}
