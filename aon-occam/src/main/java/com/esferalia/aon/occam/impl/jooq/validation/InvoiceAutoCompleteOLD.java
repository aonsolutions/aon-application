package com.esferalia.aon.occam.impl.jooq.validation;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.Filter.RegistryAddressFilter;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceBreakdown;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceTax;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.product.Tax;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.type.FinanceStatus;
import com.esferalia.aon.occam.api.model.type.InvoiceSource;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.esferalia.aon.occam.impl.jooq.dao.AccountDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CreditorDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CustomerDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DomainDAO;
import com.esferalia.aon.occam.impl.jooq.dao.GlobalDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDetailOLDDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceOLDDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ItemDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PayMethodDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryAddressDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SupplierDAO;
import com.esferalia.aon.occam.impl.jooq.dao.WorkplaceDAO;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
@Deprecated
public class InvoiceAutoCompleteOLD {
	
	private InvoiceAutoCompleteOLD() {
		
	}
	
	@Deprecated
	private static class AonConfigurationContext {
		
		private AONContext ctx;
		private AonConfiguration config;
		
		private AonConfigurationContext (AONContext ctx,AonConfiguration config) {
			this.ctx = ctx;
			this.config = config;
		}
		private AONContext getContext() {
			return ctx;
		}
		private AonConfiguration getConfiguration() {
			return config;
		}
	}
	
	
	/**
	 * Se rellena el dominio.
	 */
	@Deprecated
	public static final BiConsumer<Invoice,AonConfigurationContext> COMPLETE_DOMAIN = (inv,ctx) -> {
		if(inv.getDomain() == null) {
			inv.setDomain(ctx.getContext().getDomainId());
		}
	};

	
	
	/**
	 * Se rellena el número de referencia para las facturas de ventas.
	 */
	@Deprecated
	public static final BiConsumer<Invoice,AonConfigurationContext> COMPLETE_SALES_SERIES = (inv,ctx) -> {
		if (inv.isSales()) {
			if (AonStringUtils.isBlank(inv.getSeries())) {
				inv.setSeries(null);
			}
			if (inv.getNumber() == 0) {
				Byte[] types = new Byte[]{InvoiceType.SALES.value()};
				int number = InvoiceOLDDAO.getNextNumber(ctx.getContext(),types, inv.getSeries());
				inv.setNumber(number);
			}
			if(inv.getReferenceCode() == null || "".equals(inv.getReferenceCode())) {
				String referenceCode = AonStringUtils.leftPad(Integer.toString(inv.getNumber()), 6, "0");
				if (!AonStringUtils.isBlank(inv.getSeries())) {
					referenceCode = inv.getSeries() + "/" + referenceCode;
				}
				inv.setReferenceCode(referenceCode);
			}
		} 
	};

	/**
	 * Se rellena las serie y numero para las facturas de compras y gastos.
	 */
	@Deprecated
	public static final BiConsumer<Invoice,AonConfigurationContext> COMPLETE_PURCHASE_EXPENSES_SERIES = (inv,ctx) -> {
		if (inv.isPurchase() || inv.isExpenses()) {
			inv.setSeries(Integer.toString(AonDateUtils.getYear(inv.getIssueDate())));
			if (inv.getNumber() == 0) {
				Byte[] types = new Byte[]{InvoiceType.PURCHASE.value(),InvoiceType.EXPENSES.value()};
				int number = InvoiceOLDDAO.getNextNumber(ctx.getContext(),types, inv.getSeries());
				inv.setNumber(number);
			}
		} 
	};

	/**
	 * Se rellena las serie y numero para las facturas de compras y gastos.
	 */
	@Deprecated
	public static final BiConsumer<Invoice,AonConfigurationContext> COMPLETE_UNDEDUCTIBLE_REFERENCE_CODE = (inv,ctx) -> {
		if (inv.isUndeductible() && AonStringUtils.equalsIgnoreCase("<auto>",inv.getReferenceCode())) {
			inv.setReferenceCode( inv.getDocumentNumber());
		} 
	};

