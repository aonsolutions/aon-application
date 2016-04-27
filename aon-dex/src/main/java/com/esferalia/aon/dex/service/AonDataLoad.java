package com.esferalia.aon.dex.service;

import javax.jws.WebService;

import com.esferalia.aon.dex.process.DataLoadManager;

@WebService(endpointInterface="com.esferalia.aon.dex.service.IDataLoad", wsdlLocation="/WEB-INF/wsdl/DataLoadService.wsdl", targetNamespace = "http://ws.apache.org/axis2")
public class AonDataLoad implements IDataLoad {

	@Override
	public String getDocument(String document) {
		DataLoadManager dataLoadManager = new DataLoadManager();
System.out.println("WS DEX REQUEST: " + document);
		String documentResult = dataLoadManager.processDocument(document);
System.out.println("WS DEX RESPONSE: " + documentResult);
		return documentResult;
		//return dataLoadManager.processDocument(document);
	}

}
