package com.code.aon.ui.fiscal.controller.mod303;

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
import javax.servlet.http.HttpServletResponse;

import com.code.aon.AonVersion;
import com.code.aon.common.AonException;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.file.tax.model.MOD303.MOD303Format;
import com.code.aon.file.tax.model.MOD310.MOD310Format;
import com.code.aon.fiscal.FiscalModel;
import com.code.aon.fiscal.FiscalModelDetail;
import com.code.aon.fiscal.enumeration.FiscalModelType;
import com.code.aon.fiscal.enumeration.Mod303Key;
import com.code.aon.fiscal.mod303.IMod303Declaration;
import com.code.aon.fiscal.mod303.Mod303;
import com.code.aon.ui.fiscal.aeat.AeatUtils;
import com.code.aon.ui.fiscal.controller.model.FiscalModelController;
import com.code.aon.ui.fiscal.file.MOD303Writer;
import com.code.aon.ui.util.AonUtil;

public class Mod303Controller extends FiscalModelController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private boolean payBack;
	private boolean modulesPanelVisible;
	private boolean farmerPanelVisible;
	
	private Mod303Key selectedKey;
	private LinkedList<Mod303Key> moduleKeys;
	 
	public boolean isModulesPanelVisible() {
		return modulesPanelVisible;
	}
	public void setModulesPanelVisible(boolean modulesPanelVisible) {
		this.modulesPanelVisible = modulesPanelVisible;
		if (modulesPanelVisible) {
			this.farmerPanelVisible = false;
		}
	}
	public boolean isFarmerPanelVisible() {
		return farmerPanelVisible;
	}
	public void setFarmerPanelVisible(boolean farmerPanelVisible) {
		this.farmerPanelVisible = farmerPanelVisible;
		if (farmerPanelVisible) {
			this.modulesPanelVisible = false;
		}
	}
	
	public Mod303Key getSelectedKey() {
		return selectedKey;
	}

	public void setSelectedKey(Mod303Key selectedKey) {
		this.selectedKey = selectedKey;
	}

	@Override
	protected FiscalModelType getModelType() {
		return 	FiscalModelType.M303;
	}

	@Override
	public void onEditSearch(ActionEvent event) {
		checkFiscalActivity(FiscalModelType.M303);
		super.onEditSearch(event);	
	}

	@Override
	public boolean isDifEnabled() {
		return false;
	}
	
	public void onShowActivities(ActionEvent event) {
		onRecalculate(event);
		FacesContext ctx = FacesContext.getCurrentInstance();
		String key = ctx.getExternalContext().getRequestParameterMap().get("key");
		Mod303Key m303key = Mod303Key.valueOf(key); 
		setSelectedKey(m303key.getParentKey()==null?m303key:m303key.getParentKey());
		setModulesPanelVisible(!getSelectedKey().isFarmer());
		setFarmerPanelVisible(getSelectedKey().isFarmer());
		fillActivities();
	}
	private void fillActivities() {
		moduleKeys = new LinkedList<Mod303Key>();
		Mod303 declaration = (Mod303) getDeclaration();
		for (Mod303Key k : declaration.getMap().keySet()) {
			if (k.getParentKey() == getSelectedKey()) {
				moduleKeys.add(k);	
			}
		}
	}
	public String getSelectedActivity() {
		FiscalModelDetail detail = getDeclaration().getDetail(getSelectedKey());
		return detail.getDescription();
	}
	
	public void onHideActivityPanel(ActionEvent event) {
		setModulesPanelVisible(false);
		setFarmerPanelVisible(false);
	}
	
	public void onAcceptModules(ActionEvent event) {
		try {
			getDeclaration().calculate();
			onHideActivityPanel(event);
		} catch (Throwable e) {
			AonUtil.addErrorMessage(e.getMessage()); 
			throw new AbortProcessingException(e.getMessage(),e);
		}
	}
	
	public void onChangeZD(ActionEvent event) {
		try {
			getDeclaration().calculate();
			fillActivities();
		} catch (Throwable e) {
			AonUtil.addErrorMessage(e.getMessage()); 
			throw new AbortProcessingException(e.getMessage(),e);
		}
	}
	
	public void onChangeHD(ActionEvent event) {
		try {
			getDeclaration().calculate();
			fillActivities();
		} catch (Throwable e) {
			AonUtil.addErrorMessage(e.getMessage()); 
			throw new AbortProcessingException(e.getMessage(),e);
		}
	}

	public List<Mod303Key> getModuleKeys() {
		return moduleKeys;
	}

	public boolean isPayBack() {
		return payBack;
	}
	public void setPayBack(boolean payBack) {
		this.payBack = payBack;
	}

	@Override
	protected void accept() {
		FiscalModelDetail detail = getDeclaration().getDetail(Mod303Key.PBK );
		if (detail != null) {
			detail.setAccumulatedAmount((isPayBack())?getDeclaration().getResult():0.0);
		}
		detail = getDeclaration().getDetail(Mod303Key.CAG1_V2);
		if (detail != null) {
			detail.setAccumulatedAmount(CommonUtil.round(detail.getAccumulatedAmount() * 10000));
			detail.setAmount(CommonUtil.round(detail.getAmount() * 10000));
		}
		detail = getDeclaration().getDetail(Mod303Key.CAG2_V2);
		if (detail != null) {
			detail.setAccumulatedAmount(CommonUtil.round(detail.getAccumulatedAmount() * 10000));
			detail.setAmount(CommonUtil.round(detail.getAmount() * 10000));
		}
		super.accept();
		detail = getDeclaration().getDetail(Mod303Key.CAG1_V2);
		if (detail != null) {
			detail.setAccumulatedAmount(CommonUtil.round(detail.getAccumulatedAmount() / 10000, 5));
			detail.setAmount(CommonUtil.round(detail.getAmount() / 10000,5));
		}
		detail = getDeclaration().getDetail(Mod303Key.CAG2_V2);
		if (detail != null) {
			detail.setAccumulatedAmount(CommonUtil.round(detail.getAccumulatedAmount() / 10000,5));
			detail.setAmount(CommonUtil.round(detail.getAmount() / 10000,5));
		}
	}
	
	public void onShowFinalizePanel(ActionEvent event) {
		setPayBack(false);
		super.onShowFinalizePanel(event);
	}
	
	protected boolean mustCreateFinance(FiscalModel to) {
		if (getDeclaration().getResult() < 0 && !isPayBack()) {
			return false;
		}
		return super.mustCreateFinance(to);
	}

	public FiscalModelDetail getModUnit1()   { return getSelectedDetail("M1U"); }
	public FiscalModelDetail getModAmount1() { return getSelectedDetail("M1I"); }
	public FiscalModelDetail getModUnit2()   { return getSelectedDetail("M2U"); }
	public FiscalModelDetail getModAmount2() { return getSelectedDetail("M2I"); }
	public FiscalModelDetail getModUnit3()   { return getSelectedDetail("M3U"); }
	public FiscalModelDetail getModAmount3() { return getSelectedDetail("M3I"); }
	public FiscalModelDetail getModUnit4()   { return getSelectedDetail("M4U"); }
	public FiscalModelDetail getModAmount4() { return getSelectedDetail("M4I"); }
	public FiscalModelDetail getModUnit5()   { return getSelectedDetail("M5U"); }
	public FiscalModelDetail getModAmount5() { return getSelectedDetail("M5I"); }
	public FiscalModelDetail getModUnit6()   { return getSelectedDetail("M6U"); }
	public FiscalModelDetail getModAmount6() { return getSelectedDetail("M6I"); }
	public FiscalModelDetail getModUnit7()   { return getSelectedDetail("M7U"); }
	public FiscalModelDetail getModAmount7() { return getSelectedDetail("M7I"); }

	public Mod303Key getModCKey() { return getSelectedKey("C"); }
	public FiscalModelDetail getModC() { return getSelectedDetail("C"); }
	public Mod303Key getModDKey() { return getSelectedKey("D"); }
	public FiscalModelDetail getModD() { return getSelectedDetail("D"); }
	public Mod303Key getModZKey() { return getSelectedKey("Z"); }
	public FiscalModelDetail getModZ() { return getSelectedDetail("Z"); }
	public Mod303Key getModZAKey() { return getSelectedKey("ZA"); }
	public FiscalModelDetail getModZA() { return getSelectedDetail("ZA"); }
	public Mod303Key getModZDKey() { return getSelectedKey("ZD"); }
	public FiscalModelDetail getModZD() { return getSelectedDetail("ZD"); }
	public Mod303Key getModEKey() { return getSelectedKey("E"); }
	public FiscalModelDetail getModE() { return getSelectedDetail("E"); }
	public Mod303Key getModFKey() { return getSelectedKey("F"); }
	public FiscalModelDetail getModF() { return getSelectedDetail("F"); }
	
	public Mod303Key getModGKey() { return getSelectedKey("G"); }
	public FiscalModelDetail getModG() { return getSelectedDetail("G"); }
	public Mod303Key getModHKey() { return getSelectedKey("H"); }
	public FiscalModelDetail getModH() { return getSelectedDetail("H"); }
	public Mod303Key getModHAKey() { return getSelectedKey("HA"); }
	public FiscalModelDetail getModHA() { return getSelectedDetail("HA"); }
	public Mod303Key getModHDKey() { return getSelectedKey("HD"); }
	public FiscalModelDetail getModHD() { return getSelectedDetail("HD"); }
	public Mod303Key getModHTKey() { return getSelectedKey("HT"); }
	public FiscalModelDetail getModHT() { return getSelectedDetail("HT"); }
	public Mod303Key getModIKey() { return getSelectedKey("I"); }
	public FiscalModelDetail getModI() { return getSelectedDetail("I"); }
	public Mod303Key getModJKey() { return getSelectedKey("J"); }
	public FiscalModelDetail getModJ() { return getSelectedDetail("J"); }
	public Mod303Key getModKKey() { return getSelectedKey("K"); }
	public FiscalModelDetail getModK() { return getSelectedDetail("K"); }
	public Mod303Key getModLKey() { return getSelectedKey("L"); }
	public FiscalModelDetail getModL() { return getSelectedDetail("L"); }
	public Mod303Key getModMKey() { return getSelectedKey("M"); }
	public FiscalModelDetail getModM() { return getSelectedDetail("M"); }
	
	public Mod303Key getModV1Key() { return getSelectedKey("V1"); }
	public Mod303Key getModV2Key() { return getSelectedKey("V2"); }
	public Mod303Key getModV3Key() { return getSelectedKey("V3"); }
	public Mod303Key getModV4Key() { return getSelectedKey("V4"); }
	public Mod303Key getModV5Key() { return getSelectedKey("V5"); }
	public FiscalModelDetail getModV1() { return getSelectedDetail("V1"); }
	public FiscalModelDetail getModV2() { return getSelectedDetail("V2"); }
	public FiscalModelDetail getModV3() { return getSelectedDetail("V3"); }
	public FiscalModelDetail getModV4() { return getSelectedDetail("V4"); }
	public FiscalModelDetail getModV5() { return getSelectedDetail("V5"); }
	
	
	private FiscalModelDetail getSelectedDetail(String suffix) {
		String keyValue = getSelectedKey().toString() + "_" + suffix; 
		Mod303Key key = Mod303Key.valueOf(keyValue);  
		return getDeclaration().getMap().get( key );
	}
	private Mod303Key getSelectedKey(String suffix) {
		String keyValue = getSelectedKey().toString() + "_" + suffix; 
		return Mod303Key.valueOf(keyValue);  
	}
	
	public void onCreateDisk(ActionEvent event) {
		try { 
			MOD303Writer mod303Writer = new MOD303Writer();
			FiscalModel fm = (FiscalModel) getTo();
			List<IMod303Declaration> list = new LinkedList<IMod303Declaration>();
			Mod303 declaration = (Mod303) getDeclaration(); 
			list.add(declaration);
			MOD303Format format = MOD303Format.getFormat(fm.getAdministration(), fm.getYear());
			setFileOutput( mod303Writer.createMOD303(list, format) );
		    if (getFileOutput() != null) {
		    	if (getFileOutput().getErrors().size() > 0) {
		    		AonUtil.addErrorMessage("Se han producido errores durante la generación");
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
	
	@Override
	protected String getFormPage() {
		return "mod303_form";
	}	

	public String aeatReport() {
		try {
			if (getFileOutput() == null) {
				onCreateDisk(null);
			}
			InputStream input = getFileOutput().getFile() != null
					?new FileInputStream(getFileOutput().getFile())
					:new ByteArrayInputStream(getFileOutput().getContent());

			FacesContext faces = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
            String fileName = getAutomaticFileName();
            response.setHeader("Content-disposition", "attachment; filename=\""+fileName+"\";");
			AeatUtils.printMod303(getDeclaration().getHeader().getYear(),
					getDeclaration().getHeader().getPeriod(),
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
	
	public boolean isAeatOfficialReportEnabled() {
		return super.isAeatOfficialReportEnabled();
		//return false;
	}
	public boolean isAeatDraftReportEnabled() {
		return super.isAeatDraftReportEnabled();
		//return false;
	}
	
}
