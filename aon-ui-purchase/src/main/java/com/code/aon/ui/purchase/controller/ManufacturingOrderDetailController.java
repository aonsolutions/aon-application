package com.code.aon.ui.purchase.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.util.SeriesNumberUtil;
import com.code.aon.product.ItemComposition;
import com.code.aon.purchase.Purchase;
import com.code.aon.purchase.PurchaseDetail;
import com.code.aon.purchase.enumeration.PurchaseDetailStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.config.controller.ConfigCollectionsController;
import com.code.aon.ui.config.controller.ConfigConstants;
import com.code.aon.ui.form.AbstractPojoController;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.event.IControllerListener;
import com.code.aon.ui.purchase.util.PurchaseUtils;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.warehouse.controller.IWarehouseConstants;
import com.code.aon.ui.warehouse.util.WarehouseUtil;
import com.code.aon.warehouse.Income;
import com.code.aon.warehouse.IncomeDetail;
import com.esferalia.aon.entity.IEntityAlias;

public class ManufacturingOrderDetailController extends PurchaseDetailController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ManufacturingOrderDetailController.class.getName());
	
	private boolean showLineCloseWindow;
	
	private ManufacturingOrderManager manufacturingOrderManager;
	
	private IControllerListener productFilter;
	
	public boolean isShowLineCloseWindow() {
		return showLineCloseWindow;
	}

	public void setShowLineCloseWindow(boolean showLineCLoseWindow) {
		this.showLineCloseWindow = showLineCLoseWindow;
	}
	
	public ManufacturingOrderManager getManufacturingOrderManager() {
		if (manufacturingOrderManager == null) {
			manufacturingOrderManager = new ManufacturingOrderManager(); 
		}
		return manufacturingOrderManager;
	}

	public void setManufacturingOrderManager(ManufacturingOrderManager manufacturingOrderManager) {
		this.manufacturingOrderManager = manufacturingOrderManager;
	}
	
	protected String getLinkBackAction(){
		return MANUFACTURING_ORDER_FORM_NAME;
	}
	
	public IControllerListener getProductFilter() {
		if ( this.productFilter == null ) {
			this.productFilter = new ProductFilter();
		}
		return this.productFilter;
	}
	
	public boolean isCloseableLine() throws ManagerBeanException {
		return isPending((PurchaseDetail) this.getModel().getRowData());
	}

	public boolean isCloseableTransfer() throws ManagerBeanException {
		if(getManufacturingOrderManager().getDetailModel().isRowAvailable()){
			TransferPurchaseDetail transfer = (TransferPurchaseDetail) getManufacturingOrderManager().getDetailModel().getRowData();
			return isPending(transfer.getDetail()) && !transfer.getDetail().getItem().isWildCard();
		}
		return false;
	}

	private boolean isPending(PurchaseDetail detail) throws ManagerBeanException {
		return detail !=null && detail.getStatus().equals(PurchaseDetailStatus.PENDING);
	}

	private boolean isCloseable(PurchaseDetail detail) throws ManagerBeanException {
		return detail !=null && detail.getStatus().equals(PurchaseDetailStatus.PENDING)
				&& (!detail.getItem().getProduct().isSerializable() 
						|| (detail.getItem().getProduct().isSerializable() 
						&& detail.getItem().getSerialNumber() != null));
	}
	
	public void onLineCloseShow(ActionEvent event){
		setManufacturingOrderManager(null);
		try {
			if(this.getModel().isRowAvailable()){
				int rowIndex = this.getModel().getRowIndex();
				this.initializeModel();
				this.getModel().setRowIndex(rowIndex);
				PurchaseDetail detail = (PurchaseDetail) this.getModel().getRowData();
				List<TransferPurchaseDetail> list = new LinkedList<TransferPurchaseDetail>();
				list.add( loadTransfer(detail) );
				getManufacturingOrderManager().init(list);
				getManufacturingOrderManager().getDetailModel().setRowIndex(0);
				getManufacturingOrderManager().setUniqueDetailSelected(true);
				onSelectCloseDetail(null);
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
	public void onFullCloseShow(ActionEvent event){
		setManufacturingOrderManager(null);
		try {
			List<TransferPurchaseDetail> list = new LinkedList<TransferPurchaseDetail>();
			for(ITransferObject to: this.getManagerBean().getList(this.getCriteria())){
				PurchaseDetail detail = (PurchaseDetail) to;
				if(isPending(detail)){
					list.add( loadTransfer(detail) );
				}
			}
			getManufacturingOrderManager().init(list);
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
	public void onChangeDetailProductQuantity(ActionEvent event){
		TransferPurchaseDetail detail = ((TransferPurchaseDetail)getManufacturingOrderManager().getDetailModel().getRowData());
		for(TransferConposition tc: detail.getCompositionList()){
			tc.setTotalQuantity( tc.getComposition().getQuantity() * detail.getTotalQuantity() );
		}
	}
	
	private TransferPurchaseDetail loadTransfer(PurchaseDetail detail){
		try {
			TransferPurchaseDetail transfer = new TransferPurchaseDetail();
			transfer.setDetail(detail);
			transfer.setTotalQuantity(detail.getPendingQuantity());
			List<TransferConposition> compositionList = new LinkedList<TransferConposition>();
			List<ItemComposition> itemCompositionList = null;
			if(transfer.getDetail().getItem().getProduct().isSerializable()){
				itemCompositionList = transfer.getDetail().getItem().getProduct().getBaseItem().getItemCompositionList();
			} else {
				itemCompositionList = transfer.getDetail().getItem().getItemCompositionList();
			}
			for(ItemComposition ic: itemCompositionList){
				TransferConposition composition = new TransferConposition();
				composition.setComposition(ic);
				composition.setTotalQuantity(ic.getQuantity() * transfer.getTotalQuantity());
				compositionList.add(composition);
			}
			transfer.setCompositionList(compositionList);
			return transfer;
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
	public void onLoadManufacturedIncome(ActionEvent event) throws ManagerBeanException {
		PurchaseDetail purchaseDetail = (PurchaseDetail)this.getModel().getRowData();
		IManagerBean bean = BeanManager.getManagerBean(IncomeDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.INCOME_DETAIL_PURCHASE_DETAIL_ID), purchaseDetail.getId());
		Iterator<?> iterator = bean.getList(criteria).iterator();
		if (iterator.hasNext()) {
			IncomeDetail incomeDetail = (IncomeDetail) iterator.next();
			BasicController incomeController = (BasicController)AonUtil.getRegisteredBean(IWarehouseConstants.INCOME_CONTROLLER_NAME);
			incomeController.onLoad(event, incomeDetail.getIncome().getId(), MANUFACTURING_ORDER_FORM_NAME, null);
		} else {
			AonUtil.addErrorMessage("Albarán no encontrado.");
			throw new AbortProcessingException("Albarán no encontrado.");
		}
	}
	
	
	public void onCloseLines(ActionEvent event){
		try {
			Purchase purchase = (Purchase) this.getMasterController().getTo();
			if(purchase.getWarehouse()!=null && purchase.getWarehouse().getId()!=null){
				for(TransferPurchaseDetail transfer: getManufacturingOrderManager().getDetailList()){
					if(manufacturingOrderManager.getForceCloseRowChecked(transfer)){
						createPendingDetailLine(transfer); 
					}
					if(isCloseable(transfer.getDetail())){
						closeDetailLine(transfer);
						createManufactureIncome(transfer);
					}
				}
				
				if(isSettledAllLines()){
					closePurchase(event);
				}
				
				this.initializeModel();
			} else {
				AonUtil.addErrorMessage("No se ha definido el almacén");
			}
			
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	private void createPendingDetailLine(TransferPurchaseDetail transfer) throws ManagerBeanException {
		PurchaseUtils utils = new PurchaseUtils();
		utils.createPurchaseDetail(transfer.getDetail().getPurchase(), transfer.getDetail().getItem(), transfer.getDetail().getProject(), 
				transfer.getDetail().getProposalDetail(), utils.calculateNextLine(transfer.getDetail().getPurchase()), 
				transfer.getDetail().getDescription(), transfer.getDetail().getQuantity()-transfer.getTotalQuantity(), 
				transfer.getDetail().getPrice(), transfer.getDetail().getDiscountExpression(), transfer.getDetail().getTaxes(), 
				null, 0);
	}
	
	private void closeDetailLine(TransferPurchaseDetail transfer) throws ManagerBeanException {
		PurchaseDetail detail = transfer.getDetail();
		detail.setQuantity(transfer.getTotalQuantity());
		detail.setDelivered(transfer.getTotalQuantity());
		detail.setStatus(PurchaseDetailStatus.SETTLED);
		IManagerBean bean = BeanManager.getManagerBean(PurchaseDetail.class);
		bean.restoreNullSubPOJOs(detail);	
		bean.update(detail);
	}

	private void createManufactureIncome(TransferPurchaseDetail transferPurchaseDetail)
			throws ManagerBeanException {
		WarehouseUtil util = new WarehouseUtil();
		String referenceCode = transferPurchaseDetail.getDetail().getPurchase().getReferenceCode();
		referenceCode += "-";
		referenceCode += transferPurchaseDetail.getDetail().getLine();
		Income income = util.createIncome(transferPurchaseDetail.getDetail().getPurchase(), referenceCode);
		PurchaseDetail purchaseDetail = transferPurchaseDetail.getDetail();
		util.createIncomeDetail(purchaseDetail, income, transferPurchaseDetail.getTotalQuantity());
		for(TransferConposition tc: transferPurchaseDetail.getCompositionList()){
			util.createIncomeDetail(purchaseDetail.getPurchase().getWarehouse(), income, tc.getComposition(), tc.getTotalQuantity());
		}
	}
	
	private boolean isSettledAllLines() throws ManagerBeanException {
		for(ITransferObject to: this.getManagerBean().getList(this.getCriteria())){
			PurchaseDetail detail = (PurchaseDetail) to;
			if(!detail.isSettled()){
				return false;
			}
		}
		return true;
	}
	
	private void closePurchase(ActionEvent event) throws ManagerBeanException {
		((ManufacturingOrderController)this.getMasterController()).onClose(event);
	}

	public void onSelectCloseDetail(ActionEvent event) {
		TransferPurchaseDetail transfer = (TransferPurchaseDetail)getManufacturingOrderManager().getDetailModel().getRowData();
		getManufacturingOrderManager().setSelectedDetail(transfer);
		getManufacturingOrderManager().setCompositeList(transfer.getCompositionList());
		getManufacturingOrderManager().setCompositeModel(null);
		loadAssignSerialNumber(transfer.getDetail());
	}
	public void onAssignSerialNumber(ActionEvent event) throws ManagerBeanException {
		super.onAssignSerialNumber(event);
		if(getManufacturingOrderManager().isUniqueDetailSelected()){
			setShowLineCloseWindow(false);	
		} else {
			onFullCloseShow(event);
		}
	}
	public void onCancelAssignSerialNumber(ActionEvent event) throws ManagerBeanException {
		getManufacturingOrderManager().setSelectedDetail(null);
		getManufacturingOrderManager().setCompositeList(null);
		getManufacturingOrderManager().setCompositeModel(null);
	}
	

	public static class ManufacturingOrderManager implements Serializable {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

		private TransferPurchaseDetail selectedDetail;
		private List<TransferPurchaseDetail> detailList;
		private List<TransferConposition> compositeList;
		private DataModel detailModel;
		private DataModel compositeModel;
		private ArrayList<TransferPurchaseDetail> forceCloseChecks = new ArrayList<TransferPurchaseDetail>();
		private boolean uniqueDetailSelected;
		private String series;
		private int number;
		private boolean numberEditable;
		private int seriesListLength;
		private Date issueTime;
		
		public TransferPurchaseDetail getSelectedDetail() {
			return selectedDetail;
		}

		public void setSelectedDetail(TransferPurchaseDetail selectedDetail) {
			this.selectedDetail = selectedDetail;
		}

		public List<TransferPurchaseDetail> getDetailList() {
			return detailList;
		}

		public void setDetailList(List<TransferPurchaseDetail> detailList) {
			this.detailList = detailList;
		}

	    public List<TransferConposition> getCompositeList() {
			return compositeList;
		}

		public void setCompositeList(List<TransferConposition> list) {
			this.compositeList = list;
		}

		public DataModel getDetailModel() {
			if (detailModel == null) {
				detailModel = new SerializableListDataModel(detailList);
			}
			return detailModel;
		}

		public void setDetailModel(DataModel model) {
			this.detailModel = model;
		}

		public DataModel getCompositeModel() {
			if (compositeModel == null) {
				compositeModel = new SerializableListDataModel(compositeList);
			}
			return compositeModel;
		}

		public void setCompositeModel(DataModel model) {
			this.compositeModel = model;
		}
		
		public void init(List<TransferPurchaseDetail> list) {
			setDetailList(list);
			initSeries();
			setIssueTime(new Date());
		}
		
		public boolean isUniqueDetailSelected() {
			return uniqueDetailSelected;
		}

		public void setUniqueDetailSelected(boolean uniqueDetailSelected) {
			this.uniqueDetailSelected = uniqueDetailSelected;
		}
		
		public String getSeries() {
			return series;
		}

		public void setSeries(String series) {
			this.series = series;
		}

		public int getNumber() {
			return number;
		}

		public void setNumber(int number) {
			this.number = number;
		}
		
		public boolean isNumberEditable() {
			return numberEditable;
		}

		public void setNumberEditable(boolean numberEditable) {
			this.numberEditable = numberEditable;
		}
		
		public int getSeriesListLength() {
			return seriesListLength;
		}

		public void setSeriesListLength(int seriesListLength) {
			this.seriesListLength = seriesListLength;
		}

		public Date getIssueTime() {
			return issueTime;
		}

		public void setIssueTime(Date issueTime) {
			this.issueTime = issueTime;
		}
		
		public boolean isEmptyForceCloseChecks(){
			return forceCloseChecks==null || forceCloseChecks.size()<=0;
		}

		/**
		 *  FORCED CLOSE CHECK LIST CONTROL
		 */
		
		public void forceCloseRowSelected(ActionEvent event) {
			TransferPurchaseDetail transfer = (TransferPurchaseDetail) detailModel.getRowData();
			selectForceCloseRow(!forceCloseChecks.contains(transfer));
		}

		private void selectForceCloseRow(boolean rowChecked) {
			TransferPurchaseDetail transfer = (TransferPurchaseDetail) detailModel.getRowData();
			setForceCloseRowChecked(transfer, rowChecked);
		}

		public boolean getForceCloseRowChecked() {
			return getForceCloseRowChecked((TransferPurchaseDetail) detailModel.getRowData());
		}
		public boolean getForceCloseRowChecked(TransferPurchaseDetail transfer) {
			return forceCloseChecks.contains(transfer);
		}

		public void setForceCloseRowChecked(boolean rowChecked) {
		}

		public void setForceCloseRowChecked(TransferPurchaseDetail transfer, boolean rowChecked) {
			if (rowChecked) {
				if (!forceCloseChecks.contains(transfer)) {
					forceCloseChecks.add(transfer);
				}
			} else {
				if (forceCloseChecks.contains(transfer)) {
					forceCloseChecks.remove(transfer);
				}
			}
		}
		
		public boolean isTransferedLessThanPending() {
			TransferPurchaseDetail transfer = (TransferPurchaseDetail) getDetailModel().getRowData();
			if( transfer.getTotalQuantity() < transfer.getDetail().getPendingQuantity() ){
				return true;
			}
			return false;
		}
		
		/**
		 * SERIES-NUMBER 
		 */
		
		public List<SelectItem> getSeriesCodes() throws ManagerBeanException {
			ConfigCollectionsController ccc = (ConfigCollectionsController) AonUtil.getRegisteredBean(ConfigConstants.CONFIG_COLLECTIONS);
			return ccc.getDeliverySeriesIds();
		}	
		
		
		public void onNumberEditable(ActionEvent event) {
			if ( getNumber() == 0 ) {
				int number = obtainMaxNumber(getSeries());
				setNumber(number);
			}
		}
		
		public int obtainMaxNumber(String seriesId) {
	    	return SeriesNumberUtil.obtainNumber(seriesId, getTableName(), getSeriesCriteria());
		}
		
		protected String getTableName() {
			return StringUtils.capitalize(((AbstractPojoController)FormUtil.getController(IWarehouseConstants.WAREHOUSE_TRANSFER_CONTROLLER_NAME)).getBeanName());
		}
		
		protected Criteria getSeriesCriteria() {
			return null;
		}
		
		public void onSeriesChanged(ValueChangeEvent event)  {
			updateSeries( (String)event.getNewValue() );
		}

		public void updateSeries( String seriesCode ) {
			if ( numberEditable ) {
				int number = obtainMaxNumber(seriesCode);	
				setNumber(number);
			}
		}
		
		public void initSeries() {
			setSeries(initSeries(true));
		}
		
		public String initSeries(boolean update) {
			String seriesCode = null;
			setSeriesListLength(0);
			setNumberEditable(false);
			try {
				List<SelectItem> list = getSeriesCodes();
				setSeriesListLength(list.size());
				if ( getSeriesListLength() == 1 ) {
					seriesCode = (String) list.get(0).getValue();
					if ( update ) {
						setSeries(seriesCode);					
						updateSeries(seriesCode);	
					}
				}
			} catch (ManagerBeanException e) {
				LOGGER.error(e.getMessage(), e);
			}
			return seriesCode;
		}
		
	}

	
	
	public static class TransferPurchaseDetail implements Serializable {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
		
		private PurchaseDetail detail;
		private double totalQuantity;
		private List<TransferConposition> compositionList;
		public PurchaseDetail getDetail() {
			return detail;
		}
		public void setDetail(PurchaseDetail detail) {
			this.detail = detail;
		}
		public double getTotalQuantity() {
			return totalQuantity;
		}
		public void setTotalQuantity(double totalQuantity) {
			this.totalQuantity = totalQuantity;
		}
		public List<TransferConposition> getCompositionList() {
			return compositionList;
		}
		public void setCompositionList(List<TransferConposition> compositionList) {
			this.compositionList = compositionList;
		}
	}
	
	public static class TransferConposition implements Serializable {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
		
		private ItemComposition composition;
		private double totalQuantity;
		public ItemComposition getComposition() {
			return composition;
		}
		public void setComposition(ItemComposition composition) {
			this.composition = composition;
		}
		public double getTotalQuantity() {
			return totalQuantity;
		}
		public void setTotalQuantity(double totalQuantity) {
			this.totalQuantity = totalQuantity;
		}
	}
	
	private static class ProductFilter extends ControllerAdapter {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

		@Override
		public void beforeModelInitialized(ControllerEvent event)
				throws ControllerListenerException {
			IController controller = event.getController();
			try {
				String alias = "Item.product.manufactured";
				controller.getCriteria().addEqualExpression(alias, Boolean.TRUE);				
				alias = controller.getFieldName(IEntityAlias.ITEM_PRODUCT_SERIALIZABLE);
				Expression expr1 = ExpressionUtilities.getEqualExpression(alias, Boolean.TRUE);
				alias = controller.getFieldName(IEntityAlias.ITEM_SERIAL_NUMBER);
				Expression expr2 = ExpressionUtilities.getNullExpression(alias);
				Expression serialBaseExpression = ExpressionUtilities.getAndExpression(expr1, expr2);
				alias = controller.getFieldName(IEntityAlias.ITEM_PRODUCT_SERIALIZABLE);
				Expression notSerialExpression = ExpressionUtilities.getNotEqualExpression(alias, Boolean.TRUE);
				controller.getCriteria().addExpression(ExpressionUtilities.getOrExpression(serialBaseExpression, notSerialExpression));				
			} catch (ManagerBeanException e) {
				LOGGER.error("Error filtering product on manufacturing order line", e);
			}
		}
		
	}

	
}