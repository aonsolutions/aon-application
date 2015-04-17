package com.code.aon.ui.finance;

import java.util.Locale;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.util.AonUtil;

public enum InvoiceExportType implements IResourceable {
	
	GEYCE(ICommonMessages.FINANCE_INVOICE_EXPORT_GEYCE),
	
	A3(ICommonMessages.FINANCE_INVOICE_EXPORT_A3),
	
	APLIFISA(ICommonMessages.FINANCE_INVOICE_EXPORT_APLIFISA),
	
	LOGIC_WIN(ICommonMessages.FINANCE_INVOICE_EXPORT_LOGIC_WIN),
	
	EXCEL(ICommonMessages.FINANCE_INVOICE_EXPORT_EXCEL),
	
	DSI_GESTION(ICommonMessages.FINANCE_INVOICE_EXPORT_DSI_GESTION),
	
	GLASOF(ICommonMessages.FINANCE_INVOICE_EXPORT_GLASOF),
	
	CONTA_PLUS(ICommonMessages.FINANCE_INVOICE_EXPORT_CONTA_PLUS);

	private String label;
	
	private InvoiceExportType(String label) {
		this.label = label;
	}

	@Override
	public String getName(Locale arg0) {
		return getName();
	}

	public String getName() {
		return AonUtil.getMessage(label);
	}
	
}
