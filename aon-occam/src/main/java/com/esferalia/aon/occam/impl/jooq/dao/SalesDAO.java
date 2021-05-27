package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Carrier.CARRIER;
import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Pcategory.PCATEGORY;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.Project.PROJECT;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Sales.SALES;
import static com.esferalia.aon.jooq.tables.SalesDetail.SALES_DETAIL;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;
import static com.esferalia.aon.jooq.tables.Seller.SELLER;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Result;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.SalesDetailFilter;
import com.esferalia.aon.occam.api.model.Filter.SalesFilter;
import com.esferalia.aon.occam.api.model.Properties.SalesDetailProperties;
import com.esferalia.aon.occam.api.model.Properties.SalesProperties;
import com.esferalia.aon.occam.api.model.management.Sales;
import com.esferalia.aon.occam.api.model.management.SalesDetail;
import com.esferalia.aon.occam.api.model.product.OldItem;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.SalesDetailStatus;
import com.esferalia.aon.occam.api.model.type.SalesStatus;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.CustomerFiller;
import com.esferalia.aon.watson.util.AonEnumUtils;

public class SalesDAO {
	
	private static final SalesPropertiesDAO SALES_PROPERTIES = new SalesPropertiesDAO();
	private static final SalesDetailPropertiesDAO SALES_DETAIL_PROPERTIES = new SalesDetailPropertiesDAO();

	protected static class SalesPropertiesDAO implements SalesProperties {
		protected Condition[] getConditions(SalesFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(SALES.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(SALES.DOMAIN);}
		@Override public Property<Integer> getProjectProperty() {return new FilterDAO.PropertyDAO<Integer>(SALES.PROJECT);}
		@Override public Property<Integer> getCustomerProperty() {return new FilterDAO.PropertyDAO<Integer>(SALES.CUSTOMER);}
		@Override public Property<String> getSeriesProperty() {return new FilterDAO.PropertyDAO<String>(SALES.SERIES);}
		@Override public Property<Integer> getNumberProperty() {return new FilterDAO.PropertyDAO<Integer>(SALES.NUMBER);}
		@Override public Property<String> getPurchaseReferenceProperty() {return new FilterDAO.PropertyDAO<String>(SALES.PURCHASE_REFERENCE);}
		@Override public Property<Integer> getShippingAddressProperty() {return new FilterDAO.PropertyDAO<Integer>(SALES.SHIPPING_ADDRESS);}
		@Override public Property<Integer> getSellerProperty() {return new FilterDAO.PropertyDAO<Integer>(SALES.SELLER);}
		@Override public Property<String> getDiscountExprProperty() {return new FilterDAO.PropertyDAO<String>(SALES.DISCOUNT_EXPR);}
		@Override public Property<java.sql.Date> getIssueDateProperty() {return new FilterDAO.PropertyDAO<java.sql.Date>(SALES.ISSUE_DATE);}
		@Override public Property<Integer> getPayMethodProperty() {return new FilterDAO.PropertyDAO<Integer>(SALES.PAY_METHOD);}
		@Override public Property<Byte> getDocumentTypeProperty() {return new FilterDAO.PropertyDAO<Byte>(SALES.DOCUMENT_TYPE);}
		@Override public Property<Byte> getSecurityLevelProperty() {return new FilterDAO.PropertyDAO<Byte>(SALES.SECURITY_LEVEL);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<Byte>(SALES.STATUS);}
		@Override public Property<String> getCommentsProperty() {return new FilterDAO.PropertyDAO<String>(SALES.COMMENTS);}
		@Override public Property<String> getRemarksProperty() {return new FilterDAO.PropertyDAO<String>(SALES.REMARKS);}
		@Override public Property<Integer> getWorkplaceProperty() {return new FilterDAO.PropertyDAO<Integer>(SALES.WORKPLACE);}
		@Override public Property<Integer> getScopeProperty() {return new FilterDAO.PropertyDAO<Integer>(SALES.SCOPE);}
		@Override public Property<Short> getNumberOfPymntsProperty() {return new FilterDAO.PropertyDAO<Short>(SALES.NUMBER_OF_PYMNTS);}
		@Override public Property<Short> getDaysToFirstPymntProperty() {return new FilterDAO.PropertyDAO<Short>(SALES.DAYS_TO_FIRST_PYMNT);}
		@Override public Property<Short> getDaysBetweenPymntsProperty() {return new FilterDAO.PropertyDAO<Short>(SALES.DAYS_BETWEEN_PYMNTS);}
		@Override public Property<String> getPymntDaysProperty() {return new FilterDAO.PropertyDAO<String>(SALES.PYMNT_DAYS);}
		@Override public Property<String> getBankAccountProperty() {return new FilterDAO.PropertyDAO<String>(SALES.BANK_ACCOUNT);}
		@Override public Property<String> getBankAliasProperty() {return new FilterDAO.PropertyDAO<String>(SALES.BANK_ALIAS);}
		@Override public Property<String> getBicProperty() {return new FilterDAO.PropertyDAO<String>(SALES.BIC);}
		@Override public Property<Byte> getPurchaseGeneratedProperty() {return new FilterDAO.PropertyDAO<Byte>(SALES.PURCHASE_GENERATED);}
		@Override public Property<Integer> getCarrierProperty() {return new FilterDAO.PropertyDAO<Integer>(SALES.CARRIER);}
		@Override public Property<String> getShippingAlternativeAddressProperty() {return new FilterDAO.PropertyDAO<String>(SALES.SHIPPING_ALTERNATIVE_ADDRESS);}
		@Override public Property<String> getShippingAlternativeAddress2Property() {return new FilterDAO.PropertyDAO<String>(SALES.SHIPPING_ALTERNATIVE_ADDRESS2);}
		@Override public Property<String> getShippingAlternativeZipProperty() {return new FilterDAO.PropertyDAO<String>(SALES.SHIPPING_ALTERNATIVE_ZIP);}
		@Override public Property<String> getShippingAlternativeCityProperty() {return new FilterDAO.PropertyDAO<String>(SALES.SHIPPING_ALTERNATIVE_CITY);}
		@Override public Property<String> getShippingAlternativePhoneProperty() {return new FilterDAO.PropertyDAO<String>(SALES.SHIPPING_ALTERNATIVE_PHONE);}
		@Override public Property<String> getShippingAlternativeRecipientProperty() {return new FilterDAO.PropertyDAO<String>(SALES.SHIPPING_ALTERNATIVE_RECIPIENT);}
		@Override public Property<String> getShippingContactProperty() {return new FilterDAO.PropertyDAO<String>(SALES.SHIPPING_CONTACT);}
		@Override public Property<Byte> getShippingPeriodProperty() {return new FilterDAO.PropertyDAO<Byte>(SALES.SHIPPING_PERIOD);}
		@Override public Property<Byte> getConfidentialProperty() {return null;}
	}
	
