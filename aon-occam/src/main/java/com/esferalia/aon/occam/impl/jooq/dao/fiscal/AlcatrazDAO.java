package com.esferalia.aon.occam.impl.jooq.dao.fiscal;

import static com.esferalia.aon.jooq.tables.Alcatraz.ALCATRAZ;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Finance.FINANCE;
import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.PayMethod.PAY_METHOD;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.jooq.BatchBindStep;
import org.jooq.Condition;
import org.jooq.exception.DataAccessException;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.invoice.InvoiceFilter;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.FiscalModelDAO.FiscalModelFiller;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AlcatrazDAO {
	
	public static class Alcatraz implements Serializable {
		
		private static final long serialVersionUID = 3376369761199324699L;
		
		private Integer invoice;
		private Integer finance;
		private Integer financeTracking;
		private Integer salary;
		
		public Integer getInvoice() {
			return invoice;
		}
		public Alcatraz setInvoice(Integer invoice) {
			this.invoice = invoice;
			return this;
		}
		
		public Integer getFinance() {
			return finance;
		}
		public Alcatraz setFinance(Integer finance) {
			this.finance = finance;
			return this;
		}
		
		public Integer getSalary() {
			return salary;
		}
		public Alcatraz setSalary(Integer salary) {
			this.salary = salary;
			return this;
		}
		
		public Integer getFinanceTracking() {
			return financeTracking;
		}
		public Alcatraz setFinanceTracking(Integer financeTracking) {
			this.financeTracking = financeTracking;
			return this;
		}
		
		@Override
		public boolean equals(Object o) {
		    if (o == this) return true;
		    if (!(o instanceof Alcatraz)) return false;
		    Alcatraz other = (Alcatraz) o;
		    return AonNumberUtils.equals(this.invoice, other.invoice)
		    	&& AonNumberUtils.equals(this.finance, other.finance)
		    	&& AonNumberUtils.equals(this.financeTracking, other.financeTracking);
		}
		
		@Override
		public final int hashCode() {
		    int result = 17;
		    if (this.invoice != null) {
		        result = 31 * result + this.invoice.hashCode();
		    }
		    if (this.finance != null) {
		        result = 31 * result + this.finance.hashCode();
		    }
		    if (this.financeTracking != null) {
		        result = 31 * result + this.financeTracking.hashCode();
		    }
		    return result;
		}		
	}

	protected AlcatrazDAO() {
	}
	private static void log(AONContext ctx, String msg, Object ... params ) {
		ctx.log().debug(msg,params);
	}
	
	public static <T extends FiscalModel> T saveModelInvoices(AONContext ctx, T fm, Set<Alcatraz> invoices) {
		try {
			ctx.checkWrite();
			insertModelInvoices(ctx, fm, invoices);
			return fm;
		} catch (DataAccessException t) {
			throw new AonCoreException(t.getCause()!=null?t.getCause().getMessage():t.getMessage());
		} catch (Exception t) {
			throw new AonCoreException(t.getMessage());
		}
	}

	public static <T extends FiscalModel> T saveModelSalaries(AONContext ctx, T fm, Set<Integer> invoices) {
		try {
			ctx.checkWrite();
			insertModelSalaries(ctx, fm, invoices);
			return fm;
		} catch (DataAccessException t) {
			throw new AonCoreException(t.getCause()!=null?t.getCause().getMessage():t.getMessage());
		} catch (Exception t) {
			throw new AonCoreException(t.getMessage());
		}
	}

	private static <T extends FiscalModel> T insertModelInvoices(AONContext ctx, T fm, Set<Alcatraz> invoices) {
		
		if (invoices != null && !invoices.isEmpty()) {
			BatchBindStep batch = ctx.getDslContext()
					.batch(ctx.getDslContext().insertInto(ALCATRAZ
							,ALCATRAZ.DOMAIN
							,ALCATRAZ.FS_MODEL
							,ALCATRAZ.INVOICE
							,ALCATRAZ.FINANCE
							,ALCATRAZ.FINANCE_TRACKING)
							.values(
							 (Integer) null
							,(Integer) null
							,(Integer) null
							,(Integer) null
							,(Integer) null
							));
			invoices.stream().forEach(alc -> batch.bind(
					fm.getDomain()
					,fm.getId()
					,alc.getInvoice()
					,alc.getFinance()
					,alc.getFinanceTracking()));
			batch.execute();
			log(ctx,"\tINSERT ALCATRAZ id: {0} Mod: {1} {2} rows",fm.getId(), fm.getModel(), batch.size());
		}
		return fm;
	}
	
	private static <T extends FiscalModel> T insertModelSalaries(AONContext ctx, T fm, Set<Integer> salaries) {
		if (salaries != null && !salaries.isEmpty()) {
			BatchBindStep batch = ctx.getDslContext()
					.batch(ctx.getDslContext().insertInto(ALCATRAZ
							,ALCATRAZ.DOMAIN
							,ALCATRAZ.FS_MODEL
							,ALCATRAZ.SALARY)
							.values((Integer) null,(Integer) null,(Integer) null));
			salaries.stream().forEach(inv -> batch.bind(fm.getDomain(),fm.getId(),inv));
			batch.execute();
			log(ctx,"\tINSERT ALCATRAZ id: {0} Mod: {1} {2} rows",fm.getId(), fm.getModel(), batch.size());
		}
		return fm;
	}

	public static <T extends FiscalModel> void deleteFiscalModel(AONContext ctx, T fm) {
		ctx.checkWrite();
		int count = ctx.getDslContext()
			.delete(ALCATRAZ)
				.where(ALCATRAZ.FS_MODEL.equal(fm.getId()))
			.execute();
		log(ctx,"DELETE ALCATRAZ id: {0} Mod: {1} {2} rows",fm.getId(), fm.getModel(), count);
	}

	public static List<FiscalModel> isInvoiceDeclared(AONContext ctx, Integer invoiceId) {
		return ctx.getDslContext()
			.select()
			.from(ALCATRAZ)
			.leftOuterJoin(FS_MODEL).on(FS_MODEL.ID.equal(ALCATRAZ.FS_MODEL))
			.leftOuterJoin(DOMAIN).on(DOMAIN.ID.equal(FS_MODEL.DOMAIN))
			.leftOuterJoin(FINANCE).on(FINANCE.ID.equal(FS_MODEL.FINANCE))
			.leftOuterJoin(REGISTRY).on(REGISTRY.ID.equal(FINANCE.REGISTRY))
			.leftOuterJoin(SCOPE).on(FINANCE.SCOPE.equal(SCOPE.ID))
			.leftOuterJoin(PAY_METHOD).on(FINANCE.PAY_METHOD.equal(PAY_METHOD.ID))
			.where(ALCATRAZ.INVOICE.eq(invoiceId))
			.fetch()
			.stream()
			.map(rec -> new FiscalModelFiller<FiscalModel>().apply(rec,FiscalModel::new))
			.collect(Collectors.toCollection(LinkedList::new))
		;
	}
	
	public static List<FiscalModel> isSalaryDeclared(AONContext ctx, Integer salaryId) {
		return ctx.getDslContext()
			.select()
			.from(ALCATRAZ)
			.leftOuterJoin(FS_MODEL).on(FS_MODEL.ID.equal(ALCATRAZ.FS_MODEL))
			.leftOuterJoin(DOMAIN).on(DOMAIN.ID.equal(FS_MODEL.DOMAIN))
			.leftOuterJoin(FINANCE).on(FINANCE.ID.equal(FS_MODEL.FINANCE))
			.leftOuterJoin(REGISTRY).on(REGISTRY.ID.equal(FINANCE.REGISTRY))
			.leftOuterJoin(SCOPE).on(FINANCE.SCOPE.equal(SCOPE.ID))
			.leftOuterJoin(PAY_METHOD).on(FINANCE.PAY_METHOD.equal(PAY_METHOD.ID))
			.where(ALCATRAZ.SALARY.eq(salaryId))
			.fetch()
			.stream()
			.map(rec -> new FiscalModelFiller<FiscalModel>().apply(rec,FiscalModel::new))
			.collect(Collectors.toCollection(LinkedList::new))
		;
	}
	
	public static boolean hasAlcatrazBound(AONContext ctx, Integer fiscalModelId) {
		return ctx.getDslContext()
			.select()
			.from(ALCATRAZ)
			.where(ALCATRAZ.FS_MODEL.eq(fiscalModelId))
			.limit(1)
			.fetch()
			.stream()
			.findAny()
			.isPresent();
	}
	
	public static List<FiscalModel> isTrackingDeclared(AONContext ctx, Integer id) {
		return ctx.getDslContext()
			.select()
			.from(ALCATRAZ)
			.leftOuterJoin(FS_MODEL).on(FS_MODEL.ID.equal(ALCATRAZ.FS_MODEL))
			.leftOuterJoin(DOMAIN).on(DOMAIN.ID.equal(FS_MODEL.DOMAIN))
			.leftOuterJoin(FINANCE).on(FINANCE.ID.equal(FS_MODEL.FINANCE))
			.leftOuterJoin(REGISTRY).on(REGISTRY.ID.equal(FINANCE.REGISTRY))
			.leftOuterJoin(SCOPE).on(FINANCE.SCOPE.equal(SCOPE.ID))
			.leftOuterJoin(PAY_METHOD).on(FINANCE.PAY_METHOD.equal(PAY_METHOD.ID))
			.where(ALCATRAZ.FINANCE_TRACKING.eq(id))
			.fetch()
			.stream()
			.map(rec -> new FiscalModelFiller<FiscalModel>().apply(rec,FiscalModel::new))
			.collect(Collectors.toCollection(LinkedList::new))
		;
	}
	
	// Get invoice from FsModel
	
	public static List<Alcatraz> getAlcatrazInvoicesByFsModel(CloseableAONContext ctx, Integer fsModel, InvoiceFilter invoiceFilter) {
		Condition condition = parseCondition(ctx, invoiceFilter);
		
		return ctx.getDslContext()
				.select()
				.from(ALCATRAZ)
				.join(INVOICE).on(INVOICE.ID.eq(ALCATRAZ.INVOICE))
				.join(REGISTRY).on(REGISTRY.ID.eq(INVOICE.REGISTRY))
				.where(ALCATRAZ.FS_MODEL.eq(fsModel))
				.and(ALCATRAZ.INVOICE.isNotNull())
				.and(condition)
				.orderBy(INVOICE.ISSUE_DATE, REGISTRY.NAME)
				.limit(invoiceFilter.getPerPage())
				.offset(invoiceFilter.getPage())
				.fetch()
				.stream()
				.map(rec -> new Alcatraz()
						.setInvoice(rec.get(ALCATRAZ.INVOICE))
						.setSalary(rec.get(ALCATRAZ.SALARY))
				).collect(Collectors.toCollection(LinkedList::new))
			;
	}
	
	public static Integer getAlcatrazInvoicesCountByFsModel(AONContext ctx, Integer fsModel, InvoiceFilter invoiceFilter) {
		Condition condition = parseCondition(ctx, invoiceFilter);
		
		return ctx.getDslContext()
			.selectCount()
			.from(ALCATRAZ)
			.join(INVOICE).on(INVOICE.ID.eq(ALCATRAZ.INVOICE))
			.join(REGISTRY).on(REGISTRY.ID.eq(INVOICE.REGISTRY))
			.where(ALCATRAZ.FS_MODEL.eq(fsModel))
			.and(ALCATRAZ.INVOICE.isNotNull())
			.and(condition)
			.fetchOne()
			.value1();
	}
	
	private static Condition parseCondition(AONContext ctx, InvoiceFilter invoiceFilter) {
		Condition condition = ALCATRAZ.DOMAIN.eq(ctx.getDomainId());
		
		if(null != invoiceFilter.getTypesByte())
			condition = condition.and(INVOICE.TYPE.in(invoiceFilter.getTypesByte()));
		
		if(null != invoiceFilter.getFrom())
			condition = condition.and(INVOICE.ISSUE_DATE.ge(parseToSqlDate(invoiceFilter.getFrom())));
		
		if(null != invoiceFilter.getTo())
			condition = condition.and(INVOICE.ISSUE_DATE.le(parseToSqlDate(invoiceFilter.getTo())));
		
		if(AonStringUtils.isNotBlank(invoiceFilter.getDescription())) {
			condition = condition.and(
					INVOICE.REFERENCE_CODE.like("%" + invoiceFilter.getDescription() + "%")
					.or(INVOICE.RNAME.like("%" + invoiceFilter.getDescription() + "%"))
					.or(REGISTRY.NAME.like("%" + invoiceFilter.getDescription() + "%"))
					.or(INVOICE.SERIES.like("%" + invoiceFilter.getDescription() + "%"))
					.or(INVOICE.RDOCUMENT.like("%" + invoiceFilter.getDescription() + "%"))
					.or(REGISTRY.DOCUMENT.like("%" + invoiceFilter.getDescription() + "%"))
			);
		}
		
		return condition;
	}
	
	public static List<Alcatraz> getAlcatrazSalariesByFsModel(AONContext ctx, Integer fsModel, InvoiceFilter invoiceFilter) {
		Condition condition = parseSalaryCondition(ctx, invoiceFilter);
		
		return ctx.getDslContext()
			.select()
			.from(ALCATRAZ)
			.join(SALARY).on(SALARY.ID.eq(ALCATRAZ.SALARY))
			.join(CONTRACT).on(CONTRACT.ID.eq(SALARY.CONTRACT))
			.join(REGISTRY).on(REGISTRY.ID.eq(CONTRACT.PERSON))
			.where(ALCATRAZ.FS_MODEL.eq(fsModel))
			.and(ALCATRAZ.SALARY.isNotNull())
			.and(condition)
			.limit(invoiceFilter.getPerPage())
			.offset(invoiceFilter.getPage())
			.fetch()
			.stream()
			.map(rec -> new Alcatraz()
					.setInvoice(rec.get(ALCATRAZ.INVOICE))
					.setSalary(rec.get(ALCATRAZ.SALARY))
			)
			.collect(Collectors.toCollection(LinkedList::new))
		;
	}
	
	public static Integer getAlcatrazSalariesCountByFsModel(AONContext ctx, Integer fsModel, InvoiceFilter invoiceFilter) {
		Condition condition = parseSalaryCondition(ctx, invoiceFilter);
		
		return ctx.getDslContext()
			.selectCount()
			.from(ALCATRAZ)
			.join(SALARY).on(SALARY.ID.eq(ALCATRAZ.SALARY))
			.join(CONTRACT).on(CONTRACT.ID.eq(SALARY.CONTRACT))
			.join(REGISTRY).on(REGISTRY.ID.eq(CONTRACT.PERSON))
			.where(ALCATRAZ.FS_MODEL.eq(fsModel))
			.and(ALCATRAZ.SALARY.isNotNull())
			.and(condition)
			.fetchOne()
			.value1();
	}
	
	private static Condition parseSalaryCondition(AONContext ctx, InvoiceFilter invoiceFilter) {
		Condition condition = ALCATRAZ.DOMAIN.eq(ctx.getDomainId());
		
		if(null != invoiceFilter.getFrom())
			condition = condition.and(SALARY.ISSUE_DATE.ge(parseToSqlDate(invoiceFilter.getFrom())));
		
		if(null != invoiceFilter.getTo())
			condition = condition.and(SALARY.ISSUE_DATE.le(parseToSqlDate(invoiceFilter.getTo())));
		
		if(AonStringUtils.isNotBlank(invoiceFilter.getDescription())) {
			condition = condition.and(
					SALARY.EMPLOYEE_NAME.like("%" + invoiceFilter.getDescription() + "%")
					.or(SALARY.EMPLOYEE_DOCUMENT.like("%" + invoiceFilter.getDescription() + "%"))
					.or(REGISTRY.NAME.like("%" + invoiceFilter.getDescription() + "%"))
					.or(REGISTRY.DOCUMENT.like("%" + invoiceFilter.getDescription() + "%"))
			);
		}
		
		return condition;
	}
	
	private static java.sql.Date parseToSqlDate(Date date){
		if(null == date) return null;
		return new java.sql.Date(date.getTime());
	}
	
}
