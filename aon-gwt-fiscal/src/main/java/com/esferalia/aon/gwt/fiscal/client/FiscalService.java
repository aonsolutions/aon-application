package com.esferalia.aon.gwt.fiscal.client;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.gwt.fiscal.shared.Memory;
import com.esferalia.aon.occam.api.model.FiscalParameters;
import com.esferalia.aon.occam.api.model.fiscal.Activity;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.Mod200;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("Fiscal")
public interface FiscalService extends RemoteService {
	// ---------------------------------- COMMON
	Double mathExpression(String expression) throws AonCoreException;

	// ------------------------------------------------------- FISCAL PARAMETERS
	FiscalParameters getFiscalParameters(String domainName, int domain) throws AonCoreException;

	// -------------------------------------------------------------- ACTIVITIES
	LinkedList<Activity> getActivities(int activityGroup) throws AonCoreException;

	// ---------------------------------------------------------------MODELO 200
	LinkedList<Mod200> getMod200s(String currentDomainName, int currentDomain) throws AonCoreException;

	// --------------------------------------------------------------- NORMALIZED
	// MEMORY
	Memory readMemory(Memory memory) throws AonCoreException;

	Memory saveMemory(Memory memory) throws AonCoreException;

	void deleteMemory(Memory memory) throws AonCoreException;

	// --------------------------------------------------------------- GWT API INFO

	AonData getAonData(String domainName, Integer domainId, String user);

	AonData getAonDataToken(String domainName, Integer domainId, String token);

	Integer presentationFile(String domainName, Integer domainId, String user, FiscalModelType type, Integer id);

	void markAsFinished(String domainName, Integer domainId, String user, IFiscalModel model);

}
