package com.code.aon.desktop;

import java.util.Date;

import com.code.aon.groupware.enumeration.NoticeType;
import com.code.aon.groupware.enumeration.Priority;

public class DesktopAlarm {

    private Integer id;
    private String description;
    private Date alarmDate;
    private NoticeType noticeType;
    private Priority priority;

    public DesktopAlarm(Integer id, String description, Date alarmDate, NoticeType noticeType, Priority priority) {
        this.id = id;
        this.description = description;
        this.alarmDate = alarmDate;
        this.noticeType = noticeType;
        this.priority = priority;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    
	public String getDescriptionHead(){
		if(getDescription() != null && getDescription().length() >= 64){
			return getDescription().substring(0, 63);
		}
		return getDescription();
	}

    public Date getAlarmDate() {
        return alarmDate;
    }
    public void setAlarmDate(Date alarmDate) {
        this.alarmDate = alarmDate;
    }

    public NoticeType getNoticeType() {
        return noticeType;
    }
    public void setNoticeType(NoticeType noticeType) {
        this.noticeType = noticeType;
    }

	public Priority getPriority() {
		return priority;
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	public boolean isCall() {
        return noticeType.equals(NoticeType.CALL);
    }
    public boolean isVisit() {
        return noticeType.equals(NoticeType.VISIT);
    }
    public boolean isMessage() {
        return noticeType.equals(NoticeType.MESSAGE);
    }
    public boolean isCommunication() {
        return noticeType.equals(NoticeType.COMMUNICATION);
    }
    
	public boolean isHighPrty() {
        return priority.equals(Priority.HIGH);
    }
	
	public boolean isMediumPrty() {
        return priority.equals(Priority.NORMAL);
    }
	
	public boolean isLowPrty() {
        return priority.equals(Priority.LOW);
    }
}
