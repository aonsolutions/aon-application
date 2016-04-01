package com.code.aon.ui.loader.factory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.account.Account;
import com.code.aon.accounting.Period;
import com.code.aon.accounting.enumeration.AccountPeriodStatus;
import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.company.Company;
import com.code.aon.company.Enterprise;
import com.code.aon.company.WorkPlace;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.Domain;
import com.code.aon.config.Tax;
import com.code.aon.geozone.GeoZone;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.enumeration.AddressType;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.registry.enumeration.RegistryType;
import com.code.aon.registry.enumeration.StreetType;
import com.code.aon.ui.loader.Column;
import com.code.aon.ui.loader.ILoaderEngine;
import com.code.aon.ui.loader.ILoaderFactory;
import com.code.aon.ui.loader.LoaderParams;
import com.code.aon.ui.loader.LoaderUtils;
import com.code.aon.ui.loader.pojo.ILoadedPojo;
import com.code.aon.ui.loader.pojo.LoadedCompany;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.watson.util.AonStringUtils;

public class CompanyLoaderFactory extends RegistryLoaderFactory implements ILoaderFactory<ILoadedPojo>{
	
	private static final Column[] SUPPORTED_COLUMNS = {
			 
			 new Column(EMP,"razonSocial"		,2,64	,true	,null)
			,new Column(EMP,"alias"				,2,32	,false	,null)
			,new Column(EMP,"tipoDocumento"		,0,1	,true	,new int[] {0,1,2,3,4,5,6})
			,new Column(EMP,"paisDocumento"		,2,2	,true	,null)
			,new Column(EMP,"documento"			,2,16	,true	,null)
			,new Column(EMP,"tipoVia"			,2,2	,false	,null)
			,new Column(EMP,"direccion"			,2,128	,false	,null)
			,new Column(EMP,"numero"			,2,6	,false	,null)
			,new Column(EMP,"direccion2"		,2,128	,false	,null)
			,new Column(EMP,"direccion3"		,2,128	,false	,null)
			,new Column(EMP,"cp"				,2,16	,false	,null)
			,new Column(EMP,"ciudad"			,2,64	,false	,null)
			,new Column(EMP,"provincia"			,2,3	,false	,null)
			,new Column(EMP,"nombreProvincia"	,2,32	,false	,null)			
			,new Column(EMP,"telefono1"			,2,64	,false	,null)			
			,new Column(EMP,"fax"				,2,64	,false	,null)
			,new Column(EMP,"email"				,2,64	,false	,null)
			,new Column(EMP,"web"				,2,64	,false	,null)
			,new Column(EMP,"codAdmon"			,0,5	,false	,null)
			,new Column(EMP,"nombreContacto"	,2,128	,false	,null)
			,new Column(EMP,"telefonoContacto"	,2,128	,false	,null)
			,new Column(EMP,"regimenFiscal"		,0,1	,false	,new int[] {0,1,2,3}) // Regimen Fiscal 
			,new Column(EMP,"devMensual"		,0,1	,false	,new int[] {0,1})     // Inscrito en el Registro de Devolución Mensual
			,new Column(EMP,"recc"				,0,1	,false	,new int[] {0,1})  	  // Régimen Especial del Criterio de Caja
            ,new Column(EMP,"re"				,0,1	,false	,new int[] {0,1})     // Régimen Especial del Recargo de Equivalencia 
			,new Column(EMP,"retencion"			,0,1	,false	,new int[] {0,1})     // Retención IRPF en Factura de Venta
			,new Column(EMP,"pagosPrestamos"    ,0,1	,false	,new int[] {0,1})     // Mod 130, 131, dedica pagos prestamos para compra o rehabilitacion vivienda habitual
			,new Column(EMP,"tipoPres111"		,2,1	,false	,null) // Tipo Presentacion Modelo 111 (N, M, Q)
			,new Column(EMP,"tipoPres115"		,2,1	,false	,null) // Tipo Presentacion Modelo 115 (N, M, Q)
			,new Column(EMP,"tipoPres123"		,2,1	,false	,null) // Tipo Presentacion Modelo 123 (N, M, Q)
			,new Column(EMP,"tipoPres130"		,2,1	,false	,null) // Tipo Presentacion Modelo 130 (N, Q)
			,new Column(EMP,"tipoPres131"		,2,1	,false	,null) // Tipo Presentacion Modelo 131 (N, Q)
			,new Column(EMP,"tipoPres180"		,2,1	,false	,null) // Tipo Presentacion Modelo 180 (N, Y)
			,new Column(EMP,"tipoPres190"		,2,1	,false	,null) // Tipo Presentacion Modelo 190 (N, Y)
			,new Column(EMP,"tipoPres303RG"		,2,1	,false	,null) // Tipo Presentacion Modelo 303RG (N, M, Q)
			,new Column(EMP,"tipoPres303RS"		,2,1	,false	,null) // Tipo Presentacion Modelo 303RS (N, M, Q)
			,new Column(EMP,"tipoPres349"		,2,1	,false	,null) // Tipo Presentacion Modelo 349 (N, M, Q)
			,new Column(EMP,"tipoPres347"		,2,1	,false	,null) // Tipo Presentacion Modelo 347 (N, Y)
			,new Column(EMP,"tipoPres390"		,2,1	,false	,null) // Tipo Presentacion Modelo 390 (N, Y)
			,new Column(EMP,"codigoMunicipio"	,0,5	,false	,null)  // Código municipio
			,new Column(EMP,"porcentajeIva"		,1,16	,false	,null)  // Porcentaje IVA por defecto
			,new Column(EMP,"cuentaVentas"		,2,9	,false	,null)  // Cuenta de ventas por defecto
			,new Column(EMP,"cuentaCompras"		,2,9	,false	,null)  // Cuenta compras por defecto
			,new Column(EMP,"fechaLimite"		,3,10	,false	,null)  // Fecha limite de operaciones
			,new Column(EMP,"ejercicioActual"	,0,4	,false	,null)  // Ejercicio Actual
			,new Column(EMP,"fechaIniActual"	,3,10	,false	,null)  // Fecha Inicio Ejercicio Actual
			,new Column(EMP,"fechaFinActual"	,3,10	,false	,null)  // Fecha Fin Ejercicio Actual
			,new Column(EMP,"ejercicioAnterior"	,0,4	,false	,null)  // Ejercicio Anterior
			,new Column(EMP,"fechaIniAnterior"	,3,10	,false	,null)  // Fecha Inicio Ejercicio Anterior
			,new Column(EMP,"fechaFinAnterior"	,3,10	,false	,null)  // Fecha Fin Ejercicio Anterior
			 
	};

