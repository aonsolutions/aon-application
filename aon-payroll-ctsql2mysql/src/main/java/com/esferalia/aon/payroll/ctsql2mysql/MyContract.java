package com.esferalia.aon.payroll.ctsql2mysql;


import static com.esferalia.aon.payroll.ctsql2mysql.DefaultMysqlDB.enum2short;
import static com.esferalia.aon.payroll.ctsql2mysql.DefaultMysqlDB.toDouble;
import static com.esferalia.aon.payroll.enumeration.ContractVariables.*;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Bonifica;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Embargo;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprper;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Finiquito;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Linporco;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nomdto;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nomdtoex;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nomina;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nominadev;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nominaex;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Parteit;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Parteitnu;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Percep;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Trabajo;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Trabdto;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Trabinci;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractMysqlDB.Contract_embargo;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractMysqlDB.Salary_embargo;
import com.esferalia.aon.payroll.ctsql2mysql.IConcepts.Bonus;
import com.esferalia.aon.payroll.ctsql2mysql.MyAgreement.PercepPercnivComparator;
import com.esferalia.aon.payroll.ctsql2mysql.IConcepts.Concept;
import com.esferalia.aon.payroll.enumeration.ContractStatus;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.payroll.enumeration.OccupationType;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.payroll.sql.AbstractSQL.ISalary;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;

public class MyContract extends DefaultCtsqlDBVisitor implements IContracts{
	
	private String DELAY_SQL = "SELECT nomina.*"  
		+ " FROM nomina, nominadev" 
		+ " WHERE nomina.cdg = nominadev.cdg" 
		+ " AND nomina.numero=?"
		+ " AND nominadev.codcom=?" 
		+ " AND nomina.tipo='N'"
		+ " AND fecini >= ?"
		+ " AND fecfin <= ?";
	
	final static String PASSWORD  			= "demo";
	

	
	private static class Percents {
		private java.sql.Date startDate;
		private java.sql.Date endDate;
		
		private double total;
		private double employee;
		private double enterprise;
		
	}

	private static class ContractData {
		
		private java.sql.Date	endDate;
		private java.sql.Date 	startDate;
	
