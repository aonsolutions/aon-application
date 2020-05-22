package com.esferalia.aon.gwt.template.server.imports;

import static com.esferalia.aon.jooq.tables.AccountEntryDetail.ACCOUNT_ENTRY_DETAIL;
import static com.esferalia.aon.jooq.tables.Creditor.CREDITOR;
import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.Supplier.SUPPLIER;
import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.AccountHelper.ACCOUNT_HELPER;

import java.util.LinkedList;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.Supplier;

public class ImportFixer {

	public static void fixAccounts(Domain domain, String login) {
		Stream<Account> accounts = ACCOUNTING.getAccounts(domain.getName(), domain.getId(), login, f -> 
			f.getDomainProperty().eq(domain.getId())
			.and(
				f.getCodeProperty().like("400%")
				.or(f.getCodeProperty().like("410%"))
				.or(f.getCodeProperty().like("430%"))
			));
		
		accounts.forEach(acc -> {
			if(acc.getCode().length() > 5) {	
				Customer c = AON.getCustomer(domain.getName(), domain.getId(), login, f -> f.getAccountProperty().eq(acc.getId()));
				Optional<Supplier> s = AON.getSupplier(domain.getName(), domain.getId(), login, f -> f.getAccountProperty().eq(acc.getId()));
				Optional<Creditor> a = AON.getCreditor(domain.getName(), domain.getId(), login, f -> f.getAccountProperty().eq(acc.getId()));
				Integer size = getAccountEntryDetail(domain, login, acc.getId());
				if((c == null || c.getId() == null) && !s.isPresent() && !a.isPresent() && size == 0) {
					System.out.println(acc.getCode());
					deleteAccount(domain, login, acc.getId());
				}
			}

		});
	}
	
	public static void fixCustomer(Domain domain, String login) {
		LinkedList<Customer> customers = AON.getCustomerList(domain.getName(), domain.getId(), login, f -> f.getDomainProperty().eq(domain.getId()));
		LinkedList<String> names = customers.stream().map(r -> r.getName()).distinct().collect(Collectors.toCollection(LinkedList::new));
		names.forEach(name -> {
			LinkedList<Customer> list =  new LinkedList<>();
			try {
				list = customers.stream().filter(f -> name.equals(f.getName())).collect(Collectors.toCollection(LinkedList::new));
			} catch (Exception e) {
				System.out.println(name);
			}
			if(list.size() > 1) {
				OptionalInt accountMinId = list.stream().filter(f -> f.getAccount() != null).mapToInt(r -> r.getAccount()).min();
				if(accountMinId.isPresent()) {
					LinkedList<Integer> accounts  = list.stream().filter(f -> f.getAccount() != null).map(r -> r.getAccount()).collect(Collectors.toCollection(LinkedList::new));
					updateAccount(domain, login, accountMinId.getAsInt(), accounts);
					LinkedList<Customer> theCustomers = list.stream().filter(f -> f.getDocument() != null).collect(Collectors.toCollection(LinkedList::new));
					Integer customerId = -1;
					if(theCustomers.size() >= 1) {
						customerId = theCustomers.get(0).getId();
					} else {
						customerId = list.get(0).getId();
					}
					if(customerId >= 0) {
						final Integer cid = customerId;
						updateCustomerAccount(domain, login, customerId, accountMinId.getAsInt());
						LinkedList<Integer> otherCustomers = list.stream().filter(f -> !f.getId().equals(cid)).map(r -> r.getId()).collect(Collectors.toCollection(LinkedList::new));
						deleteCustomers(domain, login, otherCustomers);
					}
				}
			}
		});
	}
	
