package com.esferalia.aon.payroll.ctsql2mysql;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Blob;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.rowset.serial.SerialBlob;

import org.hibernate.cfg.annotations.ArrayBinder;

import com.code.aon.common.enumeration.Country;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.customer.enumeration.CustomerStatus;
import com.code.aon.registry.enumeration.AddressType;
import com.code.aon.registry.enumeration.DocumentType;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.registry.enumeration.RegistryType;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Cliente;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Delegacion;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Domicilio;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Empract;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprban;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprccc;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprctra;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprdom;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Empresa;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprnif;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprper;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractMysqlDB.Domain;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractMysqlDB.Rattach;
import com.esferalia.aon.payroll.ctsql2mysql.DefaultMysqlDB.CNAENotFoundException;
import com.esferalia.aon.payroll.ctsql2mysql.DefaultMysqlDB.InvalidFaxException;
import com.esferalia.aon.payroll.ctsql2mysql.DefaultMysqlDB.InvalidTelephoneException;
import com.esferalia.aon.payroll.ctsql2mysql.DefaultMysqlDB.NullCNAEException;
import com.esferalia.aon.payroll.enumeration.CCCType;
import com.esferalia.aon.payroll.enumeration.EnterpriseActivityType;

public class MyEnterprise extends DefaultCtsqlDBVisitor implements IEnterprises {
	
	public static class Activity {
		Integer	id;
		String 	ingespemp;
		String	indregimen;
	}
	
	@SuppressWarnings("serial")
	private static class InterruptedVisit extends Error{
		public InterruptedVisit() {
		}
	}
	
	private static class EmprCtra extends DefaultCtsqlDBVisitor{
		
		private String codCon = null;
		private Integer codAct = null;
		
		public EmprCtra(Domicilio domicilio) 
		throws SQLException {
			try {
				domicilio.visitEmprctra_domicilio(this);
			}catch (InterruptedVisit e) {
			}
		}
		
		@Override
		public void visitEmprctra_domicilio(Emprctra emprctra,
				Domicilio domicilio) throws SQLException {
			codCon = emprctra.getCodcon();
			codAct = emprctra.getCodact();
			if ( codCon != null && codAct != null ) 
				throw new InterruptedVisit();
		}
		
		@Override
		public void visitEmprper_domiclio(Emprper emprper, Domicilio domicilio)
				throws SQLException {
			codAct = emprper.getCodact();
			if ( codAct != null ) 
				throw new InterruptedVisit();
		}
		
		
	}
	
	
	
	
	private static class Enterprise {
		

		Integer 		id;
		Integer 		scopeId;
		Administration 	economicAgreement;
	
		public Enterprise(Integer id, String ceCon, Integer scopeId) {
			this.id = id;
			this.scopeId = scopeId;
			this.economicAgreement = ADMINISTRATIONS_MAP.get(ceCon);
		}
		
		final static Map<String, Administration> ADMINISTRATIONS_MAP = 
			new HashMap<String, Administration>(){
			{
				put("A", Administration.ALAVA);
				put("G", Administration.GIPUZKOA);
				put("V", Administration.BIZKAIA);
				put("R", Administration.NAVARRA);
				put("N", Administration.COMMON_TERRITORY);
				put(null, Administration.COMMON_TERRITORY);
			}
		};
	}
	
	// --------------------------------------------------------------
	// constants
	// --------------------------------------------------------------
	
	final static short 	MAIN_ADDRESS   		= 0;
	final static short 	OTHER_ADDRESS  		= 1;

	final static int 	COMPANY_REGISTRY 	= 1;
	
	private String								passwdHash;
	private String								domainSuffix;

	private Integer 							scopeId;
	
	private DefaultMysqlDB 						mysqlDB;
	private ICalendars							calendars;
	private IAgreements							agreements;
	private File								logosAndSignaturesDir;

	private Map<Integer, Map<String, Integer>> 	cccs;

	private Map<String, Integer> 				cifs;
	private Map<Integer, Enterprise> 			enterprises;
	private Map<String, Integer> 				customerChilds;
	private Integer 							customerId;
	
