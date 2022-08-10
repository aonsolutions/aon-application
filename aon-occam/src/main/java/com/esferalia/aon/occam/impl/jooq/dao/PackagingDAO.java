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
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.ItemComposition;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.ElaborationStatus;
import com.esferalia.aon.occam.api.model.warehouse.Packaging;
import com.esferalia.aon.occam.api.model.warehouse.Stock;
import com.esferalia.aon.occam.api.model.warehouse.Warehouse;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class PackagingDAO {
	
	private PackagingDAO() {
	
	}

	public static Packaging get(AONContext ctx, String barcode){
		String serialNumber = calculateSerialNumber(barcode);
		Date serialDate = calculateSerialDate(barcode);
		
		Item item = ItemDAO.get(ctx,  f ->
			f.getDomainProperty().eq(ctx.getDomainId())
			.and(f.getBarcodeProperty().eq(barcode))
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
	
	public static Packaging save(AONContext ctx, Packaging packaging) {
		if(packaging.getItem().getId() == null) {
			Item item = ItemDAO.save(ctx, packaging.getItem());
			packaging.setItem(item);
		}
		Warehouse warehouse = WarehouseDAO.getWarehouse(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId()));
		Elaboration elaboration = processElaboration(ctx, packaging, warehouse);
		return processPackaging(ctx, packaging, elaboration, warehouse);
	}
	
	private static Elaboration processElaboration(AONContext ctx, Packaging packaging, Warehouse warehouse) {
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
					.setDescription("")
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
					.setQuantity(packaging.getQuantity())
					.setWarehouse(warehouse)
					.setAddInfo("");
			Integer detailId = ElaborationDAO.insertElaborationDetail(ctx, elaborationDetail);
			elaborationDetail.setId(detailId);
			
		} else {
			elaboration = ElaborationDAO.getElaboration(ctx, elaborationDetail.getElaboration().getId());
			elaboration.setQuantity(elaboration.getQuantity() + packaging.getQuantity());
			ElaborationDAO.updateElaboration(ctx, elaboration);
			
			elaborationDetail.setQuantity(elaborationDetail.getQuantity() + packaging.getQuantity());
			ElaborationDAO.updateElaborationDetail(ctx, elaborationDetail);
		}
		
		Stock stock = WarehouseDAO.getStock(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId())
				.and(f.getItemProperty().eq(packaging.getItem().getId())));
		if(stock.getId() == null) {
			stock.setDomain(ctx.getDomainId())
			.setItem(packaging.getItem().getId())
			.setWarehouse(warehouse.getId())
			.setQuantity(packaging.getQuantity());
		} else stock.setQuantity(stock.getQuantity() != null ? stock.getQuantity() + packaging.getQuantity() : packaging.getQuantity());
		WarehouseDAO.saveStock(ctx, stock);
		return elaboration;
	}
	
	private static Packaging processPackaging(AONContext ctx, Packaging packaging, Elaboration elaboration, Warehouse warehouse) {
		String sscc = generateSSCC(ctx);
		
		Item container = packaging.getContainer();
		container.setId(null).setBarcode(null).setSerialNumber(sscc).setSerialDate(new Date());
		container = ItemDAO.save(ctx, container);
		
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
				.setItem(packaging.getItem())
				.setQuantity(packaging.getQuantity())
				.setWarehouse(warehouse)
				.setAddInfo("");
		Integer compositionId = ElaborationDAO.insertElaborationDetailComposition(ctx, composition);
		composition.setId(compositionId);
		
		ItemComposition itemComposition = new ItemComposition()
				.setDomain(ctx.getDomainId())
				.setItemId(container.getId())
				.setCompositionItemId(packaging.getItem().getId())
				.setDescription(packaging.getItem().getDescription())
				.setQuantity(packaging.getQuantity())
				.setSequence(1)
				.setDiscountExpression("0.0");
		ItemCompositionDAO.save(ctx, itemComposition);
		return packaging.setContainer(container);
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
		return barcode.contains("(") ? "" : Integer.toString(AonDateUtils.getDayOfYear(new Date()));
	}
	
	private static Date calculateSerialDate(String barcode) {
		return new Date();
	}

}
