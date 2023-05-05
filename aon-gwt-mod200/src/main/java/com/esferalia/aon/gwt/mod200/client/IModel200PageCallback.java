package com.esferalia.aon.gwt.mod200.client;

import com.esferalia.aon.gwt.mod200.client.mod200.Model200ModuleOptions;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;

public interface IModel200PageCallback {
	
	public Model200ModuleOptions getOptions();
	public IFiscalModel getModel();
	void markAsDirty();
	public boolean isDirty();	
	public void showError(String msg);

	public String getModelInformationURL();
	public void importAccountingFile();
	public String getExportAccountingAction();
	public String getValidatePrintAction();
	public String getDownloadFileAction();
	public String getSendAction();
	public void sendSuccessfully();
	public String getCheckAction();
	public String getCheckDataResponseDataAction();


}
