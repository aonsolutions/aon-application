package com.esferalia.aon.ui.payroll.controller.batch;

import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.ui.form.FormUtil;
import com.esferalia.aon.payroll.enumeration.FileStatus;
import com.esferalia.aon.payroll.enumeration.PayrollBatchAttachmentType;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;

public class ContrataBatchAttachController extends BatchAttachController {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(ContrataBatchAttachController.class);
	private final static String QUERY = "select sum(length(data)) from ContrataBatchAttachment";
	
	@Override
	public void onRemove(ActionEvent event) {
		super.onRemove(event);
		ContrataBatchController controller = (ContrataBatchController) FormUtil.getController(IPayrollConstants.CONTRATA_BATCH_CONTROLLER_NAME);
		controller.changeBatchStatus(FileStatus.PENDING);
		controller.setRecorded(false);
	}

	@Override
	protected Logger getLogger() {
		return LOGGER;
	}

	@Override
	protected String getQuery() {
		return QUERY;
	}
	
}