	/**
	 * Se rellena las serie y numero para las facturas de compras y gastos.
	 */
	@Deprecated
	public static final BiConsumer<Invoice,AonConfigurationContext> COMPLETE_UNDEDUCTIBLE_SERIES = (inv,ctx) -> {
		if (inv.isUndeductible()) {
			inv.setSeries(Integer.toString(AonDateUtils.getYear(inv.getIssueDate()==null?new Date():inv.getIssueDate())));
			if (inv.getNumber() == 0) {
				Byte[] types = new Byte[]{InvoiceType.UNDEDUCTIBLE.value()};
				int number = InvoiceOLDDAO.getNextNumber(ctx.getContext(),types, inv.getSeries());
				inv.setNumber(number);
			}
		} 
	};
	
	/**
	 * Aseguramos el SecurityLevel.
	 */
	@Deprecated
	public static final BiConsumer<Invoice,AonConfigurationContext> COMPLETE_SECURITY_LEVEL = (inv,ctx) -> {
		if (inv.getSecurityLevel() == null) inv.setSecurityLevel(SecurityLevel.OFFICIAL); 
	};
	
	/**
	 * Aseguramos la fecha de IVA..
	 */
	@Deprecated
	public static final BiConsumer<Invoice,AonConfigurationContext> COMPLETE_TAX_DATE = (inv,ctx) -> {
		if (inv.getTaxDate() == null) inv.setTaxDate(inv.getIssueDate());
	};
	
	/**
	 * Aseguramos la Actividad.
	 */
	@Deprecated
	public static final BiConsumer<Invoice,AonConfigurationContext> COMPLETE_ACTIVITY = (inv,ctx) -> {
		if (inv.getActivity().isEmpty()
			&& ctx.getConfiguration() != null 
			&& ctx.getConfiguration().getActivities() != null 
			&& ctx.getConfiguration().getActivities().size() == 1) {
			
			EnterpriseActivity act = ctx.getConfiguration().getMainActivity(); 
			inv.setActivity( act == null ? new EnterpriseActivity() : act);
		}
	};


	/**
	 * Si solo hay un vencimiento, el importe será igual al total factura.
	 */
	@Deprecated
	public static final BiConsumer<Invoice,AonConfigurationContext> COMPLETE_FIRST_FINANCE = (inv,ctx) -> {
		if (inv.hasFinances() && inv.getFinances().size() == 1 && inv.getFinances().get(0).isPending()) {
			inv.getFinances().get(0).setAmount( inv.getTotal());
		}
	};

	/**
	 * Aseguramos la fecha de IVA..
	 */
	@Deprecated
	public static final  BiConsumer<Invoice,AonConfigurationContext> COMPLETE_RECTIFICATION_TYPE = (inv,ctx) -> {
		if (inv.getRectificationType() == null) inv.setRectificationType(RectificationType.NONE);
	};
	
	
	/**
	 * Aseguramos el nombre del titular de la factura.
	 */
	@Deprecated
	public static final BiConsumer<Invoice,AonConfigurationContext> ENSURE_REGISTRY_DATA = (inv,ctx) -> {
		if (AonStringUtils.isBlank(inv.getRegistryName()) || AonStringUtils.isBlank(inv.getRegistryDocument())) {
			if (inv.getRegistry() != null) {
				Registry registry = RegistryDAO.get(ctx.getContext(), inv.getRegistry());
				if (AonStringUtils.isBlank(inv.getRegistryName())) inv.setRegistryName(registry.getName());
				if (AonStringUtils.isBlank(inv.getRegistryDocument())) {
					inv.setRegistryDocumentType(registry.getDocumentType());
					inv.setRegistryDocumentCountry(registry.getDocumentCountry());
					inv.setRegistryDocument(registry.getDocument());
				}
			}
		}
	};
	
