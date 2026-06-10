package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.Keys.FK_AGREEMENT_LEVEL_AGREEMENT;
import static com.esferalia.aon.jooq.Keys.FK_AGREEMENT_LEVEL_CATEGORY_AGREEMENT_LEVEL;
import static com.esferalia.aon.jooq.Keys.FK_AGREEMENT_LEVEL_DATA_AGREEMENT_LEVEL;
import static com.esferalia.aon.jooq.Keys.FK_AGREEMENT_PAYMENT_AGREEMENT;
import static com.esferalia.aon.jooq.Keys.FK_CONTRACT_AGREEMENT_LEVEL;
import static com.esferalia.aon.jooq.Keys.FK_SALARY_CONTRACT;
import static com.esferalia.aon.jooq.tables.Agreement.AGREEMENT;
import static com.esferalia.aon.jooq.tables.AgreementData.AGREEMENT_DATA;
import static com.esferalia.aon.jooq.tables.AgreementExtra.AGREEMENT_EXTRA;
import static com.esferalia.aon.jooq.tables.AgreementLevel.AGREEMENT_LEVEL;
import static com.esferalia.aon.jooq.tables.AgreementLevelCategory.AGREEMENT_LEVEL_CATEGORY;
import static com.esferalia.aon.jooq.tables.AgreementLevelData.AGREEMENT_LEVEL_DATA;
import static com.esferalia.aon.jooq.tables.AgreementPayment.AGREEMENT_PAYMENT;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static java.util.Calendar.DAY_OF_MONTH;

import java.sql.Connection;
import java.sql.Date;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.jooq.Cursor;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Record4;
import org.jooq.SQLDialect;
import org.jooq.SelectConditionStep;
import org.jooq.SelectHavingStep;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.Keys;
import com.esferalia.aon.jooq.tables.records.AgreementLevelRecord;
import com.esferalia.aon.jooq.tables.records.AgreementPaymentRecord;
import com.esferalia.aon.jooq.tables.records.AgreementRecord;

import net.aonsolutions.db.up2date.Update;

public class AgreementUpdate implements Update {
	
	public static final AgreementUpdate AGREEMENTUPDATE = new AgreementUpdate();
	
	private static final Date EPOCH = new Date(0);
	private static final Date FOREVER = null;
	
	private static final String WARNNING = "HIDE(\""
	+"<div>Este convenio ha sido modificado en la &uacute;ltima actualizaci&oacute;n."
	+"El convenio original est&aacute; disponible en la papelera.</div>"
	+"<div>Por favor revise las n&oacute;minas y pagas extras."
	+"Si encuentra alg&uacute;n error comun&iacute;quese con nosotros.</div>"
	+"<div>Si desea recuperar el convenio original, envie este convenio a la papelera.</div>"
	+"<div>&nbsp;</div><div class='aon-text-right'>Disculpe las molestias, <span class='aon-icon aon-icon-logo' />aon Solutions</div>\");"
	;
	
	private static final SelectHavingStep<Record1<Integer>> AGREEMENT_UPDATED = DSL
		.select(DSL.abs(AGREEMENT.ID))
		.from(AGREEMENT)
		.join(AGREEMENT_PAYMENT).onKey(FK_AGREEMENT_PAYMENT_AGREEMENT)
		//.where(AGREEMENT.ID.gt(0))
		.where(AGREEMENT_PAYMENT.START_DATE.eq(EPOCH))
		.and(AGREEMENT_PAYMENT.END_DATE.isNull())
		.groupBy(AGREEMENT.ID);

	private AgreementUpdate() {
	}

