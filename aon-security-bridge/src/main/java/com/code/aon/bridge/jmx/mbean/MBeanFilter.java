package com.code.aon.bridge.jmx.mbean;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Filtro responsable de aceptar que <code>MBean</code> son válidos para la aplicación.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 05-jul-2004
 * @since 1.0
 *  
 */
public class MBeanFilter {

	public static final String TOMCAT_MANAGER = "Catalina:type=Manager";
    /** 
     * Lista de los diferentes motores utilizados a la hora de desplegar una aplicación en el  
     * servidor de aplicaciones o contenedor de servlets
     */
    List<String> deployers;

    /** Coleccíon de <code>MBean</code> registrados en la aplicación. */
    Collection mbeans;

    /**
     * Permitir a la aplicacion que elija que aplicaciones desplegar EAR, WAR, JAR, ...
     * 
     * @return List
     */
    public static final List<String> createDeployers() {
        List<String> deployers = new LinkedList<String>();
        // TOMCAT
        deployers.add(TOMCAT_MANAGER);
        // JBOSS
        deployers.add("org.jboss.deployment.EARDeployer");
        deployers.add("MBeanProxyExt[jboss.web:service=WebServer]");
        return deployers;
    }

    /**
     * Constructor de Filtros para MBean. Construye un filtro a partir de los 
     * motores de despliegue (EAR, WAR, ...) definidos en el 
     * servidor de aplicaciones o contenedor de servlets, y los MBean ya registrados.
     * 
     * @param deployers List
     * @param mbeans Collection
     */
    public MBeanFilter(List<String> deployers, Collection mbeans) {
        this.deployers = deployers;
        this.mbeans = mbeans;
    }

    /**
     * Devuelve verdadero en caso de que el <code>MBean</code> dado cumpla los requisitos establecidos
     * en el filtro, falso en cualquier otro caso.
     * 
     * @param info IMBeanInfo
     * @return boolean
     */
    public boolean accept(IMBeanInfo info) {
        return deployers.contains(info.getDeployer()) && !mbeans.contains(info.getName());
    }
}