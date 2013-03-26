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


package es.inteco.xbrl.pgc.transform;

import java.util.Date;
import java.util.Hashtable;

import es.inteco.xbrl.pgc.apifacade.IXbrlContext;
import es.inteco.xbrl.pgc.transform.format.Module;
import es.inteco.xbrl.pgc.transform.format.Table;


/**
*
*
* Clase utilizada en el proceso de transformación desde XBRL a XML.
* <br /><br />
* Se utitiliza para almacenar un objeto Module clasificado por el periodo en el cual
* se encuentra definido en una instancia XBRL y una lista de objetos del tipo
* Table correspondientes a las dimensiones incluidas en dicho módulo.
*
*
*<br><br>
* <b>Proyecto</b>: API XBRL-PGC2007 - Grupo de utilidades y librerías en código abierto para facilitar 
*                             la integración del formato XBRL en las herramientas software de gestión de  terceros
*                             aislándose de la complejidad en el procesamiento del modelo de datos de las taxonomías.
*                             Ayudando de esta forma a las empresas finales en la labor de realización de informes XBRL
*                             y asegurar el éxito de implantación del nuevo Plan General de Contabilidad 2007 en formato XBRL
*
* @version 1.0, 14/01/2009
* @author difusioncalidad@inteco.es
*
*/

class Period 
{
	private Date startDate = null;
	private Date endDate = null;
	private Date instantDate = null;
	private Module module = null;
	private Hashtable<String, Table> tables = new Hashtable<String,Table>();
	
	/**
	 * Constructor por defecto
	 */
	public Period() {
		super();
	}
	
	/**
	 * Devuelve la fecha inicial del periodo
	 * @return
	 * fecha inicial del periodo
	 */
	final Date getStartDate()
	{
	    return startDate;
	}
	/**
	 * Establece la fecha inicial del periodo
	 * @param startDate
	 * Fecha inicial del periodo
	 */
	final void setStartDate(Date startDate)
	{
	    this.startDate = startDate;
	}
	/**
	 * Devuelve la fecha final del periodo
	 * @return
	 * fecha final del periodo
	 */
	final Date getEndDate()
	{
	    return endDate;
	}
	/**
	 * Establece la fecha final del periodo
	 * @param endDate
	 * fecha final del periodo
	 */
	final void setEndDate(Date endDate)
	{
	    this.endDate = endDate;
	}
	/**
	 * Devuelve la fecha de tipo instant
	 * @return
	 * fecha
	 */
	final Date getInstantDate()
	{
	    return instantDate;
	}
	/**
	 * Establece la fecha de tipo instant
	 * @param instantDate
	 * fecha
	 */
	final void setInstantDate(Date instantDate)
	{
	    this.instantDate = instantDate;
	}
	
	/**
	 * Actualiza el periodo de un contexto
	 * @param context
	 * contexto a actualizar
	 */
	void upgradePeriod(IXbrlContext context)
	{
	    if (context.getPeriodEndDate() != null)
	    {
		instantDate = null;
		endDate = context.getPeriodEndDate();
		startDate = context.getPeriodStartDate();
		getModule().setReportingDateEnd(new org.exolab.castor.types.Date(endDate));
		getModule().setReportingDateStart(new org.exolab.castor.types.Date(startDate));
		
	    }
	}
	
	/**
	 * Comprueba si la fecha del contexto está dentro del periodo
	 * @param context
	 * @return
	 * indicador true/false
	 */
	boolean isIncluded(IXbrlContext context)
	{
	    boolean included = false;
	    
	    Date instantPeriod = context.getPeriodInstant();
	    
	    if (instantPeriod != null)
	    {
		
		if (instantDate != null)
		{
		    included = instantDate.compareTo(instantPeriod)==0;
		}
		else
		{
		    included = ((instantPeriod.compareTo(this.startDate)>=0) &&  (instantPeriod.compareTo(this.endDate)<=0));
		}
	    }
	    else
	    {
		Date startPeriod = context.getPeriodStartDate();
		Date endPeriod = context.getPeriodEndDate();
		
		if (instantDate != null)
		{
		    included = ((instantDate.compareTo(startPeriod)>=0) &&  (instantDate.compareTo(endPeriod)<=0));
		}
		else
		{
		    included = ((startDate.compareTo(startPeriod)<=0) &&  (endDate.compareTo(endPeriod)>=0));
		}
	    }
	
	    return included;
	}
	/**
	 * Devuelve el module del periodo
	 * @return
	 * objeto tipo Module
	 */
	final Module getModule()
	{
	    return module;
	}
	/**
	 * Establece el module del periodo
	 * @param module
	 * objeto tipo Module
	 */
	final void setModule(Module module)
	{
	    this.module = module;
	}
	/**
	 * Obtiene el objeto table del periodo a través de una dimensión
	 * @param dimension
	 * @return
	 * objeto del tipo table
	 */
	final Table getTable(String dimension)
	{
	    return tables.get(dimension);
	}
	/**
	 * Añade un objeto del tipo table al periodo
	 * @param dimension
	 * se añade para una determinada dimensión
	 * @param table
	 * table agregada
	 */
	final void addTable(String dimension, Table table)
	{
	    if (!tables.containsKey(dimension))
	    {
		tables.put(dimension, table);
	    }
	}
}
