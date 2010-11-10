package com.esferalia.aon.payroll.ctsql2mysql;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;


import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import com.code.aon.company.enumeration.CCCType;
import com.code.aon.company.enumeration.EnterpriseActivityType;
import com.code.aon.customer.enumeration.CustomerStatus;
import com.code.aon.employee.enumeration.ContractStatus;
import com.code.aon.employee.enumeration.DeductionType;
import com.code.aon.employee.enumeration.PaymentType;
import com.code.aon.person.enumeration.Gender;
import com.code.aon.person.enumeration.MaritalStatus;
import com.code.aon.registry.enumeration.AddressType;
import com.code.aon.registry.enumeration.DocumentType;
import com.code.aon.registry.enumeration.StreetType;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Cliente;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Delegacion;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Domicilio;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Empract;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprccc;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprdom;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprnif;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprper;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nomdto;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nomdtoex;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nomina;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nominadev;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nominaex;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Percep;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Persona;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Provincia;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Tipdoc;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Tipovia;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Trabajo;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Trabdto;


/********************************************************************
* Copyright (c) 2010, esferalia NETWORKS S.A
*
* The copyright of the computer program herein is the property 
* of esferalia NETWORKS.
*********************************************************************
* The program may be used and/or copied only with the written 
* permission of esferalia NETWORKS, or in accordance with the 
* terms and conditions stipulated in the agreement contract 
* under which the program has been supplied.
*********************************************************************
*/

/**
 * 
 */
public class MysqlDB extends DefaultMysqlDB{
	
	
	// --------------------------------------------------------------
	// constants
	// --------------------------------------------------------------
	final static short MAIN_ADDRESS   = 0;
	final static short OTHER_ADDRESS  = 1;

	final static int COMPANY_REGISTRY = 1;
		

	// --------------------------------------------------------------
	// State related members 
	private Integer 	scopeId;				// 'scope' id where we are in 
	
	private Integer 	salaryId;				// 'salary' id where we are in 
	private Integer 	contractId;				// 'contract' id where we are in 
	// --------------------------------------------------------------
	private Date		fromDate = null;		
	
	

	private Map<Integer, Integer> persons = 
		new HashMap<Integer, Integer>();

	private Map<String, Integer> geozones = 
		new HashMap<String, Integer>();
	
	private Map<String, StreetType> streetTypes = 
		new HashMap<String, StreetType>();
	
	private Map<String, DocumentType> docTypes = 
		new HashMap<String, DocumentType>();

	private Map<Integer, Integer> enterprises = 
		new HashMap<Integer, Integer>();
	
	private Map<Integer, Integer> activities = 
		new HashMap<Integer, Integer>();

	private Map<Integer, Map<String, Integer>> cccs = 
		new HashMap<Integer, Map<String, Integer>>();

	private Map<Integer, Map<Integer, Integer>> raddresses = 
		new HashMap<Integer, Map<Integer, Integer>>();

	private Map<Integer, Map<Integer, Integer>> cnae_activity = 
		new HashMap<Integer, Map<Integer, Integer>>();

	private Map<Integer, Map<Integer, Map<Integer,Integer>>> workplaces = 
		new HashMap<Integer, Map<Integer, Map<Integer,Integer>>>();

	// --------------------------------------------------------------
	
	public MysqlDB(Connection mysqlConnection) throws SQLException {
		super(mysqlConnection);
	}
	
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	
	private boolean outOfDate ( Date date ) {
		if ( date == null )
			return false;
		if ( fromDate == null )
			return false;
		return fromDate.compareTo(date) > 0 ; 
	}
	
	@Override
	public boolean visitTipdoc(Tipdoc tipdoc) throws SQLException {
		String descripcion = tipdoc.getDescripcion();
		if ( descripcion != null ) {
			descripcion = descripcion.toUpperCase(); 
			if ( descripcion.contains("DNI"))
				docTypes.put(tipdoc.getCdg(), DocumentType.NIF);
			else if ( descripcion.contains("NUMERO")) 
				docTypes.put(tipdoc.getCdg(), DocumentType.NIE);
			else if ( descripcion.contains("CIF")) 
				docTypes.put(tipdoc.getCdg(), DocumentType.CIF);
			else if ( descripcion.contains("PASAPORTE")) 
				docTypes.put(tipdoc.getCdg(), DocumentType.PASSPORT);
			else if ( descripcion.contains("TARJETA")) 
				docTypes.put(tipdoc.getCdg(), DocumentType.COMMUNITY_CARD);
			else if ( descripcion.contains("PERMISO")) 
				docTypes.put(tipdoc.getCdg(), DocumentType.WORK_PERMIT);
		}
		return true;
	}

