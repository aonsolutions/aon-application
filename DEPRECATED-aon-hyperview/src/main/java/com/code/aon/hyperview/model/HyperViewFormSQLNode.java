package com.code.aon.hyperview.model;

import com.code.aon.hyperview.player.ViewResult;

public class HyperViewFormSQLNode extends HyperViewSQLNode {
	ViewResult result;

	public HyperViewFormSQLNode() {
		
	}
	public ViewResult getResult() {
		return result;
	}

	public void setResult(ViewResult result) {
		this.result = result;
	}

	public boolean isListView() {
		return false;
	}

	public boolean isFormView() {
		return true;
	}
	
	public boolean isImageView() {
		return false;
	}
	
	public int getColumnNumber() {
		int r = 0;
		if (result != null) {
			Object[] values = result.getValues();
			if (values != null) {
				r = values.length / 10;
			}
		}
		return r>0?r:1;
	}
}
