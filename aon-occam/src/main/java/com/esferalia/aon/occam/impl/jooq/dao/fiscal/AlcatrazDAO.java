package com.esferalia.aon.occam.impl.jooq.dao.fiscal;

import static com.esferalia.aon.jooq.tables.Alcatraz.ALCATRAZ;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Finance.FINANCE;
import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;
import static com.esferalia.aon.jooq.tables.PayMethod.PAY_METHOD;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.jooq.BatchBindStep;
import org.jooq.exception.DataAccessException;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.FiscalModelDAO.FiscalModelFiller;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonNumberUtils;

public class AlcatrazDAO {
	
	public static class Alcatraz implements Serializable {
		
		private static final long serialVersionUID = 3376369761199324699L;
		
		private Integer invoice;
		private Integer finance;
		private Integer financeTracking;
		
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
	
}