	@Override
	public boolean visitTipovia(Tipovia tipovia) throws SQLException {
		String descripcion =  tipovia.getDescripcion();
		try {
			StreetType streetType = super.getStreetType(descripcion);
			streetTypes.put(tipovia.getCdg(), streetType);
		} catch (NullStreetTypeException e) {
			error("tipovia[{}]: Null tipovia", tipovia.getCdg());
		} catch (StreetTypeNotFoundException e) {
			error("tipovia[{}]: Not found tipovia {}", 
					tipovia.getCdg(), tipovia.getDescripcion());
		}
		return true;
	}
	
	protected StreetType getStreetType(String tipovia )
	throws StreetTypeNotFoundException {
		if ( tipovia == null ){
			return null;
		}
		if ( ! streetTypes.containsKey(tipovia)) {
			throw new StreetTypeNotFoundException();
		}
		return streetTypes.get(tipovia);
	}
	
	@Override
	public boolean visitProvincia(Provincia provincia) throws SQLException {
		String descripcion = provincia.getDescripcion();
		try {
			Integer geozoneId;
			geozoneId = getGeoZoneId(descripcion);
			geozones.put(provincia.getCdg(), geozoneId);
		}catch (NullGeoZoneException e) {
			error("provincia[] : Provincia NULL", provincia.getCdg());
		} 
		catch (GeoZoneNotFoundException e) {
			error("provincia[{}] : Not found provincia {}", 
					provincia.getCdg(), descripcion);
		}
		return true;
	}

	protected Integer getGeoZone(String provincia )
	throws GeoZoneNotFoundException{
		if ( provincia == null ){
			return null;
		}
		if ( ! geozones.containsKey(provincia)) {
			throw new GeoZoneNotFoundException();
		}
		return geozones.get(provincia);
	}

	@Override
	public boolean visitPersona(Persona persona) throws SQLException { 
		Gender gender = Gender.UNKNOWN;
		try {
			gender = getGender(persona.getSexo());
		} catch (NullGenderException e1) {
			debug("persona[{}]: Null gender.", persona.getCdg());
		} catch (GenderNotFoundException e1) {
			debug("persona[{}]: Not found gender {}", persona.getCdg(), persona.getSexo());
		}
		
		MaritalStatus maritalStatus = MaritalStatus.UNKNOWN;
		try {
			maritalStatus = getMaritalStatus(persona.getEstciv());
		} catch (NullMaritalStatusException e1) {
			debug("persona[{}]: Null marital status", persona.getCdg());
		} catch (MaritalStatusNotFoundException e1) {
			debug("persona[{}]: Not found marital status {}", persona.getCdg(), persona.getEstciv());
		}
		
		
		DocumentType docType = docTypes.get(persona.getInddoc());
		
		Integer registry = insertPerson(persona.getNumdoc(), 
				docType,
				persona.getNombre(), 
				persona.getDescripcion(), 
				persona.getApellido2(), 
				persona.getAlias(), 		
				persona.getFecnac(), 
				enum2short(gender), 
				enum2short(maritalStatus), 
				persona.getNumss());
	
		StreetType streetType = null;
		try {
			streetType = getStreetType(persona.getTipovia());
		} catch (StreetTypeNotFoundException e) {
			debug("persona[{}] : Not found tippo via {}", 
					persona.getCdg(), persona.getTipovia());
		}
		Integer raddress = 
			insertRaddress(registry, 
				enum2short(AddressType.MAIN), 
				null, 									//TODO: raddress 'recipient'
				enum2short(streetType), 
				persona.getNomvia(), 
				persona.getNumero(), 
				persona.getOtrdir(),
				null,
				persona.getCodpos(), 
				persona.getLocalidad(), 
				geozones.get (persona.getProvincia()));
		try {
			insertEmail(registry, raddress, persona.getEmail()) ;
		} catch (InvalidEmailException e) {
			debug("persona[{}] : Invalid email {}", 
					persona.getCdg(), persona.getEmail());
		}
		try {
			insertTelephone (registry, raddress, persona.getTelefono() );
		} catch (InvalidTelephoneException e) {
			debug("persona[{}] : Invalid telephone {}", 
					persona.getCdg(), persona.getTelefono());
		}

		persons.put(persona.getCdg(), registry);

		return true;
	}
	
