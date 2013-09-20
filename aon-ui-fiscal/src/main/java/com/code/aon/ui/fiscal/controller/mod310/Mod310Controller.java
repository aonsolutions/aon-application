package com.code.aon.ui.fiscal.controller.mod310;

import static com.code.aon.ui.common.ICommonMessages.FINANCE_BATCH_DISK_ERROR;

import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.file.tax.model.MOD310.MOD310Format;
import com.code.aon.fiscal.FiscalModel;
import com.code.aon.fiscal.enumeration.FiscalModelType;
import com.code.aon.ui.fiscal.controller.model.FiscalModelController;
import com.code.aon.ui.fiscal.file.MOD310Writer;
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
		try { 
			MOD310Writer mod130Writer = new MOD310Writer();
			FiscalModel fm = (FiscalModel) getTo();
			List<FiscalModel> list = new LinkedList<FiscalModel>();
			list.add(fm);
			MOD310Format format = MOD310Format.getFormat(fm.getAdministration(), fm.getYear());
			setFileOutput( mod130Writer.createMOD310(list, format) );
		    if (getFileOutput() != null) {
		    	if (getFileOutput().getErrors().size() > 0) {
		    		AonUtil.addErrorMessageFromBundle(FINANCE_BATCH_DISK_ERROR);
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
		MOD310Format format = MOD310Format.getFormat(fm.getAdministration(), fm.getYear());
		return format.getMimeType();
	}	
	
}
