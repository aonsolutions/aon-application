package com.esferalia.aon.gwt.fiscal.server;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.AccountEntryDetail.ACCOUNT_ENTRY_DETAIL;
import static com.esferalia.aon.jooq.tables.AccountEntryInvoice.ACCOUNT_ENTRY_INVOICE;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoiceTax.INVOICE_TAX;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectJoinStep;
import org.jooq.impl.DSL;
import org.jooq.tools.json.ParseException;
import org.json.JSONObject;

import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.FinanceUtil;
import com.esferalia.aon.occam.api.model.finance.InvoiceIntegrityCheckError;
import com.esferalia.aon.occam.api.model.finance.InvoiceStatus;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.InvoiceSource;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.impl.jooq.dao.Filler;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;
import com.esferalia.aon.watson.util.AonEnumUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "Invoice Integrity Stream Servlet", urlPatterns = { "/aon_gwt_fiscal/roms/InvoiceIntegrityStreamServlet" })
public class InvoiceIntegrityStreamServlet extends HttpServlet {

	private static final long serialVersionUID = -2697508555670615321L;
	
	private static final Field<BigDecimal> SUM_DEBIT = DSL.sum(ACCOUNT_ENTRY_DETAIL.DEBIT);
	private static final Field<BigDecimal> SUM_CREDIT = DSL.sum(ACCOUNT_ENTRY_DETAIL.CREDIT);
	private static final Field<Integer> orderedType = InvoiceDAO.getOrderedType();
	private static final Field<?>[] DATA_FIELDS = new Field<?>[] {
		 INVOICE.ID
		,INVOICE.DOMAIN
		,orderedType
		,INVOICE.ACTIVITY
		,INVOICE.TYPE
		,INVOICE.SERIES
		,INVOICE.NUMBER
		,INVOICE.REFERENCE_CODE
		,INVOICE.ISSUE_DATE
		,INVOICE.TAX_DATE
		,INVOICE.REGISTRY
		,INVOICE.RDOCUMENT
		,INVOICE.RDOCUMENT_TYPE
		,INVOICE.RDOCUMENT_COUNTRY
		,INVOICE.RNAME
		,INVOICE.TOTAL
		,INVOICE.STATUS
		,ACCOUNT_ENTRY_INVOICE.ACCOUNT_ENTRY
	};

	private static class HeaderInvoiceFiller extends Filler implements Function<Record,JSONObject> {

		@Override
		public JSONObject apply(Record r) {
			InvoiceType invoiceType = AonEnumUtils.enumValue(InvoiceType.class,getValue(r, INVOICE.TYPE));
			String series =  getValue(r, INVOICE.SERIES);
			Integer number =  getValue(r, INVOICE.NUMBER);
			Integer accountEntryId =  getValue(r, ACCOUNT_ENTRY_INVOICE.ACCOUNT_ENTRY);
			accountEntryId = accountEntryId == null? -1 : accountEntryId;
			return new JSONObject()
					.put(IJsonNames.ID, getValue(r, INVOICE.ID) )
					.put(IJsonNames.DOMAIN, getValue(r, INVOICE.DOMAIN) )
					.put(IJsonNames.ACTIVITY, getValue(r, INVOICE.ACTIVITY) )
					.put(IJsonNames.INVOICE_TYPE, invoiceType )
					.put(IJsonNames.SERIES, series )
					.put(IJsonNames.NUMBER, number )
					.put(IJsonNames.DOCUMENT_NUMBER, FinanceUtil.getDocumentNumber( invoiceType, series, number ) )
					.put(IJsonNames.REFERENCE_CODE, getValue(r, INVOICE.REFERENCE_CODE) )
					.put(IJsonNames.REGISTRY_ID, getValue(r, INVOICE.REGISTRY) )
					.put(IJsonNames.REGISTRY_DOCUMENT, getValue(r, INVOICE.RDOCUMENT) )
					.put(IJsonNames.REGISTRY_DOCUMENT_TYPE, AonEnumUtils.enumValue(DocumentType.class,getValue(r, INVOICE.RDOCUMENT_TYPE)))
					.put(IJsonNames.REGISTRY_DOCUMENT_COUNTRY, Country.safeValueOf(getValue(r, INVOICE.RDOCUMENT_COUNTRY)) )
					.put(IJsonNames.REGISTRY_NAME, getValue(r, INVOICE.RNAME) )
					.put(IJsonNames.ISSUE_DATE, AonNumberUtils.toString(getValue(r, INVOICE.ISSUE_DATE).getTime()) )
					.put(IJsonNames.TAX_DATE, AonNumberUtils.toString(getValue(r, INVOICE.TAX_DATE).getTime()) )
					.put(IJsonNames.TOTAL, getValue(r, INVOICE.TOTAL) )
					.put(IJsonNames.STATUS, AonEnumUtils.enumValue(InvoiceStatus.class,getValue(r, INVOICE.STATUS)))
					.put(IJsonNames.ACCOUNT_ENTRY_ID, accountEntryId )
				;
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			String accountingReportParams = req.getParameter( IRequestParamsNames.ACCOUNT_REPORT_PARAMS );
			String domainName = req.getParameter( IRequestParamsNames.DOMAIN_NAME);
			String user = req.getParameter(IRequestParamsNames.USER);
			int offset = AonNumberUtils.toint( req.getParameter(IRequestParamsNames.OFFSET));
			int limit = AonNumberUtils.toint( req.getParameter(IRequestParamsNames.LIMIT));
			int domainId = Integer.parseInt(req.getParameter(IRequestParamsNames.DOMAIN_ID));
			Occam occam = new Occam()
				.setDomainName(domainName)
				.setDomain(domainId)
				.setUser(user);
			AccountingReportParams params = JsonParser.parseAccountingParams(accountingReportParams);
				
			resp.setContentType(MimeType.JSON.getName());
			PrintWriter writer = new PrintWriter (resp.getWriter(), true); 
			writer.print( "[" );
			streams(occam, params, offset, limit, writer);
			writer.print( "]" );
			resp.flushBuffer();
			
		} catch (ParseException | java.text.ParseException e) {
			try {
				resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Imposible parsear los par\u00E1metros de búsqueda");
			} catch (IOException ioe) {
				// Nothing ... everything was wrong! 
			}
		} 
	}

