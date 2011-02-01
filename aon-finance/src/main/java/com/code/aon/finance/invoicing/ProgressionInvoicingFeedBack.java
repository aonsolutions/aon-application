package com.code.aon.finance.invoicing;

import java.util.List;

public class ProgressionInvoicingFeedBack implements IInvoicingFeedBack {

	private int currentRow;
	private int rowCount;

	public void addMessage(String message) {
	}

	@SuppressWarnings("unchecked")
	public List getMessages() {
		return null;
	}

	@Override
	public int getCurrentRow() {
		return currentRow;
	}
	@Override
	public void setCurrentRow(int currentRow) {
		this.currentRow = currentRow;
	}

	@Override
	public int getRowCount() {
		return rowCount;
	}

	@Override
	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}

}
