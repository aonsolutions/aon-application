
package eus.bizkaia.ogasuna.sii.documentos.suministroinformacion;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para CompletaSinDestinatarioType.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <p>
 * <pre>
 * &lt;simpleType name="CompletaSinDestinatarioType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="S"/&gt;
 *     &lt;enumeration value="N"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "CompletaSinDestinatarioType")
@XmlEnum
public enum CompletaSinDestinatarioType {

    S,
    N;

    public String value() {
        return name();
    }

    public static CompletaSinDestinatarioType fromValue(String v) {
        return valueOf(v);
    }

}
