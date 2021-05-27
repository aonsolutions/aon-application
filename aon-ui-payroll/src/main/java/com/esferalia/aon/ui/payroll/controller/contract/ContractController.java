package com.esferalia.aon.ui.payroll.controller.contract;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.enumeration.Month;
import com.code.aon.company.Enterprise;
import com.code.aon.company.WorkPlace;
import net.aonsolutions.core.dbutils.DatabaseUtil;
import net.aonsolutions.core.pool.AonConnectionException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.RegistryDirStaff;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.report.OutputFormat;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.report.controller.ReportManager;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.file.payroll.contract.pdf.ModelOption;
import com.esferalia.aon.file.payroll.contrata.ContrataContratoParams;
import com.esferalia.aon.file.payroll.contrata.ContrataProrrogaParams;
import com.esferalia.aon.file.payroll.contrata.ContrataTransformacionesParams;
import com.esferalia.aon.payroll.Agreement;
import com.esferalia.aon.payroll.AgreementLevel;
import com.esferalia.aon.payroll.AgreementLevelCategory;
import com.esferalia.aon.payroll.AgreementLevelData;
import com.esferalia.aon.payroll.CNO;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractAttachment;
import com.esferalia.aon.payroll.ContractBonus;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.ContractDeduction;
import com.esferalia.aon.payroll.ContractInfo;
import com.esferalia.aon.payroll.ContractInfo.ContractSepeStatus;
import com.esferalia.aon.payroll.ContractInfo.ContractSsStatus;
import com.esferalia.aon.payroll.ContractInfo.ContractVariable;
import com.esferalia.aon.payroll.ContractPayment;
import com.esferalia.aon.payroll.EnterpriseActivity;
import com.esferalia.aon.payroll.EnterpriseCCC;
import com.esferalia.aon.payroll.PayrollWorkPlace;
import com.esferalia.aon.payroll.SystemData;
import com.esferalia.aon.payroll.TrainingCenter;
import com.esferalia.aon.payroll.TrainingCourse;
import com.esferalia.aon.payroll.enumeration.CCCType;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractAttachmentType;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.ContractDuration;
import com.esferalia.aon.payroll.enumeration.ContractModelCode;
import com.esferalia.aon.payroll.enumeration.ContractOption;
import com.esferalia.aon.payroll.enumeration.ContractStatus;
import com.esferalia.aon.payroll.enumeration.ContractType;
import com.esferalia.aon.payroll.enumeration.ContractWorkingDay;
import com.esferalia.aon.payroll.enumeration.OccupationType;
import com.esferalia.aon.payroll.enumeration.QuoteGroup;
import com.esferalia.aon.payroll.enumeration.QuoteType;
import com.esferalia.aon.payroll.enumeration.SuspensionCause;
import com.esferalia.aon.payroll.enumeration.TaxationType;
import com.esferalia.aon.payroll.enumeration.certificados.TLDCAUSS;
import com.esferalia.aon.payroll.enumeration.ss.T53;
import com.esferalia.aon.payroll.enumeration.ss.T54;
import com.esferalia.aon.payroll.enumeration.ss.T55;
import com.esferalia.aon.salary.enumeration.BonusType;
import com.esferalia.aon.ui.payroll.controller.ContractInfoController;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.controller.PayrollAppParamsController;
import com.esferalia.aon.ui.payroll.controller.TrainingCenterController;
import com.esferalia.aon.ui.payroll.utils.ContractUtils;
import com.esferalia.aon.ui.payroll.utils.PayrollUtils;
import com.esferalia.aon.ui.sepe.controller.CertificadosController;
import com.esferalia.aon.ui.sepe.controller.ContrataContratosController;
import com.esferalia.aon.ui.sepe.controller.ContrataProrrogasController;
import com.esferalia.aon.ui.sepe.controller.ContrataTransformacionesController;
import com.esferalia.aon.ui.sepe.controller.ISepeConstants;
import com.esferalia.aon.ui.sepe.utils.SEPEUtils;
import com.esferalia.aon.watson.util.AonUtils;

