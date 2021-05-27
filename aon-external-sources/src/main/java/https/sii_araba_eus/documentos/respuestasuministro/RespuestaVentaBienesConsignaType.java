
package https.sii_araba_eus.documentos.respuestasuministro;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import https.sii_araba_eus.documentos.suministroinformacion.IdRegistroDeclaradoType;
import https.sii_araba_eus.documentos.suministroinformacion.RegistroDuplicadoType;


/**
 *  Respuesta a un envío Sii 
 * 
 * <p>Clase Java para RespuestaVentaBienesConsignaType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="RespuestaVentaBienesConsignaType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="IdRegistroDeclarado" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}IdRegistroDeclaradoType"/&gt;
 *         &lt;element name="RefExterna" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}TextMax60Type" minOccurs="0"/&gt;
 *         &lt;element name="EstadoRegistro" type="{https://sii.araba.eus/documentos/RespuestaSuministro.xsd}EstadoRegistroType"/&gt;
 *         &lt;element name="CodigoErrorRegistro" type="{https://sii.araba.eus/documentos/RespuestaSuministro.xsd}ErrorDetalleType" minOccurs="0"/&gt;
 *         &lt;element name="DescripcionErrorRegistro" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}TextMax500Type" minOccurs="0"/&gt;
 *         &lt;element name="CSV" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="RegistroDuplicado" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}RegistroDuplicadoType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RespuestaVentaBienesConsignaType", propOrder = {
    "idRegistroDeclarado",
    "refExterna",
    "estadoRegistro",
    "codigoErrorRegistro",
    "descripcionErrorRegistro",
    "csv",
    "registroDuplicado"
})
public class RespuestaVentaBienesConsignaType {

    @XmlElement(name = "IdRegistroDeclarado", required = true)
    protected IdRegistroDeclaradoType idRegistroDeclarado;
    @XmlElement(name = "RefExterna")
    protected String refExterna;
    @XmlElement(name = "EstadoRegistro", required = true)
    @XmlSchemaType(name = "string")
    protected EstadoRegistroType estadoRegistro;
    @XmlElement(name = "CodigoErrorRegistro")
    protected BigInteger codigoErrorRegistro;
    @XmlElement(name = "DescripcionErrorRegistro")
    protected String descripcionErrorRegistro;
    @XmlElement(name = "CSV")
    protected String csv;
    @XmlElement(name = "RegistroDuplicado")
    protected RegistroDuplicadoType registroDuplicado;

    /**
     * Obtiene el valor de la propiedad idRegistroDeclarado.
     * 
     * @return
     *     possible object is
     *     {@link IdRegistroDeclaradoType }
     *     
     */
    public IdRegistroDeclaradoType getIdRegistroDeclarado() {
        return idRegistroDeclarado;
    }

    /**
     * Define el valor de la propiedad idRegistroDeclarado.
     * 
     * @param value
     *     allowed object is
     *     {@link IdRegistroDeclaradoType }
     *     
     */
    public void setIdRegistroDeclarado(IdRegistroDeclaradoType value) {
        this.idRegistroDeclarado = value;
    }

    /**
     * Obtiene el valor de la propiedad refExterna.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRefExterna() {
        return refExterna;
    }

    /**
     * Define el valor de la propiedad refExterna.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRefExterna(String value) {
        this.refExterna = value;
    }

    /**
     * Obtiene el valor de la propiedad estadoRegistro.
     * 
     * @return
     *     possible object is
     *     {@link EstadoRegistroType }
     *     
     */
    public EstadoRegistroType getEstadoRegistro() {
        return estadoRegistro;
    }

    /**
     * Define el valor de la propiedad estadoRegistro.
     * 
     * @param value
     *     allowed object is
     *     {@link EstadoRegistroType }
     *     
     */
    public void setEstadoRegistro(EstadoRegistroType value) {
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

    /**
     * Obtiene el valor de la propiedad csv.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCSV() {
        return csv;
    }

    /**
     * Define el valor de la propiedad csv.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCSV(String value) {
        this.csv = value;
    }

    /**
     * Obtiene el valor de la propiedad registroDuplicado.
     * 
     * @return
     *     possible object is
     *     {@link RegistroDuplicadoType }
     *     
     */
    public RegistroDuplicadoType getRegistroDuplicado() {
        return registroDuplicado;
    }

    /**
     * Define el valor de la propiedad registroDuplicado.
     * 
     * @param value
     *     allowed object is
     *     {@link RegistroDuplicadoType }
     *     
     */
    public void setRegistroDuplicado(RegistroDuplicadoType value) {
        this.registroDuplicado = value;
    }

}
