/**
 * PGCWSViewerService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package es.inteco.xbrl.pgc.wsclient.viewer;

public interface PGCWSViewerService extends javax.xml.rpc.Service {
    public java.lang.String getPGCWSViewerAddress();

    public es.inteco.xbrl.pgc.wsclient.viewer.PGCWSViewer getPGCWSViewer() throws javax.xml.rpc.ServiceException;

    public es.inteco.xbrl.pgc.wsclient.viewer.PGCWSViewer getPGCWSViewer(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
