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
import java.util.function.Function;

import org.jooq.Condition;

import com.esferalia.aon.jooq.tables.records.SalesDetailRecord;
import com.esferalia.aon.jooq.tables.records.SalesRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.management.Sales;
import com.esferalia.aon.occam.api.model.management.SalesDetail;
import com.esferalia.aon.occam.api.model.management.SalesDetailFilter;
import com.esferalia.aon.occam.api.model.management.SalesDetailProperties;
import com.esferalia.aon.occam.api.model.management.SalesFilter;
import com.esferalia.aon.occam.api.model.management.SalesProperties;
import com.esferalia.aon.occam.api.model.type.SalesDetailStatus;
import com.esferalia.aon.occam.api.model.type.SalesStatus;


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
	
	public static void insertSalesDetail(AONContext ctx, SalesDetail detail) {
		ctx.checkWrite();
		Timestamp creationDate = null, modificationDate = null;
		creationDate = new java.sql.Timestamp(new java.util.Date().getTime());
		
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
	
	public static void updateSalesDetail(AONContext ctx, SalesDetail detail) {
		ctx.checkWrite();
		Timestamp modificationDate = null;
		modificationDate = new java.sql.Timestamp(new java.util.Date().getTime());
		
		ctx.getDslContext()
				.update(SALES_DETAIL)
//				.set(SALES_DETAIL.DOMAIN, detail.getDomain())
//				.set(SALES_DETAIL.SALES, detail.getSales())
				.set(SALES_DETAIL.LINE, detail.getLine())
				.set(SALES_DETAIL.ITEM, detail.getItem())
				.set(SALES_DETAIL.DESCRIPTION, detail.getDescription())
				.set(SALES_DETAIL.QUANTITY, detail.getQuantity())
				.set(SALES_DETAIL.PRICE, detail.getPrice())
				.set(SALES_DETAIL.DISCOUNT_EXPR, detail.getDiscountExpression())
				.set(SALES_DETAIL.TAXES, detail.getTaxes())
				.set(SALES_DETAIL.STATUS, (byte) detail.getStatus().ordinal())
				.set(SALES_DETAIL.OFFER_DETAIL, detail.getOfferDetail())
				.set(SALES_DETAIL.DELIVERED, detail.getDelivered())
//				.set(SALES_DETAIL.CREATION_USER, ctx.getUser())
//				.set(SALES_DETAIL.CREATION_DATE, creationDate)
				.set(SALES_DETAIL.MODIFICATION_USER, ctx.getUser())
				.set(SALES_DETAIL.MODIFICATION_DATE, modificationDate)
				.where(SALES_DETAIL.ID.eq(detail.getId())).execute();
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
	

	
	private static class FullSalesFiller implements Function<SalesRecord, Sales> {
		
		@Override
		public Sales apply(SalesRecord r) {
			Sales sales = new Sales();
			sales.setId(r.getId());
			sales.setDomain(r.getDomain());
			sales.setProject(r.getProject());
			sales.setCustomer(r.getCustomer());
			sales.setSeries(r.getSeries());
			sales.setNumber(r.getNumber());
			sales.setPurchaseReference(r.getPurchaseReference());
			sales.setShippingAddress(r.getShippingAddress());
			sales.setSeller(r.getSeller());
			sales.setDiscountExpr(r.getDiscountExpr());
			sales.setIssueDate(r.getIssueDate());
			sales.setPayMethod(r.getPayMethod());
			sales.setDocumentType(r.getDocumentType());
			sales.setSecurityLevel(r.getSecurityLevel());
			sales.setStatus(SalesStatus.values()[r.getStatus()]);
			sales.setComments(r.getComments());
			sales.setRemarks(r.getRemarks());
			sales.setWorkplace(r.getWorkplace());
			sales.setScope(r.getScope());
			sales.setNumberOfPymnts(r.getNumberOfPymnts());
			sales.setDaysToFirstPymnt(r.getDaysToFirstPymnt());
			sales.setDaysBetweenPymnts(r.getDaysBetweenPymnts());
			sales.setPymntDays(r.getPymntDays());
			sales.setBankAccount(r.getBankAccount());
			sales.setBankAlias(r.getBankAlias());
			sales.setBic(r.getBic());
			sales.setPurchaseGenerated(r.getPurchaseGenerated()==1);
			sales.setCarrier(r.getCarrier());
			sales.setShippingAlternativeAddress(r.getShippingAlternativeAddress());
			sales.setShippingAlternativeAddress2(r.getShippingAlternativeAddress2());
			sales.setShippingAlternativeZip(r.getShippingAlternativeZip());
			sales.setShippingAlternativeCity(r.getShippingAlternativeCity());
			sales.setShippingAlternativePhone(r.getShippingAlternativePhone());
			sales.setShippingAlternativeRecipient(r.getShippingAlternativeRecipient());
			sales.setShippingContact(r.getShippingContact());
			sales.setShippingPeriod(r.getShippingPeriod()!=null?r.getShippingPeriod().intValue():null);
			return sales;
		}
	}
	
	private static class FullSalesDetailFiller implements Function<SalesDetailRecord, SalesDetail> {
		
		@Override
		public SalesDetail apply(SalesDetailRecord r) {
			SalesDetail detail = new SalesDetail();
			detail.setId(r.getId());
			detail.setDomain(r.getDomain());
			detail.setSales(r.getSales());
			detail.setItem(r.getItem());
			detail.setLine(r.getLine());
			detail.setDescription(r.getDescription());
			detail.setQuantity(r.getQuantity());
			detail.setPrice(r.getPrice());
			detail.setDiscountExpression(r.getDiscountExpr());
			detail.setTaxes(r.getTaxes());
			detail.setStatus(SalesDetailStatus.values()[r.getStatus()]);
			detail.setOfferDetail(r.getOfferDetail());
			detail.setDelivered(r.getDelivered());
			return detail;
		}
	}
}
