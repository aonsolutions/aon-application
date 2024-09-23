package net.aonsolutions.aon.tedi.invofox;

import java.util.LinkedList;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorKey;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorMessages;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryType;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.impl.jooq.dao.AccountingRegistryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryAddressDAO;
import com.esferalia.aon.occam.impl.jooq.dao.accounting.InvoiceRegistryInitializer;

import net.aonsolutions.aon.tedi.invofox.OCRInvoiceBuilder.OCRContext;

class OCRInvoiceBuilderRegistry {
	
	private OCRInvoiceBuilderRegistry() {
		
	}

	static interface IRegistryFiller {
		boolean accept(OCRContext ocr);
		void fill(OCRContext ocr);	
	}

	abstract static class RegistryFiller implements IRegistryFiller {
	
		protected boolean fillRegistry(OCRContext ocr, Predicate<AccountingRegistry> filterExpression) {
		    try {
				OCRInvoiceBuilderRegistry.fillRegistry(ocr.getCtx(), ocr.getConfig(), filterExpression, ocr.getInvoice());
				return true;
		    } catch (OCRTooManyOwnersException e) {
		    	ocr.add(InvoiceErrorMessages.C011.err(InvoiceErrorKey.AMBIGUOUS_REGISTRY));
		    } catch (OCROwnerNotFoundException e) {
		    }
		    if (ocr.getInvoice().getRegistryDocumentCountry() == null) {
				ocr.getInvoice().setRegistryDocumentCountry(Country.ES);
				ocr.add(InvoiceErrorMessages.C003.inf(InvoiceErrorKey.RDOCUMENT_COUNTRY,InvoiceErrorKey.RDOCUMENT_COUNTRY.getDescription(), Country.ES.getIso2()));
		    }
		    return false;
		}		
	}		

	static class EmitidaFiller extends RegistryFiller {
		@Override
		public boolean accept(OCRContext ocr) {
			return ocr.getInvoice().isSales();
		}
	
		@Override
		public void fill(OCRContext ocr) {
			super.fillRegistry(ocr, ar -> ocr.getInvoice().isSales() && ar.getType() == AccountingRegistryType.CUSTOMER);
		}
	}

	static class RecibidaFiller extends RegistryFiller {
		
		@Override
		public boolean accept(OCRContext ocr) {
			return ocr.getInvoice().isExpenses() || ocr.getInvoice().isPurchase();
		}
	
		@Override
		public void fill(OCRContext ocr) {
			super.fillRegistry(ocr, ar -> 
				(ocr.getInvoice().isExpenses() || ocr.getInvoice().isPurchase()) 
				&& (ar.getType() == AccountingRegistryType.SUPPLIER || ar.getType() == AccountingRegistryType.CREDITOR));
		}
	}

	static class TicketFiller extends RegistryFiller {
		@Override
		public boolean accept(OCRContext ocr) {
			return ocr.getInvoice().isUndeductible();
		}
	
		@Override
		public void fill(OCRContext ocr) {
			if (!super.fillRegistry(ocr, ar -> ocr.getInvoice().isUndeductible() 
					&& ar.getType() == AccountingRegistryType.CREDITOR)){
				if (ocr.getConfig().getDefaultCreditor() != null) {
					AccountingRegistry ar = ocr.getConfig().getDefaultCreditor();
					ocr.getInvoice()
						.setRegistry(ar.getId())
						.setTransaction(ar.getTransaction());
					ar.getType().visit(new InvoiceRegistryInitializer(ocr.getCtx(), ocr.getInvoice(), ocr.getConfig(), ar));
				}
			};
		}
	}

	static IRegistryFiller[] FILLERS = new IRegistryFiller[] {
		new EmitidaFiller()
		,new RecibidaFiller()
		,new TicketFiller() 
	};
	
	public static final void fillRegistry (AONContext aonCtx,
			AonConfiguration config,
			Predicate<AccountingRegistry> filterExpression, Invoice invoice) throws OCRTooManyOwnersException, OCROwnerNotFoundException  {
		LinkedList<AccountingRegistry> registries = AccountingRegistryDAO
				.getAccountingRegistries(aonCtx, f -> f.getDocumentProperty().eq(invoice.getRegistryDocument()))
				.filter(filterExpression)
				.collect(Collectors.toCollection(LinkedList::new));
		if (registries != null && !registries.isEmpty()) {
			if (registries.size() == 1) {
				AccountingRegistry ar = registries.get(0);
				invoice
					.setRegistry(ar.getId())
					.setTransaction(ar.getTransaction())
					.setRegistryData( new Registry( )
						.setId(ar.getId())
						.setDocument(ar.getDocument())
						.setDocumentType(ar.getDocumentType())
						.setDocumentCountry(ar.getDocumentCountry())
						.setName(ar.getName())
						.setAlias(ar.getAlias())
						.setNationality(ar.getNationality())
					);
				ar.getType().visit(new InvoiceRegistryInitializer(aonCtx, invoice, config, ar));
				
				if(invoice.getAddress() == null || invoice.getAddress().isEmpty()) {
					RegistryAddress address = RegistryAddressDAO.getMain(aonCtx, ar.getId());
					if(address != null && !address.isEmpty()) {
						invoice.setAddress(address);
						invoice.setRegistryAddress(address.getId());
					}
				}
				return;
			} else if(registries.size() == 2) {
				AccountingRegistry ar = registries.get(0);
				AccountingRegistry ar2 = registries.get(1);
				if(ar.getId().equals(ar2.getId())) {
					invoice.setRegistry(ar.getId())
						.setTransaction(ar.getTransaction())
						.setRegistryData( new Registry( )
							.setId(ar.getId())
							.setDocument(ar.getDocument())
							.setDocumentType(ar.getDocumentType())
							.setDocumentCountry(ar.getDocumentCountry())
							.setName(ar.getName())
							.setAlias(ar.getAlias())
							.setNationality(ar.getNationality())
						);
					ar.getType().visit(new InvoiceRegistryInitializer(aonCtx, invoice, config, ar));
					
					if(invoice.getAddress() == null || invoice.getAddress().isEmpty()) {
						RegistryAddress address = RegistryAddressDAO.getMain(aonCtx, ar.getId());
						if(address != null && !address.isEmpty()) {
							invoice.setAddress(address);
							invoice.setRegistryAddress(address.getId());
						}
					}
					return;
				} else throw new OCRTooManyOwnersException(); 

			} else {
				throw new OCRTooManyOwnersException();
			}
		} else {
		    throw new OCROwnerNotFoundException();
		}
//		if (invoice.getRegistryDocumentCountry() == null) {
//			invoice.setRegistryDocumentCountry(Country.ES);
//			throw new OCRUndefinedDocumentCountryException();
//		}
		
	}
}
 