	@Override
	public boolean visitDelegacion(AbstractCtsqlDB.Delegacion delegacion)
			throws SQLException {

		int 	newScope;

		String 	description = delegacion.getDescripcion();
		if ( description != null && description.length() > 16 )
		{
			
			String truncated = description.substring(0,15);
			warn("delegacion[{}]: Too long '{}' . Scope '{}'.", 
					delegacion.getCdg(), description, truncated );
			description = truncated;
		}
		
		newScope = insertScope(description);

		this.scopeId = newScope;
		delegacion.visitCliente(this);
		this.scopeId = null;
		
		StreetType streetType = null;
		try {
			streetType = getStreetType(delegacion.getTipovia());
		} catch (StreetTypeNotFoundException e) {
			debug("delegacion[{}] : Not found tipo via {}", 
					delegacion.getCdg(), delegacion.getTipovia());
		}
		
		Integer geozone = null;
		try {
			geozone = getGeoZone(delegacion.getProvincia());
		} catch (GeoZoneNotFoundException e) {
			warn("delegacion[{}] : Not found provincia {}", 
					delegacion.getCdg(), delegacion.getProvincia());
		}
		// each delegacion will be one 'raddress' of company.
		Integer raddress = 
			insertRaddress(COMPANY_REGISTRY, 			
						enum2short(AddressType.DELEGATION), 				
						null,										
						enum2short(streetType),	
						delegacion.getNomvia(),			
						delegacion.getNumero(), 					
						delegacion.getOtrdir(), 
						null,
						delegacion.getCodpos(), 
						delegacion.getLocalidad(), 
						geozone);
		
		try {
			insertTelephone (COMPANY_REGISTRY, raddress, delegacion.getTelefono() );
		} catch (InvalidTelephoneException e) {
			debug("delegacion[{}] : Invalid telephone {}", 
					delegacion.getCdg(), delegacion.getTelefono());
		}
		
		return true;
	}

	@Override
	public boolean visitCliente(Cliente cliente, Delegacion delegacion) throws SQLException {
		cliente.visitEmprnif(this); 
		return true;
	}
	
	@Override
	public boolean visitEmprnif(Emprnif emprnif, Cliente cliente) throws SQLException {
		Short status = "N".equals(cliente.getInactivo()) ? 
				enum2short(CustomerStatus.ACTIVE) :
				enum2short(CustomerStatus.INACTIVE);

		Integer registry = insertEnterprise(
				emprnif.getNumdoc(), 
				emprnif.getDescripcion(), 
				emprnif.getAlias(), 	
				scopeId,
				status);

		enterprises.put(emprnif.getCdg(), registry);
		
		emprnif.visitEmpract(this);
		
		return true;
	}
	
	
	@Override
	public boolean visitDomicilio(Domicilio domicilio) throws SQLException {
		domicilio.visitEmprdom(this);
		return true;
	}
	
