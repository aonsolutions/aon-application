package com.code.aon.ui.sign.controller;

import com.code.aon.common.IAttachment;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.ui.form.IController;

public interface ISignatureController extends IController {

	boolean isSigned( ITransferObject to );
	
	void setSigned( ITransferObject to, boolean value );

	IAttachment newAttachment( ITransferObject parent );
	
	String getDescription( ITransferObject parent );
	
	IManagerBean getAttachmentBean();

	String getAttachmentMimeTypeAlias();
	
	String getAttachmentParentAlias();
	
	byte[] getReportData( ITransferObject to );
	
}
