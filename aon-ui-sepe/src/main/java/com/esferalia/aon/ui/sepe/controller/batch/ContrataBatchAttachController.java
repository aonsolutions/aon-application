package com.esferalia.aon.ui.sepe.controller.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContrataBatchAttachController extends SEPEBatchAttachController {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(ContrataBatchAttachController.class);
	private final static String QUERY = "select sum(length(data)) from ContrataBatchAttachment";
	
	@Override
	protected Logger getLogger() {
		return LOGGER;
	}

	@Override
	protected String getQuery() {
		return QUERY;
	}
	
}
