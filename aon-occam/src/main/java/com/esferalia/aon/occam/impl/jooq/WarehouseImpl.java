package com.esferalia.aon.occam.impl.jooq;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IWarehouse;
import com.esferalia.aon.occam.api.Options;
import com.esferalia.aon.occam.api.model.Elaboration;
import com.esferalia.aon.occam.api.model.ElaborationDetail;
import com.esferalia.aon.occam.api.model.ElaborationDetailComposition;
import com.esferalia.aon.occam.api.model.Filter.CarrierPackingFilter;
import com.esferalia.aon.occam.api.model.Filter.DeliveryDetailFilter;
import com.esferalia.aon.occam.api.model.Filter.DeliveryFilter;
import com.esferalia.aon.occam.api.model.Filter.DepartmentFilter;
import com.esferalia.aon.occam.api.model.Filter.ElaborationDetailCompositionFilter;
import com.esferalia.aon.occam.api.model.Filter.ElaborationDetailFilter;
import com.esferalia.aon.occam.api.model.Filter.ElaborationFilter;
import com.esferalia.aon.occam.api.model.Filter.IncomeDetailFilter;
import com.esferalia.aon.occam.api.model.Filter.IncomeFilter;
import com.esferalia.aon.occam.api.model.Filter.InventoryDetailFilter;
import com.esferalia.aon.occam.api.model.Filter.ItemFilter;
import com.esferalia.aon.occam.api.model.Filter.ProductFilter;
import com.esferalia.aon.occam.api.model.Filter.SeriesFilter;
import com.esferalia.aon.occam.api.model.Filter.StockFilter;
import com.esferalia.aon.occam.api.model.Filter.WarehouseFilter;
import com.esferalia.aon.occam.api.model.Filter.WarehouseTransferFilter;
import com.esferalia.aon.occam.api.model.product.OldItem;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPacking;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryDetail;
import com.esferalia.aon.occam.api.model.warehouse.Department;
import com.esferalia.aon.occam.api.model.warehouse.Income;
import com.esferalia.aon.occam.api.model.warehouse.IncomeDetail;
import com.esferalia.aon.occam.api.model.warehouse.Inventory;
import com.esferalia.aon.occam.api.model.warehouse.InventoryDetail;
import com.esferalia.aon.occam.api.model.warehouse.Packaging;
import com.esferalia.aon.occam.api.model.warehouse.PaturpatQuality;
import com.esferalia.aon.occam.api.model.warehouse.Series;
import com.esferalia.aon.occam.api.model.warehouse.Stock;
import com.esferalia.aon.occam.api.model.warehouse.UdapaQuality;
import com.esferalia.aon.occam.api.model.warehouse.Warehouse;
import com.esferalia.aon.occam.api.model.warehouse.WarehouseTransfer;
import com.esferalia.aon.occam.api.model.warehouse.WarehouseTransferDetail;
import com.esferalia.aon.occam.impl.jooq.dao.DeliveryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ElaborationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.IncomeDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InventoryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PackagingDAO;
import com.esferalia.aon.occam.impl.jooq.dao.QualityDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SeriesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.WarehouseDAO;
import com.esferalia.aon.watson.server.AonDateUtils;

public class WarehouseImpl implements IWarehouse {

	
	@Override
	public Stream<Warehouse> getWarehouseStream(AONContext ctx, WarehouseFilter filter){
		return ctx.getDslContext().transactionResult(configuration ->
				WarehouseDAO.getWarehouseStream(ctx, filter));
	}
	
