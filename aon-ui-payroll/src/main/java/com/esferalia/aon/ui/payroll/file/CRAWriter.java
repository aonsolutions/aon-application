package com.esferalia.aon.ui.payroll.file;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryCost.SALARY_COST;
import static com.esferalia.aon.jooq.tables.SalaryPayment.SALARY_PAYMENT;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.event.AbortProcessingException;

import org.apache.commons.lang.StringUtils;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Record4;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.common.enumeration.Month;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.file.format.model.FileFiller;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.pool.AonConnectionException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.file.payroll.cra.CRA;
import com.esferalia.aon.file.payroll.cra.data.CRE;
import com.esferalia.aon.file.payroll.cra.data.DDE;
import com.esferalia.aon.file.payroll.cra.data.ETI;
import com.esferalia.aon.file.payroll.cra.data.TRB;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.EnterpriseCCC;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.util.PayrollUtils;

public class CRAWriter {
	
	private final String WHITESPACE_1  = " ";
	
	private ETI eti;
	private static Settings SETTINGS = null;
	
	public ETI getEti() {
		return eti;
	}
	public void setEti(ETI eti) {
		this.eti = eti;
	}

	public FileOutput createCRA(List<EnterpriseCCC> list, Integer year, Month month) throws ManagerBeanException {
		try {
			ETI eti = buildCRA( list, year, month );
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			PrintWriter writer = new PrintWriter(outputStream);
			FileFiller cra = new CRA(eti, writer);
			FileOutput output = new FileOutput();
			output.setErrors(cra.create());
			output.setContent(outputStream.toByteArray());
			return output;
		} catch (IOException e) {
			throw new ManagerBeanException(e);
		}
	}
	
