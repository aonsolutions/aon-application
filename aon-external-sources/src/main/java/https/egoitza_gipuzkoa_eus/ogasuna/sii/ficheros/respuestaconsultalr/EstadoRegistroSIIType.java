
package https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.respuestaconsultalr;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para EstadoRegistroSIIType.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <p>
 * <pre>
 * &lt;simpleType name="EstadoRegistroSIIType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Correcta"/&gt;
 *     &lt;enumeration value="AceptadaConErrores"/&gt;
 *     &lt;enumeration value="Anulada"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "EstadoRegistroSIIType")
@XmlEnum
public enum EstadoRegistroSIIType {


    /**
     * El registro se almacenado sin errores
     * 
     */
    @XmlEnumValue("Correcta")
    CORRECTA("Correcta"),

    /**
     * El registro se almacenado tiene algunos errores. Ver detalle del error
     * 
     */
    @XmlEnumValue("AceptadaConErrores")
    ACEPTADA_CON_ERRORES("AceptadaConErrores"),

    /**
     * El registro almacenado ha sido anulado
     * 
     */
    @XmlEnumValue("Anulada")
    ANULADA("Anulada");
    private final String value;

    EstadoRegistroSIIType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EstadoRegistroSIIType fromValue(String v) {
        for (EstadoRegistroSIIType c: EstadoRegistroSIIType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
