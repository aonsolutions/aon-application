package com.code.aon.ui.sales.util;

import static com.esferalia.aon.jooq.tables.Sales.SALES;

import java.sql.Connection;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.AbortProcessingException;

import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.code.aon.commercial.OfferDetail;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.company.WorkPlace;
import com.code.aon.config.BankAccount;
import com.code.aon.config.PayMethod;
import com.code.aon.config.Series;
import com.code.aon.config.util.SeriesNumberUtil;
import com.code.aon.customer.Customer;
import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.pool.AonConnectionException;
import com.code.aon.product.Item;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.project.Project;
import com.code.aon.purchase.PurchaseDetail;
import com.code.aon.purchase.enumeration.PurchaseDocumentType;
import com.code.aon.purchase.enumeration.PurchaseSource;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.sales.Sales;
import com.code.aon.sales.SalesDetail;
import com.code.aon.sales.enumeration.DocumentType;
import com.code.aon.sales.enumeration.SalesDetailStatus;
import com.code.aon.sales.enumeration.SalesStatus;
import com.code.aon.seller.Seller;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.carrier.Carrier;
import com.esferalia.aon.carrier.enumeration.ShipmentPeriod;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.type.PurchaseSourceType;
import com.esferalia.aon.occam.api.model.type.PurchaseStatus;
import com.esferalia.aon.occam.impl.jooq.dao.PurchaseDAO;

public class SalesUtils {
	

	/**
	 * Create a purchase with some default values: 
	 * _ pending status
	 * _ official securityLevel
	 * _ not purchaseGenerated
	 * 
	 */
	public Sales createSales(String series, Seller seller, Customer customer, Project project,
			WorkPlace workPlace, DocumentType documentType, Date issueDate, DiscountExpression discountExpr,
			int numberOfPayments, int daysToFirstPayment, int daysBetweenPayments, String paymentDays, 
			PayMethod payMethod, BankAccount bankAccount, String bankAlias, String bic,
			String comments, String remarks, Carrier carrier, String purchaseReference,
			String shippingAlternativeAddress, String shippingAlternativeAddress2, String shippingAlternativeZip,
			String shippingAlternativeCity, String shippingAlternativePhone, String shippingAlternativeRecipient, 
			String shippingContact, ShipmentPeriod shippingPeriod) throws ManagerBeanException {
		return createSales(series, seller, customer, project,
				workPlace, documentType, issueDate, discountExpr,
				numberOfPayments, daysToFirstPayment, daysBetweenPayments, paymentDays, payMethod, false,
				bankAccount, bankAlias, bic,
				comments, remarks, carrier, purchaseReference, SalesStatus.PENDING, SecurityLevel.OFFICIAL,
				shippingAlternativeAddress,
				shippingAlternativeAddress2, shippingAlternativeZip,
				shippingAlternativeCity, shippingAlternativePhone,
				shippingAlternativeRecipient, shippingContact,
				shippingPeriod);
	}

