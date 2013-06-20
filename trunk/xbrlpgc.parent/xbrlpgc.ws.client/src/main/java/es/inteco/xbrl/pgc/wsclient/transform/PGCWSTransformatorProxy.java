package es.inteco.xbrl.pgc.wsclient.transform;

public class PGCWSTransformatorProxy implements es.inteco.xbrl.pgc.wsclient.transform.PGCWSTransformator {
  private String _endpoint = null;
  private es.inteco.xbrl.pgc.wsclient.transform.PGCWSTransformator pGCWSTransformator = null;
  
  public PGCWSTransformatorProxy() {
    _initPGCWSTransformatorProxy();
  }
  
  public PGCWSTransformatorProxy(String endpoint) {
    _endpoint = endpoint;
    _initPGCWSTransformatorProxy();
  }
  
  private void _initPGCWSTransformatorProxy() {
    try {
      pGCWSTransformator = (new es.inteco.xbrl.pgc.wsclient.transform.PGCWSTransformatorServiceLocator()).getPGCWSTransformator();
      if (pGCWSTransformator != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)pGCWSTransformator)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)pGCWSTransformator)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (pGCWSTransformator != null)
      ((javax.xml.rpc.Stub)pGCWSTransformator)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public es.inteco.xbrl.pgc.wsclient.transform.PGCWSTransformator getPGCWSTransformator() {
    if (pGCWSTransformator == null)
      _initPGCWSTransformatorProxy();
    return pGCWSTransformator;
  }
  
  public es.inteco.xbrl.pgc.wsclient.transform.TransformResult transformToXml(byte[] inputDocument, boolean validate) throws java.rmi.RemoteException{
    if (pGCWSTransformator == null)
      _initPGCWSTransformatorProxy();
    return pGCWSTransformator.transformToXml(inputDocument, validate);
  }
  
  public es.inteco.xbrl.pgc.wsclient.transform.TransformResult transformToXbrl(byte[] inputDocument, boolean validate) throws java.rmi.RemoteException{
    if (pGCWSTransformator == null)
      _initPGCWSTransformatorProxy();
    return pGCWSTransformator.transformToXbrl(inputDocument, validate);
  }
  
  
}