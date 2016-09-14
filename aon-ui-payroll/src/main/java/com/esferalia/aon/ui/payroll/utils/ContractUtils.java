package com.esferalia.aon.ui.payroll.utils;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.event.AbortProcessingException;

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.file.payroll.contract.pdf.ModelOption;
import com.esferalia.aon.payroll.Agreement;
import com.esferalia.aon.payroll.CNO;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractAttachment;
import com.esferalia.aon.payroll.ContractBonus;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.ContractDeduction;
import com.esferalia.aon.payroll.ContractEmbargo;
import com.esferalia.aon.payroll.ContractInfo;
import com.esferalia.aon.payroll.ContractInfo.ContractSepeStatus;
import com.esferalia.aon.payroll.ContractInfo.ContractSsStatus;
import com.esferalia.aon.payroll.ContractInfo.ContractVariable;
import com.esferalia.aon.payroll.ContractLeave;
import com.esferalia.aon.payroll.ContractLeaveDetail;
import com.esferalia.aon.payroll.ContractPayment;
import com.esferalia.aon.payroll.IrpfData;
import com.esferalia.aon.payroll.PayrollWorkPlace;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryData;
import com.esferalia.aon.payroll.TrainingCenter;
import com.esferalia.aon.payroll.TrainingCourse;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.OccupationType;
import com.esferalia.aon.payroll.enumeration.QuoteGroup;
import com.esferalia.aon.payroll.enumeration.certificados.TLDCAUSS;
import com.esferalia.aon.payroll.enumeration.ss.T53;
import com.esferalia.aon.payroll.enumeration.ss.T54;
import com.esferalia.aon.payroll.enumeration.ss.T55;
import com.esferalia.aon.ui.payroll.controller.EnterpriseParamsController;
import com.esferalia.aon.ui.payroll.controller.contract.ContractController;
import com.esferalia.aon.ui.payroll.controller.contract.ContractController.ContractParams;
import com.esferalia.aon.ui.payroll.controller.contract.ContractController.ContractQuoteType;
import com.esferalia.aon.ui.sepe.utils.SEPEUtils;


