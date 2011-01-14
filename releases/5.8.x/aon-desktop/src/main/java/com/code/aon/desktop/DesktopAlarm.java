package com.code.aon.desktop;

import java.util.Date;
import java.util.Iterator;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.groupware.Notice;
import com.code.aon.groupware.dao.IGroupWareAlias;
import com.code.aon.groupware.enumeration.AlarmSource;
import com.code.aon.groupware.enumeration.NoticeType;
import com.code.aon.groupware.enumeration.Priority;
import com.code.aon.ql.Criteria;

public class DesktopAlarm {

    private Integer id;
    private String description;
    private Date alarmDate;
    private AlarmSource source;
    private Integer sourceId;
    private NoticeType noticeType;
    private Priority priority;

    @SuppressWarnings("unchecked")
	public DesktopAlarm(Integer id, String description, Date alarmDate, AlarmSource source, Integer sourceId, Priority priority) {
        this.id = id;
        this.description = description;
        this.alarmDate = alarmDate;
        this.source = source;
        this.sourceId = sourceId;
        this.priority = priority;
        this.noticeType = null;
        if (sourceId != null && source.equals(AlarmSource.NOTICE)) {
        	//Si se trata de un aviso buscamos su tipo
			try {
				IManagerBean noticeBean = BeanManager.getManagerBean(Notice.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(noticeBean.getFieldName(IGroupWareAlias.NOTICE_ID), sourceId);
				Iterator iter = noticeBean.getList(criteria).iterator();
				if(iter.hasNext()) this.noticeType = ((Notice)iter.next()).getType();
			} catch (ManagerBeanException e) {
				e.printStackTrace();
			}
        }
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

	public AlarmSource getSource() {
		return source;
	}

	public void setSource(AlarmSource source) {
		this.source = source;
	}

	public Integer getSourceId() {
		return sourceId;
	}

	public void setSourceId(Integer sourceId) {
		this.sourceId = sourceId;
	}

	public boolean isCall() {
		if (noticeType == null) return false;
        return noticeType.equals(NoticeType.CALL);
    }
    public boolean isVisit() {
		if (noticeType == null) return false;
        return noticeType.equals(NoticeType.VISIT);
    }
    public boolean isMessage() {
		if (noticeType == null) return false;
        return noticeType.equals(NoticeType.MESSAGE);
    }
    public boolean isCommunication() {
		if (noticeType == null) return false;
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

    public boolean isFromTask() {
        return source.equals(AlarmSource.TASK);
    }
    public boolean isFromNotice() {
        return source.equals(AlarmSource.NOTICE);
    }
    public boolean isFromExternal() {
        return source.equals(AlarmSource.EXTERNAL);
    }

}
