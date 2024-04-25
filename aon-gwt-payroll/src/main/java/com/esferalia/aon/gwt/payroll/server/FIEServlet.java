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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import org.htmlunit.FailingHttpStatusCodeException;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.exception.TooManyRowsException;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.jooq.JooqIT;
import com.esferalia.aon.gwt.payroll.shared.FIEService;
import com.esferalia.aon.gwt.payroll.shared.ITEmployee;
import com.esferalia.aon.in.payroll.tgss.fie.FieListener;
import com.esferalia.aon.in.payroll.tgss.fie.FieParser;
import com.esferalia.aon.jooq.tables.records.ContractLeaveDetailRecord;
import com.esferalia.aon.jooq.tables.records.ContractLeaveRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.LeaveBatchRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.PAYROLL;
import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.payroll.CCCInfo;
import com.esferalia.aon.occam.api.model.payroll.EmployeeNotFoundexception;
import com.esferalia.aon.occam.api.model.payroll.TooManyEmployeesException;
import com.esferalia.aon.occam.api.model.payroll.TooManyITsException;
import com.esferalia.aon.occam.api.model.type.ContractLeaveType;
import com.esferalia.aon.watson.util.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gson.GsonBuilder;

import solutions.aon.seg.social.SistemaREDINSS;
import solutions.aon.seg.social.exception.InvalidCertificateException;


@MultipartConfig
@SuppressWarnings("serial")
@WebServlet(
		name = "INNS-FIE", 
		urlPatterns = { 
				"/aon_gwt_aio/fie/*" ,
				"/aon_gwt_payroll/fie/*" 
		}
)

public class FIEServlet extends HttpServlet implements FIEService {
	private static Logger LOGGER = Logger
			.getLogger(FIEServlet.class.getName());
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String userLogin = req.getParameter(Parameter.USER.name());
		String domainName = req.getParameter(Parameter.DOMAIN.name());
		
		Date endDate = new Date();
		Date startDate = AonDateUtils.add(endDate, Calendar.DAY_OF_MONTH, -14); 
		
