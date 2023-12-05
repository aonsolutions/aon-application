package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Carrier.CARRIER;
import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.Delivery.DELIVERY;
import static com.esferalia.aon.jooq.tables.Elaboration.ELABORATION;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Pcategory.PCATEGORY;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.Project.PROJECT;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Sales.SALES;
import static com.esferalia.aon.jooq.tables.SalesDetail.SALES_DETAIL;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;
import static com.esferalia.aon.jooq.tables.Seller.SELLER;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;
import static com.esferalia.aon.occam.impl.jooq.dao.CarrierDAO.CARRIER_ALIAS;
import static com.esferalia.aon.occam.impl.jooq.dao.CustomerDAO.CUSTOMER_ALIAS;
import static com.esferalia.aon.occam.impl.jooq.dao.SellerDAO.SELLER_ALIAS;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.lang.math.NumberUtils;
import org.jooq.Record;
import org.jooq.SelectConditionStep;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Elaboration;
import com.esferalia.aon.occam.api.model.ElaborationDetail;
import com.esferalia.aon.occam.api.model.ElaborationDetailType;
import com.esferalia.aon.occam.api.model.Filter.SalesFilter;
import com.esferalia.aon.occam.api.model.Options;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.DataAttachSource;
import com.esferalia.aon.occam.api.model.management.Sales;
import com.esferalia.aon.occam.api.model.management.SalesDetail;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.product.ProductCategory;
import com.esferalia.aon.occam.api.model.product.ProductKind;
import com.esferalia.aon.occam.api.model.product.ProductStatus;
import com.esferalia.aon.occam.api.model.product.Tax;
import com.esferalia.aon.occam.api.model.registry.Carrier;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.ElaborationStatus;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.ProductType;
import com.esferalia.aon.occam.api.model.type.SalesDetailStatus;
import com.esferalia.aon.occam.api.model.type.SalesStatus;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPacking;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryDetail;
import com.esferalia.aon.occam.api.model.warehouse.SerfruitDeliveryPackaging;
import com.esferalia.aon.occam.api.model.warehouse.Warehouse;
import com.esferalia.aon.occam.impl.jooq.dao.SalesDAO.SalesFiller;
import com.esferalia.aon.occam.impl.jooq.dao.SalesDetailDAO.SalesDetailFiller;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class SerfruitDAO {
	
	private SerfruitDAO() {

	}

	public static Stream<Sales> getSalesStream(AONContext ctx, SalesFilter filter, Options... options){
		Date date = new Date();
		Date from = AonDateUtils.addDays(date, -5);
		return getFullStream(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId())
				.and(f.getStatusProperty().eq(SalesStatus.BLOCKED.value()))
				.and(f.getIssueDateProperty().ge(AonDateUtils.toSql(from)))
				.and(f.getIssueDateProperty().le(AonDateUtils.toSql(date)))
		);
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
	
	private static SelectConditionStep<Record> selectFull(AONContext ctx, SalesFilter filter) {
		 return ctx.getDslContext().select()
			.from(SALES)
			.join(SALES_DETAIL).on(SALES_DETAIL.SALES.equal(SALES.ID))
			.join(CUSTOMER).on(CUSTOMER.REGISTRY.eq(SALES.CUSTOMER))
			.join(CUSTOMER_ALIAS).on(CUSTOMER.REGISTRY.eq(CUSTOMER_ALIAS.ID))
			.join(ELABORATION).on(ELABORATION.SOURCE_ID.eq(SALES_DETAIL.ID))
			.leftOuterJoin(SELLER).on(SELLER.REGISTRY.eq(SALES.SELLER))
			.leftOuterJoin(SELLER_ALIAS).on(SELLER.REGISTRY.eq(SELLER_ALIAS.ID))
			.leftOuterJoin(CARRIER).on(CARRIER.REGISTRY.eq(SALES.CARRIER))
			.leftOuterJoin(CARRIER_ALIAS).on(CARRIER.REGISTRY.eq(CARRIER_ALIAS.ID))
			.leftOuterJoin(SCOPE).on(SCOPE.ID.equal(SALES.SCOPE))
			.leftOuterJoin(PROJECT).on(PROJECT.ID.equal(SALES.PROJECT))
			.leftOuterJoin(ITEM).on(ITEM.ID.equal(SALES_DETAIL.ITEM))
			.leftOuterJoin(PRODUCT).on(PRODUCT.ID.equal(ITEM.PRODUCT))
			.leftOuterJoin(PCATEGORY).on(PRODUCT.CATEGORY.equal(PCATEGORY.ID))
			.leftOuterJoin(WORKPLACE).on(WORKPLACE.ID.equal(SALES.WORKPLACE))
			.leftOuterJoin(RADDRESS).on(RADDRESS.ID.eq(SALES.SHIPPING_ADDRESS))
			.where(SalesDAO.SALES_PROPERTIES.getConditions(filter))
			.and(ELABORATION.STATUS.eq(ElaborationStatus.PENDING.value()));
	}
	

	public static Delivery saveDelivery(AONContext ctx, Delivery delivery) {
		delivery.setShippingAlternativeAddress(null);
		delivery.setShippingAlternativeAddress2(null);
		delivery.setShippingAlternativeCity(null);
		delivery.setShippingAlternativePhone(null);
		delivery.setShippingAlternativeRecipient(null);
		delivery.setShippingAlternativeZip(null);
		delivery.setShippingContact(null);
		delivery.setDate(AonDateUtils.getDateWithoutTime(delivery.getDate()));

		for (DeliveryDetail detail : delivery.getDetails()) {
			Item item = detail.getItem();
			item.setId(null);
			String description = item.getProduct().getName() + " #" + item.getSerialNumber();
			if(isEroski(delivery.getCustomer().getDocument())
                   && AonStringUtils.containsIgnoreCase(description, "natur")) {
                description += " CUMPLE TOTALMENTE GRASP";
            }
			item.setDescription(description);
			item.setBarcode(null);
			if(item.getSerialDate() == null) item.setSerialDate(new Date());
			if(item.getProduct().isPerishable()) {
				Date expireDate = AonDateUtils.addDays(item.getSerialDate(), 
					item.getProduct().getDaysToExpire() != null ? item.getProduct().getDaysToExpire() : 0);
				item.setExpireDate(expireDate);
			}
			item = ItemDAO.save(ctx, item);
			detail.setItem(item);
			detail.setDescription(item.getDescription());
			if(detail.getSalesDetail() != null) {
				SalesDetail aux = SalesDetailDAO.get(ctx, f -> f.getIdProperty().eq(detail.getSalesDetail()));
				if(aux == null || aux.getId() == null) detail.setSalesDetail(null);
			}
		}
		delivery = DeliveryDAO.save(ctx, delivery);
		
		try {
			delivery.getDetails().stream().map(detail -> {
				SalesDetail sd = SalesDetailDAO.get(ctx, f -> f.getIdProperty().eq(detail.getSalesDetail()));
				sd.setDelivered(sd.getDelivered() + detail.getQuantity());
				sd.setStatus(sd.getQuantity() != sd.getDelivered() 
					? SalesDetailStatus.PARTIAL_SETTLED : SalesDetailStatus.SETTLED);
				sd = SalesDetailDAO.save(ctx, sd);
				
				Elaboration elaboration = ElaborationDAO.get(ctx, f -> f.getSourceIdProperty().eq(detail.getSalesDetail()), new Options().setFull(true));
				
				ElaborationDAO.insertElaborationDetail(ctx, new ElaborationDetail()
					.setDomain(elaboration.getDomain())
					.setComposition(new LinkedList<>())
					.setDate(new Date())
					.setType(ElaborationDetailType.ELABORATION)
					.setElaboration(elaboration)
					.setItem(detail.getItem())
					.setQuantity(detail.getQuantity())
					.setWarehouse(new Warehouse().setId(detail.getWarehouse())));
				if(elaboration.getQuantity().equals(detail.getQuantity())) {
					elaboration.setStatus(ElaborationStatus.CLOSED);
					ElaborationDAO.save(ctx, elaboration);
				}
				
				return sd.getSales().getId();
			}).distinct().forEach(id -> {
				Sales ss = SalesDAO.getFull(ctx, f -> f.getIdProperty().eq(id));
				boolean notDelivered = ss.getDetails().stream().filter(f -> f.getQuantity() != f.getDelivered()).count() > 0;
				if(!notDelivered) {
					ss.setStatus(SalesStatus.SERVED);
					SalesDAO.save(ctx, ss);
				}
			});
		} catch (Exception e) {
			System.out.println("ERROR ON UPDATE SALES AND ELABORATION");
			e.printStackTrace();
		}
		return delivery;
	}
	
	public static void saveDeliveryPackaging(AONContext ctx, Delivery delivery, List<SerfruitDeliveryPackaging> packaging) {
		Warehouse w = getWarehouse(ctx, delivery);
		StringBuilder builder = new StringBuilder();
		processPackaging(ctx, delivery, packaging, w, null, builder);
		insertAttachPackaging(ctx, delivery, builder);
	}

	private static void processPackaging(AONContext ctx, Delivery delivery, List<SerfruitDeliveryPackaging> packaging, Warehouse w, Integer parentLine, StringBuilder builder) {
	
		packaging.stream().forEach(dp -> {
			if(dp.getDeliveryLine() == null) {
				Integer maxLine = delivery.getDetails().stream().mapToInt(r -> r.getLine()).max().getAsInt() + 1;
				insertDetail(ctx, delivery, dp, maxLine, w);

				processPackaging(ctx, delivery, dp.getContent(), w, maxLine, builder);
			
				if(dp.getSscc() != null) {
					builder.append("[ENV=" + maxLine + ";LIN=" + getLine(dp) +";SSCC=" + dp.getSscc()+ "]");
				} else {
					builder.append("[ENV=" + maxLine + ";CONT=" + parentLine + "]");
				}
			}
 		});
		
	}
	
	private static void insertAttachPackaging(AONContext ctx, Delivery delivery, StringBuilder builder) {
		Attach attach = new Attach();
		attach.setAttachType(AttachType.DATA);
		attach.setDomain(new Domain().setId(delivery.getDomain()));
		attach.setDate(new Date());
		attach.setData(builder.toString().getBytes());
		attach.setMimeType(MimeType.TXT);
		attach.setSourceType(DataAttachSource.DELIVERY.value());
		attach.setSourceBatch(delivery.getId());
		attach.setType((byte)0);
		
		AttachmentDAO.insertDataAttach(ctx, attach);
	}
	
	private static DeliveryDetail insertDetail(AONContext ctx, Delivery delivery, SerfruitDeliveryPackaging dp, Integer line, Warehouse w) {
		Item p = getPackage(ctx, dp);
		if(p != null) {
			DeliveryDetail detail = new DeliveryDetail();
			detail.setDomain(ctx.getDomainId());
			detail.setDelivery(delivery);
			detail.setLine(line.shortValue());
			detail.setItem(p);
			String description = p.getProduct().getName();
			detail.setDescription(description);
			detail.setWarehouse(w.getId());
			detail.setDiscountExpression("0");
			detail.setQuantity(dp.getQuantity());
			detail.setCreationUser(ctx.getUser());
			detail.setCreationDate(new Date());
			delivery.getDetails().add(detail);
			return DeliveryDetailDAO.save(ctx, detail);
		}
		return null;

	}
	
	private static Integer getLine(SerfruitDeliveryPackaging dp ) {
		if(dp.getDeliveryLine() != null) return dp.getDeliveryLine();
		
		Integer line = null;
		Integer indez = 0;
		while(line == null){
			line = getLine(dp.getContent().get(indez));
			indez++;
		}
		
		return line;
	}
	
	private static Warehouse getWarehouse(AONContext ctx, Delivery delivery) {
		Warehouse warehouse = WarehouseDAO.getWarehouse(
				ctx,
				f -> f.getDomainProperty()
				.eq(ctx.getDomainId())
				.and(f.getActiveProperty().eq((byte) 1))
				.and(f.getWorkplaceProperty().eq(
						delivery.getWorkplace().getId())));
		
		if(warehouse.isEmpty()) {
		    warehouse = WarehouseDAO.getWarehouse(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId()).and(f.getActiveProperty().eq((byte) 1)));
		}
		return warehouse;
	}
	
    public static Item getPackage(AONContext ctx, SerfruitDeliveryPackaging dp) {
        Product product = ProductDAO.get(ctx, f -> f.getDomainProperty()
                                .eq(ctx.getDomainId())
                                .and(f.getCodeProperty().eq(dp.getProduct().getCode())));
    
        if (product == null || product.getId() == null) {
			ProductCategory pc = ProductCategoryDAO.get(ctx, f -> f.getIdProperty().eq(3297));

            product = new Product();
            product.setDomain(new Domain().setId(ctx.getDomainId()));
            product.setStatus(ProductStatus.ACTIVE);
            product.setLotable(Boolean.FALSE);
            product.setSerializable(Boolean.FALSE);
            product.setPackaged(Boolean.FALSE);
            product.setInventoriable(Boolean.TRUE);
            product.setCategory(pc);
            product.setCode(dp.getProduct().getCode());
            product.setName(AonStringUtils.isBlank(dp.getProduct().getName())
            		? "ENVASE AUTOGENERADO ("+ dp.getProduct().getCode() +")"
            		: dp.getProduct().getName());
            product.setType(ProductType.AUXILIARY);
            product.setKind(ProductKind.SALE_PURCHASE);
            product.setVat(new Tax().setId(obtainDefaultVat(ctx)));
            product.setCreationUser(ctx.getUser());
            product.setCreationDate(new Date());
            product = ProductDAO.save(ctx, product);
        }
        
        return getItem(ctx, product, dp);
    }
    
    public static Item getItem(AONContext ctx, Product product, SerfruitDeliveryPackaging dp)  {
        Item baseItem = ItemDAO.get(ctx,
                        f -> f.getDomainProperty()
                                .eq(ctx.getDomainId())
                                .and(f.getProductProperty().eq(
                                        product.getId()))
                                .and(f.getSerialDateProperty().isNull())
                                .and(f.getSerialNumberProperty()
                                        .isNull()));
        if(!AonStringUtils.isBlank(dp.getSscc())) {
        	Item item = new Item()
                .setDomain(product.getDomain())
                .setProduct(product)
                .setBarcode(null)
                .setDescription(dp.getProduct().getName())
                .setStatus(ProductStatus.ACTIVE)
                .setPackFormatTag(baseItem.getPackFormatTag())
                .setPackMeasurement(baseItem.getPackMeasurement())
                .setPackMeasurementTag(baseItem.getPackMeasurementTag())
                .setPackUnits(baseItem.getPackUnits())
                .setPackUnitsTag(baseItem.getPackUnitsTag())
                .setStockUnitTag(baseItem.getStockUnitTag())
                .setSerialNumber(dp.getSscc());
        	return ItemDAO.save(ctx, item);
    	} else return baseItem;
    }
    
    public static Integer obtainDefaultVat(AONContext ctx) {
        ApplicationParameter ap = AON.getApplicationParameter(ctx.getDomainName(),
                ctx.getDomainId(),
                ctx.getUser(),
                AppParam.ACC_DEFAULT_VAT_PERCENT.name());
        if(ap!=null && ap.getValue()!=null && NumberUtils.isNumber(ap.getValue())) {
            return Integer.valueOf(ap.getValue());
        } else {
            Tax tax = AON.getTaxStream(ctx.getDomainName(),
                    ctx.getDomainId(),
                    ctx.getUser(),
                    f -> f.getDomainProperty().eq(ctx.getDomainId())
                    .and(f.getTaxTypeProperty().eq(TaxType.VAT.value()))
                    ).sorted((o1, o2) -> o1.getId().compareTo(o2.getId()))
                    .findFirst().orElse(new Tax());
            return tax.getId();
        }
    }
	
	public static void saveCarrierPacking(AONContext ctx, Delivery delivery, CarrierPacking carrierPacking) {
		if(carrierPacking.getCarrier() == null) {
			carrierPacking.setCarrier(RegistryOldDAO.getCarrierStream(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId())
					.and(f.getDocumentProperty().eq(carrierPacking.getCarrierDocument())))
			.findFirst().orElse(new Carrier()).getId());
		}

		Integer id = WarehouseDAO.insertCarrierPacking(ctx, carrierPacking);
		
		ctx.getDslContext()
		.update(DELIVERY)
		.set(DELIVERY.CARRIER_PACKING, id)
		.where(DELIVERY.ID.eq(delivery.getId()))
		.execute();
		
		List<Integer> ids = delivery.getDetails().stream().filter(f -> f.getSalesDetail() != null)
				.map(r -> r.getSalesDetail()).toList();
		ctx.getDslContext()
		.update(SALES_DETAIL)
		.set(SALES_DETAIL.CARRIER_PACKING, id)
		.where(SALES_DETAIL.ID.in(ids))
		.execute();
	}
	
    public static boolean isEroski(String document) {
        return "F20033361".equalsIgnoreCase(document)
                || "B88512975".equalsIgnoreCase(document)
                || "A08115032".equalsIgnoreCase(document);
   }
}




