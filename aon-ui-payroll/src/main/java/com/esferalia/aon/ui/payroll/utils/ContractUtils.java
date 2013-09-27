package com.esferalia.aon.ui.payroll.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.event.AbortProcessingException;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.Agreement;
import com.esferalia.aon.payroll.CNO;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractBonus;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.PayrollWorkPlace;
import com.esferalia.aon.payroll.TrainingCenter;
import com.esferalia.aon.payroll.TrainingCourse;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.OccupationType;
import com.esferalia.aon.payroll.enumeration.QuoteGroup;
import com.esferalia.aon.ui.payroll.controller.contract.ContractController.ContractParams;


public class ContractUtils {
	
	private static ContractUtils instance;
	
	private ContractUtils(){

	}
	
	public static ContractUtils getInstance(){
		if(instance == null){
			instance = new ContractUtils();
		}
		return instance;
	}
	
	
	public void insertContractData(Contract contract, ContractParams params) {
		IManagerBean bean;
		ContractData data;
		try {
			bean = BeanManager.getManagerBean(ContractData.class);
		} catch (ManagerBeanException e) {
			String msg = "Imposible grabar los datos de contrato. (" +e.getMessage() + ")";
			throw new AbortProcessingException(msg,e);
		}
		try {
			if(params.getIrpf()!=null){
				data = new ContractData();
				data.setContract(contract);
				data.setStartDate(contract.getStartDate());
				data.setEndDate(contract.getEndDate());
				data.setName( ContextVariable.IRPF_PERCENT.getName() );
				data.setExpression(params.getIrpf().toString());
				bean.insert(data);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el porcentaje IRPF. (" +e.getMessage() + ")";
			AonUtil.addErrorMessage(msg);
		}
		try {
			if(params.getQuoteGroup()!=null){
				data = new ContractData();
				data.setContract(contract);
				data.setStartDate(contract.getStartDate());
				data.setEndDate(contract.getEndDate());
				data.setName( ContextVariable.QUOTE_GROUP.getName() );
				data.setExpression("\"" + params.getQuoteGroup().getValue() + "\"");
				bean.insert(data);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el grupo de cotizacion. (" +e.getMessage() + ")";
			AonUtil.addErrorMessage(msg);
		}
		try {
			if(params.getOccupationType()!=null){
				data = new ContractData();
				data.setContract(contract);
				data.setStartDate(contract.getStartDate());
				data.setEndDate(contract.getEndDate());
				data.setName( ContextVariable.OCCUPATION.getName() );
				data.setExpression("\"" + params.getOccupationType().getValue() + "\"");
				bean.insert(data);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar la ocupacion. (" +e.getMessage() + ")";
			AonUtil.addErrorMessage(msg);
		}
		try {
			if(params.getCno()!=null && params.getCno().getId()!=null){
				data = new ContractData();
				data.setContract(contract);
				data.setStartDate(contract.getStartDate());
				data.setEndDate(contract.getEndDate());
				data.setName( ContextVariable.CNO.getName() );
				data.setExpression("\"" + params.getCno().getCode() + "\"");
				bean.insert(data);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el codigo nacional de ocupaciones. (" +e.getMessage() + ")";
			AonUtil.addErrorMessage(msg);
		}
		try {
			if(!params.isAgreementSalaryCheck() && params.getGrossSalary()!=null){
				data = new ContractData();
				data.setContract(contract);
				data.setStartDate(contract.getStartDate());
				data.setEndDate(contract.getEndDate());
				data.setName( "SALARIO_BRUTO"  );
				data.setExpression( String.valueOf(CommonUtil.round(params.getGrossSalary())) );
				bean.insert(data);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el salario bruto. (" +e.getMessage() + ")";
			AonUtil.addErrorMessage(msg);
		}
//		try {
//			if(params.getContractModelCode()!=null){
//				data = new ContractData();
//				data.setContract(contract);
//				data.setStartDate(contract.getStartDate());
//				data.setEndDate(contract.getEndDate());
//				data.setName( ContextVariable.TC2.getName() );
//				data.setExpression("\"" + params.getContractModelCode().getCode().getValue() + "\"");
//				bean.insert(data);
//			}
//		} catch (ManagerBeanException e) {
//			String msg = "Error al grabar el codigo TC2. (" +e.getMessage() + ")";
//			AonUtil.addErrorMessage(msg);
//		}
		try {
			if(params.getContractCode()!=null){
				data = new ContractData();
				data.setContract(contract);
				data.setStartDate(contract.getStartDate());
				data.setEndDate(contract.getEndDate());
				data.setName( ContextVariable.TC2.getName() );
				data.setExpression("\"" + params.getContractCode().getValue() + "\"");
				bean.insert(data);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el codigo TC2. (" +e.getMessage() + ")";
			AonUtil.addErrorMessage(msg);
		}
		try {
			if(params.getContractTransformCode()!=null){
				data = new ContractData();
				data.setContract(contract);
				data.setStartDate(contract.getStartDate());
				data.setEndDate(contract.getEndDate());
				data.setName( ContextVariable.TC2.getName() );
				data.setExpression("\"" + params.getContractTransformCode().getValue() + "\"");
				bean.insert(data);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el codigo TC2. (" +e.getMessage() + ")";
			AonUtil.addErrorMessage(msg);
		}
		try {
			if(params.getSubsidized()!=null){
				data = new ContractData();
				data.setContract(contract);
				data.setStartDate(contract.getStartDate());
				data.setEndDate(contract.getEndDate());
				data.setName( ContextVariable.SUBSIDIZED.getName() );
				data.setExpression(params.getSubsidized()?"true":"false");
				bean.insert(data);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar si el contrato se acoge a la reduccion de cuotas a la S.S. (" +e.getMessage() + ")";
			AonUtil.addErrorMessage(msg);
		}
		try {
			if(params.getTrainingCenter()!=null && params.getTrainingCenter().getId()!=null){
				data = new ContractData();
				data.setContract(contract);
				data.setStartDate(contract.getStartDate());
				data.setEndDate(contract.getEndDate());
				data.setName( ContextVariable.TRAINING_CENTER.getName() );
				data.setExpression("\"" + params.getTrainingCenter().getId() + "\"");
				bean.insert(data);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el centro de formacion. (" +e.getMessage() + ")";
			AonUtil.addErrorMessage(msg);
		}
		try {
			if(params.getTrainingCourse()!=null && params.getTrainingCourse().getId()!=null){
				data = new ContractData();
				data.setContract(contract);
				data.setStartDate(params.getTrainingStartDate());
				data.setEndDate(params.getTrainingEndDate());
				data.setName( ContextVariable.TRAINING_COURSE.getName() );
				data.setExpression("\"" + params.getTrainingCourse().getId() + "\"");
				bean.insert(data);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el curso de formacion. (" +e.getMessage() + ")";
			AonUtil.addErrorMessage(msg);
		}
		try {
			if(StringUtils.isNotBlank(params.getWorkSchedule())){
				data = new ContractData();
				data.setContract(contract);
				data.setStartDate(contract.getStartDate());
				data.setEndDate(contract.getEndDate());
				data.setName( ContextVariable.WORK_SCHEDULE.getName() );
				data.setExpression("\"" + params.getWorkSchedule() + "\"");
				bean.insert(data);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el horario laboral. (" +e.getMessage() + ")";
			AonUtil.addErrorMessage(msg);
		}
		try {
			if(StringUtils.isNotBlank(params.getTrainingSchedule())){
				data = new ContractData();
				data.setContract(contract);
				data.setStartDate(contract.getStartDate());
				data.setEndDate(contract.getEndDate());
				data.setName( ContextVariable.TRAINING_SCHEDULE.getName() );
				data.setExpression("\"" + params.getTrainingSchedule() + "\"");
				bean.insert(data);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el horario lectivo. (" +e.getMessage() + ")";
			AonUtil.addErrorMessage(msg);
		}
	}
	
	public void updateContractData(Contract contract, ContractParams params) throws ControllerListenerException {
		IManagerBean bean;
		ContractData data;
		try {
			bean = BeanManager.getManagerBean(ContractData.class);
		} catch (ManagerBeanException e) {
			String msg = "Imposible actualizar los datos de contrato. (" +e.getMessage() + ")";
			AonUtil.addErrorMessage(msg);
			throw new ControllerListenerException(msg,e);
		}
		try {
			ContractData irpfData = obtainContractData(contract, ContextVariable.IRPF_PERCENT.getName());
			if(params.getIrpf()!=null){
				data = irpfData!=null?irpfData:new ContractData();
				data.setContract(contract);
				data.setStartDate(contract.getStartDate());
				data.setEndDate(contract.getEndDate());
				data.setName( ContextVariable.IRPF_PERCENT.getName() );
				data.setExpression(params.getIrpf().toString());
				bean.insertOrUpdate(data);
			} else {
				if(irpfData != null){
					bean.remove(irpfData);
				}
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el porcentaje IRPF. (" +e.getMessage() + ")";
			AonUtil.addErrorMessage(msg);
		}
		try {
			ContractData quoteGroupData = obtainContractData(contract, ContextVariable.QUOTE_GROUP.getName());
			if(params.getQuoteGroup()!=null){
				data = quoteGroupData!=null?quoteGroupData:new ContractData();
				data.setContract(contract);
				data.setStartDate(contract.getStartDate());
				data.setEndDate(contract.getEndDate());
				data.setName( ContextVariable.QUOTE_GROUP.getName() );
				data.setExpression("\"" + params.getQuoteGroup().getValue() + "\"");
				bean.insertOrUpdate(data);
			} else {
				if(quoteGroupData != null){
					bean.remove(quoteGroupData);
				}
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el grupo de cotizacion. (" +e.getMessage() + ")";
			AonUtil.addErrorMessage(msg);
		}
		try {
			ContractData occupationData = obtainContractData(contract, ContextVariable.OCCUPATION.getName());
			if(params.getOccupationType()!=null){
				data = occupationData!=null?occupationData:new ContractData();
				data.setContract(contract);
				data.setStartDate(contract.getStartDate());
				data.setEndDate(contract.getEndDate());
				data.setName( ContextVariable.OCCUPATION.getName() );
				data.setExpression("\"" + params.getOccupationType().getValue() + "\"");
				bean.insertOrUpdate(data);
			} else {
				if(occupationData != null){
					bean.remove(occupationData);
				}
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar la ocupacion. (" +e.getMessage() + ")";
			AonUtil.addErrorMessage(msg);
		}
		try {
			ContractData cnoData = obtainContractData(contract, ContextVariable.CNO.getName());
			if(params.getCno()!=null && params.getCno().getId()!=null){
				data = cnoData!=null?cnoData:new ContractData();
				data.setContract(contract);
				data.setStartDate(contract.getStartDate());
				data.setEndDate(contract.getEndDate());
				data.setName( ContextVariable.CNO.getName() );
				data.setExpression("\"" + params.getCno().getCode() + "\"");
				bean.insertOrUpdate(data);
			} else {
				if(cnoData != null){
					bean.remove(cnoData);
				}
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el codigo nacional de ocupaciones. (" +e.getMessage() + ")";
			AonUtil.addErrorMessage(msg);
		}
//		try {
//			ContractData tc2Data = obtainContractData(contract, ContextVariable.TC2.getName());
//			if(params.getContractModelCode()!=null){
//				data = tc2Data!=null?tc2Data:new ContractData();
//				data.setContract(contract);
//				data.setStartDate(contract.getStartDate());
//				data.setEndDate(contract.getEndDate());
//				data.setName( ContextVariable.TC2.getName() );
//				data.setExpression("\"" + params.getContractModelCode().getCode().getValue() + "\"");
//				bean.insertOrUpdate(data);
//			} else {
//				if(tc2Data != null){
//					bean.remove(tc2Data);
//				}
//			}
//		} catch (ManagerBeanException e) {
//			String msg = "Error al grabar el codigo TC2. (" +e.getMessage() + ")";
//			AonUtil.addErrorMessage(msg);
//		}
		try {
			ContractData tc2Data = obtainContractData(contract, ContextVariable.TC2.getName());
			if(params.getContractCode()!=null){
				data = tc2Data!=null?tc2Data:new ContractData();
				data.setContract(contract);
				data.setStartDate(contract.getStartDate());
				data.setEndDate(contract.getEndDate());
				data.setName( ContextVariable.TC2.getName() );
				data.setExpression("\"" + params.getContractCode().getValue() + "\"");
				bean.insertOrUpdate(data);
			} else {
				if(tc2Data != null){
					bean.remove(tc2Data);
				}
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el codigo TC2. (" +e.getMessage() + ")";
			AonUtil.addErrorMessage(msg);
		}
		// TODO: updates availabla form contract transformations?
//		try {
//			ContractData tc2Data = obtainContractData(contract, ContextVariable.TC2.getName());
//			if(params.getContractTransformCode()!=null){
//				data = tc2Data!=null?tc2Data:new ContractData();
//				data.setContract(contract);
//				data.setStartDate(contract.getStartDate());
//				data.setEndDate(contract.getEndDate());
//				data.setName( ContextVariable.TC2.getName() );
//				data.setExpression("\"" + params.getContractTransformCode().getValue() + "\"");
//				bean.insertOrUpdate(data);
//			} else {
//				if(tc2Data != null){
//					bean.remove(tc2Data);
//				}
//			}
//		} catch (ManagerBeanException e) {
//			String msg = "Error al grabar el codigo TC2. (" +e.getMessage() + ")";
//			AonUtil.addErrorMessage(msg);
//		}
		try {
			ContractData subsidizedData = obtainContractData(contract, ContextVariable.SUBSIDIZED.getName());
			if(params.getSubsidized()!=null){
				data = subsidizedData!=null?subsidizedData:new ContractData();
				data.setContract(contract);
				data.setStartDate(contract.getStartDate());
				data.setEndDate(contract.getEndDate());
				data.setName( ContextVariable.SUBSIDIZED.getName() );
				data.setExpression(params.getSubsidized()?"true":"false");
				bean.insertOrUpdate(data);
			} else {
				if(subsidizedData != null){
					bean.remove(subsidizedData);
				}
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar si el contrato se acoge a la reduccion de cuotas a la S.S. (" +e.getMessage() + ")";
			AonUtil.addErrorMessage(msg);
		}
		try {
			ContractData trainingCenterData = obtainContractData(contract, ContextVariable.TRAINING_CENTER.getName());
			if(params.getTrainingCenter()!=null && params.getTrainingCenter().getId()!=null){
				data = trainingCenterData!=null?trainingCenterData:new ContractData();
				data.setContract(contract);
				data.setStartDate(contract.getStartDate());
				data.setEndDate(contract.getEndDate());
				data.setName( ContextVariable.TRAINING_CENTER.getName() );
				data.setExpression("\"" + params.getTrainingCenter().getId() + "\"");
				bean.insertOrUpdate(data);
			} else {
				if(trainingCenterData != null){
					bean.remove(trainingCenterData);
				}
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el centro de formacion. (" +e.getMessage() + ")";
			AonUtil.addErrorMessage(msg);
		}
		try {
			ContractData trainingCourseData = obtainContractData(contract, ContextVariable.TRAINING_COURSE.getName());
			if(params.getTrainingCourse()!=null && params.getTrainingCourse().getId()!=null){
				data = trainingCourseData!=null?trainingCourseData:new ContractData();
				data.setContract(contract);
				data.setStartDate(params.getTrainingStartDate());
				data.setEndDate(params.getTrainingEndDate());
				data.setName( ContextVariable.TRAINING_COURSE.getName() );
				data.setExpression("\"" + params.getTrainingCourse().getId() + "\"");
				bean.insertOrUpdate(data);
			} else {
				if(trainingCourseData != null){
					bean.remove(trainingCourseData);
				}
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el curso de formacion. (" +e.getMessage() + ")";
			AonUtil.addErrorMessage(msg);
		}
		try {
			ContractData workScheduleData = obtainContractData(contract, ContextVariable.WORK_SCHEDULE.getName());
			if(StringUtils.isNotBlank(params.getWorkSchedule())){
				data = workScheduleData!=null?workScheduleData:new ContractData();
				data.setContract(contract);
				data.setStartDate(contract.getStartDate());
				data.setEndDate(contract.getEndDate());
				data.setName( ContextVariable.WORK_SCHEDULE.getName() );
				data.setExpression("\"" + params.getWorkSchedule() + "\"");
				bean.insertOrUpdate(data);
			} else {
				if(workScheduleData != null){
					bean.remove(workScheduleData);
				}
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el horario laboral. (" +e.getMessage() + ")";
			AonUtil.addErrorMessage(msg);
		}
		try {
			ContractData trainingScheduleData = obtainContractData(contract, ContextVariable.TRAINING_SCHEDULE.getName());
			if(StringUtils.isNotBlank(params.getTrainingSchedule())){
				data = trainingScheduleData!=null?trainingScheduleData:new ContractData();
				data.setContract(contract);
				data.setStartDate(contract.getStartDate());
				data.setEndDate(contract.getEndDate());
				data.setName( ContextVariable.TRAINING_SCHEDULE.getName() );
				data.setExpression("\"" + params.getTrainingSchedule() + "\"");
				bean.insertOrUpdate(data);
			} else {
				if(trainingScheduleData != null){
					bean.remove(trainingScheduleData);
				}
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el horario lectivo. (" +e.getMessage() + ")";
			AonUtil.addErrorMessage(msg);
		}
	}
	
	public void loadContractData(Contract contract, ContractParams params) throws ManagerBeanException {
		Map<String, String> map = getContractDataMap(contract);
		
		
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
//			params.setContractModelCode( obtainContractModelCode(map.get(ContextVariable.TC2.getName()), contract.getModel()) );
			params.setContractCode( ContractCode.getContractCodeByValue(map.get(ContextVariable.TC2.getName())) );
		}
		if(map.get(ContextVariable.TC2.getName())!=null){
//			params.setContractTransformCode( ContractCode.getContractCodeByValue(map.get(ContextVariable.TC2.getName())) );
		}
		if(map.get(ContextVariable.SUBSIDIZED.getName())!=null){
			params.setSubsidized(new Boolean(map.get(ContextVariable.SUBSIDIZED.getName())));
		}
		if(isTrainingContract(contract)){
			if(map.get(ContextVariable.TRAINING_CENTER.getName())!=null){
				params.setTrainingCenter(obtainTrainingCenter(map.get(ContextVariable.TRAINING_CENTER.getName())));
			} else {
				params.setTrainingCenter((TrainingCenter) BeanManager.getManagerBean(TrainingCenter.class).createNewTo());
			}
			if(map.get(ContextVariable.TRAINING_COURSE.getName())!=null){
				params.setTrainingCourse(obtainTrainingCourse(map.get(ContextVariable.TRAINING_COURSE.getName())));
				ContractData data = obtainContractData(contract, ContextVariable.TRAINING_COURSE.getName());
				params.setTrainingStartDate(data.getStartDate());
				params.setTrainingEndDate(data.getEndDate());
			} else {
				params.setTrainingCourse((TrainingCourse) BeanManager.getManagerBean(TrainingCourse.class).createNewTo());
			}
			if(map.get(ContextVariable.WORK_SCHEDULE.getName())!=null){
				params.setWorkSchedule(map.get(ContextVariable.WORK_SCHEDULE.getName()));
			}
			if(map.get(ContextVariable.TRAINING_SCHEDULE.getName())!=null){
				params.setTrainingSchedule(map.get(ContextVariable.TRAINING_SCHEDULE.getName()));
			}
		}
		
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractBonus.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_BONUS_CONTRACT_ID), contract.getId());
			criteria.addOrder(bean.getFieldName(IEntityAlias.CONTRACT_BONUS_CONTRACT_ID));
			List<ITransferObject> list = bean.getList(criteria);
			if(!list.isEmpty()){
				params.setBonus( (ContractBonus) list.get(0) ); 
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al cargar la bonificacion del contrato";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
		
	}
	
	public void removeContractData(Contract contract, ContractParams params) throws ControllerListenerException {
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_CONTRACT_ID), contract.getId());
			for(ITransferObject to: bean.getList(criteria)){
				ContractData data = (ContractData) to;
				bean.remove(data);
				
			}
			removeContractBonus(params.getBonus());
		} catch (ManagerBeanException e) {
			String msg = "Imposible eliminar los datos de contrato. (" +e.getMessage() + ")";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}
		
	public void removeContractBonus(ContractBonus bonus) {
		try {
			if(bonus!=null && bonus.getId()!=null){
				IManagerBean bean = BeanManager.getManagerBean(ContractBonus.class);
				bean.remove(bonus);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al borrar los datos de la bonificacion";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}
	
	public Agreement obtainAgreement(Contract contract) {
		try {
			if(contract.getAgreementLevelCategory()!=null 
					&& contract.getAgreementLevelCategory().getLevel()!=null 
					&& contract.getAgreementLevelCategory().getLevel().getAgreement()!=null 
					&& contract.getAgreementLevelCategory().getLevel().getAgreement().getId()!=null){
				return contract.getAgreementLevelCategory().getLevel().getAgreement();
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
					return agreement;
				} else {
					IManagerBean aBean = BeanManager.getManagerBean(Agreement.class);
					return (Agreement) aBean.createNewTo();
				}
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al buscar el convenio. (" +e.getMessage() + ")";
			AonUtil.addErrorMessage(msg);
			return null;
		}
	}
	
	private ContractData obtainContractData(Contract contract, String name) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_CONTRACT_ID), contract.getId() );
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
	public TrainingCenter obtainTrainingCenter(String expression) {
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
	public TrainingCourse obtainTrainingCourse(String expression) {
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
	
	public boolean isTrainingContract(Contract contract){
		Map<String, String> map = getContractDataMap(contract);
		return map.get(ContextVariable.TC2.getName())!=null && ContractCode.getContractCodeByValue(map.get(ContextVariable.TC2.getName()))==ContractCode.C421;
	}
	
	public Map<String, String> getContractDataMap(Contract contract) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_CONTRACT_ID), contract.getId());
			criteria.addGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_START_DATE), contract.getStartDate());
			Expression endDateExp = ExpressionUtilities.getNullExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_END_DATE));
			if(contract.getEndDate()!=null){
//				criteria.addLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_END_DATE), contract.getEndDate());
				Expression exp = ExpressionUtilities.getLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_END_DATE), contract.getEndDate());
				endDateExp = ExpressionUtilities.getOrExpression(exp, endDateExp);
			} else {
//				criteria.addNullExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_END_DATE));
				criteria.addExpression(endDateExp);
			}
			for(ITransferObject to: bean.getList(criteria)){
				ContractData data = (ContractData) to;
				if( StringUtils.isNotEmpty(data.getName()) && StringUtils.isNotEmpty(data.getExpression()) ){
					map.put(data.getName(), data.getExpression().replace('"', ' ').trim());
				}
			}
		} catch (ManagerBeanException e) {
			// NADA, se devuelve un mapa vacio
			return map;
		}
		return map;
	}
	
	public Map<String, ContractData> getContractDataMap(Contract contract, Date startDate, Date endDate) {
		Map<String, ContractData> map = new HashMap<String, ContractData>();
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_CONTRACT_ID), contract.getId());
			if(startDate!=null){
				criteria.addGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_START_DATE), startDate);
			}
			if(endDate!=null){
				criteria.addLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_END_DATE), endDate);
			}
			for(ITransferObject to: bean.getList(criteria)){
				ContractData data = (ContractData) to;
				if(data.getExpression()!=null){
					map.put(data.getName(), data);
				}
			}
		} catch (ManagerBeanException e) {
			// NADA, que siga generando el fichero
		}
		return map;
	}
}
