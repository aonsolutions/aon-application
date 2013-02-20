package com.code.aon.ui.fiscal.controller.mod310;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.common.enumeration.MimeType;
import com.code.aon.fiscal.FiscalModel;
import com.code.aon.fiscal.enumeration.FiscalModelType;
import com.code.aon.ui.fiscal.controller.model.FiscalModelController;
import com.code.aon.ui.util.AonUtil;

public class Mod310Controller extends FiscalModelController {
	
	@Override
	protected FiscalModelType getModelType() {
		return 	FiscalModelType.M310;
	}

	@Override
	public void onEditSearch(ActionEvent event) {
		checkFiscalActivity(FiscalModelType.M310);
		super.onEditSearch(event);	
	}

	@Override
	public boolean isDifEnabled() {
		return false;
	}
	
	public void onCreateDisk(ActionEvent event) {
		String msg = "Generación de fichero no disponible";
		AonUtil.addErrorMessage(msg); 
		throw new AbortProcessingException(msg);
//		try { 
//			MOD130Writer mod130Writer = new MOD130Writer();
//			FiscalModel fm = (FiscalModel) getTo();
//			List<FiscalModel> list = new LinkedList<FiscalModel>();
//			list.add(fm);
//			MOD130Format format = MOD130Format.getFormat(fm.getAdministration(), fm.getYear());
//			setFileOutput( mod130Writer.createMOD130(list, format) );
//		    if (getFileOutput() != null) {
//		    	if (getFileOutput().getErrors().size() > 0) {
//		    		AonUtil.addErrorMessageFromBundle(IFinanceMessages.BUNDLE_KEY, IFinanceMessages.FINANCE_BATCH_DISK_ERROR);
//		        }
//		    }
//		} catch (IllegalArgumentException e) {
//			AonUtil.addErrorMessage(e.getMessage()); 
//			throw new AbortProcessingException(e.getMessage(),e);
//		} catch (ManagerBeanException e) {
//			AonUtil.addErrorMessage(e.getMessage()); 
//			throw new AbortProcessingException(e.getMessage(),e);
//		}
	}

	public String getFileName() {
		FiscalModel fm = (FiscalModel) getTo();
		return "MOD310" + fm.getYear() + fm.getPeriod();
	}
	
	@Override
	public MimeType getMimeType() {
//		FiscalModel fm = (FiscalModel) getTo();
//		MOD310Format format = MOD310Format.getFormat(fm.getAdministration(), fm.getYear());
//		return format.getMimeType();
		return null;
	}	
	
}
