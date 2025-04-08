package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.Iae.IAE;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceAttach.INVOICE_ATTACH;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.OrderField;
import org.jooq.Record;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceConsole;
import com.esferalia.aon.occam.api.model.finance.InvoiceConsoleParams;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceStatus;
import com.esferalia.aon.occam.api.model.fiscal.VatSummaryType;
import com.esferalia.aon.occam.api.model.invoice.InvoiceError;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorKey;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorLevel;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.InvoiceSource;
import com.esferalia.aon.occam.api.model.type.InvoiceSource.IInvoiceSourceVisitor;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonEnumUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;

public class InvoiceConsoleDAO {
	
	private InvoiceConsoleDAO() {

	}

	private static final Field<Byte> MIN_SOURCE = DSL.minDistinct( INVOICE_DETAIL.SOURCE);
	private static final Field<Byte> MAX_SOURCE = DSL.maxDistinct( INVOICE_DETAIL.SOURCE);
	
	// ---------------------------------------------------------------------	
	// --------------------------------------------------------- [PUBLIC] --	
	// ---------------------------------------------------------------------
	
	public static List<InvoiceConsole> getInvoiceHeaders(AONContext ctx, InvoiceConsoleParams params) {
		ctx.checkRead();
		Field<Integer> orderedType = getOrderedType();
		Field<Byte> TEDI_FIELD = DSL.inline( InvoiceSource.TEDI.value());
		return ctx.getDslContext()
			.select(
				 INVOICE.ID
				,INVOICE.DOMAIN
				,orderedType
				,INVOICE.TYPE
				,INVOICE.ISSUE_DATE
				,INVOICE.REFERENCE_CODE
				,TEDI_FIELD
			)
			.from(INVOICE)
			.where(getWhere(params))
			.and( TEDI_FIELD.in(
				ctx.getDslContext().select( MAX_SOURCE )
					.from(INVOICE_DETAIL)
					.where( INVOICE_DETAIL.INVOICE.eq( INVOICE.ID) )
					.groupBy( INVOICE_DETAIL.INVOICE )
					.having(MAX_SOURCE.eq(InvoiceSource.TEDI.value()))))
			.orderBy(getOrderBy(params))
			.limit(params.getOffset() , params.getLimit())
			.fetch()
			.stream()
			.map( r -> r.getValue(INVOICE.ID) )
			.map( id -> InvoiceDAO.getFullInvoice( ctx, id) )
			.filter( i -> i != null)
			.map(i -> new InvoiceConsole().setInvoice(i))
			.map( ic -> fillAttach(ctx, ic))
			.map( ic -> fillSource(ctx, ic))
			.map( ic -> checkInvoice(ctx, ic.getInvoice().getDomain(), ic))
			.collect(Collectors.toCollection(LinkedList::new))
		;
	}

	// ---------------------------------------------------------------------	
	// -------------------------------------------------------- [PRIVATE] --	
	// ---------------------------------------------------------------------
	
	private static InvoiceConsole fillAttach(AONContext ctx, InvoiceConsole ic) {
		return ctx.getDslContext()
			.select(INVOICE_ATTACH.ID,INVOICE_ATTACH.INVOICE,INVOICE_ATTACH.DRIVEID,INVOICE_ATTACH.MIMETYPE)
				.from(INVOICE_ATTACH)
				.where(INVOICE_ATTACH.INVOICE.eq(ic.getInvoice().getId()))
				.fetch()
				.stream()
				.map(rec -> new Attach()
					.setId(rec.getValue(INVOICE_ATTACH.ID))
					.setAttachModule(rec.getValue(INVOICE_ATTACH.INVOICE))
					.setAttachType(AttachType.INVOICE)
					.setDriveId(rec.getValue(INVOICE_ATTACH.DRIVEID))
					.setMimeType(MimeType.safeValueOf(rec.getValue(INVOICE_ATTACH.MIMETYPE)))
					)
				.map( a -> ic.setAttach(a) )
				.findFirst()
				.orElse(ic)
		;
	}

	private static InvoiceConsole fillSource(AONContext ctx, InvoiceConsole ic) {
		
		return ctx.getDslContext()
			.select( MIN_SOURCE, MAX_SOURCE)
				.from(INVOICE_DETAIL)
				.where(INVOICE_DETAIL.INVOICE.eq(ic.getInvoice().getId()))
				.groupBy( INVOICE_DETAIL.INVOICE )
				.fetch()
				.stream()
				.filter( rec -> AonNumberUtils.equals(rec.getValue(MIN_SOURCE),rec.getValue(MAX_SOURCE)))
				.map( rec -> InvoiceSource.safeValueOf( rec.getValue(MIN_SOURCE)) )
				.map( ic::setSource )
				.findFirst()
				.orElse(ic)
		;
	}
	
