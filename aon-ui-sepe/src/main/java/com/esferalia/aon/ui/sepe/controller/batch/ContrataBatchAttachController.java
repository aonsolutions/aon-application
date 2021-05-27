package com.esferalia.aon.ui.sepe.controller.batch;

import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.ui.form.FormUtil;
import com.esferalia.aon.payroll.ContrataBatchAttachment;
import com.esferalia.aon.payroll.enumeration.FileStatus;
import com.esferalia.aon.payroll.enumeration.SepeBatchAttachmentType;
import com.esferalia.aon.ui.sepe.controller.ISepeConstants;

public class ContrataBatchAttachController extends SEPEBatchAttachController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(ContrataBatchAttachController.class);
	private final static String QUERY = "select sum(length(data)) from ContrataBatchAttachment";

	@Override
	public void onRemove(ActionEvent event) {
		ContrataBatchAttachment attach = (ContrataBatchAttachment)this.getTo();
		if(attach.getAttachmentType() == SepeBatchAttachmentType.GENERATED_FILE){
			ContrataBatchController batchController = (ContrataBatchController) FormUtil.getController(ISepeConstants.CONTRATA_BATCH_CONTROLLER_NAME);
			batchController.changeBatchStatus(FileStatus.PENDING);
			batchController.setRecorded(false);
		}
		super.onRemove(event);
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
