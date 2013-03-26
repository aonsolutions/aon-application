package es.inteco.xbrl.pgc.wsclient.validator;

public class XSDWSValidatorProxy implements es.inteco.xbrl.pgc.wsclient.validator.XSDWSValidator {
  private String _endpoint = null;
  private es.inteco.xbrl.pgc.wsclient.validator.XSDWSValidator xSDWSValidator = null;
  
  public XSDWSValidatorProxy() {
    _initXSDWSValidatorProxy();
  }
  
  public XSDWSValidatorProxy(String endpoint) {
    _endpoint = endpoint;
    _initXSDWSValidatorProxy();
  }
  
  private void _initXSDWSValidatorProxy() {
    try {
      xSDWSValidator = (new es.inteco.xbrl.pgc.wsclient.validator.XSDWSValidatorServiceLocator()).getXSDWSValidator();
      if (xSDWSValidator != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)xSDWSValidator)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)xSDWSValidator)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (xSDWSValidator != null)
      ((javax.xml.rpc.Stub)xSDWSValidator)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public es.inteco.xbrl.pgc.wsclient.validator.XSDWSValidator getXSDWSValidator() {
    if (xSDWSValidator == null)
      _initXSDWSValidatorProxy();
    return xSDWSValidator;
  }
  
  public es.inteco.xbrl.pgc.wsclient.validator.ValidateResult validate(byte[] inputDocument) throws java.rmi.RemoteException{
    if (xSDWSValidator == null)
      _initXSDWSValidatorProxy();
    return xSDWSValidator.validate(inputDocument);
  }
  
  
}