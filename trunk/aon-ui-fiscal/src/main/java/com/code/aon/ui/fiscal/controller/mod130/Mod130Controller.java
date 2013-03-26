package com.code.aon.ui.fiscal.controller.mod130;

import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.common.AonException;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.file.tax.model.MOD130.MOD130Format;
import com.code.aon.fiscal.FiscalModel;
import com.code.aon.fiscal.enumeration.FiscalModelType;
import com.code.aon.fiscal.mod130.Mod130;
import com.code.aon.ui.finance.IFinanceMessages;
import com.code.aon.ui.fiscal.controller.FiscalParametersController;
import com.code.aon.ui.fiscal.controller.model.FiscalModelController;
import com.code.aon.ui.fiscal.file.MOD130Writer;
import com.code.aon.ui.util.AonUtil;

public class Mod130Controller extends FiscalModelController {
	
	@Override
	protected FiscalModelType getModelType() {
		return 	FiscalModelType.M130;
	}

	@Override
	public boolean isDifEnabled() {
		return false;
	}
	
	@Override
	public void initialize() throws AonException {
		super.initialize();
		Mod130 mod130 = (Mod130) getDeclaration();
		FiscalParametersController fiscalParams = (FiscalParametersController) AonUtil.getRegisteredBean( FiscalParametersController.FISCAL_PARAMS_BEAN_NAME);
		mod130.setPermanentAddressChanges(fiscalParams.isPermanentAddressChanges());
	}
	
	public void onCreateDisk(ActionEvent event) {
		try { 
			MOD130Writer mod130Writer = new MOD130Writer();
			FiscalModel fm = (FiscalModel) getTo();
			List<FiscalModel> list = new LinkedList<FiscalModel>();
			list.add(fm);
			MOD130Format format = MOD130Format.getFormat(fm.getAdministration(), fm.getYear());
			setFileOutput( mod130Writer.createMOD130(list, format) );
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
		return "MOD130" + fm.getYear() + fm.getPeriod();
	}
	
	@Override
	public MimeType getMimeType() {
		FiscalModel fm = (FiscalModel) getTo();
		MOD130Format format = MOD130Format.getFormat(fm.getAdministration(), fm.getYear());
		return format.getMimeType();
	}	
	
}
