package com.esferalia.aon.occam.impl.jooq;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IWarehouse;
import com.esferalia.aon.occam.api.model.Elaboration;
import com.esferalia.aon.occam.api.model.ElaborationDetail;
import com.esferalia.aon.occam.api.model.ElaborationDetailComposition;
import com.esferalia.aon.occam.api.model.Filter.CarrierPackingFilter;
import com.esferalia.aon.occam.api.model.Filter.DeliveryDetailFilter;
import com.esferalia.aon.occam.api.model.Filter.DeliveryFilter;
import com.esferalia.aon.occam.api.model.Filter.DeliveryInfoFilter;
import com.esferalia.aon.occam.api.model.Filter.DepartmentFilter;
import com.esferalia.aon.occam.api.model.Filter.ElaborationDetailCompositionFilter;
import com.esferalia.aon.occam.api.model.Filter.ElaborationDetailFilter;
import com.esferalia.aon.occam.api.model.Filter.ElaborationFilter;
import com.esferalia.aon.occam.api.model.Filter.IncomeDetailFilter;
import com.esferalia.aon.occam.api.model.Filter.IncomeFilter;
import com.esferalia.aon.occam.api.model.Filter.InventoryDetailFilter;
import com.esferalia.aon.occam.api.model.Filter.InventoryFilter;
import com.esferalia.aon.occam.api.model.Filter.ItemFilter;
import com.esferalia.aon.occam.api.model.Filter.ProductFilter;
import com.esferalia.aon.occam.api.model.Filter.StockFilter;
import com.esferalia.aon.occam.api.model.Filter.WarehouseFilter;
import com.esferalia.aon.occam.api.model.Filter.WarehouseTransferFilter;
import com.esferalia.aon.occam.api.model.Options;
import com.esferalia.aon.occam.api.model.product.ItemComposition;
import com.esferalia.aon.occam.api.model.product.OldItem;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPacking;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryDetail;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryInfo;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryPackaging;
import com.esferalia.aon.occam.api.model.warehouse.Department;
import com.esferalia.aon.occam.api.model.warehouse.Income;
import com.esferalia.aon.occam.api.model.warehouse.IncomeDetail;
import com.esferalia.aon.occam.api.model.warehouse.Inventory;
import com.esferalia.aon.occam.api.model.warehouse.InventoryDetail;
import com.esferalia.aon.occam.api.model.warehouse.Packaging;
import com.esferalia.aon.occam.api.model.warehouse.PackagingDelivery;
import com.esferalia.aon.occam.api.model.warehouse.PaturpatQuality;
import com.esferalia.aon.occam.api.model.warehouse.Stock;
import com.esferalia.aon.occam.api.model.warehouse.UdapaQuality;
import com.esferalia.aon.occam.api.model.warehouse.Warehouse;
import com.esferalia.aon.occam.api.model.warehouse.WarehouseTransfer;
import com.esferalia.aon.occam.api.model.warehouse.WarehouseTransferDetail;
import com.esferalia.aon.occam.impl.jooq.dao.CarrierPackingDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DeliveryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ElaborationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ElaborationDetailCompositionDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ElaborationDetailDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ElaborationPackageDAO;
import com.esferalia.aon.occam.impl.jooq.dao.IncomeDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InventoryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PackagingDAO;
import com.esferalia.aon.occam.impl.jooq.dao.QualityDAO;
import com.esferalia.aon.occam.impl.jooq.dao.StockDAO;
import com.esferalia.aon.occam.impl.jooq.dao.WarehouseDAO;
import com.esferalia.aon.occam.impl.jooq.dao.delivery.DeliveryInfoDAO;

public class WarehouseImpl implements IWarehouse {

	
	@Override
	public Stream<Warehouse> getWarehouseStream(AONContext ctx, WarehouseFilter filter){
		return ctx.getDslContext().transactionResult(configuration ->
				WarehouseDAO.getStream(ctx, filter));
	}
	
	@Override
	public Warehouse getWarehouse(AONContext ctx, WarehouseFilter filter){
		return ctx.getDslContext().transactionResult(configuration ->
				WarehouseDAO.get(ctx, filter));
	}

