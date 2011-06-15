package com.code.aon.ui.groupware.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.ListDataModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.User;
import com.code.aon.groupware.Alarm;
import com.code.aon.groupware.dao.IGroupWareAlias;
import com.code.aon.groupware.enumeration.AlarmStatus;
import com.code.aon.groupware.enumeration.DelayTime;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.event.ControllerListenerException;

public class AlarmController extends BasicController {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(AlarmController.class);
	
    private ListDataModel todayModel;

    private ListDataModel recentModel;
    
    private ListDataModel ancientModel;
	
	private DelayTime delayTime;

	private User user;
	
	public AlarmController() {
		this.user = UserUtils.getInstance().getLoggedUser();
	}

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
	
	public void onDelayAlarm(ActionEvent event) throws ControllerListenerException {
		if (getDelayTime() != null) {
			Alarm alarm = (Alarm)this.getTo();			
			try {
				alarm.setAlarmDate(obtainNewDate());
				alarm.setUser(user);
				alarm.setStatus(AlarmStatus.READ);
				getManagerBean().update(alarm);
			} catch (ManagerBeanException e) {
				LOGGER.error("Error updating alarm with id=" + alarm.getId(), e);
			}			
		}
	}
	
	private Date obtainNewDate() {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());
		calendar.add(Calendar.MINUTE, this.getDelayTime().getValue());
		return calendar.getTime();
	}

	public void onFinishAlarm(ActionEvent event){
		Alarm alarm = (Alarm)this.getTo();
		try {
			alarm.setStatus(AlarmStatus.FINISHED);
			alarm.setUser(user);
			getManagerBean().update(alarm);
		} catch (ManagerBeanException e) {
			LOGGER.error("Error updating alarm with id=" + alarm.getId(), e);
		}
	}
	
	public String getSender() {
		Alarm alarm = (Alarm)this.getTo();
		if ( alarm.getNotice() != null ) {
			return alarm.getNotice().getSender().getName();
		}
		return null;
	}
	
    private List<ITransferObject> getAlarmList(Date from, Date to) throws ManagerBeanException {
    	IManagerBean bean = BeanManager.getManagerBean(Alarm.class);
    	Criteria criteria = new Criteria();
    	String statusAlias = bean.getFieldName(IGroupWareAlias.ALARM_STATUS);
    	Expression expr1 = ExpressionUtilities.getEqualExpression(statusAlias, AlarmStatus.PENDING);
    	Expression expr2 = ExpressionUtilities.getEqualExpression(statusAlias, AlarmStatus.READ);
    	criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));
    	String userAlias = bean.getFieldName(IGroupWareAlias.ALARM_USER_ID);
    	criteria.addEqualExpression(userAlias, UserUtils.getInstance().getLoggedUser().getId());
    	String dateAlias = bean.getFieldName(IGroupWareAlias.ALARM_ALARM_DATE);
    	if ( from != null ) {
        	criteria.addGreaterThanOrEqualExpression(dateAlias, from);	
    	}
    	criteria.addLessThanOrEqualExpression(dateAlias, to);
    	criteria.addOrder(dateAlias, false);
    	return bean.getList(criteria);
    }	
    
    public void updateModels() throws ManagerBeanException {
    	updateTodaymModel();
    	updateRecentModel();
    	updateAncientModel();
    }

    private void updateTodaymModel() throws ManagerBeanException {
        Calendar from = new GregorianCalendar();
        from.set(Calendar.HOUR_OF_DAY, 0);
        from.set(Calendar.MINUTE, 0);
        from.set(Calendar.SECOND, 0);
        Calendar to = new GregorianCalendar();
        to.set(Calendar.HOUR_OF_DAY, 23);
        to.set(Calendar.MINUTE, 59);
        to.set(Calendar.SECOND, 59);

        this.todayModel = new ListDataModel(getAlarmList(from.getTime(), to.getTime()));
    }

    public ListDataModel getTodayModel() throws ManagerBeanException{
        return this.todayModel;
    }
    
    private void updateRecentModel() throws ManagerBeanException{
        Calendar from = new GregorianCalendar();
        from.add(Calendar.DATE, -5);
        from.set(Calendar.HOUR_OF_DAY, 0);
        from.set(Calendar.MINUTE, 0);
        from.set(Calendar.SECOND, 0);
        Calendar to = new GregorianCalendar();
        to.add(Calendar.DATE, -1);
        to.set(Calendar.HOUR_OF_DAY, 23);
        to.set(Calendar.MINUTE, 59);
        to.set(Calendar.SECOND, 59);

        this.recentModel = new ListDataModel(getAlarmList(from.getTime(), to.getTime()));
    }

    public ListDataModel getRecentModel() throws ManagerBeanException{
        return this.recentModel;
    }
    
    private void updateAncientModel() throws ManagerBeanException{
        Calendar to = new GregorianCalendar();
        to.add(Calendar.DATE, -6);
        to.set(Calendar.HOUR_OF_DAY, 23);
        to.set(Calendar.MINUTE, 59);
        to.set(Calendar.SECOND, 59);

        this.ancientModel = new ListDataModel(getAlarmList(null, to.getTime()));
    }	

    public ListDataModel getAncientModel() throws ManagerBeanException{
        return this.ancientModel;
    }	

    public void onInit(ActionEvent event) {
        try {
			updateModels();
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onInit ",e);
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
    }
    
    public void onSelectTodayAlarm(ActionEvent event) {
        onSelectAlarm(event, todayModel);
    }

    public void onSelectRecentAlarm(ActionEvent event) {
        onSelectAlarm(event, recentModel);
    }

    public void onSelectAncientAlarm(ActionEvent event) {
        onSelectAlarm(event, ancientModel);
    }

    private void onSelectAlarm(ActionEvent event, ListDataModel model) {
        Alarm alarm = (Alarm) model.getRowData();
        try {
            select(event, alarm);
        } catch (ManagerBeanException e) {
        	LOGGER.error( e.getMessage(), e );
            throw new AbortProcessingException("Error obtaining alarm with id=" + alarm.getId(), e);
        }
    }
    
}