package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.Date;
import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.impl.jooq.dao.AccountDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CreditorDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CustomerDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.Mod3902014DAO.DetailKey;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryOldDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SupplierDAO;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class InvoiceAutoComplete {

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
	public static BiConsumer<Invoice,AonConfigurationContext> COMPLETE_DOMAIN = (inv,ctx) -> {
		if(inv.getDomain() == null) {
			inv.setDomain(ctx.getContext().getDomainId());
		}
	};

	
	
	/**
	 * Se rellena el número de referencia para las facturas de ventas.
	 */
	public static BiConsumer<Invoice,AonConfigurationContext> COMPLETE_SALES_SERIES = (inv,ctx) -> {
		if (inv.isSales()) {
			if (AonStringUtils.isBlank(inv.getSeries())) {
				inv.setSeries(null);
			}
			if (inv.getNumber() == 0) {
				Byte[] types = new Byte[]{InvoiceType.SALES.value()};
				int number = InvoiceDAO.getNextNumber(ctx.getContext(),types, inv.getSeries());
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
	public static BiConsumer<Invoice,AonConfigurationContext> COMPLETE_PURCHASE_EXPENSES_SERIES = (inv,ctx) -> {
		if (inv.isPurchase() || inv.isExpenses()) {
			inv.setSeries(Integer.toString(AonDateUtils.getYear(inv.getIssueDate())));
			if (inv.getNumber() == 0) {
				Byte[] types = new Byte[]{InvoiceType.PURCHASE.value(),InvoiceType.EXPENSES.value()};
				int number = InvoiceDAO.getNextNumber(ctx.getContext(),types, inv.getSeries());
				inv.setNumber(number);
			}
		} 
	};

	/**
	 * Se rellena las serie y numero para las facturas de compras y gastos.
	 */
	public static BiConsumer<Invoice,AonConfigurationContext> COMPLETE_UNDEDUCTIBLE_REFERENCE_CODE = (inv,ctx) -> {
		if (inv.isUndeductible() && AonStringUtils.equalsIgnoreCase("<auto>",inv.getReferenceCode())) {
			inv.setReferenceCode( inv.getDocumentNumber());
		} 
	};

	/**
	 * Se rellena las serie y numero para las facturas de compras y gastos.
	 */
	public static BiConsumer<Invoice,AonConfigurationContext> COMPLETE_UNDEDUCTIBLE_SERIES = (inv,ctx) -> {
		if (inv.isUndeductible()) {
			inv.setSeries(Integer.toString(AonDateUtils.getYear(inv.getIssueDate()==null?new Date():inv.getIssueDate())));
			if (inv.getNumber() == 0) {
				Byte[] types = new Byte[]{InvoiceType.UNDEDUCTIBLE.value()};
				int number = InvoiceDAO.getNextNumber(ctx.getContext(),types, inv.getSeries());
				inv.setNumber(number);
			}
		} 
	};
	
	/**
	 * Aseguramos el SecurityLevel.
	 */
	public static BiConsumer<Invoice,AonConfigurationContext> COMPLETE_SECURITY_LEVEL = (inv,ctx) -> {
		if (inv.getSecurityLevel() == null) inv.setSecurityLevel(SecurityLevel.OFFICIAL); 
	};
	
	/**
	 * Aseguramos la fecha de IVA..
	 */
	public static BiConsumer<Invoice,AonConfigurationContext> COMPLETE_TAX_DATE = (inv,ctx) -> {
		if (inv.getTaxDate() == null) inv.setTaxDate(inv.getIssueDate());
	};
	
	/**
	 * Aseguramos la Actividad.
	 */
	public static BiConsumer<Invoice,AonConfigurationContext> COMPLETE_ACTIVITY = (inv,ctx) -> {
		if (inv.getActivity() == null
			&& ctx.getConfiguration() != null 
			&& ctx.getConfiguration().getActivities() != null 
			&& ctx.getConfiguration().getActivities().size() == 1) {
			
			EnterpriseActivity act = ctx.getConfiguration().getMainActivity(); 
			inv.setActivity( act == null ? null : act.getId() );
		}
	};


	/**
	 * Si solo hay un vencimiento, el importe será igual al total factura.
	 */
	public static BiConsumer<Invoice,AonConfigurationContext> COMPLETE_FIRST_FINANCE = (inv,ctx) -> {
		if (inv.hasFinances() && inv.getFinances().size() == 1 && inv.getFinances().get(0).isPending()) {
			inv.getFinances().get(0).setAmount( inv.getTotal());
		}
	};

	/**
	 * Aseguramos la fecha de IVA..
	 */
	public static BiConsumer<Invoice,AonConfigurationContext> COMPLETE_RECTIFICATION_TYPE = (inv,ctx) -> {
		if (inv.getRectificationType() == null) inv.setRectificationType(RectificationType.NONE);
	};
	
	
	/**
	 * Aseguramos el nombre del titular de la factura.
	 */
	public static BiConsumer<Invoice,AonConfigurationContext> ENSURE_REGISTRY_DATA = (inv,ctx) -> {
		if (AonStringUtils.isBlank(inv.getRegistryName()) || AonStringUtils.isBlank(inv.getRegistryDocument())) {
			Registry registry = RegistryOldDAO.getRegistry(ctx.getContext(), f-> f.getIdProperty().eq(inv.getRegistry()));
			if (AonStringUtils.isBlank(inv.getRegistryName())) inv.setRegistryName(registry.getName());
			if (AonStringUtils.isBlank(inv.getRegistryDocument())) {
				inv.setRegistryDocumentType(registry.getDocumentType());
				inv.setRegistryDocumentCountry(registry.getDocumentCountry());
				inv.setRegistryDocument(registry.getDocument());
			}
		}
	};
	
	/**
	 * Aseguramos el nombre del titular de la factura.
	 */
	public static BiConsumer<Invoice,AonConfigurationContext> COMPLETE_REGISTRY_DATA = (inv,ctx) -> {
		if(inv.getRegistry() == null && inv.getRegistryData() != null) {
			if(inv.getRegistryData().getId() == null) {
				if(InvoiceType.SALES.equals(inv.getType())) {
					Customer c = CustomerDAO.getStream(ctx.getContext(), f -> 
							f.getDomainProperty().eq(inv.getDomain())
							.and(f.getDocumentProperty().eq(inv.getRegistryData().getDocument()))).findFirst().orElse(new Customer());
					if(c.getId() != null) {
						inv.setRegistry(c.getId());
					} else {
						c = CustomerDAO.save(ctx.getContext(), (Customer) inv.getRegistryData());
						if(c.getId() != null) {
							inv.setRegistry(c.getId());
						}
					}
				} else if(InvoiceType.PURCHASE.equals(inv.getType())) {
					Supplier s = SupplierDAO.getStream(ctx.getContext(), f -> 
						f.getDomainProperty().eq(inv.getDomain())
					.and(f.getDocumentProperty().eq(inv.getRegistryData().getDocument()))).findFirst().orElse(new Supplier());
					if(s.getId() != null) {
						inv.setRegistry(s.getId());
					} else {
						s = SupplierDAO.save(ctx.getContext(), (Supplier) inv.getRegistryData());
						if(s.getId() != null) {
							inv.setRegistry(s.getId());
						}
					}
				} else if(InvoiceType.EXPENSES.equals(inv.getType()) 
						|| InvoiceType.UNDEDUCTIBLE.equals(inv.getType())) {
					Creditor c = CreditorDAO.getStream(ctx.getContext(), f -> 
						f.getDomainProperty().eq(inv.getDomain())
						.and(f.getDocumentProperty().eq(inv.getRegistryData().getDocument()))).findFirst().orElse(new Creditor());
					if(c.getId() != null) {
						inv.setRegistry(c.getId());
					} else {
						c = CreditorDAO.save(ctx.getContext(), (Creditor) inv.getRegistryData());
						if(c.getId() != null) {
							inv.setRegistry(c.getId());
						}
					}
				}
			} else inv.setRegistry(inv.getRegistryData().getId());
		}
	};
	
	/**
	 * Aseguramos el nombre del titular de la factura.
	 */
	public static BiConsumer<Invoice,AonConfigurationContext> COMPLETE_DETAILS = (inv,ctx) -> {
		inv.getDetails().stream().forEach(detail -> {

			detail.setDomain(inv.getDomain());
			
			for(Integer i = 0;  i< detail.getInvoiceTaxes().size() ; i++ ) {
				detail.getInvoiceTaxes().get(i).setDomain(inv.getDomain());
			}
			
			if(detail.getWorkPlace() == null) {
				detail.setWorkPlace(ctx.getConfiguration().getWorkplaces().getFirst().getId());
			}
			if(detail.getAccount() == null && detail.getAccountCode() != null) {
				Account acc = AccountDAO.get(ctx.getContext(), detail.getAccountCode());
				if(acc != null && acc.getId() != null) {
					detail.setAccount(acc.getId());
					detail.setAccountDescription(acc.getDescription());
				}
			}
		});
	};

	/**
	 * Aseguramos el nombre del titular de la factura.
	 */
	public static BiConsumer<Invoice,AonConfigurationContext> COMPLETE_SCOPE = (inv,ctx) -> {
		if(inv.getScope() == null || inv.getScope().getId() == null) {
			Integer scope;
			User user = SecurityDAO.getUser(ctx.getContext());	
			Scope s = SecurityDAO.getUserScopeStream(ctx.getContext(), user.getId(), f -> f.getDescriptionProperty().eq("GENERAL")).findFirst().orElse(null);
			
			if(s == null) {
				Integer[] scopes = SecurityDAO.getUserScopes(ctx.getContext(), user.getId());
				if(scopes != null && scopes.length > 0)
					scope = scopes[0];
				else {
					s = SecurityDAO.getScopeStream(ctx.getContext(),  f ->
						f.getDomainProperty().eq(inv.getDomain())).findFirst().orElse(null);
					if(s == null) {
						s = SecurityDAO.insertScope(ctx.getContext(), new Scope()
							.setDescription("GENERAL")
							.setDomain(inv.getDomain()));
					}
					scope = s.getId();
				}
			} else scope = s.getId();
			inv.setScope(new Scope().setId(scope));
		}
	};
	
	public static void completeInvoice(AONContext ctx, AonConfiguration config,Invoice inv) throws AonCoreException {
		COMPLETE_DOMAIN
		.andThen(COMPLETE_SALES_SERIES)
		.andThen(COMPLETE_PURCHASE_EXPENSES_SERIES)
		.andThen(COMPLETE_UNDEDUCTIBLE_SERIES)
		.andThen(COMPLETE_UNDEDUCTIBLE_REFERENCE_CODE)
		.andThen(COMPLETE_TAX_DATE)
		.andThen(COMPLETE_RECTIFICATION_TYPE)
		.andThen(ENSURE_REGISTRY_DATA)
		.andThen(COMPLETE_ACTIVITY)
		.andThen(COMPLETE_FIRST_FINANCE)
		.accept(inv, new AonConfigurationContext(ctx,config));
	}
	
	public static void completeInvoice2(AONContext ctx, AonConfiguration config,Invoice inv) throws AonCoreException {
		COMPLETE_DOMAIN
		.andThen(COMPLETE_SALES_SERIES)
		.andThen(COMPLETE_PURCHASE_EXPENSES_SERIES)
		.andThen(COMPLETE_UNDEDUCTIBLE_SERIES)
		.andThen(COMPLETE_UNDEDUCTIBLE_REFERENCE_CODE)
		.andThen(COMPLETE_TAX_DATE)
		.andThen(COMPLETE_RECTIFICATION_TYPE)
		.andThen(COMPLETE_REGISTRY_DATA)
		.andThen(ENSURE_REGISTRY_DATA)
		.andThen(COMPLETE_ACTIVITY)
		.andThen(COMPLETE_FIRST_FINANCE)
		.andThen(COMPLETE_DETAILS)
		.andThen(COMPLETE_SCOPE)
		.accept(inv, new AonConfigurationContext(ctx,config));

	}

}
