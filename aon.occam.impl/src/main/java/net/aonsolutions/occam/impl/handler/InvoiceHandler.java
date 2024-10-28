package net.aonsolutions.occam.impl.handler;

import static com.esferalia.aon.jooq.tables.Cnae2009.CNAE2009;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.Iae.IAE;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoiceDetailAccount.INVOICE_DETAIL_ACCOUNT;
import static com.esferalia.aon.jooq.tables.InvoiceFiscal.INVOICE_FISCAL;
import static com.esferalia.aon.jooq.tables.InvoiceTax.INVOICE_TAX;
import static com.esferalia.aon.jooq.tables.InvoiceTaxAccount.INVOICE_TAX_ACCOUNT;
import static com.esferalia.aon.jooq.tables.InvoiceTracking.INVOICE_TRACKING;
import static com.esferalia.aon.jooq.tables.Seller.SELLER;
import static net.aonsolutions.occam.impl.handler.SellerHandler.REGISTRY_SELLER;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Optional;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.InvoiceRecord;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.server.io.AonIOUtils;
import com.esferalia.aon.watson.server.io.DataUrl;
import com.esferalia.aon.watson.server.io.DataUrlSerializer;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.occam.api.model.Account;
import net.aonsolutions.occam.api.model.DiscountExpression;
import net.aonsolutions.occam.api.model.Filter.InvoiceFilter;
import net.aonsolutions.occam.api.model.Filter.Property;
import net.aonsolutions.occam.api.model.InvestAsset;
import net.aonsolutions.occam.api.model.Invoice;
import net.aonsolutions.occam.api.model.InvoiceCalculator;
import net.aonsolutions.occam.api.model.InvoiceDetail;
import net.aonsolutions.occam.api.model.InvoiceErrorMessages;
import net.aonsolutions.occam.api.model.InvoiceHeader;
import net.aonsolutions.occam.api.model.InvoiceTax;
import net.aonsolutions.occam.api.model.Properties.InvoiceProperties;
import net.aonsolutions.occam.api.model.Seller;
import net.aonsolutions.occam.api.model.Workplace;
import net.aonsolutions.occam.api.model.type.AttachType;
import net.aonsolutions.occam.api.model.type.Country;
import net.aonsolutions.occam.api.model.type.DocumentType;
import net.aonsolutions.occam.api.model.type.InvoiceAttachmentType;
import net.aonsolutions.occam.api.model.type.InvoiceErrorKey;
import net.aonsolutions.occam.api.model.type.InvoiceSource;
import net.aonsolutions.occam.api.model.type.InvoiceSource.InvoiceSourceVisitor;
import net.aonsolutions.occam.api.model.type.InvoiceType;
import net.aonsolutions.occam.api.model.type.RectificationType;
import net.aonsolutions.occam.api.model.type.SecurityLevel;
import net.aonsolutions.occam.api.model.type.TaxType;
import net.aonsolutions.occam.api.model.type.VatDeductionType;
import net.aonsolutions.occam.api.model.type.WithholdingType;
import net.aonsolutions.occam.impl.AONContext;
import net.aonsolutions.occam.impl.handler.FinanceHandler.FinanceAutoComplete;
import net.aonsolutions.occam.impl.handler.InvoiceFiscalHandler.InvoiceFiscalFiller;
import net.aonsolutions.occam.impl.handler.InvoiceHeaderHandler.InvoiceHeaderFiller;

class InvoiceHandler {
	
	private InvoiceHandler() {
	}
	
	// private static final com.esferalia.aon.jooq.tables.Invoice RECTIFICATION_INVOICE = INVOICE.as("RECTIFICATION_INVOICE");
	
	// Field para que salgan ordenado primero compras,gastos y gastos no .ded y luego ventas.
	// En la select se complementa con invoice.type
	static final Field<Integer> ORDERED_TYPE = DSL.decode()
		.when(INVOICE.TYPE.equal((byte) 0), 0)
		.when(INVOICE.TYPE.equal((byte) 1), 1)
		.when(INVOICE.TYPE.equal((byte) 2), 0)
		.when(INVOICE.TYPE.equal((byte) 3), 0);

	static final InvoicePropertiesHandler INVOICE_PROPERTIES = new InvoicePropertiesHandler();
	static class InvoicePropertiesHandler implements InvoiceProperties {
		
		private static final long serialVersionUID = -5116444114505029655L;
		
		Condition getCondition(InvoiceFilter filter) {
			if (filter == null) {
				throw new AonCoreException( AonError.NULL_FILTER.getMessage());
			}
			FilterImpl filterHandler = (FilterImpl) filter.filter(this);
			if (filterHandler == null)return DSL.trueCondition();
			return filterHandler.getCondition();
		}
		
