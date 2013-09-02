package com.esferalia.aon.ui.sepe.controller.batch;

import com.code.aon.ui.form.LinesController;


public class BatchDetailController extends LinesController {
	
	private BatchListCheckHandler checkHandler;
	
	public BatchListCheckHandler getCheckHandler() {
		if(checkHandler == null){
			checkHandler = new BatchListCheckHandler(this);
		}
		return checkHandler;
	}

	public void setCheckHandler(BatchListCheckHandler checkHandler) {
		this.checkHandler = checkHandler;
	}
	
}
