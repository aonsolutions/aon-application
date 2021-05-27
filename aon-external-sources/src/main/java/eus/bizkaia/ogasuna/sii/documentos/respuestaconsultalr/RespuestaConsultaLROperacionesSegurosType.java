
package eus.bizkaia.ogasuna.sii.documentos.respuestaconsultalr;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para RespuestaConsultaLROperacionesSegurosType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="RespuestaConsultaLROperacionesSegurosType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.bizkaia.eus/ogasuna/sii/documentos/RespuestaConsultaLR.xsd}RespuestaConsultaLRFacturasType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="RegistroRespuestaConsultaLROperacionesSeguros" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/RespuestaConsultaLR.xsd}RegistroRespuestaConsultaOperacionesSegurosType" maxOccurs="10000" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RespuestaConsultaLROperacionesSegurosType", propOrder = {
    "registroRespuestaConsultaLROperacionesSeguros"
})
public class RespuestaConsultaLROperacionesSegurosType
    extends RespuestaConsultaLRFacturasType
{

    @XmlElement(name = "RegistroRespuestaConsultaLROperacionesSeguros")
    protected List<RegistroRespuestaConsultaOperacionesSegurosType> registroRespuestaConsultaLROperacionesSeguros;

    /**
     * Gets the value of the registroRespuestaConsultaLROperacionesSeguros property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the registroRespuestaConsultaLROperacionesSeguros property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRegistroRespuestaConsultaLROperacionesSeguros().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RegistroRespuestaConsultaOperacionesSegurosType }
     * 
     * 
     */
    public List<RegistroRespuestaConsultaOperacionesSegurosType> getRegistroRespuestaConsultaLROperacionesSeguros() {
        if (registroRespuestaConsultaLROperacionesSeguros == null) {
            registroRespuestaConsultaLROperacionesSeguros = new ArrayList<RegistroRespuestaConsultaOperacionesSegurosType>();
        }
        return this.registroRespuestaConsultaLROperacionesSeguros;
    }

}
