package com.esferalia.aon.ui.payroll.controller.batch;

import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.ui.form.FormUtil;
import com.esferalia.aon.payroll.enumeration.FanBatchAttachmentType;
import com.esferalia.aon.payroll.enumeration.FileStatus;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;

public class FanBatchAttachController extends BatchAttachController {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(FanBatchAttachController.class);
	private final static String QUERY = "select sum(length(data)) from FanBatchAttachment";

	private FanBatchAttachmentType type;
	
	public FanBatchAttachmentType getType() {
		return type;
	}
	
	public void setType(FanBatchAttachmentType type) {
		this.type = type;
	}
	
	@Override
	public void onRemove(ActionEvent event) {
		super.onRemove(event);
		FanBatchController controller = (FanBatchController) FormUtil.getController(IPayrollConstants.FAN_BATCH_CONTROLLER_NAME);
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
