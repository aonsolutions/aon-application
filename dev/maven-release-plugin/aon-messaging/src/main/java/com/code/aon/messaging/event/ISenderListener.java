package com.code.aon.messaging.event;

public interface ISenderListener {

	void messageSent(SenderEvent event);

	void messageFailed(SenderEvent event);
}
