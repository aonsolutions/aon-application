package com.esferalia.aon.payroll.ctsql2mysql;


import static com.esferalia.aon.payroll.ctsql2mysql.DefaultMysqlDB.enum2short;
import static com.esferalia.aon.payroll.ctsql2mysql.DefaultMysqlDB.toDouble;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.ACTUAL_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CNO;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CONTRACT_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.DELAY;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.ENTRY_BY_COMPANY_ACCOUNT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.EXTRA_PAY;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.FREE_IPREM;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.FREE_IPREM_SHORT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.FULL_TIME;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.HOLIDAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.IPREM_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.IPREM_BASE_SHORT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.IRPF_PERCENT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.IRREGULAR;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MORE_THAN_65;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.OCCUPATION;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.QUOTE_GROUP;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SALARY;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SHORT_CONTRACT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SPECIAL_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TC2;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.UNEMPLOY_EMPLOYEE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WEEK_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WEEK_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WORKED_DAYS;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;

import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Bonifica;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Embargo;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprper;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Linporco;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nominadf;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Percep;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Trabajo;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Trabdto;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Trabinci;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractMysqlDB.Salary_embargo;
import com.esferalia.aon.payroll.ctsql2mysql.IConcepts.Bonus;
import com.esferalia.aon.payroll.ctsql2mysql.IConcepts.Concept;
import com.esferalia.aon.payroll.ctsql2mysql.MyAgreement.PercepPercnivComparator;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractStatus;
import com.esferalia.aon.payroll.enumeration.OccupationType;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.Period;

public class MyContract extends DefaultCtsqlDBVisitor implements IContracts{
	
	private String DELAY_SQL = "SELECT nomina.*"  
		+ " FROM nomina, nominadev" 
		+ " WHERE nomina.cdg = nominadev.cdg" 
		+ " AND nomina.numero=?"
		+ " AND nominadev.codcom=?" 
		+ " AND nomina.tipo='N'"
		+ " AND fecini >= ?"
		+ " AND fecfin <= ?";
	
	

	private static final Concept<PaymentType> NULL_CONCEPT = 
		new Concept<PaymentType>(null, null, null, null, null );
	
	private static class Percents {
		private java.sql.Date startDate;
		private java.sql.Date endDate;
		
		private double total;
		private double employee;
		private double enterprise;
		
	}

	private static class ContractPorCot {
		
		private java.sql.Date	endDate;
		private java.sql.Date 	startDate;
	
		private String 	porCot;
		
		
		public ContractPorCot(java.sql.Date startDate,
							java.sql.Date endDate,
							String porCot) 
		{
			this.startDate = startDate;
			this.endDate = endDate;
			this.porCot = porCot;
		}
	}
	
	
	
	private class TrabajoAggregate extends DefaultCtsqlDBVisitor{
		
		@Override
		public void visitTrabajo_emprper(Trabajo trabajo, Emprper emprper)
				throws SQLException {
			
			java.sql.Date fecFin = trabajo.getFecfin();
			java.sql.Date fecBaja = emprper.getFecbaj();
			
			if ( fecFin == null || DefaultMysqlDB.is9999(fecFin) ||
				( fecBaja != null && fecFin.compareTo(fecBaja)>=0 ) ){
				MyContract.this.codCon = trabajo.getCodcon();
				MyContract.this.nivel = trabajo.getNivel();
				MyContract.this.agreementLevelId = 
						MyContract.this.agreements.getAgreementLevel(trabajo.getCodcon(), trabajo.getNivel());
				MyContract.this.agreementCategory = 
					MyContract.this.agreements.getAgreementCategory(trabajo.getCodcon(), trabajo.getNivel(), trabajo.getCodcat());
				if ( MyContract.this.agreementCategory == null ) {
					MysqlDB.error("trabajo[{}] : Unknow category  {}/{}/{}", 
							trabajo.getCdg(), trabajo.getCodcon(), trabajo.getNivel(), trabajo.getCodcat() );
					MyContract.this.agreementCategory = 
						MyContract.this.agreements.insertAgreementCategory(trabajo.getCodcon(), trabajo.getNivel(), trabajo.getCodcat());
				}
			}
		}
	}
	
	
	protected static boolean before ( java.sql.Date oneDate, java.sql.Date anotherDate ) {
		if ( anotherDate == null ) 
			return true;
		if ( oneDate == null ) 
			return false;
		return oneDate.compareTo(anotherDate) <= 0;
	}

	protected static boolean after( java.sql.Date oneDate, java.sql.Date anotherDate ) {
		if ( oneDate == null ) 
			return true;
		if ( anotherDate == null ) 
			return false;
		return oneDate.compareTo(anotherDate) >= 0;
	}
	
	private String			passwdHash;
	
	
	private Integer 		contractId;
	private Integer 		agreementLevelId;
	private String 			agreementCategory;
	private String			codCon;
	private String			nivel;
	
	private Date 			fromDate;
	private boolean 		checkFVisonado;
	
	private IPersons 		persons;
	private IConcepts 		concepts;
	private IEnterprises 	enterprises;
	private IAgreements 	agreements;
	private ICalendars 		calendars;

	private MySalary		mySalary;
	private MyIrpfData		myIrpfData;
	
	private DefaultMysqlDB 	mysqlDB;
	private AbstractCtsqlDB ctsqlDB;
	
	private List<ContractPorCot> 		contractPorCots;
	private Map<String, List<Percents>> quotePercents;
	private Map<String, FullEmbargo> 	embargos;
	
	private Integer registration ;
	private java.sql.Date seniorityDate ;
	
	
	private PreparedStatement				delayStmt = null;

	
	private Map<String, SortedSet<Period>>  		perceps;
	
	private Collection<String> 						delayCodCmos;

	private Map<String, List<ContractData>> contractDatas ;
	private Map<String, List<ContractVariable>> contractVariables;
	

	public MyContract(DefaultMysqlDB mysqlDB, IEnterprises enterprises, IPersons persons, IConcepts concepts, IAgreements agreements, ICalendars calendars , String passwdHash) 
	{
		this ( mysqlDB, enterprises, persons, concepts, agreements, calendars, passwdHash, null, false );
	}

	public MyContract(DefaultMysqlDB mysqlDB, IEnterprises enterprises, IPersons persons,  IConcepts concepts, IAgreements agreements, ICalendars calendars, String passwdHash, Date fromDate, boolean checkFVisonado) 
	 {
		this.mysqlDB = mysqlDB;
		this.passwdHash = passwdHash;
		this.fromDate = fromDate;
		this.persons = persons;
		this.concepts = concepts;
		this.agreements = agreements;
		this.enterprises = enterprises;
		this.calendars = calendars;
		this.checkFVisonado = checkFVisonado;
		this.contractPorCots = new LinkedList<ContractPorCot>();
		this.quotePercents = new HashMap<String, List<Percents>>();
		this.embargos = new HashMap<String, FullEmbargo>();
		this.mySalary = new MySalary(mysqlDB, this, concepts);
		this.myIrpfData = new MyIrpfData(mysqlDB, this);
		
		this.perceps = new HashMap<String, SortedSet<Period>>();
		this.contractDatas = new HashMap<String, List<ContractData>>();
		this.contractVariables = new HashMap<String, List<ContractVariable>>();
		
	}
	
	@Override
	public Integer getContractId(Integer oldCdg) {
		return this.contractId;
	}
	
	@Override
	public boolean checkFVisionado() {
		return checkFVisonado;
	}
	
	@Override
	public boolean outOfDate ( Date date ) {
		if ( date == null )
			return false;
		if ( fromDate == null )
			return false;
		return fromDate.compareTo(date) > 0 ; 
	}
	
	@Override
	public boolean hasEmbargo(String embargoConcept) {
		return this.embargos.containsKey(embargoConcept);
	}
	
	@Override
	public FullEmbargo getEmbargo(String concepto) {
		return this.embargos.get(concepto);
	}
	
	private java.sql.Date getEndDate(java.sql.Date fecFin, Emprper emprper) throws SQLException{
		return DefaultMysqlDB.is9999(fecFin)? null /*emprper.getFecbaj()*/: fecFin;
	}

	private boolean isActive( Emprper emprper ) 
	throws SQLException {
		Date fecbaj = emprper.getFecbaj();
		if ( fecbaj == null )
			return true;
		Date today = Calendar.getInstance().getTime();
		return today.compareTo(fecbaj ) < 0;
	}
	
	private static Pattern kindPattern = 
		Pattern.compile("ESPECIE", Pattern.CASE_INSENSITIVE);
	private static Pattern advancePattern = 
		Pattern.compile("ANTICIPO", Pattern.CASE_INSENSITIVE);
	