	@Override
	public Warehouse saveWarehouse(AONContext ctx, Warehouse warehouse){
		return ctx.getDslContext().transactionResult(configuration ->
				WarehouseDAO.save(ctx, warehouse));
	}
	
	@Override
	public void deleteWarehouse(AONContext ctx, Integer warehouseId){
		ctx.getDslContext().transaction(configuration ->
				WarehouseDAO.delete(ctx, warehouseId));
	}

	
	@Override
	public IncomeDetail getLastIncomeDetail(AONContext ctx, OldItem item, Integer workplaceId, Integer warehouseId) {
		return IncomeDAO.getLastIncomeDetail(ctx, item, workplaceId, warehouseId);
	}
	
	@Override
	public IncomeDetail getLastIncomeDetailUntilDate(AONContext ctx, OldItem item, Integer workplaceId, Integer warehouseId, Date date) {
		return IncomeDAO.getLastIncomeDetailUntilDate(ctx, item, workplaceId, warehouseId, date);
	}
	
	@Override
	public LinkedList<IncomeDetail> getLastIncomeDetailList(AONContext ctx, OldItem item, Date startDate, Integer workplaceId, Integer warehouseId) {
		return IncomeDAO.getLastIncomeDetailList(ctx, item, startDate, workplaceId, warehouseId);
	}
	
	
	@Override
	public LinkedList<IncomeDetail> getLastIncomeDetailListUntilDate(AONContext ctx, OldItem item, Date startDate, Integer workplaceId, Integer warehouseId, Date date) {
		return IncomeDAO.getLastIncomeDetailListUntilDate(ctx, item, startDate, workplaceId, warehouseId, date);
	}
	
	@Override
	public LinkedList<IncomeDetail> getIncomeDetailList(AONContext ctx, OldItem item, Integer workplaceId, Integer warehouseId) {
		return IncomeDAO.getIncomeDetailList(ctx, item, workplaceId, warehouseId);
	}

	@Override
	public LinkedList<IncomeDetail> getIncomeDetailListUntilDate(AONContext ctx, OldItem item, Integer workplaceId, Integer warehouseId, Date date) {
		return IncomeDAO.getIncomeDetailListUntilDate(ctx, item, workplaceId, warehouseId, date);
	}
	
	@Override
	public InventoryDetail getInventoryDetail(AONContext ctx, Integer domain, Integer id) {
		return InventoryDAO.getInventoryDetail(ctx, domain, id);
	}
	
	@Override
	public InventoryDetail saveInventoryDetail(AONContext ctx, InventoryDetail inventoryDetail) {
		return InventoryDAO.saveInventoryDetail(ctx, inventoryDetail);
	}
	
	@Override
	public LinkedList<InventoryDetail> getInventoryDetailList(AONContext ctx, Integer inventoryId) {
		return InventoryDAO.getInventoryDetailList(ctx,inventoryId);
	}
	
	@Override
	public Stream<InventoryDetail> getInventoryDetailStream(AONContext ctx, InventoryDetailFilter filter) {
		return InventoryDAO.getInventoryDetailStream(ctx, filter);
	}
	
	@Override
	public void updateInventoryDetail(AONContext ctx, InventoryDetail inventoryDetail) {
		ctx.getDslContext().transaction(configuration -> 
		InventoryDAO.updateInventoryDetail(ctx, inventoryDetail));
	}
	
	@Override
	public void updateZeroInventoryDetail(AONContext ctx) {
		ctx.getDslContext().transaction(configuration -> 
		InventoryDAO.updateZeroInventoryDetail(ctx));
	}

	@Override
	public void updateInventory(AONContext ctx, Inventory inventory) {
		ctx.getDslContext().transaction(configuration -> 
		InventoryDAO.updateInventory(ctx, inventory));
	}
	
	@Override
	public Stream<WarehouseTransfer> getWarehouseTransferStream(AONContext ctx, WarehouseTransferFilter filter) {
		return ctx.getDslContext().transactionResult(configuration -> 
			WarehouseDAO.getWarehouseTransferStream(ctx, filter));		
	}
	