	@Override
	public Warehouse getWarehouse(AONContext ctx, WarehouseFilter filter){
		return ctx.getDslContext().transactionResult(configuration ->
				WarehouseDAO.getWarehouse(ctx, filter));
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
	public LinkedList<Inventory> getInventoryList(AONContext ctx, Date startDate, Date endDate){
		return ctx.getDslContext().transactionResult(configuration -> 
				InventoryDAO.getInventoryList(ctx, AonDateUtils.toSql(startDate), AonDateUtils.toSql(endDate)));
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
	public LinkedList<Series> getSeriesDeliveryList(AONContext ctx, Integer scopeId){
		return ctx.getDslContext().transactionResult(configuration ->
				SeriesDAO.getSeriesDeliveryList(ctx, scopeId));
	}
	
	@Override
	public Stream<Series> getSeriesStream(AONContext ctx, SeriesFilter filter){
		return ctx.getDslContext().transactionResult(configuration ->
				SeriesDAO.getSeries(ctx, filter));
	}
	
	@Override
	public Stream<Stock> getStockStream(AONContext ctx, StockFilter filter){
		return ctx.getDslContext().transactionResult(configuration ->
			WarehouseDAO.getStockStream(ctx, filter));
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
	public Elaboration getElaboration(AONContext ctx, Integer id){
		return ctx.getDslContext().transactionResult(configuration ->
			ElaborationDAO.getElaboration(ctx, id));
	}
	@Override
	public ElaborationDetail getElaborationDetail(AONContext ctx, Integer id) {
		return ctx.getDslContext().transactionResult(configuration ->
			ElaborationDAO.getElaborationDetail(ctx, id));
	}
	@Override
	public ElaborationDetailComposition getElaborationDetailComposition(AONContext ctx, Integer id) {
		return ctx.getDslContext().transactionResult(configuration ->
		ElaborationDAO.getElaborationDetailComposition(ctx, id));
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
	public List<ElaborationDetailComposition> getElaborationDetailCompositionList(AONContext ctx, Integer id){
		return ctx.getDslContext().transactionResult(configuration ->
		ElaborationDAO.getElaborationDetailCompositionList(ctx, id));
	}
	@Override
	public List<ElaborationDetailComposition> getElaborationDetailCompositionList(AONContext ctx, ElaborationDetailCompositionFilter filter){
		return ctx.getDslContext().transactionResult(configuration ->
		ElaborationDAO.getElaborationDetailCompositionList(ctx, filter));
	}
	
	@Override
	public Elaboration saveElaboration(AONContext ctx, Elaboration elaboration) {
		return ctx.getDslContext().transactionResult(configuration ->
			ElaborationDAO.save(ctx, elaboration));
	}
	
	@Override
	public Integer insertElaboration(AONContext ctx, Elaboration elaboration) {
		return ctx.getDslContext().transactionResult(configuration ->
			ElaborationDAO.insertElaboration(ctx, elaboration));
	}
	
	@Override
	public Integer insertElaborationDetail(AONContext ctx,
			ElaborationDetail detail) {
		return ctx.getDslContext().transactionResult(configuration ->
			ElaborationDAO.insertElaborationDetail(ctx, detail));
	}
	@Override
	public Integer insertElaborationDetailComposition(AONContext ctx, ElaborationDetailComposition composition) {
		return ctx.getDslContext().transactionResult(configuration ->
			ElaborationDAO.insertElaborationDetailComposition(ctx, composition));
	}
	
	
	@Override
	public Elaboration updateElaboration(AONContext ctx, Elaboration elaboration) {
		return (Elaboration) ctx.getDslContext().transactionResult(configuration ->
			ElaborationDAO.updateElaboration(ctx, elaboration));
	}
	@Override
	public ElaborationDetail updateElaborationDetail(AONContext ctx,
			ElaborationDetail detail) {
		return (ElaborationDetail) ctx.getDslContext().transactionResult(configuration ->
			ElaborationDAO.updateElaborationDetail(ctx, detail));
	}
	@Override
	public ElaborationDetailComposition updateElaborationDetailComposition(AONContext ctx,
			ElaborationDetailComposition composition) {
		return (ElaborationDetailComposition) ctx.getDslContext().transactionResult(configuration ->
			ElaborationDAO.updateElaborationDetailComposition(ctx, composition));
	}

	
	@Override
	public Elaboration deleteElaboration(AONContext ctx, Integer id) {
		return ctx.getDslContext().transactionResult(configuration ->
			ElaborationDAO.deleteElaboration(ctx, id));
	}
	@Override
	public ElaborationDetail deleteElaborationDetail(AONContext ctx, Integer id) {
		return ctx.getDslContext().transactionResult(configuration ->
			ElaborationDAO.deleteElaborationDetail(ctx, id));
	}
	@Override
	public ElaborationDetail deleteElaborationDetail(AONContext ctx, ElaborationDetailFilter filter) {
		return ctx.getDslContext().transactionResult(configuration ->
			ElaborationDAO.deleteElaborationDetail(ctx, filter));
	}
	@Override
	public ElaborationDetailComposition deleteElaborationDetailComposition(AONContext ctx,
			ElaborationDetailCompositionFilter filter) {
		return ctx.getDslContext().transactionResult(configuration ->
			ElaborationDAO.deleteElaborationDetailComposition(ctx, filter));
	}
	
	@Override
	public Integer getElaborationNextNumber(AONContext ctx, String series) {
		return ctx.getDslContext().transactionResult(configuration ->
			ElaborationDAO.getNextNumber(ctx, series));
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
	public Optional<Stock> insertStock(AONContext ctx, Stock stock) {
		return ctx.getDslContext().transactionResult(configuration -> 
				WarehouseDAO.insertStock(ctx, stock));
	}
	
	@Override
	public Optional<Stock> updateStock(AONContext ctx, Stock stock) {
		return ctx.getDslContext().transactionResult(configuration -> 
				WarehouseDAO.updateStock(ctx, stock));
	}

	@Override
	public Optional<Stock> deleteStock(AONContext ctx, Integer stockId) {
		return ctx.getDslContext().transactionResult(configuration -> 
		WarehouseDAO.deleteStock(ctx, stockId));
	}

	@Override
	public Optional<Income> deleteIncome(AONContext ctx, Integer id) {
		return ctx.getDslContext().transactionResult(configuration ->
		IncomeDAO.deleteIncome(ctx, id));
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
	public Packaging savePackaging(AONContext ctx, Packaging packaging) {
		return ctx.getDslContext().transactionResult(configuration ->
			PackagingDAO.save(ctx, packaging));
	}
	
}
