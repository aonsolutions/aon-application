package com.code.aon.ui.account.controller;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.faces.model.SelectItem;

import com.code.aon.account.Account;
import com.code.aon.account.IAccountConstants;
import com.code.aon.common.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class AccountCollectionsController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private List<SelectItem> getAccounts(Criteria criteria, boolean pojo) throws ManagerBeanException {
		List<SelectItem> list = new LinkedList<SelectItem>();
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		List<ITransferObject> accounts = accountBean.getList(criteria); 
		for (ITransferObject to : accounts ) {
			Account account = (Account) to;
			SelectItem item = new SelectItem(pojo?account:account.getId(), account.getFullDescription());
			list.add(item);
		}
		return list;
	}
	
	private List<SelectItem> getSalesAccounts(boolean pojo) throws ManagerBeanException, ExpressionException {
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_CODE), "70*");
		criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_ENABLED),new Boolean(true));
		criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_ACTIVE),new Boolean(true));
		return getAccounts(criteria,pojo);
	}
	public List<SelectItem> getSalesAccounts() throws ManagerBeanException, ExpressionException {
		return getSalesAccounts(true);
	}

	public List<SelectItem> getSalesAccountsIds() throws ManagerBeanException, ExpressionException {
		return getSalesAccounts(false);
	}


	private List<SelectItem> getPurchaseAccounts(boolean pojo) throws ManagerBeanException, ExpressionException {
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_CODE), "60*");
		criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_ENABLED),new Boolean(true));
		criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_ACTIVE),new Boolean(true));
		return getAccounts(criteria,pojo);
	}
	public List<SelectItem> getPurchaseAccounts() throws ManagerBeanException, ExpressionException {
		return getPurchaseAccounts(true);
	}

	public List<SelectItem> getPurchaseAccountsIds() throws ManagerBeanException, ExpressionException {
		return getPurchaseAccounts(false);
	}

	private List<SelectItem> getCashAccounts(boolean pojo) throws ManagerBeanException, ExpressionException {
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_CODE), "570*");
		criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_ENABLED),new Boolean(true));
		criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_ACTIVE),new Boolean(true));
		return getAccounts(criteria,pojo);
	}
	public List<SelectItem> getCashAccounts() throws ManagerBeanException, ExpressionException {
		return getCashAccounts(true);
	}

	public List<SelectItem> getCashAccountsIds() throws ManagerBeanException, ExpressionException {
		return getCashAccounts(false);
	}

	private List<SelectItem> getExpensesAccounts(boolean pojo) throws ManagerBeanException {
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Expression expression1 = ExpressionUtilities.getLikeExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_CODE), "62%");
		Expression expression2 = ExpressionUtilities.getLikeExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_CODE), "66%");
		Criteria criteria = new Criteria();
		criteria.addExpression(ExpressionUtilities.getOrExpression(expression1, expression2));
		criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_ENABLED),new Boolean(true));
		criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_ACTIVE),new Boolean(true));
		return getAccounts(criteria,pojo);
	}
	public List<SelectItem> getExpensesAccounts() throws ManagerBeanException {
		return getExpensesAccounts(true);
	}

	public List<SelectItem> getExpensesAccountsIds() throws ManagerBeanException {
		return getExpensesAccounts(false);
	}

	private List<SelectItem> getChargedVatAccounts(boolean pojo) throws ManagerBeanException, ExpressionException {
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_CODE), "477*");
		criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_ENABLED),new Boolean(true));
		criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_ACTIVE),new Boolean(true));
		return getAccounts(criteria,pojo);
	}
	public List<SelectItem> getChargedVatAccounts() throws ManagerBeanException, ExpressionException {
		return getChargedVatAccounts(true);
	}

	public List<SelectItem> getChargedVatAccountsIds() throws ManagerBeanException, ExpressionException {
		return getChargedVatAccounts(false);
	}

	private List<SelectItem> getPaidVatAccounts(boolean pojo) throws ManagerBeanException, ExpressionException {
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_CODE), "472*");
		criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_ENABLED),new Boolean(true));
		criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_ACTIVE),new Boolean(true));
		return getAccounts(criteria,pojo);
	}
	public List<SelectItem> getPaidVatAccounts() throws ManagerBeanException, ExpressionException {
		return getPaidVatAccounts(true);
	}

	public List<SelectItem> getPaidVatAccountsIds() throws ManagerBeanException, ExpressionException {
		return getPaidVatAccounts(false);
	}

	private List<SelectItem> getPaidRetentionAccounts(boolean pojo) throws ManagerBeanException, ExpressionException {
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_CODE), "473*");
		criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_ENABLED),new Boolean(true));
		criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_ACTIVE),new Boolean(true));
		return getAccounts(criteria,pojo);
	}
	public List<SelectItem> getPaidRetentionAccounts() throws ManagerBeanException, ExpressionException {
		return getPaidRetentionAccounts(true);
	}

	public List<SelectItem> getPaidRetentionAccountsIds() throws ManagerBeanException, ExpressionException {
		return getPaidRetentionAccounts(false);
	}

	private List<SelectItem> getChargedRetentionAccounts(boolean pojo) throws ManagerBeanException, ExpressionException {
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_CODE), "4751*");
		criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_ENABLED),new Boolean(true));
		criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_ACTIVE),new Boolean(true));
		return getAccounts(criteria,pojo);
	}
	public List<SelectItem> getChargedRetentionAccounts() throws ManagerBeanException, ExpressionException {
		return getChargedRetentionAccounts(true);
	}

	public List<SelectItem> getChargedRetentionAccountsIds() throws ManagerBeanException, ExpressionException {
		return getChargedRetentionAccounts(false);
	}

	private List<SelectItem> getSalaryAccounts(boolean pojo) throws ManagerBeanException, ExpressionException {
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_CODE), "640*");
		criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_ENABLED),new Boolean(true));
		criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_ACTIVE),new Boolean(true));
		return getAccounts(criteria,pojo);
	}
	public List<SelectItem> getSalaryAccounts() throws ManagerBeanException, ExpressionException {
		return getSalaryAccounts(true);
	}

	public List<SelectItem> getSalaryAccountsIds() throws ManagerBeanException, ExpressionException {
		return getSalaryAccounts(false);
	}


	private List<SelectItem> getSalaryAllowanceAccounts(boolean pojo) throws ManagerBeanException, ExpressionException {
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_CODE), "629*");
		criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_ENABLED),new Boolean(true));
		criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_ACTIVE),new Boolean(true));
		return getAccounts(criteria,pojo);
	}
	public List<SelectItem> getSalaryAllowanceAccounts() throws ManagerBeanException, ExpressionException {
		return getSalaryAllowanceAccounts(true);
	}
	public List<SelectItem> getSalaryAllowanceAccountsIds() throws ManagerBeanException, ExpressionException {
		return getSalaryAllowanceAccounts(false);
	}


	private List<SelectItem> getSalaryCompensationAccounts(boolean pojo) throws ManagerBeanException, ExpressionException {
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_CODE), "641*");
		criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_ENABLED),new Boolean(true));
		criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_ACTIVE),new Boolean(true));
		return getAccounts(criteria,pojo);
	}
	public List<SelectItem> getSalaryCompensationAccounts() throws ManagerBeanException, ExpressionException {
		return getSalaryCompensationAccounts(true);
	}
	public List<SelectItem> getSalaryCompensationAccountsIds() throws ManagerBeanException, ExpressionException {
		return getSalaryCompensationAccounts(false);
	}
	
	private List<SelectItem> getPendingSalaryAccounts(boolean pojo) throws ManagerBeanException, ExpressionException {
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_CODE), "465*");
		criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_ENABLED),new Boolean(true));
		criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_ACTIVE),new Boolean(true));
		return getAccounts(criteria,pojo);
	}
	public List<SelectItem> getPendingSalaryAccounts() throws ManagerBeanException, ExpressionException {
		return getPendingSalaryAccounts(true);
	}

	public List<SelectItem> getPendingSalaryAccountsIds() throws ManagerBeanException, ExpressionException {
		return getPendingSalaryAccounts(false);
	}


	private List<SelectItem> getSocialInsuranceAccounts(boolean pojo) throws ManagerBeanException, ExpressionException {
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_CODE), "476*");
		criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_ENABLED),new Boolean(true));
		criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_ACTIVE),new Boolean(true));
		return getAccounts(criteria,pojo);
	}
	public List<SelectItem> getSocialInsuranceAccounts() throws ManagerBeanException, ExpressionException {
		return getSocialInsuranceAccounts(true);
	}

	public List<SelectItem> getSocialInsuranceAccountsIds() throws ManagerBeanException, ExpressionException {
		return getSocialInsuranceAccounts(false);
	}

	private List<SelectItem> getEnterpriseSocialInsuranceAccounts(boolean pojo) throws ManagerBeanException, ExpressionException {
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_CODE), "642*");
		criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_ENABLED),new Boolean(true));
		criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_ACTIVE),new Boolean(true));
		return getAccounts(criteria,pojo);
	}
	public List<SelectItem> getEnterpriseSocialInsuranceAccounts() throws ManagerBeanException, ExpressionException {
		return getEnterpriseSocialInsuranceAccounts(true);
	}
	public List<SelectItem> getEnterpriseSocialInsuranceAccountsIds() throws ManagerBeanException, ExpressionException {
		return getEnterpriseSocialInsuranceAccounts(false);
	}

	private List<SelectItem> getFixedAssetAccounts(boolean pojo) throws ManagerBeanException, ExpressionException {
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_CODE), "20*|21*");
		criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_ENABLED), new Boolean(false));
		criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_LEVEL), new Integer(4));
		criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_ACTIVE),new Boolean(true));
		return getAccounts(criteria,pojo);
	}
	public List<SelectItem> getFixedAssetAccounts() throws ManagerBeanException, ExpressionException {
		return getFixedAssetAccounts(true);
	}
	public List<SelectItem> getFixedAssetAccountsIds() throws ManagerBeanException, ExpressionException {
		return getFixedAssetAccounts(false);
	}

	private List<SelectItem> getFixedAssetAccountsExtended(boolean pojo) throws ManagerBeanException, ExpressionException {
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_CODE), "20*|21*|22*");
		criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_ENABLED), new Boolean(true));
		criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_ACTIVE),new Boolean(true));
		return getAccounts(criteria,pojo);
	}
	public List<SelectItem> getFixedAssetAccountsExtended() throws ManagerBeanException, ExpressionException {
		return getFixedAssetAccountsExtended(true);
	}
	public List<SelectItem> getFixedAssetAccountsExtendedIds() throws ManagerBeanException, ExpressionException {
		return getFixedAssetAccountsExtended(false);
	}

	private List<SelectItem> getAccumulatedDepreciationAccounts(boolean pojo) throws ManagerBeanException, ExpressionException {
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_CODE), "280*|281*|282*");
		criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_ENABLED), new Boolean(false));
		criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_LEVEL), new Integer(4));
		criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_ACTIVE),new Boolean(true));
		return getAccounts(criteria,pojo);
	}
	public List<SelectItem> getAccumulatedDepreciationAccounts() throws ManagerBeanException, ExpressionException {
		return getAccumulatedDepreciationAccounts(true);
	}
	public List<SelectItem> getAccumulatedDepreciationAccountsIds() throws ManagerBeanException, ExpressionException {
		return getAccumulatedDepreciationAccounts(false);
	}

	
	private List<SelectItem> getAmortizationAllocationAccounts(boolean pojo) throws ManagerBeanException, ExpressionException {
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_CODE), "680*|681*|682*");
		criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_ENABLED), new Boolean(false));
		criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_LEVEL), new Integer(4));
		criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_ACTIVE),new Boolean(true));
		return getAccounts(criteria,pojo);
	}
	public List<SelectItem> getAmortizationAllocationAccounts() throws ManagerBeanException, ExpressionException {
		return getAmortizationAllocationAccounts(true);
	}
	public List<SelectItem> getAmortizationAllocationAccountsIds() throws ManagerBeanException, ExpressionException {
		return getAmortizationAllocationAccounts(false);
	}

	private List<SelectItem> getDebtInterestAccounts(boolean pojo) throws ManagerBeanException, ExpressionException {
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_CODE), "662*");
		criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_ENABLED),new Boolean(true));
		criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_ACTIVE),new Boolean(true));
		return getAccounts(criteria,pojo);
	}
	public List<SelectItem> getDebtInterestAccounts() throws ManagerBeanException, ExpressionException {
		return getDebtInterestAccounts(true);
	}
	public List<SelectItem> getDebtInterestAccountsIds() throws ManagerBeanException, ExpressionException {
		return getDebtInterestAccounts(false);
	}


	private List<SelectItem> getFinancialExpensesAccounts(boolean pojo) throws ManagerBeanException, ExpressionException {
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_CODE), "669*");
		criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_ENABLED),new Boolean(true));
		criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_ACTIVE),new Boolean(true));
		return getAccounts(criteria,pojo);
	}
	public List<SelectItem> getFinancialExpensesAccounts() throws ManagerBeanException, ExpressionException {
		return getFinancialExpensesAccounts(true);
	}
	public List<SelectItem> getFinancialExpensesAccountsIds() throws ManagerBeanException, ExpressionException {
		return getFinancialExpensesAccounts(false);
	}
	
	private List<SelectItem> getAssetLostAccounts(boolean pojo) throws ManagerBeanException, ExpressionException {
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_CODE), "67*");
		criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_ENABLED),new Boolean(true));
		criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_ACTIVE),new Boolean(true));
		return getAccounts(criteria,pojo);
	}
	public List<SelectItem> getAssetLostAccounts() throws ManagerBeanException, ExpressionException {
		return getAssetLostAccounts(true);
	}
	public List<SelectItem> getAssetLostAccountsIds() throws ManagerBeanException, ExpressionException {
		return getAssetLostAccounts(false);
	}
	
	private List<SelectItem> getAssetProfitAccounts(boolean pojo) throws ManagerBeanException, ExpressionException {
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_CODE), "77*");
		criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_ENABLED),new Boolean(true));
		criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_ACTIVE),new Boolean(true));
		return getAccounts(criteria,pojo);
	}
	public List<SelectItem> getAssetProfitAccounts() throws ManagerBeanException, ExpressionException {
		return getAssetProfitAccounts(true);
	}
	public List<SelectItem> getAssetProfitAccountsIds() throws ManagerBeanException, ExpressionException {
		return getAssetProfitAccounts(false);
	}
	
	private List<SelectItem> getPrepaymentAccounts(boolean pojo) throws ManagerBeanException, ExpressionException {
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_CODE), "55*");
		criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_ENABLED),new Boolean(true));
		criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_ACTIVE),new Boolean(true));
		return getAccounts(criteria,pojo);
	}
	public List<SelectItem> getPrepaymentAccounts() throws ManagerBeanException, ExpressionException {
		return getPrepaymentAccounts(true);
	}
	public List<SelectItem> getPrepaymentAccountsIds() throws ManagerBeanException, ExpressionException {
		return getPrepaymentAccounts(false);
	}
	
	public List<SelectItem> getCostCenters() {
		 try {
			IManagerBean bean = BeanManager.getManagerBean(ApplicationParameter.class);
			String alias = bean.getFieldName(IEntityAlias.APPLICATION_PARAMETER_NAME);
	        Criteria criteria = new Criteria();
	        Expression exp = ExpressionUtilities.getLikeExpression(alias, IAccountConstants.COST_CENTER_LIKE_PREFIX); 
    		criteria.addExpression(exp);		
	        criteria.addOrder(bean.getFieldName(IEntityAlias.APPLICATION_PARAMETER_VALUE));
			List<SelectItem> list = new LinkedList<SelectItem>();
			List<ITransferObject> costCenters = bean.getList(criteria); 
			for (ITransferObject to : costCenters ) {
				ApplicationParameter app = (ApplicationParameter) to;
				SelectItem item = new SelectItem(app.getValue(), app.getValue());
				list.add(item);
			}
			return list;
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("Imposible recuperar los centros de costo.");
			return new LinkedList<SelectItem>();
		}
	}
}


