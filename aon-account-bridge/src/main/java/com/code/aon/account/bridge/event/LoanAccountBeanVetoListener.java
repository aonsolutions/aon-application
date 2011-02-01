package com.code.aon.account.bridge.event;



import java.util.List;

import com.code.aon.account.bridge.LoanAccount;
import com.code.aon.account.bridge.dao.IAccountBridgeAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;

public class LoanAccountBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		LoanAccount loanAccount = (LoanAccount) evt.getTo();
		checkIfLoanAssigned(loanAccount);
	}

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		LoanAccount loanAccount = (LoanAccount) evt.getTo();
		checkIfLoanAssigned(loanAccount);
	}

	private void checkIfLoanAssigned(LoanAccount loanAccount) throws ManagerBeanVetoListenerException{
		try {
			if (loanAccount.getLoan() == null) {
				throw new ManagerBeanVetoListenerException("El préstamo es un dato requerido.");	
			}
			IManagerBean bean = BeanManager.getManagerBean(LoanAccount.class);
			Integer id = loanAccount.getLoan().getId();
			String alias = bean.getFieldName( IAccountBridgeAlias.LOAN_ACCOUNT_LOAN_ID);
			Criteria c = new Criteria();
			c.addEqualExpression(alias, id);
			if ( loanAccount.getId() != null ) {
				String idAlias = bean.getFieldName( IAccountBridgeAlias.LOAN_ACCOUNT_ID);
				Expression exp = ExpressionUtilities.getNotEqualExpression(idAlias, loanAccount.getId());
				c.addExpression( exp );	
			}
			List<ITransferObject> list = bean.getList(c);
			if (list.size() > 0 ) {
				LoanAccount dup = (LoanAccount) list.get(0);
				StringBuilder sb = new StringBuilder();
				sb.append("El préstamo ");
				sb.append(loanAccount.getAccountDescription());
				sb.append(" ya está enlazado con la cuenta ");
				sb.append(dup.getAccount().getId());
				sb.append(" ");
				sb.append(dup.getAccount().getDescription());
				sb.append(".");
				throw new ManagerBeanVetoListenerException(sb.toString()); 
			}
			
		} catch (ManagerBeanException e) {
			throw new ManagerBeanVetoListenerException(e.getMessage(),e); 
		}
	}

}
