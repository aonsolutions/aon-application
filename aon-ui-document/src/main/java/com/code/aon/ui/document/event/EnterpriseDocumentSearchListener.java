package com.code.aon.ui.document.event;


import static com.code.aon.document.BasicAlfresco.MIME_TYPE;
import static com.code.aon.document.EnterpriseDocumentAspect.ENTERPRISE_ID_NAME;
import static com.code.aon.document.EnterpriseDocumentAspect.PREFFIX;
import static com.code.aon.ui.document.controller.IDocumentConstants.MANAGER_CONTROLLER_NAME;

import java.util.Date;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.company.Enterprise;
import com.code.aon.document.AlfrescoCategory;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.document.controller.ManagerController;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.code.aon.ui.util.AonUtil;

public class EnterpriseDocumentSearchListener extends ControllerSearchListener {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EnterpriseDocumentSearchListener.class);

	private static final String PATH_FIELD = "PATH";
	private static final String TEXT_FIELD = "TEXT";
	private Enterprise enterprise;
	private Date startDate;
	private Date endDate; 
	private String text;
	private MimeType type;
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

	public MimeType getType() {
		return type;
	}

	public void setType(MimeType type) {
		this.type = type;
	}

	public AlfrescoCategory[] getSelectedCategories() {
		return selectedCategories;
	}

	public void setSelectedCategories(AlfrescoCategory[] selectedCategories) {
		this.selectedCategories = selectedCategories;
	}

	@Override
	protected void init() throws ManagerBeanException {
		reset( true );
	}
	
	private void reset( boolean createTo ) throws ManagerBeanException {
		setStartDate(null);
		setEndDate(null);
		setText(null);
		setType(null);
		setSelectedCategories(null);
		Enterprise enterprise = null;
		if ( createTo ) {
			ManagerController mc = (ManagerController) AonUtil.getRegisteredBean(MANAGER_CONTROLLER_NAME);
			if ( mc.isMainEnterprise() ) {
				IManagerBean enterpriseBean = BeanManager.getManagerBean(Enterprise.class);
				enterprise = (Enterprise) enterpriseBean.createNewTo();
			} else {
				enterprise = mc.getLoggedUser().getEnterprise();				
			}
		}
		setEnterprise(enterprise);
	}
	
	@Override
	public void afterModelSearched(ControllerEvent event) throws ControllerListenerException {
		try {			
			reset( false );
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
			throw new ControllerListenerException(e.getMessage(), e);
		}
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
		if ( getType() != null ) {
			criteria.addEqualExpression( MIME_TYPE, getType().getName());
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