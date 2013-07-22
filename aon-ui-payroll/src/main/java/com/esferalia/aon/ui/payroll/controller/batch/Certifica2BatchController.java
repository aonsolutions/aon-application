package com.esferalia.aon.ui.payroll.controller.batch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.io.IOUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.Certifica2Batch;
import com.esferalia.aon.payroll.Certifica2BatchAttachment;
import com.esferalia.aon.payroll.Certifica2BatchData;
import com.esferalia.aon.payroll.Certifica2BatchDetail;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractStatus;
import com.esferalia.aon.payroll.enumeration.FileStatus;
import com.esferalia.aon.payroll.enumeration.PayrollBatchAttachmentType;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.controller.batch.Certifica2ListController.RemesableContract;
import com.esferalia.aon.ui.payroll.file.CertificateWriter;
import com.esferalia.aon.ui.payroll.utils.PayrollUtils;

public class Certifica2BatchController extends BasicController {

	private CertificateWriter certificateWriter;
	private FileOutput fileOutput;
	private boolean recorded;
	private PayrollUtils utils;
	
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

	public PayrollUtils getUtils() {
		if(utils==null){
			utils = new PayrollUtils();
		}
		return utils;
	}

	public void setUtils(PayrollUtils utils) {
		this.utils = utils;
	}