public class ContractUtils implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
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
	
	public void insertContractData(Contract contract, String name, String expression) throws ManagerBeanException {
		insertContractData(contract, name, expression, contract.getStartDate(), contract.getEndDate());
	}

	public void insertContractData(Contract contract, String name, String expression, Date startDate, Date endDate) throws ManagerBeanException {
		IManagerBean bean;
		try {
			bean = BeanManager.getManagerBean(ContractData.class);
		} catch (ManagerBeanException e) {
			String msg = "Imposible grabar los datos de contrato. (" +e.getMessage() + ")";
			throw new AbortProcessingException(msg,e);
		}
		ContractData data;
		data = new ContractData();
		data.setContract( contract );
		data.setStartDate( startDate );
		data.setEndDate( endDate );
		data.setName( name );
		data.setExpression( expression );
		bean.insert(data);
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
			if(params.getContractQuoteType()!=null){
				data = new ContractData();
				data.setContract(contract);
				data.setStartDate(contract.getStartDate());
				data.setEndDate(null);
				data.setName( "TIPO_COTIZACION" );
				if(params.getContractQuoteType()==ContractQuoteType.COOPERATIVE_PARTNER){
					data.setExpression("\"01\"");
				} else if(params.getContractQuoteType()==ContractQuoteType.ACTIVE_RETIREMENT){
					data.setExpression("\"02\"");
				} else if(params.getContractQuoteType()==ContractQuoteType.YOUTH_GUARANTEE){
					data.setExpression("\"03\"");
				}
				bean.insert(data);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el grupo de cotizacion. (" +e.getMessage() + ")";
			AonUtil.addErrorMessage(msg);
		}
		try {
			if(params.getQuoteGroup()!=null){
				data = new ContractData();
				data.setContract(contract);
				data.setStartDate(contract.getStartDate());
				data.setEndDate(null);
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
				data.setEndDate(null);
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
				data.setEndDate(null);
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
				data.setEndDate(null);
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
				data.setEndDate(null);
				data.setName( ContextVariable.TC2.getName() );
				data.setExpression("\"" + params.getContractCode() + "\"");
				bean.insert(data);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el codigo TC2. (" +e.getMessage() + ")";
			AonUtil.addErrorMessage(msg);
		}
		try {
			if(params.getCollectivePeculiarityQuote()!=null){
				data = new ContractData();
				data.setContract(contract);
				data.setStartDate(contract.getStartDate());
				data.setEndDate(null);
				data.setName( ContextVariable.QUOTE_PECULIARITY_COLLECTIVE.getName() );
				data.setExpression("\"" + params.getCollectivePeculiarityQuote().getCode() + "\"");
				bean.insert(data);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el colectivo de peculiar cotizacion. (" +e.getMessage() + ")";
			AonUtil.addErrorMessage(msg);
		}
		try {
			if(params.getSuspensionCause()!=null){
				data = new ContractData();
				data.setContract(contract);
				data.setStartDate(contract.getStartDate());
				data.setEndDate(null);
				data.setName( ContextVariable.CONTRACT_END_CODE.getName() );
				data.setExpression("\"" + params.getSuspensionCause().getCode() + "\"");
				bean.insert(data);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar la causa de suspension. (" +e.getMessage() + ")";
			AonUtil.addErrorMessage(msg);
		}
		
		insertPartialTimeContractData(contract, params);
	}
		
	public void insertRetaContractData(Contract contract, ContractParams params) {
		IManagerBean bean;
		ContractData data;
		try {
			bean = BeanManager.getManagerBean(ContractData.class);
		} catch (ManagerBeanException e) {
			String msg = "Imposible grabar los datos de contrato. (" +e.getMessage() + ")";
			throw new AbortProcessingException(msg,e);
		}
		try {
			data = new ContractData();
			data.setContract(contract);
			data.setStartDate(contract.getStartDate());
			data.setEndDate(contract.getEndDate());
			data.setName( ContextVariable.FULL_TIME.getName() );
			data.setExpression(String.valueOf(!params.isRetaPartialTime()));
			bean.insert(data);
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el tipo de jornada. (" +e.getMessage() + ")";
			AonUtil.addErrorMessage(msg);
		}
		
		insertPartialTimeContractData(contract, params);
		
	}
	
	public void insertPartialTimeContractData(Contract contract, ContractParams params) {
		IManagerBean bean;
		ContractData data;
		try {
			bean = BeanManager.getManagerBean(ContractData.class);
		} catch (ManagerBeanException e) {
			String msg = "Imposible grabar los datos de contrato. (" +e.getMessage() + ")";
			throw new AbortProcessingException(msg,e);
		}
		try {
			if(params.getWeekDayHours()[0]!=null){
				data = new ContractData();
				data.setContract(contract);
				data.setStartDate(contract.getStartDate());
				data.setEndDate(null);
				data.setName( ContextVariable.MONDAY_HOURS.getName() );
				data.setExpression(params.getWeekDayHours()[0].toString());
				bean.insert(data);
			}
			if(params.getWeekDayHours()[1]!=null){
				data = new ContractData();
				data.setContract(contract);
				data.setStartDate(contract.getStartDate());
				data.setEndDate(null);
				data.setName( ContextVariable.TUESDAY_HOURS.getName() );
				data.setExpression(params.getWeekDayHours()[1].toString());
				bean.insert(data);
			}
			if(params.getWeekDayHours()[2]!=null){
				data = new ContractData();
				data.setContract(contract);
				data.setStartDate(contract.getStartDate());
				data.setEndDate(null);
				data.setName( ContextVariable.WEDNESDAY_HOURS.getName() );
				data.setExpression(params.getWeekDayHours()[2].toString());
				bean.insert(data);
			}
			if(params.getWeekDayHours()[3]!=null){
				data = new ContractData();
				data.setContract(contract);
				data.setStartDate(contract.getStartDate());
				data.setEndDate(null);
				data.setName( ContextVariable.THURSDAY_HOURS.getName() );
				data.setExpression(params.getWeekDayHours()[3].toString());
				bean.insert(data);
			}
			if(params.getWeekDayHours()[4]!=null){
				data = new ContractData();
				data.setContract(contract);
				data.setStartDate(contract.getStartDate());
				data.setEndDate(null);
				data.setName( ContextVariable.FRIDAY_HOURS.getName() );
				data.setExpression(params.getWeekDayHours()[4].toString());
				bean.insert(data);
			}
			if(params.getWeekDayHours()[5]!=null){
				data = new ContractData();
				data.setContract(contract);
				data.setStartDate(contract.getStartDate());
				data.setEndDate(null);
				data.setName( ContextVariable.SATURDAY_HOURS.getName() );
				data.setExpression(params.getWeekDayHours()[5].toString());
				bean.insert(data);
			}
			if(params.getWeekDayHours()[6]!=null){
				data = new ContractData();
				data.setContract(contract);
				data.setStartDate(contract.getStartDate());
				data.setEndDate(null);
				data.setName( ContextVariable.SUNDAY_HOURS.getName() );
				data.setExpression(params.getWeekDayHours()[6].toString());
				bean.insert(data);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar las horas del contrato. (" +e.getMessage() + ")";
			AonUtil.addErrorMessage(msg);
		}
		
	}
	
	public void insertContractInfo(Contract contract, ContractParams params) {
		IManagerBean bean;
		ContractInfo info;
		try {
			bean = BeanManager.getManagerBean(ContractInfo.class);
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
			if(params.getContractQuoteType()!=null){
				info = new ContractInfo();
				info.setContract(contract);
				info.setStartDate(contract.getStartDate());
				info.setEndDate(contract.getEndDate());
				if(params.getContractQuoteType()==ContractQuoteType.COOPERATIVE_PARTNER){
					info.setName( ContractVariable.COOPERATIVE_PARTNER.getValue() );
				} else if(params.getContractQuoteType()==ContractQuoteType.ACTIVE_RETIREMENT){
					info.setName( ContractVariable.ACTIVE_RETIREMENT.getValue() );
				} else if(params.getContractQuoteType()==ContractQuoteType.YOUTH_GUARANTEE){
					info.setName( ContractVariable.YOUTH_GUARANTEE.getValue() );
				}
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
		try {
			if(params.getSepeContractId()!=null){
				info = new ContractInfo();
				info.setContract(contract);
				info.setStartDate(contract.getStartDate());
				info.setEndDate(contract.getEndDate());
				info.setName( ContractVariable.SEPE_CONTRACT_ID.getValue() );
				info.setExpression(params.getSepeContractId());
				bean.insert(info);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el modelo del contrato. (" +e.getMessage() + ")";
			AonUtil.addErrorMessage(msg);
		}
		
		try {
			if(params.getPartialTimeReductionIndicator()!=null){
				info = new ContractInfo();
				info.setContract(contract);
				info.setStartDate(contract.getStartDate());
				info.setEndDate(contract.getEndDate());
				info.setName( ContractVariable.BONUS_REDUCTION_INDICATOR.getValue() );
				info.setExpression("\"" + params.getPartialTimeReductionIndicator().getCode() + "\"");
				bean.insert(info);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el indicador de reduccion de bonificacion. (" +e.getMessage() + ")";
			AonUtil.addErrorMessage(msg);
		}
		
		try {
			if(params.getDisabilityIndicator()!=null){
				info = new ContractInfo();
				info.setContract(contract);
				info.setStartDate(contract.getStartDate());
				info.setEndDate(contract.getEndDate());
				info.setName( ContractVariable.DISABILITY_INDICATOR.getValue() );
				info.setExpression("\"" + params.getDisabilityIndicator().getCode() + "\"");
				bean.insert(info);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el indicador de discapacidad. (" +e.getMessage() + ")";
			AonUtil.addErrorMessage(msg);
		}

		
	}
	
	public void insertContractInfo(Contract contract, String name, String expression) throws ManagerBeanException {
		insertContractInfo(contract, name, expression, contract.getStartDate(), contract.getEndDate());
	}

	public void insertContractInfo(Contract contract, String name, String expression, Date startDate, Date endDate) throws ManagerBeanException {
		IManagerBean bean;
		try {
			bean = BeanManager.getManagerBean(ContractInfo.class);
		} catch (ManagerBeanException e) {
			String msg = "Imposible grabar los datos de contrato. (" +e.getMessage() + ")";
			throw new AbortProcessingException(msg,e);
		}
		ContractInfo info;
		info = new ContractInfo();
		info.setContract( contract );
		info.setStartDate( startDate );
		info.setEndDate( endDate );
		info.setName( name );
		info.setExpression( expression );
		bean.insert(info);
	}
	
	public void enableCooperativePartner(Contract contract){
		 try {
			 insertContractInfo(contract, ContractVariable.COOPERATIVE_PARTNER.getValue(), Boolean.TRUE.toString(), contract.getStartDate(), contract.getEndDate());
		 } catch (ManagerBeanException e) {
			 String msg = "Error al grabar el tipo de cotizacion. (" +e.getMessage() + ")";
			 AonUtil.addErrorMessage(msg);
		 }
	 }

	public void enableActiveRetirement(Contract contract){
		try {
			insertContractInfo(contract, ContractVariable.ACTIVE_RETIREMENT.getValue(), Boolean.TRUE.toString(), contract.getStartDate(), contract.getEndDate());
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el tipo de cotizacion. (" +e.getMessage() + ")";
			AonUtil.addErrorMessage(msg);
		}
	}
	
	public void enableYouthGuarantee(Contract contract){
		try {
			insertContractInfo(contract, ContractVariable.YOUTH_GUARANTEE.getValue(), Boolean.TRUE.toString(), contract.getStartDate(), contract.getEndDate());
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el tipo de cotizacion. (" +e.getMessage() + ")";
			AonUtil.addErrorMessage(msg);
		}
	}
	
	public void updateContractData(Contract contract, String name, String value) throws ControllerListenerException {
		IManagerBean bean;
		try {
			bean = BeanManager.getManagerBean(ContractData.class);
			ContractData data = obtainContractData(contract, name);
			data = data!=null?data:new ContractData();
			data.setContract(contract);
			data.setStartDate(contract.getStartDate());
			data.setEndDate(contract.getEndDate());
			data.setName( name );
			data.setExpression(value);
			bean.insertOrUpdate(data);
		} catch (ManagerBeanException e) {
			String msg = "Imposible actualizar los datos de contrato. (" +e.getMessage() + ")";
			AonUtil.addErrorMessage(msg);
			throw new ControllerListenerException(msg,e);
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
			ContractData contractQuoteTypeData = obtainContractData(contract, "TIPO_COTIZACION");
			if(params.getContractQuoteType()!=null && params.getContractQuoteType()!=ContractQuoteType.RETA){
				data = contractQuoteTypeData!=null?contractQuoteTypeData:new ContractData();
				data.setContract(contract);
				data.setStartDate(contract.getStartDate());
				data.setEndDate(null);
				data.setName( "TIPO_COTIZACION" );
				if(params.getContractQuoteType()==ContractQuoteType.COOPERATIVE_PARTNER){
					data.setExpression("\"01\"");
				} else if(params.getContractQuoteType()==ContractQuoteType.ACTIVE_RETIREMENT){
					data.setExpression("\"02\"");
				} else if(params.getContractQuoteType()==ContractQuoteType.YOUTH_GUARANTEE){
					data.setExpression("\"03\"");
				}
				bean.insertOrUpdate(data);
			} else {
				if(contractQuoteTypeData != null){
					bean.remove(contractQuoteTypeData);
				}
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el grupo de cotizacion. (" +e.getMessage() + ")";
			AonUtil.addErrorMessage(msg);
		}
		try {
			ContractData quoteGroupData = obtainContractData(contract, ContextVariable.QUOTE_GROUP.getName());
			if(params.getQuoteGroup()!=null){
				data = quoteGroupData!=null?quoteGroupData:new ContractData();
				data.setContract(contract);
				data.setStartDate(contract.getStartDate());
				data.setEndDate(null);
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
				data.setEndDate(null);
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
				data.setEndDate(null);
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
				data.setEndDate(null);
				data.setName( ContextVariable.TC2.getName() );
				data.setExpression("\"" + params.getContractCode() + "\"");
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
			ContractData collectivePeculiarityQuote = obtainContractData(contract, ContextVariable.QUOTE_PECULIARITY_COLLECTIVE.getName());
			if(params.getCollectivePeculiarityQuote()!=null){
				data = collectivePeculiarityQuote!=null?collectivePeculiarityQuote:new ContractData();
				data.setContract(contract);
				data.setStartDate(contract.getStartDate());
				data.setEndDate(null);
				data.setName( ContextVariable.QUOTE_PECULIARITY_COLLECTIVE.getName() );
				data.setExpression("\"" + params.getCollectivePeculiarityQuote().getCode() + "\"");
				bean.insertOrUpdate(data);
			} else {
				if(collectivePeculiarityQuote != null){
					bean.remove(collectivePeculiarityQuote);
				}
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el colectivo de peculiar cotizacion. (" +e.getMessage() + ")";
			AonUtil.addErrorMessage(msg);
		}
		try {
			ContractData contractEndCode = obtainContractData(contract, ContextVariable.CONTRACT_END_CODE.getName());
			if(params.getSuspensionCause()!=null){
				data = contractEndCode!=null?contractEndCode:new ContractData();
				data.setContract(contract);
				data.setStartDate(contract.getStartDate());
				data.setEndDate(null);
				data.setName( ContextVariable.CONTRACT_END_CODE.getName() );
				data.setExpression("\"" + params.getSuspensionCause().getCode() + "\"");
				bean.insertOrUpdate(data);
			} else {
				if(contractEndCode != null){
					bean.remove(contractEndCode);
				}
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar la causa de suspension. (" +e.getMessage() + ")";
			AonUtil.addErrorMessage(msg);
		}
		
	}
	
	public void updateRetaContractData(Contract contract, ContractParams params) throws ControllerListenerException {
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
			ContractData fullTimeData = obtainContractData(contract, ContextVariable.FULL_TIME.getName());
			data = fullTimeData!=null?fullTimeData:new ContractData();
			data.setContract(contract);
			data.setStartDate(contract.getStartDate());
			data.setEndDate(contract.getEndDate());
			data.setName( ContextVariable.FULL_TIME.getName() );
			data.setExpression(String.valueOf(!params.isRetaPartialTime()));
			bean.insertOrUpdate(data);
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el tipo de jornada (" +e.getMessage() + ")";
			AonUtil.addErrorMessage(msg);
		}
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
		try {
			ContractInfo sepeContractId = obtainContractInfo(contract, ContractVariable.SEPE_CONTRACT_ID.getValue());
			if(params.getSepeContractId()!=null){
				info = sepeContractId!=null?sepeContractId:new ContractInfo();
				info.setContract(contract);
				info.setStartDate(contract.getStartDate());
				info.setEndDate(contract.getEndDate());
				info.setName( ContractVariable.SEPE_CONTRACT_ID.getValue() );
				info.setExpression("\"" + params.getSepeContractId() + "\"");
				bean.insertOrUpdate(info);
			} else {
				if(sepeContractId != null){
					bean.remove(sepeContractId);
				}
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el codigo de contrato de SEPE. (" +e.getMessage() + ")";
			AonUtil.addErrorMessage(msg);
		}

		try {
			ContractInfo bonusReductionIndicator = obtainContractInfo(contract, ContractVariable.BONUS_REDUCTION_INDICATOR.getValue());
			if(params.getPartialTimeReductionIndicator()!=null){
				info = bonusReductionIndicator!=null?bonusReductionIndicator:new ContractInfo();
				info.setContract(contract);
				info.setStartDate(contract.getStartDate());
				info.setEndDate(contract.getEndDate());
				info.setName( ContractVariable.BONUS_REDUCTION_INDICATOR.getValue() );
				info.setExpression("\"" + params.getPartialTimeReductionIndicator().getCode() + "\"");
				bean.insertOrUpdate(info);
			} else {
				if(bonusReductionIndicator != null){
					bean.remove(bonusReductionIndicator);
				}
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el indicador de reduccion de bonificacion. (" +e.getMessage() + ")";
			AonUtil.addErrorMessage(msg);
		}
		
		try {
			ContractInfo disabilityIndicator = obtainContractInfo(contract, ContractVariable.DISABILITY_INDICATOR.getValue());
			if(params.getDisabilityIndicator()!=null){
				info = disabilityIndicator!=null?disabilityIndicator:new ContractInfo();
				info.setContract(contract);
				info.setStartDate(contract.getStartDate());
				info.setEndDate(contract.getEndDate());
				info.setName( ContractVariable.DISABILITY_INDICATOR.getValue() );
				info.setExpression(params.getDisabilityIndicator().getCode());
				bean.insertOrUpdate(info);
			} else {
				if(disabilityIndicator != null){
					bean.remove(disabilityIndicator);
				}
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el indicador de reduccion de bonificacion. (" +e.getMessage() + ")";
			AonUtil.addErrorMessage(msg);
		}
		
		try {
			if(params.getSsStatusInfo()!=null){
				bean.insertOrUpdate(params.getSsStatusInfo());
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el estado del contrato. (" +e.getMessage() + ")";
			AonUtil.addErrorMessage(msg);
		}
		try {
			if(params.getSepeStatusInfo()!=null){
				bean.insertOrUpdate(params.getSepeStatusInfo());
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el estado del contrato. (" +e.getMessage() + ")";
			AonUtil.addErrorMessage(msg);
		}
		
	}
	
	public void loadContractData(Contract contract, ContractParams params) throws ManagerBeanException {
		Map<String, String> map = getContractDataMap(contract);
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
			params.setContractCode( map.get(ContextVariable.TC2.getName()) );
		}
		if(map.get(ContextVariable.TC2.getName())!=null){
//			params.setContractTransformCode( ContractCode.getContractCodeByValue(map.get(ContextVariable.TC2.getName())) );
		}
		if(map.get(ContextVariable.QUOTE_PECULIARITY_COLLECTIVE.getName())!=null){
			params.setCollectivePeculiarityQuote(T54.getEnumByValue(map.get(ContextVariable.QUOTE_PECULIARITY_COLLECTIVE.getName())));
		}
		if(map.get(ContextVariable.CONTRACT_END_CODE.getName())!=null){
			params.setSuspensionCause(TLDCAUSS.getEnumByValue(map.get(ContextVariable.CONTRACT_END_CODE.getName())));
		}
		if(map.get("DIAS_PREAVISO")!=null){
			params.setSuspensionCause(TLDCAUSS.getEnumByValue(map.get(ContextVariable.CONTRACT_END_CODE.getName())));
		}
		
		if(map.get(ContextVariable.FULL_TIME.getName())!=null){
			params.setRetaPartialTime(!new Boolean(map.get(ContextVariable.FULL_TIME.getName())));
		}
		// PART-TIME 
		if(map.get(ContextVariable.MONDAY_HOURS.getName())!=null){
			params.getWeekDayHours()[0] = (Double.parseDouble(map.get(ContextVariable.MONDAY_HOURS.getName())));
		}
		if(map.get(ContextVariable.TUESDAY_HOURS.getName())!=null){
			params.getWeekDayHours()[1] = (Double.parseDouble(map.get(ContextVariable.TUESDAY_HOURS.getName())));
		}
		if(map.get(ContextVariable.WEDNESDAY_HOURS.getName())!=null){
			params.getWeekDayHours()[2] = (Double.parseDouble(map.get(ContextVariable.WEDNESDAY_HOURS.getName())));
		}
		if(map.get(ContextVariable.THURSDAY_HOURS.getName())!=null){
			params.getWeekDayHours()[3] = (Double.parseDouble(map.get(ContextVariable.THURSDAY_HOURS.getName())));
		}
		if(map.get(ContextVariable.FRIDAY_HOURS.getName())!=null){
			params.getWeekDayHours()[4] = (Double.parseDouble(map.get(ContextVariable.FRIDAY_HOURS.getName())));
		}
		if(map.get(ContextVariable.SATURDAY_HOURS.getName())!=null){
			params.getWeekDayHours()[5] = (Double.parseDouble(map.get(ContextVariable.SATURDAY_HOURS.getName())));
		}
		if(map.get(ContextVariable.SUNDAY_HOURS.getName())!=null){
			params.getWeekDayHours()[6] = (Double.parseDouble(map.get(ContextVariable.SUNDAY_HOURS.getName())));
		}
		if(map.get(ContextVariable.WEEK_HOURS.getName())!=null){
			params.setWeekHours( Double.parseDouble(map.get(ContextVariable.WEEK_HOURS.getName())) );
			if( params.getWeekDayHours()[0]==null && params.getWeekDayHours()[1]==null && params.getWeekDayHours()[2]==null && params.getWeekDayHours()[3]==null
					&& params.getWeekDayHours()[4]==null && params.getWeekDayHours()[5]==null && params.getWeekDayHours()[6]==null ){
				params.getWeekDayHours()[0] = CommonUtil.round(params.getWeekHours()/5);
				params.getWeekDayHours()[1] = CommonUtil.round(params.getWeekHours()/5);
				params.getWeekDayHours()[2] = CommonUtil.round(params.getWeekHours()/5);
				params.getWeekDayHours()[3] = CommonUtil.round(params.getWeekHours()/5);
				params.getWeekDayHours()[4] = CommonUtil.round(params.getWeekHours()/5);
			}
		}
	}
	
	public void loadContractInfo(Contract contract, ContractParams params) throws ManagerBeanException {
		Map<String, String> map = getContractInfoMap(contract);
		if(map.get(ContractVariable.SELF_EMPLOYED.getValue())!=null){
			if(new Boolean(map.get(ContractVariable.SELF_EMPLOYED.getValue()))){
				params.setContractQuoteType(ContractQuoteType.RETA);
			}
		} else if(map.get(ContractVariable.COOPERATIVE_PARTNER.getValue())!=null){
			if(new Boolean(map.get(ContractVariable.COOPERATIVE_PARTNER.getValue()))){
				params.setContractQuoteType(ContractQuoteType.COOPERATIVE_PARTNER);
			}
		} else if(map.get(ContractVariable.ACTIVE_RETIREMENT.getValue())!=null){
			if(new Boolean(map.get(ContractVariable.ACTIVE_RETIREMENT.getValue()))){
				params.setContractQuoteType(ContractQuoteType.ACTIVE_RETIREMENT);
			}
		} else if(map.get(ContractVariable.YOUTH_GUARANTEE.getValue())!=null){
			if(new Boolean(map.get(ContractVariable.YOUTH_GUARANTEE.getValue()))){
				params.setContractQuoteType(ContractQuoteType.YOUTH_GUARANTEE);
			}
		} else {
			params.setContractQuoteType(null);
		}
			
		if(map.get(ContractVariable.CONTRACT_MODEL_OPTION.getValue())!=null){
			String ordinal = (map.get(ContractVariable.CONTRACT_MODEL_OPTION.getValue()));
			params.setContractModelOption(ModelOption.valueOf(ordinal));
		}
		if(isTrainingContract(contract, params)){
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
		}
		
		Map<String, ContractInfo> infoMap = SEPEUtils.getInstance().getContractInfoMap(contract, contract.getStartDate(), contract.getEndDate());
		if(map.get(ContractVariable.SEPE_CONTRACT.getValue())!=null){
			params.setSepeStatusInfo(infoMap.get(ContractVariable.SEPE_CONTRACT.getValue()));
		} else {
			ContractInfo sepeStatus = new ContractInfo();
			sepeStatus.setContract(contract);
			sepeStatus.setStartDate(contract.getStartDate());
			sepeStatus.setEndDate(contract.getEndDate());
			sepeStatus.setName( ContractVariable.SEPE_CONTRACT.getValue() );
			sepeStatus.setExpression(ContractSepeStatus.PENDING.getValue());
			params.setSepeStatusInfo(sepeStatus);
		}
		if(map.get(ContractVariable.SS_MA.getValue())!=null){
			params.setSsStatusInfo(infoMap.get(ContractVariable.SS_MA.getValue()));
		} else {
			ContractInfo ssStatus = new ContractInfo();
			ssStatus.setContract(contract);
			ssStatus.setStartDate(contract.getStartDate());
			ssStatus.setEndDate(contract.getEndDate());
			ssStatus.setName( ContractVariable.SS_MA.getValue() );
			ssStatus.setExpression(ContractSsStatus.PENDING.getValue());
			params.setSsStatusInfo(ssStatus);
		} 
		
		if(map.get(ContractVariable.SEPE_CONTRACT_ID.getValue())!=null){
			params.setSepeContractId( map.get(ContractVariable.SEPE_CONTRACT_ID.getValue()) );
		}
		if(map.get(ContractVariable.DISABILITY_INDICATOR.getValue())!=null){
			params.setDisabilityIndicator( T55.getEnumByValue(map.get(ContractVariable.DISABILITY_INDICATOR.getValue())) );
		}
		if(map.get(ContractVariable.BONUS_REDUCTION_INDICATOR.getValue())!=null){
			params.setPartialTimeReductionIndicator( T53.getEnumByValue(map.get(ContractVariable.BONUS_REDUCTION_INDICATOR.getValue())) );
		}
	}
	
	public void loadContractBonuses(Contract contract, ContractParams params) throws ManagerBeanException {
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractBonus.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_BONUS_CONTRACT_ID), contract.getId());
			criteria.addOrder(bean.getFieldName(IEntityAlias.CONTRACT_BONUS_START_DATE));
			criteria.addOrder(bean.getFieldName(IEntityAlias.CONTRACT_BONUS_END_DATE));
			List<ITransferObject> list = bean.getList(criteria);
			params.setBonuses( list ); 
			params.setBonusModel(null);
		} catch (ManagerBeanException e) {
			String msg = "Error al cargar las bonificaciones del contrato";
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage(e.toString());
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
		} catch (ManagerBeanException e) {
			String msg = "Imposible eliminar los datos de contrato. (" +e.getMessage() + ")";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}

	public void removeContractInfo(Contract contract, ContractParams params) throws ControllerListenerException {
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractInfo.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_INFO_CONTRACT_ID), contract.getId());
			for(ITransferObject to: bean.getList(criteria)){
				ContractInfo data = (ContractInfo) to;
				bean.remove(data);
				
			}
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
				if(agreement==null){
					EnterpriseParamsController params = (EnterpriseParamsController) AonUtil.getRegisteredBean(ICompanyConstants.ENTERPRISE_PARAMS_CONTROLLER_NAME);
					params.loadParameters();
					agreement = params.getAgreement();
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
	
	public boolean isTrainingContract(Contract contract, ContractCode contractCode){
		return contractCode!=null && contractCode==ContractCode.C421;
	}
	public boolean isTrainingContract(Contract contract, ContractParams params){
		String contractCode = null;
		if(params.getContractCode()!=null){
			contractCode = params.getContractCode();
		}
		return contractCode!=null && ContractCode.getContractCodeByValue(contractCode)==ContractCode.C421;
	}
	
	public Map<String, String> getContractDataMap(Contract contract) {
		return SEPEUtils.getInstance().getContractDataMap(contract);
	}

	public Map<String, String> getContractInfoMap(Contract contract) {
		return SEPEUtils.getInstance().getContractInfoMap(contract);
	}
	
	public String getDataCurrentValue(Contract contract, String valueName) {
		return SEPEUtils.getInstance().getDataCurrentValue(contract, valueName);
	}
	public String getInfoCurrentValue(Contract contract, String valueName) {
		return SEPEUtils.getInstance().getInfoCurrentValue(contract, valueName);
	}
	
	public static List<ITransferObject> getContractWorkdayHours(Contract contract) throws ManagerBeanException{
		String[] varList = {ContextVariable.MONDAY_HOURS.getName(),
				ContextVariable.TUESDAY_HOURS.getName(), ContextVariable.WEDNESDAY_HOURS.getName(),
				ContextVariable.THURSDAY_HOURS.getName(), ContextVariable.FRIDAY_HOURS.getName(),
				ContextVariable.SATURDAY_HOURS.getName(), ContextVariable.SUNDAY_HOURS.getName() };
		
		IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_CONTRACT_ID), contract.getId());
		criteria.addGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_START_DATE), contract.getStartDate());
		if(contract.getEndDate()!=null){
			Expression endNull = ExpressionUtilities.getNullExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_END_DATE));
			Expression endGTstart = ExpressionUtilities.getGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_END_DATE), contract.getStartDate());
			criteria.addExpression(ExpressionUtilities.getOrExpression(endNull, endGTstart));
		}
		criteria.addInExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_NAME), varList);
		criteria.addOrder(bean.getFieldName(IEntityAlias.CONTRACT_DATA_START_DATE), false);
		return bean.getList(criteria);
	}
	
	public static String getMonthName(Date date, Locale locale){
		return Month.getMonthByValue(CommonUtil.getMonth(date)).getName(locale);
	}
	
	public static Double getContractTotalMonthHours(Integer salaryId){
		Double total = new Double(0.0);
		for(MonthHours mh: getContractMonthHours(salaryId)){
			if(mh.getHours()!=null){
				total += mh.getHours();
			}
			if(mh.getExtraHours()!=null){
				total += mh.getExtraHours();
			}
		}
		return total;
	}
	
	public static List<MonthHours> getContractMonthHours(Integer salaryId){
		List<MonthHours> list = new LinkedList<MonthHours>();
		try {
			IManagerBean bean = BeanManager.getManagerBean(Salary.class);
			Salary salary = (Salary) bean.get(salaryId);
			Calendar endDate = Calendar.getInstance();
			endDate.setTime(salary.getEndDate());
			endDate.set(Calendar.DAY_OF_MONTH, endDate.getActualMaximum(Calendar.DAY_OF_MONTH));
			Calendar date = Calendar.getInstance();
			date.setTime(salary.getEndDate());
			date.set(Calendar.DAY_OF_MONTH, date.getActualMinimum(Calendar.DAY_OF_MONTH));
			
			List<SalaryData> mondayHoursList = SEPEUtils.getInstance().getSalaryDataList(salary, salary.getStartDate(), salary.getEndDate(), ContextVariable.MONDAY_HOURS.getName());
			List<SalaryData> tuesdayHoursList = SEPEUtils.getInstance().getSalaryDataList(salary, salary.getStartDate(), salary.getEndDate(), ContextVariable.TUESDAY_HOURS.getName());
			List<SalaryData> wednesdayHoursList = SEPEUtils.getInstance().getSalaryDataList(salary, salary.getStartDate(), salary.getEndDate(), ContextVariable.WEDNESDAY_HOURS.getName());
			List<SalaryData> thursdayHoursList = SEPEUtils.getInstance().getSalaryDataList(salary, salary.getStartDate(), salary.getEndDate(), ContextVariable.THURSDAY_HOURS.getName());
			List<SalaryData> fridayHoursList = SEPEUtils.getInstance().getSalaryDataList(salary, salary.getStartDate(), salary.getEndDate(), ContextVariable.FRIDAY_HOURS.getName());
			List<SalaryData> saturdayHoursList = SEPEUtils.getInstance().getSalaryDataList(salary, salary.getStartDate(), salary.getEndDate(), ContextVariable.SATURDAY_HOURS.getName());
			List<SalaryData> sundayHoursList = SEPEUtils.getInstance().getSalaryDataList(salary, salary.getStartDate(), salary.getEndDate(), ContextVariable.SUNDAY_HOURS.getName());
			while(!date.after(endDate)){
				if(date.get(Calendar.DAY_OF_WEEK)==Calendar.MONDAY){
					Double monday = obtainHours(mondayHoursList, date.getTime());
					list.add(new MonthHours(isContractEffectiveDate(salary.getContract(), date.getTime())?monday:0, null));
				} else if(date.get(Calendar.DAY_OF_WEEK)==Calendar.TUESDAY){
					Double tuesday = obtainHours(tuesdayHoursList, date.getTime());
					list.add(new MonthHours(isContractEffectiveDate(salary.getContract(), date.getTime())?tuesday:0, null));
				} else if(date.get(Calendar.DAY_OF_WEEK)==Calendar.WEDNESDAY){
					Double wednesday = obtainHours(wednesdayHoursList, date.getTime());
					list.add(new MonthHours(isContractEffectiveDate(salary.getContract(), date.getTime())?wednesday:0, null));
				} else if(date.get(Calendar.DAY_OF_WEEK)==Calendar.THURSDAY){
					Double thursday = obtainHours(thursdayHoursList, date.getTime());
					list.add(new MonthHours(isContractEffectiveDate(salary.getContract(), date.getTime())?thursday:0, null));
				} else if(date.get(Calendar.DAY_OF_WEEK)==Calendar.FRIDAY){
					Double friday = obtainHours(fridayHoursList, date.getTime());
					list.add(new MonthHours(isContractEffectiveDate(salary.getContract(), date.getTime())?friday:0, null));
				} else if(date.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY){
					Double saturday = obtainHours(saturdayHoursList, date.getTime());
					list.add(new MonthHours(isContractEffectiveDate(salary.getContract(), date.getTime())?saturday:0, null));
				} else if(date.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY){
					Double sunday = obtainHours(sundayHoursList, date.getTime());
					list.add(new MonthHours(isContractEffectiveDate(salary.getContract(), date.getTime())?sunday:0, null));
				}
				date.add(Calendar.DAY_OF_MONTH, 1);
			}
		} catch (ManagerBeanException e) {
			// do nothing ...
		}
		return list;
	}
	
	private static Double obtainHours(List<SalaryData> hoursList, Date date) {
		Double hours = 0.0;
		hours = hoursList.stream()
				.filter(sd -> (sd.getStartDate().before(date) || sd.getStartDate().equals(date))
						&& (sd.getEndDate() == null || sd.getEndDate().after(date) || sd.getEndDate().equals(date)))
						.map(o -> o.getExpression()).mapToDouble(NumberUtils::toDouble).sum();
		return hours;
	}

	private static boolean isContractEffectiveDate(Contract contract, Date date){
		return (date.after(contract.getStartDate()) || date.equals(contract.getStartDate())) 
				&& (contract.getEndDate()==null || date.before(contract.getEndDate()) || date.equals(contract.getEndDate())) ;
	}

	public static class MonthHours {
		private Double hours;
		private Double extraHours;
		public MonthHours (Double hours, Double extraHours){
			this.hours = hours;
			this.extraHours = extraHours;
		}
		public Double getHours() {
			return hours;
		}
		public void setHours(Double hours) {
			this.hours = hours;
		}
		public Double getExtraHours() {
			return extraHours;
		}
		public void setExtraHours(Double extraHours) {
			this.extraHours = extraHours;
		}
	}
	
}
