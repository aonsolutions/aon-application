package es.aonsolutions.aio.test;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.WorkPlace;
import com.code.aon.config.Tax;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.config.util.SeriesNumberUtil;
import com.code.aon.customer.Customer;
import com.code.aon.finance.Creditor;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.product.Item;
import com.code.aon.product.Product;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ql.Criteria;
import com.code.aon.supplier.Supplier;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.github.javafaker.Faker;

public class AonHibernateTestFaker {
	
	private static Faker faker = new Faker(new Locale("es"));

	
	// ***************************************
	// 								[CREDITOR] 
	// ***************************************

	public static Creditor getCreditor() throws ManagerBeanException  {
		IManagerBean bean = BeanManager.getManagerBean(Creditor.class);
		Criteria c = new Criteria();
		int count = bean.getCount(c);
		if (count > 0) {
			List<ITransferObject> creditors = bean.getList(c
				,AonHibernateTestRandom.number( 0, count-1 ),1);
			if ( creditors != null && !creditors.isEmpty()) {
				return (Creditor) creditors.get(0);
			}
		}
		return null;
	}

	// ***************************************
	// 								[CUSTOMER] 
	// ***************************************

	public static Customer getCustomer() throws ManagerBeanException  {
		IManagerBean bean = BeanManager.getManagerBean(Customer.class);
		Criteria c = new Criteria();
		int count = bean.getCount(c);
		if (count > 0) {
			List<ITransferObject> customers = bean.getList(c
				,AonHibernateTestRandom.number( 0, count-1 ),1);
			if ( customers != null && !customers.isEmpty()) {
				return (Customer) customers.get(0);
			}
		}
		return null;
	}

	// ***************************************
	// 								 [INVOICE] 
	// ***************************************
	private static IPriceStrategy priceStrategy;
	public static IPriceStrategy getPriceStrategy() {
		if (priceStrategy == null) {
			priceStrategy = new InvoicePriceStrategy();
		}
		return priceStrategy;
	}
	
	public static Invoice getInvoice() throws ManagerBeanException  {
		return getInvoice(InvoiceType.values()[ AonHibernateTestRandom.number( 0, InvoiceType.values().length - 1 )]);
	}
	
	public static Invoice getInvoice( InvoiceType type ) throws ManagerBeanException  {
		Invoice inv = null;
		if ( type == InvoiceType.SALES) inv = getSalesInvoice();
		else if ( type == InvoiceType.PURCHASE) inv = getPurchaseInvoice();
		else if ( type == InvoiceType.EXPENSES) inv = getExpensesInvoice( InvoiceType.EXPENSES );
		else if ( type == InvoiceType.UNDEDUCTIBLE) inv = getExpensesInvoice( InvoiceType.UNDEDUCTIBLE );
		Date issueDate = faker.date().past(10, TimeUnit.DAYS);
		inv.setIssueDate( issueDate );
		inv.setTaxDate( issueDate );
		return inv;
	}
	
	public static Invoice getSalesInvoice( ) throws ManagerBeanException  {
		Invoice inv = new Invoice();
		inv.setType(InvoiceType.SALES);
		inv.setSeries("2022");
		Criteria criteria = new Criteria();
		criteria.addEqualExpression("invoice.type", InvoiceType.SALES.ordinal());
		inv.setNumber(SeriesNumberUtil.obtainNumber(inv.getSeries(), "Invoice", criteria));
		Customer customer = getCustomer();
		inv.setRegistry( customer==null?null:customer.getRegistry() );
		return inv;
	}
	
	public static Invoice getPurchaseInvoice( ) throws ManagerBeanException  {
		Invoice inv = new Invoice();
		inv.setType(InvoiceType.PURCHASE);
		inv.setReferenceCode(AonHibernateTestRandom.uuid(32));
		Supplier supplier = AonHibernateTestFaker.getSupplier();
		inv.setRegistry( supplier==null?null:supplier.getRegistry() );
		return inv;
	}

	public static Invoice getExpensesInvoice(InvoiceType type ) throws ManagerBeanException  {
		Invoice inv = new Invoice();
		inv.setType(type);
		inv.setReferenceCode(AonHibernateTestRandom.uuid(32));
		Creditor creditor = getCreditor();
		inv.setRegistry( creditor ==null?null:creditor.getRegistry() );
		return inv;
	}
	
