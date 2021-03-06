
package eus.bizkaia.ogasuna.sii.documentos.suministroinformacion;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * Tipo de Operacion Sujeta
 * 
 * <p>Clase Java para SujetaPrestacionType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="SujetaPrestacionType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Exenta" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="DetalleExenta" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}DetalleExentaType" maxOccurs="7"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="NoExenta" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="TipoNoExenta" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}TipoOperacionSujetaNoExentaType"/&gt;
 *                   &lt;element name="DesgloseIVA"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;sequence&gt;
 *                             &lt;element name="DetalleIVA" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}DetalleIVAEmitidaPrestacionType" maxOccurs="6"/&gt;
 *                           &lt;/sequence&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SujetaPrestacionType", propOrder = {
    "exenta",
    "noExenta"
})
public class SujetaPrestacionType {

    @XmlElement(name = "Exenta")
    protected SujetaPrestacionType.Exenta exenta;
    @XmlElement(name = "NoExenta")
    protected SujetaPrestacionType.NoExenta noExenta;

    /**
     * Obtiene el valor de la propiedad exenta.
     * 
     * @return
     *     possible object is
     *     {@link SujetaPrestacionType.Exenta }
     *     
     */
    public SujetaPrestacionType.Exenta getExenta() {
        return exenta;
    }

    /**
     * Define el valor de la propiedad exenta.
     * 
     * @param value
     *     allowed object is
     *     {@link SujetaPrestacionType.Exenta }
     *     
     */
    public void setExenta(SujetaPrestacionType.Exenta value) {
        this.exenta = value;
    }

    /**
     * Obtiene el valor de la propiedad noExenta.
     * 
     * @return
     *     possible object is
     *     {@link SujetaPrestacionType.NoExenta }
     *     
     */
    public SujetaPrestacionType.NoExenta getNoExenta() {
        return noExenta;
    }

    /**
     * Define el valor de la propiedad noExenta.
     * 
     * @param value
     *     allowed object is
     *     {@link SujetaPrestacionType.NoExenta }
     *     
     */
    public void setNoExenta(SujetaPrestacionType.NoExenta value) {
        this.noExenta = value;
    }


    /**
     * <p>Clase Java para anonymous complex type.
     * 
     * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;sequence&gt;
     *         &lt;element name="DetalleExenta" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}DetalleExentaType" maxOccurs="7"/&gt;
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
        "detalleExenta"
    })
    public static class Exenta {

        @XmlElement(name = "DetalleExenta", required = true)
        protected List<DetalleExentaType> detalleExenta;

        /**
         * Gets the value of the detalleExenta property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the detalleExenta property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getDetalleExenta().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link DetalleExentaType }
         * 
         * 
         */
        public List<DetalleExentaType> getDetalleExenta() {
            if (detalleExenta == null) {
                detalleExenta = new ArrayList<DetalleExentaType>();
            }
            return this.detalleExenta;
        }

    }


    /**
     * <p>Clase Java para anonymous complex type.
     * 
     * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;sequence&gt;
     *         &lt;element name="TipoNoExenta" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}TipoOperacionSujetaNoExentaType"/&gt;
     *         &lt;element name="DesgloseIVA"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;sequence&gt;
     *                   &lt;element name="DetalleIVA" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}DetalleIVAEmitidaPrestacionType" maxOccurs="6"/&gt;
     *                 &lt;/sequence&gt;
     *               &lt;/restriction&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
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
        "tipoNoExenta",
        "desgloseIVA"
    })
    public static class NoExenta {

        @XmlElement(name = "TipoNoExenta", required = true)
        @XmlSchemaType(name = "string")
        protected TipoOperacionSujetaNoExentaType tipoNoExenta;
        @XmlElement(name = "DesgloseIVA", required = true)
        protected SujetaPrestacionType.NoExenta.DesgloseIVA desgloseIVA;

        /**
         * Obtiene el valor de la propiedad tipoNoExenta.
         * 
         * @return
         *     possible object is
         *     {@link TipoOperacionSujetaNoExentaType }
         *     
         */
        public TipoOperacionSujetaNoExentaType getTipoNoExenta() {
            return tipoNoExenta;
        }

        /**
         * Define el valor de la propiedad tipoNoExenta.
         * 
         * @param value
         *     allowed object is
         *     {@link TipoOperacionSujetaNoExentaType }
         *     
         */
        public void setTipoNoExenta(TipoOperacionSujetaNoExentaType value) {
            this.tipoNoExenta = value;
        }

        /**
         * Obtiene el valor de la propiedad desgloseIVA.
         * 
         * @return
         *     possible object is
         *     {@link SujetaPrestacionType.NoExenta.DesgloseIVA }
         *     
         */
        public SujetaPrestacionType.NoExenta.DesgloseIVA getDesgloseIVA() {
            return desgloseIVA;
        }

        /**
         * Define el valor de la propiedad desgloseIVA.
         * 
         * @param value
         *     allowed object is
         *     {@link SujetaPrestacionType.NoExenta.DesgloseIVA }
         *     
         */
        public void setDesgloseIVA(SujetaPrestacionType.NoExenta.DesgloseIVA value) {
            this.desgloseIVA = value;
        }


        /**
         * Desglose por tipos de iva
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
         *         &lt;element name="DetalleIVA" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}DetalleIVAEmitidaPrestacionType" maxOccurs="6"/&gt;
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
            "detalleIVA"
        })
        public static class DesgloseIVA {

            @XmlElement(name = "DetalleIVA", required = true)
            protected List<DetalleIVAEmitidaPrestacionType> detalleIVA;

            /**
             * Gets the value of the detalleIVA property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the detalleIVA property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getDetalleIVA().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link DetalleIVAEmitidaPrestacionType }
             * 
             * 
             */
            public List<DetalleIVAEmitidaPrestacionType> getDetalleIVA() {
                if (detalleIVA == null) {
                    detalleIVA = new ArrayList<DetalleIVAEmitidaPrestacionType>();
                }
                return this.detalleIVA;
            }

        }

    }

}
