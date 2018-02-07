package com.esferalia.aon.ingenet;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ITransferObject;
import com.code.aon.company.WorkPlace;
import com.code.aon.customer.Customer;
import com.code.aon.product.Item;
import com.code.aon.product.ItemComposition;
import com.code.aon.product.Product;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.sales.Sales;
import com.code.aon.sales.SalesDetail;
import com.esferalia.aon.ingenet.util.IngenetContext;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.type.SalesDetailStatus;
import com.esferalia.aon.occam.api.model.type.SalesStatus;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ProductDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SalesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.WorkplaceDAO;
import com.esferalia.aon.occam.impl.jooq.validation.ProductValidation;
import com.esferalia.aon.watson.error.AonCoreException;

@Deprecated
public class IngenetSalesManager {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(IngenetSalesManager.class.getName());

	private static IngenetSalesManager instance;
	
	
	private IngenetSalesManager(){
		
	}
	
	public static IngenetSalesManager getInstance(){
		if(instance==null){
			instance = new IngenetSalesManager();
		}
		return instance;
	}

	
	public void createSales(String domainName, String user, Sales sales) {
		int domainId = IngenetContext.getUdapaDomainId();
		int scopeId = IngenetContext.getUdapaMainScopeId();
		List<ITransferObject> salesDetailList = sales.getDetailList();

		AONContext ctx = IngenetContext.getAONContext(domainName,
				domainId, user);
		
		ctx.getDslContext().transaction(
				configuration -> {
					Integer customerId = RegistryDAO.getRegistry(ctx, f -> f.getIdProperty().eq(sales.getCustomer().getId())).getId();
					if (sales.getCustomer() != null
							&& customerId==null) {
						createRegistry(ctx, domainId, sales.getCustomer()
								.getRegistry());
						SalesDAO.createCustomer(ctx, domainId, sales
								.getCustomer().getId(), scopeId);
					}
					Integer sellerId = RegistryDAO.getRegistry(ctx, f -> f.getIdProperty().eq(sales.getSeller().getId())).getId();
					if (sales.getSeller() != null
							&& sales.getSeller().getId() != null
							&& sellerId==null) {
						createRegistry(ctx, domainId, sales.getSeller()
								.getRegistry());
						SalesDAO.createSeller(ctx, domainId, sales.getSeller()
								.getId(), scopeId);
					}
					Integer carrierId = RegistryDAO.getRegistry(ctx, f -> f.getIdProperty().eq(sales.getCarrier().getId())).getId();
					if (sales.getCarrier() != null
							&& sales.getCarrier().getId() != null
							&& carrierId==null) {
						createRegistry(ctx, domainId, sales.getCarrier()
								.getRegistry());
						SalesDAO.createCarrier(ctx, domainId, sales
								.getCarrier().getId(), scopeId);
					}
					long shippingCount = RegistryDAO.getRAddressStream(ctx, f -> f.getIdProperty().eq(sales.getShippingAddress().getId())).count();
					if (sales.getShippingAddress() != null
							&& sales.getShippingAddress().getId() != null
							&& shippingCount<=0) {
						createRegistryAddress(ctx, domainId,
								sales.getShippingAddress());
					}
					Workplace wp = WorkplaceDAO.getWorkplace(
							ctx,
							f -> f.getDomainProperty()
									.eq(ctx.getDomainId())
									.and(f.getIdProperty().eq(
											sales
							.getWorkPlace().getId())));
					if (sales.getWorkPlace() != null
							&& sales.getWorkPlace().getId() != null
							&& wp==null) {
						createWorkplace(ctx, domainId, sales.getWorkPlace());
					}
					
					createSalesLinesItems(ctx, salesDetailList);
					
				});
		
		Integer salesId = createSales(ctx, domainId, scopeId, sales);

		ctx.getDslContext().transaction(configuration -> {
			createSalesLines(ctx, salesId, salesDetailList);
		});
	}