	/**
	 * Aseguramos el nombre del titular de la factura.
	 */
	@Deprecated
	public static final BiConsumer<Invoice,AonConfigurationContext> COMPLETE_REGISTRY_DATA = (inv,ctx) -> {
		if(inv.getRegistry() == null && inv.getRegistryData() != null) {
			if(inv.getRegistryData().getId() == null && !AonStringUtils.isBlank(inv.getRegistryData().getDocument())) {
				inv.getRegistryData().setDomain(new Domain().setId(inv.getDomain()));
				
				if(InvoiceType.SALES.equals(inv.getType())) {
					Customer c = CustomerDAO.getStream(ctx.getContext(), f -> 
							f.getDomainProperty().eq(inv.getDomain())
							.and(f.getDocumentProperty().eq(inv.getRegistryData().getDocument()))).findFirst().orElse(new Customer());
					if(c.getId() != null) {
						inv.setRegistry(c.getId());
						inv.setRegistryData(c);
					} else {
						Registry registry = RegistryDAO.get(ctx.getContext(), f -> f.getDomainProperty().eq(inv.getDomain())
								.and(f.getDocumentProperty().eq(inv.getRegistryData().getDocument())));
			
						Customer customer = new Customer()
								.copy(registry.getId() != null ? registry : inv.getRegistryData()
								.setId(registry.getId()))
								.setScope(inv.getScope());
						Account account = new Account()
								.setDomain(inv.getDomain())
								.setCode(AccountDAO.getNextAccountCode(ctx.getContext(), "4300"))
								.setAlias(customer.getAlias())
								.setDescription(customer.getName())
								.setActive(true);
						account = AccountDAO.save(ctx.getContext(), account);
						customer.setAccount(account.getId());
						c = CustomerDAO.save(ctx.getContext(), customer);
						if(c.getId() != null) {
							inv.setRegistry(c.getId());
							inv.setRegistryData(c);
							if(!inv.getAddress().isEmpty()) {
								RegistryAddress raddress = RegistryAddressDAO.save(ctx.getContext(), inv.getAddress()
									.setId(null)
									.setDomain(c.getDomain().getId())
									.setRegistry(c.getId()));
								inv.setRegistryAddress(raddress.getId());
								inv.setAddress(raddress);
							}
						}
					}
				} else if(InvoiceType.PURCHASE.equals(inv.getType())) {
					Supplier s = SupplierDAO.getStream(ctx.getContext(), f -> 
						f.getDomainProperty().eq(inv.getDomain())
					.and(f.getDocumentProperty().eq(inv.getRegistryData().getDocument()))).findFirst().orElse(new Supplier());
					if(s.getId() != null) {
						inv.setRegistry(s.getId());
						inv.setRegistryData(s);
					} else {
						Registry registry = RegistryDAO.get(ctx.getContext(), f -> f.getDomainProperty().eq(inv.getDomain())
								.and(f.getDocumentProperty().eq(inv.getRegistryData().getDocument())));
						Supplier supplier = new Supplier()
								.copy(registry.getId() != null ? registry :inv.getRegistryData())
								.setId(registry.getId())
								.setScope(inv.getScope());
						Account account = new Account()
								.setDomain(inv.getDomain())
								.setCode(AccountDAO.getNextAccountCode(ctx.getContext(), "4000"))
								.setAlias(supplier.getAlias())
								.setDescription(supplier.getName())
								.setActive(true);
						account = AccountDAO.save(ctx.getContext(), account);
						supplier.setAccount(account.getId());
						s = SupplierDAO.save(ctx.getContext(), supplier);
						if(s.getId() != null) {
							inv.setRegistry(s.getId());
							inv.setRegistryData(s);
							if ( !inv.getAddress().isEmpty() ) {
								RegistryAddress raddress = RegistryAddressDAO.save(ctx.getContext(), inv.getAddress()
										.setId(null)
										.setDomain(s.getDomain().getId())
										.setRegistry(s.getId()));
								inv.setRegistryAddress(raddress.getId());
								inv.setAddress(raddress);
							}
						}
					}
				} else if(InvoiceType.EXPENSES.equals(inv.getType()) 
						|| InvoiceType.UNDEDUCTIBLE.equals(inv.getType())) {
					Creditor c = CreditorDAO.getStream(ctx.getContext(), f -> 
						f.getDomainProperty().eq(inv.getDomain())
						.and(f.getDocumentProperty().eq(inv.getRegistryData().getDocument()))).findFirst().orElse(new Creditor());
					if(c.getId() != null) {
						inv.setRegistry(c.getId());
						inv.setRegistryData(c);
					} else {
						Registry registry = RegistryDAO.get(ctx.getContext(), f -> f.getDomainProperty().eq(inv.getDomain())
								.and(f.getDocumentProperty().eq(inv.getRegistryData().getDocument())));
						
						Creditor creditor =  new Creditor()
								.copy(registry.getId() != null ? registry : inv.getRegistryData())
								.setId(registry.getId())
								.setScope(inv.getScope());
						
						Account account = new Account()
								.setDomain(inv.getDomain())
								.setCode(AccountDAO.getNextAccountCode(ctx.getContext(), "4100"))
								.setAlias(creditor.getAlias())
								.setDescription(creditor.getName())
								.setActive(true);
						account = AccountDAO.save(ctx.getContext(), account);
						creditor.setAccount(account.getId());

						c = CreditorDAO.save(ctx.getContext(),creditor);
						if(c.getId() != null) {
							inv.setRegistry(c.getId());
							inv.setRegistryData(c);
							if(!inv.getAddress().isEmpty()) {
								RegistryAddress raddress = RegistryAddressDAO.save(ctx.getContext(), inv.getAddress()
									.setId(null)
									.setDomain(c.getDomain().getId())
									.setRegistry(c.getId()));
								inv.setRegistryAddress(raddress.getId());
								inv.setAddress(raddress);
							}
						}
					}
				}
			} else inv.setRegistry(inv.getRegistryData().getId());
		}
		
		if(inv.getRegistryData().isGlobal()) {
			Registry registry = GlobalDAO.copyRegistry(ctx.getContext(), inv.getRegistryData().getId());
			inv.setRegistry(registry.getId());
			inv.setRegistryData(registry);
			if(InvoiceType.SALES.equals(inv.getType())) {
				CustomerDAO.save(ctx.getContext(), new Customer()
						.copy(registry).setScope(inv.getScope()));
			} else if(InvoiceType.PURCHASE.equals(inv.getType())) {
				SupplierDAO.save(ctx.getContext(), new Supplier()
						.copy(registry).setScope(inv.getScope()));
			} else if(InvoiceType.EXPENSES.equals(inv.getType()) 
					|| InvoiceType.UNDEDUCTIBLE.equals(inv.getType())) {
				CreditorDAO.save(ctx.getContext(), new Creditor()
						.copy(registry).setScope(inv.getScope()));
			}	
		}
	
		Integer registryId = inv.getRegistryData().getId() != null
				? inv.getRegistryData().getId() : inv.getRegistry();
		if(InvoiceType.SALES.equals(inv.getType())) {
			Customer customer = CustomerDAO.get(ctx.getContext(), registryId);
			if(customer.isEmpty()) {
				CustomerDAO.save(ctx.getContext(), new Customer()
					.copy(inv.getRegistryData().setDomain(new Domain().setId(inv.getDomain()))).setScope(inv.getScope()));
			}
		} else if(InvoiceType.PURCHASE.equals(inv.getType())) {
			Supplier supplier = SupplierDAO.get(ctx.getContext(), registryId);
			if(supplier.isEmpty()) {
				SupplierDAO.save(ctx.getContext(), new Supplier()
					.copy(inv.getRegistryData().setDomain(new Domain().setId(inv.getDomain()))).setScope(inv.getScope()));
			}
		} else if(InvoiceType.EXPENSES.equals(inv.getType()) 
				|| InvoiceType.UNDEDUCTIBLE.equals(inv.getType())) {
			Creditor creditor = CreditorDAO.get(ctx.getContext(), registryId);
			if(creditor.isEmpty()) {
				CreditorDAO.save(ctx.getContext(), new Creditor()
					.copy(inv.getRegistryData().setDomain(new Domain().setId(inv.getDomain()))).setScope(inv.getScope()));
			}
		}	
		
		if(inv.getRegistryDocumentType() == null && inv.getRegistryDocumentCountry() == null) {
		    Registry registry = RegistryDAO.get(ctx.getContext(), f -> f.getIdProperty().eq(inv.getRegistry()));
            inv.setRegistryDocumentType(registry.getDocumentType());
		    inv.setRegistryDocumentCountry(registry.getDocumentCountry());
		}
		
		if(inv.getRegistryDocumentType() == null) {
		    Registry registry = RegistryDAO.get(ctx.getContext(), f -> f.getIdProperty().eq(inv.getRegistry()));
            inv.setRegistryDocumentType(registry.getDocumentType());
        }
		
		if(inv.getRegistryDocumentCountry() == null) {
		    Registry registry = RegistryDAO.get(ctx.getContext(), f -> f.getIdProperty().eq(inv.getRegistry()));
            inv.setRegistryDocumentCountry(registry.getDocumentCountry());
		}
	};
	
