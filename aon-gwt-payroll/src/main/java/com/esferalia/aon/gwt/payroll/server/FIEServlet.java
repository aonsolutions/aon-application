package com.esferalia.aon.gwt.payroll.server;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractLeave.CONTRACT_LEAVE;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.jooq.DSLContext;
import org.jooq.exception.TooManyRowsException;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.payroll.jooq.JooqIT;
import com.esferalia.aon.gwt.payroll.shared.FIEService;
import com.esferalia.aon.gwt.payroll.shared.ITEmployee;
import com.esferalia.aon.in.payroll.tgss.fie.FieListener;
import com.esferalia.aon.in.payroll.tgss.fie.FieParser;
import com.esferalia.aon.jooq.tables.records.ContractLeaveRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.payroll.EmployeeNotFoundexception;
import com.esferalia.aon.occam.api.model.payroll.TooManyEmployeesException;
import com.esferalia.aon.occam.api.model.payroll.TooManyITsException;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


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
		doPost(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		String userLogin = req.getParameter(Parameter.USER.name());
		String domainName = req.getParameter(Parameter.DOMAIN.name());
				
		try (OutputStream os = resp.getOutputStream(); 
			AONContext ctx = AONContext.getAONContext(domainName,userLogin)
			) {

			List<Integer> itIds = new ArrayList<Integer>();
			
			ctx.transaction( configuration -> {
				for ( Part part : req.getParts() ) {
					try ( InputStream is = part.getInputStream() ) {					
						itIds.addAll(doFie(is, ctx.getDslContext()));
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
	
	


	private static Collection<Integer> doFie(InputStream is, DSLContext ctx) throws IOException, SQLException {
			List<Integer> ids = new ArrayList<Integer>();
		
			Fie2AON fie2AON = new Fie2AON() {
				@Override
				public void endDIT() {
					ids.add(addIT(ctx, getIt()));
				}
				
				@Override
				public void endITD() {
				}
				
			};
			FieParser.parse(is, fie2AON);
			return ids;
	}
	
	
	
	
	public static class IT {
		private String ccc;
		private String naf;
		private String ipf;

		private Contingency contingency;
		
		private Date startDate;
		private Date endDate;
				
		private Date fromITDate;
		private Date prevItDate;
		private int accumulatedDays;

		private Date directPayStartDate;
		private Double directPayBase;
		
		
		
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

		public Contingency getContingency() {
			return contingency;
		}

		public void setContingency(Contingency contingency) {
			this.contingency = contingency;
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
		
		
		
	}
	
	public enum Contingency {

		COMMON_DISEASE,
		OCCUPATIONAL_DISEASE,
		MATERNITY,
		PATERNITY,
		PREGNANCY_RISK,
		BREASTFEEDING_RISK,
		NON_OCCUPATIONAL_DISEASE,
		COMMON_DISEASE_AT_LACK,
		COMMON_OCCUPATIONAL_DISEASE;
		
		public byte value() {
			return (byte) this.ordinal();
		}
	}
	
	public enum EndCause {		
		
		CURATION,
		DEATH,
		MEDICAL_INSPECTION,
		DISABILITY,
		TIME_EXHAUSTION,
		IMPROVEMENT,
		ENTERING,
		CONTROL_INSS,
		RECOVERY,
		ENTERING_EDUCATION;
   
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
			// 1=Enfermedad común;
			// 2=Accidente nolaboral;
			// 3=Accidente de Trabajo; 
			// 4=Enfermedad Profesional; 
			// 5=Periodo de observación.
			switch (contingency) {
			case 1:
				it.setContingency(Contingency.COMMON_DISEASE);
				break;
			case 2:
				it.setContingency(Contingency.NON_OCCUPATIONAL_DISEASE);
				break;
			case 3:
			case 4:
				it.setContingency(Contingency.OCCUPATIONAL_DISEASE);
				break;
			default:
				it.setContingency(Contingency.COMMON_DISEASE);
				break;
			}
		}

		@Override
		public void onDitDeficiencyIndicator(String deficiencyIndicator) {
			// S=se acredita carencia; 
			// N=no se acredita carencia;
			// P=consulta la Dirección Provincial del INSS
			switch (deficiencyIndicator) {
			case "N":
				it.setContingency(Contingency.COMMON_DISEASE_AT_LACK);
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
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onDitItPartCancel(Boolean itPartCancel) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onItdDirectPaymentStartDate(Date directPaymentStartDate) {
			it.setDirectPayStartDate(directPaymentStartDate);
		}

		@Override
		public void onItdRegulatoryBase(Float regulatoryBase) {
			it.setDirectPayBase(regulatoryBase.doubleValue());
		}

	
	}
	
	public static void addIT(String domainName, Integer domainId, String userLogin, IT it) {
		try (AONContext aonContext = AONContext.getAONContext(domainName, domainId, userLogin)) {
			addIT(aonContext.getDslContext(), it);
		}
		
	}
	
	public static Integer addIT(DSLContext ctx , IT it) throws EmployeeNotFoundexception, TooManyEmployeesException {
		java.sql.Date itStartDate = new java.sql.Date(it.getStartDate().getTime());
		java.sql.Date itEndDate = it.getEndDate().map( d -> new java.sql.Date(d.getTime())).orElse(null);
		
		try {
			
			ContractRecord contractRecord = 
			ctx
			.select()
			.from(REGISTRY)
			.innerJoin(PERSON).on(PERSON.REGISTRY.eq(REGISTRY.ID))
			.innerJoin(CONTRACT).on(CONTRACT.PERSON.eq(PERSON.REGISTRY))
			.innerJoin(ENTERPRISE_CCC).onKey()
			.where(ENTERPRISE_CCC.CCC.eq(it.getCcc()))
			.and(PERSON.SOCIAL_SECURITY_NUM.eq(it.getNaf()))
			.and(CONTRACT.END_DATE.isNull().or(CONTRACT.END_DATE.ge(itStartDate)))
			.and(DSL.condition(itEndDate == null ).or(CONTRACT.START_DATE.le(itEndDate)))
			.fetchOptionalInto(CONTRACT)
			.orElseThrow(() -> new EmployeeNotFoundexception() );
			; 
			
			
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
				;
				
				contractLeaveRecord.setStartDate(itStartDate);
				contractLeaveRecord.setType(it.getContingency().value());
				contractLeaveRecord.setEndDate(it.getEndDate().map(d -> itEndDate).orElse(null));
		
				contractLeaveRecord.store();
				
				return contractLeaveRecord.getId();
				
				
			} catch ( TooManyRowsException e) {
				throw new TooManyITsException(e);
			}
		} catch ( TooManyRowsException e) {
			throw new TooManyEmployeesException(e);
		}
		
	}
	


}
