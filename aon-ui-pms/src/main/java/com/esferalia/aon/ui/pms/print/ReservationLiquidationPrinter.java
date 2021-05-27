package com.esferalia.aon.ui.pms.print;


import static com.esferalia.aon.jooq.tables.Finance.FINANCE;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.PayMethod.PAY_METHOD;
import static com.esferalia.aon.jooq.tables.PosShift.POS_SHIFT;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;

import org.apache.commons.lang.StringUtils;
import org.jooq.DSLContext;
import org.jooq.Record7;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ManagerBeanException;
import net.aonsolutions.core.dbutils.DatabaseUtil;
import com.code.aon.finance.enumeration.Shift;
import net.aonsolutions.core.pool.AonConnectionException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.pms.ProjectReservation;

public class ReservationLiquidationPrinter implements ICollectionProvider {
	
	private static Settings SETTINGS = null;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	
	private ProjectReservation projectReservation;
	
	public ProjectReservation getProjectReservation() {
		return projectReservation;
	}

	public void setProjectReservation(ProjectReservation projectReservation) {
		this.projectReservation = projectReservation;
	}

	@Override
	public Collection getCollection() {
		return loadCollection();
	}

	@Override
	public Collection getCollection(boolean forceRefresh)
			throws ManagerBeanException {
		return getCollection();
	}
	
	private List<ReservationLiquidation> loadCollection() {
		Connection connection = null; 
		try {
			connection = DatabaseUtil.getConnection(AonUtil.getDomainName());
			List<ReservationLiquidation> list  = getList(connection, projectReservation.getProject().getId());
			return list!=null?list:null;
		} catch (AonConnectionException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		} finally {
			DatabaseUtil.closeQuietly(connection);
		}	
	}
	
	private List<ReservationLiquidation> getList(Connection connection, Integer project) {
		List<ReservationLiquidation> list = new LinkedList<ReservationLiquidationPrinter.ReservationLiquidation>();
		DSLContext ctx = DSL.using(connection, getDefaultSettings());
		ctx.select(
				POS_SHIFT.START_TIME,
				POS_SHIFT.SHIFT,
				INVOICE.REFERENCE_CODE,
				INVOICE.TOTAL,
				PAY_METHOD.NAME,
				DSL.sum(DSL.decode().when(FINANCE.AMOUNT.lessThan(0.0), FINANCE.AMOUNT).otherwise(0.0)),
				DSL.sum(DSL.decode().when(FINANCE.AMOUNT.greaterOrEqual(0.0), FINANCE.AMOUNT).otherwise(0.0))
			)
			.from(INVOICE)
			.join(POS_SHIFT).on(INVOICE.POS_SHIFT.equal(POS_SHIFT.ID))
			.leftOuterJoin(FINANCE).on(INVOICE.ID.equal(FINANCE.INVOICE))
			.leftOuterJoin(PAY_METHOD).on(PAY_METHOD.ID.equal(FINANCE.PAY_METHOD))
			.where(INVOICE.TYPE.equal((byte) 1))
			.and(INVOICE.PROJECT.equal(project))
			.groupBy(INVOICE.REFERENCE_CODE, PAY_METHOD.NAME, DSL.sign(FINANCE.AMOUNT))
			.orderBy(POS_SHIFT.START_TIME, POS_SHIFT.SHIFT, PAY_METHOD.NAME, INVOICE.ID)
			.fetch()
			.forEach( record -> { 
				addReservationLiquidation(record, list);
			});
		return list;
	}
	

