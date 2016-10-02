package com.esferalia.aon.ui.payroll.event;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.esferalia.aon.entity.IEntityAlias;

public class AgreementSearchListener extends ControllerSearchListener {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final String PAY_SYSTEM_AGREEMENT = "PAY_SYSTEM_AGREEMENT";
	
	@Override
	protected void completeCriteria(Criteria criteria)
			throws ManagerBeanException, ExpressionException {
		
		criteria.addGreaterThanExpression(this.getFieldName(IEntityAlias.AGREEMENT_ID), 0);
		
		Criteria subCriteria = new Criteria();
		IManagerBean appParam = BeanManager.getManagerBean(ApplicationParameter.class);			
		subCriteria.addEqualExpression(appParam.getFieldName(IEntityAlias.APPLICATION_PARAMETER_NAME), PAY_SYSTEM_AGREEMENT);
		ProjectionList projectionList = new ProjectionList( Projection.property(appParam.getFieldName(IEntityAlias.APPLICATION_PARAMETER_VALUE)));
		
		criteria.addExpression(
			ExpressionUtilities.getOrExpression(
				ExpressionUtilities.getNotEqualExpression(appParam.getFieldName(IEntityAlias.APPLICATION_PARAMETER_DOMAIN), 0),
				ExpressionUtilities.getInExpression(
					this.getFieldName(IEntityAlias.AGREEMENT_ID), 
					ExpressionUtilities.getSubQueryExpression(
							ApplicationParameter.class, 
							subCriteria, 
							projectionList
					)
				)
			)
		);
	}

}