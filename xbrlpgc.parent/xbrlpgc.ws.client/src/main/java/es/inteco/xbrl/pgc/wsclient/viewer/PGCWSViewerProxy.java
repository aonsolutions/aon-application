package es.inteco.xbrl.pgc.wsclient.viewer;

public class PGCWSViewerProxy implements es.inteco.xbrl.pgc.wsclient.viewer.PGCWSViewer {
  private String _endpoint = null;
  private es.inteco.xbrl.pgc.wsclient.viewer.PGCWSViewer pGCWSViewer = null;
  
  public PGCWSViewerProxy() {
    _initPGCWSViewerProxy();
  }
  
  public PGCWSViewerProxy(String endpoint) {
    _endpoint = endpoint;
    _initPGCWSViewerProxy();
  }
  
  private void _initPGCWSViewerProxy() {
    try {
      pGCWSViewer = (new es.inteco.xbrl.pgc.wsclient.viewer.PGCWSViewerServiceLocator()).getPGCWSViewer();
      if (pGCWSViewer != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)pGCWSViewer)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)pGCWSViewer)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (pGCWSViewer != null)
      ((javax.xml.rpc.Stub)pGCWSViewer)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public es.inteco.xbrl.pgc.wsclient.viewer.PGCWSViewer getPGCWSViewer() {
    if (pGCWSViewer == null)
      _initPGCWSViewerProxy();
    return pGCWSViewer;
  }
  
  public es.inteco.xbrl.pgc.wsclient.viewer.ViewerResult view(byte[] inputDocument, java.lang.String module) throws java.rmi.RemoteException{
    if (pGCWSViewer == null)
      _initPGCWSViewerProxy();
    return pGCWSViewer.view(inputDocument, module);
  }
  
  public es.inteco.xbrl.pgc.wsclient.viewer.ViewerResult viewXML(byte[] inputDocument, java.lang.String module) throws java.rmi.RemoteException{
    if (pGCWSViewer == null)
      _initPGCWSViewerProxy();
    return pGCWSViewer.viewXML(inputDocument, module);
  }
  
  
}