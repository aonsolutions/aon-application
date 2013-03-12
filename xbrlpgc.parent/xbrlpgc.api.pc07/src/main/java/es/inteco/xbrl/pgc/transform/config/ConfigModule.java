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
 * $Id: ConfigModule.java,v 1.1 2011/11/30 13:50:23 pablo Exp $
 */

package es.inteco.xbrl.pgc.transform.config;

/**
*
* This class was automatically generated with 
* <a href="http://www.castor.org">Castor 1.3rc1</a>, using an XML
* Schema.
* $Id: ConfigModule.java,v 1.1 2011/11/30 13:50:23 pablo Exp $
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
public class ConfigModule implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _presentationRole.
     */
    private java.lang.String _presentationRole;

    /**
     * Field _labelRole.
     */
    private java.lang.String _labelRole;

    /**
     * Field _definitionRole.
     */
    private java.lang.String _definitionRole;

    /**
     * Field _title.
     */
    private java.lang.String _title;

    /**
     * Field _id.
     */
    private java.lang.String _id;

    /**
     * Field _fileMap.
     */
    private java.lang.String _fileMap;

    /**
     * Field _tuple.
     */
    private boolean _tuple;

    /**
     * keeps track of state for field: _tuple
     */
    private boolean _has_tuple;

    /**
     * Field _definitionRoles.
     */
    private es.inteco.xbrl.pgc.transform.config.DefinitionRoles _definitionRoles;

    /**
     * Field _labelTypeRoles.
     */
    private es.inteco.xbrl.pgc.transform.config.LabelTypeRoles _labelTypeRoles;

    /**
     * Field _fileList.
     */
    private java.util.Vector<es.inteco.xbrl.pgc.transform.config.File> _fileList;


      //----------------/
     //- Constructors -/
    //----------------/

    public ConfigModule() {
        super();
        this._fileList = new java.util.Vector<es.inteco.xbrl.pgc.transform.config.File>();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vFile
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addFile(
            final es.inteco.xbrl.pgc.transform.config.File vFile)
    throws java.lang.IndexOutOfBoundsException {
        this._fileList.addElement(vFile);
    }

    /**
     * 
     * 
     * @param index
     * @param vFile
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addFile(
            final int index,
            final es.inteco.xbrl.pgc.transform.config.File vFile)
    throws java.lang.IndexOutOfBoundsException {
        this._fileList.add(index, vFile);
    }

    /**
     */
    public void deleteTuple(
    ) {
        this._has_tuple= false;
    }

    /**
     * Method enumerateFile.
     * 
     * @return an Enumeration over all
     * es.inteco.xbrl.pgc.transform.config.File elements
     */
    public java.util.Enumeration<? extends es.inteco.xbrl.pgc.transform.config.File> enumerateFile(
    ) {
        return this._fileList.elements();
    }

    /**
     * Returns the value of field 'definitionRole'.
     * 
     * @return the value of field 'DefinitionRole'.
     */
    public java.lang.String getDefinitionRole(
    ) {
        return this._definitionRole;
    }

    /**
     * Returns the value of field 'definitionRoles'.
     * 
     * @return the value of field 'DefinitionRoles'.
     */
    public es.inteco.xbrl.pgc.transform.config.DefinitionRoles getDefinitionRoles(
    ) {
        return this._definitionRoles;
    }

    /**
     * Method getFile.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the
     * es.inteco.xbrl.pgc.transform.config.File at the given index
     */
    public es.inteco.xbrl.pgc.transform.config.File getFile(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._fileList.size()) {
            throw new IndexOutOfBoundsException("getFile: Index value '" + index + "' not in range [0.." + (this._fileList.size() - 1) + "]");
        }
        
        return (es.inteco.xbrl.pgc.transform.config.File) _fileList.get(index);
    }

    /**
     * Method getFile.Returns the contents of the collection in an
     * Array.  <p>Note:  Just in case the collection contents are
     * changing in another thread, we pass a 0-length Array of the
     * correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public es.inteco.xbrl.pgc.transform.config.File[] getFile(
    ) {
        es.inteco.xbrl.pgc.transform.config.File[] array = new es.inteco.xbrl.pgc.transform.config.File[0];
        return (es.inteco.xbrl.pgc.transform.config.File[]) this._fileList.toArray(array);
    }

    /**
     * Method getFileCount.
     * 
     * @return the size of this collection
     */
    public int getFileCount(
    ) {
        return this._fileList.size();
    }

    /**
     * Returns the value of field 'fileMap'.
     * 
     * @return the value of field 'FileMap'.
     */
    public java.lang.String getFileMap(
    ) {
        return this._fileMap;
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
     * Returns the value of field 'labelRole'.
     * 
     * @return the value of field 'LabelRole'.
     */
    public java.lang.String getLabelRole(
    ) {
        return this._labelRole;
    }

    /**
     * Returns the value of field 'labelTypeRoles'.
     * 
     * @return the value of field 'LabelTypeRoles'.
     */
    public es.inteco.xbrl.pgc.transform.config.LabelTypeRoles getLabelTypeRoles(
    ) {
        return this._labelTypeRoles;
    }

    /**
     * Returns the value of field 'presentationRole'.
     * 
     * @return the value of field 'PresentationRole'.
     */
    public java.lang.String getPresentationRole(
    ) {
        return this._presentationRole;
    }

    /**
     * Returns the value of field 'title'.
     * 
     * @return the value of field 'Title'.
     */
    public java.lang.String getTitle(
    ) {
        return this._title;
    }

    /**
     * Returns the value of field 'tuple'.
     * 
     * @return the value of field 'Tuple'.
     */
    public boolean getTuple(
    ) {
        return this._tuple;
    }

    /**
     * Method hasTuple.
     * 
     * @return true if at least one Tuple has been added
     */
    public boolean hasTuple(
    ) {
        return this._has_tuple;
    }

    /**
     * Returns the value of field 'tuple'.
     * 
     * @return the value of field 'Tuple'.
     */
    public boolean isTuple(
    ) {
        return this._tuple;
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
    public void removeAllFile(
    ) {
        this._fileList.clear();
    }

    /**
     * Method removeFile.
     * 
     * @param vFile
     * @return true if the object was removed from the collection.
     */
    public boolean removeFile(
            final es.inteco.xbrl.pgc.transform.config.File vFile) {
        boolean removed = _fileList.remove(vFile);
        return removed;
    }

    /**
     * Method removeFileAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public es.inteco.xbrl.pgc.transform.config.File removeFileAt(
            final int index) {
        java.lang.Object obj = this._fileList.remove(index);
        return (es.inteco.xbrl.pgc.transform.config.File) obj;
    }

    /**
     * Sets the value of field 'definitionRole'.
     * 
     * @param definitionRole the value of field 'definitionRole'.
     */
    public void setDefinitionRole(
            final java.lang.String definitionRole) {
        this._definitionRole = definitionRole;
    }

    /**
     * Sets the value of field 'definitionRoles'.
     * 
     * @param definitionRoles the value of field 'definitionRoles'.
     */
    public void setDefinitionRoles(
            final es.inteco.xbrl.pgc.transform.config.DefinitionRoles definitionRoles) {
        this._definitionRoles = definitionRoles;
    }

    /**
     * 
     * 
     * @param index
     * @param vFile
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setFile(
            final int index,
            final es.inteco.xbrl.pgc.transform.config.File vFile)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._fileList.size()) {
            throw new IndexOutOfBoundsException("setFile: Index value '" + index + "' not in range [0.." + (this._fileList.size() - 1) + "]");
        }
        
        this._fileList.set(index, vFile);
    }

    /**
     * 
     * 
     * @param vFileArray
     */
    public void setFile(
            final es.inteco.xbrl.pgc.transform.config.File[] vFileArray) {
        //-- copy array
        _fileList.clear();
        
        for (int i = 0; i < vFileArray.length; i++) {
                this._fileList.add(vFileArray[i]);
        }
    }

    /**
     * Sets the value of field 'fileMap'.
     * 
     * @param fileMap the value of field 'fileMap'.
     */
    public void setFileMap(
            final java.lang.String fileMap) {
        this._fileMap = fileMap;
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
     * Sets the value of field 'labelRole'.
     * 
     * @param labelRole the value of field 'labelRole'.
     */
    public void setLabelRole(
            final java.lang.String labelRole) {
        this._labelRole = labelRole;
    }

    /**
     * Sets the value of field 'labelTypeRoles'.
     * 
     * @param labelTypeRoles the value of field 'labelTypeRoles'.
     */
    public void setLabelTypeRoles(
            final es.inteco.xbrl.pgc.transform.config.LabelTypeRoles labelTypeRoles) {
        this._labelTypeRoles = labelTypeRoles;
    }

    /**
     * Sets the value of field 'presentationRole'.
     * 
     * @param presentationRole the value of field 'presentationRole'
     */
    public void setPresentationRole(
            final java.lang.String presentationRole) {
        this._presentationRole = presentationRole;
    }

    /**
     * Sets the value of field 'title'.
     * 
     * @param title the value of field 'title'.
     */
    public void setTitle(
            final java.lang.String title) {
        this._title = title;
    }

    /**
     * Sets the value of field 'tuple'.
     * 
     * @param tuple the value of field 'tuple'.
     */
    public void setTuple(
            final boolean tuple) {
        this._tuple = tuple;
        this._has_tuple = true;
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
     * es.inteco.xbrl.pgc.transform.config.ConfigModule
     */
    public static es.inteco.xbrl.pgc.transform.config.ConfigModule unmarshal(
            final java.io.Reader reader)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        return (es.inteco.xbrl.pgc.transform.config.ConfigModule) org.exolab.castor.xml.Unmarshaller.unmarshal(es.inteco.xbrl.pgc.transform.config.ConfigModule.class, reader);
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