	public void onBatchSelected(ActionEvent event) throws ManagerBeanException {
        IManagerBean contractBean = BeanManager.getManagerBean(Contract.class);
		IManagerBean certifica2BatchDetailBean = BeanManager.getManagerBean(Certifica2BatchDetail.class);
		Certifica2ListController listController = (Certifica2ListController) FormUtil.getController(IPayrollConstants.CERTIFICA2_LIST_CONTROLLER_NAME);
        checkAllSuspensionCauses(listController.getRemesableContracts(), listController.getCheckHandler().getCheckedList());
		Iterator<Object> iterator = listController.getCheckHandler().getCheckedList().iterator();
		
		PayrollUtils utils = new PayrollUtils(); 
		
		for(Certifica2ListController.RemesableContract remesable: listController.getRemesableContracts().values()){
			Map<String, String> contractData = utils.getContractDataMap(remesable.getContract());
			
			Certifica2BatchDetail certifica2BatchDetail = new Certifica2BatchDetail();
			certifica2BatchDetail.setContract(remesable.getContract());
			certifica2BatchDetail.setCcc(remesable.getContract().getEnterpriseCCC().getCcc());
			certifica2BatchDetail.setCertifica2Batch((Certifica2Batch) getTo());
			certifica2BatchDetail.setSuspensionCause(remesable.getSuspensionCause());
			certifica2BatchDetail.setEnterpriseNif(remesable.getContract().getWorkPlace().getEnterprise().getRegistry().getDocument());
			certifica2BatchDetail.setDocument(remesable.getContract().getPerson().getRegistry().getDocument());
			String name = remesable.getContract().getPerson().getName();
			certifica2BatchDetail.setName(name.length()>9?name.substring(0, 8):name);
			certifica2BatchDetail.setFirstSurname(remesable.getContract().getPerson().getFirstSurname());
			certifica2BatchDetail.setSecondSurname(remesable.getContract().getPerson().getSecondSurname());
			certifica2BatchDetail.setSsNumber(remesable.getContract().getPerson().getSocialSecurityNumber());
			String quoteGroup = contractData.get(ContextVariable.QUOTE_GROUP.getName());
			certifica2BatchDetail.setQuoteGroup(quoteGroup!=null?quoteGroup:null);
			String tc2 = contractData.get(ContextVariable.TC2.getName());
			if(tc2!=null){
				certifica2BatchDetail.setContractType(tc2);
			} else {
				String msg = "El contrato de "+remesable.getContract().getPerson().getFullName()+" no dispone de datos vigentes para el TC2";
				AonUtil.addErrorMessage(msg);
			}
			certifica2BatchDetail.setContractDuration(differenceBetweenDates(remesable.getContract().getStartDate(), remesable.getContract().getEndDate()).toString());
			certifica2BatchDetail.setContractDurationIndicator(null);
			String occupation = contractData.get(ContextVariable.CNO.getName());
			if(occupation!=null){
				certifica2BatchDetail.setOccupationCode(occupation);
			} else {
				String msg = "El contrato de "+remesable.getContract().getPerson().getFullName()+" no dispone de datos vigentes para el CNO";
				AonUtil.addErrorMessage(msg);
			}
			certifica2BatchDetail.setPublicAssociationCharge(null);
			certifica2BatchDetail.setDedicationPercent(null);
			certifica2BatchDetail.setEnterpriseStartDate(remesable.getContract().getStartDate());
			certifica2BatchDetail.setExpireDate(remesable.getContract().getEndDate());
			certifica2BatchDetail.setExpireEndDate(null);
			certifica2BatchDetail.setEre(null);
			certifica2BatchDetail.setEreReductionPercent(null);
			certifica2BatchDetail.setOtherReductionPercent(null);
			certifica2BatchDetail.setReductionCauseCode(null);
			certifica2BatchDetail.setSalaryPeriodStartDate(null);
			certifica2BatchDetail.setSalaryPeriodEndDate(null);
			certifica2BatchDetail.setSalaryProcessingDays("00000");
			certifica2BatchDetailBean.insert(certifica2BatchDetail);

			Contract contract = (Contract) iterator.next();
			contract.setStatus(ContractStatus.BATCHED);
			contractBean.update(contract);
		}
        listController.getCheckHandler().clearCheckedList();
        listController.getRemesableContracts().clear();
        loadDetails();
        onSearchContracts(event);
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

	public void onRemoveSelected(ActionEvent event) throws ManagerBeanException {
		IManagerBean certifica2BatchDetailBean = BeanManager.getManagerBean(Certifica2BatchDetail.class);
		IManagerBean contractBean = BeanManager.getManagerBean(Contract.class);
        BatchDetailController certifica2BatchDetailController = (BatchDetailController)FormUtil.getController(IPayrollConstants.CERTIFICA2_BATCH_DETAIL_CONTROLLER_NAME);
		Iterator<Object> iterator = certifica2BatchDetailController.getCheckHandler().getCheckedList().iterator();
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
	
	public void onInit(ActionEvent event) {
		try {
			onSearchContracts(event);
			checkDiskCreated();
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("error on onInit ["+e.getMessage()+"]");
		}
	}

	public void onCreateDisk(ActionEvent event) {
		try {
			
			Certifica2Batch batch = (Certifica2Batch)getTo();
			
			List<ITransferObject> detailList = getCertifica2DetailList((Certifica2Batch) this.getTo());
			
			for (ITransferObject to: detailList) {
				Certifica2BatchDetail detail = (Certifica2BatchDetail) to;
				for (Certifica2BatchData data : getDetailDataList(detail)) {
					data.setCertifica2BatchDetail(detail);
					IManagerBean bean = BeanManager.getManagerBean(Certifica2BatchData.class);
					Certifica2BatchData cbd = (Certifica2BatchData) data;
					bean.insertOrUpdate(cbd);
				}
			}
			
			File file = getCertificateWriter().createFile(batch, detailList);
			
			IManagerBean bean = BeanManager.getManagerBean(Certifica2BatchAttachment.class);
			if (file != null) {
				FileInputStream in = new FileInputStream(file);
				byte[] data = IOUtils.toByteArray(in);
				Certifica2BatchAttachment attach;
				attach = new Certifica2BatchAttachment();
				attach.setCertifica2Batch((Certifica2Batch) getTo());
				attach.setMimeType(MimeType.MIME_XML);
				attach.setDescription(getCertificateWriter().getFileName());
				attach.setSize(null);
				attach.setAttachmentType(PayrollBatchAttachmentType.GENERATED_DOCUMENT);
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
			nomina = getUtils().getSalary(detail.getContract(),  sDate.getTime(), eDate.getTime());
			calFin.add(Calendar.DATE, -calFin.get(Calendar.DAY_OF_MONTH));
			if(nomina != null) {
				Double baseCg = nomina.getCommonBase();
				Double baseAcc = nomina.getProfessionalBase();
				//TODO obtener la base por desempleo
//				Double baseDesempleo = nomina.getBasePerdes(); 
				Double baseDesempleo = nomina.getIrpfBase();
				// TODO obtener las nominas diferencia
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
	
	public void changeBatchStatus(FileStatus status) {
		Certifica2Batch b = (Certifica2Batch) getTo();
		if(b != null){
			b.setStatus(status);
			super.accept(null);
		}
	}

	private void checkDiskCreated() throws ManagerBeanException {
		LinesController controller = (LinesController)FormUtil.getController(IPayrollConstants.CERTIFICA2_BATCH_ATTACH_CONTROLLER_NAME);
		if(controller.getRowCount()>0){
			setRecorded(true);
		} else {
			setRecorded(false);
		}
	}
	
	private List<ITransferObject> getCertifica2DetailList(Certifica2Batch batch) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Certifica2BatchDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CERTIFICA2BATCH_DETAIL_CERTIFICA2BATCH_ID), batch.getId());
			return bean.getList(criteria);
		} catch (ManagerBeanException e) {
			// NADA, que siga con la generacion del fichero
		}
		return null;
	}

}