	/**
	 * Create a new sales object
	 * @param series
	 * @param seller
	 * @param customer
	 * @param project
	 * @param workPlace
	 * @param documentType
	 * @param numberOfPayments
	 * @param daysToFirstPayment
	 * @param daysBetweenPayments
	 * @param paymentDays
	 * @param payMethod
	 * @param issueDate
	 * @param discountExpr
	 * @param bankAccount
	 * @param bankAlias
	 * @param bic
	 * @param comments
	 * @param remarks
	 * @param carrier
	 * @param purchaseReference
	 * @param status
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
	public Sales createSales(String series, Seller seller, Customer customer, Project project,
			WorkPlace workPlace, DocumentType documentType, Date issueDate, DiscountExpression discountExpr,
			int numberOfPayments, int daysToFirstPayment, int daysBetweenPayments, String paymentDays, PayMethod payMethod, boolean purchaseGenerated,
			BankAccount bankAccount, String bankAlias, String bic,
			String comments, String remarks, Carrier carrier, String purchaseReference, SalesStatus status, SecurityLevel securityLevel,
			String shippingAlternativeAddress,
			String shippingAlternativeAddress2, String shippingAlternativeZip,
			String shippingAlternativeCity, String shippingAlternativePhone,
			String shippingAlternativeRecipient, String shippingContact,
			ShipmentPeriod shippingPeriod) throws ManagerBeanException {

		IManagerBean bean = BeanManager.getManagerBean(Sales.class);
		Sales sales = new Sales();
		sales.setNumberOfPayments(numberOfPayments);
		sales.setDaysToFirstPayment(daysToFirstPayment);
		sales.setDaysBetweenPayments(daysBetweenPayments);
		sales.setPaymentDays(paymentDays);
		sales.setPayMethod(payMethod);
		sales.setDiscountExpression(discountExpr);
		sales.setSeller(seller);
		sales.setCustomer(customer);
		sales.setProject(project);
		sales.setWorkPlace(workPlace);
		sales.setIssueDate(issueDate);
		sales.setStatus(status);
		sales.setDocumentType(documentType);
		sales.setShippingAddress(customer.getRegistry().getDefaultAddress());
		sales.setSecurityLevel(securityLevel);
		if(series == null){
			series = obtainWorkPlaceSerie(workPlace);
		}
		sales.setSeries(series);
		sales.setNumber(obtainSeriesMaxNumber(series));
		sales.setScope(customer.getScope());
		sales.setComments(comments);
		sales.setRemarks(remarks);
		sales.setPurchaseReference(purchaseReference);
		sales.setPurchaseGenerated(purchaseGenerated);
		sales.setBankAccount(bankAccount);
		sales.setBankAlias(bankAlias);
		sales.setBic(bic);
		
		// shipment data
		sales.setCarrier(carrier);
		sales.setShippingAlternativeAddress(shippingAlternativeAddress);
		sales.setShippingAlternativeAddress2(shippingAlternativeAddress2);
		sales.setShippingAlternativeZip(shippingAlternativeZip);
		sales.setShippingAlternativeCity(shippingAlternativeCity);
		sales.setShippingAlternativePhone(shippingAlternativePhone);
		sales.setShippingAlternativeRecipient(shippingAlternativeRecipient);
		sales.setShippingContact(shippingContact);
		sales.setShippingPeriod(shippingPeriod);
		
		bean.restoreNullSubPOJOs(sales);
		return (Sales) bean.insert(sales);
	}
	
	public void createSalesDetail(Sales sales, Item item, Integer line, String description, 
			OfferDetail offerDetail, SalesDetailStatus status, DiscountExpression discountExpression, 
			double quantity, double price, double taxes, double delivered, double transfered)
			throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(SalesDetail.class);
		SalesDetail detail = new SalesDetail();
		detail.setItem(item);
		detail.setSales(sales);
		detail.setLine(calculateNextLine(sales));
		detail.setDescription(description);
		detail.setQuantity(quantity);
		detail.setPrice(price);
		detail.setDiscountExpression(discountExpression);
		detail.setTaxes(taxes);
		detail.setStatus(status);
		detail.setDelivered(delivered);
		detail.setOfferDetail(offerDetail);
		detail.setTransfered(transfered);
		
		bean.restoreNullSubPOJOs(detail);
		bean.insert(detail);
	}
	
	public String obtainWorkPlaceSerie(WorkPlace workPlace) throws ManagerBeanException {
		List<ITransferObject> seriesList = getWorkPlaceSeries(workPlace);
		return (seriesList.size() > 0) ? ((Series)seriesList.get(0)).getCode() : "";
	}
	
	public int obtainSeriesMaxNumber(String seriesId) throws ManagerBeanException {
		return SeriesNumberUtil.obtainNumber(seriesId, "Sales", null);
	}
	
	public Integer calculateNextLine(Sales sales) throws ManagerBeanException {
		IManagerBean salesDetailBean = BeanManager.getManagerBean(SalesDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(salesDetailBean.getFieldName(IEntityAlias.SALES_DETAIL_SALES_ID), sales.getId());
		Projection projection = Projection.max(salesDetailBean.getFieldName(IEntityAlias.SALES_DETAIL_LINE));
		Object value = salesDetailBean.getUniqueResult(projection, criteria);
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
	

	
	public Result<Record2<Integer, String>> getSalesRecord(String purchaseReference) {
		return getSalesRecords(Arrays.asList(purchaseReference));
	}
	
	public Result<Record2<Integer, String>> getSalesRecords(
			Collection<String> purchaseReferenceList) {
		Connection connection = null;
		try {
			connection = DatabaseUtil.getConnection(AonUtil.getDomainName());
			Settings SETTINGS = null;
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
			DSLContext ctx = DSL.using(connection, SETTINGS);
			Result<Record2<Integer, String>> record = ctx
					.select(SALES.ID, SALES.PURCHASE_REFERENCE)
					.from(SALES)
					.where(SALES.DOMAIN.equal(DomainManager.getCurrentDomain()))
					.and(SALES.PURCHASE_REFERENCE.in(purchaseReferenceList)).fetch();
			return record;
		} catch (AonConnectionException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		} finally {
			DatabaseUtil.closeQuietly(connection);
		}
	}
	
	public void createManufacturingOrder(Sales sales) {
		AONContext ctx = AONContext
				.getAONContext(AonUtil.getDomainName(), sales.getDomain(), AonUtil.getRemoteUser());
		
		com.esferalia.aon.occam.api.model.management.Purchase p = new com.esferalia.aon.occam.api.model.management.Purchase();
		p.setDomain(sales.getDomain());
		p.setProject(sales.getProject() != null ? sales
				.getProject().getId() : null);
		p.setSeries(sales.getSeries());
		p.setPurchaseReference(sales.getPurchaseReference());
		p.setAddress(null);
		p.setDiscountExpr(sales.getDiscountExpression() != null ? sales
				.getDiscountExpression().getDiscountExpr()
				: null);
		p.setIssueDate(new Date());
		p.setPayMethod(null);
		p.setSecurityLevel(sales.getSecurityLevel()
				.ordinal());
		p.setStatus(PurchaseStatus.PENDING);
		p.setComments(sales.getComments());
		p.setRemarks(sales.getRemarks());
		p.setWorkplace(sales.getWorkPlace() != null ? sales
				.getWorkPlace().getId() : null);
		p.setWarehouse(null);
		p.setScope(sales.getScope().getId());
		p.setNumberOfPymnts(0);
		p.setDaysToFirstPymnt(0);
		p.setDaysBetweenPymnts(0);
		p.setPymntDays("0");
		p.setBankAccount(null);
		p.setBankAlias(null);
		p.setBic(null);
		p.setEmailCommunication(false);
		p.setCarrier(null);
		p.setShippingAlternativeAddress(null);
		p.setShippingAlternativeAddress2(null);
		p.setShippingAlternativeZip(null);
		p.setShippingAlternativeCity(null);
		p.setShippingAlternativePhone(null);
		p.setShippingAlternativeRecipient(null);
		p.setShippingContact(null);
		p.setShippingPeriod(0);
		
		int purchaseId = PurchaseDAO.insertManufacturePurchase(ctx, p);
		ctx.getDslContext().transaction(configuration -> {
			createPurchaseLines(ctx, sales.getDetailList(), purchaseId);
		});
	}
	
	public void createPurchaseLines(AONContext ctx,
			List<ITransferObject> list, Integer purchaseId) throws ManagerBeanException {
		list.stream()
				.map(to -> (SalesDetail) to)
				.filter(detail -> detail.getItem().getProduct().isManufactured()
						&& !isManufactureDone(detail))
				.forEach(
						detail -> {
							com.esferalia.aon.occam.api.model.management.PurchaseDetail pd = new com.esferalia.aon.occam.api.model.management.PurchaseDetail(); 
							pd.setDomain(detail.getDomain());
							pd.setPurchase(purchaseId);
							pd.setProject(null);
							pd.setLine(detail.getLine());
							pd.setItem(detail.getItem().getId());
							pd.setDescription(detail.getDescription());
							pd.setQuantity(detail.getQuantity());
							pd.setPrice(detail.getPrice());
							pd.setDiscountExpression(detail.getDiscountExpression().getDiscountExpr());
							pd.setTaxes(detail.getTaxes());
							pd.setStatus(com.esferalia.aon.occam.api.model.type.PurchaseDetailStatus.valueOf(detail.getStatus().name()));
							pd.setProposalDetail(null);
							pd.setDelivered(detail.getDelivered());
							pd.setSource(PurchaseSourceType.SALES);
							pd.setSourceId(detail.getId());
							PurchaseDAO.insertPurchaseDetail(ctx, pd);
						});
	}
	
	public boolean isManufactureDone(SalesDetail detail) {
		try {
			PurchaseDetail purchaseDetail = getTargetManufactureDetail(detail);
			return purchaseDetail!=null && purchaseDetail.getId()!=null;
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean isPurchased(SalesDetail detail) {
		try {
			PurchaseDetail purchaseDetail = getTargetPurchaseDetail(detail);
			return purchaseDetail!=null && purchaseDetail.getId()!=null;
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public PurchaseDetail getTargetManufactureDetail(SalesDetail detail) throws ManagerBeanException {
		IManagerBean purchaseDetailBean = BeanManager.getManagerBean(PurchaseDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(purchaseDetailBean.getFieldName(IEntityAlias.PURCHASE_DETAIL_SOURCE), PurchaseSource.SALES);
		criteria.addEqualExpression(purchaseDetailBean.getFieldName(IEntityAlias.PURCHASE_DETAIL_SOURCE_ID), detail.getId());
		criteria.addEqualExpression(purchaseDetailBean.getFieldName(IEntityAlias.PURCHASE_DETAIL_PURCHASE_DOCUMENT_TYPE), PurchaseDocumentType.MANUFACTURE);
		criteria.addOrder(purchaseDetailBean.getFieldName(IEntityAlias.PURCHASE_DETAIL_ID), false);
		Iterator<?> iterator = purchaseDetailBean.getList(criteria).iterator();
		if (iterator.hasNext()) {
			return (PurchaseDetail)iterator.next();
		}
		return null;
	}
	
	public PurchaseDetail getTargetPurchaseDetail(SalesDetail detail) throws ManagerBeanException {
		IManagerBean purchaseDetailBean = BeanManager.getManagerBean(PurchaseDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(purchaseDetailBean.getFieldName(IEntityAlias.PURCHASE_DETAIL_SOURCE), PurchaseSource.SALES);
		criteria.addEqualExpression(purchaseDetailBean.getFieldName(IEntityAlias.PURCHASE_DETAIL_SOURCE_ID), detail.getId());
		criteria.addEqualExpression(purchaseDetailBean.getFieldName(IEntityAlias.PURCHASE_DETAIL_PURCHASE_DOCUMENT_TYPE), PurchaseDocumentType.NORMAL);
		criteria.addOrder(purchaseDetailBean.getFieldName(IEntityAlias.PURCHASE_DETAIL_ID), false);
		Iterator<?> iterator = purchaseDetailBean.getList(criteria).iterator();
		if (iterator.hasNext()) {
			return (PurchaseDetail)iterator.next();
		}
		return null;
	}
	
}
