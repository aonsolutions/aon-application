package com.code.aon.ui.groupware.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.User;
import com.code.aon.groupware.Alarm;
import com.code.aon.groupware.Notice;
import com.code.aon.groupware.dao.IGroupWareAlias;
import com.code.aon.groupware.enumeration.AlarmSource;
import com.code.aon.groupware.enumeration.AlarmStatus;
import com.code.aon.groupware.enumeration.DelayTime;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.event.ControllerListenerException;

public class AlarmController extends BasicController {
	
	private static final Logger LOGGER = Logger.getLogger(AlarmController.class.getName());
	
	private DelayTime delayTime;
	
	public DelayTime getDelayTime() {
		return delayTime;
	}

	public void setDelayTime(DelayTime delayTime) {
		this.delayTime = delayTime;
	}

	@Override
	public void onSelect(ActionEvent event) {
		setDelayTime(null);
		super.onSelect(event);
	}
	
	@SuppressWarnings("unused")
	public void obtainInboxModel(ActionEvent event) throws ManagerBeanException{
		try {
			Criteria criteria = getCriteria();
			criteria.addEqualExpression(getFieldName(IGroupWareAlias.ALARM_STATUS), AlarmStatus.PENDING);
			criteria.addLessThanOrEqualExpression(getFieldName(IGroupWareAlias.ALARM_ALARM_DATE), new Date());
			User user = UserUtils.getInstance().getLoggedUser();
			criteria.addEqualExpression(getFieldName(IGroupWareAlias.ALARM_USER_ID), user.getId());
			onSearch(null);
		} catch (ManagerBeanException e) {
			throw new ManagerBeanException("Error obtaining inboxModel", e);
		}
	}
	
	@SuppressWarnings("unused")
	public void onDelayAlarm(ActionEvent event) throws ControllerListenerException {
		Alarm currentAlarm = (Alarm)this.getTo();
		if(this.getDelayTime() != null){
			try {
				IManagerBean alarmBean = BeanManager.getManagerBean(Alarm.class);
				Alarm alarm = new Alarm();
				alarm.setAlarmDate(obtainNewDate());
				alarm.setDescription(currentAlarm.getDescription());
				alarm.setUser(currentAlarm.getUser());
				alarm.setPriority(currentAlarm.getPriority());
				alarm.setSource(currentAlarm.getSource());
				alarm.setSourceId(currentAlarm.getSourceId());
				alarm.setStatus(AlarmStatus.PENDING);
				currentAlarm.setStatus(AlarmStatus.FINISHED);
				currentAlarm.setUser(UserUtils.getInstance().getLoggedUser());
				alarmBean.update(currentAlarm);
				this.setTo(alarmBean.insert(alarm));
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, "Error updating alarm with id=" + currentAlarm.getId(), e);
			}			
		}
		try {
			this.obtainInboxModel(null);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining inboxModel after delaying alarm", e);
		}
	}
	
	private Date obtainNewDate() {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());
		calendar.add(Calendar.MINUTE, this.getDelayTime().getValue());
		return calendar.getTime();
	}

	@SuppressWarnings("unused")
	public void onFinishAlarm(ActionEvent event){
		Alarm alarm = (Alarm)this.getTo();
		try {
			IManagerBean alarmBean = BeanManager.getManagerBean(Alarm.class);
			alarm.setStatus(AlarmStatus.FINISHED);
			alarm.setUser(UserUtils.getInstance().getLoggedUser());
			alarmBean.update(alarm);
			this.obtainInboxModel(null);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error updating alarm with id=" + alarm.getId(), e);
		}
	}
	
	public String getSender() {
		Alarm alarm = (Alarm)this.getTo();
		if ( alarm.getSource() == AlarmSource.NOTICE ) {
			try {			
				IManagerBean noticeBean = BeanManager.getManagerBean(Notice.class);
				Notice notice = (Notice) noticeBean.get(alarm.getSourceId());
				if ( notice != null ) {
					return notice.getSender().getName();
				}
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, "Error updating alarm with id=" + alarm.getId(), e);
			}			
		}
		return null;
	}
}