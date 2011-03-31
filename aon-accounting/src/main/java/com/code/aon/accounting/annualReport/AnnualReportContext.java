package com.code.aon.accounting.annualReport;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.accounting.Period;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.accounting.summary.SummaryCollection;
import com.code.aon.accounting.summary.SummaryProvider;
import com.code.aon.accounting.summary.SummaryProviderParameters;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.Company;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RecordData;
import com.code.aon.registry.RegistryDirStaff;
import com.code.aon.registry.dao.IRegistryAlias;

/**
 * Contexto para la ejecución de la Memeria Contable o cualquier otro informe del mismo tipo.
 *  
 * @author ecastellano
 *
 */
/**
 * @author ecastellano
 *
 */
public class AnnualReportContext {
	
	// COMMON
	private String SKIP = "XXX";
	private String ASTERISK = "*";
	private String EMPTY = "";
	private String OPEN_BRACKET = "(";
	private String CLOSE_BRACKET = ")";
	private String PIPE = "|";
	private String COMMA = ",";
	private Double ZERO = new Double(0);

	private AnnualReportParameters params;
	private Period previousPeriod;
	private Company company;
	private RecordData recordData; 
	private List<RegistryDirStaff> registryDirStaffs;   
	private SummaryProvider summaryProvider;
	
	public AnnualReportContext(AnnualReportParameters params) {
		this.params = params; 
	}
	
	private AnnualReportParameters getParams() {
		return params;
	}

	private SummaryProvider getSummaryProvider() {
		if (summaryProvider == null) {
			summaryProvider = new SummaryProvider();
		}
		return summaryProvider;
	}
	private Period getPreviousPeriod() {
		if (previousPeriod == null) {
			try {
				IManagerBean bean = BeanManager.getManagerBean(Period.class);
				Criteria criteria = new Criteria();
				criteria.addLessThanExpression(bean.getFieldName(IAccountingAlias.PERIOD_INITIATION_DATE),  
						getParams().getParams().getPeriod().getInitiationDate() );
				criteria.addOrder(bean.getFieldName(IAccountingAlias.PERIOD_INITIATION_DATE),  false );
				List<ITransferObject> list = bean.getList(criteria);
				if (list != null && list.size() > 0 ) {
					ITransferObject to = list.get(0);
					previousPeriod = (Period) to;
				}
			} catch (ManagerBeanException e) {
				previousPeriod = null;
			}
		}
		return previousPeriod;
	}
	
