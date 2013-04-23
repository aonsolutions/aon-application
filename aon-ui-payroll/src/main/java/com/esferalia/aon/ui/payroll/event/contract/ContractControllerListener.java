package com.esferalia.aon.ui.payroll.event.contract;


import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.person.Person;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.Agreement;
import com.esferalia.aon.payroll.CNO;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.PayrollWorkPlace;
import com.esferalia.aon.payroll.TrainingCenter;
import com.esferalia.aon.payroll.TrainingCourse;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.ContractModel;
import com.esferalia.aon.payroll.enumeration.ContractModelCode;
import com.esferalia.aon.payroll.enumeration.ContractStatus;
import com.esferalia.aon.payroll.enumeration.OccupationType;
import com.esferalia.aon.payroll.enumeration.QuoteGroup;
import com.esferalia.aon.ui.payroll.controller.EnterpriseTree;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.controller.PayrollAppParamsController;
import com.esferalia.aon.ui.payroll.controller.contract.ContractController;
import com.esferalia.aon.ui.payroll.controller.contract.ContractController.ContractParams;
import com.esferalia.aon.ui.payroll.utils.PayrollUtils;

public class ContractControllerListener extends ControllerAdapter{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ContractControllerListener.class.getName());
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		ContractController controller = (ContractController) this.getController();
		Contract contract = (Contract) controller.getTo();
		contract.setStatus(ContractStatus.PENDING);
		contract.setRegimeType(contract.getEnterpriseCCC()!=null?contract.getEnterpriseCCC().getActivity().getType():null);
		if(controller.getParams().getContractModelCode()!=null){
			contract.setModel(controller.getParams().getContractModelCode().getModel());
		}
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		ContractController controller = (ContractController) this.getController();
		Contract contract = (Contract) controller.getTo();
		contract.setRegimeType(contract.getEnterpriseCCC()!=null?contract.getEnterpriseCCC().getActivity().getType():null);
		if(controller.getParams().getContractModelCode()!=null){
			contract.setModel(controller.getParams().getContractModelCode().getModel());
		}
	}
	
	@Override
	public void beforeBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		removeChildData(event);
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		ContractController controller = (ContractController) this.getController();
		controller.setSelectedTab(null);
		controller.setEnterprise(((Contract) controller.getTo()).getWorkPlace().getEnterprise());
		controller.setWorkPlaces(null);
		controller.setActivities(null);
		controller.setEnterpriseCCCs(null);
		controller.setParams(null);
		controller.onShowVariables(null);
		try {
			loadContractData();
		} catch (ManagerBeanException e) {
			String msg = "Error loading contract data";
			LOGGER.error(msg);
		}
		searchAgreement();
	}

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		PayrollAppParamsController params = (PayrollAppParamsController) AonUtil.getRegisteredBean(IPayrollConstants.PAYROLL_APP_PARAMS_CONTROLLER_NAME);
		ContractController controller = (ContractController) this.getController();
		Contract contract = (Contract) controller.getTo();
		PayrollUtils utils = new PayrollUtils();
		controller.setEnterprise(utils.getCurrentDomainEnterprise());
		controller.setWorkPlaces(null);
		controller.setActivities(null);
		controller.setEnterpriseCCCs(null);
		controller.setParams(null);
		contract.setStartDate(new Date());
		contract.setSeniorityDate(contract.getStartDate());
		try {
			ApplicationParameter defaultContractCode = params.getParameter(PayrollAppParamsController.DEFAULT_CONTRACT_CODE);
			if(defaultContractCode!=null && defaultContractCode.getValue()!=null){
				controller.getParams().setContractModelCode(ContractModelCode.valueOf(defaultContractCode.getValue()));
			}
			IManagerBean bean = BeanManager.getManagerBean(Person.class);
			contract.setPerson((Person) bean.createNewTo());
			bean = BeanManager.getManagerBean(CNO.class);
			controller.getParams().setCno((CNO) bean.createNewTo());
			bean = BeanManager.getManagerBean(Agreement.class);
			controller.setAgreement((Agreement) bean.createNewTo());
		} catch (ManagerBeanException e) {
			String msg = "Error on afterBeanCreated";
			LOGGER.error(msg);
			throw new ControllerListenerException(msg,e);
		}
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		insertContractData();
		ContractController controller = (ContractController) this.getController();
		controller.getHandler().initializeVariables(null);
		if(controller.isShowNewContractModal()){
			controller.setShowNewContractModal(false);
			EnterpriseTree tree = (EnterpriseTree) AonUtil.getRegisteredBean(IPayrollConstants.ENTERPRISE_TREE_CONTROLLER);
			tree.loadTree();
		}
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		updateContractData();
	}
	
	@Override
	public void afterBeanCanceled(ControllerEvent event)
			throws ControllerListenerException {
		ContractController controller = (ContractController) this.getController();
		controller.setShowNewContractModal(false);
	}
	
	private void insertContractData() throws ControllerListenerException {
		ContractController controller = (ContractController) this.getController();
		Contract contract = (Contract) controller.getTo();
		IManagerBean bean;
		ContractData data;
		try {
			bean = BeanManager.getManagerBean(ContractData.class);
		} catch (ManagerBeanException e) {
			String msg = "Imposible grabar los datos de contrato. (" +e.getMessage() + ")";
			LOGGER.error(msg);
			throw new ControllerListenerException(msg,e);
		}
		try {
			if(controller.getParams().getIrpf()!=null){
				data = new ContractData();
				data.setContract(contract);
				data.setStartDate(contract.getStartDate());
				data.setEndDate(contract.getEndDate());
				data.setName( ContextVariable.IRPF_PERCENT.getName() );
				data.setExpression(controller.getParams().getIrpf().toString());
				bean.insert(data);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el porcentaje IRPF. (" +e.getMessage() + ")";
			LOGGER.error(msg);
		}
		try {
			if(controller.getParams().getQuoteGroup()!=null){
				data = new ContractData();
				data.setContract(contract);
				data.setStartDate(contract.getStartDate());
				data.setEndDate(contract.getEndDate());
				data.setName( ContextVariable.QUOTE_GROUP.getName() );
				data.setExpression("\"" + controller.getParams().getQuoteGroup().getValue() + "\"");
				bean.insert(data);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el grupo de cotizacion. (" +e.getMessage() + ")";
			LOGGER.error(msg);
		}
		try {
			if(controller.getParams().getOccupationType()!=null){
				data = new ContractData();
				data.setContract(contract);
				data.setStartDate(contract.getStartDate());
				data.setEndDate(contract.getEndDate());
				data.setName( ContextVariable.OCCUPATION.getName() );
				data.setExpression("\"" + controller.getParams().getOccupationType().getValue() + "\"");
				bean.insert(data);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar la ocupacion. (" +e.getMessage() + ")";
			LOGGER.error(msg);
		}
		try {
			if(controller.getParams().getCno()!=null && controller.getParams().getCno().getId()!=null){
				data = new ContractData();
				data.setContract(contract);
				data.setStartDate(contract.getStartDate());
				data.setEndDate(contract.getEndDate());
				data.setName( ContextVariable.CNO.getName() );
				data.setExpression("\"" + controller.getParams().getCno().getCode() + "\"");
				bean.insert(data);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el codigo nacional de ocupaciones. (" +e.getMessage() + ")";
			LOGGER.error(msg);
		}
		try {
			if(!controller.getParams().isAgreementSalaryCheck() && controller.getParams().getGrossSalary()!=null){
				data = new ContractData();
				data.setContract(contract);
				data.setStartDate(contract.getStartDate());
				data.setEndDate(contract.getEndDate());
				data.setName( "SALARIO_BRUTO"  );
				data.setExpression( String.valueOf(CommonUtil.round(controller.getParams().getGrossSalary())) );
				bean.insert(data);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el salario bruto. (" +e.getMessage() + ")";
			LOGGER.error(msg);
		}
//		try {
//			if(controller.getParams().getTc2Code()!=null){
//				data = new ContractData();
//				data.setContract(contract);
//				data.setStartDate(contract.getStartDate());
//				data.setEndDate(contract.getEndDate());
//				data.setName( ContextVariable.TC2.getName() );
//				data.setExpression("\"" + controller.getParams().getTc2Code().getValue() + "\"");
//				bean.insert(data);
//			}
//		} catch (ManagerBeanException e) {
//			String msg = "Error al grabar el codigo TC2. (" +e.getMessage() + ")";
//			LOGGER.error(msg);
//		}
		try {
			if(controller.getParams().getContractModelCode()!=null){
				data = new ContractData();
				data.setContract(contract);
				data.setStartDate(contract.getStartDate());
				data.setEndDate(contract.getEndDate());
				data.setName( ContextVariable.TC2.getName() );
				data.setExpression("\"" + controller.getParams().getContractModelCode().getCode().getValue() + "\"");
				bean.insert(data);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el codigo TC2. (" +e.getMessage() + ")";
			LOGGER.error(msg);
		}
		try {
			if(controller.getParams().getSubsidized()!=null){
				data = new ContractData();
				data.setContract(contract);
				data.setStartDate(contract.getStartDate());
				data.setEndDate(contract.getEndDate());
				data.setName( ContextVariable.SUBSIDIZED.getName() );
				data.setExpression(controller.getParams().getSubsidized()?"true":"false");
				bean.insert(data);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar si el contrato se acoge a la reduccion de cuotas a la S.S. (" +e.getMessage() + ")";
			LOGGER.error(msg);
		}
		try {
			if(controller.getParams().getTrainingCenter()!=null && controller.getParams().getTrainingCenter().getId()!=null){
				data = new ContractData();
				data.setContract(contract);
				data.setStartDate(contract.getStartDate());
				data.setEndDate(contract.getEndDate());
				data.setName( ContextVariable.TRAINING_CENTER.getName() );
				data.setExpression("\"" + controller.getParams().getTrainingCenter().getId() + "\"");
				bean.insert(data);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el centro de formacion. (" +e.getMessage() + ")";
			LOGGER.error(msg);
		}
		try {
			if(controller.getParams().getTrainingCourse()!=null && controller.getParams().getTrainingCourse().getId()!=null){
				data = new ContractData();
				data.setContract(contract);
				data.setStartDate(contract.getStartDate());
				data.setEndDate(contract.getEndDate());
				data.setName( ContextVariable.TRAINING_COURSE.getName() );
				data.setExpression("\"" + controller.getParams().getTrainingCourse().getId() + "\"");
				bean.insert(data);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el curso de formacion. (" +e.getMessage() + ")";
			LOGGER.error(msg);
		}
	}
	private void updateContractData() throws ControllerListenerException {
		ContractController controller = (ContractController) this.getController();
		Contract contract = (Contract) controller.getTo();
		IManagerBean bean;
		ContractData data;
		try {
			bean = BeanManager.getManagerBean(ContractData.class);
		} catch (ManagerBeanException e) {
			String msg = "Imposible actualizar los datos de contrato. (" +e.getMessage() + ")";
			LOGGER.error(msg);
			throw new ControllerListenerException(msg,e);
		}
		try {
			ContractData irpfData = obtainContractData(ContextVariable.IRPF_PERCENT.getName());
			if(controller.getParams().getIrpf()!=null){
				data = irpfData!=null?irpfData:new ContractData();
				data.setContract(contract);
				data.setStartDate(contract.getStartDate());
				data.setEndDate(contract.getEndDate());
				data.setName( ContextVariable.IRPF_PERCENT.getName() );
				data.setExpression(controller.getParams().getIrpf().toString());
				bean.insertOrUpdate(data);
			} else {
				if(irpfData != null){
					bean.remove(irpfData);
				}
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el porcentaje IRPF. (" +e.getMessage() + ")";
			LOGGER.error(msg);
		}
		try {
			ContractData quoteGroupData = obtainContractData(ContextVariable.QUOTE_GROUP.getName());
			if(controller.getParams().getQuoteGroup()!=null){
				data = quoteGroupData!=null?quoteGroupData:new ContractData();
				data.setContract(contract);
				data.setStartDate(contract.getStartDate());
				data.setEndDate(contract.getEndDate());
				data.setName( ContextVariable.QUOTE_GROUP.getName() );
				data.setExpression("\"" + controller.getParams().getQuoteGroup().getValue() + "\"");
				bean.insertOrUpdate(data);
			} else {
				if(quoteGroupData != null){
					bean.remove(quoteGroupData);
				}
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el grupo de cotizacion. (" +e.getMessage() + ")";
			LOGGER.error(msg);
		}
		try {
			ContractData occupationData = obtainContractData(ContextVariable.OCCUPATION.getName());
			if(controller.getParams().getOccupationType()!=null){
				data = occupationData!=null?occupationData:new ContractData();
				data.setContract(contract);
				data.setStartDate(contract.getStartDate());
				data.setEndDate(contract.getEndDate());
				data.setName( ContextVariable.OCCUPATION.getName() );
				data.setExpression("\"" + controller.getParams().getOccupationType().getValue() + "\"");
				bean.insertOrUpdate(data);
			} else {
				if(occupationData != null){
					bean.remove(occupationData);
				}
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar la ocupacion. (" +e.getMessage() + ")";
			LOGGER.error(msg);
		}
		try {
			ContractData cnoData = obtainContractData(ContextVariable.CNO.getName());
			if(controller.getParams().getCno()!=null && controller.getParams().getCno().getId()!=null){
				data = cnoData!=null?cnoData:new ContractData();
				data.setContract(contract);
				data.setStartDate(contract.getStartDate());
				data.setEndDate(contract.getEndDate());
				data.setName( ContextVariable.CNO.getName() );
				data.setExpression("\"" + controller.getParams().getCno().getCode() + "\"");
				bean.insertOrUpdate(data);
			} else {
				if(cnoData != null){
					bean.remove(cnoData);
				}
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el codigo nacional de ocupaciones. (" +e.getMessage() + ")";
			LOGGER.error(msg);
		}
		try {
			ContractData tc2Data = obtainContractData(ContextVariable.TC2.getName());
			if(controller.getParams().getContractModelCode()!=null){
				data = tc2Data!=null?tc2Data:new ContractData();
				data.setContract(contract);
				data.setStartDate(contract.getStartDate());
				data.setEndDate(contract.getEndDate());
				data.setName( ContextVariable.TC2.getName() );
				data.setExpression("\"" + controller.getParams().getContractModelCode().getCode().getValue() + "\"");
				bean.insertOrUpdate(data);
			} else {
				if(tc2Data != null){
					bean.remove(tc2Data);
				}
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el codigo TC2. (" +e.getMessage() + ")";
			LOGGER.error(msg);
		}
		try {
			ContractData subsidizedData = obtainContractData(ContextVariable.SUBSIDIZED.getName());
			if(controller.getParams().getSubsidized()!=null){
				data = subsidizedData!=null?subsidizedData:new ContractData();
				data.setContract(contract);
				data.setStartDate(contract.getStartDate());
				data.setEndDate(contract.getEndDate());
				data.setName( ContextVariable.SUBSIDIZED.getName() );
				data.setExpression(controller.getParams().getSubsidized()?"true":"false");
				bean.insertOrUpdate(data);
			} else {
				if(subsidizedData != null){
					bean.remove(subsidizedData);
				}
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar si el contrato se acoge a la reduccion de cuotas a la S.S. (" +e.getMessage() + ")";
			LOGGER.error(msg);
		}
		try {
			ContractData trainingCenterData = obtainContractData(ContextVariable.TRAINING_CENTER.getName());
			if(controller.getParams().getTrainingCenter()!=null && controller.getParams().getTrainingCenter().getId()!=null){
				data = trainingCenterData!=null?trainingCenterData:new ContractData();
				data.setContract(contract);
				data.setStartDate(contract.getStartDate());
				data.setEndDate(contract.getEndDate());
				data.setName( ContextVariable.TRAINING_CENTER.getName() );
				data.setExpression("\"" + controller.getParams().getTrainingCenter().getId() + "\"");
				bean.insertOrUpdate(data);
			} else {
				if(trainingCenterData != null){
					bean.remove(trainingCenterData);
				}
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el centro de formacion. (" +e.getMessage() + ")";
			LOGGER.error(msg);
		}
		try {
			ContractData trainingCourseData = obtainContractData(ContextVariable.TRAINING_COURSE.getName());
			if(controller.getParams().getTrainingCourse()!=null && controller.getParams().getTrainingCourse().getId()!=null){
				data = trainingCourseData!=null?trainingCourseData:new ContractData();
				data.setContract(contract);
				data.setStartDate(contract.getStartDate());
				data.setEndDate(contract.getEndDate());
				data.setName( ContextVariable.TRAINING_COURSE.getName() );
				data.setExpression("\"" + controller.getParams().getTrainingCourse().getId() + "\"");
				bean.insertOrUpdate(data);
			} else {
				if(trainingCourseData != null){
					bean.remove(trainingCourseData);
				}
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el curso de formacion. (" +e.getMessage() + ")";
			LOGGER.error(msg);
		}
	}
	
	private ContractData obtainContractData(String name) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_CONTRACT_ID), ((Contract)this.getController().getTo()).getId() );
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_NAME), name );
			List<ITransferObject> list = bean.getList(criteria);
			if( !list.isEmpty() ){
				return (ContractData) list.get(0);
			}
		} catch (ManagerBeanException e) {
			// do nothing ...
		}
		return null;
	}

	private void removeChildData(ControllerEvent event) throws ControllerListenerException {
		ContractController controller = (ContractController) event.getController();
		Contract contract = (Contract) controller.getTo();
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_CONTRACT_ID), contract.getId());
			for(ITransferObject to: bean.getList(criteria)){
				ContractData data = (ContractData) to;
				bean.remove(data);
				
			}
		} catch (ManagerBeanException e) {
			String msg = "Imposible eliminar los datos de contrato. (" +e.getMessage() + ")";
			LOGGER.error(msg);
			throw new ControllerListenerException(msg,e);
		}
	}
	
	private void searchAgreement() {
		try {
			ContractController controller = (ContractController) this.getController();
			Contract contract = (Contract) controller.getTo();
			if(contract.getAgreementLevelCategory()!=null 
					&& contract.getAgreementLevelCategory().getLevel()!=null 
					&& contract.getAgreementLevelCategory().getLevel().getAgreement()!=null 
					&& contract.getAgreementLevelCategory().getLevel().getAgreement().getId()!=null){
				controller.setAgreement(contract.getAgreementLevelCategory().getLevel().getAgreement());
			} else {
				IManagerBean bean = BeanManager.getManagerBean(PayrollWorkPlace.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PAYROLL_WORK_PLACE_WORK_PLACE_ID), contract.getWorkPlace().getId());
				List<ITransferObject> list = bean.getList(criteria);
				Agreement agreement = null;
				if(!list.isEmpty()){
					agreement = (list.get(0)==null)?null:((PayrollWorkPlace)list.get(0)).getAgreement();
				}
				if(agreement!=null){
					controller.setAgreement(agreement);
				} else {
					IManagerBean aBean = BeanManager.getManagerBean(Agreement.class);
					controller.setAgreement((Agreement) aBean.createNewTo());
				}
			}
		} catch (ManagerBeanException e) {
			// NADA, no se define ningun convenio
			String msg = "Error al buscar el convenio. (" +e.getMessage() + ")";
			LOGGER.error(msg);
		}
	}
	
	private void loadContractData() throws ManagerBeanException {
		ContractParams params = ((ContractController)this.getController()).getParams();
		PayrollUtils utils = new PayrollUtils();
		Map<String, String> map = utils.getContractDataMap((Contract) this.getController().getTo());
		
		
		
		if(map.get(ContextVariable.IRPF_PERCENT.getName())!=null){
			params.setIrpf(Double.parseDouble(map.get(ContextVariable.IRPF_PERCENT.getName())));
		}
		if(map.get(ContextVariable.QUOTE_GROUP.getName())!=null){
			params.setQuoteGroup(QuoteGroup.getQuoteGroupByValue(map.get(ContextVariable.QUOTE_GROUP.getName())));
		}
		if(map.get(ContextVariable.OCCUPATION.getName())!=null){
			params.setOccupationType(OccupationType.getOccupationTypeByValue(map.get(ContextVariable.OCCUPATION.getName())));
		}
		if(map.get(ContextVariable.CNO.getName())!=null){
			params.setCno(obtainCno(map.get(ContextVariable.CNO.getName())));
		} else {
			params.setCno((CNO) BeanManager.getManagerBean(CNO.class).createNewTo());
		}
		if(map.get(ContextVariable.TC2.getName())!=null){
			Contract contract = (Contract) this.getController().getTo();
			params.setContractModelCode( obtainContractModelCode(map.get(ContextVariable.TC2.getName()), contract.getModel()) );
		}
		if(map.get(ContextVariable.SUBSIDIZED.getName())!=null){
			params.setSubsidized(new Boolean(map.get(ContextVariable.SUBSIDIZED.getName())));
		}
		if(map.get(ContextVariable.TRAINING_CENTER.getName())!=null){
			params.setTrainingCenter(obtainTrainingCenter(map.get(ContextVariable.TRAINING_CENTER.getName())));
		}
		if(map.get(ContextVariable.TRAINING_COURSE.getName())!=null){
			params.setTrainingCourse(obtainTrainingCourse(map.get(ContextVariable.TRAINING_COURSE.getName())));
		}
		
	}
	
	private CNO obtainCno(String expression) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(CNO.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CNO_CODE), expression);
			List<ITransferObject> list = bean.getList(criteria);
			if( !list.isEmpty() ){
				return (CNO) list.get(0);
			} else {
				return (CNO) bean.createNewTo();
			}
		} catch (ManagerBeanException e) {
			// do nothing ...
		}
		return null;
	}
	public ContractModelCode obtainContractModelCode(String contractCode, ContractModel contractModel) {
		ContractCode code = ContractCode.getContractCodeByValue(contractCode);
		for( ContractModelCode o : ContractModelCode.values() ) {
			if ( (contractModel==null || o.getModel() == contractModel) && o.getCode() == code ) {
				return o;
			}
		}
		return null;
	}
	private TrainingCenter obtainTrainingCenter(String expression) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(TrainingCenter.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.TRAINING_CENTER_ID), Integer.parseInt(expression) );
			List<ITransferObject> list = bean.getList(criteria);
			if( !list.isEmpty() ){
				return (TrainingCenter) list.get(0);
			} else {
				return (TrainingCenter) bean.createNewTo();
			}
		} catch (ManagerBeanException e) {
			// do nothing ...
		}
		return null;
	}
	private TrainingCourse obtainTrainingCourse(String expression) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(TrainingCourse.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.TRAINING_COURSE_ID), Integer.parseInt(expression) );
			List<ITransferObject> list = bean.getList(criteria);
			if( !list.isEmpty() ){
				return (TrainingCourse) list.get(0);
			} else {
				return (TrainingCourse) bean.createNewTo();
			}
		} catch (ManagerBeanException e) {
			// do nothing ...
		}
		return null;
	}
	
}