	private static class HeaderInvoiceFiller  extends Filler implements Function<Record,Invoice> {

		@Override
		public Invoice apply(Record r) {
			return new Invoice()
				.setId(getValue(r,INVOICE.ID))
				.setDomain(getValue(r,INVOICE.DOMAIN))
				.setType(AonEnumUtils.enumValue(InvoiceType.class,getValue(r,INVOICE.TYPE)))
				.setTransaction(AonEnumUtils.enumValue(InvoiceTransactionType.class,getValue(r,INVOICE.TRANSACTION)))
				.setSeries(getValue(r,INVOICE.SERIES))
				.setNumber(getValue(r,INVOICE.NUMBER))
				.setReferenceCode(getValue(r,INVOICE.REFERENCE_CODE))
				.setIssueDate(getValue(r,INVOICE.ISSUE_DATE))
				.setTaxDate(getValue(r,INVOICE.TAX_DATE))
				.setSecurityLevel(AonEnumUtils.enumValue(SecurityLevel.class,getValue(r,INVOICE.SECURITY_LEVEL)))
				.setRegistry(getValue(r,INVOICE.REGISTRY))
				.setRegistryDocument(getValue(r,INVOICE.RDOCUMENT))
				.setRegistryDocumentType(AonEnumUtils.enumValue(DocumentType.class,getValue(r,INVOICE.RDOCUMENT_TYPE)))
				.setRegistryDocumentCountry(Country.safeValueOf(getValue(r,INVOICE.RDOCUMENT_COUNTRY)))
				.setRegistryName(getValue(r,INVOICE.RNAME))
				.setRecorded( getValue(r,INVOICE.STATUS) == 1)
				.setActivity(
					getValue(r,INVOICE.ACTIVITY) == null
						?null
						:new EnterpriseActivity()
							.setId(getValue(r,INVOICE.ACTIVITY))
							.setDescription(getValue(r,ENTERPRISE_ACTIVITY.DESCRIPTION))
							.setEpigraph(getValue(r,IAE.EPIGRAPH)))
			;
		}
		
	}
	
	private static Field<Integer> getOrderedType() {
		return DSL.decode()
		   .when(INVOICE.TYPE.equal((byte) 0), 0)
		   .when(INVOICE.TYPE.equal((byte) 1), 1)
		   .when(INVOICE.TYPE.equal((byte) 2), 0)
		   .when(INVOICE.TYPE.equal((byte) 3), 0);

	}
	
