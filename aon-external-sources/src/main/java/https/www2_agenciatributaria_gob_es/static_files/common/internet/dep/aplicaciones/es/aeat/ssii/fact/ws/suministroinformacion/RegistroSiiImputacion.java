
package https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.consultalr.LRFiltroFactInformadasAgrupadasClienteType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.consultalr.LRFiltroFactInformadasAgrupadasProveedorType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.consultalr.LRFiltroFactInformadasClienteType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.consultalr.LRFiltroFactInformadasProveedorType;


/**
 *  Información básica que contienen los registros de imputacion 
 * 
 * <p>Clase Java para RegistroSiiImputacion complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="RegistroSiiImputacion"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="PeriodoImputacion"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="EjercicioImputacion" type="{https://www2.agenciatributaria.gob.es/static_files/common/internet/dep/aplicaciones/es/aeat/ssii/fact/ws/SuministroInformacion.xsd}YearType"/&gt;
 *                   &lt;element name="PeriodoImputacion" type="{https://www2.agenciatributaria.gob.es/static_files/common/internet/dep/aplicaciones/es/aeat/ssii/fact/ws/SuministroInformacion.xsd}TipoPeriodoType"/&gt;
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
@XmlType(name = "RegistroSiiImputacion", propOrder = {
    "periodoImputacion"
})
@XmlSeeAlso({
    LRFiltroFactInformadasClienteType.class,
    LRFiltroFactInformadasAgrupadasClienteType.class,
    LRFiltroFactInformadasProveedorType.class,
    LRFiltroFactInformadasAgrupadasProveedorType.class
})
public class RegistroSiiImputacion {

    @XmlElement(name = "PeriodoImputacion", required = true)
    protected RegistroSiiImputacion.PeriodoImputacion periodoImputacion;

    /**
     * Obtiene el valor de la propiedad periodoImputacion.
     * 
     * @return
     *     possible object is
     *     {@link RegistroSiiImputacion.PeriodoImputacion }
     *     
     */
    public RegistroSiiImputacion.PeriodoImputacion getPeriodoImputacion() {
        return periodoImputacion;
    }

    /**
     * Define el valor de la propiedad periodoImputacion.
     * 
     * @param value
     *     allowed object is
     *     {@link RegistroSiiImputacion.PeriodoImputacion }
     *     
     */
    public void setPeriodoImputacion(RegistroSiiImputacion.PeriodoImputacion value) {
        this.periodoImputacion = value;
    }


    /**
     *  Período de imputacion de la factura 
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
     *         &lt;element name="EjercicioImputacion" type="{https://www2.agenciatributaria.gob.es/static_files/common/internet/dep/aplicaciones/es/aeat/ssii/fact/ws/SuministroInformacion.xsd}YearType"/&gt;
     *         &lt;element name="PeriodoImputacion" type="{https://www2.agenciatributaria.gob.es/static_files/common/internet/dep/aplicaciones/es/aeat/ssii/fact/ws/SuministroInformacion.xsd}TipoPeriodoType"/&gt;
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
        "ejercicioImputacion",
        "periodoImputacion"
    })
    public static class PeriodoImputacion {

        @XmlElement(name = "EjercicioImputacion", required = true)
        protected String ejercicioImputacion;
        @XmlElement(name = "PeriodoImputacion", required = true)
        protected String periodoImputacion;

        /**
         * Obtiene el valor de la propiedad ejercicioImputacion.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getEjercicioImputacion() {
            return ejercicioImputacion;
        }

        /**
         * Define el valor de la propiedad ejercicioImputacion.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setEjercicioImputacion(String value) {
            this.ejercicioImputacion = value;
        }

        /**
         * Obtiene el valor de la propiedad periodoImputacion.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPeriodoImputacion() {
            return periodoImputacion;
        }

        /**
         * Define el valor de la propiedad periodoImputacion.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPeriodoImputacion(String value) {
            this.periodoImputacion = value;
        }

    }

}