	private void createSalesLinesItems(AONContext ctx, List<ITransferObject> list) {
		
		int domainId = IngenetContext.getUdapaDomainId();
		
		
		list.stream()
		.map(to -> (SalesDetail) to)
		.forEach(
				detail -> {
					
					Item item = detail.getItem();
					
					if(item!=null && item.getId()!=null){
						Product product = item.getProduct();
						if(product!=null && product.getId()!=null){
							Integer productId = ProductDAO.getProduct(ctx, product.getCode()).getId();
							if(productId == null){
								createProduct(ctx, domainId, product);
								productId = product.getId();
							} else {
								updateProduct(ctx, domainId, product);
							}
							
							if(productId !=null){
								Integer itemId = obtainItemId(ctx, domainId, productId, item);
								
								if(itemId == null){
									createItem(ctx, domainId, productId, item);
									
									itemId = obtainItemId(ctx, domainId, productId, item);
									
									try {
										createCompositionItems(ctx, domainId, itemId, item.getItemCompositionList());
									} catch (Exception e) {
										LOGGER.error(e.getMessage());
									}
								} else {
									updateItem(ctx, domainId, productId, item, itemId);
								}
							}
						}
					}
										
				});
	}
	
	private Integer obtainItemId(AONContext ctx, Integer domainId, Integer productId, Item item){
		return AON.getItem(ctx.getDomainName(), ctx.getDomainId(), ctx.getUser(), 
				p -> p.getDomainProperty().eq(domainId)
				.and(p.getProductProperty().eq(productId))
				.and(item.getDetail()!=null?p.getDetailProperty().eq(item.getDetail()):p.getDetailProperty().isNull())
				).getId();
	}
	
	private void createSalesLines(AONContext ctx, Integer salesId, List<ITransferObject> list) {
		
		int domainId = IngenetContext.getUdapaDomainId();
		
		list.stream()
				.map(to -> (SalesDetail) to)
				.forEach(
						detail -> {
							Item item = detail.getItem();
							Integer itemId = null;
							if(itemId==null){
								itemId = obtainItemId(ctx, domainId, item.getProduct().getId(), item);
							}
							createSalesDetail(ctx, domainId, salesId, itemId, detail);
						});
	}

	

	private void createRegistry(AONContext ctx, int domain, Registry registry) {
		SalesDAO.createRegistry(ctx, domain,
				registry.getId(), 
				registry.getType()!=null?(byte)registry.getType().ordinal():null,
				registry.getName(),
				registry.getAlias(),
				registry.getNationality().getValue(),
				(byte)registry.getDocumentType().ordinal(),
				registry.getDocumentCountry().getValue(),
				registry.getDocument()
				);
	}
	
	private void createRegistryAddress(AONContext ctx, int domain,
			RegistryAddress raddress) {
		SalesDAO.createRegistryAddress(ctx, domain, raddress.getId(), raddress
				.getRegistry().getId(), raddress.getAlias(), (byte)raddress
				.getAddressType().ordinal(), raddress.getRecipient(), raddress
				.getStreetType().getValue(), raddress.getAddress(),
				raddress.getAddress2(), raddress.getAddress3(), raddress
						.getNumber(), raddress.getZip(), raddress.getCity(),
						null /* raddress.getGeozone().getId() */, 
				raddress.getMunicipalityCode());
	}
	
	private void createWorkplace(AONContext ctx, int domain, WorkPlace wp) {
		int scope = IngenetContext.getUdapaMainScopeId();

		RegistryAddress raddress = wp.getAddress();
		long addressCount = RegistryDAO.getRAddressStream(ctx, f -> f.getIdProperty().eq(raddress.getId())).count();
		if (raddress != null && raddress.getId() != null
				&& addressCount<=0 ) {
			Integer registryId = RegistryDAO.getRegistry(ctx, f -> f.getIdProperty().eq(raddress.getRegistry().getId())).getId();
			if (registryId==null) {
				createRegistry(ctx, domain, raddress.getRegistry());	
			}
			createRegistryAddress(ctx, domain, raddress);
		}
		Customer customer = wp.getCustomer();
		Integer customerId = RegistryDAO.getRegistry(ctx, f -> f.getIdProperty().eq(customer.getRegistry().getId())).getId();
		if (customer != null && customer.getId() != null
				&& customerId==null ) {
			createRegistry(ctx, domain, customer.getRegistry());
			SalesDAO.createCustomer(ctx, domain, customer.getId(), scope);
		}
		Company company = CompanyDAO.getCompany(ctx, domain);
		SalesDAO.createWorkplace(ctx, domain, wp.getId(), company.getId(), (byte) 1,
				raddress != null && raddress.getId() != null ? raddress.getId() : null,
				customer != null && customer.getId() != null ? customer.getId() : null, wp.getDescription(),
				wp.getEconomicAgreement() != null ? (byte) wp.getEconomicAgreement().ordinal() : null, scope);
	}

