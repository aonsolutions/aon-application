//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.3.0.1 
// Visite <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2023.09.28 a las 10:07:44 AM UTC 
//


package es.gob.facturae.formato.versiones.facturaev3_2_2;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para AttachmentFormatType.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <p>
 * <pre>
 * &lt;simpleType name="AttachmentFormatType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="xml"/&gt;
 *     &lt;enumeration value="doc"/&gt;
 *     &lt;enumeration value="gif"/&gt;
 *     &lt;enumeration value="rtf"/&gt;
 *     &lt;enumeration value="pdf"/&gt;
 *     &lt;enumeration value="xls"/&gt;
 *     &lt;enumeration value="jpg"/&gt;
 *     &lt;enumeration value="bmp"/&gt;
 *     &lt;enumeration value="tiff"/&gt;
 *     &lt;enumeration value="html"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "AttachmentFormatType")
@XmlEnum
public enum AttachmentFormatType {

    @XmlEnumValue("xml")
    XML("xml"),
    @XmlEnumValue("doc")
    DOC("doc"),
    @XmlEnumValue("gif")
    GIF("gif"),
    @XmlEnumValue("rtf")
    RTF("rtf"),
    @XmlEnumValue("pdf")
    PDF("pdf"),
    @XmlEnumValue("xls")
    XLS("xls"),
    @XmlEnumValue("jpg")
    JPG("jpg"),
    @XmlEnumValue("bmp")
    BMP("bmp"),
    @XmlEnumValue("tiff")
    TIFF("tiff"),
    @XmlEnumValue("html")
    HTML("html");
    private final String value;

    AttachmentFormatType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AttachmentFormatType fromValue(String v) {
        for (AttachmentFormatType c: AttachmentFormatType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
