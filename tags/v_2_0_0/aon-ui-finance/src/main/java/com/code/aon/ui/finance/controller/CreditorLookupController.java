package com.code.aon.ui.finance.controller;

import java.util.Map;

import com.code.aon.common.ITransferObject;
import com.code.aon.finance.Creditor;
import com.code.aon.ui.form.LookupController;

public class CreditorLookupController extends LookupController {
	
	/** The Constant CREDITOR_FULL_NAME used to retrieve the complete name of the supplier using the lookup. */
	private static final String CREDITOR_FULL_NAME = "Creditor_full_name";

	@Override
	protected void customizeLookupMap(ITransferObject to, Map<String, Object> map) {
		Creditor creditor = (Creditor)to;
		map.put(CREDITOR_FULL_NAME, creditor.getRegistry().getName() + " " + ((creditor.getRegistry().getSurname() == null) ? "" : creditor.getRegistry().getSurname()) );
	}
}