	private void createProduct(AONContext ctx, int domainId, Product product) {
		com.esferalia.aon.occam.api.model.product.Product newProduct = new com.esferalia.aon.occam.api.model.product.Product();
		newProduct.setId(product.getId());
		newProduct.setDomain(domainId);
		newProduct.setName(product.getName());
		newProduct.setCode(product.getCode());
		newProduct.setInventoriable(product.isInventoriable());
		newProduct.setSerializable(product.isSerializable());
		newProduct.setLotable(product.isLotable());
		newProduct.setStatus((byte) product.getStatus().ordinal());
		newProduct.setType((byte) product.getType().ordinal());
		newProduct.setManufactured((byte)(product.isManufactured()?1:0));
		newProduct.setComposition(product.isComposition());
		newProduct.setCompositionPrice(product.isCompositionPrice());
		newProduct.setPackaged(product.isPackaged());
		newProduct.setKind((byte) product.getKind().ordinal());
		newProduct.setCreationUser(ctx.getUser());
		newProduct.setCreationDate(new Date());
		ProductDAO.insertWithId(ctx, newProduct);
	}

	private void updateProduct(AONContext ctx, int domainId, Product product) {
		com.esferalia.aon.occam.api.model.product.Product newProduct = new com.esferalia.aon.occam.api.model.product.Product();
		newProduct.setId(product.getId());
		newProduct.setDomain(domainId);
		newProduct.setName(product.getName());
		newProduct.setCode(product.getCode());
		newProduct.setInventoriable(product.isInventoriable());
		newProduct.setSerializable(product.isSerializable());
		newProduct.setLotable(product.isLotable());
		newProduct.setStatus((byte) product.getStatus().ordinal());
		newProduct.setType((byte) product.getType().ordinal());
		newProduct.setManufactured((byte)(product.isManufactured()?1:0));
		newProduct.setComposition(product.isComposition());
		newProduct.setCompositionPrice(product.isCompositionPrice());
		newProduct.setPackaged(product.isPackaged());
		newProduct.setKind((byte) product.getKind().ordinal());
		newProduct.setModificationUser(ctx.getUser());
		newProduct.setModificationDate(new Date());
		ProductDAO.update(ctx, newProduct);
	}

	private void createItem(AONContext ctx, int domainId, Integer productId, Item item) {
		com.esferalia.aon.occam.api.model.product.Item newItem = new com.esferalia.aon.occam.api.model.product.Item();
		newItem.setDomain(domainId);
		newItem.setProductId(productId);
		newItem.setDetail(item.getDetail());
		newItem.setDetail2(item.getDetail2());
		newItem.setDetail3(item.getDetail3());
		newItem.setDescription(item.getDescription());
		newItem.setSerialNumber(item.getSerialNumber());
		newItem.setPrice(item.getPrice());
		newItem.setStatus((byte) item.getStatus().ordinal());
		newItem.setExpensesPercent(item.getExpensesPercent());
		newItem.setExpensesFixed(item.getExpensesFixed());
		newItem.setProfitPercent(item.getProfitPercent());
		newItem.setPurchasePrice(item.getPurchasePrice());
		newItem.setBarcode(item.getBarcode());
		newItem.setPackFormatTag(new Tag());
		newItem.setPackUnits((double) item.getPackUnits());
		newItem.setPackUnitsTag(new Tag());
		newItem.setPackMeasurement(item.getPackMeasurement());
		newItem.setPackMeasurementTag(new Tag());
		newItem.setCreationUser(ctx.getUser());
		newItem.setCreationDate(new Timestamp(new Date().getTime()));
		try {
			ProductValidation.validateItem(ctx, newItem);
			ProductDAO.insertItem(ctx, newItem);
		} catch (AonCoreException e) {
			// no es valido, no se guarda
			LOGGER.error(e.getMessage());
		}
	}
	
