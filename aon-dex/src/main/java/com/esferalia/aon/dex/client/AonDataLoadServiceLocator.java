/**
 * AonDataLoadServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.esferalia.aon.dex.client;

public class AonDataLoadServiceLocator extends org.apache.axis.client.Service implements com.esferalia.aon.dex.client.AonDataLoadService {

    public AonDataLoadServiceLocator() {
    }


    public AonDataLoadServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public AonDataLoadServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for AonDataLoadPort
    private java.lang.String AonDataLoadPort_address = "http://localhost:80/aon-ws-dex/DataLoadService";

    public java.lang.String getAonDataLoadPortAddress() {
        return AonDataLoadPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String AonDataLoadPortWSDDServiceName = "AonDataLoadPort";

    public java.lang.String getAonDataLoadPortWSDDServiceName() {
        return AonDataLoadPortWSDDServiceName;
    }

    public void setAonDataLoadPortWSDDServiceName(java.lang.String name) {
        AonDataLoadPortWSDDServiceName = name;
    }

    public com.esferalia.aon.dex.client.AonDataLoad getAonDataLoadPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(AonDataLoadPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getAonDataLoadPort(endpoint);
    }

    public com.esferalia.aon.dex.client.AonDataLoad getAonDataLoadPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.esferalia.aon.dex.client.AonDataLoadStub _stub = new com.esferalia.aon.dex.client.AonDataLoadStub(portAddress, this);
            _stub.setPortName(getAonDataLoadPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setAonDataLoadPortEndpointAddress(java.lang.String address) {
        AonDataLoadPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.esferalia.aon.dex.client.AonDataLoad.class.isAssignableFrom(serviceEndpointInterface)) {
                com.esferalia.aon.dex.client.AonDataLoadStub _stub = new com.esferalia.aon.dex.client.AonDataLoadStub(new java.net.URL(AonDataLoadPort_address), this);
                _stub.setPortName(getAonDataLoadPortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
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
        if ("AonDataLoadPort".equals(inputPortName)) {
            return getAonDataLoadPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://ws.apache.org/axis2", "AonDataLoadService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://ws.apache.org/axis2", "AonDataLoadPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("AonDataLoadPort".equals(portName)) {
            setAonDataLoadPortEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
