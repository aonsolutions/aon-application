package com.code.aon.ui.document.event;


import static com.code.aon.ui.document.controller.EnterpriseDocumentAspect.ENTERPRISE_ID_NAME;
import static com.code.aon.ui.document.controller.EnterpriseDocumentAspect.PREFFIX;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Enterprise;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.event.ControllerSearchListener;

public class EnterpriseDocumentSearchListener extends ControllerSearchListener {

	private Enterprise enterprise;
	private Date startDate;
	private Date endDate; 
	private String text;
	
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	public Enterprise getEnterprise() {
		return enterprise;
	}

	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	protected void init() throws ManagerBeanException {
		setStartDate(null);
		setEndDate(null);
		setText(null);
		IManagerBean enterpriseBean = BeanManager.getManagerBean(Enterprise.class);
		setEnterprise((Enterprise) enterpriseBean.createNewTo());
	}
	
	@Override
	protected void completeCriteria( Criteria criteria ) throws ManagerBeanException, ExpressionException {
		if ((getEnterprise() != null) && (getEnterprise().getId() != null)) {
			criteria.addEqualExpression( PREFFIX + ENTERPRISE_ID_NAME, getEnterprise().getId());			
		}
		if ( (getStartDate() != null) || (getEndDate() != null) ) {
			Object minor = (getStartDate() != null) ? getStartDate() : "MIN";
			Object mayor = (getEndDate() != null) ? getEndDate() : "MAX";
			criteria.addBetweenExpression( "cm:created", minor, mayor );
		}
		if (! StringUtils.isEmpty(getText()) ) {
			criteria.addEqualExpression( "TEXT", getText());
		}
	}

}