	public static Condition getWhere(InvoiceConsoleParams params) {
		
		Condition condition = INVOICE.DOMAIN.equal( params.getDomain() );
		
		if (params.getActivity() != null) {
			if (AonMathUtils.isNegative(params.getActivity())) {
				// S�lo las comunes. Los "sin activdad".
				condition = condition.and( INVOICE.ACTIVITY.isNull());
			} else {
				condition = condition.and( INVOICE.ACTIVITY.eq( params.getActivity() ));
			}
		}
			
		if (params.getFromDate() != null){
			condition = condition.and( INVOICE.ISSUE_DATE.ge( AonDateUtils.toSql(params.getFromDate())));
		}
		if(params.getToDate() != null){
			condition = condition.and( INVOICE.ISSUE_DATE.le( AonDateUtils.toSql(params.getToDate())));
		}

		if(params.getActivity() != null){
			condition = condition.and( INVOICE.ACTIVITY.eq( params.getActivity()));
		}

		if (params.getRegistry()  != null && params.getRegistry().intValue() != 0 ) {
			condition = condition.and( INVOICE.REGISTRY.eq( params.getRegistry() ));
		}
		
		if (params.getAccrualRegime() != null) {
			condition = condition.and( INVOICE.VAT_ACCRUAL_PAYMENT.eq( AonEnumUtils.getByte(params.getAccrualRegime())));
		}
		
		if (params.getInvestment() != null) {
			condition = condition.and( INVOICE.INVESTMENT.eq( AonEnumUtils.getByte(params.getInvestment())));
		}
		
		if (params.getRectificationType() != null) {
			condition = condition.and( INVOICE.RECTIFICATION_TYPE.eq( params.getRectificationType().value()));
		}
		
		if (params.getService() != null) {
			if ( params.getService().booleanValue() ) {
				condition = condition.and( INVOICE.SERVICE.eq((byte)1).or( INVOICE.TYPE.eq( InvoiceType.EXPENSES.value())));
			} else {
				condition = condition.and( INVOICE.SERVICE.ne((byte)1).and( INVOICE.TYPE.ne( InvoiceType.EXPENSES.value())));
			}
		}
		
		if (params.getRecorded() != null) {
			if ( params.getRecorded().booleanValue() ) {
				condition = condition.and( INVOICE.STATUS.eq( InvoiceStatus.SCORED.value()));
			} else {
				condition = condition.and( INVOICE.STATUS.eq( InvoiceStatus.PENDING.value()));
			}
		}

		if (params.getOutput() != null) {
			if (params.getOutput().booleanValue()) {
				condition = condition.and( INVOICE.TYPE.eq( InvoiceType.SALES.value() ));
			} else {
				condition = condition.and( INVOICE.TYPE.in( InvoiceType.EXPENSES.value(), InvoiceType.PURCHASE.value() ));
			}
		}
			
		if (params.getVatSummaryType() != null) {
			if (params.getVatSummaryType() == VatSummaryType.NATIONAL) {
				condition = condition.and( INVOICE.TRANSACTION.eq( InvoiceTransactionType.NATIONAL.value() ));
				condition = condition.and( INVOICE.WITHHOLDING_FARMER.eq( AonEnumUtils.getByte(false)));
			} else if (params.getVatSummaryType() == VatSummaryType.SURCHARGE){	
				condition = condition.and( INVOICE.TRANSACTION.eq( InvoiceTransactionType.NATIONAL.value() ));
				condition = condition.and( INVOICE.SURCHARGE.eq( AonEnumUtils.getByte(true)));
			} else if (params.getVatSummaryType() == VatSummaryType.FARMER) {
				condition = condition.and( INVOICE.TRANSACTION.eq( InvoiceTransactionType.NATIONAL.value() ));
				condition = condition.and( INVOICE.WITHHOLDING_FARMER.eq( AonEnumUtils.getByte(true)));
			} else if (params.getVatSummaryType() == VatSummaryType.INTRACOMMUNITY) {
				condition = condition.and( INVOICE.TRANSACTION.eq( InvoiceTransactionType.INTRACOMMUNITY.value() ));
			} else if (params.getVatSummaryType() == VatSummaryType.EXTRACOMMUNITY) {
				condition = condition.and( INVOICE.TRANSACTION.eq( InvoiceTransactionType.EXTRACOMMUNITY.value() ));
			} else if (params.getVatSummaryType() == VatSummaryType.CAN_CEU_MEL) {
				condition = condition.and( INVOICE.TRANSACTION.eq( InvoiceTransactionType.CAN_CEU_MEL.value() ));
			} else if (params.getVatSummaryType() == VatSummaryType.OTHER_ISP) {
				condition = condition.and( INVOICE.TRANSACTION.eq( InvoiceTransactionType.OTHER_ISP.value() ));
			}
		}
		return condition;
	}
	public static boolean mustReadDetails(InvoiceConsoleParams params) {
		return ( params.getSource() != null);
	}
	public static Condition getDetailWhere(InvoiceConsoleParams params) {
		Condition condition = INVOICE_DETAIL.DOMAIN.equal( params.getDomain() );
		if ( params.getSource() != null) {
			params.getSource().visit(null, new IInvoiceSourceVisitor() {

				private static final long serialVersionUID = 5981585563515519217L;

				@Override public void visitDirectExpense(InvoiceDetail detail) { visitManagement(detail); }
				@Override public void visitPurchase(InvoiceDetail detail) { }
				@Override public void visitSales(InvoiceDetail detail) { }
				@Override public void visitDelivery(InvoiceDetail detail) { }
				@Override public void visitIncome(InvoiceDetail detail) { }
				@Override public void visitFee(InvoiceDetail detail) {}
				@Override public void visitDirectInvoice(InvoiceDetail detail) {}
				@Override public void visitOffer(InvoiceDetail detail) { }
				@Override public void visitReservation(InvoiceDetail detail) {}
				@Override 
				public void visitAccount(InvoiceDetail detail) {
					condition.and( INVOICE_DETAIL.SOURCE.equal( InvoiceSource.ACCOUNT.value() ) );
				}
				@Override 
				public void visitTedi(InvoiceDetail detail) {
					condition.and( INVOICE_DETAIL.SOURCE.equal( InvoiceSource.TEDI.value() ) );
				}
				
				private void visitManagement(InvoiceDetail detail) {
					condition.and( INVOICE_DETAIL.SOURCE.notEqual( InvoiceSource.ACCOUNT.value() ) )
						.and( INVOICE_DETAIL.SOURCE.notEqual( InvoiceSource.TEDI.value() ) );
				}
				
			});
		}
		return condition;
	}

	
	private static OrderField<?>[] getOrderBy(InvoiceConsoleParams params) {
		// TODO implement order field in params and ask user.
		return new OrderField<?>[] {
			 getOrderedType()
			,INVOICE.TYPE
			,INVOICE.ISSUE_DATE.desc()
			,INVOICE.REFERENCE_CODE
		};
	}
	
