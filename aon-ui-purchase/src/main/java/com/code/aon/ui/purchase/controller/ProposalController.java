package com.code.aon.ui.purchase.controller;

import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.company.Company;
import com.code.aon.company.Department;
import com.code.aon.company.WorkPlace;
import com.code.aon.company.WorkplaceDepartment;
import com.code.aon.product.CatalogueItem;
import com.code.aon.product.ItemSupplier;
import com.code.aon.purchase.Proposal;
import com.code.aon.purchase.ProposalDetail;
import com.code.aon.purchase.enumeration.ProposalStatus;
import com.code.aon.purchase.enumeration.ProposalTransferStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.supplier.Supplier;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.event.IControllerListener;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class ProposalController extends BasicController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProposalController.class);
	
	private IControllerListener departmentItemFilter;
	private LinkedList<SelectItem> proposalTypes;
	private ProposalType proposalType;
	private WorkPlace destinationWorkPlace;
	private Department destinationDepartment;
	private Supplier companySupplier;
	private boolean showTransferWindow;
	
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

	public Department getDestinationDepartment() {
		return destinationDepartment;
	}

	public void setDestinationDepartment(Department destinationDepartment) {
		this.destinationDepartment = destinationDepartment;
	}
	
	public boolean isShowTransferWindow() {
		return showTransferWindow;
	}

	public void setShowTransferWindow(boolean showTransferWindow) {
		this.showTransferWindow = showTransferWindow;
	}
	
	private Supplier getCompanySupplier() {
		if(companySupplier == null){
			Company company = (Company) ((CompanyController)FormUtil.getController(ICompanyConstants.COMPANY_CONTROLLER_NAME)).getTo();
			try {
				IManagerBean bean = BeanManager.getManagerBean(Supplier.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SUPPLIER_REGISTRY_ID), company.getId());
				List<ITransferObject> list = bean.getList(criteria);
				if(!list.isEmpty()){
					companySupplier = (Supplier) list.get(0);
				}
			} catch (ManagerBeanException e) {
				String msg = "Error al obtener el proveedor de traspasos.";
				AonUtil.addErrorMessage(msg);
			}
		}
		return companySupplier;
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
		setDestinationDepartment(null);
		setDestinationWorkPlace(null);
	}
	
	public List<SelectItem> getDestinationDepartments() {
		return getDepartments(getDestinationWorkPlace());
	}
	
	public List<SelectItem> getDepartments() {
		Proposal proposal = (Proposal) getTo();
		return getDepartments(proposal.getWorkPlace());
	}
	
	private List<SelectItem> getDepartments(WorkPlace workPlace) {
		List<SelectItem> list = new LinkedList<SelectItem>();
		try {
			List<Integer> deptartmentIds = new LinkedList<Integer>();
			if(workPlace != null){
				for (ITransferObject ito : getWorkplaceDepartments(workPlace)) {
					WorkplaceDepartment wd = (WorkplaceDepartment)ito;
					deptartmentIds.add(wd.getDepartment().getId());
				}
				if(!deptartmentIds.isEmpty()){
					IManagerBean dBean = BeanManager.getManagerBean(Department.class);
					Criteria dCriteria = new Criteria();
					dCriteria.addInExpression(dBean.getFieldName(IEntityAlias.DEPARTMENT_ID), deptartmentIds);
					dCriteria.addOrder(dBean.getFieldName(IEntityAlias.DEPARTMENT_NAME));
					for (ITransferObject ito : dBean.getList(dCriteria)) {
						Department d = (Department)ito;
						SelectItem item = new SelectItem(d, d.getName());
						list.add(item);
					}
				}
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al obtener los departamentos";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		return list;
	}
	
	public List<Integer> getDepartmentsItemIds() throws ManagerBeanException{
		List<Integer> list = new LinkedList<Integer>();
		Proposal proposal = ((Proposal)getTo()); 
		if(proposal.getDepartment()!=null){
			List<Integer> catalogueIds = new LinkedList<Integer>();
			for(ITransferObject to: getWorkplaceDepartments(proposal)){
				WorkplaceDepartment wd = (WorkplaceDepartment) to;
				catalogueIds.add(wd.getCatalogue().getId());
			}
			if(catalogueIds.isEmpty()){
				String msg = "El departamento se ha desactivado.";
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
			IManagerBean itemSupplierBean = BeanManager.getManagerBean(ItemSupplier.class);
			Criteria itemSupCriteria = new Criteria();
			itemSupCriteria.addInExpression(itemSupplierBean.getFieldName(IEntityAlias.ITEM_SUPPLIER_ITEM_ID), itemIds);
			itemSupCriteria.addNotNullExpression(itemSupplierBean.getFieldName(IEntityAlias.ITEM_SUPPLIER_SUPPLIER_ID));

			for (ITransferObject ito : itemSupplierBean.getList(itemSupCriteria)) {
				ItemSupplier is = (ItemSupplier) ito;
				if( is.getWorkPlace() == null || is.getWorkPlace().getId() == null || is.getWorkPlace().getId().equals(proposal.getWorkPlace().getId()) ){
					list.add(is.getItem().getId());
				}
			}
		}
		if(list.isEmpty()){
			list.add(-1);
		}
		return list;
	}
	
	public IControllerListener getDepartmentItemFilter() {
		if ( this.departmentItemFilter == null ) {
			this.departmentItemFilter = new ControllerAdapter() {
				@Override
				public void beforeModelSearched(ControllerEvent event)
						throws ControllerListenerException {
					IController controller = event.getController();
					try {					
						controller.getCriteria().addInExpression(controller.getFieldName(IEntityAlias.ITEM_ID), getDepartmentsItemIds());
					} catch (ManagerBeanException e) {
						LOGGER.error("Error filtering items", e);
					}
				}
			};
		}
		return this.departmentItemFilter;
	}
	
	private List<ITransferObject> getWorkplaceDepartments(WorkPlace workPlace) throws ManagerBeanException {
		return getWorkplaceDepartments(workPlace, null, false);
	}
	
	private List<ITransferObject> getWorkplaceDepartments(Proposal proposal){
		return getWorkplaceDepartments(proposal.getWorkPlace(), proposal.getDepartment(), true);
	}
	
	private List<ITransferObject> getWorkplaceDepartments(WorkPlace workPlace, Department department, boolean  filterDepartment){
		try {
			IManagerBean bean = BeanManager.getManagerBean(WorkplaceDepartment.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.WORKPLACE_DEPARTMENT_WORK_PLACE_ID), workPlace.getId());
			if( filterDepartment){
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.WORKPLACE_DEPARTMENT_DEPARTMENT_ID), department.getId());
			}
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.WORKPLACE_DEPARTMENT_ACTIVE), Boolean.TRUE);
			return bean.getList(criteria);
		} catch (ManagerBeanException e) {
			String msg = "Error al obtener los productos";
			AonUtil.addErrorMessage(msg);
		}
		return null;
	}
	
	public List<SelectItem> getProposalTypes(){
		if (proposalTypes == null) {
			proposalTypes = new LinkedList<SelectItem>();
			SelectItem item = new SelectItem(ProposalType.ORDER, AonUtil.getMessage(IPurchaseConstants.PURCHASE_BUNDLE_NAME, "purchase_proposal_order"));
			proposalTypes.add(item);
			item = new SelectItem(ProposalType.ITEM_RETURN, AonUtil.getMessage(IPurchaseConstants.PURCHASE_BUNDLE_NAME, "purchase_proposal_item_return"));
			proposalTypes.add(item);
			item = new SelectItem(ProposalType.TRANSFER, AonUtil.getMessage(IPurchaseConstants.PURCHASE_BUNDLE_NAME, "purchase_proposal_transference"));
			proposalTypes.add(item);
		}
		return proposalTypes;
	}
	
	public void onShowTransferWindow(ActionEvent event) throws ManagerBeanException{
		if(getCompanySupplier()==null || getCompanySupplier().getId()==null){
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
				this.refresh(event);
				
				// FIN operaciones de la transaccion
				
				HibernateUtil.getSession(sessionName).flush();
				HibernateUtil.commitTransaction(sessionName);
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
				detail.setQuantity(detail.getQuantity()*(-1));
				detail.setSupplier(getCompanySupplier());
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
			destinationProposal.setDepartment(this.getDestinationDepartment());
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
				destinationProposalDetail.setSupplier(getCompanySupplier());
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
	
	public enum ProposalType {
		ORDER,
		ITEM_RETURN,
		TRANSFER;
	}
	
}
