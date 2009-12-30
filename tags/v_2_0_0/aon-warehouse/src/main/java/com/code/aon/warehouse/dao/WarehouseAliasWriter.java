package com.code.aon.warehouse.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.common.dao.AliasWriter;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.DeliveryDetail;
import com.code.aon.warehouse.Income;
import com.code.aon.warehouse.IncomeDetail;
import com.code.aon.warehouse.Inventory;
import com.code.aon.warehouse.InventoryDetail;
import com.code.aon.warehouse.Stock;
import com.code.aon.warehouse.Warehouse;

/**
 * @author Consulting & Development. jurkiri - 22/01/2007
 *
 */
public class WarehouseAliasWriter {
	
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		File file = new File("/PROYECTOS/aon-warehouse/src/com/code/aon/warehouse/dao/IWarehouseAlias.java");
//		File file = new File("c:/IWarehouseAlias.java");
		String[] classes = new String[8]; 
		classes[0] = Delivery.class.getName();
		classes[1] = DeliveryDetail.class.getName();
		classes[2] = Income.class.getName();
		classes[3] = IncomeDetail.class.getName();
		classes[4] = Inventory.class.getName();
		classes[5] = InventoryDetail.class.getName();
		classes[6] = Stock.class.getName();
		classes[7] = Warehouse.class.getName();
		AliasWriter writer = new AliasWriter("com.code.aon.warehouse.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}