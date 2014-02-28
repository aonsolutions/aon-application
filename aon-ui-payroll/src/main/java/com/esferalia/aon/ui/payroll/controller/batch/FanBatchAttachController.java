package com.esferalia.aon.ui.payroll.controller.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FanBatchAttachController extends BatchAttachController {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(FanBatchAttachController.class);
	private final static String QUERY = "select sum(length(data)) from FanBatchAttachment";
	
	@Override
	protected Logger getLogger() {
		return LOGGER;
	}

	@Override
	protected String getQuery() {
		return QUERY;
	}
	
}
