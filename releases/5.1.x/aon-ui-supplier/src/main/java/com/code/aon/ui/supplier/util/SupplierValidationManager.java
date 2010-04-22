package com.code.aon.ui.supplier.util;

import java.util.Locale;

import javax.faces.context.FacesContext;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.registry.RegistryNote;
import com.code.aon.supplier.Supplier;
import com.code.aon.supplier.enumeration.SupplierStatus;
import com.code.aon.ui.registry.util.RegistryValidationManager;
import com.code.aon.ui.util.AonUtil;

public class SupplierValidationManager extends RegistryValidationManager {

	public boolean isBlocked(ITransferObject to) {
		if (to instanceof Supplier) {
			Supplier supplier = (Supplier)to;
			if (supplier.getStatus() == SupplierStatus.BLOCKED) {
				String comments = "";
				try {
					RegistryNote observation = getRegistryObservation(supplier.getRegistry());
					comments = (observation != null) ? observation.getComments() : comments;
				} catch (ManagerBeanException e) {
					// Si falla, no saldrá el mensaje en pantalla. Se desprecia el error a posta.
				}
				Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
				AonUtil.addErrorMessage("[" + SupplierStatus.BLOCKED.getName(locale) + "] : " + comments);
				return true;
			}
		}
		return false;
	}

}
