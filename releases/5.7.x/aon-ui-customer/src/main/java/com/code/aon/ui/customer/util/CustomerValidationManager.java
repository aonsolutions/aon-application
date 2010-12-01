package com.code.aon.ui.customer.util;

import java.util.Locale;

import javax.faces.context.FacesContext;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.customer.enumeration.CustomerStatus;
import com.code.aon.registry.RegistryNote;
import com.code.aon.ui.registry.util.RegistryValidationManager;
import com.code.aon.ui.util.AonUtil;

public class CustomerValidationManager extends RegistryValidationManager {

	public boolean isBlocked(ITransferObject to) {
		if (to instanceof Customer) {
			Customer customer = (Customer)to;
			if (customer.getStatus() == CustomerStatus.BLOCKED) {
				String comments = "";
				try {
					RegistryNote observation = getRegistryObservation(customer.getRegistry());
					comments = (observation != null) ? observation.getComments() : comments;
				} catch (ManagerBeanException e) {
					// Si falla, no saldrá el mensaje en pantalla. Se desprecia el error a posta.
				}
				Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
				AonUtil.addErrorMessage("[" + CustomerStatus.BLOCKED.getName(locale) + "] : " + comments);
				return true;
			}
		}
		return false;
	}

}