	private ETI buildCRA(List<EnterpriseCCC> cccs, Integer year, Month month) throws ManagerBeanException {
		ETI eti = createETIRecord(year, month);
		Connection connection = null; 
		try {
			IManagerBean contractBean = BeanManager.getManagerBean(Contract.class);
			Calendar startCal = Calendar.getInstance();
			startCal.set(year, month.ordinal(), 1, 0, 0, 0);
			Calendar endCal = Calendar.getInstance();
			endCal.set(year, month.ordinal(), 1, 23, 59, 59);
			endCal.set(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
			connection = DatabaseUtil.getConnection(AonUtil.getDomainName());
			for (EnterpriseCCC ccc: cccs) {
				DDE dde = createDDERecord(year, month, PayrollUtils.getInstance().getRegimeCode(ccc)+ccc.getCcc());
				Result<Record4<Byte, Double, String, Integer>> paymentsRecord = getSalaryPaymentSelect(connection, ccc, startCal.getTime(), endCal.getTime() );
				TRB trb = null;
				String previousType = "";
				for (Record4<Byte, Double, String, Integer> step : paymentsRecord) {
					Byte type = step.value1();
					Double amount = step.value2();
					String concept = step.value3();
					Integer contractId = step.value4();
					if(amount!=0.0d && !isSSDelegatePayment(connection, startCal.getTime(), endCal.getTime(), concept, amount, contractId)){
						Contract contract = (Contract) contractBean.get(contractId);
						if(trb==null || !contract.getPerson().getSocialSecurityNumber().equals(trb.getNaf())){
							trb = createTRBRecord(contract.getPerson().getSocialSecurityNumber());
							dde.getTrbList().add(trb);
						}
						CRE cre = createCRERecord(String.valueOf(type), amount, previousType.equals(String.valueOf(type)));
						previousType = String.valueOf(type);
						if(cre!=null){
							trb.getCreList().add(cre);
						}
					}
				}
				if(dde.getTrbList()!=null && !dde.getTrbList().isEmpty()){
					eti.getDdeList().add(dde);
				} else {
					AonUtil.addErrorMessage("No hay datos para la cuenta de cotización: " + PayrollUtils.getInstance().getRegimeCode(ccc)+ccc.getCcc());
				}
			}
			return eti;
		} catch (AonConnectionException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		} finally {
			DatabaseUtil.closeQuietly(connection);
		}		
	}
		
		
	private ETI createETIRecord(Integer year, Month month) throws ManagerBeanException {
		ETI eti = new ETI();
		String authorizationKey = getAuthorizationKey();
		if(StringUtils.isNotBlank(authorizationKey)){
			eti.setClave(StringUtils.leftPad(authorizationKey, 8, '0'));
		} else {
			AonUtil.addErrorMessage("No se ha definido la clave de autorización.");
		}
		eti.setPrueba(WHITESPACE_1);
		setEti( eti );
		return getEti();
	}
	
	/**
	 * DDE - Datos De Empresa
	 * 
	 * @param ccc
	 * @return
	 * @throws ManagerBeanException
	 */
	private DDE createDDERecord(Integer year, Month month, String ccc) throws  ManagerBeanException {
		DDE dde = new DDE();
		dde.setAnio(year.toString());
		dde.setMes(String.valueOf(month.ordinal()+1));
		dde.setCodigoCuentaCotizacionSeguridadSocial(ccc);
		return dde;
	}
	
	/**
	 * TRB - Datos de TRabajador
	 * 
	 * @param contract
	 * @param dde
	 * @return
	 * @throws ManagerBeanException
	 */
	private TRB createTRBRecord(String socialSecurityNumber) throws ManagerBeanException {
		TRB trb = new TRB();
		trb.setNaf(socialSecurityNumber);
		return trb;
	}
	
	/**
	 * CRE - Conceptos REtributivos
	 * 
	 * @param to
	 * @return
	 */
	private CRE createCRERecord(String code, Double amount, boolean repeated) {
		// TODO
		if(amount!=0.0d){
			CRE cre = new CRE();
			cre.setConcepto(autoComplete(code, 4, "0", true));		
//			Valores posibles: (IndicativoConcepto)
//			E=concepto excluido de la base; 
//			I=concepto incluido de la base.
			cre.setIndicativoConcepto(( Integer.parseInt(code)==35 || Integer.parseInt(code)>=42 ) ? "E" : "I");
			cre.setImporte(String.valueOf((int)(CommonUtil.round(amount, 2)*100)));
//			Valores posibles: (IndicativoTipoActuacion)
//			M=modificación; 
//			C=complementario; 
//			B=baja. 
			cre.setIndicativoTipoActuacion(repeated?"C":"");
			return cre;
		}
		return null;
	}
	
	//////////////////////////
	// AUX
	//////////////////////////
	private String getAuthorizationKey() {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			String select = "SELECT value FROM app_param";
			select += " WHERE domain = " + DomainManager.getCurrentDomain();
			select += " AND name = '" + AppParam.PAY_authorization_key_PAY.getValue() + "';";
			
			ps = conn.prepareStatement(select);
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				return rs.getString(1);
			} else {
				select = "SELECT value FROM app_param";
				select += " WHERE domain = " + DomainManager.getDomainProvider().getParentDomain();
				select += " AND name = '" + AppParam.PAY_authorization_key_PAY.getValue() + "';";
				
				ps = conn.prepareStatement(select);
				rs = ps.executeQuery();
				if(rs.next()){
					return rs.getString(1);
				}
			}
		} catch (AonConnectionException e) {
			// return null
		} catch (SQLException e) {
			// return null
		} finally {
			DatabaseUtil.closeQuietly(ps);
			DatabaseUtil.closeQuietly(conn);
		}
		return null;
	}
	
	private Result<Record4<Byte, Double, String, Integer>> getSalaryPaymentSelect(Connection connection, EnterpriseCCC ccc, Date startDate, Date endDate ) {
		DSLContext ctx = DSL.using(connection, getDefaultSettings());
			
		Result<Record4<Byte, Double, String, Integer>> record = ctx.select(SALARY_PAYMENT.TYPE, SALARY_PAYMENT.AMOUNT, SALARY_PAYMENT.PAYMENT_CONCEPT, SALARY.CONTRACT)
			.from(SALARY_PAYMENT)
			.leftOuterJoin(SALARY).onKey()
			.where(SALARY.END_DATE.between(toSqlDate(startDate)).and(toSqlDate(endDate)))
			.and(SALARY.CONTRACT.in( ctx.select(CONTRACT.ID).from(CONTRACT).where(CONTRACT.ENTERPRISE_CCC.equal(ccc.getId())) ))
			.orderBy(SALARY.SOCIAL_SECURITY_NUMBER, SALARY.TYPE)
			.fetch();
		return record;
	}

	private Result<Record1<Double>> getSSDelegatePaymentSelect(Connection connection, Date startDate, Date endDate, Integer contractId) {
		DSLContext ctx = DSL.using(connection, getDefaultSettings());
		
		Result<Record1<Double>> record = ctx.select(SALARY_COST.AMOUNT)
				.from(SALARY_COST)
				.leftOuterJoin(SALARY).onKey()
				.where(SALARY.END_DATE.between(toSqlDate(startDate)).and(toSqlDate(endDate)))
				.and(SALARY.CONTRACT.equal(contractId))
				.and(SALARY_COST.COST_CONCEPT.equal("ECSS_E"))
				.fetch();
		return record;
	}
	
	private boolean isSSDelegatePayment(Connection connection, Date startDate, Date endDate, String concept, Double amount, Integer contractId){
		if(StringUtils.equals(concept,ContextVariable.PREST_IT.getName())){
			boolean isDelegatePayment = false;
			Result<Record1<Double>> costsRecord = getSSDelegatePaymentSelect(connection, startDate, endDate, contractId);
			for (Record1<Double> step : costsRecord) {
				Double costAmount = step.value1();
				if( amount.equals(Math.abs(costAmount)) ){
					isDelegatePayment = true;
				}
			}
			return isDelegatePayment;
		}
		return false;
	}
	
	protected java.sql.Date toSqlDate(Date date){
		return (date==null)?null:new java.sql.Date( date.getTime() );
	}
	
	private String autoComplete(String value, int lenght, String completeValue, boolean leftSide) {
		while( value.length() < lenght ){
			value = leftSide ? (completeValue + value) : (value + completeValue);
		}
		return value;
	}
	
	private static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
		}
		return SETTINGS;
	}
}
