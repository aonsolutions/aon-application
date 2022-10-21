package com.esferalia.aon.gwt.fiscal.client.console;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.ConsoleDomain;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainParams;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.console.ConsoleTableField;
import com.esferalia.aon.occam.api.model.console.ConsoleTableRow;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/Console")
public interface ConsoleService extends RemoteService {

	String[] getSchemas(Occam occam) throws AonCoreException;
	LinkedList<ConsoleDomain> getDomains(DomainParams params) throws AonCoreException;
	Boolean deleteDomain(DomainParams params, Integer domainId) throws AonCoreException;
	Domain changeActive(DomainParams params, Integer domainId, boolean active) throws AonCoreException;
	Domain changeExpirationDate(DomainParams params, Integer domainId, Date expireDate) throws AonCoreException;
	String remoteAccess(DomainParams params, Integer domainId) throws AonCoreException;
	String[] getAonTables() throws AonCoreException;
	ConsoleTableRow getTableRow(ConsoleTableRow row) throws AonCoreException;
	ConsoleTableRow getTableRowMetadata(ConsoleTableRow row) throws AonCoreException;
	ConsoleTableRow update(ConsoleTableRow row, ConsoleTableField field) throws AonCoreException;
	Boolean delete(ConsoleTableRow row) throws AonCoreException;
}
