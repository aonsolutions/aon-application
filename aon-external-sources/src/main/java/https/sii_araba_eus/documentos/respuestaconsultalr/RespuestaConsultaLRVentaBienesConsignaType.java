
package https.sii_araba_eus.documentos.respuestaconsultalr;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para RespuestaConsultaLRVentaBienesConsignaType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="RespuestaConsultaLRVentaBienesConsignaType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://sii.araba.eus/documentos/RespuestaConsultaLR.xsd}RespuestaConsultaLRVentaBVType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="RegistroRespuestaConsultaLRDetOperacionIntracomunitariaVentasEnConsigna" type="{https://sii.araba.eus/documentos/RespuestaConsultaLR.xsd}RegistroRespuestaConsultaVentaBienesConsignaType" maxOccurs="10000" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RespuestaConsultaLRVentaBienesConsignaType", propOrder = {
    "registroRespuestaConsultaLRDetOperacionIntracomunitariaVentasEnConsigna"
})
public class RespuestaConsultaLRVentaBienesConsignaType
    extends RespuestaConsultaLRVentaBVType
{

    @XmlElement(name = "RegistroRespuestaConsultaLRDetOperacionIntracomunitariaVentasEnConsigna")
    protected List<RegistroRespuestaConsultaVentaBienesConsignaType> registroRespuestaConsultaLRDetOperacionIntracomunitariaVentasEnConsigna;

    /**
     * Gets the value of the registroRespuestaConsultaLRDetOperacionIntracomunitariaVentasEnConsigna property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the registroRespuestaConsultaLRDetOperacionIntracomunitariaVentasEnConsigna property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRegistroRespuestaConsultaLRDetOperacionIntracomunitariaVentasEnConsigna().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RegistroRespuestaConsultaVentaBienesConsignaType }
     * 
     * 
     */
    public List<RegistroRespuestaConsultaVentaBienesConsignaType> getRegistroRespuestaConsultaLRDetOperacionIntracomunitariaVentasEnConsigna() {
        if (registroRespuestaConsultaLRDetOperacionIntracomunitariaVentasEnConsigna == null) {
            registroRespuestaConsultaLRDetOperacionIntracomunitariaVentasEnConsigna = new ArrayList<RegistroRespuestaConsultaVentaBienesConsignaType>();
        }
        return this.registroRespuestaConsultaLRDetOperacionIntracomunitariaVentasEnConsigna;
    }

}
