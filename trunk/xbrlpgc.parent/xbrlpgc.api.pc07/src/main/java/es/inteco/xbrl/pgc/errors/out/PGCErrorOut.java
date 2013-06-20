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
 * $Id: PGCErrorOut.java,v 1.1 2011/11/30 13:50:17 pablo Exp $
 */

package es.inteco.xbrl.pgc.errors.out;

/**
 * Class PGCErrorOut.
 * 
 * @version $Revision: 1.1 $ $Date: 2011/11/30 13:50:17 $
 */
@SuppressWarnings("serial")
public class PGCErrorOut implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _errorCode.
     */
    private long _errorCode;

    /**
     * keeps track of state for field: _errorCode
     */
    private boolean _has_errorCode;

    /**
     * Field _errorType.
     */
    private java.lang.String _errorType;

    /**
     * Field _PGCErrorParameterList.
     */
    private java.util.Vector<es.inteco.xbrl.pgc.errors.out.PGCErrorParameter> _PGCErrorParameterList;

    /**
     * Field _defaultDescription.
     */
    private java.lang.String _defaultDescription;

    /**
     * Field _translatedDescription.
     */
    private java.lang.String _translatedDescription;


      //----------------/
     //- Constructors -/
    //----------------/

    public PGCErrorOut() {
        super();
        this._PGCErrorParameterList = new java.util.Vector<es.inteco.xbrl.pgc.errors.out.PGCErrorParameter>();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vPGCErrorParameter
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addPGCErrorParameter(
            final es.inteco.xbrl.pgc.errors.out.PGCErrorParameter vPGCErrorParameter)
    throws java.lang.IndexOutOfBoundsException {
        this._PGCErrorParameterList.addElement(vPGCErrorParameter);
    }

    /**
     * 
     * 
     * @param index
     * @param vPGCErrorParameter
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addPGCErrorParameter(
            final int index,
            final es.inteco.xbrl.pgc.errors.out.PGCErrorParameter vPGCErrorParameter)
    throws java.lang.IndexOutOfBoundsException {
        this._PGCErrorParameterList.add(index, vPGCErrorParameter);
    }

    /**
     */
    public void deleteErrorCode(
    ) {
        this._has_errorCode= false;
    }

    /**
     * Method enumeratePGCErrorParameter.
     * 
     * @return an Enumeration over all
     * es.inteco.xbrl.pgc.errors.out.PGCErrorParameter elements
     */
    public java.util.Enumeration<? extends es.inteco.xbrl.pgc.errors.out.PGCErrorParameter> enumeratePGCErrorParameter(
    ) {
        return this._PGCErrorParameterList.elements();
    }

    /**
     * Returns the value of field 'defaultDescription'.
     * 
     * @return the value of field 'DefaultDescription'.
     */
    public java.lang.String getDefaultDescription(
    ) {
        return this._defaultDescription;
    }

    /**
     * Returns the value of field 'errorCode'.
     * 
     * @return the value of field 'ErrorCode'.
     */
    public long getErrorCode(
    ) {
        return this._errorCode;
    }

    /**
     * Returns the value of field 'errorType'.
     * 
     * @return the value of field 'ErrorType'.
     */
    public java.lang.String getErrorType(
    ) {
        return this._errorType;
    }

    /**
     * Method getPGCErrorParameter.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the
     * es.inteco.xbrl.pgc.errors.out.PGCErrorParameter at the given
     * index
     */
    public es.inteco.xbrl.pgc.errors.out.PGCErrorParameter getPGCErrorParameter(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._PGCErrorParameterList.size()) {
            throw new IndexOutOfBoundsException("getPGCErrorParameter: Index value '" + index + "' not in range [0.." + (this._PGCErrorParameterList.size() - 1) + "]");
        }
        
        return (es.inteco.xbrl.pgc.errors.out.PGCErrorParameter) _PGCErrorParameterList.get(index);
    }

    /**
     * Method getPGCErrorParameter.Returns the contents of the
     * collection in an Array.  <p>Note:  Just in case the
     * collection contents are changing in another thread, we pass
     * a 0-length Array of the correct type into the API call. 
     * This way we <i>know</i> that the Array returned is of
     * exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public es.inteco.xbrl.pgc.errors.out.PGCErrorParameter[] getPGCErrorParameter(
    ) {
        es.inteco.xbrl.pgc.errors.out.PGCErrorParameter[] array = new es.inteco.xbrl.pgc.errors.out.PGCErrorParameter[0];
        return (es.inteco.xbrl.pgc.errors.out.PGCErrorParameter[]) this._PGCErrorParameterList.toArray(array);
    }

    /**
     * Method getPGCErrorParameterCount.
     * 
     * @return the size of this collection
     */
    public int getPGCErrorParameterCount(
    ) {
        return this._PGCErrorParameterList.size();
    }

    /**
     * Returns the value of field 'translatedDescription'.
     * 
     * @return the value of field 'TranslatedDescription'.
     */
    public java.lang.String getTranslatedDescription(
    ) {
        return this._translatedDescription;
    }

    /**
     * Method hasErrorCode.
     * 
     * @return true if at least one ErrorCode has been added
     */
    public boolean hasErrorCode(
    ) {
        return this._has_errorCode;
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
    public void removeAllPGCErrorParameter(
    ) {
        this._PGCErrorParameterList.clear();
    }

    /**
     * Method removePGCErrorParameter.
     * 
     * @param vPGCErrorParameter
     * @return true if the object was removed from the collection.
     */
    public boolean removePGCErrorParameter(
            final es.inteco.xbrl.pgc.errors.out.PGCErrorParameter vPGCErrorParameter) {
        boolean removed = _PGCErrorParameterList.remove(vPGCErrorParameter);
        return removed;
    }

    /**
     * Method removePGCErrorParameterAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public es.inteco.xbrl.pgc.errors.out.PGCErrorParameter removePGCErrorParameterAt(
            final int index) {
        java.lang.Object obj = this._PGCErrorParameterList.remove(index);
        return (es.inteco.xbrl.pgc.errors.out.PGCErrorParameter) obj;
    }

    /**
     * Sets the value of field 'defaultDescription'.
     * 
     * @param defaultDescription the value of field
     * 'defaultDescription'.
     */
    public void setDefaultDescription(
            final java.lang.String defaultDescription) {
        this._defaultDescription = defaultDescription;
    }

    /**
     * Sets the value of field 'errorCode'.
     * 
     * @param errorCode the value of field 'errorCode'.
     */
    public void setErrorCode(
            final long errorCode) {
        this._errorCode = errorCode;
        this._has_errorCode = true;
    }

    /**
     * Sets the value of field 'errorType'.
     * 
     * @param errorType the value of field 'errorType'.
     */
    public void setErrorType(
            final java.lang.String errorType) {
        this._errorType = errorType;
    }

    /**
     * 
     * 
     * @param index
     * @param vPGCErrorParameter
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setPGCErrorParameter(
            final int index,
            final es.inteco.xbrl.pgc.errors.out.PGCErrorParameter vPGCErrorParameter)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._PGCErrorParameterList.size()) {
            throw new IndexOutOfBoundsException("setPGCErrorParameter: Index value '" + index + "' not in range [0.." + (this._PGCErrorParameterList.size() - 1) + "]");
        }
        
        this._PGCErrorParameterList.set(index, vPGCErrorParameter);
    }

    /**
     * 
     * 
     * @param vPGCErrorParameterArray
     */
    public void setPGCErrorParameter(
            final es.inteco.xbrl.pgc.errors.out.PGCErrorParameter[] vPGCErrorParameterArray) {
        //-- copy array
        _PGCErrorParameterList.clear();
        
        for (int i = 0; i < vPGCErrorParameterArray.length; i++) {
                this._PGCErrorParameterList.add(vPGCErrorParameterArray[i]);
        }
    }

    /**
     * Sets the value of field 'translatedDescription'.
     * 
     * @param translatedDescription the value of field
     * 'translatedDescription'.
     */
    public void setTranslatedDescription(
            final java.lang.String translatedDescription) {
        this._translatedDescription = translatedDescription;
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
     * es.inteco.xbrl.pgc.errors.out.PGCErrorOut
     */
    public static es.inteco.xbrl.pgc.errors.out.PGCErrorOut unmarshal(
            final java.io.Reader reader)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        return (es.inteco.xbrl.pgc.errors.out.PGCErrorOut) org.exolab.castor.xml.Unmarshaller.unmarshal(es.inteco.xbrl.pgc.errors.out.PGCErrorOut.class, reader);
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
