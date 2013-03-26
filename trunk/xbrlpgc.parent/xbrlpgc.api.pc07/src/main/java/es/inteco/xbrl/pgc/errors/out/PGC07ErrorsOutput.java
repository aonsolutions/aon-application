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
 * $Id: PGC07ErrorsOutput.java,v 1.1 2011/11/30 13:50:16 pablo Exp $
 */

package es.inteco.xbrl.pgc.errors.out;

/**
 * Class PGC07ErrorsOutput.
 * 
 * @version $Revision: 1.1 $ $Date: 2011/11/30 13:50:16 $
 */
@SuppressWarnings("serial")
public class PGC07ErrorsOutput implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _PGCErrorOutList.
     */
    private java.util.Vector<es.inteco.xbrl.pgc.errors.out.PGCErrorOut> _PGCErrorOutList;


      //----------------/
     //- Constructors -/
    //----------------/

    public PGC07ErrorsOutput() {
        super();
        this._PGCErrorOutList = new java.util.Vector<es.inteco.xbrl.pgc.errors.out.PGCErrorOut>();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vPGCErrorOut
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addPGCErrorOut(
            final es.inteco.xbrl.pgc.errors.out.PGCErrorOut vPGCErrorOut)
    throws java.lang.IndexOutOfBoundsException {
        this._PGCErrorOutList.addElement(vPGCErrorOut);
    }

    /**
     * 
     * 
     * @param index
     * @param vPGCErrorOut
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addPGCErrorOut(
            final int index,
            final es.inteco.xbrl.pgc.errors.out.PGCErrorOut vPGCErrorOut)
    throws java.lang.IndexOutOfBoundsException {
        this._PGCErrorOutList.add(index, vPGCErrorOut);
    }

    /**
     * Method enumeratePGCErrorOut.
     * 
     * @return an Enumeration over all
     * es.inteco.xbrl.pgc.errors.out.PGCErrorOut elements
     */
    public java.util.Enumeration<? extends es.inteco.xbrl.pgc.errors.out.PGCErrorOut> enumeratePGCErrorOut(
    ) {
        return this._PGCErrorOutList.elements();
    }

    /**
     * Method getPGCErrorOut.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the
     * es.inteco.xbrl.pgc.errors.out.PGCErrorOut at the given index
     */
    public es.inteco.xbrl.pgc.errors.out.PGCErrorOut getPGCErrorOut(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._PGCErrorOutList.size()) {
            throw new IndexOutOfBoundsException("getPGCErrorOut: Index value '" + index + "' not in range [0.." + (this._PGCErrorOutList.size() - 1) + "]");
        }
        
        return (es.inteco.xbrl.pgc.errors.out.PGCErrorOut) _PGCErrorOutList.get(index);
    }

    /**
     * Method getPGCErrorOut.Returns the contents of the collection
     * in an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public es.inteco.xbrl.pgc.errors.out.PGCErrorOut[] getPGCErrorOut(
    ) {
        es.inteco.xbrl.pgc.errors.out.PGCErrorOut[] array = new es.inteco.xbrl.pgc.errors.out.PGCErrorOut[0];
        return (es.inteco.xbrl.pgc.errors.out.PGCErrorOut[]) this._PGCErrorOutList.toArray(array);
    }

    /**
     * Method getPGCErrorOutCount.
     * 
     * @return the size of this collection
     */
    public int getPGCErrorOutCount(
    ) {
        return this._PGCErrorOutList.size();
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
    public void removeAllPGCErrorOut(
    ) {
        this._PGCErrorOutList.clear();
    }

    /**
     * Method removePGCErrorOut.
     * 
     * @param vPGCErrorOut
     * @return true if the object was removed from the collection.
     */
    public boolean removePGCErrorOut(
            final es.inteco.xbrl.pgc.errors.out.PGCErrorOut vPGCErrorOut) {
        boolean removed = _PGCErrorOutList.remove(vPGCErrorOut);
        return removed;
    }

    /**
     * Method removePGCErrorOutAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public es.inteco.xbrl.pgc.errors.out.PGCErrorOut removePGCErrorOutAt(
            final int index) {
        java.lang.Object obj = this._PGCErrorOutList.remove(index);
        return (es.inteco.xbrl.pgc.errors.out.PGCErrorOut) obj;
    }

    /**
     * 
     * 
     * @param index
     * @param vPGCErrorOut
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setPGCErrorOut(
            final int index,
            final es.inteco.xbrl.pgc.errors.out.PGCErrorOut vPGCErrorOut)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._PGCErrorOutList.size()) {
            throw new IndexOutOfBoundsException("setPGCErrorOut: Index value '" + index + "' not in range [0.." + (this._PGCErrorOutList.size() - 1) + "]");
        }
        
        this._PGCErrorOutList.set(index, vPGCErrorOut);
    }

    /**
     * 
     * 
     * @param vPGCErrorOutArray
     */
    public void setPGCErrorOut(
            final es.inteco.xbrl.pgc.errors.out.PGCErrorOut[] vPGCErrorOutArray) {
        //-- copy array
        _PGCErrorOutList.clear();
        
        for (int i = 0; i < vPGCErrorOutArray.length; i++) {
                this._PGCErrorOutList.add(vPGCErrorOutArray[i]);
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
     * es.inteco.xbrl.pgc.errors.out.PGC07ErrorsOutput
     */
    public static es.inteco.xbrl.pgc.errors.out.PGC07ErrorsOutput unmarshal(
            final java.io.Reader reader)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        return (es.inteco.xbrl.pgc.errors.out.PGC07ErrorsOutput) org.exolab.castor.xml.Unmarshaller.unmarshal(es.inteco.xbrl.pgc.errors.out.PGC07ErrorsOutput.class, reader);
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
