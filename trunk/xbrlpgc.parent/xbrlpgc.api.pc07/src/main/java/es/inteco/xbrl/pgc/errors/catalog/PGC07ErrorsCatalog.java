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
 * $Id: PGC07ErrorsCatalog.java,v 1.1 2011/11/30 13:50:12 pablo Exp $
 */

package es.inteco.xbrl.pgc.errors.catalog;

/**
 * Class PGC07ErrorsCatalog.
 * 
 * @version $Revision: 1.1 $ $Date: 2011/11/30 13:50:12 $
 */
@SuppressWarnings("serial")
public class PGC07ErrorsCatalog implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _PGCErrorList.
     */
    private java.util.Vector<es.inteco.xbrl.pgc.errors.catalog.PGCError> _PGCErrorList;


      //----------------/
     //- Constructors -/
    //----------------/

    public PGC07ErrorsCatalog() {
        super();
        this._PGCErrorList = new java.util.Vector<es.inteco.xbrl.pgc.errors.catalog.PGCError>();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vPGCError
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addPGCError(
            final es.inteco.xbrl.pgc.errors.catalog.PGCError vPGCError)
    throws java.lang.IndexOutOfBoundsException {
        this._PGCErrorList.addElement(vPGCError);
    }

    /**
     * 
     * 
     * @param index
     * @param vPGCError
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addPGCError(
            final int index,
            final es.inteco.xbrl.pgc.errors.catalog.PGCError vPGCError)
    throws java.lang.IndexOutOfBoundsException {
        this._PGCErrorList.add(index, vPGCError);
    }

    /**
     * Method enumeratePGCError.
     * 
     * @return an Enumeration over all
     * es.inteco.xbrl.pgc.errors.catalog.PGCError elements
     */
    public java.util.Enumeration<? extends es.inteco.xbrl.pgc.errors.catalog.PGCError> enumeratePGCError(
    ) {
        return this._PGCErrorList.elements();
    }

    /**
     * Method getPGCError.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the
     * es.inteco.xbrl.pgc.errors.catalog.PGCError at the given index
     */
    public es.inteco.xbrl.pgc.errors.catalog.PGCError getPGCError(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._PGCErrorList.size()) {
            throw new IndexOutOfBoundsException("getPGCError: Index value '" + index + "' not in range [0.." + (this._PGCErrorList.size() - 1) + "]");
        }
        
        return (es.inteco.xbrl.pgc.errors.catalog.PGCError) _PGCErrorList.get(index);
    }

    /**
     * Method getPGCError.Returns the contents of the collection in
     * an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public es.inteco.xbrl.pgc.errors.catalog.PGCError[] getPGCError(
    ) {
        es.inteco.xbrl.pgc.errors.catalog.PGCError[] array = new es.inteco.xbrl.pgc.errors.catalog.PGCError[0];
        return (es.inteco.xbrl.pgc.errors.catalog.PGCError[]) this._PGCErrorList.toArray(array);
    }

    /**
     * Method getPGCErrorCount.
     * 
     * @return the size of this collection
     */
    public int getPGCErrorCount(
    ) {
        return this._PGCErrorList.size();
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
    public void removeAllPGCError(
    ) {
        this._PGCErrorList.clear();
    }

    /**
     * Method removePGCError.
     * 
     * @param vPGCError
     * @return true if the object was removed from the collection.
     */
    public boolean removePGCError(
            final es.inteco.xbrl.pgc.errors.catalog.PGCError vPGCError) {
        boolean removed = _PGCErrorList.remove(vPGCError);
        return removed;
    }

    /**
     * Method removePGCErrorAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public es.inteco.xbrl.pgc.errors.catalog.PGCError removePGCErrorAt(
            final int index) {
        java.lang.Object obj = this._PGCErrorList.remove(index);
        return (es.inteco.xbrl.pgc.errors.catalog.PGCError) obj;
    }

    /**
     * 
     * 
     * @param index
     * @param vPGCError
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setPGCError(
            final int index,
            final es.inteco.xbrl.pgc.errors.catalog.PGCError vPGCError)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._PGCErrorList.size()) {
            throw new IndexOutOfBoundsException("setPGCError: Index value '" + index + "' not in range [0.." + (this._PGCErrorList.size() - 1) + "]");
        }
        
        this._PGCErrorList.set(index, vPGCError);
    }

    /**
     * 
     * 
     * @param vPGCErrorArray
     */
    public void setPGCError(
            final es.inteco.xbrl.pgc.errors.catalog.PGCError[] vPGCErrorArray) {
        //-- copy array
        _PGCErrorList.clear();
        
        for (int i = 0; i < vPGCErrorArray.length; i++) {
                this._PGCErrorList.add(vPGCErrorArray[i]);
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
     * es.inteco.xbrl.pgc.errors.catalog.PGC07ErrorsCatalog
     */
    public static es.inteco.xbrl.pgc.errors.catalog.PGC07ErrorsCatalog unmarshal(
            final java.io.Reader reader)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        return (es.inteco.xbrl.pgc.errors.catalog.PGC07ErrorsCatalog) org.exolab.castor.xml.Unmarshaller.unmarshal(es.inteco.xbrl.pgc.errors.catalog.PGC07ErrorsCatalog.class, reader);
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
