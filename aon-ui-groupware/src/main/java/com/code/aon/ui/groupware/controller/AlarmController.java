package com.code.aon.ui.groupware.controller;

import static com.code.aon.ui.common.ICommonMessages.ALARM_PENDING;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.ListDataModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.User;
import com.code.aon.groupware.Alarm;
import com.code.aon.groupware.enumeration.AlarmStatus;
import com.code.aon.groupware.enumeration.DelayTime;
import com.code.aon.groupware.enumeration.Priority;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class AlarmController extends BasicController {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(AlarmController.class);
	
	private final static String HOME_ACTION = "home";
	
    private ListDataModel todayModel;

    private ListDataModel recentModel;
    
    private ListDataModel ancientModel;
	
	private DelayTime delayTime;

	private User user;
	
	private boolean showNewAlarmWindow;
	
	private int pendingCount;
	
	private boolean returnToList;
	
	private String returnAction;
	
	public AlarmController() {
		this.user = UserUtils.getInstance().getLoggedUser();
		updatePendingCount();
		setBackAction(HOME_ACTION);
	}

	public boolean isReturnToList() {
		return returnToList;
	}

	public DelayTime getDelayTime() {
		return delayTime;
	}

	public void setDelayTime(DelayTime delayTime) {
		this.delayTime = delayTime;
	}

	public void onFinishAlarm(ActionEvent event){
		Alarm alarm = (Alarm)this.getTo();
		try {
			alarm.setStatus(AlarmStatus.FINISHED);
			alarm.setUser(user);
			getManagerBean().update(alarm);
			updateModels();
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
	
	private Criteria getCriteria(IManagerBean bean, Date from, Date to, boolean onlyPending) throws ManagerBeanException {
    	Criteria criteria = new Criteria();
    	String statusAlias = bean.getFieldName(IEntityAlias.ALARM_STATUS);
    	Expression expr1 = ExpressionUtilities.getEqualExpression(statusAlias, AlarmStatus.PENDING);
    	if ( onlyPending ) {
    		criteria.addExpression(expr1);
    	} else {
        	Expression expr2 = ExpressionUtilities.getEqualExpression(statusAlias, AlarmStatus.READ);
        	criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));    		
    	}
    	String userAlias = bean.getFieldName(IEntityAlias.ALARM_USER_ID);
    	AuthPrincipal principal = AonUtil.getAuthPrincipal();
    	criteria.addEqualExpression(userAlias, principal.getUserId());
    	String dateAlias = bean.getFieldName(IEntityAlias.ALARM_ALARM_DATE);
    	if ( from != null ) {
        	criteria.addGreaterThanOrEqualExpression(dateAlias, from);	
    	}
    	criteria.addLessThanOrEqualExpression(dateAlias, to);
    	if (! onlyPending ) {
        	criteria.addOrder(dateAlias, false);	
    	}
    	return criteria;
	}
	
    private List<ITransferObject> getAlarmList(Date from, Date to) throws ManagerBeanException {
    	IManagerBean bean = BeanManager.getManagerBean(Alarm.class);
    	Criteria criteria = getCriteria(bean, from, to, false);
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
        	this.returnToList = true;
        	this.returnAction = AonUtil.getConfigurationController().getCurrentAction();
        	setBackAction(null);
			updateModels();
			updatePendingCount();
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
    
    private void updateModel( ListDataModel model, ITransferObject alarm ) {
    	if ( model.isRowAvailable() ) {
    		@SuppressWarnings("unchecked")
			List<ITransferObject> list = (List<ITransferObject>) model.getWrappedData();	
        	int index = model.getRowIndex();
        	list.set(index, alarm);
    	}
    }

    private void onSelectAlarm(ActionEvent event, ListDataModel model) {
        Alarm alarm = (Alarm) model.getRowData();
        try {
            select(event, alarm);
            updateModel(model, getTo() );
        } catch (ManagerBeanException e) {
        	LOGGER.error( e.getMessage(), e );
            throw new AbortProcessingException("Error obtaining alarm with id=" + alarm.getId(), e);
        }
    }
    
    public boolean isShowNewAlarmWindow() {
		return showNewAlarmWindow;
	}

	public void setShowNewAlarmWindow(boolean showNewAlarmWindow) {
		this.showNewAlarmWindow = showNewAlarmWindow;
	}

	public void initAlarm() {
		Alarm alarm = (Alarm)this.getTo();
		alarm.setStatus(AlarmStatus.PENDING);
		alarm.setPriority(Priority.NONE);
		alarm.setUser(user);
    }

	public int getPendingCount() {
		return pendingCount;
	}
	
	public String getPendingAlarmTitle() {
		return AonUtil.getMessage(ALARM_PENDING, pendingCount);
	}

	public void onRefresh( ActionEvent event ) {
		updatePendingCount();
	}
	
	public void updatePendingCount() {
		try {		
	    	IManagerBean bean = BeanManager.getManagerBean(Alarm.class);
	    	Criteria criteria = getCriteria(bean, null, new Date(), true);
	    	pendingCount = bean.getCount(criteria);
		} catch (ManagerBeanException e) {
			LOGGER.error( e.getMessage(), e );
		}
	}
	
	public String returnAction() {
		return this.returnAction;
	}

	public void onDelayTimeChanged( ValueChangeEvent event ) {
		if ( event.getNewValue() != null ) {
			DelayTime dt = (DelayTime) event.getNewValue();
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(new Date());
			calendar.add(Calendar.MINUTE, dt.getValue());
			Alarm alarm = (Alarm) getTo();
			alarm.setAlarmDate(calendar.getTime());
		}
	}
	
}