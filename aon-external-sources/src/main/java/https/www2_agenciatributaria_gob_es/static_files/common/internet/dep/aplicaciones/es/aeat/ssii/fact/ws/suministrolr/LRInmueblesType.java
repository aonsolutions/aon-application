
package https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministrolr;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.IDFacturaExpedidaBCType;


/**
 * <p>Clase Java para LRInmueblesType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="LRInmueblesType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="IDFactura" type="{https://www2.agenciatributaria.gob.es/static_files/common/internet/dep/aplicaciones/es/aeat/ssii/fact/ws/SuministroInformacion.xsd}IDFacturaExpedidaBCType"/&gt;
 *         &lt;element name="DatosInmueble" type="{https://www2.agenciatributaria.gob.es/static_files/common/internet/dep/aplicaciones/es/aeat/ssii/fact/ws/SuministroLR.xsd}InmueblesAdicionalType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LRInmueblesType", propOrder = {
    "idFactura",
    "datosInmueble"
})
public class LRInmueblesType {

    @XmlElement(name = "IDFactura", required = true)
    protected IDFacturaExpedidaBCType idFactura;
    @XmlElement(name = "DatosInmueble", required = true)
    protected InmueblesAdicionalType datosInmueble;

    /**
     * Obtiene el valor de la propiedad idFactura.
     * 
     * @return
     *     possible object is
     *     {@link IDFacturaExpedidaBCType }
     *     
     */
    public IDFacturaExpedidaBCType getIDFactura() {
        return idFactura;
    }

    /**
     * Define el valor de la propiedad idFactura.
     * 
     * @param value
     *     allowed object is
     *     {@link IDFacturaExpedidaBCType }
     *     
     */
    public void setIDFactura(IDFacturaExpedidaBCType value) {
        this.idFactura = value;
    }

    /**
     * Obtiene el valor de la propiedad datosInmueble.
     * 
     * @return
     *     possible object is
     *     {@link InmueblesAdicionalType }
     *     
     */
    public InmueblesAdicionalType getDatosInmueble() {
        return datosInmueble;
    }

    /**
     * Define el valor de la propiedad datosInmueble.
     * 
     * @param value
     *     allowed object is
     *     {@link InmueblesAdicionalType }
     *     
     */
    public void setDatosInmueble(InmueblesAdicionalType value) {
        this.datosInmueble = value;
    }

}
