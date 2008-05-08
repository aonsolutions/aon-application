package com.code.aon.ui.groupware.event;

import java.util.Date;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.User;
import com.code.aon.groupware.Alarm;
import com.code.aon.groupware.Notice;
import com.code.aon.groupware.dao.IGroupWareAlias;
import com.code.aon.groupware.enumeration.AlarmSource;
import com.code.aon.groupware.enumeration.AlarmStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.groupware.controller.NoticeController;

public class NoticeControllerListener extends ControllerAdapter {

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		Notice notice = (Notice)event.getController().getTo();
		notice.setSender(UserUtils.getLoggedUser());
		notice.setDate(new Date());
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		Notice notice = (Notice)event.getController().getTo();
		insertRelatedAlarm(notice);
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		Notice notice = (Notice)event.getController().getTo();
		insertRelatedAlarm(notice);
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		NoticeController noticeController = (NoticeController)event.getController();
		Notice notice = (Notice)noticeController.getTo();
		if(notice.getWorkGroup() != null && notice.getWorkGroup().getId() != null){
			noticeController.loadUsers(notice.getWorkGroup().getId());
		}
	}
	
	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			NoticeController noticeController = (NoticeController)event.getController();
			Criteria criteria = noticeController.getCriteria();
			User user = UserUtils.getLoggedUser();
			criteria.addEqualExpression(noticeController.getFieldName(IGroupWareAlias.NOTICE_SENDER_ID), user.getId());
			criteria.addOrder(noticeController.getFieldName(IGroupWareAlias.NOTICE_DATE),false);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Error before model initialized", e);
		}
	}

	private void insertRelatedAlarm(Notice notice) throws ControllerListenerException{
		try {
			IManagerBean alarmBean = BeanManager.getManagerBean(Alarm.class);
			Alarm alarm = new Alarm();
			alarm.setAlarmDate(notice.getDate());
			alarm.setUser(notice.getRecipient());
			alarm.setSource(AlarmSource.NOTICE);
			alarm.setSourceId(notice.getId());
			alarm.setStatus(AlarmStatus.PENDING);
			alarm.setPriority(notice.getPriority());
			alarm.setDescription(notice.getSource() + " " + notice.getCompany() + " " + notice.getPhone() + " " + notice.getSubject());
			alarmBean.insert(alarm);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Error inserting related alarm", e);
		}
	}
}