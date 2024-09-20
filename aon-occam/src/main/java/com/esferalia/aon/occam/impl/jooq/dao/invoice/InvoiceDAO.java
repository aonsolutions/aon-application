package com.esferalia.aon.occam.impl.jooq.dao.invoice;

import static com.esferalia.aon.jooq.tables.Cnae2009.CNAE2009;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.Iae.IAE;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceAttach.INVOICE_ATTACH;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoiceDua.INVOICE_DUA;
import static com.esferalia.aon.jooq.tables.InvoiceFiscal.INVOICE_FISCAL;
import static com.esferalia.aon.jooq.tables.InvoiceTax.INVOICE_TAX;
import static com.esferalia.aon.jooq.tables.InvoiceTracking.INVOICE_TRACKING;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.SelectConditionStep;
import org.jooq.SelectOnConditionStep;
import org.jooq.impl.DSL;
import org.json.JSONObject;

import com.esferalia.aon.jooq.tables.records.InvoiceRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.json.invoice.InvoiceJSON;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.InvoiceCalculator;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Rawdoc;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.InvoiceAttachmentType;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IInvoiceTypeVisitor;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceBreakdown;
import com.esferalia.aon.occam.api.model.finance.InvoiceFilter;
import com.esferalia.aon.occam.api.model.finance.InvoiceMin;
import com.esferalia.aon.occam.api.model.finance.InvoiceProperties;
import com.esferalia.aon.occam.api.model.finance.InvoiceTrackingStatus;
import com.esferalia.aon.occam.api.model.finance.TbaiConfiguration;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorKey;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorMessages;
import com.esferalia.aon.occam.api.model.registry.CreditorFull;
import com.esferalia.aon.occam.api.model.registry.CustomerFull;
import com.esferalia.aon.occam.api.model.registry.SupplierFull;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.FinanceStatus;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.impl.jooq.dao.AttachmentDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO.EnterpriseActivityFiller;
import com.esferalia.aon.occam.impl.jooq.dao.CreditorDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CustomerDAO;
import com.esferalia.aon.occam.impl.jooq.dao.Filler;
import com.esferalia.aon.occam.impl.jooq.dao.FilterDAO;
import com.esferalia.aon.occam.impl.jooq.dao.FinanceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RawdocDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO.ScopeFiller;
import com.esferalia.aon.occam.impl.jooq.dao.SupplierDAO;
import com.esferalia.aon.occam.impl.jooq.dao.TbaiConfigurationDAO;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.io.AonIOUtils;
import com.esferalia.aon.watson.server.io.DataUrl;
import com.esferalia.aon.watson.server.io.DataUrlSerializer;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonEnumUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class InvoiceDAO {
	
	private InvoiceDAO() {

	}
	
	private static final com.esferalia.aon.jooq.tables.Invoice RECTIFICATION_INVOICE = INVOICE.as("RECTIFICATION_INVOICE");
	
	public static final Date VAT_ACCRUAL_START_DATE = AonDateUtils.getDate(2014, 0, 1);
	
	
	private static final InvoicePropertiesDAO INVOICE_PROPERTIES = new InvoicePropertiesDAO();
	private static class InvoicePropertiesDAO implements InvoiceProperties {
		
		private static final long serialVersionUID = -5116444114505029655L;
		
		public Condition[] getConditions(InvoiceFilter filter) {
			if (filter == null) {
				throw new AonCoreException( "Use some conditions.");
			}
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null)
				return new Condition[0];

			return new Condition[] { filterDAO.getCondition() };
		}
		
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.ID);}
		@Override public Property<Integer> getDomainProperty(){return new FilterDAO.PropertyDAO<>(INVOICE.DOMAIN);} 
		@Override public Property<Integer> getActivityProperty(){return new FilterDAO.PropertyDAO<>(INVOICE.ACTIVITY);} 
		@Override public Property<Integer> getInvestAssetProperty(){return new FilterDAO.PropertyDAO<>(INVOICE.INVEST_ASSET);}
		@Override public Property<Integer> getProjectProperty(){return new FilterDAO.PropertyDAO<>(INVOICE.PROJECT);}
		@Override public Property<String> getSeriesProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.SERIES);} 
		@Override public Property<Integer> getNumberProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.NUMBER);} 
		@Override public Property<String> getReferenceCodeProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.REFERENCE_CODE);} 
		@Override public Property<Integer> getRegistryProperty(){return new FilterDAO.PropertyDAO<>(INVOICE.REGISTRY);} 
		@Override public Property<String> getRegistryDocumentProperty(){return new FilterDAO.PropertyDAO<>(INVOICE.RDOCUMENT);} 
		@Override public Property<Byte> getRegistryDocumentTypeProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.RDOCUMENT_TYPE);} 
		@Override public Property<String> getRegistryDocumentCountryProperty(){return new FilterDAO.PropertyDAO<>(INVOICE.RDOCUMENT_COUNTRY);} 
		@Override public Property<String> getRegistryNameProperty(){return new FilterDAO.PropertyDAO<>(INVOICE.RNAME);}
		@Override public Property<Integer> getRegistryAddressProperty(){return new FilterDAO.PropertyDAO<>(INVOICE.RADDRESS);} 
		@Override public Property<java.util.Date> getIssueDateProperty() {return new FilterDAO.DatePropertyDAO(INVOICE.ISSUE_DATE);} 
		@Override public Property<java.util.Date> getTaxDateProperty() {return new FilterDAO.DatePropertyDAO(INVOICE.TAX_DATE);} 
		@Override public Property<Byte> getConfidentialProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.SECURITY_LEVEL);} 
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.STATUS);} 
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.TYPE);} 
		@Override public Property<Byte> getSurchargeProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.SURCHARGE);} 
		@Override public Property<Byte> getWithholdingProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.WITHHOLDING);} 
		@Override public Property<Byte> getWithholdingFarmerProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.WITHHOLDING_FARMER);}
		@Override public Property<Byte> getVatAccrualPayment() {return new FilterDAO.PropertyDAO<>(INVOICE.VAT_ACCRUAL_PAYMENT);} 
		@Override public Property<String> getCommentsProperty(){return new FilterDAO.PropertyDAO<>(INVOICE.COMMENTS);}
		@Override public Property<String> getRemarksProperty(){return new FilterDAO.PropertyDAO<>(INVOICE.REMARKS);}
		@Override public Property<Byte> getInvestmentProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.INVESTMENT);} 
		@Override public Property<Byte> getTransactionProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.TRANSACTION);} 
		@Override public Property<Byte> getSignedProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.SIGNED);}
		@Override public Property<Integer> getScopeProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.SCOPE);} 
		@Override public Property<Byte> getServiceProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.SERVICE);}
		@Override public Property<Byte> getRectificationTypeProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.RECTIFICATION_TYPE);} 
		@Override public Property<Integer> getRectificationInvoiceProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.RECTIFICATION_INVOICE);} 
		@Override public Property<Byte> getAdvanceProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.ADVANCE);}
		@Override public Property<Integer> getPosShiftProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.POS_SHIFT);} 
		@Override public Property<Integer> getSellerProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.SELLER);} 
		@Override public Property<Double> getTaxableBaseProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.TAXABLE_BASE);} 
		@Override public Property<Double> getVatQuotaProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.TOTAL);} 
		@Override public Property<Double> getRetentionQuotaTotalProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.TOTAL);} 
		@Override public Property<Double> getTotalProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.TOTAL);} 
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.CREATION_USER);} 
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.CREATION_DATE);} 
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.MODIFICATION_DATE);} 
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.MODIFICATION_USER);} 
	}

	// Field para que salgan ordenado primero compras,gastos y gastos no .ded y luego ventas.
	// En la select se complementa con invoice.type
	public static final Field<Integer> ORDERED_TYPE = DSL.decode()
		   .when(INVOICE.TYPE.equal((byte) 0), 0)
		   .when(INVOICE.TYPE.equal((byte) 1), 1)
		   .when(INVOICE.TYPE.equal((byte) 2), 0)
		   .when(INVOICE.TYPE.equal((byte) 3), 0);
	
	// ------------------------------------------------------------
	// ------------------------------------------------------------
	// ------------------------------------------- [PUBLIC METHODS]
	// ------------------------------------------------------------
	// ------------------------------------------------------------
	private static SelectOnConditionStep<Record> getInvoiceMinSelect(AONContext ctx ) {
		return ctx.getDslContext()
			.select(
				 INVOICE.ID
				,INVOICE.DOMAIN
				,INVOICE.ACTIVITY
				,INVOICE.TYPE
				,INVOICE.SERIES
				,INVOICE.NUMBER
				,INVOICE.REFERENCE_CODE
				,INVOICE.TRANSACTION
				,INVOICE.ISSUE_DATE
				,INVOICE.TAX_DATE
				,INVOICE.REGISTRY
				,INVOICE.RDOCUMENT
				,INVOICE.RDOCUMENT_TYPE
				,INVOICE.RDOCUMENT_COUNTRY
				,INVOICE.RNAME
				,INVOICE.SCOPE
				,INVOICE.SECURITY_LEVEL
				,INVOICE.STATUS
				,INVOICE.RECTIFICATION_TYPE
				,INVOICE.RECTIFICATION_INVOICE
				,INVOICE.TOTAL
			)
			.select(
				 IAE.EPIGRAPH
				,ENTERPRISE_ACTIVITY.DESCRIPTION
				,ORDERED_TYPE
			)
			.from(INVOICE)
			.leftOuterJoin(ENTERPRISE_ACTIVITY).on(INVOICE.ACTIVITY.eq(ENTERPRISE_ACTIVITY.ID))
			.leftOuterJoin(IAE).on(IAE.ID.equal(ENTERPRISE_ACTIVITY.IAE))
			.leftOuterJoin(CNAE2009).on(CNAE2009.ID.eq(ENTERPRISE_ACTIVITY.CNAE2009))
			;		
	}
	
	public static Stream<InvoiceMin> stream(AONContext ctx,InvoiceFilter filter) {
		return stream(ctx,filter,0,Integer.MAX_VALUE);
	}
	
	public static Stream<InvoiceMin> stream(AONContext ctx,InvoiceFilter filter, int offset , int numberOfRows) {
		ctx.checkRead();
		return getInvoiceMinSelect(ctx)
			.where(INVOICE_PROPERTIES.getConditions(filter))
			.orderBy(ORDERED_TYPE,INVOICE.TYPE,INVOICE.ISSUE_DATE.desc(),INVOICE.REFERENCE_CODE)
			.limit(offset,numberOfRows)
			.fetch()
			.stream()
			.map(new InvoiceMinFiller());
	}

	private static SelectOnConditionStep<Record> getInvoiceSelect(AONContext ctx ) {
		return ctx.getDslContext()
			.select()
			.from(INVOICE)
			.join(SCOPE).on(SCOPE.ID.equal(INVOICE.SCOPE))
			.leftOuterJoin(INVOICE_FISCAL).on(INVOICE_FISCAL.INVOICE.equal(INVOICE.ID))
			.leftOuterJoin(ENTERPRISE_ACTIVITY).on(INVOICE.ACTIVITY.eq(ENTERPRISE_ACTIVITY.ID))
			.leftOuterJoin(IAE).on(IAE.ID.equal(ENTERPRISE_ACTIVITY.IAE))
			.leftOuterJoin(CNAE2009).on(CNAE2009.ID.eq(ENTERPRISE_ACTIVITY.CNAE2009))
			.leftOuterJoin(RECTIFICATION_INVOICE).on(INVOICE.RECTIFICATION_INVOICE.equal(RECTIFICATION_INVOICE.ID));
	}
	
	public static Optional<InvoiceMin> get(AONContext ctx, Integer id) {
		ctx.checkRead();
		return getInvoiceMinSelect(ctx)
			.where(INVOICE.ID.eq(id))
			.fetch()
			.stream()
			.map( new InvoiceMinFiller() )
			.findFirst();
	}
	
	public static Optional<Invoice> getFull(AONContext ctx, Integer id) {
		ctx.checkRead();
		return getInvoiceSelect(ctx)
			.where(INVOICE.ID.eq(id))
			.fetch()
			.stream()
			.map( new InvoiceFiller() )
			.map( i -> fullInvoiceBuilder(ctx, i) )
			.findFirst();
	}

	public static int getNextNumber(AONContext ctx, Byte[] types, String series ) {
		TbaiConfiguration tbaiConfiguration = ctx.getTbaiConfiguration();
		if(tbaiConfiguration.isActive() && InvoiceType.contains(types, InvoiceType.SALES)) {
			return getTbaiNextNumber(ctx, types, series);
		} else {
			Integer next = selectMaxInvoice(ctx, types, series)
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

	private static int getTbaiNextNumber(AONContext ctx, Byte[] types, String series ) {
		Integer next = selectMaxInvoice(ctx, types, series)
			.union(selectMaxInvoiceTracking(ctx, types, series))
			.fetch()
			.stream()
			.mapToInt(rec -> (rec != null && rec.getValue(DSL.max(INVOICE.NUMBER)) != null) 
					? rec.getValue(DSL.max(INVOICE.NUMBER)) 
					: 0)
			.max().orElse(0);
		if(next < 0) next = 0;
		return ++next;
	}
	
	private static SelectConditionStep<Record1<Integer>> selectMaxInvoiceTracking(AONContext ctx, Byte[] types, String series) {
		 return ctx.getDslContext()
			.select( DSL.max(INVOICE_TRACKING.NUMBER))
			.from(INVOICE_TRACKING)
			.where(INVOICE_TRACKING.DOMAIN.eq(ctx.getDomainId()))
			.and(INVOICE_TRACKING.TYPE.in(types))
			.and( AonStringUtils.isBlank(series)
				? INVOICE_TRACKING.SERIES.isNull().or(DSL.trim(INVOICE_TRACKING.SERIES).eq(""))
				: INVOICE_TRACKING.SERIES.eq(series));
	}

	private static SelectConditionStep<Record1<Integer>> selectMaxInvoice(AONContext ctx, Byte[] types, String series) {
		 return ctx.getDslContext()
			.select( DSL.max(INVOICE.NUMBER))
			.from(INVOICE)
			.where(INVOICE.DOMAIN.eq(ctx.getDomainId()))
			.and(INVOICE.TYPE.in(types))
			.and(AonStringUtils.isBlank(series)
				? INVOICE.SERIES.isNull().or(DSL.trim(INVOICE.SERIES).eq(""))
				: INVOICE.SERIES.eq(series));
	}
	
	public static class InvoiceFiller extends Filler implements Function<Record,Invoice> {

		@Override
		public Invoice  apply(Record r) {
			return build(r);
		}

	    public static Invoice build(Record r) {
	        return build(r, INVOICE);
	    }
	     
    	static Invoice build(Record r, com.esferalia.aon.jooq.tables.Invoice inv) {
			return new Invoice()
				.setId(r.getValue(inv.ID))
				.setDomain(r.getValue(inv.DOMAIN))
				.setType(AonEnumUtils.enumValue(InvoiceType.class, r.getValue(inv.TYPE)))
				.setSeries(r.getValue(inv.SERIES))
				.setNumber(r.getValue(inv.NUMBER))
				.setReferenceCode(r.getValue(inv.REFERENCE_CODE))
				.setIssueDate(r.getValue(inv.ISSUE_DATE))
				.setTaxDate(r.getValue(inv.TAX_DATE))
				.setSecurityLevel(AonEnumUtils.enumValue(SecurityLevel.class, r.getValue(inv.SECURITY_LEVEL)))
				.setRegistry( r.getValue(inv.REGISTRY))
				.setRegistryDocument(r.getValue(inv.RDOCUMENT))
				.setRegistryDocumentType(AonEnumUtils.enumValue(DocumentType.class,r.getValue(inv.RDOCUMENT_TYPE)))
				.setRegistryDocumentCountry(Country.safeValueOf(r.getValue(inv.RDOCUMENT_COUNTRY)))
				.setRegistryName(r.getValue(inv.RNAME))
				.setRegistryAddress(r.getValue(inv.RADDRESS))
				.setScope(ScopeFiller.buildScope(r))
				.setActivity(EnterpriseActivityFiller.build(r))	
				.setInvestAsset(r.getValue(inv.INVEST_ASSET))
				.setProject(r.getValue(inv.PROJECT))
				.setRectificationType(AonEnumUtils.enumValue(RectificationType.class, r.getValue(inv.RECTIFICATION_TYPE)))
				.setRectificationInvoice(getValue(r,inv.RECTIFICATION_INVOICE) == null
					?null
					:InvoiceMinFiller.build(r, RECTIFICATION_INVOICE))
				.setTransaction(InvoiceTransactionType.safeValueOf(r.getValue(inv.TRANSACTION)))
				.setRecorded(r.getValue(inv.STATUS) != null && r.getValue(inv.STATUS) == 1 )	
				.setSurcharge(r.getValue(inv.SURCHARGE) == 1 )	
				.setWithholding(r.getValue(inv.WITHHOLDING) == 1 )	
				.setWithholdingFarmer(r.getValue(inv.WITHHOLDING_FARMER) == 1 )	
				.setVatAccrualPayment(r.getValue(inv.VAT_ACCRUAL_PAYMENT) == 1 )	
				.setInvestment(r.getValue(inv.INVESTMENT) == 1 )	
				.setService(r.getValue(inv.SERVICE) == 1 )	
				.setAdvance(r.getValue(inv.ADVANCE) == 1 )	
				.setTaxableBase(r.getValue(inv.TAXABLE_BASE))	
				.setVatQuota(r.getValue(inv.VAT_QUOTA))	
				.setRetentionQuota(r.getValue(inv.RETENTION_QUOTA))	
				.setTotal(r.getValue(inv.TOTAL))	
				.setComments(r.getValue(inv.COMMENTS))
				.setRemarks(r.getValue(inv.REMARKS))
				.setFiscal(InvoiceFiscalDAO.InvoiceFiscalFiller.buildInvoiceFiscal(r))
				.setSeller(getValue(r, inv.SELLER))
				.setCreationDate(r.getValue(inv.CREATION_DATE))
				.setCreationUser(r.getValue(inv.CREATION_USER))
				.setModificationDate(r.getValue(inv.MODIFICATION_DATE))
				.setModificationUser(r.getValue(inv.MODIFICATION_USER));
		}
	}

	public static class InvoiceMinFiller extends Filler implements Function<Record,InvoiceMin> {

		@Override
		public InvoiceMin apply(Record r) {
			return build(r);
		}

	    public static InvoiceMin build(Record r) {
	        return build(r, INVOICE);
	    }
	     
	    static InvoiceMin build(Record r, com.esferalia.aon.jooq.tables.Invoice inv) {
			return new InvoiceMin()
				.setId(getValue(r,inv.ID))
				.setDomain(getValue(r,inv.DOMAIN))
				.setType(AonEnumUtils.enumValue(InvoiceType.class, getValue(r,inv.TYPE)))
				.setActivity(getValue(r,inv.ACTIVITY))	
				.setActivityEpigraph(getValue(r, IAE.EPIGRAPH))
				.setActivityName(getValue(r, ENTERPRISE_ACTIVITY.DESCRIPTION))
				.setSeries(getValue(r,inv.SERIES))
				.setNumber(getValue(r,inv.NUMBER))
				.setReferenceCode(getValue(r,inv.REFERENCE_CODE))
				.setTransaction(InvoiceTransactionType.safeValueOf(getValue(r,inv.TRANSACTION)))
				.setIssueDate(getValue(r,inv.ISSUE_DATE))
				.setTaxDate(getValue(r,inv.TAX_DATE))
				.setRegistry(getValue(r,inv.REGISTRY))
				.setRegistryDocument(getValue(r,inv.RDOCUMENT))
				.setRegistryDocumentType(AonEnumUtils.enumValue(DocumentType.class,getValue(r,inv.RDOCUMENT_TYPE)))
				.setRegistryDocumentCountry(Country.safeValueOf(getValue(r,inv.RDOCUMENT_COUNTRY)))
				.setRegistryName(getValue(r,inv.RNAME))
				.setScope(getValue(r, inv.SCOPE))
				.setSecurityLevel(AonEnumUtils.enumValue(SecurityLevel.class, getValue(r,inv.SECURITY_LEVEL)))
				.setRecorded(getBoolean(r,inv.STATUS))	
				.setRectificationType(AonEnumUtils.enumValue(RectificationType.class, r.getValue(inv.RECTIFICATION_TYPE)))
				.setRectificationInvoiceId(getValue(r,inv.RECTIFICATION_INVOICE))
				.setTotal(getValue(r,inv.TOTAL))	
				;
		}
	}

	private static Invoice fullInvoiceBuilder(AONContext ctx, Invoice invoice) {
		if (invoice == null) return null;
		BUILD_ADDRESS
			.andThen(BUILD_DETAILS)
			.andThen(BUILD_TAX_BREAKDOWN)
			.andThen(BUILD_FINANCES)
			.andThen(BUILD_ATTACH)
			.accept(ctx,invoice);
		return invoice.calculateTaxBreakdown();
	}
	
	private static final BiConsumer<AONContext, Invoice> BUILD_ADDRESS = (ctx, invoice) -> invoice.setAddress(InvoiceAddressDAO.get(ctx, invoice));
	private static final BiConsumer<AONContext, Invoice> BUILD_DETAILS = (ctx, invoice) -> {
		invoice.deleteDetails();
		InvoiceDetailDAO.stream(ctx,invoice.getId()).forEach(d -> invoice.addDetail(d));
	};
	
	private static final BiConsumer<AONContext, Invoice> BUILD_TAX_BREAKDOWN = (ctx, invoice) -> 
		AonCollectionUtils.stream(invoice.getDetails())
			.flatMap(detail -> AonCollectionUtils.stream(detail.getInvoiceTaxes()))
			.forEach( it -> invoice.addTax(it));

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

	public static Invoice save(AONContext ctx, Invoice invoice) {
		return save(ctx, invoice, false);
	}
	public static Invoice saveAndGet(AONContext ctx, Invoice invoice) {
		return save(ctx, invoice, true);
	}
	
	public static Invoice save(AONContext ctx, Invoice invoice, boolean returnFullInvoice) {
		ctx.checkWrite();
		if (invoice.getId() == null) {
			insert(ctx, invoice);
		} else {
			update(ctx, invoice);
		}
		if (returnFullInvoice) {
			return getFull(ctx, invoice.getId() )
				.orElseThrow(() -> {
					invoice.addMessage( InvoiceErrorMessages.C500.err(InvoiceErrorKey.GENERIC));
					return new AonCoreException( InvoiceErrorMessages.C500.getMessage());
				});
		} 
		return invoice;
	}

	private static Invoice update(AONContext ctx, Invoice invoice) {
		InvoiceValidation.validate(ctx, invoice);
		InvoiceAutoComplete.completeInvoice(ctx, invoice);
		InvoiceCalculator.calculate(invoice);
		int i = ctx.getDslContext()
			.update(INVOICE)
			.set(INVOICE.DOMAIN, invoice.getDomain() )
			.set(INVOICE.ACTIVITY, invoice.getActivity().map(a -> a.getId()).orElse(null) )
			.set(INVOICE.INVEST_ASSET, invoice.getInvestAsset() )
			.set(INVOICE.PROJECT, invoice.getProject() )
			.set(INVOICE.SERIES, invoice.getSeries() )
			.set(INVOICE.NUMBER, invoice.getNumber() )
			.set(INVOICE.REFERENCE_CODE, invoice.getReferenceCode() )
			.set(INVOICE.REGISTRY, invoice.getRegistry() )
			.set(INVOICE.RDOCUMENT, invoice.getRegistryDocument() )
			.set(INVOICE.RDOCUMENT_TYPE, AonEnumUtils.getByte( invoice.getRegistryDocumentType()) )
			.set(INVOICE.RDOCUMENT_COUNTRY, Country.safeIso2( invoice.getRegistryDocumentCountry()))
			.set(INVOICE.RNAME, invoice.getRegistryName() )
			.set(INVOICE.RADDRESS, invoice.getRegistryAddress() )
			.set(INVOICE.ISSUE_DATE, AonDateUtils.toSql( invoice.getIssueDate()) )
			.set(INVOICE.TAX_DATE, AonDateUtils.toSql(invoice.getTaxDate()) )
			.set(INVOICE.SECURITY_LEVEL, AonEnumUtils.getByte( invoice.isConfidential() ) )
			.set(INVOICE.STATUS, AonEnumUtils.getByte( invoice.isRecorded() ) )
			.set(INVOICE.TYPE, AonEnumUtils.getByte( invoice.getType() ) )
			.set(INVOICE.SURCHARGE, AonEnumUtils.getByte( invoice.isSurcharge() ))
			.set(INVOICE.WITHHOLDING, AonEnumUtils.getByte( invoice.isWithholding() ))
			.set(INVOICE.WITHHOLDING_FARMER, AonEnumUtils.getByte( invoice.isWithholdingFarmer() ))
			.set(INVOICE.VAT_ACCRUAL_PAYMENT, AonEnumUtils.getByte( invoice.isVatAccrualPayment() ))
			.set(INVOICE.INVESTMENT, AonEnumUtils.getByte( invoice.isInvestment() ) )
			.set(INVOICE.TRANSACTION, AonEnumUtils.getByte( invoice.getTransaction() ) )
			.set(INVOICE.SCOPE, invoice.getScope().getId() )
			.set(INVOICE.SERVICE, AonEnumUtils.getByte(  invoice.isService() ) )
			.set(INVOICE.RECTIFICATION_TYPE, AonEnumUtils.getByte( invoice.getRectificationType()) )
			.set(INVOICE.RECTIFICATION_INVOICE, invoice.getRectificationInvoiceId() )
			.set(INVOICE.ADVANCE, AonEnumUtils.getByte( invoice.isAdvance()) )
			.set(INVOICE.SIGNED, AonEnumUtils.getByte( invoice.isSigned()) )
			.set(INVOICE.TAXABLE_BASE, invoice.getTaxableBase() )
			.set(INVOICE.VAT_QUOTA, invoice.getVatQuota() )
			.set(INVOICE.RETENTION_QUOTA, invoice.getRetentionQuota() )
			.set(INVOICE.TOTAL, invoice.getTotal() )
			.set(INVOICE.POS_SHIFT, invoice.getPosShift() )
			.set(INVOICE.SELLER, invoice.getSeller() )
			.set(INVOICE.COMMENTS, invoice.getComments() )
			.set(INVOICE.REMARKS, invoice.getRemarks() )
			.set(INVOICE.MODIFICATION_USER,ctx.getUser())
			.set(INVOICE.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()) )
			.where(INVOICE.ID.equal( invoice.getId()))
			.execute();
		ctx.log().debug("UPDATE INVOICE invoice: {0} ({1} rows)",invoice.getId(),i);
		InvoiceDetailDAO.save(ctx, invoice);
		InvoiceFiscalDAO.save(ctx, invoice);
		return invoice; 
	}
	
	private static Invoice insert(AONContext ctx, Invoice invoice) {
		ctx.checkWrite();
		InvoiceAutoComplete.completeInvoice(ctx, invoice);
		InvoiceValidation.validate(ctx, invoice);
		InvoiceRecord rec = ctx.getDslContext()
			.insertInto(INVOICE)
			.set(INVOICE.DOMAIN, invoice.getDomain() )
			.set(INVOICE.ACTIVITY, invoice.getActivity().map(a -> a.getId()).orElse(null) )
			.set(INVOICE.INVEST_ASSET, invoice.getInvestAsset() )
			.set(INVOICE.PROJECT, invoice.getProject() )
			.set(INVOICE.SERIES, invoice.getSeries() )
			.set(INVOICE.NUMBER, invoice.getNumber() )
			.set(INVOICE.REFERENCE_CODE, invoice.getReferenceCode() )
			.set(INVOICE.REGISTRY, invoice.getRegistry() )
			.set(INVOICE.RDOCUMENT, invoice.getRegistryDocument() )
			.set(INVOICE.RDOCUMENT_TYPE, AonEnumUtils.getByte( invoice.getRegistryDocumentType()) )
			.set(INVOICE.RDOCUMENT_COUNTRY, Country.safeIso2( invoice.getRegistryDocumentCountry()))
			.set(INVOICE.RNAME, invoice.getRegistryName() )
			.set(INVOICE.RADDRESS, invoice.getRegistryAddress() )
			.set(INVOICE.ISSUE_DATE, AonDateUtils.toSql( invoice.getIssueDate()) )
			.set(INVOICE.TAX_DATE, AonDateUtils.toSql(invoice.getTaxDate()) )
			.set(INVOICE.SECURITY_LEVEL, AonEnumUtils.getByte( invoice.isConfidential() ) )
			.set(INVOICE.STATUS, AonEnumUtils.getByte( invoice.isRecorded() ) )
			.set(INVOICE.TYPE, AonEnumUtils.getByte( invoice.getType() ) )
			.set(INVOICE.SURCHARGE, AonEnumUtils.getByte( invoice.isSurcharge() ))
			.set(INVOICE.WITHHOLDING, AonEnumUtils.getByte( invoice.isWithholding() ))
			.set(INVOICE.WITHHOLDING_FARMER, AonEnumUtils.getByte( invoice.isWithholdingFarmer() ))
			.set(INVOICE.VAT_ACCRUAL_PAYMENT, AonEnumUtils.getByte( invoice.isVatAccrualPayment() ))
			.set(INVOICE.INVESTMENT, AonEnumUtils.getByte( invoice.isInvestment() ) )
			.set(INVOICE.TRANSACTION, AonEnumUtils.getByte( invoice.getTransaction() ) )
			.set(INVOICE.SCOPE, invoice.getScope().getId() )
			.set(INVOICE.SERVICE, AonEnumUtils.getByte(  invoice.isService() ) )
			.set(INVOICE.RECTIFICATION_TYPE, AonEnumUtils.getByte( invoice.getRectificationType()) )
			.set(INVOICE.RECTIFICATION_INVOICE, invoice.getRectificationInvoiceId() )
			.set(INVOICE.ADVANCE, AonEnumUtils.getByte( invoice.isAdvance()) )
			.set(INVOICE.SIGNED, AonEnumUtils.getByte( invoice.isSigned()) )
			.set(INVOICE.TAXABLE_BASE, invoice.getTaxableBase() )
			.set(INVOICE.VAT_QUOTA, invoice.getVatQuota() )
			.set(INVOICE.RETENTION_QUOTA, invoice.getRetentionQuota() )
			.set(INVOICE.TOTAL, invoice.getTotal() )
			.set(INVOICE.POS_SHIFT, invoice.getPosShift() )
			.set(INVOICE.SELLER, invoice.getSeller() )
			.set(INVOICE.COMMENTS, invoice.getComments() )
			.set(INVOICE.REMARKS, invoice.getRemarks() )
			.set(INVOICE.CREATION_USER, ctx.getUser()) 
			.set(INVOICE.CREATION_DATE, new Timestamp( System.currentTimeMillis()))
			.set(INVOICE.MODIFICATION_USER, ctx.getUser()) 
			.set(INVOICE.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()))
			.returning(INVOICE.ID)
			.fetchOne();
		invoice.setId(rec.getValue(INVOICE.ID));
		ctx.log().debug("INSERT INVOICE invoice: {0} Act: {1}",invoice.getId(),
				invoice.getActivity().map(a -> a.getId()).orElse(null));
		InvoiceDetailDAO.save(ctx, invoice);
		InvoiceFiscalDAO.save(ctx, invoice);
		AonCollectionUtils.stream(invoice.getFinances())
			.forEach( finance -> FinanceDAO.saveFinance(ctx, invoice));
		insertInvoiceAttach( ctx, invoice);
		return invoice;
	}
	
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
		InvoiceDAO.onDeleteRectifier(ctx, invoice);
		InvoiceDetailDAO.deleteInvoice(ctx, invoice.getId());
		InvoiceDAO.onDeleteDUA(ctx, invoice);
		
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
					&& !invoice.getIssueDate().before(InvoiceDAO.VAT_ACCRUAL_START_DATE)
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
					&& !invoice.getIssueDate().before(InvoiceDAO.VAT_ACCRUAL_START_DATE)
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
					&& !invoice.getIssueDate().before(InvoiceDAO.VAT_ACCRUAL_START_DATE)
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
			Account withholdingAccount = null;
			InvoiceBreakdown withholdingBreakdown = new InvoiceBreakdown();
			if (ctx.getConfiguration().getDefaultWithholdingPercent() != null) {
				withholdingBreakdown.setPercentage(ctx.getConfiguration().getDefaultWithholdingPercent().getPercentage());
				withholdingBreakdown.setWithholdingType(ctx.getConfiguration().getDefaultWithholdingPercent().getWithholdingType());
				withholdingAccount = (inv.isSales())
						?ctx.getConfiguration().getDefaultWithholdingPercent().getSalesAccount()
						:ctx.getConfiguration().getDefaultWithholdingPercent().getPurchaseAccount();
			}
			if (withholdingAccount == null) {
				withholdingAccount = inv.isSales()
					?ctx.getConfiguration().accounting().getDefaultPaidRetAccount()
					:ctx.getConfiguration().accounting().getDefaultChargedRetAccount(); 
			}
			Account wa = withholdingAccount;
			inv.addBreakdown(withholdingBreakdown);
			inv.getWithholding().ifPresent( wb -> wb.setAccount(wa));
		}
	}
	
	public static Invoice validate(AONContext ctx, Invoice invoice) {
		InvoiceAutoComplete.completeInvoice(ctx, invoice);
		InvoiceValidation.validate(ctx, invoice);
		return invoice;
	}

	// *************************************************
	// ********** TEST PURPOSE METHODS *****************
	// *************************************************
	public static InvoiceMin getRandom(AONContext ctx, InvoiceFilter filter) {
		return getInvoiceMinSelect(ctx)
			.where(INVOICE_PROPERTIES.getConditions(filter))
			.orderBy( DSL.rand() )
			.fetch()
			.stream()
			.map(new InvoiceMinFiller())
			.findFirst()
			.orElse(null);
	}
}




