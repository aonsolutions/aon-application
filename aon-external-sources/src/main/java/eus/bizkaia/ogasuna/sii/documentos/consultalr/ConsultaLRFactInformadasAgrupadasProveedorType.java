
package eus.bizkaia.ogasuna.sii.documentos.consultalr;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import eus.bizkaia.ogasuna.sii.documentos.suministroinformacion.ConsultaInformacionProveedor;


/**
 * <p>Clase Java para ConsultaLRFactInformadasAgrupadasProveedorType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="ConsultaLRFactInformadasAgrupadasProveedorType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}ConsultaInformacionProveedor"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="FiltroConsulta" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/ConsultaLR.xsd}LRFiltroFactInformadasAgrupadasProveedorType"/&gt;
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
