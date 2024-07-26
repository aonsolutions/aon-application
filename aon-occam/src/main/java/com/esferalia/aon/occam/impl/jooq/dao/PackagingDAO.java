package com.esferalia.aon.occam.impl.jooq.dao;


import static com.esferalia.aon.jooq.tables.Delivery.DELIVERY;
import static com.esferalia.aon.jooq.tables.SalesDetail.SALES_DETAIL;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.json.PackagingDeliveryJSON;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.DataResponseDetail;
import com.esferalia.aon.occam.api.model.Elaboration;
import com.esferalia.aon.occam.api.model.ElaborationDetail;
import com.esferalia.aon.occam.api.model.ElaborationDetailComposition;
import com.esferalia.aon.occam.api.model.ElaborationDetailType;
import com.esferalia.aon.occam.api.model.Options;
import com.esferalia.aon.occam.api.model.management.Sales;
import com.esferalia.aon.occam.api.model.management.SalesDetail;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.ItemComposition;
import com.esferalia.aon.occam.api.model.product.ProductStatus;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.api.model.type.DeliveryStatus;
import com.esferalia.aon.occam.api.model.type.ElaborationStatus;
import com.esferalia.aon.occam.api.model.type.SalesDetailStatus;
import com.esferalia.aon.occam.api.model.type.SalesStatus;
import com.esferalia.aon.occam.api.model.warehouse.Barcode;
import com.esferalia.aon.occam.api.model.warehouse.BarcodeType;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryDetail;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryPackaging;
import com.esferalia.aon.occam.api.model.warehouse.GS1128Codes;
import com.esferalia.aon.occam.api.model.warehouse.Packaging;
import com.esferalia.aon.occam.api.model.warehouse.PackagingDelivery;
import com.esferalia.aon.occam.api.model.warehouse.PackagingDeliveryContent;
import com.esferalia.aon.occam.api.model.warehouse.Stock;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class PackagingDAO {
	
	private PackagingDAO() {
	
	}
	
	public static DeliveryPackaging getDeliveryPackaging(AONContext ctx, String sscc, Integer delivery, Integer product){
		Item container = ItemDAO.getFull(ctx, f -> 
			f.getDomainProperty().eq(ctx.getDomainId())
			.and(f.getSerialNumberProperty().eq(sscc))
			.and(f.getStatusProperty().eq(ProductStatus.ACTIVE.value())));
		if(!container.isEmpty()) {
			DeliveryPackaging dp = DeliveryPackagingDAO.get(ctx, f -> f.getItemProperty().eq(container.getId()));
			if((!dp.isEmpty() && delivery == null)
				|| (!dp.isEmpty() && delivery != null && !dp.getDelivery().getId().equals(delivery))) {
				throw new AonCoreException("El Envase pertenece a otro albarán "); 
			} else if(!dp.isEmpty() && delivery != null && dp.getDelivery().getId().equals(delivery)) {
				return dp;
			} else checkComposition(ctx, container, product, delivery);
		} else throw new AonCoreException("No existe ningún envase con el SSCC indicado"); 

		return new DeliveryPackaging()
				.setItem(container);
	}

	private static void checkComposition(AONContext ctx, Item item, Integer product, Integer delivery) {
		if(product == null && delivery != null) {
			Map<Integer, Double> productMap = new HashMap<>();
			SalesDetailDAO.getStream(ctx, f -> f.getDeliveryProperty().eq(delivery))
			.forEach(sd -> {
				Integer key = sd.getItem().getProduct().getId();
				if(productMap.containsKey(key)) {
					productMap.put(key, productMap.get(key) + sd.getQuantity());
				} else productMap.put(key, sd.getQuantity());
			});
			
			boolean contain = false;
			for(ItemComposition r : item.getItemComposition()) {
				Item i = ItemDAO.get(ctx, r.getCompositionItemId());
				r.setComposition(i);
				Integer productId = i.getProduct().getId();
				if(productMap.containsKey(productId)) {
					contain = true;
					if(r.getQuantity() > productMap.get(productId)) {
						throw new AonCoreException("El envase incluye más cantidad de la correspondiente al albarán");
					}
				}
			}
			if(!contain && !item.getItemComposition().isEmpty()) {
				throw new AonCoreException("El envase no incluye ningún producto correspondiente al albarán.");
			}
		} else {
			List<Integer> ps = new LinkedList<>();
			item.getItemComposition().stream().forEach(r -> {
				Item i = ItemDAO.get(ctx, r.getCompositionItemId());
				r.setComposition(i);
				Integer productId = i.getProduct().getId();
				ps.add(productId);
			});
			List<Integer> ps2 = ps.stream().distinct().toList();
			if(ps2.size() > 1) {
				throw new AonCoreException("El envase incluye mas de un producto."); 
			}
			if(!ps2.isEmpty() && !ps2.get(0).equals(product)) {
	 			throw new AonCoreException("El envase no incluye el producto.");
			}
		}
		
	}
	
	public static Packaging get(AONContext ctx, String barcode){
		String serialNumber = calculateSerialNumber(barcode);
		Date serialDate = calculateSerialDate(barcode);
		String pBarcode = calculateBarcode(barcode);
		Item item = ItemDAO.get(ctx,  f ->
			f.getDomainProperty().eq(ctx.getDomainId())
			.and(f.getBarcodeProperty().eq(pBarcode))
			.and(f.getSerialNumberProperty().isNull()), new Options().setFull(true));
		if(item.isEmpty()) throw new AonCoreException("El producto no existe.");

		Item item2 = ItemDAO.get(ctx, f -> 
			f.getDomainProperty().eq(ctx.getDomainId())
			.and(f.getProductProperty().eq(item.getProduct().getId()))
			.and(f.getSerialNumberProperty().eq(serialNumber)));
		if(item2.isEmpty()) {	
			item2 = item.copy()
				.setBarcode(null)
				.setSerialNumber(serialNumber)
				.setSerialDate(serialDate)
				.setDescription(item.getProduct().getName() + " #" + serialNumber);
			if(item.getProduct().isPerishable()) {
				Date expireDate = AonDateUtils.addDays(serialDate, 
					item.getProduct().getDaysToExpire() != null ? item.getProduct().getDaysToExpire() : 0);
				item2.setExpireDate(expireDate);
			}
		}
		Integer[] items = ItemCompositionDAO.getStream(ctx, f -> 
			f.getCompositionItemProperty().eq(item.getId()))
			.map(ItemComposition::getItemId).toArray(Integer[]::new);
	
		List<Item> containers = ItemDAO.getList(ctx, f -> f.getIdProperty().in(items));
		for (int i = 0; i < containers.size(); i++) {
			Integer id = containers.get(i).getId();
			List<ItemComposition> icList = ItemCompositionDAO.getList(ctx, f -> 
				f.getItemProperty().eq(id)
				.and(f.getCompositionItemProperty().eq(item.getId())));
			containers.get(i).setItemComposition(icList);
		}
		containers = containers.stream().filter(f -> f.getItemComposition().size() == 1).collect(Collectors.toCollection(LinkedList::new));
		return new Packaging()
			.setBase(item)
			.setItem(item2)
			.setContainers(containers);
	}
	
	public static List<Packaging> save(AONContext ctx, Packaging packaging) {
		Item item = ItemDAO.get(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId())
				.and(f.getProductProperty().eq(packaging.getBase().getProduct().getId()))
				.and(f.getSerialNumberProperty().eq(packaging.getItem().getSerialNumber())));
		
		if(item.isEmpty()) {
			packaging.getItem().setDescription(packaging.getBase().getProduct().getName() 
					+ " #" + packaging.getItem().getSerialNumber());
			item = ItemDAO.save(ctx, packaging.getItem());
			packaging.setItem(item);			
		} else packaging.setItem(item);

		Elaboration elaboration = processElaboration(ctx, packaging);
		return processPackaging(ctx, packaging, elaboration);
	}
	
	public static void acceptDeliveryPackaging(AONContext ctx, Integer deliveryId) {
		// CAMBIAR ESTADO DEL ALBARÁN DE VENTA
		ctx.getDslContext().update(DELIVERY).set(DELIVERY.STATUS, DeliveryStatus.PENDING.value()).where(DELIVERY.ID.eq(deliveryId)).execute();
		
		// BUSCAR TODOS LOS DETALLES DEL PEDIDO VINCULADOS CON EL ALBARAN
		SalesDetailDAO.getStream(ctx, f -> 
			f.getDomainProperty().eq(ctx.getDomainId())
			.and(f.getDeliveryProperty().eq(deliveryId)))
		.map(salesDetail -> {
			boolean delivered = salesDetail.getQuantity() - salesDetail.getDelivered() <= 0;
			
			// ELIMINAR EL VINCULO DEL DETALLE DEL PEDIDO CON EL ALBARÁN.
			ctx.getDslContext().update(SALES_DETAIL)
			.set(SALES_DETAIL.DELIVERY, (Integer) null)
			.set(SALES_DETAIL.STATUS, delivered ? SalesDetailStatus.SETTLED.value() : salesDetail.getStatus().value())
			.where(SALES_DETAIL.ID.eq(salesDetail.getId()))
			.execute();
			
			return salesDetail.getSales().getId();
		}).distinct().forEach(id -> {
			Sales ss = SalesDAO.getFull(ctx, f -> f.getIdProperty().eq(id));
			boolean notDelivered = ss.getDetails().stream().filter(f -> !f.getStatus().equals(SalesDetailStatus.SETTLED)).count() > 0;
			if(!notDelivered) {
				ss.setStatus(SalesStatus.SERVED);
				SalesDAO.save(ctx, ss);
			}
		});
		
		// TODO Marcar como descatalogados todos los item de envases (sscc).
	}
	
	public static PackagingDelivery saveDeliveryPackaging(AONContext ctx, PackagingDelivery packaging) {
		Delivery delivery = DeliveryDAO.get(ctx, packaging.getDelivery());
		Integer deliveryId = delivery.getId();
		// Container
		Item container = null;
		
		Item containerBase = null; 
		
		if(packaging.getContainer().getItem() != null) {
			container = ItemDAO.getFull(ctx, f -> f.getIdProperty().eq(packaging.getContainer().getItem()));
			Integer containerProductId = container.getProduct().getId();
			containerBase = ItemDAO.get(ctx,  f -> f.getDomainProperty().eq(ctx.getDomainId())
					.and(f.getProductProperty().eq(containerProductId))
					.and(f.getSerialNumberProperty().isNull()));
			// AÑADIR A DELIVERY PACKAGING SI NO EXISTE YA.
			Integer cId = container.getId();
			DeliveryPackaging dp = DeliveryPackagingDAO.get(ctx, f -> f.getItemProperty().eq(cId)
					.and(f.getDeliveryProperty().eq(deliveryId)));
			if(dp.getId() == null) {
				DeliveryPackagingDAO.save(ctx, new DeliveryPackaging()
						.setDomain(ctx.getDomainId())
						.setDelivery(delivery)
						.setItem(container));
			}
		} else if(packaging.getContainer().getProduct() != null){
			containerBase = ItemDAO.get(ctx,  f -> f.getDomainProperty().eq(ctx.getDomainId())
					.and(f.getProductProperty().eq(packaging.getContainer().getProduct()))
					.and(f.getSerialNumberProperty().isNull()));
			container = ItemDAO.save(ctx, containerBase.copy()
					.setSerialNumber(generateSSCC(ctx))
					.setSerialDate(new Date()));
			
			packaging.getContainer().setItem(container.getId());
			
			// CREAR ITEM DEL ENVASADO.
			// AÑADIR DELIVERY PACKAGING
			DeliveryPackagingDAO.save(ctx, new DeliveryPackaging()
					.setDomain(ctx.getDomainId())
					.setDelivery(delivery)
					.setItem(container));
		}
		
		List<ItemComposition> list = new LinkedList<>();
		for (PackagingDeliveryContent content : packaging.getContent()) {
			if(content.getSource() != null) {
				Integer containerId = container.getId();
				content.getComposition().stream().forEach(c -> {
					Item item = ItemDAO.get(ctx, c.getCompositionItemId());
					ItemComposition itemComposition = ItemCompositionDAO.save(ctx, new ItemComposition()
							.setItemId(containerId)
							.setCompositionItemId(c.getCompositionItemId())
							.setDescription(item.getDescription())
							.setDomain(ctx.getDomainId())
							.setQuantity(c.getQuantity()));
					itemComposition.setComposition(item);
					list.add(itemComposition);

					ItemComposition ic = ItemCompositionDAO.get(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId())
							.and(f.getItemProperty().eq(content.getSource()))
							.and(f.getCompositionItemProperty().eq(c.getCompositionItemId())));
					
					double q = ic.getQuantity() - c.getQuantity();
					if(q == 0) {
						// delete itemcomposition
						ItemCompositionDAO.delete(ctx, ic.getId());
					} else {
						// update itemcomposition
						ItemCompositionDAO.save(ctx, ic.setQuantity(q));
					}
				});		
			}
		}
		container.addItemComposition(list);


		// RESTAR STOCK!
		
		for (ItemComposition ic : container.getItemComposition()) {
			Integer productId = ic.getComposition().getProduct().getId();
			SalesDetail sd = SalesDetailDAO.get(ctx, f -> f.getDeliveryProperty().eq(deliveryId).and(f.getProductProperty().eq(productId)));
			
			DeliveryDetail dd = DeliveryDetailDAO.get(ctx, f -> f.getDelivery().eq(deliveryId)
					.and(f.getItem().eq(ic.getCompositionItemId())));
			if(dd.getId() != null) {
				dd.setQuantity(dd.getQuantity() + ic.getQuantity());
				DeliveryDetailDAO.save(ctx, dd);
			} else {
				Item compositionItem = ItemDAO.get(ctx, ic.getCompositionItemId());
				Integer line = delivery.getDetails().size() + 1;
				dd = new DeliveryDetail()
					.setDelivery(new Delivery().setId(deliveryId))
					.setDomain(ctx.getDomainId())
					.setDescription(compositionItem.getDescription())
					.setDiscountExpression(sd.getDiscountExpression().getDiscountExpr())
					.setPrice(sd.getPrice())
					.setSalesDetail(sd.getId())
					.setQuantity(ic.getQuantity())
					.setItem(compositionItem)
					.setLine(line.shortValue());
				DeliveryDetailDAO.save(ctx, dd);
			}
			sd.setDelivered(sd.getDelivered() + ic.getQuantity());
			Stock stock = WarehouseDAO.getStock(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId())
					.and(f.getItemProperty().eq(ic.getCompositionItemId())));
			if(stock.getId() != null) {
				stock.setQuantity(stock.getQuantity() - ic.getQuantity());
				WarehouseDAO.saveStock(ctx, stock);
			}
			
			sd.setStatus(sd.getQuantity() != sd.getDelivered() 
					? SalesDetailStatus.PARTIAL_SETTLED : SalesDetailStatus.SETTLED);
			SalesDetailDAO.save(ctx, sd);
			
			processItemBox(ctx, delivery, productId, ic.getQuantity());
		}
		
		// Añadir envase en delivery detail.
		Integer containerBaseId = containerBase.getId();
		DeliveryDetail dd = DeliveryDetailDAO.get(ctx, f -> f.getDelivery().eq(deliveryId)
				.and(f.getItem().eq(containerBaseId)));
		if(dd.getId() != null) {
			dd.setQuantity(dd.getQuantity() + 1); // TODO HAY QUE AÑADIR QUANTITY EN DELIVERY PACKAGING (POR LOS BOX...)
			DeliveryDetailDAO.save(ctx, dd);
		} else {
			Integer line = delivery.getDetails().size() + 1;
			dd = new DeliveryDetail()
				.setDelivery(new Delivery().setId(deliveryId))
				.setDomain(ctx.getDomainId())
				.setDescription(AonStringUtils.isBlank(containerBase.getDescription())
						? containerBase.getProduct().getName() : containerBase.getDescription())
				.setDiscountExpression("0.0")
				.setPrice(0.0)
				.setQuantity(1)// TODO HAY QUE AÑADIR QUANTITY EN DELIVERY PACKAGING (POR LOS BOX...)
				.setItem(containerBase)
				.setLine(line.shortValue());
			DeliveryDetailDAO.save(ctx, dd);
		}
		
		DataResponse dr = new DataResponse()
				.setDomain(ctx.getDomainId())
				.setCode("")
				.setResponseDate(new Date())
				.setSource(DataResponseSource.PACKAGING_DELIVERY)
				.setSourceId(deliveryId);
		dr = DataResponseDAO.insertDataResponse(ctx, dr);			
		
		DataResponseDetail drd = new DataResponseDetail()
				.setDomain(ctx.getDomainId())
				.setDataResponse(dr.getId())
				.setDataVariable("json")
				.setDataValue(PackagingDeliveryJSON.toJSON(packaging).toString());
		DataResponseDAO.insertDataResponseDetail(ctx, drd);
	
		return packaging;
	}
	
	private void deleteDeliveryPackaging(AONContext ctx, Delivery delivery) {
		DataResponseDAO.getStream(ctx, f -> f.getSourceProperty().eq(DataResponseSource.PACKAGING_DELIVERY.value())
			.and(f.getSourceIdProperty().eq(delivery.getId()))).forEach(dr -> {
				DataResponseDetail drd = DataResponseDAO.getDataResponseDetailStream(ctx, g -> g.getDataResponseProperty().eq(dr.getId()))
						.findFirst().orElse(null);
				if(drd != null) {
					PackagingDelivery packaging = PackagingDeliveryJSON.fromJSON(new JSONObject(drd.getDataValue()));
				
					// BORRANDO EL VINCULO DEL PALET (CON SSCC) CON EL ALBARÁN
					DeliveryPackagingDAO.delete(ctx, h -> h.getDomainProperty().eq(ctx.getDomainId())
							.and(h.getDeliveryProperty().eq(delivery.getId()))
							.and(h.getItemProperty().eq(packaging.getContainer().getItem())));
					
					// DEVOLVER EL CONTENIDO A SU PALET ORIGINAL
					for (PackagingDeliveryContent content : packaging.getContent()) {
						if(content.getSource() != null) {
							content.getComposition().stream().forEach(c -> {
								ItemComposition itemComposition = ItemCompositionDAO.get(ctx, k -> k.getItemProperty().eq(packaging.getContainer().getItem())
										.and(k.getCompositionItemProperty().eq(c.getCompositionItemId()))
										.and(k.getDomainProperty().eq(ctx.getDomainId())));
								
								double q = itemComposition.getQuantity() - c.getQuantity();
								if(q == 0) {
									// delete itemcomposition
									ItemCompositionDAO.delete(ctx, itemComposition.getId());
								} else {
									// update itemcomposition
									ItemCompositionDAO.save(ctx, itemComposition.setQuantity(q));
								}
								
								ItemComposition ic = ItemCompositionDAO.get(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId())
										.and(f.getItemProperty().eq(content.getSource()))
										.and(f.getCompositionItemProperty().eq(c.getCompositionItemId())));
								if(ic.getId() == null) {
									ic = ItemCompositionDAO.save(ctx, new ItemComposition()
										.setItemId(content.getSource())
										.setCompositionItemId(c.getCompositionItemId())
										.setDescription(itemComposition.getDescription())
										.setDomain(ctx.getDomainId())
										.setQuantity(c.getQuantity()));
								} else {
									ItemCompositionDAO.save(ctx, ic.setQuantity(ic.getQuantity() + c.getQuantity()));
								}
							
								// SUMAR STOCK Y ACTUALIZAR PEDIDO
								
								Integer productId = ic.getComposition().getProduct().getId();
								DeliveryDetail dd = DeliveryDetailDAO.get(ctx, f -> f.getDelivery().eq(delivery.getId())
										.and(f.getItem().eq(itemComposition.getCompositionItemId())));
								
								SalesDetail sd = SalesDetailDAO.get(ctx, f -> f.getIdProperty().eq(dd.getSalesDetail()));
								sd.setDelivered(sd.getDelivered() - c.getQuantity());
								sd.setStatus(sd.getDelivered() > 0 ? SalesDetailStatus.PARTIAL_SETTLED : SalesDetailStatus.PENDING);
								SalesDetailDAO.save(ctx, sd);
								
								// TODO FALTA REVISAR PEDIDO!!!
								
								Stock stock = WarehouseDAO.getStock(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId())
										.and(f.getItemProperty().eq(itemComposition.getCompositionItemId())));
								if(stock.getId() != null) {
									stock.setQuantity(stock.getQuantity() + c.getQuantity());
									WarehouseDAO.saveStock(ctx, stock);
								}
								
								deleteItemBox(ctx, delivery, productId, c.getQuantity());
							});		
						}
					}
				
					// QUITAR envase en delivery detail.
					Item containerItem = ItemDAO.get(ctx, packaging.getContainer().getItem());
					Item containerBase = ItemDAO.get(ctx,  f -> f.getDomainProperty().eq(ctx.getDomainId())
							.and(f.getProductProperty().eq(containerItem.getProduct().getId()))
							.and(f.getSerialNumberProperty().isNull()));
					
					DeliveryDetail dd = DeliveryDetailDAO.get(ctx, f -> f.getDelivery().eq(delivery.getId())
							.and(f.getItem().eq(containerBase.getId())));
					if(dd.getId() != null) {
						double bq = dd.getQuantity() - 1;
						if(bq > 0) {
							dd.setQuantity(bq);
							DeliveryDetailDAO.save(ctx, dd);
						} else DeliveryDetailDAO.delete(ctx, dd.getId());
					}
					
					// BORRAR DATA RESPONSE DETAIL Y DATA RESPONSE
					DataResponseDAO.deleteDataResponseDetail(ctx, f -> f.getIdProperty().eq(drd.getId()));
					DataResponseDAO.deleteDataResponse(ctx, f -> f.getIdProperty().eq(dr.getId()));
				}
			});
	}
	
	private static void processItemBox(AONContext ctx, Delivery delivery, Integer productId, double quantity) {
		Item base = ItemDAO.get(ctx,  f -> f.getDomainProperty().eq(ctx.getDomainId())
				.and(f.getProductProperty().eq(productId))
				.and(f.getSerialNumberProperty().isNull()));
		
		ItemComposition ic = ItemCompositionDAO.get(ctx, f -> f.getItemProperty().eq(base.getId()));
		if(ic != null && ic.getId() != null) {
			Double boxQuantity = quantity;
			if(base.getStockUnitTag().getId().equals(base.getPackMeasurementTag().getId())) {
				boxQuantity = quantity / base.getPackMeasurement();
				boxQuantity = boxQuantity / base.getPackUnits().doubleValue();	
			} else if(base.getStockUnitTag().getId().equals(base.getPackUnitsTag().getId())) {
				boxQuantity = quantity / base.getPackUnits().doubleValue();	
			}    
			boxQuantity = AonMathUtils.round(boxQuantity);
			
			Item box = ic.getComposition();
		
			DeliveryDetail dd = DeliveryDetailDAO.get(ctx, f -> f.getDelivery().eq(delivery.getId())
					.and(f.getItem().eq(box.getId())));
			if(dd.getId() != null) {
				dd.setQuantity(dd.getQuantity() + boxQuantity);
				DeliveryDetailDAO.save(ctx, dd);
			} else {
				Integer line = delivery.getDetails().size() + 1;
				dd = new DeliveryDetail()
					.setDelivery(new Delivery().setId(delivery.getId()))
					.setDomain(ctx.getDomainId())
					.setDescription(AonStringUtils.isBlank(box.getDescription())
							? box.getProduct().getName() : box.getDescription())
					.setDiscountExpression("0.0")
					.setPrice(0.0)
					.setQuantity(boxQuantity)
					.setItem(box)
					.setLine(line.shortValue());
				DeliveryDetailDAO.save(ctx, dd);
			}
			
			// TODO STOCK DE CAJAS... 
		}
	}
	
	private static void deleteItemBox(AONContext ctx, Delivery delivery, Integer productId, double quantity) {
		Item base = ItemDAO.get(ctx,  f -> f.getDomainProperty().eq(ctx.getDomainId())
				.and(f.getProductProperty().eq(productId))
				.and(f.getSerialNumberProperty().isNull()));
		
		ItemComposition ic = ItemCompositionDAO.get(ctx, f -> f.getItemProperty().eq(base.getId()));
		if(ic != null && ic.getId() != null) {
			Double boxQuantity = quantity;
			if(base.getStockUnitTag().getId().equals(base.getPackMeasurementTag().getId())) {
				boxQuantity = quantity / base.getPackMeasurement();
				boxQuantity = boxQuantity / base.getPackUnits().doubleValue();	
			} else if(base.getStockUnitTag().getId().equals(base.getPackUnitsTag().getId())) {
				boxQuantity = quantity / base.getPackUnits().doubleValue();	
			}    
			boxQuantity = AonMathUtils.round(boxQuantity);
			
			Item box = ic.getComposition();
		
			DeliveryDetail dd = DeliveryDetailDAO.get(ctx, f -> f.getDelivery().eq(delivery.getId())
					.and(f.getItem().eq(box.getId())));
			if(dd.getId() != null) {
				double bq = dd.getQuantity() - boxQuantity;
				if(bq > 0) {
					dd.setQuantity(bq);
					DeliveryDetailDAO.save(ctx, dd);
				} else DeliveryDetailDAO.delete(ctx, dd.getId());
			}
			
			// TODO STOCK DE CAJAS... 
		}
	}
	
	private static Elaboration processElaboration(AONContext ctx, Packaging packaging) {
		double quantity = packaging.getQuantity() * packaging.getCopies();
		Elaboration elaboration;
		ElaborationDetail elaborationDetail = ElaborationDAO.getElaborationDetail(ctx, f -> 
				f.getItemProperty().eq(packaging.getItem().getId())
				.and(f.getWarehouseProperty().eq(packaging.getWarehouse().getId())));
		if(elaborationDetail.isEmpty()) {
			String series = Integer.toString(AonDateUtils.getYear(new Date()));
			Integer number = ElaborationDAO.getNextNumber(ctx, series);
			String description = AonStringUtils.isBlank(packaging.getItem().getDescription())
					? packaging.getItem().getProduct().getName() 
					: packaging.getItem().getDescription();

			if(!description.contains("#")) {
				description = description + " #" + packaging.getItem().getSerialNumber();
			}
			
			elaboration = new Elaboration()
					.setDomain(ctx.getDomainId())
					.setSeries(series)
					.setNumber(number)
					.setDate(new Date())
					.setItem(packaging.getBase())
					.setDescription(description)
					.setWarehouse(packaging.getWarehouse())
					.setQuantity(packaging.getQuantity())
					.setStatus(ElaborationStatus.IN_PROGRESS)
					.setComments("")
					.setRemarks("")
					.setSource(null)
					.setSourceId(null);
			elaboration = ElaborationDAO.save(ctx, elaboration);
			
			elaborationDetail = new ElaborationDetail()
					.setDomain(ctx.getDomainId())
					.setElaboration(elaboration)
					.setType(ElaborationDetailType.ELABORATION)
					.setDate(new Date())
					.setItem(packaging.getItem())
					.setQuantity(quantity)
					.setWarehouse(packaging.getWarehouse())
					.setAddInfo("");
			Integer detailId = ElaborationDAO.insertElaborationDetail(ctx, elaborationDetail);
			elaborationDetail.setId(detailId);
		} else {
			elaboration = ElaborationDAO.getElaboration(ctx, elaborationDetail.getElaboration().getId());
			elaboration.setQuantity(elaboration.getQuantity() + packaging.getQuantity());
			ElaborationDAO.updateElaboration(ctx, elaboration);
			
			elaborationDetail.setQuantity(elaborationDetail.getQuantity() + quantity);
			ElaborationDAO.updateElaborationDetail(ctx, elaborationDetail);
		}
		
		Stock stock = WarehouseDAO.getStock(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId())
				.and(f.getItemProperty().eq(packaging.getItem().getId()))
				.and(f.getWarehouseProperty().eq(packaging.getWarehouse().getId())));
		if(stock.getId() == null) {
			stock.setDomain(ctx.getDomainId())
			.setItem(packaging.getItem().getId())
			.setWarehouse(packaging.getWarehouse().getId())
			.setQuantity(quantity);
		} else stock.setQuantity(stock.getQuantity() != null ? stock.getQuantity() + quantity : quantity);
		WarehouseDAO.saveStock(ctx, stock);
		return elaboration;
	}
	
	private static List<Packaging> processPackaging(AONContext ctx, Packaging packaging, Elaboration elaboration) {
		List<Packaging> list = new LinkedList<>();
		for(Integer i = 0; i < packaging.getCopies(); i++) {
			Packaging p = packaging.copy(); 
			String sscc = generateSSCC(ctx);
			System.out.println(sscc);
			Item container = packaging.getContainer().copy();
			container.setId(null).setBarcode(null).setSerialNumber(sscc).setSerialDate(new Date());
			container = ItemDAO.save(ctx, container);
			if(container.getProduct().isPerishable()) {
				Date expireDate = AonDateUtils.addDays(container.getSerialDate(), 
					container.getProduct().getDaysToExpire() != null ? container.getProduct().getDaysToExpire() : 0);
				container.setExpireDate(expireDate);
			}
			ElaborationDetail packing = new ElaborationDetail()
				.setDomain(ctx.getDomainId())
				.setElaboration(elaboration)
				.setQuantity(1.0)
				.setType(ElaborationDetailType.PACKAGING)
				.setDate(new Date())
				.setItem(container)
				.setWarehouse(packaging.getWarehouse())
				.setAddInfo("");
			
			Integer packingId = ElaborationDAO.insertElaborationDetail(ctx, packing);		
			packing.setId(packingId);

			ElaborationDetailComposition composition = new ElaborationDetailComposition()
				.setDomain(ctx.getDomainId())
				.setElaborationDetail(packing)
				.setItem(p.getItem())
				.setQuantity(p.getQuantity())
				.setWarehouse(packaging.getWarehouse())
				.setAddInfo("");
			Integer compositionId = ElaborationDetailCompositionDAO.insertElaborationDetailComposition(ctx, composition);
			composition.setId(compositionId);
		
			ItemComposition itemComposition = new ItemComposition()
				.setDomain(ctx.getDomainId())
				.setItemId(container.getId())
				.setCompositionItemId(p.getItem().getId())
				.setDescription(p.getItem().getDescription())
				.setQuantity(p.getQuantity())
				.setSequence(1)
				.setDiscountExpression("0.0");
			ItemCompositionDAO.save(ctx, itemComposition);
			list.add(p.setContainer(container));
			
			// SAVE CONTAINER IN STOCK
			Stock stock = new Stock()
					.setDomain(ctx.getDomainId())
					.setItem(container.getId())
					.setQuantity(1.0)
					.setWarehouse(packaging.getWarehouse().getId());
			WarehouseDAO.saveStock(ctx, stock);
			
			// TODO RESTAR STOCK CONTAINER BASE????
		}
		return list;
	}
	
	private static String generateSSCC(AONContext ctx){
		ApplicationParameter param = AppParamDAO.fetchOne(ctx, AppParam.SSCC_LAST_NUMBER);
		Integer number = 0;
		if(param != null) {
			number = AonNumberUtils.toint(param.getValue());
			number++;
		} else {
			param = new ApplicationParameter()
					.setDomain(ctx.getDomainId())
					.setName(AppParam.SSCC_LAST_NUMBER)
					.setValue(number.toString());
		}
		String aonNumber = AonStringUtils.leftPad(number.toString(), 6, "0");
		AppParamDAO.saveApplicationParameter(ctx, param.setValue(number.toString()));

		String sscc = "08437016734" + aonNumber;
		String control = calculateSsccControlDigit(sscc);
		
		return sscc + control;
	}
	
	private static String calculateSsccControlDigit(String sscc) {
		char[] array = sscc.toCharArray();
		Integer sum = 0;
		for(Integer i = 0; i < array.length; i++) {
			sum = sum + Character.getNumericValue(array[i]) * (AonNumberUtils.isPar(i) ? 3 : 1);
		}

		Integer resto = sum % 10;
		Integer value = resto.equals(0) ? resto : 10 - resto;
		return value.toString();
	}
	
	private static String calculateSerialNumber(String barcode) {
		if(barcode.length() > 14) {
			Barcode b = new Barcode().setValue(barcode).setType(BarcodeType.GS1_128);
			Integer year = AonDateUtils.getYear(new Date());
			String init = year.toString().substring(2,4);
			return init + b.parseGS1128().get(GS1128Codes.CODE_10);
		}
		return ""; //"22" + Integer.toString(AonDateUtils.getDayOfYear(new Date()));
	}
	
	private static Date calculateSerialDate(String barcode) {
		if(barcode.length() > 14) {
			Barcode b = new Barcode().setValue(barcode).setType(BarcodeType.GS1_128);
			return AonDateUtils.parse(b.parseGS1128().get(GS1128Codes.CODE_15), "yyMMdd");
		}
		return new Date();
	}
	
	private static String calculateBarcode(String barcode) {
		if(barcode.length() > 14) {
			Barcode b = new Barcode().setValue(barcode).setType(BarcodeType.GS1_128);
			return b.parseGS1128().get(GS1128Codes.CODE_01);
		}
		return barcode;
	}

}
