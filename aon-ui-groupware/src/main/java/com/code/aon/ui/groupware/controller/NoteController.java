package com.code.aon.ui.groupware.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.UserWorkGroup;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;

public class NoteController extends BasicController {
	
	
	private final static Logger LOGGER = LoggerFactory.getLogger(NoteController.class);
	
	private List<SelectItem> users = new LinkedList<SelectItem>();
	
	public List<SelectItem> getUsers() {
		return users;
	}

	public void setUsers(List<SelectItem> users) {
		this.users = users;
	}
	
	public void workGroupChange(ValueChangeEvent event) {
        if (event.getNewValue() != null && !"".equals(event.getNewValue())) {
            loadUsers(new Integer(event.getNewValue().toString()));
        } else {
        	users = new LinkedList<SelectItem>();
        }
    }

    @SuppressWarnings("unchecked")
    public void loadUsers(Integer workGroupId) {
    	users = new LinkedList<SelectItem>();
        try {
            IManagerBean managerBean = BeanManager.getManagerBean(UserWorkGroup.class);
            Criteria criteria = new Criteria();
            criteria.addEqualExpression(managerBean.getFieldName(IConfigAlias.USER_WORK_GROUP_WORK_GROUP_ID), workGroupId);
            Iterator iterator = managerBean.getList(criteria).iterator();
            while (iterator.hasNext()) {
                UserWorkGroup userWorkGroup = (UserWorkGroup)iterator.next();
                SelectItem item = new SelectItem(userWorkGroup.getId(), userWorkGroup.getUser().getName());
                users.add(item);
            }
        } catch (ManagerBeanException e) {
            LOGGER.error("Error loading users of workgroup with id= " + workGroupId.toString(), e);
        }
    }
}