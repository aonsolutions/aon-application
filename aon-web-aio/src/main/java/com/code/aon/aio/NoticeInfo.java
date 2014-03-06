package com.code.aon.aio;

import java.io.Serializable;

import com.code.aon.common.AonVersion;
import com.code.aon.groupware.enumeration.NoticeType;

public class NoticeInfo implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

    private int callCount;
    private int visitCount;
    private int messageCount;
    private int communicationCount;
    

    public void add(NoticeType noticeType, int count) {
    	switch ( noticeType ) {
    		case CALL:
    			setCallCount(count);
    			break;
    		case VISIT:
    			setVisitCount(count);
    			break;
    		case MESSAGE:
    			setMessageCount(count);
    			break;
    		case COMMUNICATION:
    			setCommunicationCount(count);
    			break;
    	}
    }


	public int getCallCount() {
		return callCount;
	}
	public void setCallCount(int callCount) {
		this.callCount = callCount;
	}


	public int getVisitCount() {
		return visitCount;
	}
	public void setVisitCount(int visitCount) {
		this.visitCount = visitCount;
	}


	public int getMessageCount() {
		return messageCount;
	}
	public void setMessageCount(int messageCount) {
		this.messageCount = messageCount;
	}


	public int getCommunicationCount() {
		return communicationCount;
	}
	public void setCommunicationCount(int communicationCount) {
		this.communicationCount = communicationCount;
	}
	
	public NoticeType getCallType() {
		return NoticeType.CALL;
	}
	public NoticeType getVisitType() {
		return NoticeType.VISIT;
	}
	public NoticeType getMessageType() {
		return NoticeType.MESSAGE;
	}
	public NoticeType getCommunicationType() {
		return NoticeType.COMMUNICATION;
	}
	
}
