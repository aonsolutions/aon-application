package com.code.aon.ui.commercial.controller;

import java.util.Date;
import java.util.Iterator;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.commercial.Expense;
import com.code.aon.commercial.ExpenseAccount;
import com.code.aon.commercial.ExpenseAccountDetail;
import com.code.aon.commercial.Offer;
import com.code.aon.commercial.OfferDetail;
import com.code.aon.commercial.dao.ICommercialAlias;
import com.code.aon.commercial.enumeration.OfferDetailStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Tariff;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.product.strategy.ICalculable;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.sales.SalesDetail;
import com.code.aon.sales.dao.ISalesAlias;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;

public class ExpenseAccountDetailController extends LinesController {



	public void onItemChanged(LookupChangeEvent event) {
		ExpenseAccountDetail expenseAccountDetail = (ExpenseAccountDetail)getTo();
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			Expense expense = (Expense)event.getNewValue();
			expenseAccountDetail.setExpense(expense);
			expenseAccountDetail.setPrice(expense.getUnitPrice());
			expenseAccountDetail.setQuantity(1);
			expenseAccountDetail.setAmount(expenseAccountDetail.getPrice()*expenseAccountDetail.getQuantity());
		}
		 
	}	

	public void onQuantityChanged(ValueChangeEvent event) {
		ExpenseAccountDetail expenseAccountDetail = (ExpenseAccountDetail)getTo();
		
			if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
				expenseAccountDetail.setQuantity((Integer)event.getNewValue());
				expenseAccountDetail.setAmount(expenseAccountDetail.getPrice()*expenseAccountDetail.getQuantity());
			}
			
		
	}
	
	public void onUnitPriceChanged(ValueChangeEvent event) {
		ExpenseAccountDetail expenseAccountDetail = (ExpenseAccountDetail)getTo();
		
			if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
				expenseAccountDetail.setPrice((Double)event.getNewValue());
				expenseAccountDetail.setAmount(expenseAccountDetail.getPrice()*expenseAccountDetail.getQuantity());
			}
			
		
	}

	
	@Override
	public void onReset(ActionEvent event) {
		// TODO Auto-generated method stub
		super.onReset(event);
		
		((ExpenseAccountDetail)this.getTo()).setQuantity(0);
		((ExpenseAccountDetail)this.getTo()).setAmount(0.00);
	}
	
	public Double getTotalAmount() throws ManagerBeanException {
		
		IManagerBean expenseBean = BeanManager.getManagerBean(ExpenseAccountDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(expenseBean.getFieldName(ICommercialAlias.EXPENSE_ACCOUNT_DETAIL_EXPENSE_ACCOUNT_ID),((ExpenseAccount)this.getMasterController().getTo()).getId());
		Projection projection = Projection.sum(expenseBean.getFieldName(ICommercialAlias.EXPENSE_ACCOUNT_DETAIL_AMOUNT));
		Object value = expenseBean.getUniqueResult(projection, criteria);
		if (value != null) {
			return (Double)value;
		}else return 0.0;
	
	}	

}