
package https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para ClaveOperacionType.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <p>
 * <pre>
 * &lt;simpleType name="ClaveOperacionType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="A"/&gt;
 *     &lt;enumeration value="B"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "ClaveOperacionType")
@XmlEnum
public enum ClaveOperacionType {


    /**
     * Indemnizaciones o prestaciones satisfechas superiores a 3005,06
     * 
     */
    A,

    /**
     * Primas o  contraprestaciones percibidas superiores a 3005,06
     * 
     */
    B;

    public String value() {
        return name();
    }

    public static ClaveOperacionType fromValue(String v) {
        return valueOf(v);
    }

}
