package com.code.aon.ui.accounting.controller;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.accounting.util.VatManager;
import com.code.aon.accounting.util.VatManagerParams;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.ui.util.AonUtil;

public class VatRegeneratorController {
	private boolean validated;
	private VatManagerParams params;

	public VatManagerParams getParams() {
		return params;
	}
	public void setParams(VatManagerParams params) {
		this.params = params;
	}

	public boolean isValidated() {
		return validated;
	}
	public void setValidated(boolean validated) {
		this.validated = validated;
	}

	public void onEditSearch(ActionEvent event) {
		setParams( new VatManagerParams() );
		getParams().setPeriod(null);
		getParams().setSecurityLevel(SecurityLevel.OFFICIAL);
		getParams().setValid(false);
		getParams().setCount(0);
		setValidated(false);
	}
	public void validate(ActionEvent event) {
		try {
			setValidated(true);
			VatManager vm = new VatManager();
			getParams().setValid( vm.validateVAT(getParams()) );
		} catch (ManagerBeanException e) {
			String msg = "- Se produjeron errores al regenerar el número en Facturas de IVA Soportado.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}

	public void regenerate(ActionEvent event) {
		try {
			VatManager vm = new VatManager();
			vm.regenerateVAT(getParams());
			AonUtil.addInfoMessage("- Los número en Facturas de IVA Soportado se han regenerado correctamente.");
		} catch (ManagerBeanException e) {
			String msg = "- Se produjeron errores al regenerar el número en Facturas de IVA Soportado.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		} 
		onEditSearch(event);
	}

}
