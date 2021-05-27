
package https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.respuestaconsultalr;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para RespuestaConsultaLRFactInformadasClienteType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="RespuestaConsultaLRFactInformadasClienteType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/RespuestaConsultaLR.xsd}RespuestaConsultaLRFacturasClienteType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="RegistroRespuestaConsultaLRFactInformadasCliente" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/RespuestaConsultaLR.xsd}RegistroRespuestaConsultaFactInformadasClienteType" maxOccurs="10000" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RespuestaConsultaLRFactInformadasClienteType", propOrder = {
    "registroRespuestaConsultaLRFactInformadasCliente"
})
public class RespuestaConsultaLRFactInformadasClienteType
    extends RespuestaConsultaLRFacturasClienteType
{

    @XmlElement(name = "RegistroRespuestaConsultaLRFactInformadasCliente")
    protected List<RegistroRespuestaConsultaFactInformadasClienteType> registroRespuestaConsultaLRFactInformadasCliente;

    /**
     * Gets the value of the registroRespuestaConsultaLRFactInformadasCliente property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the registroRespuestaConsultaLRFactInformadasCliente property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRegistroRespuestaConsultaLRFactInformadasCliente().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RegistroRespuestaConsultaFactInformadasClienteType }
     * 
     * 
     */
    public List<RegistroRespuestaConsultaFactInformadasClienteType> getRegistroRespuestaConsultaLRFactInformadasCliente() {
        if (registroRespuestaConsultaLRFactInformadasCliente == null) {
            registroRespuestaConsultaLRFactInformadasCliente = new ArrayList<RegistroRespuestaConsultaFactInformadasClienteType>();
        }
        return this.registroRespuestaConsultaLRFactInformadasCliente;
    }

}
