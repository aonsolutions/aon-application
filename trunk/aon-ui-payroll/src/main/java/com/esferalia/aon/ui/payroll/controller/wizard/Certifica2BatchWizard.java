package com.esferalia.aon.ui.payroll.controller.wizard;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.company.Enterprise;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.Certifica2Batch;
import com.esferalia.aon.payroll.Certifica2BatchData;
import com.esferalia.aon.payroll.Certifica2BatchDetail;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.FileStatus;
import com.esferalia.aon.payroll.enumeration.SuspensionCause;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.controller.contract.ContractController;

public class Certifica2BatchWizard extends Certifica2Factory implements Serializable {

	private static final long serialVersionUID = -2856413860699968619L;
	private static final Logger LOGGER = LoggerFactory.getLogger(Certifica2BatchWizard.class);
//	private static final String QUOTE_GROUP = "GRUPO_COTIZACION";
//	private static final String TC2_CODE = "TC2";

	private int currentStep;
	private static final String[] STEPS = {
			"certifica2BatchWizard_step0",
			"certifica2BatchWizard_step1",
			"certifica2BatchWizard_step2" };
	private DataModel model;
	private List<Certifica2Batch> savedRemesas;
	private boolean remesable;
	private SuspensionCause suspensionCauseForAll;
	private List<RemesableCertificate> remesasList;
	private DataModel remesasModel;
	private Enterprise enterprise;
	
	
	public Enterprise getEnterprise() {
		return enterprise;
	}
	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}

	public DataModel getRemesasModel() {
		remesasModel = new ListDataModel(getRemesasList());
		return remesasModel;
	}

	public void setRemesasModel(DataModel remesasModel) {
		this.remesasModel = remesasModel;
	}
	
	public List<RemesableCertificate> getRemesasList() {
		return remesasList;
	}

	public void setRemesasList(List<RemesableCertificate> remesasList) {
		this.remesasList = remesasList;
	}

	public SuspensionCause getSuspensionCauseForAll() {
		return suspensionCauseForAll;
	}

	public void setSuspensionCauseForAll(SuspensionCause suspensionCauseForAll) {
		this.suspensionCauseForAll = suspensionCauseForAll;
	}

	public boolean isRemesable() {
		return remesable;
	}

	public void setRemesable(boolean remesable) {
		this.remesable = remesable;
	}
	
	public List<Certifica2Batch> getSavedRemesas() {
		if (savedRemesas == null) {
			savedRemesas = new ArrayList<Certifica2Batch>();
		}
		return savedRemesas;
	}
	
	public void setSavedRemesas(List<Certifica2Batch> savedRemesas) {
		this.savedRemesas = savedRemesas;
	}

	public int getCurrentStep() {
		return this.currentStep;
	}

	public void setCurrentStep(int currentStep) {
		this.currentStep = currentStep;
	}

	public DataModel getModel() {
		if (model == null) {
			model = new ListDataModel();
		}
		return model;
	}
	
	public void setModel(DataModel model) {
		this.model = model;
	}

	private boolean isSuspensionCauseSelected(RemesableEmpleadoCertificate remesable) {
		return remesable.getSuspensionCause()!=null;
	}
	
	private void initializeModel(List<ITransferObject> list) {
		List<RemesableEmpleadoCertificate> rList = transformList(list);
		setModel(new ListDataModel(rList));
	}

	private List<RemesableEmpleadoCertificate> transformList(List<ITransferObject> list) {
		List<RemesableEmpleadoCertificate> rList = new ArrayList<RemesableEmpleadoCertificate>();
		for (ITransferObject to : list) {
			RemesableEmpleadoCertificate r = new RemesableEmpleadoCertificate();
			r.setContract((Contract) to);
			rList.add(r);
		}
		return rList;
	}

	// Action Listeners
	public void onNext(ActionEvent event) {
		if (getCurrentStep() == 0) {
			onSearch(event);
			setCurrentStep(getCurrentStep() + 1);
		} else if (getCurrentStep() == 1) {
			onValidate(event);
			setCurrentStep(getCurrentStep() + 1);
		} else if (getCurrentStep() == 2) {
			onFinish(event);
		}
	}

	public void onPrevious(ActionEvent event) {
		if (getCurrentStep() == 1) {
			onContractEditSearch(event);
		}
		setCurrentStep(getCurrentStep() - 1);
	}

	public String previous() {
		return STEPS[getCurrentStep()];
	}

	public String next() {
		return STEPS[getCurrentStep()];
	}

	public boolean isPreviousAvailable() {
		return (getCurrentStep() > 0);
	}

	public boolean isNextAvailable() {
		return (getCurrentStep() < 2);
	}

	// ***************************************************
	public void onStart(ActionEvent event) {
		setModel(null);
		setRemesasList(null);
		setRemesable(true);
		setSuspensionCauseForAll(null);
		setCurrentStep(0);
	}

	private void onValidate(ActionEvent event) {
		if (!isAnyEmpleadoSelected()) {
			String msg = "Debe seleccionar algún empleado.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		generateRemesasList();
	}

	public void onContractEditSearch(ActionEvent event) {
		ContractController controller = (ContractController) AonUtil.getRegisteredBean(IPayrollConstants.CONTRACT_CONTROLLER);
		controller.onEditSearch(event);
	}
	
	@SuppressWarnings("unchecked")
	private void generateRemesasList() {
		List<RemesableEmpleadoCertificate> remesableEmpleadosList = new LinkedList<RemesableEmpleadoCertificate>();
		for (RemesableEmpleadoCertificate remesable : (List<RemesableEmpleadoCertificate>) getModel().getWrappedData()) {
			if (remesable.isSelected()) {
				if (!isSuspensionCauseSelected(remesable)) {
					String msg = "Debe seleccionar la causa de suspension de los empleados seleccionados.";
					AonUtil.addErrorMessage(msg);
					throw new AbortProcessingException(msg);
				}
				remesableEmpleadosList.add(remesable);
			}
		}
		setRemesasList(new LinkedList<RemesableCertificate>());
		RemesableCertificate remesa = null;
		for (RemesableEmpleadoCertificate remesable : remesableEmpleadosList) {
			RemesableCertificate searchedRemesa = searchRemesa(remesable);
			if(searchedRemesa==null){
				remesa = new RemesableCertificate();
				Certifica2Batch ec = new Certifica2Batch();
				ec.setEnterprise(remesable.getContract().getWorkPlace().getEnterprise());
				ec.setStatus(FileStatus.PENDING);
				remesa.setCertificate(ec);
				remesa.addEmployee(remesable, false);
				getRemesasList().add(remesa);
			} else {
				remesa = searchedRemesa;
				remesa.addEmployee(remesable, false);
				if(!isRemesaAdded(remesa)){
					getRemesasList().add(remesa);
					addExistingEmpleadosRemesa(remesa);
				}
			}
		}
	}

	private boolean isRemesaAdded(RemesableCertificate remesa) {
		for(RemesableCertificate rc: getRemesasList()){
			if(remesa.getCertificate().getId()!=null){
				if(rc.getCertificate().getId()!=null && rc.getCertificate().getId().equals(remesa.getCertificate().getId())){
					return true;
				}
			} else {
				if(rc.getCertificate().equals(remesa.getCertificate())){
					return true;
				}
			}
		}
		return false;
	}

	private boolean isEmpresaInRemesasList(Enterprise enterprise) {
		for(RemesableCertificate c: getRemesasList()){
			if(c.getEnterprise().getId().equals(enterprise.getId())){
				return true;
			}
		}
		return false;
	}
	
	private RemesableCertificate getExistingEmpresaRemesa(Enterprise enterprise, RemesableEmpleadoCertificate remesable) {
		for(RemesableCertificate c: getRemesasList()){
			if(c.getEnterprise().getId().equals(enterprise.getId()) && !c.existEmployee(remesable)){
				return c;
			}
		}
		return null;
	}
	
	private RemesableCertificate searchRemesa(RemesableEmpleadoCertificate remesable) {
		Certifica2Batch remesa = null;
		if (!isEmpresaInRemesasList(remesable.getContract().getWorkPlace().getEnterprise())) {
			List<Certifica2Batch> existingRemesas = getExistingRemesas(remesable.getContract());
			if(existingRemesas.size()>0){
				remesa = findRemesa(existingRemesas, remesable.getContract());
				if(remesa==null){
					remesa = new Certifica2Batch();
					remesa.setEnterprise(remesable.getContract().getWorkPlace().getEnterprise());
					remesa.setStatus(FileStatus.PENDING);
				}
			} else {
				return null;
			}
		} else {
			return getExistingEmpresaRemesa(remesable.getContract().getWorkPlace().getEnterprise(), remesable);
		}
		if(remesa!=null){
			RemesableCertificate remesaCertificate = new RemesableCertificate();
			remesaCertificate.setCertificate(remesa);
			return remesaCertificate;
		}
		return null;
	}

	private void addExistingEmpleadosRemesa(RemesableCertificate rc) {
		for(Certifica2BatchDetail detalle: getDetalleRemesaCertificados(rc.getCertificate())){
			RemesableEmpleadoCertificate remesable = new RemesableEmpleadoCertificate();
			remesable.setSuspensionCause(detalle.getSuspensionCause());
			remesable.setContract(detalle.getContract());
			rc.addEmployee(remesable, true);
		}
	}
	
	private Certifica2Batch findRemesa(List<Certifica2Batch> remesas, Contract contract) {
		for(Certifica2Batch remesa: remesas){
			if(!existEmpleadoInRemesa(remesa, contract)){
				if(contract.getEndDate().before(Calendar.getInstance().getTime())){
					if(remesa.getDate().before(Calendar.getInstance().getTime())){
						return remesa;
					}
				} else {
					if(contract.getEndDate().equals(new Date(remesa.getDate().getTime()-(1*24*60*60*1000)))){
						return remesa;
					}
				}
			}
		}
		return null;
	}

	private boolean existEmpleadoInRemesa(Certifica2Batch remesa, Contract contract) {
		for(Certifica2BatchDetail detalle: getDetalleRemesaCertificados(remesa)){
			if(detalle.getContract().getId().equals(contract.getId())){
				return true;
			}
		}
		return false;
	}
	
	public boolean isAnyEmpleadoSelected() {
		for (int i = 0; i < getModel().getRowCount(); i++) {
			getModel().setRowIndex(i);
			RemesableEmpleadoCertificate r = (RemesableEmpleadoCertificate) getModel().getRowData();
			if(r.isSelected()){
				return true;
			}
		}
		return false;
	}

	private void onFinish(ActionEvent event) {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			try {
				HibernateUtil.setBeginTransaction(false);
				HibernateUtil.setCloseSession(false);
				HibernateUtil.beginTransaction(sessionName);
				// BEGIN operaciones de la transaccion
				for (RemesableCertificate r : getRemesasList()) {
					Certifica2Batch remesa = accept(r.getCertificate());
					getSavedRemesas().add(remesa);
					List<Certifica2BatchDetail> list = getRemesaDetalleList(remesa, r.getRemesableList());
					for (Certifica2BatchDetail d : list) {
						Certifica2BatchDetail cbd = accept(d);
						for (Certifica2BatchData data : getDetailDataList(cbd)) {
							data.setCertifica2BatchDetail(cbd);
							accept(data);
						}
					}
				}
				// FIN operaciones de la transaccion
				HibernateUtil.getSession(sessionName).flush();
				HibernateUtil.commitTransaction(sessionName);
			} catch (Exception e) {
				String msg = e.getMessage();
				try {
					HibernateUtil.rollbackTransaction(sessionName);
				} catch (DAOException daoe) {
					msg = "Unable to rollback transaction! (" + msg + ")";
				}
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(e);
			} finally {
				HibernateUtil.closeSession(sessionName);
			}
		} finally {
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
		onStart(event);
	}

	private List<Certifica2BatchDetail> getRemesaDetalleList(Certifica2Batch remesa, 
			List<RemesableEmpleadoCertificate> empleadosList) {
		List<Certifica2BatchDetail> list = new ArrayList<Certifica2BatchDetail>();
		for (RemesableEmpleadoCertificate d : empleadosList) {
			Certifica2BatchDetail detalle = getNewRemesaDetalle(d.getContract());
			if (detalle.getCertifica2Batch() == null
					|| detalle.getCertifica2Batch().getId() == null
					|| !remesa.equals(detalle.getCertifica2Batch())) {
				detalle.setCertifica2Batch(remesa);
				detalle.getContract().setId(d.getContract().getId());
				detalle.setSuspensionCause(d.getSuspensionCause());
				detalle.setEnterpriseNif(d.getContract().getWorkPlace().getEnterprise().getRegistry().getDocument());
				detalle.setCcc(d.getContract().getEnterpriseCCC().getCcc());
				detalle.setDocument(d.getContract().getPerson().getRegistry().getDocument());
				String name = d.getContract().getPerson().getName();
				detalle.setName(name.length()>9?name.substring(0, 8):name);
				detalle.setFirstSurname(d.getContract().getPerson().getFirstSurname());
				detalle.setSecondSurname(d.getContract().getPerson().getSecondSurname());
				detalle.setSsNumber(d.getContract().getPerson().getSocialSecurityNumber());
				detalle.setQuoteGroup(getContractDataMap(detalle.getContract()).get(ContextVariable.QUOTE_GROUP.getName()));
				detalle.setContractType(getContractDataMap(detalle.getContract()).get(ContextVariable.TC2.getName()));
				detalle.setContractDuration(differenceBetweenDates(detalle.getContract().getStartDate(), detalle.getContract().getEndDate()).toString());
//				detalle.setContractDurationIndicator;
				detalle.setOccupationCode(getContractDataMap(detalle.getContract()).get(ContextVariable.CNO.getName()));
//				detalle.setPublicAssociationCharge;
//				detalle.setDedicationPercent;
				detalle.setEnterpriseStartDate(d.getContract().getStartDate());
				detalle.setExpireDate(d.getContract().getEndDate());
//				detalle.setExpireEndDate;
//				detalle.setEre;
//				detalle.setEreReductionPercent;
//				detalle.setOtherReductionPercent;
//				detalle.setReductionCauseCode;
//				detalle.setSalaryPeriodStartDate;
//				detalle.setSalaryPeriodEndDate;
				detalle.setSalaryProcessingDays("00000");
			}
			list.add(detalle);
		}
		return list;
	}
	
	public Certifica2BatchDetail getNewRemesaDetalle(Contract contract) {
		Certifica2BatchDetail d = null;
		try {
			d = getExistingDetalle(contract);
		} catch (ManagerBeanException e) {
			// NADA, no se ha encontrado el empleado en ninguna remesa
		}
		if(d!=null){
			return d;
		} else {
			Certifica2BatchDetail detalle = new Certifica2BatchDetail();
			detalle.setContract(contract);
			return detalle;
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
	
	private Certifica2BatchDetail getExistingDetalle(Contract contract) throws ManagerBeanException{
		IManagerBean bean = BeanManager.getManagerBean(Certifica2BatchDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CERTIFICA2BATCH_DETAIL_CONTRACT_ID), contract.getId());
		Iterator<ITransferObject> it = bean.getList(criteria).iterator();
		while(it.hasNext()){
			Certifica2BatchDetail d = (Certifica2BatchDetail) it.next();
			if(d.getCertifica2Batch().getStatus()==FileStatus.PENDING){
				return d;
			}
		}
		return null;
	}

	public void onSelect(ActionEvent event) {
		setCurrentStep(1);
	}

	private void onSearch(ActionEvent event) {
		ContractController controller = (ContractController) AonUtil.getRegisteredBean(IPayrollConstants.CONTRACT_CONTROLLER);
		try {
			controller.getCriteria().addNotNullExpression(controller.getFieldName(IEntityAlias.CONTRACT_END_DATE));
			controller.getCriteria().addOrder(controller.getFieldName(IEntityAlias.CONTRACT_WORK_PLACE_ENTERPRISE_REGISTRY_NAME));
			controller.getCriteria().addOrder(controller.getFieldName(IEntityAlias.CONTRACT_PERSON_FIRST_SURNAME));
			controller.getCriteria().addOrder(controller.getFieldName(IEntityAlias.CONTRACT_PERSON_REGISTRY_NAME));
			controller.onSearch(event);
			initializeModel(controller.getManagerBean().getList(controller.getCriteria()));
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onSearch ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	public void onSelectAll(ActionEvent event) {
		processAll(true);
	}

	public void onDeselectAll(ActionEvent event) {
		processAll(false);
	}

	private void processAll(boolean selected) {
		for (int i = 0; i < getModel().getRowCount(); i++) {
			getModel().setRowIndex(i);
			RemesableEmpleadoCertificate r = (RemesableEmpleadoCertificate) getModel()
					.getRowData();
			r.setSelected(selected);
		}
	}

	public void onChangeWizard(ActionEvent event) {
		this.onNext(event);
		Certifica2FileWizard wizard = (Certifica2FileWizard) AonUtil.getRegisteredBean(IPayrollConstants.CERTIFICATE_FILE_WIZARD);
		wizard.setCurrentStep(1);
		wizard.initializeRemesasModel(getSavedRemesas());
		setSavedRemesas(null);
	}
	
	public void onApplyAllSuspensionCause(ActionEvent event) {
		applyAllSuspensionCause(getSuspensionCauseForAll());
	}
	
	private void applyAllSuspensionCause(SuspensionCause suspensionCause) {
		for (int i = 0; i < getModel().getRowCount(); i++) {
			getModel().setRowIndex(i);
			RemesableEmpleadoCertificate r = (RemesableEmpleadoCertificate) getModel()
			.getRowData();
			if(r.isSelected()){
				r.setSuspensionCause(suspensionCause);
			}
		}
	}
	
}