		@Override public Property<Integer> getIdProperty() {return new FilterImpl.PropertyDAO<>(INVOICE.ID);}
		@Override public Property<Integer> getDomainProperty(){return new FilterImpl.PropertyDAO<>(INVOICE.DOMAIN);} 
		@Override public Property<Integer> getActivityProperty(){return new FilterImpl.PropertyDAO<>(INVOICE.ACTIVITY);} 
		@Override public Property<Integer> getInvestAssetProperty(){return new FilterImpl.PropertyDAO<>(INVOICE.INVEST_ASSET);}
		@Override public Property<Integer> getProjectProperty(){return new FilterImpl.PropertyDAO<>(INVOICE.PROJECT);}
		@Override public Property<String> getSeriesProperty() {return new FilterImpl.PropertyDAO<>(INVOICE.SERIES);} 
		@Override public Property<Integer> getNumberProperty() {return new FilterImpl.PropertyDAO<>(INVOICE.NUMBER);} 
		@Override public Property<String> getReferenceCodeProperty() {return new FilterImpl.PropertyDAO<>(INVOICE.REFERENCE_CODE);} 
		@Override public Property<Integer> getRegistryProperty(){return new FilterImpl.PropertyDAO<>(INVOICE.REGISTRY);} 
		@Override public Property<String> getRegistryDocumentProperty(){return new FilterImpl.PropertyDAO<>(INVOICE.RDOCUMENT);} 
		@Override public Property<Byte> getRegistryDocumentTypeProperty() {return new FilterImpl.PropertyDAO<>(INVOICE.RDOCUMENT_TYPE);} 
		@Override public Property<String> getRegistryDocumentCountryProperty(){return new FilterImpl.PropertyDAO<>(INVOICE.RDOCUMENT_COUNTRY);} 
		@Override public Property<String> getRegistryNameProperty(){return new FilterImpl.PropertyDAO<>(INVOICE.RNAME);}
		@Override public Property<Integer> getRegistryAddressProperty(){return new FilterImpl.PropertyDAO<>(INVOICE.RADDRESS);} 
		@Override public Property<java.util.Date> getIssueDateProperty() {return new FilterImpl.DatePropertyDAO(INVOICE.ISSUE_DATE);} 
		@Override public Property<java.util.Date> getTaxDateProperty() {return new FilterImpl.DatePropertyDAO(INVOICE.TAX_DATE);} 
		@Override public Property<Byte> getSecurityLevelProperty() {return new FilterImpl.PropertyDAO<>(INVOICE.SECURITY_LEVEL);} 
		@Override public Property<Byte> getStatusProperty() {return new FilterImpl.PropertyDAO<>(INVOICE.STATUS);} 
		@Override public Property<Byte> getTypeProperty() {return new FilterImpl.PropertyDAO<>(INVOICE.TYPE);} 
		@Override public Property<Byte> getSurchargeProperty() {return new FilterImpl.PropertyDAO<>(INVOICE.SURCHARGE);} 
		@Override public Property<Byte> getWithholdingProperty() {return new FilterImpl.PropertyDAO<>(INVOICE.WITHHOLDING);} 
		@Override public Property<Byte> getWithholdingFarmerProperty() {return new FilterImpl.PropertyDAO<>(INVOICE.WITHHOLDING_FARMER);}
		@Override public Property<Byte> getVatAccrualPayment() {return new FilterImpl.PropertyDAO<>(INVOICE.VAT_ACCRUAL_PAYMENT);} 
		@Override public Property<String> getCommentsProperty(){return new FilterImpl.PropertyDAO<>(INVOICE.COMMENTS);}
		@Override public Property<String> getRemarksProperty(){return new FilterImpl.PropertyDAO<>(INVOICE.REMARKS);}
		@Override public Property<Byte> getInvestmentProperty() {return new FilterImpl.PropertyDAO<>(INVOICE.INVESTMENT);} 
		@Override public Property<Byte> getTransactionProperty() {return new FilterImpl.PropertyDAO<>(INVOICE.TRANSACTION);} 
		@Override public Property<Byte> getSignedProperty() {return new FilterImpl.PropertyDAO<>(INVOICE.SIGNED);}
		@Override public Property<Integer> getScopeProperty() {return new FilterImpl.PropertyDAO<>(INVOICE.SCOPE);} 
		@Override public Property<Byte> getServiceProperty() {return new FilterImpl.PropertyDAO<>(INVOICE.SERVICE);}
		@Override public Property<Byte> getRectificationTypeProperty() {return new FilterImpl.PropertyDAO<>(INVOICE.RECTIFICATION_TYPE);} 
		@Override public Property<Integer> getRectificationInvoiceProperty() {return new FilterImpl.PropertyDAO<>(INVOICE.RECTIFICATION_INVOICE);} 
		@Override public Property<Integer> getSellerProperty() {return new FilterImpl.PropertyDAO<>(INVOICE.SELLER);} 
		@Override public Property<Double> getTaxableBaseProperty() {return new FilterImpl.PropertyDAO<>(INVOICE.TAXABLE_BASE);} 
		@Override public Property<Double> getVatQuotaProperty() {return new FilterImpl.PropertyDAO<>(INVOICE.TOTAL);} 
		@Override public Property<Double> getRetentionQuotaTotalProperty() {return new FilterImpl.PropertyDAO<>(INVOICE.TOTAL);} 
		@Override public Property<Double> getTotalProperty() {return new FilterImpl.PropertyDAO<>(INVOICE.TOTAL);} 
		@Override public Property<String> getCreationUserProperty() {return new FilterImpl.PropertyDAO<>(INVOICE.CREATION_USER);} 
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterImpl.PropertyDAO<>(INVOICE.CREATION_DATE);} 
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterImpl.PropertyDAO<>(INVOICE.MODIFICATION_DATE);} 
		@Override public Property<String> getModificationUserProperty() {return new FilterImpl.PropertyDAO<>(INVOICE.MODIFICATION_USER);} 
	}
	static class InvoiceFiller extends Filler<Invoice> {

		@Override
		public Invoice  apply(Record r) {
			return build(r);
		}

	    static Invoice build(Record r) {
	        return build(r, INVOICE);
	    }
	     
    	static Invoice build(Record r, com.esferalia.aon.jooq.tables.Invoice inv) {
    		if (isNull(r,inv.ID)) return null;
    		return new Invoice()
				.setHeader(InvoiceHeaderFiller.build(r))	
				.setFiscal(InvoiceFiscalFiller.build(r))
			;
		}
	}

	private static SelectConditionStep<Record> select(AONContext ctx, int domain ) {
		return ctx.getDslContext()
			.select()
			.from(INVOICE)
			.leftOuterJoin(INVOICE_FISCAL).on(INVOICE_FISCAL.INVOICE.equal(INVOICE.ID))
			.leftOuterJoin(ENTERPRISE_ACTIVITY).on(ENTERPRISE_ACTIVITY.ID.eq(INVOICE.ACTIVITY))
			.leftOuterJoin(IAE).on(IAE.ID.equal(ENTERPRISE_ACTIVITY.IAE))
			.leftOuterJoin(CNAE2009).on(CNAE2009.ID.eq(ENTERPRISE_ACTIVITY.CNAE2009))
			.leftOuterJoin(SELLER).on(SELLER.REGISTRY.eq(INVOICE.SELLER))
			.leftOuterJoin(REGISTRY_SELLER).on(REGISTRY_SELLER.ID.eq(SELLER.REGISTRY))
			.where(INVOICE.DOMAIN.eq(domain))
		;
	}
	
	static Optional<Invoice> get(AONContext ctx, int domain, Integer id) {
		ctx.checkRead();
		return select(ctx, domain)
			.and(INVOICE.ID.eq(id))
			.fetch()
			.stream()
			.map( new InvoiceFiller() )
			.map(i -> InvoiceDetailHandler.fillInvoice(ctx,i))
			.map(i -> FinanceHandler.fillInvoice(ctx, domain, i))
			.map(i -> InvoiceAddressHandler.fillInvoice(ctx, i))
			.map(Invoice::refreshTaxBreakdown)
			.findFirst();
	}

	static Invoice validate(AONContext ctx, int domain, Invoice invoice) {
		InvoiceAutoComplete.completeInvoice(ctx, invoice);
		InvoiceCalculator.calculate(invoice);
		InvoiceValidation.validate(ctx, invoice);
		invoice.financeStream()
			.forEach(f -> FinanceAutoComplete.completeFinanceFromInvoice(ctx, domain, invoice, f));
		return invoice;
	}
	
	static Invoice save(AONContext ctx, int domain, Invoice invoice) {
		return save(ctx, domain, invoice, false);
	}
	static Invoice saveAndGet(AONContext ctx, int domain, Invoice invoice) {
		return save(ctx, domain, invoice, true);
	}
	
	private static Invoice save(AONContext ctx, int domain, Invoice invoice, boolean returnFullInvoice) {
		ctx.checkWrite();
		validate(ctx, domain, invoice);
		if (invoice.getId() == null) {
			insert(ctx, invoice);
		} else {
			update(ctx, invoice);
		}
		saveDetails(ctx, invoice);
		InvoiceFiscalHandler.save(ctx, invoice);
		InvoiceAddressHandler.save(ctx, invoice);
		invoice.financeStream()
			.forEach( finance -> FinanceHandler.save(ctx, domain, invoice));
		if (returnFullInvoice) {
			return get(ctx, invoice.getDomain() , invoice.getId() )
				.orElseThrow(() -> {
					invoice.addMessage( InvoiceErrorMessages.C500.err(InvoiceErrorKey.GENERIC));
					return new AonCoreException( InvoiceErrorMessages.C500.getMessage());
				});
		} 
		return invoice;
	}

	private static Invoice update(AONContext ctx, Invoice invoice) {
		InvoiceHeader header = invoice.getHeader();
		int i = ctx.getDslContext()
			.update(INVOICE)
			.set(INVOICE.DOMAIN, header.getDomain() )
			.set(INVOICE.ACTIVITY, header.getActivity().map(a -> a.getId()).orElse(null) )
			//.set(INVOICE.INVEST_ASSET, invoice.getInvestAsset() )
			.set(INVOICE.PROJECT, header.getProject() )
			.set(INVOICE.SERIES, header.getSeries() )
			.set(INVOICE.NUMBER, header.getNumber() )
			.set(INVOICE.REFERENCE_CODE, header.getReferenceCode() )
			.set(INVOICE.REGISTRY, header.getRegistry() )
			.set(INVOICE.RDOCUMENT, header.getRegistryDocument() )
			.set(INVOICE.RDOCUMENT_TYPE, DocumentType.value( header.getRegistryDocumentType()) )
			.set(INVOICE.RDOCUMENT_COUNTRY, Country.value( header.getRegistryDocumentCountry()))
			.set(INVOICE.RNAME, header.getRegistryName() )
			// .set(INVOICE.RADDRESS, invoice.getRegistryAddress() )
			.set(INVOICE.ISSUE_DATE, AonDateUtils.toSql( header.getIssueDate()) )
			.set(INVOICE.TAX_DATE, AonDateUtils.toSql(header.getTaxDate()) )
			.set(INVOICE.SECURITY_LEVEL, SecurityLevel.value( header.isConfidential() ) )
			.set(INVOICE.STATUS, AonEnumUtils.getByte( header.isRecorded() ) )
			.set(INVOICE.TYPE, InvoiceType.value( header.getType() ) )
			.set(INVOICE.SURCHARGE, AonEnumUtils.getByte( header.isSurcharge() ))
			.set(INVOICE.WITHHOLDING, AonEnumUtils.getByte( header.isWithholding() ))
			.set(INVOICE.WITHHOLDING_FARMER, AonEnumUtils.getByte( header.isWithholdingFarmer() ))
			.set(INVOICE.VAT_ACCRUAL_PAYMENT, AonEnumUtils.getByte( header.isVatAccrualPayment() ))
			.set(INVOICE.INVESTMENT, AonEnumUtils.getByte( header.isInvestment() ) )
			.set(INVOICE.TRANSACTION, AonEnumUtils.getByte( header.getTransaction() ) )
			.set(INVOICE.SCOPE, header.getScope())
			.set(INVOICE.SERVICE, AonEnumUtils.getByte(  header.isService() ) )
			.set(INVOICE.RECTIFICATION_TYPE, RectificationType.value(header.getRectificationType()) )
			.set(INVOICE.RECTIFICATION_INVOICE, header.getRectificationInvoiceId() )
			//.set(INVOICE.ADVANCE, AonEnumUtils.getByte( invoice.isAdvance()) )
			.set(INVOICE.SIGNED, AonEnumUtils.getByte( header.isSigned()) )
			.set(INVOICE.TAXABLE_BASE, header.getTaxableBase() )
			.set(INVOICE.VAT_QUOTA, header.getVatQuota() )
			.set(INVOICE.RETENTION_QUOTA, header.getRetentionQuota() )
			.set(INVOICE.TOTAL, header.getTotal() )
			//.set(INVOICE.POS_SHIFT, invoice.getPosShift() )
			.set(INVOICE.SELLER, header.getSeller().map(s -> s.getId()).orElse(null))
			.set(INVOICE.COMMENTS, header.getComments() )
			.set(INVOICE.REMARKS, header.getRemarks() )
			.set(INVOICE.MODIFICATION_USER,ctx.getUser())
			.set(INVOICE.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()) )
			.where(INVOICE.ID.equal( header.getId()))
			.execute();
		ctx.log().debug("UPDATE INVOICE invoice: {0} ({1} rows)",header.getId(),i);
		return invoice; 
	}
	
	private static Invoice insert(AONContext ctx, Invoice invoice) {
		ctx.checkWrite();
		InvoiceHeader header = invoice.getHeader();
		InvoiceRecord rec = ctx.getDslContext()
			.insertInto(INVOICE)
			.set(INVOICE.DOMAIN, header.getDomain() )
			.set(INVOICE.ACTIVITY, header.getActivity().map(a -> a.getId()).orElse(null) )
			//.set(INVOICE.INVEST_ASSET, invoice.getInvestAsset() )
			.set(INVOICE.PROJECT, header.getProject() )
			.set(INVOICE.SERIES, header.getSeries() )
			.set(INVOICE.NUMBER, header.getNumber() )
			.set(INVOICE.REFERENCE_CODE, header.getReferenceCode() )
			.set(INVOICE.REGISTRY, header.getRegistry() )
			.set(INVOICE.RDOCUMENT, header.getRegistryDocument() )
			.set(INVOICE.RDOCUMENT_TYPE, DocumentType.value(header.getRegistryDocumentType()) )
			.set(INVOICE.RDOCUMENT_COUNTRY, Country.value( header.getRegistryDocumentCountry()))
			.set(INVOICE.RNAME, header.getRegistryName() )
			//.set(INVOICE.RADDRESS, invoice.getRegistryAddress() )
			.set(INVOICE.ISSUE_DATE, AonDateUtils.toSql( header.getIssueDate()) )
			.set(INVOICE.TAX_DATE, AonDateUtils.toSql(header.getTaxDate()) )
			.set(INVOICE.SECURITY_LEVEL, SecurityLevel.value( header.isConfidential() ) )
			.set(INVOICE.STATUS, AonEnumUtils.getByte( header.isRecorded() ) )
			.set(INVOICE.TYPE, InvoiceType.value( header.getType() ) )
			.set(INVOICE.SURCHARGE, AonEnumUtils.getByte( header.isSurcharge() ))
			.set(INVOICE.WITHHOLDING, AonEnumUtils.getByte( header.isWithholding() ))
			.set(INVOICE.WITHHOLDING_FARMER, AonEnumUtils.getByte( header.isWithholdingFarmer() ))
			.set(INVOICE.VAT_ACCRUAL_PAYMENT, AonEnumUtils.getByte( header.isVatAccrualPayment() ))
			.set(INVOICE.INVESTMENT, AonEnumUtils.getByte( header.isInvestment() ) )
			.set(INVOICE.TRANSACTION, AonEnumUtils.getByte( header.getTransaction() ) )
			.set(INVOICE.SCOPE, header.getScope() )
			.set(INVOICE.SERVICE, AonEnumUtils.getByte(  header.isService() ) )
			.set(INVOICE.RECTIFICATION_TYPE, RectificationType.value(header.getRectificationType()) )
			.set(INVOICE.RECTIFICATION_INVOICE, header.getRectificationInvoiceId() )
			//.set(INVOICE.ADVANCE, AonEnumUtils.getByte( invoice.isAdvance()) )
			.set(INVOICE.SIGNED, AonEnumUtils.getByte( header.isSigned()) )
			.set(INVOICE.TAXABLE_BASE, header.getTaxableBase() )
			.set(INVOICE.VAT_QUOTA, header.getVatQuota() )
			.set(INVOICE.RETENTION_QUOTA, header.getRetentionQuota() )
			.set(INVOICE.TOTAL, header.getTotal() )
			//.set(INVOICE.POS_SHIFT, invoice.getPosShift() )
			.set(INVOICE.SELLER, header.getSeller().map(s -> s.getId()).orElse(null))
			.set(INVOICE.COMMENTS, header.getComments() )
			.set(INVOICE.REMARKS, header.getRemarks() )
			.set(INVOICE.CREATION_USER, ctx.getUser()) 
			.set(INVOICE.CREATION_DATE, new Timestamp( System.currentTimeMillis()))
			.set(INVOICE.MODIFICATION_USER, ctx.getUser()) 
			.set(INVOICE.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()))
			.returning(INVOICE.ID)
			.fetchOne();
		header.setId(rec.getValue(INVOICE.ID));
		ctx.log().debug("INSERT INVOICE invoice: {0} Act: {1}",header.getId(),
				header.getActivity().map(a -> a.getId()).orElse(null));
		insertInvoiceAttach( ctx, invoice);
		return invoice;
	}

	static int getNextNumber(AONContext ctx, int domain, Byte[] types, String series ) {
		boolean tbaiActive = ctx.getTbaiConfig(domain).isActive();
		if (tbaiActive && AonCollectionUtils.contains(types, InvoiceType.SALES.value())) {
			return getTbaiNextNumber(ctx, domain, types, series);
		} else {
			Integer next = selectMaxInvoice(ctx, domain, types, series)
			.fetch()
			.stream()
			.mapToInt(rec -> (rec != null && rec.getValue(DSL.max(INVOICE.NUMBER)) != null) 
					? rec.getValue(DSL.max(INVOICE.NUMBER)) 
					: 0)
			.findFirst()
			.orElse(0);
			if(next < 0) next = 0;
			return ++next;
		}
	}
	
	private static int getTbaiNextNumber(AONContext ctx, int domain, Byte[] types, String series ) {
		Integer next = selectMaxInvoice(ctx, domain, types, series)
			.union(selectMaxInvoiceTracking(ctx, domain, types, series))
			.fetch()
			.stream()
			.mapToInt(rec -> (rec != null && rec.getValue(DSL.max(INVOICE.NUMBER)) != null) 
					? rec.getValue(DSL.max(INVOICE.NUMBER)) 
					: 0)
			.max().orElse(0);
		if(next < 0) next = 0;
		return ++next;
	}
	
	private static SelectConditionStep<Record1<Integer>> selectMaxInvoiceTracking(AONContext ctx, int domain, Byte[] types, String series) {
		 return ctx.getDslContext()
			.select( DSL.max(INVOICE_TRACKING.NUMBER))
			.from(INVOICE_TRACKING)
			.where(INVOICE_TRACKING.DOMAIN.eq(domain))
			.and(INVOICE_TRACKING.TYPE.in(types))
			.and( AonStringUtils.isBlank(series)
				? INVOICE_TRACKING.SERIES.isNull().or(DSL.trim(INVOICE_TRACKING.SERIES).eq(""))
				: INVOICE_TRACKING.SERIES.eq(series));
	}
	
	private static SelectConditionStep<Record1<Integer>> selectMaxInvoice(AONContext ctx, int domain, Byte[] types, String series) {
		 return ctx.getDslContext()
			.select( DSL.max(INVOICE.NUMBER))
			.from(INVOICE)
			.where(INVOICE.DOMAIN.eq(domain))
			.and(INVOICE.TYPE.in(types))
			.and(AonStringUtils.isBlank(series)
				? INVOICE.SERIES.isNull().or(DSL.trim(INVOICE.SERIES).eq(""))
				: INVOICE.SERIES.eq(series));
	}
	
	// *************************************************
	// ************* INVOICE DETAILS *******************
	// *************************************************
	private static void saveDetails(AONContext ctx, Invoice invoice) {
		// Se guardan las líneas no marcadas como borradas
		invoice.detailStream()
			.filter(det -> !det.isDeleted())
			.forEach(det -> saveDetail(ctx,invoice, det));
		
		// Se borran las las líneas marcadas como borradas
		invoice.detailStream()
			.filter(InvoiceDetail::isDeleted)
			.map(det -> {			
				// REPASAR ESTE COMPORTAMIENTO!!
				// Si está marcado como borrado, porque el numero negativo???
				if (AonMathUtils.isLessThanZero(det.getId()) ) {
					det.setId(det.getId() * -1);
				}
				return det;
			})
			.forEach(det -> deleteDetail(ctx, det));
	}

	private static void deleteDetail(AONContext ctx, InvoiceDetail detail) {
		beforeDeleteDetail(ctx, detail);
		deleteTaxes(ctx,detail);
		int count = ctx.getDslContext()
			.delete(INVOICE_DETAIL)
			.where(INVOICE_DETAIL.ID.equal(detail.getId()))
			.execute();
		ctx.log().debug("DELETE INVOICE_DETAIL detalle de la factura: {0} ({1} filas)",detail.getId(),count);
	}
	
	private static void beforeDeleteDetail(AONContext ctx, final InvoiceDetail detail) {
		detail.getSource().visit(new InvoiceSourceVisitor<Void>() {
			
			@Override public Void visitSales() {return null;}
			@Override public Void visitReservation() {return null;}
			@Override public Void visitPurchase() {return null;}
			@Override public Void visitOffer() {return null;}
			@Override public Void visitIncome() {return null;}
			@Override public Void visitFee() {return null;}
			@Override public Void visitDirectInvoice() {return null;}
			@Override public Void visitDirectExpense() {return null;}
			@Override public Void visitDelivery() {return null;}
			
			@Override 
			public Void visitAccount() {
				int count = ctx.getDslContext()
					.delete(INVOICE_DETAIL_ACCOUNT)
					.where(INVOICE_DETAIL_ACCOUNT.INVOICE_DETAIL.eq(detail.getId()))
					.execute();
				ctx.log().debug("DELETE INVOICE_DETAIL_ACCOUNT ({0} filas.)",count);
				
				ctx.getDslContext().select(INVOICE_TAX.ID)
					.from(INVOICE_TAX)
					.where(INVOICE_TAX.INVOICE_DETAIL.eq(detail.getId()))
					.fetch()
					.stream()
					.mapToInt(rec -> rec.getValue(INVOICE_TAX.ID))
					.forEach(id -> {
						int x = ctx.getDslContext()
								.delete(INVOICE_TAX_ACCOUNT)
								.where(INVOICE_TAX_ACCOUNT.INVOICE_TAX.eq(id))
								.execute();
						ctx.log().debug("DELETE INVOICE_TAX_ACCOUNT ({0} filas.)",x);
					});
				return null;
			}
			
			@Override 
			public Void visitTedi() {
				visitAccount();
				return null;
			}
		});
	}
	
	private static InvoiceDetail saveDetail(AONContext ctx, Invoice invoice, InvoiceDetail invoiceDetail) {
		InvoiceDetailAutoComplete.complete(ctx, invoice, invoiceDetail);
		InvoiceDetailValidation.validate(invoiceDetail);
		return (invoiceDetail.getId() != null)
			? updateDetail(ctx, invoice, invoiceDetail)
			: insertDetail(ctx, invoice, invoiceDetail);
	}

	private static InvoiceDetail updateDetail(AONContext ctx, Invoice invoice, InvoiceDetail invoiceDetail) {
		ctx.getDslContext().update(INVOICE_DETAIL)
			.set(INVOICE_DETAIL.DOMAIN, invoiceDetail.getDomain())
			.set(INVOICE_DETAIL.INVOICE, invoiceDetail.getInvoice())
			.set(INVOICE_DETAIL.INVEST_ASSET, invoiceDetail.getInvestAsset().map(InvestAsset::getId).orElse(null))
			.set(INVOICE_DETAIL.PROJECT, invoiceDetail.getProject())
			.set(INVOICE_DETAIL.LINE, invoiceDetail.getLine())
			.set(INVOICE_DETAIL.ITEM, invoiceDetail.getItem())
			.set(INVOICE_DETAIL.DESCRIPTION, invoiceDetail.getDescription())
			.set(INVOICE_DETAIL.QUANTITY, invoiceDetail.getQuantity())
			.set(INVOICE_DETAIL.PRICE, invoiceDetail.getPrice())
			.set(INVOICE_DETAIL.DISCOUNT_EXPR, invoiceDetail.getDiscountExpression().map(DiscountExpression::getDiscountExpr).orElse(null))
			.set(INVOICE_DETAIL.SOURCE, InvoiceSource.value(invoiceDetail.getSource()))
			.set(INVOICE_DETAIL.SOURCE_ID, invoiceDetail.getSourceId())
			.set(INVOICE_DETAIL.TAXABLE_BASE, invoiceDetail.getTaxableBase())
			.set(INVOICE_DETAIL.TAXES, invoiceDetail.getTaxes())
			.set(INVOICE_DETAIL.PREPAYMENT, AonEnumUtils.getByte(invoiceDetail.isPrepayment()))
			.set(INVOICE_DETAIL.SELLER, invoiceDetail.getSeller().map(Seller::getId).orElse(null))
			.set(INVOICE_DETAIL.WORKPLACE, invoiceDetail.getWorkplace())
			.set(INVOICE_DETAIL.WAREHOUSE, invoiceDetail.getWarehouse())
			.set(INVOICE_DETAIL.MODIFICATION_USER ,ctx.getUser())
			.set(INVOICE_DETAIL.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()))
			.where(INVOICE_DETAIL.ID.eq(invoiceDetail.getId()))
			.execute();
		saveTax(ctx, invoice, invoiceDetail);
		return invoiceDetail;
	}

	private static InvoiceDetail insertDetail(AONContext ctx, Invoice invoice, InvoiceDetail invoiceDetail) {
		Integer id = ctx.getDslContext().insertInto(INVOICE_DETAIL)
			.set(INVOICE_DETAIL.DOMAIN, invoiceDetail.getDomain())
			.set(INVOICE_DETAIL.INVOICE, invoiceDetail.getInvoice())
			.set(INVOICE_DETAIL.INVEST_ASSET, invoiceDetail.getInvestAsset().map(InvestAsset::getId).orElse(null))
			.set(INVOICE_DETAIL.PROJECT, invoiceDetail.getProject())
			.set(INVOICE_DETAIL.LINE, invoiceDetail.getLine())
			.set(INVOICE_DETAIL.ITEM, invoiceDetail.getItem())
			.set(INVOICE_DETAIL.DESCRIPTION, invoiceDetail.getDescription())
			.set(INVOICE_DETAIL.QUANTITY, invoiceDetail.getQuantity())
			.set(INVOICE_DETAIL.PRICE, invoiceDetail.getPrice())
			.set(INVOICE_DETAIL.DISCOUNT_EXPR, invoiceDetail.getDiscountExpression().map(DiscountExpression::getDiscountExpr).orElse(null))
			.set(INVOICE_DETAIL.SOURCE, InvoiceSource.value(invoiceDetail.getSource()))
			.set(INVOICE_DETAIL.SOURCE_ID, invoiceDetail.getSourceId())
			.set(INVOICE_DETAIL.TAXABLE_BASE, invoiceDetail.getTaxableBase())
			.set(INVOICE_DETAIL.TAXES, invoiceDetail.getTaxes())
			.set(INVOICE_DETAIL.PREPAYMENT, AonEnumUtils.getByte(invoiceDetail.isPrepayment())) 
			.set(INVOICE_DETAIL.SELLER, invoiceDetail.getSeller().map(Seller::getId).orElse(null))
			.set(INVOICE_DETAIL.WORKPLACE, invoiceDetail.getWorkplace())
			.set(INVOICE_DETAIL.WAREHOUSE, invoiceDetail.getWarehouse())
			.set(INVOICE_DETAIL.CREATION_USER ,ctx.getUser())
			.set(INVOICE_DETAIL.CREATION_DATE, new Timestamp( System.currentTimeMillis()))
			.set(INVOICE_DETAIL.MODIFICATION_USER ,ctx.getUser())
			.set(INVOICE_DETAIL.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()))
			.returning(INVOICE_DETAIL.ID).fetchOne().getId();
		ctx.log().debug("\tINSERT INVOICE_DETAIL detalles invoice: {0}",invoiceDetail.getId());
		invoiceDetail.setId(id);
		saveTax(ctx, invoice, invoiceDetail);
		afterInsert(ctx, invoice, invoiceDetail);
		return invoiceDetail;
	}
	
	private static void afterInsert(AONContext ctx, Invoice invoice, InvoiceDetail detail) {
		detail.getSource().visit(new InvoiceSourceVisitor<Void>() {
			
			@Override public Void visitSales() { return null; }
			@Override public Void visitReservation() {return null; }
			@Override public Void visitPurchase() {return null; }
			@Override public Void visitOffer() {return null; }
			@Override public Void visitIncome() {return null; }
			@Override public Void visitFee() {return null; }
			@Override public Void visitDirectInvoice() {return null; }
			@Override public Void visitDirectExpense() {return null; }
			@Override public Void visitDelivery() {return null; }
			
			private Void saveAcountingTables() {
				saveInvoiceDetailAccount(ctx, detail);
				detail.taxStream()
					.forEach( tax -> saveInvoiceTaxAccount(ctx,invoice, detail, tax));
				return null;
			}
			
			@Override 
			public Void visitAccount() {
				if (detail.getExpAccount().isEmpty()) {
					throw new AonCoreException(AonError.ACCOUNT_ENTRY_NO_EXP_ACCOUNT.getMessage());
				}
				return saveAcountingTables();
			}
			
			@Override 
			public Void visitTedi() {
				detail.getExpAccount()
					.ifPresent(a -> saveAcountingTables()) 
				;
				return null;
			}
			
			private void saveInvoiceDetailAccount(AONContext ctx, InvoiceDetail invoiceDetail) {
				ctx.getDslContext()
					.delete(INVOICE_DETAIL_ACCOUNT)
					.where(INVOICE_DETAIL_ACCOUNT.INVOICE_DETAIL.eq(invoiceDetail.getId()))
					.and(INVOICE_DETAIL_ACCOUNT.DOMAIN.eq(invoiceDetail.getDomain()))
					.execute();
				ctx.log().debug("\tDELETE INVOICE_DETAIL_ACCOUNT");
				ctx.getDslContext().insertInto(INVOICE_DETAIL_ACCOUNT)
					.set(INVOICE_DETAIL_ACCOUNT.DOMAIN, invoiceDetail.getDomain())
					.set(INVOICE_DETAIL_ACCOUNT.INVOICE_DETAIL, invoiceDetail.getId())
					.set(INVOICE_DETAIL_ACCOUNT.ACCOUNT, invoiceDetail.getExpAccount().map(Account::getId).orElse(null))
					.execute();
				ctx.log().debug("\tINSERT INVOICE_DETAIL_ACCOUNT");
			}

			private void saveInvoiceTaxAccount(AONContext ctx, Invoice invoice, InvoiceDetail invoiceDetail, InvoiceTax invoiceTax) {
				if (invoiceDetail.isTaxEnabled(invoice) ) {
					Integer accountId = (invoice.getHeader().isSales()
								?invoiceTax.getOutputAccount()
								:invoiceTax.getInputAccount() )
							.map( Account::getId )
							.orElse( invoiceDetail.getExpAccount().map(Account::getId).orElse(null) );
					ctx.getDslContext().insertInto(INVOICE_TAX_ACCOUNT)
						.set(INVOICE_TAX_ACCOUNT.DOMAIN,invoiceDetail.getDomain())
						.set(INVOICE_TAX_ACCOUNT.INVOICE_TAX, invoiceTax.getId())
						.set(INVOICE_TAX_ACCOUNT.ACCOUNT, accountId )
					.execute();
					ctx.log().debug("\t\tINSERT INVOICE_TAX_ACCOUNT");
				} else {
					ctx.log().debug("\t\tSKIPPING INVOICE TAX ACCOUNT CREATION ({0})",
							(invoiceDetail.isPrepayment()?"PREPAYMENT":"UNDEDUCTIBLE INVOICE"));
				}
			}

		});
	}
	
	private class InvoiceDetailAutoComplete {
		private static final String DETAIL_MSG = "Fra. n\u00AA: {0} del {1,date,dd/MM/yyyy}. ";
		
		private InvoiceDetailAutoComplete() {
		}

		static void complete(AONContext ctx, Invoice invoice, InvoiceDetail detail) {
			
			detail.setInvoice(invoice.getId());
			detail.setDomain(invoice.getDomain());
			
			if (AonStringUtils.isBlank(detail.getDescription()) && detail.getSource() == InvoiceSource.ACCOUNT) {
				detail.setDescription(MessageFormat.format(DETAIL_MSG, invoice.getHeader().getReferenceCode(), invoice.getHeader().getIssueDate()));	
			}
			if (detail.getWorkplace() == null) {
				ctx.getDefaultWorkplace( invoice.getDomain() )
					.map(Workplace::getId)
					.ifPresent( detail::setWorkplace);
			}
		}
	}
	
	private class InvoiceDetailValidation {
		
		private InvoiceDetailValidation() {
		}

		static void validate(InvoiceDetail detail) {
			if (detail.getSource() == null ) {
				throw new AonCoreException(AonError.INVOICE_EMPTY_SOURCE.getMessage());
			}
			
			if(detail.getWorkplace() == null) {
				throw new AonCoreException(AonError.INVOICE_EMPTY_WORKPLACE.getMessage());
			}
		}

	}
	// *************************************************
	// ************* INVOICE TAX ***********************
	// *************************************************
	private static InvoiceDetail saveTax(AONContext ctx, Invoice invoice, InvoiceDetail detail) {
		detail.taxStream()
			.forEach(invoiceTax -> saveTax(ctx, invoice, detail, invoiceTax ));
		return detail;
	}
	
	private static InvoiceTax saveTax(AONContext ctx, Invoice invoice, InvoiceDetail detail, InvoiceTax invoiceTax) {
		if (detail.isTaxEnabled(invoice) ) {
			InvoiceTaxAutoComplete.complete(detail, invoiceTax);
			InvoiceTaxValidation.validate(invoice, detail, invoiceTax);
			invoiceTax = invoiceTax.getId() != null 
				? updateTax(ctx, invoiceTax)
				: insertTax(ctx, invoiceTax);
		} else {
			if (invoiceTax.getId() != null) {
				deleteTax(ctx, invoiceTax.getId());
				ctx.log().debug("\t\tDELETE INVOICE TAX NO TAX ALLOWED");
			} else {
				ctx.log().debug("\t\tSKIPPING INVOICE TAX CREATION ({0})",(detail.isPrepayment()? "PREPAYMENT": "UNDEDUCTIBLE INVOICE"));
			}
		}

		return invoiceTax;
	}
	
	private static void deleteTax(AONContext ctx, Integer invoiceTaxId){
		ctx.getDslContext().delete(INVOICE_TAX)
			.where(INVOICE_TAX.ID.eq(invoiceTaxId))
			.execute();
	}
	
	
	private static void deleteTaxes(AONContext ctx, InvoiceDetail detail){
		ctx.getDslContext().delete(INVOICE_TAX)
			.where(INVOICE_TAX.INVOICE_DETAIL.eq(detail.getId()))
			.execute();
	}
	
	
	private static InvoiceTax updateTax(AONContext ctx, InvoiceTax invoiceTax) {
		ctx.getDslContext().update(INVOICE_TAX)
			.set(INVOICE_TAX.DOMAIN, invoiceTax.getDomain())
			.set(INVOICE_TAX.INVOICE_DETAIL, invoiceTax.getInvoiceDetail())
			.set(INVOICE_TAX.TAX_TYPE, TaxType.value(invoiceTax.getTaxType()))
			.set(INVOICE_TAX.BASE,invoiceTax.getBase())
			.set(INVOICE_TAX.PERCENTAGE,invoiceTax.getPercentage())
			.set(INVOICE_TAX.QUOTA,invoiceTax.getQuota())
			.set(INVOICE_TAX.SURCHARGE,invoiceTax.getSurcharge())
			.set(INVOICE_TAX.SURCHARGE_QUOTA,invoiceTax.getSurchargeQuota())
			.set(INVOICE_TAX.VAT_DEDUCTION_TYPE, VatDeductionType.value(invoiceTax.getVatDeductionType()))
			.set(INVOICE_TAX.WITHHOLDING_TYPE,WithholdingType.value(invoiceTax.getWithholdingType()))
			.set(INVOICE_TAX.DEDUCTIBLE_PERCENT,invoiceTax.getDeductiblePercent())
			.set(INVOICE_TAX.DEDUCTIBLE_QUOTA ,invoiceTax.getDeductibleQuota())
			.where(INVOICE_TAX.ID.eq(invoiceTax.getId()))
			.execute();
		ctx.log().debug("\t\tUPDATE INVOICE TAX");
		return invoiceTax;
	}
	
	private static InvoiceTax insertTax(AONContext ctx, InvoiceTax invoiceTax) {
		Integer id = ctx.getDslContext().insertInto(INVOICE_TAX)
			.set(INVOICE_TAX.DOMAIN, invoiceTax.getDomain())
			.set(INVOICE_TAX.INVOICE_DETAIL, invoiceTax.getInvoiceDetail())
			.set(INVOICE_TAX.TAX_TYPE, TaxType.value(invoiceTax.getTaxType()))
			.set(INVOICE_TAX.BASE,invoiceTax.getBase())
			.set(INVOICE_TAX.PERCENTAGE,invoiceTax.getPercentage())
			.set(INVOICE_TAX.QUOTA,invoiceTax.getQuota())
			.set(INVOICE_TAX.SURCHARGE,invoiceTax.getSurcharge())
			.set(INVOICE_TAX.SURCHARGE_QUOTA,invoiceTax.getSurchargeQuota())
			.set(INVOICE_TAX.VAT_DEDUCTION_TYPE, VatDeductionType.value(invoiceTax.getVatDeductionType()))
			.set(INVOICE_TAX.WITHHOLDING_TYPE,WithholdingType.value(invoiceTax.getWithholdingType()))
			.set(INVOICE_TAX.DEDUCTIBLE_PERCENT,invoiceTax.getDeductiblePercent())
			.set(INVOICE_TAX.DEDUCTIBLE_QUOTA ,invoiceTax.getDeductibleQuota())
			.returning(INVOICE_TAX.ID)
			.fetchOne()
			.getId();
		ctx.log().debug("\t\tINSERT INVOICE TAX");
		return invoiceTax.setId(id);
	}	
	
	private class InvoiceTaxAutoComplete {
		
		private InvoiceTaxAutoComplete() {
		}
		
		static void complete(InvoiceDetail detail, InvoiceTax tax) throws AonCoreException {
			tax.setDomain(detail.getDomain())
				.setInvoiceDetail(detail.getId());
		}
	}
	
	private class InvoiceTaxValidation {
		
		private InvoiceTaxValidation() {
		}
		
		static void validate(Invoice inv, InvoiceDetail detail, InvoiceTax tax) throws AonCoreException {
			if ( AonNumberUtils.notEquals( detail.getId(), tax.getInvoiceDetail()) ) {
				inv.addMessage( InvoiceErrorMessages.C511.err(InvoiceErrorKey.TAX_RATE, "InvoiceTax", "InvoiceDetail") );
				throw new AonCoreException(AonError.INVOICE_SAVE_ERROR.getMessage());
			}
			
			if ( AonNumberUtils.notEquals( detail.getDomain(), tax.getDomain()) ) {
				inv.addMessage( InvoiceErrorMessages.C510.err(InvoiceErrorKey.TAX_RATE, "InvoiceTax", "InvoiceDetail") );
				throw new AonCoreException(AonError.INVOICE_SAVE_ERROR.getMessage());
			}
			
		}

	}
	
	// *************************************************
	// ************* INVOICE ATTACH ********************
	// *************************************************
	private static void insertInvoiceAttach(AONContext ctx, Invoice invoice) {
		invoice.getAttach()
			.ifPresent( attach -> {
				try {
					InvoiceAttachAutoComplete.completeInvoiceAttach(invoice);
					attach.setDescription("Factura");
					if (attach.getData() == null && attach.getAttachURL() != null) {
						if ( invoice.isFromRawdoc()) {
							RawdocHandler.getFull(ctx, invoice.getRawdocId())
								.ifPresent( rawdoc -> attach.setData( rawdoc.getData() ));
						} else {
							URI uri = new URI(attach.getAttachURL());
							URLConnection conn = uri.toURL().openConnection();
							conn.connect();
							try (InputStream in = new BufferedInputStream(conn.getInputStream()))  {
								attach.setData( AonIOUtils.toByteArray(in) );
							}
						}
					}
					
					if (attach.getData() != null && !invoice.isFromRawdoc()) {
						String data = new String(attach.getData(),0,4);
						if (AonStringUtils.startsWith(data, "data:")) {
							DataUrlSerializer serializer = new DataUrlSerializer();
							DataUrl unserialized = serializer.unserialize(data);
							attach.setData( unserialized.getData() );
						}
					}
					
					if ( invoice.isFromRawdoc()) {
						RawdocHandler.delete(ctx, invoice.getDomain(), invoice.getRawdocId());
					}
					
					if (attach.getData() != null) {
						Integer attachId = AttachmentHandler.insertInvoiceAttach(ctx, attach);
						attach.setId(attachId);
						ctx.log().debug("INSERT INVOICE ATTACH (invoice: {0} id : {1})",attach.getAttachModule(),attachId);
					} else {
						ctx.log().debug("INSERT INVOICE ATTACH (NO NEEDED - NO DATA)");
					}
				} catch (URISyntaxException | IOException e) {
					invoice.addMessage( InvoiceErrorMessages.C021.err(InvoiceErrorKey.ATTACH) );
					throw new AonCoreException("No se pudo grabar la factura",  e ); 
				} 
		});
	}
	
	
	private static class InvoiceAttachAutoComplete {
		
		static void completeInvoiceAttach(Invoice inv) throws AonCoreException {
			inv.getAttach().ifPresent( a ->  a
				.setDomain( inv.getDomain())
				.setAttachModule(inv.getId())
				.setDate(inv.getHeader().getIssueDate())
				.setAttachType( AttachType.INVOICE )
				.setType( InvoiceAttachmentType.INVOICE.value() )
			);
		}
		
	}
	
	// *************************************************
	// ********** TEST PURPOSE METHODS *****************
	// *************************************************
	static Optional<Invoice> getRandom(AONContext ctx, int domain, InvoiceFilter filter) {
		return select(ctx, domain)
			.and(InvoiceHandler.INVOICE_PROPERTIES.getCondition(filter))
			.orderBy( DSL.rand() )
			.fetch()
			.stream()
			.map( new InvoiceFiller() )
			.map(i -> InvoiceDetailHandler.fillInvoice(ctx,i))
			.map(i -> FinanceHandler.fillInvoice(ctx, domain, i))
			.map(i -> InvoiceAddressHandler.fillInvoice(ctx, i))
			.map(Invoice::refreshTaxBreakdown)
			.findFirst();
	}

