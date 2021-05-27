
package https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.respuestaconsultalr;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para RespuestaConsultaLRFactInformadasAgrupadasClienteType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="RespuestaConsultaLRFactInformadasAgrupadasClienteType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/RespuestaConsultaLR.xsd}RespuestaConsultaLRFacturasAgrupadasClienteType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="RegistroRespuestaConsultaLRFactInformadasAgrupadasCliente" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/RespuestaConsultaLR.xsd}RegistroRespuestaConsultaFactInformadasAgrupadasClienteType" maxOccurs="10000" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RespuestaConsultaLRFactInformadasAgrupadasClienteType", propOrder = {
    "registroRespuestaConsultaLRFactInformadasAgrupadasCliente"
})
public class RespuestaConsultaLRFactInformadasAgrupadasClienteType
    extends RespuestaConsultaLRFacturasAgrupadasClienteType
{

    @XmlElement(name = "RegistroRespuestaConsultaLRFactInformadasAgrupadasCliente")
    protected List<RegistroRespuestaConsultaFactInformadasAgrupadasClienteType> registroRespuestaConsultaLRFactInformadasAgrupadasCliente;

    /**
     * Gets the value of the registroRespuestaConsultaLRFactInformadasAgrupadasCliente property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the registroRespuestaConsultaLRFactInformadasAgrupadasCliente property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRegistroRespuestaConsultaLRFactInformadasAgrupadasCliente().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RegistroRespuestaConsultaFactInformadasAgrupadasClienteType }
     * 
     * 
     */
    public List<RegistroRespuestaConsultaFactInformadasAgrupadasClienteType> getRegistroRespuestaConsultaLRFactInformadasAgrupadasCliente() {
        if (registroRespuestaConsultaLRFactInformadasAgrupadasCliente == null) {
            registroRespuestaConsultaLRFactInformadasAgrupadasCliente = new ArrayList<RegistroRespuestaConsultaFactInformadasAgrupadasClienteType>();
        }
        return this.registroRespuestaConsultaLRFactInformadasAgrupadasCliente;
    }

}
