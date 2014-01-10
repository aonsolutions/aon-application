package com.esferalia.aon.ui.payroll.utils;

import java.util.List;
import java.util.Map;

import javax.faces.event.AbortProcessingException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.file.payroll.contract.pdf.model.AbstractContractModel.ModelOption;
import com.esferalia.aon.payroll.Agreement;
import com.esferalia.aon.payroll.CNO;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractAttachment;
import com.esferalia.aon.payroll.ContractBonus;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.ContractDeduction;
import com.esferalia.aon.payroll.ContractEmbargo;
import com.esferalia.aon.payroll.ContractInfo;
import com.esferalia.aon.payroll.ContractInfo.ContractVariable;
import com.esferalia.aon.payroll.ContractLeave;
import com.esferalia.aon.payroll.ContractLeaveDetail;
import com.esferalia.aon.payroll.ContractPayment;
import com.esferalia.aon.payroll.IrpfData;
import com.esferalia.aon.payroll.PayrollWorkPlace;
import com.esferalia.aon.payroll.TrainingCenter;
import com.esferalia.aon.payroll.TrainingCourse;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.OccupationType;
import com.esferalia.aon.payroll.enumeration.QuoteGroup;
import com.esferalia.aon.ui.payroll.controller.contract.ContractController;
import com.esferalia.aon.ui.payroll.controller.contract.ContractController.ContractParams;
import com.esferalia.aon.ui.sepe.utils.SEPEUtils;