	protected static DeductionType getDeductionType(String description) {

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
	
	private Pattern delayPattern = 
		Pattern.compile("ATRASOS", Pattern.CASE_INSENSITIVE);

	private SalaryType getSalaryType(Percep percep, Emprper emprper) 
	throws SQLException {

		String codcom = percep.getCodcom();
		
		if (delayCodCmos.contains(codcom)) {
			if ( !inSalary(percep)) {
				MysqlDB.info("percep[{}]: Percep {} {} {} at delay salary for {},{}",
						percep.getCdg(),
						codcom, 
						percep.getDesabr(),
						percep.getImporte(),
						emprper.getCdg(),
						emprper.getCodper());
				return SalaryType.DELAY;
			}
			else {
				MysqlDB.error("percep[{}]: Delay {} {} {} at standard salary for {},{}", 
						percep.getCdg(),
						codcom, 
						percep.getDesabr(),
						percep.getImporte(),
						emprper.getCdg(),
						emprper.getCodper());
			}
			
		}
		
		
		return SalaryType.SALARY;
		
	}

	
	private void addContractData(ContractPorCot contractData) {
		contractPorCots.add(contractData);
	}
	
	
	private java.sql.Date earlier ( java.sql.Date oneDate, java.sql.Date anotherDate ) {
		return before(oneDate, anotherDate) ? oneDate: anotherDate;
	}
	
	private java.sql.Date last( java.sql.Date oneDate, java.sql.Date anotherDate ) {
		return after(oneDate, anotherDate) ? oneDate: anotherDate;
	}

	private List<ContractPorCot> getContractData(java.sql.Date startDate, java.sql.Date endDate) {
		List<ContractPorCot> list = 
			new LinkedList<ContractPorCot>();
		
		for (ContractPorCot contractData : contractPorCots) {
			if ( after ( contractData.endDate, startDate ) && 
					before(contractData.startDate, endDate ) ) {
				list.add (new ContractPorCot(
						last(startDate, contractData.startDate), 
						earlier(endDate, contractData.endDate), 
						contractData.porCot));
			}
		}

		return list;
	}
	
	private Percents getPercents ( boolean indefinite, boolean fulltime ) {
		Percents percents = new Percents();
		
		percents.employee = indefinite ? 1.55 : 1.6 ; 
		
		return percents ; 
	}
	
	private Percents getPercents(String porCoti, java.sql.Date start, java.sql.Date end) {
		List<Percents> list = quotePercents.get(porCoti);
		if ( list == null ){ 
			return null;
		}
		
		for (Percents percents : list) {
			if ( before(percents.startDate, start) && 
					after(percents.endDate, end ) ){
				return percents;
			}
		}
		
		return null;
	}
	
	private String getAfectaExpression(String afecta, String expression ) {
		String format = "%s ? %s : 0.00";
		if ( "P".equals(afecta) ) 
			return String.format(format, EXTRA_PAY, expression ) ;
		if ( "A".equals(afecta) ) 
			return String.format(format, "(" + SALARY + " || " + EXTRA_PAY + ")", expression ) ;
		if ( "T".equals(afecta) ) 
			return String.format(format, DELAY, expression ) ;
		return String.format(format, SALARY, expression ) ;
	}
	
	private String getEmbargoExpression(Embargo embargo ) throws SQLException {
		String afecta = embargo.getAfecta();
		return getAfectaExpression(afecta, "(( PENDIENTE > EMBARGABLE ) ? EMBARGABLE : PENDIENTE)");
	}
	
	protected void init(AbstractCtsqlDB ctsqlDB)
			throws SQLException {
		this.ctsqlDB = ctsqlDB;
		
		this.delayCodCmos = getDelaysCodComs();
		ctsqlDB.visitLinporco(this);
		
	}
	
	@Override
	public void visit(AbstractCtsqlDB ctsqlDB) throws SQLException {
		init(ctsqlDB);
		ctsqlDB.visitEmprper(this);
		
	}
	
	private void insertEmbargos() throws SQLException{
		for (FullEmbargo embargo : embargos.values()) {
			
			
			Integer embargoId = 
				mysqlDB.insertContract_embargo(
					embargo.contractEmbargo.contract, 
					embargo.contractEmbargo.start_date, 
					embargo.contractEmbargo.end_date, 
					embargo.contractEmbargo.amount, 
					embargo.contractEmbargo.expression, 
					embargo.contractEmbargo.description);
			
			
			double left = embargo.contractEmbargo.amount;
			
			for (Salary_embargo salaryEmbargo : embargo.salaryEmbargos) {
				mysqlDB.insertSalary_embargo(
						salaryEmbargo.salary, 
						embargoId, 
						salaryEmbargo.amount, 
						salaryEmbargo.description);
				left -= salaryEmbargo.amount;
			}

			MysqlDB.info("embargo{}: {} {} ", 
					embargo.contractEmbargo.start_date , 
					embargo.contractEmbargo.description, 
					embargo.contractEmbargo.end_date == null ? "pendiente " + left :  embargo.contractEmbargo.end_date );
		}
		embargos.clear();
	}
	
	private java.sql.Date getEmbargosStartDate() {
		java.sql.Date startDate = null ;
		
		for (FullEmbargo embargo : embargos.values()) {
			if ( before(embargo.contractEmbargo.start_date, startDate)){
				startDate = embargo.contractEmbargo.start_date;
			}
		}
		
		return startDate;
	}
	
	

	private boolean inSalary(Percep percep) throws SQLException{
		ResultSet rs  = null;
		try {
			if ( delayStmt == null ){
				delayStmt = 
					ctsqlDB.ctsqlConnection.prepareStatement(DELAY_SQL);
			}
	
			delayStmt.setInt(1, percep.getNumero());
			delayStmt.setString(2, percep.getCodcom());
			delayStmt.setDate(3, percep.getFecini());
			delayStmt.setDate(4, percep.getFecfin());
			
			rs = delayStmt.executeQuery();
			return rs.next();
		}
		finally{
			if ( rs != null ) {
				rs.close();
			}
		}
	}
	
	private Collection<String> getDelaysCodComs() throws SQLException{
		ResultSet rs  = null;
		PreparedStatement stmt = null;
		try {
				stmt = 
					ctsqlDB.ctsqlConnection.prepareStatement(
							"SELECT codcom, count(*)"
							+" FROM nomina, nominadev"
							+" WHERE nomina.cdg = nominadev.cdg"
							+" AND nomina.tipo= 'A'"
							+" AND codcom <> ''"
							+" AND codcom IS NOT NULL"
							+" AND fecini >= ? "
							+" GROUP BY 1 "
							);
			java.sql.Date sqlFromDate = fromDate != null ? 
					new java.sql.Date(fromDate.getTime()) : 
						new java.sql.Date(1900, 01,01) ;
			stmt.setDate(1, sqlFromDate );
			
			rs = stmt.executeQuery();
			
			Collection<String> codComs = 
				new LinkedList<String>();
			while ( rs.next()) {
				String codcom = rs.getString("codcom");
				int count = rs.getInt(2);
				codComs.add(codcom);
				MysqlDB.info("codcom {} is i at delay {} times.", 
						codcom,count);
			}
			
			return codComs;
		}
		finally{
			if ( rs != null ) {
				rs.close();
			}
			if ( stmt != null ) {
				stmt.close();
			}
		}
	}
	
	@Override
	public void visitLinporco(Linporco linporco) throws SQLException {
		if ( outOfDate(linporco.getFecfin()))
			return ;
		Percents percents = new Percents();
		
		percents.startDate = linporco.getFecini();
		java.sql.Date fecFin = linporco.getFecfin();
		percents.endDate  =  DefaultMysqlDB.is9999(fecFin) ? null : fecFin;
		percents.employee = toDouble(linporco.getPcttra());
		percents.enterprise = toDouble(linporco.getPctemp());
		percents.total = toDouble(linporco.getPcttot());
		
		String key = linporco.getCdg();
		List<Percents> list = quotePercents.get(key);
		if ( list == null ) {
			list = new LinkedList<MyContract.Percents>();
			quotePercents.put(key, list);
		}
		
		list.add(percents);
		
	}
	
	@Override
	public void visitEmprper(Emprper emprper)
			throws SQLException {
		
		if ( outOfDate(emprper.getFecbaj()))
			return ;
		
		
		Integer workplace = 
			enterprises.getWorkplace(emprper.getCodemp(), emprper.getDomicilio());
		if ( workplace == null ){
			MysqlDB.error("emprper[{}] : Not found workplace for {}/{}", 
					emprper.getCdg(), emprper.getCodemp(), emprper.getDomicilio());
			return ;
		}
		Integer person =
			persons.getPerson( emprper.getCodper() );
		if ( person == null ){
			MysqlDB.error("emprper[{}] : Not found person {}", 
					emprper.getCdg(), emprper.getCodper());
			return ;
		}
		Integer ccc = 
			enterprises.getCCC(emprper.getCodact(), emprper.getCodccc());
		if ( ccc == null ){
			MysqlDB.debug("emprper[{}] : Without CCC {}/{}", 
					emprper.getCdg(), emprper.getCodact(), emprper.getCodccc());
		}
		
		Integer activityId = enterprises.getActivityId(emprper.getCodact());
		
		registration = null;
		seniorityDate = null;
		emprper.visitTrabajo_emprper(new DefaultCtsqlDBVisitor(){
			@Override
			public void visitTrabajo_emprper(Trabajo trabajo, Emprper emprper)
					throws SQLException {
				if ( outOfDate(trabajo.getFecfin()) ) 
					return;
				registration = trabajo.getNummat();	
				seniorityDate = trabajo.getFecant();	
			}
		});
		
		String codCCC = emprper.getCodccc();
		SSRegimeType ssRegimeType;
		if ( ccc == null && "A".equalsIgnoreCase(codCCC) ) {
			ssRegimeType = SSRegimeType.SELF_EMPLOYED;
		} else {
			String indRegimen = enterprises.getIndRegimen(emprper.getCodact());
			if ( "A".equalsIgnoreCase(indRegimen)) {
				ssRegimeType = SSRegimeType.AGRICULTURAL;
			}else if ( "M".equalsIgnoreCase(indRegimen)) {
				ssRegimeType = SSRegimeType.SEA_WORKERS;
			} else {
				ssRegimeType = SSRegimeType.GENERAL;
			}
		}
		
		this.codCon = null; 
		this.nivel = null;
		this.agreementLevelId = null; 
		this.agreementCategory = null;
		emprper.visitTrabajo_emprper(new TrabajoAggregate());
		
		if ( !agreements.inherits(emprper, this.codCon, this.nivel) ) {
			this.agreementLevelId = null; 
			this.agreementCategory = null;
		}
		
		Integer wcalendar = enterprises.getCalendar(workplace);
		Integer calendar = calendars.getCalendar(emprper.getCodemp(), emprper.getDomicilio(), emprper.getCodact());
		if ( calendar == wcalendar ) {
			calendar = null;
		}
		else {
			MysqlDB.info("emprper{}: Has diferent calendar {} {} ", emprper.getCdg(), calendar, wcalendar );
		}
		
		if ( this.agreementCategory != null ) {
			MysqlDB.info("emprper[{}]: [{}:{}] Inherits payments from {}-{}", 
					emprper.getCdg(), emprper.getCodper(), this.contractId, this.codCon, this.nivel);
			
		}
		
		this.contractId = 
			mysqlDB.insertContract(
					person, 
					workplace, 
					ccc, 
					emprper.getFecalt(), 
					emprper.getFecbaj(),
					calendar,
					null,
					null,
					//enum2short(ContractStatus.PROCESSED),
					registration,
					seniorityDate,
					activityId,
					enum2short(ssRegimeType),
					null, 									// TODO: model
					agreementCategory, 						// TODO: category_description
					enum2short(ContractStatus.PROCESSED),
					agreementLevelId 						// TODO: agreement_level
					);
		
		mysqlDB.insertContract_data(com.esferalia.aon.payroll.ContractData.COD_INT, 
				this.contractId, 
				emprper.getCodnsz(), 
				emprper.getFecalt(), 
				emprper.getFecbaj());

		String ingEspEmp = 
			enterprises.getIngEspEmp(emprper.getCodact());
		if ( "S".equalsIgnoreCase(ingEspEmp ) ){
			mysqlDB.insertContract_data(ENTRY_BY_COMPANY_ACCOUNT.getName(), 
					this.contractId, 
					Boolean.TRUE.toString(), 
					emprper.getFecalt(), 
					emprper.getFecbaj());
		} //end-if: IRPF en especie a cuenta de  la empresa.
		
		String mayor65 = emprper.getMayor65();
		if ( "S".equals(mayor65 ) ) {
			mysqlDB.insertContract_data(MORE_THAN_65.getName(), 
					this.contractId, 
					Boolean.TRUE.toString(), 
					emprper.getFecalt(), 
					emprper.getFecbaj());
			MysqlDB.info("emprper[{}]: [{}:{}] Older than 65 ", 
					emprper.getCdg(), emprper.getCodper(), this.contractId );
		}
		
		String shortContract = "N";
		Date fecBaja = emprper.getFecbaj();
		if ( fecBaja != null ) {
			Date fecIni  = emprper.getFecalt();
			long days = ( fecBaja.getTime() - fecIni.getTime() ) / (24 * 3600 * 1000) ; 
			if ( days + 1  < 7 ) {
				shortContract = "S";
			}
		}
		
		String contrTemp = emprper.getContr_temp();
		if ( contrTemp == null ) {
			contrTemp = "N";
		}
		
		if ( !shortContract.equals(contrTemp )) {
			mysqlDB.insertContract_data(
					SHORT_CONTRACT.getName(), 
					this.contractId, 
					contrTemp.equals("S") ? Boolean.TRUE.toString() : Boolean.FALSE.toString(), 
					emprper.getFecalt(), 
					emprper.getFecbaj());
			MysqlDB.error("emprper[{},{}]: [{}:{}] Contract is short contract '{}'", 
					emprper.getCdg(), this.contractId, emprper.getFecalt(), emprper.getFecbaj(), contrTemp);
		}
		
		String numDoc = 
			persons.getNumDoc(emprper.getCodper());
		
		Integer enterprise = 
			enterprises.getEnterprise(emprper.getCodemp());
		
		if ( isActive( emprper )) {
			String name = persons.getName(emprper.getCodper());
			String passwd = null;
			if ( passwdHash != null ) {
				passwd = passwdHash;
			}
			else {
				if (name != null && name.length() >= 4 && 
						numDoc != null && numDoc.length() >=3 ){
					try {
						passwd = DefaultMysqlDB.encode(name.toUpperCase().substring(0, 4) + 
								numDoc.substring(numDoc.length()-3, numDoc.length()));
					} catch (NoSuchAlgorithmException e) {
						// TODO Auto-generated catch block
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
					}
				}
			}
			/*
			int userId = 
					mysqlDB.insertUser(name, 
						numDoc, 
						enterprise, 
						person, 
						true, 
						passwd,
						null,
						(short) 0,
						null,
						null,
						null,
						null);
			
			Integer domainId = 
					mysqlDB.getDomainForEnterprisePk(enterprise);
			
			Integer applicationId = 
					mysqlDB.getApplicationId("aon-aio");
			Integer domainApplicationId = 
					mysqlDB.getDomainApplicationId(domainId, applicationId);
			Integer profileId = 
					mysqlDB.getProfileId( null , applicationId, "Invitado");

			int applicationUserId = 
					mysqlDB.insertApplication_user(
							domainId,
							userId, 				
							domainApplicationId, 	 
							true 					// active
							);
			mysqlDB.insertApplication_user_profile(
					domainId,
					applicationUserId, 
					profileId); */
		}
		contractPorCots.clear();

		
		emprper.visitTrabajo_emprper(this);
		
		perceps.clear();
		emprper.visitRel_pcp_epp(this);
		
		emprper.visitRel_dto_per(this);

		emprper.visitEmbargo_emprper(this);
		
		emprper.visitBonifica_emprper(this);
		
		Date savedDate = fromDate;
		java.sql.Date embargoStartDate = 
			getEmbargosStartDate();
		
		if ( fromDate != null 
			&& embargoStartDate != null 
			&& embargoStartDate.before(fromDate)){
			fromDate = embargoStartDate;
			MysqlDB.info("emprper[{}]: [{}:{}] Import salalrys from {} due embargos", 
					emprper.getCdg(), emprper.getCodper(), this.contractId,  fromDate);
		}// 
		
		emprper.visitRel_nom_per(mySalary);
		emprper.visitRel_pex_per(mySalary);
		emprper.visitRel_fin_epp(mySalary);
		fromDate = savedDate;
		
		emprper.visitTrabinci_emprper(this);
		
		insertEmbargos();
		
		myIrpfData.visitEmprper(emprper);
		
		new MyLeave(mysqlDB, this, mySalary).visitEmprper(emprper);
		
		insertContract_datas();
		contractVariables.clear();
	}
	
	
	@Override
	public void visitTrabajo_emprper(Trabajo trabajo, Emprper emprper )
			throws SQLException {
		
		if ( outOfDate( trabajo.getFecfin()))
			return ;

		Integer person = persons.getPerson(emprper.getCodper());

		if ( person == null ){
			MysqlDB.error("emprper[{}] : Not found person {}", 
					emprper.getCdg(), emprper.getCodper());
			return ;
		}
		
		String tc2 = trabajo.getCodtc2();
		if ( tc2 == null ){
			MysqlDB.error("emprper[{}] : Not found TC2 {}", 
					emprper.getCdg(), tc2);
			return ;
		}
		
		
		/*
		Colectivos colectivos = null ; //trabajo.getRel_tra_col();
		if ( colectivos != null ) {
			conditions = colectivos.getDescripcion();
		}*/

		
		
		java.sql.Date endDate = 
			getEndDate(trabajo.getFecfin(), emprper);
		
		java.sql.Date startDate =
			trabajo.getFecini();
		
		
		if ( startDate.compareTo(emprper.getFecalt()) != 0) {
			Calendar startCalendar = Calendar.getInstance();
			startCalendar.setTime(startDate);
			if (  startCalendar.get(Calendar.DAY_OF_MONTH) != 1 ){
				startCalendar.set(Calendar.DAY_OF_MONTH, 1);
				MysqlDB.error("trabajo[{}/{}] : Overlapped trabajo {}", 
						emprper.getCdg(), trabajo.getCdg(), startDate );
				startDate = new java.sql.Date ( startCalendar.getTimeInMillis() );
				
			}
		} // 
		
		/*mysqlDB.insertContract_data(CATEGORY.getName(), 
				this.contractId, 
				String.format("\"%s\"", trabajo.getCodcat()), 
				startDate, 
				endDate); */
		/*mysqlDB.*/insertContract_data(TC2.getName(), 
				this.contractId, 
				String.format("\"%s\"", tc2), 
				startDate, 
				endDate);
		/*mysqlDB.*/insertContract_data(QUOTE_GROUP.getName(), 
				this.contractId, 
				String.format("\"%s\"", trabajo.getCodbas()), 
				startDate, 
				endDate);
		
		String cno = trabajo.getCno();
		if ( cno != null ) {
			/*mysqlDB.*/insertContract_data(CNO.getName(), 
					this.contractId, 
					String.format("\"%s\"", trabajo.getCno()), 
					startDate, 
					endDate);
		}
		
		Integer diasTp = trabajo.getDiastp();

		String tipoTp = trabajo.getTipotp();
		if ( tipoTp != null && tipoTp.equals("I")) {
			/*mysqlDB.*/insertContract_data(IRREGULAR.getName(), 
					this.contractId, 
					String.format("%s", Boolean.TRUE), 
					startDate, 
					endDate);
			if ( diasTp > 0 ) {
				/*mysqlDB.*/insertContract_data(CONTRACT_DAYS.getName(), 
						this.contractId, 
						String.format("%d", diasTp), 
						startDate, 
						endDate);
			}
		}
		else {
			if ( diasTp > 0 ) {
				/*mysqlDB.*/insertContract_data(WEEK_DAYS.getName(), 
						this.contractId, 
						String.format("%d", diasTp), 
						trabajo.getFecini(), 
						endDate);
			}
		}
		
		
		String ocupacion2009 = trabajo.getOcupacion2009();
		if ( ocupacion2009 != null ) {
			try {
				OccupationType occupationType = 
					OccupationType.valueOf(OccupationType.class, ocupacion2009);
				/*mysqlDB.*/insertContract_data(OCCUPATION.getName(), 
						this.contractId, 
						String.format("\"%s\"", occupationType.name()), 
						startDate, 
						endDate);
			}
			catch (IllegalArgumentException e ) {
				MysqlDB.error("emprper[{}] : Unknown occupation {} ", 
						emprper.getCdg(), ocupacion2009);
			}
		}
		
		BigDecimal irpf = trabajo.getIrpf();

		/*
		String irpfFunction = getIRPFExpression(irpf);
		
		mysqlDB.insertContract_deduction(
				enum2short(DeductionType.IRPF),
				this.irpfConceptId,
				this.contractId, 
				null, 
				(short) 1,
				irpfFunction, 
				trabajo.getFecini(), 
				endDate,
				null );
		*/
		
		/*mysqlDB.*/insertContract_data(IRPF_PERCENT.getName(), 
				this.contractId, 
				String.format("%.2f",irpf), 
				startDate, 
				endDate);

		Boolean indefinite = "123".indexOf(tc2.charAt(0)) != -1;
		
		Boolean tc2Fulltime = "14".indexOf(tc2.charAt(0)) != -1; 
		
		String indTp = trabajo.getIndtp();
		Boolean fulltime = indTp == null || indTp.equals("0"); 
		if ( fulltime != tc2Fulltime ) {
			/*mysqlDB.*/insertContract_data(FULL_TIME.getName(), 
					this.contractId, 
					String.format("%b", fulltime), 
					startDate, 
					endDate);
			MysqlDB.error("emprper[{}] : Ambigous TC2 {} , {}", 
					emprper.getCdg(), tc2 , fulltime);
		} // No coindice con el código del TC2 ???? 
		
		if ( !fulltime ) {
			Integer semana = trabajo.getSemana();
			if ( semana != null ) {
				/*mysqlDB.*/insertContract_data(WEEK_HOURS.getName(), 
						this.contractId, 
						String.format("%d", semana / 60), 
						startDate, 
						endDate);
				MysqlDB.error("emprper[{}] :Week hours {}", 
						emprper.getCdg(), semana / 60 );
			} // Si existe sobreescribimos lashoras semanales ....
		} // Contrato a tiempo parcial 

//		UNEMPLOYMENT it's a system deduction, so it's final and cannot be overridden. 
//		TODO: Instead we can override 'PORCENTAJE_DESMPL', 		
//		
//		Integer desmpConceptId = 
//				mysqlDB.getDeductionConceptId(mysqlDB.getDefaultDomain(), UNEMPLOY_EMPLOYEE);
//
//		Percents newPercents = 
//			getPercents(indefinite, fulltime);
//		Percents oldPercents = 
//			getPercents(trabajo.getCodpct(), trabajo.getFecini(), endDate);
//		if ( oldPercents != null && oldPercents.employee != newPercents.employee) {
//			mysqlDB.insertContract_deduction(
//					enum2short(DeductionType.UNEMPLOYMENT), 
//					desmpConceptId, 
//					this.contractId, 
//					null, 
//					(short)1, 
//					String.format ( "BASE_CGP * %.2f/100", oldPercents.employee ), 
//					startDate, 
//					endDate, 
//					null);
//			MysqlDB.error("emprper[{}] :Bad quote percentage TC2:{}, {}%", 
//					emprper.getCdg(), tc2, oldPercents.employee  );
//		}
		

		ContractPorCot contractData = 
			new ContractPorCot(startDate, 
					endDate, 
					trabajo.getProcot());
		addContractData(contractData);
	}
	
	@Override
	public void visitEmbargo_emprper(Embargo embargo, Emprper emprper)
			throws SQLException {
		
		FullEmbargo fullEmbargo = 
			new FullEmbargo();

		String concepto = embargo.getConcepto();
		double amount = toDouble(embargo.getImporte());
		
		fullEmbargo.contractEmbargo.amount = amount; 
		fullEmbargo.contractEmbargo.description = concepto;
		fullEmbargo.contractEmbargo.contract = this.contractId;
		fullEmbargo.contractEmbargo.end_date = null;
		fullEmbargo.contractEmbargo.start_date = embargo.getFecha();
		fullEmbargo.contractEmbargo.expression = getEmbargoExpression(embargo);
		
		embargos.put(concepto, fullEmbargo);
	}
	
	@Override
	public void visitBonifica_emprper(Bonifica bonifica, Emprper emprper)
			throws SQLException {
		
		java.sql.Date endDate = 
			getEndDate(bonifica.getFecfin(), emprper);
		
		Bonus bonus = concepts.getBonusConcept(bonifica.getCdg());
		
		String expression = null;
		if ( bonus.expression == null ) {
			expression = MyConcept.getExpr(bonifica);
		}
		
		mysqlDB.insertContract_bonus(
				this.contractId, 
				null, 				// Description , heredada de Bonus concept.
				expression, 
				bonifica.getFecini(), 
				endDate, 
				bonus.id);
	}
	
	@Override
	public void visitRel_dto_per(Trabdto trabdto, Emprper emprper) throws SQLException {

		if ( outOfDate( trabdto.getFecfin()))
			return ;
		
		String concepto = trabdto.getConcepto();
		
		DeductionType type = getDeductionType(concepto);

		BigDecimal importe = trabdto.getImporte();
		String function = String.format("%.3f", 
				importe != null ? importe : 0);
		
		String expression = 
			getAfectaExpression(trabdto.getAfecta(), function);
		
		java.sql.Date endDate = 
			getEndDate(trabdto.getFecfin(), emprper);

		mysqlDB.insertContract_deduction(enum2short(type), 
				null,
				this.contractId, 
				concepto, 
				(short) 0,
				expression, 
				trabdto.getFecini(), 
				endDate,
				(short) 0 );
	}

	
	
	private Boolean isUnitsPercep(Percep percep) 
	throws SQLException {

		String calculo =  percep.getCalculo();
		if (!"0".equals(calculo))
			return false;
		
		BigDecimal importe = percep.getImporte();
		if ( importe == null )
			return false;
		BigDecimal impuni = percep.getImpuni();
		if ( impuni == null )
			return false;
		BigDecimal unidades = percep.getUnidades();
		if ( unidades == null )
			return false;
		
		return importe.doubleValue() == impuni.doubleValue() * unidades.doubleValue();
		
		
	}
	
	

	private String getInlineExpression(Percep percep) 
	throws SQLException {

		String calculo =  percep.getCalculo();
		BigDecimal importe = percep.getImporte();
		String indCom = percep.getIndcom();
		String comApl = percep.getComapl();
		String redExt = percep.getRedext();
		double garilt = toDouble(percep.getGarilt());
		
		return MyConcept.getExpr(calculo, importe, indCom, comApl, redExt, garilt);
	}
	
	private String getVariableExpression(Percep percep, String variable) 
	throws SQLException {

		String calculo =  percep.getCalculo();
		String indCom = percep.getIndcom();
		String comApl = percep.getComapl();
		String redExt = percep.getRedext();
		double garilt = toDouble(percep.getGarilt());
		
		return MyConcept.getExpr(calculo, variable, indCom, comApl, redExt, garilt);
	}
	
	

	@Override
	public void visitRel_pcp_epp(Percep percep, Emprper emprper)
		throws SQLException {
		
		java.sql.Date fecIni = percep.getFecini();
		java.sql.Date fecFin = percep.getFecfin();
		String codCom = percep.getCodcom();

		Concept<PaymentType> concept = 
			concepts.getPaymentConcept(codCom);
		
		if ( concept == null ){
			MysqlDB.error("precep[{}] : Not found concept {} ", percep.getCdg(), percep.getCodcom());
			return;
		}
		
		try {

			Period period = new Period(fecIni, fecFin);
			SortedSet<Period> periods = perceps.get(codCom);
			
			if ( !outOfDate( percep.getFecfin())){
				
				Period intersect = intersects( periods, period );

				if ( intersect != null ) {
					MysqlDB.error("contract[{}] : Percep  {} overlapped  {}..{} {}..{}", 
						contractId, 
						codCom,
						fecIni, fecFin, 
						intersect.getStart(), intersect.getEnd() );
					
					visitAnonymousRel_pcp_epp(percep, emprper);
				} 
				else {
					visitRel_pcp_epp(percep, emprper, concept);
				}
			}
			
			if ( periods == null ) {
				
				periods = new TreeSet<Period>();
				perceps.put(codCom, periods  );
			}
			
			periods.add(period);
			
			
			
		} catch ( IllegalArgumentException  e ){
			MysqlDB.error("percep[{}] : Bad dates {}-{} ", percep.getCdg(), fecIni, fecFin);
		} catch ( NoSuchPerceptionException e ) {
			Percep emptyPercep = e.getPercep();
			visitRel_pcp_epp( emptyPercep , emprper );
	  		MysqlDB.info("percep[{}-{}]: Empty percep  {} ({}..{})", 
	  				emptyPercep.getNumero(), 
	  				emptyPercep.getCdg(), 
	  				emptyPercep.getCodcom(), 
	  				emptyPercep.getFecini(),
	  				emptyPercep.getFecfin());
			visitRel_pcp_epp( percep, emprper );
		}
		
		
	}
	

	public void visitAnonymousRel_pcp_epp(Percep percep, Emprper emprper)
	 throws SQLException{
		Short month = MyConcept.getMonth(percep.getMes()) ;
		
		if ( MyContract.isExtra(percep) ) 
		{
			MysqlDB.error("percep[{}/{}]: Anonymous perception {} is pay extra {} ", 
					percep.getNumero(), 
					percep.getCdg(),
					percep.getCodcom(),
					percep.getCalculo());
			return;
		}
		java.sql.Date endDate = 
			getEndDate(percep.getFecfin(), emprper);
		
		String tipcot = percep.getTipcot();
		String dinesp = percep.getDinesp();

		String description = percep.getDescom();

		String irpf = MyConcept.getIrpfExprFormat(tipcot, dinesp);
		String quote = MyConcept.getQuoteExprFormat(tipcot);
		SalaryType salaryType = getSalaryType(percep, emprper);

		
		PaymentType type =
			mysqlDB.getPaymentType(percep, PaymentType.CRA_0000);
//			mysqlDB.getPaymentType(percep.getDescom(), 
//					percep.getDinesp(),
//					percep.getTipcot())
			;
		
		java.sql.Date startDate = 
			getStartDate(percep);//percep.getFecini();
		
		String script = null;
		
		double garilt = toDouble(percep.getGarilt());
		if ( garilt > 0.00 ) {
			MysqlDB.error("percep[{}/{}]: Anonymous perception {} guarentee {} ", 
					percep.getNumero(), 
					percep.getCdg(),
					percep.getCodcom(),
					percep.getGarilt());
			
		}
		
		script = getInlineExpression(percep);
		
		String quoteExpr = DefaultMysqlDB.format ( quote, script );

		if ( quoteExpr != null && quoteExpr.length() > 128 ) {
			MysqlDB.info("percep{}{}: Quote expression too long {}", percep.getNumero(), percep.getCdg(),quoteExpr );
			quoteExpr = quoteExpr.replaceAll(" ", "");
			quoteExpr = quoteExpr.replaceAll(IPREM_BASE.getName(), IPREM_BASE_SHORT.getName());
			quoteExpr = quoteExpr.replaceAll(FREE_IPREM.getName(), FREE_IPREM_SHORT.getName());
			MysqlDB.info("percep{}{}: Quote expression shorted {}", percep.getNumero(), percep.getCdg(), quoteExpr );
		}
		
		mysqlDB.insertContract_payment(
				enum2short(type), 
				this.contractId, 
				null,
				description, 
				(short) 0,
				script, 
				DefaultMysqlDB.format ( irpf, script ),
				quoteExpr,
				startDate, 
				month,
				endDate,
				enum2short(salaryType));
		
	}
	
	public void visitRel_pcp_epp(Percep percep, Emprper emprper, Concept<PaymentType> concept)
			throws SQLException {
		

		
		Short month = MyConcept.getMonth(percep.getMes()) ;

		java.sql.Date endDate = 
			getEndDate(percep.getFecfin(), emprper);
		
		String tipcot = percep.getTipcot();
		String dinesp = percep.getDinesp();

		String irpf = null;
		if ( ! tipcot.equals(concept.quote )) {
			irpf = MyConcept.getIrpfExprFormat(tipcot, dinesp);
		}
		
		String description = percep.getDescom();
		if ( description.equals(concept.description) ){
			description = null;
		}
		
		java.sql.Date startDate = 
			getStartDate(percep);//percep.getFecini();

		if ( MyContract.isExtra(percep) ) 
		{
			
			SalaryType salaryType = SalaryType.EXTRA;
			
			String variable = MyAgreement.getAmountVariable ( percep.getCodcom() );

			String currentVar = MyConcept.getCurrent(variable);
			String script = getVariableExpression(percep, currentVar );
			//String script = getInlineExpression(percep);
			//String importe = String.format("%.3f", percep.getImporte());

			if ( /* !"4Z".equals(percep.getCodcom()) && */ // La regularización se utiliza para los atrasos de las cotizaciones ??? 
					!agreements.containsExtra(this.codCon, percep.getCodcom()) ) {
				MysqlDB.error("percep[{}/{}]: {} Says, that it's a extra but  agreement {} says no.", 
						percep.getNumero(), 
						percep.getCdg(),
						percep.getCodcom(),
						this.codCon);
				Short delayMonth = getPossibleDelayMonth(percep, emprper);
				if ( delayMonth  != null ) {
					salaryType = SalaryType.SALARY;
					month = delayMonth;
					MysqlDB.error("percep[{}/{}]: {} Appears at delay ¿?¿ in month {}.", 
							percep.getNumero(), 
							percep.getCdg(),
							percep.getCodcom(),
							month);
				}
				else {
					MysqlDB.error("percep[{}/{}]: {} It's really a quote fix.", 
							percep.getNumero(), 
							percep.getCdg(),
							percep.getCodcom(),
							this.codCon);
					// Regularizacion de cotizaciones ???
					month = null;
				}
			}
			


			Boolean inheritFromAgreement = 
				inheritFromAgreement(percep, startDate, endDate);
			if ( !inheritFromAgreement ) {
					overrideContract_variable(
					/*mysqlDB.insertContract_data(*/
							variable, 
							this.contractId, 
							String.format("%.3f", percep.getImporte()), 
							startDate, //percep.getFecini(), 
							endDate);
				
			} 
			
			

			List<ContractPorCot> datas = 
				getContractData(startDate, endDate);
			
			PaymentType type = PaymentType.CRA_0004;
			if ( concept.type == type ) {
				type = null;
			}
			
			String quote = MyConcept.getQuoteExprFormat(tipcot);
			
			String porCot = null;
			String porQuote  = null;
			for (ContractPorCot data : datas) {
				
				if ( porCot == null || porCot.equals(data.porCot)){
					porCot = data.porCot;
					endDate = data.endDate;
					continue;
				}
				
				if ( !inheritFromAgreement || 
					!percep.getRedext().replace('S','D').equals(porCot) )
				{ 
					porQuote = MyConcept.getPorQuote(porCot, 
							MyConcept.getQuoteExprFormat(tipcot));
					mysqlDB.insertContract_payment(
							enum2short(type),
							this.contractId, 
							concept.id,
							description, 
							(short) 0,
							script, 
							DefaultMysqlDB.format( irpf, concept.code ),
							DefaultMysqlDB.format ( porQuote, variable ),
							startDate, 
							month, 
							endDate,
							enum2short(salaryType));
				} // end-if: Si el prorrateo no es mensual y la percepción es diferente.
				
				porCot = data.porCot;
				endDate = data.endDate;
				startDate = data.startDate;
			}

			if ( !inheritFromAgreement || 
				!percep.getRedext().replace('S','D').equals(porCot) )
			{ 
				porQuote = MyConcept.getPorQuote(porCot, quote);
				mysqlDB.insertContract_payment(
						enum2short(type),
						this.contractId, 
						concept.id,
						description, 
						(short) 0,
						script, 
						DefaultMysqlDB.format ( irpf, concept.code ),
						DefaultMysqlDB.format ( porQuote, variable ),
						startDate, 
						month, 
						endDate,
						enum2short(salaryType));
			} // end-if: Si el prorrateo no es mensual y la percepción es diferente.
		}
		else {
			
			String variable = 
				MyAgreement.getAmountVariable(percep.getCodcom());
			
			if ( inheritFromAgreement(percep, startDate,endDate ) ) {
				return ;
			}
			
			PaymentType type = 
				mysqlDB.getPaymentType(percep, PaymentType.CRA_0000);
//				mysqlDB.getPaymentType(percep.getDescom(), 
//						percep.getDinesp(),
//						percep.getTipcot())
				;
			
			
			if ( type == concept.type ){
				type = null;
			} //end-if: El tipo de precepción es igual al del concepto
			
			String quote = null;
			if ( ! tipcot.equals(concept.quote )) {
				quote = MyConcept.getQuoteExprFormat(tipcot);
			} //end-if: La cotización es igual a la del concepto
			
			SalaryType salaryType = getSalaryType(percep, emprper);
			
			
			
			String grtzdo = null;
			String script = null;
			
			double garilt = toDouble(percep.getGarilt());
			if ( garilt > 0.00 ) {
				grtzdo = 
					MyConcept.getGrtzdoExprFormat(percep.getCalculo(), garilt/100 );
			}
			
			if ( grtzdo != null  ){
					script = getVariableExpression(percep, variable);
					/*mysqlDB.insertContract_data(variable,*/
					overrideContract_variable(variable, 
							this.contractId, 
							String.format("%.3f", percep.getImporte()), 
							startDate, //percep.getFecini(), 
							endDate);
					/*mysqlDB.*/insertContract_data(
							MyAgreement.getGtzdoVariable(percep.getCodcom()), 
							this.contractId, 
							String.format(grtzdo, variable), 
							startDate, //percep.getFecini(), 
							endDate);
			}else if ( isUnitsPercep(percep) ){
				String impuni = String.format("%s_IMPORTE", concept.code);
				String unidades = String.format("%s_UNIDADES", concept.code);
				script = String.format("%s * %s ", unidades, impuni );
				overrideContract_variable(impuni, 
						this.contractId, 
						String.format("%.3f", percep.getImpuni()), 
						startDate, 
						endDate);
				overrideContract_variable(unidades, 
						this.contractId, 
						String.format("%.3f", percep.getUnidades()), 
						startDate, 
						endDate);
				MysqlDB.info("percep{}{}: Units expression {} {} {}", percep.getNumero(), percep.getCdg(), script, unidades, impuni  );
			}
			else {
					script = getInlineExpression(percep);
			}
			
			
			if ( month != null && endDate != null ) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(startDate);

				if ( month != calendar.get(Calendar.MONTH) ) {
					calendar.set(Calendar.MONTH, month);
					calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
					if ( startDate.after(calendar.getTime())) {
						calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 1);
					}
					startDate = new java.sql.Date(calendar.getTimeInMillis());
					
					MysqlDB.error("percep [{}-{}]: Start Bad dates for month {} {}..{} must be {}..{}",
							percep.getCdg(), percep.getNumero(), month, percep.getFecini(), endDate, startDate , endDate );
				}
				
				calendar.setTime(endDate);			
				if ( month != calendar.get(Calendar.MONTH) ) {
					Date _endDate = endDate;
					calendar.set(Calendar.MONTH, month);
					calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
					if ( endDate.before(calendar.getTime())) {
						calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 1);
					}
					endDate = new java.sql.Date(calendar.getTimeInMillis());
					MysqlDB.error("percep [{}-{}]: End Bad dates for month {} {}..{} must be {}..{}",
							percep.getCdg(), percep.getNumero(), month, startDate, _endDate, startDate , endDate );
				}
				

			}
			
