package com.code.aon.ui.project.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.project.ActivityType;
import com.code.aon.project.Dossier;
import com.code.aon.project.dao.IProjectAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.customer.controller.CustomerController;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class DossierController extends BasicController {
	
	private static final Logger LOGGER = Logger.getLogger(DossierController.class.getName());
	
	private static final String CUSTOMER_CONTROLLER_NAME = "customer";
	

	@SuppressWarnings("unused")
	public void onDossier(ActionEvent event){
		CustomerController customerController = (CustomerController)AonUtil.getController(CUSTOMER_CONTROLLER_NAME);
		Customer customer = (Customer)customerController.getTo();
		try {
			IManagerBean dossierBean = BeanManager.getManagerBean(Dossier.class);
			this.clearCriteria();
			getCriteria().addEqualExpression(dossierBean.getFieldName(IProjectAlias.DOSSIER_CUSTOMER_ID), customer.getId());
			this.onSearch(null);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error loading dossiers related with customer with id= " + customer.getId(), e);
		}
	}

	@SuppressWarnings("unchecked")
	public List getActivityTypes() throws ManagerBeanException {
		List<SelectItem> activityTypeList = new LinkedList<SelectItem>();
		IManagerBean activityTypeBean = BeanManager.getManagerBean(ActivityType.class);
		Dossier dossier = (Dossier)this.getTo();
		Criteria criteria = new Criteria();
		
		Expression e1 =  ExpressionUtilities.getEqualExpression(activityTypeBean.getFieldName(IProjectAlias.ACTIVITY_TYPE_DOSSIER_TYPE_ID), dossier.getDossierType().getId());
		Expression e2 =  ExpressionUtilities.getNullExpression(activityTypeBean.getFieldName(IProjectAlias.ACTIVITY_TYPE_DOSSIER_TYPE_ID));
		criteria.addExpression(ExpressionUtilities.getOrExpression(e1, e2)); 
		
		Iterator iter = activityTypeBean.getList(criteria).iterator();
		while(iter.hasNext()){
			ActivityType type = (ActivityType)iter.next();
			SelectItem item = new SelectItem(type.getId(), type.getDescription());
			activityTypeList.add(item);
		}
		return activityTypeList;
	}
	
	public boolean isForm(){
		if(this.isNew() || this.getTo() != null){
			return true;
		}
		return false;
	}

}