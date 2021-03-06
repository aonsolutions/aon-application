
package https.sii_araba_eus.documentos.suministroinformacion;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 *  Apunte correspondiente al libro de facturas recibidas. 
 * 
 * <p>Clase Java para FacturaRecibidaType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="FacturaRecibidaType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}FacturaType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="DesgloseFactura" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}DesgloseFacturaRecibidasType"/&gt;
 *         &lt;element name="Contraparte" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}PersonaFisicaJuridicaType"/&gt;
 *         &lt;element name="FechaRegContable" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}fecha"/&gt;
 *         &lt;element name="CuotaDeducible" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}ImporteSgn12.2Type"/&gt;
 *         &lt;element name="ADeducirEnPeriodoPosterior" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}DeducirEnPeriodoPosteriorType" minOccurs="0"/&gt;
 *         &lt;element name="EjercicioDeduccion" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}YearType" minOccurs="0"/&gt;
 *         &lt;element name="PeriodoDeduccion" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}TipoPeriodoType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FacturaRecibidaType", propOrder = {
    "desgloseFactura",
    "contraparte",
    "fechaRegContable",
    "cuotaDeducible",
    "aDeducirEnPeriodoPosterior",
    "ejercicioDeduccion",
    "periodoDeduccion"
})
public class FacturaRecibidaType
    extends FacturaType
{

    @XmlElement(name = "DesgloseFactura", required = true)
    protected DesgloseFacturaRecibidasType desgloseFactura;
    @XmlElement(name = "Contraparte", required = true)
    protected PersonaFisicaJuridicaType contraparte;
    @XmlElement(name = "FechaRegContable", required = true)
    protected String fechaRegContable;
    @XmlElement(name = "CuotaDeducible", required = true)
    protected String cuotaDeducible;
    @XmlElement(name = "ADeducirEnPeriodoPosterior")
    @XmlSchemaType(name = "string")
    protected DeducirEnPeriodoPosteriorType aDeducirEnPeriodoPosterior;
    @XmlElement(name = "EjercicioDeduccion")
    protected String ejercicioDeduccion;
    @XmlElement(name = "PeriodoDeduccion")
    protected String periodoDeduccion;

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
     * Obtiene el valor de la propiedad contraparte.
     * 
     * @return
     *     possible object is
     *     {@link PersonaFisicaJuridicaType }
     *     
     */
    public PersonaFisicaJuridicaType getContraparte() {
        return contraparte;
    }

    /**
     * Define el valor de la propiedad contraparte.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonaFisicaJuridicaType }
     *     
     */
    public void setContraparte(PersonaFisicaJuridicaType value) {
        this.contraparte = value;
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
     * Obtiene el valor de la propiedad cuotaDeducible.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCuotaDeducible() {
        return cuotaDeducible;
    }

    /**
     * Define el valor de la propiedad cuotaDeducible.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCuotaDeducible(String value) {
        this.cuotaDeducible = value;
    }

    /**
     * Obtiene el valor de la propiedad aDeducirEnPeriodoPosterior.
     * 
     * @return
     *     possible object is
     *     {@link DeducirEnPeriodoPosteriorType }
     *     
     */
    public DeducirEnPeriodoPosteriorType getADeducirEnPeriodoPosterior() {
        return aDeducirEnPeriodoPosterior;
    }

    /**
     * Define el valor de la propiedad aDeducirEnPeriodoPosterior.
     * 
     * @param value
     *     allowed object is
     *     {@link DeducirEnPeriodoPosteriorType }
     *     
     */
    public void setADeducirEnPeriodoPosterior(DeducirEnPeriodoPosteriorType value) {
        this.aDeducirEnPeriodoPosterior = value;
    }

    /**
     * Obtiene el valor de la propiedad ejercicioDeduccion.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEjercicioDeduccion() {
        return ejercicioDeduccion;
    }

    /**
     * Define el valor de la propiedad ejercicioDeduccion.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEjercicioDeduccion(String value) {
        this.ejercicioDeduccion = value;
    }

    /**
     * Obtiene el valor de la propiedad periodoDeduccion.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPeriodoDeduccion() {
        return periodoDeduccion;
    }

    /**
     * Define el valor de la propiedad periodoDeduccion.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPeriodoDeduccion(String value) {
        this.periodoDeduccion = value;
    }

}
