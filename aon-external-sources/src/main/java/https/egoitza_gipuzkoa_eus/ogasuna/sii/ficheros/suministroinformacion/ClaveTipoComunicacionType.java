
package https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministroinformacion;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para ClaveTipoComunicacionType.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <p>
 * <pre>
 * &lt;simpleType name="ClaveTipoComunicacionType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="A0"/&gt;
 *     &lt;enumeration value="A1"/&gt;
 *     &lt;enumeration value="A4"/&gt;
 *     &lt;enumeration value="A5"/&gt;
 *     &lt;enumeration value="A6"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "ClaveTipoComunicacionType")
@XmlEnum
public enum ClaveTipoComunicacionType {


    /**
     *  A0 Alta (Se añade al libro un registro una nueva factura)
     * 
     */
    @XmlEnumValue("A0")
    A_0("A0"),

    /**
     *  A1 Modificación (La información que se comunica sustituye a la existente relacionada)
     * 
     */
    @XmlEnumValue("A1")
    A_1("A1"),

    /**
     *  Modificación Factura Régimen de Viajeros
     * 
     */
    @XmlEnumValue("A4")
    A_4("A4"),

    /**
     *  Alta de las devoluciones del IVA de viajeros
     * 
     */
    @XmlEnumValue("A5")
    A_5("A5"),

    /**
     *  Modificacion de las devoluciones del IVA de viajeros
     * 
     */
    @XmlEnumValue("A6")
    A_6("A6");
    private final String value;

    ClaveTipoComunicacionType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ClaveTipoComunicacionType fromValue(String v) {
        for (ClaveTipoComunicacionType c: ClaveTipoComunicacionType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
