package com.code.aon.ui.config.event;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.User;
import com.code.aon.config.UserWorkGroup;
import com.code.aon.config.WorkGroup;
import com.code.aon.config.enumeration.WorkGroupStatus;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.listener.LinesControllerListener;

public class UserWorkGroupListener extends LinesControllerListener {
	
	private static final Logger LOGGER = Logger.getLogger(UserWorkGroupListener.class.getName());

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		User user = (User)event.getController().getTo();
		WorkGroup group = createWorkGroup(user);
		createUserWorkGroup(user, group);
		super.afterBeanAdded(event);
	}

	private WorkGroup createWorkGroup(User user) {
		WorkGroup group = new WorkGroup();
		try {
			IManagerBean workGroupBean = BeanManager.getManagerBean(WorkGroup.class);
			group.setDescription(user.getName().toUpperCase());
			group.setStatus(WorkGroupStatus.ACTIVE);
			workGroupBean.insert(group);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error creating workGroup for User with id=" + user.getId(), e);
		}
		return group;
	}
	
	private void createUserWorkGroup(User user, WorkGroup group) {
		UserWorkGroup userWorkGroup = new UserWorkGroup();
		try {
			IManagerBean userWorkGroupBean = BeanManager.getManagerBean(UserWorkGroup.class);
			userWorkGroup.setUser(user);
			userWorkGroup.setWorkGroup(group);
			userWorkGroupBean.insert(userWorkGroup);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error adding user with id=" + user.getId() + "to group with id= " + group.getId(), e);
		}
	}
}