package com.code.aon.hyperview.model;

import java.util.List;

import com.code.aon.hyperview.player.ViewResult;

public class HyperViewListSQLNode extends HyperViewSQLNode {
	List<ViewResult> queryResult;

	public List<ViewResult> getQueryResult() {
		return queryResult;
	}

	public void setQueryResult(List<ViewResult> queryResult) {
		this.queryResult = queryResult;
	}
	
	public boolean isListView() {
		return true;
	}

	public boolean isFormView() {
		return false;
	}
	
	public boolean isImageView() {
		return false;
	}

	public int getRowCount() {
		if (queryResult != null) {
			return queryResult.size();
		}
		return 0;
	}
}
