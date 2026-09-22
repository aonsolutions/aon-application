package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.FbatchDetail.FBATCH_DETAIL;
import static com.esferalia.aon.jooq.tables.Finance.FINANCE;

import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jooq.Condition;
import org.jooq.Record;

import com.esferalia.aon.jooq.tables.records.FbatchDetailRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.finance.FBatch;
import com.esferalia.aon.occam.api.model.finance.FBatchDetail;
import com.esferalia.aon.occam.api.model.finance.FBatchDetailFilter;
import com.esferalia.aon.occam.api.model.finance.FBatchDetailProperties;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.FinanceTracking;
import com.esferalia.aon.occam.api.model.type.FBatchType;
import com.esferalia.aon.occam.api.model.type.FinanceStatus;
import com.esferalia.aon.occam.api.model.type.FinanceTrackingType;
import com.esferalia.aon.watson.error.AonCoreException;

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
		
		if (fbatchDetail.getFinance() == null || fbatchDetail.getFinance().getId() == null)
			throw new AonCoreException("Detalle de remesa sin vencimiento asociado");

		// El importe y la pertenencia al dominio los decide la BD, no el cliente.
		Record financeRec = ctx.getDslContext().select(FINANCE.AMOUNT, FINANCE.DUE_DATE)
			.from(FINANCE)
			.where(FINANCE.ID.eq(fbatchDetail.getFinance().getId()))
			.and(FINANCE.DOMAIN.eq(ctx.getDomainId()))
			.fetchOne();

		if (financeRec == null)
			throw new AonCoreException("El vencimiento " + fbatchDetail.getFinance().getId() + " no existe en este dominio");

		final Double amount = financeRec.get(FINANCE.AMOUNT);
		
		FbatchDetailRecord record = ctx.getDslContext().insertInto(FBATCH_DETAIL)
				.set(FBATCH_DETAIL.DOMAIN, ctx.getDomainId())
				.set(FBATCH_DETAIL.FBATCH, fbatchDetail.getFbatch())
				.set(FBATCH_DETAIL.FINANCE, fbatchDetail.getFinance() == null ? null : fbatchDetail.getFinance().getId())
				.set(FBATCH_DETAIL.AMOUNT, amount)
				.set(FBATCH_DETAIL.STATUS, fbatchDetail.getStatus())
				.set(FBATCH_DETAIL.CREATION_USER,ctx.getUser())
				.set(FBATCH_DETAIL.CREATION_DATE, new Timestamp( System.currentTimeMillis()))
				.returning()
				.fetchOne();
		
		ctx.log().debug("INSERT FBATCH_DETAIL ID: " + record.getValue(FBATCH_DETAIL.ID));
		

		FBatch fbatch = FBatchDAO.get(ctx, fbatchDetail.getFbatch());
		
		if (FBatchType.dueDateLimitedByIssueDate(fbatch.getType())
		        && financeRec.get(FINANCE.DUE_DATE).after(fbatch.getIssueDate()))
		    throw new AonCoreException("El vencimiento " + fbatchDetail.getFinance().getId()
		            + " vence despues de la fecha de la remesa y no puede incluirse en un 19-14");
		
		if(fbatchDetail.getFinance() != null) {
			
			FinanceTrackingDAO.insert(ctx, 
					new FinanceTracking()
						.setDomain(fbatchDetail.getFinance().getDomain())
						.setFinance(fbatchDetail.getFinance())
						.setTrackingDate(new Date())
						.setType(FinanceTrackingType.BATCHED)
						.setDescription("Remesa : " + fbatch.getId() + " - " + fbatch.getDescription())
						.setAmount(amount)
			);
			
			
			ctx.getDslContext().update(FINANCE)
				.set(FINANCE.STATUS, (byte)FinanceStatus.BATCHED.ordinal())
				.where(FINANCE.ID.eq(fbatchDetail.getFinance().getId()))
				.and(FINANCE.DOMAIN.eq(ctx.getDomainId()))
				.execute();
		}
		
		return new FBatchDetailFiller().apply(record); 
	}
	
	private static FBatchDetail update(AONContext ctx, FBatchDetail fbatchDetail) {
		ctx.checkWrite();
		if(fbatchDetail.isRemoved()) {
			
			if(fbatchDetail.getFinance() != null) {
				FinanceTracking lastTracking = FinanceTrackingDAO.getLastTracking(ctx, fbatchDetail.getFinance().getId());
				if(null != lastTracking && lastTracking.getType().equals(FinanceTrackingType.BATCHED))
					FinanceTrackingDAO.delete(ctx, lastTracking);
				
				FinanceTracking previusLastTracking = FinanceTrackingDAO.getLastTracking(ctx, fbatchDetail.getFinance().getId());
				FinanceStatus newFinanceStatus = FinanceStatus.PENDING;
				
				if(null != previusLastTracking && null != previusLastTracking.getId()) {
					FinanceTrackingType type = previusLastTracking.getType();
					newFinanceStatus = type.getFinanceStatus();
				} 
				
				ctx.getDslContext().update(FINANCE)
					.set(FINANCE.STATUS, newFinanceStatus.value())
					.where(FINANCE.ID.eq(fbatchDetail.getFinance().getId()))
					.and(FINANCE.DOMAIN.eq(ctx.getDomainId()))
					.execute();
			}
			
			ctx.getDslContext().deleteFrom(FBATCH_DETAIL)
				.where(FBATCH_DETAIL.ID.equal(fbatchDetail.getId()))
				.and(FBATCH_DETAIL.DOMAIN.eq(ctx.getDomainId()))
				.execute();
			
			ctx.log().debug("DELETE FBATCH_DETAIL ID: " + fbatchDetail.getId());
		
		} else {
			ctx.getDslContext().update(FBATCH_DETAIL)
//				.set(FBATCH_DETAIL.FBATCH, fbatchDetail.getFbatch())
//				.set(FBATCH_DETAIL.FINANCE, fbatchDetail.getFinance() == null ? null : fbatchDetail.getFinance().getId())
//				.set(FBATCH_DETAIL.AMOUNT, fbatchDetail.getAmount())
				.set(FBATCH_DETAIL.STATUS, fbatchDetail.getStatus())
				.set(FBATCH_DETAIL.MODIFICATION_USER,ctx.getUser())
				.set(FBATCH_DETAIL.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()))
				.where(FBATCH_DETAIL.ID.equal(fbatchDetail.getId()))
				.and(FBATCH_DETAIL.DOMAIN.eq(ctx.getDomainId()))
				.execute();
		
			ctx.log().debug("UPDATE FBATCH_DETAIL ID: " + fbatchDetail.getId());
		
		}
		return fbatchDetail;
	}
	
	// ---------------------------------------------------------- BORRADO
	
	public static void delete(AONContext ctx, Integer id) {
		ctx.checkWrite();
		
		Optional<FBatchDetail> fbatchDetail = getList(ctx, f -> f.getIdProperty().eq(id)).stream().findFirst();
		
		if(fbatchDetail.isPresent()) {
			if(fbatchDetail.get().getFinance() != null) {
				FinanceTracking lastTracking = FinanceTrackingDAO.getLastTracking(ctx, fbatchDetail.get().getFinance().getId());
				if(null != lastTracking && FinanceTrackingType.BATCHED.equals(lastTracking.getType()))
 					FinanceTrackingDAO.delete(ctx, lastTracking);
				
				FinanceTracking previusLastTracking = FinanceTrackingDAO.getLastTracking(ctx, fbatchDetail.get().getFinance().getId());
				FinanceStatus newFinanceStatus = FinanceStatus.PENDING;
				
				if(null != previusLastTracking && null != previusLastTracking.getId()) {
					FinanceTrackingType type = previusLastTracking.getType();
					newFinanceStatus = type.getFinanceStatus();
				} 
				
				ctx.getDslContext().update(FINANCE)
					.set(FINANCE.STATUS, newFinanceStatus.value())
					.where(FINANCE.ID.eq(fbatchDetail.get().getFinance().getId()))
					.and(FINANCE.DOMAIN.eq(ctx.getDomainId()))
					.execute();
			}
		}
		
		ctx.getDslContext()
			.delete(FBATCH_DETAIL)
			.where(FBATCH_DETAIL.ID.equal(id))
			.and(FBATCH_DETAIL.DOMAIN.eq(ctx.getDomainId()))
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
