package com.esferalia.aon.occam.api;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.Elaboration;
import com.esferalia.aon.occam.api.model.ElaborationDetail;
import com.esferalia.aon.occam.api.model.ElaborationDetailComposition;
import com.esferalia.aon.occam.api.model.Filter.CarrierPackingFilter;
import com.esferalia.aon.occam.api.model.Filter.DeliveryFilter;
import com.esferalia.aon.occam.api.model.Filter.DepartmentFilter;
import com.esferalia.aon.occam.api.model.Filter.ElaborationDetailCompositionFilter;
import com.esferalia.aon.occam.api.model.Filter.ElaborationDetailFilter;
import com.esferalia.aon.occam.api.model.Filter.ElaborationFilter;
import com.esferalia.aon.occam.api.model.Filter.IncomeDetailFilter;
import com.esferalia.aon.occam.api.model.Filter.IncomeFilter;
import com.esferalia.aon.occam.api.model.Filter.InventoryDetailFilter;
import com.esferalia.aon.occam.api.model.Filter.ProductFilter;
import com.esferalia.aon.occam.api.model.Filter.SeriesFilter;
import com.esferalia.aon.occam.api.model.Filter.StockFilter;
import com.esferalia.aon.occam.api.model.Filter.WarehouseFilter;
import com.esferalia.aon.occam.api.model.Filter.WarehouseTransferFilter;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPacking;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryDetail;
import com.esferalia.aon.occam.api.model.warehouse.Department;
import com.esferalia.aon.occam.api.model.warehouse.Income;
import com.esferalia.aon.occam.api.model.warehouse.IncomeDetail;
import com.esferalia.aon.occam.api.model.warehouse.Inventory;
import com.esferalia.aon.occam.api.model.warehouse.InventoryDetail;
import com.esferalia.aon.occam.api.model.warehouse.Series;
import com.esferalia.aon.occam.api.model.warehouse.Stock;
import com.esferalia.aon.occam.api.model.warehouse.Warehouse;
import com.esferalia.aon.occam.api.model.warehouse.WarehouseTransfer;
import com.esferalia.aon.occam.api.model.warehouse.WarehouseTransferDetail;

public interface IWarehouse {
	
	// 	***********************************************
	// 	********************************* WAREHOUSE ***
	// 	***********************************************
	Stream<Warehouse> getWarehouseStream(AONContext ctx, WarehouseFilter filter);
	Warehouse getWarehouse(AONContext ctx, WarehouseFilter filter);
	
	// 	***********************************************
	// 	************************************ INCOME ***
	// 	***********************************************

	Optional<Income> insertIncome(AONContext ctx, Income income);
	Stream<Income> getIncomeStream(AONContext ctx, IncomeFilter filter);
	Optional<Income> deleteIncome(AONContext ctx, Integer id);
	
	// 	***********************************************
	// 	****************************** INCOME DETAIL***
	// 	***********************************************

	Stream<IncomeDetail> getIncomeDetailStream(AONContext ctx, IncomeDetailFilter filter);
	Stream<IncomeDetail> getIncomeDetailStream(AONContext ctx, IncomeFilter incomeFilter,
			ProductFilter productFilter);
	Optional<IncomeDetail> insertIncomeDetail(AONContext ctx, IncomeDetail incomeDetail);
	Optional<IncomeDetail> updateIncomeDetail(AONContext ctx, IncomeDetail incomeDetail);
	Optional<IncomeDetail> deleteIncomeDetail(AONContext ctx, Integer id);
	
	IncomeDetail getLastIncomeDetail(AONContext ctx, Item item, Integer workplaceId, Integer warehouseId);
	IncomeDetail getLastIncomeDetailUntilDate(AONContext ctx, Item item, Integer workplaceId, Integer warehouseId, Date date);
	LinkedList<IncomeDetail> getLastIncomeDetailList(AONContext ctx, Item item, Date startDate, Integer workplaceId, Integer warehouseId);
	LinkedList<IncomeDetail> getLastIncomeDetailListUntilDate(AONContext ctx, Item item, Date startDate, Integer workplaceId, Integer warehouseId, Date date);
	LinkedList<IncomeDetail> getIncomeDetailList(AONContext ctx, Item item, Integer workplaceId, Integer warehouseId);
	LinkedList<IncomeDetail> getIncomeDetailListUntilDate(AONContext ctx, Item item, Integer workplaceId, Integer warehouseId, Date date);

	// 	***********************************************
	// 	****************************** INCOME DETAIL***
	// 	***********************************************
	Stream<DeliveryDetail> getDeliveryDetailStream(AONContext ctx, DeliveryFilter deliveryFilter, ProductFilter productFilter);
	
	// 	***********************************************
	// 	************************** INVENTORY DETAIL ***
	// 	***********************************************
	
	Stream<InventoryDetail> getInventoryDetailStream(AONContext ctx, InventoryDetailFilter filter);
	LinkedList<InventoryDetail> getInventoryDetailList(AONContext ctx, Integer inventoryId);
	void updateInventoryDetail(AONContext ctx, InventoryDetail inventoryDetail);
	void updateZeroInventoryDetail(AONContext ctx);
	
