
package eus.bizkaia.ogasuna.sii.documentos.respuestaconsultalr;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para EstadoFactura2Type complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="EstadoFactura2Type"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="TimestampUltimaModificacion" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}Timestamp"/&gt;
 *         &lt;element name="EstadoRegistro" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/RespuestaConsultaLR.xsd}EstadoRegistroSIIType"/&gt;
 *         &lt;element name="CodigoErrorRegistro" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/RespuestaConsultaLR.xsd}ErrorDetalleType" minOccurs="0"/&gt;
 *         &lt;element name="DescripcionErrorRegistro" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}TextMax500Type" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EstadoFactura2Type", propOrder = {
    "timestampUltimaModificacion",
    "estadoRegistro",
    "codigoErrorRegistro",
    "descripcionErrorRegistro"
})
public class EstadoFactura2Type {

    @XmlElement(name = "TimestampUltimaModificacion", required = true)
    protected String timestampUltimaModificacion;
    @XmlElement(name = "EstadoRegistro", required = true)
    @XmlSchemaType(name = "string")
    protected EstadoRegistroSIIType estadoRegistro;
    @XmlElement(name = "CodigoErrorRegistro")
    protected BigInteger codigoErrorRegistro;
    @XmlElement(name = "DescripcionErrorRegistro")
    protected String descripcionErrorRegistro;

    /**
     * Obtiene el valor de la propiedad timestampUltimaModificacion.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTimestampUltimaModificacion() {
        return timestampUltimaModificacion;
    }

    /**
     * Define el valor de la propiedad timestampUltimaModificacion.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTimestampUltimaModificacion(String value) {
        this.timestampUltimaModificacion = value;
    }

    /**
     * Obtiene el valor de la propiedad estadoRegistro.
     * 
     * @return
     *     possible object is
     *     {@link EstadoRegistroSIIType }
     *     
     */
    public EstadoRegistroSIIType getEstadoRegistro() {
        return estadoRegistro;
    }

    /**
     * Define el valor de la propiedad estadoRegistro.
     * 
     * @param value
     *     allowed object is
     *     {@link EstadoRegistroSIIType }
     *     
     */
    public void setEstadoRegistro(EstadoRegistroSIIType value) {
        this.estadoRegistro = value;
    }

    /**
     * Obtiene el valor de la propiedad codigoErrorRegistro.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCodigoErrorRegistro() {
        return codigoErrorRegistro;
    }

    /**
     * Define el valor de la propiedad codigoErrorRegistro.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCodigoErrorRegistro(BigInteger value) {
        this.codigoErrorRegistro = value;
    }

    /**
     * Obtiene el valor de la propiedad descripcionErrorRegistro.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescripcionErrorRegistro() {
        return descripcionErrorRegistro;
    }

    /**
     * Define el valor de la propiedad descripcionErrorRegistro.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescripcionErrorRegistro(String value) {
        this.descripcionErrorRegistro = value;
    }

}