	private Map<String, Column[]> columns;
	//private ILoaderEngine engine;

	public CompanyLoaderFactory() {
	}
	public CompanyLoaderFactory(ILoaderEngine engine) {
		//this.engine = engine;
	}

	public Map<String, Column[]> getColumns() {
		return columns;
	}

	@Override
	public boolean accept(String obj) {
		return StringUtils.equals(obj,EMP);
	}
	
	@Override
	public boolean accept(Class<? extends ILoadedPojo> clazz) {
		return ClassUtils.isAssignable(clazz, LoadedCompany.class);
	}

	@Override
	public String getKey() {
		return EMP;
	}

	@Override
	public Column[] getSupportedColumns() {
		return SUPPORTED_COLUMNS;
	}

	@Override
	public LoadedCompany getTargetBean() {
		return new LoadedCompany();
	}

	@Override
	public ITransferObject get(Integer id) throws AonException {
		IManagerBean bean = BeanManager.getManagerBean(Company.class);		
		return bean.get(id);
	}

	@Override
	public Integer insert(LoaderParams params,ILoadedPojo loadedPojo) throws AonException {
		
		LoadedCompany loaded = (LoadedCompany) loadedPojo;
		IManagerBean bean = BeanManager.getManagerBean(Company.class);
		
		Integer domainId = DomainManager.getCurrentDomain();
		Criteria criteria = new Criteria();            
        criteria.addEqualExpression(bean.getFieldName(IEntityAlias.COMPANY_DOMAIN), domainId);
        List<ITransferObject> l = bean.getList(criteria);
        
        // Si el registro en Company, ya existe, se sobreescriben los datos
        Company company = null;
        
        if (l!=null && l.size()>0) {
        	company = (Company) l.get(0);
        }
        else {
        	company = new Company();        	
        }
		company.setDocumentCountry(loaded.getDocumentCountry());
		company.setDocumentType(loaded.getDocumentType());
		company.setDocument(loaded.getDocumento());
		if (company.getRegistryDocument() != null) {
			if (company.getRegistryDocument().isValidNIF() 
				|| company.getRegistryDocument().isValidNIE()) {
				company.setType(RegistryType.NATURAL);
			} else if (company.getRegistryDocument().isValidCIF()) {
				company.setType(RegistryType.LEGAL);	
			}
		}	
		company.setAlias(loaded.getAlias());
		company.setNationality(loaded.getNationality());
		company.setName(loaded.getRazonSocial());
		company.setSecurityLevel(params.getSecurityLevel() == null ? SecurityLevel.OFFICIAL : params.getSecurityLevel());
		company.setSurcharge(loaded.isSurcharge());                  // Aplica Recargo de equivalencia
		company.setWithholding(loaded.isWithholding());              // Aplica retencion IRPF
		company.setVatAccrualPayment(loaded.isVatAccrualPayment());  // Aplica RECC
		
		company = (Company) bean.insertOrUpdate(company);
		
		// Actualizar la descripción del dominio, con el nombre de la empresa
		IManagerBean bd = BeanManager.getManagerBean(Domain.class);
		Criteria cd = new Criteria();            
        cd.addEqualExpression(bd.getFieldName(IEntityAlias.DOMAIN_ID), domainId);       
        List<ITransferObject> ld = bd.getList(cd);
 
        Domain domain = null;
        
        if (ld!=null && ld.size()>0) {
        	domain = (Domain) ld.get(0);
        	domain.setDescription(loaded.getRazonSocial());
        	bd.update(domain);        	
        }

		// Direccion
		insertRegistryAddressCompany(company.getRegistry(),loaded,domainId,params);
		
		// Telefono
		insertRegistryMediaCompany(company.getRegistry(),MediaType.FIXED_PHONE,loaded.getTelefono1());
		
		// Fax
		insertRegistryMediaCompany(company.getRegistry(),MediaType.FAX,loaded.getFax());		
		
		// Direccion email
		insertRegistryMediaCompany(company.getRegistry(),MediaType.EMAIL, loaded.getEmail());		
		
		// Direccion internet
		insertRegistryMediaCompany(company.getRegistry(),MediaType.WEB,loaded.getWeb());
		
		// Parametros Fiscales
		insertApplicationParameter(domainId,"FS_TAX_REGIME",loaded.getRegimenFiscalStr());        // Régimen Fiscal
		insertApplicationParameter(domainId,"FS_TAX_REFUND_REGISTRY",loaded.getDevMensualStr());  // Inscrito en el registro de devolucion mensual (0,1)
		insertApplicationParameter(domainId,"FS_DEFAULT_ADMINISTRATION", "4"); 		             // Administración por defecto Territorio Común (4)
		insertApplicationParameter(domainId,"FS_ADMINISTRATION_CODE",loaded.getCodAdmonStr());    // Código Administración
		insertApplicationParameter(domainId,"FS_PERM_ADDRESS_CHANGES",loaded.getPagosPrestamosStr()); // Mod. 130 o 131 Pagos de prestamos para rehabilitación vivienda habitual  
		insertApplicationParameter(domainId,"FS_CONCTACT_PERSON",loaded.getNombreContacto());     // Nombre persona de contacto
		insertApplicationParameter(domainId,"FS_CONCTACT_PHONE",loaded.getTelefonoContacto());    // Teléfono persona de contacto		
		insertApplicationParameter(domainId,"FS_MODEL_CFG_M111",loaded.getTipoPres111());
		insertApplicationParameter(domainId,"FS_MODEL_CFG_M115",loaded.getTipoPres115());
		insertApplicationParameter(domainId,"FS_MODEL_CFG_M123",loaded.getTipoPres123());
		insertApplicationParameter(domainId,"FS_MODEL_CFG_M130",loaded.getTipoPres130());
		insertApplicationParameter(domainId,"FS_MODEL_CFG_M131",loaded.getTipoPres131());
		insertApplicationParameter(domainId,"FS_MODEL_CFG_M180",loaded.getTipoPres180());
		insertApplicationParameter(domainId,"FS_MODEL_CFG_M190",loaded.getTipoPres190());
		insertApplicationParameter(domainId,"FS_MODEL_CFG_M303_RG",loaded.getTipoPres303RG());
		insertApplicationParameter(domainId,"FS_MODEL_CFG_M303_RS",loaded.getTipoPres303RS());
		insertApplicationParameter(domainId,"FS_MODEL_CFG_M349",loaded.getTipoPres349());
		insertApplicationParameter(domainId,"FS_MODEL_CFG_M347",loaded.getTipoPres347());		
		insertApplicationParameter(domainId,"FS_MODEL_CFG_M390",loaded.getTipoPres390());
		
		if ( loaded.getPorcentajeIva() != null && loaded.getPorcentajeIva() != 0 ) {
			Tax vat = getLoaderUtils().ensureVat(loaded.getPorcentajeIva());
			if (vat.getId() != null)
				insertApplicationParameter(domainId,"ACC_DEFAULT_VAT_PERCENT", vat.getId().toString() );
		}
		
		LoaderUtils lu = new LoaderUtils();
		Account account = null;
		
		if (StringUtils.isNotBlank(loaded.getCuentaVentas())) {
			account = lu.ensureAccount(loaded.getCuentaVentas(), "");
			if (account.getId() != null) 
				insertApplicationParameter(domainId,"ACC_DEFAULT_SALES_ACC",account.getId().toString());						
		}
		if (StringUtils.isNotBlank(loaded.getCuentaCompras())) {
			account = lu.ensureAccount(loaded.getCuentaCompras(), "");
			if (account.getId() != null) 
				insertApplicationParameter(domainId,"ACC_DEFAULT_PURCHASE_ACC",account.getId().toString());						
		}
		
		if (loaded.getFechaLimite() != null) {
			insertApplicationParameter(domainId,"ACC_OPERATIONS_DEADLINE",new SimpleDateFormat("dd/MM/yyyy").format(loaded.getFechaLimite()));
		}
		
		// Crear los ejercicios actual y anterior, si llevan contenido
		insertPeriod(loaded.getEjercicioActual(), loaded.getFechaIniActual(), loaded.getFechaFinActual());
		insertPeriod(loaded.getEjercicioAnterior(), loaded.getFechaIniAnterior(), loaded.getFechaFinAnterior());
		
		return company.getId();
	}
	
