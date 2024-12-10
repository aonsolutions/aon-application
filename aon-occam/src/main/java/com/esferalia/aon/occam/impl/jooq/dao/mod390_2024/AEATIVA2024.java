//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.3.1-b171012.0423 
// Visite <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2024.11.25 a las 11:44:35 AM CET 
//


package com.esferalia.aon.occam.impl.jooq.dao.mod390_2024;

import java.math.BigDecimal;
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
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="IdDoc" type="{}tipo_Doc"/&gt;
 *         &lt;element name="DatIdent"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;choice&gt;
 *                     &lt;element name="PersFisica" type="{}tipo_PersonaFisica"/&gt;
 *                     &lt;element name="PersJuridica" type="{}tipo_PersonaJuridica"/&gt;
 *                   &lt;/choice&gt;
 *                   &lt;element name="Telefono" type="{}tipo_Telefono" minOccurs="0"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="Devengo"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="Ejercicio" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *                   &lt;choice minOccurs="0"&gt;
 *                     &lt;element name="DecSustitutiva"&gt;
 *                       &lt;complexType&gt;
 *                         &lt;complexContent&gt;
 *                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;/restriction&gt;
 *                         &lt;/complexContent&gt;
 *                       &lt;/complexType&gt;
 *                     &lt;/element&gt;
 *                     &lt;element name="DecSustitutivaRectifica"&gt;
 *                       &lt;complexType&gt;
 *                         &lt;complexContent&gt;
 *                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;/restriction&gt;
 *                         &lt;/complexContent&gt;
 *                       &lt;/complexType&gt;
 *                     &lt;/element&gt;
 *                   &lt;/choice&gt;
 *                   &lt;element name="JustDecAnterior" type="{}tipo_Justificante" minOccurs="0"/&gt;
 *                   &lt;element name="RegDevMensual" minOccurs="0"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                   &lt;element name="RegGrupoEntidades" type="{}tipo_GrupoEntidades" minOccurs="0"/&gt;
 *                   &lt;choice&gt;
 *                     &lt;element name="ConcursoAcreedores_SI" type="{}tipo_ConcursoUltPer"/&gt;
 *                     &lt;element name="ConcursoAcreedores_NO"&gt;
 *                       &lt;complexType&gt;
 *                         &lt;complexContent&gt;
 *                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;/restriction&gt;
 *                         &lt;/complexContent&gt;
 *                       &lt;/complexType&gt;
 *                     &lt;/element&gt;
 *                   &lt;/choice&gt;
 *                   &lt;choice&gt;
 *                     &lt;element name="RegCriterioCaja_SI"&gt;
 *                       &lt;complexType&gt;
 *                         &lt;complexContent&gt;
 *                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;/restriction&gt;
 *                         &lt;/complexContent&gt;
 *                       &lt;/complexType&gt;
 *                     &lt;/element&gt;
 *                     &lt;element name="RegCriterioCaja_NO"&gt;
 *                       &lt;complexType&gt;
 *                         &lt;complexContent&gt;
 *                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;/restriction&gt;
 *                         &lt;/complexContent&gt;
 *                       &lt;/complexType&gt;
 *                     &lt;/element&gt;
 *                   &lt;/choice&gt;
 *                   &lt;choice&gt;
 *                     &lt;element name="DestRegCriterioCaja_SI"&gt;
 *                       &lt;complexType&gt;
 *                         &lt;complexContent&gt;
 *                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;/restriction&gt;
 *                         &lt;/complexContent&gt;
 *                       &lt;/complexType&gt;
 *                     &lt;/element&gt;
 *                     &lt;element name="DestRegCriterioCaja_NO"&gt;
 *                       &lt;complexType&gt;
 *                         &lt;complexContent&gt;
 *                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;/restriction&gt;
 *                         &lt;/complexContent&gt;
 *                       &lt;/complexType&gt;
 *                     &lt;/element&gt;
 *                   &lt;/choice&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="DatEstadisticos"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="Pral"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;sequence&gt;
 *                             &lt;element name="Descripcion" type="{}tipo_Descripcion" minOccurs="0"/&gt;
 *                             &lt;element name="Clave" type="{}tipo_Clave"/&gt;
 *                             &lt;element name="Epigrafe" type="{}tipo_Epigrafe" minOccurs="0"/&gt;
 *                           &lt;/sequence&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                   &lt;element name="Otras" maxOccurs="5" minOccurs="0"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;sequence&gt;
 *                             &lt;element name="Descripcion" type="{}tipo_Descripcion" minOccurs="0"/&gt;
 *                             &lt;element name="Clave" type="{}tipo_Clave"/&gt;
 *                             &lt;element name="Epigrafe" type="{}tipo_Epigrafe" minOccurs="0"/&gt;
 *                           &lt;/sequence&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                   &lt;element name="OpTercerasPax" minOccurs="0"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                   &lt;element name="Conjunta" minOccurs="0"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;sequence&gt;
 *                             &lt;element name="NIF" type="{}tipo_Nif" minOccurs="0"/&gt;
 *                             &lt;element name="RazonSocial" type="{}tipo_RazonSocialConjunta" minOccurs="0"/&gt;
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
 *         &lt;choice minOccurs="0"&gt;
 *           &lt;element name="RepresentanteFisica" type="{}tipo_RepresentanteFisica" minOccurs="0"/&gt;
 *           &lt;element name="RepresentanteJuridica" type="{}tipo_RepresentanteJuridica" maxOccurs="3" minOccurs="0"/&gt;
 *         &lt;/choice&gt;
 *         &lt;element name="RegGeneral" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="BaseImponibleyCuota" minOccurs="0"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;sequence&gt;
 *                             &lt;element name="RegOrdinario" minOccurs="0"&gt;
 *                               &lt;complexType&gt;
 *                                 &lt;complexContent&gt;
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                                     &lt;sequence&gt;
 *                                       &lt;element name="Tipo0" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo2" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo4" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo5" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo75" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo10" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo21" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                     &lt;/sequence&gt;
 *                                   &lt;/restriction&gt;
 *                                 &lt;/complexContent&gt;
 *                               &lt;/complexType&gt;
 *                             &lt;/element&gt;
 *                             &lt;element name="OpIntragrupo" minOccurs="0"&gt;
 *                               &lt;complexType&gt;
 *                                 &lt;complexContent&gt;
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                                     &lt;sequence&gt;
 *                                       &lt;element name="Tipo0" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo2" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo4" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo5" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo75" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo10" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo21" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                     &lt;/sequence&gt;
 *                                   &lt;/restriction&gt;
 *                                 &lt;/complexContent&gt;
 *                               &lt;/complexType&gt;
 *                             &lt;/element&gt;
 *                             &lt;element name="RegCriterioCaja" minOccurs="0"&gt;
 *                               &lt;complexType&gt;
 *                                 &lt;complexContent&gt;
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                                     &lt;sequence&gt;
 *                                       &lt;element name="Tipo0" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo2" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo4" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo5" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo75" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo10" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo21" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                     &lt;/sequence&gt;
 *                                   &lt;/restriction&gt;
 *                                 &lt;/complexContent&gt;
 *                               &lt;/complexType&gt;
 *                             &lt;/element&gt;
 *                             &lt;element name="RegBienesUsados" minOccurs="0"&gt;
 *                               &lt;complexType&gt;
 *                                 &lt;complexContent&gt;
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                                     &lt;sequence&gt;
 *                                       &lt;element name="Tipo0" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo2" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo4" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo5" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo75" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo10" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo21" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                     &lt;/sequence&gt;
 *                                   &lt;/restriction&gt;
 *                                 &lt;/complexContent&gt;
 *                               &lt;/complexType&gt;
 *                             &lt;/element&gt;
 *                             &lt;element name="RegAgViajes" minOccurs="0"&gt;
 *                               &lt;complexType&gt;
 *                                 &lt;complexContent&gt;
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                                     &lt;sequence&gt;
 *                                       &lt;element name="Tipo21" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                     &lt;/sequence&gt;
 *                                   &lt;/restriction&gt;
 *                                 &lt;/complexContent&gt;
 *                               &lt;/complexType&gt;
 *                             &lt;/element&gt;
 *                             &lt;element name="AdqIntracomBienes" minOccurs="0"&gt;
 *                               &lt;complexType&gt;
 *                                 &lt;complexContent&gt;
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                                     &lt;sequence&gt;
 *                                       &lt;element name="Tipo0" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo2" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo4" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo5" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo75" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo10" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo21" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                     &lt;/sequence&gt;
 *                                   &lt;/restriction&gt;
 *                                 &lt;/complexContent&gt;
 *                               &lt;/complexType&gt;
 *                             &lt;/element&gt;
 *                             &lt;element name="AdqIntracomServicios" minOccurs="0"&gt;
 *                               &lt;complexType&gt;
 *                                 &lt;complexContent&gt;
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                                     &lt;sequence&gt;
 *                                       &lt;element name="Tipo0" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo2" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo4" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo5" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo75" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo10" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo21" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                     &lt;/sequence&gt;
 *                                   &lt;/restriction&gt;
 *                                 &lt;/complexContent&gt;
 *                               &lt;/complexType&gt;
 *                             &lt;/element&gt;
 *                             &lt;element name="IVAdevengadoInversionSP" minOccurs="0"&gt;
 *                               &lt;complexType&gt;
 *                                 &lt;complexContent&gt;
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                                     &lt;sequence&gt;
 *                                       &lt;element name="TipoX" type="{}tipo_BaseImponible_y_Cuota"/&gt;
 *                                     &lt;/sequence&gt;
 *                                   &lt;/restriction&gt;
 *                                 &lt;/complexContent&gt;
 *                               &lt;/complexType&gt;
 *                             &lt;/element&gt;
 *                             &lt;element name="ModBasesyCuotas" minOccurs="0"&gt;
 *                               &lt;complexType&gt;
 *                                 &lt;complexContent&gt;
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                                     &lt;sequence&gt;
 *                                       &lt;element name="TipoX" type="{}tipo_BaseImponible_y_Cuota"/&gt;
 *                                     &lt;/sequence&gt;
 *                                   &lt;/restriction&gt;
 *                                 &lt;/complexContent&gt;
 *                               &lt;/complexType&gt;
 *                             &lt;/element&gt;
 *                             &lt;element name="ModBasesyCuotasOpIntragrupo" minOccurs="0"&gt;
 *                               &lt;complexType&gt;
 *                                 &lt;complexContent&gt;
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                                     &lt;sequence&gt;
 *                                       &lt;element name="TipoX" type="{}tipo_BaseImponible_y_Cuota"/&gt;
 *                                     &lt;/sequence&gt;
 *                                   &lt;/restriction&gt;
 *                                 &lt;/complexContent&gt;
 *                               &lt;/complexType&gt;
 *                             &lt;/element&gt;
 *                             &lt;element name="ModBasesyCuotasConcursoAcreedores" minOccurs="0"&gt;
 *                               &lt;complexType&gt;
 *                                 &lt;complexContent&gt;
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                                     &lt;sequence&gt;
 *                                       &lt;element name="TipoX" type="{}tipo_BaseImponible_y_Cuota"/&gt;
 *                                     &lt;/sequence&gt;
 *                                   &lt;/restriction&gt;
 *                                 &lt;/complexContent&gt;
 *                               &lt;/complexType&gt;
 *                             &lt;/element&gt;
 *                             &lt;element name="TotalBasesyCuotasIVA" minOccurs="0"&gt;
 *                               &lt;complexType&gt;
 *                                 &lt;complexContent&gt;
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                                     &lt;sequence&gt;
 *                                       &lt;element name="TipoX" type="{}tipo_BaseImponible_y_Cuota"/&gt;
 *                                     &lt;/sequence&gt;
 *                                   &lt;/restriction&gt;
 *                                 &lt;/complexContent&gt;
 *                               &lt;/complexType&gt;
 *                             &lt;/element&gt;
 *                             &lt;element name="RecargoEquivalencia" minOccurs="0"&gt;
 *                               &lt;complexType&gt;
 *                                 &lt;complexContent&gt;
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                                     &lt;sequence&gt;
 *                                       &lt;element name="Tipo0" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo026" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo05" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo062" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo1" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo14" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo52" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo175" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                     &lt;/sequence&gt;
 *                                   &lt;/restriction&gt;
 *                                 &lt;/complexContent&gt;
 *                               &lt;/complexType&gt;
 *                             &lt;/element&gt;
 *                             &lt;element name="ModRecargoEquivalencia" minOccurs="0"&gt;
 *                               &lt;complexType&gt;
 *                                 &lt;complexContent&gt;
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                                     &lt;sequence&gt;
 *                                       &lt;element name="TipoX" type="{}tipo_BaseImponible_y_Cuota"/&gt;
 *                                     &lt;/sequence&gt;
 *                                   &lt;/restriction&gt;
 *                                 &lt;/complexContent&gt;
 *                               &lt;/complexType&gt;
 *                             &lt;/element&gt;
 *                             &lt;element name="ModRecargoEquivalenciaConcursoAcreedores" minOccurs="0"&gt;
 *                               &lt;complexType&gt;
 *                                 &lt;complexContent&gt;
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                                     &lt;sequence&gt;
 *                                       &lt;element name="TipoX" type="{}tipo_BaseImponible_y_Cuota"/&gt;
 *                                     &lt;/sequence&gt;
 *                                   &lt;/restriction&gt;
 *                                 &lt;/complexContent&gt;
 *                               &lt;/complexType&gt;
 *                             &lt;/element&gt;
 *                             &lt;element name="TotalCuotasIVA" type="{}tipo_ImpNegativo" minOccurs="0"/&gt;
 *                           &lt;/sequence&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                   &lt;element name="Deducciones" minOccurs="0"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;sequence&gt;
 *                             &lt;element name="OpInterioresBienesServiciosCorrientes" minOccurs="0"&gt;
 *                               &lt;complexType&gt;
 *                                 &lt;complexContent&gt;
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                                     &lt;sequence&gt;
 *                                       &lt;element name="Tipo2" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo4" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo5" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo75" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo10" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo21" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Total" type="{}tipo_BaseImponible_y_Cuota"/&gt;
 *                                     &lt;/sequence&gt;
 *                                   &lt;/restriction&gt;
 *                                 &lt;/complexContent&gt;
 *                               &lt;/complexType&gt;
 *                             &lt;/element&gt;
 *                             &lt;element name="OpIntragrupoCorrientes" minOccurs="0"&gt;
 *                               &lt;complexType&gt;
 *                                 &lt;complexContent&gt;
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                                     &lt;sequence&gt;
 *                                       &lt;element name="Tipo2" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo4" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo5" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo75" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo10" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo21" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Total" type="{}tipo_BaseImponible_y_Cuota"/&gt;
 *                                     &lt;/sequence&gt;
 *                                   &lt;/restriction&gt;
 *                                 &lt;/complexContent&gt;
 *                               &lt;/complexType&gt;
 *                             &lt;/element&gt;
 *                             &lt;element name="OpInterioresBienesInversion" minOccurs="0"&gt;
 *                               &lt;complexType&gt;
 *                                 &lt;complexContent&gt;
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                                     &lt;sequence&gt;
 *                                       &lt;element name="Tipo2" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo4" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo5" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo75" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo10" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo21" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Total" type="{}tipo_BaseImponible_y_Cuota"/&gt;
 *                                     &lt;/sequence&gt;
 *                                   &lt;/restriction&gt;
 *                                 &lt;/complexContent&gt;
 *                               &lt;/complexType&gt;
 *                             &lt;/element&gt;
 *                             &lt;element name="OpIntragrupoBienesInversion" minOccurs="0"&gt;
 *                               &lt;complexType&gt;
 *                                 &lt;complexContent&gt;
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                                     &lt;sequence&gt;
 *                                       &lt;element name="Tipo2" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo4" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo5" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo75" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo10" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo21" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Total" type="{}tipo_BaseImponible_y_Cuota"/&gt;
 *                                     &lt;/sequence&gt;
 *                                   &lt;/restriction&gt;
 *                                 &lt;/complexContent&gt;
 *                               &lt;/complexType&gt;
 *                             &lt;/element&gt;
 *                             &lt;element name="ImportacionesBienesCorrientes" minOccurs="0"&gt;
 *                               &lt;complexType&gt;
 *                                 &lt;complexContent&gt;
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                                     &lt;sequence&gt;
 *                                       &lt;element name="Tipo2" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo4" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo5" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo75" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo10" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo21" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Total" type="{}tipo_BaseImponible_y_Cuota"/&gt;
 *                                     &lt;/sequence&gt;
 *                                   &lt;/restriction&gt;
 *                                 &lt;/complexContent&gt;
 *                               &lt;/complexType&gt;
 *                             &lt;/element&gt;
 *                             &lt;element name="ImportacionesBienesInversion" minOccurs="0"&gt;
 *                               &lt;complexType&gt;
 *                                 &lt;complexContent&gt;
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                                     &lt;sequence&gt;
 *                                       &lt;element name="Tipo2" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo4" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo5" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo75" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo10" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo21" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Total" type="{}tipo_BaseImponible_y_Cuota"/&gt;
 *                                     &lt;/sequence&gt;
 *                                   &lt;/restriction&gt;
 *                                 &lt;/complexContent&gt;
 *                               &lt;/complexType&gt;
 *                             &lt;/element&gt;
 *                             &lt;element name="AdqIntracomunitariasBienesCorrientes" minOccurs="0"&gt;
 *                               &lt;complexType&gt;
 *                                 &lt;complexContent&gt;
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                                     &lt;sequence&gt;
 *                                       &lt;element name="Tipo2" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo4" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo5" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo75" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo10" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo21" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Total" type="{}tipo_BaseImponible_y_Cuota"/&gt;
 *                                     &lt;/sequence&gt;
 *                                   &lt;/restriction&gt;
 *                                 &lt;/complexContent&gt;
 *                               &lt;/complexType&gt;
 *                             &lt;/element&gt;
 *                             &lt;element name="AdqIntracomunitariasBienesInversion" minOccurs="0"&gt;
 *                               &lt;complexType&gt;
 *                                 &lt;complexContent&gt;
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                                     &lt;sequence&gt;
 *                                       &lt;element name="Tipo2" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo4" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo5" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo75" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo10" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo21" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Total" type="{}tipo_BaseImponible_y_Cuota"/&gt;
 *                                     &lt;/sequence&gt;
 *                                   &lt;/restriction&gt;
 *                                 &lt;/complexContent&gt;
 *                               &lt;/complexType&gt;
 *                             &lt;/element&gt;
 *                             &lt;element name="AdqIntracomunitariasServicios" minOccurs="0"&gt;
 *                               &lt;complexType&gt;
 *                                 &lt;complexContent&gt;
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                                     &lt;sequence&gt;
 *                                       &lt;element name="Tipo2" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo4" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo5" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo75" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo10" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Tipo21" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                                       &lt;element name="Total" type="{}tipo_BaseImponible_y_Cuota"/&gt;
 *                                     &lt;/sequence&gt;
 *                                   &lt;/restriction&gt;
 *                                 &lt;/complexContent&gt;
 *                               &lt;/complexType&gt;
 *                             &lt;/element&gt;
 *                             &lt;element name="ComRegAgricGanadPesca" minOccurs="0"&gt;
 *                               &lt;complexType&gt;
 *                                 &lt;complexContent&gt;
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                                     &lt;sequence&gt;
 *                                       &lt;element name="TipoX" type="{}tipo_BaseImponible_y_Cuota"/&gt;
 *                                     &lt;/sequence&gt;
 *                                   &lt;/restriction&gt;
 *                                 &lt;/complexContent&gt;
 *                               &lt;/complexType&gt;
 *                             &lt;/element&gt;
 *                             &lt;element name="CouDedResAdministrativas" minOccurs="0"&gt;
 *                               &lt;complexType&gt;
 *                                 &lt;complexContent&gt;
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                                     &lt;sequence&gt;
 *                                       &lt;element name="TipoX" type="{}tipo_BaseImponible_y_Cuota"/&gt;
 *                                     &lt;/sequence&gt;
 *                                   &lt;/restriction&gt;
 *                                 &lt;/complexContent&gt;
 *                               &lt;/complexType&gt;
 *                             &lt;/element&gt;
 *                             &lt;element name="RectifDeducciones" minOccurs="0"&gt;
 *                               &lt;complexType&gt;
 *                                 &lt;complexContent&gt;
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                                     &lt;sequence&gt;
 *                                       &lt;element name="TipoX" type="{}tipo_BaseImponible_y_Cuota"/&gt;
 *                                     &lt;/sequence&gt;
 *                                   &lt;/restriction&gt;
 *                                 &lt;/complexContent&gt;
 *                               &lt;/complexType&gt;
 *                             &lt;/element&gt;
 *                             &lt;element name="RectifOpIntragrupo" minOccurs="0"&gt;
 *                               &lt;complexType&gt;
 *                                 &lt;complexContent&gt;
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                                     &lt;sequence&gt;
 *                                       &lt;element name="TipoX" type="{}tipo_BaseImponible_y_Cuota"/&gt;
 *                                     &lt;/sequence&gt;
 *                                   &lt;/restriction&gt;
 *                                 &lt;/complexContent&gt;
 *                               &lt;/complexType&gt;
 *                             &lt;/element&gt;
 *                             &lt;element name="RegularizInversiones" type="{}tipo_ImpNegativo" minOccurs="0"/&gt;
 *                             &lt;element name="RegularizPorcProrrata" type="{}tipo_ImpNegativo" minOccurs="0"/&gt;
 *                             &lt;element name="SumDeducciones" type="{}tipo_ImpNegativo" minOccurs="0"/&gt;
 *                           &lt;/sequence&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                   &lt;element name="ResRegGeneral" type="{}tipo_ImpNegativoMasEspacio" minOccurs="0"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="RegSimplificado" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="Actividad" maxOccurs="6" minOccurs="0"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;sequence&gt;
 *                             &lt;element name="Epigrafe" type="{}tipo_Epigrafe"/&gt;
 *                             &lt;element name="Modulo" maxOccurs="7" minOccurs="0"&gt;
 *                               &lt;complexType&gt;
 *                                 &lt;complexContent&gt;
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                                     &lt;sequence&gt;
 *                                       &lt;element name="NumModulo" type="{}tipo_NumModulo"/&gt;
 *                                       &lt;element name="Unidades" type="{}tipo_ImpPositivo"/&gt;
 *                                       &lt;element name="Importe" type="{}tipo_ImpPositivo"/&gt;
 *                                     &lt;/sequence&gt;
 *                                   &lt;/restriction&gt;
 *                                 &lt;/complexContent&gt;
 *                               &lt;/complexType&gt;
 *                             &lt;/element&gt;
 *                             &lt;element name="CuotaDevengada" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
 *                             &lt;element name="Lorca" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
 *                             &lt;element name="CuotaSoportada" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
 *                             &lt;element name="IndiceCorrector" type="{}tipo_IndiceCorrector" minOccurs="0"/&gt;
 *                             &lt;element name="Resultado" type="{}tipo_ImpNegativo" minOccurs="0"/&gt;
 *                             &lt;element name="PorcCuotaMinima" type="{}tipo_Porcentaje" minOccurs="0"/&gt;
 *                             &lt;element name="DevCuotaSopOtrosPaises" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
 *                             &lt;element name="CuotaMinima" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
 *                             &lt;element name="CuotaRegSimplificado" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
 *                             &lt;element name="IndicadorAuxiliar" type="{}tipo_IndicadorAuxiliar" minOccurs="0"/&gt;
 *                           &lt;/sequence&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                   &lt;element name="ActAgricGanadForest" maxOccurs="5" minOccurs="0"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;sequence minOccurs="0"&gt;
 *                             &lt;element name="Codigo" type="{}tipo_AgrariasCod"/&gt;
 *                             &lt;element name="VolIngresos" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
 *                             &lt;element name="IndCuota" type="{}tipo_InCuota" minOccurs="0"/&gt;
 *                             &lt;element name="CuotaDevengada" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
 *                             &lt;element name="CuotasSoportadas" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
 *                             &lt;element name="CuotaRegSimplificado" type="{}tipo_ImpNegativo" minOccurs="0"/&gt;
 *                           &lt;/sequence&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                   &lt;element name="IvaDevengado" minOccurs="0"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;sequence&gt;
 *                             &lt;element name="SumaCuotasNoAgric" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
 *                             &lt;element name="SumaCuotasAgric" type="{}tipo_ImpNegativo" minOccurs="0"/&gt;
 *                             &lt;element name="AdqIntracomunitarias" type="{}tipo_ImpNegativo" minOccurs="0"/&gt;
 *                             &lt;element name="InversionSujetoPasivo" type="{}tipo_ImpNegativo" minOccurs="0"/&gt;
 *                             &lt;element name="EntregasActivosFijos" type="{}tipo_ImpNegativo" minOccurs="0"/&gt;
 *                             &lt;element name="TotalCuota" type="{}tipo_ImpNegativo"/&gt;
 *                           &lt;/sequence&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                   &lt;element name="IvaDeducible" minOccurs="0"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;sequence&gt;
 *                             &lt;element name="IVASoportadoAdqActivosFijos" type="{}tipo_ImpNegativo" minOccurs="0"/&gt;
 *                             &lt;element name="RegBienesInversion" type="{}tipo_ImpNegativo" minOccurs="0"/&gt;
 *                             &lt;element name="SumaDeducciones" type="{}tipo_ImpNegativo"/&gt;
 *                           &lt;/sequence&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                   &lt;element name="ResRegimenSimplificado" type="{}tipo_ImpNegativo" minOccurs="0"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;choice&gt;
 *           &lt;element name="LiqAnual" minOccurs="0"&gt;
 *             &lt;complexType&gt;
 *               &lt;complexContent&gt;
 *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                   &lt;sequence&gt;
 *                     &lt;element name="RegCuotas" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
 *                     &lt;element name="SumResultados" type="{}tipo_ImpNegativo" minOccurs="0"/&gt;
 *                     &lt;element name="IvaAduana" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
 *                     &lt;element name="CompCuotasEjercicioAnterior" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
 *                     &lt;element name="ResLiquidacion" type="{}tipo_ImpNegativo" minOccurs="0"/&gt;
 *                   &lt;/sequence&gt;
 *                 &lt;/restriction&gt;
 *               &lt;/complexContent&gt;
 *             &lt;/complexType&gt;
 *           &lt;/element&gt;
 *           &lt;element name="Administraciones" minOccurs="0"&gt;
 *             &lt;complexType&gt;
 *               &lt;complexContent&gt;
 *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                   &lt;sequence&gt;
 *                     &lt;element name="RegCuotas" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
 *                     &lt;element name="Comun" type="{}tipo_Porcentaje" minOccurs="0"/&gt;
 *                     &lt;element name="ArabaAlava" type="{}tipo_Porcentaje" minOccurs="0"/&gt;
 *                     &lt;element name="Gipuzkoa" type="{}tipo_Porcentaje" minOccurs="0"/&gt;
 *                     &lt;element name="Bizkaia" type="{}tipo_Porcentaje" minOccurs="0"/&gt;
 *                     &lt;element name="Navarra" type="{}tipo_Porcentaje" minOccurs="0"/&gt;
 *                     &lt;element name="SumResultados" type="{}tipo_ImpNegativo" minOccurs="0"/&gt;
 *                     &lt;element name="ResTerrComun" type="{}tipo_ImpNegativo" minOccurs="0"/&gt;
 *                     &lt;element name="IvaAduana" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
 *                     &lt;element name="ComCuotasEjercicioAnteriorTerrComun" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
 *                     &lt;element name="ResLiqAnualTerrComun" type="{}tipo_ImpNegativo" minOccurs="0"/&gt;
 *                   &lt;/sequence&gt;
 *                 &lt;/restriction&gt;
 *               &lt;/complexContent&gt;
 *             &lt;/complexType&gt;
 *           &lt;/element&gt;
 *         &lt;/choice&gt;
 *         &lt;element name="ResLiquidaciones" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="PerNoRegGrupos" minOccurs="0"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;sequence&gt;
 *                             &lt;element name="TotIngresosIVA" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
 *                             &lt;element name="TotDevIVA_SP_RegDevMensual" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
 *                             &lt;element name="ExclusionBaja" minOccurs="0"&gt;
 *                               &lt;complexType&gt;
 *                                 &lt;complexContent&gt;
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                                   &lt;/restriction&gt;
 *                                 &lt;/complexContent&gt;
 *                               &lt;/complexType&gt;
 *                             &lt;/element&gt;
 *                             &lt;element name="TotDevAdqElemTrans" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
 *                             &lt;element name="ImporteACompensarUltimoPeriodo" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
 *                             &lt;element name="ImporteADevolverUltimoPeriodo" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
 *                             &lt;element name="CuotasPendCompensar" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
 *                           &lt;/sequence&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                   &lt;element name="PerSiRegGrupos" minOccurs="0"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;sequence&gt;
 *                             &lt;element name="TotResulPositivos322" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
 *                             &lt;element name="TotResulNegativos322" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
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
 *         &lt;element name="VolOperaciones" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="OpRegGeneral" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
 *                   &lt;element name="OpRegEspCriterioCaja" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
 *                   &lt;element name="EntregasIntracomunitariasExentas" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
 *                   &lt;element name="ExportacionesExentasConDrchoDeduccion" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
 *                   &lt;element name="OpExentasSinDrchoDeduccion" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
 *                   &lt;element name="OpNoSujetas" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
 *                   &lt;element name="Box125" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
 *                   &lt;element name="Box126" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
 *                   &lt;element name="Box127" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
 *                   &lt;element name="Box128" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
 *                   &lt;element name="OpRegSimplificado" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
 *                   &lt;element name="OpRegEspAgricPescGanad" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
 *                   &lt;element name="OpRegEspRecEquivalencia" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
 *                   &lt;element name="OpRegEspBienesUsados" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
 *                   &lt;element name="OpRegEspAgViajes" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
 *                   &lt;element name="EntregasBienesInmuebles" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
 *                   &lt;element name="EntregasBienesInversion" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
 *                   &lt;element name="TotalVolOp" type="{}tipo_ImpNegativo" minOccurs="0"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="OpEspecificas" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="AdqInterioresExentas" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
 *                   &lt;element name="AdqIntracomunitariasExentas" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
 *                   &lt;element name="ImportacionesExentas" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
 *                   &lt;element name="BasesIVASoportadoNoDeducible" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
 *                   &lt;element name="OpSujetas" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
 *                   &lt;element name="EntregasInteriores" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
 *                   &lt;element name="ServInversionSP" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
 *                   &lt;element name="EntregasCriterioCajaBase" minOccurs="0"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;sequence&gt;
 *                             &lt;element name="TipoX" type="{}tipo_BaseImponible_y_Cuota"/&gt;
 *                           &lt;/sequence&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                   &lt;element name="AdqCriterioCajaBase" minOccurs="0"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;sequence&gt;
 *                             &lt;element name="TipoX" type="{}tipo_BaseImponible_y_Cuota"/&gt;
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
 *         &lt;element name="Prorratas" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="Pro" maxOccurs="30"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;sequence&gt;
 *                             &lt;element name="Actividad" type="{}tipo_ActProrratas" minOccurs="0"/&gt;
 *                             &lt;element name="CNAE" type="{}tipo_CNAE"/&gt;
 *                             &lt;element name="ImpOper" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
 *                             &lt;element name="ImpOperConDrchoDed" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
 *                             &lt;element name="Tipo" type="{}tipo_Prorrata" minOccurs="0"/&gt;
 *                             &lt;element name="Porc" type="{}tipo_Porcentaje" minOccurs="0"/&gt;
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
 *         &lt;element name="IVADeducibleGrupo1" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="OpInteriores" minOccurs="0"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;sequence&gt;
 *                             &lt;element name="BienesyServiciosCorrientes" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                             &lt;element name="BienesInversion" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                           &lt;/sequence&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                   &lt;element name="Importaciones" minOccurs="0"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;sequence&gt;
 *                             &lt;element name="BienesCorrientes" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                             &lt;element name="BienesInversion" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                           &lt;/sequence&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                   &lt;element name="AdqIntracomunitarias" minOccurs="0"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;sequence&gt;
 *                             &lt;element name="BienesCorrientes" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                             &lt;element name="BienesInversion" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                           &lt;/sequence&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                   &lt;element name="CompRegEspAgricGanadPesca" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                   &lt;element name="RectDeducciones" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                   &lt;element name="RegInversiones" type="{}tipo_ImpNegativo" minOccurs="0"/&gt;
 *                   &lt;element name="SumaDeducciones" type="{}tipo_ImpNegativo" minOccurs="0"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="IVADeducibleGrupo2" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="OpInteriores" minOccurs="0"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;sequence&gt;
 *                             &lt;element name="BienesyServiciosCorrientes" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                             &lt;element name="BienesInversion" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                           &lt;/sequence&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                   &lt;element name="Importaciones" minOccurs="0"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;sequence&gt;
 *                             &lt;element name="BienesCorrientes" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                             &lt;element name="BienesInversion" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                           &lt;/sequence&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                   &lt;element name="AdqIntracomunitarias" minOccurs="0"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;sequence&gt;
 *                             &lt;element name="BienesCorrientes" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                             &lt;element name="BienesInversion" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                           &lt;/sequence&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                   &lt;element name="CompRegEspAgricGanadPesca" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                   &lt;element name="RectDeducciones" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                   &lt;element name="RegInversiones" type="{}tipo_ImpNegativo" minOccurs="0"/&gt;
 *                   &lt;element name="SumaDeducciones" type="{}tipo_ImpNegativo" minOccurs="0"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="IVADeducibleGrupo3" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="OpInteriores" minOccurs="0"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;sequence&gt;
 *                             &lt;element name="BienesyServiciosCorrientes" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                             &lt;element name="BienesInversion" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                           &lt;/sequence&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                   &lt;element name="Importaciones" minOccurs="0"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;sequence&gt;
 *                             &lt;element name="BienesCorrientes" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                             &lt;element name="BienesInversion" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                           &lt;/sequence&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                   &lt;element name="AdqIntracomunitarias" minOccurs="0"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;sequence&gt;
 *                             &lt;element name="BienesCorrientes" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                             &lt;element name="BienesInversion" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                           &lt;/sequence&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                   &lt;element name="CompRegEspAgricGanadPesca" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                   &lt;element name="RectDeducciones" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
 *                   &lt;element name="RegInversiones" type="{}tipo_ImpNegativo" minOccurs="0"/&gt;
 *                   &lt;element name="SumaDeducciones" type="{}tipo_ImpNegativo" minOccurs="0"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="Sello" type="{}tipo_IdDoc20" minOccurs="0"/&gt;
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
    "idDoc",
    "datIdent",
    "devengo",
    "datEstadisticos",
    "representanteFisica",
    "representanteJuridica",
    "regGeneral",
    "regSimplificado",
    "liqAnual",
    "administraciones",
    "resLiquidaciones",
    "volOperaciones",
    "opEspecificas",
    "prorratas",
    "ivaDeducibleGrupo1",
    "ivaDeducibleGrupo2",
    "ivaDeducibleGrupo3",
    "sello"
})
@XmlRootElement(name = "AEATIVA2024")
public class AEATIVA2024 {

