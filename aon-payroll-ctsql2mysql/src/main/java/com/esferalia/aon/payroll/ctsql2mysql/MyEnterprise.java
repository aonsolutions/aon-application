package com.esferalia.aon.payroll.ctsql2mysql;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.code.aon.common.enumeration.Country;
import com.code.aon.company.enumeration.CCCType;
import com.code.aon.company.enumeration.EnterpriseActivityType;
import com.code.aon.customer.enumeration.CustomerStatus;
import com.code.aon.geozone.dao.IGeoZoneAlias;
import com.code.aon.registry.enumeration.AddressType;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Calendar;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Cliente;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Delegacion;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Domicilio;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Empract;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprban;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprccc;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprctra;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprdom;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprnif;
import com.esferalia.aon.payroll.ctsql2mysql.DefaultMysqlDB.CNAENotFoundException;
import com.esferalia.aon.payroll.ctsql2mysql.DefaultMysqlDB.InvalidFaxException;
import com.esferalia.aon.payroll.ctsql2mysql.DefaultMysqlDB.InvalidTelephoneException;
import com.esferalia.aon.payroll.ctsql2mysql.DefaultMysqlDB.NullCNAEException;

public class MyEnterprise extends DefaultCtsqlDBVisitor {

	// --------------------------------------------------------------
	// constants
	// --------------------------------------------------------------
	final static short 	MAIN_ADDRESS   		= 0;
	final static short 	OTHER_ADDRESS  		= 1;

	final static int 	COMPANY_REGISTRY 	= 1;

	final static String PASSWORD  			= "demo";

	private Integer 							scopeId;
	
	private DefaultMysqlDB 						mysqlDB;
	private MyAgreement							myAgreement;

	private Map<Integer, Map<String, Integer>> 	cccs;

	private Map<String, Integer> 				cifs;
	private Map<Integer, Integer> 				enterprises;
	
	private Map<Integer, String> 				ingespemps ;
	private Map<Integer, Integer> 				activities ;
	private Map<Integer, Map<Integer, Integer>> cnae_activity ;
	private Map<Integer, Map<Integer, Integer>> raddresses ;

	private Map<Integer, Map<Integer,Integer>> workplaces ;

	public MyEnterprise(DefaultMysqlDB mysqlDB, MyAgreement myAgreement) {
		this.mysqlDB = mysqlDB;
		this.myAgreement = myAgreement;
		this.ingespemps = new HashMap<Integer, String>();
		this.activities = new HashMap<Integer, Integer>();
		this.cifs= new HashMap<String, Integer>();
		this.enterprises = new HashMap<Integer, Integer>();
		this.cccs = new HashMap<Integer, Map<String, Integer>>();
		this.cnae_activity = new HashMap<Integer, Map<Integer, Integer>>();
		this.raddresses = new HashMap<Integer, Map<Integer, Integer>>();
		this.workplaces = new HashMap<Integer, Map<Integer,Integer>>();
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
			mysqlDB.warn("delegacion[{}]: Too long '{}' . Scope '{}'.", 
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
			mysqlDB.debug("delegacion[{}] : Invalid telephone {}", 
					delegacion.getCdg(), delegacion.getTelefono());
		}
	}
	
	@Override
	public void visitCliente_delegacion(Cliente cliente, Delegacion delegacion) 
	throws SQLException {
		cliente.visitRel_emp_cli(this); 
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
				null,
				emprnif.getAlias(), 	
				scopeId,
				status);
		
		
		cifs.put(emprnif.getNumdoc(), registry);
		enterprises.put(emprnif.getCdg(), registry);

		
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
	public void visitEmpract_emprnif(Empract empract, Emprnif emprnif) throws SQLException {
		
		Integer cnae = null;
		try {
			cnae = mysqlDB.getCnae2009Id(empract.getCnae2009());
		} catch (NullCNAEException e) {
			mysqlDB.error("empreact[{}]: Null CNAE (2009)", empract.getCdg());
			return ;
		} catch (CNAENotFoundException e) {
			mysqlDB.error("empreact[{}]: Not found CNAE (2009) {} {} ", 
					empract.getCdg(), empract.getCnae2009(), empract.getActeco());
			return ;
		}
		
		Integer enterprise = enterprises.get(empract.getCodemp());
		// TODO : Cómo elegimos el tipo de actividad ?

		Integer activityId  = DefaultMysqlDB.get(cnae_activity, enterprise, cnae);
		if ( activityId == null ) {
			activityId = 
				mysqlDB.insertEnterprise_activity(empract.getDescripcion(), 
										enterprise, 
										cnae, 
										DefaultMysqlDB.enum2short(EnterpriseActivityType.PRINCIPAL));
			DefaultMysqlDB.save(cnae_activity, enterprise, cnae, activityId);
		}
		activities.put(empract.getCdg(), activityId);
		ingespemps.put(empract.getCdg(), empract.getIngespemp());
		
		empract.visitEmprccc_empract(this);
	}

	
	
