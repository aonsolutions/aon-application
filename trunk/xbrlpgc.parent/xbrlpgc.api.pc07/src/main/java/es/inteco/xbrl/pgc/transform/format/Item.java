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
 * $Id: Item.java,v 1.1 2011/11/30 13:50:27 pablo Exp $
 */

package es.inteco.xbrl.pgc.transform.format;

/**
*
* This class was automatically generated with 
* <a href="http://www.castor.org">Castor 1.3rc1</a>, using an XML
* Schema.
* $Id: Item.java,v 1.1 2011/11/30 13:50:27 pablo Exp $
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
public class Item implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _id.
     */
    private java.lang.String _id;

    /**
     * Field _sign.
     */
    private es.inteco.xbrl.pgc.transform.format.types.ItemSignType _sign;

    /**
     * Field _unit.
     */
    private es.inteco.xbrl.pgc.transform.format.types.Unittype _unit;

    /**
     * Field _decimals.
     */
    private java.lang.String _decimals;

    /**
     * Field _reportingDate.
     */
    private org.exolab.castor.types.Date _reportingDate;

    /**
     * Field _value.
     */
    private java.lang.String _value;

    /**
     * Field _noteList.
     */
    private java.util.Vector<es.inteco.xbrl.pgc.transform.format.Note> _noteList;


      //----------------/
     //- Constructors -/
    //----------------/

    public Item() {
        super();
        this._noteList = new java.util.Vector<es.inteco.xbrl.pgc.transform.format.Note>();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vNote
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addNote(
            final es.inteco.xbrl.pgc.transform.format.Note vNote)
    throws java.lang.IndexOutOfBoundsException {
        this._noteList.addElement(vNote);
    }

    /**
     * 
     * 
     * @param index
     * @param vNote
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addNote(
            final int index,
            final es.inteco.xbrl.pgc.transform.format.Note vNote)
    throws java.lang.IndexOutOfBoundsException {
        this._noteList.add(index, vNote);
    }

    /**
     * Method enumerateNote.
     * 
     * @return an Enumeration over all
     * es.inteco.xbrl.pgc.transform.format.Note elements
     */
    public java.util.Enumeration<? extends es.inteco.xbrl.pgc.transform.format.Note> enumerateNote(
    ) {
        return this._noteList.elements();
    }

    /**
     * Returns the value of field 'decimals'.
     * 
     * @return the value of field 'Decimals'.
     */
    public java.lang.String getDecimals(
    ) {
        return this._decimals;
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
     * Method getNote.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the
     * es.inteco.xbrl.pgc.transform.format.Note at the given index
     */
    public es.inteco.xbrl.pgc.transform.format.Note getNote(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._noteList.size()) {
            throw new IndexOutOfBoundsException("getNote: Index value '" + index + "' not in range [0.." + (this._noteList.size() - 1) + "]");
        }
        
        return (es.inteco.xbrl.pgc.transform.format.Note) _noteList.get(index);
    }

    /**
     * Method getNote.Returns the contents of the collection in an
     * Array.  <p>Note:  Just in case the collection contents are
     * changing in another thread, we pass a 0-length Array of the
     * correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public es.inteco.xbrl.pgc.transform.format.Note[] getNote(
    ) {
        es.inteco.xbrl.pgc.transform.format.Note[] array = new es.inteco.xbrl.pgc.transform.format.Note[0];
        return (es.inteco.xbrl.pgc.transform.format.Note[]) this._noteList.toArray(array);
    }

    /**
     * Method getNoteCount.
     * 
     * @return the size of this collection
     */
    public int getNoteCount(
    ) {
        return this._noteList.size();
    }

    /**
     * Returns the value of field 'reportingDate'.
     * 
     * @return the value of field 'ReportingDate'.
     */
    public org.exolab.castor.types.Date getReportingDate(
    ) {
        return this._reportingDate;
    }

    /**
     * Returns the value of field 'sign'.
     * 
     * @return the value of field 'Sign'.
     */
    public es.inteco.xbrl.pgc.transform.format.types.ItemSignType getSign(
    ) {
        return this._sign;
    }

    /**
     * Returns the value of field 'unit'.
     * 
     * @return the value of field 'Unit'.
     */
    public es.inteco.xbrl.pgc.transform.format.types.Unittype getUnit(
    ) {
        return this._unit;
    }

    /**
     * Returns the value of field 'value'.
     * 
     * @return the value of field 'Value'.
     */
    public java.lang.String getValue(
    ) {
        return this._value;
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
    public void removeAllNote(
    ) {
        this._noteList.clear();
    }

    /**
     * Method removeNote.
     * 
     * @param vNote
     * @return true if the object was removed from the collection.
     */
    public boolean removeNote(
            final es.inteco.xbrl.pgc.transform.format.Note vNote) {
        boolean removed = _noteList.remove(vNote);
        return removed;
    }

    /**
     * Method removeNoteAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public es.inteco.xbrl.pgc.transform.format.Note removeNoteAt(
            final int index) {
        java.lang.Object obj = this._noteList.remove(index);
        return (es.inteco.xbrl.pgc.transform.format.Note) obj;
    }

    /**
     * Sets the value of field 'decimals'.
     * 
     * @param decimals the value of field 'decimals'.
     */
    public void setDecimals(
            final java.lang.String decimals) {
        this._decimals = decimals;
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
     * @param vNote
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setNote(
            final int index,
            final es.inteco.xbrl.pgc.transform.format.Note vNote)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._noteList.size()) {
            throw new IndexOutOfBoundsException("setNote: Index value '" + index + "' not in range [0.." + (this._noteList.size() - 1) + "]");
        }
        
        this._noteList.set(index, vNote);
    }

    /**
     * 
     * 
     * @param vNoteArray
     */
    public void setNote(
            final es.inteco.xbrl.pgc.transform.format.Note[] vNoteArray) {
        //-- copy array
        _noteList.clear();
        
        for (int i = 0; i < vNoteArray.length; i++) {
                this._noteList.add(vNoteArray[i]);
        }
    }

    /**
     * Sets the value of field 'reportingDate'.
     * 
     * @param reportingDate the value of field 'reportingDate'.
     */
    public void setReportingDate(
            final org.exolab.castor.types.Date reportingDate) {
        this._reportingDate = reportingDate;
    }

    /**
     * Sets the value of field 'sign'.
     * 
     * @param sign the value of field 'sign'.
     */
    public void setSign(
            final es.inteco.xbrl.pgc.transform.format.types.ItemSignType sign) {
        this._sign = sign;
    }

    /**
     * Sets the value of field 'unit'.
     * 
     * @param unit the value of field 'unit'.
     */
    public void setUnit(
            final es.inteco.xbrl.pgc.transform.format.types.Unittype unit) {
        this._unit = unit;
    }

    /**
     * Sets the value of field 'value'.
     * 
     * @param value the value of field 'value'.
     */
    public void setValue(
            final java.lang.String value) {
        this._value = value;
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
     * es.inteco.xbrl.pgc.transform.format.Item
     */
    public static es.inteco.xbrl.pgc.transform.format.Item unmarshal(
            final java.io.Reader reader)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        return (es.inteco.xbrl.pgc.transform.format.Item) org.exolab.castor.xml.Unmarshaller.unmarshal(es.inteco.xbrl.pgc.transform.format.Item.class, reader);
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