	protected static class SalesDetailPropertiesDAO implements SalesDetailProperties {
		protected Condition[] getConditions(SalesDetailFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(SALES_DETAIL.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(SALES_DETAIL.DOMAIN);}
		@Override public Property<Integer> getSalesProperty() {return new FilterDAO.PropertyDAO<Integer>(SALES_DETAIL.SALES);}
		@Override public Property<Integer> getItemProperty() {return new FilterDAO.PropertyDAO<Integer>(SALES_DETAIL.ITEM);}
		@Override public Property<Short> getLineProperty() {return new FilterDAO.PropertyDAO<Short>(SALES_DETAIL.LINE);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<String>(SALES_DETAIL.DESCRIPTION);}
		@Override public Property<Double> getQuantityProperty() {return new FilterDAO.PropertyDAO<Double>(SALES_DETAIL.QUANTITY);}
		@Override public Property<Double> getPriceProperty() {return new FilterDAO.PropertyDAO<Double>(SALES_DETAIL.PRICE);}
		@Override public Property<String> getdiscountExpressionProperty() {return new FilterDAO.PropertyDAO<String>(SALES_DETAIL.DISCOUNT_EXPR);}
		@Override public Property<Double> getTaxesProperty() {return new FilterDAO.PropertyDAO<Double>(SALES_DETAIL.TAXES);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<Byte>(SALES_DETAIL.STATUS);}
		@Override public Property<Integer> getOfferDetailProperty() {return new FilterDAO.PropertyDAO<Integer>(SALES_DETAIL.OFFER_DETAIL);}
		@Override public Property<Double> getDeliveredProperty() {return new FilterDAO.PropertyDAO<Double>(SALES_DETAIL.DELIVERED);}
	}
	