	@Override
	public Integer insertWarehouseTransfer(AONContext ctx, WarehouseTransfer warehouseTransfer) {
		return ctx.getDslContext().transactionResult(configuration -> 
			WarehouseDAO.insertWarehouseTransfer(ctx, warehouseTransfer));		
	}
	
	@Override
	public void updateWarehouseTransfer(AONContext ctx, WarehouseTransfer warehouseTransfer) {
		ctx.getDslContext().transaction(configuration -> 
			WarehouseDAO.updateWarehouseTransfer(ctx, warehouseTransfer));		
	}
	
	@Override
	public Integer insertWarehouseTransferDetail(AONContext ctx, WarehouseTransferDetail warehouseTransferDetail) {
		return ctx.getDslContext().transactionResult(configuration -> 
			WarehouseDAO.insertWarehouseTransferDetail(ctx, warehouseTransferDetail));		
	}
	
	@Override
	public Integer insertWarehouseTransferDetail(AONContext ctx, Stream<WarehouseTransferDetail> warehouseTransferDetail) {
		return ctx.getDslContext().transactionResult(configuration -> 
			WarehouseDAO.insertWarehouseTransferDetail(ctx, warehouseTransferDetail));		
	}
	
	@Override
	public void deleteWarehouseTransfer(AONContext ctx, WarehouseTransferFilter filter) {
		ctx.getDslContext().transaction(configuration -> 
			WarehouseDAO.deleteWarehouseTransfer(ctx, filter));		
	}

	@Override
	public Inventory getInventory(AONContext ctx, Integer domain, Integer id, Options...options){
		return ctx.getDslContext().transactionResult(configuration -> 
				InventoryDAO.get(ctx, domain, id, options));
	}
	
	@Override
	public List<Inventory> getInventoryList(AONContext ctx, InventoryFilter filter, Options...options){
		return ctx.getDslContext().transactionResult(configuration -> 
				InventoryDAO.getList(ctx, filter, options));
	}

	@Override
	public LinkedList<Inventory> getTwoLastInventory(AONContext ctx, Integer warehouseId) {
		return ctx.getDslContext().transactionResult(configuration -> 
			InventoryDAO.getTwoLastInventory(ctx, warehouseId));
	}

	@Override
	public void deleteInventory(AONContext ctx, Integer inventoryId) {
		ctx.getDslContext().transaction(configuration -> 
			InventoryDAO.deleteInventory(ctx, inventoryId));		
	}
	
	@Override
	public Department getDepartment(AONContext ctx, Integer workplaceId, DepartmentFilter filter){
		return ctx.getDslContext().transactionResult(configuration -> 
				WarehouseDAO.getDepartment(ctx, workplaceId, filter));
	}
	
	@Override
	public LinkedList<Department> getDepartmentList(AONContext ctx, Integer workplaceId, DepartmentFilter filter){
		return ctx.getDslContext().transactionResult(configuration -> 
				WarehouseDAO.getDepartmentList(ctx, workplaceId, filter));
	}

	@Override
	public Stock getStock(AONContext ctx, StockFilter filter){
		return ctx.getDslContext().transactionResult(configuration ->
			StockDAO.get(ctx, filter));
	}
	
	@Override
	public Stream<Stock> getStockStream(AONContext ctx, StockFilter filter){
		return ctx.getDslContext().transactionResult(configuration ->
			StockDAO.getStream(ctx, filter));
	}

	@Override
	public Integer getWarehouseTransferNextNumber(AONContext ctx, String serie) {
		return ctx.getDslContext().transactionResult(configuration -> 
			WarehouseDAO.getWarehouseTransferNextNumber(ctx, serie));
	}
	
	@Override
	public Stream<WarehouseTransferDetail> getWarehouseTransferDetailStream(AONContext ctx,
			WarehouseTransferFilter filter, ProductFilter pFilter, ItemFilter iFilter) {
		return ctx.getDslContext().transactionResult(configuration -> 
			WarehouseDAO.getWarehouseTransferDetailStream(ctx, filter, pFilter, iFilter));	
	}
	
	// ------------------ CARRIER PACKING
	
	@Override
	public List<CarrierPacking> getCarrierPackingList(AONContext ctx, CarrierPackingFilter filter, Options...options){
		return ctx.getDslContext().transactionResult(configuration ->
			CarrierPackingDAO.getList(ctx, filter, options));
	}

