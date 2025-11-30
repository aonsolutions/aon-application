package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceInfo.INVOICE_INFO;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.OrderField;
import org.jooq.SelectJoinStep;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.finance.InvoiceConsole;
import com.esferalia.aon.occam.api.model.finance.InvoiceConsoleParams;
import com.esferalia.aon.occam.api.model.finance.InvoiceStatus;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceInfoDAO;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonEnumUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class InvoiceConsoleDAO {
	
	private InvoiceConsoleDAO() {

	}

	// ---------------------------------------------------------------------	
	// --------------------------------------------------------- [PUBLIC] --	
	// ---------------------------------------------------------------------
	private static SelectJoinStep<?> select(AONContext ctx) {
		Field<Integer> orderedType = InvoiceDAO.getOrderedType();
		return ctx.getDslContext()
			.select(
				 INVOICE.ID
				,INVOICE.DOMAIN
				,orderedType
				,INVOICE.TYPE
				,INVOICE.ISSUE_DATE
				,INVOICE.REFERENCE_CODE
			)
			.from(INVOICE);
	}
	
	public static List<InvoiceConsole> getInvoiceHeaders(AONContext ctx, InvoiceConsoleParams params) {
		ctx.checkRead();
		return select(ctx)
			.where(getWhere(ctx, params))
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
			.map( ic -> fillMessages(ctx, ic))
			.map( ic -> fillCommunicationInfo(ctx, ic))
			.map( ic -> fillBreakdown(ctx, ic))
			.collect(Collectors.toCollection(LinkedList::new))
		;
	}


	// ---------------------------------------------------------------------	
	// -------------------------------------------------------- [PRIVATE] --	
	// ---------------------------------------------------------------------
	
	private static InvoiceConsole fillAttach(AONContext ctx, InvoiceConsole ic) {
		Integer dom = ic.getInvoice().getDomain();
		Integer iid = ic.getInvoice().getId();
		ic.getInvoice().setDoc( InvoiceDocDAO.get(ctx, dom, iid).orElse(null) );
		return ic;
	}

	private static InvoiceConsole fillSource(InvoiceConsole ic) {
		return ic.setSource( 
			ic.getInvoice().detailStream()
			 	.map( id -> id.getSource() )
			 	.distinct()
	            .limit(2)
	            .reduce((a, b) -> null) // Si hay más de uno, devuelve null. Factura con más de un source.
	            .orElse( null )
        );
	}
	private static InvoiceConsole fillMessages(AONContext ctx, InvoiceConsole ic) {
		InvoiceRecorderDAO.fillMessages(ctx, ic.getInvoice().getDomain(), ic.getInvoice());
		return ic;
	}
	
	private static InvoiceConsole fillCommunicationInfo(AONContext ctx, InvoiceConsole ic) {
		ic.getInvoice().addCommunicationInfo(InvoiceInfoDAO.getMap(ctx, ic.getInvoice().getDomain(), ic.getInvoice().getId()).orElse(null));
		return ic;
	}

	private static InvoiceConsole fillBreakdown(AONContext ctx, InvoiceConsole ic) {
		ic.getInvoice().refreshTaxBreakdown( );
		return ic;
	}
	
	public static Condition getWhere(AONContext ctx, InvoiceConsoleParams params) {
		
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
		
		if (AonStringUtils.isNotEmpty(params.getSeries())) {
			condition = condition.and( INVOICE.SERIES.eq( params.getSeries() ));
		}
		
		if (params.getFromNumber() != null) {
			condition = condition.and( INVOICE.NUMBER.ge( params.getFromNumber() ));
		}
		if (params.getToNumber() != null) {
			condition = condition.and( INVOICE.NUMBER.le( params.getToNumber() ));
		}

		if (AonStringUtils.isNotEmpty(params.getReferenceCode())) {
			condition = condition.and( INVOICE.REFERENCE_CODE.eq( params.getReferenceCode() ));
		}

		if (params.getRegistry()  != null && params.getRegistry().intValue() != 0 ) {
			condition = condition.and( INVOICE.REGISTRY.eq( params.getRegistry() ));
		}
		
		if (params.getAccrualRegime() != null) {
			condition = condition.and( INVOICE.VAT_ACCRUAL_PAYMENT.eq( AonEnumUtils.getByte(params.getAccrualRegime())));
		}
		
		if (params.getWithholding() != null) {
			condition = condition.and( INVOICE.WITHHOLDING.eq( AonEnumUtils.getByte(params.getWithholding())));
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
			
		if (params.getTransactionType() != null) {
			condition = condition.and( INVOICE.TRANSACTION.eq( params.getTransactionType().value() ));
		}
		
		
		if (params.getCommunicationType() != null || params.getCommunicationStatus() != null) {
			Condition invoiceInfoCondition = INVOICE_INFO.INVOICE.equal(INVOICE.ID);
			if (params.getCommunicationType() != null) {
				invoiceInfoCondition = invoiceInfoCondition.and( INVOICE_INFO.TYPE.eq( params.getCommunicationType().value() ));
			}
			if (params.getCommunicationStatus() != null) {
				invoiceInfoCondition = invoiceInfoCondition.and( INVOICE_INFO.STATUS.eq( params.getCommunicationStatus().value() ));
			}
			condition = condition.andExists( 
				ctx.getDslContext().select(INVOICE_INFO.INVOICE)
					.from(INVOICE_INFO)
					.where(invoiceInfoCondition)
					.limit(1)
				);
		}
		return condition;
	}
	
//	public static boolean mustReadDetails(InvoiceConsoleParams params) {
//		return ( params.getSource() != null);
//	}
//	
//	public static Condition getDetailWhere(InvoiceConsoleParams params) {
//		Condition condition = INVOICE_DETAIL.DOMAIN.equal( params.getDomain() );
//		if ( params.getSource() != null) {
//			params.getSource().visit(null, new IInvoiceSourceVisitor() {
//
//				private static final long serialVersionUID = 5981585563515519217L;
//
//				private void visitManagement(InvoiceDetail detail) {
//					condition
//						.and( INVOICE_DETAIL.SOURCE.notEqual( InvoiceSource.ACCOUNT.value()))
//						.and( INVOICE_DETAIL.SOURCE.notEqual( InvoiceSource.TEDI.value()));
//				}
//
//				@Override public void visitDirectExpense(InvoiceDetail detail) { visitManagement(detail); }
//				@Override public void visitPurchase(InvoiceDetail detail) { visitManagement(detail); }
//				@Override public void visitSales(InvoiceDetail detail) { visitManagement(detail); }
//				@Override public void visitDelivery(InvoiceDetail detail) { visitManagement(detail); }
//				@Override public void visitIncome(InvoiceDetail detail) { visitManagement(detail); }
//				@Override public void visitFee(InvoiceDetail detail) {visitManagement(detail); }
//				@Override public void visitDirectInvoice(InvoiceDetail detail) { visitManagement(detail); }
//				@Override public void visitOffer(InvoiceDetail detail) {  visitManagement(detail); }
//				@Override public void visitReservation(InvoiceDetail detail) { visitManagement(detail); }
//				
//				@Override 
//				public void visitAccount(InvoiceDetail detail) {
//					condition.and( INVOICE_DETAIL.SOURCE.equal( InvoiceSource.ACCOUNT.value() ) );
//				}
//				@Override 
//				public void visitTedi(InvoiceDetail detail) {
//					condition.and( INVOICE_DETAIL.SOURCE.equal( InvoiceSource.TEDI.value() ) );
//				}
//				
//			});
//		}
//		return condition;
//	}

	
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
