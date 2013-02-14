package com.esferalia.aon.ui.payroll.controller.batch;

import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.ui.form.FormUtil;
import com.esferalia.aon.payroll.enumeration.FileStatus;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;

public class LeaveBatchAttachController extends BatchAttachController {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(LeaveBatchAttachController.class);
	private final static String QUERY = "select sum(length(data)) from LeaveBatchAttachment";
	
	@Override
	public void onRemove(ActionEvent event) {
		super.onRemove(event);
		LeaveBatchController controller = (LeaveBatchController) FormUtil.getController(IPayrollConstants.LEAVE_BATCH_CONTROLLER_NAME);
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
