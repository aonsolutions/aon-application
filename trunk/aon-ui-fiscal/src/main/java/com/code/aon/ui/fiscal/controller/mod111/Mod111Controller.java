package com.code.aon.ui.fiscal.controller.mod111;

import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.file.tax.model.MOD111.MOD111Format;
import com.code.aon.fiscal.FiscalModel;
import com.code.aon.fiscal.enumeration.FiscalModelType;
import com.code.aon.ui.finance.IFinanceMessages;
import com.code.aon.ui.fiscal.controller.model.FiscalModelController;
import com.code.aon.ui.fiscal.file.MOD111Writer;
import com.code.aon.ui.util.AonUtil;

public class Mod111Controller extends FiscalModelController {

	@Override
	protected FiscalModelType getModelType() {
		return 	FiscalModelType.M111;
	}
	
	@Override
	public boolean isDifEnabled() {
		return true;
	}

	public void onCreateDisk(ActionEvent event) {
		try { 
			MOD111Writer mod111Writer = new MOD111Writer();
			FiscalModel fm = (FiscalModel) getTo();
			List<FiscalModel> list = new LinkedList<FiscalModel>();
			list.add(fm);
			MOD111Format format = MOD111Format.getFormat(fm.getAdministration(), fm.getYear());
			setFileOutput( mod111Writer.createMOD111(list, format) );
		    if (getFileOutput() != null) {
		    	if (getFileOutput().getErrors().size() > 0) {
		    		AonUtil.addErrorMessageFromBundle(IFinanceMessages.BUNDLE_KEY, IFinanceMessages.FINANCE_BATCH_DISK_ERROR);
		        }
		    }
		} catch (IllegalArgumentException e) {
			AonUtil.addErrorMessage(e.getMessage()); 
			throw new AbortProcessingException(e.getMessage(),e);
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage()); 
			throw new AbortProcessingException(e.getMessage(),e);
		}
	}

	public String getFileName() {
		FiscalModel fm = (FiscalModel) getTo();
		return "MOD111" + fm.getYear() + fm.getPeriod();
	}
	
	@Override
	public MimeType getMimeType() {
		FiscalModel fm = (FiscalModel) getTo();
		MOD111Format format = MOD111Format.getFormat(fm.getAdministration(), fm.getYear());
		return format.getMimeType();
	}	
	
}