	@Override
	public boolean visitEmprdom(Emprdom emprdom, Domicilio domicilio ) throws SQLException {
		

		Integer enterprise = enterprises.get(emprdom.getCodemp());
		
		if ( enterprise == null )
		{
			debug("domicilio[{}] : Not found enterprise {} .", 
					emprdom.getCdg(),emprdom.getCodemp());
			return true;
		}

		Integer raddress = DefaultMysqlDB.get(raddresses, enterprise, domicilio.getCdg());
		
		if ( raddress == null ) {

			StreetType streetType = null;
			try {
				streetType = getStreetType(domicilio.getTipovia());
			} catch (StreetTypeNotFoundException e) {
				debug("domicilio[{}] : Not found tipo via {}", 
						domicilio.getCdg(), domicilio.getTipovia());
			}
			Integer geozone = null;
			try {
				geozone = getGeoZone(domicilio.getProvincia());
			} catch (GeoZoneNotFoundException e) {
				debug("domicilio[{}] : Not found provincia {}", 
						domicilio.getCdg(), domicilio.getProvincia());
			}

			raddress = insertRaddress(enterprise, 
					enum2short ( AddressType.DELEGATION ), 
					null, 
					enum2short(streetType), 
					domicilio.getNomvia(), 
					domicilio.getNumero(), 
					domicilio.getOtrdir(),
					null,
					domicilio.getCodpos(), 
					domicilio.getLocalidad(), 
					geozone);
			
			try {
				insertTelephone (enterprise, raddress, domicilio.getTelefono() );
			} catch (InvalidTelephoneException e) {
				error("domicilio[{}] : Invalid telephone {}", 
						domicilio.getCdg(), domicilio.getTelefono());
			}
			try {
				insertTelephone (enterprise, raddress, domicilio.getTelefono2() );
			} catch (InvalidTelephoneException e) {
				error("domicilio[{}] : Invalid telephone {}", 
						domicilio.getCdg(), domicilio.getTelefono2());
			}
			try {
				insertTelephone (enterprise, raddress, domicilio.getTelefono3() );
			} catch (InvalidTelephoneException e) {
				error("domicilio[{}] : Invalid telephone {}", 
						domicilio.getCdg(), domicilio.getTelefono3());
			}
			try {
				insertFax(enterprise, raddress, domicilio.getFax() );
			} catch (InvalidFaxException e) {
				error("domicilio[{}] : Invalid fax {}", 
						domicilio.getCdg(), domicilio.getFax());
			}
			DefaultMysqlDB.save(raddresses, enterprise, domicilio.getCdg(), raddress);
		}

		String tipoDom = emprdom.getTipdom();
		if ( tipoDom != null && "T".equalsIgnoreCase(tipoDom.trim()) ){ 
			
			Integer workplace = DefaultMysqlDB.get(workplaces, emprdom.getCodemp(), emprdom.getCoddom(),emprdom.getCodact());
			
			if ( workplace == null ){
				String description = 
					domicilio.getAclaracion();
				if ( description == null ){
					// TODO: ¬ø Ddescripci√≥n no 'nula' ?
					description = domicilio.getNomvia(); 
				}
				
				Integer activity = activities.get(emprdom.getCodact());
				
				workplace = insertWorkplace(
						enterprise, 
						description, 
						raddress, 
						null,				// TODO:  Concierto EconÛmico del Centro de Trabajo
						true,
						activity);
	
				DefaultMysqlDB.save(workplaces, emprdom.getCodemp(), emprdom.getCoddom(),emprdom.getCodact(), workplace);
			}
		}
		
		return true;
	}
	
	
	@Override
	public boolean visitEmpract(Empract empract, Emprnif emprnif) throws SQLException {
		
		Integer cnae = null;
		try {
			cnae = getCnae2009Id(empract.getCnae2009());
		} catch (NullCNAEException e) {
			error("empreact[{}]: Null CNAE (2009)", empract.getCdg());
			return true;
		} catch (CNAENotFoundException e) {
			error("empreact[{}]: Not found CNAE (2009) {} {} ", 
					empract.getCdg(), empract.getCnae2009(), empract.getActeco());
			return true;
		}
		
		
		Integer enterprise = enterprises.get(empract.getCodemp());
		// TODO : CÛmo elegimos el tipo de actividad ?
		
		Integer activityId  = get(cnae_activity, enterprise, cnae);
		if ( activityId == null ) {
			activityId = 
				insertEnterprise_activity(empract.getDescripcion(), 
										enterprise, 
										cnae, 
										enum2short(EnterpriseActivityType.PRINCIPAL));
			save(cnae_activity, enterprise, cnae, activityId);
		}
		activities.put(empract.getCdg(), activityId);
		
		empract.visitEmprccc(this);

		return true;
	}

	
	
	@Override
	public boolean visitEmprccc(Emprccc emprccc, Empract empract) throws SQLException {
		
		String ccc = emprccc.getDescripcion();
		
		Integer geozone = null;

		if ( ccc != null ){
			String provincia = ccc.substring(0, 2) ;
			try { 
				geozone = Integer.parseInt(provincia);
			} catch (NumberFormatException e) {
				error("emprecc[{}] : Invalid CCC {}", emprccc.getCdg(), ccc);
				return true;
			}
		}
		else {
			debug("emprecc[{}] : Null CCC", emprccc.getCdg() );
		}
		

		Short type = null ;
		String tipccc = emprccc.getTipccc();
		if ( "P".equals(tipccc))
			type = enum2short(CCCType.PRINCIPAL);
		if ( "R".equals(tipccc))
			type = enum2short(CCCType.LEARNING);
		if ( "S".equals(tipccc))
			type = enum2short(CCCType.ASSIMILATEDS);
		if ( "A".equals(tipccc))
			type = enum2short(CCCType.TRADE_REPRESENTATIVE);
		
		Integer activity = activities.get(empract.getCdg());
		
		Integer cccId = insertEnterprise_ccc(ccc, 
				type, 
				activity, 
				geozone);
		
		DefaultMysqlDB.save(cccs, emprccc.getCdg(), emprccc.getTipccc(), cccId );
		
		return true;
	}
	
	
	@Override
	public boolean visitEmprper(Emprper emprper)
			throws SQLException {
		
		if ( outOfDate(emprper.getFecbaj()))
			return true;
		
		Integer workplace = 
			DefaultMysqlDB.get(workplaces, emprper.getCodemp(), emprper.getDomicilio(), emprper.getCodact());
		if ( workplace == null ){
			error("emprper[{}] : Not found workplace for {}/{}/{}", 
					emprper.getCdg(), emprper.getCodemp(), emprper.getDomicilio(), emprper.getCodact());
			return true;
		}
		Integer person =
			persons.get( emprper.getCodper() );
		if ( person == null ){
			error("emprper[{}] : Not found person {}", 
					emprper.getCdg(), emprper.getCodper());
			return true;
		}
		Integer ccc = 
			DefaultMysqlDB.get(cccs, emprper.getCodact(), emprper.getCodccc());
		if ( ccc == null ){
			error("emprper[{}] : Not found CCC {}/{}", 
					emprper.getCdg(), emprper.getCodact(), emprper.getCodccc());
			return true;
		}
		
		this.contractId = 
			insertContract(person, 
					workplace, 
					ccc, 
					emprper.getFecalt(), 
					emprper.getFecbaj(), 
					null,
					null,
					enum2short(ContractStatus.PROCESSED));
		
		emprper.visitTrabajo(this);
		emprper.visitPercep(this);
		emprper.visitTrabdto(this);
		
		emprper.visitNomina(this);
		emprper.visitNominaex(this);
	
		return true;
	}
	
