package com.esferalia.aon.dex.client;

public class AonDataLoadProxy implements com.esferalia.aon.dex.client.AonDataLoad {
  private String _endpoint = null;
  private com.esferalia.aon.dex.client.AonDataLoad aonDataLoad = null;
  
  public AonDataLoadProxy() {
    _initAonDataLoadProxy();
  }
  
  public AonDataLoadProxy(String endpoint) {
    _endpoint = endpoint;
    _initAonDataLoadProxy();
  }
  
  private void _initAonDataLoadProxy() {
    try {
      aonDataLoad = (new com.esferalia.aon.dex.client.AonDataLoadServiceLocator()).getAonDataLoadPort();
      if (aonDataLoad != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)aonDataLoad)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)aonDataLoad)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (aonDataLoad != null)
      ((javax.xml.rpc.Stub)aonDataLoad)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public com.esferalia.aon.dex.client.AonDataLoad getAonDataLoad() {
    if (aonDataLoad == null)
      _initAonDataLoadProxy();
    return aonDataLoad;
  }
  
  public java.lang.String getDocument(java.lang.String document) throws java.rmi.RemoteException{
    if (aonDataLoad == null)
      _initAonDataLoadProxy();
    return aonDataLoad.getDocument(document);
  }
  
  
}