	public static void fixSupplier(Domain domain, String login) {
		LinkedList<Supplier> suppliers = AON.getSupplierList(domain.getName(), domain.getId(), login, f -> f.getDomainProperty().eq(domain.getId()));
		LinkedList<String> names = suppliers.stream().map(r -> r.getName()).distinct().collect(Collectors.toCollection(LinkedList::new));
		names.forEach(name -> {
			LinkedList<Supplier> list =  new LinkedList<>();
			try {
				list = suppliers.stream().filter(f -> name.equals(f.getName())).collect(Collectors.toCollection(LinkedList::new));
			} catch (Exception e) {
				System.out.println(name);
			}
			if(list.size() > 1) {
				OptionalInt accountMinId = list.stream().filter(f -> f.getAccount() != null).mapToInt(r -> r.getAccount()).min();
				if(accountMinId.isPresent()) {
					LinkedList<Integer> accounts  = list.stream().filter(f -> f.getAccount() != null).map(r -> r.getAccount()).collect(Collectors.toCollection(LinkedList::new));
					updateAccount(domain, login, accountMinId.getAsInt(), accounts);
					LinkedList<Supplier> theSuppliers = list.stream().filter(f -> f.getDocument() != null).collect(Collectors.toCollection(LinkedList::new));
					Integer supplierId = -1;
					if(theSuppliers.size() > 1) {
						// TU PUTA MADRE!!!!
					} else if(theSuppliers.size() == 1) {
						supplierId = theSuppliers.get(0).getId();
					} else {
						supplierId = list.get(0).getId();
					}
					
					if(supplierId >= 0) {
						final Integer sid = supplierId;
						updateSupplierAccount(domain, login, supplierId, accountMinId.getAsInt());
						LinkedList<Integer> otherSuppliers = list.stream().filter(f -> !f.getId().equals(sid)).map(r -> r.getId()).collect(Collectors.toCollection(LinkedList::new));
						deleteSuppliers(domain, login, otherSuppliers);
					}
				}
			}
		});
	}
	
	public static void fixCreditor(Domain domain, String login) {
		LinkedList<Creditor> creditors = AON.getCreditorList(domain.getName(), domain.getId(), login, f -> f.getDomainProperty().eq(domain.getId()));
		LinkedList<String> names = creditors.stream().map(r -> r.getRegistry().getName()).distinct().collect(Collectors.toCollection(LinkedList::new));
		names.forEach(name -> {
			LinkedList<Creditor> list =  new LinkedList<>();
			try {
				list = creditors.stream().filter(f -> f.getRegistry() != null && name.equals(f.getRegistry().getName())).collect(Collectors.toCollection(LinkedList::new));
			} catch (Exception e) {
				System.out.println(name);
			}
			if(list.size() > 1) {
				OptionalInt accountMinId = list.stream().filter(f -> f.getAccount() != null && f.getAccount().getId() != null).mapToInt(r -> r.getAccount().getId()).min();
				if(accountMinId.isPresent()) {
					LinkedList<Integer> accounts  = list.stream().filter(f -> f.getAccount() != null && f.getAccount().getId() != null).map(r -> r.getAccount().getId()).collect(Collectors.toCollection(LinkedList::new));
					updateAccount(domain, login, accountMinId.getAsInt(), accounts);
					LinkedList<Creditor> theCreditors = list.stream().filter(f -> f.getRegistry().getDocument() != null ).collect(Collectors.toCollection(LinkedList::new));
					Integer creditorId = -1;
					if(theCreditors.size() > 1) {
						// TU PUTA MADRE!!!!
					} else if(theCreditors.size() == 1) {
						creditorId = theCreditors.get(0).getId();
					} else {
						creditorId = list.get(0).getId();
					}
					if(creditorId >= 0) {
						final Integer cid = creditorId;
						updateCreditorAccount(domain, login, creditorId, accountMinId.getAsInt());
						LinkedList<Integer> otherCreditors = list.stream().filter(f -> !f.getId().equals(cid)).map(r -> r.getId()).collect(Collectors.toCollection(LinkedList::new));
						deleteCreditors(domain, login, otherCreditors);
					}
				}
			}
		});
	}
	