    @XmlElement(name = "IdDoc", required = true)
    protected TipoDoc idDoc;
    @XmlElement(name = "DatIdent", required = true)
    protected AEATIVA2024 .DatIdent datIdent;
    @XmlElement(name = "Devengo", required = true)
    protected AEATIVA2024 .Devengo devengo;
    @XmlElement(name = "DatEstadisticos", required = true)
    protected AEATIVA2024 .DatEstadisticos datEstadisticos;
    @XmlElement(name = "RepresentanteFisica")
    protected TipoRepresentanteFisica representanteFisica;
    @XmlElement(name = "RepresentanteJuridica")
    protected List<TipoRepresentanteJuridica> representanteJuridica;
    @XmlElement(name = "RegGeneral")
    protected AEATIVA2024 .RegGeneral regGeneral;
    @XmlElement(name = "RegSimplificado")
    protected AEATIVA2024 .RegSimplificado regSimplificado;
    @XmlElement(name = "LiqAnual")
    protected AEATIVA2024 .LiqAnual liqAnual;
    @XmlElement(name = "Administraciones")
    protected AEATIVA2024 .Administraciones administraciones;
    @XmlElement(name = "ResLiquidaciones")
    protected AEATIVA2024 .ResLiquidaciones resLiquidaciones;
    @XmlElement(name = "VolOperaciones")
    protected AEATIVA2024 .VolOperaciones volOperaciones;
    @XmlElement(name = "OpEspecificas")
    protected AEATIVA2024 .OpEspecificas opEspecificas;
    @XmlElement(name = "Prorratas")
    protected AEATIVA2024 .Prorratas prorratas;
    @XmlElement(name = "IVADeducibleGrupo1")
    protected AEATIVA2024 .IVADeducibleGrupo1 ivaDeducibleGrupo1;
    @XmlElement(name = "IVADeducibleGrupo2")
    protected AEATIVA2024 .IVADeducibleGrupo2 ivaDeducibleGrupo2;
    @XmlElement(name = "IVADeducibleGrupo3")
    protected AEATIVA2024 .IVADeducibleGrupo3 ivaDeducibleGrupo3;
    @XmlElement(name = "Sello")
    protected String sello;

    /**
     * Obtiene el valor de la propiedad idDoc.
     * 
     * @return
     *     possible object is
     *     {@link TipoDoc }
     *     
     */
    public TipoDoc getIdDoc() {
        return idDoc;
    }

    /**
     * Define el valor de la propiedad idDoc.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoDoc }
     *     
     */
    public void setIdDoc(TipoDoc value) {
        this.idDoc = value;
    }

    /**
     * Obtiene el valor de la propiedad datIdent.
     * 
     * @return
     *     possible object is
     *     {@link AEATIVA2024 .DatIdent }
     *     
     */
    public AEATIVA2024 .DatIdent getDatIdent() {
        return datIdent;
    }

    /**
     * Define el valor de la propiedad datIdent.
     * 
     * @param value
     *     allowed object is
     *     {@link AEATIVA2024 .DatIdent }
     *     
     */
    public void setDatIdent(AEATIVA2024 .DatIdent value) {
        this.datIdent = value;
    }

    /**
     * Obtiene el valor de la propiedad devengo.
     * 
     * @return
     *     possible object is
     *     {@link AEATIVA2024 .Devengo }
     *     
     */
    public AEATIVA2024 .Devengo getDevengo() {
        return devengo;
    }

    /**
     * Define el valor de la propiedad devengo.
     * 
     * @param value
     *     allowed object is
     *     {@link AEATIVA2024 .Devengo }
     *     
     */
    public void setDevengo(AEATIVA2024 .Devengo value) {
        this.devengo = value;
    }

    /**
     * Obtiene el valor de la propiedad datEstadisticos.
     * 
     * @return
     *     possible object is
     *     {@link AEATIVA2024 .DatEstadisticos }
     *     
     */
    public AEATIVA2024 .DatEstadisticos getDatEstadisticos() {
        return datEstadisticos;
    }

    /**
     * Define el valor de la propiedad datEstadisticos.
     * 
     * @param value
     *     allowed object is
     *     {@link AEATIVA2024 .DatEstadisticos }
     *     
     */
    public void setDatEstadisticos(AEATIVA2024 .DatEstadisticos value) {
        this.datEstadisticos = value;
    }

    /**
     * Obtiene el valor de la propiedad representanteFisica.
     * 
     * @return
     *     possible object is
     *     {@link TipoRepresentanteFisica }
     *     
     */
    public TipoRepresentanteFisica getRepresentanteFisica() {
        return representanteFisica;
    }

    /**
     * Define el valor de la propiedad representanteFisica.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoRepresentanteFisica }
     *     
     */
    public void setRepresentanteFisica(TipoRepresentanteFisica value) {
        this.representanteFisica = value;
    }

    /**
     * Gets the value of the representanteJuridica property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the representanteJuridica property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRepresentanteJuridica().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TipoRepresentanteJuridica }
     * 
     * 
     */
    public List<TipoRepresentanteJuridica> getRepresentanteJuridica() {
        if (representanteJuridica == null) {
            representanteJuridica = new ArrayList<TipoRepresentanteJuridica>();
        }
        return this.representanteJuridica;
    }

    /**
     * Obtiene el valor de la propiedad regGeneral.
     * 
     * @return
     *     possible object is
     *     {@link AEATIVA2024 .RegGeneral }
     *     
     */
    public AEATIVA2024 .RegGeneral getRegGeneral() {
        return regGeneral;
    }

    /**
     * Define el valor de la propiedad regGeneral.
     * 
     * @param value
     *     allowed object is
     *     {@link AEATIVA2024 .RegGeneral }
     *     
     */
    public void setRegGeneral(AEATIVA2024 .RegGeneral value) {
        this.regGeneral = value;
    }

    /**
     * Obtiene el valor de la propiedad regSimplificado.
     * 
     * @return
     *     possible object is
     *     {@link AEATIVA2024 .RegSimplificado }
     *     
     */
    public AEATIVA2024 .RegSimplificado getRegSimplificado() {
        return regSimplificado;
    }

    /**
     * Define el valor de la propiedad regSimplificado.
     * 
     * @param value
     *     allowed object is
     *     {@link AEATIVA2024 .RegSimplificado }
     *     
     */
    public void setRegSimplificado(AEATIVA2024 .RegSimplificado value) {
        this.regSimplificado = value;
    }

    /**
     * Obtiene el valor de la propiedad liqAnual.
     * 
     * @return
     *     possible object is
     *     {@link AEATIVA2024 .LiqAnual }
     *     
     */
    public AEATIVA2024 .LiqAnual getLiqAnual() {
        return liqAnual;
    }

    /**
     * Define el valor de la propiedad liqAnual.
     * 
     * @param value
     *     allowed object is
     *     {@link AEATIVA2024 .LiqAnual }
     *     
     */
    public void setLiqAnual(AEATIVA2024 .LiqAnual value) {
        this.liqAnual = value;
    }

    /**
     * Obtiene el valor de la propiedad administraciones.
     * 
     * @return
     *     possible object is
     *     {@link AEATIVA2024 .Administraciones }
     *     
     */
    public AEATIVA2024 .Administraciones getAdministraciones() {
        return administraciones;
    }

    /**
     * Define el valor de la propiedad administraciones.
     * 
     * @param value
     *     allowed object is
     *     {@link AEATIVA2024 .Administraciones }
     *     
     */
    public void setAdministraciones(AEATIVA2024 .Administraciones value) {
        this.administraciones = value;
    }

    /**
     * Obtiene el valor de la propiedad resLiquidaciones.
     * 
     * @return
     *     possible object is
     *     {@link AEATIVA2024 .ResLiquidaciones }
     *     
     */
    public AEATIVA2024 .ResLiquidaciones getResLiquidaciones() {
        return resLiquidaciones;
    }

    /**
     * Define el valor de la propiedad resLiquidaciones.
     * 
     * @param value
     *     allowed object is
     *     {@link AEATIVA2024 .ResLiquidaciones }
     *     
     */
    public void setResLiquidaciones(AEATIVA2024 .ResLiquidaciones value) {
        this.resLiquidaciones = value;
    }

    /**
     * Obtiene el valor de la propiedad volOperaciones.
     * 
     * @return
     *     possible object is
     *     {@link AEATIVA2024 .VolOperaciones }
     *     
     */
    public AEATIVA2024 .VolOperaciones getVolOperaciones() {
        return volOperaciones;
    }

    /**
     * Define el valor de la propiedad volOperaciones.
     * 
     * @param value
     *     allowed object is
     *     {@link AEATIVA2024 .VolOperaciones }
     *     
     */
    public void setVolOperaciones(AEATIVA2024 .VolOperaciones value) {
        this.volOperaciones = value;
    }

    /**
     * Obtiene el valor de la propiedad opEspecificas.
     * 
     * @return
     *     possible object is
     *     {@link AEATIVA2024 .OpEspecificas }
     *     
     */
    public AEATIVA2024 .OpEspecificas getOpEspecificas() {
        return opEspecificas;
    }

    /**
     * Define el valor de la propiedad opEspecificas.
     * 
     * @param value
     *     allowed object is
     *     {@link AEATIVA2024 .OpEspecificas }
     *     
     */
    public void setOpEspecificas(AEATIVA2024 .OpEspecificas value) {
        this.opEspecificas = value;
    }

    /**
     * Obtiene el valor de la propiedad prorratas.
     * 
     * @return
     *     possible object is
     *     {@link AEATIVA2024 .Prorratas }
     *     
     */
    public AEATIVA2024 .Prorratas getProrratas() {
        return prorratas;
    }

    /**
     * Define el valor de la propiedad prorratas.
     * 
     * @param value
     *     allowed object is
     *     {@link AEATIVA2024 .Prorratas }
     *     
     */
    public void setProrratas(AEATIVA2024 .Prorratas value) {
        this.prorratas = value;
    }

    /**
     * Obtiene el valor de la propiedad ivaDeducibleGrupo1.
     * 
     * @return
     *     possible object is
     *     {@link AEATIVA2024 .IVADeducibleGrupo1 }
     *     
     */
    public AEATIVA2024 .IVADeducibleGrupo1 getIVADeducibleGrupo1() {
        return ivaDeducibleGrupo1;
    }

    /**
     * Define el valor de la propiedad ivaDeducibleGrupo1.
     * 
     * @param value
     *     allowed object is
     *     {@link AEATIVA2024 .IVADeducibleGrupo1 }
     *     
     */
    public void setIVADeducibleGrupo1(AEATIVA2024 .IVADeducibleGrupo1 value) {
        this.ivaDeducibleGrupo1 = value;
    }

    /**
     * Obtiene el valor de la propiedad ivaDeducibleGrupo2.
     * 
     * @return
     *     possible object is
     *     {@link AEATIVA2024 .IVADeducibleGrupo2 }
     *     
     */
    public AEATIVA2024 .IVADeducibleGrupo2 getIVADeducibleGrupo2() {
        return ivaDeducibleGrupo2;
    }

    /**
     * Define el valor de la propiedad ivaDeducibleGrupo2.
     * 
     * @param value
     *     allowed object is
     *     {@link AEATIVA2024 .IVADeducibleGrupo2 }
     *     
     */
    public void setIVADeducibleGrupo2(AEATIVA2024 .IVADeducibleGrupo2 value) {
        this.ivaDeducibleGrupo2 = value;
    }

    /**
     * Obtiene el valor de la propiedad ivaDeducibleGrupo3.
     * 
     * @return
     *     possible object is
     *     {@link AEATIVA2024 .IVADeducibleGrupo3 }
     *     
     */
    public AEATIVA2024 .IVADeducibleGrupo3 getIVADeducibleGrupo3() {
        return ivaDeducibleGrupo3;
    }

    /**
     * Define el valor de la propiedad ivaDeducibleGrupo3.
     * 
     * @param value
     *     allowed object is
     *     {@link AEATIVA2024 .IVADeducibleGrupo3 }
     *     
     */
    public void setIVADeducibleGrupo3(AEATIVA2024 .IVADeducibleGrupo3 value) {
        this.ivaDeducibleGrupo3 = value;
    }

    /**
     * Obtiene el valor de la propiedad sello.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSello() {
        return sello;
    }

    /**
     * Define el valor de la propiedad sello.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSello(String value) {
        this.sello = value;
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
     *         &lt;element name="RegCuotas" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
     *         &lt;element name="Comun" type="{}tipo_Porcentaje" minOccurs="0"/&gt;
     *         &lt;element name="ArabaAlava" type="{}tipo_Porcentaje" minOccurs="0"/&gt;
     *         &lt;element name="Gipuzkoa" type="{}tipo_Porcentaje" minOccurs="0"/&gt;
     *         &lt;element name="Bizkaia" type="{}tipo_Porcentaje" minOccurs="0"/&gt;
     *         &lt;element name="Navarra" type="{}tipo_Porcentaje" minOccurs="0"/&gt;
     *         &lt;element name="SumResultados" type="{}tipo_ImpNegativo" minOccurs="0"/&gt;
     *         &lt;element name="ResTerrComun" type="{}tipo_ImpNegativo" minOccurs="0"/&gt;
     *         &lt;element name="IvaAduana" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
     *         &lt;element name="ComCuotasEjercicioAnteriorTerrComun" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
     *         &lt;element name="ResLiqAnualTerrComun" type="{}tipo_ImpNegativo" minOccurs="0"/&gt;
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
        "regCuotas",
        "comun",
        "arabaAlava",
        "gipuzkoa",
        "bizkaia",
        "navarra",
        "sumResultados",
        "resTerrComun",
        "ivaAduana",
        "comCuotasEjercicioAnteriorTerrComun",
        "resLiqAnualTerrComun"
    })
    public static class Administraciones {

        @XmlElement(name = "RegCuotas")
        protected BigDecimal regCuotas;
        @XmlElement(name = "Comun")
        protected BigDecimal comun;
        @XmlElement(name = "ArabaAlava")
        protected BigDecimal arabaAlava;
        @XmlElement(name = "Gipuzkoa")
        protected BigDecimal gipuzkoa;
        @XmlElement(name = "Bizkaia")
        protected BigDecimal bizkaia;
        @XmlElement(name = "Navarra")
        protected BigDecimal navarra;
        @XmlElement(name = "SumResultados")
        protected BigDecimal sumResultados;
        @XmlElement(name = "ResTerrComun")
        protected BigDecimal resTerrComun;
        @XmlElement(name = "IvaAduana")
        protected BigDecimal ivaAduana;
        @XmlElement(name = "ComCuotasEjercicioAnteriorTerrComun")
        protected BigDecimal comCuotasEjercicioAnteriorTerrComun;
        @XmlElement(name = "ResLiqAnualTerrComun")
        protected BigDecimal resLiqAnualTerrComun;

        /**
         * Obtiene el valor de la propiedad regCuotas.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getRegCuotas() {
            return regCuotas;
        }

        /**
         * Define el valor de la propiedad regCuotas.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setRegCuotas(BigDecimal value) {
            this.regCuotas = value;
        }

        /**
         * Obtiene el valor de la propiedad comun.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getComun() {
            return comun;
        }

        /**
         * Define el valor de la propiedad comun.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setComun(BigDecimal value) {
            this.comun = value;
        }

        /**
         * Obtiene el valor de la propiedad arabaAlava.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getArabaAlava() {
            return arabaAlava;
        }

        /**
         * Define el valor de la propiedad arabaAlava.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setArabaAlava(BigDecimal value) {
            this.arabaAlava = value;
        }

        /**
         * Obtiene el valor de la propiedad gipuzkoa.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getGipuzkoa() {
            return gipuzkoa;
        }

        /**
         * Define el valor de la propiedad gipuzkoa.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setGipuzkoa(BigDecimal value) {
            this.gipuzkoa = value;
        }

        /**
         * Obtiene el valor de la propiedad bizkaia.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getBizkaia() {
            return bizkaia;
        }

        /**
         * Define el valor de la propiedad bizkaia.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setBizkaia(BigDecimal value) {
            this.bizkaia = value;
        }

        /**
         * Obtiene el valor de la propiedad navarra.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getNavarra() {
            return navarra;
        }

        /**
         * Define el valor de la propiedad navarra.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setNavarra(BigDecimal value) {
            this.navarra = value;
        }

        /**
         * Obtiene el valor de la propiedad sumResultados.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getSumResultados() {
            return sumResultados;
        }

        /**
         * Define el valor de la propiedad sumResultados.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setSumResultados(BigDecimal value) {
            this.sumResultados = value;
        }

        /**
         * Obtiene el valor de la propiedad resTerrComun.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getResTerrComun() {
            return resTerrComun;
        }

        /**
         * Define el valor de la propiedad resTerrComun.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setResTerrComun(BigDecimal value) {
            this.resTerrComun = value;
        }

        /**
         * Obtiene el valor de la propiedad ivaAduana.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getIvaAduana() {
            return ivaAduana;
        }

        /**
         * Define el valor de la propiedad ivaAduana.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setIvaAduana(BigDecimal value) {
            this.ivaAduana = value;
        }

        /**
         * Obtiene el valor de la propiedad comCuotasEjercicioAnteriorTerrComun.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getComCuotasEjercicioAnteriorTerrComun() {
            return comCuotasEjercicioAnteriorTerrComun;
        }

        /**
         * Define el valor de la propiedad comCuotasEjercicioAnteriorTerrComun.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setComCuotasEjercicioAnteriorTerrComun(BigDecimal value) {
            this.comCuotasEjercicioAnteriorTerrComun = value;
        }

        /**
         * Obtiene el valor de la propiedad resLiqAnualTerrComun.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getResLiqAnualTerrComun() {
            return resLiqAnualTerrComun;
        }

        /**
         * Define el valor de la propiedad resLiqAnualTerrComun.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setResLiqAnualTerrComun(BigDecimal value) {
            this.resLiqAnualTerrComun = value;
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
     *         &lt;element name="Pral"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;sequence&gt;
     *                   &lt;element name="Descripcion" type="{}tipo_Descripcion" minOccurs="0"/&gt;
     *                   &lt;element name="Clave" type="{}tipo_Clave"/&gt;
     *                   &lt;element name="Epigrafe" type="{}tipo_Epigrafe" minOccurs="0"/&gt;
     *                 &lt;/sequence&gt;
     *               &lt;/restriction&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *         &lt;element name="Otras" maxOccurs="5" minOccurs="0"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;sequence&gt;
     *                   &lt;element name="Descripcion" type="{}tipo_Descripcion" minOccurs="0"/&gt;
     *                   &lt;element name="Clave" type="{}tipo_Clave"/&gt;
     *                   &lt;element name="Epigrafe" type="{}tipo_Epigrafe" minOccurs="0"/&gt;
     *                 &lt;/sequence&gt;
     *               &lt;/restriction&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *         &lt;element name="OpTercerasPax" minOccurs="0"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *               &lt;/restriction&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *         &lt;element name="Conjunta" minOccurs="0"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;sequence&gt;
     *                   &lt;element name="NIF" type="{}tipo_Nif" minOccurs="0"/&gt;
     *                   &lt;element name="RazonSocial" type="{}tipo_RazonSocialConjunta" minOccurs="0"/&gt;
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
        "pral",
        "otras",
        "opTercerasPax",
        "conjunta"
    })
    public static class DatEstadisticos {

        @XmlElement(name = "Pral", required = true)
        protected AEATIVA2024 .DatEstadisticos.Pral pral;
        @XmlElement(name = "Otras")
        protected List<AEATIVA2024 .DatEstadisticos.Otras> otras;
        @XmlElement(name = "OpTercerasPax")
        protected AEATIVA2024 .DatEstadisticos.OpTercerasPax opTercerasPax;
        @XmlElement(name = "Conjunta")
        protected AEATIVA2024 .DatEstadisticos.Conjunta conjunta;

        /**
         * Obtiene el valor de la propiedad pral.
         * 
         * @return
         *     possible object is
         *     {@link AEATIVA2024 .DatEstadisticos.Pral }
         *     
         */
        public AEATIVA2024 .DatEstadisticos.Pral getPral() {
            return pral;
        }

        /**
         * Define el valor de la propiedad pral.
         * 
         * @param value
         *     allowed object is
         *     {@link AEATIVA2024 .DatEstadisticos.Pral }
         *     
         */
        public void setPral(AEATIVA2024 .DatEstadisticos.Pral value) {
            this.pral = value;
        }

        /**
         * Gets the value of the otras property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the otras property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getOtras().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link AEATIVA2024 .DatEstadisticos.Otras }
         * 
         * 
         */
        public List<AEATIVA2024 .DatEstadisticos.Otras> getOtras() {
            if (otras == null) {
                otras = new ArrayList<AEATIVA2024 .DatEstadisticos.Otras>();
            }
            return this.otras;
        }

        /**
         * Obtiene el valor de la propiedad opTercerasPax.
         * 
         * @return
         *     possible object is
         *     {@link AEATIVA2024 .DatEstadisticos.OpTercerasPax }
         *     
         */
        public AEATIVA2024 .DatEstadisticos.OpTercerasPax getOpTercerasPax() {
            return opTercerasPax;
        }

        /**
         * Define el valor de la propiedad opTercerasPax.
         * 
         * @param value
         *     allowed object is
         *     {@link AEATIVA2024 .DatEstadisticos.OpTercerasPax }
         *     
         */
        public void setOpTercerasPax(AEATIVA2024 .DatEstadisticos.OpTercerasPax value) {
            this.opTercerasPax = value;
        }

        /**
         * Obtiene el valor de la propiedad conjunta.
         * 
         * @return
         *     possible object is
         *     {@link AEATIVA2024 .DatEstadisticos.Conjunta }
         *     
         */
        public AEATIVA2024 .DatEstadisticos.Conjunta getConjunta() {
            return conjunta;
        }

