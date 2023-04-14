package com.esferalia.aon.occam.impl.jooq;

import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import org.jooq.Named;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.IConsole;
import com.esferalia.aon.occam.api.model.ConsoleDomain;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainCompany;
import com.esferalia.aon.occam.api.model.DomainParams;
import com.esferalia.aon.occam.api.model.console.ConsoleTableField;
import com.esferalia.aon.occam.api.model.console.ConsoleTableRow;
import com.esferalia.aon.occam.impl.jooq.console.ConsoleDeleteDomain;
import com.esferalia.aon.occam.impl.jooq.console.ConsoleParams;
import com.esferalia.aon.occam.impl.jooq.dao.DomainCustomerDAO;
import com.esferalia.aon.occam.impl.jooq.dao.console.ConsoleDAO;

public class ConsoleImpl implements IConsole {

	@Override
	public String[] getSchemaNames(AONContext ctx) {
		return ConsoleDAO.getAONSchemas(ctx)
			.map( Named::getName )
			.toArray(i -> new String[i]);
	}
	
	@Override
	public Stream<ConsoleDomain> getDomains(CloseableAONContext ctx, DomainParams params ) {
		return ConsoleDAO.getDomains(ctx, params);
	}
	
	@Override
	public boolean deleteDomain(ConsoleParams params) {
		return ConsoleDeleteDomain.delete(params);
	}
	
	@Override
	public Domain changeActive(CloseableAONContext ctx, Integer domainId, boolean active) {
		return ConsoleDAO.changeActive(ctx, domainId, active);
	}
	
	@Override
	public Domain changeExpirationDate(CloseableAONContext ctx, Integer domainId, Date expireDate) {
		return ConsoleDAO.changeExpirationDate(ctx, domainId, expireDate);
	}
	
	@Override
	public String remoteAccess(CloseableAONContext ctx, Integer domainId) {
		return ConsoleDAO.remoteAccess(ctx, domainId);
	}

	@Override
	public ConsoleTableRow getTableRow(CloseableAONContext ctx, ConsoleTableRow row) {
		return ConsoleDAO.getTableRow(ctx, row);
	}
	
	@Override
	public ConsoleTableRow getTableRowMetadata(CloseableAONContext ctx, ConsoleTableRow row) {
		return ConsoleDAO.getTableRowMetadata(ctx, row);
	}

	@Override
	public String[] getAonTables() {
		return ConsoleDAO.getAonTables();
	}
	
	@Override
	public ConsoleTableRow update(CloseableAONContext ctx, ConsoleTableRow row, ConsoleTableField field) {
		return ConsoleDAO.update(ctx, row, field);
	}
	
	@Override
	public Boolean delete(CloseableAONContext ctx, ConsoleTableRow row) {
		return ConsoleDAO.delete(ctx, row);
	}

	@Override
	public Stream<DomainCompany> getAllDomains(AONContext ctx) {
		return DomainCustomerDAO.getAllDomains(ctx);
	}

	@Override
	public Stream<DomainCompany> getCustomerDomains(AONContext ctx, Integer customer) {
		return DomainCustomerDAO.getCustomerDomains(ctx, customer);
	}

	@Override
	public List<Domain> updateDomainCustomer(AONContext ctx, List<DomainCompany> domainCompanies, Customer customer) {
		return DomainCustomerDAO.updateDomains(ctx, domainCompanies, customer);
	}
}