	@Override
	public Stream<String> getCarrierPackingSeries(AONContext ctx){
		return ctx.getDslContext().transactionResult(configuration ->
			WarehouseDAO.getCarrierPackingSeries(ctx));
	}
	
	@Override
	public Stream<CarrierPacking> getCarrierPackingStream(AONContext ctx, CarrierPackingFilter filter){
		return ctx.getDslContext().transactionResult(configuration ->
			WarehouseDAO.getCarrierPackingStream(ctx, filter));
	}


	@Override
	public CarrierPacking saveCarrierPacking(AONContext ctx, CarrierPacking carrierPacking) {
		return ctx.getDslContext().transactionResult(configuration ->
			CarrierPackingDAO.save(ctx, carrierPacking));
	}
	
	@Override
	public Integer insertCarrierPacking(AONContext ctx, CarrierPacking carrierPacking) {
		return ctx.getDslContext().transactionResult(configuration ->
			WarehouseDAO.insertCarrierPacking(ctx, carrierPacking));
	}


	@Override
	public CarrierPacking updateCarrierPacking(AONContext ctx, CarrierPacking carrierPacking,
			CarrierPackingFilter filter) {
		return ctx.getDslContext().transactionResult(configuration ->
			WarehouseDAO.updateCarrierPacking(ctx, carrierPacking, filter));
	}


	@Override
	public void deleteCarrierPacking(AONContext ctx, CarrierPackingFilter filter) {
		ctx.getDslContext().transaction(configuration ->
			WarehouseDAO.deleteCarrierPacking(ctx, filter));
	}
	
	// ------------------ ELABORATION
	
	@Override
	public Integer getElaborationNextNumber(AONContext ctx, String series) {
		return ctx.getDslContext().transactionResult(configuration ->
			ElaborationDAO.getNextNumber(ctx, series));
	}
	
	@Override
	public Elaboration getElaboration(AONContext ctx, Integer id){
		return ctx.getDslContext().transactionResult(configuration ->
			ElaborationDAO.getElaboration(ctx, id));
	}
	
	@Override
	public Elaboration getElaboration(AONContext ctx, ElaborationFilter filter, Options...options){
		return ctx.getDslContext().transactionResult(configuration ->
			ElaborationDAO.get(ctx, filter, options));
	}

	@Override
	public Stream<Elaboration> getElaborationStream(AONContext ctx, ElaborationFilter filter){
		return ctx.getDslContext().transactionResult(configuration ->
			ElaborationDAO.getElaborationStream(ctx, filter));
	}
	
	@Override
	public List<Elaboration> getElaborationList(AONContext ctx, ElaborationFilter filter, Options... options){
		return ctx.getDslContext().transactionResult(configuration ->
			ElaborationDAO.getList(ctx, filter, options));
	}
	
	@Override
	public Elaboration saveElaboration(AONContext ctx, Elaboration elaboration) {
		return ctx.getDslContext().transactionResult(configuration ->
			ElaborationDAO.save(ctx, elaboration));
	}
	
	@Override
	public Elaboration saveElaborationSerial(AONContext ctx, Elaboration elaboration) {
		return ctx.getDslContext().transactionResult(configuration ->
			ElaborationDAO.saveSerial(ctx, elaboration));
	}
	
	@Override
	public Integer insertElaboration(AONContext ctx, Elaboration elaboration) {
		return ctx.getDslContext().transactionResult(configuration ->
			ElaborationDAO.insertElaboration(ctx, elaboration));
	}

	@Override
	public Elaboration updateElaboration(AONContext ctx, Elaboration elaboration) {
		return ctx.getDslContext().transactionResult(configuration ->
			ElaborationDAO.updateElaboration(ctx, elaboration));
	}
	
	@Override
	public Elaboration deleteElaboration(AONContext ctx, Integer id) {
		return ctx.getDslContext().transactionResult(configuration ->
			ElaborationDAO.deleteElaboration(ctx, id));
	}
	
	// ---------- ELABORATION DETAIL
	