	/**
	 * Aseguramos el nombre del titular de la factura.
	 */
	@Deprecated
	public static final BiConsumer<Invoice,AonConfigurationContext> COMPLETE_REGISTRY_ADDRESS_DATA = (inv,ctx) -> {
		if(inv.getAddress().isGlobal()) {
			inv.setRegistryAddress(null);
			inv.getAddress().setId(null);
		}
		if(inv.getAddress().getId() != null) {
			RegistryAddressFilter filter = f -> f.getDomainProperty().eq(inv.getDomain()).and(f.getIdProperty().eq(inv.getAddress().getId()));
			RegistryAddress ra = RegistryAddressDAO.get(ctx.getContext(), filter);
			if(ra.getId() == null) {
				inv.setRegistryAddress(null);
				inv.getAddress().setId(null);
			}
		}
		
		if(inv.getAddress() != null && inv.getAddress().getId() == null && !inv.getAddress().isEmpty()) {
			inv.getAddress().setRegistry(inv.getRegistry());
			RegistryAddress raddress = RegistryAddressDAO.save(ctx.getContext(), inv.getAddress());
			inv.setRegistryAddress(raddress.getId());
			inv.setAddress(raddress);
		}	
	};
	
	@Deprecated
	public static final BiConsumer<Invoice,AonConfigurationContext> COMPLETE_REGISTRY_ADDRESS = (inv,ctx) -> {
		if(inv.getAddress() == null || inv.getAddress().isEmpty()) {
			RegistryAddressFilter filter = inv.getRegistryAddress() != null 
					? f -> f.getDomainProperty().eq(inv.getDomain())
							.and(f.getRegistryProperty().eq(inv.getRegistry()))
							.and(f.getIdProperty().eq(inv.getRegistryAddress()))
					: f -> f.getDomainProperty().eq(inv.getDomain())
							.and(f.getRegistryProperty().eq(inv.getRegistry())
							.and(f.getTypeProperty().eq((byte) 0)));
			
			RegistryAddress raddress = RegistryAddressDAO.get(ctx.getContext(), filter);
			inv.setAddress(raddress);
			inv.setRegistryAddress(raddress.getId());
		}
		
		if(inv.getAddress() != null && inv.getAddress().getDomain() == null && !inv.getAddress().isEmpty()) {
			inv.getAddress().setDomain(inv.getDomain());
		}
	};
	
	
	/**
	 * Aseguramos los detalles de la factura.
	 */
	@Deprecated
	public static final BiConsumer<Invoice,AonConfigurationContext> COMPLETE_DETAILS = (inv,ctx) -> {
		if((inv.getDetails() == null || inv.getDetails().isEmpty()) && inv.getBreakdown() != null) {	
			
			LinkedList<InvoiceDetail> invoiceDetails = new LinkedList<>();
			InvoiceBreakdown ret = inv.getBreakdown().stream().filter(f -> TaxType.RETENTION.equals(f.getTaxType())).findFirst().orElse(null);
			inv.getBreakdown().stream().filter(f -> TaxType.VAT.equals(f.getTaxType())).forEach(b -> {
				LinkedList<InvoiceTax> invoiceTax = new LinkedList<>();
				if(ret != null) {
					InvoiceTax it1 = new InvoiceTax()
							.setDomain(inv.getDomain())
							.setBase(b.getBase())
							.setPercentage(ret.getPercentage())
							.setQuota(b.getBase() * ret.getPercentage() / 100)
							.setTaxType(TaxType.RETENTION)
							.setWithholdingType(b.getWithholdingType());
					invoiceTax.add(it1);
				}
				
				InvoiceTax it = new InvoiceTax()
						.setDomain(inv.getDomain())
						.setTaxType(TaxType.VAT)
						.setBase(b.getBase())
						.setPercentage(b.getPercentage())
						.setQuota(b.getQuota())
						.setSurcharge(b.getSurcharge())
						.setSurchargeQuota(b.getSurchargeQuota())
						.setVatDeductionType(VatDeductionType.WITH_RIGHT)
						.setDeductiblePercent(100.0)
						.setDeductibleQuota(b.getQuota());
				invoiceTax.add(it);
				
				Domain domain = DomainDAO.getDomain(ctx.getContext(), inv.getDomain());
				Account acc = null;
				if(domain.isEnableHeredity() && domain.getParentId() != null) {
					Integer[] domains = {domain.getId(), domain.getParentId()};
					acc = AccountDAO.get(ctx.getContext(), ACCOUNT.DOMAIN.in(domains).and(ACCOUNT.CODE.eq(inv.getTediCategory())));
				} else acc = AccountDAO.get(ctx.getContext(), ACCOUNT.DOMAIN.eq(domain.getId()).and(ACCOUNT.CODE.eq(inv.getTediCategory())));
				
				double base = inv.isUndeductible() ?AonMathUtils.round(b.getBase() + b.getQuota()) : b.getBase();
				
				InvoiceDetail id = new InvoiceDetail()
						.setExpAccount( acc )
						.setDescription(acc == null || AonStringUtils.isBlank(acc.getDescription()) 
								? "IVA " + b.getPercentage() : acc.getDescription())
						.setDomain(inv.getDomain())
						.setInvoice(inv.getId())
						.setInvoiceTaxes(invoiceTax)
						.setPrice(base)
						.setQuantity(1)
						.setTaxableBase(base)
						.setSource(InvoiceSource.TEDI)
						.setWorkplace(ctx.getConfiguration().getWorkplaces() != null
							? ctx.getConfiguration().getWorkplaces().getFirst() 
							: null);
				invoiceDetails.add(id);
			});
			inv.setDetails(invoiceDetails);
		}
		inv.getDetails().stream().forEach(detail -> {
			
			if(detail.getId() != null) {
				InvoiceDetail d = InvoiceDetailOLDDAO.get(ctx.getContext(), f-> f.getIdProperty().eq(detail.getId()));
				if(d != null && d.getInvoice() != null && !d.getInvoice().equals(inv.getId())) {
					detail.setId(null);
				}
			}

			detail.setDomain(inv.getDomain());
			
			InvoiceTax it = new InvoiceTax();
			for(Integer i = 0;  i< detail.getInvoiceTaxes().size() ; i++ ) {
				detail.getInvoiceTaxes().get(i).setDomain(inv.getDomain());
				if(TaxType.VAT.equals(detail.getInvoiceTaxes().get(i).getTaxType()))
					it = detail.getInvoiceTaxes().get(i);
			}
			
			if(detail.getWorkplace() == null && AonCollectionUtils.isNotEmpty( ctx.getConfiguration().getWorkplaces() )) {
				detail.setWorkplace(ctx.getConfiguration().getWorkplaces().get(0));
			}
			
			if(detail.getExpAccount() != null 
				&& detail.getExpAccount().getId() == null
				&& detail.getExpAccount().getCode() != null) {
				Domain domain = DomainDAO.getDomain(ctx.getContext(), inv.getDomain());
				Account acc = null;
				if(domain.isEnableHeredity() && domain.getParentId() != null) {
					Integer[] domains = {domain.getId(), domain.getParentId()};
					acc = AccountDAO.get(ctx.getContext(), ACCOUNT.DOMAIN.in(domains).and(ACCOUNT.CODE.eq(inv.getTediCategory())));
				} else acc = AccountDAO.get(ctx.getContext(), ACCOUNT.DOMAIN.eq(domain.getId()).and(ACCOUNT.CODE.eq(inv.getTediCategory())));

				if(acc != null && acc.getId() != null) {
					detail.setExpAccount(acc);
				}
			}
			
			if(detail.getItem() != null && detail.getItem().isEmpty()) {
				String code = detail.getItem().getProduct().getCode();
				if(!AonStringUtils.isBlank(code)) {
					Item i = ItemDAO.get(ctx.getContext(), f -> 
						f.getDomainProperty().eq(inv.getDomain())
						.and(f.getProductCodeProperty().eq(code)));
					if(i.getId() == null) {
						if(detail.getDescription() == null) detail.setDescription(code);
						String name = detail.getDescription().length() > 63
								? detail.getDescription().substring(0, 63) 
								: detail.getDescription();
						i = createProductItem(ctx.getContext(), code, name, detail, it);
					}
					detail.setItem(i);
				}
			}
						
			if(!InvoiceSource.ACCOUNT.equals(detail.getSource()) 
					&& (detail.getItem() == null || detail.getItem().getId() == null)
					&& (detail.getExpAccount() != null
					&& !AonStringUtils.isBlank(detail.getExpAccount().getCode()))) {
				
				Item i = ItemDAO.get(ctx.getContext(), f -> f.getDomainProperty().eq(inv.getDomain()).and(
						f.getDescriptionProperty().eq(detail.getDescription())
						.or(f.getProductCodeProperty().eq(detail.getExpAccount().getCode()))
						.or(f.getProductNameProperty().eq(detail.getDescription()))));
				if(i.getId() == null) {
					String name = detail.getDescription().length() > 63
							? detail.getDescription().substring(0, 63) 
							: detail.getDescription();
					i = createProductItem(ctx.getContext(), detail.getExpAccount().getCode(), name, detail, it);
				}
				detail.setItem(new Item().setId(i.getId()));
			}
			
			if(detail.getWorkplace() == null || detail.getWorkplace().getId() == null) {
				if(detail.getWorkplace() != null){
					if ( detail.getWorkplace().getId() == null && AonStringUtils.isBlank( detail.getWorkplace().getDescription()) ) {
						Workplace wp = WorkplaceDAO.getWorkplace(ctx.getContext(), f -> f.getIdProperty().eq(detail.getWorkplace().getId()));
						detail.setWorkplace(wp);
					}
				} else {
					Workplace wp = ctx.getConfiguration().getWorkplaces().getFirst();
					if(wp != null && wp.getId() != null) {
						detail.setWorkplace(wp);
					}
				}
			}
			
			if (inv.isUndeductible() && it.getTaxType() == TaxType.VAT) {
				detail.setTaxableBase( AonMathUtils.round(it.getBase() + it.getQuota()));
			}
			
		});
	};
	
