package com.code.aon.faces.controller;

import com.code.aon.common.IAttachment;
import com.code.aon.ui.common.io.AonFile;

public interface IAttachmentController {

	IAttachment getAttachment();
	
	AonFile getAonFile();
	
	void setAonFile(AonFile aonFile);

	long getMaximumSize();
	
}
