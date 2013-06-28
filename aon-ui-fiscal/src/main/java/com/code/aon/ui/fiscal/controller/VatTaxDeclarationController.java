package com.code.aon.ui.fiscal.controller;


import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.file.tax.model.MOD303.MOD303Format;
import com.code.aon.fiscal.VatTax;
import com.code.aon.fiscal.VatTaxDeclaration;
import com.code.aon.fiscal.VatTaxDetail;
import com.code.aon.fiscal.enumeration.VatTaxDeclarationStatus;
import com.code.aon.fiscal.enumeration.VatTaxKey;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.RegistryBank;
import com.code.aon.ui.finance.IFinanceMessages;
import com.code.aon.ui.fiscal.controller.model.Mipf;
import com.code.aon.ui.fiscal.file.MOD303Writer;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class VatTaxDeclarationController extends LinesController {
	public static final String BEAN_NAME = "vatTaxDeclaration";

	private FileOutput fileOutput;
	private Mipf mipf;

	private Mipf getMipf() {
		if (this.mipf == null) {
			this.mipf = new Mipf();
		}
		return mipf;
	}

	public FileOutput getFileOutput() {
		return fileOutput;
	}
	public void setFileOutput(FileOutput fileOutput) {
		this.fileOutput = fileOutput;
	}
	
	public void calculate(VatTaxDeclaration dec) {
		VatTaxController master = (VatTaxController) getMasterController();
		dec.setQuota(CommonUtil.round( getDiference(master) * dec.getPercent() / 100 ));
		double t = 0.0;
		if (dec.getResult() > 0){
			dec.setDeposit(dec.getResult());	
			dec.setCompensate(0.0);
			dec.setPayBack(0.0);
			t = dec.getDeposit();
		} else {
			dec.setDeposit(0.0);
			t = CommonUtil.round(dec.getResult() * -1);
			if (dec.isCompensable()) {
				dec.setCompensate(t);
				dec.setPayBack(0.0);
			} else {
				dec.setCompensate(0.0);
				dec.setPayBack(t);
			}
		}
		
		dec.setTotalTaxDebt( CommonUtil.round( t + dec.getExtraCharge() + dec.getDelayInterest()
				- dec.getPreviousDeposit() - dec.getPreviousPayBack() )); 
	}

	public boolean isListEnabled() {
		try {
			return (getModel().getRowCount() > 1);
		} catch (ManagerBeanException e) {
			return true;
		}
	}
	
	private double getDiference(VatTaxController master) {
		double dif = 0.0;
		for (VatTaxDetail detail:master.getSummary()) {
			if (detail.getKey() == VatTaxKey.DF) {
				dif = detail.getQuota();
				break;
			}
		}
		return dif;
	}

	public void calculate(ActionEvent event) {
		VatTaxDeclaration dec = (VatTaxDeclaration) getTo();
		calculate(dec);
	}
	
	public void changeCompensate(ActionEvent event) {
		VatTaxDeclaration dec = (VatTaxDeclaration) getTo();
		dec.setCompensable( !dec.isCompensable() );
		calculate(dec);
	}

	public void tryAutomaticCreation() throws ManagerBeanException {
		VatTaxController master = (VatTaxController) getMasterController();
		VatTax vatTax = (VatTax) master.getTo();
		VatTax previousVatTax = getPreviousVatTax(vatTax);
		if (previousVatTax == null) {
			Administration adm = master.getFiscalParams().getDefaultAdministration();
			createDeclaration(vatTax,adm,100.0,null);
		} else {
			duplicateDeclaration(vatTax,previousVatTax);
		}
		onCancel(null);	
	}

	private void duplicateDeclaration(VatTax vatTax,VatTax previousVatTax) throws ManagerBeanException {
		Criteria criteria = new Criteria();
		criteria.addEqualExpression( getManagerBean().getFieldName(IEntityAlias.VAT_TAX_DECLARATION_VAT_TAX_ID), previousVatTax.getId());
		List<ITransferObject> list = getManagerBean().getList(criteria);
		for (ITransferObject to: list ) {
			VatTaxDeclaration dec = (VatTaxDeclaration) to;
			createDeclaration(vatTax,dec.getAdministration(),dec.getPercent(),dec.getRegistryBank());
		}
		
	}

	private void createDeclaration(VatTax vatTax,Administration adm, double percent,RegistryBank rBank) throws ManagerBeanException {
		onReset(null);
		VatTaxDeclaration dec = (VatTaxDeclaration) getTo();
		dec.setAdministration(adm);
		dec.setPercent(percent);
		dec.setStatus( VatTaxDeclarationStatus.PENDING );
		if (vatTax.isAnual()) {
			fillPreviousData(vatTax,dec);
		} else {
			fillYearPreviousData(vatTax,dec);
		}
		calculate(dec);
		accept(null);
		dec.setRegistryBank(null);	
	}

	private void fillYearPreviousData(VatTax vatTax, VatTaxDeclaration dec) throws ManagerBeanException {
		Criteria criteria = new Criteria();
		criteria.addExpression( ExpressionUtilities.getNotEqualExpression(getManagerBean().getFieldName(IEntityAlias.VAT_TAX_DECLARATION_VAT_TAX_ID), vatTax.getId()));
		criteria.addEqualExpression( getManagerBean().getFieldName(IEntityAlias.VAT_TAX_DECLARATION_VAT_TAX_YEAR), vatTax.getYear());
		criteria.addLessThanExpression( getManagerBean().getFieldName(IEntityAlias.VAT_TAX_DECLARATION_VAT_TAX_PERIOD), vatTax.getPeriod());
		criteria.addEqualExpression( getManagerBean().getFieldName(IEntityAlias.VAT_TAX_DECLARATION_ADMINISTRATION), dec.getAdministration());
		criteria.addOrder( getManagerBean().getFieldName(IEntityAlias.VAT_TAX_DECLARATION_VAT_TAX_PERIOD), false);
		criteria.addOrder( getManagerBean().getFieldName(IEntityAlias.VAT_TAX_DECLARATION_VAT_TAX_REPLACEMENT), false);
		List<ITransferObject> list = getManagerBean().getList(criteria);
		double pycq = 0.0;
		if (list != null && list.size() > 0){
			VatTaxDeclaration vtd = (VatTaxDeclaration) list .get(0);
			pycq = vtd.getCompensate();
		}
		dec.setPreviousYearCompensateQuota(pycq);
	}
	
	private void fillPreviousData(VatTax vatTax,VatTaxDeclaration dec) throws ManagerBeanException {
		Criteria criteria = new Criteria();
		criteria.addExpression( ExpressionUtilities.getNotEqualExpression(getManagerBean().getFieldName(IEntityAlias.VAT_TAX_DECLARATION_VAT_TAX_ID), vatTax.getId()));
		criteria.addEqualExpression( getManagerBean().getFieldName(IEntityAlias.VAT_TAX_DECLARATION_VAT_TAX_YEAR), vatTax.getYear());
		criteria.addEqualExpression( getManagerBean().getFieldName(IEntityAlias.VAT_TAX_DECLARATION_VAT_TAX_PERIOD), vatTax.getPeriod());
		criteria.addEqualExpression( getManagerBean().getFieldName(IEntityAlias.VAT_TAX_DECLARATION_ADMINISTRATION), dec.getAdministration());
		criteria.addOrder( getManagerBean().getFieldName(IEntityAlias.VAT_TAX_DECLARATION_VAT_TAX_NUMBER), false);
		criteria.addOrder( getManagerBean().getFieldName(IEntityAlias.VAT_TAX_DECLARATION_VAT_TAX_REPLACEMENT), false);
		List<ITransferObject> list = getManagerBean().getList(criteria);
		double pd = 0.0;
		double pp = 0.0;
		if (list != null && list.size() > 0){
			VatTaxDeclaration vtd = (VatTaxDeclaration) list .get(0);
			pd = vtd.getDeposit();
			pp = vtd.getPayBack();
		}
		dec.setPreviousDeposit(pd);
		dec.setPreviousPayBack(pp);
	}

	private VatTax getPreviousVatTax(VatTax vatTax) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(VatTax.class);
		Criteria criteria = new Criteria();
		criteria.addNotEqualExpression(bean.getFieldName(IEntityAlias.VAT_TAX_ID), vatTax.getId());
		criteria.addLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.VAT_TAX_YEAR), vatTax.getYear());
		criteria.addOrder(bean.getFieldName(IEntityAlias.VAT_TAX_YEAR), false);
		criteria.addOrder(bean.getFieldName(IEntityAlias.VAT_TAX_PERIOD), false);
		List<ITransferObject> list = bean.getList(criteria);
		if (list != null && list.size() > 0) {
			return (VatTax) list.get(0); 
		}
		return null;
		
	}

	public boolean isDiskOk() {
		int errors = 0;
		if (getFileOutput() != null) {
			errors = getFileOutput().getErrors().size();
		}
		return (errors==0);
	}

	public void onCreateDisk(ActionEvent event) throws ManagerBeanException {
		MOD303Writer mod303Writer = new MOD303Writer();
		VatTaxDeclaration vatTaxDeclaration = (VatTaxDeclaration) getTo();
		List<VatTaxDeclaration> declarations = new LinkedList<VatTaxDeclaration>();
		declarations.add(vatTaxDeclaration);
		setFileOutput( mod303Writer.createMOD303(declarations,getFormat(vatTaxDeclaration)) );
        if (getFileOutput() != null && getFileOutput().getErrors().size() > 0) {
    		AonUtil.addErrorMessageFromBundle(IFinanceMessages.BUNDLE_KEY, IFinanceMessages.FINANCE_BATCH_DISK_ERROR);
    		AonUtil.addErrorMessage("");
    		int i = 0;
    		for (Exception ex:getFileOutput().getErrors()) {
    			AonUtil.addErrorMessage(++i + ") " + ex.getLocalizedMessage());
    		}
        }
	    if (isAeatValidable()) {
	    	validateAeatFile();	
	    }
	}
	
	public boolean isAeatValidable() {
		VatTaxDeclaration to = (VatTaxDeclaration) getTo();
		return ( !isNew() && to.isFromCommonTerritory() && isScriptPresent() );
	}
	private String validateAeatFile() {
		getMipf().validateAeatFile(fileOutput);
		return null;
	}
	
	private MOD303Format getFormat(VatTaxDeclaration vatTaxDeclaration) {
		int year = vatTaxDeclaration.getVatTax().getYear();
		if (year < 2010) {
			String msg = "No se permite la generación de archivos para declaraciones anteriores al ejercicio 2010."; 
			AonUtil.addErrorMessage(msg);
			setFileOutput(null);
			throw new AbortProcessingException(msg);
		}
		if (vatTaxDeclaration.getVatTax().isAnual()) {
			String msg = "La generación de archivos para declaraciones anuales del ejercicio " + year + " no está aún implementada."; 
			AonUtil.addErrorMessage(msg);
			setFileOutput(null);
			throw new AbortProcessingException(msg);
		}
		for (MOD303Format format:MOD303Format.values()) {
			if (format.getAdministration() ==  vatTaxDeclaration.getAdministration() && year >= format.getYear()) {
				return format;
			}
		}
		String msg = "La generación de archivos para la administracion "+ vatTaxDeclaration.getAdministration() +" no está aún implementada."; 
		AonUtil.addErrorMessage(msg);
		setFileOutput(null);
		throw new AbortProcessingException(msg);
	}

	public void downloadDisk(ActionEvent event) throws ManagerBeanException {
        try {
    		FacesContext faces = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
            VatTaxDeclaration dec = (VatTaxDeclaration) getTo();
	        
	        MOD303Format format = getFormat(dec);
	        response.setContentType(format.getMimeType().getName());
	        String year = dec.getVatTax().getYear().toString();
	        String period = dec.getVatTax().getPeriod().toString();
	        String fileName = format.getFileName(year, period); 
	        response.setHeader("Content-disposition", "attachment; filename=\"" + fileName + "\";");

	        ServletOutputStream output = response.getOutputStream();
	        InputStream input = getFileOutput().getFile() != null
	        		?new FileInputStream(getFileOutput().getFile())
	        		:new ByteArrayInputStream(getFileOutput().getContent());
	        int size = IOUtils.copy(input, output);
	        if (size > 0) {
		        response.setHeader("Content-Length", String.valueOf(size));
	        }
	        output.close();
	        input.close();

	        response.flushBuffer();
	        faces.responseComplete();
		} catch (IOException e) {
			throw new ManagerBeanException(e);
		}
	}
	
	public boolean isScriptPresent() {
		return getMipf().isScriptPresent();
	}
	
}
