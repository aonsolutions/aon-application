
package https.sii_araba_eus.documentos.suministroinformacion;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para AutorizacionArticulo15.4Type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <p>
 * <pre>
 * &lt;simpleType name="AutorizacionArticulo15.4Type"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="S"/&gt;
 *     &lt;enumeration value="N"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "AutorizacionArticulo15.4Type")
@XmlEnum
public enum AutorizacionArticulo154Type {

    S,
    N;

    public String value() {
        return name();
    }

    public static AutorizacionArticulo154Type fromValue(String v) {
        return valueOf(v);
    }

}