	private Map<Integer, Activity> 				activities ;
	private Map<Integer, Map<Integer, Integer>> cnae_activity ;
	private Map<Integer, Map<Integer, Integer>> raddresses ;

	private Map<Integer, Map<Integer,Integer>> 	workplaces ;
	private Map<Integer, Integer> 				calendarsMap;
	
	private Map<Integer, Map<Integer,String>> 	workplaces_old_agreements ;
	private Map<Integer, Map<RegistryAttachmentType, List<String>>>		images;
	

	public MyEnterprise(DefaultMysqlDB mysqlDB, IAgreements agreements, ICalendars calendars, File logosAndSignaturesDir, String passwdHash, String domainSuffix ) {
		this.mysqlDB = mysqlDB;
		this.agreements = agreements;
		this.calendars = calendars;
		this.logosAndSignaturesDir = logosAndSignaturesDir;
		this.passwdHash = passwdHash;
		this.domainSuffix = domainSuffix;
		this.activities = new HashMap<Integer, Activity>();
		this.cifs= new HashMap<String, Integer>();
		this.enterprises = new HashMap<Integer, Enterprise>();
		this.cccs = new HashMap<Integer, Map<String, Integer>>();
		this.cnae_activity = new HashMap<Integer, Map<Integer, Integer>>();
		this.raddresses = new HashMap<Integer, Map<Integer, Integer>>();
		this.workplaces = new HashMap<Integer, Map<Integer,Integer>>();
		this.workplaces_old_agreements = new HashMap<Integer, Map<Integer,String>>();
		this.customerChilds = new HashMap<String,Integer>();
		this.calendarsMap = new HashMap<Integer, Integer>();
		this.images = new Hashtable<Integer, Map<RegistryAttachmentType,List<String>>>();
	}
	

	protected void init(AbstractCtsqlDB ctsqlDB)
			throws SQLException {
		
	}

	@Override
	public void visit(AbstractCtsqlDB ctsqlDB) throws SQLException {
		init(ctsqlDB);
		ctsqlDB.visitDelegacion(this);
		ctsqlDB.visitDomicilio(this);
	}

	@Override
	public void visitDelegacion(AbstractCtsqlDB.Delegacion delegacion)
			throws SQLException {

		int 	newScope;

		String 	description = delegacion.getDescripcion();
		if ( description != null && description.length() > 16 )
		{
			
			String truncated = description.substring(0,15);
			MysqlDB.warn("delegacion[{}]: Too long '{}' . Scope '{}'.", 
					delegacion.getCdg(), description, truncated );
			description = truncated;
		}
		
		newScope = mysqlDB.insertScope(description);

		this.scopeId = newScope;
		delegacion.visitCliente_delegacion(this);
		this.scopeId = null;
		
		Integer geozone = null;
		geozone = mysqlDB.getGeoZone(delegacion.getProvincia());
		// each delegacion will be one 'raddress' of company.
		Integer raddress = 
			mysqlDB.insertRaddress(COMPANY_REGISTRY, 			
						DefaultMysqlDB.enum2short(AddressType.DELEGATION), 				
						null,										
						delegacion.getTipovia(),	
						delegacion.getNomvia(),			
						delegacion.getNumero(), 					
						delegacion.getOtrdir(), 
						null,
						delegacion.getCodpos(), 
						delegacion.getLocalidad(), 
						geozone,
						null);
		
		try {
			mysqlDB.insertTelephone (COMPANY_REGISTRY, raddress, delegacion.getTelefono() );
		} catch (InvalidTelephoneException e) {
			MysqlDB.debug("delegacion[{}] : Invalid telephone {}", 
					delegacion.getCdg(), delegacion.getTelefono());
		}
	}
	
