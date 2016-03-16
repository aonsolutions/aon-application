package com.code.aon.ui.fiscal.controller.activity;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;

import org.jooq.tools.StringUtils;

import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.config.IAE;
import com.code.aon.AonVersion;
import com.code.aon.fiscal.FiscalActivity;
import com.code.aon.fiscal.FiscalActivityInfo;
import com.code.aon.fiscal.activity.Aeat2012ModuleCalculator;
import com.code.aon.fiscal.activity.Aeat2015ModuleCalculator;
import com.code.aon.fiscal.activity.Epigrafe;
import com.code.aon.fiscal.activity.IFiscalActivityContainer;
import com.code.aon.fiscal.activity.IModuleCalculator;
import com.code.aon.fiscal.activity.Mod;
import com.code.aon.fiscal.activity.Modules;
import com.code.aon.fiscal.activity.Sector;
import com.code.aon.fiscal.enumeration.FiscalActivityInfoKey;
import com.code.aon.fiscal.enumeration.FiscalActivityInfoKeyEntry;
import com.code.aon.fiscal.enumeration.FiscalActivityInfoType;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.EnterpriseActivity;

public class FiscalActivityController extends BasicController implements IFiscalActivityContainer {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static String PERCENT = "%";
	private static DecimalFormat MODULE_KEY_FORMATTER = new DecimalFormat("00");
	
	private String selectedTab;
	
	private DataModel sectors;
	private DataModel epigraphs;
	private boolean sectorPanelVisible;
	private boolean detailPanelVisible;
	private Modules modules;
	
	private List<FiscalActivityInfo> activityInfoList;
	private DataModel info;
	private Map<FiscalActivityInfoKey,List<SelectItem>> keyChoices;
	
	private List<FiscalActivityInfo> vatModulesList;
	private DataModel vatModules;

	private List<FiscalActivityInfo> irpfModulesList;
	private DataModel irpfModules;
	
	private FiscalActivityInfo infoToDetail; 
	private Map<FiscalActivityInfoKey,List<FiscalActivityInfo>> modulesDetailMap;
	private DataModel modulesDetailModel;

	private List<FiscalActivityInfo> vatInfoList;
	private DataModel vatInfo;

	private List<FiscalActivityInfo> irpfInfoList;
	private DataModel irpfInfo;
	
	private List<FiscalActivityInfo> m311List;
	private DataModel m311Model;

	private IModuleCalculator calculator;

	private Modules getModules() {
		if (modules == null) {
			modules = new Modules();
		}
		return modules;
	}
	
