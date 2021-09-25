package solutions.aon.seg.social;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.object.Liquidation.LiquidationBuilder;
import solutions.aon.seg.social.object.SituacionEmpresa.SituacionEmpresaBuilder;
import solutions.aon.seg.social.object.WorkerLiquidation.WorkerLiquidationBuilder;
import solutions.aon.seg.social.toolkit.Toolkit;

public abstract class ServicioREDRegeXML {
	protected static final String DATE_FORMAT = "dd/MM/yyyy";
	/**
	 * Not reliable, it sometimes does not pick up some values properly
	 * @param xml The xml String
	 * @return a map containing all fields with id and their innerTexts
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	@Deprecated
	protected static Map<String, String> extractSituacionEmpresaInfo (String xml) throws ParserConfigurationException, SAXException, IOException {
		HashMap<String, String> values = new HashMap<String, String>();
		
		DefaultHandler handler = new DefaultHandler() {
			String id = null;
			@Override
			public void startElement(String uri, String localName, String qName, Attributes attributes)
					throws SAXException {
				if (attributes.getValue("id") != null) {
					id = attributes.getValue("id");
				}
			}

			@Override
			public void endElement(String uri, String localName, String qName) throws SAXException {
				id = null;
			}

			@Override
			public void characters(char[] ch, int start, int length) throws SAXException {
				if (id != null) {
					String value = new String(ch, start, length);
					if (id.equals("SDFFECHASIT")) {
						System.out.println();
						System.out.println(new String(ch, 0, ch.length));
						System.out.println(value);
						System.out.println();
					}
					value = value != null ? Toolkit.removeNBSP(value) : null;
					value = value != null ? value.trim() : value;
					values.put(id, value);
				}
			}
		};
		
		 
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();
		StringReader reader = new StringReader(xml);
		InputSource is = new InputSource(reader);
        is.setEncoding("UTF-8");
        saxParser.parse(is, handler);
        	
        return values;
	}
	
	protected static void fillManagementData (SituacionEmpresaBuilder seb, Map<String, String> values) {
		String sdfEmpresario3 = values.get("SDFEMPRESARIO3");
		sdfEmpresario3 = sdfEmpresario3 != null && sdfEmpresario3.length() > 9 ?
				Toolkit.removeExtraZeros(values.get("SDFEMPRESARIO3")) : sdfEmpresario3;
				
		seb.setCcc(values.get("SDFCPROVINCIA3") + values.get("SDFCNISS3"))
		.setIdEmpresario(values.get("SDFTIPO3"))
		.setNifempresa(sdfEmpresario3)
		.setRegimen(values.get("SDFREGIMEN3"))
		.setNss(values.get("SDFPRONAF3") + values.get("SDFNUMNAF30"))
		.setCccp(values.get("SDFPROVINCIACP3"))
		.setUgtgss(values.get("SDFTESORERIA3") + values.get("SDFADMON3") + values.get("SDFURE3"))
		.setUgtgsscccp(values.get("SDFTESPPAL") + values.get("SDFADMPPAL") + values.get("SDFUREPPAL"))
		.setUgcentral(values.get("SDFUNIDAD"))
		.setOgism(values.get("SDFPROUGISM") + values.get("SDFLOCUGISM"))
		.setCccAnt(values.get("SDFPROVCANT3") + values.get("SDFNUMCANT3"))
		.setCccSuc(values.get("SDFPROSUC3") + values.get("SDFNUMSUC3") + values.get("SDFREGSUC3") + values.get("SDFCCOASUC3"))
		.setSit(values.get("SDFSITUACION3") + values.get("SDFTSITUACION3"))
		.setfSit(Toolkit.parseDate(values.get("SDFFECHASIT"), DATE_FORMAT))
		.setfAltaInicial(Toolkit.parseDate(values.get("SDFFECHAALTA"), DATE_FORMAT))
		.setTrabajadorAlta(Toolkit.strToInteger(values.get("SDFNROTRA3")))
		.setAltaPrTrab(Toolkit.parseDate(values.get("SDFFECHAALTA13"), DATE_FORMAT))
		.setUltBajaEfCot(Toolkit.parseDate(values.get("SDFFECHABAJA93"), DATE_FORMAT))
		.setTrl(values.get("SDFTIPCON") + values.get("SDFDESTIPCON"))
		.setcEspNum(values.get("SDFCOLECTIVO3"))
		.setcEspCad(values.get("SDFDESCOL3"))
		.setCnae09Num(values.get("SDFACTIV093"))
		.setCnae93Num(values.get("SDFACTIV933"))
		.setCnae09Cad(values.get("SDFTACTIV093"))
		.setCnae93Cad(values.get("SDFTACTIV933"))
		.setTa2Alta(Toolkit.strToInteger(values.get("SDFCCONDIAS3")))
		.setTa2Baja(Toolkit.strToInteger(values.get("SDFCCONDPPB3")))
		.setTiposATyEPIT(Toolkit.strToFloat(values.get("SDFTITN12")))
		.setIms(Toolkit.strToFloat(values.get("SDFTIMSN12")))
		.setTotal(Toolkit.strToFloat(values.get("SDFTTOTN12")))
		.setCoeJubNum(values.get("SDFCOREJU"))
		.setCoeJubCad(values.get("SDFDSCOREJU"))
		.setAconExtra(values.get("SDFACTMEXTR") + values.get("SDFDSACONT"))
		.setEscTaller(Toolkit.toBoolean(values.get("SDFCESTAL")))
		.setAutorizacionRed(values.get("SDFAUTORIDRED"))
		.setPlazoIncorpRed(Toolkit.parseDate(values.get("SDFPLAZORED"), DATE_FORMAT))
		.setFechaAutCan(Toolkit.parseDate(values.get("SDFFECHAAUTRED"), DATE_FORMAT));
			
	}
	
	protected static void fillIdentifyingData (SituacionEmpresaBuilder seb, Map<String, String> values) {
		seb.setAnagrama(values.get("SDFANAGR3"))
		.setEmbarcacion(values.get("SDFTIPEMB") + values.get("SDFEMB") + values.get("SDFNOMEMBAR"))
		.setTlfMovil(values.get("SDFTELMOVIL3"))
		.setTlfFijo(values.get("SDFTELFIJO3"))
		.setEmail(values.get("txtconcat1_1"))
		.setNotifDomEmpresa(Toolkit.toBoolean(values.get("SDFNOTIF3")))
		.setTipoViaDirEmpresa(values.get("SDFVIA3"))
		.setDirEmpCalle(values.get("SDFDOMICILIO3"))
		.setDirEmpNum(values.get("SDFNUMERO3"))
		.setDirEmpBis(values.get("SDFBIS3"))
		.setDirEmpBloq(values.get("SDFBLOQUE3"))
		.setDirEmpEs(values.get("SDFESCALERA3"))
		.setDirEmpPiso(values.get("SDFPISO3"))
		.setDirEmpP(values.get("SDFPUERTA3"))
		.setDirEmpCP(values.get("SDFPOSTAL3"))
		.setDirEmpNumMuni(values.get("SDFLOCALIDAD13"))
		.setDirEmpNomMuni(values.get("SDFLOCALIDAD23"))
		.setDirEmpTlf(values.get("SDFNUM9TELEFONO3"))
		.setNotifDomActividad(Toolkit.toBoolean(values.get("SDFNOTIF4")))
		.setActUgtgss(values.get("SDFTESORERIA3") + values.get("SDFADMON3") + values.get("SDFURE3"))
		.setTipoViaDirActividad(values.get("SDFVIA4"))
		.setDirActCalle(values.get("SDFDOMICILIO4"))
		.setDirActNum(values.get("SDFNUMERO3"))
		.setDirActBis(values.get("SDFBIS4"))
		.setDirActBloq(values.get("SDFBLOQUE4"))
		.setDirActEs(values.get("SDFESCALERA4"))
		.setDirActPiso(values.get("SDFPISO4"))
		.setDirActP(values.get("SDFPUERTA4"))
		.setDirActCP(values.get("SDFPOSTAL4"))
		.setDirActNumMuni(values.get("SDFMUNICIPIO4"))
		.setDirActNomMuni(values.get("SDFLOCALIDAD4"))
		.setDirActTlf(values.get("SDFNUM9TELEFONO4"));
	}
	
	protected static void liquidationDataType(LiquidationBuilder lb, Collection<String> trs) throws SegSocialException {
		for (String tr : trs) {
			LinkedList<String> rows = Toolkit.getTdsTexts(tr);
			String rowConcept=Toolkit.safeRemoveWeirdCharacters(Toolkit.safeGet(rows, 0));
			if (rowConcept != null) {
				if(rowConcept.equalsIgnoreCase("CONTINGENCIAS COMUNES")) {
					Float nmbr1= Toolkit.strToFloat(Toolkit.safeGet(rows, 1));
					lb.setCcBase(nmbr1);
					Float nmbr2 = Toolkit.strToFloat(Toolkit.safeGet(rows, 2));
					lb.setCcBusinessFee(nmbr2);
					Float nmbr3 = Toolkit.strToFloat(Toolkit.safeGet(rows, 3));
					lb.setCcWorkerFee(nmbr3);
					Float nmbr4 = Toolkit.strToFloat(Toolkit.safeGet(rows, 4));
					lb.setCcTotalFee(nmbr4);
				}
				else if(rowConcept.equalsIgnoreCase("LIQUIDO CONTINGENCIAS COMUNES")) {
					Float nmbr1 = Toolkit.strToFloat(Toolkit.safeGet(rows, 1));
					lb.setCcLiquidBase(nmbr1);
					Float nmbr2 = Toolkit.strToFloat(Toolkit.safeGet(rows, 2));
					lb.setCcLiquidBusinessFee(nmbr2);
					Float nmbr3 = Toolkit.strToFloat(Toolkit.safeGet(rows, 3));
					lb.setCcLiquidWorkerFee(nmbr3);
					Float nmbr4 = Toolkit.strToFloat(Toolkit.safeGet(rows, 4));
					lb.setCcLiquidTotalFee(nmbr4);
				}
				else if(rowConcept.equalsIgnoreCase("IT DE ACCIDENTES DE TRABAJO")) {
					Float nmbr1 = Toolkit.strToFloat(Toolkit.safeGet(rows, 1));
					lb.setItWorkAccidentBase(nmbr1);
					Float nmbr2 = Toolkit.strToFloat(Toolkit.safeGet(rows, 2));
					lb.setItWorkAccidentBusinessFee(nmbr2);
					Float nmbr3 = Toolkit.strToFloat(Toolkit.safeGet(rows, 3));
					lb.setItWorkAccidentWorkerFee(nmbr3);
					Float nmbr4 = Toolkit.strToFloat(Toolkit.safeGet(rows, 4));
					lb.setItWorkAccidentTotalFee(nmbr4);
				}
				else if(rowConcept.equalsIgnoreCase("IMS DE ACCIDENTES DE TRABAJO")) {
					Float nmbr1 = Toolkit.strToFloat(Toolkit.safeGet(rows, 1));
					lb.setImsWorkAccidentBase(nmbr1);
					Float nmbr2 = Toolkit.strToFloat(Toolkit.safeGet(rows, 2));
					lb.setImsWorkAccidentBusinessFee(nmbr2);
					Float nmbr3 = Toolkit.strToFloat(Toolkit.safeGet(rows, 3));
					lb.setImsWorkAccidentWorkerFee(nmbr3);
					Float nmbr4 = Toolkit.strToFloat(Toolkit.safeGet(rows, 4));
					lb.setImsWorkAccidentTotalFee(nmbr4);
				}
				else if(rowConcept.equalsIgnoreCase("LIQUIDO DE ACCIDENTES DE TRABAJO")) {
					Float nmbr1 = Toolkit.strToFloat(Toolkit.safeGet(rows, 1));
					lb.setWorkAccidentLiquidBase(nmbr1);
					Float nmbr2 = Toolkit.strToFloat(Toolkit.safeGet(rows, 2));
					lb.setWorkAccidentLiquidBusinessFee(nmbr2);
					Float nmbr3 = Toolkit.strToFloat(Toolkit.safeGet(rows, 3));
					lb.setWorkAccidentLiquidWorkerFee(nmbr3);
					Float nmbr4 = Toolkit.strToFloat(Toolkit.safeGet(rows, 4));
					lb.setWorkAccidentLiquidTotalFee(nmbr4);
				}
				else if(rowConcept.equalsIgnoreCase("OTRAS COTIZACIONES")) {
					Float nmbr1 = Toolkit.strToFloat(Toolkit.safeGet(rows, 1));
					lb.setOtherContributionsBase(nmbr1);
					Float nmbr2 = Toolkit.strToFloat(Toolkit.safeGet(rows, 2));
					lb.setOtherContributionsBusinessFee(nmbr2);
					Float nmbr3 = Toolkit.strToFloat(Toolkit.safeGet(rows, 3));
					lb.setOtherContributionsWorkerFee(nmbr3);
					Float nmbr4 = Toolkit.strToFloat(Toolkit.safeGet(rows, 4));
					lb.setOtherContributionsTotalFee(nmbr4);
				}
				else if(rowConcept.equalsIgnoreCase("LIQUIDO DE OTRAS COTIZACIONES")) {
					Float nmbr1 = Toolkit.strToFloat(Toolkit.safeGet(rows, 1));
					lb.setOtherContributionsLiquidBase(nmbr1);
					Float nmbr2 = Toolkit.strToFloat(Toolkit.safeGet(rows, 2));
					lb.setOtherContributionsLiquidBusinessFee(nmbr2);
					Float nmbr3 = Toolkit.strToFloat(Toolkit.safeGet(rows, 3));
					lb.setOtherContributionsLiquidWorkerFee(nmbr3);
					Float nmbr4 = Toolkit.strToFloat(Toolkit.safeGet(rows, 4));
					lb.setOtherContributionsLiquidTotalFee(nmbr4);
				}
				else if(rowConcept.equalsIgnoreCase("LIQUIDO DE TOTALES")) {
					Float nmbr1 = Toolkit.strToFloat(Toolkit.safeGet(rows, 1));
					lb.setTotalLiquidBase(nmbr1);
					Float nmbr2 = Toolkit.strToFloat(Toolkit.safeGet(rows, 2));
					lb.setTotalLiquidBusinessFee(nmbr2);
					Float nmbr3 = Toolkit.strToFloat(Toolkit.safeGet(rows, 3));
					lb.setTotalLiquidWorkerFee(nmbr3);
					Float nmbr4 = Toolkit.strToFloat(Toolkit.safeGet(rows, 4));
					lb.setTotalLiquidTotalFee(nmbr4);
				}
				else if(rowConcept.equalsIgnoreCase("BONIF.Y SUBVENC.CON CARGO AL INEM")) {
					Float nmbr1 = Toolkit.strToFloat(Toolkit.safeGet(rows, 1));
					lb.setGrantsAndBonusesBase(nmbr1);
					Float nmbr2 = Toolkit.strToFloat(Toolkit.safeGet(rows, 2));
					lb.setGrantsAndBonusesBusinessFee(nmbr2);
					Float nmbr3 = Toolkit.strToFloat(Toolkit.safeGet(rows, 3));
					lb.setGrantsAndBonusesWorkerFee(nmbr3);
					Float nmbr4 = Toolkit.strToFloat(Toolkit.safeGet(rows, 4));
					lb.setGrantsAndBonusesTotalFee(nmbr4);
				}
			}
						
		}
		
	}
	
	
	
	
	public static void workerLiquidationDataType(WorkerLiquidationBuilder wlb, Collection<String> trs) throws SegSocialException {
		
		for (String tr : trs) {
			LinkedList<String> rows = Toolkit.getTdsTexts(tr);
			String rowConcept=Toolkit.safeRemoveWeirdCharacters(Toolkit.safeGet(rows, 0));
			if (rowConcept != null) {
				
				if(rowConcept.equalsIgnoreCase("CONTINGENCIAS COMUNES")) {
					String desc = Toolkit.removeWeirdCharacters(rowConcept);
					wlb.setCcDescription(desc);
					Float nmbr1 = Toolkit.strToFloat(Toolkit.safeGet(rows,1));
					wlb.setCcBase(nmbr1);
					Float nmbr2 = Toolkit.strToFloat(Toolkit.safeGet(rows,2));
					wlb.setCcBusinessFee(nmbr2);
					Float nmbr3 = Toolkit.strToFloat(Toolkit.safeGet(rows,3));
					wlb.setCcWorkerFee(nmbr3);
					Float nmbr4 = Toolkit.strToFloat(Toolkit.safeGet(rows,4));
					wlb.setCcTotalFee(nmbr4);
				}
				else if(rowConcept.equalsIgnoreCase("LIQUIDO CONTINGENCIAS COMUNES")) {
					String desc = Toolkit.removeWeirdCharacters(rowConcept);
					wlb.setCcLiquidDescription(desc);
					Float nmbr1 = Toolkit.strToFloat(Toolkit.safeGet(rows,1));
					wlb.setCcLiquidBase(nmbr1);
					Float nmbr2 = Toolkit.strToFloat(Toolkit.safeGet(rows,2));
					wlb.setCcLiquidBusinessFee(nmbr2);
					Float nmbr3 = Toolkit.strToFloat(Toolkit.safeGet(rows,3));
					wlb.setCcLiquidWorkerFee(nmbr3);
					Float nmbr4 = Toolkit.strToFloat(Toolkit.safeGet(rows,4));
					wlb.setCcLiquidTotalFee(nmbr4);
				}
				else if(rowConcept.equalsIgnoreCase("IT DE ACCIDENTES DE TRABAJO")) {
					String desc = Toolkit.removeWeirdCharacters(rowConcept);
					wlb.setItWorkAccidentDescription(desc);
					Float nmbr1 = Toolkit.strToFloat(Toolkit.safeGet(rows,1));
					wlb.setItWorkAccidentBase(nmbr1);
					Float nmbr2 = Toolkit.strToFloat(Toolkit.safeGet(rows,2));
					wlb.setItWorkAccidentusinessFee(nmbr2);
					Float nmbr3 = Toolkit.strToFloat(Toolkit.safeGet(rows,3));
					wlb.setItWorkAccidentWorkerFee(nmbr3);
					Float nmbr4 = Toolkit.strToFloat(Toolkit.safeGet(rows,4));
					wlb.setItWorkAccidentTotalFee(nmbr4);
				}
				else if(rowConcept.equalsIgnoreCase("IMS DE ACCIDENTES DE TRABAJO")) {
					String desc = Toolkit.removeWeirdCharacters(rowConcept);
					wlb.setImsWorkAccidentDescription(desc);
					Float nmbr1 = Toolkit.strToFloat(Toolkit.safeGet(rows,1));
					wlb.setImsWorkAccidentBase(nmbr1);
					Float nmbr2 = Toolkit.strToFloat(Toolkit.safeGet(rows,2));
					wlb.setImsWorkAccidentBusinessFee(nmbr2);
					Float nmbr3 = Toolkit.strToFloat(Toolkit.safeGet(rows,3));
					wlb.setImsWorkAccidentWorkerFee(nmbr3);
					Float nmbr4 = Toolkit.strToFloat(Toolkit.safeGet(rows,4));
					wlb.setImsWorkAccidentTotalFee(nmbr4);
				}
				else if(rowConcept.equalsIgnoreCase("LIQUIDO DE ACCIDENTES DE TRABAJO")) {
					String desc = Toolkit.removeWeirdCharacters(rowConcept);
					wlb.setWorkAccidentLiquidDescription(desc);
					Float nmbr1 = Toolkit.strToFloat(Toolkit.safeGet(rows,1));
					wlb.setWorkAccidentLiquidBase(nmbr1);
					Float nmbr2 = Toolkit.strToFloat(Toolkit.safeGet(rows,2));
					wlb.setWorkAccidentLiquidBusinessFee(nmbr2);
					Float nmbr3 = Toolkit.strToFloat(Toolkit.safeGet(rows,3));
					wlb.setWorkAccidentLiquidWorkerFee(nmbr3);
					Float nmbr4 = Toolkit.strToFloat(Toolkit.safeGet(rows,4));
					wlb.setWorkAccidentLiquidTotalFee(nmbr4);
				}
				else if(rowConcept.equalsIgnoreCase("DESEMPLEO")) {
					String desc = Toolkit.removeWeirdCharacters(rowConcept);
					wlb.setUnemploymentDescription(desc);
					Float nmbr1 = Toolkit.strToFloat(Toolkit.safeGet(rows,1));
					wlb.setUnemploymentBase(nmbr1);
					Float nmbr2 = Toolkit.strToFloat(Toolkit.safeGet(rows,2));
					wlb.setUnemploymentBusinessFee(nmbr2);
					Float nmbr3 = Toolkit.strToFloat(Toolkit.safeGet(rows,3));
					wlb.setUnemploymentWorkerFee(nmbr3);
					Float nmbr4 = Toolkit.strToFloat(Toolkit.safeGet(rows,4));
					wlb.setUnemploymentTotalFee(nmbr4);
				}
				else if(rowConcept.equalsIgnoreCase("FOGASA")) {
					String desc = Toolkit.removeWeirdCharacters(rowConcept);
					wlb.setFogasaDescription(desc);
					Float nmbr1 = Toolkit.strToFloat(Toolkit.safeGet(rows,1));
					wlb.setFogasaBase(nmbr1);
					Float nmbr2 = Toolkit.strToFloat(Toolkit.safeGet(rows,2));
					wlb.setFogasaBusinessFee(nmbr2);
					Float nmbr3 = Toolkit.strToFloat(Toolkit.safeGet(rows,3));
					wlb.setFogasaWorkerFee(nmbr3);
					Float nmbr4 = Toolkit.strToFloat(Toolkit.safeGet(rows,4));
					wlb.setFogasaTotalFee(nmbr4);
				}
				else if(rowConcept.toUpperCase().contains("FORMACI") && rowConcept.toUpperCase().contains("N PROFESIONAL")) {
					String desc = Toolkit.removeWeirdCharacters(rowConcept);
					wlb.setJobTrainingDescription(desc);
					Float nmbr1 = Toolkit.strToFloat(Toolkit.safeGet(rows,1));
					wlb.setJobTrainingBase(nmbr1);
					Float nmbr2 = Toolkit.strToFloat(Toolkit.safeGet(rows,2));
					wlb.setJobTrainingBusinessFee(nmbr2);
					Float nmbr3 = Toolkit.strToFloat(Toolkit.safeGet(rows,3));
					wlb.setJobTrainingWorkerFee(nmbr3);
					Float nmbr4 = Toolkit.strToFloat(Toolkit.safeGet(rows,4));
					wlb.setJobTrainingTotalFee(nmbr4);
				}
				else if(rowConcept.equalsIgnoreCase("LIQUIDO DE OTRAS COTIZACIONES")) {
					String desc = Toolkit.removeWeirdCharacters(rowConcept);
					wlb.setOtherContributionsLiquidDescription(desc);
					Float nmbr1 = Toolkit.strToFloat(Toolkit.safeGet(rows,1));
					wlb.setOtherContributionsLiquidBase(nmbr1);
					Float nmbr2 = Toolkit.strToFloat(Toolkit.safeGet(rows,2));
					wlb.setOtherContributionsLiquidBusinessFee(nmbr2);
					Float nmbr3 = Toolkit.strToFloat(Toolkit.safeGet(rows,3));
					wlb.setOtherContributionsLiquidWorkerFee(nmbr3);
					Float nmbr4 = Toolkit.strToFloat(Toolkit.safeGet(rows,4));
					wlb.setOtherContributionsLiquidTotalFee(nmbr4);
				}
				else if(rowConcept.equalsIgnoreCase("LIQUIDO DE TOTALES")) {
					String desc = Toolkit.removeWeirdCharacters(rowConcept);
					wlb.setTotalLiquidDescription(desc);
					Float nmbr1 = Toolkit.strToFloat(Toolkit.safeGet(rows,1));
					wlb.setTotalLiquidBase(nmbr1);
					Float nmbr2 = Toolkit.strToFloat(Toolkit.safeGet(rows,2));
					wlb.setTotalLiquidBusinessFee(nmbr2);
					Float nmbr3 = Toolkit.strToFloat(Toolkit.safeGet(rows,3));
					wlb.setTotalLiquidWorkerFee(nmbr3);
					Float nmbr4 = Toolkit.strToFloat(Toolkit.safeGet(rows,4));
					wlb.setTotalLiquidTotalFee(nmbr4);
				}
				else if(rowConcept.contains("BONIF")) {
					String desc = Toolkit.removeWeirdCharacters(rowConcept);
					wlb.setGrantsAndBonusesDescription(desc);
					Float nmbr1 = Toolkit.strToFloat(Toolkit.safeGet(rows,1));
					wlb.setGrantsAndBonusesBase(nmbr1);
					Float nmbr2 = Toolkit.strToFloat(Toolkit.safeGet(rows,2));
					wlb.setGrantsAndBonusesBusinessFee(nmbr2);
					Float nmbr3 = Toolkit.strToFloat(Toolkit.safeGet(rows,3));
					wlb.setGrantsAndBonusesWorkerFee(nmbr3);
					Float nmbr4 = Toolkit.strToFloat(Toolkit.safeGet(rows,4));
					wlb.setGrantsAndBonusesTotalFee(nmbr4);
				}
				
				
			}
		}
		
	}
	
	
	
	
}
