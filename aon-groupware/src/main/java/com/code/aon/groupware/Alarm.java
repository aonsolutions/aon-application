package com.code.aon.groupware;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.config.User;
import com.code.aon.groupware.enumeration.AlarmSource;
import com.code.aon.groupware.enumeration.AlarmStatus;
import com.code.aon.groupware.enumeration.NoticeType;
import com.code.aon.groupware.enumeration.Priority;

@Entity
@Table(name="alarm")
public class Alarm implements ITransferObject {

	private static final long serialVersionUID = 5331239358786123832L;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(Alarm.class);

	private Integer id;
	
	private String description;
	
	private Date alarmDate;
	
	private AlarmStatus status;
	
	private AlarmSource source;
	
	private Integer sourceId;
	
	private ITransferObject to;
	
	private User user;
	
	private Priority priority;

	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(nullable=false)
	@Lob
	@Type(type="stringClob")	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@Column(name="alarm_date", nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getAlarmDate() {
		return alarmDate;
	}
	public void setAlarmDate(Date alarmDate) {
		this.alarmDate = alarmDate;
	}

	public AlarmStatus getStatus() {
		return status;
	}

	public void setStatus(AlarmStatus status) {
		this.status = status;
	}

	@Column(nullable=false)
	public AlarmSource getSource() {
		return source;
	}

	public void setSource(AlarmSource source) {
		this.source = source;
	}

	@Column(name="source_id")
	public Integer getSourceId() {
		return sourceId;
	}

	public void setSourceId(Integer sourceId) {
		this.sourceId = sourceId;
	}

	@ManyToOne
	@JoinColumn(name="user_id")
	@ForeignKey(name = "FK_ALARM_USER_ID")
	@Index(name = "IDX_ALARM_USER_ID")
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Column(nullable=false)
	@Enumerated
	public Priority getPriority() {
		return priority;
	}
	
	public void setPriority(Priority priority) {
		this.priority = priority;
	}
	
	@Transient
	public ITransferObject getTo() {
		return to;
	}

	public void setTo(ITransferObject to) {
		this.to = to;
	}

	@Transient
	public boolean isPending() {
        return status == AlarmStatus.PENDING;
    }
	
	@Transient
	public boolean isHighPriority() {
        return priority == Priority.HIGH;
    }
	
	@Transient
	public boolean isMediumPriority() {
        return priority == Priority.NORMAL;
    }
	
	@Transient
	public boolean isLowPriority() {
        return priority == Priority.LOW;
    }

	@Transient
    public boolean isFromTask() {
        return source == AlarmSource.TASK;
    }
    
    @Transient
    public boolean isFromNotice() {
        return source == AlarmSource.NOTICE;
    }
    
    @Transient
    public boolean isFromExternal() {
        return source == AlarmSource.EXTERNAL;
    }

    @Transient
    public Notice getNotice() {
    	if ( (source == AlarmSource.NOTICE) && (sourceId != null) ) {
    		if ( to == null ) {
    			try {
    				IManagerBean noticeBean = BeanManager.getManagerBean(Notice.class);
    				to = noticeBean.get(sourceId);
    			} catch (ManagerBeanException e) {
    				LOGGER.error( e.getMessage(), e );
    			}    			
    		}
    		if ( to != null ) {
    			return (Notice) to; 
    		}
    	}
    	return null;
    }
    
    @Transient
    public NoticeType getNoticeType() {
    	Notice notice = getNotice();
    	if ( notice != null ) {
    		return notice.getType();
    	}
    	return null;
    }
	
    @Transient
	public boolean isCall() {
        return NoticeType.CALL == getNoticeType();
    }
	
    @Transient
    public boolean isVisit() {
    	return NoticeType.VISIT == getNoticeType();
    }
    
    @Transient
    public boolean isMessage() {
    	return NoticeType.MESSAGE == getNoticeType();
    }
    
    @Transient
    public boolean isCommunication() {
    	return NoticeType.COMMUNICATION == getNoticeType();
    }    

    @Transient
    public boolean isNotice() {
    	return source == AlarmSource.NOTICE;
    }   
    
    @Transient
    public boolean isCommercialTracking() {
    	return source == AlarmSource.COMMERCIAL_TRACKING;
    }    
    
    @Transient
    public boolean isCallCenter() {
    	return source == AlarmSource.CALL_CENTER;
    }    
    
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Alarm o = (Alarm) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.alarmDate, o.alarmDate)
				.append(this.description, o.description)
				.append(this.priority, o.priority)
				.append(this.source, o.source)
				.append(this.sourceId, o.sourceId)
				.append(this.status, o.status)
				.append(this.user, o.user)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder().
			append(alarmDate).append(description).
			append(id).append(priority).
			append(source).append(sourceId).
			append(status).append(user).
			toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}