	@Override
	public void upgrade(Connection connection) {
		
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		// Establish context
		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		dslContext.transaction( (config) -> {
			//move2Trash(dslContext);
			//checkTrash(dslContext);
			//fixUpPayments(dslContext);
			cleanWarnPayments(dslContext);
		});
	}

	
	protected void move2Trash(DSLContext dslContext) {
		
		SelectConditionStep<Record1<Integer>> agreementInTrash = 
				dslContext.select(AGREEMENT.ID.mul(-1)).from(AGREEMENT).where(AGREEMENT.ID.lt(0));

		
		int agreements = 
		dslContext
		.insertInto(AGREEMENT)
		.columns(AGREEMENT.ID, AGREEMENT.DOMAIN, AGREEMENT.DESCRIPTION, AGREEMENT.CALENDAR)
		.select(dslContext.select(AGREEMENT.ID.mul(-1), AGREEMENT.DOMAIN, AGREEMENT.DESCRIPTION, AGREEMENT.CALENDAR)
				.from(AGREEMENT).where(AGREEMENT.ID.notIn(agreementInTrash)).and(AGREEMENT.ID.gt(0)).and(AGREEMENT.ID.notIn(AGREEMENT_UPDATED)))
		.execute()
		;
		
		
		// Fix PAYMENT_CONCEPT missing dependencies
		dslContext
		.update(AGREEMENT_PAYMENT)
		.set(AGREEMENT_PAYMENT.PAYMENT_CONCEPT, DSL.castNull(AGREEMENT_PAYMENT.PAYMENT_CONCEPT))
		.where(AGREEMENT_PAYMENT.PAYMENT_CONCEPT.notIn(dslContext.select(PAYMENT_CONCEPT.ID).from(PAYMENT_CONCEPT)))
		.and(AGREEMENT_PAYMENT.AGREEMENT.gt(0))
		.execute()
		;
		
		// Fix AGREEMENT missing dependencies
		dslContext
		.delete(AGREEMENT_PAYMENT)
		.where(AGREEMENT_PAYMENT.AGREEMENT.notIn(dslContext.select(AGREEMENT.ID).from(AGREEMENT)))
		.execute()
		;
		
		// Re-index 'deleted' AGREEMENT_PAYMENT  
		dslContext
		.select()
		.from(AGREEMENT_PAYMENT)
		.where(AGREEMENT_PAYMENT.ID.in(dslContext.select(AGREEMENT_PAYMENT.ID.mul(-1)).from(AGREEMENT_PAYMENT).where(AGREEMENT_PAYMENT.ID.lt(0))))
		.and(AGREEMENT_PAYMENT.AGREEMENT.notIn(AGREEMENT_UPDATED))
		.fetchInto(AGREEMENT_PAYMENT)
		.forEach( p -> { 
			AgreementPaymentRecord copy =p.copy();
			copy.insert();
			dslContext.update(AGREEMENT_EXTRA)
			.set(AGREEMENT_EXTRA.AGREEMENT_PAYMENT, copy.getId())
			.where(AGREEMENT_EXTRA.AGREEMENT_PAYMENT.eq(p.getId()))
			.execute();
			p.delete();
		})
		;


		int payments = 
		dslContext
		.insertInto(AGREEMENT_PAYMENT)
		.columns(AGREEMENT_PAYMENT.ID, AGREEMENT_PAYMENT.DOMAIN, AGREEMENT_PAYMENT.AGREEMENT, AGREEMENT_PAYMENT.PAYMENT_CONCEPT, AGREEMENT_PAYMENT.START_DATE, AGREEMENT_PAYMENT.END_DATE, AGREEMENT_PAYMENT.DESCRIPTION, AGREEMENT_PAYMENT.DESCRIPTION_DECORABLE, AGREEMENT_PAYMENT.EXPRESSION, AGREEMENT_PAYMENT.IRPF_EXPRESSION, AGREEMENT_PAYMENT.QUOTE_EXPRESSION, AGREEMENT_PAYMENT.TYPE, AGREEMENT_PAYMENT.SALARY_TYPE, AGREEMENT_PAYMENT.MONTH)
		.select(dslContext.select(AGREEMENT_PAYMENT.ID.mul(-1), AGREEMENT_PAYMENT.DOMAIN, AGREEMENT_PAYMENT.AGREEMENT.mul(-1), AGREEMENT_PAYMENT.PAYMENT_CONCEPT, AGREEMENT_PAYMENT.START_DATE, AGREEMENT_PAYMENT.END_DATE, AGREEMENT_PAYMENT.DESCRIPTION, AGREEMENT_PAYMENT.DESCRIPTION_DECORABLE, AGREEMENT_PAYMENT.EXPRESSION, AGREEMENT_PAYMENT.IRPF_EXPRESSION, AGREEMENT_PAYMENT.QUOTE_EXPRESSION, AGREEMENT_PAYMENT.TYPE, AGREEMENT_PAYMENT.SALARY_TYPE, AGREEMENT_PAYMENT.MONTH)
				.from(AGREEMENT_PAYMENT).where(AGREEMENT_PAYMENT.AGREEMENT.gt(0)).and(AGREEMENT_PAYMENT.AGREEMENT.notIn(AGREEMENT_UPDATED)))
		.execute()
		;


		// Re-index 'deleted' AGREEMENT_EXTRA  
		dslContext
		.select()
		.from(AGREEMENT_EXTRA)
		.where(AGREEMENT_EXTRA.ID.in(dslContext.select(AGREEMENT_EXTRA.ID.mul(-1)).from(AGREEMENT_EXTRA).where(AGREEMENT_EXTRA.ID.lt(0))))
		.and(AGREEMENT_EXTRA.AGREEMENT.notIn(AGREEMENT_UPDATED))
		.fetchInto(AGREEMENT_EXTRA)
		.forEach( p ->{ 
			p.copy().insert();
			p.delete();
		})
		;

		int extras = 
		dslContext
		.insertInto(AGREEMENT_EXTRA)
		.columns(AGREEMENT_EXTRA.ID, AGREEMENT_EXTRA.DOMAIN, AGREEMENT_EXTRA.AGREEMENT, AGREEMENT_EXTRA.AGREEMENT_PAYMENT, AGREEMENT_EXTRA.START_DATE, AGREEMENT_EXTRA.END_DATE, AGREEMENT_EXTRA.ISSUE_DATE)
		.select(dslContext.select(AGREEMENT_EXTRA.ID.mul(-1), AGREEMENT_EXTRA.DOMAIN, AGREEMENT_EXTRA.AGREEMENT.mul(-1), AGREEMENT_EXTRA.AGREEMENT_PAYMENT.mul(-1), AGREEMENT_EXTRA.START_DATE, AGREEMENT_EXTRA.END_DATE, AGREEMENT_EXTRA.ISSUE_DATE)
				.from(AGREEMENT_EXTRA).where(AGREEMENT_EXTRA.AGREEMENT.gt(0)).and(AGREEMENT_EXTRA.AGREEMENT.notIn(AGREEMENT_UPDATED)))
		.execute()
		;
		
		// Re-index 'deleted' AGREEMENT_DATA
		dslContext
		.select()
		.from(AGREEMENT_DATA)
		.where(AGREEMENT_DATA.ID.in(dslContext.select(AGREEMENT_DATA.ID.mul(-1)).from(AGREEMENT_DATA).where(AGREEMENT_DATA.ID.lt(0))))
		.and(AGREEMENT_DATA.AGREEMENT.notIn(AGREEMENT_UPDATED))
		.fetchInto(AGREEMENT_DATA)
		.forEach( p ->{ 
			p.copy().insert();
			p.delete();
		})
		;

		int datas =
		dslContext
		.insertInto(AGREEMENT_DATA)
		.columns(AGREEMENT_DATA.ID, AGREEMENT_DATA.DOMAIN, AGREEMENT_DATA.AGREEMENT, AGREEMENT_DATA.START_DATE, AGREEMENT_DATA.END_DATE, AGREEMENT_DATA.NAME, AGREEMENT_DATA.EXPRESSION)
		.select(dslContext.select(AGREEMENT_DATA.ID.mul(-1), AGREEMENT_DATA.DOMAIN, AGREEMENT_DATA.AGREEMENT.mul(-1), AGREEMENT_DATA.START_DATE, AGREEMENT_DATA.END_DATE, AGREEMENT_DATA.NAME, AGREEMENT_DATA.EXPRESSION)
				.from(AGREEMENT_DATA).where(AGREEMENT_DATA.AGREEMENT.gt(0)).and(AGREEMENT_DATA.AGREEMENT.notIn(AGREEMENT_UPDATED)))
		.execute()
		;
		
		// Re-index 'deleted' AGREEMENT_LEVEL
		dslContext
		.select()
		.from(AGREEMENT_LEVEL)
		.where(AGREEMENT_LEVEL.ID.in(dslContext.select(AGREEMENT_LEVEL.ID.mul(-1)).from(AGREEMENT_LEVEL).where(AGREEMENT_LEVEL.ID.lt(0))))
		.and(AGREEMENT_LEVEL.AGREEMENT.notIn(AGREEMENT_UPDATED))
		.fetchInto(AGREEMENT_LEVEL)
		.forEach( p ->{ 
			AgreementLevelRecord copy =p.copy();
			copy.insert();
			dslContext.update(CONTRACT)
			.set(CONTRACT.AGREEMENT_LEVEL, copy.getId())
			.where(CONTRACT.AGREEMENT_LEVEL.eq(p.getId()))
			.execute();
			dslContext.update(AGREEMENT_LEVEL_DATA)
			.set(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL, copy.getId())
			.where(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL.eq(p.getId()))
			.execute();
			dslContext.update(AGREEMENT_LEVEL_CATEGORY)
			.set(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL, copy.getId())
			.where(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL.eq(p.getId()))
			.execute();
			p.delete();
		})
		;

		int levels = 
		dslContext
		.insertInto(AGREEMENT_LEVEL)
		.columns(AGREEMENT_LEVEL.ID, AGREEMENT_LEVEL.DOMAIN, AGREEMENT_LEVEL.AGREEMENT, AGREEMENT_LEVEL.DESCRIPTION)
		.select(dslContext.select(AGREEMENT_LEVEL.ID.mul(-1), AGREEMENT_LEVEL.DOMAIN, AGREEMENT_LEVEL.AGREEMENT.mul(-1), AGREEMENT_LEVEL.DESCRIPTION)
				.from(AGREEMENT_LEVEL).where(AGREEMENT_LEVEL.AGREEMENT.gt(0)).and(AGREEMENT_LEVEL.AGREEMENT.notIn(AGREEMENT_UPDATED)))
		.execute()
		;

		// Re-index 'deleted' AGREEMENT_LEVEL_CATEGORY
		dslContext
		.select()
		.from(AGREEMENT_LEVEL_CATEGORY)
		.join(AGREEMENT_LEVEL).onKey(FK_AGREEMENT_LEVEL_CATEGORY_AGREEMENT_LEVEL)
		.where(AGREEMENT_LEVEL_CATEGORY.ID.in(dslContext.select(AGREEMENT_LEVEL_CATEGORY.ID.mul(-1)).from(AGREEMENT_LEVEL_CATEGORY).where(AGREEMENT_LEVEL_CATEGORY.ID.lt(0))))
		.and(AGREEMENT_LEVEL.AGREEMENT.notIn(AGREEMENT_UPDATED))
		.fetchInto(AGREEMENT_LEVEL_CATEGORY)
		.forEach( p ->{ 
			p.copy().insert();
			p.delete();
		})
		;

		int categories = 
		dslContext
		.insertInto(AGREEMENT_LEVEL_CATEGORY)
		.columns(AGREEMENT_LEVEL_CATEGORY.ID, AGREEMENT_LEVEL_CATEGORY.DOMAIN, AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL, AGREEMENT_LEVEL_CATEGORY.DESCRIPTION)
		.select(dslContext.select(AGREEMENT_LEVEL_CATEGORY.ID.mul(-1), AGREEMENT_LEVEL_CATEGORY.DOMAIN, AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL.mul(-1), AGREEMENT_LEVEL_CATEGORY.DESCRIPTION)
				.from(AGREEMENT_LEVEL_CATEGORY)
				.join(AGREEMENT_LEVEL).onKey(FK_AGREEMENT_LEVEL_CATEGORY_AGREEMENT_LEVEL)
				.where(AGREEMENT_LEVEL.AGREEMENT.gt(0)).and(AGREEMENT_LEVEL.AGREEMENT.notIn(AGREEMENT_UPDATED)))
		.execute()
		;

		// Re-index 'deleted' AGREEMENT_LEVEL_DATA
		dslContext
		.select()
		.from(AGREEMENT_LEVEL_DATA)
		.join(AGREEMENT_LEVEL).onKey(FK_AGREEMENT_LEVEL_DATA_AGREEMENT_LEVEL)
		.where(AGREEMENT_LEVEL_DATA.ID.in(dslContext.select(AGREEMENT_LEVEL_DATA.ID.mul(-1)).from(AGREEMENT_LEVEL_DATA).where(AGREEMENT_LEVEL_DATA.ID.lt(0))))
		.and(AGREEMENT_LEVEL.AGREEMENT.notIn(AGREEMENT_UPDATED))
		.fetchInto(AGREEMENT_LEVEL_DATA)
		.forEach( p ->{ 
			p.copy().insert();
			p.delete();
		})
		;

		datas += 
		dslContext
		.insertInto(AGREEMENT_LEVEL_DATA)
		.columns(AGREEMENT_LEVEL_DATA.ID, AGREEMENT_LEVEL_DATA.DOMAIN, AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL, AGREEMENT_LEVEL_DATA.START_DATE, AGREEMENT_LEVEL_DATA.END_DATE, AGREEMENT_LEVEL_DATA.NAME, AGREEMENT_LEVEL_DATA.EXPRESSION )
		.select(dslContext.select(AGREEMENT_LEVEL_DATA.ID.mul(-1), AGREEMENT_LEVEL_DATA.DOMAIN, AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL.mul(-1), AGREEMENT_LEVEL_DATA.START_DATE, AGREEMENT_LEVEL_DATA.END_DATE, AGREEMENT_LEVEL_DATA.NAME, AGREEMENT_LEVEL_DATA.EXPRESSION )
				.from(AGREEMENT_LEVEL_DATA)
				.join(AGREEMENT_LEVEL).onKey(FK_AGREEMENT_LEVEL_DATA_AGREEMENT_LEVEL)
				.where(AGREEMENT_LEVEL.AGREEMENT.gt(0)).and(AGREEMENT_LEVEL.AGREEMENT.notIn(AGREEMENT_UPDATED)))
		.execute()
		;
		
		System.out.print("Moved to trash : " + agreements + " agreements (" + payments + " payments, " + extras + " extras, " + datas  + " datas ) ");
	}

