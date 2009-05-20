package com.code.aon.ui.groupware.controller;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;
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

public class NoteController extends BasicController {
	
	private static final Logger LOGGER = Logger.getLogger(NoteController.class.getName());
	
	private Date fromDate;
	
	private Date toDate;
	
	private List<SelectItem> users = new LinkedList<SelectItem>();
	
	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	
	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
	
	@Override
	public void onSearch(ActionEvent event) {
		addFromDateExpression();
		addToDateExpression();
		super.onSearch(event);
	}

	public void addFromDateExpression(){
        if(this.fromDate != null) {
            try {
                getCriteria().addGreaterThanOrEqualExpression(getFieldName(IGroupWareAlias.NOTE_DATE), this.fromDate);
            } catch (ManagerBeanException e) {
                LOGGER.log(Level.SEVERE, "Error adding FROM due date expression", e);
            }
    		setFromDate(null);
        }
    }
    
    public void addToDateExpression(){
        if(this.toDate != null) {
            try {
                getCriteria().addLessThanOrEqualExpression(getFieldName(IGroupWareAlias.NOTE_DATE), this.toDate);
            } catch (ManagerBeanException e) {
                LOGGER.log(Level.SEVERE, "Error adding TO due date expression", e);
            }
            setToDate(null);
        }
    }	
	
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
            LOGGER.log(Level.SEVERE, "Error loading users of workgroup with id= " + workGroupId.toString(), e);
        }
    }
}