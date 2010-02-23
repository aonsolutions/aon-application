package com.code.aon.ui.finance.util;

import java.util.Locale;

import javax.faces.context.FacesContext;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Creditor;
import com.code.aon.finance.enumeration.CreditorStatus;
import com.code.aon.registry.RegistryNote;
import com.code.aon.ui.registry.util.RegistryValidationManager;
import com.code.aon.ui.util.AonUtil;

public class CreditorValidationManager extends RegistryValidationManager {

	public boolean isBlocked(ITransferObject to) {
		if (to instanceof Creditor) {
			Creditor creditor = (Creditor)to;
			if (creditor.getStatus() == CreditorStatus.BLOCKED) {
				String comments = "";
				try {
					RegistryNote observation = getRegistryObservation(creditor.getRegistry());
					comments = (observation != null) ? observation.getComments() : comments;
				} catch (ManagerBeanException e) {
					// Si falla, no saldrá el mensaje en pantalla. Se desprecia el error a posta.
				}
				Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
				AonUtil.addErrorMessage("[" + CreditorStatus.BLOCKED.getName(locale) + "] : " + comments);
				return true;
			}
		}
		return false;
	}

}
