package com.code.aon.ui.academy.controller;

import static com.code.aon.ui.customer.controller.ICustomerConstants.CUSTOMER_CONTROLLER_NAME;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.LinesController;
import com.esferalia.aon.entity.IEntityAlias;

public class CustomerLoanController extends LinesController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerLoanController.class);

	private boolean showNewWindow;
	
	private boolean history;
	
	public boolean isShowNewWindow() {
		return showNewWindow;
	}

	public void setShowNewWindow(boolean showNewWindow) {
		this.showNewWindow = showNewWindow;
	}
	
	public boolean isHistory() {
		return history;
	}

	public void setHistory(boolean history) {
		this.history = history;
		updateModel();		
	}
	
	@Override
	public void clearCriteria() throws ManagerBeanException {
		changeInitExpression();		
		super.clearCriteria();
	}

	private void changeInitExpression() {
		Expression expression = null;
		try {
			String alias = getFieldName(IEntityAlias.ALUMN_LOAN_END_DATE);
			if ( isHistory() ) {
				expression = ExpressionUtilities.getNotNullExpression(alias);
			} else {
				expression = ExpressionUtilities.getNullExpression(alias);
			}
			List<Expression> list = new LinkedList<Expression>();
			list.add(expression);
			setInitExpressions(list);			
		} catch ( ManagerBeanException e ) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	public boolean isShowTab() throws ManagerBeanException {
		Customer customer = (Customer) getMasterController().getTo();
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(getFieldName(IEntityAlias.ALUMN_LOAN_CUSTOMER_ID), customer.getId());
		return getManagerBean().getCount(criteria) > 0;
	}

	private void updateModel() {
		try {
			clearCriteria();
			Criteria criteria = getCriteria();
			IController controller = FormUtil.getController(CUSTOMER_CONTROLLER_NAME);
			Serializable id = controller.getManagerBean().getId(controller.getTo());
			criteria.addEqualExpression(getFieldName(IEntityAlias.ALUMN_LOAN_CUSTOMER_ID), id);
			onSearch(null);
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
}
