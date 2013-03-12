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


package es.inteco.xbrl.pgc.apifacade;

import java.util.ArrayList;


/**
 *
 * Interfaz que debe cumplir una clase que pretende encapsular un objeto XBRL del tipo
 * Fact
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


public interface IXbrlFact
{

    /**
     * Devuelve el concepto
     * @return
     * Concepto
     */
    public abstract String getConcept();

    /**
     * Establece el concepto
     * @param concept
     * Concepto
     */
    public abstract void setConcept(String concept);

    /**
     * Devuelve el valor del Fact
     * @return
     * Valor del fact
     */
    public abstract String getValue();

    /**
     * Establece el valor del fact
     * @param value
     * Valor del fact
     */
    public abstract void setValue(String value);

    /**
     * Devuelve el atributo decimals del fact
     * @return
     * Decimals del fact
     */
    public abstract String getDecimals();

    /**
     * Establece los decimals del fact
     * @param decimals
     * Decimals del fact
     */
    public abstract void setDecimals(String decimals);

    /**
     * Devuelve el objeto IXbrlUnit del fact
     * @return
     * objeto IXbrlUnit del fact
     */
    public abstract IXbrlUnit getUnit();

    /**
     * Establece un unit para el Fact
     * @param unit
     * objeto IXbrlUnit para el fact
     */
    public abstract void setUnit(IXbrlUnit unit);

    /**
     * Devuelve el objeto IXbrlContext del fact
     * @return
     * objeot IXbrlContext del fact
     */
    public abstract IXbrlContext getContext();

    /**
     * Establece el contexto para el fact
     * @param context
     * objeto IXbrlContext para el fact
     */
    public abstract void setContext(IXbrlContext context);

    /**
     * Devuelve el fact como Object
     * @return
     * fact en cast as object
     */
    public abstract Object getFactObject();

    /**
     * Establece el fact a partir de un object
     * @param factObject
     * objeto del tipo Object que representa el fact
     */
    public abstract void setFactObject(Object factObject);

    /**
     * Devuelve el prefijo del fact
     * @return
     * prefijo del fact
     */
    public abstract String getPrefix();

    /**
     * Establece el prefijo del fact
     * @param prefix
     * prefijo del fact
     */
    public abstract void setPrefix(String prefix);

    /**
     * Devuelve el namespace del fact
     * @return
     * namespace del fact
     */
    public abstract String getNamespace();

    /**
     * Establece el namespace para el fact
     * @param namespace
     * namespace para el fact
     */
    public abstract void setNamespace(String namespace);

    /**
     * Devuelve el signo del fact
     * @return
     * signo del fact
     */
    public abstract String getSign();

    /**
     * Establece el signo del fact
     * @param sign
     * signo del fact
     */
    public abstract void setSign(String sign);
    
    /**
     * Devuelve la lista de comentarios del fact
     * @return
     * lista de comentarios del fact
     */
    public abstract ArrayList<String> getFootNotes();

    /**
     * Establece la lista de comentarios del fact
     * @param footNotes
     * lista de comentarios del fact
     */
    public abstract void setFootNotes(ArrayList<String> footNotes);

    /**
     * Devuelve un indicador de si el fact es una tupla
     * @return
     * Indicador de si el fact es una tupla
     */
    public abstract boolean isTuple();
    
    
    /**
     * Devuelve el orden del fact
     * @return
     * orden del fact
     */
    public abstract int getOrder();
    
    /**
     * Establece el orden del fact
     * @param order
     * orden del fact
     */
    public abstract void setOrder(int order);
    
    
}
