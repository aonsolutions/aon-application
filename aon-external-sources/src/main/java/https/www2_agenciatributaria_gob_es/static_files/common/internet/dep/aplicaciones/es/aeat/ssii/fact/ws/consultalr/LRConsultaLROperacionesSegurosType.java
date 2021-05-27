
package https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.consultalr;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.ConsultaInformacion;


/**
 * <p>Clase Java para LRConsultaLROperacionesSegurosType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="LRConsultaLROperacionesSegurosType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://www2.agenciatributaria.gob.es/static_files/common/internet/dep/aplicaciones/es/aeat/ssii/fact/ws/SuministroInformacion.xsd}ConsultaInformacion"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="FiltroConsulta" type="{https://www2.agenciatributaria.gob.es/static_files/common/internet/dep/aplicaciones/es/aeat/ssii/fact/ws/ConsultaLR.xsd}LRFiltroOperacionesSegurosType"/&gt;
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
