package com.esferalia.aon.dsi.nominas;

import static com.esferalia.aon.dsi.nominas.Traspaso.getGeozone;
import static com.esferalia.aon.dsi.nominas.Traspaso.getMunicipalityCode;
import static com.esferalia.aon.dsi.nominas.Traspaso.getParentDomain;
import static com.esferalia.aon.dsi.nominas.TraspasoCalendarios.buscarCalendario;
import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.AgreementData.AGREEMENT_DATA;
import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;
import static com.esferalia.aon.jooq.tables.Calendar.CALENDAR;
import static com.esferalia.aon.jooq.tables.Cnae2009.CNAE2009;
import static com.esferalia.aon.jooq.tables.Company.COMPANY;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.DomainApplication.DOMAIN_APPLICATION;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.Iae.IAE;
import static com.esferalia.aon.jooq.tables.PayrollWorkplace.PAYROLL_WORKPLACE;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Rbank.RBANK;
import static com.esferalia.aon.jooq.tables.RdirStaff.RDIR_STAFF;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Rmedia.RMEDIA;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.jooq.tools.StringUtils;

import com.code.aon.registry.enumeration.DocumentType;
import com.esferalia.aon.dsi.nominas.dao.EmpresaDAO;
import com.esferalia.aon.dsi.nominas.model.Banco;
import com.esferalia.aon.dsi.nominas.model.Calendario;
import com.esferalia.aon.dsi.nominas.model.Centro;
import com.esferalia.aon.dsi.nominas.model.Empresa;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class TraspasoEmpresas {

	private static DSLContext ctx;
	private static int domain;
	private static int registry;
	
	public static void execute(Connection dsiConn, String domainName, int parentDom, String user) throws SQLException {
		
		// Leemos las empresas de Nóminas Omega (solo las que no tienen fecha de baja)
		LinkedList<Empresa> empresas = EmpresaDAO.select(dsiConn);

		// Leemos todas las empresas de Omega (cuentas de cotizacion), ordenadas por cif + ccc_principal
		// Para cada cif distinto, se comprueba si ese cif existe como dominio y si no existe
		// se crea el dominio añadiendo los registros en las tablas necesarias
		// Si el dominio ya existe, se borran los datos de laboral, y se actualizan el resto de datos
		String cifEmpresa = null; 
		int totalEmp = 0;
// for (int z=1;z<=6;z++)  // PRUEBA PARA PROVOCAR ERROR EN LOCAL
	
		for (Empresa empresa : empresas) {
			
			totalEmp++;
			Traspaso.info(empresa.toString());	
			
			// CIF de la empresa, los primeros 9 caracteres (en Omega el campo es hasta 10)
			String cif = AonStringUtils.left(empresa.getNif(), 9);
			
			// Comprobar si cambia el CIF
			boolean cambio = (cifEmpresa == null || !cifEmpresa.equals(cif));
			
			// Guardar CIF para la siguiente iteracion
	     	cifEmpresa = cif;
			
			try (CloseableAONContext aonCtx = AONContext.getAONContext(domainName, parentDom, user)) {
			
				ctx = aonCtx.getDslContext();	
						
				aonCtx.transaction( configuration -> {
						
					if (cambio) {
						// Añadir el dominio, si no existe (segun el cif) 
						addDomain(empresa);
						
						// Entidad AT 
					    addAppParam("PAY_ss_mutual_PAY", empresa.getEntiat());
		
					    // Cuentas Contables			    
				     	addAppParam("ACC_DEFAULT_COMPANY_SOC_INS_ACC", AonNumberUtils.toString(addAccount(empresa.getCtasegs(), "SEGURIDAD SOCIAL A CARGO DE LA EMPRESA")));		    
				     	addAppParam("ACC_DEFAULT_SOCIAL_INSURANCE_ACC", AonNumberUtils.toString(addAccount(empresa.getCtassac(), "SEGURIDAD SOCIAL ACREEDORES")));		    
				     	addAppParam("ACC_SALARY_CHARGED_RET_ACC", AonNumberUtils.toString(addAccount(empresa.getCtahacp(), "HACIENDA PUBLICA, ACREEDOR POR RETENCIONES")));
				     	addAppParam("ACC_DEFAULT_CASH_ACC", AonNumberUtils.toString(addAccount(empresa.getCtacaja(), "CAJA")));
				     	addAppParam("ACC_DEFAULT_SALARY_ACC", AonNumberUtils.toString(addAccount(empresa.getCtasuel(), "SUELDOS Y SALARIOS")));
				     	addAppParam("ACC_SALARY_DED_ADV_PAYMENT_ACC", AonNumberUtils.toString(addAccount(empresa.getCtaanti(), "ANTICIPOS DE REMUNERACIONES")));
				     	addAppParam("ACC_DEFAULT_PENDING_SALARY_ACC", AonNumberUtils.toString(addAccount(empresa.getCtapend(), "REMUNERACIONES PENDIENTES DE PAGO")));			    
		
					}
		
					// Añadir Direccion y Teléfono de la empresa 
					int address = addAddress(empresa);
					
					// Añadir Representante Laboral y Representante Fiscal (si son iguales en Omega, se añade un solo registro en AON)
					if (AonStringUtils.equals(empresa.getNomlab(), empresa.getNomfis()) && AonStringUtils.equals(empresa.getNiflab(), empresa.getNiffis())) {
						addStaff((byte) 1, (byte) 1, empresa.getNiflab(), empresa.getNomlab());
					} else {
						addStaff((byte) 0, (byte) 1, empresa.getNiflab(), empresa.getNomlab()); // Añadir Representante Laboral
						addStaff((byte) 1, (byte) 0, empresa.getNiffis(), empresa.getNomfis()); // Añadir Representante Fiscal
					}
					
					// Añadir Actividad y CCC
					addActivityAndCCC(empresa);
					
					// Añadir Centros de Trabajo. Se añade un centro de trabajo por el registro de la 
					// empresa de Omega y ademas los centros de trabajo que haya en la empresa de Omega
					String nombre = AonStringUtils.trimToEmpty(empresa.getDomicil()).toUpperCase();			
					if (nombre.isEmpty())
						nombre = "CENTRO";
					empresa.setWorkplace(addWorkplace(nombre, address, empresa));
					
					for (Centro centro : empresa.getCentros()) {
						nombre = AonStringUtils.trimToEmpty(centro.getSituacion()).toUpperCase();				
						centro.setWorkplace(addWorkplace(nombre, addAddress(centro), empresa));				
					}
					
					// Bancos
					for (Banco banco : empresa.getBancos()) {
						addBank(banco);
					}
					
					// Trabajadores
					TraspasoTrabajadores.execute(dsiConn, aonCtx, empresa, domain);
				});
			}
		}

		Traspaso.info("TOTAL EMPRESAS = "+totalEmp);
		
	}	
	
	// Añade el dominio (si no existe)
	private static int addDomain(Empresa empresa) {
		
		// Comprobar si el dominio existe, según el cif de la empresa (nueve caracteres)
		String cif = AonStringUtils.left(empresa.getNif(), 9);
		
		Record domainRecord = ctx.select()
								.from(DOMAIN
								.join(REGISTRY).on(DOMAIN.ID.eq(REGISTRY.DOMAIN))
								.join(COMPANY).on(REGISTRY.ID.eq(COMPANY.REGISTRY)))
								.where(DOMAIN.PARENT.eq(getParentDomain()).and(REGISTRY.DOCUMENT.eq(cif)))
								.fetchAny();
		
		if (domainRecord == null) {
			
			String domainSuffix = ctx.select()
									.from(DOMAIN)
									.where(DOMAIN.ID.eq(getParentDomain()))
									.fetchOne(DOMAIN.SUBDOMAINSUFFIX);
			
			String domainName = cif.toLowerCase()+ "-" + domainSuffix;			
			String creationUser = "traspaso"; 
			String owner = ""; 
			
			// Añadir domain
			domain =  ctx.insertInto(DOMAIN)
						.set(DOMAIN.CREATION_USER, creationUser)
						.set(DOMAIN.CREATION_DATE, new java.sql.Timestamp(System.currentTimeMillis()))
						.set(DOMAIN.DOMAINMANAGEMENT, (byte) 0)
						.set(DOMAIN.TYPE, (byte) 0)
						.set(DOMAIN.PARENT, getParentDomain())
						.set(DOMAIN.OWNER, owner)
						.set(DOMAIN.NAME, domainName)
						.set(DOMAIN.DESCRIPTION, empresa.getRsocial())
						.set(DOMAIN.ENABLEHEREDITY, (byte) 1)
						.set(DOMAIN.MAXDEFINEDUSERS, 0)
						.set(DOMAIN.MAXDOCUMENTSIZE, 1)
						.set(DOMAIN.MAXTOTALDOCUMENTSIZE, 16)					
						.returning(DOMAIN.ID)
						.fetchOne()
						.getId();
			
			// Se marca el dominio como que ha sido creado por el traspaso de Nominas Omega, se podrá utilizar
			// posteriormente, si se quiere volver a traspasar el mismo dominio, para borrar todos sus datos previamente
			addAppParam("TRASPASO_NOMINAS_OMEGA", "1");
			
			// Añadir domain_application
			ctx.insertInto(DOMAIN_APPLICATION)
				.set(DOMAIN_APPLICATION.DOMAIN, domain)
				.set(DOMAIN_APPLICATION.APPLICATION, 28)
				.set(DOMAIN_APPLICATION.ACTIVE, (byte) 1)
				.set(DOMAIN_APPLICATION.AUDIT_LEVEL, (byte) 0)
				.execute();	
			
			// Añadir registry
			registry = ctx.insertInto(REGISTRY)
						.set(REGISTRY.DOMAIN, domain)
						.set(REGISTRY.DOCUMENT, cif)
						.set(REGISTRY.DOCUMENT_TYPE, getDocumentType(cif))
						.set(REGISTRY.NAME, empresa.getRsocial())
						.set(REGISTRY.TYPE, (byte) 1)
						.returning(DOMAIN.ID)
						.fetchOne()
						.getId();
			
			// Añadir company
			ctx.insertInto(COMPANY)
				.set(COMPANY.REGISTRY, registry)
				.set(COMPANY.DOMAIN, domain)
				.set(COMPANY.ACTIVE,(byte) 1)
				.execute();
			
			// Añadir enterprise (scope se coge del dominio padre)
			Integer scope = ctx.selectFrom(ENTERPRISE)
								.where(ENTERPRISE.DOMAIN.eq(getParentDomain()))
								.fetchAny(ENTERPRISE.SCOPE);
			
			ctx.insertInto(ENTERPRISE)
				.set(ENTERPRISE.REGISTRY, registry)
				.set(ENTERPRISE.DOMAIN, domain)
				.set(ENTERPRISE.SCOPE, scope)
				.execute();
			
		} else {
			// El dominio ya existe, se guardan sus ids (domain y registry) y se borran sus datos de laboral
			domain = domainRecord.get(DOMAIN.ID);
			registry = domainRecord.get(REGISTRY.ID);
			
			// Borrar los datos del dominio, para volver a traspasarlo
			removeDomain();
		}
		
		return domain;
		
	}
	
	// Añadir variable a app_param, si no existe
	private static void addAppParam(String name, String value) {
		
		if (AonStringUtils.isBlank(value)) {
			return;
		}
		
		// Comprobar si la variable existe, si no existe se añade, si existe se actualiza su valor
		Integer id = ctx.select()
						.from(APP_PARAM)
						.where(APP_PARAM.DOMAIN.eq(domain)
						.and(APP_PARAM.NAME.eq(name)))
						.fetchAny(APP_PARAM.ID);
		
		if (id==null) {
			ctx.insertInto(APP_PARAM)			
				.set(APP_PARAM.DOMAIN, domain)
				.set(APP_PARAM.NAME, name)
				.set(APP_PARAM.VALUE, value)
				.execute();
		} else {
			ctx.update(APP_PARAM)						
				.set(APP_PARAM.VALUE, value)
				.where(APP_PARAM.ID.eq(id))
				.execute();
		}
		
	}
	
	// Añade una direccion (desde la empresa de Omega), si no existe 
	private static int addAddress(Empresa empresa) {

		// Buscar si existe la dirección (nombre via y código postal)
		Integer id = ctx.select()
						.from(RADDRESS)
						.where(RADDRESS.REGISTRY.eq(registry)
						.and(RADDRESS.ADDRESS.equalIgnoreCase(empresa.getDomicil()))
						.and(AonStringUtils.isBlank(empresa.getCp()) ? DSL.trueCondition() : RADDRESS.ZIP.eq(empresa.getCp())))
						.fetchAny(RADDRESS.ID);
			
		if (id == null) {
			String address2 = "";
			if (AonStringUtils.isNotBlank(empresa.getEscaler())) 
				address2 = address2 + "Esc. " + empresa.getEscaler();
			if (AonStringUtils.isNotBlank(empresa.getPiso()))
				address2 = address2 + " Piso " + empresa.getPiso();
			if (AonStringUtils.isNotBlank(empresa.getPuerta()))
				address2 = address2 + " Puerta " + empresa.getPuerta();
			address2 = address2.trim();
			
			// Comprobar si hay otra direccion principal, para añadir esta como principal o delegacion
			Integer mainAddress = ctx.select()
									.from(RADDRESS)
									.where(RADDRESS.REGISTRY.eq(registry)
									.and(RADDRESS.TYPE.eq((byte) 0)))
									.fetchAny(RADDRESS.ID);
			
			// El telefono, solo se añade cuando se añade la dirección, si es 
			// la dirección principal, si no se pone en el campo address3 de la direccion
			String address3 = "";
			if (mainAddress != null && AonStringUtils.isNotBlank(empresa.getTelef())) {
				address3 = "Teléfono " + empresa.getTelef().trim();
			}
			
			id = ctx.insertInto(RADDRESS)
					.set(RADDRESS.DOMAIN, domain)
					.set(RADDRESS.REGISTRY, registry)
					.set(RADDRESS.TYPE, mainAddress == null ? (byte) 0 : (byte) 1)  
					.set(RADDRESS.STREET_TYPE, empresa.getSg())
					.set(RADDRESS.ADDRESS, empresa.getDomicil())
					.set(RADDRESS.ADDRESS2, address2)
					.set(RADDRESS.ADDRESS3, address3)
					.set(RADDRESS.NUMBER, empresa.getNumero())
					.set(RADDRESS.ZIP, empresa.getCp())
					.set(RADDRESS.CITY, empresa.getPoblaci())
					.set(RADDRESS.GEOZONE, getGeozone(ctx, empresa.getCp(), empresa.getProvin()))
					.set(RADDRESS.MUNICIPALITY_CODE, getMunicipalityCode(empresa.getPoblaci(), empresa.getCp()))
					.returning(RADDRESS.ID)
					.fetchOne()
					.getId();	
			
			if (mainAddress == null && AonStringUtils.isNotBlank(empresa.getTelef())) {
				ctx.insertInto(RMEDIA)
				.set(RMEDIA.DOMAIN, domain)
				.set(RMEDIA.REGISTRY, registry)			
				.set(RMEDIA.MEDIA, (byte) 1)
				.set(RMEDIA.VALUE, empresa.getTelef())
				.set(RMEDIA.RADDRESS, id)
				.execute();			
			}
			
		}	
			
		return id;
				
	}
	
	// Añade una direccion (desde el centro de trabajo de Omega), si no existe (criterio de que exista: nombre via)
	private static int addAddress(Centro centro) {
		
		Integer id = ctx.select()
						.from(RADDRESS)
						.where(RADDRESS.REGISTRY.eq(registry)
						.and(RADDRESS.ADDRESS.equalIgnoreCase(centro.getSituacion())))
						.fetchAny(RADDRESS.ID);
			
		if (id == null) {
			
			id = ctx.insertInto(RADDRESS)
					.set(RADDRESS.DOMAIN, domain)
					.set(RADDRESS.REGISTRY, registry)
					.set(RADDRESS.TYPE, (byte) 1)  // La direccion asociada al centro de trabajo de Omega, siempre es de tipo "Delegación"
					.set(RADDRESS.STREET_TYPE, "")
					.set(RADDRESS.ADDRESS, centro.getSituacion())
					.returning(RADDRESS.ID)
					.fetchOne()
					.getId();				
		}	
			
		return id;
				
	}
	
	private static void addStaff(byte representative, byte representative_labor, String document, String name) {
		
		document = AonStringUtils.trimToEmpty(document);
		name = AonStringUtils.trimToEmpty(name);
		
		if (name.isEmpty() && document.isEmpty()) {
			return;
		}
		
		Integer id = ctx.select()
				.from(RDIR_STAFF)
				.where(RDIR_STAFF.REGISTRY.eq(registry)
				.and(RDIR_STAFF.REPRESENTATIVE.eq(representative)
				.and(RDIR_STAFF.REPRESENTATIVE_LABOR.eq(representative_labor)
				.and(RDIR_STAFF.DOCUMENT.equalIgnoreCase(document)
				.and(RDIR_STAFF.NAME.equalIgnoreCase(name))))))
				.fetchAny(RDIR_STAFF.ID);
		
		if (id == null) {
			ctx.insertInto(RDIR_STAFF)
				.set(RDIR_STAFF.DOMAIN, domain)
				.set(RDIR_STAFF.REGISTRY, registry)
				.set(RDIR_STAFF.REPRESENTATIVE, representative)
				.set(RDIR_STAFF.REPRESENTATIVE_LABOR, representative_labor)
				.set(RDIR_STAFF.DOCUMENT, document)
				.set(RDIR_STAFF.NAME, name)
				.execute();
		}
		
	}
	
	// Insertar actividad y el ccc asociado a la actividad
	private static void addActivityAndCCC(Empresa empresa) {

		// Comprobar si la actividad existe
		// Se comprueba por el CNAE2009 (Cotizacion ATEP en Omega), si está cumplimentado,
		// en caso contrario se comprueba por el nombre de la actividad en Omega, si este
		// ultimo no está cumplimentado se asume como nombre el literal "ACTIVIDAD"
		
		String nombreActividad = AonStringUtils.trimToEmpty(empresa.getNomact());
		if (nombreActividad.isEmpty()) {
			nombreActividad = "ACTIVIDAD";
		}
		
		Integer enterpriseActivity = null;
		if (AonStringUtils.isNotBlank(empresa.getCotatep())) {
			// Buscar por CNAE2009 (Cotizacion ATEP en Omega)
			enterpriseActivity = ctx.select()
									.from(ENTERPRISE_ACTIVITY)
									.join(CNAE2009).on(ENTERPRISE_ACTIVITY.CNAE2009.eq(CNAE2009.ID))
									.where(ENTERPRISE_ACTIVITY.DOMAIN.eq(domain)
									.and(ENTERPRISE_ACTIVITY.ENTERPRISE.eq(registry))
									.and(CNAE2009.CODE.startsWith(empresa.getCotatep())))				
									.fetchAny(ENTERPRISE_ACTIVITY.ID);
		} else {
			// Buscar por nombre
			enterpriseActivity = ctx.select()
									.from(ENTERPRISE_ACTIVITY)
									.where(ENTERPRISE_ACTIVITY.DOMAIN.eq(domain)
									.and(ENTERPRISE_ACTIVITY.ENTERPRISE.eq(registry))
									.and(ENTERPRISE_ACTIVITY.DESCRIPTION.eq(nombreActividad)))				
									.fetchAny(ENTERPRISE_ACTIVITY.ID);
		}
		
		if (enterpriseActivity == null) {
			
			// Comprobar si hay alguna actividad marcada como principal
			Integer mainActivity = ctx.select()
									.from(ENTERPRISE_ACTIVITY)
									.where(ENTERPRISE_ACTIVITY.DOMAIN.eq(domain)
									.and(ENTERPRISE_ACTIVITY.ENTERPRISE.eq(registry)))										
									.fetchAny(ENTERPRISE_ACTIVITY.ID);			
			
			enterpriseActivity = ctx.insertInto(ENTERPRISE_ACTIVITY)
									.set(ENTERPRISE_ACTIVITY.DOMAIN, domain)
									.set(ENTERPRISE_ACTIVITY.ENTERPRISE, registry)
									.set(ENTERPRISE_ACTIVITY.DESCRIPTION, nombreActividad)
									.set(ENTERPRISE_ACTIVITY.IAE, getIAE(empresa.getLicfis()))  
									.set(ENTERPRISE_ACTIVITY.TYPE, (byte) 0)
									.set(ENTERPRISE_ACTIVITY.CNAE2009, getCNAE2009(empresa.getCotatep()))
									.set(ENTERPRISE_ACTIVITY.START_DATE, AonDateUtils.toSql(empresa.getFalta()))
									.set(ENTERPRISE_ACTIVITY.PRINCIPAL, mainActivity == null ? (byte) 1 : (byte) 0) 
									.returning(ENTERPRISE_ACTIVITY.ID)
									.fetchOne()
									.getId();
 
		}
		
		// Añadir CCC (si no existe)
		Integer enterpriseCCC = ctx.select()
									.from(ENTERPRISE_CCC)
									.where(ENTERPRISE_CCC.ENTERPRISE_ACTIVITY.eq(enterpriseActivity))
									.and(ENTERPRISE_CCC.CCC.eq(empresa.getAonCCC()))
									.fetchAny(ENTERPRISE_CCC.ID);
		
		if (enterpriseCCC == null) {
			enterpriseCCC = ctx.insertInto(ENTERPRISE_CCC)
								.set(ENTERPRISE_CCC.DOMAIN, domain)
								.set(ENTERPRISE_CCC.ENTERPRISE_ACTIVITY, enterpriseActivity)
								.set(ENTERPRISE_CCC.CCC, empresa.getAonCCC())
								.set(ENTERPRISE_CCC.TYPE, getCCCType(empresa.getTipo()))
								.set(ENTERPRISE_CCC.GEOZONE, getGeozone(ctx, empresa.getSscod(), empresa.getProvin()))
								.returning(ENTERPRISE_CCC.ID)
								.fetchOne()
								.getId();
		}
		
		// Asignar los ids a la empresa (se utilizarán en el traspaso de los trabajadores)
		empresa.setEnterpriseActivity(enterpriseActivity);
		empresa.setEnterpriseCCC(enterpriseCCC);
		
	}
	
	// Devuelve el tipo del documento (cif, nif, ...) que se le pasa
	private static Byte getDocumentType(String doc){
		if ( StringUtils.isBlank(doc))
			return null;
		
		if ( doc.matches("(?i)[\\dKLM]\\d{7}\\w"))
			return (byte) DocumentType.NIF.ordinal();

		if ( doc.matches("(?i)[XYZ]\\d{7}\\w"))
			return (byte) DocumentType.NIE.ordinal();

		if ( doc.matches("(?i)[ABCDEFGHJNPQRSUVW]\\d{7}\\w"))
			return (byte) DocumentType.CIF.ordinal();
		
		return (byte) DocumentType.OTHER.ordinal();
	}

	
	// Devuelve el ID del CNAE que se le pasa
	private static Integer getCNAE2009(String cnae2009) {		
		
		return ctx.select(CNAE2009.ID)
						.from(CNAE2009)
						.where(CNAE2009.CODE.eq(cnae2009))
						.fetchAny(CNAE2009.ID);
		
	}
	
	// Devuelve el tipo CCC de AON, según el tipo CCC de Omega que se le pasa
	private static byte getCCCType(String tipoCCC) {
		
		switch (tipoCCC) {
			case "2": // Aprendizaje/Formación
				return 1; // TRAINING (Formación y aprendizaje)
			case "4": // Consejeros y Administradores
				return 4; // ASSIMILATEDS (Asimilados Regimen General)
			case "5": // Régimen Agrario
				return 7; // AGRICULTURAL (Agrarios)
			case "6": // Régimen Artistas
				return 8; // ARTIST (Artistas)
			case "7": // Representantes de comercio
				return 3; // TRADE_REPRESENTATIVE (Representantes Comercio)
			case "8": // Sistema Especial Empleados del Hogar"
				return 6; // HOME_EMPLOYEES (Empleados del Hogar)
			default:
				return 0; // PRINCIPAL
		}
		
	}
	
	// Obtiene el id del IAE de AON correspondiente al IAE de Omega que se le pasa 
	private static Integer getIAE(String iae) {
		
		// Solo vamos a buscar en la tabla de IAE de Aon en la seccion 1, pues las secciones
		// 2 y 3 solo tienen IAE de longitud 3, y en Omega solo hay un campo para el IAE que 
		// no sé como estará puesto, así que siempre se asume que es de la sección 1, si 
		// es de otra sección, habrá que modificarlo, una vez hecho el traspaso
		
		if (AonStringUtils.isBlank(iae)) {
			return null;
		}
		
		// Quitamos los puntos de Omega y buscamos en la tabla IAE de AON, tambien sin puntos
		return ctx.select(IAE.ID)
				.from(IAE)
				.where(IAE.SECTION.eq("1")
				.and(DSL.replace(IAE.EPIGRAPH,".","").eq(iae.replace(".", ""))))
				.fetchAny(IAE.ID);										
 
	}
	
	// Añadir el centro de trabajo (si no existe)
	private static Integer addWorkplace(String nombre, Integer address, Empresa empresa) {
		
		// Añadir registro en workplace (si no existe)
		
		Integer workplace = ctx.select()
				.from(WORKPLACE)
				.where(WORKPLACE.ENTERPRISE.eq(registry))
				.and(WORKPLACE.DESCRIPTION.equalIgnoreCase(nombre))
				.fetchAny(WORKPLACE.ID);
				
		if (workplace == null) {
			
			Integer scope = ctx.selectFrom(ENTERPRISE)
					.where(ENTERPRISE.REGISTRY.eq(registry)
					.and(ENTERPRISE.DOMAIN.eq(domain)))					
					.fetchAny(ENTERPRISE.SCOPE);
			
			workplace = ctx.insertInto(WORKPLACE)
					.set(WORKPLACE.DOMAIN, domain)
					.set(WORKPLACE.ENTERPRISE, registry)
					.set(WORKPLACE.DESCRIPTION, nombre)
					.set(WORKPLACE.ADDRESS, address)
					.set(WORKPLACE.SCOPE, scope)
					.set(WORKPLACE.ECONOMICAGREEMENT, getEconomicAgreement(empresa.getCodadm()))				
					.returning(WORKPLACE.ID)
					.fetchOne()
					.getId();
		}
		
		// Añadir registro en payroll_workplace (si no existe)
		
		Integer id = ctx.select()
						.from(PAYROLL_WORKPLACE)
						.where(PAYROLL_WORKPLACE.DOMAIN.eq(domain)
						.and(PAYROLL_WORKPLACE.WORKPLACE.eq(workplace)))
						.fetchAny(PAYROLL_WORKPLACE.ID);
		
		if (id == null) {
			
			// Asignar convenio al centro de trabajo
			Integer agreement = null;
			if (AonStringUtils.isNotBlank(empresa.getConven())) {
				agreement = ctx.select(AGREEMENT_DATA.AGREEMENT)
								.from(AGREEMENT_DATA)
								.where(AGREEMENT_DATA.DOMAIN.eq(getParentDomain())
								.and(AGREEMENT_DATA.NAME.equal("CODIGO_OMEGA")
								.and(AGREEMENT_DATA.EXPRESSION.equal(TraspasoConvenios.getCodigoOmega(empresa.getConven())))))
								.fetchOne(AGREEMENT_DATA.AGREEMENT);
			}

			// Asignar calendario al centro de trabajo
			Integer calendar = null; 
			if (AonStringUtils.isNotBlank(empresa.getCalend())) {
				
				// Buscar id de holiday de AON en el calendario de Omega				
				Calendario calendario = buscarCalendario(empresa.getCalend());
				
				// Se añade registro a calendar para poder asignarlo al centro de trabajo
				if (calendario != null) {
					calendar = ctx.insertInto(CALENDAR)
								.set(CALENDAR.DOMAIN, domain)						
								.set(CALENDAR.HOLIDAY, calendario.getHoliday())
								.returning(CALENDAR.ID)
								.fetchOne()
								.getId();
				}
			}
				
			ctx.insertInto(PAYROLL_WORKPLACE)
				.set(PAYROLL_WORKPLACE.DOMAIN, domain)
				.set(PAYROLL_WORKPLACE.WORKPLACE, workplace)
				.set(PAYROLL_WORKPLACE.AGREEMENT, agreement)  
				.set(PAYROLL_WORKPLACE.CALENDAR, calendar)
				.set(PAYROLL_WORKPLACE.ENTERPRISE_ACTIVITY, empresa.getEnterpriseActivity()) 
				.execute();			
		}
		
		return workplace;
	}
	
	// Devuelve el concierto economico de AON, según el código de administracion de hacienda de Omega
	private static byte getEconomicAgreement(String codigoAdmon) {
		
		if (AonStringUtils.isBlank(codigoAdmon)) {
			return 4; // Territorio común
		}
		
		switch (AonStringUtils.left(codigoAdmon, 2)) {
			case "01":
				return 0; // Alava
			case "48":
				return 1; // Bizkaia
			case "20": 
				return 2; // Gipuzkoa
			case "31":
				return 3; // Navarra
			default:
				return 4; // Territorio común 
		}
		
	}
	
	// Añadir banco, si no existe
	private static void addBank(Banco banco) {
		
		if (AonStringUtils.isBlank(banco.getIban())) {
			return; 
		}
		
		String iban = AonStringUtils.trimToEmpty(banco.getIban());
		
		Integer id = ctx.select()
				.from(RBANK)
				.where(RBANK.REGISTRY.equal(registry))
					.and(RBANK.BANK_ACCOUNT.eq(iban))				
				.fetchAny(WORKPLACE.ID);
		
		if (id == null) {
			ctx.insertInto(RBANK)
				.set(RBANK.DOMAIN, domain)
				.set(RBANK.REGISTRY,registry)
				.set(RBANK.BANK_ACCOUNT, iban)				
				.set(RBANK.BIC, banco.getBic())
				.set(RBANK.SUFIX, banco.getSufijo())  
				.set(RBANK.ALIAS, banco.getNombre())
				.set(RBANK.ACCOUNT, addAccount(banco.getCuentaContable(), banco.getNombre()))
				.execute();			
		}
		
	}
	
	// Añade la cuenta contable de Omega a AON (si no existe), previamente la convierte a 9 caracteres si es necesario
	private static Integer addAccount(String cuentaContable, String nombre) {
		
		if (AonStringUtils.isBlank(cuentaContable))
			return null;
		
		// Convertir la cuenta contable a 9 caracteres, si es necesario
		String cuenta = AonStringUtils.trimToEmpty(cuentaContable);
		if (cuentaContable.length() > 9) {
			// Longitud Cuenta de Omega es mayor de 9 (se cogen los 4 primeros y los 5 ultimos de la cuenta de omega) 
			cuenta = AonStringUtils.left(cuentaContable, 4) + AonStringUtils.right(cuentaContable, 5);
		} else if (cuentaContable.length() < 9) {
			// Longitud Cuenta de Omega es menor de 9 (se insertan ceros despues del cuarto digito de la cuenta de Omega)
			cuenta = AonStringUtils.left(cuentaContable, 4) + AonStringUtils.repeat('0', 9-cuentaContable.length()) + AonStringUtils.right(cuentaContable, cuentaContable.length()-4);			
		}
		
		// Comprobar si la cuenta contable existe, sino existe se añade
		Integer id = ctx.select()
						.from(ACCOUNT)
						.where(ACCOUNT.DOMAIN.eq(domain))
							.and(ACCOUNT.CODE.eq(cuenta))				
						.fetchAny(ACCOUNT.ID);
		
		if (id == null) {
			id = ctx.insertInto(ACCOUNT)
					.set(ACCOUNT.DOMAIN, domain)
					.set(ACCOUNT.CODE, cuenta)
					.set(ACCOUNT.DESCRIPTION, nombre)				
					.set(ACCOUNT.ENTRYENABLED, (byte) 1)
					.set(ACCOUNT.LEVEL, (byte) 5)
					.returning(ACCOUNT.ID)
					.fetchOne()
					.getId();
		}
		
		return id;
		
	}
	
	// Borrar los datos del dominio
	// Se borran practicamente todos los datos del dominio que tienen relacion con laboral 
	// Se utiliza cuando el dominio ya existe y se quieren volver a traspasar los datos
	private static void removeDomain() {
		
		// Se borran todos los datos, excepto los registros de domain, registry, company y enterprise
		
		// Tablas que se borran siempre (irpf_data, contract, person y sus registry, payroll_workplace, calendar, enterprise_ccc)
		removeTable("irpf_data_ascendants");
		removeTable("irpf_data_descendients");
		removeTable("irpf_data");		
		removeTable("contract_leave_detail");
		removeTable("contract_leave");
		removeTable("contract_attach");
		removeTable("contract_deduction");
		removeTable("contract_payment");
		removeTable("contract_info");
		removeTable("contract_data");
		removeTable("contract");
		removeTablePerson(); 
		removeTable("payroll_workplace");
		removeTable("calendar");
		removeTable("enterprise_ccc");
		
		// Las siguientes tablas, se borran solo si el dominio se creo con el traspaso de Nominas Omega
		String value = ctx.select(APP_PARAM.VALUE)
						.from(APP_PARAM)
						.where(APP_PARAM.DOMAIN.eq(domain)
						.and(APP_PARAM.NAME.eq("TRASPASO_NOMINAS_OMEGA")
						.and(APP_PARAM.VALUE.eq("1"))))
						.fetchOne(APP_PARAM.VALUE);
		
		if (value != null) {
			removeTable("rpaymethod"); 
			removeTable("workplace"); 
			removeTable("enterprise_activity"); 
			removeTable("rbank");
			removeTable("rdir_staff"); 
			removeTable("rmedia"); 
			removeTable("raddress"); 
		}
		
	}
	
	private static void removeTable(String table) {
		
		String sql = "DELETE FROM " + table + " WHERE domain=" + domain;
		ctx.execute(sql);
		
	}
	
	// Para la tabla person, se tienen que borrar sus registros y los asociados de las tablas registry
	private static void removeTablePerson() {
		
		ctx.execute("SET FOREIGN_KEY_CHECKS=0;");
		ctx.execute("DELETE FROM rpaymethod WHERE registry IN (SELECT registry FROM person WHERE DOMAIN=" + domain + ")");
		ctx.execute("DELETE FROM rbank WHERE registry IN (SELECT registry FROM person WHERE DOMAIN=" + domain + ")");
		ctx.execute("DELETE FROM rdir_staff WHERE registry IN (SELECT registry FROM person WHERE DOMAIN=" + domain + ")");
		ctx.execute("DELETE FROM rmedia WHERE registry IN (SELECT registry FROM person WHERE DOMAIN=" + domain + ")");
		ctx.execute("DELETE FROM raddress WHERE registry IN (SELECT registry FROM person WHERE DOMAIN=" + domain + ")");
		ctx.execute("DELETE FROM registry WHERE id IN (SELECT registry FROM person WHERE DOMAIN=" + domain + ")");
		ctx.execute("DELETE FROM person WHERE DOMAIN=" + domain);
		ctx.execute("SET FOREIGN_KEY_CHECKS=1;");
		
	}

}