	public static int insertSales(AONContext ctx, Sales sales) {
		ctx.checkWrite();
		
		Timestamp creationDate = null, modificationDate = null;
		creationDate = new java.sql.Timestamp(new java.util.Date().getTime());
		
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
	

	public static void updateSales(AONContext ctx, Sales sales) {
		ctx.checkWrite();
		Timestamp modificationDate = null;
		modificationDate = new java.sql.Timestamp(new java.util.Date().getTime());
		
		ctx.getDslContext()
				.update(SALES)
				.set(SALES.DOMAIN, sales.getDomain())
				.set(SALES.PROJECT, sales.getProject())
				.set(SALES.CUSTOMER, sales.getCustomer().getId())
				.set(SALES.SERIES, sales.getSeries())
				.set(SALES.NUMBER, sales.getNumber())
				.set(SALES.PURCHASE_REFERENCE, sales.getPurchaseReference())
				.set(SALES.SHIPPING_ADDRESS, sales.getShippingAddress())
				.set(SALES.SELLER, sales.getSeller())
				.set(SALES.DISCOUNT_EXPR, sales.getDiscountExpr())
				.set(SALES.ISSUE_DATE, new java.sql.Date(sales.getIssueDate().getTime()))
				.set(SALES.PAY_METHOD, sales.getPayMethod())
				.set(SALES.DOCUMENT_TYPE, (byte)sales.getDocumentType())
				.set(SALES.SECURITY_LEVEL, (byte)sales.getSecurityLevel())
				.set(SALES.STATUS, sales.getStatus().value())
				.set(SALES.COMMENTS, sales.getComments())
				.set(SALES.REMARKS, sales.getRemarks())
				.set(SALES.WORKPLACE, sales.getWorkplace())
				.set(SALES.SCOPE, sales.getScope())
				.set(SALES.NUMBER_OF_PYMNTS, (short)sales.getNumberOfPymnts())
				.set(SALES.DAYS_TO_FIRST_PYMNT, (short)sales.getDaysToFirstPymnt())
				.set(SALES.DAYS_BETWEEN_PYMNTS, (short)sales.getDaysBetweenPymnts())
				.set(SALES.PYMNT_DAYS, sales.getPymntDays())
				.set(SALES.BANK_ACCOUNT, sales.getBankAccount())
				.set(SALES.BANK_ALIAS, sales.getBankAlias())
				.set(SALES.BIC, sales.getBic())
				.set(SALES.PURCHASE_GENERATED, (byte) (sales.isPurchaseGenerated() ? 1 : 0 ))
				.set(SALES.CARRIER, sales.getCarrier())
				.set(SALES.SHIPPING_ALTERNATIVE_ADDRESS, sales.getShippingAlternativeAddress())
				.set(SALES.SHIPPING_ALTERNATIVE_ADDRESS2, sales.getShippingAlternativeAddress2())
				.set(SALES.SHIPPING_ALTERNATIVE_ZIP, sales.getShippingAlternativeZip())
				.set(SALES.SHIPPING_ALTERNATIVE_CITY, sales.getShippingAlternativeCity())
				.set(SALES.SHIPPING_ALTERNATIVE_PHONE, sales.getShippingAlternativePhone())
				.set(SALES.SHIPPING_ALTERNATIVE_RECIPIENT, sales.getShippingAlternativeRecipient())
				.set(SALES.SHIPPING_CONTACT, sales.getShippingContact())
				.set(SALES.SHIPPING_PERIOD, sales.getShippingPeriod()!=null?sales.getShippingPeriod().byteValue():null)
				.set(SALES.MODIFICATION_USER, ctx.getUser())
				.set(SALES.MODIFICATION_DATE, modificationDate)
				.where(SALES.ID.eq(sales.getId()))
				.execute();
	}
	
	public static void insertSalesDetail(AONContext ctx, SalesDetail detail) {
		ctx.checkWrite();
		Timestamp creationDate = null, modificationDate = null;
		creationDate = new java.sql.Timestamp(new java.util.Date().getTime());

		ctx.getDslContext()
				.insertInto(SALES_DETAIL, SALES_DETAIL.DOMAIN,
						SALES_DETAIL.SALES, SALES_DETAIL.LINE,
						SALES_DETAIL.ITEM, SALES_DETAIL.DESCRIPTION,
						SALES_DETAIL.QUANTITY, SALES_DETAIL.PRICE,
						SALES_DETAIL.DISCOUNT_EXPR, SALES_DETAIL.TAXES,
						SALES_DETAIL.STATUS, SALES_DETAIL.OFFER_DETAIL,
						SALES_DETAIL.DELIVERED, SALES_DETAIL.CREATION_USER,
						SALES_DETAIL.CREATION_DATE,
						SALES_DETAIL.MODIFICATION_USER,
						SALES_DETAIL.MODIFICATION_DATE)
				.values(detail.getDomain(), detail.getSales().getId(),
						detail.getLine(), detail.getItem().getId(),
						detail.getDescription(), detail.getQuantity(),
						detail.getPrice(), detail.getDiscountExpression(),
						detail.getTaxes(), (byte) detail.getStatus().ordinal(),
						detail.getOfferDetail(), detail.getDelivered(),
						ctx.getUser(), creationDate, ctx.getUser(),
						modificationDate).execute();
	}
	
	public static void updateSalesDetail(AONContext ctx, SalesDetail detail) {
		ctx.checkWrite();
		Timestamp modificationDate = null;
		modificationDate = new java.sql.Timestamp(
				new java.util.Date().getTime());

		ctx.getDslContext()
				.update(SALES_DETAIL)
				.set(SALES_DETAIL.LINE, detail.getLine())
				.set(SALES_DETAIL.ITEM, detail.getItem().getId())
				.set(SALES_DETAIL.DESCRIPTION, detail.getDescription())
				.set(SALES_DETAIL.QUANTITY, detail.getQuantity())
				.set(SALES_DETAIL.PRICE, detail.getPrice())
				.set(SALES_DETAIL.DISCOUNT_EXPR, detail.getDiscountExpression())
				.set(SALES_DETAIL.TAXES, detail.getTaxes())
				.set(SALES_DETAIL.STATUS, (byte) detail.getStatus().ordinal())
				.set(SALES_DETAIL.OFFER_DETAIL, detail.getOfferDetail())
				.set(SALES_DETAIL.DELIVERED, detail.getDelivered())
				.set(SALES_DETAIL.MODIFICATION_USER, ctx.getUser())
				.set(SALES_DETAIL.MODIFICATION_DATE, modificationDate)
				.where(SALES_DETAIL.ID.eq(detail.getId())).execute();
	}
	
	public static void deleteSalesDetail(AONContext ctx, Sales sales) {
		ctx.checkWrite();
		ctx.getDslContext()
				.delete(SALES_DETAIL)
				.where(SALES_DETAIL.SALES.eq(sales.getId())).execute();
	}
	
	public static Stream<Sales> getSalesStream(AONContext ctx, SalesFilter filter){
		return ctx.getDslContext().select().from(SALES)
				.where(SALES_PROPERTIES.getConditions(filter))
			.fetch().stream().map(new FullSalesFiller()).filter(distinctByKey(p -> p.getId()));
	}
	public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
	    Map<Object,Boolean> seen = new ConcurrentHashMap<>();
	    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}
	
