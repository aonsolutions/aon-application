package com.code.aon.cms.util;

import com.code.aon.common.ITransferObject;


public interface IActivableObject extends ITransferObject {

	public boolean isActive();
	public void setActive(boolean active);

}