	private void insertRegistryAddressCompany(Registry registry, LoadedCompany loaded, Integer domainId, LoaderParams params) throws ManagerBeanException {
		
		// Se comprueba si ya existe la direccion principal, si existe se actualiza, sino se añade
		
		IManagerBean bean = BeanManager.getManagerBean(RegistryAddress.class);
		
		Criteria criteria = new Criteria();            
        criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_ADDRESS_REGISTRY_ID), registry.getId());
        criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_ADDRESS_ADDRESS_TYPE), AddressType.MAIN);  // Principal
        
        List<ITransferObject> list = bean.getList(criteria);
		
        RegistryAddress address = null;
        
        if (list==null || list.size()==0) {
        	address = new RegistryAddress();        	
        }
        else {
        	address = (RegistryAddress) list.get(0);
        }	
        
		address.setRegistry(registry);
		if (StringUtils.isNotBlank(loaded.getTipoVia())) {
			StreetType streetType = null;
			try {
				streetType = StreetType.valueOf(loaded.getTipoVia());
				address.setStreetType(streetType);
			} catch (Throwable t) {
				// Si la sigla es erronea, no se graba nada
				address.setStreetType(null);
			}
		}		
		address.setAddress(loaded.getDireccion());
		address.setNumber(loaded.getNumero());
		address.setAddress2(loaded.getDireccion2());
		address.setAddress3(loaded.getDireccion3());
		address.setZip(loaded.getCp());
		address.setCity(loaded.getCiudad());
		
		try {
			if (StringUtils.isNotBlank( loaded.getProvincia()) || StringUtils.isNotBlank( loaded.getNombreProvincia())) {
				GeoZone geozone = ensureGeoZone(loaded.getPais(),loaded.getProvincia(),loaded.getNombreProvincia() );
				address.setGeozone(geozone);
			}
		} catch (Exception e) {
			// Si no está indicado el codigo de provincia o es erroneo, o la provincia es erronea (no es exactamente como esta definida en AON)
			// se pone la provincia a continuacion de la ciudad, en el campo de la ciudad
			if (StringUtils.isNotBlank(loaded.getNombreProvincia()))
				address.setCity(loaded.getCiudad()+" ("+loaded.getNombreProvincia()+")");
		}
		
		if (StringUtils.isNotBlank(loaded.getCodigoMunicipio()))
			address.setMunicipalityCode(loaded.getCodigoMunicipio());
		address = (RegistryAddress) bean.insertOrUpdate(address);
		
		// Comprobar si existe el centro de trabajo, si no existe se crea uno como "PRINCIPAL"
		IManagerBean bwp = BeanManager.getManagerBean(WorkPlace.class);
		Criteria cwp = new Criteria();            
        cwp.addEqualExpression(bwp.getFieldName(IEntityAlias.WORK_PLACE_DOMAIN), domainId);       
        List<ITransferObject> lwp = bwp.getList(cwp);
        
        if (lwp==null || lwp.size()==0) {
        	
        	// Obtener el registro de Enterprise del dominio
    		IManagerBean beanEnterprise = BeanManager.getManagerBean(Enterprise.class);
    		
    		Criteria criteriaEnterprise = new Criteria();            
            criteriaEnterprise.addEqualExpression(beanEnterprise.getFieldName(IEntityAlias.ENTERPRISE_DOMAIN), DomainManager.getCurrentDomain());
            List<ITransferObject> listEnterprise = beanEnterprise.getList(criteriaEnterprise);
            
            Enterprise ep = (Enterprise) listEnterprise.get(0);
        	        	
        	// Añadir el centro de trabajo
            WorkPlace wp = new WorkPlace();
        	wp.setDomain(domainId);
        	wp.setAddress(address);
        	wp.setActive(true);
        	wp.setDescription("PRINCIPAL");
        	wp.setScope(params.getScope());
        	wp.setEnterprise(ep);       	
        	bwp.insert(wp);        	
        }
        
	}
		
	private void insertRegistryMediaCompany(Registry registry, MediaType type, String value) throws ManagerBeanException {
		
		IManagerBean bean = BeanManager.getManagerBean(RegistryMedia.class);
		
		// Se busca si existe el tipo indicado, si ya existe se actualizan sus datos y si no existe se añade
		Criteria criteria = new Criteria();            
        criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_MEDIA_REGISTRY_ID ), registry.getId());
        criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_MEDIA_MEDIA_TYPE), type ); 
        
        List<ITransferObject> list = bean.getList(criteria);
		
        RegistryMedia rmedia = null;
        if (list==null || list.size() == 0) {
        	rmedia = new RegistryMedia();        	
        }
        else {        	        	
        	rmedia = (RegistryMedia) list.get(0);        	
        }
		rmedia.setRegistry(registry);
		rmedia.setMediaType(type);
		rmedia.setAdministrative(false);
		rmedia.setCommercial(false);
		rmedia.setTechnical(false);
		rmedia.setValue(value);
		bean.insertOrUpdate(rmedia);		
	}
	
	private void insertApplicationParameter(Integer domain, String name, String value) throws ManagerBeanException {
		
		// Si esta vacio en el fichero de carga, no se hace nada		
		if (StringUtils.isBlank(value))
			return;
		
		// Comprobar si el parametro ya existe, si existe se actualiza, sino se añade
		IManagerBean bean = BeanManager.getManagerBean(ApplicationParameter.class);
		
		Criteria criteria = new Criteria();            
        criteria.addEqualExpression(bean.getFieldName(IEntityAlias.APPLICATION_PARAMETER_DOMAIN), domain );
        criteria.addEqualExpression(bean.getFieldName(IEntityAlias.APPLICATION_PARAMETER_NAME), name ); 
        
        List<ITransferObject> list = bean.getList(criteria);
		
        ApplicationParameter appParam = null;
        if (list==null || list.size() == 0) {
        	appParam = new ApplicationParameter();
        }
        else {
        	appParam = (ApplicationParameter) list.get(0);
        }
		appParam.setName(name);
		appParam.setValue(value);
		bean.insertOrUpdate(appParam);
		
	}
	
	public void insertPeriod(String name, Date iniDate, Date endDate) throws ManagerBeanException {
		
		if (AonStringUtils.isNotBlank(name)) {
			
			// Comprobar si existe el ejercicio, para actualizar sus datos, o añadirlo
			
			IManagerBean periodBean = BeanManager.getManagerBean(Period.class);			
            Criteria criteria = new Criteria();            
            criteria.addEqualExpression(periodBean.getFieldName(IEntityAlias.PERIOD_NAME), name);
            List<ITransferObject> l = periodBean.getList(criteria);
            
            Period p = null;
            if (l != null && l.size() > 0) {
            	p = (Period) l.get(0);
            	periodBean.update(p);
            }
            else {
            	p = new Period();
    			p.setStatus(AccountPeriodStatus.ACTIVE);
            }
            p.setName(name);
			p.setInitiationDate(iniDate);
			p.setDeadline(endDate);
			periodBean.insertOrUpdate(p);	
			
		}
		
	}

	@Override
	public ITransferObject get(LoaderParams params, ILoadedPojo loadedPojo) throws AonException {
		return null;
	}
	
	@Override
	public void validate(LoaderParams params) throws AonException {
		// Nothing to validate.
	}
	
}
