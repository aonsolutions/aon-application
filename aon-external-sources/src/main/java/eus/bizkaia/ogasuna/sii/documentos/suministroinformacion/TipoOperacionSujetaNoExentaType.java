
package eus.bizkaia.ogasuna.sii.documentos.suministroinformacion;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para TipoOperacionSujetaNoExentaType.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <p>
 * <pre>
 * &lt;simpleType name="TipoOperacionSujetaNoExentaType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="S1"/&gt;
 *     &lt;enumeration value="S2"/&gt;
 *     &lt;enumeration value="S3"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "TipoOperacionSujetaNoExentaType")
@XmlEnum
public enum TipoOperacionSujetaNoExentaType {


    /**
     * No exenta- Sin inversion sujeto pasivo 
     * 
     */
    @XmlEnumValue("S1")
    S_1("S1"),

    /**
     * No exenta - Con Inversion sujeto pasivo
     * 
     */
    @XmlEnumValue("S2")
    S_2("S2"),

    /**
     * No exenta - Sin inversion sujeto pasivo y con inversion sujeto Pasivo
     * 
     */
    @XmlEnumValue("S3")
    S_3("S3");
    private final String value;

    TipoOperacionSujetaNoExentaType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TipoOperacionSujetaNoExentaType fromValue(String v) {
        for (TipoOperacionSujetaNoExentaType c: TipoOperacionSujetaNoExentaType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
