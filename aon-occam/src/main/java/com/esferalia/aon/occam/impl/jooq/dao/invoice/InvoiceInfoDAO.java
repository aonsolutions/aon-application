package com.esferalia.aon.occam.impl.jooq.dao.invoice;

import static com.esferalia.aon.jooq.tables.InvoiceInfo.INVOICE_INFO;

import java.sql.Timestamp;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.jooq.Record;
import org.jooq.SelectJoinStep;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceData;
import com.esferalia.aon.occam.api.model.finance.InvoiceDataName;
import com.esferalia.aon.occam.api.model.finance.InvoiceInfo;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationStatus;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType.InvoiceCommunicationTypeVisitor;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.impl.jooq.dao.DataResponseDAO;
import com.esferalia.aon.occam.impl.jooq.dao.Filler;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceCommunicationDAO;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class InvoiceInfoDAO {
	
	private static final String TBAIURL = "tbaiUrl";
	
	private InvoiceInfoDAO() {
	
	}
	// ************************************************************
	// *********************** [READ] *****************************
	// ************************************************************
	
	public static Optional<InvoiceInfo> get(AONContext ctx, Integer invoiceId, InvoiceCommunicationType type) {
		ctx.checkRead();
		if (invoiceId == null) return Optional.empty();
		if (type == null) return Optional.empty();
		return select(ctx)
			.where(INVOICE_INFO.INVOICE.eq(invoiceId))
			.and(INVOICE_INFO.TYPE.eq(type.value()))
			.orderBy(INVOICE_INFO.CREATION_DATE.desc())
			.fetch()
			.stream()
			.map(r -> {
				Integer domainId = r.getValue(INVOICE_INFO.DOMAIN);
				InvoiceCommunicationConfiguration icc = InvoiceCommunicationDAO.get(ctx, domainId, false);
				InvoiceInfo v = InvoiceInfoFiller.build(ctx, r);
				fixUrl( icc, v );
				return v;
			})
			.findFirst();	
	}
	
	public static Optional<EnumMap<InvoiceCommunicationType,InvoiceInfo>> getMap(AONContext ctx, Invoice invoice) {
		return getMap(ctx, invoice.getDomain(), invoice.getId(), invoice.getType(), invoice.getIssueDate());
	}
	public static Optional<EnumMap<InvoiceCommunicationType,InvoiceInfo>> getMap(AONContext ctx, InvoiceCommunicationConfiguration icc, Invoice invoice) {
		return getMap(ctx, icc, invoice.getDomain(), invoice.getId(), invoice.getType(), invoice.getIssueDate());
	}
	
	public static Optional<EnumMap<InvoiceCommunicationType,InvoiceInfo>> getMap(
		AONContext ctx
		, Integer domainId
		, Integer invoiceId
		, InvoiceType invoiceType
		, Date atDate
	) {
		InvoiceCommunicationConfiguration icc = InvoiceCommunicationDAO.get(ctx, domainId, false);
		return getMap(ctx, icc, domainId, invoiceId, invoiceType, atDate); 
	}
	
	public static Optional<EnumMap<InvoiceCommunicationType,InvoiceInfo>> getMap(
		AONContext ctx
		, InvoiceCommunicationConfiguration icc
		, Integer domainId
		, Integer invoiceId
		, InvoiceType invoiceType
		, Date atDate
	) {
		ctx.checkRead();
		EnumMap<InvoiceCommunicationType,InvoiceInfo> enumMap = select(ctx)
			.where(INVOICE_INFO.DOMAIN.eq(domainId))
			.and(INVOICE_INFO.INVOICE.eq(invoiceId))
			.orderBy(INVOICE_INFO.CREATION_DATE)
			.fetch()
			.stream()
			.map(r -> InvoiceInfoFiller.build(ctx, r))
			.collect(
				 () -> new EnumMap<InvoiceCommunicationType,InvoiceInfo>(InvoiceCommunicationType.class)
				,(m, v) -> m.put(v.getType(), v)
				,Map::putAll
			);
		
		if(icc.isTbai(atDate, invoiceType) && !icc.isBizkaia(atDate)) {
			DataResponseSource drs = icc.isTbaiTest() ? DataResponseSource.TBAI_TEST : DataResponseSource.TBAI;
			if(!enumMap.containsKey(InvoiceCommunicationType.TBAI)) {
				List<Boolean> list =  DataResponseDAO.getStream(ctx, f -> f.getDomainProperty().eq(domainId).and(f.getSourceProperty().eq(drs.value())).and(f.getSourceIdProperty().eq(invoiceId)))
						.map(r -> "ok".equalsIgnoreCase(r.getCode()))
						.collect(Collectors.toList());
				if(!list.isEmpty()) {
					InvoiceCommunicationStatus status = list.stream().filter(f -> f)
						.map(r -> InvoiceCommunicationStatus.ACCEPTED)
						.findFirst()
						.orElse(InvoiceCommunicationStatus.WRONG);
					InvoiceInfo invoiceInfo = new InvoiceInfo()
							.setDomain(domainId)
							.setInvoice(invoiceId)
							.setType(InvoiceCommunicationType.TBAI)
							.setStatus(status);
					save(ctx, invoiceInfo);
					enumMap.put(InvoiceCommunicationType.TBAI, invoiceInfo);					
				}	
 			}
			if(enumMap.containsKey(InvoiceCommunicationType.TBAI)) {
				String url = InvoiceDataDAO.get(ctx, domainId, invoiceId, InvoiceDataName.TBAI_URL)
						.map(InvoiceData::getValue)
						.or(() -> DataResponseDAO.getDetailValue(ctx, domainId, invoiceId, drs, TBAIURL)).orElse(null);
				enumMap.get(InvoiceCommunicationType.TBAI).setCheckUrl(url);	 
			}
		}
		if (AonCollectionUtils.isEmpty(enumMap)) {
			AonCollectionUtils.stream(icc.getTypes(invoiceType, atDate))
				.forEach( t -> enumMap.computeIfAbsent(t, 
					k -> new InvoiceInfo()
					.setType(t)
					.setInvoice(invoiceId)
					.setDomain(domainId)
					.setStatus( t.isNoVerifactu()?null:InvoiceCommunicationStatus.PENDING))
			);
		}

		// Esto es debido a que la dirección del QR no cabe en los 128 caracteres de invoice_data
		// TODO --> aumentar tamaño en BD o grabar la información en sucesivas filas .....
		// Es una buena ñapa puesto que debería guardarse la URL completa :(
		if (enumMap.containsKey(InvoiceCommunicationType.VERIFACTU)) {
			InvoiceInfo v = enumMap.get(InvoiceCommunicationType.VERIFACTU);
			fixUrl( icc, v );
		}
		
		if (enumMap.containsKey(InvoiceCommunicationType.NO_VERIFACTU)) {
			InvoiceInfo v = enumMap.get(InvoiceCommunicationType.NO_VERIFACTU);
			fixUrl( icc, v );
		}
		
		// ---------------------------------------------------------------------------------------
		
		if (AonCollectionUtils.isEmpty(enumMap)) return Optional.empty();
		return Optional.of(enumMap);
	}
	
	
	private static void fixUrl(InvoiceCommunicationConfiguration icc, InvoiceInfo v) {
		if (v != null
			 && AonStringUtils.startsWith(v.getCheckUrl(), "?")) {
				String urlQr = "https://www2.agenciatributaria.gob.es/wlpl/TIKE-CONT/ValidarQR" + (v.getType().isNoVerifactu()? "NoVerifactu":"");
				String urlQrTest = "https://prewww2.aeat.es/wlpl/TIKE-CONT/ValidarQR" + (v.getType().isNoVerifactu()? "NoVerifactu":"");
				v.setCheckUrl( (icc.isVerifactuTest()?urlQrTest:urlQr) + v.getCheckUrl());
			}
	}
	// ************************************************************
	// ********************** [WRITE] *****************************
	// ************************************************************
	public static InvoiceInfo save(AONContext ctx, InvoiceInfo invoiceInfo) {
		ctx.checkWrite();
		if (invoiceInfo == null) throw new AonCoreException(AonError.EMPTY_DATA.format("invoiceInfo")) ;
		InvoiceInfoAutoComplete.autoComplete(invoiceInfo);
		InvoiceInfoValidation.validate( invoiceInfo );
		get(ctx, invoiceInfo.getInvoice(), invoiceInfo.getType())
			.ifPresent(info -> invoiceInfo.setId(info.getId()));
		return invoiceInfo.getId() != null 
			? update(ctx, invoiceInfo)
			: insert(ctx, invoiceInfo);
	}

	public static InvoiceInfo update(AONContext ctx, InvoiceInfo invoiceInfo) {
		ctx.getDslContext()
			.update(INVOICE_INFO)
				.set(INVOICE_INFO.DOMAIN, invoiceInfo.getDomain())
				.set(INVOICE_INFO.INVOICE, invoiceInfo.getInvoice())
				.set(INVOICE_INFO.TYPE, invoiceInfo.getType().value())
				.set(INVOICE_INFO.STATUS, invoiceInfo.getStatus().value())
				.set(INVOICE_INFO.MODIFICATION_USER ,ctx.getUser())
				.set(INVOICE_INFO.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()))
			.where(INVOICE_INFO.ID.eq(invoiceInfo.getId()))
			.execute();
		return invoiceInfo;
	}

	public static InvoiceInfo insert(AONContext ctx, InvoiceInfo invoiceInfo) {
		Integer id = ctx.getDslContext()
			.insertInto(INVOICE_INFO)
				.set(INVOICE_INFO.DOMAIN, invoiceInfo.getDomain())
				.set(INVOICE_INFO.INVOICE, invoiceInfo.getInvoice())
				.set(INVOICE_INFO.TYPE, invoiceInfo.getType().value())
				.set(INVOICE_INFO.STATUS, invoiceInfo.getStatus().value())
				.set(INVOICE_INFO.CREATION_USER ,ctx.getUser())
				.set(INVOICE_INFO.CREATION_DATE, new Timestamp( System.currentTimeMillis()))
				.set(INVOICE_INFO.MODIFICATION_USER ,ctx.getUser())
				.set(INVOICE_INFO.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()))
			.returning(INVOICE_INFO.ID)
			.fetchOne()
			.getId();
		return invoiceInfo.setId(id);
	}	
	
	
	public static void deleteById(AONContext ctx, Integer id){
		ctx.checkWrite();
		ctx.getDslContext().delete(INVOICE_INFO)
			.where(INVOICE_INFO.ID.eq(id))
			.execute();
	}

	public static void deleteByInvoice(AONContext ctx, Integer invoiceId){
		ctx.checkWrite();
		ctx.getDslContext().delete(INVOICE_INFO)
			.where(INVOICE_INFO.INVOICE.eq(invoiceId))
			.execute();
	}

	// ************************************************************
	// ********************* [PRIVATE] ****************************
	// ************************************************************
	private static SelectJoinStep<Record> select(AONContext ctx){	
		return ctx.getDslContext()
			.select()
			.from(INVOICE_INFO);
	}
	
	// ***********************************************************
	// ********************* [FILLER] ****************************
	// ***********************************************************
	public static class InvoiceInfoFiller extends Filler implements BiFunction<AONContext, Record, InvoiceInfo> {

		@Override
		public InvoiceInfo apply(AONContext ctx, Record r) {
			return build(ctx, r);
		}
		
		public static InvoiceInfo build(AONContext ctx, Record r) {
			InvoiceInfo info = new InvoiceInfo()
				.setId(getValue(r,INVOICE_INFO.ID))
				.setDomain(getValue(r,INVOICE_INFO.DOMAIN))
				.setInvoice(getValue(r,INVOICE_INFO.INVOICE))
				.setType(InvoiceCommunicationType.safeValueOf(getValue(r,INVOICE_INFO.TYPE)))
				.setStatus(InvoiceCommunicationStatus.safeValueOf(getValue(r,INVOICE_INFO.STATUS)))
				.setCreationUser(getValue(r, INVOICE_INFO.CREATION_USER))
				.setCreationDate(getValue(r, INVOICE_INFO.CREATION_DATE))
				.setModificationUser(getValue(r, INVOICE_INFO.MODIFICATION_USER))
				.setModificationDate(getValue(r, INVOICE_INFO.MODIFICATION_DATE));
			return InvoiceInfoURLFiller.build(ctx, info);
		}
	}
	
	public static class InvoiceInfoURLFiller extends Filler implements BiFunction<AONContext, InvoiceInfo, InvoiceInfo> {

		@Override
		public InvoiceInfo apply(AONContext ctx, InvoiceInfo info) {
			return build(ctx, info);
		}
		
		public static InvoiceInfo build(AONContext ctx, InvoiceInfo info) {
			try {
				info.getType().visit(new InvoiceCommunicationTypeVisitor() {
					
					@Override
					public void visitVERIFACTU() {
						InvoiceDataDAO.getValue(ctx, info.getDomain(), info.getInvoice(), InvoiceDataName.VERIFACTU_QR).ifPresent( info::setCheckUrl );
					}
					@Override 
					public void visitNO_VERIFACTU() { 
						visitVERIFACTU();	
					}
					@Override 
					public void visitSIF() { 
						visitVERIFACTU();
					}
					
					@Override
					public void visitTBAI() {
						 InvoiceDataDAO.getValue(ctx, info.getDomain(), info.getInvoice(), InvoiceDataName.TBAI_URL)
							.or( () -> DataResponseDAO.getDetailValue(ctx, info.getDomain(), info.getInvoice(), DataResponseSource.TBAI, "tbaiUrl") )
							.ifPresent( info::setCheckUrl )
						;
					}
					
					@Override 
					public void visitLROE()  {
						visitTBAI();
					}
					
					@Override public void visitSII()	{ /*Nothing*/ }
					@Override public void visitSERES() 	{ /*Nothing*/ }
					@Override public void visitEMAIL() 	{ /*Nothing*/ }
					@Override public void visitCLOSING(){ /*Nothing*/ }
					@Override public void visitFACTURAE() { /*Nothing*/}
				});
			} catch (Exception e) {
				// Nothing
			}
			return info;
		}
		
	}
	
	/*
		VerifactuConfiguration verifactu = AON.getVerifactuConfiguration(domain, login);
		if(verifactu.isActive()) {	
		}
	 */

	// ************************************************************
	// ******************* [VALIDATION] ***************************
	// ************************************************************
	private static class InvoiceInfoValidation {
		
		private InvoiceInfoValidation() {
		}
		
		public static final Consumer<InvoiceInfo> EMPTY_DOMAIN = i -> {
			if (i.getDomain() == null) 
				throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
		};
		
		public static final Consumer<InvoiceInfo> EMPTY_INVOICE = i -> {
			if (i.getInvoice() == null) 
				throw new AonCoreException(AonError.EMPTY_DATA.format("invoice")) ;
		};
		
		public static final Consumer<InvoiceInfo> EMPTY_TYPE = i -> {
			if (i.getType() == null) 
				throw new AonCoreException(AonError.EMPTY_DATA.format("type")) ;
		};
		
		public static final Consumer<InvoiceInfo> EMPTY_STATUS = i -> {
			if (i.getStatus() == null) 
				throw new AonCoreException(AonError.EMPTY_DATA.format("status")) ;
		};
		
		public static void validate(InvoiceInfo invoiceInfo) throws AonCoreException {
			EMPTY_DOMAIN
				.andThen(EMPTY_INVOICE)
				.andThen(EMPTY_TYPE)
				.andThen(EMPTY_STATUS)
				.accept(invoiceInfo);
		}
	}

	// ************************************************************
	// ***************** [AUTO COMPLETE] **************************
	// ************************************************************
	private static class InvoiceInfoAutoComplete {
		
		private InvoiceInfoAutoComplete() {
		}
	
		public static final Consumer<InvoiceInfo> COMPLETE_STATUS = i -> {
			if (i.getStatus() == null) i.setStatus(InvoiceCommunicationStatus.PENDING);
		};

		public static void autoComplete(InvoiceInfo invoiceInfo) throws AonCoreException {
			COMPLETE_STATUS
				.accept(invoiceInfo);
		}
		
	}

}
