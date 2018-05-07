//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.8-b130911.1802 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2015.06.05 a las 01:17:09 PM CEST 
//


package com.esferalia.aon.occam.impl.jooq.dao.d2_deposit;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para anonymous complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="Cabecera">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;all>
 *                   &lt;element name="CIF" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="RazonSocial" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="TipoCuestionario">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;enumeration value="Normal"/>
 *                         &lt;enumeration value="Abreviado"/>
 *                         &lt;enumeration value="Pymes"/>
 *                         &lt;enumeration value="Mixto"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="IdiomaCuestionario">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;enumeration value="Castellano"/>
 *                         &lt;enumeration value="Catalan"/>
 *                         &lt;enumeration value="Valenciano"/>
 *                         &lt;enumeration value="Gallego"/>
 *                         &lt;enumeration value="Euskera"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="MemoriaNormalizada" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *                   &lt;element name="Ejercicio">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}integer">
 *                         &lt;minInclusive value="2008"/>
 *                         &lt;totalDigits value="4"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                 &lt;/all>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Claves">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Clave" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;all>
 *                             &lt;element name="Codigo" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *                             &lt;element name="Valor" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                           &lt;/all>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {

})
@XmlRootElement(name = "Esquema")
public class Esquema {
	
	protected String error;
	
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	
    @XmlElement(name = "Cabecera", required = true)
    protected Esquema.Cabecera cabecera;
    @XmlElement(name = "Claves", required = true)
    protected Esquema.Claves claves;

    /**
     * Obtiene el valor de la propiedad cabecera.
     * 
     * @return
     *     possible object is
     *     {@link Esquema.Cabecera }
     *     
     */
    public Esquema.Cabecera getCabecera() {
        return cabecera;
    }

    /**
     * Define el valor de la propiedad cabecera.
     * 
     * @param value
     *     allowed object is
     *     {@link Esquema.Cabecera }
     *     
     */
    public void setCabecera(Esquema.Cabecera value) {
        this.cabecera = value;
    }

    /**
     * Obtiene el valor de la propiedad claves.
     * 
     * @return
     *     possible object is
     *     {@link Esquema.Claves }
     *     
     */
    public Esquema.Claves getClaves() {
        return claves;
    }

    /**
     * Define el valor de la propiedad claves.
     * 
     * @param value
     *     allowed object is
     *     {@link Esquema.Claves }
     *     
     */
    public void setClaves(Esquema.Claves value) {
        this.claves = value;
    }


    /**
     * <p>Clase Java para anonymous complex type.
     * 
     * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;all>
     *         &lt;element name="CIF" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="RazonSocial" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="TipoCuestionario">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;enumeration value="Normal"/>
     *               &lt;enumeration value="Abreviado"/>
     *               &lt;enumeration value="Pymes"/>
     *               &lt;enumeration value="Mixto"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="IdiomaCuestionario">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;enumeration value="Castellano"/>
     *               &lt;enumeration value="Catalan"/>
     *               &lt;enumeration value="Valenciano"/>
     *               &lt;enumeration value="Gallego"/>
     *               &lt;enumeration value="Euskera"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="MemoriaNormalizada" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
     *         &lt;element name="Ejercicio">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}integer">
     *               &lt;minInclusive value="2008"/>
     *               &lt;totalDigits value="4"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *       &lt;/all>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {

    })
    public static class Cabecera {

        @XmlElement(name = "CIF", required = true)
        protected String cif;
        @XmlElement(name = "RazonSocial", required = true)
        protected String razonSocial;
        @XmlElement(name = "TipoCuestionario", required = true)
        protected String tipoCuestionario;
        @XmlElement(name = "IdiomaCuestionario", required = true)
        protected String idiomaCuestionario;
        @XmlElement(name = "MemoriaNormalizada")
        protected boolean memoriaNormalizada;
        @XmlElement(name = "Ejercicio", required = true)
        protected BigInteger ejercicio;

        /**
         * Obtiene el valor de la propiedad cif.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getCIF() {
            return cif;
        }

        /**
         * Define el valor de la propiedad cif.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setCIF(String value) {
            this.cif = value;
        }

        /**
         * Obtiene el valor de la propiedad razonSocial.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getRazonSocial() {
            return razonSocial;
        }

        /**
         * Define el valor de la propiedad razonSocial.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setRazonSocial(String value) {
            this.razonSocial = value;
        }

        /**
         * Obtiene el valor de la propiedad tipoCuestionario.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTipoCuestionario() {
            return tipoCuestionario;
        }

        /**
         * Define el valor de la propiedad tipoCuestionario.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTipoCuestionario(String value) {
            this.tipoCuestionario = value;
        }

        /**
         * Obtiene el valor de la propiedad idiomaCuestionario.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getIdiomaCuestionario() {
            return idiomaCuestionario;
        }

        /**
         * Define el valor de la propiedad idiomaCuestionario.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setIdiomaCuestionario(String value) {
            this.idiomaCuestionario = value;
        }

        /**
         * Obtiene el valor de la propiedad memoriaNormalizada.
         * 
         */
        public boolean isMemoriaNormalizada() {
            return memoriaNormalizada;
        }

        /**
         * Define el valor de la propiedad memoriaNormalizada.
         * 
         */
        public void setMemoriaNormalizada(boolean value) {
            this.memoriaNormalizada = value;
        }

        /**
         * Obtiene el valor de la propiedad ejercicio.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getEjercicio() {
            return ejercicio;
        }

        /**
         * Define el valor de la propiedad ejercicio.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setEjercicio(BigInteger value) {
            this.ejercicio = value;
        }

    }


    /**
     * <p>Clase Java para anonymous complex type.
     * 
     * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="Clave" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;all>
     *                   &lt;element name="Codigo" type="{http://www.w3.org/2001/XMLSchema}integer"/>
     *                   &lt;element name="Valor" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                 &lt;/all>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "clave"
    })
    public static class Claves {

        @XmlElement(name = "Clave")
        protected List<Esquema.Claves.Clave> clave;

        /**
         * Gets the value of the clave property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the clave property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getClave().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Esquema.Claves.Clave }
         * 
         * 
         */
        public List<Esquema.Claves.Clave> getClave() {
            if (clave == null) {
                clave = new ArrayList<Esquema.Claves.Clave>();
            }
            return this.clave;
        }


        /**
         * <p>Clase Java para anonymous complex type.
         * 
         * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;all>
         *         &lt;element name="Codigo" type="{http://www.w3.org/2001/XMLSchema}integer"/>
         *         &lt;element name="Valor" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *       &lt;/all>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {

        })
        public static class Clave {

            @XmlElement(name = "Codigo", required = true)
            protected BigInteger codigo;
            @XmlElement(name = "Valor", required = true)
            protected String valor;

            /**
             * Obtiene el valor de la propiedad codigo.
             * 
             * @return
             *     possible object is
             *     {@link BigInteger }
             *     
             */
            public BigInteger getCodigo() {
                return codigo;
            }

            /**
             * Define el valor de la propiedad codigo.
             * 
             * @param value
             *     allowed object is
             *     {@link BigInteger }
             *     
             */
            public void setCodigo(BigInteger value) {
                this.codigo = value;
            }

            /**
             * Obtiene el valor de la propiedad valor.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getValor() {
                return valor;
            }

            /**
             * Define el valor de la propiedad valor.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setValor(String value) {
                this.valor = value;
            }

        }

    }

}