	private void updateItem(AONContext ctx, int domainId, Integer productId, Item item, Integer itemId) {
		com.esferalia.aon.occam.api.model.product.Item newItem = new com.esferalia.aon.occam.api.model.product.Item();
		newItem.setId(itemId);
		newItem.setDomain(domainId);
		newItem.setProductId(productId);
		newItem.setDetail(item.getDetail());
		newItem.setDetail2(item.getDetail2());
		newItem.setDetail3(item.getDetail3());
		newItem.setDescription(item.getDescription());
		newItem.setSerialNumber(item.getSerialNumber());
		newItem.setPrice(item.getPrice());
		newItem.setStatus((byte) item.getStatus().ordinal());
		newItem.setExpensesPercent(item.getExpensesPercent());
		newItem.setExpensesFixed(item.getExpensesFixed());
		newItem.setProfitPercent(item.getProfitPercent());
		newItem.setPurchasePrice(item.getPurchasePrice());
		newItem.setBarcode(item.getBarcode());
		newItem.setPackFormatTag(new Tag());
		newItem.setPackUnits((double) item.getPackUnits());
		newItem.setPackUnitsTag(new Tag());
		newItem.setPackMeasurement(item.getPackMeasurement());
		newItem.setPackMeasurementTag(new Tag());
		newItem.setModificationUser(ctx.getUser());
		newItem.setModificationDate(new Timestamp(new Date().getTime()));
		try {
//			ProductValidation.validateItem(ctx, i);
			ProductDAO.updateItem(ctx, newItem);
		} catch (AonCoreException e) {
			// no es valido, no se guarda
			LOGGER.error(e.getMessage());
		}
	}
	
	private void createCompositionItems(AONContext ctx, int domainId, int itemId, List<ItemComposition> list) {
		list.stream().forEach(ic -> {
			createItem(ctx, domainId, ic.getItem().getProduct().getId(), ic.getItem());
			Integer compositionItemId = obtainItemId(ctx, domainId, ic.getItem().getProduct().getId(), ic.getItem());
			com.esferalia.aon.occam.api.model.product.ItemComposition itemComposition = new com.esferalia.aon.occam.api.model.product.ItemComposition();
			itemComposition.setDomain(domainId);
			itemComposition.setItemId(itemId);
			itemComposition.setCompositionItemId(compositionItemId);
			itemComposition.setSequence(ic.getSequence());
			itemComposition.setDescription(ic.getDescription());
			itemComposition.setQuantity(ic.getQuantity());
			itemComposition.setDiscountExpression(ic.getDiscountExpression().getDiscountExpr());
			ProductDAO.insertItemComposition(ctx, itemComposition);
		});
	}
	