		try (OutputStream os = resp.getOutputStream();
			Connection connection = AonServletUtils.getConnection(domainName);
			CloseableAONContext ctx = AONContext.getAONContext(domainName, userLogin)) {
		    
		    Integer domainId = AonServletUtils.getDomainID(domainName);
		    Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);
		    Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);
		    
		    Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "TGSS");
		    List<CCCInfo> cccs = PAYROLL.getCCCStream(domainName, domainId, userLogin).collect(Collectors.toList());		    
		    List<Integer> itIds = new ArrayList<Integer>();

		    for (CCCInfo ccc : cccs) {
			try {
			    byte[] fie = SistemaREDINSS.getFIE(certificate.getData(), certificate.getPassword(),certificate.getType(), ccc.getCccRegimeCode(), ccc.getCcc() , startDate, endDate);
			    itIds.addAll(  doFie(fie, ctx.getDslContext(), domainId) );
			} catch (FailingHttpStatusCodeException | IOException e) {
			    //
			}
		    }
		    
		    Collection<ITEmployee> itEmployees = JooqIT.getEmployeesITInfo(ctx, itIds);

		    resp.setStatus(HttpServletResponse.SC_OK);
		    byte[] content = new GsonBuilder().setDateFormat("YYYY-MM-dd").create().toJson(itEmployees)
			    .getBytes();
		    resp.setContentType("text/html");
		    resp.setContentLength(content.length);
		    os.write(content);
		    
		} catch (InvalidCertificateException | SQLException e) {
		    throw new ServletException(e);
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		String userLogin = req.getParameter(Parameter.USER.name());
		String domainName = req.getParameter(Parameter.DOMAIN.name());
				
		try (OutputStream os = resp.getOutputStream(); 
			CloseableAONContext ctx = AONContext.getAONContext(domainName,userLogin)
			) {
			
			List<Integer> itIds = new ArrayList<Integer>();
			
			ctx.transaction( configuration -> {
				for ( Part part : req.getParts() ) {
					try ( InputStream is = part.getInputStream() ) {
						itIds.addAll(doFie(is, ctx.getDslContext(), ctx.getDomainId() ));
					} catch ( IOException e ) {
						LOGGER.log(Level.WARNING, part.getName()  + ", not a .FIE message.");
					}
				}
				
			});
			
			Collection<ITEmployee> itEmployees = JooqIT.getEmployeesITInfo(ctx, itIds);
			
			resp.setStatus(HttpServletResponse.SC_OK);
			byte content [] = new GsonBuilder().setDateFormat("YYYY-MM-dd").create().toJson(itEmployees).getBytes();
			resp.setContentType("text/html");
			resp.setContentLength(content.length);
			os.write(content);
			

		} 
	}
	
	
	private static Collection<Integer> doFie(byte[] buf, DSLContext ctx, Integer domainId ) throws IOException, SQLException {
	    try ( ByteArrayInputStream is = new ByteArrayInputStream(buf)){
		return doFie(is, ctx, domainId);
	    }
	}

	private static Collection<Integer> doFie(InputStream is, DSLContext ctx, Integer domainId ) throws IOException, SQLException {
			List<Integer> ids = new ArrayList<Integer>();
		
			Fie2AON fie2AON = new Fie2AON() {
				@Override
				public void endDIT() {
					try {	
						IT itp = getIt();
						if(!itp.getCancel())
							ids.add(addIT(ctx, domainId, itp));
					} catch ( EmployeeNotFoundexception e) {
						
					}
				}
				
				@Override
				public void endITD() {
				}
				
				@Override
				public void endCIT() {
					try {
						addConfirmationPartIT(ctx, domainId, getIt());
					} catch ( Exception e) {
						
					}
				}
				
			};
			FieParser.parse(is, fie2AON);
			return ids;
	}
	
	
	
	
	public static class IT {
		private String ccc;
		private String naf;
		private String ipf;

		private ContractLeaveType contingency;
		private Byte hightCause;
		
		private Date startDate;
		private Date endDate;
				
		private Date fromITDate;
		private Date prevItDate;
		private Integer accumulatedDays;
		

		private Date directPayStartDate;
		private Double directPayBase;
		
		private Date confirmationDate;
		private String confirmationPartNumber;
		
		private boolean isRagged;
		
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

		public void setAccumulatedDays(Integer accumulatedDays) {
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

		public boolean isRagged() {
			return isRagged;
		}

		public void setRagged(boolean isRagged) {
			this.isRagged = isRagged;
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
		public void onDitItStartDate(Date itStartDate) {
			it.setStartDate(itStartDate);
		}

		@Override
		public void onDitRelapse(Boolean relapse) {
			it.setRagged(relapse);
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
			// 1=Enfermedad común;
			// 2=Accidente nolaboral;
			// 3=Accidente de Trabajo; 
			// 4=Enfermedad Profesional; 
			// 5=Periodo de observación.
			it.setContingency(ContractLeaveType.valueOfTGSS(contingency));
		}

		@Override
		public void onDitDeficiencyIndicator(String deficiencyIndicator) {
			// S=se acredita carencia; 
			// N=no se acredita carencia;
			// P=consulta la Dirección Provincial del INSS
			switch (deficiencyIndicator) {
			case "N":
				if(it.getContingency().equals(ContractLeaveType.ENFERMEDAD_COMUN)) it.setContingency(ContractLeaveType.ENFERMEDAD_COMUN_CARENCIA);
				break;
			default:
				break;
			}
		}

		@Override
		public void onDitDelegatePaymendEndDate(Date delegatePaymentEndDate) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onDitDelegatePaymendEndCause(String delegatePaymentEndCause) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onDitItEndDate(Date itEndDate) {
			it.setEndDate(itEndDate);
		}

		@Override
		public void onDitItEndCause(String itEndCause) {
			if(AonStringUtils.isNotBlank(itEndCause))
				try {
					it.setHightCause(parseAonHighCause(Byte.parseByte(itEndCause)));
				} catch (Exception e) {}
		}

		private Byte parseAonHighCause(byte hightCause) {
			switch (hightCause) {
				case 1: // Curacion
					return 0;
				case 2: // Fallecimiento
					return 1;
				case 3: // Inspeccion medica
					return 2;
				case 12: // Propuesta incapacida
					return 3;
				case 9: // Agotamiento de plazo
					return 4;
				case 6: // Mejoria que permite realizar el trabajo habitual
					return 5;
				case 7: // Incomparecencia
					return 6;
				case 17: // Recuperacion capacidad profesional
					return 8;
				case 18: // Incomparecencia contratos de formacion
					return 9;
				default:
					return 0;
			}
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
			addIT(aonContext.getDslContext(), domainId, it);
		}
	}
	
	public static Integer addIT(DSLContext ctx , Integer domainId, IT it) throws EmployeeNotFoundexception, TooManyEmployeesException {
		java.sql.Date startDateO = new java.sql.Date(it.getStartDate().getTime());
		java.sql.Date itStartDate = normalizeStartDateToSave(it.getContingency(), it.getStartDate());
		java.sql.Date itEndDate = it.getEndDate().map( d -> new java.sql.Date(d.getTime())).orElse(null);
		
		try {
			
			ContractRecord contractRecord = getContract(ctx, domainId, it);		
			
			try {
				ContractLeaveRecord contractLeaveRecord = 
				ctx
				.select()
				.from(CONTRACT_LEAVE)
				.where(CONTRACT_LEAVE.CONTRACT.eq(contractRecord.getId()))
				.and(CONTRACT_LEAVE.END_DATE.isNull().or(CONTRACT_LEAVE.END_DATE.ge(itStartDate)))
				.and(DSL.condition(itEndDate == null ).or(CONTRACT_LEAVE.START_DATE.le(itEndDate)))
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
		
				// Try to find ragged it
				if(it.isRagged()) {
					ContractLeaveRecord itRaggedRecord = ctx.selectFrom(CONTRACT_LEAVE)
						.where(CONTRACT_LEAVE.CONTRACT.eq(contractRecord.getId()))
						.and(CONTRACT_LEAVE.START_DATE.eq(new java.sql.Date(it.getPrevItDate().get().getTime())))
						.orderBy(CONTRACT_LEAVE.ID.desc())
						.limit(1)
						.fetchOne();
					
					contractLeaveRecord.setParent(null == itRaggedRecord ? null : itRaggedRecord.getId());
				}
				
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
		} catch ( TooManyRowsException e) {
			throw new TooManyEmployeesException(e);
		}
		
	}
	
	public static void addConfirmationPartIT(DSLContext ctx, Integer domainId, IT it) {
		java.sql.Date itStartDate = new java.sql.Date(it.getStartDate().getTime());
		java.sql.Date itEndDate = it.getEndDate().map( d -> new java.sql.Date(d.getTime())).orElse(null);
		java.sql.Date itConfirmationDate = new java.sql.Date(it.getConfirmationDate().getTime());
		
		try {
			
			ContractRecord contractRecord = 
			ctx
			.select()
			.from(REGISTRY)
			.innerJoin(PERSON).on(PERSON.REGISTRY.eq(REGISTRY.ID))
			.innerJoin(CONTRACT).on(CONTRACT.PERSON.eq(PERSON.REGISTRY))
			.innerJoin(ENTERPRISE_CCC).onKey()
			.where(ENTERPRISE_CCC.DOMAIN.eq(domainId))
			.and(ENTERPRISE_CCC.CCC.eq(it.getCcc()))
			.and(PERSON.SOCIAL_SECURITY_NUM.eq(it.getNaf()))
			.and(CONTRACT.END_DATE.isNull().or(CONTRACT.END_DATE.ge(itStartDate)))
			.and(DSL.condition(itEndDate == null ).or(CONTRACT.START_DATE.le(itEndDate)))
			.fetchOptionalInto(CONTRACT)
			.orElseGet(() -> 			
					ctx
					.select()
					.from(REGISTRY)
					.innerJoin(PERSON).on(PERSON.REGISTRY.eq(REGISTRY.ID))
					.innerJoin(CONTRACT).on(CONTRACT.PERSON.eq(PERSON.REGISTRY))
					.innerJoin(ENTERPRISE_CCC).on(ENTERPRISE_CCC.ID.eq(CONTRACT.ENTERPRISE_CCC))
					.innerJoin(DOMAIN).on(DOMAIN.ID.eq(ENTERPRISE_CCC.DOMAIN))
					.where(DOMAIN.PARENT.eq(domainId))
					.and(ENTERPRISE_CCC.CCC.eq(it.getCcc()))
					.and(PERSON.SOCIAL_SECURITY_NUM.eq(it.getNaf()))
					.and(CONTRACT.END_DATE.isNull().or(CONTRACT.END_DATE.ge(itStartDate)))
					.and(DSL.condition(itEndDate == null ).or(CONTRACT.START_DATE.le(itEndDate)))
					.fetchOptionalInto(CONTRACT)
					.orElseThrow(() -> new EmployeeNotFoundexception() ) 
			);
			
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
		} catch ( TooManyRowsException e) {
			throw new TooManyEmployeesException(e);
		}
	}

	private static java.sql.Date normalizeStartDateToSave(ContractLeaveType contingency, Date date) {
		if(contingency!=null && contingency.equals(ContractLeaveType.ACCIDENTE_LABORAL)) 
			date = DateUtils.addDays2Date(date, 1);
		
		return new java.sql.Date(date.getTime());
	}
	
	public static ContractRecord getContract(DSLContext ctx, Integer domainId, IT it) {
		java.sql.Date itStartDate = normalizeStartDateToSave(it.getContingency(), it.getStartDate());
		return ctx.select()
				.from(REGISTRY)
				.innerJoin(PERSON).on(PERSON.REGISTRY.eq(REGISTRY.ID))
				.innerJoin(CONTRACT).on(CONTRACT.PERSON.eq(PERSON.REGISTRY))
				.innerJoin(ENTERPRISE_CCC).onKey()
				.innerJoin(DOMAIN).on(DOMAIN.ID.eq(REGISTRY.DOMAIN))
				.where(ENTERPRISE_CCC.DOMAIN.eq(domainId))
				.and(ENTERPRISE_CCC.CCC.eq(it.getCcc()))
				.and(PERSON.SOCIAL_SECURITY_NUM.eq(it.getNaf()))
				.and(CONTRACT.START_DATE.le(itStartDate))
				.and(CONTRACT.END_DATE.isNull().or(CONTRACT.END_DATE.ge(itStartDate)))
				.and(DOMAIN.ACTIVE.eq((byte)1))
				.orderBy(CONTRACT.ID.desc())
				.fetchOptionalInto(CONTRACT)
				.orElseThrow(() -> new EmployeeNotFoundexception() );
	}
}
