package com.esferalia.aon.ingenet;

import java.util.Date;
import java.util.List;

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
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.type.SalesDetailStatus;
import com.esferalia.aon.occam.api.model.type.SalesStatus;
import com.esferalia.aon.occam.impl.jooq.dao.ProductDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SalesDAO;
import com.esferalia.aon.occam.impl.jooq.validation.ProductValidation;
import com.esferalia.aon.watson.error.AonCoreException;


public class IngenetSalesManager {

	private static IngenetSalesManager instance;
	
	
	private IngenetSalesManager(){
		
	}
	
	public static IngenetSalesManager getInstance(){
		if(instance==null){
			instance = new IngenetSalesManager();
		}
		return instance;
	}

	
	public void createSales(String domainName, String user, Sales sales, List<ITransferObject> salesDetailList) {
		int domainId = IngenetContext.getUdapaDomainId();
		int scopeId = IngenetContext.getUdapaMainScopeId();

		AONContext ctx = IngenetContext.getAONContext(domainName,
				domainId, user);
		
		ctx.getDslContext().transaction(
				configuration -> {
					if (sales.getCustomer() != null
							&& !SalesDAO.existRegistry(ctx, sales.getCustomer()
									.getId())) {
						createRegistry(ctx, domainId, sales.getCustomer()
								.getRegistry());
						SalesDAO.createCustomer(ctx, domainId, sales
								.getCustomer().getId(), scopeId);
					}
					if (sales.getSeller() != null
							&& sales.getSeller().getId() != null
							&& !SalesDAO.existRegistry(ctx, sales.getSeller()
									.getId())) {
						createRegistry(ctx, domainId, sales.getSeller()
								.getRegistry());
						SalesDAO.createSeller(ctx, domainId, sales.getSeller()
								.getId(), scopeId);
					}
					if (sales.getCarrier() != null
							&& sales.getCarrier().getId() != null
							&& !SalesDAO.existRegistry(ctx, sales.getCarrier()
									.getId())) {
						createRegistry(ctx, domainId, sales.getCarrier()
								.getRegistry());
						SalesDAO.createCarrier(ctx, domainId, sales
								.getCarrier().getId(), scopeId);
					}
					if (sales.getShippingAddress() != null
							&& sales.getShippingAddress().getId() != null
							&& !SalesDAO.existAddress(ctx, sales
									.getShippingAddress().getId())) {
						createRegistryAddress(ctx, domainId,
								sales.getShippingAddress());
					}
					if (sales.getWorkPlace() != null
							&& sales.getWorkPlace().getId() != null
							&& !SalesDAO.existWorkplace(ctx, sales
									.getWorkPlace().getId())) {
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
							Integer productId = ProductDAO.getProduct(ctx, product.getId()).getId();
							if(productId == null){
								createProduct(ctx, domainId, product);
								productId = product.getId();
							}
	//						Integer brandId = ProductDAO.getBrand(ctx, product.getBrand().getId()).getId();
	//						if(brandId == null){
	//							createBrand(ctx, domainId, product.getBrand());
	//						}
	//						Integer categoryId = ProductDAO.getProductCategory(ctx, product.getCategory().getId()).getId();
	//						if(categoryId == null){
	//							createCategory(ctx, domainId, product.getCategory());
	//						}
	//						Integer vatId;
	//						Integer eetentionId;
							
							Integer itemId = ProductDAO.getItem(ctx, 
									p -> p.getDomainProperty().eq(domainId)
									.and(p.getProductProperty().eq(item.getProduct().getId()))
									.and(item.getDetail()!=null?p.getDetailProperty().eq(item.getDetail()):p.getDetailProperty().isNull())
									).getId();
							if(itemId == null && productId !=null){
								createItem(ctx, domainId, productId, item);
								
								itemId = ProductDAO.getItem(ctx, 
										p -> p.getDomainProperty().eq(domainId)
										.and(p.getProductProperty().eq(item.getProduct().getId()))
										.and(item.getDetail()!=null?p.getDetailProperty().eq(item.getDetail()):p.getDetailProperty().isNull())
										).getId();
								
								try {
									createCompositionItems(ctx, domainId, itemId, item.getItemCompositionList());
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
//								createCompositionItems(ctx, domainId, itemId, Arrays.asList( 
//										item.getCompositions().toArray( new ItemComposition[0] ) ));
								
							}
						}
					}
					
//					WorkPlace workplace = detail.getWorkPlace();
//					if(workplace!=null && workplace.getId()!=null && !SalesDAO.existWorkplace(ctx, workplace.getId())){
//						createWorkplace(ctx, domainId, workplace);
//					}
					
				});
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
								itemId = ProductDAO.getItem(ctx, 
										p -> p.getDomainProperty().eq(domainId)
										.and(p.getProductProperty().eq(item.getProduct().getId()))
										.and(item.getDetail()!=null?p.getDetailProperty().eq(item.getDetail()):p.getDetailProperty().isNull())
										).getId();
							}
							createSalesDetail(ctx, domainId, salesId, itemId, detail);
						});
	}

	

	private void createRegistry(AONContext ctx, int domain, Registry registry) {
		SalesDAO.createRegistry(ctx, domain,
				registry.getId(), 
				(byte)registry.getType().ordinal(),
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
		if (raddress != null && raddress.getId() != null
				&& !SalesDAO.existAddress(ctx, raddress.getId())) {
			createRegistryAddress(ctx, domain, raddress);
		}
		Customer customer = wp.getCustomer();
		if (customer != null && customer.getId() != null
				&& !SalesDAO.existRegistry(ctx, customer.getId())) {
			createRegistry(ctx, domain, customer.getRegistry());
			SalesDAO.createCustomer(ctx, domain, customer.getId(), scope);
		}
		Integer enterprise = SalesDAO.obtainEnterpriseId(ctx, domain);
		SalesDAO.createWorkplace(ctx, domain, wp.getId(), enterprise, (byte) 1,
				raddress != null && raddress.getId() != null ? raddress.getId()
						: null,
				customer != null && customer.getId() != null ? customer.getId()
						: null, wp.getDescription(), (byte) wp
						.getEconomicAgreement().ordinal(), scope);
	}

	private void createProduct(AONContext ctx, int domainId, Product product) {
		com.esferalia.aon.occam.api.model.product.Product p = new com.esferalia.aon.occam.api.model.product.Product();
		p.setId(product.getId());
		p.setDomain(domainId);
		p.setName(product.getName());
		p.setCode(product.getCode());
//		p.setBrand(product.getBrand()!=null?product.getBrand().getId():null);
//		p.setCategory(product.getCategory()!=null?product.getCategory().getId():null);
		p.setInventoriable(product.isInventoriable());
		p.setSerializable(product.isSerializable());
		p.setLotable(product.isLotable());
		p.setStatus((byte) product.getStatus().ordinal());
//		p.setVat(product.getVat()!=null?product.getVat().getId():null);
//		p.setRetention(product.getRetention()!=null?product.getRetention().getId():null);
		p.setType((byte) product.getType().ordinal());
		p.setManufactured((byte)(product.isManufactured()?1:0));
		p.setComposition(product.isComposition());
		p.setCompositionPrice(product.isCompositionPrice());
		p.setPackaged(product.isPackaged());
//		p.setSalesAccount(product.getSalesAccount().getId());
//		p.setPurchaseAccount(product.getPurchaseAccount().getId());
		p.setKind((byte) product.getKind().ordinal());
		p.setCreationUser(ctx.getUser());
		p.setCreationDate(new Date());
		ProductDAO.insertWithId(ctx, p);
	}

	private void createItem(AONContext ctx, int domainId, Integer productId, Item item) {
		com.esferalia.aon.occam.api.model.product.Item i = new com.esferalia.aon.occam.api.model.product.Item();
		i.setDomain(domainId);
		i.setProductId(productId);
		i.setDetail(item.getDetail());
		i.setDetail2(item.getDetail2());
		i.setDetail3(item.getDetail3());
		i.setDescription(item.getDescription());
		i.setSerialNumber(item.getSerialNumber());
		i.setPrice(item.getPrice());
		i.setStatus((byte) item.getStatus().ordinal());
		i.setExpensesPercent(item.getExpensesPercent());
		i.setExpensesFixed(item.getExpensesFixed());
		i.setProfitPercent(item.getProfitPercent());
		i.setPurchasePrice(item.getPurchasePrice());
		i.setBarcode(item.getBarcode());
		i.setPackFormatTag(new Tag());
		i.setPackUnits((double) item.getPackUnits());
		i.setPackUnitsTag(new Tag());
		i.setPackMeasurement(item.getPackMeasurement());
		i.setPackMeasurementTag(new Tag());
		try {
			ProductValidation.validateItem(ctx, i);
			ProductDAO.insertItem(ctx, i);
		} catch (AonCoreException e) {
			// no es valido, no se guarda
//			e.printStackTrace();
		}
	}
	
	private void createCompositionItems(AONContext ctx, int domainId, int itemId, List<ItemComposition> list) {
		list.stream().forEach(ic -> {
			createItem(ctx, domainId, ic.getItem().getProduct().getId(), ic.getItem());
			Integer compositionItemId = ProductDAO.getItem(ctx, 
					p -> p.getDomainProperty().eq(domainId)
					.and(p.getProductProperty().eq(ic.getItem().getProduct().getId()))
					.and(ic.getItem().getDetail()!=null?p.getDetailProperty().eq(ic.getItem().getDetail()):p.getDetailProperty().isNull())
					).getId();
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
		com.esferalia.aon.occam.api.model.management.Sales  s = new com.esferalia.aon.occam.api.model.management.Sales();
		s.setDomain(domainId);
		s.setProject(sales.getProject() != null ? sales.getProject().getId() : null);
		s.setCustomer(sales.getCustomer() != null ? sales.getCustomer().getId() : null);
		s.setSeries(sales.getSeries());
		s.setNumber(sales.getNumber());
		s.setPurchaseReference(sales.getPurchaseReference());
		s.setShippingAddress(sales.getShippingAddress() != null ? sales.getShippingAddress().getId() : null);
		s.setSeller(sales.getSeller() != null ? sales.getSeller().getId() : null);
		s.setDiscountExpr(sales.getDiscountExpression() != null ? sales.getDiscountExpression().getDiscountExpr() : null);
		s.setIssueDate(sales.getIssueDate());
		s.setPayMethod(sales.getPayMethod() != null ? sales.getPayMethod().getId() : null);
		s.setDocumentType(sales.getDocumentType() != null ? sales.getDocumentType().ordinal() : null);
		s.setSecurityLevel(sales.getSecurityLevel().ordinal());
		s.setStatus(SalesStatus.valueOf(sales.getStatus().name()));
		s.setComments(sales.getComments());
		s.setRemarks(sales.getRemarks());
		s.setWorkplace(sales.getWorkPlace().getId());
		s.setScope(scopeId);
		s.setNumberOfPymnts(sales.getNumberOfPayments());
		s.setDaysToFirstPymnt(sales.getDaysToFirstPayment());
		s.setDaysBetweenPymnts(sales.getDaysBetweenPayments());
		s.setPymntDays(sales.getPaymentDays());
		s.setBankAccount(sales.getBankAccount().getIban());
		s.setBankAlias(sales.getBankAlias());
		s.setBic(sales.getBic());
		s.setPurchaseGenerated(sales.isPurchaseGenerated());
		s.setCarrier(sales.getCarrier() != null ? sales.getCarrier().getId() : null);
		s.setShippingAlternativeAddress(sales.getShippingAlternativeAddress());
		s.setShippingAlternativeAddress2(sales.getShippingAlternativeAddress2());
		s.setShippingAlternativeZip(sales.getShippingAlternativeZip());
		s.setShippingAlternativeCity(sales.getShippingAlternativeCity());
		s.setShippingAlternativePhone(sales.getShippingAlternativePhone());
		s.setShippingAlternativeRecipient(sales.getShippingAlternativeRecipient());
		s.setShippingContact(sales.getShippingContact());
		s.setShippingPeriod(sales.getShippingPeriod() != null ? sales.getShippingPeriod().ordinal() : null);
		return SalesDAO.insertSales(ctx, s);
	}
			
	private void createSalesDetail(AONContext ctx, int domainId,
			Integer salesId, Integer itemId, SalesDetail detail) {
		com.esferalia.aon.occam.api.model.management.SalesDetail sd = new com.esferalia.aon.occam.api.model.management.SalesDetail();
		sd.setDomain(domainId);
		sd.setSales(salesId);
		sd.setItem(itemId);
		sd.setLine(detail.getLine().shortValue());
		sd.setDescription(detail.getDescription());
		sd.setQuantity(detail.getQuantity());
		sd.setPrice(detail.getPrice());
		sd.setDiscountExpression(detail.getDiscountExpression()
				.getDiscountExpr());
		sd.setTaxes(detail.getTaxes());
		sd.setStatus(SalesDetailStatus.valueOf(detail.getStatus().name()));
		sd.setOfferDetail(detail.getOfferDetail() != null ? detail
				.getOfferDetail().getId() : null);
		sd.setDelivered(detail.getDelivered());
		SalesDAO.insertSalesDetail(ctx, sd);
	}
	
}
