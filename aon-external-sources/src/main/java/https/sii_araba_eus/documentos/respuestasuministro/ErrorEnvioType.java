
package https.sii_araba_eus.documentos.respuestasuministro;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para ErrorEnvioType.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <p>
 * <pre>
 * &lt;simpleType name="ErrorEnvioType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="ERR01"/&gt;
 *     &lt;enumeration value="ERR03"/&gt;
 *     &lt;enumeration value="ERR04"/&gt;
 *     &lt;enumeration value="ERR05"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "ErrorEnvioType")
@XmlEnum
public enum ErrorEnvioType {


    /**
     * Error de validación contra esquema
     * 
     */
    @XmlEnumValue("ERR01")
    ERR_01("ERR01"),

    /**
     * Declarante desconocido
     * 
     */
    @XmlEnumValue("ERR03")
    ERR_03("ERR03"),

    /**
     * El declarante debe identificarse mediante un NIF español.
     * 
     */
    @XmlEnumValue("ERR04")
    ERR_04("ERR04"),

    /**
     * El NIF del representante es incorrecto.
     * 
     */
    @XmlEnumValue("ERR05")
    ERR_05("ERR05");
    private final String value;

    ErrorEnvioType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ErrorEnvioType fromValue(String v) {
        for (ErrorEnvioType c: ErrorEnvioType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