	@Override
	public boolean visitTrabajo(Trabajo trabajo, Emprper emprper )
			throws SQLException {
		
		
		
		Integer person = persons.get(emprper.getCodper());

		if ( person == null ){
			error("emprper[{}] : Not found person {}", 
					emprper.getCdg(), emprper.getCodper());
			return true;
		}
		
		String tc2 = trabajo.getCodtc2();
		if ( tc2 == null ){
			error("emprper[{}] : Not found TC2 {}", 
					emprper.getCdg(), tc2);
			return true;
		}
		
		String description = null;
		/*
		Tipocont tipocont = null ; //trabajo.getRel_tra_cont();
		if ( tipocont != null ) {
			description = tipocont.getDescripcion();
		}
		*/
		
		String conditions = null;
		/*
		Colectivos colectivos = null ; //trabajo.getRel_tra_col();
		if ( colectivos != null ) {
			conditions = colectivos.getDescripcion();
		}*/
		
		insertContract_data(
				this.contractId, 
				trabajo.getCodtc2(), 
				description, 
				conditions, 
				trabajo.getFecini(), 
				trabajo.getFecfin());
		
		BigDecimal irpf = trabajo.getIrpf();
		String irpfFunction = String.format("%.3f%%", irpf != null ? irpf : 0.00 );
		
		insertContract_deduction(
				enum2short(DeductionType.IRPF), 
				this.contractId, 
				null, 
				irpfFunction, 
				trabajo.getFecini(), 
				trabajo.getFecfin());
		
		
		return true;
	}
	
	
	private Pattern overtimePattern = 
		Pattern.compile("HORAS\\s+EXTRA", Pattern.CASE_INSENSITIVE);
	private Pattern baseSalaryPattern = 
		Pattern.compile("SALARIO\\s+BASE", Pattern.CASE_INSENSITIVE);
	private Pattern compensationPattern = 
		Pattern.compile("INDEMNIZACION", Pattern.CASE_INSENSITIVE);
	private Pattern noticePattern = 
		Pattern.compile("INDEMNIZACION.*AVISO", Pattern.CASE_INSENSITIVE);
	private Pattern movingPattern = 
		Pattern.compile("INDEMNIZACION.*TRASLADO", Pattern.CASE_INSENSITIVE);
	private Pattern dismissalPattern = 
		Pattern.compile("INDEMNIZACION.*DESPIDO", Pattern.CASE_INSENSITIVE);

	
	private PaymentType getPaymentType(String description, String dinEsp ) {

		PaymentType paymetType = null;
		
		if ( "E".equalsIgnoreCase(dinEsp)){
			paymetType = PaymentType.SALARY_IN_KIND;
		}else if (baseSalaryPattern.matcher(description).find()) {
			paymetType = PaymentType.BASE_SALARY;
		}else if (overtimePattern.matcher(description).find()) {
			paymetType = PaymentType.OVERTIME_HOURS;
		}else if (noticePattern.matcher(description).find()) {
			paymetType = PaymentType.MOVING_COMPENSATION;
		}else if (movingPattern.matcher(description).find()) {
			paymetType = PaymentType.MOVING_COMPENSATION;
		}else if (dismissalPattern.matcher(description).find()) {
			paymetType = PaymentType.MOVING_COMPENSATION;
		}else if (compensationPattern.matcher(description).find()) {
			paymetType = PaymentType.COMPENSATION_SUPLY;
		}else {
			paymetType = PaymentType.SALARY_SUPPLEMENTS;
		}
		
		return paymetType;
	}

