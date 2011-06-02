package com.esferalia.aon.payroll.ctsql2mysql;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.print.CancelablePrintJob;

import com.code.aon.common.enumeration.Country;
import com.code.aon.company.enumeration.CCCType;
import com.code.aon.company.enumeration.EnterpriseActivityType;
import com.code.aon.customer.enumeration.CustomerStatus;
import com.code.aon.registry.enumeration.AddressType;
import com.code.aon.registry.enumeration.DocumentType;
import com.code.aon.registry.enumeration.RegistryType;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Cliente;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Delegacion;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Domicilio;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Empract;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprban;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprccc;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprctra;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprdom;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprnif;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprper;
import com.esferalia.aon.payroll.ctsql2mysql.DefaultMysqlDB.CNAENotFoundException;
import com.esferalia.aon.payroll.ctsql2mysql.DefaultMysqlDB.InvalidFaxException;
import com.esferalia.aon.payroll.ctsql2mysql.DefaultMysqlDB.InvalidTelephoneException;
import com.esferalia.aon.payroll.ctsql2mysql.DefaultMysqlDB.NullCNAEException;

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
	
	// --------------------------------------------------------------
	// constants
	// --------------------------------------------------------------
	final static short 	MAIN_ADDRESS   		= 0;
	final static short 	OTHER_ADDRESS  		= 1;

	final static int 	COMPANY_REGISTRY 	= 1;

	final static String PASSWORD  			= "demo";

	private Integer 							scopeId;
	
	private DefaultMysqlDB 						mysqlDB;
	private ICalendars							calendars;
	private IAgreements							agreements;

	private Map<Integer, Map<String, Integer>> 	cccs;

	private Map<String, Integer> 				cifs;
	private Map<Integer, Integer> 				enterprises;
	private Map<String, Integer> 				customerChilds;
	private Integer 							customerId;
	
	private Map<Integer, Activity> 				activities ;
	private Map<Integer, Map<Integer, Integer>> cnae_activity ;
	private Map<Integer, Map<Integer, Integer>> raddresses ;

	private Map<Integer, Map<Integer,Integer>> 	workplaces ;
	private Map<Integer, Integer> 				calendarsMap;
	
	private Map<Integer, Map<Integer,String>> workplaces_old_agreements ;

	public MyEnterprise(DefaultMysqlDB mysqlDB, IAgreements agreements, ICalendars calendars) {
		this.mysqlDB = mysqlDB;
		this.agreements = agreements;
		this.calendars = calendars;
		this.activities = new HashMap<Integer, Activity>();
		this.cifs= new HashMap<String, Integer>();
		this.enterprises = new HashMap<Integer, Integer>();
		this.cccs = new HashMap<Integer, Map<String, Integer>>();
		this.cnae_activity = new HashMap<Integer, Map<Integer, Integer>>();
		this.raddresses = new HashMap<Integer, Map<Integer, Integer>>();
		this.workplaces = new HashMap<Integer, Map<Integer,Integer>>();
		this.workplaces_old_agreements = new HashMap<Integer, Map<Integer,String>>();
		this.customerChilds = new HashMap<String,Integer>();
		this.calendarsMap = new HashMap<Integer, Integer>();
	}
	

	@Override
	public void visit(AbstractCtsqlDB ctsqlDB) throws SQLException {
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
						Country.ES.getValue());
				mysqlDB.insertCustomer(registry,null, false, false,false,null,status,null,  scopeId,false, true,true);
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
			enterprises.put(emprnif.getCdg(), registry);
			return;
		}
		
		Short status = "N".equals(cliente.getInactivo()) ? 
				DefaultMysqlDB.enum2short(CustomerStatus.ACTIVE) :
					DefaultMysqlDB.enum2short(CustomerStatus.INACTIVE);

		Country docCountry = mysqlDB.getCountry( emprnif.getPaiemi() );

		registry = mysqlDB.insertEnterprise(
				emprnif.getNumdoc(), 
				docCountry,
				emprnif.getDescripcion(), 
				Country.ES,
				emprnif.getAlias(), 	
				scopeId,
				status);
		
		
		cifs.put(emprnif.getNumdoc(), registry);
		enterprises.put(emprnif.getCdg(), registry);

		customerChilds.put(emprnif.getNumdoc(), registry);
		
		Integer userId = 
			mysqlDB.insertUser(
				emprnif.getDescripcion(), 
				emprnif.getNumdoc(), 
				registry, 
				null, 
				true, 
				PASSWORD);
		
		mysqlDB.insertUser_scope(userId, this.scopeId);
		
		emprnif.visitEmpract_emprnif(this);
	}
	
	@Override
	public void visitEmprbanc_cliente(Emprban emprban, Cliente cliente)
			throws SQLException {
		Integer bankId = mysqlDB.getBankId(emprban.getCodent());
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
					null);
		}

	}
	
	@Override
	public void visitEmpract_emprnif(Empract empract, Emprnif emprnif) throws SQLException {
		
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
		
		Integer enterprise = enterprises.get(empract.getCodemp());
		// TODO : Cómo elegimos el tipo de actividad ?
		Integer cnae2009 = null;
		String cnae2009Str = empract.getCnae2009() ;
		if ( cnae2009Str != null && !cnae2009Str.isEmpty()){
			if ( cnae2009Str.charAt(0) == '0') {
				cnae2009Str = "1000" + cnae2009Str;
			}
			cnae2009 = Integer.valueOf(cnae2009Str);
		}
		
		Integer activityId  = DefaultMysqlDB.get(cnae_activity, enterprise, cnae);
		if ( activityId == null ) {
			activityId = 
				mysqlDB.insertEnterprise_activity(empract.getDescripcion(), 
										enterprise, 
										cnae, 
										DefaultMysqlDB.enum2short(EnterpriseActivityType.PRINCIPAL),
										cnae2009);
		}
		DefaultMysqlDB.save(cnae_activity, enterprise, cnae, activityId);

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
			try { 
				geozone = Integer.parseInt(provincia);
			} catch (NumberFormatException e) {
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
		
		Integer enterprise = getEnterprise(emprdom.getCodemp());
		
		if ( enterprise == null )
		{
			MysqlDB.debug("domicilio[{}] : Not found enterprise {} .", 
					emprdom.getCdg(),emprdom.getCodemp());
			return;
		}

		Integer raddress = DefaultMysqlDB.get(raddresses, enterprise, domicilio.getCdg());
		if ( raddress == null ) {

			Integer geozone = null;
			geozone = mysqlDB.getGeoZone(domicilio.getProvincia());

			raddress = mysqlDB.insertRaddress(enterprise, 
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
				mysqlDB.insertTelephone (enterprise, raddress, domicilio.getTelefono() );
			} catch (InvalidTelephoneException e) {
				MysqlDB.error("domicilio[{}] : Invalid telephone {}", 
						domicilio.getCdg(), domicilio.getTelefono());
			}
			try {
				mysqlDB.insertTelephone (enterprise, raddress, domicilio.getTelefono2() );
			} catch (InvalidTelephoneException e) {
				MysqlDB.error("domicilio[{}] : Invalid telephone {}", 
						domicilio.getCdg(), domicilio.getTelefono2());
			}
			try {
				mysqlDB.insertTelephone (enterprise, raddress, domicilio.getTelefono3() );
			} catch (InvalidTelephoneException e) {
				MysqlDB.error("domicilio[{}] : Invalid telephone {}", 
						domicilio.getCdg(), domicilio.getTelefono3());
			}
			try {
				mysqlDB.insertFax(enterprise, raddress, domicilio.getFax() );
			} catch (InvalidFaxException e) {
				MysqlDB.error("domicilio[{}] : Invalid fax {}", 
						domicilio.getCdg(), domicilio.getFax());
			}
			DefaultMysqlDB.save(raddresses, enterprise, domicilio.getCdg(), raddress);
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
						enterprise, 
						description, 
						raddress, 
						null,				// TODO:  Concierto Económico del Centro de Trabajo
						true,
						calendar,				// TODO: ¿ Calendar ?
						null,
						agreement);
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
		return enterprises.get(oldCdg);
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
	
}
