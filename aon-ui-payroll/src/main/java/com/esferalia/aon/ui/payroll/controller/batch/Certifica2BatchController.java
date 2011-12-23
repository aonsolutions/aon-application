package com.esferalia.aon.ui.payroll.controller.batch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.company.Enterprise;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.Certifica2Batch;
import com.esferalia.aon.payroll.Certifica2BatchAttachment;
import com.esferalia.aon.payroll.Certifica2BatchData;
import com.esferalia.aon.payroll.Certifica2BatchDetail;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.enumeration.Certifica2BatchAttachmentType;
import com.esferalia.aon.payroll.enumeration.ContractStatus;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.FileStatus;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.controller.batch.Certifica2ListController.RemesableContract;
import com.esferalia.aon.ui.payroll.file.CertificateWriter;


public class Certifica2BatchController extends BasicController {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(Certifica2BatchController.class);
	
	private CertificateWriter certificateWriter;
	private FileOutput fileOutput;
	private boolean recorded;
	
	private Enterprise enterprise;

	public Enterprise getEnterprise() {
		try {
			if(enterprise == null){
				IManagerBean bean = BeanManager.getManagerBean(Enterprise.class);
				enterprise = (Enterprise) bean.createNewTo();
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> error on getEnterprise: ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
		return enterprise;
	}

	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}
	
	private CertificateWriter getCertificateWriter() {
		if (certificateWriter == null) {
			certificateWriter = new CertificateWriter();
		}
		return certificateWriter;
	}
	
	public FileOutput getFileOutput() {
		return fileOutput;
	}

	public void setFileOutput(FileOutput fileOutput) {
		this.fileOutput = fileOutput;
	}
	
	public boolean isRecorded() {
		return recorded;
	}

	public void setRecorded(boolean recorded) {
		this.recorded = recorded;
	}

	@SuppressWarnings("unchecked")
	public void onBatchSelected(ActionEvent event) throws ManagerBeanException {
        IManagerBean contractBean = BeanManager.getManagerBean(Contract.class);
		IManagerBean certifica2BatchDetailBean = BeanManager.getManagerBean(Certifica2BatchDetail.class);
		Certifica2ListController listController = (Certifica2ListController) FormUtil.getController(IPayrollConstants.CERTIFICA2_LIST_CONTROLLER_NAME);
        checkAllSuspensionCauses(listController.getRemesableContracts(), listController.getCheckHandler().getCheckedList());
		Iterator iterator = listController.getCheckHandler().getCheckedList().iterator();
		for(Certifica2ListController.RemesableContract remesable: listController.getRemesableContracts().values()){
			Contract contract = (Contract) iterator.next();
            contract.setStatus(ContractStatus.BATCHED);
            contractBean.update(contract);
			Certifica2BatchDetail certifica2BatchDetail = new Certifica2BatchDetail();
			certifica2BatchDetail.setContract(remesable.getContract());
			certifica2BatchDetail.setCcc(remesable.getContract().getEnterpriseCCC().getCcc());
			certifica2BatchDetail.setCertifica2Batch((Certifica2Batch) getTo());
			certifica2BatchDetail.setSuspensionCause(remesable.getSuspensionCause());
			certifica2BatchDetail.setEnterpriseNif(remesable.getContract().getWorkPlace().getEnterprise().getRegistry().getDocument());
			certifica2BatchDetail.setCcc(remesable.getContract().getEnterpriseCCC().getCcc());
			certifica2BatchDetail.setDocument(remesable.getContract().getPerson().getRegistry().getDocument());
			String name = remesable.getContract().getPerson().getName();
			certifica2BatchDetail.setName(name.length()>9?name.substring(0, 8):name);
			certifica2BatchDetail.setFirstSurname(remesable.getContract().getPerson().getFirstSurname());
			certifica2BatchDetail.setSecondSurname(remesable.getContract().getPerson().getSecondSurname());
			certifica2BatchDetail.setSsNumber(remesable.getContract().getPerson().getSocialSecurityNumber());
			certifica2BatchDetail.setQuoteGroup(getContractDataMap(remesable.getContract()).get(ContextVariable.QUOTE_GROUP.getName()));
			certifica2BatchDetail.setContractType(getContractDataMap(remesable.getContract()).get(ContextVariable.TC2.getName()));
			certifica2BatchDetail.setContractDuration(differenceBetweenDates(remesable.getContract().getStartDate(), remesable.getContract().getEndDate()).toString());
//			detalle.setContractDurationIndicator;
			certifica2BatchDetail.setOccupationCode(getContractDataMap(remesable.getContract()).get(ContextVariable.CNO.getName()));
//			detalle.setPublicAssociationCharge;
//			detalle.setDedicationPercent;
			certifica2BatchDetail.setEnterpriseStartDate(remesable.getContract().getStartDate());
			certifica2BatchDetail.setExpireDate(remesable.getContract().getEndDate());
//			detalle.setExpireEndDate;
//			detalle.setEre;
//			detalle.setEreReductionPercent;
//			detalle.setOtherReductionPercent;
//			detalle.setReductionCauseCode;
//			detalle.setSalaryPeriodStartDate;
//			detalle.setSalaryPeriodEndDate;
			certifica2BatchDetail.setSalaryProcessingDays("00000");
			certifica2BatchDetailBean.insert(certifica2BatchDetail);
		}
        listController.getCheckHandler().clearCheckedList();
        listController.getRemesableContracts().clear();
        loadDetails();
        onSearchContracts(event);
	}

	protected Map<String, String> getContractDataMap(Contract contract) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_CONTRACT_ID), contract.getId());
			criteria.addNotNullExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_END_DATE));
			for(ITransferObject to: bean.getList(criteria)){
				ContractData data = (ContractData) to;
				if(data.getExpression()!=null){
					map.put(data.getName(), data.getExpression().replace('"', ' ').trim());
				}
			}
		} catch (ManagerBeanException e) {
			// NADA, que siga generando el fichero
		}
		return map;
	}
	
	protected Integer differenceBetweenDates(Date from, Date to) {
		Integer diffDays = new Integer(0);
		final Double MS_PER_DAY = new Double(1000 * 60 * 60 * 24);
		if(from.before(to)) {
			diffDays = (int)((Math.floor((to.getTime() - from.getTime()) / MS_PER_DAY + 0.5d) + 1));
		}
		return diffDays;
	}
	
	private void checkAllSuspensionCauses(
			Map<Integer, RemesableContract> remesableContracts,
			ArrayList<Object> checkedList) {
		if(checkedList.size()!=remesableContracts.size()){
			String message = "Debe seleccionar la causa de suspension de los empleados seleccionados.";
			AonUtil.addErrorMessage(message);
			throw new AbortProcessingException(message);
		}
	}

	@SuppressWarnings("unchecked")
	public void onRemoveSelected(ActionEvent event) throws ManagerBeanException {
		IManagerBean certifica2BatchDetailBean = BeanManager.getManagerBean(Certifica2BatchDetail.class);
		IManagerBean contractBean = BeanManager.getManagerBean(Contract.class);
        BatchDetailController certifica2BatchDetailController = (BatchDetailController)FormUtil.getController(IPayrollConstants.CERTIFICA2_BATCH_DETAIL_CONTROLLER_NAME);
		Iterator iterator = certifica2BatchDetailController.getCheckHandler().getCheckedList().iterator();
        while(iterator.hasNext()){
        	Certifica2BatchDetail certifica2BatchDetail = (Certifica2BatchDetail) iterator.next();
        	certifica2BatchDetail.getContract().setStatus(ContractStatus.PENDING);
        	contractBean.update(certifica2BatchDetail.getContract());
        	certifica2BatchDetailBean.remove(certifica2BatchDetail);
        }
        certifica2BatchDetailController.getCheckHandler().clearCheckedList();
        loadDetails();
        onSearchContracts(event);
    }
	
	private void loadDetails() {
        LinesController batchDetailController = (LinesController)FormUtil.getController(IPayrollConstants.CERTIFICA2_BATCH_DETAIL_CONTROLLER_NAME);
        batchDetailController.onSearch(null);
    }
	
	public void onSearchContracts(ActionEvent event) throws ManagerBeanException {
		Certifica2ListController list = (Certifica2ListController) FormUtil.getController(IPayrollConstants.CERTIFICA2_LIST_CONTROLLER_NAME);
		list.clearCriteria();
		list.onSearch(event);
	}
	
	public void onEditSearchList(ActionEvent event) throws ManagerBeanException {
		Certifica2ListController list = (Certifica2ListController) FormUtil.getController(IPayrollConstants.CERTIFICA2_LIST_CONTROLLER_NAME);
		list.clearCriteria();
		list.onEditSearch(event);
	}
	
	@Override
	public void onSelect(ActionEvent event) {
		super.onSelect(event);
		onInit(event);
	}
	
	@Override
	public void onAccept(ActionEvent event) {
		Certifica2Batch b = (Certifica2Batch) getTo();
		b.setStatus(FileStatus.PENDING);
		super.onAccept(event);
		try {
			onSearchContracts(event);
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("error on onAccept ["+e.getMessage()+"]");
		}
	}
	
	public void onInit(ActionEvent event) {
		try {
			onSearchContracts(event);
			checkDiskCreated();
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("error on onInit ["+e.getMessage()+"]");
		}
	}

	@Override
	public void onReset(ActionEvent event) {
		setRecorded(false);
		super.onReset(event);
		Certifica2Batch b = (Certifica2Batch) getTo();
		b.setStatus(FileStatus.PENDING);
	}

	public void onCreateDisk(ActionEvent event) {
		try {
//			String loggedUser = AonUtil.getRemoteUser();
//			loggedUser = StringUtils.substringBefore(loggedUser, "@");
			for (Certifica2BatchDetail d : getCertifica2DetailList()) {
				for (Certifica2BatchData data : getDetailDataList(d)) {
					data.setCertifica2BatchDetail(d);
					IManagerBean bean = BeanManager.getManagerBean(Certifica2BatchData.class);
					Certifica2BatchData cbd = (Certifica2BatchData) data;
					bean.insertOrUpdate(cbd);
				}
			}
			File file = getCertificateWriter().createCertificate((Certifica2Batch)getTo(), getCertifica2DetailList()).getFile();
			IManagerBean bean = BeanManager.getManagerBean(Certifica2BatchAttachment.class);
			if (file != null) {
				FileInputStream in = new FileInputStream(file);
				byte[] data = IOUtils.toByteArray(in);
				Certifica2BatchAttachment attach;
				attach = new Certifica2BatchAttachment();
				attach.setCertifica2Batch((Certifica2Batch) getTo());
				attach.setMimeType(MimeType.MIME_TXT);
				attach.setDescription(getCertificateWriter().getCertificate().getFile());
				attach.setSize(null);
				attach.setAttachmentType(Certifica2BatchAttachmentType.CERTIFICA2_DOCUMENT);
				attach.setScope(null);
				attach.setData(data);
				attach.setAttachDate(new Date());
				bean.insertOrUpdate(attach);
				setRecorded(true);
				changeBatchStatus(FileStatus.GENERATED);
				Certifica2BatchAttachController controller = (Certifica2BatchAttachController) FormUtil.getController("certifica2BatchAttach");
				controller.initializeModel();
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("error on generateCertifica2File ["+e.getMessage()+"]");
		} catch (FileNotFoundException e) {
			AonUtil.addErrorMessage("error on generateCertifica2File ["+e.getMessage()+"]");
		} catch (IOException e) {
			AonUtil.addErrorMessage("error on generateCertifica2File ["+e.getMessage()+"]");
		}
	}
	
	private List<Certifica2BatchData> getDetailDataList(Certifica2BatchDetail detail) {
		ISalary nomina = null;
		List<Certifica2BatchData> cotizacionList = null;
		Integer totalDias = 0;
		Calendar calInicio = new GregorianCalendar();
		Calendar calFin = new GregorianCalendar();
		calInicio.setTime(detail.getContract().getStartDate());
		calFin.setTime(detail.getContract().getEndDate());
		calFin.set(Calendar.DAY_OF_MONTH, calFin.getActualMaximum(Calendar.DAY_OF_MONTH));
		cotizacionList = new ArrayList<Certifica2BatchData>();
		while((calInicio.before(calFin) || calInicio.equals(calFin)) && totalDias < 180) {
			Calendar sDate = new GregorianCalendar();
			Calendar eDate = new GregorianCalendar();
			sDate.setTime(new Date(calFin.getTimeInMillis()));
			eDate.setTime(new Date(calFin.getTimeInMillis()));
			sDate.set(Calendar.DAY_OF_MONTH, 1);
			eDate.set(Calendar.DAY_OF_MONTH, sDate.getActualMaximum(Calendar.DAY_OF_MONTH));
			nomina = getCurrentSalary(detail.getContract(), SalaryType.SALARY, sDate.getTime(), eDate.getTime());
			calFin.add(Calendar.DATE, -calFin.get(Calendar.DAY_OF_MONTH));
			if(nomina != null) {
				Double baseCg = nomina.getCommonBase();
				Double baseAcc = nomina.getProfessionalBase();
				//TODO obtener la base por desempleo
//				Double baseDesempleo = nomina.getBasePerdes(); 
				Double baseDesempleo = nomina.getIrpfBase();
				// TODO obtener las nomina diferencia
//				List<INominaDiferencia> nominasDiferencia = getNominaDAO().getNominasDiferencia(params);
//				for(INominaDiferencia nomDf:nominasDiferencia) {
//					baseCg += nomDf.getBaseCgPts();
//					baseAcc += nomDf.getBaseAccPts();
//					baseDesempleo += nomDf.getBasePerdes();
//				}
				if ((nomina.getOvertimeBase() == null || nomina.getOvertimeBase() == 0)
						&& (nomina.getNonEstructuralOvertimeBase() == null || nomina.getNonEstructuralOvertimeBase() == 0)) {
					baseDesempleo = baseAcc;
				} 
				totalDias += nomina.getTimeUnits();
				Certifica2BatchData cotizacion = new Certifica2BatchData();
				Calendar cal = new GregorianCalendar();
				cal.setTime(nomina.getEndDate());
				cotizacion.setYear(cal.get(Calendar.YEAR));
				cotizacion.setMonth(cal.get(Calendar.MONTH)+1);
				cotizacion.setContributionDays(nomina.getTimeUnits());
				cotizacion.setCgcContributionBase(baseCg);
				cotizacion.setUnemploymentContributionBase(baseDesempleo);
				cotizacion.setComments(null);
				cotizacionList.add(cotizacion);
			}
		}
		return cotizacionList;
	}
	
	private Salary getCurrentSalary(Contract contract, SalaryType type, Date startDate, Date endDate){
		try {
			IManagerBean bean = BeanManager.getManagerBean(Salary.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SALARY_CONTRACT_ID), contract.getId());
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SALARY_TYPE), type);
			criteria.addGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.SALARY_START_DATE), startDate);
			criteria.addLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.SALARY_END_DATE), endDate);
			
			List<ITransferObject> salaryList = bean.getList(criteria);
			if(!salaryList.isEmpty()){
				return (Salary) salaryList.get(0);
			} 
		} catch (ManagerBeanException e) {
			// NADA, que siga generando el fichero
		} 
		return null;
	}
	
	public void changeBatchStatus(FileStatus status) {
		Certifica2Batch b = (Certifica2Batch) getTo();
		if(b != null){
			b.setStatus(status);
			super.accept(null);
		}
	}

	private void checkDiskCreated() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Certifica2BatchAttachment.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CERTIFICA2BATCH_ATTACHMENT_CERTIFICA2BATCH_ID), ((Certifica2Batch)getTo()).getId());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CERTIFICA2BATCH_ATTACHMENT_ATTACHMENT_TYPE), Certifica2BatchAttachmentType.CERTIFICA2_DOCUMENT);
		List<ITransferObject> list = bean.getList(criteria);
		if(!list.isEmpty()){
			setRecorded(true);
		} else {
			setRecorded(false);
		}
	}

	private List<Certifica2BatchDetail> getCertifica2DetailList() {
		LinesController controller = (LinesController)FormUtil.getController(IPayrollConstants.CERTIFICA2_BATCH_DETAIL_CONTROLLER_NAME);
		List<Certifica2BatchDetail> list = new LinkedList<Certifica2BatchDetail>();
		for(ITransferObject to: controller.getWrappedList()){
			Certifica2BatchDetail detail = (Certifica2BatchDetail) to;
			list.add(detail);
		}
		return list;
	}
	
	@Override
	public void onSearch(ActionEvent event) {
		try {
			if ((getEnterprise() != null) && (getEnterprise().getId() != null)) {
				getCriteria().addEqualExpression(getFieldName(IEntityAlias.CERTIFICA2BATCH_ENTERPRISE_ID), getEnterprise().getId());			
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onSearch exception: ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
		super.onSearch(event);
	}
	
	

}
