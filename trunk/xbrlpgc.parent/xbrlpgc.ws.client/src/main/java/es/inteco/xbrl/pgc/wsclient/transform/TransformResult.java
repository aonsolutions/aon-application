/**
 * TransformResult.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package es.inteco.xbrl.pgc.wsclient.transform;

public class TransformResult  implements java.io.Serializable {
    private byte[] arrayResult;

    private java.lang.String generalError;

    private java.lang.String xbrlValidateError;

    private java.lang.String xsdValidateError;

    public TransformResult() {
    }

    public TransformResult(
           byte[] arrayResult,
           java.lang.String generalError,
           java.lang.String xbrlValidateError,
           java.lang.String xsdValidateError) {
           this.arrayResult = arrayResult;
           this.generalError = generalError;
           this.xbrlValidateError = xbrlValidateError;
           this.xsdValidateError = xsdValidateError;
    }


    /**
     * Gets the arrayResult value for this TransformResult.
     * 
     * @return arrayResult
     */
    public byte[] getArrayResult() {
        return arrayResult;
    }


    /**
     * Sets the arrayResult value for this TransformResult.
     * 
     * @param arrayResult
     */
    public void setArrayResult(byte[] arrayResult) {
        this.arrayResult = arrayResult;
    }


    /**
     * Gets the generalError value for this TransformResult.
     * 
     * @return generalError
     */
    public java.lang.String getGeneralError() {
        return generalError;
    }


    /**
     * Sets the generalError value for this TransformResult.
     * 
     * @param generalError
     */
    public void setGeneralError(java.lang.String generalError) {
        this.generalError = generalError;
    }


    /**
     * Gets the xbrlValidateError value for this TransformResult.
     * 
     * @return xbrlValidateError
     */
    public java.lang.String getXbrlValidateError() {
        return xbrlValidateError;
    }


    /**
     * Sets the xbrlValidateError value for this TransformResult.
     * 
     * @param xbrlValidateError
     */
    public void setXbrlValidateError(java.lang.String xbrlValidateError) {
        this.xbrlValidateError = xbrlValidateError;
    }


    /**
     * Gets the xsdValidateError value for this TransformResult.
     * 
     * @return xsdValidateError
     */
    public java.lang.String getXsdValidateError() {
        return xsdValidateError;
    }


    /**
     * Sets the xsdValidateError value for this TransformResult.
     * 
     * @param xsdValidateError
     */
    public void setXsdValidateError(java.lang.String xsdValidateError) {
        this.xsdValidateError = xsdValidateError;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof TransformResult)) return false;
        TransformResult other = (TransformResult) obj;
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
              this.generalError.equals(other.getGeneralError()))) &&
            ((this.xbrlValidateError==null && other.getXbrlValidateError()==null) || 
             (this.xbrlValidateError!=null &&
              this.xbrlValidateError.equals(other.getXbrlValidateError()))) &&
            ((this.xsdValidateError==null && other.getXsdValidateError()==null) || 
             (this.xsdValidateError!=null &&
              this.xsdValidateError.equals(other.getXsdValidateError())));
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
        if (getXbrlValidateError() != null) {
            _hashCode += getXbrlValidateError().hashCode();
        }
        if (getXsdValidateError() != null) {
            _hashCode += getXsdValidateError().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(TransformResult.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.inteco.es/xbrl/pgc07/transform", "TransformResult"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("arrayResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.inteco.es/xbrl/pgc07/transform", "arrayResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "base64Binary"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("generalError");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.inteco.es/xbrl/pgc07/transform", "generalError"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("xbrlValidateError");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.inteco.es/xbrl/pgc07/transform", "xbrlValidateError"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("xsdValidateError");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.inteco.es/xbrl/pgc07/transform", "xsdValidateError"));
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
