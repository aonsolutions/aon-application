package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Carrier.CARRIER;
import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Sales.SALES;
import static com.esferalia.aon.jooq.tables.SalesDetail.SALES_DETAIL;
import static com.esferalia.aon.jooq.tables.Seller.SELLER;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.sql.Timestamp;
import java.util.Date;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.management.Sales;
import com.esferalia.aon.occam.api.model.management.SalesDetail;


public class SalesDAO {
	
	
	public static int insertSales(AONContext ctx, Sales sales) {

		ctx.checkWrite();

		Timestamp creationDate = null, modificationDate = null;
		creationDate = new java.sql.Timestamp(new java.util.Date().getTime());
		modificationDate = new java.sql.Timestamp(
				new java.util.Date().getTime());

		return ctx
				.getDslContext()
				.insertInto(SALES, SALES.DOMAIN, SALES.PROJECT, SALES.CUSTOMER,
						SALES.SERIES, SALES.NUMBER, SALES.PURCHASE_REFERENCE,
						SALES.SHIPPING_ADDRESS, SALES.SELLER,
						SALES.DISCOUNT_EXPR, SALES.ISSUE_DATE,
						SALES.PAY_METHOD, SALES.DOCUMENT_TYPE,
						SALES.SECURITY_LEVEL, SALES.STATUS, SALES.COMMENTS,
						SALES.REMARKS, SALES.WORKPLACE, SALES.SCOPE,
						SALES.NUMBER_OF_PYMNTS, SALES.DAYS_TO_FIRST_PYMNT,
						SALES.DAYS_BETWEEN_PYMNTS, SALES.PYMNT_DAYS,
						SALES.BANK_ACCOUNT, SALES.BANK_ALIAS, SALES.BIC,
						SALES.PURCHASE_GENERATED, SALES.CARRIER,
						SALES.SHIPPING_ALTERNATIVE_ADDRESS,
						SALES.SHIPPING_ALTERNATIVE_ADDRESS2,
						SALES.SHIPPING_ALTERNATIVE_ZIP,
						SALES.SHIPPING_ALTERNATIVE_CITY,
						SALES.SHIPPING_ALTERNATIVE_PHONE,
						SALES.SHIPPING_ALTERNATIVE_RECIPIENT,
						SALES.SHIPPING_CONTACT, SALES.SHIPPING_PERIOD,
						SALES.CREATION_USER, SALES.CREATION_DATE,
						SALES.MODIFICATION_USER, SALES.MODIFICATION_DATE)
				.values(sales.getDomain(), sales.getProject(),
						sales.getCustomer(), sales.getSeries(),
						sales.getNumber(), sales.getPurchaseReference(),
						sales.getShippingAddress(), sales.getSeller(),
						sales.getDiscountExpr(),
						new java.sql.Date(sales.getIssueDate().getTime()),
						sales.getPayMethod(), sales.getDocumentType(),
						sales.getSecurityLevel(), (byte)sales.getStatus().ordinal(),
						sales.getComments(), sales.getRemarks(),
						sales.getWorkplace(), sales.getScope(),
						sales.getNumberOfPymnts(), sales.getDaysToFirstPymnt(),
						sales.getDaysBetweenPymnts(), sales.getPymntDays(),
						sales.getBankAccount(), sales.getBankAlias(),
						sales.getBic(), sales.isPurchaseGenerated(),
						sales.getCarrier(),
						sales.getShippingAlternativeAddress(),
						sales.getShippingAlternativeAddress2(),
						sales.getShippingAlternativeZip(),
						sales.getShippingAlternativeCity(),
						sales.getShippingAlternativePhone(),
						sales.getShippingAlternativeRecipient(),
						sales.getShippingContact(), sales.getShippingPeriod(),
						ctx.getUser(), creationDate, ctx.getUser(),
						modificationDate).returning(SALES.ID).fetchOne()
				.getId();
	}
	
	public static void insertSalesDetail(AONContext ctx, SalesDetail detail) {
		ctx.checkWrite();
		Timestamp creationDate = null, modificationDate = null;
		creationDate = new java.sql.Timestamp(new java.util.Date().getTime());
		modificationDate = new java.sql.Timestamp(
				new java.util.Date().getTime());

		ctx.getDslContext()
				.insertInto(SALES_DETAIL, SALES_DETAIL.DOMAIN,
						SALES_DETAIL.SALES, SALES_DETAIL.LINE,
						SALES_DETAIL.ITEM,
						SALES_DETAIL.DESCRIPTION,
						SALES_DETAIL.QUANTITY, SALES_DETAIL.PRICE,
						SALES_DETAIL.DISCOUNT_EXPR,
						SALES_DETAIL.TAXES, SALES_DETAIL.STATUS,
						SALES_DETAIL.OFFER_DETAIL,
						SALES_DETAIL.DELIVERED,
						SALES_DETAIL.CREATION_USER,
						SALES_DETAIL.CREATION_DATE,
						SALES_DETAIL.MODIFICATION_USER,
						SALES_DETAIL.MODIFICATION_DATE)
				.values(detail.getDomain(), detail.getSales(), detail.getLine(), detail.getItem(), detail.getDescription(),
						detail.getQuantity(), detail.getPrice(), detail.getDiscountExpression(), detail.getTaxes(),
						(byte) detail.getStatus().ordinal(), detail.getOfferDetail(), detail.getDelivered(),
						ctx.getUser(), creationDate,
						ctx.getUser(), modificationDate)
				.execute();
	}
	
