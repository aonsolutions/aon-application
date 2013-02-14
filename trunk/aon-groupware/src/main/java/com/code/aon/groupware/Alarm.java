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
import com.esferalia.aon.entity.master.AlarmDB;

@Entity
@Table(name="alarm")
public class Alarm extends AlarmDB {

	private static final long serialVersionUID = 1L;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(Alarm.class);

	private ITransferObject to;
	
	@Transient
	public ITransferObject getTo() {
		return to;
	}
	public void setTo(ITransferObject to) {
		this.to = to;
	}

	@Transient
	public boolean isPending() {
        return getStatus() == AlarmStatus.PENDING;
    }
	@Transient
	public boolean isHighPriority() {
        return getPriority() == Priority.HIGH;
    }
	@Transient
	public boolean isMediumPriority() {
        return getPriority() == Priority.NORMAL;
    }
	@Transient
	public boolean isLowPriority() {
        return getPriority() == Priority.LOW;
    }
	@Transient
    public boolean isFromTask() {
        return getSource() == AlarmSource.TASK;
    }
    @Transient
    public boolean isFromNotice() {
        return getSource() == AlarmSource.NOTICE;
    }
    @Transient
    public boolean isFromExternal() {
        return getSource() == AlarmSource.EXTERNAL;
    }

    @Transient
    public Notice getNotice() {
    	if ( (getSource() == AlarmSource.NOTICE) && (getSourceId() != null) ) {
    		if ( to == null ) {
    			try {
    				IManagerBean noticeBean = BeanManager.getManagerBean(Notice.class);
    				to = noticeBean.get(getSourceId());
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
    	return getSource() == AlarmSource.NOTICE;
    }   
    @Transient
    public boolean isCommercialTracking() {
    	return getSource() == AlarmSource.COMMERCIAL_TRACKING;
    }    
    @Transient
    public boolean isCallCenter() {
    	return getSource() == AlarmSource.CALL_CENTER;
    }    

}