	@Override
	public ElaborationDetail getElaborationDetail(AONContext ctx, Integer id) {
		return ctx.getDslContext().transactionResult(configuration ->
			ElaborationDAO.getElaborationDetail(ctx, id));
	}
	
	@Override
	public List<ElaborationDetail> getElaborationDetailList(AONContext ctx, Integer id){
		return ctx.getDslContext().transactionResult(configuration ->
			ElaborationDAO.getElaborationDetailList(ctx, id));
	}
	@Override
	public List<ElaborationDetail> getElaborationDetailList(AONContext ctx, ElaborationDetailFilter filter){
		return ctx.getDslContext().transactionResult(configuration ->
		ElaborationDAO.getElaborationDetailList(ctx, filter));
	}

	@Override
	public ElaborationDetail saveElaborationDetail(AONContext ctx, ElaborationDetail detail, boolean updateStock) {
		return ctx.getDslContext().transactionResult(configuration ->
			ElaborationDetailDAO.save(ctx, detail, updateStock));
	}
	
	@Override
	public ElaborationDetail deleteElaborationDetail(AONContext ctx, Integer id, boolean updateStock) {
		return ctx.getDslContext().transactionResult(configuration ->
			ElaborationDetailDAO.delete(ctx, id, updateStock));
	}

	// ---------- ELABORATION DETAIL COMPOSITION
	
	@Override
	public ElaborationDetailComposition getElaborationDetailComposition(AONContext ctx, Integer id) {
		return ctx.getDslContext().transactionResult(configuration ->
		ElaborationDetailCompositionDAO.getElaborationDetailComposition(ctx, id));
	}
	
	@Override
	public List<ElaborationDetailComposition> getElaborationDetailCompositionList(AONContext ctx, Integer elaborationDetailId){
		return ctx.getDslContext().transactionResult(configuration -> 
			ElaborationDetailCompositionDAO.getListByElaborationDetail(ctx, elaborationDetailId));
	}
	@Override
	public List<ElaborationDetailComposition> getElaborationDetailCompositionList(AONContext ctx, ElaborationDetailCompositionFilter filter){
		return ctx.getDslContext().transactionResult(configuration ->
			ElaborationDetailCompositionDAO.getList(ctx, filter));
	}
	
	@Override
	public Integer insertElaborationDetailComposition(AONContext ctx, ElaborationDetailComposition composition, boolean updateStock) {
		return ctx.getDslContext().transactionResult(configuration ->
			ElaborationDetailCompositionDAO.insertElaborationDetailComposition(ctx, composition));
	}
	
	@Override
	public ElaborationDetailComposition updateElaborationDetailComposition(AONContext ctx, ElaborationDetailComposition composition, boolean updateStock) {
		return ctx.getDslContext().transactionResult(configuration ->
			ElaborationDetailCompositionDAO.updateElaborationDetailComposition(ctx, composition, updateStock));
	}

	@Override
	public ElaborationDetailComposition deleteElaborationDetailComposition(AONContext ctx, Integer id, boolean updateStock) {
		return ctx.getDslContext().transactionResult(configuration ->
			ElaborationDetailCompositionDAO.deleteElaborationDetailComposition(ctx, id, updateStock));
	}
	
	// ---------- ELABORATION PACKAGE
	
	@Override
	public ElaborationDetail deleteElaborationPackage(AONContext ctx, Integer id) {
		return ctx.getDslContext().transactionResult(configuration ->
			ElaborationPackageDAO.delete(ctx, id));
	}
	
	
	// -------------------------- INCOME
	@Override
	public Stream<Income> getIncomeStream(AONContext ctx, IncomeFilter filter) {
		return ctx.getDslContext().transactionResult(configuration ->
			IncomeDAO.getIncomeStream(ctx, filter));
	}


	@Override
	public Stream<IncomeDetail> getIncomeDetailStream(AONContext ctx, IncomeDetailFilter filter) {
		return ctx.getDslContext().transactionResult(configuration ->
			IncomeDAO.getIncomeDetailStream(ctx, filter));
	}
	
