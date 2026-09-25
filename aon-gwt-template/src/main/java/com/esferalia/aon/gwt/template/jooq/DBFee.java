package com.esferalia.aon.gwt.template.jooq;

import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import java.util.LinkedList;
import java.util.stream.Collectors;

import org.jooq.Record4;
import org.jooq.Record5;
import org.jooq.Result;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.finance.InvoicingGroup;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
import com.esferalia.aon.occam.api.model.type.SellerStatus;

public class DBFee {

	public static DBFee getInstance() {
		return new DBFee();
	}
	
	
	public Customer getCustomer(Domain domain, String login, String client, Boolean ignoreInactiveClient){
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login);
			Result<Record5<Integer, String, String, String, Byte>> result = null;
			if(ignoreInactiveClient){
				result = ctx.getDslContext().select(REGISTRY.ID, REGISTRY.ALIAS,
						REGISTRY.DOCUMENT, REGISTRY.NAME,
						CUSTOMER.STATUS)
								.from(REGISTRY).join(CUSTOMER).on(REGISTRY.ID.eq(CUSTOMER.REGISTRY))
								.where(REGISTRY.DOCUMENT.eq(client))
								.and(REGISTRY.DOMAIN.eq(domain.getId()))
								.and(CUSTOMER.STATUS.eq((byte) 0))
								.fetch();
				if(result.isEmpty()){
					result = ctx.getDslContext().select(REGISTRY.ID, REGISTRY.ALIAS,
							REGISTRY.DOCUMENT, REGISTRY.NAME,
							CUSTOMER.STATUS)
									.from(REGISTRY).join(CUSTOMER).on(REGISTRY.ID.eq(CUSTOMER.REGISTRY))
									.where(REGISTRY.NAME.eq(client))
									.and(REGISTRY.DOMAIN.eq(domain.getId()))
									.and(CUSTOMER.STATUS.eq((byte) 0))
									.fetch();
				}
			}
			else{
				result = ctx.getDslContext().select(REGISTRY.ID, REGISTRY.ALIAS,
						REGISTRY.DOCUMENT, REGISTRY.NAME,
						CUSTOMER.STATUS)
								.from(REGISTRY).join(CUSTOMER).on(REGISTRY.ID.eq(CUSTOMER.REGISTRY))
								.where(REGISTRY.DOCUMENT.eq(client))
								.and(REGISTRY.DOMAIN.eq(domain.getId()))
								.fetch();
				if(result.isEmpty()){
					result = ctx.getDslContext().select(REGISTRY.ID, REGISTRY.ALIAS,
							REGISTRY.DOCUMENT, REGISTRY.NAME,
							CUSTOMER.STATUS)
									.from(REGISTRY).join(CUSTOMER).on(REGISTRY.ID.eq(CUSTOMER.REGISTRY))
									.where(REGISTRY.NAME.eq(client))
									.and(REGISTRY.DOMAIN.eq(domain.getId()))
									.fetch();
				}
			}

			if(result.isNotEmpty()){
				Customer customer = new Customer();
				Integer index = 0;
				if(result.size() > 1){
					while(index < result.size()-1 && result.get(index).value5() != 0){
						index++;
					}
				}
				
				customer.setId(result.get(index).getValue(REGISTRY.ID));
				Registry registry = new Registry()
						.setId(result.get(index).getValue(REGISTRY.ID))
						.setAlias(result.get(index).getValue(REGISTRY.ALIAS))
						.setDocument(result.get(index).getValue(REGISTRY.DOCUMENT))
						.setName(result.get(index).getValue(REGISTRY.NAME));
				customer.copy(registry);
				customer.setStatus(RegistryStatus.values()[result.get(index).value5()]);
			
				return customer;
			}
			return null;
		}finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public LinkedList<Customer> getCustomers(Domain domain, String login) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login);

			Result<Record4<Integer, String, String, String>> data = ctx.getDslContext().select(REGISTRY.ID, REGISTRY.ALIAS, REGISTRY.DOCUMENT, REGISTRY.NAME)
					.from(REGISTRY).join(CUSTOMER).on(REGISTRY.ID.eq(CUSTOMER.REGISTRY))
					.where(CUSTOMER.DOMAIN.eq(domain.getId()))
					.fetch();
			
	

			LinkedList<Customer> v = new LinkedList<Customer>();

			for (Record4<Integer, String,String,String> r : data) {
				Customer customer = new Customer();
				Registry registry = new Registry();
				registry.setId(r.value1());
				registry.setAlias(r.value2());
				registry.setDocument(r.value3());
				registry.setName(r.value4());
				customer.copy(registry);
				v.add(customer);
			}

			return v;

		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	
	
	public LinkedList<Seller> getSellers(Domain domain,  String login){
		return AON.getSellerList(domain.getName(), domain.getId(), login, f -> 
			f.getDomainProperty().eq(domain.getId())
			.and(f.getStatusProperty().eq(SellerStatus.ACTIVE.value())));
	}

	public LinkedList<Project> getProjectList(Domain domain, String login){
		return AON.getProjectList(domain.getName(), domain.getId(), login,
				filter -> filter.getDomainProperty().eq(domain.getId()));
	}
	
	public Project getProject(Domain domain,String str, Integer customer, String login){
		return AON.getProject(domain.getName(), domain.getId(), login,
				filter -> filter.getDomainProperty().eq(domain.getId())
				.and(filter.getRegistryProperty().eq(customer))
				.and(filter.getAliasProperty().eq(str).or(filter.getNameProperty().eq(str))));
	}
	
	public Integer insertProject(Domain domain, User user, String name, Integer customer){
		Project project = getProjectDefault()
				.setDomain(domain)
				.setName(name)
				.setRegistry(new Registry().setId(customer));
		return AON.insertProject(domain.getName(), domain.getId(), user.getLogin(), project);
	}
	
	public LinkedList<Workplace> getWorkplaceList(Domain domain, User user){
		Occam occam = new Occam()
			.setDomainName(domain.getName())
			.setDomain(domain.getId())
			.setUser(user.getLogin());
		return AON.getWorkplaces(occam, domain.getId())
			.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public LinkedList<InvoicingGroup> getInvoicingGroupList(Domain domain, User user){
		Occam occam = new Occam()
			.setDomainName(domain.getName())
			.setDomain(domain.getId())
			.setUser(user.getLogin());
		return AON.getInvoicingGroups(occam, domain.getId())
			.collect(Collectors.toCollection(LinkedList::new));
	}
	
	
	private Project getProjectDefault() {
		return new Project()
				.setActive(true)
				.setAlias("")
				.setCommercial(false)
				.setDate(new java.util.Date())
				.setDomain(new Domain().setId(0))
				.setName("")
				.setRegistry(new Registry())
				.setReservation(false)
				.setTas(false);
	}
}
