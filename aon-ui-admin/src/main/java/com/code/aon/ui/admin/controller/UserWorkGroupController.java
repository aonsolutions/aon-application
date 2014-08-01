package com.code.aon.ui.admin.controller;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.User;
import com.code.aon.config.UserWorkGroup;
import com.code.aon.config.WorkGroup;
import com.code.aon.config.enumeration.WorkGroupStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class UserWorkGroupController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private final static Logger LOGGER = LoggerFactory.getLogger(UserWorkGroupController.class);
	
	private WorkGroup[] workGroups;
	
	private WorkGroup[] selected;
	
	private User user;
	
	public WorkGroup[] getWorkGroups() {
		return workGroups;
	}

	public void setWorkGroups(WorkGroup[] workGroups) {
		this.workGroups = workGroups;
	}

	public WorkGroup[] getSelected() {
		return selected;
	}

	public void setSelected(WorkGroup[] selected) {
		this.selected = selected;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<WorkGroup> getWorkGroupList() {
		try {
			IManagerBean bean = BeanManager.getManagerBean(WorkGroup.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.WORK_GROUP_STATUS), WorkGroupStatus.ACTIVE);
			criteria.addOrder(bean.getFieldName(IEntityAlias.WORK_GROUP_DESCRIPTION));
			return (List) bean.getList(criteria);
		} catch (ManagerBeanException e) {
			LOGGER.error( e.getMessage(), e);
		}
		return null;
	}	

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<UserWorkGroup> getUserWorkGroups() {
		try {
			IManagerBean bean = BeanManager.getManagerBean(UserWorkGroup.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.USER_WORK_GROUP_USER_ID), user.getId());
			return (List) bean.getList(criteria);
		} catch (ManagerBeanException e) {
			LOGGER.error( e.getMessage(), e);
		}
		return null;
	}	
	
	public void accept( ActionEvent event ) {
		try {
			List<WorkGroup> _workGroups = new LinkedList<WorkGroup>(Arrays.asList(this.selected));
			IManagerBean bean = BeanManager.getManagerBean(UserWorkGroup.class);
			List<UserWorkGroup> oldWorkGroups = getUserWorkGroups();
			if (! oldWorkGroups.isEmpty() ) {
				for( UserWorkGroup uwg : oldWorkGroups ) {
					if ( _workGroups.contains(uwg.getWorkGroup()) ) {
						_workGroups.remove(uwg.getWorkGroup());
					} else {
						bean.remove(uwg);
					}
				}
			}
			if (! _workGroups.isEmpty() ) {
				for( WorkGroup workGroup : _workGroups ) {
					UserWorkGroup uwg = new UserWorkGroup();
					uwg.setUser(user);
					uwg.setWorkGroup(workGroup);
					bean.insert(uwg);
				}			
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("Error updating user workgroup list", e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}		
	}
	
	public void init( User user ) {
		this.user = user;
		List<WorkGroup> list = getWorkGroupList();
		List<UserWorkGroup> userWorkGroups = getUserWorkGroups();
		this.selected = new WorkGroup[userWorkGroups.size()];
		for( int i = 0; i < this.selected.length; i++ ) {
			this.selected[i] = userWorkGroups.get(i).getWorkGroup();
			list.remove(this.selected[i]);
		}
		this.workGroups = list.toArray(new WorkGroup[list.size()]);
	}
	
}