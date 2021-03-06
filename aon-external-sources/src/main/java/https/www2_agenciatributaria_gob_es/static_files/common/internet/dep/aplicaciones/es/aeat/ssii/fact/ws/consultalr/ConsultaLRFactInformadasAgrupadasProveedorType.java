
package https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.consultalr;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.ConsultaInformacionProveedor;


/**
 * <p>Clase Java para ConsultaLRFactInformadasAgrupadasProveedorType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="ConsultaLRFactInformadasAgrupadasProveedorType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://www2.agenciatributaria.gob.es/static_files/common/internet/dep/aplicaciones/es/aeat/ssii/fact/ws/SuministroInformacion.xsd}ConsultaInformacionProveedor"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="FiltroConsulta" type="{https://www2.agenciatributaria.gob.es/static_files/common/internet/dep/aplicaciones/es/aeat/ssii/fact/ws/ConsultaLR.xsd}LRFiltroFactInformadasAgrupadasProveedorType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConsultaLRFactInformadasAgrupadasProveedorType", propOrder = {
    "filtroConsulta"
})
public class ConsultaLRFactInformadasAgrupadasProveedorType
    extends ConsultaInformacionProveedor
{

    @XmlElement(name = "FiltroConsulta", required = true)
    protected LRFiltroFactInformadasAgrupadasProveedorType filtroConsulta;

    /**
     * Obtiene el valor de la propiedad filtroConsulta.
     * 
     * @return
     *     possible object is
     *     {@link LRFiltroFactInformadasAgrupadasProveedorType }
     *     
     */
    public LRFiltroFactInformadasAgrupadasProveedorType getFiltroConsulta() {
        return filtroConsulta;
    }

    /**
     * Define el valor de la propiedad filtroConsulta.
     * 
     * @param value
     *     allowed object is
     *     {@link LRFiltroFactInformadasAgrupadasProveedorType }
     *     
     */
    public void setFiltroConsulta(LRFiltroFactInformadasAgrupadasProveedorType value) {
        this.filtroConsulta = value;
    }

}
