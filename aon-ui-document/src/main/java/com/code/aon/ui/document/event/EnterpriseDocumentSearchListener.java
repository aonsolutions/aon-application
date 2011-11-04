package com.code.aon.ui.document.event;


import static com.code.aon.document.EnterpriseDocumentAspect.ENTERPRISE_ID_NAME;
import static com.code.aon.document.EnterpriseDocumentAspect.PREFFIX;

import java.util.Date;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Enterprise;
import com.code.aon.document.AlfrescoCategory;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.event.ControllerSearchListener;

public class EnterpriseDocumentSearchListener extends ControllerSearchListener {

	private static final String PATH_FIELD = "PATH";
	private static final String TEXT_FIELD = "TEXT";
	private Enterprise enterprise;
	private Date startDate;
	private Date endDate; 
	private String text;
	private AlfrescoCategory[] selectedCategories;
	
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

	public AlfrescoCategory[] getSelectedCategories() {
		return selectedCategories;
	}

	public void setSelectedCategories(AlfrescoCategory[] selectedCategories) {
		this.selectedCategories = selectedCategories;
	}

	@Override
	protected void init() throws ManagerBeanException {
		setStartDate(null);
		setEndDate(null);
		setText(null);
		setSelectedCategories(null);
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
			criteria.addEqualExpression( TEXT_FIELD, getText());
		}
		if (! ArrayUtils.isEmpty(getSelectedCategories()) ) {
			addCategoriesToCriteria(criteria, getSelectedCategories());
		}
	}

	private void addCategoriesToCriteria( Criteria criteria, AlfrescoCategory[] categories ) { 
		Expression expToAdd = null;
		for( AlfrescoCategory category : categories ) {
			String value = category.getSearchValue();
			if ( expToAdd == null ) {
				expToAdd = ExpressionUtilities.getEqualExpression(PATH_FIELD, value);				
			} else {
				Expression exp  = ExpressionUtilities.getEqualExpression(PATH_FIELD, value);
				expToAdd = ExpressionUtilities.getOrExpression(expToAdd, exp);
			}
		}
		if ( expToAdd != null ) {
			criteria.addExpression(expToAdd);
		}
	}		
	
}