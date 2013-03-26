/**
 * XSDWSValidatorServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package es.inteco.xbrl.pgc.wsclient.validator;

import es.inteco.xbrl.pgc.wsclient.transform.ServicesConfig;



public class XSDWSValidatorServiceLocator extends org.apache.axis.client.Service implements es.inteco.xbrl.pgc.wsclient.validator.XSDWSValidatorService {

    public XSDWSValidatorServiceLocator() {
    }


    public XSDWSValidatorServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public XSDWSValidatorServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for XSDWSValidator
    private java.lang.String XSDWSValidator_address = ServicesConfig.getString("XSDWSValidatorServiceLocator.address");//"http://localhost:8080/PGCWSProject/services/XSDWSValidator";

    public java.lang.String getXSDWSValidatorAddress() {
        return XSDWSValidator_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String XSDWSValidatorWSDDServiceName = "XSDWSValidator"; //$NON-NLS-1$

    public java.lang.String getXSDWSValidatorWSDDServiceName() {
        return XSDWSValidatorWSDDServiceName;
    }

    public void setXSDWSValidatorWSDDServiceName(java.lang.String name) {
        XSDWSValidatorWSDDServiceName = name;
    }

    public es.inteco.xbrl.pgc.wsclient.validator.XSDWSValidator getXSDWSValidator() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(XSDWSValidator_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getXSDWSValidator(endpoint);
    }

    public es.inteco.xbrl.pgc.wsclient.validator.XSDWSValidator getXSDWSValidator(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            es.inteco.xbrl.pgc.wsclient.validator.XSDWSValidatorSoapBindingStub _stub = new es.inteco.xbrl.pgc.wsclient.validator.XSDWSValidatorSoapBindingStub(portAddress, this);
            _stub.setPortName(getXSDWSValidatorWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setXSDWSValidatorEndpointAddress(java.lang.String address) {
        XSDWSValidator_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (es.inteco.xbrl.pgc.wsclient.validator.XSDWSValidator.class.isAssignableFrom(serviceEndpointInterface)) {
                es.inteco.xbrl.pgc.wsclient.validator.XSDWSValidatorSoapBindingStub _stub = new es.inteco.xbrl.pgc.wsclient.validator.XSDWSValidatorSoapBindingStub(new java.net.URL(XSDWSValidator_address), this);
                _stub.setPortName(getXSDWSValidatorWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName())); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("XSDWSValidator".equals(inputPortName)) { //$NON-NLS-1$
            return getXSDWSValidator();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.inteco.es/xbrl/pgc07/validator", "XSDWSValidatorService"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://www.inteco.es/xbrl/pgc07/validator", "XSDWSValidator")); //$NON-NLS-1$ //$NON-NLS-2$
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("XSDWSValidator".equals(portName)) { //$NON-NLS-1$
            setXSDWSValidatorEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName); //$NON-NLS-1$
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
