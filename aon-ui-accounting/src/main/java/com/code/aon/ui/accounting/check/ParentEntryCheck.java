package com.code.aon.ui.accounting.check;

import java.util.LinkedList;
import java.util.List;

import com.code.aon.account.Account;
import com.code.aon.account.dao.IAccountAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;

public class ParentEntryCheck implements IAccountCheck {

	private static final String ACCOUNT_CONTROLLER_NAME = "account";
	private List <Account> list;

	/**
	 * Comprueba el derecho de apunte de las cuentas segun su nivel
	 */
	@Override
	public void onExecute() throws AccountingCheckException {
		list = new LinkedList<Account>();
		boolean prev = HibernateUtil.mustCloseSession();
		try {
			IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
			BasicController accountController = (BasicController)FormUtil.getController(ACCOUNT_CONTROLLER_NAME);
			Criteria criteria = new Criteria();
			List<ITransferObject> toList = accountBean.getList(criteria);
			
			for (ITransferObject to: toList) {
				Account account = (Account) to;
				if(account.getLevel()!=1){
					String id;
					int lenght = calculateLevel(account);
					id = account.getId().substring(0, lenght);
					
					criteria = new Criteria();
					criteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), id);
					accountController.setCriteria(criteria);
					accountController.onSearch(null);
					if(accountController.getModel().getRowCount()==0){
						list.add(account);
					}
				}
			}
		} catch (ManagerBeanException e) {
			throw new AccountingCheckException(e.getMessage(), e);
		} finally {
			HibernateUtil.setCloseSession(prev);
		}
	}
	
	private int calculateLevel(Account account){
		//return account.getLevel()==5?account.getLevel():account.getLevel()-1;
		return account.getLevel()-1;
	}
	
	public List <Account> getList(){
		return list;
	}

}