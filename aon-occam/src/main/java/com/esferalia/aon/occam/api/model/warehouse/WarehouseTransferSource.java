package com.esferalia.aon.occam.api.model.warehouse;

public enum WarehouseTransferSource {

	DIRECT_TRANSFER,
	INVENTORY,
	MANUFACTURING_ORDER,
	INVENTORY_INIT_STOCK;
	
	public byte value() {
		return (byte) this.ordinal();
	}
}
