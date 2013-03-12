package es.inteco.xbrl.pgc.wsclient.validator;

public class XBRLWSValidatorProxy implements es.inteco.xbrl.pgc.wsclient.validator.XBRLWSValidator {
  private String _endpoint = null;
  private es.inteco.xbrl.pgc.wsclient.validator.XBRLWSValidator xBRLWSValidator = null;
  
  public XBRLWSValidatorProxy() {
    _initXBRLWSValidatorProxy();
  }
  
  public XBRLWSValidatorProxy(String endpoint) {
    _endpoint = endpoint;
    _initXBRLWSValidatorProxy();
  }
  
  private void _initXBRLWSValidatorProxy() {
    try {
      xBRLWSValidator = (new es.inteco.xbrl.pgc.wsclient.validator.XBRLWSValidatorServiceLocator()).getXBRLWSValidator();
      if (xBRLWSValidator != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)xBRLWSValidator)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)xBRLWSValidator)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (xBRLWSValidator != null)
      ((javax.xml.rpc.Stub)xBRLWSValidator)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public es.inteco.xbrl.pgc.wsclient.validator.XBRLWSValidator getXBRLWSValidator() {
    if (xBRLWSValidator == null)
      _initXBRLWSValidatorProxy();
    return xBRLWSValidator;
  }
  
  public es.inteco.xbrl.pgc.wsclient.validator.ValidateResult validate(byte[] inputDocument) throws java.rmi.RemoteException{
    if (xBRLWSValidator == null)
      _initXBRLWSValidatorProxy();
    return xBRLWSValidator.validate(inputDocument);
  }
  
  
}