package com.code.aon.ui.accounting.controller.entry;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.Period;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.accounting.enumeration.AccountPeriodStatus;
import com.code.aon.accounting.util.AccountingUtil;
import com.code.aon.accounting.util.PeriodEntriesManager;
import com.code.aon.accounting.util.PeriodEntriesParams;
import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.accounting.IAccountingConstants;
import com.code.aon.ui.accounting.check.CheckController;
import com.code.aon.ui.accounting.check.ICheckEntry;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class EndPeriodEntriesController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EndPeriodEntriesController.class.getName());
	
	private static final String INTEGRITY_ERROR = "ERROR. Existen apuntes descuadrados en el ejercicio. Verifique la integridad de la contabilidad.";
	private static final String CONFIDENTIALITY_WARNING = "AVISO. Existen apuntes confidenciales en el ejercicio, se generarán dos apuntes, uno normal y otro confidencial.";
	private static final String CONFIDENTIALITY_ERROR = "ERROR. Existen apuntes confidenciales en el ejercicio. No tiene permisos paa continuar.";
	private static final String OPERATING_ENTRY_ERROR = "ERROR. No existe asiento de apertura en el ejercicio seleccionado.";
	private static final String OPERATING_ENTRY_WARNING = "AVISO. Ya existe el asiento de explotación en el ejercicio seleccionado. Si continua, se borrará el existente y se creará de nuevo.";
	private static final String CLOSING_ENTRY_ERROR = "ERROR. No existe el asiento de explotación en el ejercicio seleccionado.";
	private static final String CLOSING_ENTRY_WARNING = "AVISO. Ya existe el asiento de cierre en el ejercicio seleccionado. Si continua, se borrará el existente y se creará de nuevo.";
	private static final String OPENING_ENTRY_WARNING = "AVISO. Ya existe el asiento de apertura. Si continua, se borrará el existente y se creará de nuevo.";
		
	private static final String OPERATING_CONCEPT = "Asiento Explotación";
	private static final String CLOSING_CONCEPT = "Asiento Cierre";
	private static final String OPENING_CONCEPT = "Asiento Apertura";

	private PeriodEntriesParams params;
	private boolean disabled;
	private List<String> messages;
	private List<String> outputMessages;
	private List<Integer> generatedEntries;
	private List<String> operatingMessages;
	private List<String> closingMessages;
	private List<String> openingMessages;
		
	public PeriodEntriesParams getParams() {
		return params;
	}
	public void setParams(PeriodEntriesParams params) {
		this.params = params;
	}
	
	public boolean isDisabled() {
		return disabled;
	}
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
	
	public List<String> getMessages() {
		return messages;
	}
	public void setMessages(List<String> messages) {
		this.messages = messages;
	}
	public List<String> getOutputMessages() {
		return outputMessages;
	}
	public void setOutputMessages(List<String> outputMessages) {
		this.outputMessages = outputMessages;
	}
	public List<Integer> getGeneratedEntries() {
		return generatedEntries;
	}
	public void setGeneratedEntries(List<Integer> generatedEntries) {
		this.generatedEntries = generatedEntries;
	}
	public List<String> getOperatingMessages() {
		return operatingMessages;
	}
	public void setOperatingMessages(List<String> operatingMessages) {
		this.operatingMessages = operatingMessages;
	}
	
	public List<String> getClosingMessages() {
		return closingMessages;
	}
	public void setClosingMessages(List<String> closingMessages) {
		this.closingMessages = closingMessages;
	}
	
	public List<String> getOpeningMessages() {
		return openingMessages;
	}
	public void setOpeningMessages(List<String> openingMessages) {
		this.openingMessages = openingMessages;
	}
	
	public void onInit(ActionEvent event) {
		setParams(new PeriodEntriesParams() );
		setDisabled(true);
		initialize();
	}
	private void initialize() {
		setMessages(new LinkedList<String>());
		setOutputMessages(new LinkedList<String>());
		setGeneratedEntries(new LinkedList<Integer>());
		setOperatingMessages(new LinkedList<String>());
		setClosingMessages(new LinkedList<String>());
		setOpeningMessages(new LinkedList<String>());
		getParams().initialize();
	}
	
	public void onPeriodChanged(ActionEvent event) {
		try {
			initialize();
			setDisabled(periodRight());
			if ( !isDisabled() ) {
				checkConfidential();
				getParams().setOperatingEntry(true);
				getParams().setClosingEntry(true);
				getParams().setOpeningEntry(true);
				onCheck(null);
			}
		} catch (ManagerBeanException e) {
			String msg = "No se pudo comprobar la selección. (" + e.getMessage() + ")";
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} 
	}

	private boolean periodRight() {
		CheckController acc = (CheckController) AonUtil.getRegisteredBean(IAccountingConstants.ACCOUNT_CHECK_CONTROLLER);
		acc.onInitialize(null);
		acc.checkUnbalancedAccountEntry();
		acc.getParams().setPeriod(getParams().getPeriod());
		acc.onExecute(null);
		List<ICheckEntry> checks = acc.getCheckEntryList();
		if (checks != null && checks.size()> 0) {
			StringBuffer buf = new StringBuffer();
			buf.append(INTEGRITY_ERROR);
			for (ICheckEntry check:checks) {
				buf.append("<br/>");
				buf.append(check.getMessage());
			}
			getMessages().add(buf.toString());
			return true; 
		}
		return false;
	}

	private void checkConfidential() throws ManagerBeanException {
		getParams().setConfidentialEntryPresent(false);
		IManagerBean bean = BeanManager.getManagerBean(AccountEntry.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_ACCOUNT_PERIOD_ID), getParams().getPeriod().getId());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_SECURITY_LEVEL), SecurityLevel.CONFIDENTIAL);
		Number count = (Number) bean.getUniqueResult(Projection.rowCount(), criteria);
		if (count.intValue() > 0) {
			if (AonUtil.getRoleManager().isConfidentiality()) {
				getMessages().add(CONFIDENTIALITY_WARNING);
				getParams().setConfidentialEntryPresent(true);
			} else {
				getMessages().add(CONFIDENTIALITY_ERROR); 
				setDisabled(true);
			}
		}
	}

	public void onCheck(ActionEvent event) {
		try {
			getParams().setOpeningPeriodCreationEnabled(false);
			setDisabled(false);
			if (!getParams().isClosingEntry()) {
				getParams().setOpeningEntry(false);
			}
			setOperatingMessages(new LinkedList<String>());
			setClosingMessages(new LinkedList<String>());
			setOpeningMessages(new LinkedList<String>());
			if (getParams().isOperatingEntry()) {
				tryToEnableOperatingCheck();
				getParams().setOperatingDate( getParams().getPeriod().getDeadline());
				getParams().setOperatingConcept(OPERATING_CONCEPT);
			}
			if (!isDisabled() && getParams().isClosingEntry()) {
				tryToEnableClosingCheck();
				getParams().setClosingDate( getParams().getPeriod().getDeadline());
				getParams().setClosingConcept(CLOSING_CONCEPT);
			}
			if (!isDisabled() && getParams().isOpeningEntry()) {
				Period openingPeriod = obtainOpeningPeriod();
				if (openingPeriod == null) {
					openingPeriod = tryToCreatePeriod();
				}
				getParams().setOpeningPeriod( openingPeriod );
				getParams().setOpeningDate( openingPeriod.getInitiationDate());
				getParams().setOpeningConcept(OPENING_CONCEPT);
				tryToEnableOpeningCheck();
			}
			decorateMessages();
		} catch (ManagerBeanException e) {
			setDisabled(true);
			String msg = "Error al comprobar la creación del asiento de Explotación.";
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} 
	}
	private void decorateMessages() {
		if (isDisabled()) {
			List<String> newMessages = new LinkedList<String>();
			for (String msg: getMessages()) {
				if (!msg.startsWith("AVISO")) {
					newMessages.add(msg);
				}
			}
			setMessages(newMessages);
			
			List<String> newOperatingMessages = new LinkedList<String>();
			for (String msg: getOperatingMessages()) {
				if (!msg.startsWith("AVISO")) {
					newOperatingMessages.add(msg);
				}
			}
			setOperatingMessages(newOperatingMessages);
			
			List<String> newClosingMessages = new LinkedList<String>();
			for (String msg: getClosingMessages()) {
				if (!msg.startsWith("AVISO")) {
					newClosingMessages.add(msg);
				}
			}
			setClosingMessages(newClosingMessages);
			
			List<String> newOpeningMessages = new LinkedList<String>();
			for (String msg: getOpeningMessages()) {
				if (!msg.startsWith("AVISO")) {
					newOpeningMessages.add(msg);
				}
			}
			setOpeningMessages(newOpeningMessages);
		}
	}
	private Period tryToCreatePeriod() {
		getParams().setOpeningPeriodCreationEnabled(true);
		Period period = new Period();
		Calendar c = Calendar.getInstance();
		c.setTime(getParams().getPeriod().getInitiationDate());
		c.add(Calendar.YEAR, 1);
		period.setName(Integer.toString(c.get(Calendar.YEAR)));
		period.setInitiationDate(c.getTime());
		c.setTime(getParams().getPeriod().getDeadline());
		c.add(Calendar.YEAR, 1);
		period.setDeadline(c.getTime());
		period.setStatus(AccountPeriodStatus.ACTIVE);
		return period;
	}
	private Period obtainOpeningPeriod() throws ManagerBeanException {
		IManagerBean periodBean = BeanManager.getManagerBean(Period.class);
		Criteria criteria = new Criteria();
		Date initialDate = getParams().getPeriod().getDeadline();
		Calendar c = Calendar.getInstance();
		c.setTime(initialDate);
		c.add(Calendar.DAY_OF_MONTH, 1);
		initialDate = c.getTime();
		criteria.addEqualExpression(periodBean.getFieldName(IEntityAlias.PERIOD_INITIATION_DATE), initialDate);
		List<ITransferObject> list = periodBean.getList(criteria); 
		if (list!= null && list.size() > 0) {
			getParams().setOpeningPeriodCreationEnabled(false);
			return (Period)  list.get(0); 
		}
		return null;
	}
	
	private void tryToEnableOperatingCheck() throws ManagerBeanException {
		AccountingUtil util = new AccountingUtil();
		if (!util.existsEntry(getParams().getPeriod(), AccountEntryType.OPENING, null )) {
			IManagerBean periodBean = BeanManager.getManagerBean(Period.class);
			Criteria c = new Criteria();
			c.addLessThanExpression(periodBean.getFieldName(IEntityAlias.PERIOD_INITIATION_DATE), getParams().getPeriod().getInitiationDate());
			if (periodBean.getList(c).size() > 0) {
				setDisabled(true);
				getOperatingMessages().add(OPERATING_ENTRY_ERROR);
			} 
		}
		if (!getParams().isClosingEntry() && util.existsEntry(getParams().getPeriod(), AccountEntryType.CLOSING, null)) {
			getOperatingMessages().add("ERROR. Existe un asiento de cierre en el ejercicio.");
			setDisabled(true);
		}
		if (!isDisabled() && util.existsEntry(getParams().getPeriod(), AccountEntryType.OPERATING, null)) {
			getOperatingMessages().add(OPERATING_ENTRY_WARNING);
		}
	}

	private void tryToEnableClosingCheck() throws ManagerBeanException {
		AccountingUtil util = new AccountingUtil();
		if (!getParams().isOperatingEntry()){
			if (!util.existsEntry(getParams().getPeriod(), AccountEntryType.OPERATING, null)) {
				getClosingMessages().add(CLOSING_ENTRY_ERROR);
				setDisabled(true);
			}
		}
		if (!getParams().isOpeningEntry()) {
			Period nextPeriod = obtainOpeningPeriod();
			if (nextPeriod != null && util.existsEntry(nextPeriod, AccountEntryType.OPENING, null)){
				getClosingMessages().add("ERROR. Existe un asiento de apertura en el ejercicio "+ nextPeriod.getName() +".");
				setDisabled(true);
			}
		}
		if (!isDisabled() && util.existsEntry(getParams().getPeriod(), AccountEntryType.CLOSING, null)) {
			getClosingMessages().add(CLOSING_ENTRY_WARNING);
		}
	}

	private void tryToEnableOpeningCheck() throws ManagerBeanException {
		AccountingUtil util = new AccountingUtil();
		if (util.existsEntry(getParams().getOpeningPeriod(), AccountEntryType.OPERATING, null)) {
			getOpeningMessages().add("ERROR. Existe un asiento de explotación en el ejercicio " + getParams().getOpeningPeriod().getName() +".");
			setDisabled(true);
		}
		if (util.existsEntry(getParams().getOpeningPeriod(), AccountEntryType.CLOSING, null)) {
			getOpeningMessages().add("ERROR. Existe un asiento de cierre en el ejercicio " + getParams().getOpeningPeriod().getName() +".");
			setDisabled(true);
		}
		if (!isDisabled() && util.existsEntry(getParams().getOpeningPeriod(), AccountEntryType.OPENING, null)) {
			getOpeningMessages().add(OPENING_ENTRY_WARNING);
		}
	}

	public void onAccept(ActionEvent event) {
		setOutputMessages(new LinkedList<String>());
		setGeneratedEntries(new LinkedList<Integer>());
		if (!getParams().isOperatingEntry() &&
			!getParams().isClosingEntry() && 
			!getParams().isOpeningEntry()) {
			String msg = "Debe marcar al menos una opción.";
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
			
		}
		if (getParams().isOperatingEntry()) {
			try {
				PeriodEntriesManager manager = new PeriodEntriesManager( getParams() );
				AccountEntry[] entries = manager.createOperatingEntry();
				for (AccountEntry entry: entries) {
					if (entry != null) {
						getOutputMessages().add("Se ha creado el asiento de explotación nº " + entry.getId() + 
								(entry.isConfidential()?" confidencial ":"") + 
								" en el ejercicio " + entry.getAccountPeriod().getName() + ".");
						getGeneratedEntries().add(entry.getId());
					}
				}
			} catch (ManagerBeanException e) {
				String msg = "Error al generar el asiento de explotación del ejercicio. (" + e.getMessage() + ")";
				LOGGER.error(msg, e);
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			} 
		}
		if (getParams().isClosingEntry()) {
			try {
				PeriodEntriesManager manager = new PeriodEntriesManager( getParams() );
				AccountEntry[] entries = manager.createClosingEntry();
				for (AccountEntry entry: entries) {
					if (entry!= null) {
						getOutputMessages().add("Se ha creado el asiento de cierre nº " + entry.getId() + 
								(entry.isConfidential()?" confidencial ":"") + 
								" en el ejercicio " + entry.getAccountPeriod().getName() + ".");
						getGeneratedEntries().add(entry.getId());
					}
				}
			} catch (ManagerBeanException e) {
				String msg = "Error al generar el asiento de cierre del ejercicio. (" + e.getMessage() + ")";
				LOGGER.error(msg, e);
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			} 
		}
		if (getParams().isOpeningEntry()) {
			try {
				PeriodEntriesManager manager = new PeriodEntriesManager( getParams() );
				AccountEntry[] entries = manager.createOpeningEntry();
				for (AccountEntry entry: entries) {
					if (entry!= null) {
						getOutputMessages().add("Se ha creado el asiento de apertura nº " + entry.getId() + 
								(entry.isConfidential()?" confidencial ":"") + 
								" en el ejercicio " + entry.getAccountPeriod().getName() + ".");
						getGeneratedEntries().add(entry.getId());
					}
				}
			} catch (ManagerBeanException e) {
				String msg = "Error al generar el asiento de apertura del ejercicio.";
				LOGGER.error(msg, e);
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			} 
		}
	}
	
	public void onSeeEntries(ActionEvent event) {
		try {
			AccountEntryController entryController = (AccountEntryController) FormUtil.getController(IAccountingConstants.ACCOUNT_ENTRY_CONTROLLER_NAME);
			Criteria criteria = new Criteria();
			for (Integer id:getGeneratedEntries()) {
				criteria.addOrExpression(entryController.getManagerBean().getFieldName(IEntityAlias.ACCOUNT_ENTRY_ID), id.toString());	
			}
			entryController.setCriteria(criteria);
			entryController.onSearch(null);
			entryController.getModel().setRowIndex(0);
			entryController.onSelect(null);
		} catch (ManagerBeanException e) {
			String msg = "Error al navegar a los apuntes generados.";
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} catch (ExpressionException e) {
			String msg = "Error al navegar a los apuntes generados.";
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	
}
