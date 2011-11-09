package com.esferalia.aon.ui.payroll.controller.batch;

import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.ui.form.FormUtil;
import com.esferalia.aon.payroll.enumeration.Certifica2BatchAttachmentType;
import com.esferalia.aon.payroll.enumeration.FileStatus;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;

public class Certifica2BatchAttachController extends BatchAttachController {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(Certifica2BatchAttachController.class);
	private final static String QUERY = "select sum(length(data)) from Certifica2BatchAttachment";

	private Certifica2BatchAttachmentType type;
	
	public Certifica2BatchAttachmentType getType() {
		return type;
	}
	
	public void setType(Certifica2BatchAttachmentType type) {
		this.type = type;
	}
	
	@Override
	public void onRemove(ActionEvent event) {
		super.onRemove(event);
		Certifica2BatchController controller = (Certifica2BatchController) FormUtil.getController(IPayrollConstants.CERTIFICA2_BATCH_CONTROLLER_NAME);
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
