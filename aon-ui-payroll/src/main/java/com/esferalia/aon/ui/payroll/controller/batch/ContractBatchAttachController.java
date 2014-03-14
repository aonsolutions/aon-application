package com.esferalia.aon.ui.payroll.controller.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.AonVersion;

public class ContractBatchAttachController extends BatchAttachController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(LeaveBatchAttachController.class);
	private final static String QUERY = "select sum(length(data)) from ContractBatchAttachment";
	
	@Override
	protected Logger getLogger() {
		return LOGGER;
	}

	@Override
	protected String getQuery() {
		return QUERY;
	}
		
}