	// 	***********************************************
	// 	********************************* INVENTORY ***
	// 	***********************************************
	
	LinkedList<Inventory> getInventoryList(AONContext ctx, Date startDate, Date endDate);
	LinkedList<Inventory> getTwoLastInventory(AONContext ctx, Integer warehouseID);
	void updateInventory(AONContext ctx, Inventory inventory);
	void deleteInventory(AONContext ctx, Integer inventoryId);
	
	// 	***********************************************
	// 	************************ WAREHOUSE TRANSFER ***
	// 	***********************************************

	Stream<WarehouseTransfer> getWarehouseTransferStream(AONContext ctx, WarehouseTransferFilter filter);
	
	void updateWarehouseTransfer(AONContext ctx, WarehouseTransfer warehouseTransfer);
	Integer insertWarehouseTransfer(AONContext ctx, WarehouseTransfer warehouseTransfer);
	Integer insertWarehouseTransferDetail(AONContext ctx, WarehouseTransferDetail warehouseTransferDetail);
	Integer insertWarehouseTransferDetail(AONContext ctx, Stream<WarehouseTransferDetail> warehouseTransferDetail);

	void deleteWarehouseTransfer(AONContext ctx, WarehouseTransferFilter filter);
	
	Integer getWarehouseTransferNextNumber(AONContext ctx, String serie);
	
	// 	***********************************************
	// 	******************************** DEPARTMENT ***
	// 	***********************************************
	
	Department getDepartment(AONContext ctx, Integer workplaceId, DepartmentFilter filter);
	LinkedList<Department> getDepartmentList(AONContext ctx, Integer workplaceId, DepartmentFilter filter);
	
	// 	***********************************************
	// 	************************************ SERIES ***
	// 	***********************************************
	
	Stream<Series> getSeriesStream(AONContext ctx, SeriesFilter filter);
	LinkedList<Series> getSeriesDeliveryList(AONContext ctx, Integer scopeId);

	// 	***********************************************
	// 	************************************ STOCK ****
	// 	***********************************************
	
	Stream<Stock> getStockStream(AONContext ctx, StockFilter filter);
	Optional<Stock> insertStock(AONContext ctx, Stock stock);
	Optional<Stock> updateStock(AONContext ctx, Stock stock);
	Optional<Stock> deleteStock(AONContext ctx, Integer stockId);
	
	// 	***********************************************
	// 	************************** CARRIER PACKING ****
	// 	***********************************************
	
	Stream<String> getCarrierPackingSeries(AONContext ctx);
	Stream<CarrierPacking> getCarrierPackingStream(AONContext ctx, CarrierPackingFilter filter);
	Integer insertCarrierPacking(AONContext ctx, CarrierPacking carrierPacking);
	CarrierPacking updateCarrierPacking(AONContext ctx, CarrierPacking carrierPacking, CarrierPackingFilter filter);
	void deleteCarrierPacking(AONContext ctx, CarrierPackingFilter filter);

	// 	***********************************************
	// 	****************************** ELABORATION ****
	// 	***********************************************
	Elaboration getElaboration(AONContext ctx, Integer id);
	Stream<Elaboration> getElaborationStream(AONContext ctx, ElaborationFilter filter);
	ElaborationDetail getElaborationDetail(AONContext ctx, Integer id);
	List<ElaborationDetail> getElaborationDetailList(AONContext ctx, Integer id);
	List<ElaborationDetail> getElaborationDetailList(AONContext ctx, ElaborationDetailFilter filter);
	ElaborationDetailComposition getElaborationDetailComposition(AONContext ctx, Integer compositionId);
	List<ElaborationDetailComposition> getElaborationDetailCompositionList(AONContext ctx, Integer id);
	List<ElaborationDetailComposition> getElaborationDetailCompositionList(AONContext ctx, ElaborationDetailCompositionFilter filter);
	Integer insertElaboration(AONContext ctx, Elaboration elaboration);
	Elaboration updateElaboration(AONContext ctx, Elaboration elaboration);
	Elaboration deleteElaboration(AONContext ctx, Integer id);
	Integer insertElaborationDetail(AONContext ctx, ElaborationDetail detail);
	ElaborationDetail updateElaborationDetail(AONContext ctx, ElaborationDetail detail);
	ElaborationDetail deleteElaborationDetail(AONContext ctx, Integer id);
	ElaborationDetail deleteElaborationDetail(AONContext ctx, ElaborationDetailFilter filter);
	Integer insertElaborationDetailComposition(AONContext ctx, ElaborationDetailComposition composition);
	ElaborationDetailComposition updateElaborationDetailComposition(AONContext ctx, ElaborationDetailComposition composition);
	ElaborationDetailComposition deleteElaborationDetailComposition(AONContext ctx, ElaborationDetailCompositionFilter filter);
	
	Integer getElaborationNextNumber(AONContext ctx, String serie);

	
}
