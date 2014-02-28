package com.esferalia.aon.ui.payroll.controller.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LeaveBatchAttachController extends BatchAttachController {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(LeaveBatchAttachController.class);
	private final static String QUERY = "select sum(length(data)) from LeaveBatchAttachment";

	@Override
	protected Logger getLogger() {
		return LOGGER;
	}

	@Override
	protected String getQuery() {
		return QUERY;
	}
	
}