public class ContractController extends BasicController {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ContractController.class);
	
	final static String ADDITIONAL_CLAUSES_TAB_NAME = "additionalClausesData";
	
	private Enterprise enterprise;
	private List<SelectItem> workPlaces;
	private List<SelectItem> enterpriseCCCs;
	private List<SelectItem> activities;
	private Agreement agreement;
	
	private ContractParams params;
	private ContractUtils contractUtils;

	private SalaryInfoHandler salaryInfoHandler;

	private boolean showNewContractModal;
	private boolean showContractEmbargoWindow;
	private boolean showContractBonusWindow;
	private boolean showContractSalaryInfoWindow;
	private boolean showWorkdayHoursWindow;
	
	private boolean showContrataCommunicationWindow;
	private boolean showCertifica2CommunicationWindow;
	
	private boolean showDocumentView;
	
	private WorkdayManager workdayManager;
	
	private AgreementLevelCategory agreementLevelCategory;
	
	
	public AgreementLevelCategory getAgreementLevelCategory() {
		return agreementLevelCategory;
	}
	public void setAgreementLevelCategory(AgreementLevelCategory agreementLevelCategory) {
		Contract contract = (Contract) getTo();
		this.agreementLevelCategory = agreementLevelCategory;
		if  ( agreementLevelCategory == null )
			contract.setAgreementLevel(null);
		else { 
			contract.setAgreementLevel(agreementLevelCategory.getLevel());
			contract.setCategoryDescription(agreementLevelCategory.getDescription());
		}
	}

	public void setAgreementLevelCategoryOnly(AgreementLevelCategory agreementLevelCategory) {
		this.agreementLevelCategory = agreementLevelCategory;
	}

	public WorkdayManager getWorkdayManager() {
		if(workdayManager==null){
			workdayManager = new WorkdayManager();
		}
		return workdayManager;
	}
	public void setWorkdayManager(WorkdayManager workdayManager) {
		this.workdayManager = workdayManager;
	}
	
	public SalaryInfoHandler getSalaryInfoHandler() {
		return salaryInfoHandler;
	}
	public void setSalaryInfoHandler(SalaryInfoHandler salaryInfoHandler) {
		this.salaryInfoHandler = salaryInfoHandler;
	}
	public boolean isRowContractRetaQuote() {
		try {
			return isContractRetaQuote((Contract) getModel().getRowData());
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage());
		}
		return false;
	}
	public boolean isContractRetaQuote(Contract contract) {
		String value = getContractUtils().getInfoCurrentValue(contract, ContractVariable.SELF_EMPLOYED.getValue());
		return new Boolean( value );
	}
	public boolean isRowCooperativePartnerQuote() {
		try {
			return isCooperativePartnerQuote((Contract) getModel().getRowData());
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage());
		}
		return false;
	}
	public boolean isCooperativePartnerQuote(Contract contract) {
		String value = getContractUtils().getInfoCurrentValue(contract, ContractVariable.COOPERATIVE_PARTNER.getValue());
		return new Boolean( value );
	}
	public boolean isRowContractInternship() {
		try {
			return isContractInternship((Contract) getModel().getRowData());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return false;
	}
	public boolean isContractInternship(Contract contract) {
		String code = getContractUtils().getDataCurrentValue(contract, ContextVariable.TC2.getName());
		return contract.getEnterpriseCCC()!=null && contract.getEnterpriseCCC().getType()==CCCType.FELLOWS 
				&& code!=null && code.equals("000");
	}
	
	public String getRowContractCode(){
		try {
			Contract contract = (Contract) getModel().getRowData();
			String code = getContractUtils().getDataCurrentValue(contract, ContextVariable.TC2.getName());
			ContractCode contractCode = ContractCode.getContractCodeByValue(code);
			if(contractCode!=null)
				return code + StringUtils.repeat(" [+]", getExtensionCount(contract)) + ", "
						+ contractCode.getName(FacesContext.getCurrentInstance().getViewRoot().getLocale());
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage());
		}
		return "";
	}
	
	public ContractUtils getContractUtils() {
		if(contractUtils==null){
			contractUtils = ContractUtils.getInstance();
		}
		return contractUtils;
	}
	public void setContractUtils(ContractUtils contractUtils) {
		this.contractUtils = contractUtils;
	}
	
	public ContractParams getParams() {
		if(params==null){
			params = new ContractParams();
		}
		return params;
	}
	public void setParams(ContractParams params) {
		this.params = params;
	}
	
	public boolean isShowNewContractModal() {
		return showNewContractModal;
	}
	public void setShowNewContractModal(boolean showNewContractModal) {
		this.showNewContractModal = showNewContractModal;
	}
	public boolean isShowContractEmbargoWindow() {
		return showContractEmbargoWindow;
	}
	public void setShowContractEmbargoWindow(boolean showContractEmbargoWindow) {
		this.showContractEmbargoWindow = showContractEmbargoWindow;
	}
	public boolean isShowContractSalaryInfoWindow() {
		return showContractSalaryInfoWindow;
	}
	public void setShowContractSalaryInfoWindow(boolean showContractSalaryInfoWindow) {
		this.showContractSalaryInfoWindow = showContractSalaryInfoWindow;
	}
	public boolean isShowContractBonusWindow() {
		return showContractBonusWindow;
	}
	public void setShowContractBonusWindow(boolean showContractBonusWindow) {
		this.showContractBonusWindow = showContractBonusWindow;
	}
	public boolean isShowWorkdayHoursWindow() {
		return showWorkdayHoursWindow;
	}
	public void setShowWorkdayHoursWindow(boolean showWorkdayHoursWindow) {
		this.showWorkdayHoursWindow = showWorkdayHoursWindow;
	}
	public boolean isShowCertifica2CommunicationWindow() {
		return showCertifica2CommunicationWindow;
	}
	public void setShowCertifica2CommunicationWindow(boolean showCertifica2CommunicationWindow) {
		this.showCertifica2CommunicationWindow = showCertifica2CommunicationWindow;
	}
	public boolean isShowContrataCommunicationWindow() {
		return showContrataCommunicationWindow;
	}
	public void setShowContrataCommunicationWindow(boolean showContrataCommunicationWindow) {
		this.showContrataCommunicationWindow = showContrataCommunicationWindow;
	}
	public Agreement getAgreement() {
		return agreement;
	}
	public void setAgreement(Agreement agreement) {
		this.agreement = agreement;
	}
	
	public Enterprise getEnterprise() {
		return enterprise;
	}
	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}

	public boolean isShowDocumentView() {
		return showDocumentView;
	}
	public void setShowDocumentView(boolean showDocumentView) {
		this.showDocumentView = showDocumentView;
	}
	
	public List<SelectItem> getWorkPlaces() {
		if(workPlaces==null){
			loadWorkPlaces();
		}
		return workPlaces;
	}
	public void setWorkPlaces(List<SelectItem> workPlaces) {
		this.workPlaces = workPlaces;
	}
	
	public List<SelectItem> getEnterpriseCCCs() {
		if(enterpriseCCCs==null){
			loadEnterpriseCCCs();
		}
		return enterpriseCCCs;
	}
	public void setEnterpriseCCCs(List<SelectItem> enterpriseCCCs) {
		this.enterpriseCCCs = enterpriseCCCs;
	}

	public List<SelectItem> getActivities() {
		if(activities==null){
			loadActivities();
		}
		return activities;
	}
	public void setActivities(List<SelectItem> activities) {
		this.activities = activities;
	}
	
	public boolean isRemovable(){
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			Contract contract = (Contract) this.getTo();
			String salarySelect = "SELECT count(*) FROM salary WHERE contract = " + contract.getId();
			ps = conn.prepareStatement(salarySelect);
			ResultSet rs = ps.executeQuery();
			rs.next();
			if (rs.getInt(1)>0) return false;
			String afiSelect = "SELECT count(*) FROM contract_batch_detail WHERE contract = " + contract.getId();
			ps = conn.prepareStatement(afiSelect);
			rs = ps.executeQuery();
			rs.next();
			if (rs.getInt(1)>0) return false;
			String contrataSelect = "SELECT count(*) FROM contrata_batch_detail WHERE contract = " + contract.getId();
			ps = conn.prepareStatement(contrataSelect);
			rs = ps.executeQuery();
			rs.next();
			if (rs.getInt(1)>0) return false;
		} catch (SQLException e) {
			String msg = "Se ha producido un error al obtener el dato requerido. ("+ e.getMessage()+")";
			AonUtil.addErrorMessage(msg);
		} catch (AonConnectionException e) {
			String msg = "Se ha producido un error al obtener el dato requerido. ("+ e.getMessage()+")";
			AonUtil.addErrorMessage(msg);
		} finally {
			DatabaseUtil.closeQuietly(ps);
			DatabaseUtil.closeQuietly(conn);
		}
		return true;
	}
	
	public boolean isEndDateRequired(){
		String contractCode = null;
		if(this.getParams().getContractCode()!=null){
			contractCode = this.getParams().getContractCode();
		}
		String[] codes = {"402", "420", "421", "430", "441", "452", "502", "520", "530", "541", "552", "970"};
		return ArrayUtils.contains(codes, contractCode) ;
	}
	
	public boolean isEndDateOptional(){
		String contractCode = null;
		if(this.getParams().getContractCode()!=null){
			contractCode = this.getParams().getContractCode();
		}
		String[] codes = {"401", "403", "410", "501", "503", "510", "540", "980", "990"};
		return ArrayUtils.contains(codes, contractCode) ;
	}
	
	public boolean isPartiallyTimeContract(){
		String contractCode = null;
		if(this.getParams().getContractCode()!=null){
			contractCode = this.getParams().getContractCode();
		}
		return (getParams().isRetaQuote() && getParams().isRetaPartialTime())
				||  (contractCode!=null && !StringUtils.startsWith(contractCode, "1") && !StringUtils.startsWith(contractCode, "4")) ;
	}
	
	public boolean isExtensibleContract(){
		String contractCode = null;
		if(this.getParams().getContractCode()!=null){
			contractCode = this.getParams().getContractCode();
		}
		return StringUtils.startsWith(contractCode, "4") || StringUtils.startsWith(contractCode, "5");
	}

	public boolean isTransformableContract(){
		String contractCode = null;
		if(this.getParams().getContractCode()!=null){
			contractCode = this.getParams().getContractCode();
		}
		return StringUtils.startsWith(contractCode, "4") || StringUtils.startsWith(contractCode, "5");
	}

	public boolean isExtendedContract(){
		Contract contract = (Contract) this.getTo();
		return getExtensionCount(contract)>0;
	}
	public Integer getExtensionCount(){
		Contract contract = (Contract) this.getTo();
		return getExtensionCount(contract);
	}
	public Integer getExtensionCount(Contract contract){
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			String sepeIdSelect = "SELECT count(*) FROM contract_attach WHERE contract = " + contract.getId() 
					+ " AND type = " + ContractAttachmentType.SEPE_EXTENSION_FILE.ordinal();
			ps = conn.prepareStatement(sepeIdSelect);
			rs = ps.executeQuery();
			if (rs.next()){
				return rs.getInt(1);
			}
		} catch (Exception e) {
			String msg = "Se ha producido un error al obtener el dato requerido. ("+ e.getMessage()+")";
			AonUtil.addErrorMessage(msg);
			LOGGER.error(e.getMessage());
		} finally {
			DatabaseUtil.closeQuietly(ps);
			DatabaseUtil.closeQuietly(conn);
		}
		return 0;
	}
	public boolean isTransformedContract(){
		String contractCode = null;
		if(this.getParams().getContractCode()!=null){
			contractCode = this.getParams().getContractCode();
		}
		String[] codes = {"109","139","189","209","239","289","309","339","389"};
		return ArrayUtils.contains(codes, contractCode) ;
	}
	
	public boolean isReadOnly(){
		Contract contract = (Contract) this.getTo();
		if(contract!=null && contract.getId()!=null ){
			Connection conn = null;
			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
				conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
				String sepeIdSelect = "SELECT name, expression  FROM contract_info  WHERE contract = " + contract.getId() + 
						" AND name IN (" +
						"'" + ContractVariable.SEPE_CONTRACT.getValue() + "'," +
						"'" + ContractVariable.SEPE_EXTENSION.getValue() + "'," +
						"'" + ContractVariable.SEPE_TRANSFORM.getValue() + "', " +
						"'" + ContractVariable.SS_MA.getValue() + "', " +
						"'" + ContractVariable.SS_MB.getValue() + "') " +
						" AND expression NOT IN (" +
						"'" + ContractSepeStatus.PENDING.getValue() + "'," +
						"'" + ContractSepeStatus.DENIED.getValue() + "') " +
						" ORDER BY start_date desc;";
				ps = conn.prepareStatement(sepeIdSelect);
				rs = ps.executeQuery();
				if (rs.next()) return true;
			} catch (SQLException e) {
				String msg = "Se ha producido un error al obtener el dato requerido. ("+ e.getMessage()+")";
				AonUtil.addErrorMessage(msg);
			} catch (AonConnectionException e) {
				String msg = "Se ha producido un error al obtener el dato requerido. ("+ e.getMessage()+")";
				AonUtil.addErrorMessage(msg);
			} catch (Exception e) {
				String msg = "Se ha producido un error al obtener el dato requerido. ("+ e.getMessage()+")";
				AonUtil.addErrorMessage(msg);
			} finally {
				DatabaseUtil.closeQuietly(ps);
				DatabaseUtil.closeQuietly(conn);
			}
		}
		return false;
	}

	public boolean isUnsuportedContract(){
		String contractCode = null;
		if(this.getParams().getContractCode()!=null){
			contractCode = this.getParams().getContractCode();
		}
		return contractCode!=null && (isTransformedContract() || !ArrayUtils.contains(ISepeConstants.AVAILABLE_CONTRACT_CODE_COMMUNICATION, contractCode));
	}
	
	public boolean isTrainingContract(){
		return getContractUtils().isTrainingContract((Contract) this.getTo(), this.getParams());
	}

	public boolean isTrainingCenterDefined(){
		Map<String, String> map = getContractUtils().getContractInfoMap((Contract) this.getTo());
		return map.get(ContractVariable.TRAINING_CENTER.getValue())!=null;
	}

	public boolean isTrainingCourseDefined(){
		Map<String, String> map = getContractUtils().getContractInfoMap((Contract) this.getTo());
		return map.get(ContractVariable.TRAINING_COURSE.getValue())!=null;
	}
	
	public boolean isPartialTimeReductionBonus(){
		boolean indicatorRequired = false;
		for(ITransferObject to: this.getParams().getBonuses()){
			ContractBonus bonus = (ContractBonus) to;
			
			if(bonus.getBonusConcept()!=null 
					&& bonus.getBonusConcept().getType()==null 
					&& bonus.getBonusConcept().getId()!=null){
				
				BonusType type = bonus.getBonusConcept().getType();

				if(type==null){
					type = PayrollUtils.getInstance().getBonusTypeByCode(Integer.toString(bonus.getBonusConcept().getId()));
				}
				
				if(type==BonusType.REDUCTION_FLAT_RATE_RDL03_2014
						|| type==BonusType.REDUCTION_RATE_RDL01_2015){
					indicatorRequired = true;
				}
			}
			
		}
		return isPartiallyTimeContract() && indicatorRequired;
	}
	
	public boolean isShowDisabilityIndicator(){
		if(this.getParams().getContractCode()!=null){
			String[] disabilityCodes = { ContractCode.C130.getValue(),
					ContractCode.C230.getValue(), ContractCode.C330.getValue(),
					ContractCode.C430.getValue(), ContractCode.C530.getValue() };
			return ArrayUtils.contains(disabilityCodes, this.getParams().getContractCode());			
		}
		return false;
	}
	
	public ContractSsStatus getModelSsStatus(){
		try {
			return getSsStatus((Contract) this.getModel().getRowData());
		} catch (ManagerBeanException e) {
			String msg = "Se ha producido un error al obtener el dato requerido. ("+ e.getMessage()+")";
			AonUtil.addErrorMessage(msg);
			LOGGER.error(msg);
		}
		return null;
	}
	public ContractSsStatus getSelectedSsStatus(){
		return getSsStatus((Contract) this.getTo());
	}
	public ContractSsStatus getSsStatus(Contract contract){
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			String ssIdSelect = "SELECT expression  FROM contract_info  WHERE contract = " + contract.getId() + 
					" AND name IN ('" + ContractVariable.SS_MA.getValue() + "', " +
					" '" + ContractVariable.SS_MB.getValue() + "') " +
					" ORDER BY start_date;";
			ps = conn.prepareStatement(ssIdSelect);
			rs = ps.executeQuery();
			if (rs.next()) return ContractSsStatus.valueOf(rs.getString(1));
		} catch (SQLException e) {
			String msg = "Se ha producido un error al obtener el dato requerido. ("+ e.getMessage()+")";
			AonUtil.addErrorMessage(msg);
		} catch (AonConnectionException e) {
			String msg = "Se ha producido un error al obtener el dato requerido. ("+ e.getMessage()+")";
			AonUtil.addErrorMessage(msg);
		} finally {
			DatabaseUtil.closeQuietly(ps);
			DatabaseUtil.closeQuietly(conn);
		}
		return null;
	}
	
	public ContractSepeStatus getModelSepeStatus(){
		try {
			return getSepeStatus((Contract) this.getModel().getRowData());
		} catch (ManagerBeanException e) {
			String msg = "Se ha producido un error al obtener el dato requerido. ("+ e.getMessage()+")";
			AonUtil.addErrorMessage(msg);
			LOGGER.error(msg);
		}
		return null;
	}
	public ContractSepeStatus getSelectedSepeStatus(){
		return getSepeStatus((Contract) this.getTo());
	}
	private ContractSepeStatus getSepeStatus(Contract contract){
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			String sepeIdSelect = "SELECT expression  FROM contract_info  WHERE contract = " + contract.getId() + 
					" AND name IN ('" + ContractVariable.SEPE_CONTRACT.getValue() + "', " +
					" '" + ContractVariable.SEPE_EXTENSION.getValue() + "', " +
					" '" + ContractVariable.SEPE_TRANSFORM.getValue() + "', " +
					" '" + ContractVariable.SEPE_CERTIFICADOS.getValue() + "') " +
//					" '" + ContractVariable.SEPE_CONTRACT_ID.getValue() + "', " +
//					" '" + ContractVariable.SEPE_EXTENSION_ID.getValue() + "', " +
//					" '" + ContractVariable.SEPE_TRANSFORM_ID.getValue() + "') " +
					" ORDER BY start_date;";
			ps = conn.prepareStatement(sepeIdSelect);
			rs = ps.executeQuery();
			if (rs.next()) return ContractSepeStatus.valueOf(rs.getString(1));
		} catch (SQLException e) {
			String msg = "Se ha producido un error al obtener el dato requerido. ("+ e.getMessage()+")";
			AonUtil.addErrorMessage(msg);
		} catch (AonConnectionException e) {
			String msg = "Se ha producido un error al obtener el dato requerido. ("+ e.getMessage()+")";
			AonUtil.addErrorMessage(msg);
		} finally {
			DatabaseUtil.closeQuietly(ps);
			DatabaseUtil.closeQuietly(conn);
		}
		return null;
	}
	
	public List<?> getContractModel() {
		ModelOption[] availableModels = ISepeConstants.AVAILABLE_CONTRACT_MODEL_OPTIONS; 
		List<SelectItem> list = new LinkedList<SelectItem>();
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		if(getParams().getContractCode()!=null){
			for(ModelOption opt: ModelOption.values()){
				if( ArrayUtils.contains(opt.getCodes(), ContractCode.getContractCodeByValue(getParams().getContractCode())) ){
					String label = ArrayUtils.contains(availableModels, opt)?"":"* ";
					label += opt.getName(locale);
					SelectItem item = new SelectItem(opt, label);
					list.add(item);
				}
			}
		}
		if(isInternship()){
			String label = ModelOption.INTERNSHIP.getName(locale);
			SelectItem item = new SelectItem(ModelOption.INTERNSHIP, label);
			list.add(item);
		}
		return list;
	}
	
	public String getSepeCommunicationId(){
		Map<String, String> map = getContractUtils().getContractInfoMap((Contract) this.getTo());
		return map.get(ContractVariable.SEPE_CONTRACT_ID.getValue());
	}
		
	public void onShowNewContractModal(ActionEvent event) {
		setShowNewContractModal(true);
	}
	
	public void onShowBonusWindow(ActionEvent event){
		try {
			getParams().setBonus((ContractBonus) BeanManager.getManagerBean(ContractBonus.class).createNewTo());
			getParams().getBonus().setContract((Contract) this.getTo());
		} catch (ManagerBeanException e) {
			String msg = "Error al mostrar la bonificacion";
			LOGGER.error(msg,e);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}
	
	public void onSelectBonus(ActionEvent event){
		if(getParams().getBonusModel().isRowAvailable()){
			getParams().setBonus((ContractBonus) getParams().getBonusModel().getRowData());
			setShowContractBonusWindow(true);
		}
	}
	
	public void onSaveBonus(ActionEvent event){
		try {
			if(getParams().getBonus()!=null){
				IManagerBean bean = BeanManager.getManagerBean(ContractBonus.class);
				bean.restoreNullSubPOJOs(getParams().getBonus());
				bean.insertOrUpdate(getParams().getBonus());
				getContractUtils().loadContractBonuses((Contract) this.getTo(), this.getParams());
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al mostrar la bonificacion";
			LOGGER.error(msg,e);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}

	public void onRemoveBonus(ActionEvent event){
		ContractBonus bonus = getParams().getBonus();
		if(bonus!=null && bonus.getId()!=null){
			getContractUtils().removeContractBonus(bonus);
			try {
				getContractUtils().loadContractBonuses((Contract) this.getTo(), this.getParams());
			} catch (ManagerBeanException e) {
				String msg = "Error al mostrar la bonificacion";
				LOGGER.error(msg,e);
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg, e);
			}
		}
	}
	
	public void onShowEmbargoWindow(ActionEvent event){
		IController controller = FormUtil.getController(IPayrollConstants.CONTRACT_EMBARGO_CONTROLLER);
		controller.onReset(event);
	}
	
	public void onSelectEmbargo(ActionEvent event){
		setShowContractEmbargoWindow(true);
		IController controller = FormUtil.getController(IPayrollConstants.CONTRACT_EMBARGO_CONTROLLER);
		controller.onSelect(event);
	}
	
	public void onShowSalaryInfoWindow(ActionEvent event){
		setSalaryInfoHandler(new SalaryInfoHandler((Contract) this.getTo()));
		getSalaryInfoHandler().onLoad(event);
	}
	
	public void onPersonBack(ActionEvent event){
		try {
			this.refresh(event);
			this.getManagerBean().restoreNullSubPOJOs(getTo());
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onPersonBack exception: ",e);
			AonUtil.addErrorMessage("Se ha producido un error al recargar los datos de persona. ");
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
	public void onEnterpriseChanged( LookupChangeEvent event ) {
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			setEnterprise((Enterprise)event.getNewValue());
		} else {
			setEnterprise(null);
		}
		
		setActivities(null);
		setWorkPlaces(null);
		setEnterpriseCCCs(null);
	}
	
	public void onActivityChanged( ActionEvent event ) {
		Contract contract = (Contract) this.getTo();
		contract.setEnterpriseCCC(null);
		loadEnterpriseCCCs();
	}
	
	public void onWorkPlaceChanged( ActionEvent event ) {
		Contract contract = (Contract) getTo();
		contract.setActivity(null);
		contract.setEnterpriseCCC(null);
		loadActivities();
		loadEnterpriseCCCs();
		loadWorkplaceAgreement(event);
	}

	private void loadWorkPlaces() {
		setWorkPlaces(new LinkedList<SelectItem>());
		if (getEnterprise() != null) {
			try {
				IManagerBean bean = BeanManager.getManagerBean(WorkPlace.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.WORK_PLACE_ENTERPRISE_ID), getEnterprise().getId());
				List<ITransferObject> list = bean.getList(criteria);
				for (ITransferObject to : list) {
					WorkPlace w = (WorkPlace)to; 
					String name = w.getDescription();
					SelectItem item = new SelectItem(w, name);
					getWorkPlaces().add(item);
				}
				Contract contract = (Contract) getTo();
				if( !getWorkPlaces().isEmpty() && (contract.getWorkPlace()==null || contract.getWorkPlace().getId()==null) ){
					contract.setWorkPlace((WorkPlace) getWorkPlaces().get(0).getValue());
				}
			} catch (ManagerBeanException e) {
				String msg = "Imposible cargar los Centros de Trabajo de la empresa.";
				AonUtil.addErrorMessage(msg);
				LOGGER.error(msg, e);
			}
			if(this.isNevv() && (getAgreement()==null || getAgreement().getId()==null)){
				loadWorkplaceAgreement(null);
			}
		}
	}

	private void loadActivities() {
		setActivities( new LinkedList<SelectItem>());
		if (getEnterprise() != null) {
			try {
				Contract contract = (Contract) getTo();
				IManagerBean pwBean = BeanManager.getManagerBean(PayrollWorkPlace.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(pwBean.getFieldName(IEntityAlias.PAYROLL_WORK_PLACE_WORK_PLACE_ID), contract.getWorkPlace().getId());
				List<ITransferObject> pwList = pwBean.getList(criteria);
				for(ITransferObject to: pwList){
					PayrollWorkPlace pw = (PayrollWorkPlace) to;
					if(pw.getEnterpriseActivity()==null || pw.getEnterpriseActivity().getId()==null){
						IManagerBean activityBean = BeanManager.getManagerBean(EnterpriseActivity.class);
						Criteria activityCriteria = new Criteria();
						activityCriteria.addEqualExpression(activityBean.getFieldName(IEntityAlias.ENTERPRISE_ACTIVITY_ENTERPRISE_ID), contract.getWorkPlace().getEnterprise().getId());
						for(ITransferObject activityTo: activityBean.getList(activityCriteria)){
							EnterpriseActivity activity = (EnterpriseActivity) activityTo;
							String name = activity.getDescription() + " - (" + activity.getCnae2009().getCode() + ") " + activity.getCnae2009().getTitle();
							SelectItem item = new SelectItem(activity, name);
							getActivities().add(item);
						}
					} else if(pw.getEnterpriseActivity()!=null && pw.getEnterpriseActivity().getCnae2009()!=null){
						String name = pw.getEnterpriseActivity().getDescription() + " - (" + pw.getEnterpriseActivity().getCnae2009().getCode() + ") " + pw.getEnterpriseActivity().getCnae2009().getTitle();
						SelectItem item = new SelectItem(pw.getEnterpriseActivity(), name);
						getActivities().add(item);
					}
				}
				if( !getActivities().isEmpty() ){
					contract.setActivity((EnterpriseActivity)getActivities().get(0).getValue());
				} else {
					contract.setActivity(null);
				}
			} catch (Exception e) {
				String msg = "Imposible cargar las Actividades de la empresa.";
				AonUtil.addErrorMessage(msg);
				LOGGER.error(msg, e);
			}
			loadEnterpriseCCCs();
		}
	}

	private void loadEnterpriseCCCs() {
		setEnterpriseCCCs( new LinkedList<SelectItem>());
		Contract contract = (Contract) getTo();
		if ( (contract.getActivity() != null && contract.getActivity().getId() != null) 
				|| (contract.getEnterpriseCCC()!=null && contract.getEnterpriseCCC().getId()!=null)){
			try {
				IManagerBean ecBean = BeanManager.getManagerBean(EnterpriseCCC.class);
				Criteria criteria = new Criteria();
				if(contract.getEnterpriseCCC()!=null && contract.getEnterpriseCCC().getId()!=null){
					contract.setActivity(contract.getEnterpriseCCC().getActivity());
					criteria.addEqualExpression(ecBean.getFieldName(IEntityAlias.ENTERPRISE_CCC_ACTIVITY_ID), contract.getActivity().getId());
					criteria.addEqualExpression(ecBean.getFieldName(IEntityAlias.ENTERPRISE_CCC_GEOZONE_ID), contract.getEnterpriseCCC().getGeozone().getId());
				} else if(contract.getActivity()!=null && contract.getActivity().getId()!=null){
					criteria.addEqualExpression(ecBean.getFieldName(IEntityAlias.ENTERPRISE_CCC_ACTIVITY_ID), contract.getActivity().getId());
					criteria.addEqualExpression(ecBean.getFieldName(IEntityAlias.ENTERPRISE_CCC_GEOZONE_ID), contract.getWorkPlace().getAddress().getGeozone().getId());
				}
				List<ITransferObject> cccList = ecBean.getList(criteria);
				Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
				for(ITransferObject to: cccList){
					EnterpriseCCC ccc = (EnterpriseCCC) to;
					String name = ccc.getType().getName(locale) +" (";
					name += PayrollUtils.getInstance().getRegimeCode(ccc);
					name += ccc.getCcc() + ")";
					SelectItem item = new SelectItem(ccc, name);
					getEnterpriseCCCs().add(item);
				}
			} catch (Exception e) {
				String msg = "Imposible cargar los CCC de la empresa.";
				AonUtil.addErrorMessage(msg);
				LOGGER.error(msg, e);
			}
		}
	}
	
	public boolean isInternship() {
		return isInternship( (Contract) this.getTo() );
	}
	
	private boolean isInternship(Contract contract) {
		return (contract.getEnterpriseCCC()!=null && contract.getEnterpriseCCC().getType()==CCCType.FELLOWS)
				&& getParams()!=null 
				&& ( getParams().getContractCode()==null 
					|| (getParams().getContractCode()!=null && getParams().getContractCode().equals("000")));
	}
	
	public void loadWorkplaceAgreement(ActionEvent event){
		Contract contract = (Contract) getTo(); 
		try {
			if ( contract.getWorkPlace()!=null && contract.getWorkPlace().getId()!=null ) {
				IManagerBean bean = BeanManager.getManagerBean(PayrollWorkPlace.class);
				Criteria  criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PAYROLL_WORK_PLACE_WORK_PLACE_ID), contract.getWorkPlace().getId());
				List<ITransferObject> list = bean.getList(criteria);
				if(!list.isEmpty()){
					PayrollWorkPlace pw = (PayrollWorkPlace) list.get(0);
					if(pw.getAgreement()!=null){
						setAgreement(pw.getAgreement());
					}
				}
			}
		} catch (Exception e) {
			String msg = "error loading workplace agreement";
			AonUtil.addErrorMessage(msg);
			LOGGER.error(msg, e);
		}
	}
	
	@Deprecated
	public List<SelectItem> getAgreementLevelCategories(){
		List<SelectItem> list = new LinkedList<SelectItem>();
		try {
			Criteria criteria = new Criteria();
			if(getAgreement()!=null && getAgreement().getId()!=null){
				PayrollUtils utils = PayrollUtils.getInstance();
				Integer[] ids = {0, DomainManager.getCurrentDomain(), utils.getParentDomainId()};
				IManagerBean cBean = BeanManager.getManagerBean(AgreementLevelCategory.class);
				criteria = new Criteria();
				criteria.setSkipDomainFilter(true);
				criteria.addInExpression("AgreementLevelCategory.domain", ids);
				criteria.addEqualExpression(cBean.getFieldName(IEntityAlias.AGREEMENT_LEVEL_CATEGORY_LEVEL_AGREEMENT_ID), getAgreement().getId());
				for (ITransferObject to : cBean.getList(criteria)) {
					AgreementLevelCategory alc = (AgreementLevelCategory) to;
					String name = alc.getLevel().getDescription() + (alc.getDescription()!=null?" - "+alc.getDescription():"");
					SelectItem item = new SelectItem(alc, name);
					list.add(item);
				}
			}
		} catch (ManagerBeanException e) {
			// NADA, no se cargan datos del convenio
		}
		return list;
	}
	
	public AgreementLevelCategory getAgreementLevelCategory(AgreementLevel agreementLevel, String categoryDescription){
		try {
			Criteria criteria = new Criteria();
			if(getAgreement()!=null && getAgreement().getId()!=null){
				PayrollUtils utils = PayrollUtils.getInstance();
				Integer[] ids = {0, DomainManager.getCurrentDomain(), utils.getParentDomainId()};
				IManagerBean cBean = BeanManager.getManagerBean(AgreementLevelCategory.class);
				criteria = new Criteria();
				criteria.setSkipDomainFilter(true);
				criteria.addInExpression("AgreementLevelCategory.domain", ids);
				criteria.addEqualExpression(cBean.getFieldName(IEntityAlias.AGREEMENT_LEVEL_CATEGORY_LEVEL_ID), agreementLevel.getId());
				AgreementLevelCategory alc = null;
				for (ITransferObject to : cBean.getList(criteria)) {
					alc = (AgreementLevelCategory) to;
					if ( AonUtils.equals(alc.getDescription(), categoryDescription ) )
						return alc;
				}
				return alc;
			}
		} catch (ManagerBeanException e) {
			// NADA, no se cargan datos del convenio
		}
		return null;
	}

	@Deprecated
	public void onChangeAgreement(LookupChangeEvent event){
		if (event.getNewValue() == null || event.getNewValue().equals("")) {
			Contract contract = (Contract) getTo();
			contract.setAgreementLevel(null);
		}
	}
	
	@Deprecated
	public void onChangeAgreementLevelCategory(ActionEvent event){
		// TODO if is new contract then ask for the salary. how obtain the salary from de agreement?
		Contract contract = (Contract) getTo();
		try {
			if(contract.getAgreementLevel()!=null){
				IManagerBean bean = BeanManager.getManagerBean(AgreementLevelData.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.AGREEMENT_LEVEL_DATA_LEVEL_ID), contract.getAgreementLevel().getId());
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.AGREEMENT_LEVEL_DATA_NAME), "P05_IMPORTE");
				List<ITransferObject> list = bean.getList(criteria);
				if(!list.isEmpty()){
					AgreementLevelData d = (AgreementLevelData) list.get(0);
					getParams().setAgreementSalaryCheck(true);
					getParams().setGrossSalary(d.getDoubleExpression());
				} else {
					getParams().setAgreementSalaryCheck(false);
					getParams().setGrossSalary(null);
				}
			}
		} catch (ManagerBeanException e) {
			// NADA
		}
	}
	
	@Deprecated
	public void onChangeCno(LookupChangeEvent event){
		Contract contract = (Contract) this.getTo();
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			String desc = ((CNO)event.getNewValue()).getTitle();
			if(StringUtils.isNotBlank(desc) && desc.length()>64){
				desc = desc.substring(0, 63);
			}
			contract.setCategoryDescription(desc);
		}
	}

	public void onChangeStartDate(ValueChangeEvent event){
		if(isNevv()){
			Contract contract = (Contract) this.getTo();
			contract.setSeniorityDate( (Date)event.getNewValue() );
		}
	}
	
	public void onChangeModel(ValueChangeEvent event){
		ModelOption model = (ModelOption) event.getNewValue();
		if(!this.isNevv() && model != null){
			this.getParams().setContractModelOption(model);
			ContractInfoController controller = (ContractInfoController) FormUtil.getController("contractDocumentInfo");
			controller.loadContractFields((Contract) this.getTo(), true);
		}
	}
	
	public List<SelectItem> getTrainingCenters(){
		List<SelectItem> list = new LinkedList<SelectItem>();
		CNO cno = getParams().getCno();
		if(cno!=null && cno.getId()!=null){
			List<TrainingCenter> centerList = new LinkedList<TrainingCenter>();
			try {
				IManagerBean bean = BeanManager.getManagerBean(TrainingCourse.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.TRAINING_COURSE_CNO_ID), cno.getId());
				for(ITransferObject to: bean.getList(criteria)){
					TrainingCourse course = (TrainingCourse) to;
					if(!centerList.contains(course.getTrainingCenter())){
						centerList.add(course.getTrainingCenter());
					}
				}
				if(centerList.size()==1){
					getParams().setTrainingCenter(centerList.get(0));
				} else if(centerList.size()>1){
					PayrollAppParamsController params = (PayrollAppParamsController) AonUtil.getRegisteredBean(IPayrollConstants.PAYROLL_APP_PARAMS_CONTROLLER_NAME);
					if( getParams().getContractModelCode() != null
							&& getParams().getContractModelCode().getCode() == ContractCode.C421 
							&& params.getDefaultTrainingCenter()!=null && params.getDefaultTrainingCenter().getId()!=null){
						if(centerList.contains(params.getDefaultTrainingCenter())){
							getParams().setTrainingCenter(params.getDefaultTrainingCenter());
						}
					}
				}
			} catch (ManagerBeanException e) {
				String msg = "No se han podido cargar los centros formativos."; 
				LOGGER.error(msg, e);
				AonUtil.addErrorMessage(msg);
			}
			
			for(TrainingCenter center: centerList){
				SelectItem item = new SelectItem(center, center.getRegistry().getFullName());
				list.add(item);
			}
		}
		return list;
	}

	public List<SelectItem> getTrainingCourses(){
		List<SelectItem> list = new LinkedList<SelectItem>();
		CNO cno = getParams().getCno();
		TrainingCenter center = getParams().getTrainingCenter();
		if( cno!=null && cno.getId()!=null
				&& center!=null && center.getId()!=null ){
			try {
				IManagerBean bean = BeanManager.getManagerBean(TrainingCourse.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.TRAINING_COURSE_CNO_ID), cno.getId());
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.TRAINING_COURSE_TRAINING_CENTER_ID), center.getId());
				for(ITransferObject to: bean.getList(criteria)){
					TrainingCourse course = (TrainingCourse) to;
					SelectItem item = new SelectItem(course, "("+course.getCode()+") "+course.getOccupationName());
					list.add(item);
				}
			} catch (ManagerBeanException e) {
				String msg = "No se han podido cargar los cursos formativos."; 
				LOGGER.error(msg, e);
				AonUtil.addErrorMessage(msg);
			}
		}
		return list;
	}
	
	public TrainingCenter getTrainingCenter(){
		return getParams().getTrainingCourse().getTrainingCenter();
	}
	
	public RegistryDirStaff getTrainingCenterDirStaff() throws ManagerBeanException{
		IManagerBean bean = BeanManager.getManagerBean(RegistryDirStaff.class);
		Criteria criteria = new Criteria();
		String alias = bean.getFieldName(IEntityAlias.REGISTRY_DIR_STAFF_REGISTRY_ID);
		criteria.addEqualExpression(alias, getTrainingCenter().getId());
		alias = bean.getFieldName(IEntityAlias.REGISTRY_DIR_STAFF_DUE_DATE);
		Expression exp1 = ExpressionUtilities.getGreaterThanOrEqualExpression(alias, new Date());
		Expression exp2 = ExpressionUtilities.getNullExpression(alias);
		criteria.addExpression(ExpressionUtilities.getOrExpression(exp1, exp2));
		Iterator<ITransferObject> iter = bean.getList(criteria).iterator();
		if(iter.hasNext()){
			return (RegistryDirStaff)iter.next();
		}
		return null;
	}
	
	public InputStream getTrainingCenterLogo() throws IOException, ManagerBeanException{
		RegistryAttachment attach = obtainTrainingCenterLogo();
		if(attach != null){
			return new ByteArrayInputStream(attach.getData());
		}
		return null;
	}
	
	public RegistryAttachment obtainTrainingCenterLogo() throws ManagerBeanException {
		IManagerBean registryAttachBean = BeanManager.getManagerBean(RegistryAttachment.class);
		Criteria criteria = new Criteria();
		String alias = registryAttachBean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ID);
		criteria.addEqualExpression(alias, getTrainingCenter().getId());
		String type = registryAttachBean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE);
		criteria.addEqualExpression(type, RegistryAttachmentType.LOGO);
		Iterator<ITransferObject> iter = registryAttachBean.getList(criteria).iterator();
		if(iter.hasNext()){
			return (RegistryAttachment)iter.next();
		}
		return null;
	}
	
	private String selectedTab;
	
	
	public String getSelectedTab() {
		return selectedTab;
	}
	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}
	
	public void onCertificadosCommunicationShow(ActionEvent event){
		Contract contract =  (Contract) this.getTo();
		CertificadosController certificadosController = (CertificadosController) AonUtil.getRegisteredBean(ISepeConstants.CONTRACT_CERTIFICADOS_CONTROLLER_NAME);
		certificadosController.initialize(contract);
		if(getParams().getSuspensionCause()!=null){
			certificadosController.setSuspensionCause(SuspensionCause.valueOf("C"+Integer.parseInt(getParams().getSuspensionCause().getCode())));
		}
	}
	public void onSepeCommunicationShow(ActionEvent event){
		if(isTransformedContract()){
			onTransformSepeShow(event);
		} else if(isExtendedContract() && getExtensionCount()==2){
			onExtension2SepeShow(event);
		} else if(isExtendedContract() && getExtensionCount()==1){
			onExtension1SepeShow(event);
		} else {
			onContractSepeShow(event);
		}
	}
	public void onContractSepeShow(ActionEvent event){
		ContrataContratosController contrataController = (ContrataContratosController) AonUtil.getRegisteredBean(ISepeConstants.CONTRACT_CONTRATA_CONTROLLER_NAME);
		contrataController.initialize((Contract) this.getTo());
		contrataController.onContrataDataShow(event);
		contrataController.setReadOnly(this.isExtendedContract() || this.isTransformedContract());
	}
	public void onExtension1SepeShow(ActionEvent event){
		ContrataProrrogasController contrataController = (ContrataProrrogasController) AonUtil.getRegisteredBean(ISepeConstants.EXTENSION_CONTRATA_CONTROLLER_NAME);
		contrataController.setExtensionNumber(1);
		contrataController.initialize((Contract) this.getTo());
		contrataController.onContrataDataShow(event);
		contrataController.setReadOnly(this.getExtensionCount()>1 || this.isTransformedContract());
	}
	public void onExtension2SepeShow(ActionEvent event){
		ContrataProrrogasController contrataController = (ContrataProrrogasController) AonUtil.getRegisteredBean(ISepeConstants.EXTENSION_CONTRATA_CONTROLLER_NAME);
		contrataController.setExtensionNumber(2);
		contrataController.initialize((Contract) this.getTo());
		contrataController.onContrataDataShow(event);
		contrataController.setReadOnly(this.getExtensionCount()>2 || this.isTransformedContract());
	}
	public void onTransformSepeShow(ActionEvent event){
		ContrataTransformacionesController contrataController = (ContrataTransformacionesController) AonUtil.getRegisteredBean(ISepeConstants.TRANSFORM_CONTRATA_CONTROLLER_NAME);
		contrataController.initialize((Contract) this.getTo());
		contrataController.onContrataDataShow(event);
//		TODO: readOnly param for Contrata transformation view 
		contrataController.setReadOnly(false);
	}
	
	public void onContrataExtensionShow(ActionEvent event){
		ContrataProrrogasController contrataController = (ContrataProrrogasController) AonUtil.getRegisteredBean(ISepeConstants.EXTENSION_CONTRATA_CONTROLLER_NAME);
		contrataController.setExtensionNumber(getExtensionCount()+1);
		contrataController.initialize((Contract) this.getTo());
		Date endDate = ((Contract) this.getTo()).getEndDate();
		if(endDate==null){
			endDate = DateUtils.addDays(new Date(), -1);
		}
		ContrataProrrogaParams params = (ContrataProrrogaParams) contrataController.getParams();
		params.setFechaInicio(DateUtils.addDays(endDate, 1));
		params.setFechaFin(DateUtils.addYears(endDate, 1));
		contrataController.onContrataDataShow(event);
	}

	public void onContrataTransformShow(ActionEvent event){
		ContrataTransformacionesController contrataController = (ContrataTransformacionesController) AonUtil.getRegisteredBean(ISepeConstants.TRANSFORM_CONTRATA_CONTROLLER_NAME);
		contrataController.initialize((Contract) this.getTo());
		Date endDate = ((Contract) this.getTo()).getEndDate();
		if(endDate==null){
			endDate = DateUtils.addDays(new Date(), -1);
		}
		ContrataTransformacionesParams params = (ContrataTransformacionesParams) contrataController.getParams();
		params.setFechaInicio(DateUtils.addDays(endDate, 1));
		contrataController.onContrataDataShow(event);
	}
	
	public void onExtendContract(ActionEvent event){
		ContrataProrrogasController contrataController = (ContrataProrrogasController) AonUtil.getRegisteredBean(ISepeConstants.EXTENSION_CONTRATA_CONTROLLER_NAME);
		contrataController.onContrataAccept(event);
		Contract contract = (Contract) this.getTo();
		contract.setEndDate(((ContrataProrrogaParams)contrataController.getHandler().getParams()).getFechaFin());
		this.accept(event);
	}
	
	public void onTransformContract(ActionEvent event){
		Contract contract = (Contract)this.getTo();
		Map<String, ContractData> sourceContractDataMap = SEPEUtils.getInstance().getContractDataMap(contract, contract.getStartDate(), null);
		
		ContrataContratosController contrataController = (ContrataContratosController) AonUtil.getRegisteredBean(ISepeConstants.CONTRACT_CONTRATA_CONTROLLER_NAME);
		ContrataContratoParams sourceContractParams = (ContrataContratoParams) contrataController.getParams();
		if(contrataController.getGeneratedFile()==null || contrataController.getGeneratedFile().getData()==null){
			contrataController.initialize(contract);
			contrataController.onContrataDataShow(event);
			contrataController.onContrataAccept(event);
		}
		
		ContrataTransformacionesController contrataTransformController = (ContrataTransformacionesController) AonUtil.getRegisteredBean(ISepeConstants.TRANSFORM_CONTRATA_CONTROLLER_NAME);
		ContrataTransformacionesParams transformParams = (ContrataTransformacionesParams) contrataTransformController.getParams();
		
		// cerrar el contrato actual
		try {
			contract.setEndDate(DateUtils.addDays(transformParams.getFechaInicio(), -1));
			IManagerBean bean = BeanManager.getManagerBean(Contract.class);
			bean.restoreNullSubPOJOs(contract);
			bean.update(contract);
		} catch (ManagerBeanException e) {
			String msg = "No se ha podido transformar el contrato. (" +e.getMessage() + ")"; 
			AonUtil.addErrorMessage(msg);
			LOGGER.error(msg);
			throw new AbortProcessingException(msg);
		}
		
		// nuevo contrato para la transformacion 
		try {
			Contract newContract = new Contract();
			newContract.setDomain(contract.getDomain());
			newContract.setPerson(contract.getPerson());
			newContract.setWorkPlace(contract.getWorkPlace());
			newContract.setActivity(contract.getActivity());
			newContract.setEnterpriseCCC(contract.getEnterpriseCCC());
			newContract.setAgreementLevel(contract.getAgreementLevel());
			newContract.setCategoryDescription(contract.getCategoryDescription());
			newContract.setCalendar(contract.getCalendar());
			newContract.setDescription(contract.getDescription());
			newContract.setSeniorityDate(contract.getSeniorityDate());
			newContract.setStartDate(transformParams.getFechaInicio());
			newContract.setEndDate(null);
			newContract.setModel(null);
			newContract.setRegimeType(contract.getRegimeType());
			newContract.setRegistration(contract.getRegistration());
			newContract.setSepeStatus(ContractStatus.PENDING);
			newContract.setSsStatus(ContractStatus.PENDING);
			
			IManagerBean bean = BeanManager.getManagerBean(Contract.class);
			contract = (Contract) bean.insert(newContract);
		} catch (ManagerBeanException e) {
			String msg = "No se ha podido transformar el contrato. (" +e.getMessage() + ")"; 
			AonUtil.addErrorMessage(msg);
			LOGGER.error(msg);
			throw new AbortProcessingException(msg);
		}
		
		// datos contrato origen
		for(ContractData data: sourceContractDataMap.values()){
			try {
				getContractUtils().insertContractData(contract, 
						data.getName(), data.getExpression(), 
						data.getStartDate(), DateUtils.addDays(transformParams.getFechaInicio(), -1));
			} catch (Exception e) {
				String msg = "("+data.getName()+") No se ha podido guardar del contrato origen. (" +e.getMessage() + ")"; 
				AonUtil.addErrorMessage(msg);
				LOGGER.error(msg);
			}
		}
		
		// datos basicos necesarios
		getParams().setContractCode(transformParams.getTransformCode());
		getParams().setCno(sourceContractParams.getCno());
		getParams().setSuspensionCause(null);
		getParams().setCollectivePeculiarityQuote(null);
		getContractUtils().insertContractData(contract, getParams());
		
		try {
			ContractAttachment newContractContrata = new ContractAttachment();
			newContractContrata.setAttachDate(contract.getSeniorityDate());
			newContractContrata.setAttachmentType(ContractAttachmentType.SEPE_CONTRACT_FILE);
			newContractContrata.setMimeType(MimeType.MIME_XML);
			newContractContrata.setContract(contract);
			newContractContrata.setData(contrataController.getGeneratedFile().getData());
			newContractContrata.setDomain(contract.getDomain());
			newContractContrata.setDescription(ContractAttachmentType.SEPE_CONTRACT_FILE.getName(AonUtil.getCurrentLocale()));
			IManagerBean bean = BeanManager.getManagerBean(ContractAttachment.class);
			bean.insert(newContractContrata);
			
			contrataTransformController.getHandler().initialize(contract);
			contrataTransformController.onContrataDataShow(event);
			((ContrataTransformacionesParams)contrataTransformController.getParams()).setCno(sourceContractParams.getCno());
			contrataTransformController.onContrataAccept(event);
			
			this.onLoad(event, contract.getId(), null, null);
		} catch (ManagerBeanException e) {
			String msg = "No se ha podido cargar el contrato transformado. (" +e.getMessage() + ")"; 
			AonUtil.addErrorMessage(msg);
			LOGGER.error(msg);
			throw new AbortProcessingException(msg);
		}
	}
	
	public void onUndoContractExtension(ActionEvent event){
		ContrataProrrogasController contrataProrrogasController = (ContrataProrrogasController) AonUtil.getRegisteredBean(ISepeConstants.EXTENSION_CONTRATA_CONTROLLER_NAME);
		contrataProrrogasController.setExtensionNumber(getExtensionCount());
		contrataProrrogasController.initialize((Contract) this.getTo());
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractAttachment.class);
			if(contrataProrrogasController.getGeneratedFile()!=null && contrataProrrogasController.getGeneratedFile().getId()!=null){
				bean.remove(contrataProrrogasController.getGeneratedFile().getId());
			}
		} catch (ManagerBeanException e) {
			String msg = "Imposible borrar los datos de la prorroga (ficheros SEPE). (" +e.getMessage() + ")";
			throw new AbortProcessingException(msg,e);
		}
		
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractInfo.class);
			ContractInfo info = SEPEUtils.getInstance().getContractInfoMap((Contract)this.getTo(), null, null).get(ContractVariable.SEPE_EXTENSION.getValue());
			if(info!=null && info.getId()!=null){
				bean.remove(info.getId());
			}
			info = SEPEUtils.getInstance().getContractInfoMap((Contract)this.getTo(), null, null).get(ContractVariable.SEPE_EXTENSION_ID.getValue());
			if(info!=null && info.getId()!=null){
				bean.remove(info.getId());
			}
		} catch (ManagerBeanException e) {
			String msg = "Imposible borrar los datos de la prorroga (INFO). (" +e.getMessage() + ")";
			throw new AbortProcessingException(msg,e);
		}
		
		Contract contract = (Contract) this.getTo();
		Date endDate = null;
		if(getExtensionCount()>0){
			contrataProrrogasController = (ContrataProrrogasController) AonUtil.getRegisteredBean(ISepeConstants.EXTENSION_CONTRATA_CONTROLLER_NAME);
			contrataProrrogasController.setExtensionNumber(getExtensionCount());
			contrataProrrogasController.initialize(contract);
			contrataProrrogasController.onContrataDataShow(null);
			endDate = ((ContrataProrrogaParams)contrataProrrogasController.getParams()).getFechaFin();
		} else {
			ContrataContratosController contrataContratosController = (ContrataContratosController) AonUtil.getRegisteredBean(ISepeConstants.CONTRACT_CONTRATA_CONTROLLER_NAME);
			contrataContratosController.initialize(contract);
			contrataContratosController.onContrataDataShow(null);
			endDate = ((ContrataContratoParams)contrataContratosController.getParams()).getEndDate();
		}
		contract.setEndDate(endDate);
		this.accept(event);
	}
	