	private static final Consumer<InvoicePreRecordContext> CHECK_IF_INVESTMENT = c -> {
		if (c.ic.getInvoice().isInvestment()) {
			InvoiceError error = new InvoiceError(
				InvoiceErrorKey.INVESTMENT
				,InvoiceErrorLevel.INF
				,AonError.INVOICE_RECORDER_INVESTMENT.getMessage());
			c.ic.getInvoice().addMessage(error);
		}
	};

	private static final Consumer<InvoicePreRecordContext> CHECK_IF_SURCHARGE = c -> {
		if (c.ic.getInvoice().isSurcharge()) {
			InvoiceError error = new InvoiceError(
				InvoiceErrorKey.SURCHARGE
				,InvoiceErrorLevel.INF
				,AonError.INVOICE_RECORDER_SURCHARGE.getMessage());
			c.ic.getInvoice().addMessage(error);
		}
	};
	
	private static final Consumer<InvoicePreRecordContext> CHECK_IF_WITHHOLDING = c -> {
		if (c.ic.getInvoice().isWithholding()) {
			InvoiceError error = new InvoiceError(
				InvoiceErrorKey.WITHHOLDING
				,InvoiceErrorLevel.INF
				,AonError.INVOICE_RECORDER_WITHHOLDING.getMessage());
			c.ic.getInvoice().addMessage(error);
		}
	};

	private static final Consumer<InvoicePreRecordContext> CHECK_TRANSACTION = c -> {
		if (!c.ic.getInvoice().isNational()) {
			InvoiceError error = new InvoiceError(
				InvoiceErrorKey.TRANSACTION
				,InvoiceErrorLevel.INF
				,AonError.INVOICE_RECORDER_TRANSACTION.format(c.ic.getInvoice().getTransaction().getDescription()));
			c.ic.getInvoice().addMessage(error);
		}
	};

	private static final Consumer<InvoicePreRecordContext> CHECK_PREPAYMENT = c -> {
		if (c.ctx.getDslContext()
			.select( INVOICE_DETAIL.ID )
			.from(INVOICE_DETAIL)
			.where( INVOICE_DETAIL.INVOICE.eq(c.ic.getInvoice().getId()))
			.and(INVOICE_DETAIL.PREPAYMENT.eq((byte) 1))
			.limit(1)
			.fetch()
			.stream()
			.findFirst()
			.isPresent()) {
			InvoiceError error = new InvoiceError(
				InvoiceErrorKey.GENERIC
				,InvoiceErrorLevel.INF
				,AonError.INVOICE_RECORDER_PREPAYMENT.getMessage());
			c.ic.getInvoice().addMessage(error);
		}
	};

	private static final Consumer<InvoicePreRecordContext> CHECK_EXPENSES = c -> {
		if ( c.ic.getInvoice().isExpenses() || c.ic.getInvoice().isUndeductible() ) {
			Optional.ofNullable( InvoiceDAO.getFullInvoice(c.ctx, c.ic.getInvoice().getId()) )
				.ifPresent( i-> {
					AonCollectionUtils.stream( i.getDetails() )
						.filter( d -> d.getAccount() == null )
						.findAny()
						.ifPresent( d -> {
							InvoiceError error = new InvoiceError(
								InvoiceErrorKey.EXPENSE_ACCOUNT
								,InvoiceErrorLevel.ERR
								,AonError.INVOICE_RECORDER_EXPENSE_ACCOUNT.getMessage());
							c.ic.getInvoice().addMessage(error);
						})
						;
				});
		}
	};

	private record InvoicePreRecordContext( AONContext ctx, int domain, InvoiceConsole ic ) {};
	private static InvoiceConsole checkInvoice(AONContext ctx, int domain, InvoiceConsole ic) {
		CHECK_IF_INVESTMENT
			.andThen(CHECK_IF_SURCHARGE)
			.andThen(CHECK_IF_WITHHOLDING)
			.andThen(CHECK_TRANSACTION)
			.andThen(CHECK_PREPAYMENT)
			.andThen(CHECK_EXPENSES)
			.accept(new InvoicePreRecordContext(ctx, domain, ic));
		return ic;
	}
}
