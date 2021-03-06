
package eus.bizkaia.ogasuna.sii.documentos.respuestasuministro;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para EstadoEnvioType.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <p>
 * <pre>
 * &lt;simpleType name="EstadoEnvioType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Correcto"/&gt;
 *     &lt;enumeration value="ParcialmenteCorrecto"/&gt;
 *     &lt;enumeration value="Incorrecto"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "EstadoEnvioType")
@XmlEnum
public enum EstadoEnvioType {


    /**
     * Correcto
     * 
     */
    @XmlEnumValue("Correcto")
    CORRECTO("Correcto"),

    /**
     * Parcialmente correcto. Ver detalle de errores
     * 
     */
    @XmlEnumValue("ParcialmenteCorrecto")
    PARCIALMENTE_CORRECTO("ParcialmenteCorrecto"),

    /**
     * Incorrecto
     * 
     */
    @XmlEnumValue("Incorrecto")
    INCORRECTO("Incorrecto");
    private final String value;

    EstadoEnvioType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EstadoEnvioType fromValue(String v) {
        for (EstadoEnvioType c: EstadoEnvioType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