	private Integer createSales(AONContext ctx, int domainId, int scopeId, Sales sales){
		com.esferalia.aon.occam.api.model.management.Sales  newSales = new com.esferalia.aon.occam.api.model.management.Sales();
		newSales.setDomain(domainId);
		newSales.setProject(sales.getProject() != null ? sales.getProject().getId() : null);
		newSales.setCustomer(new com.esferalia.aon.occam.api.model.Customer());
		newSales.getCustomer().setId(sales.getCustomer() != null ? sales.getCustomer().getId() : null);
		newSales.setSeries(sales.getSeries());
		newSales.setNumber(sales.getNumber());
		newSales.setPurchaseReference(sales.getPurchaseReference());
		newSales.setShippingAddress(sales.getShippingAddress() != null ? sales.getShippingAddress().getId() : null);
		newSales.setSeller(sales.getSeller() != null ? sales.getSeller().getId() : null);
		newSales.setDiscountExpr(sales.getDiscountExpression() != null ? sales.getDiscountExpression().getDiscountExpr() : null);
		newSales.setIssueDate(sales.getIssueDate());
		newSales.setPayMethod(sales.getPayMethod() != null ? sales.getPayMethod().getId() : null);
		newSales.setDocumentType(sales.getDocumentType() != null ? sales.getDocumentType().ordinal() : null);
		newSales.setSecurityLevel(sales.getSecurityLevel().ordinal());
		newSales.setStatus(SalesStatus.valueOf(sales.getStatus().name()));
		newSales.setComments(sales.getComments());
		newSales.setRemarks(sales.getRemarks());
		newSales.setWorkplace(sales.getWorkPlace().getId());
		newSales.setScope(scopeId);
		newSales.setNumberOfPymnts(sales.getNumberOfPayments());
		newSales.setDaysToFirstPymnt(sales.getDaysToFirstPayment());
		newSales.setDaysBetweenPymnts(sales.getDaysBetweenPayments());
		newSales.setPymntDays(sales.getPaymentDays());
		newSales.setBankAccount(sales.getBankAccount().getIban());
		newSales.setBankAlias(sales.getBankAlias());
		newSales.setBic(sales.getBic());
		newSales.setPurchaseGenerated(sales.isPurchaseGenerated());
		newSales.setCarrier(sales.getCarrier() != null ? sales.getCarrier().getId() : null);
		newSales.setShippingAlternativeAddress(sales.getShippingAlternativeAddress());
		newSales.setShippingAlternativeAddress2(sales.getShippingAlternativeAddress2());
		newSales.setShippingAlternativeZip(sales.getShippingAlternativeZip());
		newSales.setShippingAlternativeCity(sales.getShippingAlternativeCity());
		newSales.setShippingAlternativePhone(sales.getShippingAlternativePhone());
		newSales.setShippingAlternativeRecipient(sales.getShippingAlternativeRecipient());
		newSales.setShippingContact(sales.getShippingContact());
		newSales.setShippingPeriod(sales.getShippingPeriod() != null ? sales.getShippingPeriod().ordinal() : null);
		
		Integer salesId = SalesDAO.getSales(ctx, 
				filter -> filter.getSeriesProperty().eq(sales.getSeries())
				.and(filter.getNumberProperty().eq(sales.getNumber()))
				.and(filter.getStatusProperty().eq(SalesStatus.PENDING.value()))
				).getId();
		
		if(salesId!=null){
			newSales.setId(salesId);
			SalesDAO.updateSales(ctx, newSales);
			SalesDAO.deleteSalesDetail(ctx, newSales);
		} else {
			salesId = SalesDAO.insertSales(ctx, newSales);
		}
		return salesId;
	}
			
	private void createSalesDetail(AONContext ctx, int domainId,
			Integer salesId, Integer itemId, SalesDetail detail) {
		com.esferalia.aon.occam.api.model.management.SalesDetail newDetail = new com.esferalia.aon.occam.api.model.management.SalesDetail();
		newDetail.setDomain(domainId);
		newDetail.setSales(new com.esferalia.aon.occam.api.model.management.Sales());
		newDetail.getSales().setId(salesId);
		newDetail.setItem(new com.esferalia.aon.occam.api.model.product.Item());
		newDetail.getItem().setId(itemId);
		newDetail.setLine(detail.getLine().shortValue());
		newDetail.setDescription(detail.getDescription());
		newDetail.setQuantity(detail.getQuantity());
		newDetail.setPrice(detail.getPrice());
		newDetail.setDiscountExpression(detail.getDiscountExpression()
				.getDiscountExpr());
		newDetail.setTaxes(detail.getTaxes());
		newDetail.setStatus(SalesDetailStatus.valueOf(detail.getStatus().name()));
		newDetail.setOfferDetail(detail.getOfferDetail() != null ? detail
				.getOfferDetail().getId() : null);
		newDetail.setDelivered(detail.getDelivered());
		SalesDAO.insertSalesDetail(ctx, newDetail);
	}
	
}
