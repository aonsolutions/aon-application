
package https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.consultalr;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.IDFacturaRecibidaNombreBCType;


/**
 * <p>Clase Java para LRFiltroPagosType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="LRFiltroPagosType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="IDFactura" type="{https://www2.agenciatributaria.gob.es/static_files/common/internet/dep/aplicaciones/es/aeat/ssii/fact/ws/SuministroInformacion.xsd}IDFacturaRecibidaNombreBCType"/&gt;
 *         &lt;element name="ClavePaginacion" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LRFiltroPagosType", propOrder = {
    "idFactura",
    "clavePaginacion"
})
public class LRFiltroPagosType {

    @XmlElement(name = "IDFactura", required = true)
    protected IDFacturaRecibidaNombreBCType idFactura;
    @XmlElement(name = "ClavePaginacion")
    protected Double clavePaginacion;

    /**
     * Obtiene el valor de la propiedad idFactura.
     * 
     * @return
     *     possible object is
     *     {@link IDFacturaRecibidaNombreBCType }
     *     
     */
    public IDFacturaRecibidaNombreBCType getIDFactura() {
        return idFactura;
    }

    /**
     * Define el valor de la propiedad idFactura.
     * 
     * @param value
     *     allowed object is
     *     {@link IDFacturaRecibidaNombreBCType }
     *     
     */
    public void setIDFactura(IDFacturaRecibidaNombreBCType value) {
        this.idFactura = value;
    }

    /**
     * Obtiene el valor de la propiedad clavePaginacion.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getClavePaginacion() {
        return clavePaginacion;
    }

    /**
     * Define el valor de la propiedad clavePaginacion.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setClavePaginacion(Double value) {
        this.clavePaginacion = value;
    }

}
