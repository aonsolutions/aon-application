package com.esferalia.aon.occam.impl.jooq.dao.fiscal;

import static com.esferalia.aon.jooq.tables.Alcatraz.ALCATRAZ;
import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;

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

public class AlcatrazDAO {
	
	protected AlcatrazDAO() {
	}
	private static void log(AONContext ctx, String msg, Object ... params ) {
		ctx.log().debug(msg,params);
	}
	
	public static <T extends FiscalModel> T saveModelInvoices(AONContext ctx, T fm, Set<Integer> invoices) {
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

	private static <T extends FiscalModel> T insertModelInvoices(AONContext ctx, T fm, Set<Integer> invoices) {
		
		if (invoices != null && !invoices.isEmpty()) {
			BatchBindStep batch = ctx.getDslContext()
					.batch(ctx.getDslContext().insertInto(ALCATRAZ
							,ALCATRAZ.DOMAIN
							,ALCATRAZ.FS_MODEL
							,ALCATRAZ.INVOICE)
							.values((Integer) null,(Integer) null,(Integer) null));
			invoices.stream().forEach(inv -> batch.bind(fm.getDomain(),fm.getId(),inv));
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
			.where(ALCATRAZ.INVOICE.eq(invoiceId))
			.fetch()
			.stream()
			.map(rec -> new FiscalModelFiller<FiscalModel>().apply(rec,FiscalModel::new))
			.collect(Collectors.toCollection(LinkedList::new))
		;
	}

}
