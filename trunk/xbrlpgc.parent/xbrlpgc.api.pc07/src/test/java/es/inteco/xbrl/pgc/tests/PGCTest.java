/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3rc1</a>, using an XML
 * Schema.
 * $Id: PGCTest.java,v 1.1 2011/11/30 13:50:21 pablo Exp $
 */

package es.inteco.xbrl.pgc.tests;

/**
 * Class PGCTest.
 * 
 * @version $Revision: 1.1 $ $Date: 2011/11/30 13:50:21 $
 */
@SuppressWarnings("serial")
public class PGCTest implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _id.
     */
    private java.lang.String _id;

    /**
     * Field _inputPath.
     */
    private java.lang.String _inputPath;

    /**
     * Field _outputPath.
     */
    private java.lang.String _outputPath;

    /**
     * Field _expectedResult.
     */
    private java.lang.String _expectedResult;


      //----------------/
     //- Constructors -/
    //----------------/

    public PGCTest() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'expectedResult'.
     * 
     * @return the value of field 'ExpectedResult'.
     */
    public java.lang.String getExpectedResult(
    ) {
        return this._expectedResult;
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
     * Returns the value of field 'inputPath'.
     * 
     * @return the value of field 'InputPath'.
     */
    public java.lang.String getInputPath(
    ) {
        return this._inputPath;
    }

    /**
     * Returns the value of field 'outputPath'.
     * 
     * @return the value of field 'OutputPath'.
     */
    public java.lang.String getOutputPath(
    ) {
        return this._outputPath;
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
     * Sets the value of field 'expectedResult'.
     * 
     * @param expectedResult the value of field 'expectedResult'.
     */
    public void setExpectedResult(
            final java.lang.String expectedResult) {
        this._expectedResult = expectedResult;
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
     * Sets the value of field 'inputPath'.
     * 
     * @param inputPath the value of field 'inputPath'.
     */
    public void setInputPath(
            final java.lang.String inputPath) {
        this._inputPath = inputPath;
    }

    /**
     * Sets the value of field 'outputPath'.
     * 
     * @param outputPath the value of field 'outputPath'.
     */
    public void setOutputPath(
            final java.lang.String outputPath) {
        this._outputPath = outputPath;
    }

    /**
     * Method unmarshal.
     * 
     * @param reader
     * @throws org.exolab.castor.xml.MarshalException if object is
     * null or if any SAXException is thrown during marshaling
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     * @return the unmarshaled es.inteco.xbrl.pgc.tests.PGCTest
     */
    public static es.inteco.xbrl.pgc.tests.PGCTest unmarshal(
            final java.io.Reader reader)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        return (es.inteco.xbrl.pgc.tests.PGCTest) org.exolab.castor.xml.Unmarshaller.unmarshal(es.inteco.xbrl.pgc.tests.PGCTest.class, reader);
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
