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

import java.util.Date;

import es.inteco.xbrl.pgc.apifacade.IXbrlContext;



/**
 *
 * Clase usada para encapsular un objeto correspondiente a un contexto XBRL.
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
 * @version 1.0, 14/01/2009
 * @author difusioncalidad@inteco.es
 *
 */


public class XbrlContext implements IXbrlContext 
{

    private String id =null;
    private String entityIdentifierSchema =null;
    private String entityIdentifierValue =null;
    private Date periodStartDate =null;
    private Date periodEndDate =null;
    private Date periodInstant =null;
    private boolean scenarioIsDimensional =false;
    private Object contextObject = null;
    private Object periodObject = null;
    private String dimension  = null;
    private String member = null;
    
    /**
     * Constructor
     */
    XbrlContext() {
	super();

    }


    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.apifacade.IXbrlContext#getId()
     */
    public final String getId()
    {
        return id;
    }


    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.apifacade.IXbrlContext#setId(java.lang.String)
     */
    public final void setId(String id)
    {
        this.id = id;
    }


    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.apifacade.IXbrlContext#getEntityIdentifierSchema()
     */
    public final String getEntityIdentifierSchema()
    {
        return entityIdentifierSchema;
    }


    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.apifacade.IXbrlContext#setEntityIdentifierSchema(java.lang.String)
     */
    public final void setEntityIdentifierSchema(String entityIdentifierSchema)
    {
        this.entityIdentifierSchema = entityIdentifierSchema;
    }


    public final String getEntityIdentifierValue()
    {
        return entityIdentifierValue;
    }


    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.apifacade.IXbrlContext#setEntityIdentifierValue(java.lang.String)
     */
    public final void setEntityIdentifierValue(String entityIdentifierValue)
    {
        this.entityIdentifierValue = entityIdentifierValue;
    }


    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.apifacade.IXbrlContext#getPeriodStartDate()
     */
    public final Date getPeriodStartDate()
    {
        return periodStartDate;
    }


    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.apifacade.IXbrlContext#setPeriodStartDate(java.util.Date)
     */
    public final void setPeriodStartDate(Date periodStartDate)
    {
        this.periodStartDate = periodStartDate;
    }


    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.apifacade.IXbrlContext#getPeriodEndDate()
     */
    public final Date getPeriodEndDate()
    {
        return periodEndDate;
    }


    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.apifacade.IXbrlContext#setPeriodEndDate(java.util.Date)
     */
    public final void setPeriodEndDate(Date periodEndDate)
    {
        this.periodEndDate = periodEndDate;
    }


    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.apifacade.IXbrlContext#getPeriodInstant()
     */
    public final Date getPeriodInstant()
    {
        return periodInstant;
    }


    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.apifacade.IXbrlContext#setPeriodInstant(java.util.Date)
     */
    public final void setPeriodInstant(Date periodInstant)
    {
        this.periodInstant = periodInstant;
    }


    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.apifacade.IXbrlContext#isScenarioIsDimensional()
     */
    public final boolean isScenarioIsDimensional()
    {
        return scenarioIsDimensional;
    }


    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.apifacade.IXbrlContext#setScenarioIsDimensional(boolean)
     */
    public final void setScenarioIsDimensional(boolean scenarioIsDimensional)
    {
        this.scenarioIsDimensional = scenarioIsDimensional;
    }


    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.apifacade.IXbrlContext#getContextObject()
     */
    public final Object getContextObject()
    {
        return contextObject;
    }


    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.apifacade.IXbrlContext#setContextObject(java.lang.Object)
     */
    public final void setContextObject(Object contextObject)
    {
        this.contextObject = contextObject;
    }


    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.apifacade.IXbrlContext#getPeriodObject()
     */
    public final Object getPeriodObject()
    {
        return periodObject;
    }


    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.apifacade.IXbrlContext#setPeriodObject(java.lang.Object)
     */
    public final void setPeriodObject(Object periodObject)
    {
        this.periodObject = periodObject;
    }


    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.apifacade.IXbrlContext#getDimension()
     */
    public String getDimension()
    {
	return dimension;
    }


    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.apifacade.IXbrlContext#setDimension(java.lang.String)
     */
    public void setDimension(String dimension)
    {
	this.dimension = dimension;
	
    }


    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.apifacade.IXbrlContext#getMember()
     */
    public String getMember()
    {
	return member;
    }


    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.apifacade.IXbrlContext#setMember(java.lang.String)
     */
    public void setMember(String member)
    {
	this.member = member;
	
    }
    
      
    
    
    
}
