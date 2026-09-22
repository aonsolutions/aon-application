package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.AmortizationInvoice.AMORTIZATION_INVOICE;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoiceFiscal.INVOICE_FISCAL;
import static com.esferalia.aon.jooq.tables.InvoiceInfo.INVOICE_INFO;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.OrderField;
import org.jooq.Record;
import org.jooq.Select;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceConsole;
import com.esferalia.aon.occam.api.model.finance.InvoiceConsoleParams;
import com.esferalia.aon.occam.api.model.finance.InvoiceConsoleParams.OrderBy.InvoiceConsoleParamsOrderVisitor;
import com.esferalia.aon.occam.api.model.finance.InvoiceStatus;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.invoice.InvoiceConsoleAnalysis;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceInfoDAO;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonEnumUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class InvoiceConsoleDAO {
	
	private static final Field<Integer> F_ID = DSL.field("id", Integer.class);
	private static final Field<Integer> F_DOMAIN = DSL.field("domain", Integer.class);
	private static final Field<Integer> F_ORDERED_TYPE = DSL.field("orderedType", Integer.class);
	private static final Field<Byte>    F_TYPE = DSL.field("type", Byte.class);
	private static final Field<Date>    F_ISSUE_DATE = DSL.field("issueDate", Date.class);
	private static final Field<String>  F_REFERENCE_CODE = DSL.field("referenceCode", String.class);
	private static final Field<String>  F_SERIES = DSL.field("series", String.class);
	private static final Field<Integer>  F_NUMBER = DSL.field("number", Integer.class);
	private static final Field<String>  F_RDOCUMENT = DSL.field("rDocument", String.class);
	private static final Field<String>  F_RNAME = DSL.field("rName", String.class);
	private static final Field<Double>  F_TOTAL = DSL.field("total", Double.class);
	private static final Field<Byte> F_ANNULLED = DSL.field("annulled", Byte.class);
	private static final Field<Timestamp> F_CREATION_DATE = DSL.field("creationTime", Timestamp.class);
	private static final Field<Byte> F_STATUS = DSL.field("statuc", Byte.class);
	private static final Field<Date> F_EXP_DATE = DSL.field("expDate", Date.class);
	private static final Field<Integer> ORDERED_TYPE = InvoiceDAO.getOrderedType();

	private static final Field<?>[] SELECT_FIELDS = new Field<?>[] {
		 INVOICE.ID.as( F_ID )
		,INVOICE.DOMAIN.as( F_DOMAIN )
		,ORDERED_TYPE.as( F_ORDERED_TYPE )
		,INVOICE.TYPE.as( F_TYPE )
		,INVOICE.ISSUE_DATE.as( F_ISSUE_DATE )
		,INVOICE.REFERENCE_CODE.as( F_REFERENCE_CODE )
		,INVOICE.SERIES.as( F_SERIES)
		,INVOICE.NUMBER.as( F_NUMBER)
		,INVOICE.STATUS.as( F_STATUS )
		,INVOICE.RDOCUMENT.as( F_RDOCUMENT)
		,INVOICE.RNAME.as( F_RNAME)
		,INVOICE.TOTAL.as( F_TOTAL)
		,INVOICE.CREATION_DATE.as( F_CREATION_DATE )
		,INVOICE.ANNULLED.as( F_ANNULLED )
		,INVOICE_FISCAL.EXP_DATE.as( F_EXP_DATE )
	};
	
	private InvoiceConsoleDAO() {

	}

	// ---------------------------------------------------------------------	
	// --------------------------------------------------------- [PUBLIC] --	
	// ---------------------------------------------------------------------
	private static SelectConditionStep<Record> getInvoiceSelect( AONContext ctx, InvoiceConsoleParams params ) {
		return ctx.getDslContext()
			.select(SELECT_FIELDS )
			.from(INVOICE)
			.leftOuterJoin(INVOICE_FISCAL).on(INVOICE_FISCAL.INVOICE.equal(INVOICE.ID))
			.where(new InvoiceConditionBuilder().build(ctx, params))
			;
	}
	
	public static List<InvoiceConsole> getInvoiceHeaders(AONContext ctx, InvoiceConsoleParams params) {
		ctx.checkRead();
		Select<Record> select =
			getInvoiceSelect(ctx, params)
				.orderBy(getOrderBy(params))
				.limit(params.getOffset() , params.getLimit());
		return getInvoiceHeadersStream(ctx, select, params)
			.collect(Collectors.toCollection(LinkedList::new));
	}
	
	private static Stream<InvoiceConsole> getInvoiceHeadersStream(AONContext ctx, Select<Record> select, InvoiceConsoleParams params) {
		InvoiceCommunicationConfiguration icc = params.isCommunicationExcluded() ? null : InvoiceCommunicationDAO.get(ctx, params.getDomain() );	
		return getInvoiceHeadersStream(ctx, icc, select, params);
	}
	
	private static Stream<InvoiceConsole> getInvoiceHeadersStream(AONContext ctx, InvoiceCommunicationConfiguration icc, Select<Record> select, InvoiceConsoleParams params) {
		ctx.checkRead();
		return select
			.fetch()
			.stream()
			.map(r -> {
				Invoice inv = new Invoice()
					.setId(r.getValue(F_ID))
					.setAnnulled( r.getValue(F_ANNULLED) == 1)
					.setDomain( r.getValue(F_DOMAIN) )
					.setType(AonEnumUtils.enumValue(InvoiceType.class, r.getValue(F_TYPE)))
					.setIssueDate( r.getValue(F_ISSUE_DATE) )
					.setReferenceCode( r.getValue(F_REFERENCE_CODE) )
					.setSeries( r.getValue(F_SERIES) )
					.setNumber( r.getValue(F_NUMBER) )
					.setRecorded(r.getValue(F_STATUS) != null && r.getValue(F_STATUS) == 1 )
					.setRegistryDocument( r.getValue(F_RDOCUMENT) )
					.setRegistryName( r.getValue(F_RNAME) )
					.setTotal( r.getValue(F_TOTAL) )
					.setCreationDate( r.getValue(F_CREATION_DATE) )
				;
				java.util.Date expDate = r.getValue(F_EXP_DATE);
				if (expDate == null) {
					expDate = inv.isProforma()? new java.util.Date() : inv.getIssueDate();
				}
				inv.ensureFiscal().setExpDate( expDate );
				return inv;
			})
			.map( i -> new InvoiceConsole().setInvoice(i) )
			.map( ic -> params.isAttachExcluded() ? ic : fillAttach(ctx, ic))
			.map( ic -> params.isCommunicationExcluded() ? ic : fillCommunicationInfo(ctx, icc, ic))
		;
	}

	public static InvoiceConsoleAnalysis analyze(AONContext ctx, InvoiceConsoleParams params) {
		params.setCommunicationExcluded(false);
		params.setAttachExcluded(true);
		InvoiceCommunicationConfiguration icc = InvoiceCommunicationDAO.get(ctx, params.getDomain() );
		InvoiceConsoleAnalysis analysis = new InvoiceConsoleAnalysis()
			.setCommunicationConfiguration( icc );
		Select<Record> select = getInvoiceSelect(ctx, params)
			.limit(0, Integer.MAX_VALUE);
		getInvoiceHeadersStream(ctx, icc, select, params)
			.map(ic -> ic.getInvoice())
			.forEach( analysis::addInvoice );
		return analysis;
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

	private static InvoiceConsole fillCommunicationInfo(AONContext ctx, InvoiceCommunicationConfiguration icc, InvoiceConsole ic) {
		if (icc != null) {
			ic.getInvoice().addCommunicationInfo(
				InvoiceInfoDAO.getMap(
					ctx
					, icc
					, ic.getInvoice().getDomain()
					, ic.getInvoice().getId()
					, ic.getInvoice().getType()
					, ic.getInvoice().getExpDate()
					).orElse(null));
		}
		return ic;
	}

	private static class InvoiceConditionBuilder extends ConditionBuilder {
		
		@Override
		protected Condition buildDomainCondition(AONContext ctx, InvoiceConsoleParams params) {
			return buildDomainCondition(ctx, INVOICE.DOMAIN, params);
		}

		@Override
		protected Condition buildIdCondition(AONContext ctx, Condition cond, InvoiceConsoleParams params) {
			return buildIdCondition(ctx, INVOICE.ID, cond, params);
		}
		
		@Override
		protected Condition buildIssueDateCondition(AONContext ctx, Condition cond, InvoiceConsoleParams params) {
			return buildIssueDateCondition(ctx, INVOICE.ISSUE_DATE, cond, params);
			
		}

		@Override
		protected Condition buildSeriesCondition(AONContext ctx, Condition cond, InvoiceConsoleParams params) {
			return buildSeriesCondition(ctx, INVOICE.SERIES , cond, params);
		}

		@Override
		protected Condition buildNumberCondition(AONContext ctx, Condition cond, InvoiceConsoleParams params) {
			return buildNumberCondition(ctx, INVOICE.NUMBER, cond, params);
		}

		@Override
		protected Condition buildReferenceCodeCondition(AONContext ctx, Condition cond, InvoiceConsoleParams params) {
			return buildReferenceCodeCondition(ctx, INVOICE.REFERENCE_CODE, cond, params);
		}

		@Override
		protected Condition buildOutputCondition(AONContext ctx, Condition cond, InvoiceConsoleParams params) {
			return  buildOutputCondition(ctx, INVOICE.TYPE, cond, params);
		}

		@Override
		protected Condition buildActivityCondition(AONContext ctx, Condition cond, InvoiceConsoleParams params) {
			if (params.getActivity() != null) {	
				if (AonMathUtils.isNegative(params.getActivity())) {
					// Solo las comunes. Los "sin activdad".
					cond = cond.and( INVOICE.ACTIVITY.isNull());
				} else {
					cond = cond.and( INVOICE.ACTIVITY.eq( params.getActivity() ));
				}
			}
			return cond;
		}
		
		@Override
		protected Condition buildRegistryCondition(AONContext ctx, Condition cond, InvoiceConsoleParams params) {
			if (params.getRegistry()  != null && params.getRegistry().intValue() != 0 ) {
				cond = cond.and( INVOICE.REGISTRY.eq( params.getRegistry() ));
			}
			return cond;
		}

		@Override
		protected Condition buildAccrualRegimeCondition(AONContext ctx, Condition cond, InvoiceConsoleParams params) {
			if (params.getAccrualRegime() != null) {
				cond = cond.and( INVOICE.VAT_ACCRUAL_PAYMENT.eq( AonEnumUtils.getByte(params.getAccrualRegime())));
			}
			return cond;
		}

		@Override
		protected Condition buildWithholdingCondition(AONContext ctx, Condition cond, InvoiceConsoleParams params) {
			if (params.getWithholding() != null) {
				cond = cond.and( INVOICE.WITHHOLDING.eq( AonEnumUtils.getByte(params.getWithholding())));
			}
			return cond;
		}

		@Override
		protected Condition buildInvestmentCondition(AONContext ctx, Condition cond, InvoiceConsoleParams params) {
			if (params.getInvestment() != null) {
				cond = cond.and( INVOICE.INVESTMENT.eq( AonEnumUtils.getByte(params.getInvestment())));
			}
			return cond;
		}

		@Override
		protected Condition buildRectificationTypeCondition(AONContext ctx, Condition cond, InvoiceConsoleParams params) {
			if (params.getRectificationType() != null) {
				cond = cond.and( INVOICE.RECTIFICATION_TYPE.eq( params.getRectificationType().value()));
			}
			return cond;
		}

		@Override
		protected Condition buildServiceCondition(AONContext ctx, Condition cond, InvoiceConsoleParams params) {
			if (params.getService() != null) {
				if ( params.getService().booleanValue() ) {
					cond = cond.and( INVOICE.SERVICE.eq((byte)1).or( INVOICE.TYPE.eq( InvoiceType.EXPENSES.value())));
				} else {
					cond = cond.and( INVOICE.SERVICE.ne((byte)1).and( INVOICE.TYPE.ne( InvoiceType.EXPENSES.value())));
				}
			}
			return cond;
		}

		@Override
		protected Condition buildRecordedCondition(AONContext ctx, Condition cond, InvoiceConsoleParams params) {
			if (params.getRecorded() != null) {
				if ( params.getRecorded().booleanValue() ) {
					cond = cond.and( INVOICE.STATUS.eq( InvoiceStatus.SCORED.value()));
				} else {
					cond = cond.and( INVOICE.STATUS.eq( InvoiceStatus.PENDING.value()));
				}
			}
			return cond;
		}

		@Override
		protected Condition buildAnnulledCondition(AONContext ctx, Condition cond, InvoiceConsoleParams params) {
			if (params.getAnnulled() != null) {
				cond = cond.and( INVOICE.ANNULLED.eq( AonEnumUtils.getByte(params.getAnnulled()) ));
			}
			return cond;
		}

		@Override
		protected Condition buildProformaCondition(AONContext ctx, Condition cond, InvoiceConsoleParams params) {
			if (params.getProforma() != null) {
				if ( params.getProforma().booleanValue() ) {
					cond = cond.and( INVOICE.NUMBER.lt( 0 ));
				} else {
					cond = cond.and( INVOICE.NUMBER.ge( 0 ));
				}
			}
			return cond;
		}
		
		@Override
		protected Condition buildAmortizationBindedCondition(AONContext ctx, Condition cond, InvoiceConsoleParams params) {
			if (params.getAmortizationBinded() != null) {
				if ( params.getAmortizationBinded().booleanValue() ) {
					cond = cond.and( INVOICE.ID.in( 
						ctx.getDslContext()
							.select(AMORTIZATION_INVOICE.INVOICE)
								.from(AMORTIZATION_INVOICE)
								.where( AMORTIZATION_INVOICE.DOMAIN.eq( params.getDomain() ))
						));
				} else {
					cond = cond.and( INVOICE.ID.notIn( 
						ctx.getDslContext()
							.select(AMORTIZATION_INVOICE.INVOICE)
								.from(AMORTIZATION_INVOICE)
								.where( AMORTIZATION_INVOICE.DOMAIN.eq( params.getDomain() ))
						));
				}
			}
			return cond;
		}

		@Override
		protected Condition buildTransactionTypeCondition(AONContext ctx, Condition cond, InvoiceConsoleParams params) {
			if (params.getTransactionType() != null) {
				cond = cond.and( INVOICE.TRANSACTION.eq( params.getTransactionType().value() ));
			}
			return cond;
		}

		@Override
		protected Condition buildCommunicationTypeCondition(AONContext ctx, Condition cond, InvoiceConsoleParams params) {
			if (params.getCommunicationType() != null || params.getCommunicationStatus() != null) {
				Condition invoiceInfoCondition = INVOICE_INFO.INVOICE.equal(INVOICE.ID);
				if (params.getCommunicationType() != null) {
					invoiceInfoCondition = invoiceInfoCondition.and( INVOICE_INFO.TYPE.eq( params.getCommunicationType().value() ));
				}
				if (params.getCommunicationStatus() != null) {
					invoiceInfoCondition = invoiceInfoCondition.and( INVOICE_INFO.STATUS.eq( params.getCommunicationStatus().value() ));
				}
				cond = cond.andExists( 
					ctx.getDslContext().select(INVOICE_INFO.INVOICE)
						.from(INVOICE_INFO)
						.where(invoiceInfoCondition)
						.limit(1)
					);
			}
			return cond;
		}

		@Override
		protected Condition buildSourceCondition(AONContext ctx, Condition cond, InvoiceConsoleParams params) {
			if ( params.getSource() != null) {
				cond = cond.andExists( 
					ctx.getDslContext().selectFrom( INVOICE_DETAIL )
						.where( INVOICE_DETAIL.INVOICE.equal( INVOICE.ID )
							.and( INVOICE_DETAIL.SOURCE.equal( params.getSource().value() ))
						)
						.limit(1)
					);
			}
			return cond;
		}
		
	}
	
	private abstract static class ConditionBuilder {
		Condition build(AONContext ctx, InvoiceConsoleParams params) {
			Condition condition = buildDomainCondition(ctx, params);
			condition = buildIdCondition(ctx, condition, params);
			condition = buildActivityCondition(ctx, condition, params);
			condition = buildIssueDateCondition(ctx, condition, params);
			condition = buildSeriesCondition(ctx, condition, params);
			condition = buildNumberCondition(ctx, condition, params);
			condition = buildReferenceCodeCondition(ctx, condition, params);
			condition = buildRegistryCondition(ctx, condition, params);
			condition = buildAccrualRegimeCondition(ctx, condition, params);
			condition = buildWithholdingCondition(ctx, condition, params);
			condition = buildInvestmentCondition(ctx, condition, params);
			condition = buildRectificationTypeCondition(ctx, condition, params);
			condition = buildServiceCondition(ctx, condition, params);
			condition = buildRecordedCondition(ctx, condition, params);
			condition = buildAnnulledCondition(ctx, condition, params);
			condition = buildProformaCondition(ctx, condition, params);
			condition = buildAmortizationBindedCondition(ctx, condition, params);
			condition = buildOutputCondition(ctx, condition, params);
			condition = buildTransactionTypeCondition(ctx, condition, params);
			condition = buildCommunicationTypeCondition(ctx, condition, params);
			condition = buildSourceCondition(ctx, condition, params);
			return condition;
		}
		
		protected Condition buildDomainCondition(AONContext ctx, Field<Integer> field, InvoiceConsoleParams params) {
			return field.equal( params.getDomain() );	
		}
		protected Condition buildIdCondition(AONContext ctx, Field<Integer> field, Condition cond, InvoiceConsoleParams params) {
			if (params.getId() != null) {
				cond = cond.and( field.eq( params.getId() ));
			}
			if (params.getFromId() != null) {
				cond = cond.and( field.ge( params.getFromId() ));
			}
			if (params.getToId() != null) {
				cond = cond.and( field.le( params.getToId() ));
			}
			if (AonCollectionUtils.isNotEmpty( params.getIds())) {
				cond = cond.and( field.in( params.getIds()) );
			}
			return cond;
		}

		protected Condition buildIssueDateCondition(AONContext ctx, Field<Date> field, Condition cond, InvoiceConsoleParams params) {
			if (params.getFromDate() != null){
				cond = cond.and( field.ge( AonDateUtils.toSql(params.getFromDate())));
			}
			if(params.getToDate() != null){
				cond = cond.and( field.le( AonDateUtils.toSql(params.getToDate())));
			}
			return cond;
		}
		
		protected Condition buildSeriesCondition(AONContext ctx, Field<String> field, Condition cond, InvoiceConsoleParams params) {
			if (AonStringUtils.isNotEmpty(params.getSeries())) {
				cond = cond.and( field.eq( params.getSeries() ));
			}
			return cond;
		}

		protected Condition buildNumberCondition(AONContext ctx, Field<Integer> field, Condition cond, InvoiceConsoleParams params) {
			if (params.getFromNumber() != null) {
				cond = cond.and( field.ge( params.getFromNumber() ));
			}
			if (params.getToNumber() != null) {
				cond = cond.and( field.le( params.getToNumber() ));
			}
			return cond;
		}
		
		protected Condition buildReferenceCodeCondition(AONContext ctx, Field<String> field, Condition cond, InvoiceConsoleParams params) {
			if (AonStringUtils.isNotEmpty(params.getReferenceCode())) {
				cond = cond.and( field.like( AonStringUtils.SQLlike( params.getReferenceCode())));
			}
			return cond;
		}

		protected Condition buildOutputCondition(AONContext ctx, Field<Byte> field, Condition cond, InvoiceConsoleParams params) {
			if (params.getOutput() != null) {
				if (params.getOutput().booleanValue()) {
					cond = cond.and( field.eq( InvoiceType.SALES.value() ));
				} else {
					cond = cond.and( field.in( InvoiceType.EXPENSES.value(), InvoiceType.PURCHASE.value() ));
				}
			}
			return cond;
		}

		protected abstract Condition buildDomainCondition(AONContext ctx, InvoiceConsoleParams params);
		protected abstract Condition buildIdCondition(AONContext ctx, Condition cond, InvoiceConsoleParams params);
		protected abstract Condition buildActivityCondition(AONContext ctx, Condition cond, InvoiceConsoleParams params);
		protected abstract Condition buildIssueDateCondition(AONContext ctx, Condition cond, InvoiceConsoleParams params);
		protected abstract Condition buildSeriesCondition(AONContext ctx, Condition cond, InvoiceConsoleParams params);
		protected abstract Condition buildNumberCondition(AONContext ctx, Condition cond, InvoiceConsoleParams params);
		protected abstract Condition buildReferenceCodeCondition(AONContext ctx, Condition cond, InvoiceConsoleParams params);
		protected abstract Condition buildRegistryCondition(AONContext ctx, Condition cond, InvoiceConsoleParams params);
		protected abstract Condition buildAccrualRegimeCondition(AONContext ctx, Condition cond, InvoiceConsoleParams params);
		protected abstract Condition buildWithholdingCondition(AONContext ctx, Condition cond, InvoiceConsoleParams params);
		protected abstract Condition buildInvestmentCondition(AONContext ctx, Condition cond, InvoiceConsoleParams params);
		protected abstract Condition buildRectificationTypeCondition(AONContext ctx, Condition cond, InvoiceConsoleParams params);
		protected abstract Condition buildServiceCondition(AONContext ctx, Condition cond, InvoiceConsoleParams params);
		protected abstract Condition buildRecordedCondition(AONContext ctx, Condition cond, InvoiceConsoleParams params);
		protected abstract Condition buildAnnulledCondition(AONContext ctx, Condition cond, InvoiceConsoleParams params);
		protected abstract Condition buildProformaCondition(AONContext ctx, Condition cond, InvoiceConsoleParams params);
		protected abstract Condition buildAmortizationBindedCondition(AONContext ctx, Condition cond, InvoiceConsoleParams params);
		protected abstract Condition buildOutputCondition(AONContext ctx, Condition cond, InvoiceConsoleParams params);
		protected abstract Condition buildTransactionTypeCondition(AONContext ctx, Condition cond, InvoiceConsoleParams params);
		protected abstract Condition buildCommunicationTypeCondition(AONContext ctx, Condition cond, InvoiceConsoleParams params);
		protected abstract Condition buildSourceCondition(AONContext ctx, Condition cond, InvoiceConsoleParams params);
		
	}
	
	private static OrderField<?>[] getOrderBy(InvoiceConsoleParams params) {
		if (params.getOrderBy() == null) {
			return new OrderField<?>[] {
				 F_ORDERED_TYPE
				,F_ISSUE_DATE.desc()
				,F_REFERENCE_CODE
			};
		}
		return params.getOrderBy().visit( new InvoiceConsoleParamsOrderVisitor<OrderField<?>[]>() {
			private OrderField<?> field(Field<?> field) {
				return params.isDescending() ? field.desc() : field ;
			}

			@Override
			public OrderField<?>[] issueDate() {
				return new OrderField<?>[] {
					field( F_ISSUE_DATE )
				};
			}

			@Override
			public OrderField<?>[] registry() {
				return new OrderField<?>[] {
					field( F_RNAME )
				};
			}

			@Override
			public OrderField<?>[] seriesNumber() {
				return new OrderField<?>[] {
					 field( F_ORDERED_TYPE )
					,field( F_SERIES )
					,field( F_NUMBER )
				};
			}

			@Override
			public OrderField<?>[] referenceCode() {
				return new OrderField<?>[] {
					 field( F_ORDERED_TYPE )
					,field( F_REFERENCE_CODE )
				};
			}
			@Override
			public OrderField<?>[] id() {
				return new OrderField<?>[] {
					field( F_ID )
				};
			}
		});
	}
}
