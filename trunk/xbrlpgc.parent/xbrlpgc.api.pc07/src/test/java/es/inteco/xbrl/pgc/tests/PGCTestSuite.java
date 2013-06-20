/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3rc1</a>, using an XML
 * Schema.
 * $Id: PGCTestSuite.java,v 1.1 2011/11/30 13:50:21 pablo Exp $
 */

package es.inteco.xbrl.pgc.tests;

/**
 * Class PGCTestSuite.
 * 
 * @version $Revision: 1.1 $ $Date: 2011/11/30 13:50:21 $
 */
@SuppressWarnings("serial")
public class PGCTestSuite implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _PGCTestList.
     */
    private java.util.Vector<es.inteco.xbrl.pgc.tests.PGCTest> _PGCTestList;


      //----------------/
     //- Constructors -/
    //----------------/

    public PGCTestSuite() {
        super();
        this._PGCTestList = new java.util.Vector<es.inteco.xbrl.pgc.tests.PGCTest>();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vPGCTest
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addPGCTest(
            final es.inteco.xbrl.pgc.tests.PGCTest vPGCTest)
    throws java.lang.IndexOutOfBoundsException {
        this._PGCTestList.addElement(vPGCTest);
    }

    /**
     * 
     * 
     * @param index
     * @param vPGCTest
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addPGCTest(
            final int index,
            final es.inteco.xbrl.pgc.tests.PGCTest vPGCTest)
    throws java.lang.IndexOutOfBoundsException {
        this._PGCTestList.add(index, vPGCTest);
    }

    /**
     * Method enumeratePGCTest.
     * 
     * @return an Enumeration over all
     * es.inteco.xbrl.pgc.tests.PGCTest elements
     */
    public java.util.Enumeration<? extends es.inteco.xbrl.pgc.tests.PGCTest> enumeratePGCTest(
    ) {
        return this._PGCTestList.elements();
    }

    /**
     * Method getPGCTest.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the es.inteco.xbrl.pgc.tests.PGCTest at
     * the given index
     */
    public es.inteco.xbrl.pgc.tests.PGCTest getPGCTest(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._PGCTestList.size()) {
            throw new IndexOutOfBoundsException("getPGCTest: Index value '" + index + "' not in range [0.." + (this._PGCTestList.size() - 1) + "]");
        }
        
        return (es.inteco.xbrl.pgc.tests.PGCTest) _PGCTestList.get(index);
    }

    /**
     * Method getPGCTest.Returns the contents of the collection in
     * an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public es.inteco.xbrl.pgc.tests.PGCTest[] getPGCTest(
    ) {
        es.inteco.xbrl.pgc.tests.PGCTest[] array = new es.inteco.xbrl.pgc.tests.PGCTest[0];
        return (es.inteco.xbrl.pgc.tests.PGCTest[]) this._PGCTestList.toArray(array);
    }

    /**
     * Method getPGCTestCount.
     * 
     * @return the size of this collection
     */
    public int getPGCTestCount(
    ) {
        return this._PGCTestList.size();
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
    public void removeAllPGCTest(
    ) {
        this._PGCTestList.clear();
    }

    /**
     * Method removePGCTest.
     * 
     * @param vPGCTest
     * @return true if the object was removed from the collection.
     */
    public boolean removePGCTest(
            final es.inteco.xbrl.pgc.tests.PGCTest vPGCTest) {
        boolean removed = _PGCTestList.remove(vPGCTest);
        return removed;
    }

    /**
     * Method removePGCTestAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public es.inteco.xbrl.pgc.tests.PGCTest removePGCTestAt(
            final int index) {
        java.lang.Object obj = this._PGCTestList.remove(index);
        return (es.inteco.xbrl.pgc.tests.PGCTest) obj;
    }

    /**
     * 
     * 
     * @param index
     * @param vPGCTest
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setPGCTest(
            final int index,
            final es.inteco.xbrl.pgc.tests.PGCTest vPGCTest)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._PGCTestList.size()) {
            throw new IndexOutOfBoundsException("setPGCTest: Index value '" + index + "' not in range [0.." + (this._PGCTestList.size() - 1) + "]");
        }
        
        this._PGCTestList.set(index, vPGCTest);
    }

    /**
     * 
     * 
     * @param vPGCTestArray
     */
    public void setPGCTest(
            final es.inteco.xbrl.pgc.tests.PGCTest[] vPGCTestArray) {
        //-- copy array
        _PGCTestList.clear();
        
        for (int i = 0; i < vPGCTestArray.length; i++) {
                this._PGCTestList.add(vPGCTestArray[i]);
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
     * @return the unmarshaled es.inteco.xbrl.pgc.tests.PGCTestSuite
     */
    public static es.inteco.xbrl.pgc.tests.PGCTestSuite unmarshal(
            final java.io.Reader reader)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        return (es.inteco.xbrl.pgc.tests.PGCTestSuite) org.exolab.castor.xml.Unmarshaller.unmarshal(es.inteco.xbrl.pgc.tests.PGCTestSuite.class, reader);
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
