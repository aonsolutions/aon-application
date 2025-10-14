package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Carrier.CARRIER;
import static com.esferalia.aon.jooq.tables.Category.CATEGORY;
import static com.esferalia.aon.jooq.tables.ContractAttach.CONTRACT_ATTACH;
import static com.esferalia.aon.jooq.tables.ContractDoc.CONTRACT_DOC;
import static com.esferalia.aon.jooq.tables.Creditor.CREDITOR;
import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.Delivery.DELIVERY;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.Finance.FINANCE;
import static com.esferalia.aon.jooq.tables.Hotel.HOTEL;
import static com.esferalia.aon.jooq.tables.Income.INCOME;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.MkCampaign.MK_CAMPAIGN;
import static com.esferalia.aon.jooq.tables.MkTemplate.MK_TEMPLATE;
import static com.esferalia.aon.jooq.tables.News.NEWS;
import static com.esferalia.aon.jooq.tables.Newsletter.NEWSLETTER;
import static com.esferalia.aon.jooq.tables.Offer.OFFER;
import static com.esferalia.aon.jooq.tables.PayrollBatchAttach.PAYROLL_BATCH_ATTACH;
import static com.esferalia.aon.jooq.tables.Proposal.PROPOSAL;
import static com.esferalia.aon.jooq.tables.Purchase.PURCHASE;
import static com.esferalia.aon.jooq.tables.Rattach.RATTACH;
import static com.esferalia.aon.jooq.tables.Rdoc.RDOC;
import static com.esferalia.aon.jooq.tables.Sales.SALES;
import static com.esferalia.aon.jooq.tables.Seller.SELLER;
import static com.esferalia.aon.jooq.tables.SepeBatchAttach.SEPE_BATCH_ATTACH;
import static com.esferalia.aon.jooq.tables.Series.SERIES;
import static com.esferalia.aon.jooq.tables.Supplier.SUPPLIER;
import static com.esferalia.aon.jooq.tables.Survey.SURVEY;
import static com.esferalia.aon.jooq.tables.Target.TARGET;
import static com.esferalia.aon.jooq.tables.UserScope.USER_SCOPE;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.watson.util.AonCollectionUtils;

public class ScopeDAO {
	
	public static final Table<?>[] SCOPE_TABLES = new Table[] {	
		CARRIER,	CATEGORY,	CONTRACT_ATTACH,		CONTRACT_DOC,	CREDITOR,
		CUSTOMER,	DELIVERY,	ENTERPRISE,				FINANCE,		HOTEL,
		INCOME,		INVOICE,	MK_CAMPAIGN,			MK_TEMPLATE,	NEWS,
		NEWSLETTER,	OFFER,		PAYROLL_BATCH_ATTACH,	PROPOSAL,		PURCHASE,
		RATTACH,	RDOC,		SALES,					SELLER,			SEPE_BATCH_ATTACH,
		SERIES,		SUPPLIER,	SURVEY,					TARGET,			USER_SCOPE,
		WORKPLACE
	};


	private ScopeDAO() {
		throw new IllegalStateException("Utility class");
	}

	public static boolean canBeDeleted( AONContext ctx, Integer domainId, Integer scopeId ) {
		return AonCollectionUtils.stream( SCOPE_TABLES )
			.flatMap( t -> 
				ctx.getDslContext().select(DSL.count())
					.from(t)
					.where(getScopeField(t).eq(scopeId))
					.fetch()
					.stream())
			.map( r -> r.get(DSL.count()) )
			.noneMatch( count -> count != null && count > 0 );
	}

	@SuppressWarnings("unchecked")
	private static <T extends Record> Field<Integer> getScopeField(Table<T> table) {
		return (TableField<T, Integer>) table.field("scope");
	}
}
