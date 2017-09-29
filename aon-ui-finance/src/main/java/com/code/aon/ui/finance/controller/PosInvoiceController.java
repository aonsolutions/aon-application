package com.code.aon.ui.finance.controller;

import static com.code.aon.ui.common.ICommonMessages.DECIMAL_2_PATTERN;
import static com.code.aon.ui.common.ICommonMessages.FINANCE_CHARGED;
import static com.code.aon.ui.common.ICommonMessages.TIMESTAMP_PATTERN;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.Tag;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.customer.Customer;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceAddress;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.PosCatalogue;
import com.code.aon.finance.PosShift;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.product.CatalogueItem;
import com.code.aon.product.Item;
import com.code.aon.product.Product;
import com.code.aon.product.ProductTag;
import com.code.aon.product.enumeration.ProductStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.seller.Seller;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.config.controller.ConfigCollectionsController;
import com.code.aon.ui.config.controller.ConfigConstants;
import com.code.aon.ui.finance.util.PosUtils;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class PosInvoiceController extends SaleInvoiceController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private PosShift posShift;
	private Customer defaultCustomer;
	private Seller seller;
	private List<ProductTag> productTags;
	private List<Tag> tags;
	private List<Product> selectedProducts;
	private List<Tag> pagedTags;
	private List<Product> pagedSelectedProducts;
	private Integer tagPage;
	private Integer selectedProductPage;
	private List<Invoice> suspendedInvoiceList;
	private boolean showFinishTicketWindow;
	private boolean showRecoverTicketWindow;
	private String recoverReferenceCode;

	public PosInvoiceController() {
		setInvoiceAddressControllerName(POS_INVOICE_ADDRESS_CONTROLLER_NAME);
		setInvoiceDetailControllerName(POS_INVOICE_DETAIL_CONTROLLER_NAME);
		setInvoiceFinanceControllerName(POS_INVOICE_FINANCE_CONTROLLER_NAME);
	}

	public PosShift getPosShift() {
		return posShift;
	}

	public void setPosShift(PosShift posShift) {
		this.posShift = posShift;
	}

	public Customer getDefaultCustomer() {
		return defaultCustomer;
	}

	public void setDefaultCustomer(Customer defaultCustomer) {
		this.defaultCustomer = defaultCustomer;
	}

	public Seller getSeller() {
		return seller;
	}

	public void setSeller(Seller seller) {
		this.seller = seller;
	}

	public Integer getTagPage() {
		return tagPage;
	}

	public void setTagPage(Integer tagPage) {
		this.tagPage = tagPage;
	}

	public Integer getSelectedProductPage() {
		return selectedProductPage;
	}

	public void setSelectedProductPage(Integer selectedProductPage) {
		this.selectedProductPage = selectedProductPage;
	}

	public boolean isShowFinishTicketWindow() {
		return showFinishTicketWindow;
	}

	public void setShowFinishTicketWindow(boolean value) {
		this.showFinishTicketWindow = value;
	}

	public boolean isShowRecoverTicketWindow() {
		return showRecoverTicketWindow;
	}

	public void setShowRecoverTicketWindow(boolean value) {
		this.showRecoverTicketWindow = value;
	}

	public String getRecoverReferenceCode() {
		return recoverReferenceCode;
	}

	public void setRecoverReferenceCode(String recoverReferenceCode) {
		this.recoverReferenceCode = recoverReferenceCode;
	}

	public void onLoad(ActionEvent event) throws ManagerBeanException {
		setPosShift(PosUtils.getUserPosShift());
		if (getPosShift() == null) {
			String msg = "No se puede Facturar. El Usuario no ha abierto la Caja.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		setSeller((Seller)BeanManager.getManagerBean(Seller.class).createNewTo());

		onReset(event);
		FormUtil.getController(getInvoiceDetailControllerName()).onReset(null);
		resetTagsView();
	}

	private void resetTagsView() {
		setProductTags(null);
		setTags(null);
		setSelectedProducts(null);
		setPagedTags(null);
		setPagedSelectedProducts(null);
		setTagPage(1);
		setSelectedProductPage(1);
	}

	@Override
	public void onReset(ActionEvent event) {
		super.onReset(event);
		setSuspendedInvoiceList(null);
	}

	@Override
	public void accept(ActionEvent event) {
		super.accept(event);
		FormUtil.getController(getInvoiceDetailControllerName()).onReset(event);
	}

	@Override
	public void acceptInvoice(ActionEvent event) {
		super.acceptInvoice(event);
		FormUtil.getController(getInvoiceDetailControllerName()).onReset(event);
	}

	@Override
	public void refresh(ActionEvent event) throws ManagerBeanException {
		super.refresh(event);
		FormUtil.getController(getInvoiceDetailControllerName()).onSearch(event);
		FormUtil.getController(getInvoiceDetailControllerName()).onReset(event);
	}

	public List<ProductTag> getProductTags() {
		if (productTags == null && getPosShift().getPos().isTouchScreen()) {
			productTags = new LinkedList<ProductTag>();
			try {
				IManagerBean posCatalogueBean = BeanManager.getManagerBean(PosCatalogue.class);
				IManagerBean catalogueItemBean = BeanManager.getManagerBean(CatalogueItem.class);
				IManagerBean productTagBean = BeanManager.getManagerBean(ProductTag.class);

				Criteria criteria = new Criteria();
				criteria.addEqualExpression(posCatalogueBean.getFieldName(IEntityAlias.POS_CATALOGUE_POS_ID), getPosShift().getPos().getId());
				criteria.addLessThanOrEqualExpression(posCatalogueBean.getFieldName(IEntityAlias.POS_CATALOGUE_CATALOGUE_START_DATE), getInvoice().getIssueDate());
				Expression dateExpr = ExpressionUtilities.getGreaterThanOrEqualExpression(posCatalogueBean.getFieldName(IEntityAlias.POS_CATALOGUE_CATALOGUE_END_DATE), getInvoice().getIssueDate());
				Expression nullExpr = ExpressionUtilities.getNullExpression(posCatalogueBean.getFieldName(IEntityAlias.POS_CATALOGUE_CATALOGUE_END_DATE));
				criteria.addExpression(ExpressionUtilities.getOrExpression(dateExpr, nullExpr));
				for (ITransferObject ito : posCatalogueBean.getList(criteria)) {
					PosCatalogue posCatalogue = (PosCatalogue)ito;
					criteria = new Criteria();
					criteria.addEqualExpression(catalogueItemBean.getFieldName(IEntityAlias.CATALOGUE_ITEM_CATALOGUE_ID), posCatalogue.getCatalogue().getId());
					criteria.addNotNullExpression(catalogueItemBean.getFieldName(IEntityAlias.CATALOGUE_ITEM_ITEM));
					criteria.addOrder(catalogueItemBean.getFieldName(IEntityAlias.CATALOGUE_ITEM_PRODUCT_NAME));
					criteria.addOrder(catalogueItemBean.getFieldName(IEntityAlias.CATALOGUE_ITEM_ITEM_DETAIL));
					criteria.addOrder(catalogueItemBean.getFieldName(IEntityAlias.CATALOGUE_ITEM_ITEM_DETAIL2));
					criteria.addOrder(catalogueItemBean.getFieldName(IEntityAlias.CATALOGUE_ITEM_ITEM_DETAIL3));
					criteria.addOrder(catalogueItemBean.getFieldName(IEntityAlias.CATALOGUE_ITEM_ITEM_SERIAL_NUMBER));
					for (ITransferObject itr : catalogueItemBean.getList(criteria)) {
						CatalogueItem catalogueItem = (CatalogueItem)itr;
						if (catalogueItem.getItem().isActive()) {
							criteria = new Criteria();
							criteria.addEqualExpression(productTagBean.getFieldName(IEntityAlias.PRODUCT_TAG_PRODUCT_ID), catalogueItem.getItem().getProduct().getId());
							for (ITransferObject itt : productTagBean.getList(criteria)) {
								ProductTag productTag = (ProductTag)itt;
								if (!productTags.contains(productTag)) {
									productTags.add(productTag);
								}
							}
						}
					}
				}
			} catch (ManagerBeanException ex) {
				String msg = "Error obteniendo la lista de Productos disponibles.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
		}
		return productTags;
	}

	public void setProductTags(List<ProductTag> productTags) {
		this.productTags = productTags;
	}

	public List<Tag> getTags() {
		if (tags == null && getPosShift().getPos().isTouchScreen()) {
			tags = new LinkedList<Tag>();
			for (ProductTag productTag : getProductTags()) {
				if (!tags.contains(productTag.getTag())) {
					tags.add(productTag.getTag());
				}
			}

			class TagComparator implements Comparator<ITransferObject> {
				public int compare(ITransferObject o1, ITransferObject o2) {
					if (o1 instanceof Tag && o2 instanceof Tag) {
						Tag tag1 = (Tag)o1;
						Tag tag2 = (Tag)o2;
						return tag1.getName().compareTo(tag2.getName());
					}
					return 0;
				}
			}
			Collections.sort(tags, new TagComparator());
		}
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	public int getTagsCount() {
		return (getTags() == null) ? 0 : getTags().size();
	}

	public int getTagsNumCols() {
		double cols = (double)getTagsCount() / (double)getPosShift().getPos().getNumRows();
		return (cols > 2) ? 3 : ((cols > 1) ? 2 : 1);
	}

	public int getTagsLimit() {
		return getTagsNumCols() * getPosShift().getPos().getNumRows();
	}

	public List<Product> getSelectedProducts() {
		return selectedProducts;
	}

	public void setSelectedProducts(List<Product> selectedProducts) {
		this.selectedProducts = selectedProducts;
	}

	public int getSelectedProductsCount() {
		return (getSelectedProducts() == null) ? 0 : getSelectedProducts().size();
	}

	public List<Tag> getPagedTags() {
		if (pagedTags == null && getPosShift().getPos().isTouchScreen()) {
			int tagsLimit = getTagsLimit();
			List<Tag> subList = null;
			if (getTagsCount() <= tagsLimit) {
				subList = getTags().subList(0, getTagsCount());
			} else {
				int fromIndex = (getTagPage() == 1) ? 0 : tagsLimit * (getTagPage() - 1) - (2 * (getTagPage() - 1) - 1);
				int toIndex = (getTagPage() == 1) ? tagsLimit - 1 : fromIndex + tagsLimit - 2;
				subList = getTags().subList(fromIndex, (toIndex < getTagsCount()) ? toIndex : getTagsCount());
			}
			pagedTags = new LinkedList<Tag>(subList);
		}
		return pagedTags;
	}

	public void setPagedTags(List<Tag> pagedTags) {
		this.pagedTags = pagedTags;	
	}

	public int getPagedTagsCount() {
		return (getPagedTags() == null) ? 0 : getPagedTags().size();
	}

	public void onPreviousTagPage(ActionEvent event) {
		setTagPage(getTagPage() - 1);
		setPagedTags(null);
	}

	public void onNextTagPage(ActionEvent event) {
		setTagPage(getTagPage() + 1);
		setPagedTags(null);
	}

	public void onSelectTag(ActionEvent event) {
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		Map<String, String> params = ec.getRequestParameterMap();
		Integer selectedTag = new Integer(params.get("selectedTag"));

		setPagedSelectedProducts(null);
		setSelectedProducts(new LinkedList<Product>());
		for (ProductTag productTag : getProductTags()) {
			if (productTag.getTag().getId().equals(selectedTag)) {
				getSelectedProducts().add(productTag.getProduct());
			}
		}
	}

	public List<Product> getPagedSelectedProducts() {
		if (pagedSelectedProducts == null && selectedProducts != null && getPosShift().getPos().isTouchScreen()) {
			int posLimit = getPosShift().getPos().getLimit();
			List<Product> subList = null;
			if (getSelectedProductsCount() <= posLimit) {
				subList = getSelectedProducts().subList(0, getSelectedProductsCount());
			} else {
				int fromIndex = (getSelectedProductPage() == 1) ? 0 : posLimit * (getSelectedProductPage() - 1) - (2 * (getSelectedProductPage() - 1) - 1);
				int toIndex = (getSelectedProductPage() == 1) ? posLimit - 1 : fromIndex + posLimit - 2;
				subList = getSelectedProducts().subList(fromIndex, (toIndex < getSelectedProductsCount()) ? toIndex : getSelectedProductsCount());
			}
			pagedSelectedProducts = new LinkedList<Product>(subList);
		}
		return pagedSelectedProducts;
	}

	public void setPagedSelectedProducts(List<Product> pagedSelectedProducts) {
		this.pagedSelectedProducts = pagedSelectedProducts;
	}

	public int getPagedSelectedProductsCount() {
		return (getPagedSelectedProducts() == null) ? 0 : getPagedSelectedProducts().size();
	}

	public void onPreviousSelectedProductPage(ActionEvent event) {
		setSelectedProductPage(getSelectedProductPage() - 1);
		setPagedSelectedProducts(null);
	}

	public void onNextSelectedProductPage(ActionEvent event) {
		setSelectedProductPage(getSelectedProductPage() + 1);
		setPagedSelectedProducts(null);
	}

	public void onSelectProduct(ActionEvent event) {
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		Map<String, String> params = ec.getRequestParameterMap();
		Item item = obtainItem(new Integer(params.get("selectedProduct")));

		InvoiceDetail invoiceDetail = (InvoiceDetail)FormUtil.getController(getInvoiceDetailControllerName()).getTo();
		invoiceDetail.setItem(item);
		invoiceDetail.setDescription(item.getFullName());
		invoiceDetail.setPrice(item.getPrice());
		FormUtil.getController(getInvoiceDetailControllerName()).onAccept(event);
	}

	private Item obtainItem(Integer productId) {
		try {
			IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_PRODUCT_ID), productId);
			criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_STATUS), ProductStatus.ACTIVE);
			for (ITransferObject ito : itemBean.getList(criteria)) {
				return (Item)ito;
			}
		} catch (ManagerBeanException ex) {
			String msg = "Error obteniendo la lista de Productos disponibles.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		return null;
	}

	public DataModel getTicketModel() {
		List<Invoice> ticketList = new LinkedList<Invoice>();
		ticketList.add(getInvoice());
		return new SerializableListDataModel(ticketList);
	}

	public void onNewTicket(ActionEvent event) {
		try {
			if (!isNevv() && getInvoice().getDetailList().size() == 0) {
				getManagerBean().remove(getInvoice());
			}
		} catch (ManagerBeanException ex) {
			String msg = "Error al Borrar Factura vacia.";
			AonUtil.addErrorMessage(msg);
		}

		onReset(event);

		Invoice invoice = getInvoice();
		if (invoice.getRegistry() == null || invoice.getRegistry().getId() == null) {
			String msg = "No hay Cliente Contado definido.";
			AonUtil.addErrorMessage(msg);
		} else {
			accept(event);
		}
	}

	public void onShowFinishTicket(ActionEvent event) {
		PosInvoiceFinanceController financeController = (PosInvoiceFinanceController)FormUtil.getController(getInvoiceFinanceControllerName());
		financeController.setPendingAmount(getPendingAmount());
		financeController.resetFinances();
		financeController.onNewFinance(event);
	}

	public void onFinishTicket(ActionEvent event) {
		PosInvoiceFinanceController financeController = (PosInvoiceFinanceController)FormUtil.getController(getInvoiceFinanceControllerName());
		if (!financeController.isFinancesPayMethodOk()) {
			String msg = "Especifique la Forma de Pago.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		if (!financeController.isFinancesAmountOk()) {
			String msg = "El Importe de los Pagos es incorrecto.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}

		double returnChange = financeController.getFinancesCashChange();
		double totalAmount = 0;
		Invoice invoice = getInvoice();
		for (Finance finance : financeController.getFinances()) {
			finance.setPayment(false);
			finance.setInvoice(invoice);
			finance.setRegistry(invoice.getRegistry());
			finance.setRegistryName(invoice.getRegistryName());
			finance.setRegistryDocument(invoice.getRegistryDocument());
			finance.setRegistryDocumentType(invoice.getRegistryDocumentType());
			finance.setRegistryDocumentCountry(invoice.getRegistryDocumentCountry());
	        finance.setConcept(invoice.getDocumentNumber()); 
			finance.setDueDate(invoice.getIssueDate());
			finance.setSecurityLevel(invoice.getSecurityLevel());
			finance.setFinanceStatus(FinanceStatus.PENDING);
			if (finance.getPayMethod().getType() == PayMethodType.CASH_BASIS && returnChange > 0) {
				finance.setAmount(CommonUtil.round(financeController.getFinancesCashAmount() - returnChange));
			}
			if (finance.getAmount() != 0) {
				totalAmount = CommonUtil.round(totalAmount + finance.getAmount());
				try {
					BeanManager.getManagerBean(Finance.class).insert(finance);
				} catch (ManagerBeanException ex) {
					String msg = "Error al cobrar la Factura.";
					AonUtil.addErrorMessage(msg);
					throw new AbortProcessingException(msg);
				}
			}
		}

		NumberFormat numberFormat = new DecimalFormat(AonUtil.getMessage(DECIMAL_2_PATTERN));
		DateFormat dateFormat = new SimpleDateFormat(AonUtil.getMessage(TIMESTAMP_PATTERN));
		String comments = StringUtils.isNotBlank(invoice.getComments()) ? invoice.getComments() + "\n" : "";
		comments = comments + dateFormat.format(new Date()) + " - " + AonUtil.getMessage(FINANCE_CHARGED) + ": " + numberFormat.format(totalAmount) + "\n";
		invoice.setComments(comments);
		accept(event);
		FormUtil.getController(getInvoiceFinanceControllerName()).onSearch(null);
	}

	public boolean isTicketFinished() {
		return StringUtils.contains(getInvoice().getComments(), AonUtil.getMessage(FINANCE_CHARGED)) && getPendingAmount() == 0;
	}

	public List<Invoice> getSuspendedInvoiceList() throws ManagerBeanException {
		if (suspendedInvoiceList == null) {
			suspendedInvoiceList = new LinkedList<Invoice>();
			Criteria criteria = new Criteria();
			if (!isNevv()) {
				criteria.addNotEqualExpression(getFieldName(IEntityAlias.INVOICE_ID), getInvoice().getId());
			}
			criteria.addEqualExpression(getFieldName(IEntityAlias.INVOICE_POS_SHIFT_POS_ID), getPosShift().getPos().getId());
			criteria.addEqualExpression(getFieldName(IEntityAlias.INVOICE_TYPE), InvoiceType.SALES);
			criteria.addEqualExpression(getFieldName(IEntityAlias.INVOICE_ISSUE_DATE), new Date());
			criteria.addEqualExpression(getFieldName(IEntityAlias.INVOICE_STATUS), InvoiceStatus.PENDING);
			criteria.addNullExpression(getFieldName(IEntityAlias.INVOICE_COMMENTS));
			for (ITransferObject ito : getManagerBean().getList(criteria)) {
				Invoice invoice = (Invoice)ito;
				suspendedInvoiceList.add(invoice);
			}
		}
		return suspendedInvoiceList;
	}

	public void onSuspendTicket(ActionEvent event) {
		accept(event);
		onNewTicket(event);
	}

	public void setSuspendedInvoiceList(List<Invoice> suspendedInvoiceList) {
		this.suspendedInvoiceList = suspendedInvoiceList;
	}

	public int getSuspendedInvoiceCount() throws ManagerBeanException {
		return getSuspendedInvoiceList().size();
	}

	public void onRecoverSuspendedInvoice(ActionEvent event) {
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		Map<String, String> params = ec.getRequestParameterMap();
		Integer suspendedInvoiceId = new Integer(params.get("suspendedInvoice"));
		recoverTicket(suspendedInvoiceId);
	}

	private void recoverTicket(Integer invoiceId) {
		try {
			load(null, invoiceId);
			FormUtil.getController(getInvoiceDetailControllerName()).onReset(null);
			setSuspendedInvoiceList(null);
		} catch (ManagerBeanException ex) {
			String msg = "Error al recuperar la Factura aparcada.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public void onShowRecoverTicket(ActionEvent event) {
		setRecoverReferenceCode(null);
		setNumberEditable(true);
	}

	public void onRecoverTicket(ActionEvent event) {
		try {
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(getFieldName(IEntityAlias.INVOICE_TYPE), InvoiceType.SALES);
			criteria.addNotNullExpression(getFieldName(IEntityAlias.INVOICE_POS_SHIFT_ID));
			criteria.addEqualExpression(getFieldName(IEntityAlias.INVOICE_REFERENCE_CODE), getRecoverReferenceCode());
			Projection prjId = Projection.property(getFieldName(IEntityAlias.INVOICE_ID));
			List<?> invoiceIdList = getManagerBean().getList(new ProjectionList(prjId), criteria);
			if (invoiceIdList.size() == 0) {
				String msg = "No se ha encontrado la Factura " + getRecoverReferenceCode() + ".";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			} else if (invoiceIdList.size() > 1) {
				String msg = "Error al recuperar la Factura " + getRecoverReferenceCode() + ".";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			} else {
				Integer recoverInvoiceId = (Integer)invoiceIdList.get(0);
				recoverTicket(recoverInvoiceId);
			}
		} catch (ManagerBeanException ex) {
			String msg = "Error al recuperar la Factura " + getRecoverReferenceCode() + ".";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public void onCancelTicket(ActionEvent event) {
		try {
			cancelTicket(true);
			if (!isNevv()) {
				refresh(event);
			} else {
				FormUtil.getController(getInvoiceDetailControllerName()).onReset(event);
			}
		} catch (ManagerBeanException ex) {
			String msg = "Error al cancelar la Factura.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public void onCancelLine(ActionEvent event) {
		try {
			cancelTicket(false);
			if (!isNevv()) {
				refresh(event);
			} else {
				FormUtil.getController(getInvoiceDetailControllerName()).onReset(event);
			}
		} catch (ManagerBeanException ex) {
			String msg = "Error al cancelar la Factura.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	private void cancelTicket(boolean entireTicket) throws ManagerBeanException {
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_ID), getInvoice().getId());
		criteria.addOrder(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_LINE), entireTicket);
		List<ITransferObject> invoiceDetailList = invoiceDetailBean.getList(criteria);
		int line = invoiceDetailList.size();
		for (ITransferObject ito : invoiceDetailList) {
			InvoiceDetail invoiceDetail = (InvoiceDetail)ito;
			invoiceDetail.fillTaxDataInDetail();
			if (entireTicket) {
				invoiceDetail.getInvoice().setUpdateEnabled(invoiceDetailList.indexOf(invoiceDetail) == (invoiceDetailList.size()-1));
			} else {
				if (invoiceDetail.getQuantity() < 0 || line != invoiceDetailList.size()) {
					break;
				}
			}

			invoiceDetail.setId(null);
			invoiceDetail.setLine(++line);
			invoiceDetail.setQuantity(invoiceDetail.getQuantity() * (-1));
			invoiceDetail.setTaxableBase(invoiceDetail.getTaxableBase() * (-1));
			invoiceDetailBean.insert(invoiceDetail);
		}

		if (entireTicket && invoiceDetailList.size() == 0) {
			getManagerBean().remove(getInvoice());
			onReset(null);
		}
	}

	public void onReturnTicket(ActionEvent event) {
		Invoice invoice = getInvoice();
		try {
			Invoice returnInvoice = new Invoice();
			returnInvoice.setProject(invoice.getProject());
			returnInvoice.setSeries(invoice.getSeries());
			returnInvoice.setNumber(obtainMaxNumber(invoice.getSeries()));
			returnInvoice.setRegistry(invoice.getRegistry());
			returnInvoice.setRegistryDocument(invoice.getRegistryDocument());
			returnInvoice.setRegistryDocumentType(invoice.getRegistryDocumentType());
			returnInvoice.setRegistryDocumentCountry(invoice.getRegistryDocumentCountry());
			returnInvoice.setRegistryName(invoice.getRegistryName());
			returnInvoice.setRegistryAddress(invoice.getRegistryAddress());
			returnInvoice.setSecurityLevel(invoice.getSecurityLevel());
			returnInvoice.setStatus(InvoiceStatus.PENDING);
			returnInvoice.setType(InvoiceType.SALES);
			returnInvoice.setSurcharge(invoice.isSurcharge());
			returnInvoice.setWithholding(invoice.isWithholding());
			returnInvoice.setVatAccrualPayment(invoice.isVatAccrualPayment());
			returnInvoice.setTransaction(invoice.getTransaction());
			returnInvoice.setPosShift(invoice.getPosShift());
			returnInvoice.setSeller(getSeller());
			getManagerBean().restoreNullSubPOJOs(returnInvoice);
			returnInvoice = (Invoice)getManagerBean().insert(returnInvoice);

			IController addressController = FormUtil.getController(getInvoiceAddressControllerName());
			InvoiceAddress returnAddress = (InvoiceAddress)addressController.getTo();
			if (returnAddress != null && returnAddress.getId() != null) {
				returnAddress.setId(null);
				returnAddress.setInvoice(returnInvoice);
				addressController.getManagerBean().restoreNullSubPOJOs(returnAddress);
				addressController.getManagerBean().insert(returnAddress);
			}

			int line = 0;
			PosInvoiceDetailController detailController = (PosInvoiceDetailController)FormUtil.getController(getInvoiceDetailControllerName());
			for (InvoiceDetail returnDetail : detailController.getCheckedDetails()) {
				returnDetail.setId(null);
				returnDetail.setInvoice(returnInvoice);
				returnDetail.setLine(++line);
				returnDetail.setQuantity(CommonUtil.round(returnDetail.getQuantity() * (-1), 3));
				returnDetail.setTaxableBase(CommonUtil.round(returnDetail.getTaxableBase() * (-1)));
				returnDetail.fillTaxDataInDetail();
				returnDetail.setVatQuota(CommonUtil.round(returnDetail.getVatQuota() * (-1)));
				returnDetail.setSurchargeQuota(CommonUtil.round(returnDetail.getSurchargeQuota() * (-1)));
				returnDetail.setRetentionQuota(CommonUtil.round(returnDetail.getRetentionQuota() * (-1)));
				returnDetail.setTaxDataInDetail(true);
				returnDetail.setUpdateEnabled(line == detailController.getCheckedCount());
				detailController.getManagerBean().restoreNullSubPOJOs(returnDetail);
				detailController.getManagerBean().insert(returnDetail);
			}

			recoverTicket(returnInvoice.getId());
		} catch (ManagerBeanException ex) {
			String msg = "Error al efectuar la Devolución.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	@Override
	public List<SelectItem> getSeriesCodes() throws ManagerBeanException {
		ConfigCollectionsController ccc = (ConfigCollectionsController) AonUtil.getRegisteredBean(ConfigConstants.CONFIG_COLLECTIONS);
		return ccc.getPosSeriesIds();
	}
	
}