	// ***************************************
	// 						  [INVOICE_DETAIL] 
	// ***************************************
	public static List<InvoiceDetail> getInvoiceDetails(Invoice invoice) throws ManagerBeanException {
		List<InvoiceDetail> details = new LinkedList<InvoiceDetail>();
		WorkPlace workplace = AonHibernateTestFaker.getWorkplace();
		int times = AonHibernateTestRandom.number(1, 15);
		for (int i = 0; i < times; i++) {
			InvoiceDetail detail = new InvoiceDetail();
			detail.setInvoice(invoice);
			detail.setWorkPlace( workplace );
			Item item = getItem();
			detail.setItem( item );
			detail.setDescription(item.getDescription());
			detail.setQuantity(AonHibernateTestRandom.getDouble(0, 10));

			String discountExpression = null;
			if ( AonHibernateTestRandom.gt(90) ) {
				if ( AonHibernateTestRandom.gt(80)) {
					discountExpression =  IntStream.range(0,AonHibernateTestRandom.getInt(1,4))
						.boxed()
						.map( x -> AonNumberUtils.toString( AonHibernateTestRandom.getDouble(0, 100)))
						.collect( Collectors.joining("+"));
				} else {
					discountExpression = AonNumberUtils.toString( AonHibernateTestRandom.getDouble(0, 100));		
				}
				DiscountExpression de = new DiscountExpression( discountExpression ); 
				detail.setDiscountExpression( de );
			}
			detail.setPrice(item.getPrice() );
	//		if ( invoice.getType() == InvoiceType.SALES) inv = getSalesInvoice();
	//		else if ( invoice.getType() == InvoiceType.PURCHASE) inv = getPurchaseInvoice();
	//		else if ( invoice.getType() == InvoiceType.EXPENSES) inv = getExpensesInvoice( InvoiceType.EXPENSES );
	//		else if ( invoice.getType() == InvoiceType.UNDEDUCTIBLE) inv = getExpensesInvoice( InvoiceType.UNDEDUCTIBLE );
	//		detail.setSource(InvoiceSource.DIRECT_INVOICE);
			detail.setPrepayment(AonHibernateTestRandom.gt(98));
			detail.setTaxableBase(getPriceStrategy().getBasePrice(detail));
			details.add(detail);
		}
		return details;
	}


	// ***************************************
	// 									[ITEM] 
	// ***************************************
	public static Item getItem( ) throws ManagerBeanException  {
		return getItem( false );
	}
	public static Item getItem( boolean nullable) throws ManagerBeanException  {
		IManagerBean bean = BeanManager.getManagerBean(Item.class);
		Criteria c = new Criteria();
		int count = bean.getCount(c);
		if (count > 0) {
			List<ITransferObject> items = bean.getList(c
				,AonHibernateTestRandom.number( 0, count - (nullable?0:1) ),1);
			if ( items != null && !items.isEmpty()) {
				return (Item) items.get(0);
			}
		}
		return null;
	}
	
	public static Item getnewItem() throws ManagerBeanException {
		Product product = getNewProduct();
		Item item = new Item();
		item.setProduct(product);
		item.setDetail("11");
		item.setDetail2("22");
		item.setDetail2("33");
		item.setPrice(AonHibernateTestRandom.getDouble(0, 100));
		return item;
	}
	
	// **************************************
	// 								[PRODUCT] 
	// **************************************
	
	public static Product getNewProduct() throws ManagerBeanException {
		Product p =  new Product();
		p.setName(faker.commerce().productName());
		p.setCode(AonHibernateTestRandom.string(0, 1, 14));
		p.setVat( getTax( TaxType.VAT ) );
		p.setRetention( getTax( TaxType.RETENTION )); 
		return  p;
	}

	// ***************************************
	// 								[SUPPLIER] 
	// ***************************************

	public static Supplier getSupplier() throws ManagerBeanException  {
		IManagerBean bean = BeanManager.getManagerBean(Supplier.class);
		Criteria c = new Criteria();
		int count = bean.getCount(c);
		if (count > 0) {
			List<ITransferObject> suppliers = bean.getList(c
				,AonHibernateTestRandom.number( 0, count-1 ),1);
			if ( suppliers != null && !suppliers.isEmpty()) {
				return (Supplier) suppliers.get(0);
			}
		}
		return null;
	}
	
	// ***************************************
	// 									 [TAX] 
	// ***************************************
	
	public static Tax getTax( TaxType type) throws ManagerBeanException  {
		IManagerBean bean = BeanManager.getManagerBean(Tax.class);
		Criteria c = new Criteria();
		c.addEqualExpression(bean.getFieldName(IEntityAlias.TAX_TYPE), type);
		int count = bean.getCount(c);
		if (count > 0) {
			List<ITransferObject> taxes = bean.getList(c
				,AonHibernateTestRandom.number( 0, count - 1 ),1);
			if ( taxes != null && !taxes.isEmpty()) {
				return (Tax) taxes.get(0);
			}
		}
		return null;
	}
	
	// ***************************************
	// 							   [WORKPLACE] 
	// ***************************************

	public static WorkPlace getWorkplace() throws ManagerBeanException  {
		IManagerBean bean = BeanManager.getManagerBean(WorkPlace.class);
		Criteria c = new Criteria();
		int count = bean.getCount(c);
		if (count > 0) {
			List<ITransferObject> wps = bean.getList(c
				,AonHibernateTestRandom.number( 0, count-1 ),1);
			if ( wps != null && !wps.isEmpty()) {
				return (WorkPlace) wps.get(0);
			}
		}
		return null;
	}
}

