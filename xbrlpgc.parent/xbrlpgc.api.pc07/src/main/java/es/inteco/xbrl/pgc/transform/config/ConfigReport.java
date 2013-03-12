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
 * $Id: ConfigReport.java,v 1.1 2011/11/30 13:50:24 pablo Exp $
 */

package es.inteco.xbrl.pgc.transform.config;

/**
*
* This class was automatically generated with 
* <a href="http://www.castor.org">Castor 1.3rc1</a>, using an XML
* Schema.
* $Id: ConfigReport.java,v 1.1 2011/11/30 13:50:24 pablo Exp $
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
public class ConfigReport implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _id.
     */
    private java.lang.String _id;

    /**
     * Field _nsItemList.
     */
    private java.util.Vector<es.inteco.xbrl.pgc.transform.config.NsItem> _nsItemList;

    /**
     * Field _unitList.
     */
    private java.util.Vector<es.inteco.xbrl.pgc.transform.config.Unit> _unitList;

    /**
     * Field _schemaRef.
     */
    private es.inteco.xbrl.pgc.transform.config.SchemaRef _schemaRef;

    /**
     * Field _configModuleList.
     */
    private java.util.Vector<es.inteco.xbrl.pgc.transform.config.ConfigModule> _configModuleList;


      //----------------/
     //- Constructors -/
    //----------------/

    public ConfigReport() {
        super();
        this._nsItemList = new java.util.Vector<es.inteco.xbrl.pgc.transform.config.NsItem>();
        this._unitList = new java.util.Vector<es.inteco.xbrl.pgc.transform.config.Unit>();
        this._configModuleList = new java.util.Vector<es.inteco.xbrl.pgc.transform.config.ConfigModule>();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vConfigModule
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addConfigModule(
            final es.inteco.xbrl.pgc.transform.config.ConfigModule vConfigModule)
    throws java.lang.IndexOutOfBoundsException {
        this._configModuleList.addElement(vConfigModule);
    }

    /**
     * 
     * 
     * @param index
     * @param vConfigModule
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addConfigModule(
            final int index,
            final es.inteco.xbrl.pgc.transform.config.ConfigModule vConfigModule)
    throws java.lang.IndexOutOfBoundsException {
        this._configModuleList.add(index, vConfigModule);
    }

    /**
     * 
     * 
     * @param vNsItem
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addNsItem(
            final es.inteco.xbrl.pgc.transform.config.NsItem vNsItem)
    throws java.lang.IndexOutOfBoundsException {
        this._nsItemList.addElement(vNsItem);
    }

    /**
     * 
     * 
     * @param index
     * @param vNsItem
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addNsItem(
            final int index,
            final es.inteco.xbrl.pgc.transform.config.NsItem vNsItem)
    throws java.lang.IndexOutOfBoundsException {
        this._nsItemList.add(index, vNsItem);
    }

    /**
     * 
     * 
     * @param vUnit
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addUnit(
            final es.inteco.xbrl.pgc.transform.config.Unit vUnit)
    throws java.lang.IndexOutOfBoundsException {
        this._unitList.addElement(vUnit);
    }

    /**
     * 
     * 
     * @param index
     * @param vUnit
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addUnit(
            final int index,
            final es.inteco.xbrl.pgc.transform.config.Unit vUnit)
    throws java.lang.IndexOutOfBoundsException {
        this._unitList.add(index, vUnit);
    }

    /**
     * Method enumerateConfigModule.
     * 
     * @return an Enumeration over all
     * es.inteco.xbrl.pgc.transform.config.ConfigModule elements
     */
    public java.util.Enumeration<? extends es.inteco.xbrl.pgc.transform.config.ConfigModule> enumerateConfigModule(
    ) {
        return this._configModuleList.elements();
    }

    /**
     * Method enumerateNsItem.
     * 
     * @return an Enumeration over all
     * es.inteco.xbrl.pgc.transform.config.NsItem elements
     */
    public java.util.Enumeration<? extends es.inteco.xbrl.pgc.transform.config.NsItem> enumerateNsItem(
    ) {
        return this._nsItemList.elements();
    }

    /**
     * Method enumerateUnit.
     * 
     * @return an Enumeration over all
     * es.inteco.xbrl.pgc.transform.config.Unit elements
     */
    public java.util.Enumeration<? extends es.inteco.xbrl.pgc.transform.config.Unit> enumerateUnit(
    ) {
        return this._unitList.elements();
    }

    /**
     * Method getConfigModule.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the
     * es.inteco.xbrl.pgc.transform.config.ConfigModule at the
     * given index
     */
    public es.inteco.xbrl.pgc.transform.config.ConfigModule getConfigModule(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._configModuleList.size()) {
            throw new IndexOutOfBoundsException("getConfigModule: Index value '" + index + "' not in range [0.." + (this._configModuleList.size() - 1) + "]");
        }
        
        return (es.inteco.xbrl.pgc.transform.config.ConfigModule) _configModuleList.get(index);
    }

    /**
     * Method getConfigModule.Returns the contents of the
     * collection in an Array.  <p>Note:  Just in case the
     * collection contents are changing in another thread, we pass
     * a 0-length Array of the correct type into the API call. 
     * This way we <i>know</i> that the Array returned is of
     * exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public es.inteco.xbrl.pgc.transform.config.ConfigModule[] getConfigModule(
    ) {
        es.inteco.xbrl.pgc.transform.config.ConfigModule[] array = new es.inteco.xbrl.pgc.transform.config.ConfigModule[0];
        return (es.inteco.xbrl.pgc.transform.config.ConfigModule[]) this._configModuleList.toArray(array);
    }

    /**
     * Method getConfigModuleCount.
     * 
     * @return the size of this collection
     */
    public int getConfigModuleCount(
    ) {
        return this._configModuleList.size();
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
     * Method getNsItem.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the
     * es.inteco.xbrl.pgc.transform.config.NsItem at the given index
     */
    public es.inteco.xbrl.pgc.transform.config.NsItem getNsItem(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._nsItemList.size()) {
            throw new IndexOutOfBoundsException("getNsItem: Index value '" + index + "' not in range [0.." + (this._nsItemList.size() - 1) + "]");
        }
        
        return (es.inteco.xbrl.pgc.transform.config.NsItem) _nsItemList.get(index);
    }

    /**
     * Method getNsItem.Returns the contents of the collection in
     * an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public es.inteco.xbrl.pgc.transform.config.NsItem[] getNsItem(
    ) {
        es.inteco.xbrl.pgc.transform.config.NsItem[] array = new es.inteco.xbrl.pgc.transform.config.NsItem[0];
        return (es.inteco.xbrl.pgc.transform.config.NsItem[]) this._nsItemList.toArray(array);
    }

    /**
     * Method getNsItemCount.
     * 
     * @return the size of this collection
     */
    public int getNsItemCount(
    ) {
        return this._nsItemList.size();
    }

    /**
     * Returns the value of field 'schemaRef'.
     * 
     * @return the value of field 'SchemaRef'.
     */
    public es.inteco.xbrl.pgc.transform.config.SchemaRef getSchemaRef(
    ) {
        return this._schemaRef;
    }

    /**
     * Method getUnit.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the
     * es.inteco.xbrl.pgc.transform.config.Unit at the given index
     */
    public es.inteco.xbrl.pgc.transform.config.Unit getUnit(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._unitList.size()) {
            throw new IndexOutOfBoundsException("getUnit: Index value '" + index + "' not in range [0.." + (this._unitList.size() - 1) + "]");
        }
        
        return (es.inteco.xbrl.pgc.transform.config.Unit) _unitList.get(index);
    }

    /**
     * Method getUnit.Returns the contents of the collection in an
     * Array.  <p>Note:  Just in case the collection contents are
     * changing in another thread, we pass a 0-length Array of the
     * correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public es.inteco.xbrl.pgc.transform.config.Unit[] getUnit(
    ) {
        es.inteco.xbrl.pgc.transform.config.Unit[] array = new es.inteco.xbrl.pgc.transform.config.Unit[0];
        return (es.inteco.xbrl.pgc.transform.config.Unit[]) this._unitList.toArray(array);
    }

    /**
     * Method getUnitCount.
     * 
     * @return the size of this collection
     */
    public int getUnitCount(
    ) {
        return this._unitList.size();
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
    public void removeAllConfigModule(
    ) {
        this._configModuleList.clear();
    }

    /**
     */
    public void removeAllNsItem(
    ) {
        this._nsItemList.clear();
    }

    /**
     */
    public void removeAllUnit(
    ) {
        this._unitList.clear();
    }

    /**
     * Method removeConfigModule.
     * 
     * @param vConfigModule
     * @return true if the object was removed from the collection.
     */
    public boolean removeConfigModule(
            final es.inteco.xbrl.pgc.transform.config.ConfigModule vConfigModule) {
        boolean removed = _configModuleList.remove(vConfigModule);
        return removed;
    }

    /**
     * Method removeConfigModuleAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public es.inteco.xbrl.pgc.transform.config.ConfigModule removeConfigModuleAt(
            final int index) {
        java.lang.Object obj = this._configModuleList.remove(index);
        return (es.inteco.xbrl.pgc.transform.config.ConfigModule) obj;
    }

    /**
     * Method removeNsItem.
     * 
     * @param vNsItem
     * @return true if the object was removed from the collection.
     */
    public boolean removeNsItem(
            final es.inteco.xbrl.pgc.transform.config.NsItem vNsItem) {
        boolean removed = _nsItemList.remove(vNsItem);
        return removed;
    }

    /**
     * Method removeNsItemAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public es.inteco.xbrl.pgc.transform.config.NsItem removeNsItemAt(
            final int index) {
        java.lang.Object obj = this._nsItemList.remove(index);
        return (es.inteco.xbrl.pgc.transform.config.NsItem) obj;
    }

    /**
     * Method removeUnit.
     * 
     * @param vUnit
     * @return true if the object was removed from the collection.
     */
    public boolean removeUnit(
            final es.inteco.xbrl.pgc.transform.config.Unit vUnit) {
        boolean removed = _unitList.remove(vUnit);
        return removed;
    }

    /**
     * Method removeUnitAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public es.inteco.xbrl.pgc.transform.config.Unit removeUnitAt(
            final int index) {
        java.lang.Object obj = this._unitList.remove(index);
        return (es.inteco.xbrl.pgc.transform.config.Unit) obj;
    }

    /**
     * 
     * 
     * @param index
     * @param vConfigModule
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setConfigModule(
            final int index,
            final es.inteco.xbrl.pgc.transform.config.ConfigModule vConfigModule)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._configModuleList.size()) {
            throw new IndexOutOfBoundsException("setConfigModule: Index value '" + index + "' not in range [0.." + (this._configModuleList.size() - 1) + "]");
        }
        
        this._configModuleList.set(index, vConfigModule);
    }

    /**
     * 
     * 
     * @param vConfigModuleArray
     */
    public void setConfigModule(
            final es.inteco.xbrl.pgc.transform.config.ConfigModule[] vConfigModuleArray) {
        //-- copy array
        _configModuleList.clear();
        
        for (int i = 0; i < vConfigModuleArray.length; i++) {
                this._configModuleList.add(vConfigModuleArray[i]);
        }
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
     * @param vNsItem
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setNsItem(
            final int index,
            final es.inteco.xbrl.pgc.transform.config.NsItem vNsItem)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._nsItemList.size()) {
            throw new IndexOutOfBoundsException("setNsItem: Index value '" + index + "' not in range [0.." + (this._nsItemList.size() - 1) + "]");
        }
        
        this._nsItemList.set(index, vNsItem);
    }

    /**
     * 
     * 
     * @param vNsItemArray
     */
    public void setNsItem(
            final es.inteco.xbrl.pgc.transform.config.NsItem[] vNsItemArray) {
        //-- copy array
        _nsItemList.clear();
        
        for (int i = 0; i < vNsItemArray.length; i++) {
                this._nsItemList.add(vNsItemArray[i]);
        }
    }

    /**
     * Sets the value of field 'schemaRef'.
     * 
     * @param schemaRef the value of field 'schemaRef'.
     */
    public void setSchemaRef(
            final es.inteco.xbrl.pgc.transform.config.SchemaRef schemaRef) {
        this._schemaRef = schemaRef;
    }

    /**
     * 
     * 
     * @param index
     * @param vUnit
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setUnit(
            final int index,
            final es.inteco.xbrl.pgc.transform.config.Unit vUnit)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._unitList.size()) {
            throw new IndexOutOfBoundsException("setUnit: Index value '" + index + "' not in range [0.." + (this._unitList.size() - 1) + "]");
        }
        
        this._unitList.set(index, vUnit);
    }

    /**
     * 
     * 
     * @param vUnitArray
     */
    public void setUnit(
            final es.inteco.xbrl.pgc.transform.config.Unit[] vUnitArray) {
        //-- copy array
        _unitList.clear();
        
        for (int i = 0; i < vUnitArray.length; i++) {
                this._unitList.add(vUnitArray[i]);
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
     * es.inteco.xbrl.pgc.transform.config.ConfigReport
     */
    public static es.inteco.xbrl.pgc.transform.config.ConfigReport unmarshal(
            final java.io.Reader reader)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        return (es.inteco.xbrl.pgc.transform.config.ConfigReport) org.exolab.castor.xml.Unmarshaller.unmarshal(es.inteco.xbrl.pgc.transform.config.ConfigReport.class, reader);
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
