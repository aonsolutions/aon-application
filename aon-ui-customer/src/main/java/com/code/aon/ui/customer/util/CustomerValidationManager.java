package com.code.aon.ui.customer.util;

import java.util.Locale;

import javax.faces.context.FacesContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.customer.enumeration.CustomerStatus;
import com.code.aon.registry.RegistryNote;
import com.code.aon.ui.registry.util.RegistryValidationManager;
import com.code.aon.ui.util.AonUtil;

public class CustomerValidationManager extends RegistryValidationManager {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private final static Logger LOGGER = LoggerFactory.getLogger(CustomerValidationManager.class);
	
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
					LOGGER.debug(e.getMessage(), e);
				}
				Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
				AonUtil.addErrorMessage("[" + CustomerStatus.BLOCKED.getName(locale) + "] : " + comments);
				return true;
			}
		}
		return false;
	}

}