	protected void checkTrash(DSLContext dslContext) {

		SelectConditionStep<Record1<Integer>> selectInTrash = DSL.select(AGREEMENT.ID.mul(-1)).from(AGREEMENT).where(AGREEMENT.ID.lt(0));
		

		List<AgreementRecord> agreements =
		dslContext
		.select()
		.from(AGREEMENT)
		.join(AGREEMENT_PAYMENT).onKey(FK_AGREEMENT_PAYMENT_AGREEMENT)
		.where(AGREEMENT.ID.gt(0))
		.and(AGREEMENT.ID.notIn(selectInTrash))
		.and(AGREEMENT.ID.notIn(AGREEMENT_UPDATED))
		.fetchInto(AGREEMENT)
		;
		if ( agreements.size() > 0 ) 
			throw new RuntimeException("Some agreements haven't got bakcup.");
		

	}

	protected void fixUpPayments(DSLContext dslContext) {
		
		Calendar calendar = Calendar.getInstance();
		// clean time
		calendar.set(Calendar.HOUR_OF_DAY,0);
		calendar.set(Calendar.MINUTE,0);
		calendar.set(Calendar.SECOND,0);
		calendar.set(Calendar.MILLISECOND,0);
		
		calendar.set(DAY_OF_MONTH, 1);
		Date firstDayOfMonth = new Date ( calendar.getTimeInMillis());
		calendar.set(DAY_OF_MONTH, calendar.getActualMaximum(DAY_OF_MONTH));
		Date lastDayOfMonth = new Date ( calendar.getTimeInMillis());
		
		Cursor<Record4<Integer, Integer, Date,Date>> agreements = 
		dslContext
		.select(AGREEMENT.ID, 
				AGREEMENT.DOMAIN,
				DSL.max(SALARY.START_DATE).as(SALARY.START_DATE),
				DSL.max(SALARY.END_DATE).as(SALARY.END_DATE))
		.from(AGREEMENT)
		.leftJoin(AGREEMENT_LEVEL).onKey(FK_AGREEMENT_LEVEL_AGREEMENT)
		.leftJoin(CONTRACT).onKey(FK_CONTRACT_AGREEMENT_LEVEL)
		.leftJoin(SALARY).onKey(FK_SALARY_CONTRACT)
		.where(AGREEMENT.ID.gt(0))
		.and(AGREEMENT.ID.notIn(AGREEMENT_UPDATED))
		.groupBy(AGREEMENT.ID)
		.fetchLazy();
		;
		
		while ( agreements.hasNext() ) {
			Record4<Integer, Integer, Date,Date> record = agreements.fetchNext();
			
			Date endDate = record.get(SALARY.END_DATE);
			Date startDate = record.get(SALARY.START_DATE);
			Integer agreement = record.get(AGREEMENT.ID);
			Integer domain = record.get(AGREEMENT.DOMAIN);
			
			if ( startDate == null || startDate.after(lastDayOfMonth))
				startDate = firstDayOfMonth;
			if ( endDate == null || endDate.after(lastDayOfMonth) )
				endDate = lastDayOfMonth;
			
			AgreementPaymentRecord activePayments [] =
			dslContext
			.select()
			.from(AGREEMENT_PAYMENT)
			.where(AGREEMENT_PAYMENT.AGREEMENT.eq(agreement))
			.and(AGREEMENT_PAYMENT.START_DATE.le(endDate))
			.and(AGREEMENT_PAYMENT.END_DATE.isNull().or(AGREEMENT_PAYMENT.END_DATE.ge(startDate)))
			.fetchInto(AGREEMENT_PAYMENT)
			.stream()
			.peek(p->p.setStartDate(EPOCH))
			.peek(p->p.setEndDate(FOREVER))
			.peek(p-> p.update())
			.toArray(AgreementPaymentRecord[]::new)
			;
			
			dslContext
			.select()
			.from(AGREEMENT_EXTRA)
			.join(AGREEMENT_PAYMENT).onKey(Keys.FK_AGREEMENT_EXTRA_AGREEMENT_PAYMENT)
			.where(AGREEMENT_EXTRA.AGREEMENT.eq(agreement))
			.and(AGREEMENT_PAYMENT.START_DATE.ne(EPOCH))
			.fetchInto(AGREEMENT_PAYMENT)
			.stream()
			.forEach(p-> 
			dslContext
			.update(AGREEMENT_EXTRA)
			.set(AGREEMENT_EXTRA.AGREEMENT_PAYMENT, getExtraCounterPart(p, activePayments))
			.where(AGREEMENT_EXTRA.AGREEMENT_PAYMENT.eq(p.getId()))
			.execute()
			)
			;
			int deleted =
			dslContext
			.delete(AGREEMENT_PAYMENT)
			.where(AGREEMENT_PAYMENT.START_DATE.ne(EPOCH))
			.and(AGREEMENT_PAYMENT.AGREEMENT.eq(agreement))
			.execute()
			;
			
			dslContext.insertInto(AGREEMENT_PAYMENT)
			.set(AGREEMENT_PAYMENT.DOMAIN,domain)
			.set(AGREEMENT_PAYMENT.AGREEMENT,agreement)
			.set(AGREEMENT_PAYMENT.TYPE, (byte) 1)
			.set(AGREEMENT_PAYMENT.START_DATE, EPOCH)
			.set(AGREEMENT_PAYMENT.SALARY_TYPE, (byte) 1)
			.set(AGREEMENT_PAYMENT.EXPRESSION, WARNNING)
			.set(AGREEMENT_PAYMENT.DESCRIPTION, "WARNNING")
			.execute();
			
		}
		
		
		
	
	}
	