	public static Integer obtainEnterpriseId(AONContext ctx, int domain) {
		return ctx.getDslContext().select(ENTERPRISE.REGISTRY).from(ENTERPRISE)
				.where(ENTERPRISE.DOMAIN.eq(domain)).fetchAny().value1();
	}
	
	public static boolean existRegistry(AONContext ctx, Integer id) {
		return ctx.getDslContext().selectCount().from(REGISTRY)
				.where(REGISTRY.ID.eq(id)).fetchOne().value1()>0;
	}
	
	public static boolean existAddress(AONContext ctx, Integer id) {
		return ctx.getDslContext().selectCount().from(RADDRESS)
				.where(RADDRESS.ID.eq(id)).fetchOne().value1()>0;
	}
	
	public static boolean existWorkplace(AONContext ctx, Integer id) {
		return ctx.getDslContext().selectCount().from(WORKPLACE)
				.where(WORKPLACE.ID.eq(id)).fetchOne().value1()>0;
	}
	
	public static void createCustomer(AONContext ctx, int domain,
			int registry, int scope) {
		ctx.getDslContext()
				.insertInto(CUSTOMER, CUSTOMER.DOMAIN, CUSTOMER.REGISTRY,
						CUSTOMER.SCOPE, CUSTOMER.WITHHOLDING, CUSTOMER.STATUS,
						CUSTOMER.CREATION_DATE, CUSTOMER.CREATION_USER,
						CUSTOMER.MODIFICATION_DATE, CUSTOMER.MODIFICATION_USER)
				.values(domain, registry, scope, (byte) 0, (byte) 0,
						new java.sql.Timestamp(new Date().getTime()),
						ctx.getUser(),
						new java.sql.Timestamp(new Date().getTime()),
						ctx.getUser()).execute();
	}
	
	public static void createSeller(AONContext ctx, int domain, int registry,
			int scope) {
		ctx.getDslContext()
				.insertInto(SELLER, SELLER.DOMAIN, SELLER.REGISTRY,
						SELLER.SCOPE, SELLER.STATUS)
				.values(domain, registry, scope, (byte) 0).execute();
	}
	
	public static void createCarrier(AONContext ctx, int domain, int registry,
			int scope) {
		ctx.getDslContext()
				.insertInto(CARRIER, CARRIER.DOMAIN, CARRIER.REGISTRY,
						CARRIER.SCOPE).values(domain, registry, scope)
				.execute();
	}
	
	public static void createRegistry(AONContext ctx, int domain, int id,
			Byte type, String name, String alias, String nationality,
			Byte documentType, String documentCountry, String document) {
		ctx.getDslContext()
				.insertInto(REGISTRY, REGISTRY.DOMAIN, REGISTRY.ID,
						REGISTRY.TYPE, REGISTRY.NAME, REGISTRY.ALIAS,
						REGISTRY.NATIONALITY, REGISTRY.DOCUMENT_TYPE,
						REGISTRY.DOCUMENT_COUNTRY, REGISTRY.DOCUMENT)
				.values(domain, id, type, name, alias, nationality,
						documentType, documentCountry, document).execute();
	}

	public static void createRegistryAddress(AONContext ctx, int domain,
			int id, int registry, String alias, byte type, String recipient,
			String streetType, String address, String address2,
			String address3, String number, String zip, String city,
			Integer geozone, String municipalityCode) {
		ctx.getDslContext()
				.insertInto(RADDRESS, RADDRESS.DOMAIN, RADDRESS.ID,
						RADDRESS.REGISTRY, RADDRESS.ALIAS, RADDRESS.TYPE,
						RADDRESS.RECIPIENT, RADDRESS.STREET_TYPE,
						RADDRESS.ADDRESS, RADDRESS.ADDRESS2, RADDRESS.ADDRESS3,
						RADDRESS.NUMBER, RADDRESS.ZIP, RADDRESS.CITY,
						RADDRESS.GEOZONE, RADDRESS.MUNICIPALITY_CODE)
				.values(domain, id, registry, alias, type, recipient,
						streetType, address, address2, address3, number, zip,
						city, geozone, municipalityCode).execute();
	}
	
	public static void createWorkplace(AONContext ctx, int domain, int id,
			int enterprise, byte active, int address, Integer customer,
			String description, Byte economicAgreement, int scope) {
		ctx.getDslContext()
				.insertInto(WORKPLACE, WORKPLACE.DOMAIN, WORKPLACE.ID,
						WORKPLACE.ENTERPRISE, WORKPLACE.ACTIVE,
						WORKPLACE.ADDRESS, WORKPLACE.CUSTOMER,
						WORKPLACE.DESCRIPTION, WORKPLACE.ECONOMICAGREEMENT,
						WORKPLACE.SCOPE)
				.values(domain, id, enterprise, active, address, customer,
						description, economicAgreement, scope).execute();
	}

	
	
	
}
