
package eus.bizkaia.ogasuna.sii.documentos.suministrolr;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import eus.bizkaia.ogasuna.sii.documentos.suministroinformacion.IDFacturaRecibidaNombreBCType;
import eus.bizkaia.ogasuna.sii.documentos.suministroinformacion.PagosType;


/**
 * <p>Clase Java para LRPagosEmitidasType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="LRPagosEmitidasType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="IDFactura" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}IDFacturaRecibidaNombreBCType"/&gt;
 *         &lt;element name="Pagos" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}PagosType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LRPagosEmitidasType", propOrder = {
    "idFactura",
    "pagos"
})
public class LRPagosEmitidasType {

    @XmlElement(name = "IDFactura", required = true)
    protected IDFacturaRecibidaNombreBCType idFactura;
    @XmlElement(name = "Pagos", required = true)
    protected PagosType pagos;

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
     * Obtiene el valor de la propiedad pagos.
     * 
     * @return
     *     possible object is
     *     {@link PagosType }
     *     
     */
    public PagosType getPagos() {
        return pagos;
    }

    /**
     * Define el valor de la propiedad pagos.
     * 
     * @param value
     *     allowed object is
     *     {@link PagosType }
     *     
     */
    public void setPagos(PagosType value) {
        this.pagos = value;
    }

}
