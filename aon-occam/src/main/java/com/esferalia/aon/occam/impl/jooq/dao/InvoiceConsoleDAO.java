package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceAttach.INVOICE_ATTACH;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.OrderField;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.finance.InvoiceConsole;
import com.esferalia.aon.occam.api.model.finance.InvoiceConsoleParams;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceStatus;
import com.esferalia.aon.occam.api.model.fiscal.VatSummaryType;
import com.esferalia.aon.occam.api.model.type.InvoiceSource;
import com.esferalia.aon.occam.api.model.type.InvoiceSource.IInvoiceSourceVisitor;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonEnumUtils;
import com.esferalia.aon.watson.util.AonMathUtils;

public class InvoiceConsoleDAO {
	
	private InvoiceConsoleDAO() {

	}

	private static final Field<Byte> MIN_SOURCE = DSL.minDistinct( INVOICE_DETAIL.SOURCE);
	private static final Field<Byte> TEDI_FIELD = DSL.inline( InvoiceSource.TEDI.value());
	
	// ---------------------------------------------------------------------	
	// --------------------------------------------------------- [PUBLIC] --	
	// ---------------------------------------------------------------------
	
	public static List<InvoiceConsole> getInvoiceHeaders(AONContext ctx, InvoiceConsoleParams params) {
		ctx.checkRead();
		Field<Integer> orderedType = InvoiceDAO.getOrderedType();
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
				ctx.getDslContext().select( MIN_SOURCE )
					.from(INVOICE_DETAIL)
					.where( INVOICE_DETAIL.INVOICE.eq( INVOICE.ID) )
					.groupBy( INVOICE_DETAIL.INVOICE )
					.having(MIN_SOURCE.eq(InvoiceSource.TEDI.value()))))
			.orderBy(getOrderBy(params))
			.limit(params.getOffset() , params.getLimit())
			.fetch()
			.stream()
			.map( r -> r.getValue(INVOICE.ID) )
			.map( id -> InvoiceDAO.getFullInvoice( ctx, id) )
			.filter( i -> i != null)
			.map(i -> new InvoiceConsole().setInvoice(i))
			.map( ic -> fillAttach(ctx, ic))
			.map( ic -> fillSource(ic))
			.map( ic -> InvoiceRecorderDAO.fillMessages(ctx, ic.getInvoice().getDomain(), ic))
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
				.map( ic::setAttach )
				.findFirst()
				.orElse(ic)
		;
	}

	private static InvoiceConsole fillSource(InvoiceConsole ic) {
		return ic.setSource( 
			AonCollectionUtils.stream( ic.getInvoice().getDetails())
			 	.map( id -> id.getSource() )
			 	.distinct()
	            .limit(2)
	            .reduce((a, b) -> null) // Si hay más de uno, devuelve null. Factura con más de un source.
	            .orElse( null )
        );
	}
	
	public static Condition getWhere(InvoiceConsoleParams params) {
		
		Condition condition = INVOICE.DOMAIN.equal( params.getDomain() );
		
		if (params.getActivity() != null) {
			if (AonMathUtils.isNegative(params.getActivity())) {
				// Solo las comunes. Los "sin activdad".
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

				private void visitManagement(InvoiceDetail detail) {
					condition
						.and( INVOICE_DETAIL.SOURCE.notEqual( InvoiceSource.ACCOUNT.value()))
						.and( INVOICE_DETAIL.SOURCE.notEqual( InvoiceSource.TEDI.value()));
				}

				@Override public void visitDirectExpense(InvoiceDetail detail) { visitManagement(detail); }
				@Override public void visitPurchase(InvoiceDetail detail) { visitManagement(detail); }
				@Override public void visitSales(InvoiceDetail detail) { visitManagement(detail); }
				@Override public void visitDelivery(InvoiceDetail detail) { visitManagement(detail); }
				@Override public void visitIncome(InvoiceDetail detail) { visitManagement(detail); }
				@Override public void visitFee(InvoiceDetail detail) {visitManagement(detail); }
				@Override public void visitDirectInvoice(InvoiceDetail detail) { visitManagement(detail); }
				@Override public void visitOffer(InvoiceDetail detail) {  visitManagement(detail); }
				@Override public void visitReservation(InvoiceDetail detail) { visitManagement(detail); }
				
				@Override 
				public void visitAccount(InvoiceDetail detail) {
					condition.and( INVOICE_DETAIL.SOURCE.equal( InvoiceSource.ACCOUNT.value() ) );
				}
				@Override 
				public void visitTedi(InvoiceDetail detail) {
					condition.and( INVOICE_DETAIL.SOURCE.equal( InvoiceSource.TEDI.value() ) );
				}
				
			});
		}
		return condition;
	}

	
	private static OrderField<?>[] getOrderBy(InvoiceConsoleParams params) {
		// implementar order field in params and ask user.
		return new OrderField<?>[] {
			 InvoiceDAO.getOrderedType()
			,INVOICE.TYPE
			,INVOICE.ISSUE_DATE.desc()
			,INVOICE.REFERENCE_CODE
		};
	}
	
}