	protected void cleanWarnPayments(DSLContext dslContext) {
		
		
		Cursor<Record1<Integer>> agreements = 
		dslContext
		.select(AGREEMENT.ID)
		.from(AGREEMENT)
		.join(AGREEMENT_PAYMENT).onKey(FK_AGREEMENT_PAYMENT_AGREEMENT)
		.where(AGREEMENT.ID.gt(0))
		.and(AGREEMENT_PAYMENT.START_DATE.eq(EPOCH))
		.and(AGREEMENT_PAYMENT.END_DATE.isNull())
		.groupBy(AGREEMENT.ID)
		.fetchLazy();
		;
		
		while ( agreements.hasNext() ) {
			Record1<Integer> record = agreements.fetchNext();
			
			Integer agreement = record.get(AGREEMENT.ID);
			
			dslContext
			.delete(AGREEMENT_PAYMENT)
			.where(AGREEMENT_PAYMENT.AGREEMENT.eq(agreement))
			.and(AGREEMENT_PAYMENT.EXPRESSION.eq(WARNNING))
			.execute()
			;
			
		}
		
		
		
	
	}

	private Integer getExtraCounterPart(AgreementPaymentRecord extraPayment, AgreementPaymentRecord agreementPayments[] ) {
		
		AgreementPaymentRecord sameMonthPayments [] =
		Arrays.stream(agreementPayments)
		.filter( p -> p.getMonth() != null )
		.filter(p -> p.getMonth().equals(extraPayment.getMonth()))
		.toArray(AgreementPaymentRecord[]::new)
		;

		if ( sameMonthPayments.length == 0 )
			return null;

		if ( sameMonthPayments.length == 1 )
			return sameMonthPayments[0].getId();
		
		AgreementPaymentRecord soundsEqualPayments [] =
		Arrays.stream(sameMonthPayments)
		.filter( p -> soundsEqual(extraPayment, p))
		.toArray(AgreementPaymentRecord[]::new)
		;
		
		if ( soundsEqualPayments.length == 0 )
			return null;

		if ( soundsEqualPayments.length == 1 )
			return sameMonthPayments[0].getId();
		
		return null;
	}
	
	private boolean soundsEqual(AgreementPaymentRecord p1, AgreementPaymentRecord p2) {
		String d1 = p1.getDescription();
		String d2 = p2.getDescription();
		
		if ( d1 == null && d2 == null )
			return eq(p1.getPaymentConcept(), p2.getPaymentConcept()); 
		
		if ( d1 == null || d2 == null )
			return false;
		
		d1 = d1.toLowerCase();
		d2 = d2.toLowerCase();
		d1.replaceAll("\\s+", "");
		
		return eq(d1, d2);
	}
	
	private boolean eq(Object obj1, Object obj2 ) {
		if ( obj1 == obj2 )
			return true;
		if ( obj1 == null )
			return false;
		if ( obj2 == null )
			return false;
		return obj1.equals(obj2);
	}
}
