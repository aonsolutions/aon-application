package com.code.aon.ui.product.controller;

import java.util.Map;

import com.code.aon.common.ITransferObject;
import com.code.aon.ui.form.LookupController;

public class RetentionLookupController extends LookupController {
	
	private static final String RETENTION_PERCENTAGE = "Tax_percentage";

	@Override
	protected void customizeLookupMap(ITransferObject to, Map<String, Object> map) {
		if(map.get(RETENTION_PERCENTAGE) == null || "".equals(map.get(RETENTION_PERCENTAGE).toString())){
			map.put(RETENTION_PERCENTAGE, 0.0);
		}
	}
}
