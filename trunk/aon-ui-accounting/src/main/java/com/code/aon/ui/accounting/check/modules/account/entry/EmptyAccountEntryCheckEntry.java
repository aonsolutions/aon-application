package com.code.aon.ui.accounting.check.modules.account.entry;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.accounting.AccountEntry;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.accounting.check.AonCheckException;
import com.code.aon.ui.accounting.check.CheckEntryAdapter;
import com.code.aon.ui.util.AonUtil;

public class EmptyAccountEntryCheckEntry extends CheckEntryAdapter {

	private boolean fixed = false;
	private String fixLabel = "Borrar Apunte";

	@Override
	public void onFix(ActionEvent event) throws AonCheckException{
		try {
			AccountEntry entry = (AccountEntry) getTo();
			IManagerBean bean = BeanManager.getManagerBean(AccountEntry.class);
			bean.remove(entry);
			fixed = true;
		} catch (ManagerBeanException e) {
			String message = "No se pudo borrar el apunte contable. [" + e.getMessage() + "]";
			AonUtil.addErrorMessage(message);
			throw new AbortProcessingException(message,e);
		}
	}

	@Override
	public boolean isFixAvailable() {
		return true;
	}

	@Override
	public boolean isFixed() {
		return fixed;
	}

	@Override
	public String getMessage() {
		AccountEntry entry = (AccountEntry) getTo();
		return super.getMessage() + "(" + entry.getId() + ")";	
	}

	@Override
	public String getFixActionLabel() {
		return fixLabel;
	}

}