	public static Sales getSales(AONContext ctx, SalesFilter filter){
		return ctx.getDslContext().select().from(SALES).where(SALES_PROPERTIES.getConditions(filter))
				.limit(1).fetchInto(SALES).stream().map(new FullSalesFiller()).findFirst().orElse(new Sales());
	}
	
	public static Sales getSales(AONContext ctx, Integer salesId){	
		return getSales(ctx, o -> o.getIdProperty().eq(salesId));
	}
	
	public static Sales getSales(AONContext ctx, String series, int number) {
		return getSales(ctx, o -> o.getSeriesProperty().eq(series).and(o.getNumberProperty().eq(number)));
	}
	
	public static Stream<SalesDetail> getSalesDetailStream(AONContext ctx, SalesDetailFilter filter){
		return ctx.getDslContext().select().from(SALES_DETAIL)
				.join(ITEM).on(SALES_DETAIL.ITEM.eq(ITEM.ID))
				.join(PRODUCT).on(ITEM.PRODUCT.eq(PRODUCT.ID))
				.join(SALES).on(SALES_DETAIL.SALES.eq(SALES.ID))
			.where(SALES_DETAIL_PROPERTIES.getConditions(filter))
			.fetch().stream().map(new FullSalesDetailFiller());
	}
	
	public static SalesDetail getSalesDetail(AONContext ctx, SalesDetailFilter filter){
		return ctx.getDslContext().select().from(SALES_DETAIL).where(SALES_DETAIL_PROPERTIES.getConditions(filter))
				.limit(1).fetchInto(SALES_DETAIL).stream().map(new FullSalesDetailFiller()).findFirst().orElse(new SalesDetail());
	}
	
