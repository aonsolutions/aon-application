
package https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para ClaveTipoFacturaType.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <p>
 * <pre>
 * &lt;simpleType name="ClaveTipoFacturaType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="F1"/&gt;
 *     &lt;enumeration value="F2"/&gt;
 *     &lt;enumeration value="R1"/&gt;
 *     &lt;enumeration value="R2"/&gt;
 *     &lt;enumeration value="R3"/&gt;
 *     &lt;enumeration value="R4"/&gt;
 *     &lt;enumeration value="R5"/&gt;
 *     &lt;enumeration value="F3"/&gt;
 *     &lt;enumeration value="F4"/&gt;
 *     &lt;enumeration value="F5"/&gt;
 *     &lt;enumeration value="F6"/&gt;
 *     &lt;enumeration value="LC"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "ClaveTipoFacturaType")
@XmlEnum
public enum ClaveTipoFacturaType {


    /**
     * FACTURA
     * 
     */
    @XmlEnumValue("F1")
    F_1("F1"),

    /**
     * FACTURA SIMPLIFICADA
     * 
     */
    @XmlEnumValue("F2")
    F_2("F2"),

    /**
     * FACTURA RECTIFICATIVA	(Art 80.1 y 80.2 y error fundado en derecho)
     * 
     */
    @XmlEnumValue("R1")
    R_1("R1"),

    /**
     * FACTURA RECTIFICATIVA (Art. 80.3)
     * 
     */
    @XmlEnumValue("R2")
    R_2("R2"),

    /**
     * FACTURA RECTIFICATIVA	(Art. 80.4)
     * 
     */
    @XmlEnumValue("R3")
    R_3("R3"),

    /**
     * FACTURA RECTIFICATIVA	(Resto)
     * 
     */
    @XmlEnumValue("R4")
    R_4("R4"),

    /**
     * FACTURA RECTIFICATIVA	EN FACTURAS SIMPLIFICADAS
     * 
     */
    @XmlEnumValue("R5")
    R_5("R5"),

    /**
     * FACTURA EMITIDA EN SUSTITUCIÓN DE FACTURAS SIMPLIFICADAS FACTURADAS Y DECLARADAS
     * 
     */
    @XmlEnumValue("F3")
    F_3("F3"),

    /**
     * ASIENTO RESUMEN DE FACTURAS
     * 
     */
    @XmlEnumValue("F4")
    F_4("F4"),

    /**
     * IMPORTACIONES (DUA)
     * 
     */
    @XmlEnumValue("F5")
    F_5("F5"),

    /**
     * OTROS JUSTIFICANTES CONTABLES
     * 
     */
    @XmlEnumValue("F6")
    F_6("F6"),

    /**
     * ADUANAS - LIQUIDACION COMPLEMENTARIA
     * 
     */
    LC("LC");
    private final String value;

    ClaveTipoFacturaType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ClaveTipoFacturaType fromValue(String v) {
        for (ClaveTipoFacturaType c: ClaveTipoFacturaType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
