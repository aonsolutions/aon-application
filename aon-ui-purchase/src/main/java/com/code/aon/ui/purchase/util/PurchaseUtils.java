package com.code.aon.ui.purchase.util;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.code.aon.common.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.company.Company;
import com.code.aon.company.WorkPlace;
import com.code.aon.config.Series;
import com.code.aon.config.util.SeriesNumberUtil;
import com.code.aon.product.Item;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.project.Project;
import com.code.aon.purchase.ProposalDetail;
import com.code.aon.purchase.Purchase;
import com.code.aon.purchase.PurchaseDetail;
import com.code.aon.purchase.enumeration.ProposalDetailStatus;
import com.code.aon.purchase.enumeration.PurchaseDetailStatus;
import com.code.aon.purchase.enumeration.PurchaseDocumentType;
import com.code.aon.purchase.enumeration.PurchaseStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.supplier.Supplier;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.carrier.Carrier;
import com.esferalia.aon.carrier.enumeration.ShipmentPeriod;
import com.esferalia.aon.entity.IEntityAlias;

public class PurchaseUtils implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private Supplier companySupplier;

	/**
	 * Creates a purchase with basic data
	 * @param supplier
	 * @param workPlace
	 * @param documentType
	 * @param comments
	 * @return
	 * @throws ManagerBeanException
	 */
	public Purchase createPurchase(Supplier supplier, WorkPlace workPlace,
			PurchaseDocumentType documentType, String comments)
			throws ManagerBeanException {
		return createPurchase(null, supplier, workPlace, documentType,
				comments, null);
	}
	
	/**
	 * Creates a purchase with basic data and remarks
	 * @param supplier
	 * @param workPlace
	 * @param documentType
	 * @param comments
	 * @param remarks
	 * @return
	 * @throws ManagerBeanException
	 */
	public Purchase createPurchase(Supplier supplier, WorkPlace workPlace,
			PurchaseDocumentType documentType, String comments, String remarks)
			throws ManagerBeanException {
		return createPurchase(null, supplier, workPlace, documentType,
				comments, remarks, null, null, null, null, null, null, null,
				null, null, null);
	}
	
	public Purchase createPurchase(String series, Supplier supplier,
			WorkPlace workPlace, PurchaseDocumentType documentType,
			String comments, String remarks) throws ManagerBeanException {
		return createPurchase(series, supplier, workPlace, documentType,
				comments, remarks, null, null, null, null, null, null, null,
				null, null, null);
	}

	/**
	 * Creates a purchase with all possible data (basic data, remarks and shipping data)
	 * @param series
	 * @param supplier
	 * @param workPlace
	 * @param documentType
	 * @param comments
	 * @param remarks
	 * @param carrier
	 * @param purchaseReference
	 * @param shippingAlternativeAddress
	 * @param shippingAlternativeAddress2
	 * @param shippingAlternativeZip
	 * @param shippingAlternativeCity
	 * @param shippingAlternativePhone
	 * @param shippingAlternativeRecipient
	 * @param shippingContact
	 * @param shippingPeriod
	 * @return
	 * @throws ManagerBeanException
	 */
	public Purchase createPurchase(String series, Supplier supplier,
			WorkPlace workPlace, PurchaseDocumentType documentType,
			String comments, String remarks, Carrier carrier, String purchaseReference,
			String shippingAlternativeAddress,
			String shippingAlternativeAddress2, String shippingAlternativeZip,
			String shippingAlternativeCity, String shippingAlternativePhone,
			String shippingAlternativeRecipient, String shippingContact,
			ShipmentPeriod shippingPeriod) throws ManagerBeanException {

		IManagerBean bean = BeanManager.getManagerBean(Purchase.class);
		Purchase pur = new Purchase();
		pur.setNumberOfPayments(1);
		pur.setDaysToFirstPayment(0);
		pur.setDaysBetweenPayments(0);
		pur.setPaymentDays("");
		pur.setSupplier(supplier);
		pur.setWorkPlace(workPlace);
		pur.setIssueDate(new Date());
		pur.setStatus(PurchaseStatus.PENDING);
		pur.setDocumentType(documentType);
		pur.setRegistryAddress(supplier.getRegistry().getDefaultAddress());
		pur.setSecurityLevel(SecurityLevel.OFFICIAL);
		if(series == null){
			series = obtainWorkPlaceSerie(workPlace);
		}
		pur.setSeries(series);
		pur.setNumber(obtainSeriesMaxNumber(series));
		pur.setScope(supplier.getScope());
	    pur.setComments(comments);
		pur.setRemarks(remarks);
		pur.setPurchaseReference(purchaseReference);
		
		// shipment data
		pur.setCarrier(carrier);
		pur.setShippingAlternativeAddress(shippingAlternativeAddress);
		pur.setShippingAlternativeAddress2(shippingAlternativeAddress2);
		pur.setShippingAlternativeZip(shippingAlternativeZip);
		pur.setShippingAlternativeCity(shippingAlternativeCity);
		pur.setShippingAlternativePhone(shippingAlternativePhone);
		pur.setShippingAlternativeRecipient(shippingAlternativeRecipient);
		pur.setShippingContact(shippingContact);
		pur.setShippingPeriod(shippingPeriod);
		
		bean.restoreNullSubPOJOs(pur);
		return (Purchase) bean.insert(pur);
	}
	
	public void insertPurchaseDetail(Purchase purchase, ProposalDetail proposalDetail) throws ManagerBeanException {
		Project project = (purchase.getProject() != null && purchase.getProject().getId() != null) ? purchase.getProject() : null;
		double quantity = (proposalDetail.getProposal().isItemReturn()?-1:1)*proposalDetail.getQuantity();
		DiscountExpression discountExpression = null;
		if(proposalDetail.getDiscountExpr()!=null){
			discountExpression = new DiscountExpression(proposalDetail.getDiscountExpr());
		}
		createPurchaseDetail(purchase, proposalDetail.getItem(), project,
				proposalDetail, calculateNextLine(purchase), proposalDetail
						.getItem().getProduct().getName(), quantity,
				proposalDetail.getPrice(), discountExpression, 0,
				PurchaseDetailStatus.PENDING, 0);
	}
	
	public void createPurchaseDetail(Purchase purchase, Item item,
			Project project, ProposalDetail proposalDetail, Integer line,
			String description, double quantity, double price,
			DiscountExpression discountExpression, double taxes,
			PurchaseDetailStatus status, double delivered)
			throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(PurchaseDetail.class);
		PurchaseDetail detail = new PurchaseDetail();

		detail.setItem(item);
		detail.setPurchase(purchase);
		detail.setProject(project);
		detail.setProposalDetail(proposalDetail);
		detail.setLine(calculateNextLine(purchase));
		detail.setDescription(description);
		detail.setQuantity(quantity);
		detail.setPrice(price);
		detail.setDiscountExpression(discountExpression);
		detail.setTaxes(taxes);
		detail.setStatus(PurchaseDetailStatus.PENDING);
		detail.setDelivered(delivered);

		bean.insert(detail);
	}
	
	public void updateProposalDetailStatus(Integer proposalDetailId) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ProposalDetail.class);
		ProposalDetail pd = (ProposalDetail) bean.get(proposalDetailId);
		pd.setStatus(ProposalDetailStatus.PROCESSED);
		bean.update(pd);
	}
	
	private String obtainWorkPlaceSerie(WorkPlace workPlace) throws ManagerBeanException {
		List<ITransferObject> seriesList = getWorkPlaceSeries(workPlace);
		return (seriesList.size() > 0) ? ((Series)seriesList.get(0)).getCode() : "";
	}
	
	private int obtainSeriesMaxNumber(String seriesId) throws ManagerBeanException {
		return SeriesNumberUtil.obtainNumber(seriesId, "Purchase", null);
	}
	
	public Integer calculateNextLine(Purchase purchase) throws ManagerBeanException {
		IManagerBean purchaseDetailBean = BeanManager.getManagerBean(PurchaseDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(purchaseDetailBean.getFieldName(IEntityAlias.PURCHASE_DETAIL_PURCHASE_ID), purchase.getId());
		Projection projection = Projection.max(purchaseDetailBean.getFieldName(IEntityAlias.PURCHASE_DETAIL_LINE));
		Object value = purchaseDetailBean.getUniqueResult(projection, criteria);
		return (value != null) ? ((Integer)value) + 1 : 1;
	}
	
	private List<ITransferObject> getWorkPlaceSeries(WorkPlace workPlace) throws ManagerBeanException {
		IManagerBean seriesBean = BeanManager.getManagerBean(Series.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_SCOPE_ID), workPlace.getScope().getId());
		criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_ACTIVE), new Boolean(true));
		criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_SECURITY_LEVEL), SecurityLevel.OFFICIAL);
		criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_INVOICE), new Boolean(true));
		return seriesBean.getList(criteria);
	}
	
	/**
	 * Se recupera el supplier cuyo id coincide con el de company
	 * @return
	 */
	public Supplier getCompanySupplier() {
		if(companySupplier == null) {
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

}