	private String getFunction(BigDecimal importe, BigDecimal impuni, BigDecimal unidades) {
		if ( impuni != null && impuni.doubleValue() != 0 ) {
			if ( unidades != null ) {
				return String.format("%.3f * %.3f", impuni, unidades );
			} 
		}
		return String.format("%.3f", importe );
	}
	
	
	@Override
	public boolean visitPercep(Percep percep, Emprper emprper)
			throws SQLException {
		

		
		PaymentType paymetType = 
			getPaymentType(percep.getDescom(), percep.getDinesp());
		
		BigDecimal impuni = percep.getImpuni();
		BigDecimal importe = percep.getImporte();
		BigDecimal unidades = percep.getUnidades();
		String script = getFunction(importe, impuni, unidades);
		
		insertContract_payment(
				enum2short(paymetType), 
				this.contractId, 
				percep.getDescom(), 
				script, 
				percep.getFecini(), 
				percep.getFecfin());
		
		return true;
	}

	private Pattern kindPattern = 
		Pattern.compile("ESPECIE", Pattern.CASE_INSENSITIVE);
	private Pattern advancePattern = 
		Pattern.compile("ANTICIPO", Pattern.CASE_INSENSITIVE);
	
	private DeductionType getDeductionType(String description) {

		DeductionType deductionType = null;
		
		if (advancePattern.matcher(description).find()) {
			deductionType = DeductionType.ADVANCE_PAYMENT;
		}if (kindPattern.matcher(description).find()) {
			deductionType = DeductionType.IN_KIND;
		}else {
			deductionType = DeductionType.OTHER;
		}
		
		return deductionType;
	}

	@Override
	public boolean visitTrabdto(Trabdto trabdto, Emprper emprper) throws SQLException {

		
		String concepto = trabdto.getConcepto();
		
		DeductionType type = getDeductionType(concepto);

		BigDecimal importe = trabdto.getImporte();
		String function = String.format("%.3f", 
				importe != null ? importe : 0);
		
		insertContract_deduction(enum2short(type), 
				this.contractId, 
				concepto, 
				function, 
				trabdto.getFecini(), 
				trabdto.getFecfin());
		
		return true;
	}

	
	private double toDouble(BigDecimal... bigDecimals) {
		double result = 0;
		for (BigDecimal bigDecimal : bigDecimals) {
			if ( bigDecimal != null  ) {
					result += bigDecimal.doubleValue();
			}
		}
		return result;
	}
	

	@Override
	public boolean visitNomina(Nomina nomina, Emprper emprper) throws SQLException {
		
		if ( outOfDate( nomina.getFecfin()))
			return true;
		
		Double totalPayment = toDouble(nomina.getTotal_devengos()); 
		Double totalDeduction = toDouble(nomina.getTotal_deducir());
		Double totalLiquid = toDouble(nomina.getTotal_liquido());
		
		Double baseConcom = toDouble(nomina.getBase_concom());
		Double baseIRPF = toDouble(nomina.getBase_irpf());
		Double baseprorrataPagas = toDouble(nomina.getBase_proext());
		Double renumeration = toDouble(nomina.getRemuneracion());
		Double total = toDouble(nomina.getTotal_1());
		Double baseHExtras = toDouble(nomina.getBase_hextras(), nomina.getBase_hextras_no());
		Double baseProfessional = toDouble(nomina.getBase_acc(),nomina.getBase_desempleo(), 
					nomina.getBase_fogasa(),nomina.getBase_fp());
		//Double totalCuotas = toDouble(nomina.getImporte_cuotas());
		
		this.salaryId= 
			insertSalary(this.contractId, 
					nomina.getFecini(), 
					nomina.getFecfin(), 
					nomina.getLocalidad(), 
					nomina.getNomper(), 
					nomina.getNomemp(), 
					nomina.getNummat(), 
					nomina.getDiasnomina(), // TODO: Dias efectivos .. 
					totalPayment, 
					totalDeduction, 
					totalLiquid, 
					nomina.getFecemi(), 
					renumeration, 
					baseprorrataPagas, 
					total, 
					baseConcom, 
					baseProfessional,
					baseHExtras,
					baseIRPF);
		
		Double importeCg = toDouble(nomina.getImporte_cg());
		if ( importeCg > 0 ) {
			BigDecimal cgPercentage = 
				nomina.getPrc_cg();
			String cgFunction = String.format("%.2f%%", 
					cgPercentage != null ? cgPercentage : 0);
			insertSalary_deduction(this.salaryId, 
					enum2short(DeductionType.COMMON_CONTINGENCY), 
					null, 
					cgFunction, 
					importeCg);
		}

		Double importeAcc = toDouble(nomina.getImporte_acc());
		
		if ( importeAcc > 0  ){ 
			
			double accPercentage = toDouble(nomina.getPrc_acc());
			
			if ( accPercentage == 1.65 &&  accPercentage == 1.70 ) {
				double jobPercentage = 0.10;
				double uePercentage = accPercentage - jobPercentage; 
				
				double importeJob = jobPercentage * importeAcc / accPercentage;
				String jobFunction = String.format("%.2f%%", jobPercentage);
				insertSalary_deduction(this.salaryId, 
						enum2short(DeductionType.JOB_TRAINING), 
						null, 
						jobFunction, 
						importeJob);

				String ueFunction = String.format("%.2f%%", uePercentage);
				insertSalary_deduction(this.salaryId, 
						enum2short(DeductionType.UNEMPLOYMENT), 
						null, 
						ueFunction, 
						importeAcc - importeJob);
			}
			else {
				String accFunction = String.format("%.2f%%", accPercentage);
				insertSalary_deduction(this.salaryId, 
						enum2short(DeductionType.PROFESSIONAL_CONTINGENCY), 
						null, 
						accFunction, 
						importeAcc);
			}
		}
		
		Double importeHex = toDouble(nomina.getImporte_hex());
		if ( importeHex > 0 ) {
			BigDecimal hexPercentage = 
				nomina.getPrc_hex();
			String hexFunction = String.format("%.2f%%", 
					hexPercentage != null ? hexPercentage : 0);
			insertSalary_deduction(this.salaryId, 
					enum2short(DeductionType.STRUCTURAL_OVERTIME), 
					null, 
					hexFunction, 
					importeHex);
		}

		Double importeHexNo = toDouble(nomina.getImporte_hexno());
		if ( importeHexNo > 0 ) {
			BigDecimal hexNoPercentage = 
				nomina.getPrc_hexno();
			String hexNoFunction = String.format("%.2f%%", 
					hexNoPercentage != null ? hexNoPercentage : 0);
			insertSalary_deduction(this.salaryId, 
					enum2short(DeductionType.NON_STRUCTURAL_OVERTIME), 
					null, 
					hexNoFunction, 
					importeHexNo);
		}


		Double importeIrpf = toDouble(nomina.getImporte_irpf());
		
		if ( importeIrpf > 0 ){ 
			BigDecimal irpfPercentage = 
				nomina.getPrc_irpf();
			String irpfFunction = String.format("%.2f%%", 
					irpfPercentage != null ? irpfPercentage : 0);
			insertSalary_deduction(this.salaryId, 
					enum2short(DeductionType.IRPF), 
					null, 
					irpfFunction, 
					importeIrpf);
		}
		
		
		nomina.visitNominadev(this);
		nomina.visitNomdto(this);

		return true;
	}

