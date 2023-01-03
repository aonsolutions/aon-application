package com.code.aon.ui.fiscal.controller.mod303;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.AonVersion;
import com.code.aon.common.AonException;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.file.tax.model.MOD303.MOD303Format;
import com.code.aon.fiscal.FiscalModel;
import com.code.aon.fiscal.FiscalModelDetail;
import com.code.aon.fiscal.enumeration.FiscalModelType;
import com.code.aon.fiscal.enumeration.Mod303Key;
import com.code.aon.fiscal.enumeration.Period;
import com.code.aon.fiscal.mod303.IMod303Declaration;
import com.code.aon.fiscal.mod303.Mod303;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.fiscal.aeat.AeatUtils;
import com.code.aon.ui.fiscal.controller.model.FiscalModelController;
import com.code.aon.ui.fiscal.file.MOD303Writer;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.occam.api.model.type.Activities.Type1Activities;
import com.esferalia.aon.occam.api.model.type.Activities.TypeActivity;

public class Mod303Controller extends FiscalModelController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private boolean payBack;
	private boolean modulesPanelVisible;
	private boolean farmerPanelVisible;
	private boolean epigraphPanelVisible;

	private int activityGroupSelected;
	private Mod303Key selectedKey;
	private LinkedList<Mod303Key> moduleKeys;
	private DataModel epigraphModel;
	
	 
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
	public boolean isExtraTabVisible() {
		return (!isNevv() 
				&& getDeclaration() != null 
				&& getDeclaration().getHeader() != null 
				&& getDeclaration().getHeader().getYear() >= 2014		
				&& getDeclaration().getHeader().getPeriod() == Period.T4);
	}
	
	public Mod303Key getSelectedKey() {
		return selectedKey;
	}

	public void setSelectedKey(Mod303Key selectedKey) {
		this.selectedKey = selectedKey;
	}
	public boolean isEpigraphPanelVisible() {
		return epigraphPanelVisible;
	}
	public void setEpigraphPanelVisible(boolean epigraphPanelVisible) {
		this.epigraphPanelVisible = epigraphPanelVisible;
	}
	public int getActivityGroupSelected() {
		return activityGroupSelected;
	}
	public void setActivityGroupSelected(int activityGroupSelected) {
		this.activityGroupSelected = activityGroupSelected;
	}
	
	@Override
	protected FiscalModelType getModelType() {
		return 	FiscalModelType.M303;
	}

	@Override
	public void onEditSearch(ActionEvent event) {
		if (getModelType() == FiscalModelType.M303) {
			checkFiscalActivity(FiscalModelType.M303);
		}
		super.onEditSearch(event);	
	}

	@Override
	public boolean isDifEnabled() {
		return false;
	}
	public void onShowEpigraphPanel(ActionEvent event) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		String key = ctx.getExternalContext().getRequestParameterMap().get("key");
		Mod303Key m303key = Mod303Key.valueOf(key); 
		setSelectedKey(m303key);
		setEpigraphPanelVisible(true);
	}
	public DataModel getEpigraphModel() {
		if (epigraphModel == null) {
			List<TypeActivity> list = Arrays.asList(Type1Activities.values()); 
			epigraphModel = new SerializableListDataModel(list);
		}
		return epigraphModel;
	}
	public void onDeleteEpigraph(ActionEvent event) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		String k = ctx.getExternalContext().getRequestParameterMap().get("key");
		Mod303Key m303key = Mod303Key.valueOf(k);
		Mod303Key key = null;
		Mod303Key epigraph = null;
		Mod303Key description = null;
		if (m303key == Mod303Key.IAC_01) {
			key = Mod303Key.IAC_01;
			epigraph = Mod303Key.IAE_01;
			description = Mod303Key.IAD_01;
		} else if (m303key == Mod303Key.IAC_02) {
			key = Mod303Key.IAC_02;
			epigraph = Mod303Key.IAE_02;
			description = Mod303Key.IAD_02;
		} else if (m303key == Mod303Key.IAC_03) {
			key = Mod303Key.IAC_03;
			epigraph = Mod303Key.IAE_03;
			description = Mod303Key.IAD_03;
		} else if (m303key == Mod303Key.IAC_04) {
			key = Mod303Key.IAC_04;
			epigraph = Mod303Key.IAE_04;
			description = Mod303Key.IAD_04;
		} else if (m303key == Mod303Key.IAC_05) {
			key = Mod303Key.IAC_05;
			epigraph = Mod303Key.IAE_05;
			description = Mod303Key.IAD_05;
		} else if (m303key == Mod303Key.IAC_06) {
			key = Mod303Key.IAC_06;
			epigraph = Mod303Key.IAE_06;
			description = Mod303Key.IAD_06;
		}
		getDeclaration().getMap().get( key ).setDescription( null );
		getDeclaration().getMap().get( epigraph).setDescription( null );
		getDeclaration().getMap().get( description ).setDescription( null );
		onHideEpigraphPanel(event);
	}
	
	public void onSelectEpigraph(ActionEvent event) {
		TypeActivity activity = (TypeActivity) getEpigraphModel().getRowData();
		Mod303Key key = null;
		Mod303Key epigraph = null;
		Mod303Key description = null;
		if (getSelectedKey() == Mod303Key.IAC_01) {
			key = Mod303Key.IAC_01;
			epigraph = Mod303Key.IAE_01;
			description = Mod303Key.IAD_01;
		} else if (getSelectedKey() == Mod303Key.IAC_02) {
			key = Mod303Key.IAC_02;
			epigraph = Mod303Key.IAE_02;
			description = Mod303Key.IAD_02;
		} else if (getSelectedKey() == Mod303Key.IAC_03) {
			key = Mod303Key.IAC_03;
			epigraph = Mod303Key.IAE_03;
			description = Mod303Key.IAD_03;
		} else if (getSelectedKey() == Mod303Key.IAC_04) {
			key = Mod303Key.IAC_04;
			epigraph = Mod303Key.IAE_04;
			description = Mod303Key.IAD_04;
		} else if (getSelectedKey() == Mod303Key.IAC_05) {
			key = Mod303Key.IAC_05;
			epigraph = Mod303Key.IAE_05;
			description = Mod303Key.IAD_05;
		} else if (getSelectedKey() == Mod303Key.IAC_06) {
			key = Mod303Key.IAC_06;
			epigraph = Mod303Key.IAE_06;
			description = Mod303Key.IAD_06;
		}
		getDeclaration().getMap().get( key ).setDescription( "1" );
		getDeclaration().getMap().get( epigraph).setDescription( activity.getEpigraph() );
		getDeclaration().getMap().get( description ).setDescription( activity.getLiteral() );
		onHideEpigraphPanel(event);
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
	
	public void onHideEpigraphPanel(ActionEvent event) {
		setEpigraphPanelVisible(false);
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
	
	public void onChangeExtraInfo(ActionEvent event) {
		try {
			getDeclaration().calculate();
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
			detail.setAccumulatedAmount((isPayBack() && getDeclaration().getResult() < 0)?getDeclaration().getResult():0.0);
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
		setPayBack(true);
		super.onShowFinalizePanel(event);
	}
	public void onReopen(ActionEvent event) {
		boolean payBack = isPayBack();
		try {
			setPayBack(true);
			super.onReopen(event);
		} catch (Throwable t) {
			setPayBack(payBack);
			throw t;
		}
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
	
	public Mod303Key getModG0Key() { return getSelectedKey("G0"); }
	public FiscalModelDetail getModG0() { return getSelectedDetail("G0"); }
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
	public Mod303Key getModV6Key() { return getSelectedKey("V6"); }
	public Mod303Key getModV7Key() { return getSelectedKey("V7"); }
	public FiscalModelDetail getModV1() { return getSelectedDetail("V1"); }
	public FiscalModelDetail getModV2() { return getSelectedDetail("V2"); }
	public FiscalModelDetail getModV3() { return getSelectedDetail("V3"); }
	public FiscalModelDetail getModV4() { return getSelectedDetail("V4"); }
	public FiscalModelDetail getModV5() { return getSelectedDetail("V5"); }
	public FiscalModelDetail getModV6() { return getSelectedDetail("V6"); }
	public FiscalModelDetail getModV7() { return getSelectedDetail("V7"); }
	
	
	
	public FiscalModelDetail getDetailC59()   { return getDeclaration().getMap().get(Mod303Key.C59); }
	public Mod303Key getKeyC59()   { return Mod303Key.C59; }
	public FiscalModelDetail getDetailC60()   { return getDeclaration().getMap().get(Mod303Key.C60); }
	public Mod303Key getKeyC60()   { return Mod303Key.C60; }
	public FiscalModelDetail getDetailC61()   { return getDeclaration().getMap().get(Mod303Key.C61); }
	public Mod303Key getKeyC61()   { return Mod303Key.C61; }
	public FiscalModelDetail getDetailC62()   { return getDeclaration().getMap().get(Mod303Key.C62); }
	public Mod303Key getKeyC62()   { return Mod303Key.C62; }
	public FiscalModelDetail getDetailC63()   { return getDeclaration().getMap().get(Mod303Key.C63); }
	public Mod303Key getKeyC63()   { return Mod303Key.C63; }
	public FiscalModelDetail getDetailC74()   { return getDeclaration().getMap().get(Mod303Key.C74); }
	public Mod303Key getKeyC74()   { return Mod303Key.C74; }
	public FiscalModelDetail getDetailC75()   { return getDeclaration().getMap().get(Mod303Key.C75); }
	public Mod303Key getKeyC75()   { return Mod303Key.C75; }
	
	public FiscalModelDetail getDetailIAC_01()   { return getDeclaration().getMap().get(Mod303Key.IAC_01); }
	public Mod303Key getKeyIAC_01()   { return Mod303Key.IAC_01; }
	public FiscalModelDetail getDetailIAE_01()   { return getDeclaration().getMap().get(Mod303Key.IAE_01); }
	public Mod303Key getKeyIAE_01()   { return Mod303Key.IAE_01; }
	public FiscalModelDetail getDetailIAD_01()   { return getDeclaration().getMap().get(Mod303Key.IAD_01); }
	public Mod303Key getKeyIAD_01()   { return Mod303Key.IAD_01; }
	
	
	public FiscalModelDetail getDetailIAC_02()   { return getDeclaration().getMap().get(Mod303Key.IAC_02); }
	public Mod303Key getKeyIAC_02()   { return Mod303Key.IAC_02; }
	public FiscalModelDetail getDetailIAE_02()   { return getDeclaration().getMap().get(Mod303Key.IAE_02); }
	public Mod303Key getKeyIAE_02()   { return Mod303Key.IAE_02; }
	public FiscalModelDetail getDetailIAD_02()   { return getDeclaration().getMap().get(Mod303Key.IAD_02); }
	public Mod303Key getKeyIAD_02()   { return Mod303Key.IAD_02; }
	
	public FiscalModelDetail getDetailIAC_03()   { return getDeclaration().getMap().get(Mod303Key.IAC_03); }
	public Mod303Key getKeyIAC_03()   { return Mod303Key.IAC_03; }
	public FiscalModelDetail getDetailIAE_03()   { return getDeclaration().getMap().get(Mod303Key.IAE_03); }
	public Mod303Key getKeyIAE_03()   { return Mod303Key.IAE_03; }
	public FiscalModelDetail getDetailIAD_03()   { return getDeclaration().getMap().get(Mod303Key.IAD_03); }
	public Mod303Key getKeyIAD_03()   { return Mod303Key.IAD_03; }
	
	public FiscalModelDetail getDetailIAC_04()   { return getDeclaration().getMap().get(Mod303Key.IAC_04); }
	public Mod303Key getKeyIAC_04()   { return Mod303Key.IAC_04; }
	public FiscalModelDetail getDetailIAE_04()   { return getDeclaration().getMap().get(Mod303Key.IAE_04); }
	public Mod303Key getKeyIAE_04()   { return Mod303Key.IAE_04; }
	public FiscalModelDetail getDetailIAD_04()   { return getDeclaration().getMap().get(Mod303Key.IAD_04); }
	public Mod303Key getKeyIAD_04()   { return Mod303Key.IAD_04; }
	
	public FiscalModelDetail getDetailIAC_05()   { return getDeclaration().getMap().get(Mod303Key.IAC_05); }
	public Mod303Key getKeyIAC_05()   { return Mod303Key.IAC_05; }
	public FiscalModelDetail getDetailIAE_05()   { return getDeclaration().getMap().get(Mod303Key.IAE_05); }
	public Mod303Key getKeyIAE_05()   { return Mod303Key.IAE_05; }
	public FiscalModelDetail getDetailIAD_05()   { return getDeclaration().getMap().get(Mod303Key.IAD_05); }
	public Mod303Key getKeyIAD_05()   { return Mod303Key.IAD_05; }
	
	public FiscalModelDetail getDetailIAC_06()   { return getDeclaration().getMap().get(Mod303Key.IAC_06); }
	public Mod303Key getKeyIAC_06()   { return Mod303Key.IAC_06; }
	public FiscalModelDetail getDetailIAE_06()   { return getDeclaration().getMap().get(Mod303Key.IAE_06); }
	public Mod303Key getKeyIAE_06()   { return Mod303Key.IAE_06; }
	public FiscalModelDetail getDetailIAD_06()   { return getDeclaration().getMap().get(Mod303Key.IAD_06); }
	public Mod303Key getKeyIAD_06()   { return Mod303Key.IAD_06; }
	
	public FiscalModelDetail getDetailD()   { return getDeclaration().getMap().get(Mod303Key.D); }
	public Mod303Key getKeyD()   { return Mod303Key.D; }
	public FiscalModelDetail getDetailC80()   { return getDeclaration().getMap().get(Mod303Key.C80); }
	public Mod303Key getKeyC80()   { return Mod303Key.C80; }
	public FiscalModelDetail getDetailC81()   { return getDeclaration().getMap().get(Mod303Key.C81); }
	public Mod303Key getKeyC81()   { return Mod303Key.C81; }
	public FiscalModelDetail getDetailC82()   { return getDeclaration().getMap().get(Mod303Key.C82); }
	public Mod303Key getKeyC82()   { return Mod303Key.C82; }
	public FiscalModelDetail getDetailC83()   { return getDeclaration().getMap().get(Mod303Key.C83); }
	public Mod303Key getKeyC83()   { return Mod303Key.C83; }
	public FiscalModelDetail getDetailC84()   { return getDeclaration().getMap().get(Mod303Key.C84); }
	public Mod303Key getKeyC84()   { return Mod303Key.C84; }
	public FiscalModelDetail getDetailC85()   { return getDeclaration().getMap().get(Mod303Key.C85); }
	public Mod303Key getKeyC85()   { return Mod303Key.C85; }
	public FiscalModelDetail getDetailC86()   { return getDeclaration().getMap().get(Mod303Key.C86); }
	public Mod303Key getKeyC86()   { return Mod303Key.C86; }
	public FiscalModelDetail getDetailC87()   { return getDeclaration().getMap().get(Mod303Key.C87); }
	public Mod303Key getKeyC87()   { return Mod303Key.C87; }
	public FiscalModelDetail getDetailC88()   { return getDeclaration().getMap().get(Mod303Key.C88); }
	public Mod303Key getKeyC88()   { return Mod303Key.C88; }
	
	
	public FiscalModelDetail getDetailC79()   { return getDeclaration().getMap().get(Mod303Key.C79); }
	public Mod303Key getKeyC79()   { return Mod303Key.C79; }
	public FiscalModelDetail getDetailC89()   { return getDeclaration().getMap().get(Mod303Key.C89); }
	public Mod303Key getKeyC89()   { return Mod303Key.C89; }
	public FiscalModelDetail getDetailC90()   { return getDeclaration().getMap().get(Mod303Key.C90); }
	public Mod303Key getKeyC90()   { return Mod303Key.C90; }
	public FiscalModelDetail getDetailC91()   { return getDeclaration().getMap().get(Mod303Key.C91); }
	public Mod303Key getKeyC91()   { return Mod303Key.C91; }
	public FiscalModelDetail getDetailC92()   { return getDeclaration().getMap().get(Mod303Key.C92); }
	public Mod303Key getKeyC92()   { return Mod303Key.C92; }
	public FiscalModelDetail getDetailC93()   { return getDeclaration().getMap().get(Mod303Key.C93); }
	public Mod303Key getKeyC93()   { return Mod303Key.C93; }
	public FiscalModelDetail getDetailC94()   { return getDeclaration().getMap().get(Mod303Key.C94); }
	public Mod303Key getKeyC94()   { return Mod303Key.C94; }
	public FiscalModelDetail getDetailC95()   { return getDeclaration().getMap().get(Mod303Key.C95); }
	public Mod303Key getKeyC95()   { return Mod303Key.C95; }
	public FiscalModelDetail getDetailC96()   { return getDeclaration().getMap().get(Mod303Key.C96); }
	public Mod303Key getKeyC96()   { return Mod303Key.C96; }
	public FiscalModelDetail getDetailC97()   { return getDeclaration().getMap().get(Mod303Key.C97); }
	public Mod303Key getKeyC97()   { return Mod303Key.C97; }
	public FiscalModelDetail getDetailC98()   { return getDeclaration().getMap().get(Mod303Key.C98); }
	public Mod303Key getKeyC98()   { return Mod303Key.C98; }
	public FiscalModelDetail getDetailC99()   { return getDeclaration().getMap().get(Mod303Key.C99); }
	public Mod303Key getKeyC99()   { return Mod303Key.C99; }
	
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
			MOD303Format format = MOD303Format.getFormat(fm.getAdministration(), fm.getYear(), fm.getPeriod());
			setFileOutput( mod303Writer.createMOD303(list, format, null) );
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
		MOD303Format format = MOD303Format.getFormat(fm.getAdministration(), fm.getYear(), fm.getPeriod());
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
	}
	public boolean isAeatDraftReportEnabled() {
		return super.isAeatDraftReportEnabled();
	}
	
	public ArrayList<?> getActivities(int activityGroup) {
		AonUtil.addErrorMessage("Illegal operation"); 
		throw new AbortProcessingException("Illegal operation");
	}
}
