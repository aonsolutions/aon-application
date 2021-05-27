
package eus.bizkaia.ogasuna.sii.documentos.respuestaconsultalr;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import eus.bizkaia.ogasuna.sii.documentos.suministroinformacion.DesgloseFacturaRecibidasType;


/**
 *  Datos de factura Informada por el cliente  
 * 
 * <p>Clase Java para FacturaRespuestaInformadaClienteType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="FacturaRespuestaInformadaClienteType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.bizkaia.eus/ogasuna/sii/documentos/RespuestaConsultaLR.xsd}FacturaRespuestaType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="DesgloseFactura" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}DesgloseFacturaRecibidasType"/&gt;
 *         &lt;element name="FechaRegContable" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}fecha"/&gt;
 *         &lt;element name="Pagos" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/RespuestaConsultaLR.xsd}FacturaARType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FacturaRespuestaInformadaClienteType", propOrder = {
    "desgloseFactura",
    "fechaRegContable",
    "pagos"
})
public class FacturaRespuestaInformadaClienteType
    extends FacturaRespuestaType
{

    @XmlElement(name = "DesgloseFactura", required = true)
    protected DesgloseFacturaRecibidasType desgloseFactura;
    @XmlElement(name = "FechaRegContable", required = true)
    protected String fechaRegContable;
    @XmlElement(name = "Pagos", required = true)
    @XmlSchemaType(name = "string")
    protected FacturaARType pagos;

    /**
     * Obtiene el valor de la propiedad desgloseFactura.
     * 
     * @return
     *     possible object is
     *     {@link DesgloseFacturaRecibidasType }
     *     
     */
    public DesgloseFacturaRecibidasType getDesgloseFactura() {
        return desgloseFactura;
    }

    /**
     * Define el valor de la propiedad desgloseFactura.
     * 
     * @param value
     *     allowed object is
     *     {@link DesgloseFacturaRecibidasType }
     *     
     */
    public void setDesgloseFactura(DesgloseFacturaRecibidasType value) {
        this.desgloseFactura = value;
    }

    /**
     * Obtiene el valor de la propiedad fechaRegContable.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFechaRegContable() {
        return fechaRegContable;
    }

    /**
     * Define el valor de la propiedad fechaRegContable.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFechaRegContable(String value) {
        this.fechaRegContable = value;
    }

    /**
     * Obtiene el valor de la propiedad pagos.
     * 
     * @return
     *     possible object is
     *     {@link FacturaARType }
     *     
     */
    public FacturaARType getPagos() {
        return pagos;
    }

    /**
     * Define el valor de la propiedad pagos.
     * 
     * @param value
     *     allowed object is
     *     {@link FacturaARType }
     *     
     */
    public void setPagos(FacturaARType value) {
        this.pagos = value;
    }

}
