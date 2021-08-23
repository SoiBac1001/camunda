package com.fsoft.fhn.fhs.camunda;

import com.fasterxml.jackson.annotation.JsonValue;

public enum MessageType {
    OK("OK", 1), ERROR("ERROR", 2);
    private String value;
    private int index;

    MessageType(String value, int index) {
        this.value = value;
        this.index = index;
    }

    public static MessageType findByString(String s) {
        for (MessageType type : MessageType.values()) {
            if (type.getValue().equalsIgnoreCase(s)) return type;
        }
        return null;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public int getIndex() {
        return index;
    }

}
