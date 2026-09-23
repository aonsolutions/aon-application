package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.AccountEntryFbatch.ACCOUNT_ENTRY_FBATCH;
import static com.esferalia.aon.jooq.tables.Fbatch.FBATCH;
import static com.esferalia.aon.jooq.tables.Rattach.RATTACH;
import static com.esferalia.aon.jooq.tables.Rbank.RBANK;

import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SortField;

import com.esferalia.aon.jooq.tables.records.FbatchRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.finance.FBatch;
import com.esferalia.aon.occam.api.model.finance.FBatchFilter;
import com.esferalia.aon.occam.api.model.finance.FBatchProperties;
import com.esferalia.aon.occam.api.model.type.FBatchStatus;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryBankDAO.RegistryBankFiller;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;

public class FBatchDAO {

	// ---------------------------------------------------------- FILTROS
	
	private static final FBatchPropertiesDAO FBATCH_PROPERTIES = new FBatchPropertiesDAO();
	protected static class FBatchPropertiesDAO implements FBatchProperties {

		public Condition[] getConditions(FBatchFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null)
				return new Condition[0];

			return new Condition[] { filterDAO.getCondition() };
		}
		
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(FBATCH.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(FBATCH.DOMAIN);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<String>(FBATCH.DESCRIPTION);}
		@Override public Property<Date> getIssueDateProperty() {return new FilterDAO.DatePropertyDAO(FBATCH.ISSUE_DATE);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<Byte>(FBATCH.TYPE);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<Byte>(FBATCH.STATUS);}
		@Override public Property<Integer> getRBankProperty() {return new FilterDAO.PropertyDAO<Integer>(FBATCH.RBANK);}
		@Override public Property<Integer> getBankStatementLinkProperty() {return new FilterDAO.PropertyDAO<Integer>(FBATCH.BANK_STATEMENT_LINK);}
		@Override public Property<Byte> getPaymentProperty() {return new FilterDAO.PropertyDAO<Byte>(FBATCH.PAYMENT);}
		@Override public Property<Byte> getConfidentialProperty() {return new FilterDAO.PropertyDAO<Byte>(FBATCH.SECURITY_LEVEL);}
		@Override public Property<Integer> getRAttachProperty() {return new FilterDAO.PropertyDAO<Integer>(FBATCH.RATTACH);}
	}
	
	// ---------------------------------------------------------- ORDER
	
	public static enum FBatchOrder {
		ISSUE_DATE ( FBATCH.ISSUE_DATE.desc(), FBATCH.ID.asc() )
		;
		
		private SortField<?>[] fields;
		
		private FBatchOrder( SortField<?> ...fields) {
			this.fields = fields;
		}
		public SortField<?>[] getFields() {
			return fields;
		}
		
		public static FBatchOrder safeEnum(int order) {
			if (order < 0 || order > FBatchOrder.values().length) return ISSUE_DATE;
			
			return FBatchOrder.values()[order];
		}
	}
	
	// ---------------------------------------------------------- LECTURA
	
	public static LinkedList<FBatch> getList(AONContext ctx, FBatchFilter filter, int offset, int limit) {
		return getList(ctx, filter, FBatchOrder.ISSUE_DATE, offset, limit);
	}
	
	public static LinkedList<FBatch> getList(AONContext ctx, FBatchFilter filter, FBatchOrder orderBy, int offset, int limit) {
		ctx.checkRead();
		LinkedList<FBatch> fBatches = ctx.getDslContext().select()
			.from(FBATCH)
			.leftOuterJoin(RBANK).on(RBANK.ID.eq(FBATCH.RBANK))
			.where(FBATCH_PROPERTIES.getConditions(filter))
			.orderBy(orderBy.getFields())
			.limit(offset, limit)
			.fetch()
			.stream()
			.map(new FBatchFiller())
			.collect(Collectors.toCollection(LinkedList::new));
		
		fBatches.forEach(fBatch -> fBatch.setBatchDetails(FBatchDetailDAO.getList(ctx, f -> f.getFBatchProperty().eq(fBatch.getId()))));
		
		return fBatches;
	}
	
	public static FBatch get(AONContext ctx, Integer fbatchId) {
		ctx.checkRead();
		if (fbatchId == null) throw new AonCoreException(AonError.EMPTY_DATA.format("ID Remesa"));
		Record record = ctx.getDslContext().select()
			.from(FBATCH)
			.leftOuterJoin(RBANK).on(RBANK.ID.eq(FBATCH.RBANK))
			.where(FBATCH.ID.eq(fbatchId))
			.and(FBATCH.DOMAIN.eq(ctx.getDomainId()))
			.fetchOne();
		
		if (record == null)
			throw new AonCoreException(AonError.NOT_EXIST.format("Remesa con id " + fbatchId));			
		
		FBatch fBatch = new FBatchFiller().apply(record);
		
		fBatch.setBatchDetails(FBatchDetailDAO.getFullList(ctx, f -> f.getFBatchProperty().eq(fBatch.getId())));
		
		return fBatch;
	}

	// ---------------------------------------------------------- ESCRITURA

	public static FBatch save(AONContext ctx, FBatch fbatch) {
		return fbatch.getId() == null ? insert(ctx, fbatch) : update(ctx, fbatch);
	}
	
	private static FBatch insert(AONContext ctx, FBatch fbatch) {
		ctx.checkWrite();
		FbatchRecord record = ctx.getDslContext().insertInto(FBATCH)
				.set(FBATCH.DOMAIN, ctx.getDomainId())
				.set(FBATCH.DESCRIPTION, fbatch.getDescription())
				.set(FBATCH.ISSUE_DATE, AonDateUtils.toSql(fbatch.getIssueDate()))
				.set(FBATCH.TYPE, fbatch.getType())
				.set(FBATCH.STATUS, (byte) fbatch.getStatus().ordinal())
				.set(FBATCH.RBANK, fbatch.getRbank() == null ? null : fbatch.getRbank().getId())
				.set(FBATCH.BANK_STATEMENT_LINK, fbatch.getBankStatementLink())
				.set(FBATCH.PAYMENT, fbatch.getPayment())
				.set(FBATCH.SECURITY_LEVEL, AonEnumUtils.getByte(fbatch.isConfidential()))
				.set(FBATCH.RATTACH, fbatch.getRattach())
				.set(FBATCH.CREATION_USER,ctx.getUser())
				.set(FBATCH.CREATION_DATE, new Timestamp( System.currentTimeMillis()))
				.returning()
				.fetchOne();
		
		ctx.log().debug("INSERT FBATCH ID: " + record.getValue(FBATCH.ID));	
		
		return new FBatchFiller().apply(record); 
	}
	
	private static FBatch update(AONContext ctx, FBatch fbatch) {
		ctx.checkWrite();
		
		// Estado real en BD: el cliente no decide si la remesa es modificable.
		FBatch current = get(ctx, fbatch.getId());
		if (current.isRecorded())
			throw new AonCoreException(AonError.INVALID_DATA.format(
					"No se puede modificar una remesa contabilizada"));
		
		ctx.getDslContext().update(FBATCH)
			.set(FBATCH.DESCRIPTION, fbatch.getDescription())
			.set(FBATCH.ISSUE_DATE, AonDateUtils.toSql(fbatch.getIssueDate()))
			.set(FBATCH.TYPE, fbatch.getType())
			.set(FBATCH.STATUS, (byte) fbatch.getStatus().ordinal())
			.set(FBATCH.RBANK, fbatch.getRbank() == null ? null : fbatch.getRbank().getId())
			.set(FBATCH.BANK_STATEMENT_LINK, fbatch.getBankStatementLink())
			.set(FBATCH.PAYMENT, fbatch.getPayment())
			.set(FBATCH.SECURITY_LEVEL, AonEnumUtils.getByte(fbatch.isConfidential()))
			.set(FBATCH.RATTACH, fbatch.getRattach())
			.set(FBATCH.MODIFICATION_USER,ctx.getUser())
			.set(FBATCH.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()))
			.where(FBATCH.ID.equal(fbatch.getId()))
			.and(FBATCH.DOMAIN.eq(ctx.getDomainId()))
			.execute();
		ctx.log().debug("UPDATE FBATCH ID: " + fbatch.getId());
		
		fbatch.getBatchDetails().forEach(fBatchDetail -> {
			if (fBatchDetail.getFbatch() != null && !fBatchDetail.getFbatch().equals(fbatch.getId()))
				throw new AonCoreException(AonError.INVALID_DATA.format(
						"Detalle de remesa que no pertenece a la remesa " + fbatch.getId()));
			FBatchDetailDAO.save(ctx, fBatchDetail);
		});
		
		return get(ctx, fbatch.getId());
	}
	
	// ---------------------------------------------------------- BORRADO
	
	public static void delete(CloseableAONContext ctx, LinkedList<Integer> fBatchIds) {
		fBatchIds.forEach(fBatchId -> delete(ctx, fBatchId));
	}

	public static void delete(CloseableAONContext ctx, Integer id) {
		ctx.checkWrite();
		FBatch fbatch = get(ctx, id);
		
		if (!fbatch.isPending() && !fbatch.isGenerated())
			throw new AonCoreException(AonError.INVALID_DATA.format(
					"Solo se pueden eliminar remesas pendientes o con fichero generado"));

		removeFBatchDetails(ctx, fbatch);

		if(fbatch.getRattach() != null)
			removeRattach(ctx, fbatch.getRattach());

		ctx.getDslContext()
			.delete(FBATCH)
			.where(FBATCH.ID.equal(id))
			.and(FBATCH.DOMAIN.eq(ctx.getDomainId()))
			.execute();

		ctx.log().debug("DELETE FBATCH ID " + fbatch.getId());
	}
	
	private static void removeRattach(CloseableAONContext ctx, Integer rattachId) {
		ctx.getDslContext()
		.delete(RATTACH)
		.where(RATTACH.ID.eq(rattachId))
		.execute();
	
		ctx.log().debug("DELETE RATTACH ID: " + rattachId);
	}

	private static void removeFBatchDetails(AONContext ctx, FBatch fbatch) {
		fbatch.getBatchDetails().forEach(fbatchDetail ->  
			FBatchDetailDAO.delete(ctx, fbatchDetail.getId())	
		);
		
		ctx.log().debug("DELETE FBATCH_DETAILS, FBATCH ID: " + fbatch.getId());
	}
	
	// ---------------------------------------------------------- MAP
	
	public static class FBatchFiller extends Filler  implements Function<Record, FBatch> {
		@Override
		public FBatch apply(Record record) {
			return new FBatch()
				.setId(record.getValue(FBATCH.ID))
				.setDomain(record.getValue(FBATCH.DOMAIN))
				.setDescription(record.getValue(FBATCH.DESCRIPTION))
				.setIssueDate(record.getValue(FBATCH.ISSUE_DATE))
				.setType(record.getValue(FBATCH.TYPE))
				.setStatus(FBatchStatus.safeValueOf(record.getValue(FBATCH.STATUS)))
				.setRbank(checkField(record, RBANK.ID) && null != record.get(RBANK.ID) ? RegistryBankFiller.build(record) : null)
				.setBankStatementLink(record.getValue(FBATCH.BANK_STATEMENT_LINK))
				.setPayment(record.getValue(FBATCH.PAYMENT))
				.setSecurityLevel(SecurityLevel.safeValueOf(record.getValue(FBATCH.SECURITY_LEVEL)))
				.setRattach(record.getValue(FBATCH.RATTACH))
				.setCreationUser(record.getValue(FBATCH.CREATION_USER))
				.setCreationDate(record.getValue(FBATCH.CREATION_DATE))
				.setModificationUser(record.getValue(FBATCH.MODIFICATION_USER))
				.setModificationDate(record.getValue(FBATCH.MODIFICATION_DATE))
				;
		}
			
	}

	// ---------------------------------------------------------- ACCOUNTING
	public static FBatch record(AONContext ctx, Integer fbatchId, Date paymentDate) {
		ctx.checkWrite();
		
		if (fbatchId == null) 
			throw new AonCoreException(AonError.EMPTY_DATA.format("ID Remesa"));
		
		FBatch fbatch = get(ctx, fbatchId);
		
		if (fbatch == null || fbatch.getId() == null) 
			throw new AonCoreException(AonError.NOT_EXIST.format("Remesa con id " + fbatchId));
		if (fbatch.isRecorded())
			throw new AonCoreException(AonError.INVALID_DATA.format("La remesa " + fbatchId + " ya está contabilizada"));
		if (fbatch.getBatchDetails() == null || fbatch.getBatchDetails().isEmpty())
			throw new AonCoreException(AonError.EMPTY_DATA.format("Vencimientos de la remesa " + fbatchId));
		
		FinanceEntryDAO.recordFBatch(ctx, fbatch, paymentDate);
		
		return get(ctx, fbatchId);
	}

	private static Optional<Integer> getAccountEntryId(AONContext ctx, Integer fbatchId) {
		ctx.checkRead();
		return ctx.getDslContext().select( ACCOUNT_ENTRY_FBATCH.ACCOUNT_ENTRY )
			.from(ACCOUNT_ENTRY_FBATCH)
			.where(ACCOUNT_ENTRY_FBATCH.FBATCH.eq(fbatchId))
			.fetch()
			.stream()
			.map(rec -> rec.getValue(ACCOUNT_ENTRY_FBATCH.ACCOUNT_ENTRY))
			.findFirst()
		;
	}
	
	public static FBatch unrecord(AONContext ctx, Integer fbatchId) {
		ctx.checkWrite();
		if (fbatchId == null) throw new AonCoreException(AonError.EMPTY_DATA.format("FBatch id"));
		FBatch fbatch = get(ctx, fbatchId);
		if (fbatch == null || fbatch.getId() == null) throw new AonCoreException(AonError.NOT_EXIST.format("Remesa con id " + fbatchId));
		Integer accountEntryId = getAccountEntryId(ctx, fbatchId)
			.orElseThrow(() -> new AonCoreException(AonError.NOT_EXIST.format("Asiento contable de la remesa con id " + fbatchId)));
		AccountEntryDAO.delete(ctx, accountEntryId);
		return get(ctx, fbatchId);
	}

	public static AccountEntry getAccountEntry(AONContext ctx, Integer fbatchId) {
		return getAccountEntryId(ctx,fbatchId)
			.map(accountEntryId -> AccountEntryDAO.getAccountEntry(ctx, accountEntryId))
			.orElseThrow(() -> new AonCoreException(AonError.ACCOUNT_ENTRY_NOT_FOUND.getMessage()))	
		;
	}
	
}