	public static SalesDetail getSalesDetail(AONContext ctx, Integer detailId){	
		return getSalesDetail(ctx, o -> o.getIdProperty().eq(detailId));
	}
	
	public static SalesDetail getSalesDetail(AONContext ctx, Integer salesId, Short line){	
		return getSalesDetail(ctx, o -> o.getSalesProperty().eq(salesId).and(o.getLineProperty().eq(line)));
	}
	
	public static List<Customer> getCustomerList(AONContext ctx, String document) {
		return ctx.getDslContext().select().from(CUSTOMER).join(REGISTRY)
				.on(REGISTRY.ID.eq(CUSTOMER.REGISTRY))
				.where(CUSTOMER.DOMAIN.eq(ctx.getDomainId()))
				.and(REGISTRY.DOCUMENT.eq(document)).fetch().stream()
				.map(new CustomerFiller()).collect(Collectors.toList());
	}
	
	public static void createCustomer(AONContext ctx, int domain,
			int registry, int scope) {
		ctx.checkWrite();
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
		ctx.checkWrite();
		ctx.getDslContext()
				.insertInto(SELLER, SELLER.DOMAIN, SELLER.REGISTRY,
						SELLER.SCOPE, SELLER.STATUS)
				.values(domain, registry, scope, (byte) 0).execute();
	}
	
	public static void createCarrier(AONContext ctx, int domain, int registry,
			int scope) {
		ctx.checkWrite();
		ctx.getDslContext()
				.insertInto(CARRIER, CARRIER.DOMAIN, CARRIER.REGISTRY,
						CARRIER.SCOPE).values(domain, registry, scope)
				.execute();
	}
	
	public static void createRegistry(AONContext ctx, int domain, int id,
			Byte type, String name, String alias, String nationality,
			Byte documentType, String documentCountry, String document) {
		ctx.checkWrite();
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
		ctx.checkWrite();
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
		ctx.checkWrite();
		ctx.getDslContext()
				.insertInto(WORKPLACE, WORKPLACE.DOMAIN, WORKPLACE.ID,
						WORKPLACE.ENTERPRISE, WORKPLACE.ACTIVE,
						WORKPLACE.ADDRESS, WORKPLACE.CUSTOMER,
						WORKPLACE.DESCRIPTION, WORKPLACE.ECONOMICAGREEMENT,
						WORKPLACE.SCOPE)
				.values(domain, id, enterprise, active, address, customer,
						description, economicAgreement, scope).execute();
	}
	
	
	private static Result<Record> getFullSales(AONContext ctx, SalesFilter filter) {
		ctx.checkRead();

		return ctx.getDslContext()
			.select(
				 SALES.ID
				,SALES.DOMAIN
				,SALES.STATUS
				,SALES.SERIES
				,SALES.NUMBER
				,SALES.DOCUMENT_TYPE
				,SALES.ISSUE_DATE
				,SALES.PURCHASE_REFERENCE
				,SALES.SHIPPING_ADDRESS
				,REGISTRY.ID
				,REGISTRY.DOCUMENT
				,REGISTRY.DOCUMENT_TYPE
				,REGISTRY.DOCUMENT_COUNTRY
				,REGISTRY.NAME
				,SCOPE.DESCRIPTION
				,PROJECT.NAME
				,SALES_DETAIL.LINE
				,SALES_DETAIL.ITEM
				,PCATEGORY.NAME
				,PRODUCT.ID
				,PRODUCT.NAME
				,PRODUCT.CODE
				,ITEM.DETAIL
				,ITEM.DETAIL2
				,ITEM.DETAIL3
				,ITEM.DESCRIPTION
				,SALES_DETAIL.DESCRIPTION
				,SALES_DETAIL.QUANTITY
				,SALES_DETAIL.PRICE
				,SALES_DETAIL.DISCOUNT_EXPR
				,WORKPLACE.DESCRIPTION
			)
			.from(SALES)
			.join(SALES_DETAIL).on(SALES_DETAIL.SALES.equal(SALES.ID))
			.join(REGISTRY).on(REGISTRY.ID.equal(SALES.CUSTOMER))
			.leftOuterJoin(SCOPE).on(SCOPE.ID.equal(SALES.SCOPE))
			.leftOuterJoin(PROJECT).on(PROJECT.ID.equal(SALES.PROJECT))
			.leftOuterJoin(ITEM).on(ITEM.ID.equal(SALES_DETAIL.ITEM))
			.leftOuterJoin(PRODUCT).on(PRODUCT.ID.equal(ITEM.PRODUCT))
			.leftOuterJoin(PCATEGORY).on(PRODUCT.CATEGORY.equal(PCATEGORY.ID))
			.leftOuterJoin(WORKPLACE).on(WORKPLACE.ID.equal(SALES.WORKPLACE))
			.where(SALES_PROPERTIES.getConditions(filter))
			.orderBy(SALES.ISSUE_DATE,SALES.SERIES,SALES.NUMBER,SALES_DETAIL.LINE)
			.fetch();
	}
	
