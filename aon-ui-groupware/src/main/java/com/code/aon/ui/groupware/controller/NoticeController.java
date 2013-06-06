package com.code.aon.ui.groupware.controller;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.User;
import com.code.aon.config.UserWorkGroup;
import com.code.aon.config.WorkGroup;
import com.code.aon.config.enumeration.WorkGroupStatus;
import com.code.aon.groupware.Notice;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.webmail.db.MailAccount;
import com.esferalia.aon.entity.IEntityAlias;
import com.sun.faces.util.MessageFactory;

public class NoticeController extends BasicController {

	private final static Logger LOGGER = LoggerFactory.getLogger(NoticeController.class);
	public final static Integer SELECT_ONE_VALUE = -1;
	
	private List<SelectItem> workGroups;
	private List<SelectItem> users;
	private boolean sendMail = false;
	private String mailList = "";

	public Integer getSelectOneValue() {
		return SELECT_ONE_VALUE;
	}

	public List<SelectItem> getUsers() {
		return users;
	}

	public int getUserCount() {
		return this.users.size();
	}

	public List<SelectItem> getWorkGroups() {
		return this.workGroups;
	}

	public int getWorkGroupCount() {
		return this.workGroups.size();
	}

	public boolean isRecipientSelectable() {
		WorkGroup wg = ((Notice) getTo()).getWorkGroup();
		return (wg != null) && (!SELECT_ONE_VALUE.equals(wg.getId()));
	}

	public void workGroupChange(ValueChangeEvent event) {
		mailList = null;
		Integer workGroupId = (Integer) event.getNewValue();
		if (!SELECT_ONE_VALUE.equals(workGroupId)) {
			((Notice) getTo()).setRecipient(null);
			loadUsers(workGroupId);
		}
	}

	public void recipientChange(ValueChangeEvent event) {
		mailList = null;
	}

	public void resetUsers() {
		users = new LinkedList<SelectItem>();
	}

	@SuppressWarnings("unchecked")
	private List<User> getUser(Integer id) throws ManagerBeanException {
		IManagerBean managerBean = BeanManager.getManagerBean(User.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(managerBean.getFieldName(IEntityAlias.USER_ID), id);
		criteria.addEqualExpression(managerBean.getFieldName(IEntityAlias.USER_ACTIVE), true);
		criteria.addOrder(managerBean.getFieldName(IEntityAlias.USER_NAME));
		List<?> list = managerBean.getList(criteria); 
		return (List<User>) list; 
	}

	@SuppressWarnings("unchecked")
	private List<User> getAllUsers() throws ManagerBeanException {
		IManagerBean managerBean = BeanManager.getManagerBean(User.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(managerBean.getFieldName(IEntityAlias.USER_ACTIVE), true);
		criteria.addOrder(managerBean.getFieldName(IEntityAlias.USER_NAME));
		List<?> list = managerBean.getList(criteria); 
		return (List<User>) list; 
	}

	private List<User> getWorkGroupUsers(Integer workGroupId) throws ManagerBeanException {
		IManagerBean managerBean = BeanManager.getManagerBean(UserWorkGroup.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(managerBean.getFieldName(IEntityAlias.USER_WORK_GROUP_WORK_GROUP_ID), workGroupId);
		criteria.addOrder(managerBean.getFieldName(IEntityAlias.USER_WORK_GROUP_USER_NAME));
		List<User> result = new LinkedList<User>();
		for (ITransferObject to : managerBean.getList(criteria)) {
			UserWorkGroup userWorkGroup = (UserWorkGroup) to;
			if (userWorkGroup.getUser().isActive()) {
				result.add(userWorkGroup.getUser());
			}
		}
		return result;
	}

	private List<User> getSelectedUsers() throws ManagerBeanException {
		Notice notice = (Notice) getTo();
		User recipient = notice.getRecipient();
		if ((recipient != null) && (recipient.getId() != null)) {
			return getUser(recipient.getId());
		}
		WorkGroup wg = notice.getWorkGroup();
		if (wg != null) {
			if (wg.getId() == null) {
				return getAllUsers();
			} else if (!SELECT_ONE_VALUE.equals(wg.getId())) {
				return getWorkGroupUsers(wg.getId());
			}
		}
		return Collections.emptyList();
	}

	public void loadUsers(Integer workGroupId) {
		resetUsers();
		try {
			List<User> list = (workGroupId != null) ? getWorkGroupUsers(workGroupId) : getAllUsers();
			for (User user : list) {
				SelectItem item = new SelectItem(user, user.getName());
				users.add(item);
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("Error loading users of workgroup with id= " + workGroupId, e);
		}
	}

	private boolean hasUsers(WorkGroup workGroup) {
		try {
			IManagerBean managerBean = BeanManager.getManagerBean(UserWorkGroup.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(managerBean.getFieldName(IEntityAlias.USER_WORK_GROUP_WORK_GROUP_ID),
					workGroup.getId());
			return managerBean.getCount(criteria) > 0;
		} catch (ManagerBeanException e) {
			LOGGER.error("Error counting users of the workgroup " + workGroup.getId(), e);
		}
		return false;
	}

	public void loadWorkGroups() {
		try {
			this.workGroups = new LinkedList<SelectItem>();
			IManagerBean workGroupBean = BeanManager.getManagerBean(WorkGroup.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(workGroupBean.getFieldName(IEntityAlias.WORK_GROUP_STATUS),
					WorkGroupStatus.ACTIVE);
			criteria.addOrder(workGroupBean.getFieldName(IEntityAlias.WORK_GROUP_DESCRIPTION));
			Iterator<ITransferObject> iter = workGroupBean.getList(criteria).iterator();
			while (iter.hasNext()) {
				WorkGroup workGroup = (WorkGroup) iter.next();
				if (hasUsers(workGroup)) {
					SelectItem item = new SelectItem(workGroup.getId(), workGroup.getDescription());
					workGroups.add(item);
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("Error loading workgroups", e);
		}
	}

	private void chargeMails() {
		mailList = "";

		String SEP = "";
		try {
			for (User user : getSelectedUsers()) {
				String userEmail = getUserMail(user);
				if (! StringUtils.isEmpty(userEmail) ) {
					mailList += SEP + userEmail;
					SEP = ", ";					
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("Error retrieving emails", e);
		}
	}

	private String getUserMail( User user ) {
		try {
			IManagerBean  bean = BeanManager.getManagerBean(MailAccount.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.MAIL_ACCOUNT_USER_ID), user.getId());
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.MAIL_ACCOUNT_DEFAULT_ACCOUNT), Boolean.TRUE);
			List<ITransferObject> list = bean.getList(criteria);
			if (! list.isEmpty() ) {
				MailAccount mailAccount = (MailAccount) list.get(0);
				return mailAccount.getEmail();
			}
		} catch (ManagerBeanException e) {
			LOGGER.error( e.getMessage(), e );
		}
		return null;
	}
	
	public boolean isSendMail() {
		return sendMail;
	}

	public void setSendMail(boolean sendMail) {
		mailList = null;
		this.sendMail = sendMail;
	}

	public String getMailList() {
		if (sendMail && mailList == null)
			chargeMails();
		else if (!sendMail)
			mailList = null;
		return mailList;
	}

	public void setMailList(String mailList) {
		this.mailList = mailList;
	}

	public void workGroupCheck(FacesContext context, UIComponent component, Object value) {
		if (SELECT_ONE_VALUE.equals(value)) {
			throw new ValidatorException(MessageFactory.getMessage(context, UIInput.REQUIRED_MESSAGE_ID,
					MessageFactory.getLabel(context, component)));
		}
	}

}