public class ContractUtils {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ContractUtils.class.getName());
	
	private static ContractUtils instance;
	
	private ContractUtils(){
	}
	
	public static ContractUtils getInstance(){
		if(instance == null){
			instance = new ContractUtils();
		}
		return instance;
	}
	
	public void removeContractLines(ControllerEvent event) throws ControllerListenerException {
		ContractController controller = (ContractController) event.getController();
		Contract contract = (Contract) controller.getTo();
		
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);
			HibernateUtil.beginTransaction(sessionName);

			removeContrataAttach(contract);
			removeContractPayment(contract);
			removeContractDeduction(contract);
			removeContractBonus(contract);
			removeContractEmbargo(contract);
			removeContractIrpfdata(contract);
			removeContractLeave(contract);
			
			HibernateUtil.commitTransaction(sessionName);
		} catch (Exception e) {
			AonUtil.addErrorMessage(e.getMessage());
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {
				String msg = "Unable to rollback transaction!";
				LOGGER.error(msg, e);
			}
			String msg = "Error durante el borrado de datos. ";
			throw new AbortProcessingException(msg  + e.getMessage());
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
	    }
	}
	
	private void removeContrataAttach(Contract contract) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ContractAttachment.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_ATTACHMENT_CONTRACT_ID), contract.getId());
		for(ITransferObject to: bean.getList(criteria)){
			bean.remove(to);
		}
	}
	
	private void removeContractPayment(Contract contract) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ContractPayment.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_PAYMENT_CONTRACT_ID), contract.getId());
		for(ITransferObject to: bean.getList(criteria)){
			bean.remove(to);
		}
	}
	private void removeContractDeduction(Contract contract) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ContractDeduction.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DEDUCTION_CONTRACT_ID), contract.getId());
		for(ITransferObject to: bean.getList(criteria)){
			bean.remove(to);
		}
	}
	private void removeContractBonus(Contract contract) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ContractBonus.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_BONUS_CONTRACT_ID), contract.getId());
		for(ITransferObject to: bean.getList(criteria)){
			bean.remove(to);
		}
	}
	private void removeContractEmbargo(Contract contract) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ContractEmbargo.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_EMBARGO_CONTRACT_ID), contract.getId());
		for(ITransferObject to: bean.getList(criteria)){
			bean.remove(to);
		}
	}
	private void removeContractIrpfdata(Contract contract) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(IrpfData.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.IRPF_DATA_CONTRACT_ID), contract.getId());
		for(ITransferObject to: bean.getList(criteria)){
			bean.remove(to);
		}
	}
	private void removeContractLeave(Contract contract) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ContractLeave.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_LEAVE_CONTRACT_ID), contract.getId());
		for(ITransferObject to: bean.getList(criteria)){
			removeLeaveDetails((ContractLeave)to);
			bean.remove(to);
		}
	}
	private void removeLeaveDetails(ContractLeave contractLeave) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ContractLeaveDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_LEAVE_DETAIL_CONTRACT_LEAVE_ID), contractLeave.getId());
		for(ITransferObject to: bean.getList(criteria)){
			bean.remove(to);
		}
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
		
		insertContractInfo(contract, params);
		
	}
	
	public void insertContractInfo(Contract contract, ContractParams params) {
		IManagerBean bean;
		ContractInfo info;
		try {
			bean = BeanManager.getManagerBean(ContractData.class);
		} catch (ManagerBeanException e) {
			String msg = "Imposible grabar los datos de contrato. (" +e.getMessage() + ")";
			throw new AbortProcessingException(msg,e);
		}
		
		try {
			if(params.isRetaQuote()){
				info = new ContractInfo();
				info.setContract(contract);
				info.setStartDate(contract.getStartDate());
				info.setEndDate(contract.getEndDate());
				info.setName( ContractVariable.SELF_EMPLOYED.getValue() );
				info.setExpression(Boolean.TRUE.toString());
				bean.insert(info);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el centro de formacion. (" +e.getMessage() + ")";
			AonUtil.addErrorMessage(msg);
		}
		
		try {
			if(params.getTrainingCenter()!=null && params.getTrainingCenter().getId()!=null){
				info = new ContractInfo();
				info.setContract(contract);
				info.setStartDate(contract.getStartDate());
				info.setEndDate(contract.getEndDate());
				info.setName( ContractVariable.TRAINING_CENTER.getValue() );
				info.setExpression("\"" + params.getTrainingCenter().getId() + "\"");
				bean.insert(info);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el centro de formacion. (" +e.getMessage() + ")";
			AonUtil.addErrorMessage(msg);
		}
		try {
			if(params.getTrainingCourse()!=null && params.getTrainingCourse().getId()!=null){
				info = new ContractInfo();
				info.setContract(contract);
				info.setStartDate(params.getTrainingStartDate());
				info.setEndDate(params.getTrainingEndDate());
				info.setName( ContractVariable.TRAINING_COURSE.getValue() );
				info.setExpression("\"" + params.getTrainingCourse().getId() + "\"");
				bean.insert(info);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el curso de formacion. (" +e.getMessage() + ")";
			AonUtil.addErrorMessage(msg);
		}
		try {
			if(StringUtils.isNotBlank(params.getWorkSchedule())){
				info = new ContractInfo();
				info.setContract(contract);
				info.setStartDate(contract.getStartDate());
				info.setEndDate(contract.getEndDate());
				info.setName( ContractVariable.WORK_SCHEDULE.getValue() );
				info.setExpression("\"" + params.getWorkSchedule() + "\"");
				bean.insert(info);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el horario laboral. (" +e.getMessage() + ")";
			AonUtil.addErrorMessage(msg);
		}
		try {
			if(StringUtils.isNotBlank(params.getTrainingSchedule())){
				info = new ContractInfo();
				info.setContract(contract);
				info.setStartDate(contract.getStartDate());
				info.setEndDate(contract.getEndDate());
				info.setName( ContractVariable.TRAINING_SCHEDULE.getValue() );
				info.setExpression("\"" + params.getTrainingSchedule() + "\"");
				bean.insert(info);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el horario lectivo. (" +e.getMessage() + ")";
			AonUtil.addErrorMessage(msg);
		}
		try {
			if(params.getContractModelOption()!=null){
				info = new ContractInfo();
				info.setContract(contract);
				info.setStartDate(contract.getStartDate());
				info.setEndDate(contract.getEndDate());
				info.setName( ContractVariable.CONTRACT_MODEL_OPTION.getValue() );
				info.setExpression("\"" + params.getContractModelOption() + "\"");
				bean.insert(info);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el modelo del contrato. (" +e.getMessage() + ")";
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
		// TODO: updates available form contract transformations?
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
		
		updateContractInfo(contract, params);
		
	}
	
	public void updateContractInfo(Contract contract, ContractParams params) throws ControllerListenerException {
		ContractInfo info;
		IManagerBean bean;
		try {
			bean = BeanManager.getManagerBean(ContractInfo.class);
		} catch (ManagerBeanException e) {
			String msg = "Imposible grabar los datos de contrato. (" +e.getMessage() + ")";
			throw new AbortProcessingException(msg,e);
		}
		
		try {
			ContractInfo trainingCenterInfo = obtainContractInfo(contract, ContractVariable.TRAINING_CENTER.getValue());
			if(params.getTrainingCenter()!=null && params.getTrainingCenter().getId()!=null){
				info = trainingCenterInfo!=null?trainingCenterInfo:new ContractInfo();
				info.setContract(contract);
				info.setStartDate(contract.getStartDate());
				info.setEndDate(contract.getEndDate());
				info.setName( ContractVariable.TRAINING_CENTER.getValue() );
				info.setExpression("\"" + params.getTrainingCenter().getId() + "\"");
				bean.insertOrUpdate(info);
			} else {
				if(trainingCenterInfo != null){
					bean.remove(trainingCenterInfo);
				}
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el centro de formacion. (" +e.getMessage() + ")";
			AonUtil.addErrorMessage(msg);
		}
		try {
			ContractInfo trainingCourseInfo = obtainContractInfo(contract, ContractVariable.TRAINING_COURSE.getValue());
			if(params.getTrainingCourse()!=null && params.getTrainingCourse().getId()!=null){
				info = trainingCourseInfo!=null?trainingCourseInfo:new ContractInfo();
				info.setContract(contract);
				info.setStartDate(params.getTrainingStartDate());
				info.setEndDate(params.getTrainingEndDate());
				info.setName( ContractVariable.TRAINING_COURSE.getValue() );
				info.setExpression("\"" + params.getTrainingCourse().getId() + "\"");
				bean.insertOrUpdate(info);
			} else {
				if(trainingCourseInfo != null){
					bean.remove(trainingCourseInfo);
				}
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el curso de formacion. (" +e.getMessage() + ")";
			AonUtil.addErrorMessage(msg);
		}
		try {
			ContractInfo workScheduleInfo = obtainContractInfo(contract, ContractVariable.WORK_SCHEDULE.getValue());
			if(StringUtils.isNotBlank(params.getWorkSchedule())){
				info = workScheduleInfo!=null?workScheduleInfo:new ContractInfo();
				info.setContract(contract);
				info.setStartDate(contract.getStartDate());
				info.setEndDate(contract.getEndDate());
				info.setName( ContractVariable.WORK_SCHEDULE.getValue() );
				info.setExpression("\"" + params.getWorkSchedule() + "\"");
				bean.insertOrUpdate(info);
			} else {
				if(workScheduleInfo != null){
					bean.remove(workScheduleInfo);
				}
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el horario laboral. (" +e.getMessage() + ")";
			AonUtil.addErrorMessage(msg);
		}
		try {
			ContractInfo trainingScheduleInfo = obtainContractInfo(contract, ContractVariable.TRAINING_SCHEDULE.getValue());
			if(StringUtils.isNotBlank(params.getTrainingSchedule())){
				info = trainingScheduleInfo!=null?trainingScheduleInfo:new ContractInfo();
				info.setContract(contract);
				info.setStartDate(contract.getStartDate());
				info.setEndDate(contract.getEndDate());
				info.setName( ContractVariable.TRAINING_SCHEDULE.getValue() );
				info.setExpression("\"" + params.getTrainingSchedule() + "\"");
				bean.insertOrUpdate(info);
			} else {
				if(trainingScheduleInfo != null){
					bean.remove(trainingScheduleInfo);
				}
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el horario lectivo. (" +e.getMessage() + ")";
			AonUtil.addErrorMessage(msg);
		}
		try {
			ContractInfo contractModelOption = obtainContractInfo(contract, ContractVariable.CONTRACT_MODEL_OPTION.getValue());
			if(params.getContractModelOption()!=null){
				info = contractModelOption!=null?contractModelOption:new ContractInfo();
				info.setContract(contract);
				info.setStartDate(contract.getStartDate());
				info.setEndDate(contract.getEndDate());
				info.setName( ContractVariable.CONTRACT_MODEL_OPTION.getValue() );
				info.setExpression("\"" + params.getContractModelOption() + "\"");
				bean.insertOrUpdate(info);
			} else {
				if(contractModelOption != null){
					bean.remove(contractModelOption);
				}
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el modelo del contrato. (" +e.getMessage() + ")";
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

		loadContractInfo(contract, params);
		loadContractBonuses(contract, params);
		
	}
	public void loadContractInfo(Contract contract, ContractParams params) throws ManagerBeanException {
		Map<String, String> map = getContractInfoMap(contract);
		
		if(map.get(ContractVariable.SELF_EMPLOYED.getValue())!=null){
			params.setRetaQuote(new Boolean(map.get(ContractVariable.SELF_EMPLOYED.getValue())));
		} 
		if(map.get(ContractVariable.CONTRACT_MODEL_OPTION.getValue())!=null){
			String ordinal = (map.get(ContractVariable.CONTRACT_MODEL_OPTION.getValue()));
			params.setContractModelOption(ModelOption.valueOf(ordinal));
		}
		if(isTrainingContract(contract)){
			if(map.get(ContractVariable.TRAINING_CENTER.getValue())!=null){
				params.setTrainingCenter(obtainTrainingCenter(map.get(ContractVariable.TRAINING_CENTER.getValue())));
			} else {
				params.setTrainingCenter((TrainingCenter) BeanManager.getManagerBean(TrainingCenter.class).createNewTo());
			}
			if(map.get(ContractVariable.TRAINING_COURSE.getValue())!=null){
				params.setTrainingCourse(obtainTrainingCourse(map.get(ContractVariable.TRAINING_COURSE.getValue())));
				ContractInfo info = obtainContractInfo(contract, ContractVariable.TRAINING_COURSE.getValue());
				params.setTrainingStartDate(info.getStartDate());
				params.setTrainingEndDate(info.getEndDate());
			} else {
				params.setTrainingCourse((TrainingCourse) BeanManager.getManagerBean(TrainingCourse.class).createNewTo());
			}
			if(map.get(ContractVariable.WORK_SCHEDULE.getValue())!=null){
				params.setWorkSchedule(map.get(ContractVariable.WORK_SCHEDULE.getValue()));
			}
			if(map.get(ContractVariable.TRAINING_SCHEDULE.getValue())!=null){
				params.setTrainingSchedule(map.get(ContractVariable.TRAINING_SCHEDULE.getValue()));
			}
		}
	}
	
	public void loadContractBonuses(Contract contract, ContractParams params) throws ManagerBeanException {
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractBonus.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_BONUS_CONTRACT_ID), contract.getId());
			criteria.addOrder(bean.getFieldName(IEntityAlias.CONTRACT_BONUS_CONTRACT_ID));
			List<ITransferObject> list = bean.getList(criteria);
			params.setBonuses( list ); 
			params.setBonusModel(null);
		} catch (ManagerBeanException e) {
			String msg = "Error al cargar las bonificaciones del contrato";
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

	private ContractInfo obtainContractInfo(Contract contract, String name) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractInfo.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_INFO_CONTRACT_ID), contract.getId() );
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_INFO_NAME), name );
			List<ITransferObject> list = bean.getList(criteria);
			if( !list.isEmpty() ){
				return (ContractInfo) list.get(0);
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
		return SEPEUtils.getInstance().getContractDataMap(contract);
	}

	public Map<String, String> getContractInfoMap(Contract contract) {
		return SEPEUtils.getInstance().getContractInfoMap(contract);
	}
	
//	public Map<String, String> getContractDataMap(Contract contract) {
//		Map<String, String> map = new HashMap<String, String>();
//		try {
//			IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
//			Criteria criteria = new Criteria();
//			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_CONTRACT_ID), contract.getId());
//			criteria.addGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_START_DATE), contract.getStartDate());
//			Expression endDateExp = ExpressionUtilities.getNullExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_END_DATE));
//			if(contract.getEndDate()!=null){
//				Expression exp = ExpressionUtilities.getLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_END_DATE), contract.getEndDate());
//				endDateExp = ExpressionUtilities.getOrExpression(exp, endDateExp);
//			} else {
//				criteria.addExpression(endDateExp);
//			}
//			for(ITransferObject to: bean.getList(criteria)){
//				ContractData data = (ContractData) to;
//				if( StringUtils.isNotEmpty(data.getName()) && StringUtils.isNotEmpty(data.getExpression()) ){
//					map.put(data.getName(), data.getExpression().replace('"', ' ').trim());
//				}
//			}
//		} catch (ManagerBeanException e) {
//			// NADA, se devuelve un mapa vacio
//			return map;
//		}
//		return map;
//	}
//	
//	public Map<String, ContractData> getContractDataMap(Contract contract, Date startDate, Date endDate) {
//		Map<String, ContractData> map = new HashMap<String, ContractData>();
//		try {
//			IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
//			Criteria criteria = new Criteria();
//			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_CONTRACT_ID), contract.getId());
//			if(startDate!=null){
//				criteria.addGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_START_DATE), startDate);
//			}
//			if(endDate!=null){
//				criteria.addLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_END_DATE), endDate);
//			}
//			for(ITransferObject to: bean.getList(criteria)){
//				ContractData data = (ContractData) to;
//				if(data.getExpression()!=null){
//					map.put(data.getName(), data);
//				}
//			}
//		} catch (ManagerBeanException e) {
//			// NADA, que siga generando el fichero
//		}
//		return map;
//	}
//	
//	public Map<String, String> getContractInfoMap(Contract contract) {
//		Map<String, String> map = new HashMap<String, String>();
//		try {
//			IManagerBean bean = BeanManager.getManagerBean(ContractInfo.class);
//			Criteria criteria = new Criteria();
//			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_INFO_CONTRACT_ID), contract.getId());
//			criteria.addGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_INFO_START_DATE), contract.getStartDate());
//			Expression endDateExp = ExpressionUtilities.getNullExpression(bean.getFieldName(IEntityAlias.CONTRACT_INFO_END_DATE));
//			if(contract.getEndDate()!=null){
////				criteria.addLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_END_DATE), contract.getEndDate());
//				Expression exp = ExpressionUtilities.getLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_INFO_END_DATE), contract.getEndDate());
//				endDateExp = ExpressionUtilities.getOrExpression(exp, endDateExp);
//			} else {
////				criteria.addNullExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_END_DATE));
//				criteria.addExpression(endDateExp);
//			}
//			for(ITransferObject to: bean.getList(criteria)){
//				ContractData data = (ContractData) to;
//				if( StringUtils.isNotEmpty(data.getName()) && StringUtils.isNotEmpty(data.getExpression()) ){
//					map.put(data.getName(), data.getExpression().replace('"', ' ').trim());
//				}
//			}
//		} catch (ManagerBeanException e) {
//			// NADA, se devuelve un mapa vacio
//			return map;
//		}
//		return map;
//	}
//	
//	public Map<String, ContractInfo> getContractInfoMap(Contract contract, Date startDate, Date endDate) {
//		Map<String, ContractInfo> map = new HashMap<String, ContractInfo>();
//		try {
//			IManagerBean bean = BeanManager.getManagerBean(ContractInfo.class);
//			Criteria criteria = new Criteria();
//			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_INFO_CONTRACT_ID), contract.getId());
//			if(startDate!=null){
//				criteria.addGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_INFO_START_DATE), startDate);
//			}
//			if(endDate!=null){
//				criteria.addLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_INFO_END_DATE), endDate);
//			}
//			for(ITransferObject to: bean.getList(criteria)){
//				ContractInfo info = (ContractInfo) to;
//				if(info.getExpression()!=null){
//					map.put(info.getName(), info);
//				}
//			}
//		} catch (ManagerBeanException e) {
//			// NADA, que siga generando el fichero
//		}
//		return map;
//	}
	
	
}
