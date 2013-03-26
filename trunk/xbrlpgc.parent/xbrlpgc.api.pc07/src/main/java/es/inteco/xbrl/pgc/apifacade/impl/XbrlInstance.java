/**
 *
 * API XBRL-PGC2007 is a set of packages for the treatment of instances XBRL
 * (eXtensible Business Reporting Language) corresponding to the taxonomy PGC2007.
 * The General Plan of Accounting 2007 is the legal text that regulates the accounting of
 * the companies in Spain.
 *
 * This program is part of the API XBRL-PGC2007.
 *
 * Copyright (C) 2009  INTECO (Instituto Nacional de Tecnologías de la
 * Comunicación, S.A.)
 *
 * Authors: Members of Software Quality Department inside INTECO
 *
 * E-mail: difusioncalidad@inteco.es
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software Foundation; 
 * either version 3 of the License, or (at your opinion) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along
 * with this program. If not, see http://www.gnu.org/licenses/
 */


package es.inteco.xbrl.pgc.apifacade.impl;

import es.inteco.xbrl.pgc.apifacade.IXbrlInstance;



/**
 *
 * Clase usada para encapsular un objeto correspondiente a una instancia y un DTS XBRL.
 *
 *
 *<br><br>
 * <b>Proyecto</b>: API XBRL-PGC2007 - Grupo de utilidades y librerías en código abierto para facilitar 
 *                             la integración del formato XBRL en las herramientas software de gestión de  terceros
 *                             aislándose de la complejidad en el procesamiento del modelo de datos de las taxonomías.
 *                             Ayudando de esta forma a las empresas finales en la labor de realización de informes XBRL
 *                             y asegurar el éxito de implantación del nuevo Plan General de Contabilidad 2007 en formato XBRL
 *
 *
 *
 * @version 1.0, 14/01/2009
 * 
 * @author difusioncalidad@inteco.es
 *
 */


public class XbrlInstance implements IXbrlInstance
{

    private Object instance = null;
    private Object dts = null;
    private Object footNoteLink = null;
    
    
        
     /**
      * Constructor
     * @param instance
     * @param dts
     */
    XbrlInstance(Object instance, Object dts) {
	super();
	this.instance = instance;
	this.dts = dts;

    }
    
     
     /**
     * Constructor por defecto
     */
    XbrlInstance(){
	super();
    }
    
    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.apifacade.IXbrlInstance#getInstance()
     */
    public final Object getInstance()
    {
        return instance;
    }
    
    
    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.apifacade.IXbrlInstance#setInstance(java.lang.Object)
     */
    public final void setInstance(Object instance)
    {
        this.instance = instance;
    }
    
    
    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.apifacade.IXbrlInstance#getDts()
     */
    public final Object getDts()
    {
        return dts;
    }
    
    
    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.apifacade.IXbrlInstance#setDts(java.lang.Object)
     */
    public final void setDts(Object dts)
    {
        this.dts = dts;
    }
    
    
    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.apifacade.IXbrlInstance#getFootNoteLink()
     */
    public final Object getFootNoteLink()
    {
        return footNoteLink;
    }
    
    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.apifacade.IXbrlInstance#setFootNoteLink(java.lang.Object)
     */
    public final void setFootNoteLink(Object footNoteLink)
    {
        this.footNoteLink = footNoteLink;
    }
    
    
}
