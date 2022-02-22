package com.esferalia.aon.occam.impl.jooq.dao.fiscal;

import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;
import static com.esferalia.aon.jooq.tables.FsModelInvoice.FS_MODEL_INVOICE;

import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Collectors;

import org.jooq.BatchBindStep;
import org.jooq.exception.DataAccessException;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.FiscalModelDAO.FiscalModelFiller;
import com.esferalia.aon.watson.error.AonCoreException;


public class FiscalModelInvoiceDAO {
	
	protected FiscalModelInvoiceDAO() {
	}
	
	private static void log(AONContext ctx, String msg, Object ... params ) {
		ctx.log().info(msg,params);
	}
	
	public static <T extends FiscalModel> T save(AONContext ctx, T fm, Set<Integer> invoices) {
		try {
			ctx.checkWrite();
			delete(ctx, fm);
			insert(ctx, fm, invoices);
			return fm;
		} catch (DataAccessException t) {
			throw new AonCoreException(t.getCause()!=null?t.getCause().getMessage():t.getMessage());
		} catch (Exception t) {
			throw new AonCoreException(t.getMessage());
		}
	}

	private static <T extends FiscalModel> T insert(AONContext ctx, T fm, Set<Integer> invoices) {
		if (invoices != null && !invoices.isEmpty()) {
			BatchBindStep batch = ctx.getDslContext()
					.batch(ctx.getDslContext().insertInto(FS_MODEL_INVOICE
							,FS_MODEL_INVOICE.DOMAIN
							,FS_MODEL_INVOICE.FS_MODEL
							,FS_MODEL_INVOICE.INVOICE)
							.values((Integer) null,(Integer) null,(Integer) null));
			invoices.stream().forEach(inv -> batch.bind(fm.getDomain(),fm.getId(),inv));
			batch.execute();
			log(ctx,"\tINSERT FS_MODEL_INVOICE id: {0} Mod: {1} {2} rows",fm.getId(), fm.getModel(), batch.size());
		}
		return fm;
	}
	
	public static <T extends FiscalModel> void delete(AONContext ctx, T fm) {
		ctx.checkWrite();
		int count = ctx.getDslContext()
			.delete(FS_MODEL_INVOICE)
				.where(FS_MODEL_INVOICE.FS_MODEL.equal(fm.getId()))
			.execute();
		log(ctx,"DELETE FS_MODEL_INVOICE id: {0} Mod: {1} {2} rows",fm.getId(), fm.getModel(), count);
	}

	public static LinkedList<FiscalModel> isDeclared(AONContext ctx, Integer invoiceId) {
		return ctx.getDslContext()
			.select()
			.from(FS_MODEL_INVOICE)
			.leftOuterJoin(FS_MODEL).on(FS_MODEL.ID.equal(FS_MODEL_INVOICE.FS_MODEL))
			.where(FS_MODEL_INVOICE.INVOICE.eq(invoiceId))
			.fetch()
			.stream()
			.map(rec -> new FiscalModelFiller<FiscalModel>().apply(rec,FiscalModel::new))
			.collect(Collectors.toCollection(LinkedList::new))
		;
	}

}
