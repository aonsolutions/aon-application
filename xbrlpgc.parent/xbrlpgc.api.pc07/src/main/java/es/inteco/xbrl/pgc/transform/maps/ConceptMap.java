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
 * $Id: ConceptMap.java,v 1.1 2011/11/30 13:50:34 pablo Exp $
 */

package es.inteco.xbrl.pgc.transform.maps;

/**
*
* This class was automatically generated with 
* <a href="http://www.castor.org">Castor 1.3rc1</a>, using an XML
* Schema.
* $Id: ConceptMap.java,v 1.1 2011/11/30 13:50:34 pablo Exp $
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
public class ConceptMap implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * El nombre del tema XBRL
     */
    private java.lang.String _outputID;

    /**
     * El codigo EspaÃ±ol.
     */
    private java.lang.String _inputID;

    /**
     * Field _ns.
     */
    private java.lang.String _ns;

    /**
     * Field _nsPrefix.
     */
    private java.lang.String _nsPrefix;

    /**
     * Nombre de la dimensiÃ³n
     */
    private java.lang.String _domain;

    /**
     * Field _parent.
     */
    private java.lang.String _parent;

    /**
     * Field _order.
     */
    private java.math.BigDecimal _order;

    /**
     * Field _memberList.
     */
    private java.util.Vector<es.inteco.xbrl.pgc.transform.maps.Member> _memberList;


      //----------------/
     //- Constructors -/
    //----------------/

    public ConceptMap() {
        super();
        this._memberList = new java.util.Vector<es.inteco.xbrl.pgc.transform.maps.Member>();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vMember
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addMember(
            final es.inteco.xbrl.pgc.transform.maps.Member vMember)
    throws java.lang.IndexOutOfBoundsException {
        this._memberList.addElement(vMember);
    }

    /**
     * 
     * 
     * @param index
     * @param vMember
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addMember(
            final int index,
            final es.inteco.xbrl.pgc.transform.maps.Member vMember)
    throws java.lang.IndexOutOfBoundsException {
        this._memberList.add(index, vMember);
    }

    /**
     * Method enumerateMember.
     * 
     * @return an Enumeration over all
     * es.inteco.xbrl.pgc.transform.maps.Member elements
     */
    public java.util.Enumeration<? extends es.inteco.xbrl.pgc.transform.maps.Member> enumerateMember(
    ) {
        return this._memberList.elements();
    }

    /**
     * Returns the value of field 'domain'. The field 'domain' has
     * the following description: Nombre de la dimensiÃ³n
     * 
     * @return the value of field 'Domain'.
     */
    public java.lang.String getDomain(
    ) {
        return this._domain;
    }

    /**
     * Returns the value of field 'inputID'. The field 'inputID'
     * has the following description: El codigo EspaÃ±ol.
     * 
     * @return the value of field 'InputID'.
     */
    public java.lang.String getInputID(
    ) {
        return this._inputID;
    }

    /**
     * Method getMember.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the
     * es.inteco.xbrl.pgc.transform.maps.Member at the given index
     */
    public es.inteco.xbrl.pgc.transform.maps.Member getMember(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._memberList.size()) {
            throw new IndexOutOfBoundsException("getMember: Index value '" + index + "' not in range [0.." + (this._memberList.size() - 1) + "]");
        }
        
        return (es.inteco.xbrl.pgc.transform.maps.Member) _memberList.get(index);
    }

    /**
     * Method getMember.Returns the contents of the collection in
     * an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public es.inteco.xbrl.pgc.transform.maps.Member[] getMember(
    ) {
        es.inteco.xbrl.pgc.transform.maps.Member[] array = new es.inteco.xbrl.pgc.transform.maps.Member[0];
        return (es.inteco.xbrl.pgc.transform.maps.Member[]) this._memberList.toArray(array);
    }

    /**
     * Method getMemberCount.
     * 
     * @return the size of this collection
     */
    public int getMemberCount(
    ) {
        return this._memberList.size();
    }

    /**
     * Returns the value of field 'ns'.
     * 
     * @return the value of field 'Ns'.
     */
    public java.lang.String getNs(
    ) {
        return this._ns;
    }

    /**
     * Returns the value of field 'nsPrefix'.
     * 
     * @return the value of field 'NsPrefix'.
     */
    public java.lang.String getNsPrefix(
    ) {
        return this._nsPrefix;
    }

    /**
     * Returns the value of field 'order'.
     * 
     * @return the value of field 'Order'.
     */
    public java.math.BigDecimal getOrder(
    ) {
        return this._order;
    }

    /**
     * Returns the value of field 'outputID'. The field 'outputID'
     * has the following description: El nombre del tema XBRL
     * 
     * @return the value of field 'OutputID'.
     */
    public java.lang.String getOutputID(
    ) {
        return this._outputID;
    }

    /**
     * Returns the value of field 'parent'.
     * 
     * @return the value of field 'Parent'.
     */
    public java.lang.String getParent(
    ) {
        return this._parent;
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
    public void removeAllMember(
    ) {
        this._memberList.clear();
    }

    /**
     * Method removeMember.
     * 
     * @param vMember
     * @return true if the object was removed from the collection.
     */
    public boolean removeMember(
            final es.inteco.xbrl.pgc.transform.maps.Member vMember) {
        boolean removed = _memberList.remove(vMember);
        return removed;
    }

    /**
     * Method removeMemberAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public es.inteco.xbrl.pgc.transform.maps.Member removeMemberAt(
            final int index) {
        java.lang.Object obj = this._memberList.remove(index);
        return (es.inteco.xbrl.pgc.transform.maps.Member) obj;
    }

    /**
     * Sets the value of field 'domain'. The field 'domain' has the
     * following description: Nombre de la dimensiÃ³n
     * 
     * @param domain the value of field 'domain'.
     */
    public void setDomain(
            final java.lang.String domain) {
        this._domain = domain;
    }

    /**
     * Sets the value of field 'inputID'. The field 'inputID' has
     * the following description: El codigo EspaÃ±ol.
     * 
     * @param inputID the value of field 'inputID'.
     */
    public void setInputID(
            final java.lang.String inputID) {
        this._inputID = inputID;
    }

    /**
     * 
     * 
     * @param index
     * @param vMember
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setMember(
            final int index,
            final es.inteco.xbrl.pgc.transform.maps.Member vMember)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._memberList.size()) {
            throw new IndexOutOfBoundsException("setMember: Index value '" + index + "' not in range [0.." + (this._memberList.size() - 1) + "]");
        }
        
        this._memberList.set(index, vMember);
    }

    /**
     * 
     * 
     * @param vMemberArray
     */
    public void setMember(
            final es.inteco.xbrl.pgc.transform.maps.Member[] vMemberArray) {
        //-- copy array
        _memberList.clear();
        
        for (int i = 0; i < vMemberArray.length; i++) {
                this._memberList.add(vMemberArray[i]);
        }
    }

    /**
     * Sets the value of field 'ns'.
     * 
     * @param ns the value of field 'ns'.
     */
    public void setNs(
            final java.lang.String ns) {
        this._ns = ns;
    }

    /**
     * Sets the value of field 'nsPrefix'.
     * 
     * @param nsPrefix the value of field 'nsPrefix'.
     */
    public void setNsPrefix(
            final java.lang.String nsPrefix) {
        this._nsPrefix = nsPrefix;
    }

    /**
     * Sets the value of field 'order'.
     * 
     * @param order the value of field 'order'.
     */
    public void setOrder(
            final java.math.BigDecimal order) {
        this._order = order;
    }

    /**
     * Sets the value of field 'outputID'. The field 'outputID' has
     * the following description: El nombre del tema XBRL
     * 
     * @param outputID the value of field 'outputID'.
     */
    public void setOutputID(
            final java.lang.String outputID) {
        this._outputID = outputID;
    }

    /**
     * Sets the value of field 'parent'.
     * 
     * @param parent the value of field 'parent'.
     */
    public void setParent(
            final java.lang.String parent) {
        this._parent = parent;
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
     * es.inteco.xbrl.pgc.transform.maps.ConceptMap
     */
    public static es.inteco.xbrl.pgc.transform.maps.ConceptMap unmarshal(
            final java.io.Reader reader)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        return (es.inteco.xbrl.pgc.transform.maps.ConceptMap) org.exolab.castor.xml.Unmarshaller.unmarshal(es.inteco.xbrl.pgc.transform.maps.ConceptMap.class, reader);
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
