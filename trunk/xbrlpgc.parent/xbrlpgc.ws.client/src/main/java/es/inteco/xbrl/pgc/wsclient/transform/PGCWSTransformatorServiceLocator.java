/**
 * PGCWSTransformatorServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package es.inteco.xbrl.pgc.wsclient.transform;


public class PGCWSTransformatorServiceLocator extends org.apache.axis.client.Service implements es.inteco.xbrl.pgc.wsclient.transform.PGCWSTransformatorService {

    public PGCWSTransformatorServiceLocator() {
    }


    public PGCWSTransformatorServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public PGCWSTransformatorServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for PGCWSTransformator
    private java.lang.String PGCWSTransformator_address = ServicesConfig.getString("PGCWSTransformatorServiceLocator.address"); //$NON-NLS-1$

    public java.lang.String getPGCWSTransformatorAddress() {
        return PGCWSTransformator_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String PGCWSTransformatorWSDDServiceName = "PGCWSTransformator"; //$NON-NLS-1$

    public java.lang.String getPGCWSTransformatorWSDDServiceName() {
        return PGCWSTransformatorWSDDServiceName;
    }

    public void setPGCWSTransformatorWSDDServiceName(java.lang.String name) {
        PGCWSTransformatorWSDDServiceName = name;
    }

    public es.inteco.xbrl.pgc.wsclient.transform.PGCWSTransformator getPGCWSTransformator() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(PGCWSTransformator_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getPGCWSTransformator(endpoint);
    }

    public es.inteco.xbrl.pgc.wsclient.transform.PGCWSTransformator getPGCWSTransformator(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            es.inteco.xbrl.pgc.wsclient.transform.PGCWSTransformatorSoapBindingStub _stub = new es.inteco.xbrl.pgc.wsclient.transform.PGCWSTransformatorSoapBindingStub(portAddress, this);
            _stub.setPortName(getPGCWSTransformatorWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setPGCWSTransformatorEndpointAddress(java.lang.String address) {
        PGCWSTransformator_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (es.inteco.xbrl.pgc.wsclient.transform.PGCWSTransformator.class.isAssignableFrom(serviceEndpointInterface)) {
                es.inteco.xbrl.pgc.wsclient.transform.PGCWSTransformatorSoapBindingStub _stub = new es.inteco.xbrl.pgc.wsclient.transform.PGCWSTransformatorSoapBindingStub(new java.net.URL(PGCWSTransformator_address), this);
                _stub.setPortName(getPGCWSTransformatorWSDDServiceName());
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
        if ("PGCWSTransformator".equals(inputPortName)) { //$NON-NLS-1$
            return getPGCWSTransformator();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.inteco.es/xbrl/pgc07/transform", "PGCWSTransformatorService"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://www.inteco.es/xbrl/pgc07/transform", "PGCWSTransformator")); //$NON-NLS-1$ //$NON-NLS-2$
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("PGCWSTransformator".equals(portName)) { //$NON-NLS-1$
            setPGCWSTransformatorEndpointAddress(address);
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
