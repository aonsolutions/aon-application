package com.esferalia.aon.ui.payroll.controller.batch;

import javax.faces.event.ActionEvent;

import com.esferalia.aon.ui.payroll.controller.batch.ContrataBatchController.BatchFileType;


public class ContrataListController extends ContractListController {

	private BatchFileType fileType;
	
	public BatchFileType getFileType() {
		return fileType;
	}

	public void setFileType(BatchFileType fileType) {
		this.fileType = fileType;
	}

	@Override
	public void onSearch(ActionEvent event) {
		super.onSearch(event);
	}

}