		private String 	porCot;
		
		
		public ContractData(java.sql.Date startDate,
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
				MyContract.this.agreementCategoryId = 
					MyContract.this.agreements.getAgreementCategory(trabajo.getCodcon(), trabajo.getNivel(), trabajo.getCodcat());
				if ( MyContract.this.agreementCategoryId == null ) {
					MysqlDB.error("trabajo[{}] : Unknow category  {}/{}/{}", 
							trabajo.getCdg(), trabajo.getCodcon(), trabajo.getNivel(), trabajo.getCodcat() );
					MyContract.this.agreementCategoryId = 
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
	
	private Integer 		contractId;
	private Integer 		agreementCategoryId;
	private String			codCon;
	private String			nivel;
	
	private Date 			fromDate;
	
	private IPersons 		persons;
	private IConcepts 		concepts;
	private IEnterprises 	enterprises;
	private IAgreements 	agreements;
	private ICalendars 		calendars;

	private MySalary		mySalary;
	
	private DefaultMysqlDB 	mysqlDB;
	private AbstractCtsqlDB ctsqlDB;
	
	private Integer 		irpfConceptId;

	private List<ContractData> 			contractDatas;
	private Map<Date, Integer> 			parteIts;
	private Map<String, List<Percents>> quotePercents;
	private Map<String, FullEmbargo> 		embargos;
	
	private Integer registration ;
	private java.sql.Date seniorityDate ;
	
	
	private PreparedStatement	delayStmt = null;

	
	public MyContract(DefaultMysqlDB mysqlDB, IEnterprises enterprises, IPersons persons, IConcepts concepts, IAgreements agreements, ICalendars calendars) {
		this ( mysqlDB, enterprises, persons, concepts, agreements, calendars, null );
	}

	public MyContract(DefaultMysqlDB mysqlDB, IEnterprises enterprises, IPersons persons,  IConcepts concepts, IAgreements agreements, ICalendars calendars, Date fromDate) {
		this.mysqlDB = mysqlDB;
		this.fromDate = fromDate;
		this.persons = persons;
		this.concepts = concepts;
		this.agreements = agreements;
		this.enterprises = enterprises;
		this.calendars = calendars;
		this.contractDatas = new LinkedList<ContractData>();
		this.parteIts = new HashMap<Date, Integer>();
		this.quotePercents = new HashMap<String, List<Percents>>();
		this.embargos = new HashMap<String, FullEmbargo>();
		this.mySalary = new MySalary(mysqlDB, this, concepts);
		
	}
	
	@Override
	public Integer getContractId(Integer oldCdg) {
		return this.contractId;
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
		return DefaultMysqlDB.is9999(fecFin)? emprper.getFecbaj(): fecFin;
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
		
		if ("61".equals(codcom) ) {
			if ( !inSalary(percep)) {
				return SalaryType.DELAY;
			}
			else {
				MysqlDB.error("percep[{}]: Delay {} {} at standard salary for {},{}", 
						percep.getCdg(),
						percep.getDesabr(),
						percep.getImporte(),
						emprper.getCdg(),
						emprper.getCodper());
			}
		}
		
		
		return SalaryType.SALARY;
		
	}

	
	private void addContractData(ContractData contractData) {
		contractDatas.add(contractData);
	}
	
	
	private java.sql.Date earlier ( java.sql.Date oneDate, java.sql.Date anotherDate ) {
		return before(oneDate, anotherDate) ? oneDate: anotherDate;
	}
	
	private java.sql.Date last( java.sql.Date oneDate, java.sql.Date anotherDate ) {
		return after(oneDate, anotherDate) ? oneDate: anotherDate;
	}

	private List<ContractData> getContractData(java.sql.Date startDate, java.sql.Date endDate) {
		List<ContractData> list = 
			new LinkedList<ContractData>();
		
		for (ContractData contractData : contractDatas) {
			if ( after ( contractData.endDate, startDate ) && 
					before(contractData.startDate, endDate ) ) {
				list.add (new ContractData(
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
	
	private String getEmbargoExpression(Embargo embargo ) throws SQLException {
		String afecta = embargo.getAfecta();
		if ( "P".equals(afecta) ) 
			return "PAGA_EXTRA ? (( PENDIENTE > EMBARGABLE ) ? EMBARGABLE : PENDIENTE ) : 0.00 ";
		if ( "A".equals(afecta) ) 
			return "( NOMINA || PAGA_EXTRA ) ? (( PENDIENTE > EMBARGABLE ) ? EMBARGABLE : PENDIENTE ) : 0.00 ";
		if ( "T".equals(afecta) ) 
			return "ATRASOS ? (( PENDIENTE > EMBARGABLE ) ? EMBARGABLE : PENDIENTE ) : 0.00 ";
		return "NOMINA ? (( PENDIENTE > EMBARGABLE ) ? EMBARGABLE : PENDIENTE ) : 0.00 ";
	}
	
	@Override
	public void visit(AbstractCtsqlDB ctsqlDB) throws SQLException {
		this.irpfConceptId = 
			mysqlDB.getDeductionConceptId("IRPF");
		this.ctsqlDB = ctsqlDB;
		
		ctsqlDB.visitLinporco(this);
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
		this.agreementCategoryId = null; 
		emprper.visitTrabajo_emprper(new TrabajoAggregate());
		
		if ( !agreements.inherits(emprper, this.codCon, this.nivel) ) {
			this.agreementCategoryId = null;
		}
		
		Integer wcalendar = enterprises.getCalendar(workplace);
		Integer calendar = calendars.getCalendar(emprper.getCodemp(), emprper.getDomicilio(), emprper.getCodact());
		if ( calendar == wcalendar ) {
			calendar = null;
		}
		else {
			MysqlDB.info("emprper{}: Has diferent calendar {} {} ", emprper.getCdg(), calendar, wcalendar );
		}
		
		if ( this.agreementCategoryId != null ) {
			MysqlDB.info("emprper[{}]: [{}:{}] Inherits payments from {}-{}", 
					emprper.getCdg(), emprper.getCodper(), this.contractId, this.codCon, this.nivel);
			
		}

		this.contractId = 
			mysqlDB.insertContract(person, 
					workplace, 
					ccc, 
					emprper.getFecalt(), 
					emprper.getFecbaj(),
					calendar,
					null,
					null,
					enum2short(ContractStatus.PROCESSED),
					registration,
					seniorityDate,
					activityId,
					enum2short(ssRegimeType),
					this.agreementCategoryId);
		
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
			mysqlDB.insertUser(persons.getName(emprper.getCodper()), 
					numDoc, 
					enterprise, 
					person, 
					true, 
					PASSWORD);
		}
		contractDatas.clear();

		
		emprper.visitTrabajo_emprper(this);
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
		
		emprper.visitRel_pit_epp(this);
		emprper.visitPitnu_emprper(this);
		
		insertEmbargos();

		
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
		
		mysqlDB.insertContract_data(CATEGORY.getName(), 
				this.contractId, 
				String.format("\"%s\"", trabajo.getCodcat()), 
				startDate, 
				endDate);
		mysqlDB.insertContract_data(TC2.getName(), 
				this.contractId, 
				String.format("\"%s\"", tc2), 
				startDate, 
				endDate);
		mysqlDB.insertContract_data(QUOTE_GROUP.getName(), 
				this.contractId, 
				String.format("\"%s\"", trabajo.getCodbas()), 
				startDate, 
				endDate);
		
		String cno = trabajo.getCno();
		if ( cno != null ) {
			mysqlDB.insertContract_data(CNO.getName(), 
					this.contractId, 
					String.format("\"%s\"", trabajo.getCno()), 
					startDate, 
					endDate);
		}
		
		Integer diasTp = trabajo.getDiastp();

		String tipoTp = trabajo.getTipotp();
		if ( tipoTp != null && tipoTp.equals("I")) {
			mysqlDB.insertContract_data(IRREGULAR.getName(), 
					this.contractId, 
					String.format("%s", Boolean.TRUE), 
					startDate, 
					endDate);
			if ( diasTp > 0 ) {
				mysqlDB.insertContract_data(CONTRACT_DAYS.getName(), 
						this.contractId, 
						String.format("%d", diasTp), 
						startDate, 
						endDate);
			}
		}
		else {
			if ( diasTp > 0 ) {
				mysqlDB.insertContract_data(WEEK_DAYS.getName(), 
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
				mysqlDB.insertContract_data(OCCUPATION.getName(), 
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
		
		mysqlDB.insertContract_data(IRPF_PERCENT.getName(), 
				this.contractId, 
				String.format("%.2f",irpf), 
				startDate, 
				endDate);

		Boolean indefinite = "123".indexOf(tc2.charAt(0)) != -1;
		
		Boolean tc2Fulltime = "14".indexOf(tc2.charAt(0)) != -1; 
		
		String indTp = trabajo.getIndtp();
		Boolean fulltime = indTp == null || indTp.equals("0"); 
		if ( fulltime != tc2Fulltime ) {
			mysqlDB.insertContract_data(FULL_TIME.getName(), 
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
				mysqlDB.insertContract_data(WEEK_HOURS.getName(), 
						this.contractId, 
						String.format("%d", semana / 60), 
						startDate, 
						endDate);
				MysqlDB.error("emprper[{}] :Week hours {}", 
						emprper.getCdg(), semana / 60 );
			} // Si existe sobreescribimos lashoras semanales ....
		} // Contrato a tiempo parcial 
		
		Percents newPercents = 
			getPercents(indefinite, fulltime);
		Percents oldPercents = 
			getPercents(trabajo.getCodpct(), trabajo.getFecini(), endDate);
		if ( oldPercents != null && oldPercents.employee != newPercents.employee) {
			mysqlDB.insertContract_deduction(
					enum2short(DeductionType.UNEMPLOYMENT), 
					mysqlDB.getDeductionConceptId("DESMP"), 
					this.contractId, 
					null, 
					(short)1, 
					String.format ( "BASE_CGP * %.2f/100", oldPercents.employee ), 
					startDate, 
					endDate, 
					null);
			MysqlDB.error("emprper[{}] :Bad quote percentage TC2:{}, {}%", 
					emprper.getCdg(), tc2, oldPercents.employee  );
		}
		

		ContractData contractData = 
			new ContractData(startDate, 
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
		
		
		java.sql.Date endDate = 
			getEndDate(trabdto.getFecfin(), emprper);

		mysqlDB.insertContract_deduction(enum2short(type), 
				null,
				this.contractId, 
				concepto, 
				(short) 0,
				function, 
				trabdto.getFecini(), 
				endDate,
				(short) 0 );
	}

	
	
	
	
	private String getInlineExpression(Percep percep) 
	throws SQLException {

		String calculo =  percep.getCalculo();
		BigDecimal importe = percep.getImporte();
		String indCom = percep.getIndcom();
		String comApl = percep.getComapl();
		
		return MyConcept.getExprFormat(calculo, importe, indCom, comApl);
	}
	
	private String getVariableExpression(Percep percep, String variable) 
	throws SQLException {

		String calculo =  percep.getCalculo();
		String indCom = percep.getIndcom();
		String comApl = percep.getComapl();
		
		return MyConcept.getExprFormat(calculo, variable, indCom, comApl);
	}


	@Override
	public void visitRel_pcp_epp(Percep percep, Emprper emprper)
			throws SQLException {
		
		if ( outOfDate( percep.getFecfin()))
			return ;


		Concept<PaymentType> concept = 
			concepts.getPaymentConcept(percep.getCodcom());
		
		if ( concept == null ){
			MysqlDB.error("precep[{}] : Not found concept {} ", percep.getCdg(), percep.getCodcom());
			return ;
		}
			
		
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
		
		if ( "P".equals(percep.getIndcom()) &&
				"6".equals(percep.getCalculo()) )
		{
			
			String script = getInlineExpression(percep);

			java.sql.Date startDate = percep.getFecini();
			List<ContractData> datas = 
				getContractData(startDate, endDate);
			
			PaymentType type = PaymentType.SALARY_SUPPLEMENTS;
			if ( concept.type == type ) {
				type = null;
			}
			
			String quote = MyConcept.getQuoteExprFormat(tipcot);
			
			String porCot = null;
			String porQuote  = null;
			for (ContractData data : datas) {
				
				if ( porCot == null || porCot.equals(data.porCot)){
					porCot = data.porCot;
					endDate = data.endDate;
					continue;
				}
				
				if ( !inheritFromAgreement(percep, endDate) || 
					!percep.getRedext().replace('S','D').equals(porCot) )
				{ 
					porQuote = MyConcept.getPorQuote(porCot, 
							MyConcept.getQuoteExprFormat(tipcot),
							SalaryType.EXTRA);
					mysqlDB.insertContract_payment(
							enum2short(type),
							this.contractId, 
							concept.id,
							description, 
							(short) 0,
							script, 
							DefaultMysqlDB.format( irpf, concept.code ),
							DefaultMysqlDB.format ( porQuote, concept.code ),
							startDate, 
							month, 
							endDate,
							enum2short(SalaryType.EXTRA));
				} // end-if: Si el prorrateo no es mensual y la percepción es diferente.
				
				porCot = data.porCot;
				endDate = data.endDate;
				startDate = data.startDate;
			}

			if ( !inheritFromAgreement(percep, endDate) || 
				!percep.getRedext().replace('S','D').equals(porCot) )
			{ 
				porQuote = MyConcept.getPorQuote(porCot, quote, SalaryType.EXTRA);
				mysqlDB.insertContract_payment(
						enum2short(type),
						this.contractId, 
						concept.id,
						description, 
						(short) 0,
						script, 
						DefaultMysqlDB.format ( irpf, concept.code ),
						DefaultMysqlDB.format ( porQuote, concept.code ),
						startDate, 
						month, 
						endDate,
						enum2short(SalaryType.EXTRA));
			} // end-if: Si el prorrateo no es mensual y la percepción es diferente.
		}
		else {
			
			String variable = 
				MyAgreement.getAmountVariable(percep.getCodcom());
			
			if ( inheritFromAgreement(percep, endDate ) ) {
				return ;
			}
			
			PaymentType type = 
				mysqlDB.getPaymentType(percep.getDescom(), 
						percep.getDinesp(),
						percep.getTipcot());
			
			
			
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
					mysqlDB.insertContract_data(variable, 
							this.contractId, 
							String.format("%.3f", percep.getImporte()), 
							percep.getFecini(), 
							endDate);
					mysqlDB.insertContract_data(
							MyAgreement.getGtzdoVariable(percep.getCodcom()), 
							this.contractId, 
							String.format(grtzdo, variable), 
							percep.getFecini(), 
							endDate);
			}
			else {
					script = getInlineExpression(percep);
			}
			
			java.sql.Date startDate = 
				percep.getFecini();
			
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
			expression = String.format("%d", trabinci.getCantidad());
			
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
			expression = String.format("%d", trabinci.getCantidad());
		}else if ( codinc.contains("ESPECIALES")) {
			name = SPECIAL_DAYS.getName();
			expression = String.format("%d", trabinci.getCantidad());
		}else {
			name = codinc;
		}

		
		mysqlDB.insertContract_data(name, 
				this.contractId, 
				expression, 
				trabinci.getFecini(), 
				endDate);
	}

	
	private LeaveType getLeaveType(String tipoIt, String riesgo ) {
		if ( "E".equalsIgnoreCase(tipoIt) ) {
			return LeaveType.COMMON_DISEASE;
		}
		if ( "A".equalsIgnoreCase(tipoIt) ) {
			return LeaveType.OCCUPATIONAL_DISEASE;
		}
		if ( "M".equalsIgnoreCase(tipoIt) ) {
			return LeaveType.MATERNITY;
		}
		if ( "S".equalsIgnoreCase(riesgo)) {
			return LeaveType.PREGNANCY_RISK;
		}
		return null;
	}
	
	@Override
	public void visitRel_pit_epp(Parteit parteit, Emprper emprper)
			throws SQLException {
		
		java.sql.Date endDate = 
			getEndDate(parteit.getFecfin(), emprper);
		
		Double dailyCgcBase = toDouble(parteit.getBasediacg());
		Double dailyCgpBase = toDouble(parteit.getBasediaacc());
		Double dailyRegBase = toDouble(parteit.getBaseregdia());
		
		Short type = 
			enum2short(getLeaveType(parteit.getTipoit(), parteit.getRiesgo()));
		
		
		
		Integer id = mysqlDB.insertContract_leave(
				type, 
				this.contractId, 
				null, 
				parteit.getFecini(), 
				endDate, 
				dailyCgcBase, 
				dailyCgpBase,
				parteIts.get(parteit.getFeciniori()),
				dailyRegBase,
				null);
		
		if ( parteit.getProret().equals("D") ) {
			mysqlDB.insertContract_data(
					QUOTE_IT.getName(), 
					this.contractId, 
					"\"DIARIA\"", 
					parteit.getFecini(), 
					endDate);
		}
		
		parteIts.put(parteit.getFecini(), id);
	}
	
	@Override
	public void visitPitnu_emprper(Parteitnu parteitnu, Emprper emprper)
			throws SQLException {
		java.sql.Date endDate = 
			getEndDate(parteitnu.getFecfin(), emprper);
		
		Double dailyCgcBase = toDouble(parteitnu.getBasediacg());
		Double dailyCgpBase = toDouble(parteitnu.getBasediaacc());
		Double dailyRegBase = toDouble(parteitnu.getBaseregdia());
		
		Short type = 
			enum2short(getLeaveType(parteitnu.getTipoit(), parteitnu.getRiesgo()));
		
		Integer id = mysqlDB.insertContract_leave(
				type, 
				this.contractId, 
				null, 
				parteitnu.getFecini(), 
				endDate, 
				dailyCgcBase, 
				dailyCgpBase,
				parteIts.get(parteitnu.getFeciniori()),
				dailyRegBase,
				null);

		parteIts.put(parteitnu.getFecini(), id);
	}

	private boolean inheritFromAgreement (Percep percep, java.sql.Date endDate) 
	throws SQLException {

		if ( this.agreementCategoryId == null ) {
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
		
		if ( compare == PercepPercnivComparator.EQUALS ) {
			MysqlDB.debug("percep[{},{}]: Perception equals ON {},{},{}", 
					percep.getCdg(), percep.getNumero(), this.codCon,  this.nivel, percep.getCodcom()); 
			return true ;
		} // end-if : Son exactamante iguales

		
		String variable = MyAgreement.getAmountVariable(percep.getCodcom());
		if ( ( compare & PercepPercnivComparator.NOT_AMOUNT ) > 0 ) {
			//Sólo tienen diferente el importe
			mysqlDB.insertContract_data(
						variable, 
						this.contractId, 
						String.format("%.3f", percep.getImporte()), 
						percep.getFecini(), 
						endDate);
		}
		

		if ( ( compare & PercepPercnivComparator.NOT_GARILT) > 0 ) {

			//Sólo tienen diferente el garantizado
			double garilt = toDouble(percep.getGarilt()); 
			String variableGtzdo = 
				MyAgreement.getGtzdoVariable(percep.getCodcom());
			String grtzdo = 
				MyConcept.getGrtzdoExprFormat(percep.getCalculo(), garilt/100 );
			mysqlDB.insertContract_data(
						variableGtzdo, 
						this.contractId, 
						DefaultMysqlDB.format(grtzdo, variable ), 
						percep.getFecini(), 
						endDate);
			
		}

		return true ;
	}
	

}