	@Override
	public boolean visitNominadev(Nominadev nominadev, Nomina nomina)
			throws SQLException {
		
		String description = 
			nominadev.getDescom();
		
		PaymentType paymetType = 
			getPaymentType( description, nominadev.getDinesp());
		
		BigDecimal importe = nominadev.getImporte();
		BigDecimal impuni = nominadev.getImpuni();
		BigDecimal unidades = nominadev.getUnidades();
		
		String function = getFunction(importe, impuni, unidades);
		
		insertSalary_payment(this.salaryId, 
				enum2short(paymetType), 
				description, 
				function,
				importe != null ? importe.doubleValue() : 0.00 );
		
		return true;
	}
	
	
	@Override
	public boolean visitNomdto(Nomdto nomdto, Nomina nomina) throws SQLException {

		String concepto = nomdto.getConcepto();
		
		DeductionType type = getDeductionType(concepto);

		Double importe = toDouble(nomdto.getImporte());

		String function = String.format("%.3f", 
				importe != null ? importe : 0);
		
		
		insertSalary_deduction(
				this.salaryId, 
				enum2short(type), 
				concepto, 
				function, 
				importe);
		
		return true;
	}
	

	@Override
	public boolean visitNominaex(Nominaex nominaex, Emprper emprper) throws SQLException {
		
		if ( outOfDate( nominaex.getFecfin()))
			return true;
		
		Double totalPayment = toDouble(nominaex.getImporte()); 
		Double totalDeduction = toDouble(nominaex.getTotal_deducir());
		Double totalLiquid = toDouble(nominaex.getLiquido());
		
		
		Double baseIRPF = totalPayment;
		
		this.salaryId= 
			insertSalary(this.contractId, 
					nominaex.getFecini(), 
					nominaex.getFecfin(), 
					nominaex.getLocalidad(), 
					nominaex.getNomper(), 
					nominaex.getNomemp(), 
					nominaex.getNummat(), 
					0, 					// TODO: Dias efectivos .. 
					totalPayment, 
					totalDeduction, 
					totalLiquid, 
					nominaex.getFecemi(), 
					0.00, 
					0.00, 
					0.00, 
					0.00, 
					0.00,
					0.00,
					baseIRPF);
		
		Double importeIrpf = toDouble(nominaex.getImpirpf());
		
		if ( importeIrpf > 0 ){ 
			BigDecimal irpfPercentage = 
				nominaex.getIrpf();
			String irpfFunction = String.format("%.2f%%", 
					irpfPercentage != null ? irpfPercentage : 0);
			insertSalary_deduction(this.salaryId, 
					enum2short(DeductionType.IRPF), 
					null, 
					irpfFunction, 
					importeIrpf);
		}

		
		nominaex.visitNomdtoex(this);
		
		return true;
	}
	
	
	@Override
	public boolean visitNomdtoex(Nomdtoex nomdtoex, Nominaex nominaex)
			throws SQLException {
		String concepto = nomdtoex.getConcepto();
		
		DeductionType type = getDeductionType(concepto);

		Double importe = toDouble(nomdtoex.getImporte());

		String function = String.format("%.3f", 
				importe != null ? importe : 0);
		
		
		insertSalary_deduction(
				this.salaryId, 
				enum2short(type), 
				concepto, 
				function, 
				importe);
		
		return true;
	}
	
