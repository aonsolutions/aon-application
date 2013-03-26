package com.code.aon.ui.fiscal.controller.mod131;

import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.file.tax.model.MOD131.MOD131Format;
import com.code.aon.fiscal.FiscalModel;
import com.code.aon.fiscal.enumeration.FiscalModelType;
import com.code.aon.ui.finance.IFinanceMessages;
import com.code.aon.ui.fiscal.controller.model.FiscalModelController;
import com.code.aon.ui.fiscal.file.MOD131Writer;
import com.code.aon.ui.util.AonUtil;

public class Mod131Controller extends FiscalModelController {
	
	@Override
	protected FiscalModelType getModelType() {
		return 	FiscalModelType.M131;
	}

	@Override
	public boolean isDifEnabled() {
		return false;
	}
	
	@Override
	public void onEditSearch(ActionEvent event) {
		checkFiscalActivity(FiscalModelType.M131);
		super.onEditSearch(event);	
	}
	
	public void onCreateDisk(ActionEvent event) {
		try { 
			MOD131Writer mod131Writer = new MOD131Writer();
			FiscalModel fm = (FiscalModel) getTo();
			List<FiscalModel> list = new LinkedList<FiscalModel>();
			list.add(fm);
			MOD131Format format = MOD131Format.getFormat(fm.getAdministration(), fm.getYear());
			setFileOutput( mod131Writer.createMOD131(list, format) );
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
		return "MOD131" + fm.getYear() + fm.getPeriod();
	}
	
	@Override
	public MimeType getMimeType() {
		FiscalModel fm = (FiscalModel) getTo();
		MOD131Format format = MOD131Format.getFormat(fm.getAdministration(), fm.getYear());
		return format.getMimeType();
	}	
	
}