	@Override
	public Stream<IncomeDetail> getIncomeDetailStream(AONContext ctx, IncomeFilter incomeFilter, IncomeDetailFilter detailFilter,
			ProductFilter productFilter, ItemFilter itemFilter) {
		return ctx.getDslContext().transactionResult(configuration ->
			IncomeDAO.getIncomeDetailStream(ctx, incomeFilter, detailFilter, productFilter, itemFilter));
	}

	@Override
	public Optional<Income> insertIncome(AONContext ctx, Income income) {
		return ctx.getDslContext().transactionResult(configuration ->
			IncomeDAO.insertIncome(ctx, income));
	}
	
	@Override
	public Optional<IncomeDetail> insertIncomeDetail(AONContext ctx, IncomeDetail incomeDetail) {
		return ctx.getDslContext().transactionResult(configuration ->
			IncomeDAO.insertIncomeDetail(ctx, incomeDetail));
	}

	@Override
	public Optional<IncomeDetail> updateIncomeDetail(AONContext ctx, IncomeDetail incomeDetail) {
		return ctx.getDslContext().transactionResult(configuration ->
		IncomeDAO.updateIncomeDetail(ctx, incomeDetail));	}

	@Override
	public Optional<IncomeDetail> deleteIncomeDetail(AONContext ctx, Integer id) {
		return ctx.getDslContext().transactionResult(configuration ->
			IncomeDAO.deleteIncomeDetail(ctx, id));
	}

	@Override
	public Optional<Income> deleteIncome(AONContext ctx, Integer id) {
		return ctx.getDslContext().transactionResult(configuration ->
		IncomeDAO.deleteIncome(ctx, id));
	}

	// -------------------------- STOCK
	
	@Override
	public Optional<Stock> insertStock(AONContext ctx, Stock stock) {
		return ctx.getDslContext().transactionResult(configuration -> 
			StockDAO.insert(ctx, stock));
	}
	
	@Override
	public Optional<Stock> updateStock(AONContext ctx, Stock stock) {
		return ctx.getDslContext().transactionResult(configuration -> 
				StockDAO.update(ctx, stock));
	}

	@Override
	public Optional<Stock> deleteStock(AONContext ctx, Integer stockId) {
		return ctx.getDslContext().transactionResult(configuration -> 
		StockDAO.delete(ctx, stockId));
	}
	
	@Override
	public Stock saveStock(AONContext ctx, Stock stock) {
		return ctx.getDslContext().transactionResult(configuration -> 
			StockDAO.save(ctx, stock));
	}
	
	@Override
	public Stock addPackageStock(AONContext ctx, Integer item, Integer warehouse) {
		return ctx.getDslContext().transactionResult(configuration -> 
			PackagingDAO.addPackageStock(ctx, item, warehouse));
	}
	
	@Override
	public void movePackageStock(AONContext ctx, Integer item, Integer sourceWarehouse, Integer destinyWarehouse) {
		ctx.getDslContext().transaction(configuration -> PackagingDAO.movePackageStock(ctx, item, sourceWarehouse, destinyWarehouse));
	}
	
	
	@Override
	public Stock addStock(AONContext ctx, Integer itemId, Integer warehouse, double quantity) {
		return ctx.getDslContext().transactionResult(configuration -> 
			StockDAO.add(ctx, itemId, warehouse, quantity));
	}

	@Override
	public Stock subtractStock(AONContext ctx, Integer itemId, Integer warehouse, double quantity) {
		return ctx.getDslContext().transactionResult(configuration -> 
			StockDAO.subtract(ctx, itemId, warehouse, quantity));
	}
	
	// -------------------------- DELIVERY
	@Override
	public Stream<DeliveryDetail> getDeliveryDetailStream(AONContext ctx, DeliveryFilter deliveryFilter, DeliveryDetailFilter detailFilter,
			ProductFilter productFilter, ItemFilter itemFilter) {
		return ctx.getDslContext().transactionResult(configuration ->
			DeliveryDAO.getDeliveryDetailStream(ctx, deliveryFilter, detailFilter, productFilter, itemFilter));
	}

	@Override
	public Stream<UdapaQuality> getUdapaQualityStream(AONContext ctx, Map<String, String[]> map) {
		return ctx.getDslContext().transactionResult(configuration ->
			QualityDAO.getUdapaQualityStream(ctx,map));
	}