/*

	private static final BiConsumer<AONContext, Invoice> BUILD_ADDRESS = (ctx, invoice) -> invoice.setInvoiceAddress(InvoiceAddressHandler.get(ctx, invoice).orElse(null));
	
	private static final BiConsumer<AONContext, Invoice> BUILD_DETAILS = (ctx, invoice) -> {
		invoice.deleteDetails();
		InvoiceDetailHandler.stream(ctx,invoice.getId()).forEach(d -> invoice.addDetail(d, false));
	};

	private static Invoice fullInvoiceBuilder(AONContext ctx, Invoice invoice) {
		if (invoice == null) return null;
		BUILD_ADDRESS
			.andThen(BUILD_DETAILS)
//			.andThen(BUILD_TAX_BREAKDOWN)
//			.andThen(BUILD_FINANCES)
//			.andThen(BUILD_ATTACH)
			.accept(ctx,invoice);
		//return invoice.calculateTaxBreakdown();
		return invoice;
	}
	
*/	
	
	// *******************************************************************
	// *******************************************************************
	// *******************************************************************
	// *******************************************************************
	// *******************************************************************
	
	
/*	
	
	public static final Date VAT_ACCRUAL_START_DATE = AonDateUtils.getDate(2014, 0, 1);


	

	
//	private static final BiConsumer<AONContext, Invoice> BUILD_TAX_BREAKDOWN = (ctx, invoice) -> 
//		AonCollectionUtils.stream(invoice.getDetails())
//			.flatMap(detail -> AonCollectionUtils.stream(detail.getInvoiceTaxes()))
//			.forEach( it -> invoice.addTax(it));

	private static final BiConsumer<AONContext, Invoice> BUILD_FINANCES = (ctx, invoice) -> invoice.setFinances(FinanceDAO.getInvoiceFinances(ctx, invoice.getId()));
	
	private static final BiConsumer<AONContext, Invoice> BUILD_ATTACH = (ctx, invoice) -> 
		invoice.setAttach(
			AttachmentDAO.getInvoiceAttachStream(ctx
				, f -> f.getAttachModuleProperty().eq(invoice.getId())
					.and(f.getTypeProperty().eq( InvoiceAttachmentType.INVOICE.value() ) )
				, false)
				.findFirst()
				.orElse(null)
		);

	private static void insertInvoiceAttach(AONContext ctx, Invoice invoice) {
		if (invoice.getAttach().isPresent()) {
			try {
				InvoiceAttachAutoComplete.completeInvoiceAttach(ctx, invoice);
				Attach attach = invoice.getAttach().get();
				attach.setDescription("Factura");
				
				if (attach.getData() == null && attach.getAttachURL() != null) {
					if ( invoice.isFromRawdoc()) {
						Rawdoc rawdoc = RawdocDAO.getFull(ctx, invoice.getRawdocId());
						if (rawdoc != null) attach.setData( rawdoc.getData() );
					} else {
						URI uri = new URI(attach.getAttachURL());
						URLConnection conn = uri.toURL().openConnection();
						conn.connect();
						try (InputStream in = new BufferedInputStream(conn.getInputStream()))  {
							attach.setData( AonIOUtils.toByteArray(in) );
						}
					}
				}
				
				if (attach.getData() != null && !invoice.isFromRawdoc()) {
					String data = new String(attach.getData(),0,4);
					if (AonStringUtils.startsWith(data, "data:")) {
						DataUrlSerializer serializer = new DataUrlSerializer();
						DataUrl unserialized = serializer.unserialize(data);
						attach.setData( unserialized.getData() );
					}
				}
				
				if ( invoice.isFromRawdoc()) {
					RawdocDAO.delete(ctx, invoice.getDomain(), invoice.getRawdocId());
				}
				
				if (attach.getData() != null) {
					Integer attachId = AttachmentDAO.insertInvoiceAttach(ctx, attach);
					attach.setId(attachId);
					ctx.log().debug("INSERT INVOICE ATTACH (invoice: {0} id : {1})",attach.getAttachModule(),attachId);
				} else {
					ctx.log().debug("INSERT INVOICE ATTACH (NO NEEDED - NO DATA)");
				}
			} catch (URISyntaxException | IOException e) {
				invoice.addMessage( InvoiceErrorMessages.C021.err(InvoiceErrorKey.ATTACH) );
				throw new AonCoreException("No se pudo grabar la factura",  e ); 
			} 
		}
		
	}
	
	public static void delete(AONContext ctx, Integer id) {
		ctx.checkWrite();
		Invoice invoice = getFull(ctx, id)
			.orElseThrow(() -> new AonCoreException(AonError.INVOICE_NOT_FOUND.getMessage()));
		InvoiceValidation.validateDeletion(ctx, invoice);
		InvoiceHandler.onDeleteRectifier(ctx, invoice);
		InvoiceDetailDAO.deleteInvoice(ctx, invoice.getId());
		InvoiceHandler.onDeleteDUA(ctx, invoice);
		
		int count = ctx.getDslContext()
			.delete(INVOICE_ATTACH)
			.where(INVOICE_ATTACH.INVOICE.equal(id))
			.execute();
		ctx.log().debug("DELETE INVOICE_ATTACH adjuntos de la factura: {0} ({1} filas)",id,count);
		
		FinanceDAO.deleteInvoiceFinances(ctx,id);
		InvoiceFiscalDAO.delete(ctx, id);
		InvoiceAddressDAO.delete(ctx, id);
		InvoiceBatchDetailDAO.delete(ctx, f-> f.getInvoiceProperty().eq(id));
		InvoiceInfoDAO.delete(ctx, f-> f.getInvoiceProperty().eq(id));
		
		count = ctx.getDslContext()
			.delete(INVOICE)
			.where(INVOICE.ID.equal(id))
			.execute();
		ctx.log().debug("DELETE INVOICE factura: {0} ({1} filas)",id,count);

		// ONLY IF IS TICKET BAI.
		TbaiConfiguration tbaiConfiguration = TbaiConfigurationDAO.get(ctx);
		if(tbaiConfiguration.isActive() && invoice.getNumber() > 0) {
			saveInvoiceTracking(ctx, invoice, InvoiceTrackingStatus.DELETED);
		}
	}
	
	private static void onDeleteRectifier(AONContext ctx, Invoice invoice) {
		if (invoice.isRectifier() && invoice.getRectificationInvoiceId() != null) {
			final InvoiceMin rectified = get(ctx, invoice.getRectificationInvoiceId())
					.orElseThrow( () -> new AonCoreException(AonError.INVOICE_RECTIFIED_NOT_FOUND.getMessage()));
			if (rectified.getRectificationInvoiceId() != null &&
				AonNumberUtils.equals(invoice.getId(), rectified.getRectificationInvoiceId())) {
				// La factura rectificada, solo lo esta una vez, y es por la factura que estamos borrando.
				// Luego marcamos la factura rectificada como "NO RECTIFICADA".
				ctx.getDslContext().update(INVOICE)
					.set(INVOICE.RECTIFICATION_TYPE, RectificationType.NONE.value())
					.set(INVOICE.RECTIFICATION_INVOICE, (Integer) null)
					.set(INVOICE.MODIFICATION_USER,ctx.getUser())
					.set(INVOICE.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()) )
					.where(INVOICE.ID.equal( rectified.getId() ))
					.execute();
				ctx.log().debug("UPDATE INVOICE (factura rectificada, se marca como NO RECTIFICADA - SOLO UNA): {0}",rectified.getId());
			} else {
				// En la factura rectificada no hay constancia de cual es la factura que la 
				// rectifica, por lo tanto puede haber mas de una.
				
				rectified.setRectificationType(RectificationType.NONE);  // Si no entra en el buble, no quedan facturas rectificativas.
				
				stream(ctx, p -> p.getDomainProperty().eq(ctx.getDomainId())
					.and(p.getRectificationInvoiceProperty().eq(invoice.getRectificationInvoiceId()))
					.and(p.getIdProperty().ne(invoice.getId()))
				)
				.forEach( rectifier -> {
					if (rectified.getRectificationInvoiceId() == null) {
						// Primera iteracion.
						rectified.setRectificationType(RectificationType.RECTIFIED);							
						rectified.setRectificationInvoiceId(rectifier.getId());
					} else {
						// Segunda iteracion y sucesivas. Hay mas de una, debe continuar a null.
						rectified.setRectificationInvoiceId(null);
					}
				});
				
				ctx.getDslContext().update(INVOICE)
					.set(INVOICE.RECTIFICATION_TYPE, rectified.getRectificationType().value())
					.set(INVOICE.RECTIFICATION_INVOICE, rectified.getRectificationInvoiceId())
					.set(INVOICE.MODIFICATION_USER,ctx.getUser())
					.set(INVOICE.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()) )
					.where(INVOICE.ID.equal( rectified.getId() ))
					.execute();
				ctx.log().debug("UPDATE INVOICE (factura rectificada, se marca como NO RECTIFICADA - MAS DE UNA): {0}",rectified.getId());
			}
		}
	}
	
	private static void onDeleteDUA(AONContext ctx, Invoice invoice) {
		if ( invoice.isDUAAllowed() ) {
			 Integer importInvoice = ctx.getDslContext()
				.select(INVOICE_DUA.INVOICE_IMPORT)
				.from(INVOICE_DUA)
				.where(INVOICE_DUA.INVOICE_NATIONAL.equal(invoice.getId()))
				.and(INVOICE_DUA.DOMAIN.eq(invoice.getDomain()))
				.fetch()
				.stream()
				.map( rec -> rec.getValue(INVOICE_DUA.INVOICE_IMPORT))				
				.findFirst()
				.orElse(null);
			if (importInvoice != null) {
				ctx.log().debug("\tDUA LINKED");
				ctx.getDslContext()
					.select(INVOICE_DETAIL.TAXABLE_BASE, INVOICE_TAX.ID,INVOICE_TAX.PERCENTAGE,INVOICE_TAX.SURCHARGE)
					.from(INVOICE_DETAIL)
					.innerJoin(INVOICE_TAX).on(INVOICE_TAX.INVOICE_DETAIL.eq(INVOICE_DETAIL.ID))
					.where(INVOICE_DETAIL.INVOICE.eq(importInvoice))
					.and(INVOICE_DETAIL.DOMAIN.eq(invoice.getDomain()))
					.fetch()
					.stream()
					.forEach(rec -> {
						int taxId = rec.getValue(INVOICE_TAX.ID);
						double base = rec.getValue(INVOICE_DETAIL.TAXABLE_BASE);
						double percent = rec.getValue(INVOICE_TAX.PERCENTAGE);
						double surcharge = rec.getValue(INVOICE_TAX.SURCHARGE);
						double quota = AonMathUtils.round( base * percent / 100 );
						double surchargeQuota = AonMathUtils.round( base * surcharge / 100 );
						int count = ctx.getDslContext().update(INVOICE_TAX)
								.set(INVOICE_TAX.BASE, base )
								.set(INVOICE_TAX.QUOTA, quota )
								.set(INVOICE_TAX.SURCHARGE_QUOTA, surchargeQuota)
								.set(INVOICE_TAX.DEDUCTIBLE_PERCENT, 100.0)
								.set(INVOICE_TAX.DEDUCTIBLE_QUOTA, quota)
								.where(INVOICE_TAX.ID.equal( taxId ))
								.execute();
						ctx.log().debug("\tUPDATE INVOICE_TAX (RESTORE PREVIOUS INFO): {0} ({1} filas)",invoice.getId(),count);	
					});
				int count = ctx.getDslContext()
						.delete(INVOICE_DUA)
						.where(INVOICE_DUA.INVOICE_NATIONAL.equal(invoice.getId()))
						.execute();
				ctx.log().debug("\tDELETE INVOICE_DUA: {0} ({1} filas)",invoice.getId(),count);
			}
		}
	}
	
	private static void saveInvoiceTracking(AONContext ctx, Invoice invoice, InvoiceTrackingStatus status) {
		JSONObject json = InvoiceJSON.toJSON(invoice, true);
		ctx.getDslContext().insertInto(INVOICE_TRACKING)
			.set(INVOICE_TRACKING.ID, invoice.getId())
			.set(INVOICE_TRACKING.DOMAIN, invoice.getDomain())
			.set(INVOICE_TRACKING.SERIES, invoice.getSeries())
			.set(INVOICE_TRACKING.NUMBER, invoice.getNumber())
			.set(INVOICE_TRACKING.REFERENCE_CODE, invoice.getReferenceCode())
			.set(INVOICE_TRACKING.ISSUE_DATE, AonDateUtils.toSql(invoice.getIssueDate()))
			.set(INVOICE_TRACKING.RDOCUMENT, invoice.getRegistryDocument())
			.set(INVOICE_TRACKING.RNAME, invoice.getRegistryName())
			.set(INVOICE_TRACKING.STATUS, status.value())
			.set(INVOICE_TRACKING.TYPE, invoice.getType().value())
			.set(INVOICE_TRACKING.TOTAL, invoice.getTotal())
			.set(INVOICE_TRACKING.JSON, json.toString())
			.set(INVOICE_TRACKING.CREATION_USER, invoice.getCreationUser())
			.set(INVOICE_TRACKING.CREATION_DATE, AonDateUtils.toTimestamp(invoice.getCreationDate()))
			.set(INVOICE_TRACKING.MODIFICATION_USER, ctx.getUser())
			.set(INVOICE_TRACKING.MODIFICATION_DATE, AonDateUtils.toTimestamp(new Date()))
			.execute();
	}
	public static Invoice initialize(final AONContext ctx
			, final Integer domain
			, final InvoiceType type
			, final Integer registry) {
		return initialize(ctx, domain, type, registry, null, null  );
	}
	
	public static Invoice initialize(final AONContext ctx
			, final Integer domain
			, final InvoiceType type
			, final Integer registry
			, final Date issueDate
			, final Integer activity) {

			
			if (domain == null) throw new AonCoreException( AonError.INVOICE_EMPTY_DOMAIN.getMessage() );
			if (type == null) throw new AonCoreException( AonError.INVOICE_EMPTY_TYPE.getMessage() );
			if (registry == null) throw new AonCoreException(AonError.INVOICE_EMPTY_REGISTRY.getMessage() );
			
			if (ctx.getConfiguration().getFirstWorkplace().isEmpty()) {
				throw new AonCoreException( AonError.EMPTY_WORKPLACE.getMessage() );
			}
			
			Date invoiceDate = Optional.ofNullable(issueDate).orElse(new Date());
			
			EnterpriseActivity act = null;
			if (activity != null) {
				act = AonCollectionUtils.stream(ctx.getConfiguration().getActivities())
					.filter(a -> AonNumberUtils.equals(activity, a.getId()))
					.findAny()
					.orElse(null);
			}
			
			Invoice inv = new Invoice()
				.setDomain(domain)
				.setRecorded(false)
				.setRectificationType(RectificationType.NONE)
				.setConfidential(false)
				.setIssueDate(invoiceDate)
				.setTaxDate(invoiceDate)
				.setType(type)
				.setActivity(act )
				.setSeries(null)
				.setNumber(0)
				.setReferenceCode(null)
					.addFinance(new Finance()
						.setDueDate(invoiceDate)
						.setPayment(type != InvoiceType.SALES) 
						.setFinanceStatus(FinanceStatus.PENDING))
			;
			type.visit(inv, new InvoiceTypeInitializerVisitor(ctx, registry));
			initializeWithholding(ctx,inv);
			return inv;
			
		}
	
	private static final class InvoiceTypeInitializerVisitor implements IInvoiceTypeVisitor<Invoice> {
		private final AONContext ctx;
		private final Integer registry;

		private InvoiceTypeInitializerVisitor(AONContext ctx, Integer registry) {
			this.ctx = ctx;
			this.registry = registry;
		}

		@Override
		public Invoice visitPurchase(Invoice invoice) {
			SupplierFull sup = SupplierDAO.getFull(ctx, registry);
			return invoice
				.setRegistry(sup.getId())
				.setRegistryDocumentType(sup.getRegistry().getDocumentType())
				.setRegistryDocumentCountry(sup.getRegistry().getDocumentCountry())
				.setRegistryDocument(sup.getRegistry().getDocument())
				.setRegistryName(sup.getRegistry().getName())
				.setRegistryAccount( sup.getAccount() )
				.setScope(sup.getRegistry().getScope() )
				.setTransaction(sup.getRegistry().getTransaction())
				.setService( false )
				.setVatAccrualPayment(invoice.isNational()
					&& !invoice.getIssueDate().before(InvoiceHandler.VAT_ACCRUAL_START_DATE)
					&& ( ctx.getConfiguration().getCompany().isVatAccrualPayment() 
					  || sup.getRegistry().isVatAccrualPayment()))
				.setSurcharge(ctx.getConfiguration().getCompany().isSurcharge())
				.setWithholding(sup.getRegistry().isWithholding())
				.setWithholdingFarmer(sup.getRegistry().isWithholdingFarmer());
		}

		@Override
		public Invoice visitSales(Invoice invoice) {
			CustomerFull cus = CustomerDAO.getFull(ctx, registry);
			return invoice
				.setRegistry(cus.getId())
				.setRegistryDocumentType(cus.getRegistry().getDocumentType())
				.setRegistryDocumentCountry(cus.getRegistry().getDocumentCountry())
				.setRegistryDocument(cus.getRegistry().getDocument())
				.setRegistryName(cus.getRegistry().getName())
				.setRegistryAccount( cus.getAccount() )
				.setScope(cus.getRegistry().getScope() )
				.setTransaction(cus.getRegistry().getTransaction())
				.setService( false )
				.setVatAccrualPayment(invoice.isNational()
					&& !invoice.getIssueDate().before(InvoiceHandler.VAT_ACCRUAL_START_DATE)
					&& ctx.getConfiguration().getCompany().isVatAccrualPayment())
				.setSurcharge(cus.getRegistry().isSurcharge())
				.setWithholding(cus.getRegistry().isWithholding() && ctx.getConfiguration().getCompany().isWithholding())
				.setWithholdingFarmer(false)
				.setSeries(ctx.getConfiguration().getDefaultInvoiceSeries())
			;
		}

		@Override
		public Invoice visitExpenses(Invoice invoice) {
			CreditorFull cre = CreditorDAO.getFull(ctx, registry);
			return invoice
				.setRegistry(cre.getId())
				.setRegistryDocumentType(cre.getRegistry().getDocumentType())
				.setRegistryDocumentCountry(cre.getRegistry().getDocumentCountry())
				.setRegistryDocument(cre.getRegistry().getDocument())
				.setRegistryName(cre.getRegistry().getName())
				.setRegistryAccount( cre.getAccount() )
				.setScope(cre.getRegistry().getScope() )
				.setTransaction(cre.getRegistry().getTransaction())
				.setService( true )
				.setVatAccrualPayment(invoice.isNational()
					&& !invoice.getIssueDate().before(InvoiceHandler.VAT_ACCRUAL_START_DATE)
					&& ( ctx.getConfiguration().getCompany().isVatAccrualPayment() 
					  || cre.getRegistry().isVatAccrualPayment()))
				.setSurcharge(false)
				.setWithholding(cre.getRegistry().isWithholding())
				.setWithholdingFarmer(false)
			;
		}

		@Override
		public Invoice visitUndeductible(Invoice invoice) {
			return visitExpenses(invoice)
				.setSurcharge(false)
				.setWithholding(false)
				.setWithholdingFarmer(false)
				.setVatAccrualPayment(false);
		}
	}

	private static void initializeWithholding(AONContext ctx, Invoice inv) {
		if (inv.isWithholding()) {
			Account wa = null;
			InvoiceWithholding iw = inv.ensureWithholdingData();
			if (ctx.getConfiguration().getDefaultWithholdingPercent() != null) {
				iw.setPercentage(ctx.getConfiguration().getDefaultWithholdingPercent().getPercentage());
				iw.setWithholdingType(ctx.getConfiguration().getDefaultWithholdingPercent().getWithholdingType());
				wa = (inv.isSales())
					?ctx.getConfiguration().getDefaultWithholdingPercent().getSalesAccount()
					:ctx.getConfiguration().getDefaultWithholdingPercent().getPurchaseAccount();
			}
			if (wa == null) {
				wa = inv.isSales()
					?ctx.getConfiguration().accounting().getDefaultPaidRetAccount()
					:ctx.getConfiguration().accounting().getDefaultChargedRetAccount(); 
			}
			iw.setAccount(wa);
		}
	}
	
	public static Invoice validate(AONContext ctx, Invoice invoice) {
		InvoiceAutoComplete.completeInvoice(ctx, invoice);
		InvoiceValidation.validate(ctx, invoice);
		return invoice;
	}

*/
}




