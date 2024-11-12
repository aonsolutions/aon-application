package com.esferalia.aon.occam.api;

import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.ConsoleDomain;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainCompany;
import com.esferalia.aon.occam.api.model.DomainLinked;
import com.esferalia.aon.occam.api.model.DomainParams;
import com.esferalia.aon.occam.api.model.Filter.DomainFilter;
import com.esferalia.aon.occam.api.model.console.ConsoleTableField;
import com.esferalia.aon.occam.api.model.console.ConsoleTableRow;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.AonStatus;
import com.esferalia.aon.occam.impl.jooq.console.ConsoleParams;

public interface IConsole {
	
	public String[] getSchemaNames(AONContext ctx);
	public Stream<ConsoleDomain> getDomains(AONContext ctx, DomainParams params);
	public boolean deleteDomain(ConsoleParams params);
	public Domain changeActive(AONContext ctx, Integer domainId, boolean active);
	public Domain changeExpirationDate(AONContext ctx, Integer domainId, Date expireDate);
	public boolean switchRemoteAccess(AONContext ctx, Integer domainId);
	public Stream<User> availableUsers(AONContext ctx, Integer domainId);
	public String[] getAonTables();		
	public ConsoleTableRow getTableRow(AONContext ctx, ConsoleTableRow row);
	public ConsoleTableRow getTableRowMetadata(AONContext ctx, ConsoleTableRow row);
	public ConsoleTableRow update(AONContext ctx, ConsoleTableRow row, ConsoleTableField field);
	public Boolean delete(AONContext ctx, ConsoleTableRow row);
	public Stream<DomainCompany> getAllDomains(AONContext ctx);
	public Stream<DomainCompany> getDomains(AONContext ctx, DomainFilter filter);
	public Stream<DomainCompany> areDomainsSync(AONContext ctx, DomainFilter filter);
	public Stream<DomainCompany> getCustomerDomains(AONContext ctx, Integer customer);
	public List<Domain> updateDomainCustomer(AONContext ctx, List<DomainCompany> domainCompanies, Integer customer);
	public DomainCompany updateDomainStatus(AONContext ctx, DomainCompany domainCompany, AonStatus aonStatus);
	public Stream<DomainCompany> getDomainsByDocument(AONContext ctx, String customerDocument, Integer customerId);
	public DomainLinked saveDomainLink(AONContext ctx, DomainLinked domainLinked);
	public void deleteDomainLink(AONContext ctx, DomainLinked domainLinked);
}
