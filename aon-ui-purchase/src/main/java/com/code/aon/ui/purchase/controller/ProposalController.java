package com.code.aon.ui.purchase.controller;

import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.company.WorkPlace;
import com.code.aon.company.WorkplaceDepartment;
import com.code.aon.product.CatalogueItem;
import com.code.aon.purchase.Proposal;
import com.code.aon.purchase.ProposalDetail;
import com.code.aon.purchase.enumeration.ProposalStatus;
import com.code.aon.purchase.enumeration.ProposalTransferStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryItem;
import com.code.aon.registry.enumeration.RegistryMode;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.common.controller.IAuditableController;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.event.IControllerListener;
import com.code.aon.ui.purchase.util.PurchaseUtils;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.warehouse.controller.WarehouseCollectionsController;
import com.code.aon.warehouse.Warehouse;
import com.esferalia.aon.entity.IEntityAlias;

public class ProposalController extends BasicController implements IAuditableController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProposalController.class);
	
	private IControllerListener warehouseItemFilter;
	private LinkedList<SelectItem> proposalTypes;
	private ProposalType proposalType;
	private WorkPlace destinationWorkPlace;
	private Warehouse destinationWarehouse;
	private PurchaseUtils utils;
	private boolean showTransferWindow;
	private boolean showAuditInfoWindow;
	
	public PurchaseUtils getUtils() {
		if(utils == null) {
			utils = new PurchaseUtils();
		}
		return utils;
	}

	public void setUtils(PurchaseUtils utils) {
		this.utils = utils;
	}

	public ProposalType getProposalType() {
		return proposalType;
	}

	public void setProposalType(ProposalType proposalType) {
		this.proposalType = proposalType;
	}

	public WorkPlace getDestinationWorkPlace() {
		return destinationWorkPlace;
	}

	public void setDestinationWorkPlace(WorkPlace destinationWorkPlace) {
		this.destinationWorkPlace = destinationWorkPlace;
	}

	public Warehouse getDestinationWarehouse() {
		return destinationWarehouse;
	}

	public void setDestinationWarehouse(Warehouse destinationWarehouse) {
		this.destinationWarehouse = destinationWarehouse;
	}
	
	public boolean isShowTransferWindow() {
		return showTransferWindow;
	}

	public void setShowTransferWindow(boolean showTransferWindow) {
		this.showTransferWindow = showTransferWindow;
	}
	
	public boolean isPending() {
		Proposal proposal = (Proposal) getTo();
		return proposal.getStatus()==ProposalStatus.PENDING;
	}
	
	public boolean isPartialProcessed() {
		Proposal proposal = (Proposal) getTo();
		return proposal.getStatus()==ProposalStatus.PARTIAL_PROCESSED;
	}
	
	public boolean isProcessed() {
		Proposal proposal = (Proposal) getTo();
		return proposal.getStatus()==ProposalStatus.PROCESSED;
	}
	
	public boolean isTransfer() {
		Proposal proposal = (Proposal) getTo();
		return proposal.getTransferStatus()!=ProposalTransferStatus.NO_TRANSFER;
	}
	
	public boolean isTransferPending() {
		Proposal proposal = (Proposal) getTo();
		return proposal.getTransferStatus()==ProposalTransferStatus.TRANSFER_PENDING;
	}
	
	public boolean isTransferProcessed() {
		Proposal proposal = (Proposal) getTo();
		return proposal.getTransferStatus()==ProposalTransferStatus.TRANSFER_PROCESSED;
	}
	
	public void init() {
		setProposalType(ProposalType.ORDER);
		setDestinationWarehouse(null);
		setDestinationWorkPlace(null);
	}
	
	public List<SelectItem> getDestinationWorkPlaces() throws ManagerBeanException {
		List<SelectItem> workPlaces = new LinkedList<SelectItem>();
		IManagerBean workPlaceBean = BeanManager.getManagerBean(WorkPlace.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(workPlaceBean.getFieldName(IEntityAlias.WORK_PLACE_ACTIVE), new Boolean(true));
		criteria.addNotEqualExpression(workPlaceBean.getFieldName(IEntityAlias.WORK_PLACE_SCOPE_ID), getWorkPlaceScopeToExclude());
		criteria.addNotEqualExpression(workPlaceBean.getFieldName(IEntityAlias.WORK_PLACE_ID), ((Proposal)getTo()).getWorkPlace().getId());
		criteria.addOrder(workPlaceBean.getFieldName(IEntityAlias.WORK_PLACE_DESCRIPTION));
		workPlaceBean.getList(criteria);
		for(ITransferObject to: workPlaceBean.getList(criteria)){
			WorkPlace workPlace = (WorkPlace)to;
			workPlaces.add(new SelectItem(workPlace, workPlace.getDescription()));
		}
		return workPlaces;
	}
	
	private Integer getWorkPlaceScopeToExclude() {
		// Id del ambito de hoteles externos
		return 107;
	}
	
	public List<SelectItem> getDestinationWarehouses() throws ManagerBeanException {
		return WarehouseCollectionsController.getWarehouses(getDestinationWorkPlace());
	}
	
	public List<SelectItem> getWarehouses() throws ManagerBeanException {
		Proposal proposal = (Proposal) getTo();
		return WarehouseCollectionsController.getWarehouses(proposal.getWorkPlace());
	}
	
	public IControllerListener getWarehouseItemFilter() {
		if ( this.warehouseItemFilter == null ) {
			this.warehouseItemFilter = new WarehouseItemFilter();
		}
		return this.warehouseItemFilter;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static List<WorkplaceDepartment> getWorkplaceDepartments(WorkPlace workPlace, Warehouse warehouse, boolean  filterWarehouse){
		try {
			IManagerBean bean = BeanManager.getManagerBean(WorkplaceDepartment.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.WORKPLACE_DEPARTMENT_WORK_PLACE_ID), workPlace.getId());
			if( filterWarehouse){
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.WORKPLACE_DEPARTMENT_WAREHOUSE_ID), warehouse.getId());
			}
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.WORKPLACE_DEPARTMENT_ACTIVE), Boolean.TRUE);
			return (List) bean.getList(criteria);
		} catch (ManagerBeanException e) {
			String msg = "Error al obtener los productos";
			AonUtil.addErrorMessage(msg);
		}
		return null;
	}
	
	public List<SelectItem> getProposalTypes(){
		if (proposalTypes == null) {
			proposalTypes = new LinkedList<SelectItem>();
			SelectItem item = new SelectItem(ProposalType.ORDER, AonUtil.getMessage(ICommonMessages.INVOICE_SALES));
			proposalTypes.add(item);
			item = new SelectItem(ProposalType.ITEM_RETURN, AonUtil.getMessage(ICommonMessages.RETURN));
			proposalTypes.add(item);
			item = new SelectItem(ProposalType.TRANSFER, AonUtil.getMessage(ICommonMessages.LINK_TRANSFER));
			proposalTypes.add(item);
		}
		return proposalTypes;
	}
	
	public void onShowTransferWindow(ActionEvent event) throws ManagerBeanException{
		if(getUtils().getCompanySupplier()==null || getUtils().getCompanySupplier().getId()==null){
			String msg = "No se ha definido el proveedor para traspasos.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		ProposalDetailController detailController = (ProposalDetailController) FormUtil.getController(IPurchaseConstants.PROPOSAL_DETAIL_CONTROLLER_NAME); 
		if(detailController.getModel().getRowCount() <= 0){
			String msg = "No hay productos para traspasar.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	
	private Warehouse obtainDestinationHotelWarehouse() throws ManagerBeanException {
		if(getDestinationWorkPlace()==null || getDestinationWorkPlace().getId()==null){
			return null;
		}
		IManagerBean bean = BeanManager.getManagerBean(WorkplaceDepartment.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.WORKPLACE_DEPARTMENT_WORK_PLACE_ID), getDestinationWorkPlace().getId());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.WORKPLACE_DEPARTMENT_WAREHOUSE_ID), ((Proposal)getTo()).getWarehouse().getId());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.WORKPLACE_DEPARTMENT_ACTIVE), true);
		List<ITransferObject> list = bean.getList(criteria);
		if(list.size() > 0){
			return ((WorkplaceDepartment)list.get(0)).getWarehouse();
		} 
		return null;
	}
	
	public void onChangeDestinationWorkPlace(ActionEvent event) throws ManagerBeanException{
		setDestinationWarehouse( obtainDestinationHotelWarehouse() );
	}

	public void onTransfer(ActionEvent event){
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			try {
				HibernateUtil.setBeginTransaction(false);
				HibernateUtil.setCloseSession(false);
				HibernateUtil.beginTransaction(sessionName);
				// BEGIN operaciones de la transaccion
				Proposal destinationProposal = createDestinationProposal();
				updateSourceProposal(destinationProposal);
				// FIN operaciones de la transaccion
				HibernateUtil.getSession(sessionName).flush();
				HibernateUtil.commitTransaction(sessionName);
				this.refresh(event);
			} catch (Exception e) {
				String msg = e.getMessage();
				try {
					HibernateUtil.rollbackTransaction(sessionName);
				} catch (DAOException daoe) {
					msg = "Unable to rollback transaction! (" + msg + ")";
				}
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(e);
			} finally {
				HibernateUtil.closeSession(sessionName);
			}
		} finally {
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}
	
	public void onUndoTransfer(ActionEvent event) throws ManagerBeanException{
		Proposal proposal = (Proposal) this.getTo();
		if(proposal.getTransferProposal().getStatus()==ProposalStatus.PENDING){
			removeProposalDetail(proposal.getTransferProposal());
			removeProposal(proposal.getTransferProposal());
			this.refresh(event);
		}
	}

	private void updateSourceProposal(Proposal destinationProposal) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Proposal.class);
		Proposal proposal = (Proposal) bean.get(((Proposal) this.getTo()).getId());
		updateProposalDetails(proposal);
		proposal.setItemReturn(true);
		proposal.setTransferStatus(ProposalTransferStatus.TRANSFER_PROCESSED);
		proposal.setTransferProposal(destinationProposal);
		bean.update(proposal);
	}
	
	private void updateProposalDetails(Proposal proposal) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(ProposalDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROPOSAL_DETAIL_PROPOSAL_ID), proposal.getId());
			List<ITransferObject> list = bean.getList(criteria);
			if(list.size()<=0){
				String msg = "Nada para actualizar.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
			for(ITransferObject to: list){
				ProposalDetail detail = (ProposalDetail) to;
				detail.setQuantity(detail.getQuantity());
				detail.setSupplier(getUtils().getCompanySupplier());
				bean.update(detail);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al obtener las lineas de la solicitud.";
			AonUtil.addErrorMessage(msg);
		}
	}

	private Proposal createDestinationProposal() {
		Proposal proposal = (Proposal) this.getTo();
		try {
			IManagerBean bean = BeanManager.getManagerBean(Proposal.class);
			Proposal destinationProposal = new Proposal();
			destinationProposal.setTransferProposal(proposal);
			destinationProposal.setItemReturn(false);
			destinationProposal.setIssueDate(proposal.getIssueDate());
			destinationProposal.setWarehouse(this.getDestinationWarehouse());
			destinationProposal.setDepartment(this.getDestinationWarehouse().getDepartment());
			destinationProposal.setWorkPlace(getDestinationWorkPlace());
			destinationProposal.setRemarks(proposal.getRemarks());
			destinationProposal.setScope(proposal.getScope());
			destinationProposal.setStatus(proposal.getStatus());
			destinationProposal.setTransferStatus(ProposalTransferStatus.TRANSFER_PROCESSED);
			destinationProposal = (Proposal) bean.insert(destinationProposal);
			createDestinationProposalDetails(destinationProposal);
			return destinationProposal;
		} catch (ManagerBeanException e) {
			String msg = "Error al crear la solicitud del hotel destino.";
			AonUtil.addErrorMessage(msg);
		}
		return null;
	}

	private void createDestinationProposalDetails(Proposal destinationProposal) {
		Proposal proposal = (Proposal) this.getTo();
		try {
			IManagerBean bean = BeanManager.getManagerBean(ProposalDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROPOSAL_DETAIL_PROPOSAL_ID), proposal.getId());
			List<ITransferObject> list = bean.getList(criteria);
			if(list.size()<=0){
				String msg = "Nada para traspasar.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
			for(ITransferObject to: list){
				ProposalDetail detail = (ProposalDetail) to;
				ProposalDetail destinationProposalDetail = new ProposalDetail();
				destinationProposalDetail.setProposal(destinationProposal);
				destinationProposalDetail.setDescription(detail.getDescription());
				destinationProposalDetail.setDiscountExpr(detail.getDiscountExpr());
				destinationProposalDetail.setItem(detail.getItem());
				destinationProposalDetail.setPrice(detail.getPrice());
				destinationProposalDetail.setQuantity(detail.getQuantity());
				destinationProposalDetail.setStatus(detail.getStatus());
				destinationProposalDetail.setSupplier(getUtils().getCompanySupplier());
				bean.insert(destinationProposalDetail);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al crear las lineas de la solicitud del hotel destino.";
			AonUtil.addErrorMessage(msg);
		}
	}
	
	public void removeProposal(Proposal proposal){
		try {
			IManagerBean bean = BeanManager.getManagerBean(Proposal.class);
			removeForeignKeyConstraint(proposal);
			bean.remove(proposal);
		} catch (ManagerBeanException e) {
			String msg = "Error al borrar la solicitud.";
			AonUtil.addErrorMessage(msg);
		}
	}
	private void removeForeignKeyConstraint(Proposal proposal) throws ManagerBeanException {
		Proposal sourceProposal = proposal.getTransferProposal();
		IManagerBean bean = BeanManager.getManagerBean(Proposal.class);
		sourceProposal.setTransferProposal(null);
		sourceProposal.setTransferStatus(ProposalTransferStatus.TRANSFER_PENDING);
		bean.update(sourceProposal);
		proposal.setTransferProposal(null);
		bean.update(proposal);
	}

	public void removeProposalDetail(Proposal proposal){
		try {
			IManagerBean bean = BeanManager.getManagerBean(ProposalDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROPOSAL_DETAIL_PROPOSAL_ID), proposal.getId());
			List<ITransferObject> list = bean.getList(criteria);
			for(ITransferObject to: list){
				ProposalDetail detail = (ProposalDetail) to;
				bean.remove(detail);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al borrar las lineas de la solicitud.";
			AonUtil.addErrorMessage(msg);
		}
	}
	
	@Override
	public boolean isShowAuditInfoWindow() {
		return showAuditInfoWindow;
	}

	@Override
	public void setShowAuditInfoWindow(boolean showAuditInfoWindow) {
		this.showAuditInfoWindow = showAuditInfoWindow;
	}	
	
	public enum ProposalType {
		ORDER,
		ITEM_RETURN,
		TRANSFER;
	}
	
	private static class WarehouseItemFilter  extends ControllerAdapter {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

		private List<WorkplaceDepartment> getWorkplaceDepartments(Proposal proposal){
			return ProposalController.getWorkplaceDepartments(proposal.getWorkPlace(), proposal.getWarehouse(), true);
		}
				
		private List<Integer> getWarehousesItemIds() throws ManagerBeanException{
			List<Integer> list = new LinkedList<Integer>();
			IController proposalController = FormUtil.getController(IPurchaseConstants.PROPOSAL_CONTROLLER_NAME);
			Proposal proposal = (Proposal)proposalController.getTo(); 
			if(proposal.getWarehouse()!=null){
				List<Integer> catalogueIds = new LinkedList<Integer>();
				for(WorkplaceDepartment wd: getWorkplaceDepartments(proposal)){
					catalogueIds.add(wd.getCatalogue().getId());
				}
				if(catalogueIds.isEmpty()){
					String msg = "El almacen se ha desactivado.";
					AonUtil.addErrorMessage(msg);
					catalogueIds.add(-1);
				}
				IManagerBean catalogueItemBean = BeanManager.getManagerBean(CatalogueItem.class);
				Criteria deptCriteria = new Criteria();
				deptCriteria.addInExpression(catalogueItemBean.getFieldName(IEntityAlias.CATALOGUE_ITEM_CATALOGUE_ID), catalogueIds);
				List<Integer> itemIds = new LinkedList<Integer>();
				for (ITransferObject ito : catalogueItemBean.getList(deptCriteria)) {
					itemIds.add(((CatalogueItem)ito).getItem().getId());
				}
				if(itemIds.isEmpty()){
					itemIds.add(-1);
				}
				IManagerBean rItemBean = BeanManager.getManagerBean(RegistryItem.class);
				Criteria rItemCriteria = new Criteria();
				rItemCriteria.addNotNullExpression(rItemBean.getFieldName(IEntityAlias.REGISTRY_ITEM_REGISTRY_ID));
				rItemCriteria.addInExpression(rItemBean.getFieldName(IEntityAlias.REGISTRY_ITEM_ITEM_ID), itemIds);
				rItemCriteria.addEqualExpression(rItemBean.getFieldName(IEntityAlias.REGISTRY_ITEM_TYPE), RegistryMode.SUPPLIER);

				for (ITransferObject ito : rItemBean.getList(rItemCriteria)) {
					RegistryItem rItem = (RegistryItem)ito;
					if (rItem.getWorkPlace() == null || rItem.getWorkPlace().getId() == null || rItem.getWorkPlace().getId().equals(proposal.getWorkPlace().getId())) {
						list.add(rItem.getItem().getId());
					}
				}
			}
			if(list.isEmpty()){
				list.add(-1);
			}
			return list;
		}		
		
		@Override
		public void beforeModelSearched(ControllerEvent event)
				throws ControllerListenerException {
			IController controller = event.getController();
			try {					
				controller.getCriteria().addInExpression(controller.getFieldName(IEntityAlias.ITEM_ID), getWarehousesItemIds());
			} catch (ManagerBeanException e) {
				LOGGER.error("Error filtering items", e);
			}
		}
		
	}
	
}
