package com.code.aon.ui.finance.event;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.enumeration.FinanceBatchStatus;
import com.code.aon.finance.enumeration.FinanceBatchType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.finance.controller.IFinanceController;
import com.code.aon.ui.form.event.ControllerSearchListenerEx;
import com.esferalia.aon.entity.IEntityAlias;

public class FBatchSearchListener extends ControllerSearchListenerEx {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private FinanceBatchType type;
	
	private FinanceBatchStatus status;
	
	public FinanceBatchType getType() {
		return type;
	}

	public void setType(FinanceBatchType type) {
		this.type = type;
	}

	public FinanceBatchStatus getStatus() {
		return status;
	}

	public void setStatus(FinanceBatchStatus status) {
		this.status = status;
	}

	@Override
	protected void init() throws ManagerBeanException {
		setType(null);
		setStatus(null);
	}	
	
	@Override
	protected void completeCriteria( Criteria criteria ) throws ManagerBeanException, ExpressionException {
		IFinanceController controller = (IFinanceController) getController();
		criteria.addEqualExpression(getFieldName(IEntityAlias.FINANCE_BATCH_PAYMENT), controller.isPayment());
		String typeAlias = getFieldName(IEntityAlias.FINANCE_BATCH_FINANCE_BATCH_TYPE);		
		if ( type != null ) {
			criteria.addEqualExpression(typeAlias, type);
		} else if ( controller.isPayment() ) {
			if (controller.isPayroll()) {
				Expression expr1 = ExpressionUtilities.getEqualExpression(typeAlias, FinanceBatchType.AEB_34_N);
				Expression expr2 = ExpressionUtilities.getEqualExpression(typeAlias, FinanceBatchType.SEPA_34_14_N_XML);
				criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));
			} else {
				criteria.addNotEqualExpression(typeAlias, FinanceBatchType.AEB_34_N);
				criteria.addNotEqualExpression(typeAlias, FinanceBatchType.SEPA_34_14_N_XML);
			}
		}
		if ( getStatus() != null ) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.FINANCE_BATCH_FINANCE_BATCH_STATUS), getStatus());
		}
	}

}