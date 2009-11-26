package com.code.aon.ui.accounting.controller;

import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ui.form.LinesController;

public class AccountEntryDetailController extends LinesController {
	
	public void onBalanceAmount(ActionEvent event) {
		
	}
	
	public void onBalance(ActionEvent event) {
		
	}

	@SuppressWarnings("unchecked")
	public void onAccept(ActionEvent event) {
		try {
			boolean adding = isNew();
			super.onAccept(event);
			if (adding) {
				List<AccountEntryDetail> list = (List <AccountEntryDetail>) getModel().getWrappedData();
				double imp = getBalance(list);
				if (imp != 0.0) {
					super.onReset(event);	
				}
			}
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException(e.getMessage());
		}
		 
	}

	private double getBalance(List<AccountEntryDetail> list) {
		double imp = 0;
		for (AccountEntryDetail detail: list) {
			imp = CommonUtil.round(imp + detail.getDebit());
			imp = CommonUtil.round(imp - detail.getCredit());
		}
		return imp;
	}
	
}
