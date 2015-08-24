package com.code.aon.ui.purchase.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.ItemComposition;
import com.code.aon.purchase.PurchaseDetail;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.event.IControllerListener;
import com.code.aon.ui.util.AonUtil;
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
	
	public IControllerListener getProductFilter() {
		if ( this.productFilter == null ) {
			this.productFilter = new ProductFilter();
		}
		return this.productFilter;
	}
	
	// TODO enable line close button
	public boolean isCloseableLine() throws ManagerBeanException{
//		PurchaseDetail detail = (PurchaseDetail) this.getModel().getRowData();
//		return this.isPending() && detail !=null
//				&& (!detail.getItem().getProduct().isSerializable() || (detail
//						.getItem().getProduct().isSerializable() && detail
//						.getItem().getSerialNumber() != null));
		return false;
	}
	
	public void onLineCloseShow(ActionEvent event){
		setManufacturingOrderManager(null);
		try {
			List<TransferPurchaseDetail> list = new LinkedList<TransferPurchaseDetail>();
			PurchaseDetail detail = (PurchaseDetail) this.getModel().getRowData();
			list.add( loadTransfer(detail) );
			getManufacturingOrderManager().setDetailList(list);
			getManufacturingOrderManager().getDetailModel().setRowIndex(0);
			getManufacturingOrderManager().onSelectDetail(null);
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
				list.add( loadTransfer(detail) );
			}
			getManufacturingOrderManager().setDetailList(list);
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
	private TransferPurchaseDetail loadTransfer(PurchaseDetail detail){
		try {
			TransferPurchaseDetail transfer = new TransferPurchaseDetail();
			transfer.setDetail(detail);
			transfer.setTotalQuantity(detail.getPendingQuantity());
			List<TransferConposition> compositionList = new LinkedList<TransferConposition>();
			for(ItemComposition ic: transfer.getDetail().getItem().getItemCompositionList()){
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
	
	// TODO onCloseLines
	public void onCloseLines(ActionEvent event){
//		for(TransferPurchaseDetail transfer: getManufacturingOrderManager().getDetailList()){
//			
//		}
		
		AonUtil.addInfoMessage("Opcion NO implementada");
	}
	
	
	
	public static class ManufacturingOrderManager implements Serializable {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

		private TransferPurchaseDetail selectedDetail;
		private List<TransferPurchaseDetail> detailList;
		private List<TransferConposition> compositeList;
		private DataModel detailModel;
		private DataModel compositeModel;
		private ArrayList<TransferPurchaseDetail> forceCloseChecks = new ArrayList<TransferPurchaseDetail>();

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
		
		public void onSelectDetail(ActionEvent event) {
			TransferPurchaseDetail transfer = (TransferPurchaseDetail)getDetailModel().getRowData();
			setSelectedDetail(transfer);
			setCompositeList(transfer.getCompositionList());
			setCompositeModel(null);
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
			TransferPurchaseDetail transfer = (TransferPurchaseDetail) detailModel.getRowData();
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
		
	}

	
	
	public static class TransferPurchaseDetail {
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
	
	public static class TransferConposition {
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