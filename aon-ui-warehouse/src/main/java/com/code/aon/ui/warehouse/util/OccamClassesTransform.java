package com.code.aon.ui.warehouse.util;

import java.sql.Timestamp;

import com.code.aon.product.Product;
import com.code.aon.product.enumeration.ProductStatus;
import com.code.aon.warehouse.Inventory;
import com.code.aon.warehouse.InventoryDetail;
import com.esferalia.aon.occam.api.model.product.OldItem;

public class OccamClassesTransform {

	public static OldItem getItem(com.code.aon.product.Item item){
		OldItem i = new OldItem();
		i.setId(item.getId());
		i.setDomain(item.getDomain());
		if(item.getProduct() != null) i.setProductId(item.getProduct().getId());
		i.setDetail(item.getDetail());
		i.setDetail2(item.getDetail2());
		i.setDetail3(item.getDetail3());
		i.setDescription(item.getDescription());
		i.setSerialNumber(item.getSerialNumber());
		i.setPrice(item.getPrice());
		if(item.getStatus() != null) i.setStatus((byte)item.getStatus().ordinal());
		i.setExpensesFixed(item.getExpensesFixed());
		i.setProfitPercent(item.getProfitPercent());
		i.setPurchasePrice(item.getPurchasePrice());
		i.setInternet(item.isInternet());
		i.setBarcode(item.getBarcode());
		if(item.getCreationDate() != null) i.setCreationDate(new Timestamp(item.getCreationDate().getTime()));
		i.setCreationUser(item.getCreationUser());
		if(item.getModificationDate() != null) i.setModificationDate(new Timestamp(item.getModificationDate().getTime()));
		i.setModificationUser(item.getModificationUser());
		
		return i;
	}
	
	public static com.code.aon.product.Item getItem(OldItem item){
		com.code.aon.product.Item i = new com.code.aon.product.Item();
		i.setId(item.getId());
		i.setDomain(item.getDomain());
		Product product = new Product();
		product.setId(item.getProductId());
		i.setProduct(product);
		i.setDetail(item.getDetail());
		i.setDetail2(item.getDetail2());
		i.setDetail3(item.getDetail3());
		i.setDescription(item.getDescription());
		i.setSerialNumber(item.getSerialNumber());
		i.setPrice(item.getPrice());
		if(item.getStatus() != null) i.setStatus(ProductStatus.values()[item.getStatus()]);
		i.setExpensesFixed(item.getExpensesFixed());
		i.setProfitPercent(item.getProfitPercent());
		i.setPurchasePrice(item.getPurchasePrice());
		i.setInternet(item.isInternet());
		i.setBarcode(item.getBarcode());
		if(item.getCreationDate() != null) i.setCreationDate(new Timestamp(item.getCreationDate().getTime()));
		i.setCreationUser(item.getCreationUser());
		if(item.getModificationDate() != null) i.setModificationDate(new Timestamp(item.getModificationDate().getTime()));
		i.setModificationUser(item.getModificationUser());
		
		return i;
	}
	
	public static InventoryDetail getInventoryDetail(com.esferalia.aon.occam.api.model.warehouse.InventoryDetail id){
		InventoryDetail inventoryDetail = new InventoryDetail();
		
		inventoryDetail.setActualQuantity(id.getActualQuantity());
		inventoryDetail.setCost(id.getCost());
		inventoryDetail.setCreationDate(id.getCreationDate());
		inventoryDetail.setCreationUser(id.getCreationUser());
		inventoryDetail.setDomain(id.getDomain());
		inventoryDetail.setId(id.getId());
		Inventory inventory = new Inventory();
		inventory.setId(id.getInventory().getId());
		inventoryDetail.setInventory(inventory);
		inventoryDetail.setItem(getItem(id.getItem()));
		inventoryDetail.setModificationDate(id.getModificationDate());
		inventoryDetail.setModificationUser(id.getModificationUser());
		inventoryDetail.setRealQuantity(id.getRealQuantity());
		
		return inventoryDetail;
	}
	
	public static com.esferalia.aon.occam.api.model.warehouse.Inventory getInventory(Inventory inventory){
		com.esferalia.aon.occam.api.model.warehouse.Inventory i = new com.esferalia.aon.occam.api.model.warehouse.Inventory();
		i.setCreationDate(inventory.getCreationDate());
		i.setCreationUser(inventory.getCreationUser());
		i.setDescription(inventory.getDescription());
		i.setDomain(inventory.getDomain());
		i.setId(inventory.getId());
		i.setInventoryDate(inventory.getInventoryDate());
		i.setModificationDate(inventory.getModificationDate());
		i.setModificationUser(inventory.getModificationUser());
		i.setStatus(inventory.getStatus().ordinal());
		i.setWarehouse(inventory.getWarehouse() != null ? inventory.getWarehouse().getId() : null);
		return i;
	}
}
