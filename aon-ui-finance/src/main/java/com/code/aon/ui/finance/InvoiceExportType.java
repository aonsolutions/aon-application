package com.code.aon.ui.finance;

import static com.code.aon.ui.common.ICommonMessages.FINANCE_INVOICE_EXPORT_A3;
import static com.code.aon.ui.common.ICommonMessages.FINANCE_INVOICE_EXPORT_APLIFISA;
import static com.code.aon.ui.common.ICommonMessages.FINANCE_INVOICE_EXPORT_GEYCE;

import java.util.Locale;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.ui.util.AonUtil;

public enum InvoiceExportType implements IResourceable {
	
	GEYCE(FINANCE_INVOICE_EXPORT_GEYCE),
	
	A3(FINANCE_INVOICE_EXPORT_A3),
	
	APLIFISA(FINANCE_INVOICE_EXPORT_APLIFISA);

	private String label;
	
	private InvoiceExportType(String label) {
		this.label = label;
	}

	@Override
	public String getName(Locale arg0) {
		return AonUtil.getMessage(label);
	}

	
}
