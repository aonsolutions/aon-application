package com.esferalia.aon.dex.service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

@WebService(name="AonDataLoad", targetNamespace = "http://ws.apache.org/axis2")
public interface IDataLoad {

    @WebMethod(operationName = "GetDocument")
    @WebResult(name = "GetDocumentResult")
    public String getDocument(
            @WebParam(name = "Document", targetNamespace = "http://ws.apache.org/axis2")
            String document);

}
