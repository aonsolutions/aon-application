/**
 * XBRLWSValidatorService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package es.inteco.xbrl.pgc.wsclient.validator;

public interface XBRLWSValidatorService extends javax.xml.rpc.Service {
    public java.lang.String getXBRLWSValidatorAddress();

    public es.inteco.xbrl.pgc.wsclient.validator.XBRLWSValidator getXBRLWSValidator() throws javax.xml.rpc.ServiceException;

    public es.inteco.xbrl.pgc.wsclient.validator.XBRLWSValidator getXBRLWSValidator(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
