
package eus.bizkaia.ogasuna.sii.documentos.consultalr;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import eus.bizkaia.ogasuna.sii.documentos.suministroinformacion.ConsultaInformacion;


/**
 * <p>Clase Java para ConsultaPagosType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="ConsultaPagosType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}ConsultaInformacion"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="FiltroConsultaPagos" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/ConsultaLR.xsd}LRFiltroPagosType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConsultaPagosType", propOrder = {
    "filtroConsultaPagos"
})
public class ConsultaPagosType
    extends ConsultaInformacion
{

    @XmlElement(name = "FiltroConsultaPagos", required = true)
    protected LRFiltroPagosType filtroConsultaPagos;

    /**
     * Obtiene el valor de la propiedad filtroConsultaPagos.
     * 
     * @return
     *     possible object is
     *     {@link LRFiltroPagosType }
     *     
     */
    public LRFiltroPagosType getFiltroConsultaPagos() {
        return filtroConsultaPagos;
    }

    /**
     * Define el valor de la propiedad filtroConsultaPagos.
     * 
     * @param value
     *     allowed object is
     *     {@link LRFiltroPagosType }
     *     
     */
    public void setFiltroConsultaPagos(LRFiltroPagosType value) {
        this.filtroConsultaPagos = value;
    }

}
