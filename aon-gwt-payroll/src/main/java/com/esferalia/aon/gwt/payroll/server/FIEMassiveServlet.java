package com.esferalia.aon.gwt.payroll.server;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractLeave.CONTRACT_LEAVE;
import static com.esferalia.aon.jooq.tables.ContractLeaveDetail.CONTRACT_LEAVE_DETAIL;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.LeaveBatch.LEAVE_BATCH;
import static com.esferalia.aon.jooq.tables.LeaveBatchDetail.LEAVE_BATCH_DETAIL;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.exception.TooManyRowsException;
import org.jooq.impl.DSL;
import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.jooq.JooqIT;
import com.esferalia.aon.gwt.payroll.shared.EmployeeFieNotFound;
import com.esferalia.aon.gwt.payroll.shared.FIEService;
import com.esferalia.aon.gwt.payroll.shared.ITEmployee;
import com.esferalia.aon.gwt.payroll.shared.ITPart;
import com.esferalia.aon.in.payroll.tgss.fie.FieListener;
import com.esferalia.aon.in.payroll.tgss.fie.FieMassiveParser;
import com.esferalia.aon.jooq.tables.records.ContractLeaveDetailRecord;
import com.esferalia.aon.jooq.tables.records.ContractLeaveRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.LeaveBatchRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.payroll.EmployeeNotFoundexception;
import com.esferalia.aon.occam.api.model.payroll.TooManyEmployeesException;
import com.esferalia.aon.occam.api.model.payroll.TooManyITsException;
import com.esferalia.aon.occam.api.model.type.ContractLeaveType;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;


@MultipartConfig
@SuppressWarnings("serial")
@WebServlet(
		name = "INNS-FIE-M", 
		urlPatterns = { 
				"/aon_gwt_aio/fie_massive/*" ,
				"/aon_gwt_payroll/fie_massive/*" 
		}
)
public class FIEMassiveServlet extends HttpServlet implements FIEService {
	
	private static Logger LOGGER = Logger.getLogger(FIEMassiveServlet.class.getName());
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	private static Collection<EmployeeFieNotFound> noImportEmployees;
	private static Condition condition;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		String userLogin = req.getParameter(Parameter.USER.name());
		String domainName = req.getParameter(Parameter.DOMAIN.name());
		
