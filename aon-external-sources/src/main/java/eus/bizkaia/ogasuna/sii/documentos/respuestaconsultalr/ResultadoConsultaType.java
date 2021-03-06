
package eus.bizkaia.ogasuna.sii.documentos.respuestaconsultalr;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para ResultadoConsultaType.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <p>
 * <pre>
 * &lt;simpleType name="ResultadoConsultaType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="ConDatos"/&gt;
 *     &lt;enumeration value="SinDatos"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "ResultadoConsultaType")
@XmlEnum
public enum ResultadoConsultaType {

    @XmlEnumValue("ConDatos")
    CON_DATOS("ConDatos"),
    @XmlEnumValue("SinDatos")
    SIN_DATOS("SinDatos");
    private final String value;

    ResultadoConsultaType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ResultadoConsultaType fromValue(String v) {
        for (ResultadoConsultaType c: ResultadoConsultaType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
