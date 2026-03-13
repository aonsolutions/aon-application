package com.code.aon.accounting.annualReport;

import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;

import com.code.aon.accounting.summary.SummaryCollection;
import com.code.aon.accounting.summary.SummaryProvider;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.Company;
import com.code.aon.geozone.GeoZone;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RecordData;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryDirStaff;
import com.code.aon.registry.RegistryMedia;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.occam.api.model.registry.CommercialRegistryCode;
import com.esferalia.aon.occam.api.model.registry.RecordDataType;

/**
 * Contexto para la ejecución de la Memeria Contable o cualquier otro informe del mismo tipo.
 *  
 * @author ecastellano
 *
 */
public class AnnualReportContext {
	
	// COMMON
	private String ASTERISK = "*";
	private String EMPTY = "";
	private String OPEN_BRACKET = "(";
	private String CLOSE_BRACKET = ")";
	private String PIPE = "|";
	private String COMMA = ",";
	private String ERROR = "<ERROR>";
	private Double ZERO = new Double(0);

	private AnnualReportParameters params;
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
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.RECORD_DATA_REGISTRY_ID), getCompany().getId());
				
				// FALTA - SACAR EL DE CONSTITUCION, NO SE DONDE SE USARA ESTO EN TODA LA APLICACION
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.RECORD_DATA_TYPE), RecordDataType.INCORPORATION.value());
				
