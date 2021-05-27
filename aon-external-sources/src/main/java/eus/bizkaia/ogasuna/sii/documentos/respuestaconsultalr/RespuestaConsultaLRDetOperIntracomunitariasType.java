
package eus.bizkaia.ogasuna.sii.documentos.respuestaconsultalr;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para RespuestaConsultaLRDetOperIntracomunitariasType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="RespuestaConsultaLRDetOperIntracomunitariasType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.bizkaia.eus/ogasuna/sii/documentos/RespuestaConsultaLR.xsd}RespuestaConsultaLRFacturasType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="RegistroRespuestaConsultaLRDetOperIntracomunitarias" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/RespuestaConsultaLR.xsd}RegistroRespuestaConsultaDetOperIntracomunitariasType" maxOccurs="10000" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RespuestaConsultaLRDetOperIntracomunitariasType", propOrder = {
    "registroRespuestaConsultaLRDetOperIntracomunitarias"
})
public class RespuestaConsultaLRDetOperIntracomunitariasType
    extends RespuestaConsultaLRFacturasType
{

    @XmlElement(name = "RegistroRespuestaConsultaLRDetOperIntracomunitarias")
    protected List<RegistroRespuestaConsultaDetOperIntracomunitariasType> registroRespuestaConsultaLRDetOperIntracomunitarias;

    /**
     * Gets the value of the registroRespuestaConsultaLRDetOperIntracomunitarias property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the registroRespuestaConsultaLRDetOperIntracomunitarias property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRegistroRespuestaConsultaLRDetOperIntracomunitarias().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RegistroRespuestaConsultaDetOperIntracomunitariasType }
     * 
     * 
     */
    public List<RegistroRespuestaConsultaDetOperIntracomunitariasType> getRegistroRespuestaConsultaLRDetOperIntracomunitarias() {
        if (registroRespuestaConsultaLRDetOperIntracomunitarias == null) {
            registroRespuestaConsultaLRDetOperIntracomunitarias = new ArrayList<RegistroRespuestaConsultaDetOperIntracomunitariasType>();
        }
        return this.registroRespuestaConsultaLRDetOperIntracomunitarias;
    }

}
