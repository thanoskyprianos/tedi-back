package com.network.network.messages;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MessageRepr {
    private String message;
    private int recipient;
}
