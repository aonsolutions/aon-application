package com.esferalia.aon.occam.impl.jooq.dao;


import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Elaboration;
import com.esferalia.aon.occam.api.model.ElaborationDetail;
import com.esferalia.aon.occam.api.model.ElaborationDetailComposition;
import com.esferalia.aon.occam.api.model.ElaborationDetailType;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.management.SalesDetail;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.ItemComposition;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.DeliveryStatus;
import com.esferalia.aon.occam.api.model.type.ElaborationStatus;
import com.esferalia.aon.occam.api.model.warehouse.Barcode;
import com.esferalia.aon.occam.api.model.warehouse.BarcodeType;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryDetail;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryPackaging;
import com.esferalia.aon.occam.api.model.warehouse.GS1128Codes;
import com.esferalia.aon.occam.api.model.warehouse.Packaging;
import com.esferalia.aon.occam.api.model.warehouse.PackagingDelivery;
import com.esferalia.aon.occam.api.model.warehouse.Stock;
import com.esferalia.aon.occam.api.model.warehouse.Warehouse;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class PackagingDAO {
	
	private PackagingDAO() {
	
	}
	

	public static DeliveryPackaging getDeliveryPackaging(AONContext ctx, String sscc, Integer delivery, Integer product){
		Item container = ItemDAO.getFull(ctx, f -> 
			f.getDomainProperty().eq(ctx.getDomainId())
			.and(f.getSerialNumberProperty().eq(sscc)));
		if(!container.isEmpty()) {
			checkComposition(ctx, container, product);
			DeliveryPackaging dp = DeliveryPackagingDAO.get(ctx, f -> f.getItemProperty().eq(container.getId()));
			if((!dp.isEmpty() && delivery == null)
				|| (!dp.isEmpty() && delivery != null && !dp.getDelivery().getId().equals(delivery))) {
				throw new AonCoreException("El Envase pertenece a otro albarán "); 
			} else if(!dp.isEmpty() && delivery != null && dp.getDelivery().getId().equals(delivery)) {
				return dp;
			}
		} else throw new AonCoreException("No existe ningún envase con el SSCC indicado"); 

		return new DeliveryPackaging()
				.setItem(container);
	}

	private static void checkComposition(AONContext ctx, Item item, Integer product) {
		if(item.getItemComposition().size() > 1) {
			List<Integer> ps = new LinkedList<>();
	 		item.getItemComposition().stream().forEach(r -> {
	 			Item i = ItemDAO.get(ctx, r.getItemId());
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
			.and(f.getSerialNumberProperty().isNull()));
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
			List<ItemComposition> icList = ItemCompositionDAO.getList(ctx, f -> f.getItemProperty().eq(id));
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

		Warehouse warehouse = WarehouseDAO.getWarehouse(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId()));
		Elaboration elaboration = processElaboration(ctx, packaging, warehouse);
		return processPackaging(ctx, packaging, elaboration, warehouse);
	}
	
	public static PackagingDelivery saveDeliveryPackaging(AONContext ctx, PackagingDelivery packaging) {
		SalesDetail sd = SalesDetailDAO.get(ctx, packaging.getSalesDetail());
		
		Delivery delivery = null;
		if(packaging.getDelivery() == null) {
			// CREAR DELIVERY
			Integer number = DeliveryDAO.getNextNumber(ctx, "A23");

			delivery = new Delivery()
				.setSeries("A23")
				.setNumber(number)
				.setDate(new Date())
				.setAddress(RegistryAddressDAO.getMain(ctx, sd.getSales().getCustomer().getId()))
				.setDomain(ctx.getDomainId())
				.setStatus(DeliveryStatus.PENDING)
				.setWorkplace(new Workplace().setId(11208))
				.setScope(new Scope().setId(3540))
				.setCustomer(sd.getSales().getCustomer())
				.setPymntDays("");
			delivery = DeliveryDAO.save(ctx, delivery);
		} else {
			delivery = DeliveryDAO.get(ctx, packaging.getDelivery());
		}
		
		Integer deliveryId = delivery.getId();
		// Container
		Item container = null;
		if(packaging.getContainer().getItem() != null) {
			container = ItemDAO.getFull(ctx, f -> f.getIdProperty().eq(packaging.getContainer().getItem()));
			// AÑADIR A DELIVERY PACKAGING SI NO EXISTE YA.
			Integer cId = container.getId();
			DeliveryPackaging dp = DeliveryPackagingDAO.get(ctx, f -> f.getItemProperty().eq(cId)
					.and(f.getDeliveryProperty().eq(deliveryId)));
			if(dp.getId() == null) {
				dp = DeliveryPackagingDAO.save(ctx, new DeliveryPackaging()
						.setDomain(ctx.getDomainId())
						.setDelivery(delivery)
						.setItem(container));
			}
		} else if(packaging.getContainer().getProduct() != null){
			Item base = ItemDAO.get(ctx,  f -> f.getDomainProperty().eq(ctx.getDomainId())
					.and(f.getProductProperty().eq(packaging.getContainer().getProduct()))
					.and(f.getSerialNumberProperty().isNull()));
			container = ItemDAO.save(ctx, base.copy()
					.setSerialNumber(generateSSCC(ctx))
					.setSerialDate(new Date()));
			// CREAR ITEM DEL ENVASADO.
			// AÑADIR DELIVERY PACKAGING
			DeliveryPackaging dp = DeliveryPackagingDAO.save(ctx, new DeliveryPackaging()
					.setDomain(ctx.getDomainId())
					.setDelivery(delivery)
					.setItem(container));
		}
		
		if(packaging.getContent().getSource() != null) {
			List<ItemComposition> list = new LinkedList<>();
			Integer containerId = container.getId();
			packaging.getContent().getComposition().stream().forEach(c -> {
				Item item = ItemDAO.get(ctx, c.getCompositionItemId());
				ItemComposition itemComposition = ItemCompositionDAO.save(ctx, new ItemComposition()
						.setItemId(containerId)
						.setCompositionItemId(c.getCompositionItemId())
						.setDescription(item.getDescription())
						.setDomain(ctx.getDomainId())
						.setQuantity(c.getQuantity()));
				list.add(itemComposition);

				ItemComposition ic = ItemCompositionDAO.get(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId())
						.and(f.getItemProperty().eq(packaging.getContent().getSource()))
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
			container.setItemComposition(list);

			// AÑADIR COMPOSITION SI ES NECESARIO
			// QUITAR COMPOSITION DEL SOURCE SI ES NECESARIO			
		}

		// RESTAR STOCK!
		
		for (ItemComposition ic : container.getItemComposition()) {
			DeliveryDetail dd = DeliveryDetailDAO.get(ctx, f -> f.getDelivery().eq(deliveryId)
					.and(f.getItem().eq(ic.getCompositionItemId())));
			if(dd.getId() != null) {
				dd.setQuantity(dd.getQuantity() + ic.getQuantity());
				DeliveryDetailDAO.save(ctx, dd);
			} else {
				Integer line = delivery.getDetails().size() + 1;
				dd = new DeliveryDetail()
					.setDelivery(new Delivery().setId(deliveryId))
					.setDomain(ctx.getDomainId())
					.setDescription(ic.getDescription())
					.setDiscountExpression(sd.getDiscountExpression().getDiscountExpr())
					.setPrice(sd.getPrice())
					.setSalesDetail(sd.getId())
					.setQuantity(ic.getQuantity())
					.setItem(new Item().setId(ic.getCompositionItemId()))
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
		}
	
		SalesDetailDAO.save(ctx, sd);
		return packaging;
	}
	
	private static Elaboration processElaboration(AONContext ctx, Packaging packaging, Warehouse warehouse) {
		double quantity = packaging.getQuantity() * packaging.getCopies();
		Elaboration elaboration;
		ElaborationDetail elaborationDetail = ElaborationDAO.getElaborationDetail(ctx, f -> f.getItemProperty().eq(packaging.getItem().getId()));
		if(elaborationDetail.isEmpty()) {
			String series = Integer.toString(AonDateUtils.getYear(new Date()));
			Integer number = ElaborationDAO.getNextNumber(ctx, series);
			elaboration = new Elaboration()
					.setDomain(ctx.getDomainId())
					.setSeries(series)
					.setNumber(number)
					.setDate(new Date())
					.setItem(packaging.getBase())
					.setDescription(packaging.getItem().getProduct().getName())
					.setWarehouse(warehouse)
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
					.setWarehouse(warehouse)
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
				.and(f.getItemProperty().eq(packaging.getItem().getId())));
		if(stock.getId() == null) {
			stock.setDomain(ctx.getDomainId())
			.setItem(packaging.getItem().getId())
			.setWarehouse(warehouse.getId())
			.setQuantity(quantity);
		} else stock.setQuantity(stock.getQuantity() != null ? stock.getQuantity() + quantity : quantity);
		WarehouseDAO.saveStock(ctx, stock);
		return elaboration;
	}
	
	private static List<Packaging> processPackaging(AONContext ctx, Packaging packaging, Elaboration elaboration, Warehouse warehouse) {
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
				.setWarehouse(warehouse)
				.setAddInfo("");
			
			Integer packingId = ElaborationDAO.insertElaborationDetail(ctx, packing);		
			packing.setId(packingId);

			ElaborationDetailComposition composition = new ElaborationDetailComposition()
				.setDomain(ctx.getDomainId())
				.setElaborationDetail(packing)
				.setItem(p.getItem())
				.setQuantity(p.getQuantity())
				.setWarehouse(warehouse)
				.setAddInfo("");
			Integer compositionId = ElaborationDAO.insertElaborationDetailComposition(ctx, composition);
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