	@Deprecated
	private static Item createProductItem(AONContext ctx, String code,
			String name, InvoiceDetail id, InvoiceTax it) {
		Product p = new Product();
		p.setDomain(new Domain().setId(id.getDomain()));
		p.setCode(code);
		p.setName(AonStringUtils.isBlank(name) ? code : name);
		p.setVat(new Tax()
			.setName("IVA " + it.getPercentage())
			.setDomain(id.getDomain())
			.setPercentage(it.getPercentage())
			.setStartDate(new Date())
			.setType(TaxType.VAT)
			.setVatDeductionType(VatDeductionType.WITH_RIGHT)
			.setSalesAccount(new Account())
			.setPurchaseAccount(new Account()));
		
		Item i = new Item()
				.setDomain(new Domain().setId(id.getDomain()))
				.setProduct(p)
				.setDescription(id.getDescription())
				.setPrice(id.getPrice());
		
		i = ItemDAO.save(ctx, i);
		return i;
	}
	
	/**
	 * Aseguramos el nombre del titular de la factura.
	 */
	@Deprecated
	public static final BiConsumer<Invoice,AonConfigurationContext> COMPLETE_FINANCES = (inv,ctx) -> 
		inv.getFinances().stream().forEach(finance -> {
			finance.setDomain(inv.getDomain());
			finance.setInvoice(inv);
			finance.setRegistry(inv.getRegistryData());
			finance.setRegistryName(inv.getRegistryName());
			finance.setRegistryDocument(inv.getRegistryDocument());
			finance.setRegistryDocumentType(inv.getRegistryDocumentType());
			finance.setRegistryDocumentCountry(inv.getRegistryDocumentCountry());
			if(finance.getFinanceStatus() == null) {
				finance.setFinanceStatus(FinanceStatus.PENDING);
			}
			
			finance.setPayment(!inv.isSales());
			
			if(finance.getPayMethod() == null && finance.getPayMethodType() != null) {
				PayMethod pm = PayMethodDAO.get(ctx.getContext(), f -> f.getTypeProperty().eq(finance.getPayMethodType().value()));
				if(pm == null || pm.getId() == null) {
					pm = new PayMethod()
							.setDomain(inv.getDomain())
							.setName(finance.getPayMethodType().getDescription() + " (Autogenerado)")
							.setType(finance.getPayMethodType());
					pm = PayMethodDAO.save(ctx.getContext(), pm);
				}
				finance.setPayMethod(pm.getId());
				finance.setPayMethodName(pm.getName());
			}
		});
	

