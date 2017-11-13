package com.code.aon.ui.warehouse.controller;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.common.controller.IAuditableController;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.warehouse.util.OccamClassesTransform;
import com.code.aon.warehouse.Inventory;
import com.code.aon.warehouse.InventoryDetail;
import com.code.aon.warehouse.Stock;
import com.code.aon.warehouse.Warehouse;
import com.code.aon.warehouse.WarehouseTransfer;
import com.code.aon.warehouse.enumeration.InventoryStatus;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.warehouse.IncomeDetail;
import com.esferalia.aon.occam.api.model.warehouse.Series;
import com.esferalia.aon.occam.api.model.warehouse.WarehouseTransferDetail;
import com.esferalia.aon.occam.api.model.warehouse.WarehouseTransferSource;

/**
 * Controller for Inventory.
 * 
 * @author Consulting & Development.
 * @since 1.0
 *
 */
public class InventoryController extends BasicController implements IAuditableController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(InventoryController.class.getName());
	
	private Warehouse warehouse; 
	private boolean initStock;
	private boolean showInventoryAdjustmentWindow;
	private boolean showAuditInfoWindow;
	private boolean showConfirmWindow;
	private boolean showSeriesNumberWindow;
	private boolean oneSeries;
	private String series;
	
	private String text;
	
	public Warehouse getWarehouse() {
		return warehouse;
	}
	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}

	public boolean isInitStock() {
		return initStock;
	}
	public void setInitStock(boolean initStock) {
		this.initStock = initStock;
	}
	
	public boolean isShowInventoryAdjustmentWindow() {
		return showInventoryAdjustmentWindow;
	}
	public void setShowInventoryAdjustmentWindow(boolean showInventoryAdjustmentWindow) {
		this.showInventoryAdjustmentWindow = showInventoryAdjustmentWindow;
	}
	
	public boolean isShowConfirmWindow() {
		return showConfirmWindow;
	}
	public void setShowConfirmWindow(boolean showConfirmWindow) {
		this.showConfirmWindow = showConfirmWindow;
	}
	
	public boolean isShowAuditInfoWindow() {
		return showAuditInfoWindow;
	}
	public void setShowAuditInfoWindow(boolean showAuditInfoWindow) {
		this.showAuditInfoWindow = showAuditInfoWindow;
	}
	
	public boolean isShowSeriesNumberWindow() {
		return showSeriesNumberWindow;
	}
	public void setShowSeriesNumberWindow(boolean showSeriesNumberWindow) {
		this.showSeriesNumberWindow = showSeriesNumberWindow;
	}
	
	public String getSeries() {
		return series;
	}
	public void setSeries(String series) {
		this.series = series;
	}
	public boolean isOneSeries() {
		return oneSeries;
	}
	public void setOneSeries(boolean oneSeries) {
		this.oneSeries = oneSeries;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public void onClosing(ActionEvent event) {
		setShowSeriesNumberWindow(false);
		closeValidation();
		try {
			closeInventary();
		} catch (Exception e) {
			String msg = "Imposible cerrar el inventario. [" + e.getMessage() + "]";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}
	
	public void onStartClosing(ActionEvent event) throws Exception {
		this.initStock = false;
		this.warehouse = null;
		super.onReset(event);
	}
	

	
	public boolean isOneSeries2(){
		String domainName = AonUtil.getDomainName();
		Integer domainId = DomainManager.getCurrentDomain();
		String user = AonUtil.getRemoteUser();
		if(user.contains("=")){
			Integer index = user.indexOf("=");
			user = user.substring(index + 1);
		}
		Integer userId = AON.getUser(domainName, domainId, user).getId();
		Integer[] array = AON.getUserScopes(domainName, domainId, user, userId);
		LinkedList<Series> list = AON.getSeriesList(domainName, domainId, user,
				f -> f.getActiveProperty().eq((byte) 1)
				.and(f.getDeliveryProperty().eq((byte)1))
				.and(f.getScopeProperty().in(array)));
		setOneSeries(list.size() <= 1);
		if(isOneSeries()) setSeries(list.size() > 0 ? list.get(0).getCode() : null);
		return isOneSeries();
	}
	
	public boolean isCloseButton1(){
		return isOneSeries2() || !isInitStock();
	}
	
	public boolean isCloseButton2(){
		return !isOneSeries2() && isInitStock();
	}
	
	public void onContinueClosing(ActionEvent event) throws Exception {
		if(initStock && !isOneSeries()) {
			setShowSeriesNumberWindow(true);
			IController controller = FormUtil.getController(IWarehouseConstants.WAREHOUSE_TRANSFER_CONTROLLER_NAME);
			controller.onReset(event);
		} else {
			if(initStock) ;
			onClosing(event);
		}
	}
	
	private com.esferalia.aon.occam.api.model.warehouse.WarehouseTransfer createWarehouseTransfer(String domainName, Integer domainId, String user, Integer warehouseId) {
		Integer number; String series;
		if(isOneSeries()){
			series = getSeries();
			number = AON.getWarehouseTransferNextNumber(domainName, domainId, user, getSeries());
		} else {
			WarehouseTransferController wtc = (WarehouseTransferController) AonUtil.getRegisteredBean(IWarehouseConstants.WAREHOUSE_TRANSFER_CONTROLLER_NAME);
			WarehouseTransfer _wt = (WarehouseTransfer) wtc.getTo();
			series = _wt.getSeries();
			number = _wt.getNumber();
		}
	
		com.esferalia.aon.occam.api.model.warehouse.WarehouseTransfer wt = new com.esferalia.aon.occam.api.model.warehouse.WarehouseTransfer()
			.setDomain(domainId)
			.setSeries(series)
			.setNumber(number)
			.setIssueTime(new Date())
			.setComments("")
			.setSourceWarehouse(warehouse.getId())
			.setSource((byte) 0)
			.setSourceId(0);
		wt.setId(AON.insertWarehouseTransfer(domainName, domainId, user, wt));
		
		AON.insertWarehouseTransferDetail(domainName, domainId, user, 
			AON.getStockStream(domainName, domainId, user, f -> f.getWarehouseProperty().eq(warehouse.getId())).map(s -> {
				return new WarehouseTransferDetail()
					.setDomain(DomainManager.getCurrentDomain())
					.setItem(new com.esferalia.aon.occam.api.model.product.Item().setId(s.getItem()))
					.setQuantity(s.getQuantity())
					.setWarehouseTransfer(wt);
			})
		);
		return wt;
	}
	
	private void closeInventary() throws Exception{
		HibernateUtil.setCloseSession(false);
		HibernateUtil.setBeginTransaction(false);
		String sessionName = HibernateUtil.getSessionFactoryName(); 
		try{
			HibernateUtil.beginTransaction(sessionName);
			IManagerBean stockBean = BeanManager.getManagerBean(Stock.class);
			IManagerBean inventoryBean = BeanManager.getManagerBean(Inventory.class);
			IManagerBean inventoryDetailBean = BeanManager.getManagerBean(InventoryDetail.class);
			
			com.esferalia.aon.occam.api.model.warehouse.WarehouseTransfer wt = null;
			String domainName = AonUtil.getDomainName();
			Integer domainId = DomainManager.getCurrentDomain();
			String user = AonUtil.getRemoteUser();
			
			Session session = HibernateUtil.getSession(sessionName);
			if (initStock){
				wt = createWarehouseTransfer(domainName, domainId, user, warehouse.getId());

				Criteria c = new Criteria();
				c.addEqualExpression(stockBean.getFieldName(IEntityAlias.STOCK_WAREHOUSE_ID), warehouse.getId());
				Iterator<?> initStockListIter = stockBean.getList(c).iterator();
				while (initStockListIter.hasNext()){
					Stock initStock = (Stock) initStockListIter.next();
					initStock.setQuantity(0.0);
					stockBean.update(initStock);
				}
			}
			
			Inventory inventory = new Inventory();
			inventory.setStatus(InventoryStatus.OPEN);
			inventory.setInventoryDate(((Inventory)this.getTo()).getInventoryDate());
			inventory.setDescription(((Inventory)this.getTo()).getDescription());
			
			inventory.setWarehouse(warehouse);
			inventory = (Inventory) inventoryBean.insert(inventory);
			
			com.esferalia.aon.occam.api.model.ApplicationParameter ap = AON.getApplicationParamenter(domainName, domainId, user, com.esferalia.aon.occam.api.model.type.AppParam.AON_PRODUCT_VALUATION_METHOD);
			
	        Query q = session.createQuery(
	                " select item, sum(stock.quantity), item.id " +
	                " from Item as item, Stock as stock " +
	                " where stock.item=item.id " +
	                " and stock.warehouse=" + warehouse.getId() +
	                " and " + DomainManager.getSQLWhereClause("stock.domain") +
	                " and (item.product.serializable = 0 or item.serialNumber is not null) " +
	                " group by item.id " +
	                " order by item.detail");
			Iterator<?> iter = q.list().iterator();
			while (iter.hasNext()){
				Integer workplaceId = null;
				if(inventory.getWarehouse().getWorkPlace() != null)
					workplaceId = inventory.getWarehouse().getWorkPlace().getId();
				InventoryDetail inventoryDetail = new InventoryDetail();
				inventoryDetail.setInventory(inventory);
				Object[] o = (Object[]) iter.next();
				Item item = (Item) o[0];
				Double total = (Double) o[1];
				inventoryDetail.setItem(item);
				inventoryDetail.setRealQuantity(total);
				inventoryDetail.setActualQuantity(total);
				inventoryDetail.setCost(getCost(inventoryDetail, workplaceId, inventory.getWarehouse().getId(), inventory.getInventoryDate(), ap));
				inventoryDetail = (InventoryDetail) inventoryDetailBean.insert(inventoryDetail);
			}
			HibernateUtil.commitTransaction(sessionName);
			this.onEditSearch(null);
			getCriteria().addEqualExpression(inventoryBean.getFieldName(IEntityAlias.INVENTORY_ID), inventory.getId());
			this.onSearch(null);
			this.getModel().setRowIndex(0);
			this.onSelect(null);
			if(initStock) AON.updateWarehouseTransfer(domainName, domainId, user, 
					wt.setSource(WarehouseTransferSource.INVENTORY_INIT_STOCK.value()).setSourceId(inventory.getId()).setInventory(
					new com.esferalia.aon.occam.api.model.warehouse.Inventory().setId(inventory.getId())));

		} catch (Exception e) {
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {
			}
			throw e;
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(true);
			HibernateUtil.setBeginTransaction(true);
		}
	}

	private void closeValidation() {
		Date date = ((Inventory)this.getTo()).getInventoryDate();
		try {
			IManagerBean bean = BeanManager.getManagerBean(Inventory.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.INVENTORY_WAREHOUSE_ID), getWarehouse().getId());
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.INVENTORY_STATUS), InventoryStatus.OPEN);
			if (bean.getCount(criteria) > 0) {
				String message = AonUtil.getMessage(ICommonMessages.WAREHOUSE_INVENTORY_CLOSE_ERROR);
				AonUtil.addErrorMessage(message);
				throw new AbortProcessingException(message);					
			}

			criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.INVENTORY_WAREHOUSE_ID), getWarehouse().getId());
			criteria.addGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.INVENTORY_INVENTORY_DATE), date);
			if (bean.getCount(criteria) > 0) {
				String message = AonUtil.getMessage(ICommonMessages.WAREHOUSE_INVENTORY_DATE_ERROR);
				AonUtil.addErrorMessage(message);
				throw new AbortProcessingException(message);					
			}
		} catch ( ManagerBeanException e ) {
			LOGGER.error(e.getMessage(), e);
		}
	}	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<InventoryDetail> getDetails( Inventory inventory, boolean newElements ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(InventoryDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.INVENTORY_DETAIL_INVENTORY_ID), inventory.getId());
		String actualAlias = bean.getFieldName(IEntityAlias.INVENTORY_DETAIL_ACTUAL_QUANTITY);
		Expression expr1 = ExpressionUtilities.getIdentifierExpression(actualAlias);
		String realAlias = bean.getFieldName(IEntityAlias.INVENTORY_DETAIL_REAL_QUANTITY);
		if ( newElements ) {
			criteria.addExpression(ExpressionUtilities.getGreaterThanExpression(realAlias, expr1));
		} else {
			criteria.addExpression(ExpressionUtilities.getLessThanExpression(realAlias, expr1));
		}
		return (List) bean.getList(criteria);
	}
	
	private WarehouseTransfer getWarehouseTransfer( Inventory inventory, boolean newElements ) throws ManagerBeanException {
		WarehouseTransferController wtc = (WarehouseTransferController) AonUtil.getRegisteredBean(IWarehouseConstants.WAREHOUSE_TRANSFER_CONTROLLER_NAME);
		WarehouseTransfer _wt = (WarehouseTransfer) wtc.getTo();
		WarehouseTransfer wt = new WarehouseTransfer();
		wt.setSeries(_wt.getSeries());
		if ( _wt.getNumber() == 0 ) {
			_wt.setNumber(wtc.obtainMaxNumber(_wt.getSeries()));
		}
		wt.setNumber(_wt.getNumber());
		wt.setSecurityLevel(_wt.getSecurityLevel());
		wt.setInventory(inventory);
		wt.setIssueTime(inventory.getInventoryDate());
		if ( newElements ) {
			wt.setTargetWarehouse(inventory.getWarehouse());
		} else {
			wt.setSourceWarehouse(inventory.getWarehouse());
		}
		IManagerBean bean = BeanManager.getManagerBean(WarehouseTransfer.class);
		bean.insert(wt);
		_wt.setNumber(wtc.obtainMaxNumber(_wt.getSeries()));		
		return wt;
	}
	
	private void createWarehouseTransfer( Inventory inventory, boolean newElements ) throws ManagerBeanException {
		List<InventoryDetail> details = getDetails(inventory, newElements);
		if (! details.isEmpty() ) {
			WarehouseTransfer wt = getWarehouseTransfer(inventory, newElements);
			
			String domainName = AonUtil.getDomainName();
			Integer domainId = DomainManager.getCurrentDomain();
			String user = AonUtil.getRemoteUser();
			
			AON.insertWarehouseTransferDetail(domainName, domainId, user, 
				details.stream().map(detail -> {
					Optional<com.esferalia.aon.occam.api.model.warehouse.Stock> stck = AON.getStockStream(domainName, domainId, user, f -> f.getItemProperty().eq(detail.getItem().getId()))
							.findFirst();
					if(stck.isPresent())  {
						stck.get().setQuantity(Math.abs(detail.getActualQuantity()-detail.getRealQuantity()));
						AON.updateStock(domainName, domainId, user, stck.get());
					}
					return new WarehouseTransferDetail()
						.setDomain(detail.getDomain())
						.setItem(new com.esferalia.aon.occam.api.model.product.Item().setId(detail.getItem().getId()))
						.setQuantity(Math.abs(detail.getActualQuantity()-detail.getRealQuantity()))
						.setWarehouseTransfer(new com.esferalia.aon.occam.api.model.warehouse.WarehouseTransfer().setId(wt.getId()));
				})
			);
		}	
	}
	
	public void onStartAdjustment(ActionEvent event) {
		Inventory inventory = (Inventory) getTo();
		String domainName = AonUtil.getDomainName();
		Integer domainId = DomainManager.getCurrentDomain();
		String user = AonUtil.getRemoteUser();
		
		com.esferalia.aon.occam.api.model.ApplicationParameter ap = AON.getApplicationParamenter(domainName, domainId, user, com.esferalia.aon.occam.api.model.type.AppParam.AON_PRODUCT_VALUATION_METHOD);
		AON.getInventoryDetailStream(domainName, domainId, user, f -> f.getInventoryProperty().eq(inventory.getId()))
		.forEach(id -> {
			Integer workplaceId = inventory.getWarehouse().getWorkPlace() != null ? 
					workplaceId = inventory.getWarehouse().getWorkPlace().getId() : null;
			InventoryDetail inventoryDetail = OccamClassesTransform.getInventoryDetail(id);
			Double cost =  getCost(inventoryDetail, workplaceId, inventory.getWarehouse().getId(), inventory.getInventoryDate(), ap);
			inventoryDetail.setCost(cost);
			inventoryDetail.setInventory(inventory);
			try {
				getManagerBean().update(inventoryDetail);
			} catch (ManagerBeanException e) {
				e.printStackTrace();
			}
		});
		
		try {
			getManagerBean().update(inventory);
			
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
		
		setShowInventoryAdjustmentWindow(true);
		IController controller = FormUtil.getController(IWarehouseConstants.WAREHOUSE_TRANSFER_CONTROLLER_NAME);
		controller.onReset(event);
	}
	
	public boolean isShowRevert(){
		Inventory inventory = (Inventory) getTo();
		String domainName = AonUtil.getDomainName();
		Integer domainId = DomainManager.getCurrentDomain();
		String user = AonUtil.getRemoteUser();
	
		LinkedList<com.esferalia.aon.occam.api.model.warehouse.Inventory> list =  AON.getTwoLastInventory(domainName, domainId, user, inventory.getWarehouse().getId());
		
		return (inventory.isClosed() && list.getFirst().getId().equals(inventory.getId())) 
				|| (inventory.isClosed() && list.getLast().getId().equals(inventory.getId()) &&
						InventoryStatus.values()[list.getFirst().getStatus()].equals(InventoryStatus.OPEN));
	}
	public void onAdjustment(ActionEvent event) {
		Inventory inventory = (Inventory) getTo();
		try {
			createWarehouseTransfer(inventory, true);
			createWarehouseTransfer(inventory, false);
			inventory.setStatus(InventoryStatus.PROCESSED);
			getManagerBean().update(inventory);
		} catch ( ManagerBeanException e ) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	public void onStartRevert(ActionEvent event){
		setText("Estás seguro de revertir el cierre de inventario.");
		setShowConfirmWindow(true);
	}
	
	public void onRevert(ActionEvent event) {
		String s = "Inventario abierto, Traspasos asociados eliminados y Stock actualizado";
		if(!s.equals(getText())){
			Inventory inventory = (Inventory) getTo();
			String domainName = AonUtil.getDomainName();
			Integer domainId = DomainManager.getCurrentDomain();
			String user = AonUtil.getRemoteUser();
			
			AON.deleteWarehouseTransfer(domainName, domainId, user,
					f -> f.getInventoryProperty().eq(inventory.getId())
					.and(f.getSourceProperty().ne(WarehouseTransferSource.INVENTORY_INIT_STOCK.value())));
			//AON.updateInventory(domainName, domainId, user,
			//	OccamClassesTransform.getInventory(inventory));
			LinkedList<com.esferalia.aon.occam.api.model.warehouse.Inventory> list =  AON.getTwoLastInventory(domainName, domainId, user, inventory.getWarehouse().getId());
				
			if(list.getLast().getId().equals(inventory.getId()) &&
				InventoryStatus.values()[list.getFirst().getStatus()].equals(InventoryStatus.OPEN)) {
				AON.deleteInventory(domainName, domainId, user, list.getFirst().getId());
			}
			try {
				inventory.setStatus(InventoryStatus.OPEN);
				getManagerBean().update(inventory);
			} catch (ManagerBeanException e) {
				LOGGER.error(e.getMessage(), e);
			}	
			setShowInventoryAdjustmentWindow(false);
			setText(s);
		}
		else{
			setShowInventoryAdjustmentWindow(false);
			setShowConfirmWindow(false);
			initializeModel();
		}
	}
	
	public static Double getCost(InventoryDetail inventoryDetail, Integer workplaceId, Integer warehouseId, Date inventoryDate, com.esferalia.aon.occam.api.model.ApplicationParameter ap){
		switch ((ap != null && ap.getValue() != null) ? ap.getValue() : "0") {
			case "0": return inventoryDetail.getRealQuantity() != 0 ? inventoryDetail.getItem().getPurchasePrice() : 0.0;
			case "1": return getLastPurchasePrice(inventoryDetail.getItem(), inventoryDetail.getRealQuantity(), AonUtil.getRemoteUser(), workplaceId,warehouseId, inventoryDate);
			case "2": return getAveragePurchasePrice(inventoryDetail.getItem(), inventoryDetail.getRealQuantity(), AonUtil.getRemoteUser(), workplaceId,warehouseId, inventoryDate);
			case "3": return getFifoPrice(inventoryDetail.getItem(), inventoryDetail.getRealQuantity(), AonUtil.getRemoteUser(), workplaceId,warehouseId, inventoryDate);	
			default : return inventoryDetail.getItem().getPurchasePrice(); 
		}
	}

	public static Double getLastPurchasePrice(Item item, Double quantity, String user, Integer workplaceId, Integer warehouseId, Date inventoryDate){
		if(quantity == 0) return 0.0;
		if(item.getProduct().isInventoriable() && item.getProduct().isManufactured()) 
			return item.getPurchasePrice();
		
		String domainName = AonUtil.getDomainName();

		// COMPRAS (Albaranes)
		IncomeDetail incomeDetail = AON.getLastIncomeDetailUntilDate(domainName, item.getDomain(), user, OccamClassesTransform.getItem(item), workplaceId, warehouseId, inventoryDate);
	
		Double price1 = 0.0;
		Date date1 = new Date();
		if(incomeDetail.getId() != null){
			if(incomeDetail.getIncome().getIssueDate() != null) date1 = incomeDetail.getIncome().getIssueDate();
			if(incomeDetail.getPrice() != null) price1 = incomeDetail.getPrice();
		}
			
		// COMPRAS (Facturas)
		InvoiceDetail invoiceDetail = AON.getLastInvoiceDetailUntilDate(domainName, item.getDomain(), user, OccamClassesTransform.getItem(item), workplaceId, warehouseId, inventoryDate);
		Double price2 = 0.0;
		Date date2 = new Date();
		if(invoiceDetail.getId() != null){
			if(invoiceDetail.getInvoice().getIssueDate() != null) 
				date2 = invoiceDetail.getInvoice().getIssueDate();
			if(invoiceDetail.getPrice() != null) 
				price2 = invoiceDetail.getPrice();
		}
			
		if(incomeDetail.getId() == null && invoiceDetail.getId() == null) return item.getPurchasePrice();
		else if(incomeDetail.getId() == null) return price2;
		else if(invoiceDetail.getId() == null) return price1;
		else return date1.compareTo(date2) < 0 ? price1 : price2;
	}
	
	public static Double getAveragePurchasePrice(Item item, Double quantity, String user, Integer workplaceId, Integer warehouseId , Date inventoryDate){
		if(quantity == 0) return 0.0;
		if(item.getProduct().isInventoriable() && item.getProduct().isManufactured()) 
			return item.getPurchasePrice();
					
		String domainName = AonUtil.getDomainName();
		Integer domainId = item.getDomain();
		
		ApplicationParameter ap = AppParamUtil.getParameter(AppParam.AON_PRODUCT_AVERAGE_MONTHS);

		LinkedList<InvoiceDetail> invoiceList = AON.getLastInvoiceDetailListUntilDate(domainName, domainId, user, OccamClassesTransform.getItem(item), ap.getValue(), workplaceId, warehouseId, inventoryDate);
		LinkedList<IncomeDetail> incomeList = AON.getLastIncomeDetailListUntilDate(domainName, domainId, user, OccamClassesTransform.getItem(item), ap.getValue(), workplaceId, warehouseId, inventoryDate);
		
		Double invoiceSum = invoiceList.stream().mapToDouble(x -> x.getPrice() * (1 -(Double.parseDouble(x.getDiscountExpression())/100.0)) * Math.abs(x.getQuantity())).sum();
		Double incomeSum = incomeList.stream().mapToDouble(x -> x.getPrice() * (1 -(Double.parseDouble(x.getDiscountExpression())/100.0)) * Math.abs(x.getQuantity())).sum();
		Double sum = invoiceSum + incomeSum;
		Double invoiceQuantity = invoiceList.stream().mapToDouble(x -> Math.abs(x.getQuantity())).sum();
		Double incomeQuantity = incomeList.stream().mapToDouble(x -> Math.abs(x.getQuantity())).sum();
		Double totalQuantity = invoiceQuantity + incomeQuantity;
		
		if(totalQuantity == 0) return item.getPurchasePrice();
		return  sum / totalQuantity;
	}

	public static Double getFifoPrice(Item item, Double quantity, String user, Integer workplaceId, Integer warehouseId, Date inventoryDate){
		if(quantity == 0) return 0.0; 
		if((item.getProduct().isInventoriable() && item.getProduct().isManufactured())) 
			return item.getPurchasePrice();
		
		String domainName = AonUtil.getDomainName();
		Integer domainId = item.getDomain();
		LinkedList<InvoiceDetail> invoiceList = AON.getInvoiceDetailListUntilDate(domainName, domainId, user, OccamClassesTransform.getItem(item), workplaceId, warehouseId, inventoryDate);
		LinkedList<IncomeDetail> incomeList = AON.getIncomeDetailListUntilDate(domainName, domainId, user, OccamClassesTransform.getItem(item), workplaceId, warehouseId, inventoryDate);
		
		LinkedList<Fifo> fifoList = new LinkedList<InventoryController.Fifo>();

		Double q = 0.0;
		Double qError = 0.0;
		Integer i = 0;
		Integer j = 0;
		Double quantity2 = Math.abs(quantity) ;
		while(q < quantity2 &&  qError == 0.0){
			
			InvoiceDetail invoiceDetail = invoiceList.size() > i  ? invoiceList.get(i) : null;
			IncomeDetail incomeDetail = incomeList.size() > j ? incomeList.get(j) : null;
			
			if((invoiceDetail!= null && incomeDetail == null) ||(invoiceDetail!= null &&
					invoiceDetail.getInvoice().getIssueDate().compareTo(incomeDetail.getIncome().getIssueDate())>= 0)){
				if(invoiceDetail.getQuantity() > 0){
					q = q + invoiceDetail.getQuantity(); 
					Fifo fifo = new Fifo(invoiceDetail.getPrice(), invoiceDetail.getQuantity(), Double.parseDouble(invoiceDetail.getDiscountExpression()));
					fifoList.add(fifo);
				}
				i++;	
			}
			else if(incomeDetail != null){
				if(incomeDetail.getQuantity() > 0){
					q = q + incomeDetail.getQuantity(); 
					Fifo fifo = new Fifo(incomeDetail.getPrice(), incomeDetail.getQuantity(), Double.parseDouble(incomeDetail.getDiscountExpression()));
					fifoList.add(fifo);
				}
				j++;
			}
			else qError = quantity2;
		}
		if(qError != 0.0) return item.getPurchasePrice();
		if(q == quantity2){
			Double fifoPrice = fifoList.stream().mapToDouble(x -> x.getPrice() * x.getQuantity()).sum();
			return fifoPrice / quantity2;
		}
		else{
			Double fifoPrice = fifoList.stream().limit(fifoList.size()-1).mapToDouble(x -> x.getPrice() * (1 -(x.getDiscount()/100.0)) * x.getQuantity()).sum();
			Double lastFifoPrice = fifoList.getLast().getPrice() * (1 - (fifoList.getLast().getDiscount()/100.0)) * (fifoList.getLast().getQuantity() - (q-quantity2));
			return (fifoPrice + lastFifoPrice) / quantity2;
		}
	}
	
	public static class Fifo {
		private Double price;
		private Double quantity;
		private Double discount;
	
		public Fifo(Double price, Double quantity, Double discount) {
			this.price = price;
			this.quantity = quantity;
			this.discount = discount;
		}
		
		public Double getPrice() {
			return price;
		}
		public void setPrice(Double price) {
			this.price = price;
		}
		public Double getQuantity() {
			return quantity;
		}
		public void setQuantity(Double quantity) {
			this.quantity = quantity;
		}

		public Double getDiscount() {
			return discount;
		}

		public void setDiscount(Double discount) {
			this.discount = discount;
		}
	}
	
	@Override
	public void onRemove(ActionEvent event) {
		Inventory inventory = (Inventory) getTo();
		String domainName = AonUtil.getDomainName();
		Integer domainId = DomainManager.getCurrentDomain();
		String user = AonUtil.getRemoteUser();
		
		AON.deleteWarehouseTransfer(domainName, domainId, user,
				f -> f.getSourceProperty().eq(WarehouseTransferSource.INVENTORY_INIT_STOCK.value())
				.and(f.getSourceIdProperty().eq(inventory.getId())));
		
		super.onRemove(event);
	}

}