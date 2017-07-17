package com.esferalia.aon.ui.payroll.controller;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import net.aonsolutions.core.dbutils.DatabaseUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryDirStaff;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.file.payroll.contract.pdf.IContractFieldName;
import com.esferalia.aon.file.payroll.contract.pdf.ModelOption;
import com.esferalia.aon.file.payroll.contract.pdf.PdfFieldIndefinite;
import com.esferalia.aon.file.payroll.contract.pdf.PdfFieldLearning;
import com.esferalia.aon.file.payroll.contract.pdf.PdfFieldPractice;
import com.esferalia.aon.file.payroll.contract.pdf.PdfFieldTemporary;
import com.esferalia.aon.file.payroll.contract.pdf.model.IndefiniteModel;
import com.esferalia.aon.file.payroll.contract.pdf.model.LearningModel;
import com.esferalia.aon.file.payroll.contract.pdf.model.PracticeModel;
import com.esferalia.aon.file.payroll.contract.pdf.model.TemporaryModel;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractInfo;
import com.esferalia.aon.payroll.ContractInfo.ContractVariable;
import com.esferalia.aon.payroll.enumeration.ContractModel;
import com.esferalia.aon.ui.payroll.controller.contract.ContractController;

public class ContractInfoController extends BasicController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ContractInfoController.class);

	private static final String CONTRACT_FIELDS_CONTROLLER = "contractDocumentInfo";

	private boolean commonInfo;
	
	private ContractModel contractModel;
	
	private List<ContractField> contractFieldList;
	
	private List<String> overridableFieldList;
	
	private List<ContractField> indefiniteFieldList;
	private List<ContractField> temporaryFieldList;
	private List<ContractField> practiceFieldList;
	private List<ContractField> learningFieldList;
	

	
	public boolean isCommonInfo() {
		return commonInfo;
	}
	public void setCommonInfo(boolean commonInfo) {
		this.commonInfo = commonInfo;
	}
	public ContractModel getContractModel() {
		return contractModel;
	}
	public void setContractModel(ContractModel contractModel) {
		this.contractModel = contractModel;
	}
	public List<ContractField> getContractFieldList() {
		return contractFieldList;
	}
	public void setContractFieldList(List<ContractField> contractFieldList) {
		this.contractFieldList = contractFieldList;
	}	
	public List<ContractField> getIndefiniteFieldList() {
		if(indefiniteFieldList==null){
			indefiniteFieldList = loadFieldList(PdfFieldIndefinite.values());
		}
		return indefiniteFieldList;
	}
	public void setIndefiniteFieldList(List<ContractField> indefiniteFieldList) {
		this.indefiniteFieldList = indefiniteFieldList;
	}
	public List<ContractField> getTemporaryFieldList() {
		if(temporaryFieldList==null){
			temporaryFieldList = loadFieldList(PdfFieldTemporary.values());
		}
		return temporaryFieldList;
	}
	public void setTemporaryFieldList(List<ContractField> temporaryFieldList) {
		this.temporaryFieldList = temporaryFieldList;
	}
	public List<ContractField> getPracticeFieldList() {
		if(practiceFieldList==null){
			practiceFieldList = loadFieldList(PdfFieldPractice.values());
		}
		return practiceFieldList;
	}
	public void setPracticeFieldList(List<ContractField> practiceFieldList) {
		this.practiceFieldList = practiceFieldList;
	}
	public List<ContractField> getLearningFieldList() {
		if(learningFieldList==null){
			learningFieldList = loadFieldList(PdfFieldLearning.values());
		}
		return learningFieldList;
	}
	public void setLearningFieldList(List<ContractField> learningFieldList) {
		this.learningFieldList = learningFieldList;
	}

	private List<ContractField> loadFieldList(IContractFieldName[] values) {
		List<ContractField> fieldList = new LinkedList<ContractField>();
		for(IContractFieldName fn: getModelFields(values)){
			ContractField field = new ContractField();
			field.setContractInfo(obtainDomainField(fn));
			field.setFieldName(fn);
			fieldList.add(field);
		}
		return fieldList;
	}
	
	@Override
	public void initializeModel() {
		overridableFieldList = null;
		try {
			if(isCommonInfo()){
				this.getCriteria().addNullExpression("ContractInfo.contract");
			} else {
				Contract contract = (Contract) FormUtil.getController(IPayrollConstants.CONTRACT_CONTROLLER).getTo();
				this.getCriteria().addEqualExpression(this.getFieldName(IEntityAlias.CONTRACT_INFO_CONTRACT_ID), contract.getId());
				if(this.getBeanName().equals(CONTRACT_FIELDS_CONTROLLER)){
					this.getCriteria().addInExpression(this.getFieldName(IEntityAlias.CONTRACT_INFO_NAME), getOverridableFieldNames(contract));
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> Unable to clear criteria! ",e);
			addMessage(e.getMessage());
		}
		super.initializeModel();
	}
	
	private ContractInfo obtainDomainField(IContractFieldName fn) {
		String suffix = "";
		if(fn instanceof PdfFieldIndefinite){
			suffix = "I_";
		} else if(fn instanceof PdfFieldTemporary){
			suffix = "T_";
		} else if(fn instanceof PdfFieldPractice){
			suffix = "P_";
		} else if(fn instanceof PdfFieldLearning){
			suffix = "L_";
		}
		ContractInfo contractInfo  = null;
		try {
			Criteria criteria = new Criteria(); 
			criteria.addNullExpression("ContractInfo.contract");
			criteria.addEqualExpression(this.getFieldName(IEntityAlias.CONTRACT_INFO_NAME), suffix + fn.toString());
			List<ITransferObject> list = this.getManagerBean().getList(criteria);
			if(!list.isEmpty()){
				contractInfo = (ContractInfo) list.get(0); 
				if(StringUtils.isNotBlank(contractInfo.getExpression())){
					contractInfo.setExpression(contractInfo.getExpression().replace("\"", ""));
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> Unable to load contract document fields from contract_info ",e);
			addMessage(e.getMessage());
		}
		if(contractInfo == null){
			contractInfo = new ContractInfo();
			contractInfo.setContract(null);
			contractInfo.setStartDate(new Date());
			contractInfo.setEndDate(null);
			contractInfo.setName(suffix + fn.toString());
		}
		return contractInfo;
	}
	
	private List<String> getOverridableFieldNames(Contract contract){
		if(overridableFieldList == null){
			overridableFieldList = new LinkedList<String>();
			for(IContractFieldName fn: getOverridableFields(contract)){
				if(fn.isOverridable()){
					overridableFieldList.add(fn.toString());
				}
			}
			if(overridableFieldList.isEmpty()){
				overridableFieldList.add("");
			}
		}
		return overridableFieldList;
	}
	
	private List<IContractFieldName> getOverridableFields(Contract contract){
		List<IContractFieldName> list = new LinkedList<IContractFieldName>();
		
		ContractController controller = (ContractController) FormUtil.getController(IPayrollConstants.CONTRACT_CONTROLLER);
		ModelOption contractModel = controller.getParams().getContractModelOption();
		if(contract!=null && contractModel!=null){
			if(IndefiniteModel.MODEL_NAME.equals(contractModel.getPdfModel())){
				addAllOptionFields(list, PdfFieldIndefinite.values(), contractModel.toString());
			} else if(TemporaryModel.MODEL_NAME.equals(contractModel.getPdfModel())){
				addAllOptionFields(list, PdfFieldTemporary.values(), contractModel.toString());
			} else if(LearningModel.MODEL_NAME.equals(contractModel.getPdfModel())){
				addAllOptionFields(list, PdfFieldLearning.values(), contractModel.toString());
			} else if(PracticeModel.MODEL_NAME.equals(contractModel.getPdfModel())){
				addAllOptionFields(list, PdfFieldPractice.values(), contractModel.toString());
			}
		}
		
		return list;
	}

	private List<IContractFieldName> getModelFields(IContractFieldName[] values){
		List<IContractFieldName> list = new LinkedList<IContractFieldName>();
		for(IContractFieldName field: values){
			if( field.isOverridable() ){
				if(field.toString().length()>32){
					LOGGER.error(" ######## ContractInfo ########  OPTION FIELD NAME TOO LONG -> " + field.toString());
				} else {
					list.add(field);
				}
			}
		}
		return list;
	}
	
	private void addAllOptionFields(List<IContractFieldName> list , IContractFieldName[] fieldNameValues, String option){
		option = StringUtils.substring(option, option.indexOf("_")+1, option.length()); 
		for(IContractFieldName field: fieldNameValues){ 
			if( (field.isCommonValue() ||  StringUtils.substring(field.toString(), 0, field.toString().indexOf("_")).equals(option)) 
					&& field.isOverridable() ){
				if(field.toString().length()>32){
					LOGGER.error(" ######## ContractInfo ########  OPTION FIELD NAME TOO LONG -> " + field.toString());
				} else {
					list.add(field);
				}
			}
		}
	}
	
	/*
	 * Contract especific field functions
	 */
	
	public ContractInfo obtainContractField(Contract contract, IContractFieldName fieldName, boolean completeDefault){
		ContractInfo contractInfo  = null;
		try {
			Criteria criteria = new Criteria(); 
			criteria.addEqualExpression(this.getFieldName(IEntityAlias.CONTRACT_INFO_CONTRACT_ID), contract.getId());
			if(this.getBeanName().equals(CONTRACT_FIELDS_CONTROLLER)){
				criteria.addInExpression(this.getFieldName(IEntityAlias.CONTRACT_INFO_NAME), getOverridableFieldNames(contract));
			}
			criteria.addEqualExpression(this.getFieldName(IEntityAlias.CONTRACT_INFO_NAME), fieldName.toString());
			List<ITransferObject> list = this.getManagerBean().getList(criteria);
			if(!list.isEmpty()){
				contractInfo = (ContractInfo) list.get(0); 
				if(StringUtils.isNotBlank(contractInfo.getExpression())){
					contractInfo.setExpression(contractInfo.getExpression().replace("\"", ""));
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> Unable to load contract document fields from contract_info ",e);
			addMessage(e.getMessage());
		}
		if(contractInfo == null){
			contractInfo = new ContractInfo();
			contractInfo.setContract(contract);
			contractInfo.setStartDate(contract.getStartDate());
			contractInfo.setEndDate(contract.getEndDate());
			contractInfo.setName(fieldName.toString());
			if(completeDefault){
				String expression = obtainFieldValue(fieldName.toString(), DomainManager.getCurrentDomain());
				if(expression==null){
					expression = obtainFieldValue(fieldName.toString(), DomainManager.getDomainProvider().getParentDomain());
				}
				contractInfo.setExpression(expression);
			}
		}
		return contractInfo;
	}
	
	private String obtainFieldValue(String name, Integer domain) {
		ContractController controller = (ContractController) FormUtil.getController(IPayrollConstants.CONTRACT_CONTROLLER);
		String contractModel = controller.getParams().getContractModelOption().getPdfModel();
		if(StringUtils.isNotBlank(contractModel)){
			String suffix = "";
			if(IndefiniteModel.MODEL_NAME.equals(contractModel)){
				suffix = "I_";
			} else if(TemporaryModel.MODEL_NAME.equals(contractModel)){
				suffix = "T_";
			} else if(LearningModel.MODEL_NAME.equals(contractModel)){
				suffix = "L_";
			} else if(PracticeModel.MODEL_NAME.equals(contractModel)){
				suffix = "P_";
			}
			Connection conn = null;
			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
				conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
				String select = "SELECT expression FROM contract_info WHERE contract is null" +
						" AND domain = " + domain +
						" AND name = '" + suffix + name + "';";
				ps = conn.prepareStatement(select);
				rs = ps.executeQuery();
				if (rs.next())
					return rs.getString(1);				
			} catch (Exception e) {
				LOGGER.error(e.getMessage());
			} finally {
				DatabaseUtil.closeQuietly(ps);
				DatabaseUtil.closeQuietly(conn);
			}
		}
		return null;
	}
	
	public void onLoadContractFields(ActionEvent event){
		overridableFieldList = null;
		contractFieldList = new LinkedList<ContractField>();
		Contract contract = (Contract) FormUtil.getController(IPayrollConstants.CONTRACT_CONTROLLER).getTo();
		loadContractFields(contract, false);
	}
	
	public void loadContractFields(Contract contract, boolean completeDefault){
		overridableFieldList = null;
		contractFieldList = new LinkedList<ContractField>();
		for(IContractFieldName fn: getOverridableFields(contract)){
			ContractField field = new ContractField();
			field.setContractInfo(obtainContractField(contract, fn, completeDefault));
			field.setFieldName(fn);
			contractFieldList.add(field);
		}
		completeDirStaff(contract);
	}
	
	private void completeDirStaff(Contract contract) {
		ContractController controller = (ContractController) FormUtil.getController(IPayrollConstants.CONTRACT_CONTROLLER);
		IContractFieldName nameField = null;
		IContractFieldName nifField = null;
		IContractFieldName chargeField = null;
		if(controller.getParams()!=null && controller.getParams().getContractModelOption()!=null
			&& controller.getParams().getContractModelOption().getPdfModel()!=null){
			String contractModel = controller.getParams().getContractModelOption().getPdfModel();
			if(IndefiniteModel.MODEL_NAME.equals(contractModel)){
				nameField = PdfFieldIndefinite.ENTERPRISE_DIR_STAFF_NAME;
				nifField = PdfFieldIndefinite.ENTERPRISE_DIR_STAFF_NIF;
				chargeField = PdfFieldIndefinite.ENTERPRISE_DIR_STAFF_CHARGE;
			} else if(TemporaryModel.MODEL_NAME.equals(contractModel)){
				nameField = PdfFieldTemporary.ENTERPRISE_DIR_STAFF_NAME;
				nifField = PdfFieldTemporary.ENTERPRISE_DIR_STAFF_NIF;
				chargeField = PdfFieldTemporary.ENTERPRISE_DIR_STAFF_CHARGE;
			} else if(LearningModel.MODEL_NAME.equals(contractModel)){
				nameField = PdfFieldTemporary.ENTERPRISE_DIR_STAFF_NAME;
				nifField = PdfFieldTemporary.ENTERPRISE_DIR_STAFF_NIF;
				chargeField = PdfFieldTemporary.ENTERPRISE_DIR_STAFF_CHARGE;
			} else if(PracticeModel.MODEL_NAME.equals(contractModel)){
				nameField = PdfFieldPractice.ENTERPRISE_DIR_STAFF_NAME;
				nifField = PdfFieldPractice.ENTERPRISE_DIR_STAFF_NIF;
				chargeField = PdfFieldPractice.ENTERPRISE_DIR_STAFF_CHARGE;
			}
			
			ContractInfo name = null;
			ContractInfo nif = null;
			ContractInfo charge = null;
			for(ContractField field: getContractFieldList()){
				if(StringUtils.equals(nameField.toString(), field.getContractInfo().getName())){
					name = field.getContractInfo();
				}
				if(StringUtils.equals(nifField.toString(), field.getContractInfo().getName())){
					nif = field.getContractInfo();
				}
				if(StringUtils.equals(chargeField.toString(), field.getContractInfo().getName())){
					charge = field.getContractInfo();
				}
			}
			if( name != null && StringUtils.isBlank(name.getExpression()) 
					&& nif != null && StringUtils.isBlank(nif.getExpression())
					&& charge != null && StringUtils.isBlank(charge.getExpression()) ){
				try {
					RegistryDirStaff rDirStaff = obtainRegistryDirStaff(contract.getWorkPlace().getEnterprise().getRegistry()); 
					name.setExpression(rDirStaff.getName());
					nif.setExpression(rDirStaff.getDocument());
					
					String rDirStaddCharge = null;
					if ( rDirStaff.isShareHolder() ){
						rDirStaddCharge = "Socio";
					} else if ( rDirStaff.isRepresentative() ){
						rDirStaddCharge = "Apoderado";
					} else if( rDirStaff.isDirector() ){
						rDirStaddCharge = "Administrador";
					} else if ( rDirStaff.isRepresentativeLabor() ){
						rDirStaddCharge = "Repr. laboral";
					}
					charge.setExpression(rDirStaddCharge);
				} catch (NullPointerException e) {
					// do nothing
				} catch (ManagerBeanException e) {
					// do nothing
				}
			}
		}
	}
	
	public RegistryDirStaff obtainRegistryDirStaff(Registry registry) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(RegistryDirStaff.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_DIR_STAFF_REGISTRY_ID), registry.getId());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_DIR_STAFF_REPRESENTATIVE_LABOR), Boolean.TRUE);
		Expression exp1 = ExpressionUtilities.getGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_DIR_STAFF_DUE_DATE), new Date());
		Expression exp2 = ExpressionUtilities.getNullExpression(bean.getFieldName(IEntityAlias.REGISTRY_DIR_STAFF_DUE_DATE));
		criteria.addExpression(ExpressionUtilities.getOrExpression(exp1, exp2));
		List<ITransferObject> list = bean.getList(criteria);
		if(!list.isEmpty()){
			return (RegistryDirStaff) list.get(0);
		} else {
			bean = BeanManager.getManagerBean(RegistryDirStaff.class);
			criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_DIR_STAFF_REGISTRY_ID), registry.getId());
			exp1 = ExpressionUtilities.getGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_DIR_STAFF_DUE_DATE), new Date());
			exp2 = ExpressionUtilities.getNullExpression(bean.getFieldName(IEntityAlias.REGISTRY_DIR_STAFF_DUE_DATE));
			criteria.addExpression(ExpressionUtilities.getOrExpression(exp1, exp2));
			list = bean.getList(criteria);
			if(!list.isEmpty()){
				return (RegistryDirStaff) list.get(0);
			}
		}
		return null;
	}
	
	public void saveContractFields(){
		saveContractFields(contractFieldList);
	}
	
	private void saveContractFields(List<ContractField> contractFieldList) {
		try {
			if(contractFieldList!=null){
				for(ContractField field: contractFieldList){
					if(StringUtils.isNotBlank(field.getContractInfo().getExpression())
							&& !StringUtils.equalsIgnoreCase(field.getContractInfo().getExpression(), "false")){
						if(field.getContractInfo().getName().length()>32){
							LOGGER.error(" ######## ContractInfo ########  OPTION FIELD NAME TOO LONG -> " + field.getContractInfo().getName());
						} else {
							field.setContractInfo( (ContractInfo) this.getManagerBean().insertOrUpdate(field.getContractInfo()) );
						}
					} else if(field.getContractInfo().getId()!=null){
						this.getManagerBean().remove(field.getContractInfo());
					}
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("No se han podido guardar los datos correctamente.",e);
			AonUtil.addErrorMessage("No se han podido guardar los datos correctamente.");
			AonUtil.addErrorMessage(e.getMessage());
		}
	}
	
	public void onSaveDomainFields(ActionEvent event) throws ManagerBeanException{
		saveContractFields(indefiniteFieldList);
		saveContractFields(temporaryFieldList);
		saveContractFields(practiceFieldList);
		saveContractFields(learningFieldList);
	}

	public void onSaveContractModel(ActionEvent event) throws ManagerBeanException{
		IManagerBean bean = BeanManager.getManagerBean(ContractInfo.class);
		ContractController controller = (ContractController) FormUtil.getController(IPayrollConstants.CONTRACT_CONTROLLER);
		Contract contract = (Contract) controller.getTo();
		if(controller.getParams().getContractModelOption()!=null){
			try {
				if(controller.getParams().getContractModelOption()!=null){
					ContractInfo info = new ContractInfo();
					info.setContract(contract);
					info.setStartDate(contract.getStartDate());
					info.setEndDate(contract.getEndDate());
					info.setName( ContractVariable.CONTRACT_MODEL_OPTION.getValue() );
					info.setExpression("\"" + controller.getParams().getContractModelOption() + "\"");
					bean.insert(info);
				}
				loadContractFields(contract, false);
				saveContractFields(contractFieldList);
			} catch (ManagerBeanException e) {
				String msg = "Error al grabar el modelo del contrato. (" +e.getMessage() + ")";
				AonUtil.addErrorMessage(msg);
			}
		} else {
			AonUtil.addErrorMessage("Modalidad de contrato no seleccionada.");
		}
	}
	
	/*
	 * INNER CLASSES
	 */
	public static class ContractField implements Serializable {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
		
		private final String MESSAGE_PREFIX = "payroll_contract_document_";
		private ContractInfo contractInfo;
		private IContractFieldName fieldName;
		public String getName() {
			return getMessage(this.contractInfo.getName());
		}
		public ContractInfo getContractInfo() {
			return contractInfo;
		}
		public void setContractInfo(ContractInfo contractInfo) {
			this.contractInfo = contractInfo;
		}
		public IContractFieldName getFieldName() {
			return fieldName;
		}
		public void setFieldName(IContractFieldName fieldName) {
			this.fieldName = fieldName;
		}
		public String getExpression(){
			return this.contractInfo.getExpression();
		}
		public void setExpression(String expression){
			this.contractInfo.setExpression(expression);
		}
		public boolean isCheck(){
			return fieldName!=null && fieldName.isCheck();
		}
		public boolean isComposite(){
			return fieldName!=null && fieldName.getCompositeValues()!=null && fieldName.getCompositeValues().length>0;
		}
		public List<SelectItem> getCompositeList(){
			List<SelectItem> list = new LinkedList<SelectItem>();
			for(IContractFieldName field: fieldName.getCompositeValues()){
				list.add(new SelectItem(field, getMessage(field.toString())));
			}
			return list;
		}
		public String getMessage(String key){
			try {
				if(key.startsWith("I_") || key.startsWith("T_") || key.startsWith("P_") || key.startsWith("L_") ){
					return  AonUtil.getMessage(MESSAGE_PREFIX + key.substring(2, key.length()));
				}
				return  AonUtil.getMessage(MESSAGE_PREFIX + key);
			} catch (Exception e) {
				LOGGER.error(" ######## ContractInfo ########  NO MESSAGE WAS DEFINED FOR FIELD NAME -> " + key);
				return key;
			}
		}
	}
	
}
