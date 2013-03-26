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


/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3rc1</a>, using an XML
 * Schema.
 * $Id: Global.java,v 1.1 2011/11/30 13:50:24 pablo Exp $
 */

package es.inteco.xbrl.pgc.transform.config;

/**
*
* This class was automatically generated with 
* <a href="http://www.castor.org">Castor 1.3rc1</a>, using an XML
* Schema.
* $Id: Global.java,v 1.1 2011/11/30 13:50:24 pablo Exp $
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
*
* @version 1.0, 14/01/2009
* @author difusioncalidad@inteco.es
*
*/
@SuppressWarnings("serial")
public class Global implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * See http://www.w3.org/TR/xmlbase/ for
     *  information about this attribute.
     */
    private java.lang.String _base;

    /**
     * Field _configReportList.
     */
    private java.util.Vector<es.inteco.xbrl.pgc.transform.config.ConfigReport> _configReportList;


      //----------------/
     //- Constructors -/
    //----------------/

    public Global() {
        super();
        this._configReportList = new java.util.Vector<es.inteco.xbrl.pgc.transform.config.ConfigReport>();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vConfigReport
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addConfigReport(
            final es.inteco.xbrl.pgc.transform.config.ConfigReport vConfigReport)
    throws java.lang.IndexOutOfBoundsException {
        this._configReportList.addElement(vConfigReport);
    }

    /**
     * 
     * 
     * @param index
     * @param vConfigReport
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addConfigReport(
            final int index,
            final es.inteco.xbrl.pgc.transform.config.ConfigReport vConfigReport)
    throws java.lang.IndexOutOfBoundsException {
        this._configReportList.add(index, vConfigReport);
    }

    /**
     * Method enumerateConfigReport.
     * 
     * @return an Enumeration over all
     * es.inteco.xbrl.pgc.transform.config.ConfigReport elements
     */
    public java.util.Enumeration<? extends es.inteco.xbrl.pgc.transform.config.ConfigReport> enumerateConfigReport(
    ) {
        return this._configReportList.elements();
    }

    /**
     * Returns the value of field 'base'. The field 'base' has the
     * following description: See http://www.w3.org/TR/xmlbase/ for
     *  information about this attribute.
     * 
     * @return the value of field 'Base'.
     */
    public java.lang.String getBase(
    ) {
        return this._base;
    }

    /**
     * Method getConfigReport.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the
     * es.inteco.xbrl.pgc.transform.config.ConfigReport at the
     * given index
     */
    public es.inteco.xbrl.pgc.transform.config.ConfigReport getConfigReport(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._configReportList.size()) {
            throw new IndexOutOfBoundsException("getConfigReport: Index value '" + index + "' not in range [0.." + (this._configReportList.size() - 1) + "]");
        }
        
        return (es.inteco.xbrl.pgc.transform.config.ConfigReport) _configReportList.get(index);
    }

    /**
     * Method getConfigReport.Returns the contents of the
     * collection in an Array.  <p>Note:  Just in case the
     * collection contents are changing in another thread, we pass
     * a 0-length Array of the correct type into the API call. 
     * This way we <i>know</i> that the Array returned is of
     * exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public es.inteco.xbrl.pgc.transform.config.ConfigReport[] getConfigReport(
    ) {
        es.inteco.xbrl.pgc.transform.config.ConfigReport[] array = new es.inteco.xbrl.pgc.transform.config.ConfigReport[0];
        return (es.inteco.xbrl.pgc.transform.config.ConfigReport[]) this._configReportList.toArray(array);
    }

    /**
     * Method getConfigReportCount.
     * 
     * @return the size of this collection
     */
    public int getConfigReportCount(
    ) {
        return this._configReportList.size();
    }

    /**
     * Method isValid.
     * 
     * @return true if this object is valid according to the schema
     */
    public boolean isValid(
    ) {
        try {
            validate();
        } catch (org.exolab.castor.xml.ValidationException vex) {
            return false;
        }
        return true;
    }

    /**
     * 
     * 
     * @param out
     * @throws org.exolab.castor.xml.MarshalException if object is
     * null or if any SAXException is thrown during marshaling
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     */
    public void marshal(
            final java.io.Writer out)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        org.exolab.castor.xml.Marshaller.marshal(this, out);
    }

    /**
     * 
     * 
     * @param handler
     * @throws java.io.IOException if an IOException occurs during
     * marshaling
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     * @throws org.exolab.castor.xml.MarshalException if object is
     * null or if any SAXException is thrown during marshaling
     */
    public void marshal(
            final org.xml.sax.ContentHandler handler)
    throws java.io.IOException, org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        org.exolab.castor.xml.Marshaller.marshal(this, handler);
    }

    /**
     */
    public void removeAllConfigReport(
    ) {
        this._configReportList.clear();
    }

    /**
     * Method removeConfigReport.
     * 
     * @param vConfigReport
     * @return true if the object was removed from the collection.
     */
    public boolean removeConfigReport(
            final es.inteco.xbrl.pgc.transform.config.ConfigReport vConfigReport) {
        boolean removed = _configReportList.remove(vConfigReport);
        return removed;
    }

    /**
     * Method removeConfigReportAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public es.inteco.xbrl.pgc.transform.config.ConfigReport removeConfigReportAt(
            final int index) {
        java.lang.Object obj = this._configReportList.remove(index);
        return (es.inteco.xbrl.pgc.transform.config.ConfigReport) obj;
    }

    /**
     * Sets the value of field 'base'. The field 'base' has the
     * following description: See http://www.w3.org/TR/xmlbase/ for
     *  information about this attribute.
     * 
     * @param base the value of field 'base'.
     */
    public void setBase(
            final java.lang.String base) {
        this._base = base;
    }

    /**
     * 
     * 
     * @param index
     * @param vConfigReport
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setConfigReport(
            final int index,
            final es.inteco.xbrl.pgc.transform.config.ConfigReport vConfigReport)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._configReportList.size()) {
            throw new IndexOutOfBoundsException("setConfigReport: Index value '" + index + "' not in range [0.." + (this._configReportList.size() - 1) + "]");
        }
        
        this._configReportList.set(index, vConfigReport);
    }

    /**
     * 
     * 
     * @param vConfigReportArray
     */
    public void setConfigReport(
            final es.inteco.xbrl.pgc.transform.config.ConfigReport[] vConfigReportArray) {
        //-- copy array
        _configReportList.clear();
        
        for (int i = 0; i < vConfigReportArray.length; i++) {
                this._configReportList.add(vConfigReportArray[i]);
        }
    }

    /**
     * Method unmarshal.
     * 
     * @param reader
     * @throws org.exolab.castor.xml.MarshalException if object is
     * null or if any SAXException is thrown during marshaling
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     * @return the unmarshaled
     * es.inteco.xbrl.pgc.transform.config.Global
     */
    public static es.inteco.xbrl.pgc.transform.config.Global unmarshal(
            final java.io.Reader reader)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        return (es.inteco.xbrl.pgc.transform.config.Global) org.exolab.castor.xml.Unmarshaller.unmarshal(es.inteco.xbrl.pgc.transform.config.Global.class, reader);
    }

    /**
     * 
     * 
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     */
    public void validate(
    )
    throws org.exolab.castor.xml.ValidationException {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    }

}
