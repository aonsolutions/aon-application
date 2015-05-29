
package com.esferalia.aon.gwt.fiscal.server.normalizedMemory.xml;

import javax.xml.bind.annotation.XmlRegistry;


@XmlRegistry
public class ObjectFactory {


    public ObjectFactory() {
    }

    public Esquema createEsquema() {
        return new Esquema();
    }
    public Cabecera createCabecera() {
    	return new Cabecera();
    }
    public Claves createClaves() {
    	return new Claves();
    }
    public Clave createClave() {
    	return new Clave();
    }
    
}
