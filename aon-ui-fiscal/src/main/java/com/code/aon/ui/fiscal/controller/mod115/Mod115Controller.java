package com.code.aon.ui.fiscal.controller.mod115;

import static com.code.aon.ui.common.ICommonMessages.FINANCE_BATCH_DISK_ERROR;
import static com.code.aon.ui.common.ICommonMessages.FINANCE_BUNDLE;

import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.file.tax.model.MOD115.MOD115Format;
import com.code.aon.fiscal.FiscalModel;
import com.code.aon.fiscal.enumeration.FiscalModelType;
import com.code.aon.ui.fiscal.controller.model.FiscalModelController;
import com.code.aon.ui.fiscal.file.MOD115Writer;
import com.code.aon.ui.util.AonUtil;

public class Mod115Controller extends FiscalModelController {

	@Override
	protected FiscalModelType getModelType() {
		return 	FiscalModelType.M115;
	}

	@Override
	public boolean isDifEnabled() {
		return true;
	}
	
	public void onCreateDisk(ActionEvent event) {
		try { 
			MOD115Writer mod115Writer = new MOD115Writer();
			FiscalModel fm = (FiscalModel) getTo();
			List<FiscalModel> list = new LinkedList<FiscalModel>();
			list.add(fm);
			MOD115Format format = MOD115Format.getFormat(fm.getAdministration(), fm.getYear());
			setFileOutput( mod115Writer.createMOD115(list, format) );
		    if (getFileOutput() != null) {
		    	if (getFileOutput().getErrors().size() > 0) {
		    		AonUtil.addErrorMessageFromBundle(FINANCE_BUNDLE, FINANCE_BATCH_DISK_ERROR);
		        }
		    }
		    if (isAeatValidable()) {
		    	validateAeatFile();	
		    }
		} catch (IllegalArgumentException e) {
			AonUtil.addErrorMessage(e.getMessage()); 
			throw new AbortProcessingException(e.getMessage(),e);
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage()); 
			throw new AbortProcessingException(e.getMessage(),e);
		}
	}
	
	@Override
	public MimeType getMimeType() {
		FiscalModel fm = (FiscalModel) getTo();
		MOD115Format format = MOD115Format.getFormat(fm.getAdministration(), fm.getYear());
		return format.getMimeType();
	}	
	
}