	/**
	 * Aseguramos el ambito de la factura.
	 */
		@Deprecated
	public static final BiConsumer<Invoice,AonConfigurationContext> COMPLETE_SCOPE = (inv,ctx) -> {
		if((inv.getScope() == null || inv.getScope().getId() == null) && inv.getRegistry() != null)  {
			if(inv.isSales()) {
				Customer customer = CustomerDAO.get(ctx.getContext(), inv.getRegistry());
				inv.setScope(customer.getScope());
			} else if(inv.isPurchase()) {
				Supplier supplier = SupplierDAO.get(ctx.getContext(), inv.getRegistry());
				inv.setScope(supplier.getScope());				
			} else if(inv.isExpenses() || inv.isUndeductible()){
				Creditor creditor = CreditorDAO.get(ctx.getContext(), inv.getRegistry());
				inv.setScope(creditor.getScope());
			}
		}

		if(inv.getScope() == null || inv.getScope().getId() == null) {
			List<Scope> scopes = SecurityDAO.getScopeStream(ctx.getContext(),  f -> f.getDomainProperty().eq(inv.getDomain())).toList();
			if(scopes.isEmpty()) {
				Scope scope = SecurityDAO.insertScope(ctx.getContext(), new Scope()
						.setDescription("GENERAL")
						.setDomain(inv.getDomain()));
				inv.setScope(scope);
			} else if(scopes.size() == 1) {
				inv.setScope(scopes.getFirst());	
			} else {
				Workplace wp = new Workplace();
				if(!inv.getDetails().isEmpty() && inv.getDetails().getFirst().getWorkplace() != null) {
					wp = inv.getDetails().getFirst().getWorkplace();
				}
				if((wp == null ||  wp.getId() == null) && !ctx.getConfiguration().getWorkplaces().isEmpty()) {
					wp = ctx.getConfiguration().getWorkplaces().getFirst();  
				}
				inv.setScope(wp.getScope() != null ? new Scope().setId(wp.getScope()): scopes.getFirst());
			}
		}
	};
	

