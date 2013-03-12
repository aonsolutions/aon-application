/**
 * PGCWSViewer.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package es.inteco.xbrl.pgc.wsclient.viewer;

public interface PGCWSViewer extends java.rmi.Remote {
    public es.inteco.xbrl.pgc.wsclient.viewer.ViewerResult view(byte[] inputDocument, java.lang.String module) throws java.rmi.RemoteException;
    public es.inteco.xbrl.pgc.wsclient.viewer.ViewerResult viewXML(byte[] inputDocument, java.lang.String module) throws java.rmi.RemoteException;
}
