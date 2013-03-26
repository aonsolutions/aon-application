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
 * $Id: Report.java,v 1.1 2011/11/30 13:50:28 pablo Exp $
 */

package es.inteco.xbrl.pgc.transform.format;

/**
*
* This class was automatically generated with 
* <a href="http://www.castor.org">Castor 1.3rc1</a>, using an XML
* Schema.
* $Id: Report.java,v 1.1 2011/11/30 13:50:28 pablo Exp $
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
public class Report implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _id.
     */
    private java.lang.String _id;

    /**
     * Field _date.
     */
    private java.util.Date _date;

    /**
     * Field _entity.
     */
    private es.inteco.xbrl.pgc.transform.format.Entity _entity;

    /**
     * Field _moduleList.
     */
    private java.util.Vector<es.inteco.xbrl.pgc.transform.format.Module> _moduleList;


      //----------------/
     //- Constructors -/
    //----------------/

    public Report() {
        super();
        this._moduleList = new java.util.Vector<es.inteco.xbrl.pgc.transform.format.Module>();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vModule
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addModule(
            final es.inteco.xbrl.pgc.transform.format.Module vModule)
    throws java.lang.IndexOutOfBoundsException {
        this._moduleList.addElement(vModule);
    }

    /**
     * 
     * 
     * @param index
     * @param vModule
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addModule(
            final int index,
            final es.inteco.xbrl.pgc.transform.format.Module vModule)
    throws java.lang.IndexOutOfBoundsException {
        this._moduleList.add(index, vModule);
    }

    /**
     * Method enumerateModule.
     * 
     * @return an Enumeration over all
     * es.inteco.xbrl.pgc.transform.format.Module elements
     */
    public java.util.Enumeration<? extends es.inteco.xbrl.pgc.transform.format.Module> enumerateModule(
    ) {
        return this._moduleList.elements();
    }

    /**
     * Returns the value of field 'date'.
     * 
     * @return the value of field 'Date'.
     */
    public java.util.Date getDate(
    ) {
        return this._date;
    }

    /**
     * Returns the value of field 'entity'.
     * 
     * @return the value of field 'Entity'.
     */
    public es.inteco.xbrl.pgc.transform.format.Entity getEntity(
    ) {
        return this._entity;
    }

    /**
     * Returns the value of field 'id'.
     * 
     * @return the value of field 'Id'.
     */
    public java.lang.String getId(
    ) {
        return this._id;
    }

    /**
     * Method getModule.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the
     * es.inteco.xbrl.pgc.transform.format.Module at the given index
     */
    public es.inteco.xbrl.pgc.transform.format.Module getModule(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._moduleList.size()) {
            throw new IndexOutOfBoundsException("getModule: Index value '" + index + "' not in range [0.." + (this._moduleList.size() - 1) + "]");
        }
        
        return (es.inteco.xbrl.pgc.transform.format.Module) _moduleList.get(index);
    }

    /**
     * Method getModule.Returns the contents of the collection in
     * an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public es.inteco.xbrl.pgc.transform.format.Module[] getModule(
    ) {
        es.inteco.xbrl.pgc.transform.format.Module[] array = new es.inteco.xbrl.pgc.transform.format.Module[0];
        return (es.inteco.xbrl.pgc.transform.format.Module[]) this._moduleList.toArray(array);
    }

    /**
     * Method getModuleCount.
     * 
     * @return the size of this collection
     */
    public int getModuleCount(
    ) {
        return this._moduleList.size();
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
    public void removeAllModule(
    ) {
        this._moduleList.clear();
    }

    /**
     * Method removeModule.
     * 
     * @param vModule
     * @return true if the object was removed from the collection.
     */
    public boolean removeModule(
            final es.inteco.xbrl.pgc.transform.format.Module vModule) {
        boolean removed = _moduleList.remove(vModule);
        return removed;
    }

    /**
     * Method removeModuleAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public es.inteco.xbrl.pgc.transform.format.Module removeModuleAt(
            final int index) {
        java.lang.Object obj = this._moduleList.remove(index);
        return (es.inteco.xbrl.pgc.transform.format.Module) obj;
    }

    /**
     * Sets the value of field 'date'.
     * 
     * @param date the value of field 'date'.
     */
    public void setDate(
            final java.util.Date date) {
        this._date = date;
    }

    /**
     * Sets the value of field 'entity'.
     * 
     * @param entity the value of field 'entity'.
     */
    public void setEntity(
            final es.inteco.xbrl.pgc.transform.format.Entity entity) {
        this._entity = entity;
    }

    /**
     * Sets the value of field 'id'.
     * 
     * @param id the value of field 'id'.
     */
    public void setId(
            final java.lang.String id) {
        this._id = id;
    }

    /**
     * 
     * 
     * @param index
     * @param vModule
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setModule(
            final int index,
            final es.inteco.xbrl.pgc.transform.format.Module vModule)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._moduleList.size()) {
            throw new IndexOutOfBoundsException("setModule: Index value '" + index + "' not in range [0.." + (this._moduleList.size() - 1) + "]");
        }
        
        this._moduleList.set(index, vModule);
    }

    /**
     * 
     * 
     * @param vModuleArray
     */
    public void setModule(
            final es.inteco.xbrl.pgc.transform.format.Module[] vModuleArray) {
        //-- copy array
        _moduleList.clear();
        
        for (int i = 0; i < vModuleArray.length; i++) {
                this._moduleList.add(vModuleArray[i]);
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
     * es.inteco.xbrl.pgc.transform.format.Report
     */
    public static es.inteco.xbrl.pgc.transform.format.Report unmarshal(
            final java.io.Reader reader)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        return (es.inteco.xbrl.pgc.transform.format.Report) org.exolab.castor.xml.Unmarshaller.unmarshal(es.inteco.xbrl.pgc.transform.format.Report.class, reader);
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
