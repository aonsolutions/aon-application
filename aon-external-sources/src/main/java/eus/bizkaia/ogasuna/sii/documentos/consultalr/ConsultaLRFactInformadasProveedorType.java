
package eus.bizkaia.ogasuna.sii.documentos.consultalr;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import eus.bizkaia.ogasuna.sii.documentos.suministroinformacion.ConsultaInformacionProveedor;


/**
 * <p>Clase Java para ConsultaLRFactInformadasProveedorType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="ConsultaLRFactInformadasProveedorType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}ConsultaInformacionProveedor"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="FiltroConsulta" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/ConsultaLR.xsd}LRFiltroFactInformadasProveedorType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConsultaLRFactInformadasProveedorType", propOrder = {
    "filtroConsulta"
})
public class ConsultaLRFactInformadasProveedorType
    extends ConsultaInformacionProveedor
{

    @XmlElement(name = "FiltroConsulta", required = true)
    protected LRFiltroFactInformadasProveedorType filtroConsulta;

    /**
     * Obtiene el valor de la propiedad filtroConsulta.
     * 
     * @return
     *     possible object is
     *     {@link LRFiltroFactInformadasProveedorType }
     *     
     */
    public LRFiltroFactInformadasProveedorType getFiltroConsulta() {
        return filtroConsulta;
    }

    /**
     * Define el valor de la propiedad filtroConsulta.
     * 
     * @param value
     *     allowed object is
     *     {@link LRFiltroFactInformadasProveedorType }
     *     
     */
    public void setFiltroConsulta(LRFiltroFactInformadasProveedorType value) {
        this.filtroConsulta = value;
    }

}
