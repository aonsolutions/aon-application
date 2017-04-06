package com.code.aon.ui.fiscal.controller;


import static com.code.aon.ui.common.ICommonMessages.FINANCE_BATCH_DISK_ERROR;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.Company;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.file.tax.model.MOD303.MOD303Format;
import com.code.aon.fiscal.FiscalModel;
import com.code.aon.fiscal.VatTax;
import com.code.aon.fiscal.VatTaxDeclaration;
import com.code.aon.fiscal.VatTaxDetail;
import com.code.aon.fiscal.enumeration.Mod303Key;
import com.code.aon.fiscal.enumeration.Period;
import com.code.aon.fiscal.enumeration.VatTaxDeclarationStatus;
import com.code.aon.fiscal.enumeration.VatTaxKey;
import com.code.aon.fiscal.enumeration.VatTaxStatus;
import com.code.aon.fiscal.mod303.IMod303Declaration;
import com.code.aon.fiscal.mod303.Mod303;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryBank;
import com.code.aon.ui.fiscal.aeat.AeatUtils;
import com.code.aon.ui.fiscal.aeat.AeatUtils.Mod303Type;
import com.code.aon.ui.fiscal.controller.mod303.Mod303AIController;
import com.code.aon.ui.fiscal.controller.model.Mipf;
import com.code.aon.ui.fiscal.file.MOD303Writer;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.watson.util.AonStringUtils;

