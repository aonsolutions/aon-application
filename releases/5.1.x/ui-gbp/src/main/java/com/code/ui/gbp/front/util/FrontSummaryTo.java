package com.code.ui.gbp.front.util;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.enumeration.IResourceable;

public class FrontSummaryTo implements ITransferObject {

	private IResourceable status;
	
	private int count;

	public IResourceable getStatus() {
		return status;
	}

	public void setStatus(IResourceable status) {
		this.status = status;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
}