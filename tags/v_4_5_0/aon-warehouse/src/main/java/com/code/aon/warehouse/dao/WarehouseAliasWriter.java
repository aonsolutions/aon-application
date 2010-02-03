package com.code.aon.warehouse.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.common.dao.AliasWriter;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.DeliveryDetail;
import com.code.aon.warehouse.DeliveryDetailLabour;
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
		File file = new File("/AON-PROJECT/aon-warehouse/src/main/java/com/code/aon/warehouse/dao/IWarehouseAlias.java");
		String[] classes = new String[] { 
			Delivery.class.getName(),
			DeliveryDetail.class.getName(),
			DeliveryDetailLabour.class.getName(),
			Income.class.getName(),
			IncomeDetail.class.getName(),
			Inventory.class.getName(),
			InventoryDetail.class.getName(),
			Stock.class.getName(),
			Warehouse.class.getName() };
		HibernateUtil.getSessionFactory(null);
		AliasWriter writer = new AliasWriter("com.code.aon.warehouse.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}