package com.esferalia.aon.ui.sepe.controller.batch;

import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.AonVersion;
import com.code.aon.ui.form.FormUtil;
import com.esferalia.aon.payroll.Certifica2BatchAttachment;
import com.esferalia.aon.payroll.enumeration.FileStatus;
import com.esferalia.aon.payroll.enumeration.SepeBatchAttachmentType;
import com.esferalia.aon.ui.sepe.controller.ISepeConstants;

public class Certifica2BatchAttachController extends SEPEBatchAttachController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(Certifica2BatchAttachController.class);
	private final static String QUERY = "select sum(length(data)) from Certifica2BatchAttachment";
	
	@Override
	public void onRemove(ActionEvent event) {
		Certifica2BatchAttachment attach = (Certifica2BatchAttachment)this.getTo();
		if(attach.getAttachmentType() == SepeBatchAttachmentType.GENERATED_FILE){
			Certifica2BatchController batchController = (Certifica2BatchController) FormUtil.getController(ISepeConstants.CERTIFICA2_BATCH_CONTROLLER_NAME);
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