	private void addReservationLiquidation(Record7<Timestamp, Byte, String, Double, String, BigDecimal, BigDecimal> record, List<ReservationLiquidation> list){
		
		String name = sdf.format(record.value1()) +" "+ getShift(new Integer(record.value2()));
		String referenceCode = record.value3();
		Double total = record.value4();
		String paymethod = record.value5();
		Double payment = record.value6().doubleValue();
		Double charge = record.value7().doubleValue();
		
		ReservationLiquidation liq = null;
		if(!list.isEmpty() && name.equals(list.get(list.size()-1).getShiftName())){
			liq = list.get(list.size()-1);
			
			if(!liq.getInvoices().isEmpty() && !referenceCode.equals(liq.getInvoices().get(liq.getInvoices().size()-1).getReferenceCode())){
				ReservationLiquidationInvoice invoice = new ReservationLiquidationInvoice();
				invoice.setReferenceCode(referenceCode);
				invoice.setTotal(total);
				liq.getInvoices().add(invoice);
			}
			if(!liq.getFinances().isEmpty() && paymethod.equals(liq.getFinances().get(liq.getFinances().size()-1).getPaymethod())){
				ReservationLiquidationFinance finance = liq.getFinances().get(liq.getFinances().size()-1);
				finance.setPaymethod(paymethod);
				finance.setPayment(finance.getPayment()+payment);
				finance.setCharge(finance.getCharge()+charge);
			} else {
				if(StringUtils.isNotBlank(paymethod)){
					ReservationLiquidationFinance finance = new ReservationLiquidationFinance();
					finance.setPaymethod(paymethod);
					finance.setPayment(payment);
					finance.setCharge(charge);
					liq.getFinances().add(finance);
				}
			}
		} else {
			liq = new ReservationLiquidation();
			liq.setShiftName(name);

			liq.setInvoices(new LinkedList<>());
			ReservationLiquidationInvoice invoice = new ReservationLiquidationInvoice();
			invoice.setReferenceCode(referenceCode);
			invoice.setTotal(total);
			liq.getInvoices().add(invoice);
			
			liq.setFinances(new LinkedList<>());
			if(StringUtils.isNotBlank(paymethod)){
				ReservationLiquidationFinance finance = new ReservationLiquidationFinance();
				finance.setPaymethod(paymethod);
				finance.setPayment(payment);
				finance.setCharge(charge);
				liq.getFinances().add(finance);
			}
			list.add(liq);
		}
		
	}
	
	private String getShift(Integer ordinal){
		try {
			Shift shift = Shift.values()[ordinal];
			return shift!=null?shift.getName(AonUtil.getCurrentLocale()):null;
		} catch(Throwable th){
			return null;
		}
	}
	
	private static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
		}
		return SETTINGS;
	}

	
	
	public class ReservationLiquidation {
		private String shiftName;
		private List<ReservationLiquidationInvoice> invoices;
		private List<ReservationLiquidationFinance> finances;
		public String getShiftName() {
			return shiftName;
		}
		public void setShiftName(String shiftName) {
			this.shiftName = shiftName;
		}
		public List<ReservationLiquidationInvoice> getInvoices() {
			return invoices;
		}
		public void setInvoices(List<ReservationLiquidationInvoice> invoices) {
			this.invoices = invoices;
		}
		public List<ReservationLiquidationFinance> getFinances() {
			return finances;
		}
		public void setFinances(List<ReservationLiquidationFinance> finances) {
			this.finances = finances;
		}
	}

	public class ReservationLiquidationInvoice {
		private String referenceCode;
		private Double total;
		public String getReferenceCode() {
			return referenceCode;
		}
		public void setReferenceCode(String referenceCode) {
			this.referenceCode = referenceCode;
		}
		public Double getTotal() {
			return total;
		}
		public void setTotal(Double total) {
			this.total = total;
		}
	}
	
	public class ReservationLiquidationFinance {
		private String paymethod;
		private Double charge;
		private Double payment;
		public String getPaymethod() {
			return paymethod;
		}
		public void setPaymethod(String paymethod) {
			this.paymethod = paymethod;
		}
		public Double getCharge() {
			return charge;
		}
		public void setCharge(Double charge) {
			this.charge = charge;
		}
		public Double getPayment() {
			return payment;
		}
		public void setPayment(Double payment) {
			this.payment = payment;
		}
	}
	

}