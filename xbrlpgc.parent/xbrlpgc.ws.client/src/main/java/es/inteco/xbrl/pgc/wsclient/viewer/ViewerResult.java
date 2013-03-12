/**
 * ViewerResult.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package es.inteco.xbrl.pgc.wsclient.viewer;

public class ViewerResult  implements java.io.Serializable {
    private byte[] arrayResult;

    private java.lang.String generalError;

    public ViewerResult() {
    }

    public ViewerResult(
           byte[] arrayResult,
           java.lang.String generalError) {
           this.arrayResult = arrayResult;
           this.generalError = generalError;
    }


    /**
     * Gets the arrayResult value for this ViewerResult.
     * 
     * @return arrayResult
     */
    public byte[] getArrayResult() {
        return arrayResult;
    }


    /**
     * Sets the arrayResult value for this ViewerResult.
     * 
     * @param arrayResult
     */
    public void setArrayResult(byte[] arrayResult) {
        this.arrayResult = arrayResult;
    }


    /**
     * Gets the generalError value for this ViewerResult.
     * 
     * @return generalError
     */
    public java.lang.String getGeneralError() {
        return generalError;
    }


    /**
     * Sets the generalError value for this ViewerResult.
     * 
     * @param generalError
     */
    public void setGeneralError(java.lang.String generalError) {
        this.generalError = generalError;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ViewerResult)) return false;
        ViewerResult other = (ViewerResult) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.arrayResult==null && other.getArrayResult()==null) || 
             (this.arrayResult!=null &&
              java.util.Arrays.equals(this.arrayResult, other.getArrayResult()))) &&
            ((this.generalError==null && other.getGeneralError()==null) || 
             (this.generalError!=null &&
              this.generalError.equals(other.getGeneralError())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getArrayResult() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getArrayResult());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getArrayResult(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getGeneralError() != null) {
            _hashCode += getGeneralError().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ViewerResult.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://viewer.pgc.xbrl.inteco.es", "ViewerResult"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("arrayResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://viewer.pgc.xbrl.inteco.es", "arrayResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "base64Binary"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("generalError");
        elemField.setXmlName(new javax.xml.namespace.QName("http://viewer.pgc.xbrl.inteco.es", "generalError"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
