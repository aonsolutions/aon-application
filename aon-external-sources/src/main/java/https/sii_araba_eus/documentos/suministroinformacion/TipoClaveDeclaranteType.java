
package https.sii_araba_eus.documentos.suministroinformacion;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para TipoClaveDeclaranteType.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <p>
 * <pre>
 * &lt;simpleType name="TipoClaveDeclaranteType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="V"/&gt;
 *     &lt;enumeration value="A"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "TipoClaveDeclaranteType")
@XmlEnum
public enum TipoClaveDeclaranteType {

    V,
    A;

    public String value() {
        return name();
    }

    public static TipoClaveDeclaranteType fromValue(String v) {
        return valueOf(v);
    }

}
