
package https.sii_araba_eus.documentos.respuestaconsultalr;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para RespuestaConsultaLRBienesInversionType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="RespuestaConsultaLRBienesInversionType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://sii.araba.eus/documentos/RespuestaConsultaLR.xsd}RespuestaConsultaLRFacturasType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="RegistroRespuestaConsultaLRBienesInversion" type="{https://sii.araba.eus/documentos/RespuestaConsultaLR.xsd}RegistroRespuestaConsultaBienesType" maxOccurs="10000" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RespuestaConsultaLRBienesInversionType", propOrder = {
    "registroRespuestaConsultaLRBienesInversion"
})
public class RespuestaConsultaLRBienesInversionType
    extends RespuestaConsultaLRFacturasType
{

    @XmlElement(name = "RegistroRespuestaConsultaLRBienesInversion")
    protected List<RegistroRespuestaConsultaBienesType> registroRespuestaConsultaLRBienesInversion;

    /**
     * Gets the value of the registroRespuestaConsultaLRBienesInversion property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the registroRespuestaConsultaLRBienesInversion property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRegistroRespuestaConsultaLRBienesInversion().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RegistroRespuestaConsultaBienesType }
     * 
     * 
     */
    public List<RegistroRespuestaConsultaBienesType> getRegistroRespuestaConsultaLRBienesInversion() {
        if (registroRespuestaConsultaLRBienesInversion == null) {
            registroRespuestaConsultaLRBienesInversion = new ArrayList<RegistroRespuestaConsultaBienesType>();
        }
        return this.registroRespuestaConsultaLRBienesInversion;
    }

}