	/**
	 * Aseguramos la base imponible de la factura.
	 */
	@Deprecated
	public static final BiConsumer<Invoice,AonConfigurationContext> COMPLETE_TAXABLE_BASE = (inv,ctx) -> {
		if (inv.isUndeductible() ) {
			inv.setTaxableBase(inv.getTotal());
			inv.setVatQuota( 0.0 );
			inv.setRetentionQuota( 0.0 );
		} else if(inv.getTaxableBase() == 0 ) {
			Double taxableBase = inv.getBreakdown().stream().filter(f -> TaxType.VAT.equals(f.getTaxType()))
					.mapToDouble(r -> r.getBase()).sum();
			double prepayment = inv.getDetails().stream().filter(f -> f.isPrepayment())
					.mapToDouble(r -> r.getPrice() * r.getQuantity() * (1 - r.getDiscount()/100))
					.sum();
			inv.setTaxableBase(AonMathUtils.round(taxableBase + prepayment));
		} 
		
	};
	
	@Deprecated
	public static void completeInvoice(AONContext ctx, AonConfiguration config,Invoice inv) throws AonCoreException {
		COMPLETE_DOMAIN
		.andThen(COMPLETE_SALES_SERIES)
		.andThen(COMPLETE_PURCHASE_EXPENSES_SERIES)
		.andThen(COMPLETE_UNDEDUCTIBLE_SERIES)
		.andThen(COMPLETE_UNDEDUCTIBLE_REFERENCE_CODE)
		.andThen(COMPLETE_TAX_DATE)
		.andThen(COMPLETE_RECTIFICATION_TYPE)
		.andThen(ENSURE_REGISTRY_DATA)
		.andThen(COMPLETE_REGISTRY_ADDRESS)
		.andThen(COMPLETE_ACTIVITY)
		.andThen(COMPLETE_SECURITY_LEVEL)
		.andThen(COMPLETE_FIRST_FINANCE)
		.accept(inv, new AonConfigurationContext(ctx,config));
	}
	
