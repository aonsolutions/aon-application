package com.code.aon.ui.account.event;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.account.AccountEntryDetail;
import com.code.aon.account.dao.IAccountAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.account.controller.AccountEntryReportController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class AccountEntryReportControllerListener extends ControllerAdapter {
	
	private static final Logger LOGGER = Logger.getLogger(AccountEntryReportControllerListener.class.getName());
	
	@Override
	public void afterBeanReset(ControllerEvent event) throws ControllerListenerException {
		try {
			AccountEntryReportController accountEntryDetailController = (AccountEntryReportController)event.getController();
			IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
			if(accountEntryDetailController.isAbstractMode()){
				accountEntryDetailController.getCriteria().addOrder(accountEntryDetailBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ID));
			}
			if(accountEntryDetailController.isDaybookMode()){
				accountEntryDetailController.getCriteria().addOrder(accountEntryDetailBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ID));
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error seting criteria afterBeanReset", e);
		}
	}
}
