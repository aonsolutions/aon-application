package com.esferalia.aon.ui.payroll.controller.batch;

import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.ui.form.FormUtil;
import com.esferalia.aon.payroll.enumeration.FileStatus;
import com.esferalia.aon.payroll.enumeration.LeaveBatchAttachmentType;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;

public class ContractBatchAttachController extends BatchAttachController {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(LeaveBatchAttachController.class);
	private final static String QUERY = "select sum(length(data)) from ContractBatchAttachment";

	private LeaveBatchAttachmentType type;
	
	public LeaveBatchAttachmentType getType() {
		return type;
	}
	
	public void setType(LeaveBatchAttachmentType type) {
		this.type = type;
	}
	
	@Override
	public void onRemove(ActionEvent event) {
		super.onRemove(event);
		ContractBatchController controller = (ContractBatchController) FormUtil.getController(IPayrollConstants.CONTRACT_BATCH_CONTROLLER_NAME);
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
