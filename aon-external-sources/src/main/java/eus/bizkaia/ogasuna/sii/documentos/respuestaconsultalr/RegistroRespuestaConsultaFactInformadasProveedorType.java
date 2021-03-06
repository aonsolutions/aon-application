
package eus.bizkaia.ogasuna.sii.documentos.respuestaconsultalr;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import eus.bizkaia.ogasuna.sii.documentos.suministroinformacion.IDFacturaImputacionType;
import eus.bizkaia.ogasuna.sii.documentos.suministroinformacion.PersonaFisicaJuridicaUnicaESType;


/**
 * <p>Clase Java para RegistroRespuestaConsultaFactInformadasProveedorType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="RegistroRespuestaConsultaFactInformadasProveedorType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="IDFactura" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}IDFacturaImputacionType"/&gt;
 *         &lt;element name="PeriodoLiquidacion"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="Ejercicio" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}YearType"/&gt;
 *                   &lt;element name="Periodo" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}TipoPeriodoType"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="DatosFacturaInformadaProveedor" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/RespuestaConsultaLR.xsd}FacturaRespuestaInformadaProveedorType"/&gt;
 *         &lt;element name="Proveedor" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}PersonaFisicaJuridicaUnicaESType"/&gt;
 *         &lt;element name="EstadoFactura" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/RespuestaConsultaLR.xsd}EstadoFacturaImputacionType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RegistroRespuestaConsultaFactInformadasProveedorType", propOrder = {
    "idFactura",
    "periodoLiquidacion",
    "datosFacturaInformadaProveedor",
    "proveedor",
    "estadoFactura"
})
public class RegistroRespuestaConsultaFactInformadasProveedorType {

    @XmlElement(name = "IDFactura", required = true)
    protected IDFacturaImputacionType idFactura;
    @XmlElement(name = "PeriodoLiquidacion", required = true)
    protected RegistroRespuestaConsultaFactInformadasProveedorType.PeriodoLiquidacion periodoLiquidacion;
    @XmlElement(name = "DatosFacturaInformadaProveedor", required = true)
    protected FacturaRespuestaInformadaProveedorType datosFacturaInformadaProveedor;
    @XmlElement(name = "Proveedor", required = true)
    protected PersonaFisicaJuridicaUnicaESType proveedor;
    @XmlElement(name = "EstadoFactura", required = true)
    protected EstadoFacturaImputacionType estadoFactura;

    /**
     * Obtiene el valor de la propiedad idFactura.
     * 
     * @return
     *     possible object is
     *     {@link IDFacturaImputacionType }
     *     
     */
    public IDFacturaImputacionType getIDFactura() {
        return idFactura;
    }

    /**
     * Define el valor de la propiedad idFactura.
     * 
     * @param value
     *     allowed object is
     *     {@link IDFacturaImputacionType }
     *     
     */
    public void setIDFactura(IDFacturaImputacionType value) {
        this.idFactura = value;
    }

    /**
     * Obtiene el valor de la propiedad periodoLiquidacion.
     * 
     * @return
     *     possible object is
     *     {@link RegistroRespuestaConsultaFactInformadasProveedorType.PeriodoLiquidacion }
     *     
     */
    public RegistroRespuestaConsultaFactInformadasProveedorType.PeriodoLiquidacion getPeriodoLiquidacion() {
        return periodoLiquidacion;
    }

    /**
     * Define el valor de la propiedad periodoLiquidacion.
     * 
     * @param value
     *     allowed object is
     *     {@link RegistroRespuestaConsultaFactInformadasProveedorType.PeriodoLiquidacion }
     *     
     */
    public void setPeriodoLiquidacion(RegistroRespuestaConsultaFactInformadasProveedorType.PeriodoLiquidacion value) {
        this.periodoLiquidacion = value;
    }

    /**
     * Obtiene el valor de la propiedad datosFacturaInformadaProveedor.
     * 
     * @return
     *     possible object is
     *     {@link FacturaRespuestaInformadaProveedorType }
     *     
     */
    public FacturaRespuestaInformadaProveedorType getDatosFacturaInformadaProveedor() {
        return datosFacturaInformadaProveedor;
    }

    /**
     * Define el valor de la propiedad datosFacturaInformadaProveedor.
     * 
     * @param value
     *     allowed object is
     *     {@link FacturaRespuestaInformadaProveedorType }
     *     
     */
    public void setDatosFacturaInformadaProveedor(FacturaRespuestaInformadaProveedorType value) {
        this.datosFacturaInformadaProveedor = value;
    }

    /**
     * Obtiene el valor de la propiedad proveedor.
     * 
     * @return
     *     possible object is
     *     {@link PersonaFisicaJuridicaUnicaESType }
     *     
     */
    public PersonaFisicaJuridicaUnicaESType getProveedor() {
        return proveedor;
    }

    /**
     * Define el valor de la propiedad proveedor.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonaFisicaJuridicaUnicaESType }
     *     
     */
    public void setProveedor(PersonaFisicaJuridicaUnicaESType value) {
        this.proveedor = value;
    }

    /**
     * Obtiene el valor de la propiedad estadoFactura.
     * 
     * @return
     *     possible object is
     *     {@link EstadoFacturaImputacionType }
     *     
     */
    public EstadoFacturaImputacionType getEstadoFactura() {
        return estadoFactura;
    }

    /**
     * Define el valor de la propiedad estadoFactura.
     * 
     * @param value
     *     allowed object is
     *     {@link EstadoFacturaImputacionType }
     *     
     */
    public void setEstadoFactura(EstadoFacturaImputacionType value) {
        this.estadoFactura = value;
    }


    /**
     *  Período al que corresponden los apuntes 
     * 
     * <p>Clase Java para anonymous complex type.
     * 
     * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;sequence&gt;
     *         &lt;element name="Ejercicio" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}YearType"/&gt;
     *         &lt;element name="Periodo" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}TipoPeriodoType"/&gt;
     *       &lt;/sequence&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "ejercicio",
        "periodo"
    })
    public static class PeriodoLiquidacion {

        @XmlElement(name = "Ejercicio", required = true)
        protected String ejercicio;
        @XmlElement(name = "Periodo", required = true)
        protected String periodo;

        /**
         * Obtiene el valor de la propiedad ejercicio.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getEjercicio() {
            return ejercicio;
        }

        /**
         * Define el valor de la propiedad ejercicio.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setEjercicio(String value) {
            this.ejercicio = value;
        }

        /**
         * Obtiene el valor de la propiedad periodo.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPeriodo() {
            return periodo;
        }

        /**
         * Define el valor de la propiedad periodo.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPeriodo(String value) {
            this.periodo = value;
        }

    }

}
