package com.code.aon.ui.groupware.event;

import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_MAIL_CONFIG;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import jakarta.mail.internet.InternetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.User;
import com.code.aon.config.UserWorkGroup;
import com.code.aon.groupware.Alarm;
import com.code.aon.groupware.Notice;
import com.code.aon.groupware.enumeration.AlarmSource;
import com.code.aon.groupware.enumeration.AlarmStatus;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.groupware.controller.NoticeController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.MailConfigController;
import com.code.aon.webmail.IMailAccount;
import com.code.aon.webmail.WebmailException;
import com.code.aon.webmail.bean.AonMessage;
import com.code.aon.webmail.bean.AonServer;
import com.esferalia.aon.entity.IEntityAlias;

public class NoticeControllerListener extends ControllerAdapter {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(NoticeControllerListener.class);
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		Notice notice = (Notice)event.getController().getTo();
		notice.setSender(UserUtils.getInstance().getLoggedUser());
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		NoticeController noticeController = (NoticeController)event.getController();
		Notice notice = (Notice)noticeController.getTo();
		insertRelatedAlarm(notice);
		if (noticeController.isSendMail() && noticeController.getMailList() != null) {
			sendNoticeMail(notice, noticeController.getMailList());
		}
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		Notice notice = (Notice)event.getController().getTo();
		insertRelatedAlarm(notice);
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		NoticeController noticeController = (NoticeController)event.getController();
		noticeController.setMailList(null);
		noticeController.setSendMail(false);
		Notice notice = (Notice) noticeController.getTo();
		noticeController.loadWorkGroups();
		noticeController.loadUsers( notice.getWorkGroup().getId() );
	}

	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		NoticeController noticeController = (NoticeController)event.getController();
		Notice notice = (Notice)noticeController.getTo();
		notice.setDate(new Date());
		notice.getWorkGroup().setId(NoticeController.SELECT_ONE_VALUE);
		noticeController.setMailList(null);
		noticeController.setSendMail(false);
		noticeController.loadWorkGroups();
		noticeController.resetUsers();
	}

	private void insertRelatedAlarm(Notice notice) throws ControllerListenerException{
		List<User> users = new ArrayList<User>();
		try {
			if (notice.getWorkGroup() == null && notice.getRecipient() == null) {
				//Se envia a todos los usuarios.
				users = getUsers(null);
			} else {
				if (notice.getRecipient() != null) {
					//Se envia a un solo usuario.
					users.add(notice.getRecipient());
				} else {
					//Se envia a un grupo.
					users = getUsers(notice.getWorkGroup().getId());
				}
			}

			for (int i=0;i<users.size();i++) {
				IManagerBean alarmBean = BeanManager.getManagerBean(Alarm.class);
				Alarm alarm = new Alarm();
				alarm.setAlarmDate(notice.getDate());
				alarm.setUser(users.get(i));
				alarm.setSource(AlarmSource.NOTICE);
				alarm.setSourceId(notice.getId());
				alarm.setStatus(AlarmStatus.PENDING);
				alarm.setPriority(notice.getPriority());
				alarm.setDescription(notice.getSource() + " " + notice.getCompany() + " " + notice.getPhone() + " " + notice.getSubject());
				alarmBean.insert(alarm);
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Error inserting related alarm", e);
		}
	}

	private List<User> getUsers(Integer workGroupId) throws ManagerBeanException {
		List<User> users = new ArrayList<User>();
    	if (workGroupId == null) {
            IManagerBean managerBean = BeanManager.getManagerBean(User.class);
            Criteria criteria = new Criteria();
            criteria.addEqualExpression(managerBean.getFieldName(IEntityAlias.USER_ACTIVE), true);
            criteria.addOrder(managerBean.getFieldName(IEntityAlias.USER_NAME));
            Iterator<ITransferObject> iterator = managerBean.getList(criteria).iterator();
            while (iterator.hasNext()) {
                User user = (User)iterator.next();
                users.add(user);
            }
    	}
    	else {
            IManagerBean managerBean = BeanManager.getManagerBean(UserWorkGroup.class);
            Criteria criteria = new Criteria();
            criteria.addEqualExpression(managerBean.getFieldName(IEntityAlias.USER_WORK_GROUP_WORK_GROUP_ID), workGroupId);
            criteria.addOrder(managerBean.getFieldName(IEntityAlias.USER_WORK_GROUP_USER_NAME));
            Iterator<ITransferObject> iterator = managerBean.getList(criteria).iterator();
            while (iterator.hasNext()) {
                UserWorkGroup userWorkGroup = (UserWorkGroup)iterator.next();
                User user = userWorkGroup.getUser();
                users.add(user);
            }
    	}
    	return users;
	}
	
	private void sendNoticeMail(Notice notice, String mailList) {
		AuthPrincipal user = AonUtil.getAuthPrincipal();
		String domain = user.getDomain();
		String login = user.getShortName();
		String username = UserUtils.getInstance().getLoggedUser().getName();
		String from = ""+login+"@"+domain+"";
		String to = mailList;
		String subject = "[AVISO] - Notificacion de " + domain;
		String content = "DE: " + notice.getSource() + "\nEMPRESA: " + notice.getCompany() + "\nTELEFONO: " + notice.getPhone() + "\nASUNTO: " + notice.getSubject();
		
		try {
			MailConfigController mailConfig = (MailConfigController) AonUtil.getRegisteredBean(BEAN_MAIL_CONFIG);
			IMailAccount mailAccount = mailConfig.getDefaultMailAccount(false);			
			AonServer server = new AonServer(mailAccount);
			InternetAddress iafrom = new InternetAddress(from, username);
			AonMessage aonMessage = server.createAonMessage(iafrom);
			aonMessage.setSender(iafrom);
			aonMessage.setRecipientsTo(to);
			aonMessage.setSubject(subject);
			aonMessage.setContent(content);
			server.sendMessage(aonMessage);
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (WebmailException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

}