//	 * ************************************
//	 * 			DOWNLOAD & UPLOAD METHODS		
//	 * ************************************
	
	public void onDownloadContract( ActionEvent event ) {
		try {
			Contract c = (Contract)this.getModel().getRowData();
			download(c, ContractAttachmentType.CONTRACT_DOC);
		} catch (IOException e) {
			String msg = "Imposible descargar el Contrato. (" +e.getMessage() + ")"; 
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		} catch (ManagerBeanException e) {
			String msg = "Imposible descargar el Contrato. (" +e.getMessage() + ")"; 
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}

	private void download(Contract c, ContractAttachmentType type) throws IOException {
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
		ContractAttachment attach = obtainContractAttachDocument(c, type);
		byte[] buffer = attach.getData();
		InputStream in = new ByteArrayInputStream(buffer);
		int bytes = in.read(buffer);
		while (bytes != -1) {
			response.getOutputStream().write(buffer, 0, bytes);
			bytes = in.read(buffer);
		}
		in.close();
		response.setContentType(MimeType.MIME_PDF.getName()); 
		response.flushBuffer();
		context.responseComplete();
	}
	
	public ContractAttachment obtainContractAttachDocument(Contract contract, ContractAttachmentType type) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractAttachment.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_ATTACHMENT_CONTRACT_ID), contract.getId());
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_ATTACHMENT_ATTACHMENT_TYPE), type);
			List<ITransferObject> list = bean.getList(criteria);
			if(!list.isEmpty()){
				return (ContractAttachment) list.get(0);
			} 
		} catch (ManagerBeanException e) {
			// NADA, el documento se queda vacio
		}
		return null;
	}
	
	public boolean getExistSignedContractDocument(){
		try {
			if(this.getModel().isRowAvailable()){
				Contract c = (Contract)this.getModel().getRowData();
				return obtainContractAttachDocument(c, ContractAttachmentType.CONTRACT_DOC) != null;
			}
		} catch (ManagerBeanException e) {
			String msg = "No se ha podido obtener el documento del contrato. (" +e.getMessage() + ")"; 
			LOGGER.error(msg, e);
		}
		return false;
	}
	
	public boolean getExistSignedBasicCopyDocument(){
		try {
			if(this.getModel().isRowAvailable()){
				Contract c = (Contract)this.getModel().getRowData();
				return obtainContractAttachDocument(c, ContractAttachmentType.BASIC_COPY) != null;
			}
		} catch (ManagerBeanException e) {
			String msg = "No se ha podido obtener el documento del contrato. (" +e.getMessage() + ")"; 
			LOGGER.error(msg, e);
		}
		return false;
	}
	
	
	public String onTrainingCenterDirectDebitReport() throws ManagerBeanException{
		TrainingCenterController tcController = (TrainingCenterController) AonUtil.getRegisteredBean(IPayrollConstants.TRAINING_CENTER_CONTROLLER_NAME);
		try {
			Map<String, String> map = getContractUtils().getContractInfoMap((Contract) this.getTo());
			if(map.get(ContractVariable.TRAINING_COURSE.getValue())!=null){
				String courseId = map.get(ContractVariable.TRAINING_COURSE.getValue());
				TrainingCourse course = (TrainingCourse) BeanManager.getManagerBean(TrainingCourse.class).get(Integer.parseInt(courseId));
				tcController.select(null, course.getTrainingCenter().getId());
			}
			ReportManager report = (ReportManager) AonUtil.getRegisteredBean("report");
			report.setReportKey("trainingDirectDebit");
			report.setOutputFormat(OutputFormat.PDF);
			return report.onExecute();
		} finally {
			tcController.clearCriteria();
			tcController.initializeModel();
		}
	}
	
	public void onChangeEndDate(ActionEvent event){
		if(((Contract)this.getTo()).getEndDate()==null){
			getParams().setSuspensionCause(null);
		} else {
			Calendar startCal = Calendar.getInstance();
			startCal.setTime(((Contract)this.getTo()).getStartDate());
			startCal.set(Calendar.HOUR_OF_DAY, 0);  
			startCal.set(Calendar.MINUTE, 0);  
			startCal.set(Calendar.SECOND, 0);  
			startCal.set(Calendar.MILLISECOND, 0);  
			Calendar endCal = Calendar.getInstance();
			endCal.setTime(((Contract)this.getTo()).getEndDate());
			endCal.set(Calendar.HOUR_OF_DAY, 0);  
			endCal.set(Calendar.MINUTE, 0);  
			endCal.set(Calendar.SECOND, 0);  
			endCal.set(Calendar.MILLISECOND, 0);  
			
			if(endCal.before(startCal)){
				((Contract)this.getTo()).setEndDate(null);
				getParams().setSuspensionCause(null);
				AonUtil.addErrorMessage("La fecha fin no puede ser anterior a la fecha inicio.");
			}
		}
	}
	
	public List<SelectItem> getSepeStatuses(){
		LinkedList<SelectItem> list = new LinkedList<SelectItem>();
		SelectItem item = new SelectItem(ContractSepeStatus.PENDING, "Pendiente");
		list.add(item);			
//		item = new SelectItem(ContractSepeStatus.BATCHED, "Remesado", "", true);
//		list.add(item);			
//		item = new SelectItem(ContractSepeStatus.ACCEPTED, "Aceptado", "", true);
//		list.add(item);			
//		item = new SelectItem(ContractSepeStatus.ACCEPTED_WITH_ERRORS, "Aceptado con errores", "", true);
//		list.add(item);			
//		item = new SelectItem(ContractSepeStatus.DENIED, "Rechazado", "", true);
//		list.add(item);			
		item = new SelectItem(ContractSepeStatus.BLOCKED, "Bloqueado");
		list.add(item);			
		item = new SelectItem(ContractSepeStatus.MANUAL, "Manual");
		list.add(item);			
		return list;
	}

	public List<SelectItem> getSsStatuses(){
		LinkedList<SelectItem> list = new LinkedList<SelectItem>();
		SelectItem item = new SelectItem(ContractSsStatus.PENDING, "Pendiente");
		list.add(item);			
//		item = new SelectItem(ContractSsStatus.BATCHED, "Remesado", "", true);
//		list.add(item);			
//		item = new SelectItem(ContractSsStatus.RECORDED, "Grabado", "", true);
//		list.add(item);			
//		item = new SelectItem(ContractSsStatus.DENIED, "Rechazado", "", true);
//		list.add(item);			
		item = new SelectItem(ContractSsStatus.BLOCKED, "Bloqueado");
		list.add(item);			
		item = new SelectItem(ContractSsStatus.MANUAL, "Manual");
		list.add(item);			
		return list;
	}
	
	public Map<String, String> obtainContractSpecialQuote() {
		// TODO
		try {
			IManagerBean bean = BeanManager.getManagerBean(SystemData.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SYSTEM_DATA_DOMAIN), 0);
			criteria.addGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.SYSTEM_DATA_START_DATE), new Date());
			Expression expr1 = ExpressionUtilities.getLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.SYSTEM_DATA_END_DATE), 0);
			Expression expr2 = ExpressionUtilities.getNullExpression(bean.getFieldName(IEntityAlias.SYSTEM_DATA_END_DATE));
			criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SYSTEM_DATA_NAME), "TIPOS_COTIZACION");
		} catch (ManagerBeanException e) {
			String msg = "Imposible obtener los valores de los tipos de cotizacion";
			throw new AbortProcessingException(msg,e);
		}
		