	public static Stream<SalesDetail> getSalesDetails(AONContext ctx, SalesFilter filter) {
		return getFullSales(ctx, filter)
			.stream()
			.map(new FullSaleDetailFiller2());
	}
	
	private static class FullSaleDetailFiller2  implements Function<Record,SalesDetail> {

		@Override
		public SalesDetail apply(Record record) {
			Customer customer = new Customer();
			customer.setId(record.getValue(REGISTRY.ID));
			customer.setDocument(record.getValue(REGISTRY.DOCUMENT));
			customer.setDocumentType(AonEnumUtils.enumValue(DocumentType.class,
									record.getValue(REGISTRY.DOCUMENT_TYPE)));
			customer.setDocumentCountry(Country.safeValueOf(record
									.getValue(REGISTRY.DOCUMENT_COUNTRY)));
			customer.setName(record.getValue(REGISTRY.NAME));
			
			return new SalesDetail()
				.setSales(new Sales()
					.setId(record.getValue(SALES.ID))
					.setDomain(record.getValue(SALES.DOMAIN))
					.setDocumentType(record.getValue(SALES.DOCUMENT_TYPE))
					.setStatus(AonEnumUtils.enumValue(SalesStatus.class,
									record.getValue(SALES.STATUS)))
					.setSeries(record.getValue(SALES.SERIES))
					.setNumber(record.getValue(SALES.NUMBER))
					.setIssueDate(record.getValue(SALES.ISSUE_DATE))
					.setCustomer(customer)
					.setScopeName(record.getValue(SCOPE.DESCRIPTION))						
					.setPurchaseReference(record.getValue(SALES.PURCHASE_REFERENCE))
					.setProjectName(record.getValue(PROJECT.NAME))
					.setShippingAddress(record.getValue(SALES.SHIPPING_ADDRESS))
					)
				
				.setLine(record.getValue(SALES_DETAIL.LINE))
				.setDescription(record.getValue(SALES_DETAIL.DESCRIPTION))
				.setQuantity(record.getValue(SALES_DETAIL.QUANTITY))
				.setPrice(record.getValue(SALES_DETAIL.PRICE))
				.setDiscountExpression(record.getValue(SALES_DETAIL.DISCOUNT_EXPR))
				.setItem((record.getValue(SALES_DETAIL.ITEM) == null)
					? null
					: new OldItem()
						.setId(record.getValue(SALES_DETAIL.ITEM))
						.setCategory(record.getValue(PCATEGORY.NAME))
						.setProductId(record.getValue(PRODUCT.ID))
						.setName(record.getValue(PRODUCT.NAME))
						.setCode(record.getValue(PRODUCT.CODE))
						.setDetail(record.getValue(ITEM.DETAIL))
						.setDetail2(record.getValue(ITEM.DETAIL2))
						.setDetail3(record.getValue(ITEM.DETAIL3))
						.setDescription(record.getValue(ITEM.DESCRIPTION)));
		}
	}


	
	private static class FullSalesFiller implements Function<Record, Sales> {
		