//				List<ITransferObject> list = bean.getList(null);
				List<ITransferObject> list = bean.getList(criteria);
				
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
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_DIR_STAFF_ID), getCompany().getId());
				criteria.addOrder(bean.getFieldName(IEntityAlias.REGISTRY_DIR_STAFF_PERCENT_SHARE));
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
		SummaryCollection sc = getSummaryProvider().getSummaryCollection(getParams().getParams(accountExp),false);
		return sc;
	}

	private SummaryCollection getPreviousSummaryCollection(String accounts) throws ManagerBeanException {
		String accountExp = getAccountsExpression(accounts);
		SummaryCollection sc = getSummaryProvider().getSummaryCollection(getParams().getPreviousParams(accountExp),false);
		return sc;
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
	 * METODOS EXPUESTOS AL CLIENTE. COMENTADOS CON JAVADOC POR FAVOR.
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
		return getParams().getParams().getPeriod().getName();
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
		return getParams().getPreviousPeriod() == null ? null : getParams().getPreviousPeriod().getName();
	}
	
	/**
	 * Devuelve el domicio completo, de la dirección principal de la empresa. Es
	 * el dato introducido en la pantalla de "Datos de Empresa".
	 * 
	 * @return Cadena de caracteres.
	 */
	public String domicilioEmpresa() {
		try {
			RegistryAddress raddress = getCompany().getDefaultAddress();
			if (raddress != null) {
				return raddress.getFullAddress();
			}
			return null;
		} catch (ManagerBeanException e) {
			return ERROR;
		}
	}

	/**
	 * Devuelve el nombre de la provincia de la empresa. Es el dato incluido en la dirección principal de la empresa.
	 * @return
	 */
	public String provinciaEmpresa() {
		try {
			RegistryAddress raddress = getCompany().getDefaultAddress();
			if (raddress != null) {
				GeoZone geozone = raddress.getGeozone();
				if (geozone != null) {
					return geozone.getName();
				}
			}
			return null;
		} catch (ManagerBeanException e) {
			return "<ERROR>";
		}
	}

	/**
	 * Devuelve el codigo de la provincia de la empresa. Es el dato incluido en la dirección principal de la empresa.
	 * @return
	 */
	public String codigoProvinciaEmpresa() {
		try {
			RegistryAddress raddress = getCompany().getDefaultAddress();
			if (raddress != null) {
				GeoZone geozone = raddress.getGeozone();
				if (geozone != null) {
					return geozone.getCode();
				}
			}
			return null;
		} catch (ManagerBeanException e) {
			return "<ERROR>";
		}
	}
	
	/**
	 * Devuelve el municipio de la empresa. Es el dato incluido en la dirección principal de la empresa.
	 * @return
	 */
	public String municipioEmpresa() {
		try {
			RegistryAddress raddress = getCompany().getDefaultAddress();
			if (raddress != null) {
				if (raddress.getMunicipalityCode() != null) {
					ResourceBundle bundle = ResourceBundle.getBundle("com.code.aon.common.i18n.municipalities");
					return bundle.getString(raddress.getMunicipalityCode());
				}
			}
			return null;
		} catch (ManagerBeanException e) {
			return "<ERROR>";
		}
	}

	/**
	 * Devuelve la localidad (localidad + provincia) de la empresa. Es el dato incluido en la dirección principal de la empresa.
	 * @return
	 */
	public String localidadEmpresa() {
		try {
			RegistryAddress raddress = getCompany().getDefaultAddress();
			if (raddress != null) {
				return raddress.getLocation();
			}
			return null;
		} catch (ManagerBeanException e) {
			return "<ERROR>";
		}
	}
	
	/**
	 * Devuelve la localidad (solo localidad) de la empresa. Es el dato incluido en la dirección principal de la empresa.
	 * @return
	 */
	public String localidadSimpleEmpresa() {
		try {
			RegistryAddress raddress = getCompany().getDefaultAddress();
			if (raddress != null) {
				return raddress.getCity();
			}
			return null;
		} catch (ManagerBeanException e) {
			return "<ERROR>";
		}
	}

	/**
	 * Devuelve el código postal de la empresa. Es el dato incluido en la dirección principal de la empresa.
	 * @return
	 */
	public String codigoPostalEmpresa() {
		try {
			RegistryAddress raddress = getCompany().getDefaultAddress();
			if (raddress != null) {
				return raddress.getZip();
			}
			return null;
		} catch (ManagerBeanException e) {
			return "<ERROR>";
		}
	}
	
	/**
	 * Devuelve el fax de la empresa. Es el dato incluido en la pantalla "Datos de empresa".
	 * @return
	 */
	public String faxEmpresa() {
		try {
			RegistryMedia rmedia = getCompany().getFax();
			if (rmedia != null) {
				return rmedia.getValue();
			}
			return null;
		} catch (ManagerBeanException e) {
			return "<ERROR>";
		}
	}

	/**
	 * Devuelve el fax de la empresa. Es el dato incluido en la pantalla "Datos de empresa".
	 * @return
	 */
	public String telefonoEmpresa() {
		try {
			RegistryMedia rmedia = getCompany().getPhone();
			if (rmedia != null) {
				return rmedia.getValue();
			}
			return null;
		} catch (ManagerBeanException e) {
			return "<ERROR>";
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
	 * Devuelve el dato "Codigo de Registro Mercantil" introducido en la solapa de
	 * "Datos Registrales" de la pantalla "Datos de Empresa".
	 * 
	 * @return Cadena de caracteres.
	 */
	public String codigoRegistroMercantil(){
		String code = null;
		if (getRecordData() != null && getRecordData().getCommercialRegistryCode() != null) {
			CommercialRegistryCode commercialRegistryCode = CommercialRegistryCode.safeValueOf(getRecordData().getCommercialRegistryCode());
			if (commercialRegistryCode != null) {
				code = commercialRegistryCode.getCode();
			}
		}
		return code;
	}
	
	/**
	 * Devuelve el dato "Codigo Registro Mercantil" (descripcion) introducido en la solapa de
	 * "Datos Registrales" de la pantalla "Datos de Empresa".
	 * 
	 * @return Cadena de caracteres.
	 */
	public String nombreRegistroMercantil(){
		String code = null;
		if (getRecordData() != null && getRecordData().getCommercialRegistryCode() != null) {
			CommercialRegistryCode commercialRegistryCode = CommercialRegistryCode.safeValueOf(getRecordData().getCommercialRegistryCode());
			if (commercialRegistryCode != null) {
				code = commercialRegistryCode.getDescription();
			}
		}
		return code;
	}
	
	
	/**
	 * Devuelve el dato "IRUS" introducido en la solapa de
	 * "Datos Registrales" de la pantalla "Datos de Empresa".
	 * 
	 * @return Cadena de caracteres.
	 */
	public String irusRegistroMercantil(){
		return (getRecordData() != null) ? getRecordData().getIrus() : null;
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
	
	//*************************************************************************************
	//*************************************************************************************
	//********************************* S A L D O S ***************************************
	//*************************************************************************************
	//*************************************************************************************

	/**
	 * Devuelve el saldo deudor inicial de la o las cuentas indicadas. Si el saldo de la cuenta es
	 * negativo, devuelve cero.
	 * 
	 * @param accounts
	 *            Cadena de caracteres, es decir, entre comillas dobles.
	 * @return Número Decimal.
	 */
	public Double saldoDeudorInicial(String accounts) {
		try {
			SummaryCollection summaryCollection = getSummaryCollection(accounts);
			double saldo = CommonUtil.round(summaryCollection.getInitialDebit() - summaryCollection.getInitialCredit()); 
			return saldo > 0 ? saldo : ZERO;
		} catch (ManagerBeanException e) {
			return ZERO;
		}
	}

	/**
	 * Devuelve el sumatorio del debe entre los asientos de apertura y, explotación o cierre, sin incluir el valor de éstos. 
	 * 
	 * @param accounts
	 *            Cadena de caracteres, es decir, entre comillas dobles.
	 * @return Número Decimal.
	 */
	public Double variacionDebe(String accounts) {
		try {
			SummaryCollection summaryCollection = getSummaryCollection(accounts);
			return CommonUtil.round(summaryCollection.getDebit() - summaryCollection.getInitialDebit());
		} catch (ManagerBeanException e) {
			return ZERO;
		}
	}

	/**
	 * Devuelve el saldo deudor de la o las cuentas indicadas. Si el saldo de la cuenta es
	 * negativo, devuelve cero.
	 * 
	 * @param accounts
	 *            Cadena de caracteres, es decir, entre comillas dobles.
	 * @return Número Decimal.
	 */
	public Double saldoDeudor(String accounts) {
		try {
			if ("600".equals(accounts)) {
				System.out.println(accounts);
			}
			SummaryCollection summaryCollection = getSummaryCollection(accounts);
			return summaryCollection.getUnpaidBalance();
		} catch (ManagerBeanException e) {
			//TODO ERROR.
			return ZERO;
		}
	}


	/**
	 * Devuelve el saldo deudor inicial del ejercicio anterior, de la o las cuentas indicadas. Si el saldo de la cuenta es
	 * negativo, devuelve cero.
	 * 
	 * @param accounts
	 *            Cadena de caracteres, es decir, entre comillas dobles.
	 * @return Número Decimal.
	 */
	public Double saldoDeudorInicialAnterior(String accounts) {
		try {
			SummaryCollection summaryCollection = getPreviousSummaryCollection(accounts);
			double saldo = CommonUtil.round(summaryCollection.getInitialDebit() - summaryCollection.getInitialCredit()); 
			return saldo > 0 ? saldo : ZERO;
		} catch (ManagerBeanException e) {
			return ZERO;
		}
	}

	/**
	 * Devuelve el sumatorio del debe entre los asientos de apertura y, explotación o cierre, sin incluir el valor de éstos, del ejericio anterior. 
	 * 
	 * @param accounts
	 *            Cadena de caracteres, es decir, entre comillas dobles.
	 * @return Número Decimal.
	 */
	public Double variacionDebeAnterior(String accounts) {
		try {
			SummaryCollection summaryCollection = getPreviousSummaryCollection(accounts);
			return CommonUtil.round(summaryCollection.getDebit() - summaryCollection.getInitialDebit());
		} catch (ManagerBeanException e) {
			return ZERO;
		}
	}

	/**
	 * Devuelve el saldo deudor del ejercicio anterior, de la o las cuentas
	 * indicadas. Si el saldo de la cuenta es negativo, devuelve cero.
	 * 
	 * @param accounts
	 *            Cadena de caracteres, es decir, entre comillas dobles.
	 * @return Número Decimal.
	 */
	public Double saldoDeudorAnterior(String accounts) {
		try {
			if ("600".equals(accounts)) {
				System.out.println(accounts);
			}
			SummaryCollection summaryCollection = getPreviousSummaryCollection(accounts);
			return summaryCollection.getUnpaidBalance();
		} catch (ManagerBeanException e) {
			return ZERO;
		}
	}

	/**
	 * Devuelve el saldo acreedor de la o las cuentas indicadas. Si el saldo de la cuenta es
	 * negativo, devuelve cero.
	 * 
	 * @param accounts
	 *            Cadena de caracteres, es decir, entre comillas dobles.
	 * @return Número Decimal.
	 */
	public Double saldoAcreedor(String accounts) {
		try {
			SummaryCollection summaryCollection = getSummaryCollection(accounts);
			return summaryCollection.getCreditBalance();
		} catch (ManagerBeanException e) {
			return ZERO;
		}
	}

	/**
	 * Devuelve el sumatorio del haber entre los asientos de apertura y, explotación o cierre, sin incluir el valor de éstos. 
	 * 
	 * @param accounts
	 *            Cadena de caracteres, es decir, entre comillas dobles.
	 * @return Número Decimal.
	 */
	public Double variacionHaber(String accounts) {
		try {
			SummaryCollection summaryCollection = getSummaryCollection(accounts);
			return CommonUtil.round(summaryCollection.getCredit() - summaryCollection.getInitialCredit());
		} catch (ManagerBeanException e) {
			return ZERO;
		}
	}

	
	/**
	 * Devuelve el saldo acreedor inicial de la o las cuentas indicadas. Si el Saldo de la cuenta es
	 * negativo, devuelve cero.
	 * 
	 * @param accounts
	 *            Cadena de caracteres, es decir, entre comillas dobles.
	 * @return Número Decimal.
	 */
	public Double saldoAcreedorInicial(String accounts) {
		try {
			SummaryCollection summaryCollection = getSummaryCollection(accounts);
			double saldo = CommonUtil.round(summaryCollection.getInitialCredit() - summaryCollection.getInitialDebit()); 
			return saldo > 0 ? saldo : ZERO;
		} catch (ManagerBeanException e) {
			return ZERO;
		}
	}


	/**
	 * Devuelve el saldo acreedor del ejercicio anterior, de la o las cuentas
	 * indicadas. Si el saldo de la cuenta es negativo, devuelve cero.
	 * 
	 * @param accounts
	 *            Cadena de caracteres, es decir, entre comillas dobles.
	 * @return Número Decimal.
	 */
	public Double saldoAcreedorAnterior(String accounts) {
		try {
			SummaryCollection summaryCollection = getPreviousSummaryCollection(accounts);
			return summaryCollection.getCreditBalance();
		} catch (ManagerBeanException e) {
			// TODO ERROR.
			return ZERO;
		}
	}	

	/**
	 * Devuelve el sumatorio del haber entre los asientos de apertura y, explotación o cierre, sin incluir el valor de éstos del ejercicio anterior. 
	 * 
	 * @param accounts
	 *            Cadena de caracteres, es decir, entre comillas dobles.
	 * @return Número Decimal.
	 */
	public Double variacionHaberAnterior(String accounts) {
		try {
			SummaryCollection summaryCollection = getPreviousSummaryCollection(accounts);
			return CommonUtil.round(summaryCollection.getCredit() - summaryCollection.getInitialCredit());
		} catch (ManagerBeanException e) {
			return ZERO;
		}
	}

	
	/**
	 * Devuelve el saldo acreedor inicial del ejercicio anterior de la o las cuentas indicadas. Si el Saldo de la cuenta es
	 * negativo, devuelve cero.
	 * 
	 * @param accounts
	 *            Cadena de caracteres, es decir, entre comillas dobles.
	 * @return Número Decimal.
	 */
	public Double saldoAcreedorInicialAnterior(String accounts) {
		try {
			SummaryCollection summaryCollection = getPreviousSummaryCollection(accounts);
			double saldo = CommonUtil.round(summaryCollection.getInitialCredit() - summaryCollection.getInitialDebit()); 
			return saldo > 0 ? saldo : ZERO;
		} catch (ManagerBeanException e) {
			return ZERO;
		}
	}
}
