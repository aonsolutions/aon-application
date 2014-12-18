package com.esferalia.aon.ui.payroll.event.contract;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.esferalia.aon.entity.IEntityAlias;

public class ContractClauseSkipDomainListener extends ControllerSearchListener {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	protected void completeCriteria( Criteria criteria ) throws ManagerBeanException, ExpressionException {
		Integer parentDomain = DomainManager.getParentDomain();
		if(parentDomain!=null){
			criteria.setSkipDomainFilter(true);
			Expression expr1 = ExpressionUtilities.getEqualExpression(this.getFieldName(IEntityAlias.CONTRACT_CLAUSE_DOMAIN), DomainManager.getCurrentDomain());
			Expression expr2 = ExpressionUtilities.getEqualExpression(this.getFieldName(IEntityAlias.CONTRACT_CLAUSE_DOMAIN), parentDomain);
			criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));
		}
	}
	
}
