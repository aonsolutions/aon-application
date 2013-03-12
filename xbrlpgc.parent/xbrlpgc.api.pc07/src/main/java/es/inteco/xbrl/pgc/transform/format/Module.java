
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
 * $Id: Module.java,v 1.1 2011/11/30 13:50:27 pablo Exp $
 */

package es.inteco.xbrl.pgc.transform.format;

/**
*
* This class was automatically generated with 
* <a href="http://www.castor.org">Castor 1.3rc1</a>, using an XML
* Schema.
* $Id: Module.java,v 1.1 2011/11/30 13:50:27 pablo Exp $
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
* @version 1.1, 27/01/2009
* @author difusioncalidad@inteco.es
*
*/
@SuppressWarnings("serial")
public class Module implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _id.
     */
    private java.lang.String _id;

    /**
     * Field _reportingDateStart.
     */
    private org.exolab.castor.types.Date _reportingDateStart;

    /**
     * Field _reportingDateEnd.
     */
    private org.exolab.castor.types.Date _reportingDateEnd;

    /**
     * Field _baseUnit.
     */
    private es.inteco.xbrl.pgc.transform.format.types.Unittype _baseUnit = es.inteco.xbrl.pgc.transform.format.types.Unittype.fromValue("string");

    /**
     * Field _baseDecimals.
     */
    private java.lang.String _baseDecimals = "0";

    /**
     * Field _itemList.
     */
    private java.util.Vector<es.inteco.xbrl.pgc.transform.format.Item> _itemList;

    /**
     * Field _recordList.
     */
    private java.util.Vector<es.inteco.xbrl.pgc.transform.format.Record> _recordList;

    /**
     * Field _tableList.
     */
    private java.util.Vector<es.inteco.xbrl.pgc.transform.format.Table> _tableList;


      //----------------/
     //- Constructors -/
    //----------------/

    public Module() {
        super();
        setBaseUnit(es.inteco.xbrl.pgc.transform.format.types.Unittype.fromValue("string"));
        setBaseDecimals("0");
        this._itemList = new java.util.Vector<es.inteco.xbrl.pgc.transform.format.Item>();
        this._recordList = new java.util.Vector<es.inteco.xbrl.pgc.transform.format.Record>();
        this._tableList = new java.util.Vector<es.inteco.xbrl.pgc.transform.format.Table>();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vItem
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addItem(
            final es.inteco.xbrl.pgc.transform.format.Item vItem)
    throws java.lang.IndexOutOfBoundsException {
        this._itemList.addElement(vItem);
    }

    /**
     * 
     * 
     * @param index
     * @param vItem
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addItem(
            final int index,
            final es.inteco.xbrl.pgc.transform.format.Item vItem)
    throws java.lang.IndexOutOfBoundsException {
        this._itemList.add(index, vItem);
    }

    /**
     * 
     * 
     * @param vRecord
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addRecord(
            final es.inteco.xbrl.pgc.transform.format.Record vRecord)
    throws java.lang.IndexOutOfBoundsException {
        this._recordList.addElement(vRecord);
    }

    /**
     * 
     * 
     * @param index
     * @param vRecord
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addRecord(
            final int index,
            final es.inteco.xbrl.pgc.transform.format.Record vRecord)
    throws java.lang.IndexOutOfBoundsException {
        this._recordList.add(index, vRecord);
    }

    /**
     * 
     * 
     * @param vTable
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addTable(
            final es.inteco.xbrl.pgc.transform.format.Table vTable)
    throws java.lang.IndexOutOfBoundsException {
        this._tableList.addElement(vTable);
    }

    /**
     * 
     * 
     * @param index
     * @param vTable
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addTable(
            final int index,
            final es.inteco.xbrl.pgc.transform.format.Table vTable)
    throws java.lang.IndexOutOfBoundsException {
        this._tableList.add(index, vTable);
    }

    /**
     * Method enumerateItem.
     * 
     * @return an Enumeration over all
     * es.inteco.xbrl.pgc.transform.format.Item elements
     */
    public java.util.Enumeration<? extends es.inteco.xbrl.pgc.transform.format.Item> enumerateItem(
    ) {
        return this._itemList.elements();
    }

    /**
     * Method enumerateRecord.
     * 
     * @return an Enumeration over all
     * es.inteco.xbrl.pgc.transform.format.Record elements
     */
    public java.util.Enumeration<? extends es.inteco.xbrl.pgc.transform.format.Record> enumerateRecord(
    ) {
        return this._recordList.elements();
    }

    /**
     * Method enumerateTable.
     * 
     * @return an Enumeration over all
     * es.inteco.xbrl.pgc.transform.format.Table elements
     */
    public java.util.Enumeration<? extends es.inteco.xbrl.pgc.transform.format.Table> enumerateTable(
    ) {
        return this._tableList.elements();
    }

    /**
     * Returns the value of field 'baseDecimals'.
     * 
     * @return the value of field 'BaseDecimals'.
     */
    public java.lang.String getBaseDecimals(
    ) {
        return this._baseDecimals;
    }

    /**
     * Returns the value of field 'baseUnit'.
     * 
     * @return the value of field 'BaseUnit'.
     */
    public es.inteco.xbrl.pgc.transform.format.types.Unittype getBaseUnit(
    ) {
        return this._baseUnit;
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
     * Method getItem.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the
     * es.inteco.xbrl.pgc.transform.format.Item at the given index
     */
    public es.inteco.xbrl.pgc.transform.format.Item getItem(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._itemList.size()) {
            throw new IndexOutOfBoundsException("getItem: Index value '" + index + "' not in range [0.." + (this._itemList.size() - 1) + "]");
        }
        
        return (es.inteco.xbrl.pgc.transform.format.Item) _itemList.get(index);
    }

    /**
     * Method getItem.Returns the contents of the collection in an
     * Array.  <p>Note:  Just in case the collection contents are
     * changing in another thread, we pass a 0-length Array of the
     * correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public es.inteco.xbrl.pgc.transform.format.Item[] getItem(
    ) {
        es.inteco.xbrl.pgc.transform.format.Item[] array = new es.inteco.xbrl.pgc.transform.format.Item[0];
        return (es.inteco.xbrl.pgc.transform.format.Item[]) this._itemList.toArray(array);
    }

    /**
     * Method getItemCount.
     * 
     * @return the size of this collection
     */
    public int getItemCount(
    ) {
        return this._itemList.size();
    }

    /**
     * Method getRecord.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the
     * es.inteco.xbrl.pgc.transform.format.Record at the given index
     */
    public es.inteco.xbrl.pgc.transform.format.Record getRecord(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._recordList.size()) {
            throw new IndexOutOfBoundsException("getRecord: Index value '" + index + "' not in range [0.." + (this._recordList.size() - 1) + "]");
        }
        
        return (es.inteco.xbrl.pgc.transform.format.Record) _recordList.get(index);
    }

    /**
     * Method getRecord.Returns the contents of the collection in
     * an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public es.inteco.xbrl.pgc.transform.format.Record[] getRecord(
    ) {
        es.inteco.xbrl.pgc.transform.format.Record[] array = new es.inteco.xbrl.pgc.transform.format.Record[0];
        return (es.inteco.xbrl.pgc.transform.format.Record[]) this._recordList.toArray(array);
    }

    /**
     * Method getRecordCount.
     * 
     * @return the size of this collection
     */
    public int getRecordCount(
    ) {
        return this._recordList.size();
    }

    /**
     * Returns the value of field 'reportingDateEnd'.
     * 
     * @return the value of field 'ReportingDateEnd'.
     */
    public org.exolab.castor.types.Date getReportingDateEnd(
    ) {
        return this._reportingDateEnd;
    }

    /**
     * Returns the value of field 'reportingDateStart'.
     * 
     * @return the value of field 'ReportingDateStart'.
     */
    public org.exolab.castor.types.Date getReportingDateStart(
    ) {
        return this._reportingDateStart;
    }

    /**
     * Method getTable.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the
     * es.inteco.xbrl.pgc.transform.format.Table at the given index
     */
    public es.inteco.xbrl.pgc.transform.format.Table getTable(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._tableList.size()) {
            throw new IndexOutOfBoundsException("getTable: Index value '" + index + "' not in range [0.." + (this._tableList.size() - 1) + "]");
        }
        
        return (es.inteco.xbrl.pgc.transform.format.Table) _tableList.get(index);
    }

    /**
     * Method getTable.Returns the contents of the collection in an
     * Array.  <p>Note:  Just in case the collection contents are
     * changing in another thread, we pass a 0-length Array of the
     * correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public es.inteco.xbrl.pgc.transform.format.Table[] getTable(
    ) {
        es.inteco.xbrl.pgc.transform.format.Table[] array = new es.inteco.xbrl.pgc.transform.format.Table[0];
        return (es.inteco.xbrl.pgc.transform.format.Table[]) this._tableList.toArray(array);
    }

    /**
     * Method getTableCount.
     * 
     * @return the size of this collection
     */
    public int getTableCount(
    ) {
        return this._tableList.size();
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
    public void removeAllItem(
    ) {
        this._itemList.clear();
    }

    /**
     */
    public void removeAllRecord(
    ) {
        this._recordList.clear();
    }

    /**
     */
    public void removeAllTable(
    ) {
        this._tableList.clear();
    }

    /**
     * Method removeItem.
     * 
     * @param vItem
     * @return true if the object was removed from the collection.
     */
    public boolean removeItem(
            final es.inteco.xbrl.pgc.transform.format.Item vItem) {
        boolean removed = _itemList.remove(vItem);
        return removed;
    }

    /**
     * Method removeItemAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public es.inteco.xbrl.pgc.transform.format.Item removeItemAt(
            final int index) {
        java.lang.Object obj = this._itemList.remove(index);
        return (es.inteco.xbrl.pgc.transform.format.Item) obj;
    }

    /**
     * Method removeRecord.
     * 
     * @param vRecord
     * @return true if the object was removed from the collection.
     */
    public boolean removeRecord(
            final es.inteco.xbrl.pgc.transform.format.Record vRecord) {
        boolean removed = _recordList.remove(vRecord);
        return removed;
    }

    /**
     * Method removeRecordAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public es.inteco.xbrl.pgc.transform.format.Record removeRecordAt(
            final int index) {
        java.lang.Object obj = this._recordList.remove(index);
        return (es.inteco.xbrl.pgc.transform.format.Record) obj;
    }

    /**
     * Method removeTable.
     * 
     * @param vTable
     * @return true if the object was removed from the collection.
     */
    public boolean removeTable(
            final es.inteco.xbrl.pgc.transform.format.Table vTable) {
        boolean removed = _tableList.remove(vTable);
        return removed;
    }

    /**
     * Method removeTableAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public es.inteco.xbrl.pgc.transform.format.Table removeTableAt(
            final int index) {
        java.lang.Object obj = this._tableList.remove(index);
        return (es.inteco.xbrl.pgc.transform.format.Table) obj;
    }

    /**
     * Sets the value of field 'baseDecimals'.
     * 
     * @param baseDecimals the value of field 'baseDecimals'.
     */
    public void setBaseDecimals(
            final java.lang.String baseDecimals) {
        this._baseDecimals = baseDecimals;
    }

    /**
     * Sets the value of field 'baseUnit'.
     * 
     * @param baseUnit the value of field 'baseUnit'.
     */
    public void setBaseUnit(
            final es.inteco.xbrl.pgc.transform.format.types.Unittype baseUnit) {
        this._baseUnit = baseUnit;
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
     * @param vItem
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setItem(
            final int index,
            final es.inteco.xbrl.pgc.transform.format.Item vItem)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._itemList.size()) {
            throw new IndexOutOfBoundsException("setItem: Index value '" + index + "' not in range [0.." + (this._itemList.size() - 1) + "]");
        }
        
        this._itemList.set(index, vItem);
    }

    /**
     * 
     * 
     * @param vItemArray
     */
    public void setItem(
            final es.inteco.xbrl.pgc.transform.format.Item[] vItemArray) {
        //-- copy array
        _itemList.clear();
        
        for (int i = 0; i < vItemArray.length; i++) {
                this._itemList.add(vItemArray[i]);
        }
    }

    /**
     * 
     * 
     * @param index
     * @param vRecord
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setRecord(
            final int index,
            final es.inteco.xbrl.pgc.transform.format.Record vRecord)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._recordList.size()) {
            throw new IndexOutOfBoundsException("setRecord: Index value '" + index + "' not in range [0.." + (this._recordList.size() - 1) + "]");
        }
        
        this._recordList.set(index, vRecord);
    }

    /**
     * 
     * 
     * @param vRecordArray
     */
    public void setRecord(
            final es.inteco.xbrl.pgc.transform.format.Record[] vRecordArray) {
        //-- copy array
        _recordList.clear();
        
        for (int i = 0; i < vRecordArray.length; i++) {
                this._recordList.add(vRecordArray[i]);
        }
    }

    /**
     * Sets the value of field 'reportingDateEnd'.
     * 
     * @param reportingDateEnd the value of field 'reportingDateEnd'
     */
    public void setReportingDateEnd(
            final org.exolab.castor.types.Date reportingDateEnd) {
        this._reportingDateEnd = reportingDateEnd;
    }

    /**
     * Sets the value of field 'reportingDateStart'.
     * 
     * @param reportingDateStart the value of field
     * 'reportingDateStart'.
     */
    public void setReportingDateStart(
            final org.exolab.castor.types.Date reportingDateStart) {
        this._reportingDateStart = reportingDateStart;
    }

    /**
     * 
     * 
     * @param index
     * @param vTable
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setTable(
            final int index,
            final es.inteco.xbrl.pgc.transform.format.Table vTable)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._tableList.size()) {
            throw new IndexOutOfBoundsException("setTable: Index value '" + index + "' not in range [0.." + (this._tableList.size() - 1) + "]");
        }
        
        this._tableList.set(index, vTable);
    }

    /**
     * 
     * 
     * @param vTableArray
     */
    public void setTable(
            final es.inteco.xbrl.pgc.transform.format.Table[] vTableArray) {
        //-- copy array
        _tableList.clear();
        
        for (int i = 0; i < vTableArray.length; i++) {
                this._tableList.add(vTableArray[i]);
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
     * es.inteco.xbrl.pgc.transform.format.Module
     */
    public static es.inteco.xbrl.pgc.transform.format.Module unmarshal(
            final java.io.Reader reader)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        return (es.inteco.xbrl.pgc.transform.format.Module) org.exolab.castor.xml.Unmarshaller.unmarshal(es.inteco.xbrl.pgc.transform.format.Module.class, reader);
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