        /**
         * Define el valor de la propiedad conjunta.
         * 
         * @param value
         *     allowed object is
         *     {@link AEATIVA2024 .DatEstadisticos.Conjunta }
         *     
         */
        public void setConjunta(AEATIVA2024 .DatEstadisticos.Conjunta value) {
            this.conjunta = value;
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
         *         &lt;element name="NIF" type="{}tipo_Nif" minOccurs="0"/&gt;
         *         &lt;element name="RazonSocial" type="{}tipo_RazonSocialConjunta" minOccurs="0"/&gt;
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
            "nif",
            "razonSocial"
        })
        public static class Conjunta {

            @XmlElement(name = "NIF")
            protected String nif;
            @XmlElement(name = "RazonSocial")
            protected String razonSocial;

            /**
             * Obtiene el valor de la propiedad nif.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getNIF() {
                return nif;
            }

            /**
             * Define el valor de la propiedad nif.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setNIF(String value) {
                this.nif = value;
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
         *     &lt;/restriction&gt;
         *   &lt;/complexContent&gt;
         * &lt;/complexType&gt;
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class OpTercerasPax {


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
         *         &lt;element name="Descripcion" type="{}tipo_Descripcion" minOccurs="0"/&gt;
         *         &lt;element name="Clave" type="{}tipo_Clave"/&gt;
         *         &lt;element name="Epigrafe" type="{}tipo_Epigrafe" minOccurs="0"/&gt;
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
            "descripcion",
            "clave",
            "epigrafe"
        })
        public static class Otras {

            @XmlElement(name = "Descripcion")
            protected String descripcion;
            @XmlElement(name = "Clave", required = true)
            protected String clave;
            @XmlElement(name = "Epigrafe")
            protected String epigrafe;

            /**
             * Obtiene el valor de la propiedad descripcion.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getDescripcion() {
                return descripcion;
            }

            /**
             * Define el valor de la propiedad descripcion.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setDescripcion(String value) {
                this.descripcion = value;
            }

            /**
             * Obtiene el valor de la propiedad clave.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getClave() {
                return clave;
            }

            /**
             * Define el valor de la propiedad clave.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setClave(String value) {
                this.clave = value;
            }

            /**
             * Obtiene el valor de la propiedad epigrafe.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getEpigrafe() {
                return epigrafe;
            }

            /**
             * Define el valor de la propiedad epigrafe.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setEpigrafe(String value) {
                this.epigrafe = value;
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
         *         &lt;element name="Descripcion" type="{}tipo_Descripcion" minOccurs="0"/&gt;
         *         &lt;element name="Clave" type="{}tipo_Clave"/&gt;
         *         &lt;element name="Epigrafe" type="{}tipo_Epigrafe" minOccurs="0"/&gt;
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
            "descripcion",
            "clave",
            "epigrafe"
        })
        public static class Pral {

            @XmlElement(name = "Descripcion")
            protected String descripcion;
            @XmlElement(name = "Clave", required = true)
            protected String clave;
            @XmlElement(name = "Epigrafe")
            protected String epigrafe;

            /**
             * Obtiene el valor de la propiedad descripcion.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getDescripcion() {
                return descripcion;
            }

            /**
             * Define el valor de la propiedad descripcion.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setDescripcion(String value) {
                this.descripcion = value;
            }

            /**
             * Obtiene el valor de la propiedad clave.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getClave() {
                return clave;
            }

            /**
             * Define el valor de la propiedad clave.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setClave(String value) {
                this.clave = value;
            }

            /**
             * Obtiene el valor de la propiedad epigrafe.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getEpigrafe() {
                return epigrafe;
            }

            /**
             * Define el valor de la propiedad epigrafe.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setEpigrafe(String value) {
                this.epigrafe = value;
            }

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
     *         &lt;choice&gt;
     *           &lt;element name="PersFisica" type="{}tipo_PersonaFisica"/&gt;
     *           &lt;element name="PersJuridica" type="{}tipo_PersonaJuridica"/&gt;
     *         &lt;/choice&gt;
     *         &lt;element name="Telefono" type="{}tipo_Telefono" minOccurs="0"/&gt;
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
        "persFisica",
        "persJuridica",
        "telefono"
    })
    public static class DatIdent {

        @XmlElement(name = "PersFisica")
        protected TipoPersonaFisica persFisica;
        @XmlElement(name = "PersJuridica")
        protected TipoPersonaJuridica persJuridica;
        @XmlElement(name = "Telefono")
        protected String telefono;

        /**
         * Obtiene el valor de la propiedad persFisica.
         * 
         * @return
         *     possible object is
         *     {@link TipoPersonaFisica }
         *     
         */
        public TipoPersonaFisica getPersFisica() {
            return persFisica;
        }

        /**
         * Define el valor de la propiedad persFisica.
         * 
         * @param value
         *     allowed object is
         *     {@link TipoPersonaFisica }
         *     
         */
        public void setPersFisica(TipoPersonaFisica value) {
            this.persFisica = value;
        }

        /**
         * Obtiene el valor de la propiedad persJuridica.
         * 
         * @return
         *     possible object is
         *     {@link TipoPersonaJuridica }
         *     
         */
        public TipoPersonaJuridica getPersJuridica() {
            return persJuridica;
        }

        /**
         * Define el valor de la propiedad persJuridica.
         * 
         * @param value
         *     allowed object is
         *     {@link TipoPersonaJuridica }
         *     
         */
        public void setPersJuridica(TipoPersonaJuridica value) {
            this.persJuridica = value;
        }

        /**
         * Obtiene el valor de la propiedad telefono.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTelefono() {
            return telefono;
        }

        /**
         * Define el valor de la propiedad telefono.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTelefono(String value) {
            this.telefono = value;
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
     *         &lt;element name="Ejercicio" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
     *         &lt;choice minOccurs="0"&gt;
     *           &lt;element name="DecSustitutiva"&gt;
     *             &lt;complexType&gt;
     *               &lt;complexContent&gt;
     *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;/restriction&gt;
     *               &lt;/complexContent&gt;
     *             &lt;/complexType&gt;
     *           &lt;/element&gt;
     *           &lt;element name="DecSustitutivaRectifica"&gt;
     *             &lt;complexType&gt;
     *               &lt;complexContent&gt;
     *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;/restriction&gt;
     *               &lt;/complexContent&gt;
     *             &lt;/complexType&gt;
     *           &lt;/element&gt;
     *         &lt;/choice&gt;
     *         &lt;element name="JustDecAnterior" type="{}tipo_Justificante" minOccurs="0"/&gt;
     *         &lt;element name="RegDevMensual" minOccurs="0"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *               &lt;/restriction&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *         &lt;element name="RegGrupoEntidades" type="{}tipo_GrupoEntidades" minOccurs="0"/&gt;
     *         &lt;choice&gt;
     *           &lt;element name="ConcursoAcreedores_SI" type="{}tipo_ConcursoUltPer"/&gt;
     *           &lt;element name="ConcursoAcreedores_NO"&gt;
     *             &lt;complexType&gt;
     *               &lt;complexContent&gt;
     *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;/restriction&gt;
     *               &lt;/complexContent&gt;
     *             &lt;/complexType&gt;
     *           &lt;/element&gt;
     *         &lt;/choice&gt;
     *         &lt;choice&gt;
     *           &lt;element name="RegCriterioCaja_SI"&gt;
     *             &lt;complexType&gt;
     *               &lt;complexContent&gt;
     *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;/restriction&gt;
     *               &lt;/complexContent&gt;
     *             &lt;/complexType&gt;
     *           &lt;/element&gt;
     *           &lt;element name="RegCriterioCaja_NO"&gt;
     *             &lt;complexType&gt;
     *               &lt;complexContent&gt;
     *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;/restriction&gt;
     *               &lt;/complexContent&gt;
     *             &lt;/complexType&gt;
     *           &lt;/element&gt;
     *         &lt;/choice&gt;
     *         &lt;choice&gt;
     *           &lt;element name="DestRegCriterioCaja_SI"&gt;
     *             &lt;complexType&gt;
     *               &lt;complexContent&gt;
     *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;/restriction&gt;
     *               &lt;/complexContent&gt;
     *             &lt;/complexType&gt;
     *           &lt;/element&gt;
     *           &lt;element name="DestRegCriterioCaja_NO"&gt;
     *             &lt;complexType&gt;
     *               &lt;complexContent&gt;
     *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;/restriction&gt;
     *               &lt;/complexContent&gt;
     *             &lt;/complexType&gt;
     *           &lt;/element&gt;
     *         &lt;/choice&gt;
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
        "decSustitutiva",
        "decSustitutivaRectifica",
        "justDecAnterior",
        "regDevMensual",
        "regGrupoEntidades",
        "concursoAcreedoresSI",
        "concursoAcreedoresNO",
        "regCriterioCajaSI",
        "regCriterioCajaNO",
        "destRegCriterioCajaSI",
        "destRegCriterioCajaNO"
    })
    public static class Devengo {

        @XmlElement(name = "Ejercicio")
        protected int ejercicio;
        @XmlElement(name = "DecSustitutiva")
        protected AEATIVA2024 .Devengo.DecSustitutiva decSustitutiva;
        @XmlElement(name = "DecSustitutivaRectifica")
        protected AEATIVA2024 .Devengo.DecSustitutivaRectifica decSustitutivaRectifica;
        @XmlElement(name = "JustDecAnterior")
        protected String justDecAnterior;
        @XmlElement(name = "RegDevMensual")
        protected AEATIVA2024 .Devengo.RegDevMensual regDevMensual;
        @XmlElement(name = "RegGrupoEntidades")
        protected TipoGrupoEntidades regGrupoEntidades;
        @XmlElement(name = "ConcursoAcreedores_SI")
        protected TipoConcursoUltPer concursoAcreedoresSI;
        @XmlElement(name = "ConcursoAcreedores_NO")
        protected AEATIVA2024 .Devengo.ConcursoAcreedoresNO concursoAcreedoresNO;
        @XmlElement(name = "RegCriterioCaja_SI")
        protected AEATIVA2024 .Devengo.RegCriterioCajaSI regCriterioCajaSI;
        @XmlElement(name = "RegCriterioCaja_NO")
        protected AEATIVA2024 .Devengo.RegCriterioCajaNO regCriterioCajaNO;
        @XmlElement(name = "DestRegCriterioCaja_SI")
        protected AEATIVA2024 .Devengo.DestRegCriterioCajaSI destRegCriterioCajaSI;
        @XmlElement(name = "DestRegCriterioCaja_NO")
        protected AEATIVA2024 .Devengo.DestRegCriterioCajaNO destRegCriterioCajaNO;

        /**
         * Obtiene el valor de la propiedad ejercicio.
         * 
         */
        public int getEjercicio() {
            return ejercicio;
        }

        /**
         * Define el valor de la propiedad ejercicio.
         * 
         */
        public void setEjercicio(int value) {
            this.ejercicio = value;
        }

        /**
         * Obtiene el valor de la propiedad decSustitutiva.
         * 
         * @return
         *     possible object is
         *     {@link AEATIVA2024 .Devengo.DecSustitutiva }
         *     
         */
        public AEATIVA2024 .Devengo.DecSustitutiva getDecSustitutiva() {
            return decSustitutiva;
        }

        /**
         * Define el valor de la propiedad decSustitutiva.
         * 
         * @param value
         *     allowed object is
         *     {@link AEATIVA2024 .Devengo.DecSustitutiva }
         *     
         */
        public void setDecSustitutiva(AEATIVA2024 .Devengo.DecSustitutiva value) {
            this.decSustitutiva = value;
        }

        /**
         * Obtiene el valor de la propiedad decSustitutivaRectifica.
         * 
         * @return
         *     possible object is
         *     {@link AEATIVA2024 .Devengo.DecSustitutivaRectifica }
         *     
         */
        public AEATIVA2024 .Devengo.DecSustitutivaRectifica getDecSustitutivaRectifica() {
            return decSustitutivaRectifica;
        }

        /**
         * Define el valor de la propiedad decSustitutivaRectifica.
         * 
         * @param value
         *     allowed object is
         *     {@link AEATIVA2024 .Devengo.DecSustitutivaRectifica }
         *     
         */
        public void setDecSustitutivaRectifica(AEATIVA2024 .Devengo.DecSustitutivaRectifica value) {
            this.decSustitutivaRectifica = value;
        }

        /**
         * Obtiene el valor de la propiedad justDecAnterior.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getJustDecAnterior() {
            return justDecAnterior;
        }

        /**
         * Define el valor de la propiedad justDecAnterior.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setJustDecAnterior(String value) {
            this.justDecAnterior = value;
        }

        /**
         * Obtiene el valor de la propiedad regDevMensual.
         * 
         * @return
         *     possible object is
         *     {@link AEATIVA2024 .Devengo.RegDevMensual }
         *     
         */
        public AEATIVA2024 .Devengo.RegDevMensual getRegDevMensual() {
            return regDevMensual;
        }

        /**
         * Define el valor de la propiedad regDevMensual.
         * 
         * @param value
         *     allowed object is
         *     {@link AEATIVA2024 .Devengo.RegDevMensual }
         *     
         */
        public void setRegDevMensual(AEATIVA2024 .Devengo.RegDevMensual value) {
            this.regDevMensual = value;
        }

        /**
         * Obtiene el valor de la propiedad regGrupoEntidades.
         * 
         * @return
         *     possible object is
         *     {@link TipoGrupoEntidades }
         *     
         */
        public TipoGrupoEntidades getRegGrupoEntidades() {
            return regGrupoEntidades;
        }

        /**
         * Define el valor de la propiedad regGrupoEntidades.
         * 
         * @param value
         *     allowed object is
         *     {@link TipoGrupoEntidades }
         *     
         */
        public void setRegGrupoEntidades(TipoGrupoEntidades value) {
            this.regGrupoEntidades = value;
        }

        /**
         * Obtiene el valor de la propiedad concursoAcreedoresSI.
         * 
         * @return
         *     possible object is
         *     {@link TipoConcursoUltPer }
         *     
         */
        public TipoConcursoUltPer getConcursoAcreedoresSI() {
            return concursoAcreedoresSI;
        }

        /**
         * Define el valor de la propiedad concursoAcreedoresSI.
         * 
         * @param value
         *     allowed object is
         *     {@link TipoConcursoUltPer }
         *     
         */
        public void setConcursoAcreedoresSI(TipoConcursoUltPer value) {
            this.concursoAcreedoresSI = value;
        }

        /**
         * Obtiene el valor de la propiedad concursoAcreedoresNO.
         * 
         * @return
         *     possible object is
         *     {@link AEATIVA2024 .Devengo.ConcursoAcreedoresNO }
         *     
         */
        public AEATIVA2024 .Devengo.ConcursoAcreedoresNO getConcursoAcreedoresNO() {
            return concursoAcreedoresNO;
        }

        /**
         * Define el valor de la propiedad concursoAcreedoresNO.
         * 
         * @param value
         *     allowed object is
         *     {@link AEATIVA2024 .Devengo.ConcursoAcreedoresNO }
         *     
         */
        public void setConcursoAcreedoresNO(AEATIVA2024 .Devengo.ConcursoAcreedoresNO value) {
            this.concursoAcreedoresNO = value;
        }

        /**
         * Obtiene el valor de la propiedad regCriterioCajaSI.
         * 
         * @return
         *     possible object is
         *     {@link AEATIVA2024 .Devengo.RegCriterioCajaSI }
         *     
         */
        public AEATIVA2024 .Devengo.RegCriterioCajaSI getRegCriterioCajaSI() {
            return regCriterioCajaSI;
        }

        /**
         * Define el valor de la propiedad regCriterioCajaSI.
         * 
         * @param value
         *     allowed object is
         *     {@link AEATIVA2024 .Devengo.RegCriterioCajaSI }
         *     
         */
        public void setRegCriterioCajaSI(AEATIVA2024 .Devengo.RegCriterioCajaSI value) {
            this.regCriterioCajaSI = value;
        }

        /**
         * Obtiene el valor de la propiedad regCriterioCajaNO.
         * 
         * @return
         *     possible object is
         *     {@link AEATIVA2024 .Devengo.RegCriterioCajaNO }
         *     
         */
        public AEATIVA2024 .Devengo.RegCriterioCajaNO getRegCriterioCajaNO() {
            return regCriterioCajaNO;
        }

        /**
         * Define el valor de la propiedad regCriterioCajaNO.
         * 
         * @param value
         *     allowed object is
         *     {@link AEATIVA2024 .Devengo.RegCriterioCajaNO }
         *     
         */
        public void setRegCriterioCajaNO(AEATIVA2024 .Devengo.RegCriterioCajaNO value) {
            this.regCriterioCajaNO = value;
        }

        /**
         * Obtiene el valor de la propiedad destRegCriterioCajaSI.
         * 
         * @return
         *     possible object is
         *     {@link AEATIVA2024 .Devengo.DestRegCriterioCajaSI }
         *     
         */
        public AEATIVA2024 .Devengo.DestRegCriterioCajaSI getDestRegCriterioCajaSI() {
            return destRegCriterioCajaSI;
        }

        /**
         * Define el valor de la propiedad destRegCriterioCajaSI.
         * 
         * @param value
         *     allowed object is
         *     {@link AEATIVA2024 .Devengo.DestRegCriterioCajaSI }
         *     
         */
        public void setDestRegCriterioCajaSI(AEATIVA2024 .Devengo.DestRegCriterioCajaSI value) {
            this.destRegCriterioCajaSI = value;
        }

        /**
         * Obtiene el valor de la propiedad destRegCriterioCajaNO.
         * 
         * @return
         *     possible object is
         *     {@link AEATIVA2024 .Devengo.DestRegCriterioCajaNO }
         *     
         */
        public AEATIVA2024 .Devengo.DestRegCriterioCajaNO getDestRegCriterioCajaNO() {
            return destRegCriterioCajaNO;
        }

        /**
         * Define el valor de la propiedad destRegCriterioCajaNO.
         * 
         * @param value
         *     allowed object is
         *     {@link AEATIVA2024 .Devengo.DestRegCriterioCajaNO }
         *     
         */
        public void setDestRegCriterioCajaNO(AEATIVA2024 .Devengo.DestRegCriterioCajaNO value) {
            this.destRegCriterioCajaNO = value;
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
         *     &lt;/restriction&gt;
         *   &lt;/complexContent&gt;
         * &lt;/complexType&gt;
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class ConcursoAcreedoresNO {


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
         *     &lt;/restriction&gt;
         *   &lt;/complexContent&gt;
         * &lt;/complexType&gt;
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class DecSustitutiva {


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
         *     &lt;/restriction&gt;
         *   &lt;/complexContent&gt;
         * &lt;/complexType&gt;
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class DecSustitutivaRectifica {


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
         *     &lt;/restriction&gt;
         *   &lt;/complexContent&gt;
         * &lt;/complexType&gt;
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class DestRegCriterioCajaNO {


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
         *     &lt;/restriction&gt;
         *   &lt;/complexContent&gt;
         * &lt;/complexType&gt;
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class DestRegCriterioCajaSI {


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
         *     &lt;/restriction&gt;
         *   &lt;/complexContent&gt;
         * &lt;/complexType&gt;
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class RegCriterioCajaNO {


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
         *     &lt;/restriction&gt;
         *   &lt;/complexContent&gt;
         * &lt;/complexType&gt;
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class RegCriterioCajaSI {


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
         *     &lt;/restriction&gt;
         *   &lt;/complexContent&gt;
         * &lt;/complexType&gt;
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class RegDevMensual {


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
     *         &lt;element name="OpInteriores" minOccurs="0"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;sequence&gt;
     *                   &lt;element name="BienesyServiciosCorrientes" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                   &lt;element name="BienesInversion" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                 &lt;/sequence&gt;
     *               &lt;/restriction&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *         &lt;element name="Importaciones" minOccurs="0"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;sequence&gt;
     *                   &lt;element name="BienesCorrientes" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                   &lt;element name="BienesInversion" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                 &lt;/sequence&gt;
     *               &lt;/restriction&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *         &lt;element name="AdqIntracomunitarias" minOccurs="0"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;sequence&gt;
     *                   &lt;element name="BienesCorrientes" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                   &lt;element name="BienesInversion" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                 &lt;/sequence&gt;
     *               &lt;/restriction&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *         &lt;element name="CompRegEspAgricGanadPesca" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *         &lt;element name="RectDeducciones" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *         &lt;element name="RegInversiones" type="{}tipo_ImpNegativo" minOccurs="0"/&gt;
     *         &lt;element name="SumaDeducciones" type="{}tipo_ImpNegativo" minOccurs="0"/&gt;
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
        "opInteriores",
        "importaciones",
        "adqIntracomunitarias",
        "compRegEspAgricGanadPesca",
        "rectDeducciones",
        "regInversiones",
        "sumaDeducciones"
    })
    public static class IVADeducibleGrupo1 {

        @XmlElement(name = "OpInteriores")
        protected AEATIVA2024 .IVADeducibleGrupo1 .OpInteriores opInteriores;
        @XmlElement(name = "Importaciones")
        protected AEATIVA2024 .IVADeducibleGrupo1 .Importaciones importaciones;
        @XmlElement(name = "AdqIntracomunitarias")
        protected AEATIVA2024 .IVADeducibleGrupo1 .AdqIntracomunitarias adqIntracomunitarias;
        @XmlElement(name = "CompRegEspAgricGanadPesca")
        protected TipoBaseImponibleYCuota compRegEspAgricGanadPesca;
        @XmlElement(name = "RectDeducciones")
        protected TipoBaseImponibleYCuota rectDeducciones;
        @XmlElement(name = "RegInversiones")
        protected BigDecimal regInversiones;
        @XmlElement(name = "SumaDeducciones")
        protected BigDecimal sumaDeducciones;

        /**
         * Obtiene el valor de la propiedad opInteriores.
         * 
         * @return
         *     possible object is
         *     {@link AEATIVA2024 .IVADeducibleGrupo1 .OpInteriores }
         *     
         */
        public AEATIVA2024 .IVADeducibleGrupo1 .OpInteriores getOpInteriores() {
            return opInteriores;
        }

        /**
         * Define el valor de la propiedad opInteriores.
         * 
         * @param value
         *     allowed object is
         *     {@link AEATIVA2024 .IVADeducibleGrupo1 .OpInteriores }
         *     
         */
        public void setOpInteriores(AEATIVA2024 .IVADeducibleGrupo1 .OpInteriores value) {
            this.opInteriores = value;
        }

        /**
         * Obtiene el valor de la propiedad importaciones.
         * 
         * @return
         *     possible object is
         *     {@link AEATIVA2024 .IVADeducibleGrupo1 .Importaciones }
         *     
         */
        public AEATIVA2024 .IVADeducibleGrupo1 .Importaciones getImportaciones() {
            return importaciones;
        }

        /**
         * Define el valor de la propiedad importaciones.
         * 
         * @param value
         *     allowed object is
         *     {@link AEATIVA2024 .IVADeducibleGrupo1 .Importaciones }
         *     
         */
        public void setImportaciones(AEATIVA2024 .IVADeducibleGrupo1 .Importaciones value) {
            this.importaciones = value;
        }

        /**
         * Obtiene el valor de la propiedad adqIntracomunitarias.
         * 
         * @return
         *     possible object is
         *     {@link AEATIVA2024 .IVADeducibleGrupo1 .AdqIntracomunitarias }
         *     
         */
        public AEATIVA2024 .IVADeducibleGrupo1 .AdqIntracomunitarias getAdqIntracomunitarias() {
            return adqIntracomunitarias;
        }

        /**
         * Define el valor de la propiedad adqIntracomunitarias.
         * 
         * @param value
         *     allowed object is
         *     {@link AEATIVA2024 .IVADeducibleGrupo1 .AdqIntracomunitarias }
         *     
         */
        public void setAdqIntracomunitarias(AEATIVA2024 .IVADeducibleGrupo1 .AdqIntracomunitarias value) {
            this.adqIntracomunitarias = value;
        }

        /**
         * Obtiene el valor de la propiedad compRegEspAgricGanadPesca.
         * 
         * @return
         *     possible object is
         *     {@link TipoBaseImponibleYCuota }
         *     
         */
        public TipoBaseImponibleYCuota getCompRegEspAgricGanadPesca() {
            return compRegEspAgricGanadPesca;
        }

        /**
         * Define el valor de la propiedad compRegEspAgricGanadPesca.
         * 
         * @param value
         *     allowed object is
         *     {@link TipoBaseImponibleYCuota }
         *     
         */
        public void setCompRegEspAgricGanadPesca(TipoBaseImponibleYCuota value) {
            this.compRegEspAgricGanadPesca = value;
        }

        /**
         * Obtiene el valor de la propiedad rectDeducciones.
         * 
         * @return
         *     possible object is
         *     {@link TipoBaseImponibleYCuota }
         *     
         */
        public TipoBaseImponibleYCuota getRectDeducciones() {
            return rectDeducciones;
        }

        /**
         * Define el valor de la propiedad rectDeducciones.
         * 
         * @param value
         *     allowed object is
         *     {@link TipoBaseImponibleYCuota }
         *     
         */
        public void setRectDeducciones(TipoBaseImponibleYCuota value) {
            this.rectDeducciones = value;
        }

        /**
         * Obtiene el valor de la propiedad regInversiones.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getRegInversiones() {
            return regInversiones;
        }

        /**
         * Define el valor de la propiedad regInversiones.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setRegInversiones(BigDecimal value) {
            this.regInversiones = value;
        }

        /**
         * Obtiene el valor de la propiedad sumaDeducciones.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getSumaDeducciones() {
            return sumaDeducciones;
        }

        /**
         * Define el valor de la propiedad sumaDeducciones.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setSumaDeducciones(BigDecimal value) {
            this.sumaDeducciones = value;
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
         *         &lt;element name="BienesCorrientes" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *         &lt;element name="BienesInversion" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
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
            "bienesCorrientes",
            "bienesInversion"
        })
        public static class AdqIntracomunitarias {

            @XmlElement(name = "BienesCorrientes")
            protected TipoBaseImponibleYCuota bienesCorrientes;
            @XmlElement(name = "BienesInversion")
            protected TipoBaseImponibleYCuota bienesInversion;

            /**
             * Obtiene el valor de la propiedad bienesCorrientes.
             * 
             * @return
             *     possible object is
             *     {@link TipoBaseImponibleYCuota }
             *     
             */
            public TipoBaseImponibleYCuota getBienesCorrientes() {
                return bienesCorrientes;
            }

            /**
             * Define el valor de la propiedad bienesCorrientes.
             * 
             * @param value
             *     allowed object is
             *     {@link TipoBaseImponibleYCuota }
             *     
             */
            public void setBienesCorrientes(TipoBaseImponibleYCuota value) {
                this.bienesCorrientes = value;
            }

            /**
             * Obtiene el valor de la propiedad bienesInversion.
             * 
             * @return
             *     possible object is
             *     {@link TipoBaseImponibleYCuota }
             *     
             */
            public TipoBaseImponibleYCuota getBienesInversion() {
                return bienesInversion;
            }

            /**
             * Define el valor de la propiedad bienesInversion.
             * 
             * @param value
             *     allowed object is
             *     {@link TipoBaseImponibleYCuota }
             *     
             */
            public void setBienesInversion(TipoBaseImponibleYCuota value) {
                this.bienesInversion = value;
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
         *         &lt;element name="BienesCorrientes" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *         &lt;element name="BienesInversion" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
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
            "bienesCorrientes",
            "bienesInversion"
        })
        public static class Importaciones {

            @XmlElement(name = "BienesCorrientes")
            protected TipoBaseImponibleYCuota bienesCorrientes;
            @XmlElement(name = "BienesInversion")
            protected TipoBaseImponibleYCuota bienesInversion;

            /**
             * Obtiene el valor de la propiedad bienesCorrientes.
             * 
             * @return
             *     possible object is
             *     {@link TipoBaseImponibleYCuota }
             *     
             */
            public TipoBaseImponibleYCuota getBienesCorrientes() {
                return bienesCorrientes;
            }

            /**
             * Define el valor de la propiedad bienesCorrientes.
             * 
             * @param value
             *     allowed object is
             *     {@link TipoBaseImponibleYCuota }
             *     
             */
            public void setBienesCorrientes(TipoBaseImponibleYCuota value) {
                this.bienesCorrientes = value;
            }

            /**
             * Obtiene el valor de la propiedad bienesInversion.
             * 
             * @return
             *     possible object is
             *     {@link TipoBaseImponibleYCuota }
             *     
             */
            public TipoBaseImponibleYCuota getBienesInversion() {
                return bienesInversion;
            }

            /**
             * Define el valor de la propiedad bienesInversion.
             * 
             * @param value
             *     allowed object is
             *     {@link TipoBaseImponibleYCuota }
             *     
             */
            public void setBienesInversion(TipoBaseImponibleYCuota value) {
                this.bienesInversion = value;
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
         *         &lt;element name="BienesyServiciosCorrientes" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *         &lt;element name="BienesInversion" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
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
            "bienesyServiciosCorrientes",
            "bienesInversion"
        })
        public static class OpInteriores {

            @XmlElement(name = "BienesyServiciosCorrientes")
            protected TipoBaseImponibleYCuota bienesyServiciosCorrientes;
            @XmlElement(name = "BienesInversion")
            protected TipoBaseImponibleYCuota bienesInversion;

            /**
             * Obtiene el valor de la propiedad bienesyServiciosCorrientes.
             * 
             * @return
             *     possible object is
             *     {@link TipoBaseImponibleYCuota }
             *     
             */
            public TipoBaseImponibleYCuota getBienesyServiciosCorrientes() {
                return bienesyServiciosCorrientes;
            }

            /**
             * Define el valor de la propiedad bienesyServiciosCorrientes.
             * 
             * @param value
             *     allowed object is
             *     {@link TipoBaseImponibleYCuota }
             *     
             */
            public void setBienesyServiciosCorrientes(TipoBaseImponibleYCuota value) {
                this.bienesyServiciosCorrientes = value;
            }

            /**
             * Obtiene el valor de la propiedad bienesInversion.
             * 
             * @return
             *     possible object is
             *     {@link TipoBaseImponibleYCuota }
             *     
             */
            public TipoBaseImponibleYCuota getBienesInversion() {
                return bienesInversion;
            }

            /**
             * Define el valor de la propiedad bienesInversion.
             * 
             * @param value
             *     allowed object is
             *     {@link TipoBaseImponibleYCuota }
             *     
             */
            public void setBienesInversion(TipoBaseImponibleYCuota value) {
                this.bienesInversion = value;
            }

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
     *         &lt;element name="OpInteriores" minOccurs="0"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;sequence&gt;
     *                   &lt;element name="BienesyServiciosCorrientes" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                   &lt;element name="BienesInversion" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                 &lt;/sequence&gt;
     *               &lt;/restriction&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *         &lt;element name="Importaciones" minOccurs="0"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;sequence&gt;
     *                   &lt;element name="BienesCorrientes" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                   &lt;element name="BienesInversion" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                 &lt;/sequence&gt;
     *               &lt;/restriction&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *         &lt;element name="AdqIntracomunitarias" minOccurs="0"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;sequence&gt;
     *                   &lt;element name="BienesCorrientes" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                   &lt;element name="BienesInversion" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                 &lt;/sequence&gt;
     *               &lt;/restriction&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *         &lt;element name="CompRegEspAgricGanadPesca" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *         &lt;element name="RectDeducciones" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *         &lt;element name="RegInversiones" type="{}tipo_ImpNegativo" minOccurs="0"/&gt;
     *         &lt;element name="SumaDeducciones" type="{}tipo_ImpNegativo" minOccurs="0"/&gt;
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
        "opInteriores",
        "importaciones",
        "adqIntracomunitarias",
        "compRegEspAgricGanadPesca",
        "rectDeducciones",
        "regInversiones",
        "sumaDeducciones"
    })
    public static class IVADeducibleGrupo2 {

        @XmlElement(name = "OpInteriores")
        protected AEATIVA2024 .IVADeducibleGrupo2 .OpInteriores opInteriores;
        @XmlElement(name = "Importaciones")
        protected AEATIVA2024 .IVADeducibleGrupo2 .Importaciones importaciones;
        @XmlElement(name = "AdqIntracomunitarias")
        protected AEATIVA2024 .IVADeducibleGrupo2 .AdqIntracomunitarias adqIntracomunitarias;
        @XmlElement(name = "CompRegEspAgricGanadPesca")
        protected TipoBaseImponibleYCuota compRegEspAgricGanadPesca;
        @XmlElement(name = "RectDeducciones")
        protected TipoBaseImponibleYCuota rectDeducciones;
        @XmlElement(name = "RegInversiones")
        protected BigDecimal regInversiones;
        @XmlElement(name = "SumaDeducciones")
        protected BigDecimal sumaDeducciones;

        /**
         * Obtiene el valor de la propiedad opInteriores.
         * 
         * @return
         *     possible object is
         *     {@link AEATIVA2024 .IVADeducibleGrupo2 .OpInteriores }
         *     
         */
        public AEATIVA2024 .IVADeducibleGrupo2 .OpInteriores getOpInteriores() {
            return opInteriores;
        }

        /**
         * Define el valor de la propiedad opInteriores.
         * 
         * @param value
         *     allowed object is
         *     {@link AEATIVA2024 .IVADeducibleGrupo2 .OpInteriores }
         *     
         */
        public void setOpInteriores(AEATIVA2024 .IVADeducibleGrupo2 .OpInteriores value) {
            this.opInteriores = value;
        }

        /**
         * Obtiene el valor de la propiedad importaciones.
         * 
         * @return
         *     possible object is
         *     {@link AEATIVA2024 .IVADeducibleGrupo2 .Importaciones }
         *     
         */
        public AEATIVA2024 .IVADeducibleGrupo2 .Importaciones getImportaciones() {
            return importaciones;
        }

        /**
         * Define el valor de la propiedad importaciones.
         * 
         * @param value
         *     allowed object is
         *     {@link AEATIVA2024 .IVADeducibleGrupo2 .Importaciones }
         *     
         */
        public void setImportaciones(AEATIVA2024 .IVADeducibleGrupo2 .Importaciones value) {
            this.importaciones = value;
        }

        /**
         * Obtiene el valor de la propiedad adqIntracomunitarias.
         * 
         * @return
         *     possible object is
         *     {@link AEATIVA2024 .IVADeducibleGrupo2 .AdqIntracomunitarias }
         *     
         */
        public AEATIVA2024 .IVADeducibleGrupo2 .AdqIntracomunitarias getAdqIntracomunitarias() {
            return adqIntracomunitarias;
        }

        /**
         * Define el valor de la propiedad adqIntracomunitarias.
         * 
         * @param value
         *     allowed object is
         *     {@link AEATIVA2024 .IVADeducibleGrupo2 .AdqIntracomunitarias }
         *     
         */
        public void setAdqIntracomunitarias(AEATIVA2024 .IVADeducibleGrupo2 .AdqIntracomunitarias value) {
            this.adqIntracomunitarias = value;
        }

        /**
         * Obtiene el valor de la propiedad compRegEspAgricGanadPesca.
         * 
         * @return
         *     possible object is
         *     {@link TipoBaseImponibleYCuota }
         *     
         */
        public TipoBaseImponibleYCuota getCompRegEspAgricGanadPesca() {
            return compRegEspAgricGanadPesca;
        }

        /**
         * Define el valor de la propiedad compRegEspAgricGanadPesca.
         * 
         * @param value
         *     allowed object is
         *     {@link TipoBaseImponibleYCuota }
         *     
         */
        public void setCompRegEspAgricGanadPesca(TipoBaseImponibleYCuota value) {
            this.compRegEspAgricGanadPesca = value;
        }

        /**
         * Obtiene el valor de la propiedad rectDeducciones.
         * 
         * @return
         *     possible object is
         *     {@link TipoBaseImponibleYCuota }
         *     
         */
        public TipoBaseImponibleYCuota getRectDeducciones() {
            return rectDeducciones;
        }

        /**
         * Define el valor de la propiedad rectDeducciones.
         * 
         * @param value
         *     allowed object is
         *     {@link TipoBaseImponibleYCuota }
         *     
         */
        public void setRectDeducciones(TipoBaseImponibleYCuota value) {
            this.rectDeducciones = value;
        }

        /**
         * Obtiene el valor de la propiedad regInversiones.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getRegInversiones() {
            return regInversiones;
        }

        /**
         * Define el valor de la propiedad regInversiones.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setRegInversiones(BigDecimal value) {
            this.regInversiones = value;
        }

        /**
         * Obtiene el valor de la propiedad sumaDeducciones.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getSumaDeducciones() {
            return sumaDeducciones;
        }

        /**
         * Define el valor de la propiedad sumaDeducciones.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setSumaDeducciones(BigDecimal value) {
            this.sumaDeducciones = value;
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
         *         &lt;element name="BienesCorrientes" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *         &lt;element name="BienesInversion" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
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
            "bienesCorrientes",
            "bienesInversion"
        })
        public static class AdqIntracomunitarias {

            @XmlElement(name = "BienesCorrientes")
            protected TipoBaseImponibleYCuota bienesCorrientes;
            @XmlElement(name = "BienesInversion")
            protected TipoBaseImponibleYCuota bienesInversion;

            /**
             * Obtiene el valor de la propiedad bienesCorrientes.
             * 
             * @return
             *     possible object is
             *     {@link TipoBaseImponibleYCuota }
             *     
             */
            public TipoBaseImponibleYCuota getBienesCorrientes() {
                return bienesCorrientes;
            }

            /**
             * Define el valor de la propiedad bienesCorrientes.
             * 
             * @param value
             *     allowed object is
             *     {@link TipoBaseImponibleYCuota }
             *     
             */
            public void setBienesCorrientes(TipoBaseImponibleYCuota value) {
                this.bienesCorrientes = value;
            }

            /**
             * Obtiene el valor de la propiedad bienesInversion.
             * 
             * @return
             *     possible object is
             *     {@link TipoBaseImponibleYCuota }
             *     
             */
            public TipoBaseImponibleYCuota getBienesInversion() {
                return bienesInversion;
            }

            /**
             * Define el valor de la propiedad bienesInversion.
             * 
             * @param value
             *     allowed object is
             *     {@link TipoBaseImponibleYCuota }
             *     
             */
            public void setBienesInversion(TipoBaseImponibleYCuota value) {
                this.bienesInversion = value;
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
         *         &lt;element name="BienesCorrientes" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *         &lt;element name="BienesInversion" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
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
            "bienesCorrientes",
            "bienesInversion"
        })
        public static class Importaciones {

            @XmlElement(name = "BienesCorrientes")
            protected TipoBaseImponibleYCuota bienesCorrientes;
            @XmlElement(name = "BienesInversion")
            protected TipoBaseImponibleYCuota bienesInversion;

            /**
             * Obtiene el valor de la propiedad bienesCorrientes.
             * 
             * @return
             *     possible object is
             *     {@link TipoBaseImponibleYCuota }
             *     
             */
            public TipoBaseImponibleYCuota getBienesCorrientes() {
                return bienesCorrientes;
            }

            /**
             * Define el valor de la propiedad bienesCorrientes.
             * 
             * @param value
             *     allowed object is
             *     {@link TipoBaseImponibleYCuota }
             *     
             */
            public void setBienesCorrientes(TipoBaseImponibleYCuota value) {
                this.bienesCorrientes = value;
            }

            /**
             * Obtiene el valor de la propiedad bienesInversion.
             * 
             * @return
             *     possible object is
             *     {@link TipoBaseImponibleYCuota }
             *     
             */
            public TipoBaseImponibleYCuota getBienesInversion() {
                return bienesInversion;
            }

            /**
             * Define el valor de la propiedad bienesInversion.
             * 
             * @param value
             *     allowed object is
             *     {@link TipoBaseImponibleYCuota }
             *     
             */
            public void setBienesInversion(TipoBaseImponibleYCuota value) {
                this.bienesInversion = value;
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
         *         &lt;element name="BienesyServiciosCorrientes" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *         &lt;element name="BienesInversion" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
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
            "bienesyServiciosCorrientes",
            "bienesInversion"
        })
        public static class OpInteriores {

            @XmlElement(name = "BienesyServiciosCorrientes")
            protected TipoBaseImponibleYCuota bienesyServiciosCorrientes;
            @XmlElement(name = "BienesInversion")
            protected TipoBaseImponibleYCuota bienesInversion;

            /**
             * Obtiene el valor de la propiedad bienesyServiciosCorrientes.
             * 
             * @return
             *     possible object is
             *     {@link TipoBaseImponibleYCuota }
             *     
             */
            public TipoBaseImponibleYCuota getBienesyServiciosCorrientes() {
                return bienesyServiciosCorrientes;
            }

            /**
             * Define el valor de la propiedad bienesyServiciosCorrientes.
             * 
             * @param value
             *     allowed object is
             *     {@link TipoBaseImponibleYCuota }
             *     
             */
            public void setBienesyServiciosCorrientes(TipoBaseImponibleYCuota value) {
                this.bienesyServiciosCorrientes = value;
            }

            /**
             * Obtiene el valor de la propiedad bienesInversion.
             * 
             * @return
             *     possible object is
             *     {@link TipoBaseImponibleYCuota }
             *     
             */
            public TipoBaseImponibleYCuota getBienesInversion() {
                return bienesInversion;
            }

            /**
             * Define el valor de la propiedad bienesInversion.
             * 
             * @param value
             *     allowed object is
             *     {@link TipoBaseImponibleYCuota }
             *     
             */
            public void setBienesInversion(TipoBaseImponibleYCuota value) {
                this.bienesInversion = value;
            }

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
     *         &lt;element name="OpInteriores" minOccurs="0"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;sequence&gt;
     *                   &lt;element name="BienesyServiciosCorrientes" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                   &lt;element name="BienesInversion" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                 &lt;/sequence&gt;
     *               &lt;/restriction&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *         &lt;element name="Importaciones" minOccurs="0"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;sequence&gt;
     *                   &lt;element name="BienesCorrientes" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                   &lt;element name="BienesInversion" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                 &lt;/sequence&gt;
     *               &lt;/restriction&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *         &lt;element name="AdqIntracomunitarias" minOccurs="0"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;sequence&gt;
     *                   &lt;element name="BienesCorrientes" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                   &lt;element name="BienesInversion" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                 &lt;/sequence&gt;
     *               &lt;/restriction&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *         &lt;element name="CompRegEspAgricGanadPesca" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *         &lt;element name="RectDeducciones" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *         &lt;element name="RegInversiones" type="{}tipo_ImpNegativo" minOccurs="0"/&gt;
     *         &lt;element name="SumaDeducciones" type="{}tipo_ImpNegativo" minOccurs="0"/&gt;
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
        "opInteriores",
        "importaciones",
        "adqIntracomunitarias",
        "compRegEspAgricGanadPesca",
        "rectDeducciones",
        "regInversiones",
        "sumaDeducciones"
    })
    public static class IVADeducibleGrupo3 {

        @XmlElement(name = "OpInteriores")
        protected AEATIVA2024 .IVADeducibleGrupo3 .OpInteriores opInteriores;
        @XmlElement(name = "Importaciones")
        protected AEATIVA2024 .IVADeducibleGrupo3 .Importaciones importaciones;
        @XmlElement(name = "AdqIntracomunitarias")
        protected AEATIVA2024 .IVADeducibleGrupo3 .AdqIntracomunitarias adqIntracomunitarias;
        @XmlElement(name = "CompRegEspAgricGanadPesca")
        protected TipoBaseImponibleYCuota compRegEspAgricGanadPesca;
        @XmlElement(name = "RectDeducciones")
        protected TipoBaseImponibleYCuota rectDeducciones;
        @XmlElement(name = "RegInversiones")
        protected BigDecimal regInversiones;
        @XmlElement(name = "SumaDeducciones")
        protected BigDecimal sumaDeducciones;

        /**
         * Obtiene el valor de la propiedad opInteriores.
         * 
         * @return
         *     possible object is
         *     {@link AEATIVA2024 .IVADeducibleGrupo3 .OpInteriores }
         *     
         */
        public AEATIVA2024 .IVADeducibleGrupo3 .OpInteriores getOpInteriores() {
            return opInteriores;
        }

        /**
         * Define el valor de la propiedad opInteriores.
         * 
         * @param value
         *     allowed object is
         *     {@link AEATIVA2024 .IVADeducibleGrupo3 .OpInteriores }
         *     
         */
        public void setOpInteriores(AEATIVA2024 .IVADeducibleGrupo3 .OpInteriores value) {
            this.opInteriores = value;
        }

        /**
         * Obtiene el valor de la propiedad importaciones.
         * 
         * @return
         *     possible object is
         *     {@link AEATIVA2024 .IVADeducibleGrupo3 .Importaciones }
         *     
         */
        public AEATIVA2024 .IVADeducibleGrupo3 .Importaciones getImportaciones() {
            return importaciones;
        }

        /**
         * Define el valor de la propiedad importaciones.
         * 
         * @param value
         *     allowed object is
         *     {@link AEATIVA2024 .IVADeducibleGrupo3 .Importaciones }
         *     
         */
        public void setImportaciones(AEATIVA2024 .IVADeducibleGrupo3 .Importaciones value) {
            this.importaciones = value;
        }

        /**
         * Obtiene el valor de la propiedad adqIntracomunitarias.
         * 
         * @return
         *     possible object is
         *     {@link AEATIVA2024 .IVADeducibleGrupo3 .AdqIntracomunitarias }
         *     
         */
        public AEATIVA2024 .IVADeducibleGrupo3 .AdqIntracomunitarias getAdqIntracomunitarias() {
            return adqIntracomunitarias;
        }

        /**
         * Define el valor de la propiedad adqIntracomunitarias.
         * 
         * @param value
         *     allowed object is
         *     {@link AEATIVA2024 .IVADeducibleGrupo3 .AdqIntracomunitarias }
         *     
         */
        public void setAdqIntracomunitarias(AEATIVA2024 .IVADeducibleGrupo3 .AdqIntracomunitarias value) {
            this.adqIntracomunitarias = value;
        }

        /**
         * Obtiene el valor de la propiedad compRegEspAgricGanadPesca.
         * 
         * @return
         *     possible object is
         *     {@link TipoBaseImponibleYCuota }
         *     
         */
        public TipoBaseImponibleYCuota getCompRegEspAgricGanadPesca() {
            return compRegEspAgricGanadPesca;
        }

        /**
         * Define el valor de la propiedad compRegEspAgricGanadPesca.
         * 
         * @param value
         *     allowed object is
         *     {@link TipoBaseImponibleYCuota }
         *     
         */
        public void setCompRegEspAgricGanadPesca(TipoBaseImponibleYCuota value) {
            this.compRegEspAgricGanadPesca = value;
        }

        /**
         * Obtiene el valor de la propiedad rectDeducciones.
         * 
         * @return
         *     possible object is
         *     {@link TipoBaseImponibleYCuota }
         *     
         */
        public TipoBaseImponibleYCuota getRectDeducciones() {
            return rectDeducciones;
        }

        /**
         * Define el valor de la propiedad rectDeducciones.
         * 
         * @param value
         *     allowed object is
         *     {@link TipoBaseImponibleYCuota }
         *     
         */
        public void setRectDeducciones(TipoBaseImponibleYCuota value) {
            this.rectDeducciones = value;
        }

        /**
         * Obtiene el valor de la propiedad regInversiones.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getRegInversiones() {
            return regInversiones;
        }

        /**
         * Define el valor de la propiedad regInversiones.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setRegInversiones(BigDecimal value) {
            this.regInversiones = value;
        }

        /**
         * Obtiene el valor de la propiedad sumaDeducciones.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getSumaDeducciones() {
            return sumaDeducciones;
        }

        /**
         * Define el valor de la propiedad sumaDeducciones.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setSumaDeducciones(BigDecimal value) {
            this.sumaDeducciones = value;
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
         *         &lt;element name="BienesCorrientes" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *         &lt;element name="BienesInversion" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
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
            "bienesCorrientes",
            "bienesInversion"
        })
        public static class AdqIntracomunitarias {

            @XmlElement(name = "BienesCorrientes")
            protected TipoBaseImponibleYCuota bienesCorrientes;
            @XmlElement(name = "BienesInversion")
            protected TipoBaseImponibleYCuota bienesInversion;

            /**
             * Obtiene el valor de la propiedad bienesCorrientes.
             * 
             * @return
             *     possible object is
             *     {@link TipoBaseImponibleYCuota }
             *     
             */
            public TipoBaseImponibleYCuota getBienesCorrientes() {
                return bienesCorrientes;
            }

            /**
             * Define el valor de la propiedad bienesCorrientes.
             * 
             * @param value
             *     allowed object is
             *     {@link TipoBaseImponibleYCuota }
             *     
             */
            public void setBienesCorrientes(TipoBaseImponibleYCuota value) {
                this.bienesCorrientes = value;
            }

            /**
             * Obtiene el valor de la propiedad bienesInversion.
             * 
             * @return
             *     possible object is
             *     {@link TipoBaseImponibleYCuota }
             *     
             */
            public TipoBaseImponibleYCuota getBienesInversion() {
                return bienesInversion;
            }

            /**
             * Define el valor de la propiedad bienesInversion.
             * 
             * @param value
             *     allowed object is
             *     {@link TipoBaseImponibleYCuota }
             *     
             */
            public void setBienesInversion(TipoBaseImponibleYCuota value) {
                this.bienesInversion = value;
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
         *         &lt;element name="BienesCorrientes" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *         &lt;element name="BienesInversion" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
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
            "bienesCorrientes",
            "bienesInversion"
        })
        public static class Importaciones {

            @XmlElement(name = "BienesCorrientes")
            protected TipoBaseImponibleYCuota bienesCorrientes;
            @XmlElement(name = "BienesInversion")
            protected TipoBaseImponibleYCuota bienesInversion;

            /**
             * Obtiene el valor de la propiedad bienesCorrientes.
             * 
             * @return
             *     possible object is
             *     {@link TipoBaseImponibleYCuota }
             *     
             */
            public TipoBaseImponibleYCuota getBienesCorrientes() {
                return bienesCorrientes;
            }

            /**
             * Define el valor de la propiedad bienesCorrientes.
             * 
             * @param value
             *     allowed object is
             *     {@link TipoBaseImponibleYCuota }
             *     
             */
            public void setBienesCorrientes(TipoBaseImponibleYCuota value) {
                this.bienesCorrientes = value;
            }

            /**
             * Obtiene el valor de la propiedad bienesInversion.
             * 
             * @return
             *     possible object is
             *     {@link TipoBaseImponibleYCuota }
             *     
             */
            public TipoBaseImponibleYCuota getBienesInversion() {
                return bienesInversion;
            }

            /**
             * Define el valor de la propiedad bienesInversion.
             * 
             * @param value
             *     allowed object is
             *     {@link TipoBaseImponibleYCuota }
             *     
             */
            public void setBienesInversion(TipoBaseImponibleYCuota value) {
                this.bienesInversion = value;
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
         *         &lt;element name="BienesyServiciosCorrientes" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *         &lt;element name="BienesInversion" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
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
            "bienesyServiciosCorrientes",
            "bienesInversion"
        })
        public static class OpInteriores {

            @XmlElement(name = "BienesyServiciosCorrientes")
            protected TipoBaseImponibleYCuota bienesyServiciosCorrientes;
            @XmlElement(name = "BienesInversion")
            protected TipoBaseImponibleYCuota bienesInversion;

            /**
             * Obtiene el valor de la propiedad bienesyServiciosCorrientes.
             * 
             * @return
             *     possible object is
             *     {@link TipoBaseImponibleYCuota }
             *     
             */
            public TipoBaseImponibleYCuota getBienesyServiciosCorrientes() {
                return bienesyServiciosCorrientes;
            }

            /**
             * Define el valor de la propiedad bienesyServiciosCorrientes.
             * 
             * @param value
             *     allowed object is
             *     {@link TipoBaseImponibleYCuota }
             *     
             */
            public void setBienesyServiciosCorrientes(TipoBaseImponibleYCuota value) {
                this.bienesyServiciosCorrientes = value;
            }

            /**
             * Obtiene el valor de la propiedad bienesInversion.
             * 
             * @return
             *     possible object is
             *     {@link TipoBaseImponibleYCuota }
             *     
             */
            public TipoBaseImponibleYCuota getBienesInversion() {
                return bienesInversion;
            }

            /**
             * Define el valor de la propiedad bienesInversion.
             * 
             * @param value
             *     allowed object is
             *     {@link TipoBaseImponibleYCuota }
             *     
             */
            public void setBienesInversion(TipoBaseImponibleYCuota value) {
                this.bienesInversion = value;
            }

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
     *         &lt;element name="RegCuotas" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
     *         &lt;element name="SumResultados" type="{}tipo_ImpNegativo" minOccurs="0"/&gt;
     *         &lt;element name="IvaAduana" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
     *         &lt;element name="CompCuotasEjercicioAnterior" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
     *         &lt;element name="ResLiquidacion" type="{}tipo_ImpNegativo" minOccurs="0"/&gt;
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
        "regCuotas",
        "sumResultados",
        "ivaAduana",
        "compCuotasEjercicioAnterior",
        "resLiquidacion"
    })
    public static class LiqAnual {

        @XmlElement(name = "RegCuotas")
        protected BigDecimal regCuotas;
        @XmlElement(name = "SumResultados")
        protected BigDecimal sumResultados;
        @XmlElement(name = "IvaAduana")
        protected BigDecimal ivaAduana;
        @XmlElement(name = "CompCuotasEjercicioAnterior")
        protected BigDecimal compCuotasEjercicioAnterior;
        @XmlElement(name = "ResLiquidacion")
        protected BigDecimal resLiquidacion;

        /**
         * Obtiene el valor de la propiedad regCuotas.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getRegCuotas() {
            return regCuotas;
        }

        /**
         * Define el valor de la propiedad regCuotas.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setRegCuotas(BigDecimal value) {
            this.regCuotas = value;
        }

        /**
         * Obtiene el valor de la propiedad sumResultados.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getSumResultados() {
            return sumResultados;
        }

        /**
         * Define el valor de la propiedad sumResultados.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setSumResultados(BigDecimal value) {
            this.sumResultados = value;
        }

        /**
         * Obtiene el valor de la propiedad ivaAduana.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getIvaAduana() {
            return ivaAduana;
        }

        /**
         * Define el valor de la propiedad ivaAduana.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setIvaAduana(BigDecimal value) {
            this.ivaAduana = value;
        }

        /**
         * Obtiene el valor de la propiedad compCuotasEjercicioAnterior.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getCompCuotasEjercicioAnterior() {
            return compCuotasEjercicioAnterior;
        }

        /**
         * Define el valor de la propiedad compCuotasEjercicioAnterior.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setCompCuotasEjercicioAnterior(BigDecimal value) {
            this.compCuotasEjercicioAnterior = value;
        }

        /**
         * Obtiene el valor de la propiedad resLiquidacion.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getResLiquidacion() {
            return resLiquidacion;
        }

        /**
         * Define el valor de la propiedad resLiquidacion.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setResLiquidacion(BigDecimal value) {
            this.resLiquidacion = value;
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
     *         &lt;element name="AdqInterioresExentas" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
     *         &lt;element name="AdqIntracomunitariasExentas" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
     *         &lt;element name="ImportacionesExentas" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
     *         &lt;element name="BasesIVASoportadoNoDeducible" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
     *         &lt;element name="OpSujetas" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
     *         &lt;element name="EntregasInteriores" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
     *         &lt;element name="ServInversionSP" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
     *         &lt;element name="EntregasCriterioCajaBase" minOccurs="0"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;sequence&gt;
     *                   &lt;element name="TipoX" type="{}tipo_BaseImponible_y_Cuota"/&gt;
     *                 &lt;/sequence&gt;
     *               &lt;/restriction&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *         &lt;element name="AdqCriterioCajaBase" minOccurs="0"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;sequence&gt;
     *                   &lt;element name="TipoX" type="{}tipo_BaseImponible_y_Cuota"/&gt;
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
        "adqInterioresExentas",
        "adqIntracomunitariasExentas",
        "importacionesExentas",
        "basesIVASoportadoNoDeducible",
        "opSujetas",
        "entregasInteriores",
        "servInversionSP",
        "entregasCriterioCajaBase",
        "adqCriterioCajaBase"
    })
    public static class OpEspecificas {

        @XmlElement(name = "AdqInterioresExentas")
        protected BigDecimal adqInterioresExentas;
        @XmlElement(name = "AdqIntracomunitariasExentas")
        protected BigDecimal adqIntracomunitariasExentas;
        @XmlElement(name = "ImportacionesExentas")
        protected BigDecimal importacionesExentas;
        @XmlElement(name = "BasesIVASoportadoNoDeducible")
        protected BigDecimal basesIVASoportadoNoDeducible;
        @XmlElement(name = "OpSujetas")
        protected BigDecimal opSujetas;
        @XmlElement(name = "EntregasInteriores")
        protected BigDecimal entregasInteriores;
        @XmlElement(name = "ServInversionSP")
        protected BigDecimal servInversionSP;
        @XmlElement(name = "EntregasCriterioCajaBase")
        protected AEATIVA2024 .OpEspecificas.EntregasCriterioCajaBase entregasCriterioCajaBase;
        @XmlElement(name = "AdqCriterioCajaBase")
        protected AEATIVA2024 .OpEspecificas.AdqCriterioCajaBase adqCriterioCajaBase;

        /**
         * Obtiene el valor de la propiedad adqInterioresExentas.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getAdqInterioresExentas() {
            return adqInterioresExentas;
        }

        /**
         * Define el valor de la propiedad adqInterioresExentas.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setAdqInterioresExentas(BigDecimal value) {
            this.adqInterioresExentas = value;
        }

        /**
         * Obtiene el valor de la propiedad adqIntracomunitariasExentas.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getAdqIntracomunitariasExentas() {
            return adqIntracomunitariasExentas;
        }

        /**
         * Define el valor de la propiedad adqIntracomunitariasExentas.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setAdqIntracomunitariasExentas(BigDecimal value) {
            this.adqIntracomunitariasExentas = value;
        }

        /**
         * Obtiene el valor de la propiedad importacionesExentas.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getImportacionesExentas() {
            return importacionesExentas;
        }

        /**
         * Define el valor de la propiedad importacionesExentas.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setImportacionesExentas(BigDecimal value) {
            this.importacionesExentas = value;
        }

        /**
         * Obtiene el valor de la propiedad basesIVASoportadoNoDeducible.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getBasesIVASoportadoNoDeducible() {
            return basesIVASoportadoNoDeducible;
        }

        /**
         * Define el valor de la propiedad basesIVASoportadoNoDeducible.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setBasesIVASoportadoNoDeducible(BigDecimal value) {
            this.basesIVASoportadoNoDeducible = value;
        }

        /**
         * Obtiene el valor de la propiedad opSujetas.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getOpSujetas() {
            return opSujetas;
        }

        /**
         * Define el valor de la propiedad opSujetas.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setOpSujetas(BigDecimal value) {
            this.opSujetas = value;
        }

        /**
         * Obtiene el valor de la propiedad entregasInteriores.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getEntregasInteriores() {
            return entregasInteriores;
        }

        /**
         * Define el valor de la propiedad entregasInteriores.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setEntregasInteriores(BigDecimal value) {
            this.entregasInteriores = value;
        }

        /**
         * Obtiene el valor de la propiedad servInversionSP.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getServInversionSP() {
            return servInversionSP;
        }

        /**
         * Define el valor de la propiedad servInversionSP.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setServInversionSP(BigDecimal value) {
            this.servInversionSP = value;
        }

        /**
         * Obtiene el valor de la propiedad entregasCriterioCajaBase.
         * 
         * @return
         *     possible object is
         *     {@link AEATIVA2024 .OpEspecificas.EntregasCriterioCajaBase }
         *     
         */
        public AEATIVA2024 .OpEspecificas.EntregasCriterioCajaBase getEntregasCriterioCajaBase() {
            return entregasCriterioCajaBase;
        }

        /**
         * Define el valor de la propiedad entregasCriterioCajaBase.
         * 
         * @param value
         *     allowed object is
         *     {@link AEATIVA2024 .OpEspecificas.EntregasCriterioCajaBase }
         *     
         */
        public void setEntregasCriterioCajaBase(AEATIVA2024 .OpEspecificas.EntregasCriterioCajaBase value) {
            this.entregasCriterioCajaBase = value;
        }

        /**
         * Obtiene el valor de la propiedad adqCriterioCajaBase.
         * 
         * @return
         *     possible object is
         *     {@link AEATIVA2024 .OpEspecificas.AdqCriterioCajaBase }
         *     
         */
        public AEATIVA2024 .OpEspecificas.AdqCriterioCajaBase getAdqCriterioCajaBase() {
            return adqCriterioCajaBase;
        }

        /**
         * Define el valor de la propiedad adqCriterioCajaBase.
         * 
         * @param value
         *     allowed object is
         *     {@link AEATIVA2024 .OpEspecificas.AdqCriterioCajaBase }
         *     
         */
        public void setAdqCriterioCajaBase(AEATIVA2024 .OpEspecificas.AdqCriterioCajaBase value) {
            this.adqCriterioCajaBase = value;
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
         *         &lt;element name="TipoX" type="{}tipo_BaseImponible_y_Cuota"/&gt;
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
            "tipoX"
        })
        public static class AdqCriterioCajaBase {

            @XmlElement(name = "TipoX", required = true)
            protected TipoBaseImponibleYCuota tipoX;

            /**
             * Obtiene el valor de la propiedad tipoX.
             * 
             * @return
             *     possible object is
             *     {@link TipoBaseImponibleYCuota }
             *     
             */
            public TipoBaseImponibleYCuota getTipoX() {
                return tipoX;
            }

            /**
             * Define el valor de la propiedad tipoX.
             * 
             * @param value
             *     allowed object is
             *     {@link TipoBaseImponibleYCuota }
             *     
             */
            public void setTipoX(TipoBaseImponibleYCuota value) {
                this.tipoX = value;
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
         *         &lt;element name="TipoX" type="{}tipo_BaseImponible_y_Cuota"/&gt;
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
            "tipoX"
        })
        public static class EntregasCriterioCajaBase {

            @XmlElement(name = "TipoX", required = true)
            protected TipoBaseImponibleYCuota tipoX;

            /**
             * Obtiene el valor de la propiedad tipoX.
             * 
             * @return
             *     possible object is
             *     {@link TipoBaseImponibleYCuota }
             *     
             */
            public TipoBaseImponibleYCuota getTipoX() {
                return tipoX;
            }

            /**
             * Define el valor de la propiedad tipoX.
             * 
             * @param value
             *     allowed object is
             *     {@link TipoBaseImponibleYCuota }
             *     
             */
            public void setTipoX(TipoBaseImponibleYCuota value) {
                this.tipoX = value;
            }

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
     *         &lt;element name="Pro" maxOccurs="30"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;sequence&gt;
     *                   &lt;element name="Actividad" type="{}tipo_ActProrratas" minOccurs="0"/&gt;
     *                   &lt;element name="CNAE" type="{}tipo_CNAE"/&gt;
     *                   &lt;element name="ImpOper" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
     *                   &lt;element name="ImpOperConDrchoDed" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
     *                   &lt;element name="Tipo" type="{}tipo_Prorrata" minOccurs="0"/&gt;
     *                   &lt;element name="Porc" type="{}tipo_Porcentaje" minOccurs="0"/&gt;
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
        "pro"
    })
    public static class Prorratas {

        @XmlElement(name = "Pro", required = true)
        protected List<AEATIVA2024 .Prorratas.Pro> pro;

        /**
         * Gets the value of the pro property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the pro property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getPro().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link AEATIVA2024 .Prorratas.Pro }
         * 
         * 
         */
        public List<AEATIVA2024 .Prorratas.Pro> getPro() {
            if (pro == null) {
                pro = new ArrayList<AEATIVA2024 .Prorratas.Pro>();
            }
            return this.pro;
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
         *         &lt;element name="Actividad" type="{}tipo_ActProrratas" minOccurs="0"/&gt;
         *         &lt;element name="CNAE" type="{}tipo_CNAE"/&gt;
         *         &lt;element name="ImpOper" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
         *         &lt;element name="ImpOperConDrchoDed" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
         *         &lt;element name="Tipo" type="{}tipo_Prorrata" minOccurs="0"/&gt;
         *         &lt;element name="Porc" type="{}tipo_Porcentaje" minOccurs="0"/&gt;
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
            "actividad",
            "cnae",
            "impOper",
            "impOperConDrchoDed",
            "tipo",
            "porc"
        })
        public static class Pro {

            @XmlElement(name = "Actividad")
            protected String actividad;
            @XmlElement(name = "CNAE", required = true)
            protected String cnae;
            @XmlElement(name = "ImpOper")
            protected BigDecimal impOper;
            @XmlElement(name = "ImpOperConDrchoDed")
            protected BigDecimal impOperConDrchoDed;
            @XmlElement(name = "Tipo")
            protected String tipo;
            @XmlElement(name = "Porc")
            protected BigDecimal porc;

            /**
             * Obtiene el valor de la propiedad actividad.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getActividad() {
                return actividad;
            }

            /**
             * Define el valor de la propiedad actividad.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setActividad(String value) {
                this.actividad = value;
            }

            /**
             * Obtiene el valor de la propiedad cnae.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getCNAE() {
                return cnae;
            }

            /**
             * Define el valor de la propiedad cnae.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setCNAE(String value) {
                this.cnae = value;
            }

            /**
             * Obtiene el valor de la propiedad impOper.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getImpOper() {
                return impOper;
            }

            /**
             * Define el valor de la propiedad impOper.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setImpOper(BigDecimal value) {
                this.impOper = value;
            }

            /**
             * Obtiene el valor de la propiedad impOperConDrchoDed.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getImpOperConDrchoDed() {
                return impOperConDrchoDed;
            }

            /**
             * Define el valor de la propiedad impOperConDrchoDed.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setImpOperConDrchoDed(BigDecimal value) {
                this.impOperConDrchoDed = value;
            }

            /**
             * Obtiene el valor de la propiedad tipo.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getTipo() {
                return tipo;
            }

            /**
             * Define el valor de la propiedad tipo.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setTipo(String value) {
                this.tipo = value;
            }

            /**
             * Obtiene el valor de la propiedad porc.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getPorc() {
                return porc;
            }

            /**
             * Define el valor de la propiedad porc.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setPorc(BigDecimal value) {
                this.porc = value;
            }

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
     *         &lt;element name="BaseImponibleyCuota" minOccurs="0"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;sequence&gt;
     *                   &lt;element name="RegOrdinario" minOccurs="0"&gt;
     *                     &lt;complexType&gt;
     *                       &lt;complexContent&gt;
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                           &lt;sequence&gt;
     *                             &lt;element name="Tipo0" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo2" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo4" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo5" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo75" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo10" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo21" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                           &lt;/sequence&gt;
     *                         &lt;/restriction&gt;
     *                       &lt;/complexContent&gt;
     *                     &lt;/complexType&gt;
     *                   &lt;/element&gt;
     *                   &lt;element name="OpIntragrupo" minOccurs="0"&gt;
     *                     &lt;complexType&gt;
     *                       &lt;complexContent&gt;
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                           &lt;sequence&gt;
     *                             &lt;element name="Tipo0" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo2" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo4" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo5" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo75" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo10" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo21" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                           &lt;/sequence&gt;
     *                         &lt;/restriction&gt;
     *                       &lt;/complexContent&gt;
     *                     &lt;/complexType&gt;
     *                   &lt;/element&gt;
     *                   &lt;element name="RegCriterioCaja" minOccurs="0"&gt;
     *                     &lt;complexType&gt;
     *                       &lt;complexContent&gt;
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                           &lt;sequence&gt;
     *                             &lt;element name="Tipo0" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo2" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo4" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo5" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo75" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo10" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo21" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                           &lt;/sequence&gt;
     *                         &lt;/restriction&gt;
     *                       &lt;/complexContent&gt;
     *                     &lt;/complexType&gt;
     *                   &lt;/element&gt;
     *                   &lt;element name="RegBienesUsados" minOccurs="0"&gt;
     *                     &lt;complexType&gt;
     *                       &lt;complexContent&gt;
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                           &lt;sequence&gt;
     *                             &lt;element name="Tipo0" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo2" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo4" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo5" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo75" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo10" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo21" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                           &lt;/sequence&gt;
     *                         &lt;/restriction&gt;
     *                       &lt;/complexContent&gt;
     *                     &lt;/complexType&gt;
     *                   &lt;/element&gt;
     *                   &lt;element name="RegAgViajes" minOccurs="0"&gt;
     *                     &lt;complexType&gt;
     *                       &lt;complexContent&gt;
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                           &lt;sequence&gt;
     *                             &lt;element name="Tipo21" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                           &lt;/sequence&gt;
     *                         &lt;/restriction&gt;
     *                       &lt;/complexContent&gt;
     *                     &lt;/complexType&gt;
     *                   &lt;/element&gt;
     *                   &lt;element name="AdqIntracomBienes" minOccurs="0"&gt;
     *                     &lt;complexType&gt;
     *                       &lt;complexContent&gt;
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                           &lt;sequence&gt;
     *                             &lt;element name="Tipo0" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo2" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo4" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo5" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo75" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo10" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo21" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                           &lt;/sequence&gt;
     *                         &lt;/restriction&gt;
     *                       &lt;/complexContent&gt;
     *                     &lt;/complexType&gt;
     *                   &lt;/element&gt;
     *                   &lt;element name="AdqIntracomServicios" minOccurs="0"&gt;
     *                     &lt;complexType&gt;
     *                       &lt;complexContent&gt;
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                           &lt;sequence&gt;
     *                             &lt;element name="Tipo0" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo2" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo4" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo5" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo75" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo10" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo21" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                           &lt;/sequence&gt;
     *                         &lt;/restriction&gt;
     *                       &lt;/complexContent&gt;
     *                     &lt;/complexType&gt;
     *                   &lt;/element&gt;
     *                   &lt;element name="IVAdevengadoInversionSP" minOccurs="0"&gt;
     *                     &lt;complexType&gt;
     *                       &lt;complexContent&gt;
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                           &lt;sequence&gt;
     *                             &lt;element name="TipoX" type="{}tipo_BaseImponible_y_Cuota"/&gt;
     *                           &lt;/sequence&gt;
     *                         &lt;/restriction&gt;
     *                       &lt;/complexContent&gt;
     *                     &lt;/complexType&gt;
     *                   &lt;/element&gt;
     *                   &lt;element name="ModBasesyCuotas" minOccurs="0"&gt;
     *                     &lt;complexType&gt;
     *                       &lt;complexContent&gt;
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                           &lt;sequence&gt;
     *                             &lt;element name="TipoX" type="{}tipo_BaseImponible_y_Cuota"/&gt;
     *                           &lt;/sequence&gt;
     *                         &lt;/restriction&gt;
     *                       &lt;/complexContent&gt;
     *                     &lt;/complexType&gt;
     *                   &lt;/element&gt;
     *                   &lt;element name="ModBasesyCuotasOpIntragrupo" minOccurs="0"&gt;
     *                     &lt;complexType&gt;
     *                       &lt;complexContent&gt;
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                           &lt;sequence&gt;
     *                             &lt;element name="TipoX" type="{}tipo_BaseImponible_y_Cuota"/&gt;
     *                           &lt;/sequence&gt;
     *                         &lt;/restriction&gt;
     *                       &lt;/complexContent&gt;
     *                     &lt;/complexType&gt;
     *                   &lt;/element&gt;
     *                   &lt;element name="ModBasesyCuotasConcursoAcreedores" minOccurs="0"&gt;
     *                     &lt;complexType&gt;
     *                       &lt;complexContent&gt;
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                           &lt;sequence&gt;
     *                             &lt;element name="TipoX" type="{}tipo_BaseImponible_y_Cuota"/&gt;
     *                           &lt;/sequence&gt;
     *                         &lt;/restriction&gt;
     *                       &lt;/complexContent&gt;
     *                     &lt;/complexType&gt;
     *                   &lt;/element&gt;
     *                   &lt;element name="TotalBasesyCuotasIVA" minOccurs="0"&gt;
     *                     &lt;complexType&gt;
     *                       &lt;complexContent&gt;
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                           &lt;sequence&gt;
     *                             &lt;element name="TipoX" type="{}tipo_BaseImponible_y_Cuota"/&gt;
     *                           &lt;/sequence&gt;
     *                         &lt;/restriction&gt;
     *                       &lt;/complexContent&gt;
     *                     &lt;/complexType&gt;
     *                   &lt;/element&gt;
     *                   &lt;element name="RecargoEquivalencia" minOccurs="0"&gt;
     *                     &lt;complexType&gt;
     *                       &lt;complexContent&gt;
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                           &lt;sequence&gt;
     *                             &lt;element name="Tipo0" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo026" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo05" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo062" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo1" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo14" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo52" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo175" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                           &lt;/sequence&gt;
     *                         &lt;/restriction&gt;
     *                       &lt;/complexContent&gt;
     *                     &lt;/complexType&gt;
     *                   &lt;/element&gt;
     *                   &lt;element name="ModRecargoEquivalencia" minOccurs="0"&gt;
     *                     &lt;complexType&gt;
     *                       &lt;complexContent&gt;
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                           &lt;sequence&gt;
     *                             &lt;element name="TipoX" type="{}tipo_BaseImponible_y_Cuota"/&gt;
     *                           &lt;/sequence&gt;
     *                         &lt;/restriction&gt;
     *                       &lt;/complexContent&gt;
     *                     &lt;/complexType&gt;
     *                   &lt;/element&gt;
     *                   &lt;element name="ModRecargoEquivalenciaConcursoAcreedores" minOccurs="0"&gt;
     *                     &lt;complexType&gt;
     *                       &lt;complexContent&gt;
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                           &lt;sequence&gt;
     *                             &lt;element name="TipoX" type="{}tipo_BaseImponible_y_Cuota"/&gt;
     *                           &lt;/sequence&gt;
     *                         &lt;/restriction&gt;
     *                       &lt;/complexContent&gt;
     *                     &lt;/complexType&gt;
     *                   &lt;/element&gt;
     *                   &lt;element name="TotalCuotasIVA" type="{}tipo_ImpNegativo" minOccurs="0"/&gt;
     *                 &lt;/sequence&gt;
     *               &lt;/restriction&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *         &lt;element name="Deducciones" minOccurs="0"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;sequence&gt;
     *                   &lt;element name="OpInterioresBienesServiciosCorrientes" minOccurs="0"&gt;
     *                     &lt;complexType&gt;
     *                       &lt;complexContent&gt;
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                           &lt;sequence&gt;
     *                             &lt;element name="Tipo2" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo4" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo5" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo75" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo10" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo21" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Total" type="{}tipo_BaseImponible_y_Cuota"/&gt;
     *                           &lt;/sequence&gt;
     *                         &lt;/restriction&gt;
     *                       &lt;/complexContent&gt;
     *                     &lt;/complexType&gt;
     *                   &lt;/element&gt;
     *                   &lt;element name="OpIntragrupoCorrientes" minOccurs="0"&gt;
     *                     &lt;complexType&gt;
     *                       &lt;complexContent&gt;
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                           &lt;sequence&gt;
     *                             &lt;element name="Tipo2" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo4" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo5" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo75" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo10" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo21" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Total" type="{}tipo_BaseImponible_y_Cuota"/&gt;
     *                           &lt;/sequence&gt;
     *                         &lt;/restriction&gt;
     *                       &lt;/complexContent&gt;
     *                     &lt;/complexType&gt;
     *                   &lt;/element&gt;
     *                   &lt;element name="OpInterioresBienesInversion" minOccurs="0"&gt;
     *                     &lt;complexType&gt;
     *                       &lt;complexContent&gt;
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                           &lt;sequence&gt;
     *                             &lt;element name="Tipo2" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo4" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo5" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo75" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo10" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo21" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Total" type="{}tipo_BaseImponible_y_Cuota"/&gt;
     *                           &lt;/sequence&gt;
     *                         &lt;/restriction&gt;
     *                       &lt;/complexContent&gt;
     *                     &lt;/complexType&gt;
     *                   &lt;/element&gt;
     *                   &lt;element name="OpIntragrupoBienesInversion" minOccurs="0"&gt;
     *                     &lt;complexType&gt;
     *                       &lt;complexContent&gt;
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                           &lt;sequence&gt;
     *                             &lt;element name="Tipo2" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo4" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo5" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo75" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo10" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo21" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Total" type="{}tipo_BaseImponible_y_Cuota"/&gt;
     *                           &lt;/sequence&gt;
     *                         &lt;/restriction&gt;
     *                       &lt;/complexContent&gt;
     *                     &lt;/complexType&gt;
     *                   &lt;/element&gt;
     *                   &lt;element name="ImportacionesBienesCorrientes" minOccurs="0"&gt;
     *                     &lt;complexType&gt;
     *                       &lt;complexContent&gt;
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                           &lt;sequence&gt;
     *                             &lt;element name="Tipo2" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo4" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo5" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo75" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo10" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo21" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Total" type="{}tipo_BaseImponible_y_Cuota"/&gt;
     *                           &lt;/sequence&gt;
     *                         &lt;/restriction&gt;
     *                       &lt;/complexContent&gt;
     *                     &lt;/complexType&gt;
     *                   &lt;/element&gt;
     *                   &lt;element name="ImportacionesBienesInversion" minOccurs="0"&gt;
     *                     &lt;complexType&gt;
     *                       &lt;complexContent&gt;
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                           &lt;sequence&gt;
     *                             &lt;element name="Tipo2" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo4" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo5" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo75" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo10" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo21" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Total" type="{}tipo_BaseImponible_y_Cuota"/&gt;
     *                           &lt;/sequence&gt;
     *                         &lt;/restriction&gt;
     *                       &lt;/complexContent&gt;
     *                     &lt;/complexType&gt;
     *                   &lt;/element&gt;
     *                   &lt;element name="AdqIntracomunitariasBienesCorrientes" minOccurs="0"&gt;
     *                     &lt;complexType&gt;
     *                       &lt;complexContent&gt;
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                           &lt;sequence&gt;
     *                             &lt;element name="Tipo2" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo4" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo5" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo75" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo10" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo21" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Total" type="{}tipo_BaseImponible_y_Cuota"/&gt;
     *                           &lt;/sequence&gt;
     *                         &lt;/restriction&gt;
     *                       &lt;/complexContent&gt;
     *                     &lt;/complexType&gt;
     *                   &lt;/element&gt;
     *                   &lt;element name="AdqIntracomunitariasBienesInversion" minOccurs="0"&gt;
     *                     &lt;complexType&gt;
     *                       &lt;complexContent&gt;
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                           &lt;sequence&gt;
     *                             &lt;element name="Tipo2" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo4" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo5" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo75" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo10" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo21" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Total" type="{}tipo_BaseImponible_y_Cuota"/&gt;
     *                           &lt;/sequence&gt;
     *                         &lt;/restriction&gt;
     *                       &lt;/complexContent&gt;
     *                     &lt;/complexType&gt;
     *                   &lt;/element&gt;
     *                   &lt;element name="AdqIntracomunitariasServicios" minOccurs="0"&gt;
     *                     &lt;complexType&gt;
     *                       &lt;complexContent&gt;
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                           &lt;sequence&gt;
     *                             &lt;element name="Tipo2" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo4" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo5" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo75" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo10" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Tipo21" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
     *                             &lt;element name="Total" type="{}tipo_BaseImponible_y_Cuota"/&gt;
     *                           &lt;/sequence&gt;
     *                         &lt;/restriction&gt;
     *                       &lt;/complexContent&gt;
     *                     &lt;/complexType&gt;
     *                   &lt;/element&gt;
     *                   &lt;element name="ComRegAgricGanadPesca" minOccurs="0"&gt;
     *                     &lt;complexType&gt;
     *                       &lt;complexContent&gt;
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                           &lt;sequence&gt;
     *                             &lt;element name="TipoX" type="{}tipo_BaseImponible_y_Cuota"/&gt;
     *                           &lt;/sequence&gt;
     *                         &lt;/restriction&gt;
     *                       &lt;/complexContent&gt;
     *                     &lt;/complexType&gt;
     *                   &lt;/element&gt;
     *                   &lt;element name="CouDedResAdministrativas" minOccurs="0"&gt;
     *                     &lt;complexType&gt;
     *                       &lt;complexContent&gt;
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                           &lt;sequence&gt;
     *                             &lt;element name="TipoX" type="{}tipo_BaseImponible_y_Cuota"/&gt;
     *                           &lt;/sequence&gt;
     *                         &lt;/restriction&gt;
     *                       &lt;/complexContent&gt;
     *                     &lt;/complexType&gt;
     *                   &lt;/element&gt;
     *                   &lt;element name="RectifDeducciones" minOccurs="0"&gt;
     *                     &lt;complexType&gt;
     *                       &lt;complexContent&gt;
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                           &lt;sequence&gt;
     *                             &lt;element name="TipoX" type="{}tipo_BaseImponible_y_Cuota"/&gt;
     *                           &lt;/sequence&gt;
     *                         &lt;/restriction&gt;
     *                       &lt;/complexContent&gt;
     *                     &lt;/complexType&gt;
     *                   &lt;/element&gt;
     *                   &lt;element name="RectifOpIntragrupo" minOccurs="0"&gt;
     *                     &lt;complexType&gt;
     *                       &lt;complexContent&gt;
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                           &lt;sequence&gt;
     *                             &lt;element name="TipoX" type="{}tipo_BaseImponible_y_Cuota"/&gt;
     *                           &lt;/sequence&gt;
     *                         &lt;/restriction&gt;
     *                       &lt;/complexContent&gt;
     *                     &lt;/complexType&gt;
     *                   &lt;/element&gt;
     *                   &lt;element name="RegularizInversiones" type="{}tipo_ImpNegativo" minOccurs="0"/&gt;
     *                   &lt;element name="RegularizPorcProrrata" type="{}tipo_ImpNegativo" minOccurs="0"/&gt;
     *                   &lt;element name="SumDeducciones" type="{}tipo_ImpNegativo" minOccurs="0"/&gt;
     *                 &lt;/sequence&gt;
     *               &lt;/restriction&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *         &lt;element name="ResRegGeneral" type="{}tipo_ImpNegativoMasEspacio" minOccurs="0"/&gt;
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
        "baseImponibleyCuota",
        "deducciones",
        "resRegGeneral"
    })
    public static class RegGeneral {

        @XmlElement(name = "BaseImponibleyCuota")
        protected AEATIVA2024 .RegGeneral.BaseImponibleyCuota baseImponibleyCuota;
        @XmlElement(name = "Deducciones")
        protected AEATIVA2024 .RegGeneral.Deducciones deducciones;
        @XmlElement(name = "ResRegGeneral")
        protected String resRegGeneral;

        /**
         * Obtiene el valor de la propiedad baseImponibleyCuota.
         * 
         * @return
         *     possible object is
         *     {@link AEATIVA2024 .RegGeneral.BaseImponibleyCuota }
         *     
         */
        public AEATIVA2024 .RegGeneral.BaseImponibleyCuota getBaseImponibleyCuota() {
            return baseImponibleyCuota;
        }

        /**
         * Define el valor de la propiedad baseImponibleyCuota.
         * 
         * @param value
         *     allowed object is
         *     {@link AEATIVA2024 .RegGeneral.BaseImponibleyCuota }
         *     
         */
        public void setBaseImponibleyCuota(AEATIVA2024 .RegGeneral.BaseImponibleyCuota value) {
            this.baseImponibleyCuota = value;
        }

        /**
         * Obtiene el valor de la propiedad deducciones.
         * 
         * @return
         *     possible object is
         *     {@link AEATIVA2024 .RegGeneral.Deducciones }
         *     
         */
        public AEATIVA2024 .RegGeneral.Deducciones getDeducciones() {
            return deducciones;
        }

        /**
         * Define el valor de la propiedad deducciones.
         * 
         * @param value
         *     allowed object is
         *     {@link AEATIVA2024 .RegGeneral.Deducciones }
         *     
         */
        public void setDeducciones(AEATIVA2024 .RegGeneral.Deducciones value) {
            this.deducciones = value;
        }

        /**
         * Obtiene el valor de la propiedad resRegGeneral.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getResRegGeneral() {
            return resRegGeneral;
        }

        /**
         * Define el valor de la propiedad resRegGeneral.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setResRegGeneral(String value) {
            this.resRegGeneral = value;
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
         *         &lt;element name="RegOrdinario" minOccurs="0"&gt;
         *           &lt;complexType&gt;
         *             &lt;complexContent&gt;
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *                 &lt;sequence&gt;
         *                   &lt;element name="Tipo0" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo2" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo4" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo5" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo75" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo10" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo21" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                 &lt;/sequence&gt;
         *               &lt;/restriction&gt;
         *             &lt;/complexContent&gt;
         *           &lt;/complexType&gt;
         *         &lt;/element&gt;
         *         &lt;element name="OpIntragrupo" minOccurs="0"&gt;
         *           &lt;complexType&gt;
         *             &lt;complexContent&gt;
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *                 &lt;sequence&gt;
         *                   &lt;element name="Tipo0" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo2" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo4" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo5" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo75" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo10" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo21" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                 &lt;/sequence&gt;
         *               &lt;/restriction&gt;
         *             &lt;/complexContent&gt;
         *           &lt;/complexType&gt;
         *         &lt;/element&gt;
         *         &lt;element name="RegCriterioCaja" minOccurs="0"&gt;
         *           &lt;complexType&gt;
         *             &lt;complexContent&gt;
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *                 &lt;sequence&gt;
         *                   &lt;element name="Tipo0" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo2" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo4" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo5" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo75" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo10" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo21" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                 &lt;/sequence&gt;
         *               &lt;/restriction&gt;
         *             &lt;/complexContent&gt;
         *           &lt;/complexType&gt;
         *         &lt;/element&gt;
         *         &lt;element name="RegBienesUsados" minOccurs="0"&gt;
         *           &lt;complexType&gt;
         *             &lt;complexContent&gt;
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *                 &lt;sequence&gt;
         *                   &lt;element name="Tipo0" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo2" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo4" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo5" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo75" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo10" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo21" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                 &lt;/sequence&gt;
         *               &lt;/restriction&gt;
         *             &lt;/complexContent&gt;
         *           &lt;/complexType&gt;
         *         &lt;/element&gt;
         *         &lt;element name="RegAgViajes" minOccurs="0"&gt;
         *           &lt;complexType&gt;
         *             &lt;complexContent&gt;
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *                 &lt;sequence&gt;
         *                   &lt;element name="Tipo21" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                 &lt;/sequence&gt;
         *               &lt;/restriction&gt;
         *             &lt;/complexContent&gt;
         *           &lt;/complexType&gt;
         *         &lt;/element&gt;
         *         &lt;element name="AdqIntracomBienes" minOccurs="0"&gt;
         *           &lt;complexType&gt;
         *             &lt;complexContent&gt;
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *                 &lt;sequence&gt;
         *                   &lt;element name="Tipo0" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo2" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo4" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo5" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo75" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo10" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo21" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                 &lt;/sequence&gt;
         *               &lt;/restriction&gt;
         *             &lt;/complexContent&gt;
         *           &lt;/complexType&gt;
         *         &lt;/element&gt;
         *         &lt;element name="AdqIntracomServicios" minOccurs="0"&gt;
         *           &lt;complexType&gt;
         *             &lt;complexContent&gt;
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *                 &lt;sequence&gt;
         *                   &lt;element name="Tipo0" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo2" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo4" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo5" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo75" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo10" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo21" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                 &lt;/sequence&gt;
         *               &lt;/restriction&gt;
         *             &lt;/complexContent&gt;
         *           &lt;/complexType&gt;
         *         &lt;/element&gt;
         *         &lt;element name="IVAdevengadoInversionSP" minOccurs="0"&gt;
         *           &lt;complexType&gt;
         *             &lt;complexContent&gt;
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *                 &lt;sequence&gt;
         *                   &lt;element name="TipoX" type="{}tipo_BaseImponible_y_Cuota"/&gt;
         *                 &lt;/sequence&gt;
         *               &lt;/restriction&gt;
         *             &lt;/complexContent&gt;
         *           &lt;/complexType&gt;
         *         &lt;/element&gt;
         *         &lt;element name="ModBasesyCuotas" minOccurs="0"&gt;
         *           &lt;complexType&gt;
         *             &lt;complexContent&gt;
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *                 &lt;sequence&gt;
         *                   &lt;element name="TipoX" type="{}tipo_BaseImponible_y_Cuota"/&gt;
         *                 &lt;/sequence&gt;
         *               &lt;/restriction&gt;
         *             &lt;/complexContent&gt;
         *           &lt;/complexType&gt;
         *         &lt;/element&gt;
         *         &lt;element name="ModBasesyCuotasOpIntragrupo" minOccurs="0"&gt;
         *           &lt;complexType&gt;
         *             &lt;complexContent&gt;
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *                 &lt;sequence&gt;
         *                   &lt;element name="TipoX" type="{}tipo_BaseImponible_y_Cuota"/&gt;
         *                 &lt;/sequence&gt;
         *               &lt;/restriction&gt;
         *             &lt;/complexContent&gt;
         *           &lt;/complexType&gt;
         *         &lt;/element&gt;
         *         &lt;element name="ModBasesyCuotasConcursoAcreedores" minOccurs="0"&gt;
         *           &lt;complexType&gt;
         *             &lt;complexContent&gt;
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *                 &lt;sequence&gt;
         *                   &lt;element name="TipoX" type="{}tipo_BaseImponible_y_Cuota"/&gt;
         *                 &lt;/sequence&gt;
         *               &lt;/restriction&gt;
         *             &lt;/complexContent&gt;
         *           &lt;/complexType&gt;
         *         &lt;/element&gt;
         *         &lt;element name="TotalBasesyCuotasIVA" minOccurs="0"&gt;
         *           &lt;complexType&gt;
         *             &lt;complexContent&gt;
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *                 &lt;sequence&gt;
         *                   &lt;element name="TipoX" type="{}tipo_BaseImponible_y_Cuota"/&gt;
         *                 &lt;/sequence&gt;
         *               &lt;/restriction&gt;
         *             &lt;/complexContent&gt;
         *           &lt;/complexType&gt;
         *         &lt;/element&gt;
         *         &lt;element name="RecargoEquivalencia" minOccurs="0"&gt;
         *           &lt;complexType&gt;
         *             &lt;complexContent&gt;
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *                 &lt;sequence&gt;
         *                   &lt;element name="Tipo0" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo026" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo05" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo062" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo1" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo14" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo52" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo175" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                 &lt;/sequence&gt;
         *               &lt;/restriction&gt;
         *             &lt;/complexContent&gt;
         *           &lt;/complexType&gt;
         *         &lt;/element&gt;
         *         &lt;element name="ModRecargoEquivalencia" minOccurs="0"&gt;
         *           &lt;complexType&gt;
         *             &lt;complexContent&gt;
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *                 &lt;sequence&gt;
         *                   &lt;element name="TipoX" type="{}tipo_BaseImponible_y_Cuota"/&gt;
         *                 &lt;/sequence&gt;
         *               &lt;/restriction&gt;
         *             &lt;/complexContent&gt;
         *           &lt;/complexType&gt;
         *         &lt;/element&gt;
         *         &lt;element name="ModRecargoEquivalenciaConcursoAcreedores" minOccurs="0"&gt;
         *           &lt;complexType&gt;
         *             &lt;complexContent&gt;
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *                 &lt;sequence&gt;
         *                   &lt;element name="TipoX" type="{}tipo_BaseImponible_y_Cuota"/&gt;
         *                 &lt;/sequence&gt;
         *               &lt;/restriction&gt;
         *             &lt;/complexContent&gt;
         *           &lt;/complexType&gt;
         *         &lt;/element&gt;
         *         &lt;element name="TotalCuotasIVA" type="{}tipo_ImpNegativo" minOccurs="0"/&gt;
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
            "regOrdinario",
            "opIntragrupo",
            "regCriterioCaja",
            "regBienesUsados",
            "regAgViajes",
            "adqIntracomBienes",
            "adqIntracomServicios",
            "ivAdevengadoInversionSP",
            "modBasesyCuotas",
            "modBasesyCuotasOpIntragrupo",
            "modBasesyCuotasConcursoAcreedores",
            "totalBasesyCuotasIVA",
            "recargoEquivalencia",
            "modRecargoEquivalencia",
            "modRecargoEquivalenciaConcursoAcreedores",
            "totalCuotasIVA"
        })
        public static class BaseImponibleyCuota {

            @XmlElement(name = "RegOrdinario")
            protected AEATIVA2024 .RegGeneral.BaseImponibleyCuota.RegOrdinario regOrdinario;
            @XmlElement(name = "OpIntragrupo")
            protected AEATIVA2024 .RegGeneral.BaseImponibleyCuota.OpIntragrupo opIntragrupo;
            @XmlElement(name = "RegCriterioCaja")
            protected AEATIVA2024 .RegGeneral.BaseImponibleyCuota.RegCriterioCaja regCriterioCaja;
            @XmlElement(name = "RegBienesUsados")
            protected AEATIVA2024 .RegGeneral.BaseImponibleyCuota.RegBienesUsados regBienesUsados;
            @XmlElement(name = "RegAgViajes")
            protected AEATIVA2024 .RegGeneral.BaseImponibleyCuota.RegAgViajes regAgViajes;
            @XmlElement(name = "AdqIntracomBienes")
            protected AEATIVA2024 .RegGeneral.BaseImponibleyCuota.AdqIntracomBienes adqIntracomBienes;
            @XmlElement(name = "AdqIntracomServicios")
            protected AEATIVA2024 .RegGeneral.BaseImponibleyCuota.AdqIntracomServicios adqIntracomServicios;
            @XmlElement(name = "IVAdevengadoInversionSP")
            protected AEATIVA2024 .RegGeneral.BaseImponibleyCuota.IVAdevengadoInversionSP ivAdevengadoInversionSP;
            @XmlElement(name = "ModBasesyCuotas")
            protected AEATIVA2024 .RegGeneral.BaseImponibleyCuota.ModBasesyCuotas modBasesyCuotas;
            @XmlElement(name = "ModBasesyCuotasOpIntragrupo")
            protected AEATIVA2024 .RegGeneral.BaseImponibleyCuota.ModBasesyCuotasOpIntragrupo modBasesyCuotasOpIntragrupo;
            @XmlElement(name = "ModBasesyCuotasConcursoAcreedores")
            protected AEATIVA2024 .RegGeneral.BaseImponibleyCuota.ModBasesyCuotasConcursoAcreedores modBasesyCuotasConcursoAcreedores;
            @XmlElement(name = "TotalBasesyCuotasIVA")
            protected AEATIVA2024 .RegGeneral.BaseImponibleyCuota.TotalBasesyCuotasIVA totalBasesyCuotasIVA;
            @XmlElement(name = "RecargoEquivalencia")
            protected AEATIVA2024 .RegGeneral.BaseImponibleyCuota.RecargoEquivalencia recargoEquivalencia;
            @XmlElement(name = "ModRecargoEquivalencia")
            protected AEATIVA2024 .RegGeneral.BaseImponibleyCuota.ModRecargoEquivalencia modRecargoEquivalencia;
            @XmlElement(name = "ModRecargoEquivalenciaConcursoAcreedores")
            protected AEATIVA2024 .RegGeneral.BaseImponibleyCuota.ModRecargoEquivalenciaConcursoAcreedores modRecargoEquivalenciaConcursoAcreedores;
            @XmlElement(name = "TotalCuotasIVA")
            protected BigDecimal totalCuotasIVA;

            /**
             * Obtiene el valor de la propiedad regOrdinario.
             * 
             * @return
             *     possible object is
             *     {@link AEATIVA2024 .RegGeneral.BaseImponibleyCuota.RegOrdinario }
             *     
             */
            public AEATIVA2024 .RegGeneral.BaseImponibleyCuota.RegOrdinario getRegOrdinario() {
                return regOrdinario;
            }

            /**
             * Define el valor de la propiedad regOrdinario.
             * 
             * @param value
             *     allowed object is
             *     {@link AEATIVA2024 .RegGeneral.BaseImponibleyCuota.RegOrdinario }
             *     
             */
            public void setRegOrdinario(AEATIVA2024 .RegGeneral.BaseImponibleyCuota.RegOrdinario value) {
                this.regOrdinario = value;
            }

            /**
             * Obtiene el valor de la propiedad opIntragrupo.
             * 
             * @return
             *     possible object is
             *     {@link AEATIVA2024 .RegGeneral.BaseImponibleyCuota.OpIntragrupo }
             *     
             */
            public AEATIVA2024 .RegGeneral.BaseImponibleyCuota.OpIntragrupo getOpIntragrupo() {
                return opIntragrupo;
            }

            /**
             * Define el valor de la propiedad opIntragrupo.
             * 
             * @param value
             *     allowed object is
             *     {@link AEATIVA2024 .RegGeneral.BaseImponibleyCuota.OpIntragrupo }
             *     
             */
            public void setOpIntragrupo(AEATIVA2024 .RegGeneral.BaseImponibleyCuota.OpIntragrupo value) {
                this.opIntragrupo = value;
            }

            /**
             * Obtiene el valor de la propiedad regCriterioCaja.
             * 
             * @return
             *     possible object is
             *     {@link AEATIVA2024 .RegGeneral.BaseImponibleyCuota.RegCriterioCaja }
             *     
             */
            public AEATIVA2024 .RegGeneral.BaseImponibleyCuota.RegCriterioCaja getRegCriterioCaja() {
                return regCriterioCaja;
            }

            /**
             * Define el valor de la propiedad regCriterioCaja.
             * 
             * @param value
             *     allowed object is
             *     {@link AEATIVA2024 .RegGeneral.BaseImponibleyCuota.RegCriterioCaja }
             *     
             */
            public void setRegCriterioCaja(AEATIVA2024 .RegGeneral.BaseImponibleyCuota.RegCriterioCaja value) {
                this.regCriterioCaja = value;
            }

            /**
             * Obtiene el valor de la propiedad regBienesUsados.
             * 
             * @return
             *     possible object is
             *     {@link AEATIVA2024 .RegGeneral.BaseImponibleyCuota.RegBienesUsados }
             *     
             */
            public AEATIVA2024 .RegGeneral.BaseImponibleyCuota.RegBienesUsados getRegBienesUsados() {
                return regBienesUsados;
            }

            /**
             * Define el valor de la propiedad regBienesUsados.
             * 
             * @param value
             *     allowed object is
             *     {@link AEATIVA2024 .RegGeneral.BaseImponibleyCuota.RegBienesUsados }
             *     
             */
            public void setRegBienesUsados(AEATIVA2024 .RegGeneral.BaseImponibleyCuota.RegBienesUsados value) {
                this.regBienesUsados = value;
            }

            /**
             * Obtiene el valor de la propiedad regAgViajes.
             * 
             * @return
             *     possible object is
             *     {@link AEATIVA2024 .RegGeneral.BaseImponibleyCuota.RegAgViajes }
             *     
             */
            public AEATIVA2024 .RegGeneral.BaseImponibleyCuota.RegAgViajes getRegAgViajes() {
                return regAgViajes;
            }

            /**
             * Define el valor de la propiedad regAgViajes.
             * 
             * @param value
             *     allowed object is
             *     {@link AEATIVA2024 .RegGeneral.BaseImponibleyCuota.RegAgViajes }
             *     
             */
            public void setRegAgViajes(AEATIVA2024 .RegGeneral.BaseImponibleyCuota.RegAgViajes value) {
                this.regAgViajes = value;
            }

            /**
             * Obtiene el valor de la propiedad adqIntracomBienes.
             * 
             * @return
             *     possible object is
             *     {@link AEATIVA2024 .RegGeneral.BaseImponibleyCuota.AdqIntracomBienes }
             *     
             */
            public AEATIVA2024 .RegGeneral.BaseImponibleyCuota.AdqIntracomBienes getAdqIntracomBienes() {
                return adqIntracomBienes;
            }

            /**
             * Define el valor de la propiedad adqIntracomBienes.
             * 
             * @param value
             *     allowed object is
             *     {@link AEATIVA2024 .RegGeneral.BaseImponibleyCuota.AdqIntracomBienes }
             *     
             */
            public void setAdqIntracomBienes(AEATIVA2024 .RegGeneral.BaseImponibleyCuota.AdqIntracomBienes value) {
                this.adqIntracomBienes = value;
            }

            /**
             * Obtiene el valor de la propiedad adqIntracomServicios.
             * 
             * @return
             *     possible object is
             *     {@link AEATIVA2024 .RegGeneral.BaseImponibleyCuota.AdqIntracomServicios }
             *     
             */
            public AEATIVA2024 .RegGeneral.BaseImponibleyCuota.AdqIntracomServicios getAdqIntracomServicios() {
                return adqIntracomServicios;
            }

            /**
             * Define el valor de la propiedad adqIntracomServicios.
             * 
             * @param value
             *     allowed object is
             *     {@link AEATIVA2024 .RegGeneral.BaseImponibleyCuota.AdqIntracomServicios }
             *     
             */
            public void setAdqIntracomServicios(AEATIVA2024 .RegGeneral.BaseImponibleyCuota.AdqIntracomServicios value) {
                this.adqIntracomServicios = value;
            }

            /**
             * Obtiene el valor de la propiedad ivAdevengadoInversionSP.
             * 
             * @return
             *     possible object is
             *     {@link AEATIVA2024 .RegGeneral.BaseImponibleyCuota.IVAdevengadoInversionSP }
             *     
             */
            public AEATIVA2024 .RegGeneral.BaseImponibleyCuota.IVAdevengadoInversionSP getIVAdevengadoInversionSP() {
                return ivAdevengadoInversionSP;
            }

            /**
             * Define el valor de la propiedad ivAdevengadoInversionSP.
             * 
             * @param value
             *     allowed object is
             *     {@link AEATIVA2024 .RegGeneral.BaseImponibleyCuota.IVAdevengadoInversionSP }
             *     
             */
            public void setIVAdevengadoInversionSP(AEATIVA2024 .RegGeneral.BaseImponibleyCuota.IVAdevengadoInversionSP value) {
                this.ivAdevengadoInversionSP = value;
            }

            /**
             * Obtiene el valor de la propiedad modBasesyCuotas.
             * 
             * @return
             *     possible object is
             *     {@link AEATIVA2024 .RegGeneral.BaseImponibleyCuota.ModBasesyCuotas }
             *     
             */
            public AEATIVA2024 .RegGeneral.BaseImponibleyCuota.ModBasesyCuotas getModBasesyCuotas() {
                return modBasesyCuotas;
            }

            /**
             * Define el valor de la propiedad modBasesyCuotas.
             * 
             * @param value
             *     allowed object is
             *     {@link AEATIVA2024 .RegGeneral.BaseImponibleyCuota.ModBasesyCuotas }
             *     
             */
            public void setModBasesyCuotas(AEATIVA2024 .RegGeneral.BaseImponibleyCuota.ModBasesyCuotas value) {
                this.modBasesyCuotas = value;
            }

            /**
             * Obtiene el valor de la propiedad modBasesyCuotasOpIntragrupo.
             * 
             * @return
             *     possible object is
             *     {@link AEATIVA2024 .RegGeneral.BaseImponibleyCuota.ModBasesyCuotasOpIntragrupo }
             *     
             */
            public AEATIVA2024 .RegGeneral.BaseImponibleyCuota.ModBasesyCuotasOpIntragrupo getModBasesyCuotasOpIntragrupo() {
                return modBasesyCuotasOpIntragrupo;
            }

            /**
             * Define el valor de la propiedad modBasesyCuotasOpIntragrupo.
             * 
             * @param value
             *     allowed object is
             *     {@link AEATIVA2024 .RegGeneral.BaseImponibleyCuota.ModBasesyCuotasOpIntragrupo }
             *     
             */
            public void setModBasesyCuotasOpIntragrupo(AEATIVA2024 .RegGeneral.BaseImponibleyCuota.ModBasesyCuotasOpIntragrupo value) {
                this.modBasesyCuotasOpIntragrupo = value;
            }

            /**
             * Obtiene el valor de la propiedad modBasesyCuotasConcursoAcreedores.
             * 
             * @return
             *     possible object is
             *     {@link AEATIVA2024 .RegGeneral.BaseImponibleyCuota.ModBasesyCuotasConcursoAcreedores }
             *     
             */
            public AEATIVA2024 .RegGeneral.BaseImponibleyCuota.ModBasesyCuotasConcursoAcreedores getModBasesyCuotasConcursoAcreedores() {
                return modBasesyCuotasConcursoAcreedores;
            }

            /**
             * Define el valor de la propiedad modBasesyCuotasConcursoAcreedores.
             * 
             * @param value
             *     allowed object is
             *     {@link AEATIVA2024 .RegGeneral.BaseImponibleyCuota.ModBasesyCuotasConcursoAcreedores }
             *     
             */
            public void setModBasesyCuotasConcursoAcreedores(AEATIVA2024 .RegGeneral.BaseImponibleyCuota.ModBasesyCuotasConcursoAcreedores value) {
                this.modBasesyCuotasConcursoAcreedores = value;
            }

            /**
             * Obtiene el valor de la propiedad totalBasesyCuotasIVA.
             * 
             * @return
             *     possible object is
             *     {@link AEATIVA2024 .RegGeneral.BaseImponibleyCuota.TotalBasesyCuotasIVA }
             *     
             */
            public AEATIVA2024 .RegGeneral.BaseImponibleyCuota.TotalBasesyCuotasIVA getTotalBasesyCuotasIVA() {
                return totalBasesyCuotasIVA;
            }

            /**
             * Define el valor de la propiedad totalBasesyCuotasIVA.
             * 
             * @param value
             *     allowed object is
             *     {@link AEATIVA2024 .RegGeneral.BaseImponibleyCuota.TotalBasesyCuotasIVA }
             *     
             */
            public void setTotalBasesyCuotasIVA(AEATIVA2024 .RegGeneral.BaseImponibleyCuota.TotalBasesyCuotasIVA value) {
                this.totalBasesyCuotasIVA = value;
            }

            /**
             * Obtiene el valor de la propiedad recargoEquivalencia.
             * 
             * @return
             *     possible object is
             *     {@link AEATIVA2024 .RegGeneral.BaseImponibleyCuota.RecargoEquivalencia }
             *     
             */
            public AEATIVA2024 .RegGeneral.BaseImponibleyCuota.RecargoEquivalencia getRecargoEquivalencia() {
                return recargoEquivalencia;
            }

            /**
             * Define el valor de la propiedad recargoEquivalencia.
             * 
             * @param value
             *     allowed object is
             *     {@link AEATIVA2024 .RegGeneral.BaseImponibleyCuota.RecargoEquivalencia }
             *     
             */
            public void setRecargoEquivalencia(AEATIVA2024 .RegGeneral.BaseImponibleyCuota.RecargoEquivalencia value) {
                this.recargoEquivalencia = value;
            }

            /**
             * Obtiene el valor de la propiedad modRecargoEquivalencia.
             * 
             * @return
             *     possible object is
             *     {@link AEATIVA2024 .RegGeneral.BaseImponibleyCuota.ModRecargoEquivalencia }
             *     
             */
            public AEATIVA2024 .RegGeneral.BaseImponibleyCuota.ModRecargoEquivalencia getModRecargoEquivalencia() {
                return modRecargoEquivalencia;
            }

            /**
             * Define el valor de la propiedad modRecargoEquivalencia.
             * 
             * @param value
             *     allowed object is
             *     {@link AEATIVA2024 .RegGeneral.BaseImponibleyCuota.ModRecargoEquivalencia }
             *     
             */
            public void setModRecargoEquivalencia(AEATIVA2024 .RegGeneral.BaseImponibleyCuota.ModRecargoEquivalencia value) {
                this.modRecargoEquivalencia = value;
            }

            /**
             * Obtiene el valor de la propiedad modRecargoEquivalenciaConcursoAcreedores.
             * 
             * @return
             *     possible object is
             *     {@link AEATIVA2024 .RegGeneral.BaseImponibleyCuota.ModRecargoEquivalenciaConcursoAcreedores }
             *     
             */
            public AEATIVA2024 .RegGeneral.BaseImponibleyCuota.ModRecargoEquivalenciaConcursoAcreedores getModRecargoEquivalenciaConcursoAcreedores() {
                return modRecargoEquivalenciaConcursoAcreedores;
            }

            /**
             * Define el valor de la propiedad modRecargoEquivalenciaConcursoAcreedores.
             * 
             * @param value
             *     allowed object is
             *     {@link AEATIVA2024 .RegGeneral.BaseImponibleyCuota.ModRecargoEquivalenciaConcursoAcreedores }
             *     
             */
            public void setModRecargoEquivalenciaConcursoAcreedores(AEATIVA2024 .RegGeneral.BaseImponibleyCuota.ModRecargoEquivalenciaConcursoAcreedores value) {
                this.modRecargoEquivalenciaConcursoAcreedores = value;
            }

            /**
             * Obtiene el valor de la propiedad totalCuotasIVA.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getTotalCuotasIVA() {
                return totalCuotasIVA;
            }

            /**
             * Define el valor de la propiedad totalCuotasIVA.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setTotalCuotasIVA(BigDecimal value) {
                this.totalCuotasIVA = value;
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
             *         &lt;element name="Tipo0" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo2" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo4" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo5" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo75" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo10" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo21" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
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
                "tipo0",
                "tipo2",
                "tipo4",
                "tipo5",
                "tipo75",
                "tipo10",
                "tipo21"
            })
            public static class AdqIntracomBienes {

                @XmlElement(name = "Tipo0")
                protected TipoBaseImponibleYCuota tipo0;
                @XmlElement(name = "Tipo2")
                protected TipoBaseImponibleYCuota tipo2;
                @XmlElement(name = "Tipo4")
                protected TipoBaseImponibleYCuota tipo4;
                @XmlElement(name = "Tipo5")
                protected TipoBaseImponibleYCuota tipo5;
                @XmlElement(name = "Tipo75")
                protected TipoBaseImponibleYCuota tipo75;
                @XmlElement(name = "Tipo10")
                protected TipoBaseImponibleYCuota tipo10;
                @XmlElement(name = "Tipo21")
                protected TipoBaseImponibleYCuota tipo21;

                /**
                 * Obtiene el valor de la propiedad tipo0.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo0() {
                    return tipo0;
                }

                /**
                 * Define el valor de la propiedad tipo0.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo0(TipoBaseImponibleYCuota value) {
                    this.tipo0 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo2.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo2() {
                    return tipo2;
                }

                /**
                 * Define el valor de la propiedad tipo2.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo2(TipoBaseImponibleYCuota value) {
                    this.tipo2 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo4.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo4() {
                    return tipo4;
                }

                /**
                 * Define el valor de la propiedad tipo4.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo4(TipoBaseImponibleYCuota value) {
                    this.tipo4 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo5.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo5() {
                    return tipo5;
                }

                /**
                 * Define el valor de la propiedad tipo5.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo5(TipoBaseImponibleYCuota value) {
                    this.tipo5 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo75.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo75() {
                    return tipo75;
                }

                /**
                 * Define el valor de la propiedad tipo75.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo75(TipoBaseImponibleYCuota value) {
                    this.tipo75 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo10.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo10() {
                    return tipo10;
                }

                /**
                 * Define el valor de la propiedad tipo10.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo10(TipoBaseImponibleYCuota value) {
                    this.tipo10 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo21.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo21() {
                    return tipo21;
                }

                /**
                 * Define el valor de la propiedad tipo21.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo21(TipoBaseImponibleYCuota value) {
                    this.tipo21 = value;
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
             *         &lt;element name="Tipo0" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo2" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo4" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo5" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo75" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo10" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo21" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
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
                "tipo0",
                "tipo2",
                "tipo4",
                "tipo5",
                "tipo75",
                "tipo10",
                "tipo21"
            })
            public static class AdqIntracomServicios {

                @XmlElement(name = "Tipo0")
                protected TipoBaseImponibleYCuota tipo0;
                @XmlElement(name = "Tipo2")
                protected TipoBaseImponibleYCuota tipo2;
                @XmlElement(name = "Tipo4")
                protected TipoBaseImponibleYCuota tipo4;
                @XmlElement(name = "Tipo5")
                protected TipoBaseImponibleYCuota tipo5;
                @XmlElement(name = "Tipo75")
                protected TipoBaseImponibleYCuota tipo75;
                @XmlElement(name = "Tipo10")
                protected TipoBaseImponibleYCuota tipo10;
                @XmlElement(name = "Tipo21")
                protected TipoBaseImponibleYCuota tipo21;

                /**
                 * Obtiene el valor de la propiedad tipo0.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo0() {
                    return tipo0;
                }

                /**
                 * Define el valor de la propiedad tipo0.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo0(TipoBaseImponibleYCuota value) {
                    this.tipo0 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo2.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo2() {
                    return tipo2;
                }

                /**
                 * Define el valor de la propiedad tipo2.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo2(TipoBaseImponibleYCuota value) {
                    this.tipo2 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo4.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo4() {
                    return tipo4;
                }

                /**
                 * Define el valor de la propiedad tipo4.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo4(TipoBaseImponibleYCuota value) {
                    this.tipo4 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo5.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo5() {
                    return tipo5;
                }

                /**
                 * Define el valor de la propiedad tipo5.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo5(TipoBaseImponibleYCuota value) {
                    this.tipo5 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo75.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo75() {
                    return tipo75;
                }

                /**
                 * Define el valor de la propiedad tipo75.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo75(TipoBaseImponibleYCuota value) {
                    this.tipo75 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo10.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo10() {
                    return tipo10;
                }

                /**
                 * Define el valor de la propiedad tipo10.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo10(TipoBaseImponibleYCuota value) {
                    this.tipo10 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo21.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo21() {
                    return tipo21;
                }

                /**
                 * Define el valor de la propiedad tipo21.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo21(TipoBaseImponibleYCuota value) {
                    this.tipo21 = value;
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
             *         &lt;element name="TipoX" type="{}tipo_BaseImponible_y_Cuota"/&gt;
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
                "tipoX"
            })
            public static class IVAdevengadoInversionSP {

                @XmlElement(name = "TipoX", required = true)
                protected TipoBaseImponibleYCuota tipoX;

                /**
                 * Obtiene el valor de la propiedad tipoX.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipoX() {
                    return tipoX;
                }

                /**
                 * Define el valor de la propiedad tipoX.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipoX(TipoBaseImponibleYCuota value) {
                    this.tipoX = value;
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
             *         &lt;element name="TipoX" type="{}tipo_BaseImponible_y_Cuota"/&gt;
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
                "tipoX"
            })
            public static class ModBasesyCuotas {

                @XmlElement(name = "TipoX", required = true)
                protected TipoBaseImponibleYCuota tipoX;

                /**
                 * Obtiene el valor de la propiedad tipoX.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipoX() {
                    return tipoX;
                }

                /**
                 * Define el valor de la propiedad tipoX.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipoX(TipoBaseImponibleYCuota value) {
                    this.tipoX = value;
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
             *         &lt;element name="TipoX" type="{}tipo_BaseImponible_y_Cuota"/&gt;
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
                "tipoX"
            })
            public static class ModBasesyCuotasConcursoAcreedores {

                @XmlElement(name = "TipoX", required = true)
                protected TipoBaseImponibleYCuota tipoX;

                /**
                 * Obtiene el valor de la propiedad tipoX.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipoX() {
                    return tipoX;
                }

                /**
                 * Define el valor de la propiedad tipoX.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipoX(TipoBaseImponibleYCuota value) {
                    this.tipoX = value;
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
             *         &lt;element name="TipoX" type="{}tipo_BaseImponible_y_Cuota"/&gt;
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
                "tipoX"
            })
            public static class ModBasesyCuotasOpIntragrupo {

                @XmlElement(name = "TipoX", required = true)
                protected TipoBaseImponibleYCuota tipoX;

                /**
                 * Obtiene el valor de la propiedad tipoX.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipoX() {
                    return tipoX;
                }

                /**
                 * Define el valor de la propiedad tipoX.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipoX(TipoBaseImponibleYCuota value) {
                    this.tipoX = value;
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
             *         &lt;element name="TipoX" type="{}tipo_BaseImponible_y_Cuota"/&gt;
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
                "tipoX"
            })
            public static class ModRecargoEquivalencia {

                @XmlElement(name = "TipoX", required = true)
                protected TipoBaseImponibleYCuota tipoX;

                /**
                 * Obtiene el valor de la propiedad tipoX.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipoX() {
                    return tipoX;
                }

                /**
                 * Define el valor de la propiedad tipoX.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipoX(TipoBaseImponibleYCuota value) {
                    this.tipoX = value;
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
             *         &lt;element name="TipoX" type="{}tipo_BaseImponible_y_Cuota"/&gt;
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
                "tipoX"
            })
            public static class ModRecargoEquivalenciaConcursoAcreedores {

                @XmlElement(name = "TipoX", required = true)
                protected TipoBaseImponibleYCuota tipoX;

                /**
                 * Obtiene el valor de la propiedad tipoX.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipoX() {
                    return tipoX;
                }

                /**
                 * Define el valor de la propiedad tipoX.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipoX(TipoBaseImponibleYCuota value) {
                    this.tipoX = value;
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
             *         &lt;element name="Tipo0" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo2" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo4" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo5" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo75" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo10" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo21" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
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
                "tipo0",
                "tipo2",
                "tipo4",
                "tipo5",
                "tipo75",
                "tipo10",
                "tipo21"
            })
            public static class OpIntragrupo {

                @XmlElement(name = "Tipo0")
                protected TipoBaseImponibleYCuota tipo0;
                @XmlElement(name = "Tipo2")
                protected TipoBaseImponibleYCuota tipo2;
                @XmlElement(name = "Tipo4")
                protected TipoBaseImponibleYCuota tipo4;
                @XmlElement(name = "Tipo5")
                protected TipoBaseImponibleYCuota tipo5;
                @XmlElement(name = "Tipo75")
                protected TipoBaseImponibleYCuota tipo75;
                @XmlElement(name = "Tipo10")
                protected TipoBaseImponibleYCuota tipo10;
                @XmlElement(name = "Tipo21")
                protected TipoBaseImponibleYCuota tipo21;

                /**
                 * Obtiene el valor de la propiedad tipo0.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo0() {
                    return tipo0;
                }

                /**
                 * Define el valor de la propiedad tipo0.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo0(TipoBaseImponibleYCuota value) {
                    this.tipo0 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo2.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo2() {
                    return tipo2;
                }

                /**
                 * Define el valor de la propiedad tipo2.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo2(TipoBaseImponibleYCuota value) {
                    this.tipo2 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo4.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo4() {
                    return tipo4;
                }

                /**
                 * Define el valor de la propiedad tipo4.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo4(TipoBaseImponibleYCuota value) {
                    this.tipo4 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo5.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo5() {
                    return tipo5;
                }

                /**
                 * Define el valor de la propiedad tipo5.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo5(TipoBaseImponibleYCuota value) {
                    this.tipo5 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo75.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo75() {
                    return tipo75;
                }

                /**
                 * Define el valor de la propiedad tipo75.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo75(TipoBaseImponibleYCuota value) {
                    this.tipo75 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo10.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo10() {
                    return tipo10;
                }

                /**
                 * Define el valor de la propiedad tipo10.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo10(TipoBaseImponibleYCuota value) {
                    this.tipo10 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo21.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo21() {
                    return tipo21;
                }

                /**
                 * Define el valor de la propiedad tipo21.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo21(TipoBaseImponibleYCuota value) {
                    this.tipo21 = value;
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
             *         &lt;element name="Tipo0" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo026" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo05" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo062" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo1" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo14" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo52" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo175" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
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
                "tipo0",
                "tipo026",
                "tipo05",
                "tipo062",
                "tipo1",
                "tipo14",
                "tipo52",
                "tipo175"
            })
            public static class RecargoEquivalencia {

                @XmlElement(name = "Tipo0")
                protected TipoBaseImponibleYCuota tipo0;
                @XmlElement(name = "Tipo026")
                protected TipoBaseImponibleYCuota tipo026;
                @XmlElement(name = "Tipo05")
                protected TipoBaseImponibleYCuota tipo05;
                @XmlElement(name = "Tipo062")
                protected TipoBaseImponibleYCuota tipo062;
                @XmlElement(name = "Tipo1")
                protected TipoBaseImponibleYCuota tipo1;
                @XmlElement(name = "Tipo14")
                protected TipoBaseImponibleYCuota tipo14;
                @XmlElement(name = "Tipo52")
                protected TipoBaseImponibleYCuota tipo52;
                @XmlElement(name = "Tipo175")
                protected TipoBaseImponibleYCuota tipo175;

                /**
                 * Obtiene el valor de la propiedad tipo0.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo0() {
                    return tipo0;
                }

                /**
                 * Define el valor de la propiedad tipo0.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo0(TipoBaseImponibleYCuota value) {
                    this.tipo0 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo026.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo026() {
                    return tipo026;
                }

                /**
                 * Define el valor de la propiedad tipo026.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo026(TipoBaseImponibleYCuota value) {
                    this.tipo026 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo05.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo05() {
                    return tipo05;
                }

                /**
                 * Define el valor de la propiedad tipo05.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo05(TipoBaseImponibleYCuota value) {
                    this.tipo05 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo062.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo062() {
                    return tipo062;
                }

                /**
                 * Define el valor de la propiedad tipo062.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo062(TipoBaseImponibleYCuota value) {
                    this.tipo062 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo1.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo1() {
                    return tipo1;
                }

                /**
                 * Define el valor de la propiedad tipo1.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo1(TipoBaseImponibleYCuota value) {
                    this.tipo1 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo14.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo14() {
                    return tipo14;
                }

                /**
                 * Define el valor de la propiedad tipo14.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo14(TipoBaseImponibleYCuota value) {
                    this.tipo14 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo52.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo52() {
                    return tipo52;
                }

                /**
                 * Define el valor de la propiedad tipo52.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo52(TipoBaseImponibleYCuota value) {
                    this.tipo52 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo175.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo175() {
                    return tipo175;
                }

                /**
                 * Define el valor de la propiedad tipo175.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo175(TipoBaseImponibleYCuota value) {
                    this.tipo175 = value;
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
             *         &lt;element name="Tipo21" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
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
                "tipo21"
            })
            public static class RegAgViajes {

                @XmlElement(name = "Tipo21")
                protected TipoBaseImponibleYCuota tipo21;

                /**
                 * Obtiene el valor de la propiedad tipo21.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo21() {
                    return tipo21;
                }

                /**
                 * Define el valor de la propiedad tipo21.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo21(TipoBaseImponibleYCuota value) {
                    this.tipo21 = value;
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
             *         &lt;element name="Tipo0" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo2" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo4" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo5" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo75" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo10" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo21" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
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
                "tipo0",
                "tipo2",
                "tipo4",
                "tipo5",
                "tipo75",
                "tipo10",
                "tipo21"
            })
            public static class RegBienesUsados {

                @XmlElement(name = "Tipo0")
                protected TipoBaseImponibleYCuota tipo0;
                @XmlElement(name = "Tipo2")
                protected TipoBaseImponibleYCuota tipo2;
                @XmlElement(name = "Tipo4")
                protected TipoBaseImponibleYCuota tipo4;
                @XmlElement(name = "Tipo5")
                protected TipoBaseImponibleYCuota tipo5;
                @XmlElement(name = "Tipo75")
                protected TipoBaseImponibleYCuota tipo75;
                @XmlElement(name = "Tipo10")
                protected TipoBaseImponibleYCuota tipo10;
                @XmlElement(name = "Tipo21")
                protected TipoBaseImponibleYCuota tipo21;

                /**
                 * Obtiene el valor de la propiedad tipo0.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo0() {
                    return tipo0;
                }

                /**
                 * Define el valor de la propiedad tipo0.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo0(TipoBaseImponibleYCuota value) {
                    this.tipo0 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo2.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo2() {
                    return tipo2;
                }

                /**
                 * Define el valor de la propiedad tipo2.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo2(TipoBaseImponibleYCuota value) {
                    this.tipo2 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo4.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo4() {
                    return tipo4;
                }

                /**
                 * Define el valor de la propiedad tipo4.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo4(TipoBaseImponibleYCuota value) {
                    this.tipo4 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo5.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo5() {
                    return tipo5;
                }

                /**
                 * Define el valor de la propiedad tipo5.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo5(TipoBaseImponibleYCuota value) {
                    this.tipo5 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo75.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo75() {
                    return tipo75;
                }

                /**
                 * Define el valor de la propiedad tipo75.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo75(TipoBaseImponibleYCuota value) {
                    this.tipo75 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo10.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo10() {
                    return tipo10;
                }

                /**
                 * Define el valor de la propiedad tipo10.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo10(TipoBaseImponibleYCuota value) {
                    this.tipo10 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo21.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo21() {
                    return tipo21;
                }

                /**
                 * Define el valor de la propiedad tipo21.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo21(TipoBaseImponibleYCuota value) {
                    this.tipo21 = value;
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
             *         &lt;element name="Tipo0" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo2" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo4" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo5" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo75" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo10" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo21" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
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
                "tipo0",
                "tipo2",
                "tipo4",
                "tipo5",
                "tipo75",
                "tipo10",
                "tipo21"
            })
            public static class RegCriterioCaja {

                @XmlElement(name = "Tipo0")
                protected TipoBaseImponibleYCuota tipo0;
                @XmlElement(name = "Tipo2")
                protected TipoBaseImponibleYCuota tipo2;
                @XmlElement(name = "Tipo4")
                protected TipoBaseImponibleYCuota tipo4;
                @XmlElement(name = "Tipo5")
                protected TipoBaseImponibleYCuota tipo5;
                @XmlElement(name = "Tipo75")
                protected TipoBaseImponibleYCuota tipo75;
                @XmlElement(name = "Tipo10")
                protected TipoBaseImponibleYCuota tipo10;
                @XmlElement(name = "Tipo21")
                protected TipoBaseImponibleYCuota tipo21;

                /**
                 * Obtiene el valor de la propiedad tipo0.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo0() {
                    return tipo0;
                }

                /**
                 * Define el valor de la propiedad tipo0.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo0(TipoBaseImponibleYCuota value) {
                    this.tipo0 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo2.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo2() {
                    return tipo2;
                }

                /**
                 * Define el valor de la propiedad tipo2.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo2(TipoBaseImponibleYCuota value) {
                    this.tipo2 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo4.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo4() {
                    return tipo4;
                }

                /**
                 * Define el valor de la propiedad tipo4.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo4(TipoBaseImponibleYCuota value) {
                    this.tipo4 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo5.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo5() {
                    return tipo5;
                }

                /**
                 * Define el valor de la propiedad tipo5.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo5(TipoBaseImponibleYCuota value) {
                    this.tipo5 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo75.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo75() {
                    return tipo75;
                }

                /**
                 * Define el valor de la propiedad tipo75.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo75(TipoBaseImponibleYCuota value) {
                    this.tipo75 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo10.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo10() {
                    return tipo10;
                }

                /**
                 * Define el valor de la propiedad tipo10.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo10(TipoBaseImponibleYCuota value) {
                    this.tipo10 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo21.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo21() {
                    return tipo21;
                }

                /**
                 * Define el valor de la propiedad tipo21.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo21(TipoBaseImponibleYCuota value) {
                    this.tipo21 = value;
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
             *         &lt;element name="Tipo0" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo2" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo4" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo5" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo75" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo10" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo21" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
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
                "tipo0",
                "tipo2",
                "tipo4",
                "tipo5",
                "tipo75",
                "tipo10",
                "tipo21"
            })
            public static class RegOrdinario {

                @XmlElement(name = "Tipo0")
                protected TipoBaseImponibleYCuota tipo0;
                @XmlElement(name = "Tipo2")
                protected TipoBaseImponibleYCuota tipo2;
                @XmlElement(name = "Tipo4")
                protected TipoBaseImponibleYCuota tipo4;
                @XmlElement(name = "Tipo5")
                protected TipoBaseImponibleYCuota tipo5;
                @XmlElement(name = "Tipo75")
                protected TipoBaseImponibleYCuota tipo75;
                @XmlElement(name = "Tipo10")
                protected TipoBaseImponibleYCuota tipo10;
                @XmlElement(name = "Tipo21")
                protected TipoBaseImponibleYCuota tipo21;

                /**
                 * Obtiene el valor de la propiedad tipo0.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo0() {
                    return tipo0;
                }

                /**
                 * Define el valor de la propiedad tipo0.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo0(TipoBaseImponibleYCuota value) {
                    this.tipo0 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo2.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo2() {
                    return tipo2;
                }

                /**
                 * Define el valor de la propiedad tipo2.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo2(TipoBaseImponibleYCuota value) {
                    this.tipo2 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo4.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo4() {
                    return tipo4;
                }

                /**
                 * Define el valor de la propiedad tipo4.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo4(TipoBaseImponibleYCuota value) {
                    this.tipo4 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo5.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo5() {
                    return tipo5;
                }

                /**
                 * Define el valor de la propiedad tipo5.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo5(TipoBaseImponibleYCuota value) {
                    this.tipo5 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo75.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo75() {
                    return tipo75;
                }

                /**
                 * Define el valor de la propiedad tipo75.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo75(TipoBaseImponibleYCuota value) {
                    this.tipo75 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo10.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo10() {
                    return tipo10;
                }

                /**
                 * Define el valor de la propiedad tipo10.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo10(TipoBaseImponibleYCuota value) {
                    this.tipo10 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo21.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo21() {
                    return tipo21;
                }

                /**
                 * Define el valor de la propiedad tipo21.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo21(TipoBaseImponibleYCuota value) {
                    this.tipo21 = value;
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
             *         &lt;element name="TipoX" type="{}tipo_BaseImponible_y_Cuota"/&gt;
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
                "tipoX"
            })
            public static class TotalBasesyCuotasIVA {

                @XmlElement(name = "TipoX", required = true)
                protected TipoBaseImponibleYCuota tipoX;

                /**
                 * Obtiene el valor de la propiedad tipoX.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipoX() {
                    return tipoX;
                }

                /**
                 * Define el valor de la propiedad tipoX.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipoX(TipoBaseImponibleYCuota value) {
                    this.tipoX = value;
                }

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
         *         &lt;element name="OpInterioresBienesServiciosCorrientes" minOccurs="0"&gt;
         *           &lt;complexType&gt;
         *             &lt;complexContent&gt;
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *                 &lt;sequence&gt;
         *                   &lt;element name="Tipo2" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo4" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo5" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo75" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo10" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo21" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Total" type="{}tipo_BaseImponible_y_Cuota"/&gt;
         *                 &lt;/sequence&gt;
         *               &lt;/restriction&gt;
         *             &lt;/complexContent&gt;
         *           &lt;/complexType&gt;
         *         &lt;/element&gt;
         *         &lt;element name="OpIntragrupoCorrientes" minOccurs="0"&gt;
         *           &lt;complexType&gt;
         *             &lt;complexContent&gt;
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *                 &lt;sequence&gt;
         *                   &lt;element name="Tipo2" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo4" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo5" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo75" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo10" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo21" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Total" type="{}tipo_BaseImponible_y_Cuota"/&gt;
         *                 &lt;/sequence&gt;
         *               &lt;/restriction&gt;
         *             &lt;/complexContent&gt;
         *           &lt;/complexType&gt;
         *         &lt;/element&gt;
         *         &lt;element name="OpInterioresBienesInversion" minOccurs="0"&gt;
         *           &lt;complexType&gt;
         *             &lt;complexContent&gt;
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *                 &lt;sequence&gt;
         *                   &lt;element name="Tipo2" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo4" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo5" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo75" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo10" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo21" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Total" type="{}tipo_BaseImponible_y_Cuota"/&gt;
         *                 &lt;/sequence&gt;
         *               &lt;/restriction&gt;
         *             &lt;/complexContent&gt;
         *           &lt;/complexType&gt;
         *         &lt;/element&gt;
         *         &lt;element name="OpIntragrupoBienesInversion" minOccurs="0"&gt;
         *           &lt;complexType&gt;
         *             &lt;complexContent&gt;
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *                 &lt;sequence&gt;
         *                   &lt;element name="Tipo2" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo4" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo5" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo75" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo10" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo21" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Total" type="{}tipo_BaseImponible_y_Cuota"/&gt;
         *                 &lt;/sequence&gt;
         *               &lt;/restriction&gt;
         *             &lt;/complexContent&gt;
         *           &lt;/complexType&gt;
         *         &lt;/element&gt;
         *         &lt;element name="ImportacionesBienesCorrientes" minOccurs="0"&gt;
         *           &lt;complexType&gt;
         *             &lt;complexContent&gt;
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *                 &lt;sequence&gt;
         *                   &lt;element name="Tipo2" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo4" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo5" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo75" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo10" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo21" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Total" type="{}tipo_BaseImponible_y_Cuota"/&gt;
         *                 &lt;/sequence&gt;
         *               &lt;/restriction&gt;
         *             &lt;/complexContent&gt;
         *           &lt;/complexType&gt;
         *         &lt;/element&gt;
         *         &lt;element name="ImportacionesBienesInversion" minOccurs="0"&gt;
         *           &lt;complexType&gt;
         *             &lt;complexContent&gt;
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *                 &lt;sequence&gt;
         *                   &lt;element name="Tipo2" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo4" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo5" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo75" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo10" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo21" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Total" type="{}tipo_BaseImponible_y_Cuota"/&gt;
         *                 &lt;/sequence&gt;
         *               &lt;/restriction&gt;
         *             &lt;/complexContent&gt;
         *           &lt;/complexType&gt;
         *         &lt;/element&gt;
         *         &lt;element name="AdqIntracomunitariasBienesCorrientes" minOccurs="0"&gt;
         *           &lt;complexType&gt;
         *             &lt;complexContent&gt;
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *                 &lt;sequence&gt;
         *                   &lt;element name="Tipo2" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo4" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo5" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo75" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo10" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo21" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Total" type="{}tipo_BaseImponible_y_Cuota"/&gt;
         *                 &lt;/sequence&gt;
         *               &lt;/restriction&gt;
         *             &lt;/complexContent&gt;
         *           &lt;/complexType&gt;
         *         &lt;/element&gt;
         *         &lt;element name="AdqIntracomunitariasBienesInversion" minOccurs="0"&gt;
         *           &lt;complexType&gt;
         *             &lt;complexContent&gt;
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *                 &lt;sequence&gt;
         *                   &lt;element name="Tipo2" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo4" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo5" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo75" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo10" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo21" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Total" type="{}tipo_BaseImponible_y_Cuota"/&gt;
         *                 &lt;/sequence&gt;
         *               &lt;/restriction&gt;
         *             &lt;/complexContent&gt;
         *           &lt;/complexType&gt;
         *         &lt;/element&gt;
         *         &lt;element name="AdqIntracomunitariasServicios" minOccurs="0"&gt;
         *           &lt;complexType&gt;
         *             &lt;complexContent&gt;
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *                 &lt;sequence&gt;
         *                   &lt;element name="Tipo2" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo4" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo5" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo75" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo10" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Tipo21" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
         *                   &lt;element name="Total" type="{}tipo_BaseImponible_y_Cuota"/&gt;
         *                 &lt;/sequence&gt;
         *               &lt;/restriction&gt;
         *             &lt;/complexContent&gt;
         *           &lt;/complexType&gt;
         *         &lt;/element&gt;
         *         &lt;element name="ComRegAgricGanadPesca" minOccurs="0"&gt;
         *           &lt;complexType&gt;
         *             &lt;complexContent&gt;
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *                 &lt;sequence&gt;
         *                   &lt;element name="TipoX" type="{}tipo_BaseImponible_y_Cuota"/&gt;
         *                 &lt;/sequence&gt;
         *               &lt;/restriction&gt;
         *             &lt;/complexContent&gt;
         *           &lt;/complexType&gt;
         *         &lt;/element&gt;
         *         &lt;element name="CouDedResAdministrativas" minOccurs="0"&gt;
         *           &lt;complexType&gt;
         *             &lt;complexContent&gt;
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *                 &lt;sequence&gt;
         *                   &lt;element name="TipoX" type="{}tipo_BaseImponible_y_Cuota"/&gt;
         *                 &lt;/sequence&gt;
         *               &lt;/restriction&gt;
         *             &lt;/complexContent&gt;
         *           &lt;/complexType&gt;
         *         &lt;/element&gt;
         *         &lt;element name="RectifDeducciones" minOccurs="0"&gt;
         *           &lt;complexType&gt;
         *             &lt;complexContent&gt;
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *                 &lt;sequence&gt;
         *                   &lt;element name="TipoX" type="{}tipo_BaseImponible_y_Cuota"/&gt;
         *                 &lt;/sequence&gt;
         *               &lt;/restriction&gt;
         *             &lt;/complexContent&gt;
         *           &lt;/complexType&gt;
         *         &lt;/element&gt;
         *         &lt;element name="RectifOpIntragrupo" minOccurs="0"&gt;
         *           &lt;complexType&gt;
         *             &lt;complexContent&gt;
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *                 &lt;sequence&gt;
         *                   &lt;element name="TipoX" type="{}tipo_BaseImponible_y_Cuota"/&gt;
         *                 &lt;/sequence&gt;
         *               &lt;/restriction&gt;
         *             &lt;/complexContent&gt;
         *           &lt;/complexType&gt;
         *         &lt;/element&gt;
         *         &lt;element name="RegularizInversiones" type="{}tipo_ImpNegativo" minOccurs="0"/&gt;
         *         &lt;element name="RegularizPorcProrrata" type="{}tipo_ImpNegativo" minOccurs="0"/&gt;
         *         &lt;element name="SumDeducciones" type="{}tipo_ImpNegativo" minOccurs="0"/&gt;
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
            "opInterioresBienesServiciosCorrientes",
            "opIntragrupoCorrientes",
            "opInterioresBienesInversion",
            "opIntragrupoBienesInversion",
            "importacionesBienesCorrientes",
            "importacionesBienesInversion",
            "adqIntracomunitariasBienesCorrientes",
            "adqIntracomunitariasBienesInversion",
            "adqIntracomunitariasServicios",
            "comRegAgricGanadPesca",
            "couDedResAdministrativas",
            "rectifDeducciones",
            "rectifOpIntragrupo",
            "regularizInversiones",
            "regularizPorcProrrata",
            "sumDeducciones"
        })
        public static class Deducciones {

            @XmlElement(name = "OpInterioresBienesServiciosCorrientes")
            protected AEATIVA2024 .RegGeneral.Deducciones.OpInterioresBienesServiciosCorrientes opInterioresBienesServiciosCorrientes;
            @XmlElement(name = "OpIntragrupoCorrientes")
            protected AEATIVA2024 .RegGeneral.Deducciones.OpIntragrupoCorrientes opIntragrupoCorrientes;
            @XmlElement(name = "OpInterioresBienesInversion")
            protected AEATIVA2024 .RegGeneral.Deducciones.OpInterioresBienesInversion opInterioresBienesInversion;
            @XmlElement(name = "OpIntragrupoBienesInversion")
            protected AEATIVA2024 .RegGeneral.Deducciones.OpIntragrupoBienesInversion opIntragrupoBienesInversion;
            @XmlElement(name = "ImportacionesBienesCorrientes")
            protected AEATIVA2024 .RegGeneral.Deducciones.ImportacionesBienesCorrientes importacionesBienesCorrientes;
            @XmlElement(name = "ImportacionesBienesInversion")
            protected AEATIVA2024 .RegGeneral.Deducciones.ImportacionesBienesInversion importacionesBienesInversion;
            @XmlElement(name = "AdqIntracomunitariasBienesCorrientes")
            protected AEATIVA2024 .RegGeneral.Deducciones.AdqIntracomunitariasBienesCorrientes adqIntracomunitariasBienesCorrientes;
            @XmlElement(name = "AdqIntracomunitariasBienesInversion")
            protected AEATIVA2024 .RegGeneral.Deducciones.AdqIntracomunitariasBienesInversion adqIntracomunitariasBienesInversion;
            @XmlElement(name = "AdqIntracomunitariasServicios")
            protected AEATIVA2024 .RegGeneral.Deducciones.AdqIntracomunitariasServicios adqIntracomunitariasServicios;
            @XmlElement(name = "ComRegAgricGanadPesca")
            protected AEATIVA2024 .RegGeneral.Deducciones.ComRegAgricGanadPesca comRegAgricGanadPesca;
            @XmlElement(name = "CouDedResAdministrativas")
            protected AEATIVA2024 .RegGeneral.Deducciones.CouDedResAdministrativas couDedResAdministrativas;
            @XmlElement(name = "RectifDeducciones")
            protected AEATIVA2024 .RegGeneral.Deducciones.RectifDeducciones rectifDeducciones;
            @XmlElement(name = "RectifOpIntragrupo")
            protected AEATIVA2024 .RegGeneral.Deducciones.RectifOpIntragrupo rectifOpIntragrupo;
            @XmlElement(name = "RegularizInversiones")
            protected BigDecimal regularizInversiones;
            @XmlElement(name = "RegularizPorcProrrata")
            protected BigDecimal regularizPorcProrrata;
            @XmlElement(name = "SumDeducciones")
            protected BigDecimal sumDeducciones;

            /**
             * Obtiene el valor de la propiedad opInterioresBienesServiciosCorrientes.
             * 
             * @return
             *     possible object is
             *     {@link AEATIVA2024 .RegGeneral.Deducciones.OpInterioresBienesServiciosCorrientes }
             *     
             */
            public AEATIVA2024 .RegGeneral.Deducciones.OpInterioresBienesServiciosCorrientes getOpInterioresBienesServiciosCorrientes() {
                return opInterioresBienesServiciosCorrientes;
            }

            /**
             * Define el valor de la propiedad opInterioresBienesServiciosCorrientes.
             * 
             * @param value
             *     allowed object is
             *     {@link AEATIVA2024 .RegGeneral.Deducciones.OpInterioresBienesServiciosCorrientes }
             *     
             */
            public void setOpInterioresBienesServiciosCorrientes(AEATIVA2024 .RegGeneral.Deducciones.OpInterioresBienesServiciosCorrientes value) {
                this.opInterioresBienesServiciosCorrientes = value;
            }

            /**
             * Obtiene el valor de la propiedad opIntragrupoCorrientes.
             * 
             * @return
             *     possible object is
             *     {@link AEATIVA2024 .RegGeneral.Deducciones.OpIntragrupoCorrientes }
             *     
             */
            public AEATIVA2024 .RegGeneral.Deducciones.OpIntragrupoCorrientes getOpIntragrupoCorrientes() {
                return opIntragrupoCorrientes;
            }

            /**
             * Define el valor de la propiedad opIntragrupoCorrientes.
             * 
             * @param value
             *     allowed object is
             *     {@link AEATIVA2024 .RegGeneral.Deducciones.OpIntragrupoCorrientes }
             *     
             */
            public void setOpIntragrupoCorrientes(AEATIVA2024 .RegGeneral.Deducciones.OpIntragrupoCorrientes value) {
                this.opIntragrupoCorrientes = value;
            }

            /**
             * Obtiene el valor de la propiedad opInterioresBienesInversion.
             * 
             * @return
             *     possible object is
             *     {@link AEATIVA2024 .RegGeneral.Deducciones.OpInterioresBienesInversion }
             *     
             */
            public AEATIVA2024 .RegGeneral.Deducciones.OpInterioresBienesInversion getOpInterioresBienesInversion() {
                return opInterioresBienesInversion;
            }

            /**
             * Define el valor de la propiedad opInterioresBienesInversion.
             * 
             * @param value
             *     allowed object is
             *     {@link AEATIVA2024 .RegGeneral.Deducciones.OpInterioresBienesInversion }
             *     
             */
            public void setOpInterioresBienesInversion(AEATIVA2024 .RegGeneral.Deducciones.OpInterioresBienesInversion value) {
                this.opInterioresBienesInversion = value;
            }

            /**
             * Obtiene el valor de la propiedad opIntragrupoBienesInversion.
             * 
             * @return
             *     possible object is
             *     {@link AEATIVA2024 .RegGeneral.Deducciones.OpIntragrupoBienesInversion }
             *     
             */
            public AEATIVA2024 .RegGeneral.Deducciones.OpIntragrupoBienesInversion getOpIntragrupoBienesInversion() {
                return opIntragrupoBienesInversion;
            }

            /**
             * Define el valor de la propiedad opIntragrupoBienesInversion.
             * 
             * @param value
             *     allowed object is
             *     {@link AEATIVA2024 .RegGeneral.Deducciones.OpIntragrupoBienesInversion }
             *     
             */
            public void setOpIntragrupoBienesInversion(AEATIVA2024 .RegGeneral.Deducciones.OpIntragrupoBienesInversion value) {
                this.opIntragrupoBienesInversion = value;
            }

            /**
             * Obtiene el valor de la propiedad importacionesBienesCorrientes.
             * 
             * @return
             *     possible object is
             *     {@link AEATIVA2024 .RegGeneral.Deducciones.ImportacionesBienesCorrientes }
             *     
             */
            public AEATIVA2024 .RegGeneral.Deducciones.ImportacionesBienesCorrientes getImportacionesBienesCorrientes() {
                return importacionesBienesCorrientes;
            }

            /**
             * Define el valor de la propiedad importacionesBienesCorrientes.
             * 
             * @param value
             *     allowed object is
             *     {@link AEATIVA2024 .RegGeneral.Deducciones.ImportacionesBienesCorrientes }
             *     
             */
            public void setImportacionesBienesCorrientes(AEATIVA2024 .RegGeneral.Deducciones.ImportacionesBienesCorrientes value) {
                this.importacionesBienesCorrientes = value;
            }

            /**
             * Obtiene el valor de la propiedad importacionesBienesInversion.
             * 
             * @return
             *     possible object is
             *     {@link AEATIVA2024 .RegGeneral.Deducciones.ImportacionesBienesInversion }
             *     
             */
            public AEATIVA2024 .RegGeneral.Deducciones.ImportacionesBienesInversion getImportacionesBienesInversion() {
                return importacionesBienesInversion;
            }

            /**
             * Define el valor de la propiedad importacionesBienesInversion.
             * 
             * @param value
             *     allowed object is
             *     {@link AEATIVA2024 .RegGeneral.Deducciones.ImportacionesBienesInversion }
             *     
             */
            public void setImportacionesBienesInversion(AEATIVA2024 .RegGeneral.Deducciones.ImportacionesBienesInversion value) {
                this.importacionesBienesInversion = value;
            }

            /**
             * Obtiene el valor de la propiedad adqIntracomunitariasBienesCorrientes.
             * 
             * @return
             *     possible object is
             *     {@link AEATIVA2024 .RegGeneral.Deducciones.AdqIntracomunitariasBienesCorrientes }
             *     
             */
            public AEATIVA2024 .RegGeneral.Deducciones.AdqIntracomunitariasBienesCorrientes getAdqIntracomunitariasBienesCorrientes() {
                return adqIntracomunitariasBienesCorrientes;
            }

            /**
             * Define el valor de la propiedad adqIntracomunitariasBienesCorrientes.
             * 
             * @param value
             *     allowed object is
             *     {@link AEATIVA2024 .RegGeneral.Deducciones.AdqIntracomunitariasBienesCorrientes }
             *     
             */
            public void setAdqIntracomunitariasBienesCorrientes(AEATIVA2024 .RegGeneral.Deducciones.AdqIntracomunitariasBienesCorrientes value) {
                this.adqIntracomunitariasBienesCorrientes = value;
            }

            /**
             * Obtiene el valor de la propiedad adqIntracomunitariasBienesInversion.
             * 
             * @return
             *     possible object is
             *     {@link AEATIVA2024 .RegGeneral.Deducciones.AdqIntracomunitariasBienesInversion }
             *     
             */
            public AEATIVA2024 .RegGeneral.Deducciones.AdqIntracomunitariasBienesInversion getAdqIntracomunitariasBienesInversion() {
                return adqIntracomunitariasBienesInversion;
            }

            /**
             * Define el valor de la propiedad adqIntracomunitariasBienesInversion.
             * 
             * @param value
             *     allowed object is
             *     {@link AEATIVA2024 .RegGeneral.Deducciones.AdqIntracomunitariasBienesInversion }
             *     
             */
            public void setAdqIntracomunitariasBienesInversion(AEATIVA2024 .RegGeneral.Deducciones.AdqIntracomunitariasBienesInversion value) {
                this.adqIntracomunitariasBienesInversion = value;
            }

            /**
             * Obtiene el valor de la propiedad adqIntracomunitariasServicios.
             * 
             * @return
             *     possible object is
             *     {@link AEATIVA2024 .RegGeneral.Deducciones.AdqIntracomunitariasServicios }
             *     
             */
            public AEATIVA2024 .RegGeneral.Deducciones.AdqIntracomunitariasServicios getAdqIntracomunitariasServicios() {
                return adqIntracomunitariasServicios;
            }

            /**
             * Define el valor de la propiedad adqIntracomunitariasServicios.
             * 
             * @param value
             *     allowed object is
             *     {@link AEATIVA2024 .RegGeneral.Deducciones.AdqIntracomunitariasServicios }
             *     
             */
            public void setAdqIntracomunitariasServicios(AEATIVA2024 .RegGeneral.Deducciones.AdqIntracomunitariasServicios value) {
                this.adqIntracomunitariasServicios = value;
            }

            /**
             * Obtiene el valor de la propiedad comRegAgricGanadPesca.
             * 
             * @return
             *     possible object is
             *     {@link AEATIVA2024 .RegGeneral.Deducciones.ComRegAgricGanadPesca }
             *     
             */
            public AEATIVA2024 .RegGeneral.Deducciones.ComRegAgricGanadPesca getComRegAgricGanadPesca() {
                return comRegAgricGanadPesca;
            }

            /**
             * Define el valor de la propiedad comRegAgricGanadPesca.
             * 
             * @param value
             *     allowed object is
             *     {@link AEATIVA2024 .RegGeneral.Deducciones.ComRegAgricGanadPesca }
             *     
             */
            public void setComRegAgricGanadPesca(AEATIVA2024 .RegGeneral.Deducciones.ComRegAgricGanadPesca value) {
                this.comRegAgricGanadPesca = value;
            }

            /**
             * Obtiene el valor de la propiedad couDedResAdministrativas.
             * 
             * @return
             *     possible object is
             *     {@link AEATIVA2024 .RegGeneral.Deducciones.CouDedResAdministrativas }
             *     
             */
            public AEATIVA2024 .RegGeneral.Deducciones.CouDedResAdministrativas getCouDedResAdministrativas() {
                return couDedResAdministrativas;
            }

            /**
             * Define el valor de la propiedad couDedResAdministrativas.
             * 
             * @param value
             *     allowed object is
             *     {@link AEATIVA2024 .RegGeneral.Deducciones.CouDedResAdministrativas }
             *     
             */
            public void setCouDedResAdministrativas(AEATIVA2024 .RegGeneral.Deducciones.CouDedResAdministrativas value) {
                this.couDedResAdministrativas = value;
            }

            /**
             * Obtiene el valor de la propiedad rectifDeducciones.
             * 
             * @return
             *     possible object is
             *     {@link AEATIVA2024 .RegGeneral.Deducciones.RectifDeducciones }
             *     
             */
            public AEATIVA2024 .RegGeneral.Deducciones.RectifDeducciones getRectifDeducciones() {
                return rectifDeducciones;
            }

            /**
             * Define el valor de la propiedad rectifDeducciones.
             * 
             * @param value
             *     allowed object is
             *     {@link AEATIVA2024 .RegGeneral.Deducciones.RectifDeducciones }
             *     
             */
            public void setRectifDeducciones(AEATIVA2024 .RegGeneral.Deducciones.RectifDeducciones value) {
                this.rectifDeducciones = value;
            }

            /**
             * Obtiene el valor de la propiedad rectifOpIntragrupo.
             * 
             * @return
             *     possible object is
             *     {@link AEATIVA2024 .RegGeneral.Deducciones.RectifOpIntragrupo }
             *     
             */
            public AEATIVA2024 .RegGeneral.Deducciones.RectifOpIntragrupo getRectifOpIntragrupo() {
                return rectifOpIntragrupo;
            }

            /**
             * Define el valor de la propiedad rectifOpIntragrupo.
             * 
             * @param value
             *     allowed object is
             *     {@link AEATIVA2024 .RegGeneral.Deducciones.RectifOpIntragrupo }
             *     
             */
            public void setRectifOpIntragrupo(AEATIVA2024 .RegGeneral.Deducciones.RectifOpIntragrupo value) {
                this.rectifOpIntragrupo = value;
            }

            /**
             * Obtiene el valor de la propiedad regularizInversiones.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getRegularizInversiones() {
                return regularizInversiones;
            }

            /**
             * Define el valor de la propiedad regularizInversiones.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setRegularizInversiones(BigDecimal value) {
                this.regularizInversiones = value;
            }

            /**
             * Obtiene el valor de la propiedad regularizPorcProrrata.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getRegularizPorcProrrata() {
                return regularizPorcProrrata;
            }

            /**
             * Define el valor de la propiedad regularizPorcProrrata.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setRegularizPorcProrrata(BigDecimal value) {
                this.regularizPorcProrrata = value;
            }

            /**
             * Obtiene el valor de la propiedad sumDeducciones.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getSumDeducciones() {
                return sumDeducciones;
            }

            /**
             * Define el valor de la propiedad sumDeducciones.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setSumDeducciones(BigDecimal value) {
                this.sumDeducciones = value;
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
             *         &lt;element name="Tipo2" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo4" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo5" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo75" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo10" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo21" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Total" type="{}tipo_BaseImponible_y_Cuota"/&gt;
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
                "tipo2",
                "tipo4",
                "tipo5",
                "tipo75",
                "tipo10",
                "tipo21",
                "total"
            })
            public static class AdqIntracomunitariasBienesCorrientes {

                @XmlElement(name = "Tipo2")
                protected TipoBaseImponibleYCuota tipo2;
                @XmlElement(name = "Tipo4")
                protected TipoBaseImponibleYCuota tipo4;
                @XmlElement(name = "Tipo5")
                protected TipoBaseImponibleYCuota tipo5;
                @XmlElement(name = "Tipo75")
                protected TipoBaseImponibleYCuota tipo75;
                @XmlElement(name = "Tipo10")
                protected TipoBaseImponibleYCuota tipo10;
                @XmlElement(name = "Tipo21")
                protected TipoBaseImponibleYCuota tipo21;
                @XmlElement(name = "Total", required = true)
                protected TipoBaseImponibleYCuota total;

                /**
                 * Obtiene el valor de la propiedad tipo2.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo2() {
                    return tipo2;
                }

                /**
                 * Define el valor de la propiedad tipo2.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo2(TipoBaseImponibleYCuota value) {
                    this.tipo2 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo4.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo4() {
                    return tipo4;
                }

                /**
                 * Define el valor de la propiedad tipo4.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo4(TipoBaseImponibleYCuota value) {
                    this.tipo4 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo5.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo5() {
                    return tipo5;
                }

                /**
                 * Define el valor de la propiedad tipo5.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo5(TipoBaseImponibleYCuota value) {
                    this.tipo5 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo75.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo75() {
                    return tipo75;
                }

                /**
                 * Define el valor de la propiedad tipo75.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo75(TipoBaseImponibleYCuota value) {
                    this.tipo75 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo10.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo10() {
                    return tipo10;
                }

                /**
                 * Define el valor de la propiedad tipo10.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo10(TipoBaseImponibleYCuota value) {
                    this.tipo10 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo21.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo21() {
                    return tipo21;
                }

                /**
                 * Define el valor de la propiedad tipo21.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo21(TipoBaseImponibleYCuota value) {
                    this.tipo21 = value;
                }

                /**
                 * Obtiene el valor de la propiedad total.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTotal() {
                    return total;
                }

                /**
                 * Define el valor de la propiedad total.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTotal(TipoBaseImponibleYCuota value) {
                    this.total = value;
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
             *         &lt;element name="Tipo2" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo4" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo5" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo75" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo10" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo21" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Total" type="{}tipo_BaseImponible_y_Cuota"/&gt;
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
                "tipo2",
                "tipo4",
                "tipo5",
                "tipo75",
                "tipo10",
                "tipo21",
                "total"
            })
            public static class AdqIntracomunitariasBienesInversion {

                @XmlElement(name = "Tipo2")
                protected TipoBaseImponibleYCuota tipo2;
                @XmlElement(name = "Tipo4")
                protected TipoBaseImponibleYCuota tipo4;
                @XmlElement(name = "Tipo5")
                protected TipoBaseImponibleYCuota tipo5;
                @XmlElement(name = "Tipo75")
                protected TipoBaseImponibleYCuota tipo75;
                @XmlElement(name = "Tipo10")
                protected TipoBaseImponibleYCuota tipo10;
                @XmlElement(name = "Tipo21")
                protected TipoBaseImponibleYCuota tipo21;
                @XmlElement(name = "Total", required = true)
                protected TipoBaseImponibleYCuota total;

                /**
                 * Obtiene el valor de la propiedad tipo2.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo2() {
                    return tipo2;
                }

                /**
                 * Define el valor de la propiedad tipo2.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo2(TipoBaseImponibleYCuota value) {
                    this.tipo2 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo4.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo4() {
                    return tipo4;
                }

                /**
                 * Define el valor de la propiedad tipo4.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo4(TipoBaseImponibleYCuota value) {
                    this.tipo4 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo5.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo5() {
                    return tipo5;
                }

                /**
                 * Define el valor de la propiedad tipo5.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo5(TipoBaseImponibleYCuota value) {
                    this.tipo5 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo75.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo75() {
                    return tipo75;
                }

                /**
                 * Define el valor de la propiedad tipo75.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo75(TipoBaseImponibleYCuota value) {
                    this.tipo75 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo10.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo10() {
                    return tipo10;
                }

                /**
                 * Define el valor de la propiedad tipo10.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo10(TipoBaseImponibleYCuota value) {
                    this.tipo10 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo21.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo21() {
                    return tipo21;
                }

                /**
                 * Define el valor de la propiedad tipo21.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo21(TipoBaseImponibleYCuota value) {
                    this.tipo21 = value;
                }

                /**
                 * Obtiene el valor de la propiedad total.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTotal() {
                    return total;
                }

                /**
                 * Define el valor de la propiedad total.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTotal(TipoBaseImponibleYCuota value) {
                    this.total = value;
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
             *         &lt;element name="Tipo2" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo4" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo5" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo75" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo10" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo21" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Total" type="{}tipo_BaseImponible_y_Cuota"/&gt;
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
                "tipo2",
                "tipo4",
                "tipo5",
                "tipo75",
                "tipo10",
                "tipo21",
                "total"
            })
            public static class AdqIntracomunitariasServicios {

                @XmlElement(name = "Tipo2")
                protected TipoBaseImponibleYCuota tipo2;
                @XmlElement(name = "Tipo4")
                protected TipoBaseImponibleYCuota tipo4;
                @XmlElement(name = "Tipo5")
                protected TipoBaseImponibleYCuota tipo5;
                @XmlElement(name = "Tipo75")
                protected TipoBaseImponibleYCuota tipo75;
                @XmlElement(name = "Tipo10")
                protected TipoBaseImponibleYCuota tipo10;
                @XmlElement(name = "Tipo21")
                protected TipoBaseImponibleYCuota tipo21;
                @XmlElement(name = "Total", required = true)
                protected TipoBaseImponibleYCuota total;

                /**
                 * Obtiene el valor de la propiedad tipo2.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo2() {
                    return tipo2;
                }

                /**
                 * Define el valor de la propiedad tipo2.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo2(TipoBaseImponibleYCuota value) {
                    this.tipo2 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo4.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo4() {
                    return tipo4;
                }

                /**
                 * Define el valor de la propiedad tipo4.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo4(TipoBaseImponibleYCuota value) {
                    this.tipo4 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo5.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo5() {
                    return tipo5;
                }

                /**
                 * Define el valor de la propiedad tipo5.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo5(TipoBaseImponibleYCuota value) {
                    this.tipo5 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo75.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo75() {
                    return tipo75;
                }

                /**
                 * Define el valor de la propiedad tipo75.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo75(TipoBaseImponibleYCuota value) {
                    this.tipo75 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo10.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo10() {
                    return tipo10;
                }

                /**
                 * Define el valor de la propiedad tipo10.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo10(TipoBaseImponibleYCuota value) {
                    this.tipo10 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo21.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo21() {
                    return tipo21;
                }

                /**
                 * Define el valor de la propiedad tipo21.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo21(TipoBaseImponibleYCuota value) {
                    this.tipo21 = value;
                }

                /**
                 * Obtiene el valor de la propiedad total.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTotal() {
                    return total;
                }

                /**
                 * Define el valor de la propiedad total.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTotal(TipoBaseImponibleYCuota value) {
                    this.total = value;
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
             *         &lt;element name="TipoX" type="{}tipo_BaseImponible_y_Cuota"/&gt;
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
                "tipoX"
            })
            public static class ComRegAgricGanadPesca {

                @XmlElement(name = "TipoX", required = true)
                protected TipoBaseImponibleYCuota tipoX;

                /**
                 * Obtiene el valor de la propiedad tipoX.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipoX() {
                    return tipoX;
                }

                /**
                 * Define el valor de la propiedad tipoX.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipoX(TipoBaseImponibleYCuota value) {
                    this.tipoX = value;
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
             *         &lt;element name="TipoX" type="{}tipo_BaseImponible_y_Cuota"/&gt;
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
                "tipoX"
            })
            public static class CouDedResAdministrativas {

                @XmlElement(name = "TipoX", required = true)
                protected TipoBaseImponibleYCuota tipoX;

                /**
                 * Obtiene el valor de la propiedad tipoX.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipoX() {
                    return tipoX;
                }

                /**
                 * Define el valor de la propiedad tipoX.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipoX(TipoBaseImponibleYCuota value) {
                    this.tipoX = value;
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
             *         &lt;element name="Tipo2" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo4" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo5" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo75" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo10" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo21" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Total" type="{}tipo_BaseImponible_y_Cuota"/&gt;
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
                "tipo2",
                "tipo4",
                "tipo5",
                "tipo75",
                "tipo10",
                "tipo21",
                "total"
            })
            public static class ImportacionesBienesCorrientes {

                @XmlElement(name = "Tipo2")
                protected TipoBaseImponibleYCuota tipo2;
                @XmlElement(name = "Tipo4")
                protected TipoBaseImponibleYCuota tipo4;
                @XmlElement(name = "Tipo5")
                protected TipoBaseImponibleYCuota tipo5;
                @XmlElement(name = "Tipo75")
                protected TipoBaseImponibleYCuota tipo75;
                @XmlElement(name = "Tipo10")
                protected TipoBaseImponibleYCuota tipo10;
                @XmlElement(name = "Tipo21")
                protected TipoBaseImponibleYCuota tipo21;
                @XmlElement(name = "Total", required = true)
                protected TipoBaseImponibleYCuota total;

                /**
                 * Obtiene el valor de la propiedad tipo2.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo2() {
                    return tipo2;
                }

                /**
                 * Define el valor de la propiedad tipo2.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo2(TipoBaseImponibleYCuota value) {
                    this.tipo2 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo4.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo4() {
                    return tipo4;
                }

                /**
                 * Define el valor de la propiedad tipo4.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo4(TipoBaseImponibleYCuota value) {
                    this.tipo4 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo5.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo5() {
                    return tipo5;
                }

                /**
                 * Define el valor de la propiedad tipo5.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo5(TipoBaseImponibleYCuota value) {
                    this.tipo5 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo75.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo75() {
                    return tipo75;
                }

                /**
                 * Define el valor de la propiedad tipo75.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo75(TipoBaseImponibleYCuota value) {
                    this.tipo75 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo10.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo10() {
                    return tipo10;
                }

                /**
                 * Define el valor de la propiedad tipo10.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo10(TipoBaseImponibleYCuota value) {
                    this.tipo10 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo21.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo21() {
                    return tipo21;
                }

                /**
                 * Define el valor de la propiedad tipo21.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo21(TipoBaseImponibleYCuota value) {
                    this.tipo21 = value;
                }

                /**
                 * Obtiene el valor de la propiedad total.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTotal() {
                    return total;
                }

                /**
                 * Define el valor de la propiedad total.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTotal(TipoBaseImponibleYCuota value) {
                    this.total = value;
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
             *         &lt;element name="Tipo2" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo4" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo5" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo75" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo10" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo21" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Total" type="{}tipo_BaseImponible_y_Cuota"/&gt;
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
                "tipo2",
                "tipo4",
                "tipo5",
                "tipo75",
                "tipo10",
                "tipo21",
                "total"
            })
            public static class ImportacionesBienesInversion {

                @XmlElement(name = "Tipo2")
                protected TipoBaseImponibleYCuota tipo2;
                @XmlElement(name = "Tipo4")
                protected TipoBaseImponibleYCuota tipo4;
                @XmlElement(name = "Tipo5")
                protected TipoBaseImponibleYCuota tipo5;
                @XmlElement(name = "Tipo75")
                protected TipoBaseImponibleYCuota tipo75;
                @XmlElement(name = "Tipo10")
                protected TipoBaseImponibleYCuota tipo10;
                @XmlElement(name = "Tipo21")
                protected TipoBaseImponibleYCuota tipo21;
                @XmlElement(name = "Total", required = true)
                protected TipoBaseImponibleYCuota total;

                /**
                 * Obtiene el valor de la propiedad tipo2.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo2() {
                    return tipo2;
                }

                /**
                 * Define el valor de la propiedad tipo2.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo2(TipoBaseImponibleYCuota value) {
                    this.tipo2 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo4.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo4() {
                    return tipo4;
                }

                /**
                 * Define el valor de la propiedad tipo4.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo4(TipoBaseImponibleYCuota value) {
                    this.tipo4 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo5.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo5() {
                    return tipo5;
                }

                /**
                 * Define el valor de la propiedad tipo5.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo5(TipoBaseImponibleYCuota value) {
                    this.tipo5 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo75.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo75() {
                    return tipo75;
                }

                /**
                 * Define el valor de la propiedad tipo75.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo75(TipoBaseImponibleYCuota value) {
                    this.tipo75 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo10.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo10() {
                    return tipo10;
                }

                /**
                 * Define el valor de la propiedad tipo10.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo10(TipoBaseImponibleYCuota value) {
                    this.tipo10 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo21.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo21() {
                    return tipo21;
                }

                /**
                 * Define el valor de la propiedad tipo21.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo21(TipoBaseImponibleYCuota value) {
                    this.tipo21 = value;
                }

                /**
                 * Obtiene el valor de la propiedad total.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTotal() {
                    return total;
                }

                /**
                 * Define el valor de la propiedad total.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTotal(TipoBaseImponibleYCuota value) {
                    this.total = value;
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
             *         &lt;element name="Tipo2" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo4" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo5" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo75" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo10" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo21" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Total" type="{}tipo_BaseImponible_y_Cuota"/&gt;
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
                "tipo2",
                "tipo4",
                "tipo5",
                "tipo75",
                "tipo10",
                "tipo21",
                "total"
            })
            public static class OpInterioresBienesInversion {

                @XmlElement(name = "Tipo2")
                protected TipoBaseImponibleYCuota tipo2;
                @XmlElement(name = "Tipo4")
                protected TipoBaseImponibleYCuota tipo4;
                @XmlElement(name = "Tipo5")
                protected TipoBaseImponibleYCuota tipo5;
                @XmlElement(name = "Tipo75")
                protected TipoBaseImponibleYCuota tipo75;
                @XmlElement(name = "Tipo10")
                protected TipoBaseImponibleYCuota tipo10;
                @XmlElement(name = "Tipo21")
                protected TipoBaseImponibleYCuota tipo21;
                @XmlElement(name = "Total", required = true)
                protected TipoBaseImponibleYCuota total;

                /**
                 * Obtiene el valor de la propiedad tipo2.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo2() {
                    return tipo2;
                }

                /**
                 * Define el valor de la propiedad tipo2.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo2(TipoBaseImponibleYCuota value) {
                    this.tipo2 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo4.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo4() {
                    return tipo4;
                }

                /**
                 * Define el valor de la propiedad tipo4.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo4(TipoBaseImponibleYCuota value) {
                    this.tipo4 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo5.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo5() {
                    return tipo5;
                }

                /**
                 * Define el valor de la propiedad tipo5.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo5(TipoBaseImponibleYCuota value) {
                    this.tipo5 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo75.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo75() {
                    return tipo75;
                }

                /**
                 * Define el valor de la propiedad tipo75.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo75(TipoBaseImponibleYCuota value) {
                    this.tipo75 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo10.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo10() {
                    return tipo10;
                }

                /**
                 * Define el valor de la propiedad tipo10.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo10(TipoBaseImponibleYCuota value) {
                    this.tipo10 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo21.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo21() {
                    return tipo21;
                }

                /**
                 * Define el valor de la propiedad tipo21.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo21(TipoBaseImponibleYCuota value) {
                    this.tipo21 = value;
                }

                /**
                 * Obtiene el valor de la propiedad total.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTotal() {
                    return total;
                }

                /**
                 * Define el valor de la propiedad total.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTotal(TipoBaseImponibleYCuota value) {
                    this.total = value;
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
             *         &lt;element name="Tipo2" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo4" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo5" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo75" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo10" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo21" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Total" type="{}tipo_BaseImponible_y_Cuota"/&gt;
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
                "tipo2",
                "tipo4",
                "tipo5",
                "tipo75",
                "tipo10",
                "tipo21",
                "total"
            })
            public static class OpInterioresBienesServiciosCorrientes {

                @XmlElement(name = "Tipo2")
                protected TipoBaseImponibleYCuota tipo2;
                @XmlElement(name = "Tipo4")
                protected TipoBaseImponibleYCuota tipo4;
                @XmlElement(name = "Tipo5")
                protected TipoBaseImponibleYCuota tipo5;
                @XmlElement(name = "Tipo75")
                protected TipoBaseImponibleYCuota tipo75;
                @XmlElement(name = "Tipo10")
                protected TipoBaseImponibleYCuota tipo10;
                @XmlElement(name = "Tipo21")
                protected TipoBaseImponibleYCuota tipo21;
                @XmlElement(name = "Total", required = true)
                protected TipoBaseImponibleYCuota total;

                /**
                 * Obtiene el valor de la propiedad tipo2.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo2() {
                    return tipo2;
                }

                /**
                 * Define el valor de la propiedad tipo2.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo2(TipoBaseImponibleYCuota value) {
                    this.tipo2 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo4.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo4() {
                    return tipo4;
                }

                /**
                 * Define el valor de la propiedad tipo4.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo4(TipoBaseImponibleYCuota value) {
                    this.tipo4 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo5.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo5() {
                    return tipo5;
                }

                /**
                 * Define el valor de la propiedad tipo5.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo5(TipoBaseImponibleYCuota value) {
                    this.tipo5 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo75.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo75() {
                    return tipo75;
                }

                /**
                 * Define el valor de la propiedad tipo75.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo75(TipoBaseImponibleYCuota value) {
                    this.tipo75 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo10.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo10() {
                    return tipo10;
                }

                /**
                 * Define el valor de la propiedad tipo10.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo10(TipoBaseImponibleYCuota value) {
                    this.tipo10 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo21.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo21() {
                    return tipo21;
                }

                /**
                 * Define el valor de la propiedad tipo21.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo21(TipoBaseImponibleYCuota value) {
                    this.tipo21 = value;
                }

                /**
                 * Obtiene el valor de la propiedad total.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTotal() {
                    return total;
                }

                /**
                 * Define el valor de la propiedad total.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTotal(TipoBaseImponibleYCuota value) {
                    this.total = value;
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
             *         &lt;element name="Tipo2" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo4" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo5" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo75" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo10" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo21" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Total" type="{}tipo_BaseImponible_y_Cuota"/&gt;
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
                "tipo2",
                "tipo4",
                "tipo5",
                "tipo75",
                "tipo10",
                "tipo21",
                "total"
            })
            public static class OpIntragrupoBienesInversion {

                @XmlElement(name = "Tipo2")
                protected TipoBaseImponibleYCuota tipo2;
                @XmlElement(name = "Tipo4")
                protected TipoBaseImponibleYCuota tipo4;
                @XmlElement(name = "Tipo5")
                protected TipoBaseImponibleYCuota tipo5;
                @XmlElement(name = "Tipo75")
                protected TipoBaseImponibleYCuota tipo75;
                @XmlElement(name = "Tipo10")
                protected TipoBaseImponibleYCuota tipo10;
                @XmlElement(name = "Tipo21")
                protected TipoBaseImponibleYCuota tipo21;
                @XmlElement(name = "Total", required = true)
                protected TipoBaseImponibleYCuota total;

                /**
                 * Obtiene el valor de la propiedad tipo2.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo2() {
                    return tipo2;
                }

                /**
                 * Define el valor de la propiedad tipo2.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo2(TipoBaseImponibleYCuota value) {
                    this.tipo2 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo4.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo4() {
                    return tipo4;
                }

                /**
                 * Define el valor de la propiedad tipo4.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo4(TipoBaseImponibleYCuota value) {
                    this.tipo4 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo5.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo5() {
                    return tipo5;
                }

                /**
                 * Define el valor de la propiedad tipo5.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo5(TipoBaseImponibleYCuota value) {
                    this.tipo5 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo75.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo75() {
                    return tipo75;
                }

                /**
                 * Define el valor de la propiedad tipo75.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo75(TipoBaseImponibleYCuota value) {
                    this.tipo75 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo10.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo10() {
                    return tipo10;
                }

                /**
                 * Define el valor de la propiedad tipo10.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo10(TipoBaseImponibleYCuota value) {
                    this.tipo10 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo21.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo21() {
                    return tipo21;
                }

                /**
                 * Define el valor de la propiedad tipo21.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo21(TipoBaseImponibleYCuota value) {
                    this.tipo21 = value;
                }

                /**
                 * Obtiene el valor de la propiedad total.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTotal() {
                    return total;
                }

                /**
                 * Define el valor de la propiedad total.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTotal(TipoBaseImponibleYCuota value) {
                    this.total = value;
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
             *         &lt;element name="Tipo2" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo4" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo5" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo75" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo10" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Tipo21" type="{}tipo_BaseImponible_y_Cuota" minOccurs="0"/&gt;
             *         &lt;element name="Total" type="{}tipo_BaseImponible_y_Cuota"/&gt;
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
                "tipo2",
                "tipo4",
                "tipo5",
                "tipo75",
                "tipo10",
                "tipo21",
                "total"
            })
            public static class OpIntragrupoCorrientes {

                @XmlElement(name = "Tipo2")
                protected TipoBaseImponibleYCuota tipo2;
                @XmlElement(name = "Tipo4")
                protected TipoBaseImponibleYCuota tipo4;
                @XmlElement(name = "Tipo5")
                protected TipoBaseImponibleYCuota tipo5;
                @XmlElement(name = "Tipo75")
                protected TipoBaseImponibleYCuota tipo75;
                @XmlElement(name = "Tipo10")
                protected TipoBaseImponibleYCuota tipo10;
                @XmlElement(name = "Tipo21")
                protected TipoBaseImponibleYCuota tipo21;
                @XmlElement(name = "Total", required = true)
                protected TipoBaseImponibleYCuota total;

                /**
                 * Obtiene el valor de la propiedad tipo2.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo2() {
                    return tipo2;
                }

                /**
                 * Define el valor de la propiedad tipo2.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo2(TipoBaseImponibleYCuota value) {
                    this.tipo2 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo4.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo4() {
                    return tipo4;
                }

                /**
                 * Define el valor de la propiedad tipo4.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo4(TipoBaseImponibleYCuota value) {
                    this.tipo4 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo5.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo5() {
                    return tipo5;
                }

                /**
                 * Define el valor de la propiedad tipo5.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo5(TipoBaseImponibleYCuota value) {
                    this.tipo5 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo75.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo75() {
                    return tipo75;
                }

                /**
                 * Define el valor de la propiedad tipo75.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo75(TipoBaseImponibleYCuota value) {
                    this.tipo75 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo10.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo10() {
                    return tipo10;
                }

                /**
                 * Define el valor de la propiedad tipo10.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo10(TipoBaseImponibleYCuota value) {
                    this.tipo10 = value;
                }

                /**
                 * Obtiene el valor de la propiedad tipo21.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipo21() {
                    return tipo21;
                }

                /**
                 * Define el valor de la propiedad tipo21.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipo21(TipoBaseImponibleYCuota value) {
                    this.tipo21 = value;
                }

                /**
                 * Obtiene el valor de la propiedad total.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTotal() {
                    return total;
                }

                /**
                 * Define el valor de la propiedad total.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTotal(TipoBaseImponibleYCuota value) {
                    this.total = value;
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
             *         &lt;element name="TipoX" type="{}tipo_BaseImponible_y_Cuota"/&gt;
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
                "tipoX"
            })
            public static class RectifDeducciones {

                @XmlElement(name = "TipoX", required = true)
                protected TipoBaseImponibleYCuota tipoX;

                /**
                 * Obtiene el valor de la propiedad tipoX.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipoX() {
                    return tipoX;
                }

                /**
                 * Define el valor de la propiedad tipoX.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipoX(TipoBaseImponibleYCuota value) {
                    this.tipoX = value;
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
             *         &lt;element name="TipoX" type="{}tipo_BaseImponible_y_Cuota"/&gt;
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
                "tipoX"
            })
            public static class RectifOpIntragrupo {

                @XmlElement(name = "TipoX", required = true)
                protected TipoBaseImponibleYCuota tipoX;

                /**
                 * Obtiene el valor de la propiedad tipoX.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public TipoBaseImponibleYCuota getTipoX() {
                    return tipoX;
                }

                /**
                 * Define el valor de la propiedad tipoX.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TipoBaseImponibleYCuota }
                 *     
                 */
                public void setTipoX(TipoBaseImponibleYCuota value) {
                    this.tipoX = value;
                }

            }

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
     *         &lt;element name="Actividad" maxOccurs="6" minOccurs="0"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;sequence&gt;
     *                   &lt;element name="Epigrafe" type="{}tipo_Epigrafe"/&gt;
     *                   &lt;element name="Modulo" maxOccurs="7" minOccurs="0"&gt;
     *                     &lt;complexType&gt;
     *                       &lt;complexContent&gt;
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                           &lt;sequence&gt;
     *                             &lt;element name="NumModulo" type="{}tipo_NumModulo"/&gt;
     *                             &lt;element name="Unidades" type="{}tipo_ImpPositivo"/&gt;
     *                             &lt;element name="Importe" type="{}tipo_ImpPositivo"/&gt;
     *                           &lt;/sequence&gt;
     *                         &lt;/restriction&gt;
     *                       &lt;/complexContent&gt;
     *                     &lt;/complexType&gt;
     *                   &lt;/element&gt;
     *                   &lt;element name="CuotaDevengada" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
     *                   &lt;element name="Lorca" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
     *                   &lt;element name="CuotaSoportada" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
     *                   &lt;element name="IndiceCorrector" type="{}tipo_IndiceCorrector" minOccurs="0"/&gt;
     *                   &lt;element name="Resultado" type="{}tipo_ImpNegativo" minOccurs="0"/&gt;
     *                   &lt;element name="PorcCuotaMinima" type="{}tipo_Porcentaje" minOccurs="0"/&gt;
     *                   &lt;element name="DevCuotaSopOtrosPaises" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
     *                   &lt;element name="CuotaMinima" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
     *                   &lt;element name="CuotaRegSimplificado" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
     *                   &lt;element name="IndicadorAuxiliar" type="{}tipo_IndicadorAuxiliar" minOccurs="0"/&gt;
     *                 &lt;/sequence&gt;
     *               &lt;/restriction&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *         &lt;element name="ActAgricGanadForest" maxOccurs="5" minOccurs="0"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;sequence minOccurs="0"&gt;
     *                   &lt;element name="Codigo" type="{}tipo_AgrariasCod"/&gt;
     *                   &lt;element name="VolIngresos" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
     *                   &lt;element name="IndCuota" type="{}tipo_InCuota" minOccurs="0"/&gt;
     *                   &lt;element name="CuotaDevengada" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
     *                   &lt;element name="CuotasSoportadas" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
     *                   &lt;element name="CuotaRegSimplificado" type="{}tipo_ImpNegativo" minOccurs="0"/&gt;
     *                 &lt;/sequence&gt;
     *               &lt;/restriction&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *         &lt;element name="IvaDevengado" minOccurs="0"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;sequence&gt;
     *                   &lt;element name="SumaCuotasNoAgric" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
     *                   &lt;element name="SumaCuotasAgric" type="{}tipo_ImpNegativo" minOccurs="0"/&gt;
     *                   &lt;element name="AdqIntracomunitarias" type="{}tipo_ImpNegativo" minOccurs="0"/&gt;
     *                   &lt;element name="InversionSujetoPasivo" type="{}tipo_ImpNegativo" minOccurs="0"/&gt;
     *                   &lt;element name="EntregasActivosFijos" type="{}tipo_ImpNegativo" minOccurs="0"/&gt;
     *                   &lt;element name="TotalCuota" type="{}tipo_ImpNegativo"/&gt;
     *                 &lt;/sequence&gt;
     *               &lt;/restriction&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *         &lt;element name="IvaDeducible" minOccurs="0"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;sequence&gt;
     *                   &lt;element name="IVASoportadoAdqActivosFijos" type="{}tipo_ImpNegativo" minOccurs="0"/&gt;
     *                   &lt;element name="RegBienesInversion" type="{}tipo_ImpNegativo" minOccurs="0"/&gt;
     *                   &lt;element name="SumaDeducciones" type="{}tipo_ImpNegativo"/&gt;
     *                 &lt;/sequence&gt;
     *               &lt;/restriction&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *         &lt;element name="ResRegimenSimplificado" type="{}tipo_ImpNegativo" minOccurs="0"/&gt;
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
        "actividad",
        "actAgricGanadForest",
        "ivaDevengado",
        "ivaDeducible",
        "resRegimenSimplificado"
    })
    public static class RegSimplificado {

        @XmlElement(name = "Actividad")
        protected List<AEATIVA2024 .RegSimplificado.Actividad> actividad;
        @XmlElement(name = "ActAgricGanadForest")
        protected List<AEATIVA2024 .RegSimplificado.ActAgricGanadForest> actAgricGanadForest;
        @XmlElement(name = "IvaDevengado")
        protected AEATIVA2024 .RegSimplificado.IvaDevengado ivaDevengado;
        @XmlElement(name = "IvaDeducible")
        protected AEATIVA2024 .RegSimplificado.IvaDeducible ivaDeducible;
        @XmlElement(name = "ResRegimenSimplificado")
        protected BigDecimal resRegimenSimplificado;

        /**
         * Gets the value of the actividad property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the actividad property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getActividad().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link AEATIVA2024 .RegSimplificado.Actividad }
         * 
         * 
         */
        public List<AEATIVA2024 .RegSimplificado.Actividad> getActividad() {
            if (actividad == null) {
                actividad = new ArrayList<AEATIVA2024 .RegSimplificado.Actividad>();
            }
            return this.actividad;
        }

        /**
         * Gets the value of the actAgricGanadForest property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the actAgricGanadForest property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getActAgricGanadForest().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link AEATIVA2024 .RegSimplificado.ActAgricGanadForest }
         * 
         * 
         */
        public List<AEATIVA2024 .RegSimplificado.ActAgricGanadForest> getActAgricGanadForest() {
            if (actAgricGanadForest == null) {
                actAgricGanadForest = new ArrayList<AEATIVA2024 .RegSimplificado.ActAgricGanadForest>();
            }
            return this.actAgricGanadForest;
        }

        /**
         * Obtiene el valor de la propiedad ivaDevengado.
         * 
         * @return
         *     possible object is
         *     {@link AEATIVA2024 .RegSimplificado.IvaDevengado }
         *     
         */
        public AEATIVA2024 .RegSimplificado.IvaDevengado getIvaDevengado() {
            return ivaDevengado;
        }

        /**
         * Define el valor de la propiedad ivaDevengado.
         * 
         * @param value
         *     allowed object is
         *     {@link AEATIVA2024 .RegSimplificado.IvaDevengado }
         *     
         */
        public void setIvaDevengado(AEATIVA2024 .RegSimplificado.IvaDevengado value) {
            this.ivaDevengado = value;
        }

        /**
         * Obtiene el valor de la propiedad ivaDeducible.
         * 
         * @return
         *     possible object is
         *     {@link AEATIVA2024 .RegSimplificado.IvaDeducible }
         *     
         */
        public AEATIVA2024 .RegSimplificado.IvaDeducible getIvaDeducible() {
            return ivaDeducible;
        }

        /**
         * Define el valor de la propiedad ivaDeducible.
         * 
         * @param value
         *     allowed object is
         *     {@link AEATIVA2024 .RegSimplificado.IvaDeducible }
         *     
         */
        public void setIvaDeducible(AEATIVA2024 .RegSimplificado.IvaDeducible value) {
            this.ivaDeducible = value;
        }

        /**
         * Obtiene el valor de la propiedad resRegimenSimplificado.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getResRegimenSimplificado() {
            return resRegimenSimplificado;
        }

        /**
         * Define el valor de la propiedad resRegimenSimplificado.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setResRegimenSimplificado(BigDecimal value) {
            this.resRegimenSimplificado = value;
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
         *       &lt;sequence minOccurs="0"&gt;
         *         &lt;element name="Codigo" type="{}tipo_AgrariasCod"/&gt;
         *         &lt;element name="VolIngresos" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
         *         &lt;element name="IndCuota" type="{}tipo_InCuota" minOccurs="0"/&gt;
         *         &lt;element name="CuotaDevengada" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
         *         &lt;element name="CuotasSoportadas" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
         *         &lt;element name="CuotaRegSimplificado" type="{}tipo_ImpNegativo" minOccurs="0"/&gt;
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
            "codigo",
            "volIngresos",
            "indCuota",
            "cuotaDevengada",
            "cuotasSoportadas",
            "cuotaRegSimplificado"
        })
        public static class ActAgricGanadForest {

            @XmlElement(name = "Codigo")
            protected String codigo;
            @XmlElement(name = "VolIngresos")
            protected BigDecimal volIngresos;
            @XmlElement(name = "IndCuota")
            protected BigDecimal indCuota;
            @XmlElement(name = "CuotaDevengada")
            protected BigDecimal cuotaDevengada;
            @XmlElement(name = "CuotasSoportadas")
            protected BigDecimal cuotasSoportadas;
            @XmlElement(name = "CuotaRegSimplificado")
            protected BigDecimal cuotaRegSimplificado;

            /**
             * Obtiene el valor de la propiedad codigo.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getCodigo() {
                return codigo;
            }

            /**
             * Define el valor de la propiedad codigo.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setCodigo(String value) {
                this.codigo = value;
            }

            /**
             * Obtiene el valor de la propiedad volIngresos.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getVolIngresos() {
                return volIngresos;
            }

            /**
             * Define el valor de la propiedad volIngresos.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setVolIngresos(BigDecimal value) {
                this.volIngresos = value;
            }

            /**
             * Obtiene el valor de la propiedad indCuota.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getIndCuota() {
                return indCuota;
            }

            /**
             * Define el valor de la propiedad indCuota.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setIndCuota(BigDecimal value) {
                this.indCuota = value;
            }

            /**
             * Obtiene el valor de la propiedad cuotaDevengada.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getCuotaDevengada() {
                return cuotaDevengada;
            }

            /**
             * Define el valor de la propiedad cuotaDevengada.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setCuotaDevengada(BigDecimal value) {
                this.cuotaDevengada = value;
            }

            /**
             * Obtiene el valor de la propiedad cuotasSoportadas.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getCuotasSoportadas() {
                return cuotasSoportadas;
            }

            /**
             * Define el valor de la propiedad cuotasSoportadas.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setCuotasSoportadas(BigDecimal value) {
                this.cuotasSoportadas = value;
            }

            /**
             * Obtiene el valor de la propiedad cuotaRegSimplificado.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getCuotaRegSimplificado() {
                return cuotaRegSimplificado;
            }

            /**
             * Define el valor de la propiedad cuotaRegSimplificado.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setCuotaRegSimplificado(BigDecimal value) {
                this.cuotaRegSimplificado = value;
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
         *         &lt;element name="Epigrafe" type="{}tipo_Epigrafe"/&gt;
         *         &lt;element name="Modulo" maxOccurs="7" minOccurs="0"&gt;
         *           &lt;complexType&gt;
         *             &lt;complexContent&gt;
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *                 &lt;sequence&gt;
         *                   &lt;element name="NumModulo" type="{}tipo_NumModulo"/&gt;
         *                   &lt;element name="Unidades" type="{}tipo_ImpPositivo"/&gt;
         *                   &lt;element name="Importe" type="{}tipo_ImpPositivo"/&gt;
         *                 &lt;/sequence&gt;
         *               &lt;/restriction&gt;
         *             &lt;/complexContent&gt;
         *           &lt;/complexType&gt;
         *         &lt;/element&gt;
         *         &lt;element name="CuotaDevengada" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
         *         &lt;element name="Lorca" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
         *         &lt;element name="CuotaSoportada" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
         *         &lt;element name="IndiceCorrector" type="{}tipo_IndiceCorrector" minOccurs="0"/&gt;
         *         &lt;element name="Resultado" type="{}tipo_ImpNegativo" minOccurs="0"/&gt;
         *         &lt;element name="PorcCuotaMinima" type="{}tipo_Porcentaje" minOccurs="0"/&gt;
         *         &lt;element name="DevCuotaSopOtrosPaises" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
         *         &lt;element name="CuotaMinima" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
         *         &lt;element name="CuotaRegSimplificado" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
         *         &lt;element name="IndicadorAuxiliar" type="{}tipo_IndicadorAuxiliar" minOccurs="0"/&gt;
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
            "epigrafe",
            "modulo",
            "cuotaDevengada",
            "lorca",
            "cuotaSoportada",
            "indiceCorrector",
            "resultado",
            "porcCuotaMinima",
            "devCuotaSopOtrosPaises",
            "cuotaMinima",
            "cuotaRegSimplificado",
            "indicadorAuxiliar"
        })
        public static class Actividad {

            @XmlElement(name = "Epigrafe", required = true)
            protected String epigrafe;
            @XmlElement(name = "Modulo")
            protected List<AEATIVA2024 .RegSimplificado.Actividad.Modulo> modulo;
            @XmlElement(name = "CuotaDevengada")
            protected BigDecimal cuotaDevengada;
            @XmlElement(name = "Lorca")
            protected BigDecimal lorca;
            @XmlElement(name = "CuotaSoportada")
            protected BigDecimal cuotaSoportada;
            @XmlElement(name = "IndiceCorrector")
            protected BigDecimal indiceCorrector;
            @XmlElement(name = "Resultado")
            protected BigDecimal resultado;
            @XmlElement(name = "PorcCuotaMinima")
            protected BigDecimal porcCuotaMinima;
            @XmlElement(name = "DevCuotaSopOtrosPaises")
            protected BigDecimal devCuotaSopOtrosPaises;
            @XmlElement(name = "CuotaMinima")
            protected BigDecimal cuotaMinima;
            @XmlElement(name = "CuotaRegSimplificado")
            protected BigDecimal cuotaRegSimplificado;
            @XmlElement(name = "IndicadorAuxiliar")
            protected String indicadorAuxiliar;

            /**
             * Obtiene el valor de la propiedad epigrafe.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getEpigrafe() {
                return epigrafe;
            }

            /**
             * Define el valor de la propiedad epigrafe.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setEpigrafe(String value) {
                this.epigrafe = value;
            }

            /**
             * Gets the value of the modulo property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the modulo property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getModulo().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link AEATIVA2024 .RegSimplificado.Actividad.Modulo }
             * 
             * 
             */
            public List<AEATIVA2024 .RegSimplificado.Actividad.Modulo> getModulo() {
                if (modulo == null) {
                    modulo = new ArrayList<AEATIVA2024 .RegSimplificado.Actividad.Modulo>();
                }
                return this.modulo;
            }

            /**
             * Obtiene el valor de la propiedad cuotaDevengada.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getCuotaDevengada() {
                return cuotaDevengada;
            }

            /**
             * Define el valor de la propiedad cuotaDevengada.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setCuotaDevengada(BigDecimal value) {
                this.cuotaDevengada = value;
            }

            /**
             * Obtiene el valor de la propiedad lorca.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getLorca() {
                return lorca;
            }

            /**
             * Define el valor de la propiedad lorca.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setLorca(BigDecimal value) {
                this.lorca = value;
            }

            /**
             * Obtiene el valor de la propiedad cuotaSoportada.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getCuotaSoportada() {
                return cuotaSoportada;
            }

            /**
             * Define el valor de la propiedad cuotaSoportada.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setCuotaSoportada(BigDecimal value) {
                this.cuotaSoportada = value;
            }

            /**
             * Obtiene el valor de la propiedad indiceCorrector.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getIndiceCorrector() {
                return indiceCorrector;
            }

            /**
             * Define el valor de la propiedad indiceCorrector.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setIndiceCorrector(BigDecimal value) {
                this.indiceCorrector = value;
            }

            /**
             * Obtiene el valor de la propiedad resultado.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getResultado() {
                return resultado;
            }

            /**
             * Define el valor de la propiedad resultado.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setResultado(BigDecimal value) {
                this.resultado = value;
            }

            /**
             * Obtiene el valor de la propiedad porcCuotaMinima.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getPorcCuotaMinima() {
                return porcCuotaMinima;
            }

            /**
             * Define el valor de la propiedad porcCuotaMinima.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setPorcCuotaMinima(BigDecimal value) {
                this.porcCuotaMinima = value;
            }

            /**
             * Obtiene el valor de la propiedad devCuotaSopOtrosPaises.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getDevCuotaSopOtrosPaises() {
                return devCuotaSopOtrosPaises;
            }

            /**
             * Define el valor de la propiedad devCuotaSopOtrosPaises.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setDevCuotaSopOtrosPaises(BigDecimal value) {
                this.devCuotaSopOtrosPaises = value;
            }

            /**
             * Obtiene el valor de la propiedad cuotaMinima.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getCuotaMinima() {
                return cuotaMinima;
            }

            /**
             * Define el valor de la propiedad cuotaMinima.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setCuotaMinima(BigDecimal value) {
                this.cuotaMinima = value;
            }

            /**
             * Obtiene el valor de la propiedad cuotaRegSimplificado.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getCuotaRegSimplificado() {
                return cuotaRegSimplificado;
            }

            /**
             * Define el valor de la propiedad cuotaRegSimplificado.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setCuotaRegSimplificado(BigDecimal value) {
                this.cuotaRegSimplificado = value;
            }

            /**
             * Obtiene el valor de la propiedad indicadorAuxiliar.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getIndicadorAuxiliar() {
                return indicadorAuxiliar;
            }

            /**
             * Define el valor de la propiedad indicadorAuxiliar.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setIndicadorAuxiliar(String value) {
                this.indicadorAuxiliar = value;
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
             *         &lt;element name="NumModulo" type="{}tipo_NumModulo"/&gt;
             *         &lt;element name="Unidades" type="{}tipo_ImpPositivo"/&gt;
             *         &lt;element name="Importe" type="{}tipo_ImpPositivo"/&gt;
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
                "numModulo",
                "unidades",
                "importe"
            })
            public static class Modulo {

                @XmlElement(name = "NumModulo", required = true)
                protected String numModulo;
                @XmlElement(name = "Unidades", required = true)
                protected BigDecimal unidades;
                @XmlElement(name = "Importe", required = true)
                protected BigDecimal importe;

                /**
                 * Obtiene el valor de la propiedad numModulo.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getNumModulo() {
                    return numModulo;
                }

                /**
                 * Define el valor de la propiedad numModulo.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setNumModulo(String value) {
                    this.numModulo = value;
                }

                /**
                 * Obtiene el valor de la propiedad unidades.
                 * 
                 * @return
                 *     possible object is
                 *     {@link BigDecimal }
                 *     
                 */
                public BigDecimal getUnidades() {
                    return unidades;
                }

                /**
                 * Define el valor de la propiedad unidades.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link BigDecimal }
                 *     
                 */
                public void setUnidades(BigDecimal value) {
                    this.unidades = value;
                }

                /**
                 * Obtiene el valor de la propiedad importe.
                 * 
                 * @return
                 *     possible object is
                 *     {@link BigDecimal }
                 *     
                 */
                public BigDecimal getImporte() {
                    return importe;
                }

                /**
                 * Define el valor de la propiedad importe.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link BigDecimal }
                 *     
                 */
                public void setImporte(BigDecimal value) {
                    this.importe = value;
                }

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
         *         &lt;element name="IVASoportadoAdqActivosFijos" type="{}tipo_ImpNegativo" minOccurs="0"/&gt;
         *         &lt;element name="RegBienesInversion" type="{}tipo_ImpNegativo" minOccurs="0"/&gt;
         *         &lt;element name="SumaDeducciones" type="{}tipo_ImpNegativo"/&gt;
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
            "ivaSoportadoAdqActivosFijos",
            "regBienesInversion",
            "sumaDeducciones"
        })
        public static class IvaDeducible {

            @XmlElement(name = "IVASoportadoAdqActivosFijos")
            protected BigDecimal ivaSoportadoAdqActivosFijos;
            @XmlElement(name = "RegBienesInversion")
            protected BigDecimal regBienesInversion;
            @XmlElement(name = "SumaDeducciones", required = true)
            protected BigDecimal sumaDeducciones;

            /**
             * Obtiene el valor de la propiedad ivaSoportadoAdqActivosFijos.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getIVASoportadoAdqActivosFijos() {
                return ivaSoportadoAdqActivosFijos;
            }

            /**
             * Define el valor de la propiedad ivaSoportadoAdqActivosFijos.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setIVASoportadoAdqActivosFijos(BigDecimal value) {
                this.ivaSoportadoAdqActivosFijos = value;
            }

            /**
             * Obtiene el valor de la propiedad regBienesInversion.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getRegBienesInversion() {
                return regBienesInversion;
            }

            /**
             * Define el valor de la propiedad regBienesInversion.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setRegBienesInversion(BigDecimal value) {
                this.regBienesInversion = value;
            }

            /**
             * Obtiene el valor de la propiedad sumaDeducciones.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getSumaDeducciones() {
                return sumaDeducciones;
            }

            /**
             * Define el valor de la propiedad sumaDeducciones.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setSumaDeducciones(BigDecimal value) {
                this.sumaDeducciones = value;
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
         *         &lt;element name="SumaCuotasNoAgric" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
         *         &lt;element name="SumaCuotasAgric" type="{}tipo_ImpNegativo" minOccurs="0"/&gt;
         *         &lt;element name="AdqIntracomunitarias" type="{}tipo_ImpNegativo" minOccurs="0"/&gt;
         *         &lt;element name="InversionSujetoPasivo" type="{}tipo_ImpNegativo" minOccurs="0"/&gt;
         *         &lt;element name="EntregasActivosFijos" type="{}tipo_ImpNegativo" minOccurs="0"/&gt;
         *         &lt;element name="TotalCuota" type="{}tipo_ImpNegativo"/&gt;
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
            "sumaCuotasNoAgric",
            "sumaCuotasAgric",
            "adqIntracomunitarias",
            "inversionSujetoPasivo",
            "entregasActivosFijos",
            "totalCuota"
        })
        public static class IvaDevengado {

            @XmlElement(name = "SumaCuotasNoAgric")
            protected BigDecimal sumaCuotasNoAgric;
            @XmlElement(name = "SumaCuotasAgric")
            protected BigDecimal sumaCuotasAgric;
            @XmlElement(name = "AdqIntracomunitarias")
            protected BigDecimal adqIntracomunitarias;
            @XmlElement(name = "InversionSujetoPasivo")
            protected BigDecimal inversionSujetoPasivo;
            @XmlElement(name = "EntregasActivosFijos")
            protected BigDecimal entregasActivosFijos;
            @XmlElement(name = "TotalCuota", required = true)
            protected BigDecimal totalCuota;

            /**
             * Obtiene el valor de la propiedad sumaCuotasNoAgric.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getSumaCuotasNoAgric() {
                return sumaCuotasNoAgric;
            }

            /**
             * Define el valor de la propiedad sumaCuotasNoAgric.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setSumaCuotasNoAgric(BigDecimal value) {
                this.sumaCuotasNoAgric = value;
            }

            /**
             * Obtiene el valor de la propiedad sumaCuotasAgric.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getSumaCuotasAgric() {
                return sumaCuotasAgric;
            }

            /**
             * Define el valor de la propiedad sumaCuotasAgric.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setSumaCuotasAgric(BigDecimal value) {
                this.sumaCuotasAgric = value;
            }

            /**
             * Obtiene el valor de la propiedad adqIntracomunitarias.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getAdqIntracomunitarias() {
                return adqIntracomunitarias;
            }

            /**
             * Define el valor de la propiedad adqIntracomunitarias.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setAdqIntracomunitarias(BigDecimal value) {
                this.adqIntracomunitarias = value;
            }

            /**
             * Obtiene el valor de la propiedad inversionSujetoPasivo.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getInversionSujetoPasivo() {
                return inversionSujetoPasivo;
            }

            /**
             * Define el valor de la propiedad inversionSujetoPasivo.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setInversionSujetoPasivo(BigDecimal value) {
                this.inversionSujetoPasivo = value;
            }

            /**
             * Obtiene el valor de la propiedad entregasActivosFijos.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getEntregasActivosFijos() {
                return entregasActivosFijos;
            }

            /**
             * Define el valor de la propiedad entregasActivosFijos.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setEntregasActivosFijos(BigDecimal value) {
                this.entregasActivosFijos = value;
            }

            /**
             * Obtiene el valor de la propiedad totalCuota.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getTotalCuota() {
                return totalCuota;
            }

            /**
             * Define el valor de la propiedad totalCuota.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setTotalCuota(BigDecimal value) {
                this.totalCuota = value;
            }

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
     *         &lt;element name="PerNoRegGrupos" minOccurs="0"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;sequence&gt;
     *                   &lt;element name="TotIngresosIVA" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
     *                   &lt;element name="TotDevIVA_SP_RegDevMensual" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
     *                   &lt;element name="ExclusionBaja" minOccurs="0"&gt;
     *                     &lt;complexType&gt;
     *                       &lt;complexContent&gt;
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                         &lt;/restriction&gt;
     *                       &lt;/complexContent&gt;
     *                     &lt;/complexType&gt;
     *                   &lt;/element&gt;
     *                   &lt;element name="TotDevAdqElemTrans" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
     *                   &lt;element name="ImporteACompensarUltimoPeriodo" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
     *                   &lt;element name="ImporteADevolverUltimoPeriodo" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
     *                   &lt;element name="CuotasPendCompensar" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
     *                 &lt;/sequence&gt;
     *               &lt;/restriction&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *         &lt;element name="PerSiRegGrupos" minOccurs="0"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;sequence&gt;
     *                   &lt;element name="TotResulPositivos322" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
     *                   &lt;element name="TotResulNegativos322" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
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
        "perNoRegGrupos",
        "perSiRegGrupos"
    })
    public static class ResLiquidaciones {

        @XmlElement(name = "PerNoRegGrupos")
        protected AEATIVA2024 .ResLiquidaciones.PerNoRegGrupos perNoRegGrupos;
        @XmlElement(name = "PerSiRegGrupos")
        protected AEATIVA2024 .ResLiquidaciones.PerSiRegGrupos perSiRegGrupos;

        /**
         * Obtiene el valor de la propiedad perNoRegGrupos.
         * 
         * @return
         *     possible object is
         *     {@link AEATIVA2024 .ResLiquidaciones.PerNoRegGrupos }
         *     
         */
        public AEATIVA2024 .ResLiquidaciones.PerNoRegGrupos getPerNoRegGrupos() {
            return perNoRegGrupos;
        }

        /**
         * Define el valor de la propiedad perNoRegGrupos.
         * 
         * @param value
         *     allowed object is
         *     {@link AEATIVA2024 .ResLiquidaciones.PerNoRegGrupos }
         *     
         */
        public void setPerNoRegGrupos(AEATIVA2024 .ResLiquidaciones.PerNoRegGrupos value) {
            this.perNoRegGrupos = value;
        }

        /**
         * Obtiene el valor de la propiedad perSiRegGrupos.
         * 
         * @return
         *     possible object is
         *     {@link AEATIVA2024 .ResLiquidaciones.PerSiRegGrupos }
         *     
         */
        public AEATIVA2024 .ResLiquidaciones.PerSiRegGrupos getPerSiRegGrupos() {
            return perSiRegGrupos;
        }

        /**
         * Define el valor de la propiedad perSiRegGrupos.
         * 
         * @param value
         *     allowed object is
         *     {@link AEATIVA2024 .ResLiquidaciones.PerSiRegGrupos }
         *     
         */
        public void setPerSiRegGrupos(AEATIVA2024 .ResLiquidaciones.PerSiRegGrupos value) {
            this.perSiRegGrupos = value;
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
         *         &lt;element name="TotIngresosIVA" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
         *         &lt;element name="TotDevIVA_SP_RegDevMensual" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
         *         &lt;element name="ExclusionBaja" minOccurs="0"&gt;
         *           &lt;complexType&gt;
         *             &lt;complexContent&gt;
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *               &lt;/restriction&gt;
         *             &lt;/complexContent&gt;
         *           &lt;/complexType&gt;
         *         &lt;/element&gt;
         *         &lt;element name="TotDevAdqElemTrans" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
         *         &lt;element name="ImporteACompensarUltimoPeriodo" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
         *         &lt;element name="ImporteADevolverUltimoPeriodo" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
         *         &lt;element name="CuotasPendCompensar" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
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
            "totIngresosIVA",
            "totDevIVASPRegDevMensual",
            "exclusionBaja",
            "totDevAdqElemTrans",
            "importeACompensarUltimoPeriodo",
            "importeADevolverUltimoPeriodo",
            "cuotasPendCompensar"
        })
        public static class PerNoRegGrupos {

            @XmlElement(name = "TotIngresosIVA")
            protected BigDecimal totIngresosIVA;
            @XmlElement(name = "TotDevIVA_SP_RegDevMensual")
            protected BigDecimal totDevIVASPRegDevMensual;
            @XmlElement(name = "ExclusionBaja")
            protected AEATIVA2024 .ResLiquidaciones.PerNoRegGrupos.ExclusionBaja exclusionBaja;
            @XmlElement(name = "TotDevAdqElemTrans")
            protected BigDecimal totDevAdqElemTrans;
            @XmlElement(name = "ImporteACompensarUltimoPeriodo")
            protected BigDecimal importeACompensarUltimoPeriodo;
            @XmlElement(name = "ImporteADevolverUltimoPeriodo")
            protected BigDecimal importeADevolverUltimoPeriodo;
            @XmlElement(name = "CuotasPendCompensar")
            protected BigDecimal cuotasPendCompensar;

            /**
             * Obtiene el valor de la propiedad totIngresosIVA.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getTotIngresosIVA() {
                return totIngresosIVA;
            }

            /**
             * Define el valor de la propiedad totIngresosIVA.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setTotIngresosIVA(BigDecimal value) {
                this.totIngresosIVA = value;
            }

            /**
             * Obtiene el valor de la propiedad totDevIVASPRegDevMensual.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getTotDevIVASPRegDevMensual() {
                return totDevIVASPRegDevMensual;
            }

            /**
             * Define el valor de la propiedad totDevIVASPRegDevMensual.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setTotDevIVASPRegDevMensual(BigDecimal value) {
                this.totDevIVASPRegDevMensual = value;
            }

            /**
             * Obtiene el valor de la propiedad exclusionBaja.
             * 
             * @return
             *     possible object is
             *     {@link AEATIVA2024 .ResLiquidaciones.PerNoRegGrupos.ExclusionBaja }
             *     
             */
            public AEATIVA2024 .ResLiquidaciones.PerNoRegGrupos.ExclusionBaja getExclusionBaja() {
                return exclusionBaja;
            }

            /**
             * Define el valor de la propiedad exclusionBaja.
             * 
             * @param value
             *     allowed object is
             *     {@link AEATIVA2024 .ResLiquidaciones.PerNoRegGrupos.ExclusionBaja }
             *     
             */
            public void setExclusionBaja(AEATIVA2024 .ResLiquidaciones.PerNoRegGrupos.ExclusionBaja value) {
                this.exclusionBaja = value;
            }

            /**
             * Obtiene el valor de la propiedad totDevAdqElemTrans.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getTotDevAdqElemTrans() {
                return totDevAdqElemTrans;
            }

            /**
             * Define el valor de la propiedad totDevAdqElemTrans.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setTotDevAdqElemTrans(BigDecimal value) {
                this.totDevAdqElemTrans = value;
            }

            /**
             * Obtiene el valor de la propiedad importeACompensarUltimoPeriodo.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getImporteACompensarUltimoPeriodo() {
                return importeACompensarUltimoPeriodo;
            }

            /**
             * Define el valor de la propiedad importeACompensarUltimoPeriodo.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setImporteACompensarUltimoPeriodo(BigDecimal value) {
                this.importeACompensarUltimoPeriodo = value;
            }

            /**
             * Obtiene el valor de la propiedad importeADevolverUltimoPeriodo.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getImporteADevolverUltimoPeriodo() {
                return importeADevolverUltimoPeriodo;
            }

            /**
             * Define el valor de la propiedad importeADevolverUltimoPeriodo.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setImporteADevolverUltimoPeriodo(BigDecimal value) {
                this.importeADevolverUltimoPeriodo = value;
            }

            /**
             * Obtiene el valor de la propiedad cuotasPendCompensar.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getCuotasPendCompensar() {
                return cuotasPendCompensar;
            }

            /**
             * Define el valor de la propiedad cuotasPendCompensar.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setCuotasPendCompensar(BigDecimal value) {
                this.cuotasPendCompensar = value;
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
             *     &lt;/restriction&gt;
             *   &lt;/complexContent&gt;
             * &lt;/complexType&gt;
             * </pre>
             * 
             * 
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "")
            public static class ExclusionBaja {


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
         *         &lt;element name="TotResulPositivos322" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
         *         &lt;element name="TotResulNegativos322" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
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
            "totResulPositivos322",
            "totResulNegativos322"
        })
        public static class PerSiRegGrupos {

            @XmlElement(name = "TotResulPositivos322")
            protected BigDecimal totResulPositivos322;
            @XmlElement(name = "TotResulNegativos322")
            protected BigDecimal totResulNegativos322;

            /**
             * Obtiene el valor de la propiedad totResulPositivos322.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getTotResulPositivos322() {
                return totResulPositivos322;
            }

            /**
             * Define el valor de la propiedad totResulPositivos322.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setTotResulPositivos322(BigDecimal value) {
                this.totResulPositivos322 = value;
            }

            /**
             * Obtiene el valor de la propiedad totResulNegativos322.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getTotResulNegativos322() {
                return totResulNegativos322;
            }

            /**
             * Define el valor de la propiedad totResulNegativos322.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setTotResulNegativos322(BigDecimal value) {
                this.totResulNegativos322 = value;
            }

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
     *         &lt;element name="OpRegGeneral" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
     *         &lt;element name="OpRegEspCriterioCaja" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
     *         &lt;element name="EntregasIntracomunitariasExentas" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
     *         &lt;element name="ExportacionesExentasConDrchoDeduccion" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
     *         &lt;element name="OpExentasSinDrchoDeduccion" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
     *         &lt;element name="OpNoSujetas" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
     *         &lt;element name="Box125" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
     *         &lt;element name="Box126" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
     *         &lt;element name="Box127" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
     *         &lt;element name="Box128" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
     *         &lt;element name="OpRegSimplificado" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
     *         &lt;element name="OpRegEspAgricPescGanad" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
     *         &lt;element name="OpRegEspRecEquivalencia" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
     *         &lt;element name="OpRegEspBienesUsados" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
     *         &lt;element name="OpRegEspAgViajes" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
     *         &lt;element name="EntregasBienesInmuebles" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
     *         &lt;element name="EntregasBienesInversion" type="{}tipo_ImpPositivo" minOccurs="0"/&gt;
     *         &lt;element name="TotalVolOp" type="{}tipo_ImpNegativo" minOccurs="0"/&gt;
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
        "opRegGeneral",
        "opRegEspCriterioCaja",
        "entregasIntracomunitariasExentas",
        "exportacionesExentasConDrchoDeduccion",
        "opExentasSinDrchoDeduccion",
        "opNoSujetas",
        "box125",
        "box126",
        "box127",
        "box128",
        "opRegSimplificado",
        "opRegEspAgricPescGanad",
        "opRegEspRecEquivalencia",
        "opRegEspBienesUsados",
        "opRegEspAgViajes",
        "entregasBienesInmuebles",
        "entregasBienesInversion",
        "totalVolOp"
    })
    public static class VolOperaciones {

        @XmlElement(name = "OpRegGeneral")
        protected BigDecimal opRegGeneral;
        @XmlElement(name = "OpRegEspCriterioCaja")
        protected BigDecimal opRegEspCriterioCaja;
        @XmlElement(name = "EntregasIntracomunitariasExentas")
        protected BigDecimal entregasIntracomunitariasExentas;
        @XmlElement(name = "ExportacionesExentasConDrchoDeduccion")
        protected BigDecimal exportacionesExentasConDrchoDeduccion;
        @XmlElement(name = "OpExentasSinDrchoDeduccion")
        protected BigDecimal opExentasSinDrchoDeduccion;
        @XmlElement(name = "OpNoSujetas")
        protected BigDecimal opNoSujetas;
        @XmlElement(name = "Box125")
        protected BigDecimal box125;
        @XmlElement(name = "Box126")
        protected BigDecimal box126;
        @XmlElement(name = "Box127")
        protected BigDecimal box127;
        @XmlElement(name = "Box128")
        protected BigDecimal box128;
        @XmlElement(name = "OpRegSimplificado")
        protected BigDecimal opRegSimplificado;
        @XmlElement(name = "OpRegEspAgricPescGanad")
        protected BigDecimal opRegEspAgricPescGanad;
        @XmlElement(name = "OpRegEspRecEquivalencia")
        protected BigDecimal opRegEspRecEquivalencia;
        @XmlElement(name = "OpRegEspBienesUsados")
        protected BigDecimal opRegEspBienesUsados;
        @XmlElement(name = "OpRegEspAgViajes")
        protected BigDecimal opRegEspAgViajes;
        @XmlElement(name = "EntregasBienesInmuebles")
        protected BigDecimal entregasBienesInmuebles;
        @XmlElement(name = "EntregasBienesInversion")
        protected BigDecimal entregasBienesInversion;
        @XmlElement(name = "TotalVolOp")
        protected BigDecimal totalVolOp;

        /**
         * Obtiene el valor de la propiedad opRegGeneral.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getOpRegGeneral() {
            return opRegGeneral;
        }

        /**
         * Define el valor de la propiedad opRegGeneral.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setOpRegGeneral(BigDecimal value) {
            this.opRegGeneral = value;
        }

        /**
         * Obtiene el valor de la propiedad opRegEspCriterioCaja.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getOpRegEspCriterioCaja() {
            return opRegEspCriterioCaja;
        }

        /**
         * Define el valor de la propiedad opRegEspCriterioCaja.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setOpRegEspCriterioCaja(BigDecimal value) {
            this.opRegEspCriterioCaja = value;
        }

        /**
         * Obtiene el valor de la propiedad entregasIntracomunitariasExentas.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getEntregasIntracomunitariasExentas() {
            return entregasIntracomunitariasExentas;
        }

        /**
         * Define el valor de la propiedad entregasIntracomunitariasExentas.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setEntregasIntracomunitariasExentas(BigDecimal value) {
            this.entregasIntracomunitariasExentas = value;
        }

        /**
         * Obtiene el valor de la propiedad exportacionesExentasConDrchoDeduccion.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getExportacionesExentasConDrchoDeduccion() {
            return exportacionesExentasConDrchoDeduccion;
        }

        /**
         * Define el valor de la propiedad exportacionesExentasConDrchoDeduccion.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setExportacionesExentasConDrchoDeduccion(BigDecimal value) {
            this.exportacionesExentasConDrchoDeduccion = value;
        }

        /**
         * Obtiene el valor de la propiedad opExentasSinDrchoDeduccion.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getOpExentasSinDrchoDeduccion() {
            return opExentasSinDrchoDeduccion;
        }

        /**
         * Define el valor de la propiedad opExentasSinDrchoDeduccion.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setOpExentasSinDrchoDeduccion(BigDecimal value) {
            this.opExentasSinDrchoDeduccion = value;
        }

        /**
         * Obtiene el valor de la propiedad opNoSujetas.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getOpNoSujetas() {
            return opNoSujetas;
        }

        /**
         * Define el valor de la propiedad opNoSujetas.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setOpNoSujetas(BigDecimal value) {
            this.opNoSujetas = value;
        }

        /**
         * Obtiene el valor de la propiedad box125.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getBox125() {
            return box125;
        }

        /**
         * Define el valor de la propiedad box125.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setBox125(BigDecimal value) {
            this.box125 = value;
        }

        /**
         * Obtiene el valor de la propiedad box126.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getBox126() {
            return box126;
        }

        /**
         * Define el valor de la propiedad box126.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setBox126(BigDecimal value) {
            this.box126 = value;
        }

        /**
         * Obtiene el valor de la propiedad box127.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getBox127() {
            return box127;
        }

        /**
         * Define el valor de la propiedad box127.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setBox127(BigDecimal value) {
            this.box127 = value;
        }

        /**
         * Obtiene el valor de la propiedad box128.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getBox128() {
            return box128;
        }

        /**
         * Define el valor de la propiedad box128.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setBox128(BigDecimal value) {
            this.box128 = value;
        }

        /**
         * Obtiene el valor de la propiedad opRegSimplificado.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getOpRegSimplificado() {
            return opRegSimplificado;
        }

        /**
         * Define el valor de la propiedad opRegSimplificado.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setOpRegSimplificado(BigDecimal value) {
            this.opRegSimplificado = value;
        }

        /**
         * Obtiene el valor de la propiedad opRegEspAgricPescGanad.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getOpRegEspAgricPescGanad() {
            return opRegEspAgricPescGanad;
        }

        /**
         * Define el valor de la propiedad opRegEspAgricPescGanad.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setOpRegEspAgricPescGanad(BigDecimal value) {
            this.opRegEspAgricPescGanad = value;
        }

        /**
         * Obtiene el valor de la propiedad opRegEspRecEquivalencia.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getOpRegEspRecEquivalencia() {
            return opRegEspRecEquivalencia;
        }

        /**
         * Define el valor de la propiedad opRegEspRecEquivalencia.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setOpRegEspRecEquivalencia(BigDecimal value) {
            this.opRegEspRecEquivalencia = value;
        }

        /**
         * Obtiene el valor de la propiedad opRegEspBienesUsados.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getOpRegEspBienesUsados() {
            return opRegEspBienesUsados;
        }

        /**
         * Define el valor de la propiedad opRegEspBienesUsados.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setOpRegEspBienesUsados(BigDecimal value) {
            this.opRegEspBienesUsados = value;
        }

        /**
         * Obtiene el valor de la propiedad opRegEspAgViajes.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getOpRegEspAgViajes() {
            return opRegEspAgViajes;
        }

        /**
         * Define el valor de la propiedad opRegEspAgViajes.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setOpRegEspAgViajes(BigDecimal value) {
            this.opRegEspAgViajes = value;
        }

        /**
         * Obtiene el valor de la propiedad entregasBienesInmuebles.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getEntregasBienesInmuebles() {
            return entregasBienesInmuebles;
        }

        /**
         * Define el valor de la propiedad entregasBienesInmuebles.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setEntregasBienesInmuebles(BigDecimal value) {
            this.entregasBienesInmuebles = value;
        }

        /**
         * Obtiene el valor de la propiedad entregasBienesInversion.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getEntregasBienesInversion() {
            return entregasBienesInversion;
        }

        /**
         * Define el valor de la propiedad entregasBienesInversion.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setEntregasBienesInversion(BigDecimal value) {
            this.entregasBienesInversion = value;
        }

        /**
         * Obtiene el valor de la propiedad totalVolOp.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getTotalVolOp() {
            return totalVolOp;
        }

        /**
         * Define el valor de la propiedad totalVolOp.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setTotalVolOp(BigDecimal value) {
            this.totalVolOp = value;
        }

    }

}
