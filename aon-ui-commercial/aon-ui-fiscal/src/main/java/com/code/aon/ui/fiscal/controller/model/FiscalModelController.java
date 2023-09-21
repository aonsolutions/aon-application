package com.code.aon.ui.fiscal.controller.model;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.company.Company;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.finance.Creditor;
import com.code.aon.finance.Finance;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.fiscal.FiscalActivity;
import com.code.aon.fiscal.FiscalModel;
import com.code.aon.fiscal.FiscalModelDetail;
import com.code.aon.fiscal.enumeration.FiscalModelStatus;
import com.code.aon.fiscal.enumeration.FiscalModelType;
import com.code.aon.fiscal.enumeration.Period;
import com.code.aon.fiscal.model.FiscalModelManagerFactory;
import com.code.aon.fiscal.model.IFiscalDeclaration;
import com.code.aon.fiscal.model.IFiscalModelManager;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryBank;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.enumeration.DocumentType;
import com.code.aon.registry.enumeration.RegistryType;
import com.code.aon.ui.common.controller.IAuditableController;
import com.code.aon.ui.company.controller.CompanyCollectionsController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.finance.controller.FinanceController;
import com.code.aon.ui.fiscal.controller.FiscalParametersController;
import com.code.aon.ui.fiscal.controller.IFiscalModelController;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public abstract class FiscalModelController extends BasicController implements IFiscalModelController, IAuditableController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private final static String DATA_TAB = "headerData";
	private final static String LIQUIDATION_TAB = "liquidationTab";
	private final static String FINANCE_CONTROLLER_NAME = "finance";	
	
	private IFiscalDeclaration declaration;
	private IFiscalModelManager manager;
	private FileOutput fileOutput;
	private String selectedTab;
	private boolean showAuditInfoWindow;
	private boolean finalizePanelVisible;
	
	private List<FiscalModel> previousDeclarations;
	private String previousDeclarationDocument;
	
	private RegistryBank registryBank;
	
	public String getDataTabName() {
		return DATA_TAB;  
	}
	public String getLiquidationTabName () {
		return LIQUIDATION_TAB;  
	}

	private Company getCompany() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Company.class);
		List<ITransferObject> list = bean.getList(null);
		if (list != null && list.size() > 0 ){
			return (Company) list.get(0);
		}
		throw new ManagerBeanException("No se encuentran los 'Datos de la empresa'. ");
	}

	public IFiscalModelManager getFiscalModelManager() throws AonException {
		if (manager == null) {
			String domainName = AonUtil.getDomainName();
			FiscalModelManagerFactory factory = new FiscalModelManagerFactory(domainName);
			manager = factory.getManager(getModelType());
		}
		return manager;
	}

	private Registry getAdmonCreditor() {
		FiscalParametersController fpc = (FiscalParametersController) AonUtil
				.getRegisteredBean(FiscalParametersController.FISCAL_PARAMS_BEAN_NAME);
		Creditor creditor = fpc.getAdmonCreditor();
		return (creditor == null ? null : creditor.getRegistry());
	}
	
	public String getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}

	public FileOutput getFileOutput() {
		return fileOutput;
	}

	public void setFileOutput(FileOutput fileOutput) {
		this.fileOutput = fileOutput;
	}

	public boolean isFinalizePanelVisible() {
		return finalizePanelVisible;
	}

	public void setFinalizePanelVisible(boolean finalizePanelVisible) {
		this.finalizePanelVisible = finalizePanelVisible;
	}
	
	public boolean isMultiDeclarationEnabled() {
		return (getModelType() == FiscalModelType.M130 
				&& getPreviousDeclarations() != null 
				&& getPreviousDeclarations().size() > 0);
	}

	public boolean isShowAuditInfoWindow() {
		return showAuditInfoWindow;
	}
	public void setShowAuditInfoWindow(boolean showAuditInfoWindow) {
		this.showAuditInfoWindow = showAuditInfoWindow;
	}
	
	public RegistryBank getRegistryBank() {
		return registryBank;
	}

	public void setRegistryBank(RegistryBank registryBank) {
		this.registryBank = registryBank;
	}

	public IFiscalDeclaration getDeclaration() {
		return declaration;
	}

	public void setDeclaration(IFiscalDeclaration fiscalModel) {
		this.declaration = fiscalModel;
	}

	public void initialize() throws AonException {
		FiscalModel to = (FiscalModel) getTo();
		FiscalParametersController fiscalParams = (FiscalParametersController) AonUtil
				.getRegisteredBean(FiscalParametersController.FISCAL_PARAMS_BEAN_NAME);
		String defYear = fiscalParams.getDefaultYear();
		to.setYear(StringUtils.isEmpty(defYear) ? null : Integer.parseInt(defYear));
		Administration admon = fiscalParams.getDefaultAdministration();
		if (admon == null) {
			to.setAdministration(Administration.COMMON_TERRITORY);
		}
		if (admon != null) {
			to.setAdministration(admon);
			if (admon == Administration.COMMON_TERRITORY) {
				to.setAdmonAeat(fiscalParams.getAdministrationCode());
			}
		}
		
		to.setModel(getModelType());
		setDeclaration((getFiscalModelManager().initializeFiscalModel(to)));
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(getFieldName(IEntityAlias.FISCAL_MODEL_MODEL), getModelType());
		criteria.addOrder(getFieldName(IEntityAlias.FISCAL_MODEL_YEAR), false);
		criteria.addOrder(getFieldName(IEntityAlias.FISCAL_MODEL_PERIOD), false);
		List<ITransferObject> list = getManagerBean().getList(criteria);
		setPreviousDeclarations(null);
		setPreviousDeclarationDocument(null);
		if (list != null) {
			if (list.size() == 1) {
				FiscalModel fs = (FiscalModel) list.get(0);
				initializeData(fs);
			} else {
				setPreviousDeclarations( new LinkedList<FiscalModel>());
				for (ITransferObject t : list) {
					FiscalModel fs = (FiscalModel) t;
					boolean found = false;
					for (FiscalModel fm : previousDeclarations) {
						if (StringUtils.isBlank(fs.getDocument())
							|| StringUtils.equals(fs.getDocument(), fm.getDocument())) {
							found = true;
							break;
						}
					}
					if (!found) {
						previousDeclarations.add(fs);	
					}
				}
				if (previousDeclarations.size() == 1) {
					setPreviousDeclarations(null);
					setPreviousDeclarationDocument(null);
				}
			}
		}
		
		Company company = getCompany();
		if (StringUtils.isEmpty(to.getDocument())) {
			to.setDocument(company.getDocument());	
		}
		to.setCompanyDocument(company.getDocument());
		to.setParticipationPercent(100.0);
		
		if (StringUtils.isEmpty(to.getName())) {
			String name = company.getName();
			if (company.getRegistry().getType() == RegistryType.NATURAL 
				|| company.getDocumentType() != DocumentType.CIF) {
				if (StringUtils.contains(name, ',')) {
					to.setName(StringUtils.trim(StringUtils.substringAfter(name, ",")));
					to.setSurname(StringUtils.trim(StringUtils.substringBefore(name, ",")));
				} else {
					to.setName(StringUtils.trim(StringUtils.substringBefore(name, " ")));
					to.setSurname(StringUtils.trim(StringUtils.substringAfter(name, " ")));
				}
			} else {
				to.setName(name);	
				to.setSurname(null);
			}
		}
		
		if (StringUtils.isEmpty(to.getStreetName())) {
			RegistryAddress address = company.getDefaultAddress();
			if (address != null) {
				to.setStreetInitial(address.getStreetType().getValue());
				to.setStreetName( StringUtils.left(address.getAddress(),17) );
				to.setStreetNumber( address.getNumber() ); 
				to.setTown( StringUtils.left(address.getCity(),20));
				to.setProvince(StringUtils.left(address.getGeozone()==null?"":address.getGeozone().getName(),15));
				to.setZip("00000");
				if (address.getZip() != null){
					to.setZip(address.getZip());
				}
			}
		}
		
		if (StringUtils.isEmpty(to.getPhone())) {
			RegistryMedia  phone = company.getPhone();
			if (phone != null){
				to.setPhone(phone.getValue() );
			}
		}
		
		if (StringUtils.isEmpty(to.getContactPerson())) {
			to.setContactPerson( fiscalParams.getContactPerson() );
		}		
		if (StringUtils.isEmpty(to.getContactPhone())) {
			to.setContactPhone(fiscalParams.getContactPhone() );
		}		
		if (StringUtils.isEmpty(to.getContactCellular())) {
			to.setContactCellular( fiscalParams.getContactCellular() );
		}		
		if (StringUtils.isEmpty(to.getContactEmail())) {
			to.setContactEmail( fiscalParams.getContactMail() );
		}		
	}
	
	private void initializeData(FiscalModel fs) {
		FiscalModel to = (FiscalModel) getTo();
		to.setDocument(fs.getDocument());
		to.setName(fs.getName());
		to.setSurname(fs.getSurname());
		
		to.setStreetInitial(fs.getStreetInitial());
		to.setStreetName( fs.getStreetName() );
		to.setStreetNumber( fs.getStreetNumber() ); 
		to.setTown( fs.getTown());
		to.setProvince(fs.getProvince());
		to.setZip( fs.getZip() );
		
		to.setPhone(fs.getPhone() );
		
		to.setContactPerson( fs.getContactPerson() );
		to.setContactPhone(fs.getContactPhone() );
		to.setContactCellular( fs.getContactCellular() );
		to.setContactEmail( fs.getContactEmail() );
	}
	
	public List<FiscalModel> getPreviousDeclarations() {
		return previousDeclarations;
	}
	public void setPreviousDeclarations(List<FiscalModel> previousDeclarations) {
		this.previousDeclarations = previousDeclarations;
	}
	public String getPreviousDeclarationDocument() {
		return previousDeclarationDocument;
	}
	public void setPreviousDeclarationDocument(String previousDeclarationDocument) {
		this.previousDeclarationDocument = previousDeclarationDocument;
	}
	public List<SelectItem> getPreviousDeclarationDocuments() {
		List<SelectItem> list = new LinkedList<SelectItem>();
		list.add(new SelectItem(null," ------- "));
		for ( FiscalModel fm : getPreviousDeclarations()) {
			String name = fm.getName(); 
			String surname = fm.getSurname();
			list.add(new SelectItem(fm.getDocument(),(name==null?"":(name + " ") + surname)));
		}
		return list;
	}

	public void initializeDetails() throws AonException {
		setDeclaration((getFiscalModelManager()
				.initializeFiscalModelDetails(getDeclaration())));
		insertOrUpdateDetails();
	}

	public void load() throws AonException {
		setPreviousDeclarations(null);
		setPreviousDeclarationDocument(null);
		FiscalModel to = (FiscalModel) getTo();
		setDeclaration((getFiscalModelManager().loadFiscalModel(to)));
	}

	public void unload() throws AonException {
		setPreviousDeclarations(null);
		setPreviousDeclarationDocument(null);
		setDeclaration(null);
	}

	public void onShowFinalizePanel(ActionEvent event) {
		FiscalModel to = (FiscalModel) getTo();
		if (mustCreateFinance(to)) {
			setFinalizePanelVisible(true);
			setRegistryBank(null);
			to.getFinance().setRegistry(getAdmonCreditor());
		} else {
			finish(to, event);
		}
	}

	public void onHideFinalizePanel(ActionEvent event) {
		setFinalizePanelVisible(false);
		setRegistryBank(null);
	}

	public void onFinish(ActionEvent event) {
		try {
			FiscalModel to = (FiscalModel) getTo();
			if (mustCreateFinance(to)) {
				createFinance(to);
			}
			finish(to, event);
			onHideFinalizePanel(event);
		} catch (Exception e) {
			e.printStackTrace();
			String msg = "No se pueden finalizar la declaración."
					+ e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	private void finish(FiscalModel to, ActionEvent event) {
		to.setStatus(FiscalModelStatus.FINISHED);
		accept(event);
	}

	public List<SelectItem> getActiveBanks() throws ManagerBeanException {
		CompanyCollectionsController c = (CompanyCollectionsController) AonUtil
				.getRegisteredBean(ICompanyConstants.COLLECTIONS_CONTROLLER_NAME);
		return c.getActiveCompanyBanks();
	}

	protected boolean mustCreateFinance(FiscalModel to) {
		if (getDeclaration().isWithoutActivityDeclarationAvailable()
				&& to.isWithoutActivity()) {
			return false;
		}
		if (getDeclaration().isToDeductDeclarationAvailable()
				&& getDeclaration().isToDeduct()) {
			return false;
		}
		if (getDeclaration().isDeclarationNegativeAvailable()
				&& getDeclaration().isNegative()) {
			return false;
		}
		if (getDeclaration().isCompensateDeclarationAvailable()
				&& getDeclaration().isCompensate()) {
			return false;
		}
		if (getDeclaration().getResult() == 0) {
			return false;
		}
		return true;
	}

	public boolean isBankAccountVisible() {
		FiscalModel to = (FiscalModel) getTo();
		Finance finance = to.getFinance();
		if (finance != null && finance.getPayMethod() != null
				&& finance.getPayMethod().getType() != PayMethodType.CASH_BASIS) {
			return true;
		}
		return false;
	}

	private void createFinance(FiscalModel to) throws ManagerBeanException {
		Finance finance = to.getFinance();
		Registry registry = finance.getRegistry();
		if (registry == null || registry.getId() == null) {
			throw new ManagerBeanException(
					"No se ha indicado un acreedor válido.");
		}
		finance.setRegistryDocument(registry.getDocument());
		finance.setRegistryDocumentCountry(registry.getDocumentCountry());
		finance.setRegistryDocumentType(registry.getDocumentType());
		finance.setRegistryName(registry.getName());
		finance.setPayment(true);
		finance.setSecurityLevel(to.getSecurityLevel());
		finance.setAmount(declaration.getResult());
		if (finance.getPayMethod() == null) {
			throw new ManagerBeanException(
					"No se ha indicado una forma de pago válida.");
		}
		if (finance.getPayMethod().getType() == PayMethodType.CASH_BASIS) {

		} else if (finance.getPayMethod().getType() == PayMethodType.NEGOTIABLE_DOCUMENT) {
			finance.setBankAccount(getRegistryBank().getBankAccount());
			finance.setBankAlias(getRegistryBank().getBankAlias());
			finance.setBic(getRegistryBank().getBic());
		}
		IManagerBean bean = BeanManager.getManagerBean(Finance.class);
		bean.restoreNullSubPOJOs(finance);
		finance.setFinanceStatus(FinanceStatus.PENDING);
		finance.setConcept(to.getModel() + " " + to.getYear() + "-"
				+ to.getPeriod());
		finance = (Finance) bean.insert(finance);
		to.setFinance(finance);
	}

	public void onReopen(ActionEvent event) {
		try {
			FiscalModel to = (FiscalModel) getTo();
			Finance finance = to.getFinance();
			if (finance.getId() != null
					&& finance.getFinanceStatus() != FinanceStatus.PENDING) {
				throw new ManagerBeanException(
						"No se pueden reabrir la declaración. El vencimiento asociado no está pendiente");
			}
			to.setStatus(FiscalModelStatus.PENDING);
			to.setFinance(null);
			accept(event);
			if (finance.getId() != null) {
				IManagerBean bean = BeanManager.getManagerBean(Finance.class);
				bean.remove(finance);
			}
		} catch (Exception e) {
			String msg = "No se pueden reabrir la declaración."
					+ e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}

	}

	public void onRecalculate(ActionEvent event) {
		try {
			getDeclaration().calculate();
		} catch (AonException e) {
			String msg = "No se pudo recalcular la declaración";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}

	public void insertOrUpdateDetails() throws AonException {
		getDeclaration().calculate();
		FiscalModel to = (FiscalModel) getTo();
		IManagerBean bean = BeanManager.getManagerBean(FiscalModelDetail.class);
		for (FiscalModelDetail detail : getDeclaration().getDetails()) {
			detail.setFiscalModel(to);
			detail = (FiscalModelDetail) bean.insertOrUpdate(detail);
		}
	}

	public void removeDetails() throws AonException {
		IManagerBean bean = BeanManager.getManagerBean(FiscalModelDetail.class);
		for (FiscalModelDetail detail : getDeclaration().getDetails()) {
			bean.remove(detail);
		}
	}

	public boolean isDiskOk() {
		int errors = 0;
		if (getFileOutput() != null) {
			errors = getFileOutput().getErrors().size();
		}
		return (errors == 0);
	}
	
	public void downloadDisk(ActionEvent event) throws ManagerBeanException {
		try {
			FacesContext faces = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) faces
					.getExternalContext().getResponse();
			String fileName = getAutomaticFileName();
			MimeType mimeType = getMimeType();
			response.setCharacterEncoding("ISO-8859-1");
			response.setHeader("Content-disposition", "attachment; filename=\""
					+ fileName + "." + mimeType.getExtension() + "\";");
			ServletOutputStream output = response.getOutputStream();
			InputStream input = getFileOutput().getFile() != null ? new FileInputStream(
					getFileOutput().getFile()) : new ByteArrayInputStream(
					getFileOutput().getContent());
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

	protected String getAutomaticFileName() {
		FiscalModel fm = (FiscalModel) getTo();
		
		String s = fm.getName() + fm.getSurname();
	    StringBuilder sb = new StringBuilder();
	    if(!Character.isJavaIdentifierStart(s.charAt(0))) {
	        sb.append("_");
	    }
	    for (char c : s.toCharArray()) {
	        if(Character.isJavaIdentifierPart(c)) {
	            sb.append(c);
	        }
	    }		
		
		return fm.getModel() 
				+ "_" + fm.getYear() 
				+ "_" + fm.getPeriod()
				+ "_" + sb.toString();
	}
	
	
	protected void checkFiscalActivity(FiscalModelType type) {
		try {
			int year = Calendar.getInstance().get(Calendar.YEAR);
			IManagerBean bean = BeanManager
					.getManagerBean(FiscalActivity.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(
					bean.getFieldName(IEntityAlias.FISCAL_ACTIVITY_YEAR), year);
			int count = bean.getCount(criteria);
			if (count == 0) {
				AonUtil.addErrorMessage("No se ha realizado la introducción de los datos previos de actividades, necesarios para la confección del impuesto del ejercicio " + year +".");
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
		}
	}

	public boolean isActivityButtonEnabled() {
		return (getModelType() == FiscalModelType.M303); 
	}
	protected abstract FiscalModelType getModelType();
	protected abstract String getFormPage();

	public abstract boolean isDifEnabled();

//	public abstract String getFileName();

	public abstract MimeType getMimeType();

	public abstract void onCreateDisk(ActionEvent event);

	public void onLoadFinance(ActionEvent event) throws ManagerBeanException {
		Finance finance = ( (FiscalModel) getTo()).getFinance();
		if (finance.getId() != null) {
			String backAction = getBeanName() + "_form";
			FinanceController financeController = (FinanceController) AonUtil.getRegisteredBean(FINANCE_CONTROLLER_NAME);
			financeController.setPayment(true);
			financeController.onLoad(event, finance.getId(), backAction, null);
		}
	}
	
	public boolean isAeatValidable() {
		FiscalModel to = (FiscalModel) getTo();
		return ( !isNevv() 
			&& to.isFinished() 
			&& to.getAdministration() == Administration.COMMON_TERRITORY
			&& to.getYear() < 2015
			&& (to.getModel() == FiscalModelType.M303)
			&& isScriptPresent());
	}

	public boolean isAeatReportEnabled() {
		FiscalModel to = (FiscalModel) getTo();
		return (!isNevv() && to.isFinished() && isScriptPresent()
			 && (to.getModel() == FiscalModelType.M303)
				);
	}
	public boolean isAeatOfficialReportEnabled() {
		FiscalModel to = (FiscalModel) getTo();
		return ( isAeatReportEnabled() 
			&& (to.isCashBasis() 
				|| getDeclaration().isNegative()
				|| getDeclaration().isToDeduct()
				|| getDeclaration().isCompensate())
			);
	}

	public boolean isAeatDraftReportEnabled() {
		FiscalModel to = (FiscalModel) getTo();
		return ( isAeatReportEnabled() 
				&& !to.isCashBasis() 
				&& !getDeclaration().isNegative()
				&& !getDeclaration().isToDeduct()
				&& !getDeclaration().isCompensate());
	}
	
	public boolean isScriptPresent() {
		return false;
	}

	public String getAeatWebPage() {
		return null; 
	}

	public String aeatReport() {
		return null;
	}

	protected String validateAeatFile() {
		return null;
	}
	
	public boolean isParticipationPercentEnabled() {
		return (getModelType() == FiscalModelType.M130);
	}
	
	public void onSelectPreviousDeclarationDocument(ActionEvent event){
		for (FiscalModel fm : getPreviousDeclarations()) {
			if (StringUtils.equals(fm.getDocument(), getPreviousDeclarationDocument())) {
				initializeData(fm);
				initializeCustomData(fm);
				break;
			}
		}
	}
	protected void initializeCustomData(FiscalModel fm) {
		FiscalModel fiscalModel = (FiscalModel) getTo();
		fiscalModel.setParticipationPercent(null);
	}
	
	@Override
	public String editModel(Administration administration, int year,Period period) throws ManagerBeanException {
		onEditSearch(null);
		Criteria criteria = getCriteria();
		String yearAlias = getManagerBean().getFieldName(IEntityAlias.FISCAL_MODEL_YEAR); 
		String periodAlias = getManagerBean().getFieldName(IEntityAlias.FISCAL_MODEL_PERIOD);
		String admonAlias = getManagerBean().getFieldName(IEntityAlias.FISCAL_MODEL_ADMINISTRATION);
		criteria.addEqualExpression(yearAlias,year);
		criteria.addEqualExpression(periodAlias,period);
		criteria.addEqualExpression(admonAlias,administration);
		setCriteria(criteria);
		onSearch(null);
		getModel().setRowIndex(0);
		onSelect(null);
		return getFormPage();
	}

	@Override
	public String newModel(Administration administration, int year,
			Period period) throws ManagerBeanException{
		onReset(null);
		FiscalModel fm = (FiscalModel) getTo();
		fm.setYear(year);
		fm.setPeriod(period);
		fm.setAdministration(administration);
		return getFormPage();
	}	

	@Override
	public void printModel(Administration administration, int year,Period period) throws ManagerBeanException {
		onEditSearch(null);
		Criteria criteria = getCriteria();
		String yearAlias = getManagerBean().getFieldName(IEntityAlias.FISCAL_MODEL_YEAR); 
		String periodAlias = getManagerBean().getFieldName(IEntityAlias.FISCAL_MODEL_PERIOD);
		String admonAlias = getManagerBean().getFieldName(IEntityAlias.FISCAL_MODEL_ADMINISTRATION);
		criteria.addEqualExpression(yearAlias,year);
		criteria.addEqualExpression(periodAlias,period);
		criteria.addEqualExpression(admonAlias,administration);
		setCriteria(criteria);
		onSearch(null);
		getModel().setRowIndex(0);
		onSelect(null);
	}
}
