package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Fbatch.FBATCH;
import static com.esferalia.aon.jooq.tables.FbatchDetail.FBATCH_DETAIL;
import static com.esferalia.aon.jooq.tables.Finance.FINANCE;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jooq.Condition;
import org.jooq.Record;

import com.esferalia.aon.jooq.tables.records.FbatchDetailRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.finance.FBatchDetail;
import com.esferalia.aon.occam.api.model.finance.FBatchDetailFilter;
import com.esferalia.aon.occam.api.model.finance.FBatchDetailProperties;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.type.FinanceStatus;

public class FBatchDetailDAO {

	// ---------------------------------------------------------- FILTROS
	
	private static final FBatchDetailPropertiesDAO FBATCH_DETAIL_PROPERTIES = new FBatchDetailPropertiesDAO();
	protected static class FBatchDetailPropertiesDAO implements FBatchDetailProperties {

		public Condition[] getConditions(FBatchDetailFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null)
				return new Condition[0];

			return new Condition[] { filterDAO.getCondition() };
		}
		
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(FBATCH_DETAIL.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(FBATCH_DETAIL.DOMAIN);}
		@Override public Property<Integer> getFBatchProperty() {return new FilterDAO.PropertyDAO<Integer>(FBATCH_DETAIL.FBATCH);}
		@Override public Property<Integer> getFinanceProperty() {return new FilterDAO.PropertyDAO<Integer>(FBATCH_DETAIL.FINANCE);}
		@Override public Property<Double> getAmountProperty() {return new FilterDAO.PropertyDAO<Double>(FBATCH_DETAIL.AMOUNT);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<Byte>(FBATCH_DETAIL.STATUS);}
	}
	
	// ---------------------------------------------------------- LECTURA
	
	public static LinkedList<FBatchDetail> getFullList(AONContext ctx, FBatchDetailFilter filter) {
		ctx.checkRead();
		LinkedList<FBatchDetail> fBatchDeatils = ctx.getDslContext().select()
			.from(FBATCH_DETAIL)
			.where(FBATCH_DETAIL_PROPERTIES.getConditions(filter))
			.fetch()
			.stream()
			.map(new FBatchDetailFiller())
			.collect(Collectors.toCollection(LinkedList::new));
		
		fBatchDeatils.forEach(fBatchDeatil -> {
			fBatchDeatil.setFinance(FinanceDAO.getFinance(ctx, fBatchDeatil.getFinance().getId()));
		});
		
		return fBatchDeatils;
	}
	
	public static LinkedList<FBatchDetail> getList(AONContext ctx, FBatchDetailFilter filter) {
		ctx.checkRead();
		LinkedList<FBatchDetail> fBatchDeatils = ctx.getDslContext().select()
			.from(FBATCH_DETAIL)
			.where(FBATCH_DETAIL_PROPERTIES.getConditions(filter))
			.fetch()
			.stream()
			.map(new FBatchDetailFiller())
			.collect(Collectors.toCollection(LinkedList::new));
		
		return fBatchDeatils;
	}

	// ---------------------------------------------------------- ESCRITURA

	public static FBatchDetail save(AONContext ctx, FBatchDetail fbatchDetail) {
		if(fbatchDetail.getId() == null && fbatchDetail.isRemoved()) return fbatchDetail;
		return fbatchDetail.getId() == null ? insert(ctx, fbatchDetail) : update(ctx, fbatchDetail);
	}
	
	private static FBatchDetail insert(AONContext ctx, FBatchDetail fbatchDetail) {
		ctx.checkWrite();
		FbatchDetailRecord record = ctx.getDslContext().insertInto(FBATCH_DETAIL)
				.set(FBATCH_DETAIL.DOMAIN, ctx.getDomainId())
				.set(FBATCH_DETAIL.FBATCH, fbatchDetail.getFbatch())
				.set(FBATCH_DETAIL.FINANCE, fbatchDetail.getFinance() == null ? null : fbatchDetail.getFinance().getId())
				.set(FBATCH_DETAIL.AMOUNT, fbatchDetail.getAmount())
				.set(FBATCH_DETAIL.STATUS, fbatchDetail.getStatus())
				.set(FBATCH_DETAIL.CREATION_USER,ctx.getUser())
				.set(FBATCH_DETAIL.CREATION_DATE, new Timestamp( System.currentTimeMillis()))
				.returning()
				.fetchOne();
		
		ctx.log().debug("INSERT FBATCH_DETAIL ID: " + record.getValue(FBATCH.ID));
		
		if(fbatchDetail.getFinance() != null)
			ctx.getDslContext().update(FINANCE)
				.set(FINANCE.STATUS, (byte)FinanceStatus.BATCHED.ordinal())
				.where(FINANCE.ID.eq(fbatchDetail.getFinance().getId()))
				.execute();
		
		return new FBatchDetailFiller().apply(record); 
	}
	
	private static FBatchDetail update(AONContext ctx, FBatchDetail fbatchDetail) {
		ctx.checkWrite();
		if(fbatchDetail.isRemoved()) {
			ctx.getDslContext().deleteFrom(FBATCH_DETAIL)
				.where(FBATCH_DETAIL.ID.equal(fbatchDetail.getId()))
				.execute();
			
			if(fbatchDetail.getFinance() != null)
				ctx.getDslContext().update(FINANCE)
					.set(FINANCE.STATUS, (byte)FinanceStatus.PENDING.ordinal())
					.where(FINANCE.ID.eq(fbatchDetail.getFinance().getId()))
					.execute();
		
			ctx.log().debug("DELETE FBATCH_DETAIL ID: " + fbatchDetail.getId());
		
		} else {
			ctx.getDslContext().update(FBATCH_DETAIL)
				.set(FBATCH_DETAIL.FBATCH, fbatchDetail.getFbatch())
				.set(FBATCH_DETAIL.FINANCE, fbatchDetail.getFinance() == null ? null : fbatchDetail.getFinance().getId())
				.set(FBATCH_DETAIL.AMOUNT, fbatchDetail.getAmount())
				.set(FBATCH_DETAIL.STATUS, fbatchDetail.getStatus())
				.set(FBATCH_DETAIL.MODIFICATION_USER,ctx.getUser())
				.set(FBATCH_DETAIL.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()))
				.where(FBATCH_DETAIL.ID.equal(fbatchDetail.getId()))
				.execute();
		
			ctx.log().debug("UPDATE FBATCH_DETAIL ID: " + fbatchDetail.getId());
		
		}
		return fbatchDetail;
	}
	
	// ---------------------------------------------------------- BORRADO
	
	public static void delete(AONContext ctx, Integer id) {
		ctx.checkWrite();
		
		FbatchDetailRecord fbatchDetail = ctx.getDslContext()
			.selectFrom(FBATCH_DETAIL)
			.where(FBATCH_DETAIL.ID.equal(id))
			.fetchOne();
		
		if(fbatchDetail.getFinance() != null)
			ctx.getDslContext().update(FINANCE)
				.set(FINANCE.STATUS, (byte)FinanceStatus.PENDING.ordinal())
				.where(FINANCE.ID.eq(fbatchDetail.getFinance()))
				.execute();
		
		ctx.getDslContext()
			.delete(FBATCH_DETAIL)
			.where(FBATCH_DETAIL.ID.equal(id))
			.execute();
		
		ctx.log().debug("DELETE FBATCH_DETAIL ID " + id);
	}
	
	// ---------------------------------------------------------- MAP
	
	public static class FBatchDetailFiller implements Function<Record, FBatchDetail> {
		@Override
		public FBatchDetail apply(Record record) {
			return new FBatchDetail()
				.setId(record.getValue(FBATCH_DETAIL.ID))
				.setDomain(record.getValue(FBATCH_DETAIL.DOMAIN))
				.setFbatch(record.getValue(FBATCH_DETAIL.FBATCH))
				.setFinance(new Finance().setId(record.getValue(FBATCH_DETAIL.FINANCE)))
				.setAmount(record.getValue(FBATCH_DETAIL.AMOUNT))
				.setStatus(record.getValue(FBATCH_DETAIL.STATUS))
				.setCreationUser(record.getValue(FBATCH_DETAIL.CREATION_USER))
				.setCreationDate(record.getValue(FBATCH_DETAIL.CREATION_DATE))
				.setModificationUser(record.getValue(FBATCH_DETAIL.MODIFICATION_USER))
				.setModificationDate(record.getValue(FBATCH_DETAIL.MODIFICATION_DATE))
				;
		}
			
	}


}