		noImportEmployees = new ArrayList<>();
				
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName,userLogin)) {
			
			List<Integer> itIds = new ArrayList<Integer>();
			
			ctx.transaction( configuration -> {
				for ( Part part : req.getParts() ) {
					if(AonStringUtils.equalsIgnoreCase(part.getName(), "FILE")) {
						try ( InputStream is = part.getInputStream() ) {					
							itIds.addAll(doFieMassive(is, ctx.getDslContext()));
						} catch ( IOException e ) {
							e.printStackTrace();
							LOGGER.log(Level.WARNING, part.getName()  + ", not a .FIE message.");
						}
					}
				}
				
			});
			
			Collection<ITEmployee> itEmployees = JooqIT.getEmployeesITInfo(ctx, itIds);
			JSONArray importIT = toImportITJSON(itEmployees);
			JSONArray noImportIT = toNoImportITJSON(noImportEmployees);
			JSONObject json = new JSONObject();
			json.put("import", importIT);
			json.put("noImport", noImportIT);
			
			resp.setContentType("application/json");     
			PrintWriter out = resp.getWriter();
			out.print(json);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static Collection<Integer> doFieMassive(InputStream is, DSLContext ctx) throws IOException, SQLException {
			List<Integer> ids = new ArrayList<Integer>();
		
			Fie2AON fie2AON = new Fie2AON() {
				@Override
				public void endDIT() {
					try {	
						IT itp = getIt();
						if(!itp.getCancel())
							ids.add(addIT(ctx, itp));
					} catch ( EmployeeNotFoundexception e) {
						
					}
				}
				
				@Override
				public void endITD() {
				}
				
				@Override
				public void endCIT() {
					try {
						addConfirmationPartIT(ctx, getIt());
					} catch ( Exception e) {
						
					}
				}
				
			};
			FieMassiveParser.parse(is, fie2AON);
			return ids;
	}
	
	public static class IT {
		private String ccc;
		private String naf;
		private String ipf;
		private String name;
		private String surname;
		private String secondSurname;

		private ContractLeaveType contingency;
		private Byte hightCause;
		
		private Date startDate;
		private Date endDate;
				
		private Date fromITDate;
		private Date prevItDate;
		private int accumulatedDays;
		

		private Date directPayStartDate;
		private Double directPayBase;
		
		private Date confirmationDate;
		private String confirmationPartNumber;
		
		private boolean cancel;
		
		public String getCcc() {
			return ccc;
		}

		public void setCcc(String ccc) {
			this.ccc = ccc;
		}

		public String getNaf() {
			return naf;
		}

		public void setNaf(String naf) {
			this.naf = naf;
		}

		public String getIpf() {
			return ipf;
		}

		public void setIpf(String ipf) {
			this.ipf = ipf;
		}
		
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getSurname() {
			return surname;
		}

		public void setSurname(String surname) {
			this.surname = surname;
		}

		public String getSecondSurname() {
			return secondSurname;
		}

		public void setSecondSurname(String secondSurname) {
			this.secondSurname = secondSurname;
		}
		
		public String getFullName() {
			return (null == getSurname() ? "" :  getSurname() + " ") + 
					(null == getSecondSurname() ? "" : getSecondSurname() + ", ") + 
					getName();
		}

		public Date getStartDate() {
			return startDate;
		}

		public void setStartDate(Date startDate) {
			this.startDate = startDate;
		}

		public Optional<Date> getEndDate() {
			return Optional.ofNullable(endDate);
		}

		public void setEndDate(Date endDate) {
			this.endDate = endDate;
		}

		public ContractLeaveType getContingency() {
			return contingency;
		}

		public void setContingency(ContractLeaveType contingency) {
			this.contingency = contingency;
		}

		public Byte getHightCause() {
			return hightCause;
		}

		public void setHightCause(Byte hightCause) {
			this.hightCause = hightCause;
		}

		public Optional<Date> getFromITDate() {
			return Optional.ofNullable(fromITDate);
		}

		public void setFromITDate(Date fromITDate) {
			this.fromITDate = fromITDate;
		}

		public Optional<Date> getPrevItDate() {
			return Optional.ofNullable(prevItDate);
		}

		public void setPrevItDate(Date prevItDate) {
			this.prevItDate = prevItDate;
		}

		public Optional<Integer> getAccumulatedDays() {
			return Optional.ofNullable(accumulatedDays);
		}

		public void setAccumulatedDays(int accumulatedDays) {
			this.accumulatedDays = accumulatedDays;
		}

		public Optional<Date> getDirectPayStartDate() {
			return Optional.ofNullable(directPayStartDate);
		}

		public void setDirectPayStartDate(Date directPayStartDate) {
			this.directPayStartDate = directPayStartDate;
		}

		public Optional<Double> getDirectPayBase() {
			return Optional.ofNullable(directPayBase);
		}

		public void setDirectPayBase(Double directPayBase) {
			this.directPayBase = directPayBase;
		}

		public Date getConfirmationDate() {
			return confirmationDate;
		}

		public void setConfirmationDate(Date confirmationDate) {
			this.confirmationDate = confirmationDate;
		}

		public String getConfirmationPartNumber() {
			return confirmationPartNumber;
		}

		public void setConfirmationPartNumber(String confirmationPartNumber) {
			this.confirmationPartNumber = confirmationPartNumber;
		}
		
		public boolean getCancel() {
			return cancel;
		}

		public void setCancel(boolean cancel) {
			this.cancel = cancel;
		}
	}
	
	private static class Fie2AON implements FieListener {
		
		IT it = new IT();
		
		public IT getIt() {
			return it;
		}
		
		@Override
		public void endEmployee() {
		}
				
		@Override
		public void onCCC(String ccc) {
			it.setCcc(ccc);
		}

		@Override
		public void onNaf(String naf) {
			it.setNaf(naf);
		}

		@Override
		public void onIPF(String ipf) {
			it.setIpf(ipf);
			
		}
		
		@Override
		public void onName(String name) {
			it.setName(name);
		}
		
		@Override
		public void onFirstSurname(String firstSurname) {
			it.setSurname(firstSurname);
		}
		
		@Override
		public void onSecondSurname(String secondSurname) {
			it.setSecondSurname(secondSurname);
		}

		@Override
		public void onDitItStartDate(Date itStartDate) {
			it.setStartDate(itStartDate);
		}

		@Override
		public void onDitRelapse(Boolean relapse) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onDitInitialProcessDate(Date initialProcessDate) {
			it.setFromITDate(initialProcessDate);
		}

		@Override
		public void onDitLastProcessDate(Date lastProcessDate) {
			it.setPrevItDate(lastProcessDate);
		}

		@Override
		public void onDitAcumulatedDays(Integer acumulatedDays) {
			it.setAccumulatedDays(acumulatedDays);
		}

		@Override
		public void onDitNonExistantProcessDate(Date nonExistantProcessDate) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onDitNonExistantProcessCause(String nonExistantProcessCause) {
			// TODO Auto-generated method stub		
		}

		@Override
		public void onDitContingency(Integer contingency) {
			// 1=Enfermedad com?;
			// 2=Accidente nolaboral;
			// 3=Accidente de Trabajo; 
			// 4=Enfermedad Profesional; 
			// 5=Periodo de observaci?.
			it.setContingency(ContractLeaveType.valueOfTGSS(contingency));
		}

		@Override
		public void onDitDeficiencyIndicator(String deficiencyIndicator) {
			// S=se acredita carencia; 
			// N=no se acredita carencia;
			// P=consulta la Direcci? Provincial del INSS
			switch (deficiencyIndicator) {
			case "N":
				it.setContingency(ContractLeaveType.ENFERMEDAD_COMUN_CARENCIA);
				break;
			default:
				break;
			}
		}

		@Override
		public void onDitDelegatePaymendEndDate(Date delegatePaymentEndDate) {
			
		}

		@Override
		public void onDitDelegatePaymendEndCause(String delegatePaymentEndCause) {
			
		}

		@Override
		public void onDitItEndDate(Date itEndDate) {
			it.setEndDate(itEndDate);
		}

		@Override
		public void onDitItEndCause(String itEndCause) {
			if(AonStringUtils.isNotBlank(itEndCause))
				it.setHightCause(Byte.parseByte(itEndCause));
		}

		@Override
		public void onDitItPartCancel(boolean itPartCancel) {
			it.setCancel(itPartCancel);
		}

		@Override
		public void onItdDirectPaymentStartDate(Date directPaymentStartDate) {
			it.setDirectPayStartDate(directPaymentStartDate);
		}

		@Override
		public void onItdRegulatoryBase(Float regulatoryBase) {
			it.setDirectPayBase(regulatoryBase.doubleValue());
		}
		
		@Override
		public void onCitConfirmationStartDate(Date confirmationDate) {
			it.setConfirmationDate(confirmationDate);
		}
		
		@Override
		public void onCitConfirmationNumberPart(String confirmationNumberPart) {
			it.setConfirmationPartNumber(confirmationNumberPart);
		}

	
	}

	public static void addIT(String domainName, Integer domainId, String userLogin, IT it) {
		try (CloseableAONContext aonContext = AONContext.getAONContext(domainName, domainId, userLogin)) {
			addIT(aonContext.getDslContext(), it);
		}
	}
	
	public static Integer addIT(DSLContext ctx , IT it) throws EmployeeNotFoundexception, TooManyEmployeesException {
		java.sql.Date startDateO = new java.sql.Date(it.getStartDate().getTime());
		java.sql.Date itStartDate = normalizeStartDateToSave(it.getContingency(), it.getStartDate());
		java.sql.Date itEndDate = it.getEndDate().map( d -> new java.sql.Date(d.getTime())).orElse(null);
		
		try {
			
			ContractRecord contractRecord = getContract(ctx, it);	
			
			try {
				ContractLeaveRecord contractLeaveRecord = 
				ctx
				.select()
				.from(CONTRACT_LEAVE)
				.where(CONTRACT_LEAVE.CONTRACT.eq(contractRecord.getId()))
				.and(CONTRACT_LEAVE.END_DATE.isNull().or(CONTRACT_LEAVE.END_DATE.ge(itStartDate)))
				.and(DSL.condition(itEndDate == null ).or(CONTRACT_LEAVE.START_DATE.le(itEndDate)))
				.orderBy(CONTRACT_LEAVE.ID.desc())
				.limit(1)
				.fetchOptionalInto(CONTRACT_LEAVE)
				.orElseGet(() -> {
					ContractLeaveRecord r = ctx.newRecord(CONTRACT_LEAVE);
					r.setContract(contractRecord.getId());
					r.setDomain(contractRecord.getDomain());
					return r;
				});
		
				contractLeaveRecord.setStartDate(itStartDate);
				contractLeaveRecord.setType(it.getContingency().value());
				contractLeaveRecord.setEndDate(it.getEndDate().map(d -> itEndDate).orElse(null));
				contractLeaveRecord.setDischargeCause(it.getHightCause());
		
				contractLeaveRecord.store();
				
				// Create Contract Leave Detail (LOW)
				
				Result<Record> lowContractLeaveDetails = ctx.select().from(CONTRACT_LEAVE_DETAIL)
						.where(CONTRACT_LEAVE_DETAIL.CONTRACT_LEAVE.eq(contractLeaveRecord.getId()))
						.and(CONTRACT_LEAVE_DETAIL.TYPE.eq((byte)0))
						.fetch();
				
				ContractLeaveDetailRecord lowContractLeaveDetail = null;
				
				if (lowContractLeaveDetails.isEmpty())
					lowContractLeaveDetail = ctx.insertInto(CONTRACT_LEAVE_DETAIL)
						.set(CONTRACT_LEAVE_DETAIL.DOMAIN, contractRecord.getDomain())
						.set(CONTRACT_LEAVE_DETAIL.TYPE, (byte)0)
						.set(CONTRACT_LEAVE_DETAIL.CONTRACT_LEAVE, contractLeaveRecord.getId())
						.set(CONTRACT_LEAVE_DETAIL.DATE,startDateO)
						.returning(CONTRACT_LEAVE_DETAIL.ID)
						.fetchOne();
				else {
					lowContractLeaveDetail = (ContractLeaveDetailRecord) lowContractLeaveDetails.get(0);
					lowContractLeaveDetail.set(CONTRACT_LEAVE_DETAIL.DATE, startDateO);
					lowContractLeaveDetail.update();
				}
					
				
				// Create Contract Leave Detail (HIGH)
				
				if(null != contractLeaveRecord.getEndDate()) {
					
					Result<Record> highContractLeaveDetails  = ctx.select().from(CONTRACT_LEAVE_DETAIL)
							.where(CONTRACT_LEAVE_DETAIL.CONTRACT_LEAVE.eq(contractLeaveRecord.getId()))
							.and(CONTRACT_LEAVE_DETAIL.TYPE.eq((byte)2))
							.fetch();
					
					if(highContractLeaveDetails.isEmpty())
						ctx.insertInto(CONTRACT_LEAVE_DETAIL)
							.set(CONTRACT_LEAVE_DETAIL.DOMAIN, contractRecord.getDomain())
							.set(CONTRACT_LEAVE_DETAIL.TYPE, (byte)2)
							.set(CONTRACT_LEAVE_DETAIL.CONTRACT_LEAVE, contractLeaveRecord.getId())
							.set(CONTRACT_LEAVE_DETAIL.DATE, contractLeaveRecord.getEndDate())
							.execute();
					
				}
				
				// Create Leave Detail
				
				Result<Record> leaveBatchDetails = ctx.select().from(LEAVE_BATCH_DETAIL)
						.where(LEAVE_BATCH_DETAIL.CONTRACT_LEAVE_DETAIL.eq(lowContractLeaveDetail.getId()))
						.fetch();
				
				if(leaveBatchDetails.isEmpty()) {
					LeaveBatchRecord leaveBatchRecord = ctx.insertInto(LEAVE_BATCH)
							.set(LEAVE_BATCH.DOMAIN, contractRecord.getDomain())
							.set(LEAVE_BATCH.DATE, new Timestamp(new java.util.Date().getTime()))
							.set(LEAVE_BATCH.STATUS, (byte)1)
							.set(LEAVE_BATCH.COMMUNICATION_ID, "COMUNICA")
							.set(LEAVE_BATCH.INCOME_FILE, (byte[]) null)
							.set(LEAVE_BATCH.OUTCOME_FILE, (byte[]) null)
							.returning(LEAVE_BATCH.ID).fetchOne();
						
					ctx.insertInto(LEAVE_BATCH_DETAIL)
						.set(LEAVE_BATCH_DETAIL.DOMAIN, contractRecord.getDomain())
						.set(LEAVE_BATCH_DETAIL.LEAVE_BATCH, leaveBatchRecord.getId())
						.set(LEAVE_BATCH_DETAIL.CONTRACT_LEAVE_DETAIL, lowContractLeaveDetail.getId())
						.execute();
				}
					
				return contractLeaveRecord.getId();
				
			} catch ( TooManyRowsException e) {
				throw new TooManyITsException(e);
			}
		} catch (EmployeeNotFoundexception e) {
			EmployeeFieNotFound employeeFieNotFound = new EmployeeFieNotFound()
					.setCcc(it.getCcc())
					.setIpf(it.getIpf())
					.setNaf(it.getNaf())
					.setFullName(it.getFullName());
			
			noImportEmployees.add(employeeFieNotFound);
			
			System.out.println("Contract not found --> CCC :" + it.getCcc() + ", Naf : " + it.getNaf());
			throw new EmployeeNotFoundexception(e);
		}
		catch ( Exception e) {
			e.printStackTrace();
			throw new TooManyEmployeesException(e);
		}
		
	}
	
	public static void addConfirmationPartIT(DSLContext ctx, /*Integer domainId,*/ IT it) {
		java.sql.Date itStartDate = new java.sql.Date(it.getStartDate().getTime());
		java.sql.Date itEndDate = it.getEndDate().map( d -> new java.sql.Date(d.getTime())).orElse(null);
		java.sql.Date itConfirmationDate = new java.sql.Date(it.getConfirmationDate().getTime());
		
		try {
			condition = PERSON.SOCIAL_SECURITY_NUM.eq(it.getNaf());
			condition = condition.and(CONTRACT.END_DATE.isNull().or(CONTRACT.END_DATE.ge(itStartDate)));
			condition = condition.and(DSL.condition(itEndDate == null ).or(CONTRACT.START_DATE.le(itEndDate)));
			
			if(AonStringUtils.isNotBlank(it.getCcc()))
				condition = condition.and(ENTERPRISE_CCC.CCC.eq(it.getCcc()));
			
			ContractRecord contractRecord = 
			ctx
			.select()
			.from(REGISTRY)
			.innerJoin(PERSON).on(PERSON.REGISTRY.eq(REGISTRY.ID))
			.innerJoin(CONTRACT).on(CONTRACT.PERSON.eq(PERSON.REGISTRY))
			.innerJoin(ENTERPRISE_CCC).on(ENTERPRISE_CCC.ID.eq(CONTRACT.ENTERPRISE_CCC))
			.where(condition)
			.fetchOptionalInto(CONTRACT)
			.orElseGet(() -> 			
					ctx
					.select()
					.from(REGISTRY)
					.innerJoin(PERSON).on(PERSON.REGISTRY.eq(REGISTRY.ID))
					.innerJoin(CONTRACT).on(CONTRACT.PERSON.eq(PERSON.REGISTRY))
					.innerJoin(ENTERPRISE_CCC).on(ENTERPRISE_CCC.ID.eq(CONTRACT.ENTERPRISE_CCC))
					.innerJoin(DOMAIN).on(DOMAIN.ID.eq(ENTERPRISE_CCC.DOMAIN))
					.where(condition)
					.fetchOptionalInto(CONTRACT)
					.orElseThrow(() -> new EmployeeNotFoundexception() ) 
			);
			
			if(null != contractRecord) {
				try {
					ContractLeaveRecord contractLeaveRecord = 
					ctx
					.select()
					.from(CONTRACT_LEAVE)
					.where(CONTRACT_LEAVE.CONTRACT.eq(contractRecord.getId()))
					.and(CONTRACT_LEAVE.START_DATE.eq(itStartDate))
					.fetchOptionalInto(CONTRACT_LEAVE)
					.orElseGet(() -> {
						ContractLeaveRecord r = ctx.newRecord(CONTRACT_LEAVE);
						r.setContract(contractRecord.getId());
						r.setDomain(contractRecord.getDomain());
						return r;
					});
		
					// Create Contract Leave Detail
					
					ctx.insertInto(CONTRACT_LEAVE_DETAIL)
						.set(CONTRACT_LEAVE_DETAIL.DOMAIN, contractRecord.getDomain())
						.set(CONTRACT_LEAVE_DETAIL.TYPE, (byte)1)
						.set(CONTRACT_LEAVE_DETAIL.CONTRACT_LEAVE, contractLeaveRecord.getId())
						.set(CONTRACT_LEAVE_DETAIL.DATE, itConfirmationDate)
						.set(CONTRACT_LEAVE_DETAIL.CONFIRM_ORDER, Byte.parseByte(it.getConfirmationPartNumber()))
						.execute();
					
				} catch ( TooManyRowsException e) {
					throw new TooManyITsException(e);
				}
			}
			
		} catch ( TooManyRowsException e) {
			throw new TooManyEmployeesException(e);
		}
	}
	
	public static ContractRecord getContract(DSLContext ctx, IT it) {
		java.sql.Date itStartDate = normalizeStartDateToSave(it.getContingency(), it.getStartDate());
		
		Condition condition = PERSON.SOCIAL_SECURITY_NUM.eq(it.getNaf());
		condition = condition.and(CONTRACT.START_DATE.le(itStartDate));
		condition = condition.and(CONTRACT.END_DATE.isNull().or(CONTRACT.END_DATE.ge(itStartDate)));
		
		if(AonStringUtils.isNotBlank(it.getCcc()))
			condition = condition.and(ENTERPRISE_CCC.CCC.eq(it.getCcc()));
		
		return ctx.select()
				.from(REGISTRY)
				.innerJoin(PERSON).on(PERSON.REGISTRY.eq(REGISTRY.ID))
				.innerJoin(CONTRACT).on(CONTRACT.PERSON.eq(PERSON.REGISTRY))
				.innerJoin(ENTERPRISE_CCC).on(ENTERPRISE_CCC.ID.eq(CONTRACT.ENTERPRISE_CCC))
				.where(condition)
				.orderBy(CONTRACT.ID.desc())
				.fetchOptionalInto(CONTRACT)
				.orElseThrow(() -> new EmployeeNotFoundexception() );
	}

	private static java.sql.Date normalizeStartDateToSave(ContractLeaveType contingency, Date date) {
		if(contingency!=null && contingency.equals(ContractLeaveType.ACCIDENTE_LABORAL)) 
			date = DateUtils.addDays2Date(date, 1);
		
		return new java.sql.Date(date.getTime());
	}
	
	private JSONArray toImportITJSON(Collection<ITEmployee> itEmployees) {
		JSONArray result = new JSONArray();
		
		for(ITEmployee itEmployee : itEmployees) {
			
			JSONObject employee = new JSONObject();
			JSONArray its = new JSONArray();
			
			employee.put("enterprise", itEmployee.getContractInfo().getEnterpriseName());
			employee.put("name", itEmployee.getEmployeeInfo().getName());
			employee.put("surname", itEmployee.getEmployeeInfo().getSurName());
			employee.put("secondSurname", itEmployee.getEmployeeInfo().getSecondSurName());
			employee.put("ipf", itEmployee.getEmployeeInfo().getDocument());
			employee.put("naf", itEmployee.getEmployeeInfo().getSsNumber());
			
			for(com.esferalia.aon.gwt.payroll.shared.IT it : itEmployee.getIts()) {
				
				JSONObject itJson = new JSONObject();
				JSONArray itParts = new JSONArray();
				
				itJson.put("lowCause", it.getTypeLowPart());
				itJson.put("highCause", it.getTypeHighPart());
				
				for( ITPart itPartIt : it.getITParts()) {
					JSONObject itPart = new JSONObject();
					
					itPart.put("type", itPartIt.getType());
					itPart.put("date", dateFormat.format(itPartIt.getDate()));
					
					itParts.put(itPart);
				}
				
				itJson.put("itParts", itParts);
				its.put(itJson);
			}
			
			employee.put("its", its);
			result.put(employee);
		}
		
		return result;
	}
	
	private JSONArray toNoImportITJSON(Collection<EmployeeFieNotFound> noImportEmployees) {
		JSONArray result = new JSONArray();
		
		for(EmployeeFieNotFound noImportEmployee : noImportEmployees) {
			JSONObject employee = new JSONObject();
			
			employee.put("ccc", noImportEmployee.getCcc());
			employee.put("ipf", noImportEmployee.getIpf());
			employee.put("naf", noImportEmployee.getNaf());
			employee.put("fullName", noImportEmployee.getFullName());
			
			result.put(employee);
		}
		
		return result;
	}
	
	protected static String normalize(String input) {
		if(AonStringUtils.isBlank(input)) return input;
		 
		return Normalizer
	        .normalize(input, Normalizer.Form.NFD)
	        .replaceAll("[^\\p{ASCII}]", "");
	}
	
}
