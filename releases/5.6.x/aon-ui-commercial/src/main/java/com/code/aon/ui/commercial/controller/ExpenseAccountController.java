package com.code.aon.ui.commercial.controller;

import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;

import com.code.aon.commercial.ExpenseAccount;
import com.code.aon.commercial.ExpenseAccountDetail;
import com.code.aon.commercial.dao.ICommercialAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ui.form.BasicController;


public class ExpenseAccountController extends BasicController {

	private List<ITransferObject> selectedList;
	
	public List<ITransferObject> getSelectedList() {
		return selectedList;
	}

	public void setSelectedList(List<ITransferObject> selectedList) {
		this.selectedList = selectedList;
	}
	
	@Override
	public void onSelect(ActionEvent event) {
	selectedList = new LinkedList<ITransferObject>();
		selectedList.add(this.getTo());			
	
		super.onSelect(event);		
}
	
	public Double getTotalAmount() throws ManagerBeanException {
		
		IManagerBean expenseBean = BeanManager.getManagerBean(ExpenseAccountDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(expenseBean.getFieldName(ICommercialAlias.EXPENSE_ACCOUNT_DETAIL_EXPENSE_ACCOUNT_ID),((ExpenseAccount)this.getTo()).getId());
		Projection projection = Projection.sum(expenseBean.getFieldName(ICommercialAlias.EXPENSE_ACCOUNT_DETAIL_AMOUNT));
		Object value = expenseBean.getUniqueResult(projection, criteria);
		if (value != null) {
			return (Double)value;
		}else return 0.0;
	
	}
	
	public double getExpenseTotalAmount() throws ManagerBeanException{
		ExpenseAccount expenseAccount = (ExpenseAccount)this.getModel().getRowData();
		IManagerBean expenseBean = BeanManager.getManagerBean(ExpenseAccountDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(expenseBean.getFieldName(ICommercialAlias.EXPENSE_ACCOUNT_DETAIL_EXPENSE_ACCOUNT_ID),expenseAccount.getId());
		Projection projection = Projection.sum(expenseBean.getFieldName(ICommercialAlias.EXPENSE_ACCOUNT_DETAIL_AMOUNT));
		Object value = expenseBean.getUniqueResult(projection, criteria);
		if (value != null) {
			return (Double)value;
		}else return 0.0;
		
		
	}


}