		@Override
		public Sales apply(Record r) {
			Customer customer = new Customer();
			customer.setId(r.getValue(SALES.CUSTOMER));

			Sales sales = new Sales();
			sales.setId(r.getValue(SALES.ID));
			sales.setDomain(r.getValue(SALES.DOMAIN));
			sales.setProject(r.getValue(SALES.PROJECT));
			sales.setCustomer(customer);
			sales.setSeries(r.getValue(SALES.SERIES));
			sales.setNumber(r.getValue(SALES.NUMBER));
			sales.setPurchaseReference(r.getValue(SALES.PURCHASE_REFERENCE));
			sales.setShippingAddress(r.getValue(SALES.SHIPPING_ADDRESS));
			sales.setSeller(r.getValue(SALES.SELLER));
			sales.setDiscountExpr(r.getValue(SALES.DISCOUNT_EXPR));
			sales.setIssueDate(r.getValue(SALES.ISSUE_DATE));
			sales.setDeliveryDate(r.getValue(SALES.DELIVERY_DATE));
			sales.setPayMethod(r.getValue(SALES.PAY_METHOD));
			sales.setDocumentType((int) r.getValue(SALES.DOCUMENT_TYPE));
			sales.setSecurityLevel((int) r.getValue(SALES.SECURITY_LEVEL));
			sales.setStatus(SalesStatus.values()[r.getValue(SALES.STATUS)]);
			sales.setComments(r.getValue(SALES.COMMENTS));
			sales.setRemarks(r.getValue(SALES.REMARKS));
			sales.setWorkplace(r.getValue(SALES.WORKPLACE));
			sales.setScope(r.getValue(SALES.SCOPE));
			sales.setNumberOfPymnts((int) r.getValue(SALES.NUMBER_OF_PYMNTS));
			sales.setDaysToFirstPymnt((int) r.getValue(SALES.DAYS_TO_FIRST_PYMNT));
			sales.setDaysBetweenPymnts((int) r.getValue(SALES.DAYS_BETWEEN_PYMNTS));
			sales.setPymntDays(r.getValue(SALES.PYMNT_DAYS));
			sales.setBankAccount(r.getValue(SALES.BANK_ACCOUNT));
			sales.setBankAlias(r.getValue(SALES.BANK_ALIAS));
			sales.setBic(r.getValue(SALES.BIC));
			sales.setPurchaseGenerated(r.getValue(SALES.PURCHASE_GENERATED)==1);
			sales.setCarrier(r.getValue(SALES.CARRIER));
			sales.setShippingAlternativeAddress(r.getValue(SALES.SHIPPING_ALTERNATIVE_ADDRESS));
			sales.setShippingAlternativeAddress2(r.getValue(SALES.SHIPPING_ALTERNATIVE_ADDRESS2));
			sales.setShippingAlternativeZip(r.getValue(SALES.SHIPPING_ALTERNATIVE_ZIP));
			sales.setShippingAlternativeCity(r.getValue(SALES.SHIPPING_ALTERNATIVE_CITY));
			sales.setShippingAlternativePhone(r.getValue(SALES.SHIPPING_ALTERNATIVE_PHONE));
			sales.setShippingAlternativeRecipient(r.getValue(SALES.SHIPPING_ALTERNATIVE_RECIPIENT));
			sales.setShippingContact(r.getValue(SALES.SHIPPING_CONTACT));
			sales.setShippingPeriod(r.getValue(SALES.SHIPPING_PERIOD)!=null?r.getValue(SALES.SHIPPING_PERIOD).intValue():null);
			return sales;
		}
	}
	
	private static class FullSalesDetailFiller implements Function<Record, SalesDetail> {
		
		@Override
		public SalesDetail apply(Record r) {
			SalesDetail detail = new SalesDetail();
			detail.setId(r.getValue(SALES_DETAIL.ID));
			detail.setDomain(r.getValue(SALES_DETAIL.DOMAIN));
			detail.setSales(new Sales().setId(r.getValue(SALES_DETAIL.SALES)));
			detail.setItem(new OldItem().setId(r.getValue(SALES_DETAIL.ITEM)));
			detail.setLine(r.getValue(SALES_DETAIL.LINE));
			detail.setDescription(r.getValue(SALES_DETAIL.DESCRIPTION));
			detail.setQuantity(r.getValue(SALES_DETAIL.QUANTITY));
			detail.setPrice(r.getValue(SALES_DETAIL.PRICE));
			detail.setDiscountExpression(r.getValue(SALES_DETAIL.DISCOUNT_EXPR));
			detail.setTaxes(r.getValue(SALES_DETAIL.TAXES));
			detail.setStatus(SalesDetailStatus.values()[r.getValue(SALES_DETAIL.STATUS)]);
			detail.setOfferDetail(r.getValue(SALES_DETAIL.OFFER_DETAIL));
			detail.setDelivered(r.getValue(SALES_DETAIL.DELIVERED));
			return detail;
		}
	}
}
