
package https.sii_araba_eus.documentos.respuestasuministro;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para EstadoRegistroType.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <p>
 * <pre>
 * &lt;simpleType name="EstadoRegistroType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Correcto"/&gt;
 *     &lt;enumeration value="AceptadoConErrores"/&gt;
 *     &lt;enumeration value="Incorrecto"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "EstadoRegistroType")
@XmlEnum
public enum EstadoRegistroType {


    /**
     * Correcto
     * 
     */
    @XmlEnumValue("Correcto")
    CORRECTO("Correcto"),

    /**
     * Aceptado con Errores. Ver detalle del error
     * 
     */
    @XmlEnumValue("AceptadoConErrores")
    ACEPTADO_CON_ERRORES("AceptadoConErrores"),

    /**
     * Incorrecto
     * 
     */
    @XmlEnumValue("Incorrecto")
    INCORRECTO("Incorrecto");
    private final String value;

    EstadoRegistroType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EstadoRegistroType fromValue(String v) {
        for (EstadoRegistroType c: EstadoRegistroType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
