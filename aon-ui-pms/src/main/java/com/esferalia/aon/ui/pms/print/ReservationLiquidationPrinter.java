package com.esferalia.aon.ui.pms.print;


import static com.esferalia.aon.jooq.tables.Finance.FINANCE;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.PayMethod.PAY_METHOD;
import static com.esferalia.aon.jooq.tables.PosShift.POS_SHIFT;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;

import org.jooq.DSLContext;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.finance.enumeration.Shift;
import com.code.aon.pool.AonConnectionException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.pms.ProjectReservation;

public class ReservationLiquidationPrinter implements ICollectionProvider {
	
	private static Settings SETTINGS = null;
	
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
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		DSLContext ctx = DSL.using(connection, getDefaultSettings());
		
		List<ReservationLiquidation> list = new LinkedList<ReservationLiquidationPrinter.ReservationLiquidation>();
		
		ctx.select(
				POS_SHIFT.START_TIME,
				POS_SHIFT.SHIFT,
				INVOICE.REFERENCE_CODE,
				INVOICE.TOTAL,
				PAY_METHOD.NAME,
				DSL.sum(DSL.decode().when(FINANCE.AMOUNT.greaterOrEqual(0.0), FINANCE.AMOUNT).otherwise(0.0)),
				DSL.sum(DSL.decode().when(FINANCE.AMOUNT.lessThan(0.0), FINANCE.AMOUNT).otherwise(0.0))
			)
			.from(INVOICE)
			.join(POS_SHIFT).on(INVOICE.POS_SHIFT.equal(POS_SHIFT.ID))
			.leftOuterJoin(FINANCE).on(INVOICE.ID.equal(FINANCE.INVOICE))
			.leftOuterJoin(PAY_METHOD).on(PAY_METHOD.ID.equal(FINANCE.PAY_METHOD))
			.where(INVOICE.TYPE.equal((byte) 1))
			.and(INVOICE.PROJECT.equal(project))
			.groupBy(INVOICE.REFERENCE_CODE, PAY_METHOD.NAME, DSL.sign(FINANCE.AMOUNT))
			.orderBy(INVOICE.REFERENCE_CODE, PAY_METHOD.NAME)
			.fetch()
			.forEach( record -> {
				ReservationLiquidation liq = new ReservationLiquidation();
				liq.setShiftName(sdf.format(record.value1()) +" / "+ getShift(new Integer(record.value2())));
				liq.setReferenceCode(record.value3());
				liq.setTotal(record.value4());
				liq.setPaymethod(record.value5());
				liq.setPayment(record.value6().doubleValue());
				liq.setCharge(record.value7().doubleValue());
				list.add(liq);
			});
		return list;
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
		private String referenceCode;
		private Double total;
		private String paymethod;
		private Double charge;
		private Double payment;
		public String getShiftName() {
			return shiftName;
		}
		public void setShiftName(String shiftName) {
			this.shiftName = shiftName;
		}
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