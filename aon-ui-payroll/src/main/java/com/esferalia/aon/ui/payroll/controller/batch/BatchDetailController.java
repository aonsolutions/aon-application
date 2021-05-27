package com.esferalia.aon.ui.payroll.controller.batch;

import java.util.List;

import com.code.aon.AonVersion;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.LinesController;


public class BatchDetailController extends LinesController implements BatchListController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
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

	@Override
	public List<ITransferObject> getAllList() throws ManagerBeanException {
		return this.getManagerBean().getList(this.getCriteria());
	}
	
}
