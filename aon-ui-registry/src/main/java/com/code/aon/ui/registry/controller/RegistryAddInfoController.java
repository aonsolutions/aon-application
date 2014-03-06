package com.code.aon.ui.registry.controller;

import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.code.aon.common.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.customer.enumeration.CustomerStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.RegistryAddInfo;
import com.code.aon.ui.form.BasicController;
import com.esferalia.aon.entity.IEntityAlias;

public class RegistryAddInfoController extends BasicController {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private boolean customersOnly;

	public boolean isCustomersOnly() {
		return customersOnly;
	}

	public void setCustomerMode(ActionEvent event) {
		customersOnly = true;
	}

	public Expression getCustomersOnlyExpression() throws ManagerBeanException {
		IManagerBean customerBean = BeanManager.getManagerBean(Customer.class);			
		Criteria subCriteria = new Criteria();
		subCriteria.addEqualExpression(customerBean.getFieldName(IEntityAlias.CUSTOMER_STATUS), CustomerStatus.ACTIVE);
		ProjectionList projectionList = new ProjectionList(Projection.property(customerBean.getFieldName(IEntityAlias.CUSTOMER_ID)));
		return ExpressionUtilities.getSubQueryExpression(Customer.class, subCriteria, projectionList);
	}

	public List<SelectItem> getAddInfoAttributes() throws ManagerBeanException {
		List<SelectItem> attributes = new LinkedList<SelectItem>();
		IManagerBean rAddInfoBean = BeanManager.getManagerBean(RegistryAddInfo.class);
		Criteria criteria = new Criteria();
		if (isCustomersOnly()) {
			criteria.addInExpression(getFieldName(IEntityAlias.REGISTRY_ADD_INFO_REGISTRY_ID), getCustomersOnlyExpression());			
		}
		criteria.addOrder(rAddInfoBean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_ATTRIBUTE));
		Projection projection = Projection.group(rAddInfoBean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_ATTRIBUTE));
		for (Object obj : rAddInfoBean.getList(new ProjectionList(projection), criteria)) {
			String attribute = (String)obj;
			attributes.add(new SelectItem(attribute, attribute));
		}
		return attributes;
	}

}