	@Override
	public void visitEmprccc_empract(Emprccc emprccc, Empract empract) throws SQLException {
		
		String ccc = emprccc.getDescripcion();
		
		Integer geozone = null;

		if ( ccc != null ){
			String provincia = ccc.substring(0, 2) ;
			try { 
				geozone = Integer.parseInt(provincia);
			} catch (NumberFormatException e) {
				mysqlDB.error("emprecc[{}] : Invalid CCC {}", emprccc.getCdg(), ccc);
				return ;
			}
		}
		else {
			mysqlDB.debug("emprecc[{}] : Null CCC", emprccc.getCdg() );
		}
		

		Short type = null ;
		String tipccc = emprccc.getTipccc();
		if ( "P".equals(tipccc))
			type = DefaultMysqlDB.enum2short(CCCType.PRINCIPAL);
		else if ( "R".equals(tipccc))
			type = DefaultMysqlDB.enum2short(CCCType.LEARNING);
		else if ( "S".equals(tipccc))
			type = DefaultMysqlDB.enum2short(CCCType.ASSIMILATEDS);
		else if ( "A".equals(tipccc))
			type = DefaultMysqlDB.enum2short(CCCType.TRADE_REPRESENTATIVE);
		
		Integer activity = activities.get(empract.getCdg());
		
		Integer cccId = mysqlDB.insertEnterprise_ccc(ccc, 
				type, 
				activity, 
				geozone);
		
		DefaultMysqlDB.save(cccs, emprccc.getCdg(), emprccc.getTipccc(), cccId );
		
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
			mysqlDB.debug("domicilio[{}] : Not found enterprise {} .", 
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
				mysqlDB.error("domicilio[{}] : Invalid telephone {}", 
						domicilio.getCdg(), domicilio.getTelefono());
			}
			try {
				mysqlDB.insertTelephone (enterprise, raddress, domicilio.getTelefono2() );
			} catch (InvalidTelephoneException e) {
				mysqlDB.error("domicilio[{}] : Invalid telephone {}", 
						domicilio.getCdg(), domicilio.getTelefono2());
			}
			try {
				mysqlDB.insertTelephone (enterprise, raddress, domicilio.getTelefono3() );
			} catch (InvalidTelephoneException e) {
				mysqlDB.error("domicilio[{}] : Invalid telephone {}", 
						domicilio.getCdg(), domicilio.getTelefono3());
			}
			try {
				mysqlDB.insertFax(enterprise, raddress, domicilio.getFax() );
			} catch (InvalidFaxException e) {
				mysqlDB.error("domicilio[{}] : Invalid fax {}", 
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
				
				
				
				Integer agreement = null; 
				
				workplace = mysqlDB.insertWorkplace(
						enterprise, 
						description, 
						raddress, 
						null,				// TODO:  Concierto Económico del Centro de Trabajo
						true,
						null,				// TODO: ¿ Calendar ?
						null,
						agreement);
				
				DefaultMysqlDB.save(workplaces, emprdom.getCodemp(), emprdom.getCoddom(),workplace);
			}
		}
	}

	public Integer getEnterprise(Integer oldCdg) {
		return enterprises.get(oldCdg);
	}
	
	public String getIngEspEmp( Integer oldCdgAct) {
		return this.ingespemps.get(oldCdgAct);
	}

	public Integer getCCC( Integer oldCdgAct, String oldCdgCCC) {
		return DefaultMysqlDB.get(cccs, oldCdgAct, oldCdgCCC);
	}
	
	public Integer getWorkplace(Integer oldCdgEmp, Integer oldCdgDomicilio) {
		return DefaultMysqlDB.get( workplaces, oldCdgEmp, oldCdgDomicilio);
	}

	
}
