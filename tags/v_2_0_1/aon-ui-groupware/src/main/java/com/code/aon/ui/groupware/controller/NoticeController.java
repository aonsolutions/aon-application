package com.code.aon.ui.groupware.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.UserWorkGroup;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.groupware.dao.IGroupWareAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;

public class NoticeController extends BasicController {
	
	private static final Logger LOGGER = Logger.getLogger(NoticeController.class.getName());

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
            criteria.addOrder(managerBean.getFieldName(IConfigAlias.USER_WORK_GROUP_USER_NAME));
            Iterator iterator = managerBean.getList(criteria).iterator();
            while (iterator.hasNext()) {
                UserWorkGroup userWorkGroup = (UserWorkGroup)iterator.next();
                SelectItem item = new SelectItem(userWorkGroup.getUser().getId(), userWorkGroup.getUser().getName());
                users.add(item);
            }
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error loading users of workgroup with id= " + workGroupId.toString(), e);
        }
    }
    
	public void addFromDateExpression(ValueChangeEvent event){
        if(event.getNewValue() != null) {
            try {
                getCriteria().addGreaterThanOrEqualExpression(getFieldName(IGroupWareAlias.NOTICE_DATE), event.getNewValue());
            } catch (ManagerBeanException e) {
                LOGGER.log(Level.SEVERE, "Error adding FROM due date expression", e);
            }
        }
    }
    
    public void addToDateExpression(ValueChangeEvent event){
        if(event.getNewValue() != null) {
            try {
                getCriteria().addLessThanOrEqualExpression(getFieldName(IGroupWareAlias.NOTICE_DATE), event.getNewValue());
            } catch (ManagerBeanException e) {
                LOGGER.log(Level.SEVERE, "Error adding TO due date expression", e);
            }
        }
    }
}