//		Pattern BASE_CGC_MIN_PATTERN = Pattern.compile("[\"01\":0.0, \"02\":0.0]"); 
		Pattern BASE_CGC_MIN_PATTERN = Pattern.compile("\\[(.*\\d{2}.*):(\\d{1,2}\\.\\d{1,2}),{0,1}\\]"); 
		Matcher m = BASE_CGC_MIN_PATTERN.matcher("");
		Map<String, String> values = new HashMap<>();
		if(m.find()) {
//			if(keys.length>0){
//				putValue(values, keys[0], (m.group(1)));
//			}
//			if(keys.length>1){
//				putValue(values, keys[1], (m.group(2)));
//			}
//			if(keys.length>2){
//				putValue(values, keys[2], (m.group(3)));
//			}
//		values.put(key, value);
		}
		return values;
	}

	public List<SelectItem> getSpecialQuotes() {
		LinkedList<SelectItem> list = new LinkedList<SelectItem>();
		SelectItem item = new SelectItem("00", "Común");
		list.add(item);			
		item = new SelectItem("RETA", "RETA");
		list.add(item);
		Map<String, String> values = obtainContractSpecialQuote();
		for(String key: values.keySet()){
			item = new SelectItem(key, values.get(key));
			list.add(item);			
		}
		return list;
	}
	
	/*
	 * INNER CLASES
	 */
	public static class SalaryInfoHandler implements Serializable {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
		
		public final String IPREM_FORMMULA = "EXCESO_IPREM";
		public final String ZERO_VALUE = "0";
		private Contract contract;
		private DataModel contractDataModel;
		private DataModel paymentModel;
		private DataModel deductionModel;
		private ContractData selectedData;
		private ContractPayment selectedPayment;
		private ContractDeduction selectedDeduction;
		private List<ITransferObject> dataTracking;
		private List<ITransferObject> paymentTracking;
		// filter options
		private Integer filterYear;
		private Month filterMonth;
		
		// PECULIAR QUOTE PERCENT VALUES
		private Map<String, ContractData> peculiarQuoteMap;
		private Map<String, Boolean> checkedQuoteMap;
		private SpecialQuote specialQuote;
		
		public SalaryInfoHandler(Contract contract){
			this.contract = contract;
			this.filterYear = Calendar.getInstance().get(Calendar.YEAR);
			this.filterMonth = Month.getMonthByValue(Calendar.getInstance().get(Calendar.MONTH));
		}
		
		public Map<String, ContractData> getPeculiarQuoteMap() {
			return peculiarQuoteMap;
		}
		public void setPeculiarQuoteMap(Map<String, ContractData> peculiarQuoteMap) {
			this.peculiarQuoteMap = peculiarQuoteMap;
		}
		public Map<String, Boolean> getCheckedQuoteMap() {
			return checkedQuoteMap;
		}
		public void setCheckedQuoteMap(Map<String, Boolean> checkedQuoteMap) {
			this.checkedQuoteMap = checkedQuoteMap;
		}
		public SpecialQuote getSpecialQuote() {
			return specialQuote;
		}
		public void setSpecialQuote(SpecialQuote specialQuote) {
			this.specialQuote = specialQuote;
		}
		public Integer getFilterYear() {
			return filterYear;
		}
		public void setFilterYear(Integer filterYear) {
			this.filterYear = filterYear;
		}
		public Month getFilterMonth() {
			return filterMonth;
		}
		public void setFilterMonth(Month filterMonth) {
			this.filterMonth = filterMonth;
		}
		public DataModel getContractDataModel() {
			return contractDataModel;
		}
		public void setContractDataModel(DataModel contractDataModel) {
			this.contractDataModel = contractDataModel;
		}
		public DataModel getPaymentModel() {
			return paymentModel;
		}
		public DataModel getDeductionModel() {
			return deductionModel;
		}
		public void setDeductionModel(DataModel deductionModel) {
			this.deductionModel = deductionModel;
		}
		public void setPaymentModel(DataModel paymentModel) {
			this.paymentModel = paymentModel;
		}
		public ContractData getSelectedData() {
			return selectedData;
		}
		public void setSelectedData(ContractData selectedData) {
			this.selectedData = selectedData;
		}
		public ContractPayment getSelectedPayment() {
			return selectedPayment;
		}
		public void setSelectedPayment(ContractPayment selectedPayment) {
			this.selectedPayment = selectedPayment;
		}
		public ContractDeduction getSelectedDeduction() {
			return selectedDeduction;
		}
		public void setSelectedDeduction(ContractDeduction selectedDeduction) {
			this.selectedDeduction = selectedDeduction;
		}
		public List<ITransferObject> getDataTracking() {
			return dataTracking;
		}
		public void setDataTracking(List<ITransferObject> dataTracking) {
			this.dataTracking = dataTracking;
		}
		public List<ITransferObject> getPaymentTracking() {
			return paymentTracking;
		}
		public void setPaymentTracking(List<ITransferObject> paymentTracking) {
			this.paymentTracking = paymentTracking;
		}
		public DataModel getDataTrackingModel(){
			return new SerializableListDataModel(dataTracking);
		}
		public DataModel getPaymentTrackingModel(){
			return new SerializableListDataModel(paymentTracking);
		}
		public Integer getDataTrackingCount(){
			if(getContractDataModel().isRowAvailable()){
				ContractData data = (ContractData) getContractDataModel().getRowData();
				try {
					IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
					Criteria criteria = new Criteria();
					criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_CONTRACT_ID), contract.getId());
					criteria.addNotEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_ID), data.getId());
					criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_NAME), data.getName());
					criteria.addLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_START_DATE), data.getStartDate());
					return bean.getCount(criteria);
				} catch (ManagerBeanException e) {
					AonUtil.addErrorMessage("No se han podido cargar correctamente los datos de contrato");
				}
			}
			return null;
		}
		public Integer getPaymentTrackingCount(){
			if(getPaymentModel()!=null && getPaymentModel().isRowAvailable()){
				ContractPayment payment = (ContractPayment) getPaymentModel().getRowData();
				if(payment.getPaymentConcept()!=null){
					try {
						IManagerBean bean = BeanManager.getManagerBean(ContractPayment.class);
						Criteria criteria = new Criteria();
						criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_PAYMENT_CONTRACT_ID), contract.getId());
						criteria.addNotEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_PAYMENT_ID), payment.getId());
						criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_PAYMENT_PAYMENT_CONCEPT_ID), payment.getPaymentConcept().getId());
						criteria.addLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_PAYMENT_START_DATE), payment.getStartDate());
						return bean.getCount(criteria);
					} catch (ManagerBeanException e) {
						AonUtil.addErrorMessage("No se han podido cargar correctamente los devengos");
					}
				}
			}
			return null;
		}
		
		public String getResolvedPayment(){
			ContractPayment payment = (ContractPayment) getPaymentModel().getRowData();
			if(payment!=null && payment.getId()!=null){
				if (NumberUtils.isNumber(payment.getExpression()) ) {
					return payment.getExpression();
				}
			}
			return "expresión";
		}
		
		public boolean isPaymentQuote(){
			return getPaymentQuoteValue() != QuoteType.NO_QUOTE;
		}
		public QuoteType getPaymentQuoteValue(){
			ContractPayment payment = (ContractPayment) getPaymentModel().getRowData();
			if(payment!=null && payment.getId()!=null){
				String expression = payment.getQuoteExpression();
				if(expression!=null){
					if (NumberUtils.isNumber(expression) && Double.parseDouble(expression)==0) {
						return QuoteType.NO_QUOTE;
					} else if (expression.equals(IPREM_FORMMULA)) {
						return QuoteType.IPREM_EXCESS;
					} else if (expression != null) {
						return QuoteType.QUOTE;
					}
				} else if(payment.getPaymentConcept()!=null){
					expression = payment.getPaymentConcept().getQuoteExpression();
					if(expression==null){
						return QuoteType.NO_QUOTE;
					} else if (NumberUtils.isNumber(expression) && Double.parseDouble(expression)==0) {
						return QuoteType.NO_QUOTE;
					} else if (expression.equals(payment.getPaymentConcept().getCode())) {
						return QuoteType.QUOTE;
					} else if (expression.equals(IPREM_FORMMULA)) {
						return QuoteType.IPREM_EXCESS;
					} else {
						return QuoteType.MANUAL;
					}
				}
			}
			return null;
		}
		
		public boolean isPaymentIrpf(){
			return getPaymentIrpfValue() != TaxationType.NO_TAXED;
		}
		public TaxationType getPaymentIrpfValue(){
			ContractPayment payment = (ContractPayment) getPaymentModel().getRowData();
			if(payment!=null && payment.getId()!=null){
				String expression = payment.getIrpfExpression();
				if(expression!=null){
					if (NumberUtils.isNumber(expression) && Double.parseDouble(expression)==0) {
						return TaxationType.NO_TAXED;
					} else if (expression != null) {
						return TaxationType.TAXED;
					}
				} else if(payment.getPaymentConcept()!=null){
					expression = payment.getPaymentConcept().getIrpfExpression();
					if(expression==null){
						return TaxationType.NO_TAXED;
					} else if (NumberUtils.isNumber(expression) && Double.parseDouble(expression)==0) {
						return TaxationType.NO_TAXED;
					} else if (expression.equals(payment.getPaymentConcept().getCode())) {
						return TaxationType.TAXED;
					} else {
						return TaxationType.MANUAL;
					} 
				}
			}
			return null;
		}

		public String getResolvedDeduction(){
			ContractDeduction deduction = (ContractDeduction) getDeductionModel().getRowData();
			if(deduction!=null && deduction.getId()!=null){
				if (NumberUtils.isNumber(deduction.getExpression()) ) {
					return deduction.getExpression();
				}
			}
			return "expresión";
		}
		
		private Date getFilterStartDate(){
			if(getFilterYear()!=null && getFilterMonth()!=null){
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.YEAR, getFilterYear());
				cal.set(Calendar.MONTH, getFilterMonth().getValue());
				cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
				return cal.getTime();
			}
			return null;
		}
		private Date getFilterEndDate(){
			if(getFilterYear()!=null && getFilterMonth()!=null){
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.YEAR, getFilterYear());
				cal.set(Calendar.MONTH, getFilterMonth().getValue());
				cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
				return cal.getTime();
			}
			return null;
		}
		
		public void onChangeCurrentFilter(ValueChangeEvent event){
			boolean searchCurrent = (Boolean) event.getNewValue();
			if(searchCurrent){
				this.filterYear = Calendar.getInstance().get(Calendar.YEAR);
				this.filterMonth = Month.getMonthByValue(Calendar.getInstance().get(Calendar.MONTH));
			}
		}
		
		public void onLoad(ActionEvent event){
			setSelectedData(null);
			setSelectedPayment(null);
			setSelectedDeduction(null);
			dataTracking = null;
			paymentTracking = null;
			specialQuote = null;
			loadContractData(contract);
			loadPayments(contract);
			loadDeductions(contract);
			loadPeculiarQuote(contract);
		}
		public void loadContractData(Contract contract){
			try {
				IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_CONTRACT_ID), contract.getId());
				criteria.addLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_START_DATE), getFilterEndDate());
				Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_END_DATE), getFilterStartDate());
				Expression expr2 = ExpressionUtilities.getNullExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_END_DATE));
				criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));
				criteria.addOrder(bean.getFieldName(IEntityAlias.CONTRACT_DATA_NAME));
				setContractDataModel( new SerializableListDataModel(bean.getList(criteria)));
			} catch (ManagerBeanException e) {
				AonUtil.addErrorMessage("No se han podido cargar correctamente los datos de contrato");
			}
		}
		public void loadDataTracking(Contract contract){
			try {
				IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_CONTRACT_ID), contract.getId());
				criteria.addNotEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_ID), getSelectedData().getId());
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_NAME), getSelectedData().getName());
				criteria.addLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_START_DATE), getSelectedData().getStartDate());
				criteria.addOrder(bean.getFieldName(IEntityAlias.CONTRACT_DATA_NAME));
				criteria.addOrder(bean.getFieldName(IEntityAlias.CONTRACT_DATA_START_DATE), false);
				dataTracking = bean.getList(criteria);
			} catch (ManagerBeanException e) {
				AonUtil.addErrorMessage("No se han podido cargar correctamente los datos de contrato");
			}
		}
		public void loadPayments(Contract contract){
			try {
				IManagerBean bean = BeanManager.getManagerBean(ContractPayment.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_PAYMENT_CONTRACT_ID), contract.getId());
				criteria.addLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_PAYMENT_START_DATE), getFilterEndDate());
				Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_PAYMENT_END_DATE), getFilterStartDate());
				Expression expr2 = ExpressionUtilities.getNullExpression(bean.getFieldName(IEntityAlias.CONTRACT_PAYMENT_END_DATE));
				criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));
				setPaymentModel( new SerializableListDataModel(bean.getList(criteria)));
			} catch (ManagerBeanException e) {
				AonUtil.addErrorMessage("No se han podido cargar correctamente los devengos");
			}
		}
		public void loadPaymentTracking(Contract contract){
			try {
				if(getSelectedPayment().getPaymentConcept()!=null && getSelectedPayment().getPaymentConcept().getId()!=null){
					IManagerBean bean = BeanManager.getManagerBean(ContractPayment.class);
					Criteria criteria = new Criteria();
					criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_PAYMENT_CONTRACT_ID), contract.getId());
					criteria.addNotEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_PAYMENT_ID), getSelectedPayment().getId());
					criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_PAYMENT_PAYMENT_CONCEPT_ID), getSelectedPayment().getPaymentConcept().getId());
					criteria.addLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_PAYMENT_START_DATE), getSelectedPayment().getStartDate());
					criteria.addOrder(bean.getFieldName(IEntityAlias.CONTRACT_PAYMENT_START_DATE), false);
					paymentTracking = bean.getList(criteria);
				}
			} catch (ManagerBeanException e) {
				AonUtil.addErrorMessage("No se han podido cargar correctamente los devengos");
			}
		}
		public void loadDeductions(Contract contract){
			try {
				IManagerBean bean = BeanManager.getManagerBean(ContractDeduction.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DEDUCTION_CONTRACT_ID), contract.getId());
				criteria.addLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DEDUCTION_START_DATE), getFilterEndDate());
				Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DEDUCTION_END_DATE), getFilterStartDate());
				Expression expr2 = ExpressionUtilities.getNullExpression(bean.getFieldName(IEntityAlias.CONTRACT_DEDUCTION_END_DATE));
				criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));
				setDeductionModel( new SerializableListDataModel(bean.getList(criteria)));
			} catch (ManagerBeanException e) {
				AonUtil.addErrorMessage("No se han podido cargar correctamente las deducciones");
			}
		}
		
		public void onSelectData(ActionEvent event){
			setSelectedData((ContractData) getContractDataModel().getRowData());
			loadDataTracking(contract);
		}
		public void onSelectPayment(ActionEvent event){
			setSelectedPayment((ContractPayment) getPaymentModel().getRowData());
			loadPaymentTracking(contract);
		}
		public void onSelectDeduction(ActionEvent event){
			setSelectedDeduction((ContractDeduction) getDeductionModel().getRowData());
		}
		public void onCancelData(ActionEvent event){
			setSelectedData(null);
		}
		public void onCancelPayment(ActionEvent event){
			setSelectedPayment(null);
		}
		public void onCancelDeduction(ActionEvent event){
			setSelectedDeduction(null);
		}
		public void onRemoveData(ActionEvent event){
			try {
				BeanManager.getManagerBean(ContractData.class).remove(getSelectedData());
				loadContractData(contract);
			} catch (ManagerBeanException e) {
				throw new AbortProcessingException("No se ha podido borrar el objeto.");
			}
		}
		public void onRemovePayment(ActionEvent event){
			try {
				BeanManager.getManagerBean(ContractPayment.class).remove(getSelectedPayment());
				loadPayments(contract);
			} catch (ManagerBeanException e) {
				throw new AbortProcessingException("No se ha podido borrar el objeto.");
			}
		}
		public void onRemoveDeduction(ActionEvent event){
			try {
				BeanManager.getManagerBean(ContractDeduction.class).remove(getSelectedDeduction());
				loadDeductions(contract);
			} catch (ManagerBeanException e) {
				throw new AbortProcessingException("No se ha podido borrar el objeto.");
			}
		}
		
		private void loadPeculiarQuote(Contract contract){
			peculiarQuoteMap = new HashMap<String, ContractData>();
			checkedQuoteMap = new HashMap<String, Boolean>();
			
			SEPEUtils utils = SEPEUtils.getInstance();
			Map<String, ContractData> map = utils.getContractDataMap(contract, contract.getStartDate(), contract.getEndDate());

			if(utils.getContractInfoMap(contract).containsKey(ContractVariable.COOPERATIVE_PARTNER.getValue())){
				this.setSpecialQuote(SpecialQuote.COOPERATIVE_PARTNER);
			} else if(utils.getContractInfoMap(contract).containsKey(ContractVariable.ACTIVE_RETIREMENT.getValue())){
				this.setSpecialQuote(SpecialQuote.ACTIVE_RETIREMENT);
			} else if(utils.getContractInfoMap(contract).containsKey(ContractVariable.YOUTH_GUARANTEE.getValue())){
				this.setSpecialQuote(SpecialQuote.YOUTH_GUARANTEE);
			} else {
				this.setSpecialQuote(null);
			}

//			TRABAJADOR
			if(map.containsKey("PORCENTAJE_DESMPL")){
				peculiarQuoteMap.put("PORCENTAJE_DESMPL", map.get("PORCENTAJE_DESMPL"));
				checkedQuoteMap.put("PORCENTAJE_DESMPL", Boolean.TRUE);
			} else {
				ContractData data = new ContractData();
				data.setName("PORCENTAJE_DESMPL");
				peculiarQuoteMap.put("PORCENTAJE_DESMPL", data);
			}
			if(map.containsKey("PORCENTAJE_CGC")){
				peculiarQuoteMap.put("PORCENTAJE_CGC", map.get("PORCENTAJE_CGC"));
				checkedQuoteMap.put("PORCENTAJE_CGC", Boolean.TRUE);
			} else {
				ContractData data = new ContractData();
				data.setName("PORCENTAJE_CGC");
				peculiarQuoteMap.put("PORCENTAJE_CGC", data);
			}
			if(map.containsKey("PORCENTAJE_FP")){
				peculiarQuoteMap.put("PORCENTAJE_FP", map.get("PORCENTAJE_FP"));
				checkedQuoteMap.put("PORCENTAJE_FP", Boolean.TRUE);
			} else {
				ContractData data = new ContractData();
				data.setName("PORCENTAJE_FP");
				peculiarQuoteMap.put("PORCENTAJE_FP", data);
			}
			if(map.containsKey("PORCENTAJE_EXTR")){
				peculiarQuoteMap.put("PORCENTAJE_EXTR", map.get("PORCENTAJE_EXTR"));
				checkedQuoteMap.put("PORCENTAJE_EXTR", Boolean.TRUE);
			} else {
				ContractData data = new ContractData();
				data.setName("PORCENTAJE_EXTR");
				peculiarQuoteMap.put("PORCENTAJE_EXTR", data);
			}
			if(map.containsKey("PORCENTAJE_NEXTR")){
				peculiarQuoteMap.put("PORCENTAJE_NEXTR", map.get("PORCENTAJE_NEXTR"));
				checkedQuoteMap.put("PORCENTAJE_NEXTR", Boolean.TRUE);
			} else {
				ContractData data = new ContractData();
				data.setName("PORCENTAJE_NEXTR");
				peculiarQuoteMap.put("PORCENTAJE_NEXTR", data);
			}
			
//			EMPRESA
			if(map.containsKey("PORCENTAJE_DESMPL_E")){
				peculiarQuoteMap.put("PORCENTAJE_DESMPL_E", map.get("PORCENTAJE_DESMPL_E"));
				checkedQuoteMap.put("PORCENTAJE_DESMPL_E", Boolean.TRUE);
			} else {
				ContractData data = new ContractData();
				data.setName("PORCENTAJE_DESMPL_E");
				peculiarQuoteMap.put("PORCENTAJE_DESMPL_E", data);
			}
			if(map.containsKey("PORCENTAJE_FOGASA")){
				peculiarQuoteMap.put("PORCENTAJE_FOGASA", map.get("PORCENTAJE_FOGASA"));
				checkedQuoteMap.put("PORCENTAJE_FOGASA", Boolean.TRUE);
			} else {
				ContractData data = new ContractData();
				data.setName("PORCENTAJE_FOGASA");
				peculiarQuoteMap.put("PORCENTAJE_FOGASA", data);
			}
			if(map.containsKey("PORCENTAJE_CGC_E")){
				peculiarQuoteMap.put("PORCENTAJE_CGC_E", map.get("PORCENTAJE_CGC_E"));
				checkedQuoteMap.put("PORCENTAJE_CGC_E", Boolean.TRUE);
			} else {
				ContractData data = new ContractData();
				data.setName("PORCENTAJE_CGC_E");
				peculiarQuoteMap.put("PORCENTAJE_CGC_E", data);
			}
			if(map.containsKey("PORCENTAJE_EXTR_E")){
				peculiarQuoteMap.put("PORCENTAJE_EXTR_E", map.get("PORCENTAJE_EXTR_E"));
				checkedQuoteMap.put("PORCENTAJE_EXTR_E", Boolean.TRUE);
			} else {
				ContractData data = new ContractData();
				data.setName("PORCENTAJE_EXTR_E");
				peculiarQuoteMap.put("PORCENTAJE_EXTR_E", data);
			}
			if(map.containsKey("PORCENTAJE_NEXTR_E")){
				peculiarQuoteMap.put("PORCENTAJE_NEXTR_E", map.get("PORCENTAJE_NEXTR_E"));
				checkedQuoteMap.put("PORCENTAJE_NEXTR_E", Boolean.TRUE);
			} else {
				ContractData data = new ContractData();
				data.setName("PORCENTAJE_NEXTR_E");
				peculiarQuoteMap.put("PORCENTAJE_NEXTR_E", data);
			}
			if(map.containsKey("PORCENTAJE_FP_E")){
				peculiarQuoteMap.put("PORCENTAJE_FP_E", map.get("PORCENTAJE_FP_E"));
				checkedQuoteMap.put("PORCENTAJE_FP_E", Boolean.TRUE);
			} else {
				ContractData data = new ContractData();
				data.setName("PORCENTAJE_FP_E");
				peculiarQuoteMap.put("PORCENTAJE_FP_E", data);
			}
			if(map.containsKey("PORCENTAJE_CORTA_DURACION")){
				peculiarQuoteMap.put("PORCENTAJE_CORTA_DURACION", map.get("PORCENTAJE_CORTA_DURACION"));
				checkedQuoteMap.put("PORCENTAJE_CORTA_DURACION", Boolean.TRUE);
			} else {
				ContractData data = new ContractData();
				data.setName("PORCENTAJE_CORTA_DURACION");
				peculiarQuoteMap.put("PORCENTAJE_CORTA_DURACION", data);
			}
			if(map.containsKey("PORCENTAJE_IT")){
				peculiarQuoteMap.put("PORCENTAJE_IT", map.get("PORCENTAJE_IT"));
				checkedQuoteMap.put("PORCENTAJE_IT", Boolean.TRUE);
			} else {
				ContractData data = new ContractData();
				data.setName("PORCENTAJE_IT");
				peculiarQuoteMap.put("PORCENTAJE_IT", data);
			}
			if(map.containsKey("PORCENTAJE_IMS")){
				peculiarQuoteMap.put("PORCENTAJE_IMS", map.get("PORCENTAJE_IMS"));
				checkedQuoteMap.put("PORCENTAJE_IMS", Boolean.TRUE);
			} else {
				ContractData data = new ContractData();
				data.setName("PORCENTAJE_IMS");
				peculiarQuoteMap.put("PORCENTAJE_IMS", data);
			}
		}
		
		public void onAcceptPeculiarQuote(ActionEvent event){
			try {
				IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
				for(String key: peculiarQuoteMap.keySet()){
					ContractData data = peculiarQuoteMap.get(key);
					if( checkedQuoteMap.containsKey(key) && checkedQuoteMap.get(key) && StringUtils.isNotBlank(data.getExpression()) ){
						data.setContract(contract);
						data.setStartDate(contract.getStartDate());
						data.setEndDate(contract.getEndDate());
						bean.insertOrUpdate(data);
					} else {
						if(data.getId() != null){
							bean.remove(data);
						}
					}
				}
			} catch (ManagerBeanException e) {
				String msg = "Error al grabar los porcentajes de cotizacion. (" +e.getMessage() + ")";
				AonUtil.addErrorMessage(msg);
			}
			ContractController controller = ((ContractController)AonUtil.getRegisteredBean(IPayrollConstants.CONTRACT_CONTROLLER));
			try {
				IManagerBean bean = BeanManager.getManagerBean(ContractInfo.class);
				SEPEUtils utils = SEPEUtils.getInstance();
				ContractInfo info = utils.getContractInfoMap(contract, null, null).get(ContractVariable.COOPERATIVE_PARTNER.getValue());
				
				if(getSpecialQuote()!=null && getSpecialQuote()==SpecialQuote.COOPERATIVE_PARTNER){
					if(info==null || info.getId()==null){
						ContractUtils.getInstance().enableCooperativePartner(contract);
					}
				} else {
					if(info!=null){
						bean.remove(info);
					}
				}
				if(getSpecialQuote()!=null && getSpecialQuote()==SpecialQuote.ACTIVE_RETIREMENT){
					if(info==null || info.getId()==null){
						ContractUtils.getInstance().enableActiveRetirement(contract);
					}
				} else {
					if(info!=null){
						bean.remove(info);
					}
				}
				if(getSpecialQuote()!=null && getSpecialQuote()==SpecialQuote.YOUTH_GUARANTEE){
					if(info==null || info.getId()==null){
						ContractUtils.getInstance().enableYouthGuarantee(contract);
					}
				} else {
					if(info!=null){
						bean.remove(info);
					}
				}
				controller.getContractUtils().loadContractInfo((Contract) controller.getTo(), controller.getParams());
			} catch (ManagerBeanException e) {
				String msg = "Error al grabar los porcentajes de cotizacion. (" +e.getMessage() + ")";
				AonUtil.addErrorMessage(msg);
			}
			try {
				ContractUtils.getInstance().updateContractData(contract, controller.getParams());
			} catch (ControllerListenerException e) {
				String msg = "Error al grabar el tipo de cotizacion. (" +e.getMessage() + ")";
				AonUtil.addErrorMessage(msg);
			}
		}
	}
	
