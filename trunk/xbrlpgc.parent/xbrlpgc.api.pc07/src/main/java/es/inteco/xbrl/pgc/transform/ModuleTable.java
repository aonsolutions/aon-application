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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import es.inteco.xbrl.pgc.apifacade.IXbrlContext;





/**
*
*
* Clase usada por el proceso de transformación desde XML a XBRL.
* <br /><br />
* Su función es la de almacenar los módulos clasificados por identificador y periodo. Esta clase
* mantiene una tabla donde almacena una lista de objetos del tipo Period por cada módulo.
* <br /><br />
* Por cada period se guarda un objeto Module y una lista de tables pertenecientes a dicho periodo. 
*
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
class ModuleTable
{
    private Hashtable<String, ArrayList<Period>> modules = new Hashtable<String, ArrayList<Period>>();

    
    /**
     * Constructor por defecto
     */    
    public ModuleTable() {
		super();
	}

    /*Metodo inicializador*/
    void initialize()
    {
	modules.clear();
    }
    
	/**
	 * Obtiene los periodos de un module
	 * @param moduleID
	 * @return
	 * lista de periodos
	 */
	ArrayList<Period> getPeriods(String moduleID)
    {
	ArrayList<Period> periods = modules.get(moduleID);
	if (periods == null)
	{
	    periods = new ArrayList<Period>();
	    modules.put(moduleID, periods);
	}
	return modules.get(moduleID);
    }

    /**
     * Crea un periodo dentro de un module
     * @param moduleID
     * @param context
     * @return
     * Objeto Period creado
     */
    Period  addPeriod(String moduleID, IXbrlContext context)
    {
	Period newPeriod = new Period();
	if (context.getPeriodInstant() != null)
	{
	    newPeriod.setInstantDate(context.getPeriodInstant());
	}
	else
	{
	    newPeriod.setStartDate(context.getPeriodStartDate());
	    newPeriod.setEndDate(context.getPeriodEndDate());
	}
	getPeriods(moduleID).add(newPeriod);
	return newPeriod;
    }

    /**
     * Busca un peridodo en un module
     * @param moduleID
     * @param context
     * @return
     * Objeto periodo encontrado
     */
    Period searchPeriod(String moduleID, IXbrlContext context)
    {
	Period foundPeriod = null;

	ArrayList<Period> periods = getPeriods(moduleID);

	if ((periods != null) && (!periods.isEmpty()))
	{
	    for (Iterator<Period> iterator = periods.iterator(); iterator.hasNext();)
	    {
		Period currentPeriod = iterator.next();
		if (currentPeriod.isIncluded(context))
		{
		    foundPeriod = currentPeriod;
		    break;
		}
	    }
	}
	return foundPeriod;
    }
    

}