	private Company getCompany() {
		if (company == null) {
			try {
				IManagerBean bean = BeanManager.getManagerBean(Company.class);
				List<ITransferObject> list = bean.getList(null);
				if (list != null && list.size() > 0 ) {
					ITransferObject to = list.get(0);
					company = (Company) to;
				}
				//TODO WARNING if 0
			} catch (ManagerBeanException e) {
				//TODO ERROR.
				company = null;
			}
		}
		return company;
	}
	private RecordData getRecordData() {
		if (recordData == null) {
			try {
				IManagerBean bean = BeanManager.getManagerBean(RecordData.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IRegistryAlias.RECORD_DATA_REGISTRY_ID), getCompany().getId());
				List<ITransferObject> list = bean.getList(null);
				if (list != null && list.size() > 0 ) {
					ITransferObject to = list.get(0);
					recordData = (RecordData) to;
				}
				//TODO WARNING if 0
			} catch (ManagerBeanException e) {
				//TODO ERROR.
				recordData = null;
			}
		}
		return recordData;
	}
	
	@SuppressWarnings("unchecked")
	private List<RegistryDirStaff> getRegistryDirStaffs() {
		if (registryDirStaffs == null) {
			try {
				IManagerBean bean = BeanManager.getManagerBean(RegistryDirStaff.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IRegistryAlias.REGISTRY_DIR_STAFF_ID), getCompany().getId());
				criteria.addOrder(bean.getFieldName(IRegistryAlias.REGISTRY_DIR_STAFF_PERCENT_SHARE));
				List<?> list = bean.getList(null);
				registryDirStaffs = (List<RegistryDirStaff>) list;
				//TODO WARNING if 0
			} catch (ManagerBeanException e) {
				//TODO ERROR.
				registryDirStaffs = null;
			}
		}
		return registryDirStaffs;
	}
	
	private SummaryCollection getSummaryCollection(String accounts) throws ManagerBeanException {
		String accountExp = getAccountsExpression(accounts);
		getParams().getParams().setAccountExpression(accountExp.toString());
		return getSummaryProvider().getSummaryCollection(getParams().getParams(),false);
	}

	private SummaryCollection getPreviousSummaryCollection(String accounts) throws ManagerBeanException {
		try {
			String accountExp = getAccountsExpression(accounts);
			SummaryProviderParameters spp = getParams().getParams().clone();
			spp.setPeriod(getPreviousPeriod());
			getParams().getParams().setAccountExpression(accountExp.toString());
			return getSummaryProvider().getSummaryCollection(getParams().getParams(),false);
		} catch (CloneNotSupportedException e) {
			// TODO ERROR
			return null;
		}
	}

	private String getAccountsExpression(String accounts) {
		StringBuilder accountExp = new StringBuilder();
		String[] tokens = StringUtils.split(accounts,COMMA+OPEN_BRACKET+CLOSE_BRACKET);
		for (String token:tokens) {
			token = token.trim();
			if (StringUtils.isNotBlank(token)) {
				accountExp.append(accountExp.length()>0?PIPE:EMPTY);
				accountExp.append(token);	
				accountExp.append(ASTERISK);
			}
		}
		return accountExp.toString(); 
	}

	/*
	 * METODOS EXPUESTOS AL CLIENTE. COMENTARLOS CON JAVADOC POR FAVOR.
	 */
	
	/**
	 * Razón Social de la Empresa. Es el dato introducido en la pantalla de
	 * "Datos de Empresa".
	 * 
	 * @return Cadena de caracteres.
	 */
	public String empresa() {
		return getCompany().getFullName();
	}

	/**
	 * Devuelve el código del ejercicio contable seleccionado en la pantalla
	 * previa al lanzamiento del informe.
	 * 
	 * @return Cadena de caracteres.
	 */
	public String ejercicio() {
		return getParams().getParams().getPeriod().getId();
	}
	
	/**
	 * Devuelve la fecha final del ejercicio contable seleccionado en la
	 * pantalla previa al lanzamiento del informe.
	 * 
	 * @return Fecha.
	 */
	public Date fechaFinEjercicio() {
		return getParams().getParams().getPeriod().getDeadline();
	}
	
	/**
	 * Devuelve el ejercicio contable cuya fech de inicio sea inmediatamente
	 * anterior a la fecha de incio del ejercicio contable seleccionado en la
	 * pantalla previa al lanzamiento del informe.
	 * 
	 * @return Fecha.
	 */
	public String ejercicioAnterior() {
		return getPreviousPeriod() == null ? null : getPreviousPeriod().getId();
	}
	
	/**
	 * Devuelve el domicio completo, de la dirección principal de la empresa. Es
	 * el dato introducido en la pantalla de "Datos de Empresa", solapa
	 * "Direcciones".
	 * 
	 * @return Cadena de caracteres.
	 */
	public String domicilioEmpresa() {
		try {
			return getCompany().getDefaultAddress().getFullAddress();
		} catch (ManagerBeanException e) {
			return null;
		}
	}

	/**
	 * Devuelve el número de documento de la empresa. Es
	 * el dato introducido en la pantalla de "Datos de Empresa".
	 * 
	 * @return Cadena de caracteres.
	 */
	public String nifEmpresa(){
		return getCompany().getDocument();
	}
	
	/**
	 * Devuelve el dato "Inscripción" introducido en la solapa de
	 * "Datos Registrales" de la pantalla "Datos de Empresa".
	 * 
	 * @return Cadena de caracteres.
	 */
	public String registroMercantil() {
		return (getRecordData() != null) ? getRecordData().getRegistration() : null;
	}

	/**
	 * Devuelve el dato "Fecha Registro" introducido en la solapa de
	 * "Datos Registrales" de la pantalla "Datos de Empresa".
	 * 
	 * @return Fecha.
	 */
	public Date fechaInscripcionRegistroMercantil(){
		return (getRecordData() != null) ? getRecordData().getRecordDate() : null;
	}
	
	/**
	 * Devuelve el dato "Tomo" introducido en la solapa de
	 * "Datos Registrales" de la pantalla "Datos de Empresa".
	 * 
	 * @return Cadena de caracteres.
	 */
	public String tomoRegistroMercantil(){
		return (getRecordData() != null) ? getRecordData().getVolume() : null;
	}

	/**
	 * Devuelve el dato "Folio" introducido en la solapa de
	 * "Datos Registrales" de la pantalla "Datos de Empresa".
	 * 
	 * @return Cadena de caracteres.
	 */
	public String folioRegistroMercantil(){
		return (getRecordData() != null) ? getRecordData().getPage() : null;
	}
	
	/**
	 * Devuelve el dato "Hoja" introducido en la solapa de
	 * "Datos Registrales" de la pantalla "Datos de Empresa".
	 * 
	 * @return Cadena de caracteres.
	 */
	public String hojaRegistroMercantil(){
		return (getRecordData() != null) ? getRecordData().getSheet() : null;
	}	
	
	/**
	 * Devuelve el sumatorio del campo "Nº acciones" introducido en la solapa de
	 * "Datos Mercantiles" de la pantalla "Datos de Empresa".
	 * 
	 * @return Número Entero.
	 */
	public Integer numeroTotalAcciones() {
		if (getRegistryDirStaffs() != null) {
			int i = 0;
			for (RegistryDirStaff staff:getRegistryDirStaffs()) {
				i += staff.getShareNumber();
			}
			return i;	
		}
		return 0;
	}
	
	/**
	 * Devuelve el sumatorio del campo "Valor Nominal" introducido en la solapa de
	 * "Datos Mercantiles" de la pantalla "Datos de Empresa".
	 * 
	 * @return Número Decimal.
	 */
	public Double valorNominalTotalAcciones() {
		if (getRegistryDirStaffs() != null) {
			double i = 0;
			for (RegistryDirStaff staff:getRegistryDirStaffs()) {
				i += staff.getNominalValue();
			}
			return i;	
		}
		return ZERO;
	}

	/**
	 * Devuelve el Saldo acumulado de la o las cuentas indicadas. Si el Saldo es
	 * deudor, el resultado será negativo.
	 * 
	 * @param account
	 *            Número Entero, es decir, sin comillas.
	 * @return Número Decimal.
	 */
	public Double saldo(long account) {
		return saldo(Long.toString(account));
	}

	/**
	 * Devuelve el Saldo acumulado de la o las cuentas indicadas. Si el Saldo es
	 * deudor, el resultado será negativo.
	 * 
	 * @param accounts
	 *            Cadena de caracteres, es decir, entre comillas dobles.
	 * @return Número Decimal.
	 */
	public Double saldo(String accounts) {
		if (!SKIP.equals(accounts)) {
			try {
				SummaryCollection summaryCollection = getSummaryCollection(accounts);
				return CommonUtil.round(summaryCollection.getCredit() - summaryCollection.getDebit());
			} catch (ManagerBeanException e) {
				//TODO ERROR.
				return ZERO;
			}
		}
		//TODO ERROR.
		return ZERO;
	}

	/**
	 * Devuelve el saldo deudor de la o las cuentas indicadas. Si el Saldo de la cuenta es
	 * negativo, devuelve cero.
	 * 
	 * @param accounts
	 *            Número Entero, es decir, sin comillas.
	 * @return Número Decimal.
	 */
	public Double saldoDeudor(long account) {
		return saldoDeudor(Long.toString(account));
	}

	/**
	 * Devuelve el Saldo deudor de la o las cuentas indicadas. Si el Saldo de la cuenta es
	 * negativo, devuelve cero.
	 * 
	 * @param accounts
	 *            Cadena de caracteres, es decir, entre comillas dobles.
	 * @return Número Decimal.
	 */
	public Double saldoDeudor(String accounts) {
		if (!SKIP.equals(accounts)) {
			try {
				SummaryCollection summaryCollection = getSummaryCollection(accounts);
				double saldo = CommonUtil.round(summaryCollection.getDebit() - summaryCollection.getCredit()); 
				return saldo > 0 ? saldo : ZERO;
			} catch (ManagerBeanException e) {
				//TODO ERROR.
				return ZERO;
			}
		}
		//TODO ERROR.
		return ZERO;
	}

	/**
	 * Devuelve el Saldo acreedor de la o las cuentas indicadas. Si el Saldo de la cuenta es
	 * negativo, devuelve cero.
	 * 
	 * @param accounts
	 *            Número Entero, es decir, sin comillas.
	 * @return Número Decimal.
	 */
	public Double saldoAcreedor(long account) {
		return saldoAcreedor(Long.toString(account));
	}

	/**
	 * Devuelve el Saldo acreedor de la o las cuentas indicadas. Si el Saldo de la cuenta es
	 * negativo, devuelve cero.
	 * 
	 * @param accounts
	 *            Cadena de caracteres, es decir, entre comillas dobles.
	 * @return Número Decimal.
	 */
	public Double saldoAcreedor(String accounts) {
		if (!SKIP.equals(accounts)) {
			try {
				SummaryCollection summaryCollection = getSummaryCollection(accounts);
				double saldo = CommonUtil.round(summaryCollection.getDebit() - summaryCollection.getCredit()); 
				return saldo > 0 ? saldo : ZERO;
			} catch (ManagerBeanException e) {
				//TODO ERROR.
				return ZERO;
			}
		}
		//TODO ERROR.
		return ZERO;
	}

	/**
	 * Devuelve el Saldo acumulado del ejercicio anterior, de la o las cuentas
	 * indicadas. Si el Saldo es deudor, el resultado será negativo.
	 * 
	 * @param account
	 *            Número Entero, es decir, sin comillas.
	 * @return Número Decimal.
	 */
	public Double saldoAnterior(long account) {
		return saldo(Long.toString(account));
	}

	/**
	 * Devuelve el Saldo acumulado del ejercicio anterior, de la o las cuentas
	 * indicadas. Si el Saldo es deudor, el resultado será negativo.
	 * 
	 * @param accounts
	 *            Cadena de caracteres, es decir, entre comillas dobles.
	 * @return Número Decimal.
	 */
	public Double saldoAnterior(String accounts) {
		if (!SKIP.equals(accounts)) {
			try {
				SummaryCollection summaryCollection = getPreviousSummaryCollection(accounts);
				return CommonUtil.round(summaryCollection.getCredit() - summaryCollection.getDebit());
			} catch (ManagerBeanException e) {
				// TODO ERROR.
				return ZERO;
			}
		}
		// TODO ERROR.
		return ZERO;
	}

	/**
	 * Devuelve el saldo deudor del ejercicio anterior, de la o las cuentas
	 * indicadas. Si el Saldo de la cuenta es negativo, devuelve cero.
	 * 
	 * @param accounts
	 *            Número Entero, es decir, sin comillas.
	 * @return Número Decimal.
	 */
	public Double saldoDeudorAnterior(long account) {
		return saldoDeudor(Long.toString(account));
	}

	/**
	 * Devuelve el Saldo deudor del ejercicio anterior, de la o las cuentas
	 * indicadas. Si el Saldo de la cuenta es negativo, devuelve cero.
	 * 
	 * @param accounts
	 *            Cadena de caracteres, es decir, entre comillas dobles.
	 * @return Número Decimal.
	 */
	public Double saldoDeudorAnterior(String accounts) {
		if (!SKIP.equals(accounts)) {
			try {
				SummaryCollection summaryCollection = getPreviousSummaryCollection(accounts);
				double saldo = CommonUtil.round(summaryCollection.getDebit() - summaryCollection.getCredit());
				return saldo > 0 ? saldo : ZERO;
			} catch (ManagerBeanException e) {
				// TODO ERROR.
				return ZERO;
			}
		}
		// TODO ERROR.
		return ZERO;
	}

	/**
	 * Devuelve el Saldo acreedor del ejercicio anterior, de la o las cuentas
	 * indicadas. Si el Saldo de la cuenta es negativo, devuelve cero.
	 * 
	 * @param accounts
	 *            Número Entero, es decir, sin comillas.
	 * @return Número Decimal.
	 */
	public Double saldoAcreedorAnterior(long account) {
		return saldoAcreedor(Long.toString(account));
	}

	/**
	 * Devuelve el Saldo acreedor del ejercicio anterior, de la o las cuentas
	 * indicadas. Si el Saldo de la cuenta es negativo, devuelve cero.
	 * 
	 * @param accounts
	 *            Cadena de caracteres, es decir, entre comillas dobles.
	 * @return Número Decimal.
	 */
	public Double saldoAcreedorAnterior(String accounts) {
		if (!SKIP.equals(accounts)) {
			try {
				SummaryCollection summaryCollection = getPreviousSummaryCollection(accounts);
				double saldo = CommonUtil.round(summaryCollection.getDebit() - summaryCollection.getCredit());
				return saldo > 0 ? saldo : ZERO;
			} catch (ManagerBeanException e) {
				// TODO ERROR.
				return ZERO;
			}
		}
		// TODO ERROR.
		return ZERO;
	}	
	
}