	private void streams(Occam occam, AccountingReportParams params, int offset, int limit, PrintWriter writer) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			Condition basicCondition = InvoiceDAO.getWhere(params, true);
			
			Stream.of(
					Checks.DATES_DIFFERENT_YEARS.stream( ctx, basicCondition, offset , limit )
					,Checks.REGISTRY_DOCUMENT_DIFFERENT.stream( ctx, basicCondition, offset , limit )
					,Checks.ACCOUNT_ENTRY_SUM_VS_INVOICE_TOTAL.stream( ctx, basicCondition, offset , limit )
					,Checks.ACCOUNT_ENTRY_VAT_VS_INVOICE_TAX.stream( ctx, basicCondition, offset , limit )
					, Checks.INVOICE_TAX_DUPLICATE.stream(ctx, basicCondition, offset, limit)
					, Checks.INVOICE_TAXABLE_BASE0.stream(ctx, basicCondition, offset, limit)
				)
				.flatMap( s -> s)
				.forEach( js -> writer.print( js.toString() ))
			;
		}
	}

	private static SelectJoinStep<Record> basicSelect( AONContext ctx) {
		return ctx.getDslContext()
			.select(DATA_FIELDS)
			.from(INVOICE)
			.leftOuterJoin(ACCOUNT_ENTRY_INVOICE).on(ACCOUNT_ENTRY_INVOICE.INVOICE.eq(INVOICE.ID) );
	}
	
	private static Stream<JSONObject> getInvoices(
			 Supplier<SelectJoinStep<Record>> selectSupplier
			,Supplier<Condition> conditionSupplier
			, int offset 
			, int numberOfRows) {
		return selectSupplier.get()
			.where(conditionSupplier.get())
			.orderBy(orderedType,INVOICE.TYPE,INVOICE.ISSUE_DATE.desc(),INVOICE.REFERENCE_CODE)
			.limit(offset , numberOfRows )
			.fetch()
			.stream()
			.map(new HeaderInvoiceFiller())
		;
	}
	
	enum Checks {
		// ******************************************************************************************
		// ** Las fechas de emisión y de impuestos tiene diferente año  
		// ******************************************************************************************
		DATES_DIFFERENT_YEARS {
			@Override
			Stream<JSONObject> stream(AONContext ctx, Condition basicCondition, int offset, int limit) {
				return getInvoices( 
					 () -> basicSelect(ctx)
					,() -> basicCondition
						.and( DSL.year( INVOICE.ISSUE_DATE).ne(DSL.year( INVOICE.TAX_DATE)) )
					, offset
					, limit )
					.map( json -> json.put(IJsonNames.ERROR
						, "El a\u00F1o de la fecha de emisi\u00F3n es diferente al a\u00F1o de la fecha de impuestos.") ); 
			}
		}
		// ******************************************************************************************
		// ** El NIF de la factura no coincide con el de la ficha correspondiente  
		// ******************************************************************************************
		,REGISTRY_DOCUMENT_DIFFERENT {
			@Override
			Stream<JSONObject> stream(AONContext ctx, Condition basicCondition, int offset, int limit) {
				return getInvoices( 
					 () -> ctx.getDslContext()
						.select(DATA_FIELDS)
						.from(INVOICE)
						.innerJoin(REGISTRY).on(REGISTRY.ID.eq(INVOICE.REGISTRY) )
						.leftOuterJoin(ACCOUNT_ENTRY_INVOICE).on(ACCOUNT_ENTRY_INVOICE.INVOICE.eq(INVOICE.ID) )
					,() -> basicCondition
						.and( INVOICE.RDOCUMENT.ne(REGISTRY.DOCUMENT))
						.and( INVOICE.TYPE.ne(InvoiceType.UNDEDUCTIBLE.value()))
					, offset
					, limit )
					.map( json -> json.put(IJsonNames.ERROR, InvoiceIntegrityCheckError.ACCOUNT_ENTRY_SUM_VS_INVOICE_TOTAL.name())); 

			}
		}
		// ******************************************************************************************
		// ** El total factura no coincide con el total apunte.  
		// ******************************************************************************************
		,ACCOUNT_ENTRY_SUM_VS_INVOICE_TOTAL {
			@Override
			Stream<JSONObject> stream(AONContext ctx, Condition basicCondition, int offset, int limit) {
				return getInvoices( 
					 () -> basicSelect(ctx)
					,() -> basicCondition
						.and( INVOICE.TYPE.ne(InvoiceType.UNDEDUCTIBLE.value() ))
						.and( INVOICE.STATUS.eq( InvoiceStatus.SCORED.value() ))
						.and( INVOICE.WITHHOLDING.eq( (byte) 1 ))
					, offset
					, limit )
					.filter( json -> {
						Integer id = JsonUtils.getInteger(json, IJsonNames.ID);
						Optional<Integer> count = ctx.getDslContext()
							.select(DSL.count())
							.from(INVOICE_DETAIL)
							.innerJoin(INVOICE_TAX).on(INVOICE_DETAIL.ID.eq(INVOICE_TAX.INVOICE_DETAIL) )
							.where( INVOICE_DETAIL.INVOICE.eq(id))
							.and( INVOICE_TAX.TAX_TYPE.eq( TaxType.RETENTION.value()))
							.fetch()
							.stream()
							.map( r -> r.getValue(DSL.count()))
							.findFirst()
						;
						return (count.isPresent() && AonMathUtils.isZero(count.get()));
					})
					.map(json -> json.put(IJsonNames.ERROR, InvoiceIntegrityCheckError.ACCOUNT_ENTRY_SUM_VS_INVOICE_TOTAL.name()));

			}
		}
		// ******************************************************************************************
		// ** El total factura no coincide con el total apunte.  
		// ******************************************************************************************
		,ACCOUNT_ENTRY_VAT_VS_INVOICE_TAX  {
			@Override
			Stream<JSONObject> stream(AONContext ctx, Condition basicCondition, int offset, int limit) {
				return getInvoices( 
					 () -> ctx.getDslContext()
						.select(DATA_FIELDS)
							.from(INVOICE)
							.innerJoin(INVOICE_DETAIL).on(INVOICE_DETAIL.INVOICE.eq(INVOICE.ID) )
							.leftOuterJoin(ACCOUNT_ENTRY_INVOICE).on(ACCOUNT_ENTRY_INVOICE.INVOICE.eq(INVOICE.ID) )
					,() -> basicCondition
						.and( INVOICE.TYPE.ne(InvoiceType.UNDEDUCTIBLE.value() ))
						.and( INVOICE.STATUS.eq( InvoiceStatus.SCORED.value() ))
						.and( INVOICE_DETAIL.SOURCE.in( InvoiceSource.ACCOUNT.value(), InvoiceSource.TEDI.value() ))
					, offset
					, limit )
					.filter( json -> {
						InvoiceType type =  InvoiceType.safeValueOf(JsonUtils.getString(json, IJsonNames.INVOICE_TYPE));
						Integer id = JsonUtils.getInteger(json, IJsonNames.ID);
						Double vatSum = ctx.getDslContext()
							.select(DSL.sum( INVOICE_TAX.QUOTA))
							.from(INVOICE_DETAIL)
							.innerJoin(INVOICE_TAX).on(INVOICE_DETAIL.ID.eq(INVOICE_TAX.INVOICE_DETAIL) )
							.where( INVOICE_DETAIL.INVOICE.eq(id))
							.and( INVOICE_TAX.TAX_TYPE.eq( TaxType.VAT.value()))
							.fetch()
							.stream()
							.map( r -> r.getValue(DSL.sum( INVOICE_TAX.QUOTA)))
							.filter( b -> b != null)
							.map(BigDecimal::doubleValue)
							.findFirst()
							.orElse(0.0)
						;
						Double vatAccount = ctx.getDslContext()
							.select(SUM_DEBIT,SUM_CREDIT)
							.from(ACCOUNT_ENTRY_DETAIL)
							.innerJoin(ACCOUNT_ENTRY_INVOICE).on(ACCOUNT_ENTRY_INVOICE.ACCOUNT_ENTRY.eq(ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY) )
							.innerJoin(ACCOUNT).on(ACCOUNT_ENTRY_DETAIL.ACCOUNT.eq(ACCOUNT.ID) )
							.where( ACCOUNT_ENTRY_INVOICE.INVOICE.eq(id))
							.and( ACCOUNT.CODE.like("472%").or(ACCOUNT.CODE.like("477%")) )
							.fetch()
							.stream()
							.map( r -> r.getValue( type == InvoiceType.SALES ? SUM_CREDIT : SUM_DEBIT))
							.filter( b -> b != null)
							.map(BigDecimal::doubleValue)
							.findFirst()
							.orElse(0.0)
						;
						boolean ret = !AonMathUtils.equals(vatAccount,vatSum);
						if (ret ) {
							System.out.println( type + " -- " + vatAccount + " ----- " + vatSum);
						}
						return ret;
					})
					.map(json -> json.put(IJsonNames.ERROR, InvoiceIntegrityCheckError.ACCOUNT_ENTRY_VAT_VS_INVOICE_TAX.name())); 

			}
		}, INVOICE_TAX_DUPLICATE  {
			@Override
			Stream<JSONObject> stream(AONContext ctx, Condition basicCondition, int offset, int limit) {
				List<Integer> invoiceDetails = ctx.getDslContext().select(INVOICE_TAX.DOMAIN, INVOICE_TAX.INVOICE_DETAIL, DSL.count(INVOICE_TAX.INVOICE_DETAIL))
					.from(INVOICE_TAX)
					.where(INVOICE_TAX.DOMAIN.eq(ctx.getDomainId()))
					.groupBy(INVOICE_TAX.INVOICE_DETAIL, INVOICE_TAX.TAX_TYPE)
					.having(DSL.count(INVOICE_TAX.INVOICE_DETAIL).eq(2))
					.fetch().stream()
					.map(r -> r.getValue(INVOICE_TAX.INVOICE_DETAIL))
					.toList();
				
				return getInvoices(
					 () -> ctx.getDslContext()
						.select(DATA_FIELDS)
							.from(INVOICE)
							.innerJoin(INVOICE_DETAIL).on(INVOICE_DETAIL.INVOICE.eq(INVOICE.ID) )
							.leftOuterJoin(ACCOUNT_ENTRY_INVOICE).on(ACCOUNT_ENTRY_INVOICE.INVOICE.eq(INVOICE.ID) )
					,() -> basicCondition.and(INVOICE_DETAIL.ID.in(invoiceDetails))
					, offset, limit )
					.map( json -> {
						json.put(IJsonNames.ERROR, InvoiceIntegrityCheckError.INVOICE_TAX_DUPLICATE.name());
						return json;
					}); 
			}
		}, INVOICE_TAXABLE_BASE0 {
			@Override
			Stream<JSONObject> stream(AONContext ctx, Condition basicCondition, int offset, int limit) {
				return getInvoices(
						 () -> basicSelect(ctx)
						,() -> basicCondition.and(INVOICE.TAXABLE_BASE.eq(0.0).or(  
									INVOICE.VAT_QUOTA.eq(0.0).and(INVOICE.TOTAL.ne(INVOICE.TAXABLE_BASE))
							))
						, offset, limit )
						.map( json -> {
							json.put(IJsonNames.ERROR, InvoiceIntegrityCheckError.INVOICE_TAXABLE_BASE0.name());
							return json;
						}); 
			}
		}
		;
		abstract Stream<JSONObject> stream(AONContext ctx, Condition basicCondition, int offset, int limit);
	}
	// ----------------------------------------------------------------------
	// 1.- 
	
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
	
}