	public String getSelectedTab() {
		return selectedTab;
	}
	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}

	@Override
	public FiscalActivity getFiscalActivity() {
		FiscalActivity fa = (FiscalActivity) getTo();
		return fa;
	}

	@Override
	public List<FiscalActivityInfo> getActivityInfoList() {
		if (activityInfoList == null) {
			setActivityInfoList( new LinkedList<FiscalActivityInfo>() );
		}
		return activityInfoList;
	}
	public void setActivityInfoList(List<FiscalActivityInfo> activityInfoList) {
		this.activityInfoList = activityInfoList;
	}

	public DataModel getInfo() {
		if (info == null) {
			setInfo( new SerializableListDataModel( getActivityInfoList() ) );
		}
		return info;
	}
	public void setInfo(DataModel info) {
		this.info = info;
	}
	
	@Override
	public List<FiscalActivityInfo> getVatModulesList() {
		if (vatModulesList == null) {
			setVatModulesList( new LinkedList<FiscalActivityInfo>() );
		}
		return vatModulesList;
	}
	public void setVatModulesList(List<FiscalActivityInfo> vatModulesList) {
		this.vatModulesList = vatModulesList;
	}

	public DataModel getVatModules() {
		if (vatModules == null) {
			setVatModules( new SerializableListDataModel( getVatModulesList() ) );
		}
		return vatModules;
	}
	public void setVatModules(DataModel vatModules) {
		this.vatModules = vatModules;
	}
	
	@Override
	public List<FiscalActivityInfo> getM311List() {
		if (m311List == null) {
			setM311List( new LinkedList<FiscalActivityInfo>() );
		}
		return m311List;
	}

	public void setM311List(List<FiscalActivityInfo> m311List) {
		this.m311List = m311List;
	}

	public DataModel getM311Model() {
		if (m311Model == null) {
			setM311Model( new SerializableListDataModel( getM311List()) );
		}
		return m311Model;
	}
	public void setM311Model(DataModel m311Model) {
		this.m311Model = m311Model;
	}

	@Override
	public List<FiscalActivityInfo> getIrpfModulesList() {
		if (irpfModulesList == null) {
			setIrpfModulesList( new LinkedList<FiscalActivityInfo>() );
		}
		return irpfModulesList;
	}

	public void setIrpfModulesList(List<FiscalActivityInfo> irpfModulesList) {
		this.irpfModulesList = irpfModulesList;
	}
	
	public FiscalActivityInfo getInfoToDetail() {
		return infoToDetail;
	}
	public void setInfoToDetail(FiscalActivityInfo infoToDetail) {
		this.infoToDetail = infoToDetail;
	}
	
	public List<FiscalActivityInfo> getModulesDetailList() {
		if (getInfoToDetail() != null) {
			return getModulesDetailMap().get(getInfoToDetail().getInfoKey());
		}
		return null;
	}

	public Map<FiscalActivityInfoKey, List<FiscalActivityInfo>> getModulesDetailMap() {
		if (modulesDetailMap == null) {
			modulesDetailMap = new HashMap<FiscalActivityInfoKey, List<FiscalActivityInfo>>();
		}
		return modulesDetailMap;
	}

	public void setModulesDetailMap(
			Map<FiscalActivityInfoKey, List<FiscalActivityInfo>> modulesDetailMap) {
		this.modulesDetailMap = modulesDetailMap;
	}
	
	public DataModel getModulesDetailModel() {
		return modulesDetailModel;
	}

	public void setModulesDetailModel(DataModel modulesDetailModel) {
		this.modulesDetailModel = modulesDetailModel;
	}

	public DataModel getIrpfModules() {
		if (irpfModules == null) {
			setIrpfModules( new SerializableListDataModel( getIrpfModulesList() ) );
		}
		return irpfModules;
	}

	public void setIrpfModules(DataModel irpfModules) {
		this.irpfModules = irpfModules;
	}

	@Override
	public List<FiscalActivityInfo> getVatInfoList() {
		if (vatInfoList == null) {
			setVatInfoList( new LinkedList<FiscalActivityInfo>() );
		}
		return vatInfoList;
	}
	public void setVatInfoList(List<FiscalActivityInfo> vatInfoList) {
		this.vatInfoList = vatInfoList;
	}

	public DataModel getVatInfo() {
		if (vatInfo== null) {
			setVatInfo( new SerializableListDataModel( getVatInfoList() ) );
		}
		return vatInfo;
	}
	public void setVatInfo(DataModel vatInfo) {
		this.vatInfo = vatInfo;
	}
	
	@Override
	public List<FiscalActivityInfo> getIrpfInfoList() {
		if (irpfInfoList == null) {
			setIrpfInfoList( new LinkedList<FiscalActivityInfo>() );
		}
		return irpfInfoList;
	}

	public void setIrpfInfoList(List<FiscalActivityInfo> irpfInfoList) {
		this.irpfInfoList = irpfInfoList;
	}

	public DataModel getIrpfInfo() {
		if (irpfInfo == null) {
			setIrpfInfo( new SerializableListDataModel( getIrpfInfoList() ) );
		}
		return irpfInfo;
	}

	public void setIrpfInfo(DataModel irpfInfo) {
		this.irpfInfo = irpfInfo;
	}

	public boolean isSectorPanelVisible() {
		return sectorPanelVisible;
	}
	public void setSectorPanelVisible(boolean sectorPanelVisible) {
		this.sectorPanelVisible = sectorPanelVisible;
	}

	public boolean isDetailPanelVisible() {
		return detailPanelVisible;
	}
	public void setDetailPanelVisible(boolean detailPanelVisible) {
		this.detailPanelVisible = detailPanelVisible;
	}

	public Map<FiscalActivityInfoKey,List<SelectItem>> getKeyChoices() {
		if (keyChoices == null ) {
			keyChoices = new HashMap<FiscalActivityInfoKey, List<SelectItem>>();	
		}
		return keyChoices;
	}
	public void setKeyChoices(Map<FiscalActivityInfoKey,List<SelectItem>> keyChoices) {
		this.keyChoices = keyChoices;
	}
	
	public DataModel getSectors() {
		if (sectors == null) {
			
			try {
				setSectors( new SerializableListDataModel( getModules().getSectors( getFiscalActivity().isFarmer() ) ));
			} catch (AonException e) {
				AonUtil.addErrorMessage("Imposible recuperar los sectores de los epígrafes");
				setSectors( new SerializableListDataModel( ));
			} 
		}
		return sectors; 
	}
	public void setSectors(DataModel sectors) {
		this.sectors = sectors;
	}
	
	public List<Sector> getSectors(Object suggest) throws AonException {
		String sug = (String) suggest;
		sug = PERCENT + (sug!=null?(sug+ PERCENT):"");
		Modules modules = new Modules();
		return modules.getSectors(sug,getFiscalActivity().isFarmer());
	}
	
	public void onClearSector(ActionEvent event) {
		FiscalActivity fa = getFiscalActivity();
		fa.setSector(null);
		initialize();
	}
	
	public void onSectorSelected(ActionEvent event) {
		try {
			FiscalActivity fa = getFiscalActivity();
			Integer id = fa.getSector().getId();
			if (id != null) {
				Modules modules = new Modules();
				List<Epigrafe> list = modules.getEpigrafes( id );
				setEpigraphs( new SerializableListDataModel( list ) );
			} else {
				setEpigraphs( new SerializableListDataModel() );	
			}
		} catch (AonException e) {
			String msg =  "Imposible recuperar epígrafes";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}

	public DataModel getEpigraphs() {
		return epigraphs;
	}

	public void setEpigraphs(DataModel epigraphs) {
		this.epigraphs = epigraphs;
	}
	
	public void onSelectSector(ActionEvent event) {
		Sector sector = (Sector) getSectors().getRowData();
		FiscalActivity fa = getFiscalActivity();
		fa.setSector(sector);
		onSectorSelected(event);
		onHideSectors(event);
	}
	public void onHideSectors(ActionEvent event) {
		setSectorPanelVisible(false);
	}
	public void onShowSectors(ActionEvent event) {
		setSectorPanelVisible(true);
	}

	public void onSelectEpigraph(ActionEvent event) {
		Epigrafe epigrafe = (Epigrafe) getEpigraphs().getRowData();
		selectEpigrafe(epigrafe);
	}

	private void selectEpigrafe(Epigrafe epigrafe) {
		FiscalActivity fa =  getFiscalActivity();
		fa.setEpigraph(epigrafe.getCode());
		fa.setDescription(epigrafe.getDescription());
		fa.setMaxImport(fa.getSector().getMaxImport());
		fa.setMaxPerson(fa.getSector().getMaxPerson());
		fa.setVatPercent(fa.getSector().getVatPercent());
		if (!fa.isFarmer()) {
			fillInfo( fa );
			fillVatModules(fa, epigrafe );
			fillIrpfModules(fa, epigrafe );
			fillInfoChoices();
		}
		if (fa.getYear() < 2014) {
			fillM311( fa );
		}
		if (fa.getActivity() == null || fa.getActivity().getId() == null) {
			try {
				IManagerBean companyBean = BeanManager.getManagerBean(Company.class);
		    	Iterator<?> iterator = companyBean.getList(null).iterator();
		    	if (iterator.hasNext()) {
		    		Company company = (Company)iterator.next();

					IManagerBean bean = BeanManager.getManagerBean(EnterpriseActivity.class);
					Criteria criteria = new Criteria();
					criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ENTERPRISE_ACTIVITY_ENTERPRISE_ID), company.getId());
					criteria.addNullExpression(bean.getFieldName(IEntityAlias.ENTERPRISE_ACTIVITY_END_DATE));
					criteria.addOrder(bean.getFieldName(IEntityAlias.ENTERPRISE_ACTIVITY_PRINCIPAL), Boolean.FALSE);
					criteria.addOrder(bean.getFieldName(IEntityAlias.ENTERPRISE_ACTIVITY_DESCRIPTION));
					for (ITransferObject ito : bean.getList(criteria)) {
						EnterpriseActivity activity = (EnterpriseActivity)ito;
						IAE iae = activity.getIae();
						if (iae != null) {
							if (StringUtils.equals(epigrafe.getCode(),iae.getEpigraph())) {
								fa.setActivity(activity);
								break;
							}
						}
					}
				}
			} catch (ManagerBeanException e) {
				// Nothing
			}
		}
		
	}

	private void fillInfo(FiscalActivity fa) {
		setActivityInfoList(null);
		setInfo(null);
		int i = 0;
		FiscalActivityInfoKey[] keys = getCalculator().getActivityKeys( fa.getYear(), fa.getEpigraph() );
		for ( FiscalActivityInfoKey key : keys ) {
			FiscalActivityInfo info = new FiscalActivityInfo();
			info.setFiscalActivity(fa);
			info.setInfoKey(key);
			info.setValue(key.getDefaultValue());
			info.setLine(++i);
			getActivityInfoList().add(info);
		}
		for ( FiscalActivityInfoKey key : FiscalActivityInfoKey.values() ) {
			if (fa.getYear() >= key.getFromYear() && fa.getYear() <= key.getToYear()) {
				if (key.getType() == FiscalActivityInfoType.VAT_INFO) {
					FiscalActivityInfo info = new FiscalActivityInfo();
					info.setFiscalActivity(fa);
					info.setInfoKey(key);
					info.setValue(key.getDefaultValue());
					if (key == FiscalActivityInfoKey.V05) {
						info.setDoubleValue(fa.getVatPercent());	
					}
					info.setLine(++i);
					getVatInfoList().add(info);
				} else if (key.getType() == FiscalActivityInfoType.IRPF_INFO) {
					FiscalActivityInfo info = new FiscalActivityInfo();
					info.setFiscalActivity(fa);
					info.setInfoKey(key);
					info.setValue(key.getDefaultValue());
					info.setLine(++i);
					getIrpfInfoList().add(info);
				}
			}
		}
	}
	
	public void fillVatModules(FiscalActivity fa,Epigrafe epigrafe) {
		try {
			setVatModulesList(null);
			setVatModules(null);
			List<Mod> modules = getModules().getIVAMods(epigrafe);
			fillModulesList(fa, getVatModulesList() ,modules );
		} catch (AonException e) {
			String msg = "Imposible recuperar los módulos IVA";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}

	public void fillM311(FiscalActivity fa) {
		setM311List(null);
		setM311Model(null);
		if (!fa.isFarmer()) {
			for ( FiscalActivityInfoKey key : FiscalActivityInfoKey.values() ) {
				if (key.getType() == FiscalActivityInfoType.M311_DETAIL) {
					FiscalActivityInfo info = new FiscalActivityInfo();
					info.setFiscalActivity(fa);
					info.setInfoKey(key);
					info.setValue(key.getDefaultValue());
					info.setType(FiscalActivityInfoType.M311_DETAIL);
					getM311List().add(info);
				}
			}
		} else {
			for ( FiscalActivityInfoKey key : FiscalActivityInfoKey.values() ) {
				if (key.getType() == FiscalActivityInfoType.M311_FARMER_DETAIL) {
					FiscalActivityInfo info = new FiscalActivityInfo();
					info.setFiscalActivity(fa);
					info.setInfoKey(key);
					info.setValue(key.getDefaultValue());
					info.setType(FiscalActivityInfoType.M311_FARMER_DETAIL);
					getM311List().add(info);
				}
			}
		}
	}

	public void fillIrpfModules(FiscalActivity fa,Epigrafe epigrafe) {
		try {
			setIrpfModulesList(null);
			setIrpfModules(null);
			List<Mod> modules = getModules().getIRPFMods(epigrafe);
			fillModulesList(fa,getIrpfModulesList(), modules );
		} catch (AonException e) {
			String msg = "Imposible recuperar los módulos IRPF";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}
	
	private void fillModulesList(FiscalActivity fa,List<FiscalActivityInfo> list, List<Mod> modules) {
		for (Mod mod : modules ) {
			String keyStr =  FiscalActivityInfoKey.MODULE_PREFIX + 
					MODULE_KEY_FORMATTER.format(mod.getId());
			FiscalActivityInfoKey key = FiscalActivityInfoKey.valueOf(keyStr);
			FiscalActivityInfo info = new FiscalActivityInfo();
			info.setFiscalActivity(fa);
			info.setInfoKey(key);
			info.setValue(key.getDefaultValue());
			info.setUnit(mod.getUnit());
			info.setFactor(mod.getFactor());
			info.setLine(mod.getLine());
			list.add(info);
			if (key.isDetailed()) {
				fillDetailedModules(fa,key);
			}
		}
	}
	
	private void fillDetailedModules(FiscalActivity fa,FiscalActivityInfoKey key) {
		List<FiscalActivityInfo> list = getModulesDetailMap().get(key);
		if (list == null) {
			list = new LinkedList<FiscalActivityInfo>();
			getModulesDetailMap().put(key,list);	
		}
		for (FiscalActivityInfoKey det : getCalculator().getDetailedKeys(key) ) {
			FiscalActivityInfo info = new FiscalActivityInfo();
			info.setFiscalActivity(fa);
			info.setInfoKey(det);
			info.setValue(det.getDefaultValue());
			list.add(info);
		}
	}

	public void fillInfoChoices() {
		fillInfoChoices( getActivityInfoList() );
		fillInfoChoices( getIrpfInfoList() );
		fillInfoChoices( getIrpfModulesList() );
		fillInfoChoices( getVatInfoList() );
		fillInfoChoices( getVatModulesList() );
		fillInfoChoices( getM311List() );
	}

	private void fillInfoChoices(List<FiscalActivityInfo> infos) {
		for (FiscalActivityInfo info : infos ) {
			FiscalActivityInfoKey key = info.getInfoKey();
			fillInfoChoices(key);
		}
	}

	private void fillInfoChoices(FiscalActivityInfoKey key) {
		if ( key.isChoice() ) {
			List<SelectItem> list = new LinkedList<SelectItem>();
			for ( FiscalActivityInfoKeyEntry entry : key.getValues()) {
				list.add(new SelectItem( entry.getValue(), entry.getLabel()) );
			}
			getKeyChoices().put(key, list);
		}
		if (key.isDetailed()) {
			for (FiscalActivityInfoKey detailedKey : getCalculator().getDetailedKeys(key) ) {
				fillInfoChoices(detailedKey);	
			}
		}
	}

	public FiscalActivityInfo getInfo(FiscalActivityInfoKey key ) {
		for (FiscalActivityInfo info : getActivityInfoList() ) {
			if (info.getInfoKey() == key) {
				return info; 
			}
		}
		return null;
	}
	
	public void onChangeInfo(ActionEvent event)  {
		try {
			calculate();
		} catch (AonException e) {
			String msg = "Imposible recuperar los módulos IVA";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}
	
	public void onChangeVatModule(ActionEvent event) {
		try {
			FiscalActivityInfo info = (FiscalActivityInfo) getVatModules().getRowData();
			getCalculator().changeVatModule( info );
		} catch (AonException e) {
			String msg = "Error en el cálculo";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}
	public void onChangeIrpfModule(ActionEvent event) {
		try {
			FiscalActivityInfo info = (FiscalActivityInfo) getIrpfModules().getRowData();
			getCalculator().changeIrpfModule( info );
		} catch (AonException e) {
			String msg = "Error en el cálculo";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}
	public void onChangeM311(ActionEvent event) {
		try {
			calculateM311();
		} catch (AonException e) {
			String msg = "Error en el cálculo";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}
	public void onChangeIrpfInfo(ActionEvent event) {
		try {
			getCalculator().calculate();
		} catch (AonException e) {
			String msg = "Error en el cálculo";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}
	public void onChangeDetailModule(ActionEvent event) {
		try {
			FiscalActivityInfo info = (FiscalActivityInfo) getModulesDetailModel().getRowData();
			getCalculator().changeDetailModule( info );
		} catch (AonException e) {
			String msg = "Error en el cálculo";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}

	public void initialize() {
		setSectors(null);
		setEpigraphs(null);
		setSectorPanelVisible(false);
		setKeyChoices(null);
		setInfo(null);
		setActivityInfoList(null);
		setVatModulesList(null);
		setVatModules(null);
		setIrpfModulesList(null);
		setIrpfModules(null);
		setVatInfoList(null);
		setVatInfo(null);
		setM311List(null);
		setM311Model(null);
		setIrpfInfoList(null);
		setIrpfInfo(null);
		setModulesDetailMap(null);
		setModulesDetailModel(null);
		setInfoToDetail( null );
		setSelectedTab("InfoTab");
	}

	private void calculate() throws AonException {
		calculateVat();
		calculateIrpf();
		calculateM311();
	}

	public void calculateM311() throws AonException {
		FiscalActivity fa =  getFiscalActivity();
		if (fa.getYear() < 2014) {
			if (fa.isFarmer()) {
				getCalculator().calculateFarmerM311();
			} else {
				getCalculator().calculateM311();
			}
		}
	}

	private void calculateIrpf() {
		getCalculator().calculateIrpf(  );
	}

	private void calculateVat() {
		getCalculator().calculateVat();
	}
	
	public IModuleCalculator getCalculator() {
		if (calculator == null) {
			FiscalActivity fa =  getFiscalActivity();
			if (fa.getYear() < 2015) {
				setCalculator( new Aeat2012ModuleCalculator(this));
			} else {
				setCalculator( new Aeat2015ModuleCalculator(this));
			}
		}
		return calculator;
	}
	public void setCalculator(IModuleCalculator calculator) {
		this.calculator = calculator;
	}

	public void onHideDetailPanel(ActionEvent event) {
		setDetailPanelVisible(false);
	}
	public void onShowIRPFDetailPanel(ActionEvent event) {
		FiscalActivityInfo info = (FiscalActivityInfo) getIrpfModules().getRowData();
		setInfoToDetail( info );
		FiscalActivityInfoKey key = info.getInfoKey();
		showDetailPanel(key);
	}
	public void onShowVATDetailPanel(ActionEvent event) {
		FiscalActivityInfo info = (FiscalActivityInfo) getVatModules().getRowData();
		setInfoToDetail( info );
		FiscalActivityInfoKey key = info.getInfoKey();
		showDetailPanel(key);
	}
	private void showDetailPanel(FiscalActivityInfoKey key) {
		setDetailPanelVisible(true);
		List<FiscalActivityInfo> list = getModulesDetailMap().get(key);
		setModulesDetailModel(new SerializableListDataModel( list ));
	}
	
}
