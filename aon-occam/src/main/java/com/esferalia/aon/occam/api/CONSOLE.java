package com.esferalia.aon.occam.api;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.exception.DataAccessException;

import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.ConsoleDomain;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainCompany;
import com.esferalia.aon.occam.api.model.DomainLinked;
import com.esferalia.aon.occam.api.model.DomainParams;
import com.esferalia.aon.occam.api.model.Filter.DomainFilter;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.console.ConsoleTableField;
import com.esferalia.aon.occam.api.model.console.ConsoleTableRow;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.AonStatus;
import com.esferalia.aon.occam.impl.jooq.ConsoleImpl;
import com.esferalia.aon.occam.impl.jooq.console.ConsoleConnectionParams;
import com.esferalia.aon.occam.impl.jooq.console.ConsoleParams;

public class CONSOLE {

	private CONSOLE() {
		
	}
	
	private static IConsole getConsole() {
		return new ConsoleImpl();
	}

	public static String[] getSchemaNames(Occam occam) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getConsole().getSchemaNames(ctx);
		}
	}

	public static Stream<ConsoleDomain> getDomains(DomainParams params) {
		try (CloseableAONContext ctx = AONContext.getAONContext(params.getSchema())) {
			return getConsole().getDomains(ctx, params);
		}
	}
	
	public static Stream<DomainCompany> getAllDomains() {
		List<DomainCompany> list = new LinkedList<>();
		List<String> schemas = AONContext.getSchemas();
		for(String schema: schemas) {
			try (CloseableAONContext ctx = AONContext.getAONContext(schema)) {
					List<DomainCompany> domains = getConsole().getAllDomains(ctx).collect(Collectors.toList());
					domains.forEach(domain -> {
						domain.setSchema(schema);
						list.add(domain);						
					});
			} catch (DataAccessException e) {
				e.printStackTrace();
			}
		}
		return list.stream();

	}
	
	public static Stream<DomainCompany> getDomains(DomainFilter filter) {
		List<DomainCompany> list = new LinkedList<>();
		List<String> schemas = AONContext.getSchemas();
		for(String schema: schemas) {
			try (CloseableAONContext ctx = AONContext.getAONContext(schema)) {
				List<DomainCompany> domains = getConsole().getDomains(ctx, filter).collect(Collectors.toList());
				domains.forEach(domain -> {
					domain.setSchema(schema);
					list.add(domain);						
				});
			} catch (DataAccessException e) {
				e.printStackTrace();
			}
		}
		return list.stream();
		
	}
	
	public static Stream<DomainCompany> areDomainsSync(DomainFilter filter) {
		List<DomainCompany> list = new LinkedList<>();
		List<String> schemas = AONContext.getSchemas();
		for(String schema: schemas) {
			try (CloseableAONContext ctx = AONContext.getAONContext(schema)) {
				List<DomainCompany> domains = getConsole().areDomainsSync(ctx, filter).collect(Collectors.toList());
				domains.forEach(domain -> {
					domain.setSchema(schema);
					list.add(domain);						
				});
			} catch (DataAccessException e) {
				e.printStackTrace();
			}
		}
		return list.stream();
		
	}
	
	public static Stream<DomainCompany> getCustomerDomains(Integer customer) {
		List<DomainCompany> list = new LinkedList<>();
		List<String> schemas = AONContext.getSchemas();
		for(String schema: schemas) {
			try (CloseableAONContext ctx = AONContext.getAONContext(schema)) {
				List<DomainCompany> domains = getConsole().getCustomerDomains(ctx, customer).collect(Collectors.toList());
				domains.forEach(domain -> {
					domain.setSchema(schema);
					list.add(domain);						
				});
			} catch (DataAccessException e) {
				e.printStackTrace();
			}
		}
		return list.stream();

	}

	public static Stream<DomainCompany> getDomainsByDocument(String customerDocument, Integer customerId) {
		List<DomainCompany> list = new LinkedList<>();
		List<String> schemas = AONContext.getSchemas();
		for(String schema: schemas) {
			try (CloseableAONContext ctx = AONContext.getAONContext(schema)) {
				List<DomainCompany> domains = getConsole().getDomainsByDocument(ctx, customerDocument, customerId).collect(Collectors.toList());
				list.addAll(domains);
			} catch (DataAccessException e) {
				e.printStackTrace();
			}
		}
		return list.stream();
		
	}
	
	public static List<Domain> updateDomainCustomerData(List<DomainCompany> domainCompanies, Integer customer) {
		List<Domain> updatedDomains = new LinkedList<>(); 
		if (domainCompanies != null) {
			for (DomainCompany domainCompany : domainCompanies) {
				Domain domain = domainCompany.getDomain();
				if (domain != null) {
					try (CloseableAONContext ctx = AONContext.getAONContext(domain, "")) {
						updatedDomains.addAll(getConsole().updateDomainCustomer(ctx, Arrays.asList(domainCompany), customer));
					} catch (DataAccessException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return updatedDomains;
	}
	
	public DomainCompany updateDomainStatus(DomainCompany domainCompany, AonStatus aonStatus) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainCompany.getDomain(), "")) {
			if (domainCompany != null && domainCompany.getDomain() != null) {
				getConsole().updateDomainStatus(ctx, domainCompany, aonStatus);
			}
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
		return domainCompany;
	}

	public static boolean deleteDomain(DomainParams params, Integer domainId) {
		try (CloseableAONContext ctx = AONContext.getAONContext(params.getSchema())) {
			ConsoleParams consoleParams = new ConsoleParams(); 
			ConsoleConnectionParams conParams = new ConsoleConnectionParams()
				.setAONContext(ctx)
				.setSchemaName(params.getSchema())
				.setDomain(new Domain().setId(domainId));
			consoleParams.setFromConnection(conParams);
			return getConsole().deleteDomain(consoleParams);
		}
	}

	public static Domain changeActive(DomainParams params, Integer domainId, boolean active) {
		try (CloseableAONContext ctx = AONContext.getAONContext(params.getSchema())) {
			return getConsole().changeActive(ctx,domainId, active);
		}
	}

	public static Domain changeExpirationDate(DomainParams params, Integer domainId, Date expireDate) {
		try (CloseableAONContext ctx = AONContext.getAONContext(params.getSchema())) {
			return getConsole().changeExpirationDate(ctx,domainId,expireDate);
		}
	}

	public static LinkedList<User> availableUsers(Occam occam, Integer domainId) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getConsole().availableUsers(ctx,domainId)
					.collect(Collectors.toCollection( LinkedList::new));
		}
	}
	
	public static boolean switchRemoteAccess(DomainParams params, Integer domainId) {
		try (CloseableAONContext ctx = AONContext.getAONContext(params.getSchema())) {
			return getConsole().switchRemoteAccess(ctx,domainId);
		}
	}
	
	public static boolean switchRemoteAccess(Occam occam, Integer domainId) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			return getConsole().switchRemoteAccess(ctx,domainId);
		}
	}

	public static ConsoleTableRow getTableRow(ConsoleTableRow row) {
		try (CloseableAONContext ctx = AONContext.getAONContext(row.getSchema())) {
			return getConsole().getTableRow(ctx,row);
		}
	}

	public static ConsoleTableRow getTableRowMetadata(ConsoleTableRow row) {
		try (CloseableAONContext ctx = AONContext.getAONContext(row.getSchema())) {
			return getConsole().getTableRowMetadata(ctx,row);
		}
	}

	public static String[] getAonTables() {
		return getConsole().getAonTables();
	}
	
	public static ConsoleTableRow update(ConsoleTableRow row, ConsoleTableField field) {
		try (CloseableAONContext ctx = AONContext.getAONContext(row.getSchema())) {
			return getConsole().update(ctx,row,field);
		}
	}
	
	public static Boolean delete(ConsoleTableRow row) {
		try (CloseableAONContext ctx = AONContext.getAONContext(row.getSchema())) {
			return getConsole().delete(ctx,row);
		}
	}
	
	public static DomainLinked saveDomainLink(Domain domain, User user, DomainLinked domainLinked) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)) {
			return getConsole().saveDomainLink(ctx, domainLinked);
		}
	}
	
	public static void deleteDomainLink(Domain domain, User user, DomainLinked domainLinked) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)) {
			getConsole().deleteDomainLink(ctx, domainLinked);
		}
	}

}