	@Override
	public void visitCliente_delegacion(Cliente cliente, Delegacion delegacion) 
	throws SQLException {
		
		customerChilds.clear();
		cliente.visitRel_emp_cli(this); 
		
		if ( customerChilds.size() > 1 ) {
			Integer registry  = customerChilds.get(cliente.getNumdoc());
			if (registry == null ){
			
					Short status = "N".equals(cliente.getInactivo()) ? 
						DefaultMysqlDB.enum2short(CustomerStatus.ACTIVE) :
							DefaultMysqlDB.enum2short(CustomerStatus.INACTIVE);
				Country docCountry = mysqlDB.getCountry( cliente.getPaiemi() );
				
				registry =  mysqlDB.insertRegistry(cliente.getNumdoc(), 
						MysqlDB.enum2short(DocumentType.CIF), 
						docCountry != null ? docCountry.getValue() : Country.ES.getValue(), 
						cliente.getDescripcion(), 
						cliente.getAlias(), 
						MysqlDB.enum2short(RegistryType.LEGAL), 
						Country.ES.getValue(),
						DefaultMysqlDB.enum2short(SecurityLevel.OFFICIAL));
				mysqlDB.insertCustomer(registry,null, false, false,null,status, /*null,*/ scopeId,false, true,true);
			}
			
			Integer group = mysqlDB.insertInvoicing_group(registry);

			for (Map.Entry<String, Integer> child  : customerChilds.entrySet()) {
				mysqlDB.insertInvoicing_group_detail(group, child.getValue(), false);
			}
			customerId = registry;
			cliente.visitEmprbanc_cliente(this);
		}
		else if (customerChilds.size() == 1) {
			customerId = customerChilds.values().iterator().next();
			cliente.visitEmprbanc_cliente(this);
		}
		
		
	}

	
	@Override
	public void visitRel_emp_cli(Emprnif emprnif, Cliente cliente) throws SQLException {
		
		Integer registry = 
			cifs.get(emprnif.getNumdoc());
	
		if ( registry != null ) {
			enterprises.put(emprnif.getCdg(), new Enterprise(registry, emprnif.getCecon(), scopeId) );
			return;
		}
		
		Short status = "N".equals(cliente.getInactivo()) ? 
				DefaultMysqlDB.enum2short(CustomerStatus.ACTIVE) :
					DefaultMysqlDB.enum2short(CustomerStatus.INACTIVE);

		Country docCountry = mysqlDB.getCountry( emprnif.getPaiemi() );

		String name = emprnif.getDescripcion();
		String doc = emprnif.getNumdoc();
		
		StringBuffer domainNameBuff = new StringBuffer();
		for ( int i = 0 ; i < name.length(); i++){
			char ch = name.charAt(i);
			if ( "?,".indexOf(ch) != -1 ) break;
			if ( ". ".indexOf(ch) != -1 ) continue;
			domainNameBuff.append(Character.toLowerCase(ch));
		}
		String domainName = domainNameBuff.toString();
		
		
		int MaxLength = 64 - ( this.domainSuffix.length() +1 );

		if ( domainName.length() > MaxLength )
		{
			
			String truncated = domainName.substring(0,MaxLength-1);
			MysqlDB.warn("emprnif[{}]: Domain name too long '{}' . Truncated '{}'.", 
					emprnif.getCdg(), domainName, truncated );
			domainName = truncated;
		}
		
		Integer domain ;
		try {
			domain = mysqlDB.newEnterpriseDomain( String.format("%s.%s", domainName, this.domainSuffix ) , mysqlDB.getDefaultDomain() );
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (InterruptedException e1) {
			throw new RuntimeException(e1);
		}
		
		
		
		registry = mysqlDB.insertEnterprise(
				domain,
				doc, 
				docCountry,
				name, 
				Country.ES,
				emprnif.getAlias(), 	
				scopeId,
				status);
		
		// TODO: Company ... related entries, like 'logo'
		mysqlDB.insertCompany(registry, domain, true, true, true, true);

		String nroDocRep = emprnif.getNrodocrep();
		String represantante = emprnif.getRepresentante();
		if ( represantante != null && nroDocRep != null ) {
			mysqlDB.insertRdir_staff(
					registry, 
					domain,
					nroDocRep, 
					represantante, 
					false, 						// shareholder, 
					true, 						// representative, 
					false, 						// director, 
					0.00,						// percent_share, 
					0, 							// share_number, 
					0.00, 						// nominal_value, 
					null,						// due_date, 
					true ); 					// representative_labor
		} else {
			MysqlDB.error("emprnif[{}]: Enterprise {} {} {} without labour represantive ", 
					registry, emprnif.getNumdoc(), emprnif.getDescripcion());
		}
		
		cifs.put(emprnif.getNumdoc(), registry);
		enterprises.put(emprnif.getCdg(), new Enterprise(registry, emprnif.getCecon(),scopeId));

		customerChilds.put(emprnif.getNumdoc(), registry);


		String passwd = null;
		if ( passwdHash != null ) {
			passwd = passwdHash;
		}
		else {
			if (name != null && name.length() >= 4 && 
					doc != null && doc.length() >=3 ){
				try {
					passwd = DefaultMysqlDB.encode(name.toUpperCase().substring(0, 4) + 
							doc.substring(doc.length()-3, doc.length()));
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
				}
			}
		}
		
		Integer userId = 
			mysqlDB.insertUser(
				domain,
				name, 
				doc, 
				registry, 
				null, 
				true, 
				passwd,
				null,
				(short) 0);
		Integer applicationId = 
				mysqlDB.getApplicationId("aon-aio");

		Integer domainApplicationId = 
				mysqlDB.getDomainApplicationId(domain, applicationId);
		Integer  profileId = 
				mysqlDB.getProfileId(null, applicationId, "Administrador");

		int applicationUserId = 
				mysqlDB.insertApplication_user(
						domain,
						userId, 				
						domainApplicationId, 	 
						true 					// active
						);
				mysqlDB.insertApplication_user_profile(
						domain,
						applicationUserId, 
						profileId);
		
		mysqlDB.insertUser_scope(domain, userId, this.scopeId);
		
		emprnif.visitEmpract_emprnif(this);

	}
	
	@Override
	public void visitEmprbanc_cliente(Emprban emprban, Cliente cliente)
			throws SQLException {
		
		
		Integer bankId = mysqlDB.getBankId(mysqlDB.getDefaultDomain(),
				emprban.getCodent());
		
		if ( bankId == null ) {
			Emprbanc_entidad emprbanc_entidad = 
				new Emprbanc_entidad();
			emprban.visitEmprbanc_entidad(emprbanc_entidad);
			String name = emprbanc_entidad.getEntidad_Descripcion();
			if ( name != null ) {
				bankId = mysqlDB.insertBank(emprbanc_entidad.getEntidad_Descripcion(), emprban.getCodent());
			}
			else {
				MysqlDB.error("emprban[{}]: Entidad {} without name ", emprban.getCdg() , emprban.getCodent());
				return;
			}
		}
		
		String dc = emprban.getDc();
		String codent = emprban.getCodent();
		String codsuc = emprban.getCodsuc();
		String numcta = emprban.getNumcta();
		
		String bankAccount = String.format("%s%s%s%s", 
				codent, 
				codsuc,
				dc != null ? dc : "XX" ,
				numcta);
		// TODOD : Chequear con BankAccount ...
		if ( bankAccount.length() == 20 ) {
		
			mysqlDB.insertRbank(customerId, 
					bankId, 
					bankAccount, 
					null,
					null,
					true);
		}

	}
	
	@Override
	public void visitEmpract_emprnif(Empract empract, Emprnif emprnif) throws SQLException {
		
		Enterprise enterprise = enterprises.get(empract.getCodemp());

		insertLogo(enterprise, empract);
		insertSignature(enterprise, empract);

		Integer cnae = null;
		try {
			cnae = mysqlDB.getCnae2009Id(empract.getCnae2009());
		} catch (NullCNAEException e) {
			MysqlDB.error("empreact[{}]: Null CNAE (2009)", empract.getCdg());
			return ;
		} catch (CNAENotFoundException e) {
			MysqlDB.error("empreact[{}]: Not found CNAE (2009) {} {} ", 
					empract.getCdg(), empract.getCnae2009(), empract.getActeco());
			return ;
		}
		
		// TODO : Cómo elegimos el tipo de actividad ?
		Integer cnae2009 = null;
		String cnae2009Str = empract.getCnae2009() ;
		if ( cnae2009Str != null && !cnae2009Str.isEmpty()){
			if ( cnae2009Str.charAt(0) == '0') {
				cnae2009Str = "1000" + cnae2009Str;
			}
			cnae2009 = Integer.valueOf(cnae2009Str);
		}
		
		Integer activityId  = DefaultMysqlDB.get(cnae_activity, enterprise.id, cnae);
		if ( activityId == null ) {
			activityId = 
				mysqlDB.insertEnterprise_activity(empract.getDescripcion(), 
										enterprise.id, 
										cnae, 
										DefaultMysqlDB.enum2short(EnterpriseActivityType.PRINCIPAL),
										cnae2009);
		}
		DefaultMysqlDB.save(cnae_activity, enterprise.id, cnae, activityId);

		Activity activity = new Activity();
		activity.id = activityId ;
		activity.ingespemp = empract.getIngespemp();
		activity.indregimen = empract.getIndregimen();
		activities.put(empract.getCdg(), activity );
		
		empract.visitEmprccc_empract(this);
		
		
		//emprnif.visitOtrperc_emprnif(this);
	}

	
	
	@Override
	public void visitEmprccc_empract(Emprccc emprccc, Empract empract) throws SQLException {
		
		String tipccc = emprccc.getTipccc();
		String ccc = emprccc.getDescripcion();
		
		Integer geozone = null;

		if ( ccc != null ){
			String provincia = ccc.substring(0, 2) ;
			geozone = mysqlDB.getGeoZone(provincia);
			if ( geozone == null ){
				MysqlDB.error("emprecc[{}] : Invalid CCC {}", emprccc.getCdg(), ccc);
				return ;
			}
		}
		else {
			if ( "A".equals(tipccc) ) // Altos cargos
				return;
			
			MysqlDB.debug("emprecc[{}] : Null CCC", emprccc.getCdg() );
		}
		

		Short type = null ;
		if ( "P".equals(tipccc))
			type = DefaultMysqlDB.enum2short(CCCType.PRINCIPAL);
		else if ( "R".equals(tipccc))
			type = DefaultMysqlDB.enum2short(CCCType.LEARNING);
		else if ( "S".equals(tipccc))
			type = DefaultMysqlDB.enum2short(CCCType.ASSIMILATEDS);
		else if ( "A".equals(tipccc))
			type = DefaultMysqlDB.enum2short(CCCType.TRADE_REPRESENTATIVE);
		else if ( "B".equals(tipccc))
			type = DefaultMysqlDB.enum2short(CCCType.FELLOWS); // TODO B: ???
		else if ( "E".equals(tipccc))
			type = DefaultMysqlDB.enum2short(CCCType.HOME_EMPLOYEES); // TODO E: ???
		
		Activity activity = activities.get(empract.getCdg());
		
		Integer cccId = mysqlDB.insertEnterprise_ccc(ccc, 
				type, 
				activity.id, 
				geozone);
		
		DefaultMysqlDB.save(cccs, emprccc.getCdg()/**/, tipccc, cccId );
		
	}
	
	@Override
	public void visitDomicilio(Domicilio domicilio) throws SQLException {
		domicilio.visitEmprdom_domicilio(this);
	}
	
	@Override
	public void visitEmprdom_domicilio(Emprdom emprdom, Domicilio domicilio ) throws SQLException {
		
		Enterprise enterprise = enterprises.get(emprdom.getCodemp());
		
		
		
		if ( enterprise == null )
		{
			MysqlDB.debug("domicilio[{}] : Not found enterprise {} .", 
					emprdom.getCdg(),emprdom.getCodemp());
			return;
		}

		Integer raddress = DefaultMysqlDB.get(raddresses, enterprise.id, domicilio.getCdg());
		if ( raddress == null ) {

			Integer geozone = null;
			geozone = mysqlDB.getGeoZone(domicilio.getProvincia());

			raddress = mysqlDB.insertRaddress(enterprise.id, 
					DefaultMysqlDB.enum2short ( AddressType.DELEGATION ), 
					null, 
					domicilio.getTipovia(), 
					domicilio.getNomvia(), 
					domicilio.getNumero(), 
					domicilio.getOtrdir(),
					null,
					domicilio.getCodpos(), 
					domicilio.getLocalidad(), 
					geozone,
					null);
			
			try {
				mysqlDB.insertTelephone (enterprise.id, raddress, domicilio.getTelefono() );
			} catch (InvalidTelephoneException e) {
				MysqlDB.error("domicilio[{}] : Invalid telephone {}", 
						domicilio.getCdg(), domicilio.getTelefono());
			}
			try {
				mysqlDB.insertTelephone (enterprise.id, raddress, domicilio.getTelefono2() );
			} catch (InvalidTelephoneException e) {
				MysqlDB.error("domicilio[{}] : Invalid telephone {}", 
						domicilio.getCdg(), domicilio.getTelefono2());
			}
			try {
				mysqlDB.insertTelephone (enterprise.id, raddress, domicilio.getTelefono3() );
			} catch (InvalidTelephoneException e) {
				MysqlDB.error("domicilio[{}] : Invalid telephone {}", 
						domicilio.getCdg(), domicilio.getTelefono3());
			}
			try {
				mysqlDB.insertFax(enterprise.id, raddress, domicilio.getFax() );
			} catch (InvalidFaxException e) {
				MysqlDB.error("domicilio[{}] : Invalid fax {}", 
						domicilio.getCdg(), domicilio.getFax());
			}
			DefaultMysqlDB.save(raddresses, enterprise.id, domicilio.getCdg(), raddress);
		}

		String tipoDom = emprdom.getTipdom();
		if ( tipoDom != null && "T".equalsIgnoreCase(tipoDom.trim()) ){ 
			
			Integer workplace = DefaultMysqlDB.get(workplaces, emprdom.getCodemp(), emprdom.getCoddom());
			
			if ( workplace == null ){
				String description = 
					domicilio.getAclaracion();
				if ( description == null ){
					description = domicilio.getNomvia(); 
				}
				if ( description == null ){
					description = " "; 
				}
				
				
				EmprCtra emprCtra = new EmprCtra(domicilio);
				
				Integer agreement = emprCtra.codCon != null ? 
					agreements.getAgreement(emprCtra.codCon) : null;
				
					
				Integer calendar = 
					calendars.getCalendar(emprdom.getCodemp(), emprdom.getCoddom(), emprCtra.codAct);	
				if ( calendar == null ) {
					MysqlDB.info("emprdom[{}]: Calendar not found for {}/{}/{}", 
							emprdom.getCdg(), emprdom.getCodemp(), emprdom.getCoddom(), emprCtra.codAct);
					
				} 
				
				workplace = mysqlDB.insertWorkplace(
						enterprise.id, 
						description, 
						raddress, 
						enterprise.scopeId,
						MysqlDB.enum2short(enterprise.economicAgreement),
						true);
				
				mysqlDB.insertPayroll_workplace(
						workplace, 
						agreement, 
						null, 
						calendar);
				
				if ( calendar != null ) {
					calendarsMap.put(workplace, calendar);
				}
				DefaultMysqlDB.save(workplaces, emprdom.getCodemp(), emprdom.getCoddom(),workplace);
				DefaultMysqlDB.save(workplaces_old_agreements, emprdom.getCodemp(), emprdom.getCoddom(),emprCtra.codCon);
			}
		}
	}
	
	@Override
	public Integer getCalendar(Integer workplace) {
		return calendarsMap.get(workplace);
	}

	@Override
	public Integer getEnterprise(Integer oldCdg) {
		Enterprise enterprise = enterprises.get(oldCdg); 
		return enterprise != null ? enterprise.id : null;
	}
	
	public Activity getActivity( Integer oldCdgAct) {
		return this.activities.get(oldCdgAct);
	}

	@Override
	public Integer getActivityId( Integer oldCdgAct) {
		Activity activity = this.activities.get(oldCdgAct);
		return activity != null ? activity.id: null ;
	}

	@Override
	public String getIngEspEmp( Integer oldCdgAct) {
		Activity activity = this.activities.get(oldCdgAct);
		return activity != null ? activity.ingespemp : null ;
	}

	@Override
	public String getIndRegimen( Integer oldCdgAct) {
		Activity activity = this.activities.get(oldCdgAct);
		return activity != null ? activity.indregimen: null ;
	}

	@Override
	public Integer getCCC( Integer oldCdgAct, String oldCdgCCC) {
		return DefaultMysqlDB.get(cccs, oldCdgAct, oldCdgCCC);
	}
	
	@Override
	public Integer getWorkplace(Integer oldCdgEmp, Integer oldCdgDomicilio) {
		return DefaultMysqlDB.get( workplaces, oldCdgEmp, oldCdgDomicilio);
	}

	public String getOldAgreement(Integer oldCdgEmp, Integer oldCdgDomicilio) {
		return DefaultMysqlDB.get( workplaces_old_agreements, oldCdgEmp, oldCdgDomicilio);
	}
	
	
	protected Set<Integer> getEmprnifCdgs() {
		return enterprises.keySet();
	}
	
	private void insertLogo(Enterprise enterprise, Empract empract) throws SQLException{
		insertImage(enterprise, empract, RegistryAttachmentType.LOGO, "L");
	}

	private void insertSignature(Enterprise enterprise, Empract empract) throws SQLException{
		insertImage(enterprise, empract, RegistryAttachmentType.SIGNATURE, "F");
	}

	private void insertImage(Enterprise enterprise, Empract empract, RegistryAttachmentType type, String preffix) throws SQLException{
		
		if ( logosAndSignaturesDir == null  || 
				!logosAndSignaturesDir.exists()){
			return ;
		}
		
		File file = new File(logosAndSignaturesDir, 
				String.format("%s%s.bmp", preffix, empract.getCdg()));
		if ( !file.exists() ) {
			return;
		}
		String description = null;
		try {
			int length = ( int ) file.length();
			byte bytes [ ] = new byte [ length ];
			InputStream is = new FileInputStream(file);
			is.read(bytes);
			
			
			try {
				String md5 = hash(bytes);
				List<String> md5s = DefaultMysqlDB.get(images, enterprise.id, type);
				if ( md5s == null ) {
					md5s = new ArrayList<String>();
					md5s.add(md5);
					DefaultMysqlDB.save(images, enterprise.id, type, md5s);
					MysqlDB.info("empreact[{}]: new {} {}. {}", 
							empract.getCdg(), type.toString() , md5, enterprise.id);

				}
				else { 
					if ( !md5s.contains(md5)) {
						description = empract.getCnae2009();
						MysqlDB.error("empreact[{}]: has different {} {}. {}", 
								empract.getCdg(), type.toString() , md5, enterprise.id);
						md5s.add(md5);
					}
					else {
						MysqlDB.info("empreact[{}]: {} {} already saved. {}", 
								empract.getCdg(), type.toString(), md5, enterprise.id);
					}
				}
			} catch (NoSuchAlgorithmException e1) {
			}
			
			Blob blob = new SerialBlob(bytes);
			Rattach rattach = new Rattach();
			rattach.registry = enterprise.id;
			rattach.domain = 1;
			rattach.mimeType = DefaultMysqlDB.enum2short(MimeType.MIME_BMP);
			rattach.data = blob;
			rattach.description = description;
			rattach.type = DefaultMysqlDB.enum2short(type);
			rattach.scope = scopeId;
			rattach.security_level = 0;
			List<Rattach> rattachs = 
					Collections.nCopies(1, rattach);
			mysqlDB.insertRattach(rattachs);
			is.close();
		} catch (IOException e) {
		} 
	}
	
	private static final char[] HEXADECIMAL = { '0', '1', '2', '3',
        '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	
	private  String hash(byte input []) throws NoSuchAlgorithmException  {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] bytes = md.digest(input);
        StringBuilder sb = new StringBuilder(2 * bytes.length);
        for (int i = 0; i < bytes.length; i++) {
            int low = (int)(bytes[i] & 0x0f);
            int high = (int)((bytes[i] & 0xf0) >> 4);
            sb.append(HEXADECIMAL[high]);
            sb.append(HEXADECIMAL[low]);
        }
        return sb.toString();
	}
}