	@Override
	public Stream<PaturpatQuality> getPaturpatQualityStream(AONContext ctx, Map<String, String[]> map) {
		return ctx.getDslContext().transactionResult(configuration -> 
			QualityDAO.getPaturpatQualityStream(ctx, map));
	}

	@Override
	public Packaging getPackaging(AONContext ctx, String barcode) {
		return ctx.getDslContext().transactionResult(configuration -> 
			PackagingDAO.get(ctx, barcode));
	}

	@Override
	public List<Packaging> savePackaging(AONContext ctx, Packaging packaging) {
		return ctx.getDslContext().transactionResult(configuration ->
			PackagingDAO.save(ctx, packaging));
	}
	
	@Override
	public DeliveryPackaging getDeliveryPackaging(AONContext ctx, String sscc, Integer delivery, Integer product) {
		return ctx.getDslContext().transactionResult(configuration -> 
			PackagingDAO.getDeliveryPackaging(ctx, sscc, delivery, product));
	}
	
	@Override
	public PackagingDelivery saveDeliveryPackaging(AONContext ctx, PackagingDelivery packaging) {
		return ctx.getDslContext().transactionResult(configuration ->
			PackagingDAO.saveDeliveryPackaging(ctx, packaging));
	}
	
	@Override
	public void acceptDeliveryPackaging(AONContext ctx, Integer deliveryId) {
		ctx.getDslContext().transaction(configuration -> PackagingDAO.acceptDeliveryPackaging(ctx, deliveryId));
	}
	
	@Override
	public void deleteDeliveryPackaging(AONContext ctx, Integer deliveryId, String sscc) {
		ctx.getDslContext().transaction(configuration -> PackagingDAO.deleteDeliveryPackaging(ctx, deliveryId, sscc));
	}

	@Override
	public void subtractDeliveryPackagingComposition(AONContext ctx, Integer deliveryId, ItemComposition composition, String destiny, Double quantity, boolean skipDestiny) {
		ctx.getDslContext().transaction(configuration -> PackagingDAO.subtractDeliveryPackagingComposition(ctx, deliveryId, composition, destiny, quantity, skipDestiny));
	}
	
	@Override
	public void addDeliveryPackagingComposition(AONContext ctx, Integer deliveryId, ItemComposition composition, String source, Double quantity, boolean skipSource) {
		ctx.getDslContext().transaction(configuration -> PackagingDAO.addDeliveryPackagingComposition(ctx, deliveryId, composition, source, quantity, skipSource));
	}

	// DELIVERY
	
	@Override
	public Delivery getDeliveryByPackage(AONContext ctx, Integer itemPackageId, Options...options) {
		return ctx.getDslContext().transactionResult(
				configuration -> DeliveryDAO.getByPackage(ctx, itemPackageId, options));
	}
	
	// DELIVERY INFO
	
	@Override
	public DeliveryInfo getDeliveryInfo(AONContext ctx, DeliveryInfoFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> DeliveryInfoDAO.get(ctx, filter));
	}
	
	@Override
	public DeliveryInfo saveDeliveryInfo(AONContext ctx, DeliveryInfo deliveryInfo) {
		return ctx.getDslContext().transactionResult(
				configuration -> DeliveryInfoDAO.save(ctx, deliveryInfo));				
	}

	@Override
	public void deleteDeliveryInfo(AONContext ctx, Integer deliveryId) {
		ctx.getDslContext().transaction(
				configuration -> DeliveryInfoDAO.delete(ctx, f -> f.getDeliveryProperty().eq(deliveryId)));
	}
	
	@Override
	public void deletePackage(AONContext ctx, Integer itemId) {
		ctx.getDslContext().transaction(
				configuration -> PackagingDAO.deletePackage(ctx, itemId));
	}
	
	@Override
	public void deletePackage(AONContext ctx, String sscc) {
		ctx.getDslContext().transaction(configuration -> PackagingDAO.deletePackage(ctx, sscc));
	}
	
	@Override
	public void adjustPackageComposition(AONContext ctx, ItemComposition ic) {
		ctx.getDslContext().transaction(configuration -> PackagingDAO.adjustPackageComposition(ctx, ic));
	}

}
