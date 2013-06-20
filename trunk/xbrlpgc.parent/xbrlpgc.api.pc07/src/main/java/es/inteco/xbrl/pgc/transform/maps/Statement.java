
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
 * $Id: Statement.java,v 1.1 2011/11/30 13:50:34 pablo Exp $
 */

package es.inteco.xbrl.pgc.transform.maps;

/**
*
* This class was automatically generated with 
* <a href="http://www.castor.org">Castor 1.3rc1</a>, using an XML
* Schema.
* $Id: Statement.java,v 1.1 2011/11/30 13:50:34 pablo Exp $
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
public class Statement implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _name.
     */
    private java.lang.String _name;

    /**
     * Field _mainSchemaURL.
     */
    private java.lang.String _mainSchemaURL;

    /**
     * Field _mainSchemaPrefix.
     */
    private java.lang.String _mainSchemaPrefix;

    /**
     * Field _mainSchemaNamespace.
     */
    private java.lang.String _mainSchemaNamespace;

    /**
     * Para uso en la creaciÃ³n de nombres de archivo
     */
    private java.lang.String _statementID;

    /**
     * Field _moduleName.
     */
    private java.lang.String _moduleName;

    /**
     * Field _moduleTitle.
     */
    private java.lang.String _moduleTitle;

    /**
     * Field _roleURI.
     */
    private java.lang.String _roleURI;

    /**
     * Field _roleID.
     */
    private java.lang.String _roleID;

    /**
     * Field _conceptMapList.
     */
    private java.util.Vector<es.inteco.xbrl.pgc.transform.maps.ConceptMap> _conceptMapList;


      //----------------/
     //- Constructors -/
    //----------------/

    public Statement() {
        super();
        this._conceptMapList = new java.util.Vector<es.inteco.xbrl.pgc.transform.maps.ConceptMap>();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vConceptMap
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addConceptMap(
            final es.inteco.xbrl.pgc.transform.maps.ConceptMap vConceptMap)
    throws java.lang.IndexOutOfBoundsException {
        this._conceptMapList.addElement(vConceptMap);
    }

    /**
     * 
     * 
     * @param index
     * @param vConceptMap
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addConceptMap(
            final int index,
            final es.inteco.xbrl.pgc.transform.maps.ConceptMap vConceptMap)
    throws java.lang.IndexOutOfBoundsException {
        this._conceptMapList.add(index, vConceptMap);
    }

    /**
     * Method enumerateConceptMap.
     * 
     * @return an Enumeration over all
     * es.inteco.xbrl.pgc.transform.maps.ConceptMap elements
     */
    public java.util.Enumeration<? extends es.inteco.xbrl.pgc.transform.maps.ConceptMap> enumerateConceptMap(
    ) {
        return this._conceptMapList.elements();
    }

    /**
     * Method getConceptMap.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the
     * es.inteco.xbrl.pgc.transform.maps.ConceptMap at the given
     * index
     */
    public es.inteco.xbrl.pgc.transform.maps.ConceptMap getConceptMap(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._conceptMapList.size()) {
            throw new IndexOutOfBoundsException("getConceptMap: Index value '" + index + "' not in range [0.." + (this._conceptMapList.size() - 1) + "]");
        }
        
        return (es.inteco.xbrl.pgc.transform.maps.ConceptMap) _conceptMapList.get(index);
    }

    /**
     * Method getConceptMap.Returns the contents of the collection
     * in an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public es.inteco.xbrl.pgc.transform.maps.ConceptMap[] getConceptMap(
    ) {
        es.inteco.xbrl.pgc.transform.maps.ConceptMap[] array = new es.inteco.xbrl.pgc.transform.maps.ConceptMap[0];
        return (es.inteco.xbrl.pgc.transform.maps.ConceptMap[]) this._conceptMapList.toArray(array);
    }

    /**
     * Method getConceptMapCount.
     * 
     * @return the size of this collection
     */
    public int getConceptMapCount(
    ) {
        return this._conceptMapList.size();
    }

    /**
     * Returns the value of field 'mainSchemaNamespace'.
     * 
     * @return the value of field 'MainSchemaNamespace'.
     */
    public java.lang.String getMainSchemaNamespace(
    ) {
        return this._mainSchemaNamespace;
    }

    /**
     * Returns the value of field 'mainSchemaPrefix'.
     * 
     * @return the value of field 'MainSchemaPrefix'.
     */
    public java.lang.String getMainSchemaPrefix(
    ) {
        return this._mainSchemaPrefix;
    }

    /**
     * Returns the value of field 'mainSchemaURL'.
     * 
     * @return the value of field 'MainSchemaURL'.
     */
    public java.lang.String getMainSchemaURL(
    ) {
        return this._mainSchemaURL;
    }

    /**
     * Returns the value of field 'moduleName'.
     * 
     * @return the value of field 'ModuleName'.
     */
    public java.lang.String getModuleName(
    ) {
        return this._moduleName;
    }

    /**
     * Returns the value of field 'moduleTitle'.
     * 
     * @return the value of field 'ModuleTitle'.
     */
    public java.lang.String getModuleTitle(
    ) {
        return this._moduleTitle;
    }

    /**
     * Returns the value of field 'name'.
     * 
     * @return the value of field 'Name'.
     */
    public java.lang.String getName(
    ) {
        return this._name;
    }

    /**
     * Returns the value of field 'roleID'.
     * 
     * @return the value of field 'RoleID'.
     */
    public java.lang.String getRoleID(
    ) {
        return this._roleID;
    }

    /**
     * Returns the value of field 'roleURI'.
     * 
     * @return the value of field 'RoleURI'.
     */
    public java.lang.String getRoleURI(
    ) {
        return this._roleURI;
    }

    /**
     * Returns the value of field 'statementID'. The field
     * 'statementID' has the following description: Para uso en la
     * creaciÃ³n de nombres de archivo
     * 
     * @return the value of field 'StatementID'.
     */
    public java.lang.String getStatementID(
    ) {
        return this._statementID;
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
    public void removeAllConceptMap(
    ) {
        this._conceptMapList.clear();
    }

    /**
     * Method removeConceptMap.
     * 
     * @param vConceptMap
     * @return true if the object was removed from the collection.
     */
    public boolean removeConceptMap(
            final es.inteco.xbrl.pgc.transform.maps.ConceptMap vConceptMap) {
        boolean removed = _conceptMapList.remove(vConceptMap);
        return removed;
    }

    /**
     * Method removeConceptMapAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public es.inteco.xbrl.pgc.transform.maps.ConceptMap removeConceptMapAt(
            final int index) {
        java.lang.Object obj = this._conceptMapList.remove(index);
        return (es.inteco.xbrl.pgc.transform.maps.ConceptMap) obj;
    }

    /**
     * 
     * 
     * @param index
     * @param vConceptMap
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setConceptMap(
            final int index,
            final es.inteco.xbrl.pgc.transform.maps.ConceptMap vConceptMap)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._conceptMapList.size()) {
            throw new IndexOutOfBoundsException("setConceptMap: Index value '" + index + "' not in range [0.." + (this._conceptMapList.size() - 1) + "]");
        }
        
        this._conceptMapList.set(index, vConceptMap);
    }

    /**
     * 
     * 
     * @param vConceptMapArray
     */
    public void setConceptMap(
            final es.inteco.xbrl.pgc.transform.maps.ConceptMap[] vConceptMapArray) {
        //-- copy array
        _conceptMapList.clear();
        
        for (int i = 0; i < vConceptMapArray.length; i++) {
                this._conceptMapList.add(vConceptMapArray[i]);
        }
    }

    /**
     * Sets the value of field 'mainSchemaNamespace'.
     * 
     * @param mainSchemaNamespace the value of field
     * 'mainSchemaNamespace'.
     */
    public void setMainSchemaNamespace(
            final java.lang.String mainSchemaNamespace) {
        this._mainSchemaNamespace = mainSchemaNamespace;
    }

    /**
     * Sets the value of field 'mainSchemaPrefix'.
     * 
     * @param mainSchemaPrefix the value of field 'mainSchemaPrefix'
     */
    public void setMainSchemaPrefix(
            final java.lang.String mainSchemaPrefix) {
        this._mainSchemaPrefix = mainSchemaPrefix;
    }

    /**
     * Sets the value of field 'mainSchemaURL'.
     * 
     * @param mainSchemaURL the value of field 'mainSchemaURL'.
     */
    public void setMainSchemaURL(
            final java.lang.String mainSchemaURL) {
        this._mainSchemaURL = mainSchemaURL;
    }

    /**
     * Sets the value of field 'moduleName'.
     * 
     * @param moduleName the value of field 'moduleName'.
     */
    public void setModuleName(
            final java.lang.String moduleName) {
        this._moduleName = moduleName;
    }

    /**
     * Sets the value of field 'moduleTitle'.
     * 
     * @param moduleTitle the value of field 'moduleTitle'.
     */
    public void setModuleTitle(
            final java.lang.String moduleTitle) {
        this._moduleTitle = moduleTitle;
    }

    /**
     * Sets the value of field 'name'.
     * 
     * @param name the value of field 'name'.
     */
    public void setName(
            final java.lang.String name) {
        this._name = name;
    }

    /**
     * Sets the value of field 'roleID'.
     * 
     * @param roleID the value of field 'roleID'.
     */
    public void setRoleID(
            final java.lang.String roleID) {
        this._roleID = roleID;
    }

    /**
     * Sets the value of field 'roleURI'.
     * 
     * @param roleURI the value of field 'roleURI'.
     */
    public void setRoleURI(
            final java.lang.String roleURI) {
        this._roleURI = roleURI;
    }

    /**
     * Sets the value of field 'statementID'. The field
     * 'statementID' has the following description: Para uso en la
     * creaciÃ³n de nombres de archivo
     * 
     * @param statementID the value of field 'statementID'.
     */
    public void setStatementID(
            final java.lang.String statementID) {
        this._statementID = statementID;
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
     * es.inteco.xbrl.pgc.transform.maps.Statement
     */
    public static es.inteco.xbrl.pgc.transform.maps.Statement unmarshal(
            final java.io.Reader reader)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        return (es.inteco.xbrl.pgc.transform.maps.Statement) org.exolab.castor.xml.Unmarshaller.unmarshal(es.inteco.xbrl.pgc.transform.maps.Statement.class, reader);
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