// ************************************
// ************************************
	public static class ContractParams implements Serializable {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
		

		private ContractInfo sepeStatusInfo;
		private ContractInfo ssStatusInfo;
		
		private boolean retaPartialTime;
		private SpecialQuote specialQuote;
		private ContractOption contractOption;
		private ContractType contractType;
		private ContractModelCode contractModelCode;
		private ContractCode contractCode;
		private QuoteGroup quoteGroup;
		private OccupationType occupationType;
		private Double irpf;
		private ContractCode tc2Code;
		private ContractDuration contractDuration;
		private ContractWorkingDay contractWorkingDay;
		private CNO cno;
		private ContractBonus bonus;
		private List<ITransferObject> bonuses;
		private DataModel bonusModel;
		private T54 collectivePeculiarityQuote;
		private T53 partialTimeReductionIndicator;
		private T55 disabilityIndicator;
		
		private boolean agreementSalaryCheck;
		private boolean agreementSalary;
		private Double grossSalary;
		private TrainingCenter trainingCenter;
		private TrainingCourse trainingCourse;
		private Date trainingStartDate;
		private Date trainingEndDate;
		private ModelOption contractModelOption;
		private Double weekHours;
		
		private Double[] weekDayHours = new Double[7];

		
		private TLDCAUSS suspensionCause;
		private String contractEndCode;
		private String contractEndDescription;
		private Integer settleAdvanceNoticeDays;
		
		private String sepeContractId;
		private String code;
		
		
		public String getSepeContractId() {
			return sepeContractId;
		}
		public void setSepeContractId(String sepeContractId) {
			this.sepeContractId = sepeContractId;
		}
		public ContractInfo getSepeStatusInfo() {
			return sepeStatusInfo;
		}
		public void setSepeStatusInfo(ContractInfo sepeStatusInfo) {
			this.sepeStatusInfo = sepeStatusInfo;
		}
		public ContractSepeStatus getSepeStatus() {
			if(getSepeStatusInfo()!=null){
				return ContractSepeStatus.valueOf(getSepeStatusInfo().getExpression());
			}
			return null;
		}
		public void setSepeStatus(ContractSepeStatus sepeStatus) {
			if(this.sepeStatusInfo!=null && sepeStatus!=null){
				this.sepeStatusInfo.setExpression(sepeStatus.getValue());
			}
		}
		public ContractInfo getSsStatusInfo() {
			return ssStatusInfo;
		}
		public void setSsStatusInfo(ContractInfo ssStatusInfo) {
			this.ssStatusInfo = ssStatusInfo;
		}
		public ContractSsStatus getSsStatus() {
			if(getSsStatusInfo()!=null){
				return ContractSsStatus.valueOf(getSsStatusInfo().getExpression());
			}
			return null;
		}
		public void setSsStatus(ContractSsStatus ssStatus) {
			if(this.ssStatusInfo!=null && ssStatus!=null){
				this.ssStatusInfo.setExpression(ssStatus.getValue());
			}
		}
		public boolean isAgreementSalaryCheck() {
			return agreementSalaryCheck;
		}
		public void setAgreementSalaryCheck(boolean agreementSalaryCheck) {
			this.agreementSalaryCheck = agreementSalaryCheck;
		}
		public boolean isAgreementSalary() {
			return agreementSalary;
		}
		public void setAgreementSalary(boolean agreementSalary) {
			this.agreementSalary = agreementSalary;
		}
		public Double getGrossSalary() {
			return grossSalary;
		}
		public void setGrossSalary(Double grossSalary) {
			this.grossSalary = grossSalary;
		}
		public TrainingCenter getTrainingCenter() {
			return trainingCenter;
		}
		public void setTrainingCenter(TrainingCenter trainingCenter) {
			this.trainingCenter = trainingCenter;
		}
		public TrainingCourse getTrainingCourse() {
			return trainingCourse;
		}
		public void setTrainingCourse(TrainingCourse trainingCourse) {
			this.trainingCourse = trainingCourse;
			if(trainingCourse!=null && trainingCourse.getId()!=null && trainingCourse.getCNO()!=null){
				setCno(trainingCourse.getCNO());
			}
		}
		public Date getTrainingStartDate() {
			return trainingStartDate;
		}
		public void setTrainingStartDate(Date trainingStartDate) {
			this.trainingStartDate = trainingStartDate;
		}
		public Date getTrainingEndDate() {
			return trainingEndDate;
		}
		public void setTrainingEndDate(Date trainingEndDate) {
			this.trainingEndDate = trainingEndDate;
		}
		
		public ModelOption getContractModelOption() {
			return contractModelOption;
		}
		public void setContractModelOption(ModelOption contractModelOption) {
			this.contractModelOption = contractModelOption;
		}
		public Double getIrpf() {
			return irpf;
		}
		public void setIrpf(Double irpf) {
			this.irpf = irpf;
		}
		public ContractOption getContractOption() {
			return contractOption;
		}
		public void setContractOption(ContractOption contractOption) {
			this.contractOption = contractOption;
		}

		public ContractType getContractType() {
			return contractType;
		}
		public void setContractType(ContractType contractType) {
			this.contractType = contractType;
		}
		public ContractModelCode getContractModelCode() {
			return contractModelCode;
		}
		public void setContractModelCode(ContractModelCode contractModelCode) {
			this.contractModelCode = contractModelCode;
		}
		
		public String getContractCode() {
			return code;
		}
		public void setContractCode(String contractCode) {
			this.code = contractCode;
		}
		public ContractCode getContractCodeEnum() {
			return ContractCode.getContractCodeByValue(code);
		}
		public QuoteGroup getQuoteGroup() {
			return quoteGroup;
		}
		public void setQuoteGroup(QuoteGroup quoteGroup) {
			this.quoteGroup = quoteGroup;
		}
		public OccupationType getOccupationType() {
			return occupationType;
		}
		public void setOccupationType(OccupationType occupationType) {
			this.occupationType = occupationType;
		}
		public ContractCode getTc2Code() {
			return tc2Code;
		}
		public void setTc2Code(ContractCode tc2Code) {
			this.tc2Code = tc2Code;
		}
		public ContractDuration getContractDuration() {
			return contractDuration;
		}
		public void setContractDuration(ContractDuration contractDuration) {
			this.contractDuration = contractDuration;
		}
		public ContractWorkingDay getContractWorkingDay() {
			return contractWorkingDay;
		}
		public void setContractWorkingDay(ContractWorkingDay contractWorkingDay) {
			this.contractWorkingDay = contractWorkingDay;
		}
		public CNO getCno() {
			return cno;
		}
		public void setCno(CNO cno) {
			this.cno = cno;
		}
		public ContractBonus getBonus() {
			return bonus;
		}
		public void setBonus(ContractBonus bonus) {
			this.bonus = bonus;
		}
		public List<ITransferObject> getBonuses() {
			return bonuses;
		}
		public void setBonuses(List<ITransferObject> bonuses) {
			this.bonuses = bonuses;
		}
		public DataModel getBonusModel() {
			if(bonusModel==null){
				bonusModel = new SerializableListDataModel(getBonuses());
			}
			return bonusModel;
		}
		public void setBonusModel(DataModel bonusModel) {
			this.bonusModel = bonusModel;
		}
		public boolean isRetaQuote() {
			return specialQuote!=null && specialQuote==SpecialQuote.RETA;
		}
		
		public boolean isRetaPartialTime() {
			return retaPartialTime;
		}
		public void setRetaPartialTime(boolean retaPartialTime) {
			this.retaPartialTime = retaPartialTime;
		}
		public boolean isCooperativePartnerQuote() {
			return specialQuote!=null && specialQuote==SpecialQuote.COOPERATIVE_PARTNER;
		}
		public SpecialQuote getSpecialQuote() {
			return specialQuote;
		}
		public void setSpecialQuote(SpecialQuote specialQuote) {
			this.specialQuote = specialQuote;
		}
		public Double getWeekHours() {
			return weekHours;
		}
		public void setWeekHours(Double weekHours) {
			this.weekHours = weekHours;
		}
		
		public Double[] getWeekDayHours() {
			return weekDayHours;
		}
		public void setWeekDayHours(Double[] weekDayHours) {
			this.weekDayHours = weekDayHours;
		}
		public void reloadTotalWeekHours(ActionEvent event){
			Double total = 0.0;
			for(int i=0; i<7; i++){
				if( weekDayHours[i]!=null ){
					total +=  weekDayHours[i];
				}
			}
			weekHours = total;
		}
		
		
		public T54 getCollectivePeculiarityQuote() {
			return collectivePeculiarityQuote;
		}
		public void setCollectivePeculiarityQuote(T54 collectivePeculiarityQuote) {
			this.collectivePeculiarityQuote = collectivePeculiarityQuote;
		}
		public T53 getPartialTimeReductionIndicator() {
			return partialTimeReductionIndicator;
		}
		public void setPartialTimeReductionIndicator(T53 partialTimeReductionIndicator) {
			this.partialTimeReductionIndicator = partialTimeReductionIndicator;
		}
		public T55 getDisabilityIndicator() {
			return disabilityIndicator;
		}
		public void setDisabilityIndicator(T55 disabilityIndicator) {
			this.disabilityIndicator = disabilityIndicator;
		}
		public TLDCAUSS getSuspensionCause() {
			return suspensionCause;
		}
		public void setSuspensionCause(TLDCAUSS suspensionCause) {
			this.suspensionCause = suspensionCause;
		}
		public Integer getSettleAdvanceNoticeDays() {
			return settleAdvanceNoticeDays;
		}
		public void setSettleAdvanceNoticeDays(Integer settleAdvanceNoticeDays) {
			this.settleAdvanceNoticeDays = settleAdvanceNoticeDays;
		}
		public Date getSettleAdvanceNoticeDate() {
			Contract contract = (Contract) ((ContractController)AonUtil.getRegisteredBean(IPayrollConstants.CONTRACT_CONTROLLER)).getTo(); 
			if( contract.getEndDate()!=null && settleAdvanceNoticeDays!=null){
				Calendar cal = Calendar.getInstance();
				cal.setTime(contract.getEndDate());
				cal.add(Calendar.DAY_OF_MONTH, settleAdvanceNoticeDays);
				return cal.getTime();
			}
			return null;
		}
		
	}
	
	public enum SpecialQuote {
		RETA,
		COOPERATIVE_PARTNER,
//		Jubilación Activa
		ACTIVE_RETIREMENT,
//		Garantía Juvenil
		YOUTH_GUARANTEE
		;
	}
	
	public class WorkdayManager implements Serializable {
		
		private static final long serialVersionUID = 1L;
		
		private Date workdayDate;
		private ContractData[] weekDayHours;
		private List<ContractData[]> weekList;
		
		public Date getWorkdayDate() {
			return workdayDate;
		}
		public void setWorkdayDate(Date workdayDate) {
			this.workdayDate = workdayDate;
		}
		public ContractData[] getWeekDayHours() {
			return weekDayHours;
		}
		public void setWeekDayHours(ContractData[] weekDayHours) {
			this.weekDayHours = weekDayHours;
		}
		
		public List<ContractData[]> getWeekList(){
			if(weekList==null){
				loadWeekList();
			}
			return weekList;
		}
		public int getWeekListCount(){
			return getWeekList()==null?0:getWeekList().size();
		}
		public ContractData[] getCurrentWeekDayHours(){
			List<ContractData[]> list = getWeekList();
			if(list!=null && list.size()>0){
				return list.get(0);
			}
			return null;
		}
		public Double getWeekHours(){
			Double total = 0.0;
			ContractData[] weekDayHours = getCurrentWeekDayHours();
			for(int i=0; i<7; i++){
				if( weekDayHours[i]!=null && NumberUtils.isNumber(weekDayHours[i].getExpression())){
					total += Double.parseDouble(weekDayHours[i].getExpression());
				}
			}
			return total;
		}
		
		public void onInit(ActionEvent event){
			weekDayHours = null;
			workdayDate = null;
			weekList = null;
		}
			
		public void onReset(ActionEvent event){
			weekDayHours = new ContractData[7];
			for(int i=0; i<7;i++){
				weekDayHours[i] = new ContractData();
			}
			if(weekList==null || weekList.size()==0){
				setWorkdayDate(((Contract)getTo()).getStartDate());
			} else {
				setWorkdayDate(new Date());
			}
		}
		
		public void onAccept(ActionEvent event) throws ManagerBeanException{
			
			checkDate();
			
			closePrevious();
			
			Double weekHours = 0.0;
			IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
			for(int i=0; i<7;i++){
				ContractData data = weekDayHours[i];
				if(data==null)
					data = new ContractData();
				if(i==0){
					data.setName(ContextVariable.MONDAY_HOURS.getName());
				} else if(i==1){
					data.setName(ContextVariable.TUESDAY_HOURS.getName());
				} else if(i==2){
					data.setName(ContextVariable.WEDNESDAY_HOURS.getName());
				} else if(i==3){
					data.setName(ContextVariable.THURSDAY_HOURS.getName());
				} else if(i==4){
					data.setName(ContextVariable.FRIDAY_HOURS.getName());
				} else if(i==5){
					data.setName(ContextVariable.SATURDAY_HOURS.getName());
				} else if(i==6){
					data.setName(ContextVariable.SUNDAY_HOURS.getName());
				}
				if(data.getExpression()==null || data.getExpression().equals(""))
					data.setExpression("0.0");
				data.setStartDate(getWorkdayDate());
				data.setContract((Contract) getTo());
				data.setDomain(((Contract) getTo()).getDomain());
				bean.insert(data);
				
				if(data.getExpression()!=null && NumberUtils.isNumber(data.getExpression())){
					weekHours += Double.parseDouble(data.getExpression());
				}
			}
			
			onInit(event);
		}
		
		private void checkDate() {
			Date lastDate = null;
			if(weekList!=null && weekList.size()>0){
				ContractData[] week = weekList.get(0);
				for(ContractData data: week){
					if(data!=null && data.getId()!=null){
						lastDate = data.getStartDate();
					}
				}
				if(workdayDate!=null && !workdayDate.after(lastDate)){
					Calendar cal = Calendar.getInstance();
					cal.setTime(lastDate);
					cal.add(Calendar.DAY_OF_MONTH, 1);
					workdayDate = cal.getTime();
					AonUtil.addErrorMessage("La fecha se solapa datos anteriores.");
					throw new AbortProcessingException("La fecha se solapa datos anteriores.");
				}
			}
		}
		public void onRemove(ActionEvent event) throws ManagerBeanException{
			openPrevious();
			
			IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
			ContractData[] week = weekList.get(0);
			for(ContractData data: week){
				if(data!=null && data.getId()!=null){
					bean.remove(data);
				}
			}
			
			onInit(event);
		}
		
		private void closePrevious() throws ManagerBeanException {
			IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
			if(weekList!=null && weekList.size()>0){
				ContractData[] week = weekList.get(0);
				for(ContractData data: week){
					if(data!=null && data.getId()!=null){
						Calendar cal = Calendar.getInstance();
						cal.setTime(workdayDate);
						cal.add(Calendar.DAY_OF_MONTH, -1);
						data.setEndDate(cal.getTime());
						bean.update(data);
					}
				}
			}
		}
		
		private void openPrevious() throws ManagerBeanException {
			IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
			if(weekList!=null && weekList.size()>1){
				ContractData[] week = weekList.get(1);
				for(ContractData data: week){
					if(data!=null && data.getId()!=null){
						data.setEndDate(null);
						bean.update(data);
					}
				}
			}
			
		}
		
		public DataModel getModel(){
			return new SerializableListDataModel( getWeekList() );
		}
		
		public void loadWeekList(){
			weekList = SEPEUtils.getInstance().obtainWeekList((Contract) getTo());
		}
		
	}
}