	public void writeAll(CtsqlDB ctsqlReader) throws SQLException {
		
		start();
		
		ctsqlReader.visitTipdoc(this);
		ctsqlReader.visitTipovia(this);
		ctsqlReader.visitProvincia(this);
		
		
		ctsqlReader.visitPersona(this);

		ctsqlReader.visitDelegacion(this);
		ctsqlReader.visitDomicilio(this);
		
		// contracts
		ctsqlReader.visitEmprper(this);


		finish();
	}
	
	
	
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
		
		// create the command line parser
    	CommandLineParser parser = new PosixParser();   
    	
    	// create the Options
    	Options options = new Options();

    	OptionBuilder.isRequired(false);
    	OptionBuilder.hasArg(false);
    	OptionBuilder.withDescription("imprime esta ayuda.");
    	Option helpOption = OptionBuilder.create( "help" );

    	
    	OptionBuilder.isRequired(false);
    	OptionBuilder.hasArg(true);
    	OptionBuilder.withArgName( "URL" );
    	OptionBuilder.withType(String.class);
    	OptionBuilder.withDescription(  "cadena de conexi√≥n." );
    	Option ctsqlURLOption = OptionBuilder.create( "url" );

    	OptionBuilder.isRequired(false);
    	OptionBuilder.hasArg(true);
    	OptionBuilder.withArgName( "name" );
    	OptionBuilder.withType(String.class);
    	OptionBuilder.withDescription(  "usuario para conectarse." );
    	Option ctsqlUserOption = OptionBuilder.create( "user" );

    	OptionBuilder.isRequired(false);
    	OptionBuilder.hasArg(true);
    	OptionBuilder.withArgName( "name" );
    	OptionBuilder.withType(String.class);
    	OptionBuilder.withDescription(  "clave para conectarse." );
    	Option ctsqlPasswdOption = OptionBuilder.create( "passwd" );


    	options.addOption(helpOption);
    	options.addOption(ctsqlURLOption);
    	options.addOption(ctsqlUserOption);
    	options.addOption(ctsqlPasswdOption);
    	
    	HelpFormatter helpFormatter = new HelpFormatter();
    	
    	try {
    		
    		// first of all load JDBC drivers
            Class.forName("org.gjt.mm.mysql.Driver");

            // parse the command line arguments
            CommandLine line = parser.parse( options, args );
            
            if ( line.hasOption(helpOption.getOpt()) )
            	helpFormatter.printHelp(HelpFormatter.DEFAULT_SYNTAX_PREFIX, options, true);
            
            String url = line.getOptionValue(ctsqlURLOption.getOpt(), 
            		"jdbc:mysql://127.0.0.1:3306/payroll-esferalia-org");
            String user = line.getOptionValue(ctsqlUserOption.getOpt(), "dbuser");
            String passwd = line.getOptionValue(ctsqlPasswdOption.getOpt(), "serubd2000");
            
            String mysqlDBArgs [] = {
            		"-url", url,
            		"-user", user,
            		"-passwd", passwd,
            		"-out" , "src/main/java/com/esferalia/aon/payroll/ctsql2mysql/AbstractMysqlDB.java" ,
            		"-template" , "src/main/java/com/esferalia/aon/payroll/ctsql2mysql/templates/MysqlDB.java.vm" 
            };
            DBContext.main(mysqlDBArgs);
            
            
    	}
        catch( ParseException exp ) {
            // oops, something went wrong
            System.err.println( "Error : " + exp.getMessage() );
        	helpFormatter.printHelp(HelpFormatter.DEFAULT_SYNTAX_PREFIX, options, true);
        } 

	}


}
