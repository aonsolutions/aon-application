package com.code.aon.purchase.util;

import com.code.aon.common.ITransferObject;


public interface IEmailControllerListener {
	
	
	void beforeEmailSend(ITransferObject to) ;
	
	
}