			mysqlDB.insertContract_payment(
					enum2short(type), 
					this.contractId, 
					concept.id,
					description, 
					(short) 0,
					script, 
					DefaultMysqlDB.format ( irpf, concept.code ),
					DefaultMysqlDB.format ( quote, concept.code ),
					startDate, 
					month,
					endDate,
					enum2short(salaryType));

		}
	}
	
	
	@Override
	public void visitTrabinci_emprper(Trabinci trabinci, Emprper emprper)
			throws SQLException {

		if ( outOfDate( trabinci.getFecfin()))
			return ;
		
		String name = null ;
		String expression = null;
		String codinc = trabinci.getCodinc();
		
		java.sql.Date endDate = 
			getEndDate(trabinci.getFecfin(), emprper);

		if ( codinc.contains("VACACIONES")) {
			name = HOLIDAYS.getName();
			expression = String.format("%.2f", toDouble(trabinci.getCantidad()));
			
			mysqlDB.insertContract_data(name, 
					this.contractId, 
					expression, 
					trabinci.getFecini(), 
					endDate);

			expression = String.format("%s - %s",
					WORKED_DAYS.getName(),
					HOLIDAYS.getName());
			
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(trabinci.getFecini());
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			
			java.sql.Date monthFirstday =
				new java.sql.Date(calendar.getTimeInMillis());
			
			calendar.setTime(trabinci.getFecfin());
			int lastday = 
				calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
			calendar.set(Calendar.DAY_OF_MONTH, lastday);

			java.sql.Date monthLastday = 
				new java.sql.Date(calendar.getTimeInMillis());
			
			mysqlDB.insertContract_data(WORKED_DAYS.getName(), 
					this.contractId, 
					expression, 
					monthFirstday, 
					monthLastday);
			
			return;
		}else if ( codinc.contains("EFECTIVOS")) {
			name = ACTUAL_DAYS.getName();
			expression = String.format("%.2f", toDouble(trabinci.getCantidad()));
		}else if ( codinc.contains("ESPECIALES")) {
			name = SPECIAL_DAYS.getName();
			expression = String.format("%.2f", toDouble(trabinci.getCantidad()));
		}else {
			name = codinc;
		}

		
		mysqlDB.insertContract_data(name, 
				this.contractId, 
				expression, 
				trabinci.getFecini(), 
				endDate);
	}

	
	private boolean inheritFromAgreement (Percep percep, java.sql.Date startDate, java.sql.Date endDate) 
	throws SQLException {

		if ( this.agreementCategory == null ) {
			return false;
		} // end-if : 

		int compare = 
			agreements.hasPayment(this.codCon, 
					this.nivel,
					percep.getCodcom(), 
					percep);
		
		if (compare == PercepPercnivComparator.NOT_FOUND ) {
			MysqlDB.debug("percep[{},{}]: Perception Not found ON {},{},{}", 
					percep.getCdg(), percep.getNumero(), this.codCon,  this.nivel, percep.getCodcom()); 
			return false;
		} // Esta percepcion no existe en el convenio

		if (compare == PercepPercnivComparator.NOT_EQUALS ) {
			MysqlDB.debug("percep[{},{}]: Perception Not equals ON {},{},{}", 
					percep.getCdg(), percep.getNumero(), this.codCon,  this.nivel, percep.getCodcom()); 
			return false;
		} // Existe pero es diferente
		
		String variable = MyAgreement.getAmountVariable(percep.getCodcom());

		

		if ( ( compare & PercepPercnivComparator.NOT_AMOUNT ) > 0 ) {
			//Sólo tienen diferente el importe
			
			/* mysqlDB.insertContract_data( */
			overrideContract_variable(
						variable, 
						this.contractId, 
						String.format("%.3f", percep.getImporte()), 
						startDate, //percep.getFecini(), 
						endDate);
		}
		else {
			inheritContract_variable(
					variable, 
					this.contractId, 
					String.format("%.3f", percep.getImporte()), 
					startDate, //percep.getFecini(), 
					endDate);
			
		}
		

		if ( !MyContract.isExtra(percep) &&  
				( ( compare & PercepPercnivComparator.NOT_GARILT) > 0 ) ) {

			//Sólo tienen diferente el garantizado
			double garilt = toDouble(percep.getGarilt()); 
			String variableGtzdo = 
				MyAgreement.getGtzdoVariable(percep.getCodcom());
			String grtzdo = 
				MyConcept.getGrtzdoExprFormat(percep.getCalculo(), garilt/100 );
			/*mysqlDB.*/insertContract_data(
						variableGtzdo, 
						this.contractId, 
						DefaultMysqlDB.format(grtzdo, variable ), 
						startDate, //percep.getFecini(), 
						endDate);
			
		}

		if ( compare == PercepPercnivComparator.EQUALS ) {
			MysqlDB.debug("percep[{},{}]: Perception equals ON {},{},{}", 
					percep.getCdg(), percep.getNumero(), this.codCon,  this.nivel, percep.getCodcom()); 
		} // end-if : Son exactamante iguales
		
		return true ;
	}
	
	
	
	private void overrideContract_variable (
			String variable, 
			Integer contract, 
			String expr, 
			java.sql.Date startDate, 
			java.sql.Date endDate) throws SQLException {
		
		ContractVariable contractVariable = 
				pushContract_variable(variable, contract, expr, startDate, endDate);
		insertContract_variable(contractVariable);
	}
	
	private void inheritContract_variable (
			String variable, 
			Integer contract, 
			String expr, 
			java.sql.Date startDate, 
			java.sql.Date endDate) throws SQLException {
		
		ContractVariable inheritVariable = 
				pushContract_variable(variable, contract, expr, startDate, endDate);
		
		
		List<ContractVariable> variableList = 
				contractVariables.get(inheritVariable.name);
		
		for (ContractVariable contractVariable : variableList) {
			
			if ( inheritVariable == contractVariable ) {
				continue;
			}

			if ( contractVariable.inherit ) {
				continue;
			}
			
			if ( !inheritVariable.intersects(contractVariable )) {
				continue;
			}
			
			MysqlDB.warn("contract_data[{}] override  agreement variable  {} = {}, {}..{} {}, {}..{}", 
					contractVariable.contract, 
					contractVariable.name, 
					contractVariable.expression, 
					contractVariable.startDate,
					contractVariable.endDate,
					inheritVariable.expression,
					inheritVariable.startDate,
					inheritVariable.endDate);

			insertContract_variable(inheritVariable);
		}
		
	}
	
	private void insertContract_variable (ContractVariable overrideVariable) throws SQLException {
		
		mysqlDB.insertContract_data(overrideVariable.name, 
				overrideVariable.contract, 
				overrideVariable.expression, 
				overrideVariable.startDate,  
				overrideVariable.endDate);
		
		overrideVariable.inherit = false;
		
		List<ContractVariable> variableList = 
				contractVariables.get(overrideVariable.name);
		
		for (ContractVariable contractVariable : variableList) {
			
			if ( !contractVariable.inherit ) {
				continue;
			}
			
			if ( !overrideVariable.intersects(contractVariable )) {
				continue;
			}
			
			MysqlDB.warn("contract_data[{}] override agreement variable  {} = {}, {}..{} {}, {}..{}", 
					contractVariable.contract, 
					contractVariable.name, 
					contractVariable.expression, 
					contractVariable.startDate,
					contractVariable.endDate,
					overrideVariable.expression,
					overrideVariable.startDate,
					overrideVariable.endDate);

			insertContract_variable(contractVariable);
		}
		
	}
	
	private ContractVariable pushContract_variable (
			String variable, 
			Integer contract, 
			String expr, 
			java.sql.Date startDate, 
			java.sql.Date endDate) throws SQLException {
		
		ContractVariable contractVariable = 
				new ContractVariable();
		contractVariable.name = variable;
		contractVariable.expression = expr;
		contractVariable.contract = contract;
		contractVariable.startDate = startDate;
		contractVariable.endDate = endDate;
		contractVariable.inherit = true;
		
		pushContract_variable (contractVariable);
		return contractVariable;
	}
	
	private void pushContract_variable (ContractVariable contractVariable) 
			throws SQLException {
		List<ContractVariable> variableList = 
				contractVariables.get(contractVariable.name);
		
		if ( variableList  == null )  {
			variableList = new LinkedList<ContractVariable>();
			contractVariables.put(contractVariable.name, variableList);
		}
		variableList.add( contractVariable );
	}
	
	private void insertContract_data (
			String name, 
			Integer contract, 
			String expression, 
			java.sql.Date startDate, 
			java.sql.Date endDate) throws SQLException {
		
		if ( expression == null || 
				expression.trim().isEmpty() ) 
			return ;
		
		ContractData contractData = 
			new ContractData();
		contractData.name = name;
		contractData.contract = contract;
		contractData.startDate = startDate;
		contractData.endDate = endDate;
		contractData.expression = expression;

		List<ContractData> dataList = 
			contractDatas.get(name);
		
		if ( dataList == null ) {
			dataList = new ArrayList<ContractData>();
			contractDatas.put(name, dataList);
		}
		
		dataList.add(contractData);
		
	}
	
	private void insertContract_datas() throws SQLException {
		
		for (List<ContractData> dataList : contractDatas.values()) {
			Collections.sort(dataList);
			ContractData contractData = dataList.get(0);
			for (int i = 1; i < dataList.size(); i++) {
				ContractData nextContractData = dataList.get(i);
				if ( !join(contractData, nextContractData)) {
					insertContract_data(contractData);
					contractData = nextContractData;
				}
			}
			insertContract_data(contractData);
		}
		contractDatas.clear();
	}
	
	private void insertContract_data(ContractData contractData)
	throws SQLException{
		mysqlDB.insertContract_data(
				contractData.name, 
				contractData.contract, 
				contractData.expression, 
				contractData.startDate, 
				contractData.endDate);
	}
	
	private boolean join(ContractData prev , ContractData next ){
		if ( prev.endDate == null ) {
			MysqlDB.warn("contract_data[{}] overlapped variable  {} = {}, {}..{} {}..{}", 
					prev.contract, 
					prev.name, 
					prev.expression, 
					prev.startDate,
					prev.endDate,
					next.startDate,
					next.endDate);
			return false;
		}
		
		if ( prev.expression == null && next.expression != null)
			return false;
		
		if ( prev.expression != null && next.expression == null)
			return false;

		if ( prev.expression != next.expression && 
				!prev.expression.equals(next.expression))
			return false;
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(prev.endDate);
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		Date prevEndDate = calendar.getTime();
		
		if ( ! prevEndDate.equals(next.startDate) ) {
			MysqlDB.warn("contract_data[{}] disjoint variable  {} = {}, {}..{} {}..{}", 
					prev.contract, 
					prev.name, 
					prev.expression, 
					prev.startDate,
					prev.endDate,
					next.startDate,
					next.endDate);
			return false;
		}

		MysqlDB.info("contract_data[{}] join variable {} = {}, {}..{} {}..{}", 
				prev.contract, 
				prev.name, 
				prev.expression, 
				prev.startDate,
				prev.endDate,
				next.startDate,
				next.endDate);
		
		prev.endDate = next.endDate;
		
		return true;
	}
	
	
	private  java.sql.Date getStartDate(Percep percep)
	throws SQLException {

		String codCom = percep.getCodcom(); 
		java.sql.Date fecIni = percep.getFecini();
		java.sql.Date fecRet = percep.getFecret();
		
		if ( fecRet == null || fecRet.equals(fecIni) ) {
			return fecIni;
		}
		if ( fecIni.before(fecRet) ) {
			MysqlDB.error("percep[{}-{}]: Date of retroactivity, mus be before than start. {} ({}<-{})", 
					percep.getNumero(), 
					percep.getCdg(), 
					codCom, 
					fecRet, 
					fecIni);
			return fecIni;
		}
		
		
		if ( !perceps.containsKey(codCom) ) {
			MysqlDB.error("percep[{}-{}]: Bad retroactivity, no prev perception {} ({}<-{})", 
					percep.getNumero(), 
					percep.getCdg(), 
					codCom, 
					fecRet, 
					fecIni);
			java.sql.Date fecFin =  
				new java.sql.Date(fecIni.getTime() - 1000*60*60*24);
			throw new NoSuchPerceptionException(createEmptyRetroPercep(percep, fecRet, fecFin));
		}
		
		
		SortedSet<Period> periods = perceps.get(codCom);
		
		//Period period = contains(periods, fecRet);
		
		Period period = periods.last();
		
  		if ( Period.compare(fecRet, period.getEnd()) > 0 ) {
  			MysqlDB.error("percep[{}-{}]: Bad date of retroactivity for {}, {} must be before than {}", 
					percep.getNumero(), 
					percep.getCdg(), 
					codCom, 
					fecRet, 
					period.getEnd());
			java.sql.Date fecFin =  
				new java.sql.Date(fecIni.getTime() - 1000*60*60*24);
			throw new NoSuchPerceptionException(createEmptyRetroPercep(percep, fecRet, fecFin));
  		}

  		Date prevEnd = period.getEnd();
  		if ( prevEnd != null  ) {
  			Calendar calendarStart = Calendar.getInstance();
  			calendarStart.setTime(fecIni);
  			calendarStart.add(Calendar.DAY_OF_MONTH, -1);
  			Date start = calendarStart.getTime();
  			if ( !prevEnd.equals(start)) {
				MysqlDB.error("percep[{}-{}]: Retroactivity, disjoint ranges for {} {}..{} {}..{}", 
						percep.getNumero(), 
						percep.getCdg(), 
						codCom, 
						period.getStart(),
						period.getEnd(),
						fecIni,
						percep.getFecfin());
  			}
  		}

  		MysqlDB.info("percep[{}-{}]: Retroactivity, for {} ({}<-{}, {}..{})", 
				percep.getNumero(), 
				percep.getCdg(), 
				codCom, 
				fecRet, 
				fecIni,
				period.getStart(),
				period.getEnd());
		

		return fecRet;
	}
	
	
	
	public static class ContractData implements Comparable<ContractData> {
		protected String name  ; 
		
		protected Integer contract  ; 
		
		protected String expression  ; 
		
		protected java.sql.Date startDate  ; 
		
		protected java.sql.Date endDate  ; 
		
		@Override
		public int compareTo(ContractData o) {
			return startDate.compareTo(o.startDate);
		}
		
	}

	public static class ContractVariable extends ContractData {
		protected boolean inherit;
		
		protected Period getPeriod() {
			return new Period(startDate, endDate);
		}

		protected boolean intersects(ContractVariable other) {
			return getPeriod().intersects(other.getPeriod());
		}
		
	}

	private Period intersects(SortedSet<Period> periods, Period period) {
		if ( periods == null )
			return null;
		
		for (Period p : periods) {
			if ( p.intersects(period) ){ 
				return p;
			}
		}
		
		return null;
	}

	
	
	private static class PossibleDelayMonthGetter 
		extends DefaultCtsqlDBVisitor{
		
		Short month ;
		Percep percep;
		
		private class Found extends RuntimeException {}
		
		public PossibleDelayMonthGetter(Percep percep , Emprper emprper) 
		 throws SQLException {
			this.percep = percep;
			try {
				emprper.visitNominadf_emprper(this);
			} catch ( Found e) {}
		}
		
		
		@Override
		public void visitNominadf_emprper(Nominadf nominadf, Emprper emprper)
				throws SQLException {
			
			if ( !before(nominadf.getFecini(), percep.getFecfin()) ){
				return;
			}
			
			if ( !after(nominadf.getFecfin(), percep.getFecini()) ){
				return;
			}
			
			double totalPayment = toDouble(nominadf.getTotal_devengos());
			double importe = toDouble(percep.getImporte());
			if ( importe == totalPayment ) {
				month = MyConcept.getMonth(nominadf.getMes());
				throw new Found();
			}
		}
		
		
	}
	
	private Short getPossibleDelayMonth(Percep percep, Emprper emprper) 
		throws SQLException {
		PossibleDelayMonthGetter delayMonthGetter = 
			new PossibleDelayMonthGetter(percep,emprper);
		return delayMonthGetter.month;
	}

	
	private Percep createEmptyRetroPercep (final Percep percep, final java.sql.Date startDate, final java.sql.Date endDate ) throws SQLException {
		return this.ctsqlDB.new Percep(null){

			public Integer getCdg() throws SQLException {
				return percep.getCdg();
			}

			public Integer getNumero() throws SQLException {
				return percep.getNumero();
			}

			public java.sql.Date getFecini() throws SQLException {
				return startDate;
			}

			public java.sql.Date getFecfin() throws SQLException {
				return endDate;
			}

			public java.sql.Date getFecret() throws SQLException {
				return null;
			}

			public String getCodcom() throws SQLException {
				return percep.getCodcom();
			}

			public String getDescom() throws SQLException {
				return percep.getDescom();
			}

			public String getDesabr() throws SQLException {
				return percep.getDesabr();
			}

			public String getCalculo() throws SQLException {
				return percep.getCalculo();
			}

			public String getTipcot() throws SQLException {
				return percep.getTipcot();
			}

			public BigDecimal getUnidades() throws SQLException {
				return BigDecimal.ZERO;
			}

			public BigDecimal getImpuni() throws SQLException {
				return BigDecimal.ZERO;
			}

			public BigDecimal getImporte() throws SQLException {
				return BigDecimal.ZERO;
			}

			public Integer getMes() throws SQLException {
				return percep.getMes();
			}

			public BigDecimal getGarilt() throws SQLException {
				return percep.getGarilt();
			}

			public String getComapl() throws SQLException {
				return percep.getComapl();
			}

			public String getRedext() throws SQLException {
				return percep.getRedext();
			}

			public String getFijovar() throws SQLException {
				return percep.getFijovar();
			}

			public java.sql.Date getFecnew() throws SQLException {
				return percep.getFecnew();
			}

			public Time getHornew() throws SQLException {
				return percep.getHornew();
			}

			public java.sql.Date getFecmod() throws SQLException {
				return percep.getFecmod();
			}

			public Time getHormod() throws SQLException {
				return percep.getHormod();
			}

			public String getIndcom() throws SQLException {
				return percep.getIndcom();
			}

			public String getTipcom() throws SQLException {
				return percep.getTipcom();
			}

			public String getDinesp() throws SQLException {
				return percep.getDinesp();
			}
			
			@Override
			public String getConcepto() throws SQLException {
				return percep.getConcepto();
			}
			
			@Override
			public String getExcinc() throws SQLException {
				return percep.getExcinc();
			}
			
			public void visitRel_pcp_epp(CtsqlDBVisitor ctsqlDBVisitor)
					throws SQLException {
				percep.visitRel_pcp_epp(ctsqlDBVisitor);
			}

			public void visitRel_pcp_com(CtsqlDBVisitor ctsqlDBVisitor)
					throws SQLException {
				percep.visitRel_pcp_com(ctsqlDBVisitor);
			}
		};
	}
	
	private class NoSuchPerceptionException extends RuntimeException {
		
		private Percep percep;
		
		public NoSuchPerceptionException(Percep percep) {
			this.percep = percep;
		}
		
		public Percep getPercep() {
			return percep;
		}
	}

	public static Boolean isExtra(Percep percep) throws SQLException{
		return ( "P".equals(percep.getIndcom()) &&
				"6".equals(percep.getCalculo()) );
		
	}
	
	
}