	@Deprecated
	public static void completeInvoice2(AONContext ctx, AonConfiguration config, Invoice inv) throws AonCoreException {
		COMPLETE_DOMAIN
		.andThen(COMPLETE_SALES_SERIES)
		.andThen(COMPLETE_PURCHASE_EXPENSES_SERIES)
		.andThen(COMPLETE_UNDEDUCTIBLE_SERIES)
		.andThen(COMPLETE_UNDEDUCTIBLE_REFERENCE_CODE)
		.andThen(COMPLETE_TAX_DATE)
		.andThen(COMPLETE_RECTIFICATION_TYPE)
		.andThen(COMPLETE_SCOPE)
		.andThen(COMPLETE_REGISTRY_DATA)
		.andThen(ENSURE_REGISTRY_DATA)
		.andThen(COMPLETE_REGISTRY_ADDRESS_DATA)
		.andThen(COMPLETE_REGISTRY_ADDRESS)
		.andThen(COMPLETE_ACTIVITY)
		.andThen(COMPLETE_FIRST_FINANCE)
		.andThen(COMPLETE_DETAILS)
		.andThen(COMPLETE_FINANCES)
		.andThen(COMPLETE_TAXABLE_BASE)
		.accept(inv, new AonConfigurationContext(ctx,config));
	}
}
