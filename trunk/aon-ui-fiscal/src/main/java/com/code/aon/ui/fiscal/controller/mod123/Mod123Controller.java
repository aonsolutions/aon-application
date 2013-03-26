package com.code.aon.ui.fiscal.controller.mod123;

import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.file.tax.model.MOD123.MOD123Format;
import com.code.aon.fiscal.FiscalModel;
import com.code.aon.fiscal.enumeration.FiscalModelType;
import com.code.aon.ui.finance.IFinanceMessages;
import com.code.aon.ui.fiscal.controller.model.FiscalModelController;
import com.code.aon.ui.fiscal.file.MOD123Writer;
import com.code.aon.ui.util.AonUtil;

public class Mod123Controller extends FiscalModelController {
	
	@Override
	protected FiscalModelType getModelType() {
		return 	FiscalModelType.M123;
	}

	@Override
	public boolean isDifEnabled() {
		return true;
	}
	
	public void onCreateDisk(ActionEvent event) {
		try { 
			MOD123Writer mod123Writer = new MOD123Writer();
			FiscalModel fm = (FiscalModel) getTo();
			List<FiscalModel> list = new LinkedList<FiscalModel>();
			list.add(fm);
			MOD123Format format = MOD123Format.getFormat(fm.getAdministration(), fm.getYear());
			setFileOutput( mod123Writer.createMOD123(list, format) );
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
		return "MOD123" + fm.getYear() + fm.getPeriod();
	}
	
	@Override
	public MimeType getMimeType() {
		FiscalModel fm = (FiscalModel) getTo();
		MOD123Format format = MOD123Format.getFormat(fm.getAdministration(), fm.getYear());
		return format.getMimeType();
	}	
	
}
