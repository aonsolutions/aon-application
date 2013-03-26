/**
 * ValidateResult.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package es.inteco.xbrl.pgc.wsclient.validator;

public class ValidateResult  implements java.io.Serializable {
    private java.lang.String errors;

    private java.lang.String generalError;

    private boolean valid;

    public ValidateResult() {
    }

    public ValidateResult(
           java.lang.String errors,
           java.lang.String generalError,
           boolean valid) {
           this.errors = errors;
           this.generalError = generalError;
           this.valid = valid;
    }


    /**
     * Gets the errors value for this ValidateResult.
     * 
     * @return errors
     */
    public java.lang.String getErrors() {
        return errors;
    }


    /**
     * Sets the errors value for this ValidateResult.
     * 
     * @param errors
     */
    public void setErrors(java.lang.String errors) {
        this.errors = errors;
    }


    /**
     * Gets the generalError value for this ValidateResult.
     * 
     * @return generalError
     */
    public java.lang.String getGeneralError() {
        return generalError;
    }


    /**
     * Sets the generalError value for this ValidateResult.
     * 
     * @param generalError
     */
    public void setGeneralError(java.lang.String generalError) {
        this.generalError = generalError;
    }


    /**
     * Gets the valid value for this ValidateResult.
     * 
     * @return valid
     */
    public boolean isValid() {
        return valid;
    }


    /**
     * Sets the valid value for this ValidateResult.
     * 
     * @param valid
     */
    public void setValid(boolean valid) {
        this.valid = valid;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ValidateResult)) return false;
        ValidateResult other = (ValidateResult) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.errors==null && other.getErrors()==null) || 
             (this.errors!=null &&
              this.errors.equals(other.getErrors()))) &&
            ((this.generalError==null && other.getGeneralError()==null) || 
             (this.generalError!=null &&
              this.generalError.equals(other.getGeneralError()))) &&
            this.valid == other.isValid();
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
        if (getErrors() != null) {
            _hashCode += getErrors().hashCode();
        }
        if (getGeneralError() != null) {
            _hashCode += getGeneralError().hashCode();
        }
        _hashCode += (isValid() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ValidateResult.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.inteco.es/xbrl/pgc07/validator", "ValidateResult"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("errors");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.inteco.es/xbrl/pgc07/validator", "errors"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("generalError");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.inteco.es/xbrl/pgc07/validator", "generalError"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("valid");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.inteco.es/xbrl/pgc07/validator", "valid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
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
