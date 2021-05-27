
package eus.bizkaia.ogasuna.sii.documentos.consultalr;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import eus.bizkaia.ogasuna.sii.documentos.suministroinformacion.ConsultaInformacion;


/**
 * <p>Clase Java para LRConsultaLROperacionesSegurosType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="LRConsultaLROperacionesSegurosType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}ConsultaInformacion"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="FiltroConsulta" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/ConsultaLR.xsd}LRFiltroOperacionesSegurosType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LRConsultaLROperacionesSegurosType", propOrder = {
    "filtroConsulta"
})
public class LRConsultaLROperacionesSegurosType
    extends ConsultaInformacion
{

    @XmlElement(name = "FiltroConsulta", required = true)
    protected LRFiltroOperacionesSegurosType filtroConsulta;

    /**
     * Obtiene el valor de la propiedad filtroConsulta.
     * 
     * @return
     *     possible object is
     *     {@link LRFiltroOperacionesSegurosType }
     *     
     */
    public LRFiltroOperacionesSegurosType getFiltroConsulta() {
        return filtroConsulta;
    }

    /**
     * Define el valor de la propiedad filtroConsulta.
     * 
     * @param value
     *     allowed object is
     *     {@link LRFiltroOperacionesSegurosType }
     *     
     */
    public void setFiltroConsulta(LRFiltroOperacionesSegurosType value) {
        this.filtroConsulta = value;
    }

}
