package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Carrier.CARRIER;
import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.PayMethod.PAY_METHOD;
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
import static com.esferalia.aon.occam.impl.jooq.dao.CarrierDAO.CARRIER_ALIAS;
import static com.esferalia.aon.occam.impl.jooq.dao.CustomerDAO.CUSTOMER_ALIAS;
import static com.esferalia.aon.occam.impl.jooq.dao.SellerDAO.SELLER_COMERCIAL_ALIAS;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Options;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.SalesDetailFilter;
import com.esferalia.aon.occam.api.model.Filter.SalesFilter;
import com.esferalia.aon.occam.api.model.Properties.SalesProperties;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.api.model.management.Sales;
import com.esferalia.aon.occam.api.model.management.SalesDetail;
import com.esferalia.aon.occam.api.model.management.ShipmentPeriod;
import com.esferalia.aon.occam.api.model.registry.Carrier;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.type.SalesStatus;
import com.esferalia.aon.occam.api.model.type.SalesType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.impl.jooq.dao.CarrierDAO.CarrierFiller;
import com.esferalia.aon.occam.impl.jooq.dao.CustomerDAO.CustomerFiller;
import com.esferalia.aon.occam.impl.jooq.dao.PayMethodDAO.PayMethodFiller;
import com.esferalia.aon.occam.impl.jooq.dao.ProjectDAO.ProjectFiller;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryAddressDAO.RegistryAddressFiller;
import com.esferalia.aon.occam.impl.jooq.dao.SalesDetailDAO.SalesDetailFiller;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO.ScopeFiller;
import com.esferalia.aon.occam.impl.jooq.dao.SellerDAO.SellerFiller;
import com.esferalia.aon.occam.impl.jooq.dao.WorkplaceDAO.WorkplaceFiller;
import com.esferalia.aon.occam.impl.jooq.validation.SalesValidation;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class SalesDAO {
	
	public static final SalesPropertiesDAO SALES_PROPERTIES = new SalesPropertiesDAO();

	private SalesDAO() {
	
	}
	
	protected static class SalesPropertiesDAO implements SalesProperties {
		protected Condition[] getConditions(SalesFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(SALES.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(SALES.DOMAIN);}
		@Override public Property<Integer> getProjectProperty() {return new FilterDAO.PropertyDAO<>(SALES.PROJECT);}
		@Override public Property<Integer> getCustomerProperty() {return new FilterDAO.PropertyDAO<>(SALES.CUSTOMER);}
		@Override public Property<String> getSeriesProperty() {return new FilterDAO.PropertyDAO<>(SALES.SERIES);}
		@Override public Property<Integer> getNumberProperty() {return new FilterDAO.PropertyDAO<>(SALES.NUMBER);}
		@Override public Property<String> getPurchaseReferenceProperty() {return new FilterDAO.PropertyDAO<>(SALES.PURCHASE_REFERENCE);}
		@Override public Property<Integer> getShippingAddressProperty() {return new FilterDAO.PropertyDAO<>(SALES.SHIPPING_ADDRESS);}
		@Override public Property<Integer> getSellerProperty() {return new FilterDAO.PropertyDAO<>(SALES.SELLER);}
		@Override public Property<String> getDiscountExprProperty() {return new FilterDAO.PropertyDAO<>(SALES.DISCOUNT_EXPR);}
		@Override public Property<java.sql.Date> getIssueDateProperty() {return new FilterDAO.PropertyDAO<>(SALES.ISSUE_DATE);}
		@Override public Property<Integer> getPayMethodProperty() {return new FilterDAO.PropertyDAO<>(SALES.PAY_METHOD);}
		@Override public Property<Byte> getDocumentTypeProperty() {return new FilterDAO.PropertyDAO<>(SALES.DOCUMENT_TYPE);}
		@Override public Property<Byte> getSecurityLevelProperty() {return new FilterDAO.PropertyDAO<>(SALES.SECURITY_LEVEL);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<>(SALES.STATUS);}
		@Override public Property<String> getCommentsProperty() {return new FilterDAO.PropertyDAO<>(SALES.COMMENTS);}
		@Override public Property<String> getRemarksProperty() {return new FilterDAO.PropertyDAO<>(SALES.REMARKS);}
		@Override public Property<Integer> getWorkplaceProperty() {return new FilterDAO.PropertyDAO<>(SALES.WORKPLACE);}
		@Override public Property<Integer> getScopeProperty() {return new FilterDAO.PropertyDAO<>(SALES.SCOPE);}
		@Override public Property<Short> getNumberOfPymntsProperty() {return new FilterDAO.PropertyDAO<>(SALES.NUMBER_OF_PYMNTS);}
		@Override public Property<Short> getDaysToFirstPymntProperty() {return new FilterDAO.PropertyDAO<>(SALES.DAYS_TO_FIRST_PYMNT);}
		@Override public Property<Short> getDaysBetweenPymntsProperty() {return new FilterDAO.PropertyDAO<>(SALES.DAYS_BETWEEN_PYMNTS);}
		@Override public Property<String> getPymntDaysProperty() {return new FilterDAO.PropertyDAO<>(SALES.PYMNT_DAYS);}
		@Override public Property<String> getBankAccountProperty() {return new FilterDAO.PropertyDAO<>(SALES.BANK_ACCOUNT);}
		@Override public Property<String> getBankAliasProperty() {return new FilterDAO.PropertyDAO<>(SALES.BANK_ALIAS);}
		@Override public Property<String> getBicProperty() {return new FilterDAO.PropertyDAO<>(SALES.BIC);}
		@Override public Property<Byte> getPurchaseGeneratedProperty() {return new FilterDAO.PropertyDAO<>(SALES.PURCHASE_GENERATED);}
		@Override public Property<Integer> getCarrierProperty() {return new FilterDAO.PropertyDAO<>(SALES.CARRIER);}
		@Override public Property<String> getShippingAlternativeAddressProperty() {return new FilterDAO.PropertyDAO<>(SALES.SHIPPING_ALTERNATIVE_ADDRESS);}
		@Override public Property<String> getShippingAlternativeAddress2Property() {return new FilterDAO.PropertyDAO<>(SALES.SHIPPING_ALTERNATIVE_ADDRESS2);}
		@Override public Property<String> getShippingAlternativeZipProperty() {return new FilterDAO.PropertyDAO<>(SALES.SHIPPING_ALTERNATIVE_ZIP);}
		@Override public Property<String> getShippingAlternativeCityProperty() {return new FilterDAO.PropertyDAO<>(SALES.SHIPPING_ALTERNATIVE_CITY);}
		@Override public Property<String> getShippingAlternativePhoneProperty() {return new FilterDAO.PropertyDAO<>(SALES.SHIPPING_ALTERNATIVE_PHONE);}
		@Override public Property<String> getShippingAlternativeRecipientProperty() {return new FilterDAO.PropertyDAO<>(SALES.SHIPPING_ALTERNATIVE_RECIPIENT);}
		@Override public Property<String> getShippingContactProperty() {return new FilterDAO.PropertyDAO<>(SALES.SHIPPING_CONTACT);}
		@Override public Property<Byte> getShippingPeriodProperty() {return new FilterDAO.PropertyDAO<>(SALES.SHIPPING_PERIOD);}
		@Override public Property<Byte> getConfidentialProperty() {return null;}
		@Override public Property<Integer> getSalesDetailIdProperty() {return new FilterDAO.PropertyDAO<>(SALES_DETAIL.ID);}
		@Override public Property<Integer> getCarrierPackingProperty() {return new FilterDAO.PropertyDAO<>(SALES.CARRIER_PACKING);}
	}
	
	public static int getNextNumber(AONContext ctx, String series ) {
		Integer next = ctx.getDslContext()
			.select( DSL.max(SALES.NUMBER))
			.from(SALES)
			.where(SALES.DOMAIN.eq(ctx.getDomainId()))
			.and(AonStringUtils.isBlank(series)
				? SALES.SERIES.isNull().or(DSL.trim(SALES.SERIES).eq(""))
				: SALES.SERIES.eq(series))
			.fetch()
			.stream()
			.mapToInt(rec -> (rec != null && rec.getValue(DSL.max(SALES.NUMBER)) != null) 
					? rec.getValue(DSL.max(SALES.NUMBER)) 
					: 0)
			.findFirst()
			.orElse(0);
		if(next < 0) next = 0;
		return ++next;
	}
	
	// ----- SELECT
	
	private static SelectConditionStep<Record> select(AONContext ctx, SalesFilter filter) {
		 return ctx.getDslContext().select()
			.from(SALES)
			.where(SALES_PROPERTIES.getConditions(filter));
	}

	private static SelectConditionStep<Record> selectFull(AONContext ctx, SalesFilter filter) {
		 return ctx.getDslContext().select()
			.from(SALES)
			.join(SALES_DETAIL).on(SALES_DETAIL.SALES.equal(SALES.ID))
			.join(CUSTOMER).on(CUSTOMER.REGISTRY.eq(SALES.CUSTOMER))
			.join(CUSTOMER_ALIAS).on(CUSTOMER.REGISTRY.eq(CUSTOMER_ALIAS.ID))
			.leftOuterJoin(SELLER).on(SELLER.REGISTRY.eq(SALES.SELLER))
			.leftOuterJoin(SELLER_COMERCIAL_ALIAS).on(SELLER.REGISTRY.eq(SELLER_COMERCIAL_ALIAS.ID))
			.leftOuterJoin(CARRIER).on(CARRIER.REGISTRY.eq(SALES.CARRIER))
			.leftOuterJoin(CARRIER_ALIAS).on(CARRIER.REGISTRY.eq(CARRIER_ALIAS.ID))
			.leftOuterJoin(SCOPE).on(SCOPE.ID.equal(SALES.SCOPE))
			.leftOuterJoin(PROJECT).on(PROJECT.ID.equal(SALES.PROJECT))
			.leftOuterJoin(ITEM).on(ITEM.ID.equal(SALES_DETAIL.ITEM))
			.leftOuterJoin(PRODUCT).on(PRODUCT.ID.equal(ITEM.PRODUCT))
			.leftOuterJoin(PCATEGORY).on(PRODUCT.CATEGORY.equal(PCATEGORY.ID))
			.leftOuterJoin(WORKPLACE).on(WORKPLACE.ID.equal(SALES.WORKPLACE))
			.leftOuterJoin(RADDRESS).on(RADDRESS.ID.eq(SALES.SHIPPING_ADDRESS))
			.where(SALES_PROPERTIES.getConditions(filter));
	}
	
	// ----- GET
	
	public static Sales get(AONContext ctx, Integer salesId){
		return get(ctx, f -> f.getIdProperty().eq(salesId));
	}
	
	public static Sales get(AONContext ctx, SalesFilter filter, Options... options){
		if(options.length > 0 && options[0].isFull())
			return getFull(ctx, filter);
		return select(ctx, filter).limit(1).fetch().stream().map(new SalesFiller())
			.findFirst().orElse(new Sales());
	}
	
	public static Sales getFull(AONContext ctx, SalesFilter filter){
		return getFullStream(ctx, filter).findFirst().orElse(new Sales()); 
	}
	
	// ----- GET STREAM
	
	public static Stream<Sales> getStream(AONContext ctx, SalesFilter filter, Options... options){
		if(options.length > 0) 
			return getStream(ctx, filter, options[0]);
		return select(ctx, filter).fetch().stream().map(new SalesFiller());
	}

	public static Stream<Sales> getStream(AONContext ctx, SalesFilter filter, Integer page, Integer perPage){
		return select(ctx, filter)
			.limit(perPage).offset(perPage * (page -1))
			.fetch().stream().map(new SalesFiller());
	}
	
	private static Stream<Sales> getStream(AONContext ctx, SalesFilter filter, Options options){
		if(options.isFull() && options.isPagination())
			return getFullStream(ctx, filter, options.getPage(), options.getPerPage());
		else if(options.isFull())
			return getFullStream(ctx, filter);
		else if(options.isPagination())
			return getStream(ctx, filter, options.getPage(), options.getPerPage());
		else return getStream(ctx, filter);
	}

	public static Stream<Sales> getFullStream(AONContext ctx, SalesFilter filter){
		Map<Sales, List<SalesDetail>> map = selectFull(ctx, filter)
			.groupBy(SALES.ID, SALES_DETAIL.ID)
			.fetchGroups(
				new SalesFiller()::apply,
				new SalesDetailFiller()::apply
			);
		map.forEach((object, details) -> details.forEach(object::addDetail));
		return map.keySet().stream(); 
	}
	
	public static Stream<Sales> getFullStream(AONContext ctx, SalesFilter filter, Integer page, Integer perPage){
		Map<Sales, List<SalesDetail>> map = selectFull(ctx, filter)
			.groupBy(SALES.ID, SALES_DETAIL.ID)
			.limit(perPage).offset(perPage * (page -1))
			.fetchGroups(
				new SalesFiller()::apply,
				new SalesDetailFiller()::apply
			);
		map.forEach((object, details) -> details.forEach(object::addDetail));
		return map.keySet().stream(); 
	}
	
	// ----- GET LIST
	
	public static List<Sales> getList(AONContext ctx, SalesFilter filter){
		return getStream(ctx, filter).collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static List<Sales> getList(AONContext ctx, SalesFilter filter, Integer page, Integer perPage){
		return getStream(ctx, filter, page, perPage).collect(Collectors.toCollection(LinkedList::new));
	}
	
	/**
	 * @deprecated  Replaced by get
	 */
	@Deprecated(forRemoval = true )
	public static Sales getSales(AONContext ctx, SalesFilter filter){
		return ctx.getDslContext().select().from(SALES).where(SALES_PROPERTIES.getConditions(filter))
				.limit(1).fetchInto(SALES).stream().map(new SalesFiller()).findFirst().orElse(new Sales());
	}

	/**
	 * @deprecated  Replaced by get
	 */
	@Deprecated(forRemoval = true )
	public static Sales getSales(AONContext ctx, Integer salesId){	
		return getSales(ctx, o -> o.getIdProperty().eq(salesId));
	}
	
	/**
	 * @deprecated  Replaced by get
	 */
	@Deprecated(forRemoval = true )
	public static Sales getSales(AONContext ctx, String series, int number) {
		return getSales(ctx, o -> o.getSeriesProperty().eq(series).and(o.getNumberProperty().eq(number)));
	}
	
	/**
	 * @deprecated  Replaced by SalesDetailDAO.getStream
	 */
	@Deprecated(forRemoval = true )
	public static Stream<SalesDetail> getSalesDetailStream(AONContext ctx, SalesDetailFilter filter){
		return SalesDetailDAO.getStream(ctx, filter);
	}
	
	/**
	 * @deprecated  Replaced by SalesDetailDAO.getStream
	 */
	@Deprecated(forRemoval = true )
	public static SalesDetail getSalesDetail(AONContext ctx, SalesDetailFilter filter){
		return SalesDetailDAO.get(ctx, filter);
	}
	
	/**
	 * @deprecated  Replaced by SalesDetailDAO.get
	 */
	@Deprecated(forRemoval = true )
	public static SalesDetail getSalesDetail(AONContext ctx, Integer detailId){	
		return getSalesDetail(ctx, o -> o.getIdProperty().eq(detailId));
	}

	/**
	 * @deprecated  Replaced by SalesDetailDAO.get
	 */
	@Deprecated(forRemoval = true )
	public static SalesDetail getSalesDetail(AONContext ctx, Integer salesId, Short line){	
		return getSalesDetail(ctx, o -> o.getSalesProperty().eq(salesId).and(o.getLineProperty().eq(line)));
	}
	
	private static Result<Record> getFullSales(AONContext ctx, SalesFilter filter) {
		ctx.checkRead();

		return ctx.getDslContext()
			.select()
			.from(SALES)
			.join(SALES_DETAIL).on(SALES_DETAIL.SALES.equal(SALES.ID))
			.join(CUSTOMER).on(CUSTOMER.REGISTRY.eq(SALES.CUSTOMER))
			.join(CUSTOMER_ALIAS).on(CUSTOMER.REGISTRY.eq(CUSTOMER_ALIAS.ID))
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
			.map(new SalesDetailFiller());
	}
	
	// ----- INSERT & UPDATE
	
	public static Sales save(AONContext ctx, Sales sales) {
		SalesValidation.autocomplete(ctx, sales);
		SalesValidation.validate(ctx, sales);
		
		sales = sales.hasId()
			? update(ctx, sales)
			: insert(ctx, sales);
		
		sales.setDetails(SalesDetailDAO.save(ctx, sales.getDetails()));
		return sales;
	}
	
	public static Sales insert(AONContext ctx, Sales sales) {
		ctx.checkWrite();

		Integer id = ctx.getDslContext()
				.insertInto(SALES)
				.set(SALES.DOMAIN, sales.getDomain())
				.set(SALES.PROJECT, sales.getProject().getId())
				.set(SALES.CUSTOMER, sales.getCustomer().getId())
				.set(SALES.SERIES, sales.getSeries())
				.set(SALES.NUMBER, sales.getNumber())
				.set(SALES.PURCHASE_REFERENCE, sales.getPurchaseReference())
				.set(SALES.SHIPPING_ADDRESS, sales.getShippingAddress().getId())
				.set(SALES.SELLER, sales.getSeller().getId())
				.set(SALES.DISCOUNT_EXPR, sales.getDiscountExpr())
				.set(SALES.ISSUE_DATE, AonDateUtils.toSql(sales.getDate()))
				.set(SALES.PAY_METHOD, sales.getPayMethod().getId())
				.set(SALES.DOCUMENT_TYPE,  sales.getDocumentType().value())
				.set(SALES.SECURITY_LEVEL, sales.getSecurityLevel().value())
				.set(SALES.STATUS, sales.getStatus().value())
				.set(SALES.COMMENTS, sales.getComments())
				.set(SALES.REMARKS, sales.getRemarks())
				.set(SALES.WORKPLACE, sales.getWorkplace().getId())
				.set(SALES.SCOPE, sales.getScope().getId())
				.set(SALES.NUMBER_OF_PYMNTS, sales.getNumberOfPymnts())
				.set(SALES.DAYS_TO_FIRST_PYMNT, sales.getDaysToFirstPymnt())
				.set(SALES.DAYS_BETWEEN_PYMNTS, sales.getDaysBetweenPymnts())
				.set(SALES.PYMNT_DAYS, sales.getPymntDays())
				.set(SALES.BANK_ACCOUNT, sales.getBankAccount())
				.set(SALES.BANK_ALIAS, sales.getBankAlias())
				.set(SALES.BIC, sales.getBic())
				.set(SALES.PURCHASE_GENERATED, (byte) (sales.isPurchaseGenerated() ? 1 : 0 ))
				.set(SALES.CARRIER, sales.getCarrier().getId())
				.set(SALES.SHIPPING_ALTERNATIVE_ADDRESS, sales.getShippingAlternativeAddress())
				.set(SALES.SHIPPING_ALTERNATIVE_ADDRESS2, sales.getShippingAlternativeAddress2())
				.set(SALES.SHIPPING_ALTERNATIVE_ZIP, sales.getShippingAlternativeZip())
				.set(SALES.SHIPPING_ALTERNATIVE_CITY, sales.getShippingAlternativeCity())
				.set(SALES.SHIPPING_ALTERNATIVE_PHONE, sales.getShippingAlternativePhone())
				.set(SALES.SHIPPING_ALTERNATIVE_RECIPIENT, sales.getShippingAlternativeRecipient())
				.set(SALES.SHIPPING_CONTACT, sales.getShippingContact())
				.set(SALES.SHIPPING_PERIOD, sales.getShippingPeriodValue())
				.set(SALES.CREATION_DATE, AonDateUtils.toTimestamp(new Date()))
				.set(SALES.CREATION_USER, ctx.getUser())
				.set(SALES.MODIFICATION_DATE, AonDateUtils.toTimestamp(new Date()))
				.set(SALES.MODIFICATION_USER, ctx.getUser())
				.returning(SALES.ID).fetchOne().getId();
			
		return sales.setId(id);
	}
	
	/**
	 * @deprecated  Replaced by save
	 */
	@Deprecated(forRemoval = true )
	public static int insertSales(AONContext ctx, Sales sales) {
		return insert(ctx, sales).getId();
	}

	public static Sales update(AONContext ctx, Sales sales) {
		ctx.checkWrite();
		
		ctx.getDslContext()
				.update(SALES)
				.set(SALES.DOMAIN, sales.getDomain())
				.set(SALES.PROJECT, sales.getProject().getId())
				.set(SALES.CUSTOMER, sales.getCustomer().getId())
				.set(SALES.SERIES, sales.getSeries())
				.set(SALES.NUMBER, sales.getNumber())
				.set(SALES.PURCHASE_REFERENCE, sales.getPurchaseReference())
				.set(SALES.SHIPPING_ADDRESS, sales.getShippingAddress().getId())
				.set(SALES.SELLER, sales.getSeller().getId())
				.set(SALES.DISCOUNT_EXPR, sales.getDiscountExpr())
				.set(SALES.ISSUE_DATE, AonDateUtils.toSql(sales.getDate()))
				.set(SALES.PAY_METHOD, sales.getPayMethod().getId())
				.set(SALES.DOCUMENT_TYPE, sales.getDocumentType().value())
				.set(SALES.SECURITY_LEVEL, sales.getSecurityLevel().value())
				.set(SALES.STATUS, sales.getStatus().value())
				.set(SALES.COMMENTS, sales.getComments())
				.set(SALES.REMARKS, sales.getRemarks())
				.set(SALES.WORKPLACE, sales.getWorkplace().getId())
				.set(SALES.SCOPE, sales.getScope().getId())
				.set(SALES.NUMBER_OF_PYMNTS, sales.getNumberOfPymnts())
				.set(SALES.DAYS_TO_FIRST_PYMNT, sales.getDaysToFirstPymnt())
				.set(SALES.DAYS_BETWEEN_PYMNTS, sales.getDaysBetweenPymnts())
				.set(SALES.PYMNT_DAYS, sales.getPymntDays())
				.set(SALES.BANK_ACCOUNT, sales.getBankAccount())
				.set(SALES.BANK_ALIAS, sales.getBankAlias())
				.set(SALES.BIC, sales.getBic())
				.set(SALES.PURCHASE_GENERATED, (byte) (sales.isPurchaseGenerated() ? 1 : 0 ))
				.set(SALES.CARRIER, sales.getCarrier().getId())
				.set(SALES.SHIPPING_ALTERNATIVE_ADDRESS, sales.getShippingAlternativeAddress())
				.set(SALES.SHIPPING_ALTERNATIVE_ADDRESS2, sales.getShippingAlternativeAddress2())
				.set(SALES.SHIPPING_ALTERNATIVE_ZIP, sales.getShippingAlternativeZip())
				.set(SALES.SHIPPING_ALTERNATIVE_CITY, sales.getShippingAlternativeCity())
				.set(SALES.SHIPPING_ALTERNATIVE_PHONE, sales.getShippingAlternativePhone())
				.set(SALES.SHIPPING_ALTERNATIVE_RECIPIENT, sales.getShippingAlternativeRecipient())
				.set(SALES.SHIPPING_CONTACT, sales.getShippingContact())
				.set(SALES.SHIPPING_PERIOD, sales.getShippingPeriodValue())
				.set(SALES.MODIFICATION_USER, ctx.getUser())
				.set(SALES.MODIFICATION_DATE, AonDateUtils.toTimestamp(new Date()))
				.where(SALES.ID.eq(sales.getId()))
				.execute();
		return sales;
	}
	
	/**
	 * @deprecated  Replaced by save
	 */
	@Deprecated(forRemoval = true )
	public static void updateSales(AONContext ctx, Sales sales) {
		update(ctx, sales);
	}
	
	/**
	 * @deprecated  Replaced by SalesDetailDAO.save
	 */
	@Deprecated(forRemoval = true )
	public static void insertSalesDetail(AONContext ctx, SalesDetail detail) {
		SalesDetailDAO.save(ctx, detail);
	}
	
	/**
	 * @deprecated  Replaced by SalesDetailDAO.save
	 */
	@Deprecated(forRemoval = true )
	public static void updateSalesDetail(AONContext ctx, SalesDetail detail) {
		SalesDetailDAO.save(ctx, detail);
	}
	
	// ----- DELETE
	
	public static void delete(AONContext ctx, Integer salesId) {
		SalesDetailDAO.delete(ctx, f -> f.getSalesProperty().eq(salesId));
		delete(ctx, f -> f.getIdProperty().eq(salesId));
	}
	
	public static void delete(AONContext ctx, SalesFilter filter) {
		ctx.checkWrite();
		ctx.getDslContext()
			.delete(SALES)
			.where(SALES_PROPERTIES.getConditions(filter))
			.execute();
	}
	
	// ----- OTHER

	@Deprecated
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
	
	@Deprecated
	public static void createSeller(AONContext ctx, int domain, int registry,
			int scope) {
		ctx.checkWrite();
		ctx.getDslContext()
				.insertInto(SELLER, SELLER.DOMAIN, SELLER.REGISTRY,
						SELLER.SCOPE, SELLER.STATUS)
				.values(domain, registry, scope, (byte) 0).execute();
	}

	@Deprecated
	public static void createCarrier(AONContext ctx, int domain, int registry,
			int scope) {
		ctx.checkWrite();
		ctx.getDslContext()
				.insertInto(CARRIER, CARRIER.DOMAIN, CARRIER.REGISTRY,
						CARRIER.SCOPE).values(domain, registry, scope)
				.execute();
	}
	
	@Deprecated
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

	@Deprecated
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
	
	@Deprecated
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
	
	// ----- FILLER
	
	public static class SalesFiller extends Filler implements Function<Record, Sales> {
		
		@Override
		public Sales apply(Record r) {
			return build(r);
		}
		
		public static Sales build(Record r) {
			return new Sales()
				.setId(getValue(r, SALES.ID))
				.setDomain(getValue(r, SALES.DOMAIN))
				.setProject(checkField(r, PROJECT.ID)
						? ProjectFiller.build(r)
						: new Project().setId(getValue(r, SALES.PROJECT)))
				.setCustomer(checkField(r, CUSTOMER.REGISTRY) || checkField(r, CUSTOMER_ALIAS.ID)
						? CustomerFiller.buildCustomer(r, CUSTOMER_ALIAS)
						: new Customer().setId(getValue(r, SALES.CUSTOMER)))
				.setSeries(getValue(r, SALES.SERIES))
				.setNumber(getValue(r, SALES.NUMBER))
				.setPurchaseReference(getValue(r, SALES.PURCHASE_REFERENCE))
				.setShippingAddress(checkField(r, RADDRESS.ID)
					? RegistryAddressFiller.build(r)
					: new RegistryAddress().setId(getValue(r, SALES.SHIPPING_ADDRESS)))
				.setSeller(checkField(r, SELLER.REGISTRY) || checkField(r, SELLER_COMERCIAL_ALIAS.ID)
					? SellerFiller.build(r, true) 	
					: new Seller().setId(getValue(r, SALES.SELLER)))
				.setDiscountExpr(r.getValue(SALES.DISCOUNT_EXPR))
				.setDate(r.getValue(SALES.ISSUE_DATE))
				.setDeliveryDate(r.getValue(SALES.DELIVERY_DATE))
				.setPayMethod(checkField(r, PAY_METHOD.ID)
					? PayMethodFiller.build(r)
					: new PayMethod().setId(r.getValue(SALES.PAY_METHOD)))
				.setDocumentType(SalesType.safeValueOf(r.getValue(SALES.DOCUMENT_TYPE)))
				.setSecurityLevel(SecurityLevel.safeValueOf(getValue(r, SALES.SECURITY_LEVEL)))
				.setStatus(SalesStatus.values()[r.getValue(SALES.STATUS)])
				.setComments(r.getValue(SALES.COMMENTS))
				.setRemarks(r.getValue(SALES.REMARKS))
				.setWorkplace(checkField(r, WORKPLACE.ID)
					? WorkplaceFiller.build(r)
					: new Workplace().setId(r.getValue(SALES.WORKPLACE)))
				.setScope(checkField(r, SCOPE.ID)
					? ScopeFiller.buildScope(r)
					: new Scope().setId(getValue(r, SALES.SCOPE)))
				.setNumberOfPymnts(getShort(r, SALES.NUMBER_OF_PYMNTS))
				.setDaysToFirstPymnt(getShort(r, SALES.DAYS_TO_FIRST_PYMNT))
				.setDaysBetweenPymnts(getShort(r, SALES.DAYS_BETWEEN_PYMNTS))
				.setPymntDays(getValue(r, SALES.PYMNT_DAYS))
				.setBankAccount(getValue(r, SALES.BANK_ACCOUNT))
				.setBankAlias(getValue(r, SALES.BANK_ALIAS))
				.setBic(getValue(r, SALES.BIC))
				.setPurchaseGenerated(getBoolean(r, SALES.PURCHASE_GENERATED))
				.setCarrier(checkField(r, CARRIER.REGISTRY) || checkField(r, CARRIER_ALIAS.ID)
						? CarrierFiller.build(r)
						: new Carrier().setId(getValue(r, SALES.CARRIER)))
				.setCarrierPacking(getValue(r, SALES.CARRIER_PACKING))
				.setShippingAlternativeAddress(getValue(r, SALES.SHIPPING_ALTERNATIVE_ADDRESS))
				.setShippingAlternativeAddress2(getValue(r, SALES.SHIPPING_ALTERNATIVE_ADDRESS2))
				.setShippingAlternativeZip(getValue(r, SALES.SHIPPING_ALTERNATIVE_ZIP))
				.setShippingAlternativeCity(getValue(r, SALES.SHIPPING_ALTERNATIVE_CITY))
				.setShippingAlternativePhone(getValue(r, SALES.SHIPPING_ALTERNATIVE_PHONE))
				.setShippingAlternativeRecipient(getValue(r, SALES.SHIPPING_ALTERNATIVE_RECIPIENT))
				.setShippingContact(getValue(r, SALES.SHIPPING_CONTACT))
				.setShippingPeriod(ShipmentPeriod.safeValueOf(getValue(r, SALES.SHIPPING_PERIOD)))
				.setCreationDate(getValue(r, SALES.CREATION_DATE))
				.setCreationUser(getValue(r, SALES.CREATION_USER))
				.setModificationDate(getValue(r, SALES.MODIFICATION_DATE))
				.setModificationUser(getValue(r, SALES.MODIFICATION_USER));
		}
	}
}