public class VatTaxDeclarationController extends LinesController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	public static final String BEAN_NAME = "vatTaxDeclaration";

	private FileOutput fileOutput;
	private Mipf mipf;
	
	private boolean additionalDataPanelVisible;

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

	public void tryAutomaticCreation(VatTax vatTax) throws ManagerBeanException {
		VatTaxController master = (VatTaxController) getMasterController();
		VatTax previousVatTax = getPreviousVatTax(vatTax);
		if (previousVatTax == null) {
			Administration adm = master.getFiscalParams().getDefaultAdministration();
			if (adm == null) {
				adm = Administration.COMMON_TERRITORY;
			}
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
		fillYearPreviousData(vatTax,dec);
		if (vatTax.isAnual()) {
			fillPreviousData(vatTax,dec);
		} else {
		}
		calculate(dec);
		accept(null);
		dec.setRegistryBank(null);	
	}

	private void fillYearPreviousData(VatTax vatTax, VatTaxDeclaration dec) throws ManagerBeanException {
		Criteria criteria = new Criteria();
		criteria.addNotEqualExpression(getManagerBean().getFieldName(IEntityAlias.VAT_TAX_DECLARATION_VAT_TAX_ID), vatTax.getId());
		if (!vatTax.isAnual()) { 
			criteria.addEqualExpression( getManagerBean().getFieldName(IEntityAlias.VAT_TAX_DECLARATION_VAT_TAX_YEAR), vatTax.getYear());
			criteria.addLessThanExpression( getManagerBean().getFieldName(IEntityAlias.VAT_TAX_DECLARATION_VAT_TAX_PERIOD), vatTax.getPeriod());
		} else {
			criteria.addEqualExpression(getManagerBean().getFieldName(IEntityAlias.VAT_TAX_DECLARATION_VAT_TAX_YEAR), (vatTax.getYear() -1));
			criteria.addEqualExpression( getManagerBean().getFieldName(IEntityAlias.VAT_TAX_DECLARATION_VAT_TAX_PERIOD), Period.YEAR );
		}
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
		criteria.addNotEqualExpression(getManagerBean().getFieldName(IEntityAlias.VAT_TAX_DECLARATION_VAT_TAX_ID), vatTax.getId());
		criteria.addEqualExpression( getManagerBean().getFieldName(IEntityAlias.VAT_TAX_DECLARATION_VAT_TAX_YEAR), vatTax.getYear());
		criteria.addLessThanExpression(getManagerBean().getFieldName(IEntityAlias.VAT_TAX_DECLARATION_VAT_TAX_PERIOD), vatTax.getPeriod());
		criteria.addEqualExpression( getManagerBean().getFieldName(IEntityAlias.VAT_TAX_DECLARATION_ADMINISTRATION), dec.getAdministration());
		criteria.addOrder( getManagerBean().getFieldName(IEntityAlias.VAT_TAX_DECLARATION_VAT_TAX_NUMBER), false);
		criteria.addOrder( getManagerBean().getFieldName(IEntityAlias.VAT_TAX_DECLARATION_VAT_TAX_REPLACEMENT), false);
		List<ITransferObject> list = getManagerBean().getList(criteria);
		double pd = 0.0;
		double pp = 0.0;
		for (ITransferObject to : list ) {
			VatTaxDeclaration vtd = (VatTaxDeclaration) to;
			pd = CommonUtil.round( pd + vtd.getDeposit() );
			pp = CommonUtil.round( pp + vtd.getPayBack() );
		}
		dec.setDoneDeposits(pd);
		dec.setDoneRefunds(pp);
	}

	private VatTax getPreviousVatTax(VatTax vatTax) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(VatTax.class);
		Criteria criteria = new Criteria();
		criteria.addNotEqualExpression(bean.getFieldName(IEntityAlias.VAT_TAX_ID), vatTax.getId());
		criteria.addLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.VAT_TAX_YEAR), vatTax.getYear());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.VAT_TAX_STATUS), VatTaxStatus.FINISHED);
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
		List<IMod303Declaration> declarations = new LinkedList<IMod303Declaration>();
		declarations.add(vatTaxDeclaration);
		Mod303 additionalInfo = null;
		if (isAdditionalDataDefined()) {
			Mod303AIController m303Controller = (Mod303AIController) AonUtil.getRegisteredBean("mod303_ai");
			additionalInfo = (Mod303) m303Controller.getDeclaration();
		}
		setFileOutput( mod303Writer.createMOD303(declarations,getFormat(vatTaxDeclaration), additionalInfo) );
        if (getFileOutput() != null && getFileOutput().getErrors().size() > 0) {
    		AonUtil.addErrorMessageFromBundle(FINANCE_BATCH_DISK_ERROR);
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
	
	public boolean isAeatDraftReportEnabled() {
		VatTaxDeclaration to = (VatTaxDeclaration) getTo();
		return ( !isNevv() && to.isFromCommonTerritory() && to.getVatTax().getYear() > 2013);		
	}
	
	public boolean isAeatValidable() {
		VatTaxDeclaration to = (VatTaxDeclaration) getTo();
		return ( !isNevv() && to.isFromCommonTerritory() && isScriptPresent() && to.getVatTax().getYear() < 2014);
	}
	private String validateAeatFile() {
		VatTaxDeclaration to = (VatTaxDeclaration) getTo();
		getMipf().validateAeatFile(to.getVatTax().getYear(),fileOutput);
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
	        String fileName = getAutomaticFileName() + "." + format.getMimeType().getExtension(); 
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
	
	@Override
	public void onRemove(ActionEvent event) {
		System.out.println("onRemove");
		super.onRemove(event);
	}
	
	public boolean isScriptPresent() {
		VatTaxDeclaration to = (VatTaxDeclaration) getTo();
		return to != null && to.getVatTax() != null && getMipf().isScriptPresent(to.getVatTax().getYear());
	}
	
	public String onAEATPrint() {
		try {
			if (getFileOutput() == null) {
				onCreateDisk(null);
			}
			InputStream input = getFileOutput().getFile() != null
					?new FileInputStream(getFileOutput().getFile())
					:new ByteArrayInputStream(getFileOutput().getContent());

			FacesContext faces = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
            VatTaxDeclaration dec = (VatTaxDeclaration) getTo();
            String fileName = getAutomaticFileName();
            response.setHeader("Content-disposition", "attachment; filename=\""+fileName+"\";");
			AeatUtils.printMod303(dec.getVatTax().getYear(),
					dec.getVatTax().getPeriod(),
					input,response.getOutputStream());					
	        response.flushBuffer();
	        faces.responseComplete();
		} catch (FileNotFoundException e) {
			AonUtil.addErrorMessage(e.getMessage()); 
			throw new AbortProcessingException(e.getMessage(),e);
		} catch (UnsupportedEncodingException e) {
			AonUtil.addErrorMessage(e.getMessage()); 
			throw new AbortProcessingException(e.getMessage(),e);
		} catch (IOException e) {
			AonUtil.addErrorMessage(e.getMessage()); 
			throw new AbortProcessingException(e.getMessage(),e);
		} catch (AonException e) {
			AonUtil.addErrorMessage(e.getMessage()); 
			throw new AbortProcessingException(e.getMessage(),e);
		}
		return null;
	}
	
	public String onAEATSend() {
		try {
			if (getFileOutput() == null) {
				onCreateDisk(null);
			}
			InputStream input = getFileOutput().getFile() != null
					?new FileInputStream(getFileOutput().getFile())
					:new ByteArrayInputStream(getFileOutput().getContent());

			FacesContext faces = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
            VatTaxDeclaration dec = (VatTaxDeclaration) getTo();
            AeatUtils.Mod303Type type = null;
            if (dec.isPayBackEnabled()) {
            	type = Mod303Type.D;
            } else if (dec.isCompensateEnabled() ) {
            	type = Mod303Type.C;
            } else if (dec.getDeposit() == 0) {
           		type = Mod303Type.N;	
           	} else {
           		type = Mod303Type.I;	
            }
            String document = ""; 
			AeatUtils.sendMod303(dec.getVatTax().getYear(),
					dec.getVatTax().getPeriod(),type,document,
					input,response.getOutputStream());					
	        response.flushBuffer();
	        faces.responseComplete();
		} catch (FileNotFoundException e) {
			AonUtil.addErrorMessage(e.getMessage()); 
			throw new AbortProcessingException(e.getMessage(),e);
		} catch (UnsupportedEncodingException e) {
			AonUtil.addErrorMessage(e.getMessage()); 
			throw new AbortProcessingException(e.getMessage(),e);
		} catch (IOException e) {
			AonUtil.addErrorMessage(e.getMessage()); 
			throw new AbortProcessingException(e.getMessage(),e);
		} catch (AonException e) {
			AonUtil.addErrorMessage(e.getMessage()); 
			throw new AbortProcessingException(e.getMessage(),e);
		}
		return null;
	}

	private Company getCompany(int domain) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Company.class);
		Criteria c  = new Criteria();
		c.addEqualExpression("Company.domain", domain);
		c.setSkipDomainFilter(true);
		List<ITransferObject> list = bean.getList(c);
		if (list != null && list.size() > 0 ){
			return (Company) list.get(0);
		}
		throw new ManagerBeanException("No puedo encontrar 'Company' para el dominio " + domain);
	}
	
	private String getAutomaticFileName() {
		VatTaxDeclaration dec = (VatTaxDeclaration) getTo();
		Company company;
		try {
			company = getCompany(dec.getDomain());
			String s = company.getFullName();
		    StringBuilder sb = new StringBuilder();
		    if(!Character.isJavaIdentifierStart(s.charAt(0))) {
		        sb.append("_");
		    }
		    for (char c : s.toCharArray()) {
		        if(Character.isJavaIdentifierPart(c)) {
		            sb.append(c);
		        }
		    }		
			return "M303_" + dec.getVatTax().getYear() 
					+ "_" + dec.getVatTax().getPeriod()
					+ "_" + sb.toString();
		} catch (ManagerBeanException e) {
			return "M303_" + dec.getVatTax().getYear() 
					+ "_" + dec.getVatTax().getPeriod();
		}
	}
	
	public boolean isAdditionalDataEnabled() {
		VatTaxDeclaration dec = (VatTaxDeclaration) getTo();
		return !isNevv() && dec.getAdministration() == Administration.COMMON_TERRITORY &&
				(dec.getVatTax().getPeriod() == Period.T4  || dec.getVatTax().getPeriod() == Period.M12);
		
	}
	
	public void onHideAdditionalData(ActionEvent event) {
		setAdditionalDataPanelVisible(false);
	}

	public boolean isAdditionalDataPanelVisible() {
		return additionalDataPanelVisible;
	}

	public void setAdditionalDataPanelVisible(boolean additionalDataPanelVisible) {
		this.additionalDataPanelVisible = additionalDataPanelVisible;
	}
	
	public boolean isAdditionalDataDefined() {
		try {
			VatTaxDeclaration dec = (VatTaxDeclaration) getTo();
			if (isNevv()) return false;
			Mod303AIController m303Controller = (Mod303AIController) AonUtil.getRegisteredBean("mod303_ai");
			m303Controller.clearCriteria();
			Criteria criteria = m303Controller.getCriteria();
			String yearAlias = m303Controller.getManagerBean().getFieldName(IEntityAlias.FISCAL_MODEL_YEAR); 
			String periodAlias = m303Controller.getManagerBean().getFieldName(IEntityAlias.FISCAL_MODEL_PERIOD);
			String admonAlias = m303Controller.getManagerBean().getFieldName(IEntityAlias.FISCAL_MODEL_ADMINISTRATION);
			criteria.addEqualExpression(yearAlias,dec.getVatTax().getYear());
			criteria.addEqualExpression(periodAlias,dec.getVatTax().getPeriod());
			criteria.addEqualExpression(admonAlias,dec.getAdministration());
			System.out.println( "isAdditionalDataDefined --> " + criteria );
			int count = m303Controller.getManagerBean().getCount(m303Controller.getCriteria());
			if (count == 0) {
				return false;
			} else {
				return true;
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage()); 
			throw new AbortProcessingException(e.getMessage(),e);
		}
	}
	
	public void onAcceptAdditionalData(ActionEvent event ) {
		try {
			Mod303AIController m303Controller = (Mod303AIController) AonUtil.getRegisteredBean("mod303_ai");
			m303Controller.accept(event);
			String epi1 = m303Controller.getDeclaration().ensureDetail(Mod303Key.IAE_01).getDescription();
			if (AonStringUtils.equals(epi1,"8611") || AonStringUtils.equals(epi1,"8612")) {
				m303Controller.getDeclaration().ensureDetail(Mod303Key.IAC_01).setDescription("3");	
			}
			String epi2 = m303Controller.getDeclaration().ensureDetail(Mod303Key.IAE_02).getDescription();
			if (AonStringUtils.equals(epi2,"8611") || AonStringUtils.equals(epi2,"8612")) {
				m303Controller.getDeclaration().ensureDetail(Mod303Key.IAC_01).setDescription("3");	
			}
			String epi3 = m303Controller.getDeclaration().ensureDetail(Mod303Key.IAE_03).getDescription();
			if (AonStringUtils.equals(epi3,"8611") || AonStringUtils.equals(epi3,"8612")) {
				m303Controller.getDeclaration().ensureDetail(Mod303Key.IAC_01).setDescription("3");	
			}
			String epi4 = m303Controller.getDeclaration().ensureDetail(Mod303Key.IAE_04).getDescription();
			if (AonStringUtils.equals(epi4,"8611") || AonStringUtils.equals(epi4,"8612")) {
				m303Controller.getDeclaration().ensureDetail(Mod303Key.IAC_01).setDescription("3");	
			}
			String epi5 = m303Controller.getDeclaration().ensureDetail(Mod303Key.IAE_05).getDescription();
			if (AonStringUtils.equals(epi5,"8611") || AonStringUtils.equals(epi5,"8612")) {
				m303Controller.getDeclaration().ensureDetail(Mod303Key.IAC_01).setDescription("3");	
			}
			String epi6 = m303Controller.getDeclaration().ensureDetail(Mod303Key.IAE_06).getDescription();
			if (AonStringUtils.equals(epi6,"8611") || AonStringUtils.equals(epi6,"8612")) {
				m303Controller.getDeclaration().ensureDetail(Mod303Key.IAC_01).setDescription("3");	
			}
			onHideAdditionalData(event);
		} catch (Throwable e) {
			AonUtil.addErrorMessage(e.getMessage()); 
			throw new AbortProcessingException(e.getMessage(),e);
		}
	}
	
	public void onRemoveAdditionalData(ActionEvent event ) {
		try {
			VatTaxDeclaration dec = (VatTaxDeclaration) getTo();
			Mod303AIController m303Controller = (Mod303AIController) AonUtil.getRegisteredBean("mod303_ai");
			m303Controller.clearCriteria();
			Criteria criteria = m303Controller.getCriteria();
			String yearAlias = m303Controller.getManagerBean().getFieldName(IEntityAlias.FISCAL_MODEL_YEAR); 
			String periodAlias = m303Controller.getManagerBean().getFieldName(IEntityAlias.FISCAL_MODEL_PERIOD);
			String admonAlias = m303Controller.getManagerBean().getFieldName(IEntityAlias.FISCAL_MODEL_ADMINISTRATION);
			criteria.addEqualExpression(yearAlias,dec.getVatTax().getYear());
			criteria.addEqualExpression(periodAlias,dec.getVatTax().getPeriod());
			criteria.addEqualExpression(admonAlias,dec.getAdministration());
			m303Controller.setCriteria(criteria);
			System.out.println( "onRemoveAdditionalData --> " + criteria );
			m303Controller.onSearch(null);
			if (m303Controller.getRowCount() == 1) {
				m303Controller.getModel().setRowIndex(0);
				m303Controller.onSelect(null);
				m303Controller.remove(event);
			}
			onHideAdditionalData(event);
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage()); 
			throw new AbortProcessingException(e.getMessage(),e);
		}
	}
	
	public void onAdditionalData(ActionEvent event ) {
		try {
			VatTaxDeclaration dec = (VatTaxDeclaration) getTo();
			Mod303AIController m303Controller = (Mod303AIController) AonUtil.getRegisteredBean("mod303_ai");
			m303Controller.clearCriteria();			
			Criteria criteria = m303Controller.getCriteria();
			String yearAlias = m303Controller.getManagerBean().getFieldName(IEntityAlias.FISCAL_MODEL_YEAR); 
			String periodAlias = m303Controller.getManagerBean().getFieldName(IEntityAlias.FISCAL_MODEL_PERIOD);
			String admonAlias = m303Controller.getManagerBean().getFieldName(IEntityAlias.FISCAL_MODEL_ADMINISTRATION);
			criteria.addEqualExpression(yearAlias,dec.getVatTax().getYear());
			criteria.addEqualExpression(periodAlias,dec.getVatTax().getPeriod());
			criteria.addEqualExpression(admonAlias,dec.getAdministration());
			m303Controller.setCriteria(criteria);
			System.out.println( "onAdditionalData --> " + criteria );
			m303Controller.onSearch(null);
			if (m303Controller.getRowCount() == 0) {
				m303Controller.onReset(event);
				FiscalModel fs = (FiscalModel) m303Controller.getTo();
				fs.setYear(dec.getVatTax().getYear());
				fs.setPeriod(dec.getVatTax().getPeriod());
				fs.setAdministration(dec.getAdministration());
				fs.setAdmonAeat("XXXXX");
				m303Controller.accept(event);
				VatTaxController taxController =  (VatTaxController) AonUtil.getRegisteredBean("vatTax");
				if (taxController.getVatTaxModel() != null) {
					@SuppressWarnings("unchecked")
					List<VatTaxDetail> vatDetails = (List<VatTaxDetail>) taxController.getVatTaxModel().getWrappedData(); 
					for (VatTaxDetail vatDetail : vatDetails) {
						if (vatDetail.getKey() == VatTaxKey.A1) {
							m303Controller.getDeclaration().ensureDetail(Mod303Key.C80).addAccumulatedAmount(vatDetail.getTaxableBaseAccumulated());
						}
					}
					m303Controller.getDeclaration().ensureDetail(Mod303Key.C86).setAccumulatedAmount(0.0);
				}
				
				m303Controller.accept(event);
			} else {
				m303Controller.getModel().setRowIndex(0);
				m303Controller.onSelect(null);
			}
			setAdditionalDataPanelVisible(true);
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage()); 
			throw new AbortProcessingException(e.getMessage(),e);
		}
	}
	
	public String getPredeclarationURL() {
		return "https://www2.agenciatributaria.gob.es/static_files/common/internet/dep/aplicaciones/ov/servurlp.html?WEB=INTERNET&PRG=303&EJE=0003&URL=IMP";
	}
}
