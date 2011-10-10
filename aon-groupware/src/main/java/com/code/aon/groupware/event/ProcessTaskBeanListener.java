package com.code.aon.groupware.event;

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;
import com.code.aon.groupware.Alarm;
import com.code.aon.groupware.ProcessTask;
import com.code.aon.groupware.Task;
import com.code.aon.groupware.enumeration.AlarmSource;
import com.code.aon.groupware.enumeration.AlarmStatus;
import com.code.aon.groupware.enumeration.Priority;

public class ProcessTaskBeanListener extends ManagerBeanListenerAdapter {

	@Override
	public void beanInserted(ManagerBeanEvent evt) throws ManagerBeanException {
		ProcessTask to = (ProcessTask) evt.getTo();
		if (to.getProcessDetail().getAlertDays() > 0) {
			Task task = to.getTask();
			Alarm alarm = new Alarm();
			alarm.setDescription(task.getDescription());
			Calendar alertCalendar = new GregorianCalendar();
			alertCalendar.setTime(task.getDueDate());
			alertCalendar.add(Calendar.DATE, 0 - to.getProcessDetail().getAlertDays());
			alarm.setAlarmDate(alertCalendar.getTime());
			alarm.setStatus(AlarmStatus.PENDING);
			alarm.setSource(AlarmSource.TASK);
			alarm.setSourceId(task.getId());
			alarm.setPriority(Priority.NONE);
			IManagerBean alarmBean = BeanManager.getManagerBean(Alarm.class);
			alarmBean.insert(alarm);
		}
	}
	
}