	private static void updateAccount(Domain domain, String login, Integer account, LinkedList<Integer> accounts) {
		try (AONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){		
			ctx.getDslContext()
				.update(ACCOUNT_ENTRY_DETAIL)
				.set(ACCOUNT_ENTRY_DETAIL.ACCOUNT, account)
				.where(ACCOUNT_ENTRY_DETAIL.ACCOUNT.in(accounts))
				.execute();
			
			ctx.getDslContext()
				.update(ACCOUNT_ENTRY_DETAIL)
				.set(ACCOUNT_ENTRY_DETAIL.BALANCING_ACCOUNT, account)
				.where(ACCOUNT_ENTRY_DETAIL.BALANCING_ACCOUNT.in(accounts))
				.execute();
		}
	}
	
	private static void updateCustomerAccount(Domain domain, String login, Integer customerId, Integer account) {
		try (AONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){		
			ctx.getDslContext()
				.update(CUSTOMER)
				.set(CUSTOMER.ACCOUNT, account)
				.where(CUSTOMER.REGISTRY.eq(customerId))
				.execute();
		}
	}
	
	private static void deleteCustomers(Domain domain, String login, LinkedList<Integer> customers) {
		try (AONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){		
			ctx.getDslContext()
				.delete(CUSTOMER)
				.where(CUSTOMER.REGISTRY.in(customers))
				.execute();
		}
		//customers.stream().forEach(id -> AON.deleteRegistry(domain.getName(), domain.getId(), login, id));
	}
	
	private static void updateSupplierAccount(Domain domain, String login, Integer supplierId, Integer account) {
		try (AONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){		
			ctx.getDslContext()
				.update(SUPPLIER)
				.set(SUPPLIER.ACCOUNT, account)
				.where(SUPPLIER.REGISTRY.eq(supplierId))
				.execute();
		}
	}
	
	private static void deleteSuppliers(Domain domain, String login, LinkedList<Integer> suppliers) {
		try (AONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){		
			ctx.getDslContext()
				.delete(SUPPLIER)
				.where(SUPPLIER.REGISTRY.in(suppliers))
				.execute();
		}
		//suppliers.stream().forEach(id -> AON.deleteRegistry(domain.getName(), domain.getId(), login, id));
	}
	
	private static void updateCreditorAccount(Domain domain, String login, Integer creditorId, Integer account) {
		try (AONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){		
			ctx.getDslContext()
				.update(CREDITOR)
				.set(CREDITOR.ACCOUNT, account)
				.where(CREDITOR.REGISTRY.eq(creditorId))
				.execute();
		}
	}
	
	private static void deleteCreditors(Domain domain, String login, LinkedList<Integer> creditors) {
		try (AONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){		
			ctx.getDslContext()
				.delete(CREDITOR)
				.where(CREDITOR.REGISTRY.in(creditors))
				.execute();
		}
		//creditors.stream().forEach(id -> AON.deleteRegistry(domain.getName(), domain.getId(), login, id));
	}
	
	private static Integer getAccountEntryDetail(Domain domain, String login, Integer account) {
		try (AONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){		
			return ctx.getDslContext()
				.select()
				.from(ACCOUNT_ENTRY_DETAIL)
				.where(ACCOUNT_ENTRY_DETAIL.ACCOUNT.eq(account))
				.fetch().size();
		}
	}
	
	private static void deleteAccount(Domain domain, String login, Integer account) {
		try (AONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){
			ctx.getDslContext()
				.delete(ACCOUNT_HELPER)
				.where(
					ACCOUNT_HELPER.ACCOUNT.eq(account)
					.or(ACCOUNT_HELPER.BALANCING_ACCOUNT.eq(account)))
				.execute();

			ctx.getDslContext()
				.delete(ACCOUNT)
				.where(ACCOUNT.ID.eq(account))
				.execute();
		}
	}
}
