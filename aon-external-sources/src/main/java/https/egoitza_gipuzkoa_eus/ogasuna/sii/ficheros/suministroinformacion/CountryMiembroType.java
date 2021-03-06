
package https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministroinformacion;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para CountryMiembroType.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <p>
 * <pre>
 * &lt;simpleType name="CountryMiembroType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="DE"/&gt;
 *     &lt;enumeration value="AT"/&gt;
 *     &lt;enumeration value="BE"/&gt;
 *     &lt;enumeration value="BG"/&gt;
 *     &lt;enumeration value="CZ"/&gt;
 *     &lt;enumeration value="CY"/&gt;
 *     &lt;enumeration value="HR"/&gt;
 *     &lt;enumeration value="DK"/&gt;
 *     &lt;enumeration value="SK"/&gt;
 *     &lt;enumeration value="SI"/&gt;
 *     &lt;enumeration value="EE"/&gt;
 *     &lt;enumeration value="FI"/&gt;
 *     &lt;enumeration value="FR"/&gt;
 *     &lt;enumeration value="GR"/&gt;
 *     &lt;enumeration value="HU"/&gt;
 *     &lt;enumeration value="IE"/&gt;
 *     &lt;enumeration value="IT"/&gt;
 *     &lt;enumeration value="LV"/&gt;
 *     &lt;enumeration value="LT"/&gt;
 *     &lt;enumeration value="LU"/&gt;
 *     &lt;enumeration value="MT"/&gt;
 *     &lt;enumeration value="NL"/&gt;
 *     &lt;enumeration value="PL"/&gt;
 *     &lt;enumeration value="PT"/&gt;
 *     &lt;enumeration value="GB"/&gt;
 *     &lt;enumeration value="RO"/&gt;
 *     &lt;enumeration value="SE"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "CountryMiembroType")
@XmlEnum
public enum CountryMiembroType {

    DE,
    AT,
    BE,
    BG,
    CZ,
    CY,
    HR,
    DK,
    SK,
    SI,
    EE,
    FI,
    FR,
    GR,
    HU,
    IE,
    IT,
    LV,
    LT,
    LU,
    MT,
    NL,
    PL,
    PT,
    GB,
    RO,
    SE;

    public String value() {
        return name();
    }

    public static CountryMiembroType fromValue(String v) {
        return valueOf(v);
    }

}
