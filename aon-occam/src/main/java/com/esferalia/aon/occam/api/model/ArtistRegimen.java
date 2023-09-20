package com.esferalia.aon.occam.api.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ArtistRegimen {

	protected ArtistRegimen() {
		super();
	}

	private static final Map<String, String> artistRegimenTable;

	static {

		Map<String, String> artistTableMap = new HashMap<>();
		artistTableMap.put("", "");
		artistTableMap.put("6000101", "DTOR TEAT MUS VAR");
		artistTableMap.put("6000102", "DIR COREOGRAF TE/MU");
		artistTableMap.put("6000103", "DIR ESCENA TE/MU");
		artistTableMap.put("6000104", "DIR ARTIST TE/MU");
		artistTableMap.put("6000105", " PRIM. MAEST TE/MU ");
		artistTableMap.put("6000106", " DIRECT RAD TELEV ");
		artistTableMap.put("6000107", " PRESENT RAD TEL ");
		artistTableMap.put("6000208", " SEG MAEST DIR TE/MU ");
		artistTableMap.put("6000209", " TERC MAES DIR TE/MU ");
		artistTableMap.put("6000210", " PRIM MAES SUS TE/MU ");
		artistTableMap.put(" 6000211 ", " SEG MAES SUS TE/MU ");
		artistTableMap.put("6000212", " DIREC ORQUESTA TE/MU ");
		artistTableMap.put("6000313", " MAEST COREOGR TE/MU ");
		artistTableMap.put("6000314", " MAESTRO COROS TE/MU ");
		artistTableMap.put(" 6000315 ", " MAEST APUNT TE/MU ");
		artistTableMap.put(" 6000316 ", " DIRECT BANDA TE/MU ");
		artistTableMap.put("6000317", " REGIDOR TE/MU ");
		artistTableMap.put(" 6000318 ", " APUNTADOR TE/MU ");
		artistTableMap.put(" 6000319 ", " LOCU RADIO TELEV ");
		artistTableMap.put("6000320", " ACTOR TEATRO ");
		artistTableMap.put("6000321", " CANTANT LIRICO TE/MU ");
		artistTableMap.put(" 6000322 ", " CANT MUSICA LIGERA ");
		artistTableMap.put("6000323", " CARICATOS TE/MU ");
		artistTableMap.put("6000324", " ANIMAD SAL FIESTA ");
		artistTableMap.put("6000325", " BAILARINES TE/MU ");
		artistTableMap.put("6000326", " MUSICOS TE/MU ");
		artistTableMap.put("6000327", " ART CIR VA FOL TE/MU ");
		artistTableMap.put("6000528", " ADJUNT DIREC TE/MU ");
		artistTableMap.put("6000729", " SECRET DIREC TE/MU ");
		artistTableMap.put("6100101", " DTOR CINE TV ");
		artistTableMap.put("6100202", " DIRECT FOTOG CINE TV ");
		artistTableMap.put(" 6100303 ", " DIREC PRODUC CINE TV ");
		artistTableMap.put(" 6100304 ", " DIRECTOR TECNICO ");
		artistTableMap.put("6100305", " ACTOR CINE TV ");
		artistTableMap.put(" 6100406 ", " DECORADOR ");
		artistTableMap.put("6100407", " ESCENOG ESP VIV ");
		artistTableMap.put("6100408", " DISEÑ MAQ ESCENI ");
		artistTableMap.put(" 6100409 ", " DIRECTOR SONIDO ");
		artistTableMap.put("6100410", " DIR ILUMINACION ");
		artistTableMap.put("6100411", " DIR SASTRERIA ");
		artistTableMap.put("6100512", "MONTADOR");
		artistTableMap.put("6100513", " TECN DOBLAJE ");
		artistTableMap.put("6100514", " JEFE TECNICO ");
		artistTableMap.put("6100515", " ADAPT DIALOG ");
		artistTableMap.put("6100516", " SEG OPERADOR ");
		artistTableMap.put("6100517", "MAQUILLADOR");
		artistTableMap.put("6100518", " AYUD TECNICOS ");
		artistTableMap.put("6100519", " PRIM AY PROD ");
		artistTableMap.put("6100520", " FOTOG F FIJA ");
		artistTableMap.put("6100521", " FIGURINISTA ");
		artistTableMap.put("6100522", "ILUMINADOR");
		artistTableMap.put("6100523", " TECN SONIDO ");
		artistTableMap.put("6100524", " TECN MAQUIN ESC ");
		artistTableMap.put("6100525", " TECN UTILERIA ");
		artistTableMap.put("6100526", " TECN SASTRERIA ");
		artistTableMap.put("6100727", " AYUD OPERAD ");
		artistTableMap.put("6100728", " AYUD MAQUILLAJE ");
		artistTableMap.put("6100729", " AYUD CARACTERIZ ");
		artistTableMap.put("6100730", " AYUD SONIDO ");
		artistTableMap.put("6100731", " AYUD REGIDURIA ");
		artistTableMap.put(" 6100732 ", " AYUD ILUMINADOR ");
		artistTableMap.put("6100733", " AYUD MAQ ESCENICA ");
		artistTableMap.put("6100734", " AYUD UTILERIA ");
		artistTableMap.put("6100735", " AYUD SASTRERIA ");
		artistTableMap.put("6100736", " AYUD DECORADOR ");
		artistTableMap.put("6100737", "PELUQUERO");
		artistTableMap.put("6100738", " AYUD PELUQUER ");
		artistTableMap.put(" 6100739 ", " SEG AY PRODUC ");
		artistTableMap.put("6100740", " SECRET RODAJE ");
		artistTableMap.put("6100741", " SECR PROD ROD ");
		artistTableMap.put(" 6100742 ", " AYUD MONTAJE ");
		artistTableMap.put("6100743", " AUX DIRECCION ");
		artistTableMap.put("6100744", " AUX MAQUILLADOR ");
		artistTableMap.put("6100745", " AUX PRODUCCION ");
		artistTableMap.put(" 6100746 ", " COMPARSERIA ");
		artistTableMap.put(" 6100747 ", "FIGURACION");
		artistTableMap.put("6100748", "AVISADOR");

		artistRegimenTable = Collections.unmodifiableMap(artistTableMap);

	}
	
	public static Collection<String> getAllEntriesCollection(){
		Collection<String> entries = new ArrayList<>();
		
		for(Entry<String, String> entry : artistRegimenTable.entrySet())
			entries.add(entry.getKey() + " - " + entry.getValue());
		
		return entries;
	}
	
	public static Map<String, String> getArtistRegimen() {
		return artistRegimenTable;
	}
	
	public static String getEntryByCode(String code) {
		if(null == code) return "";
		
		return code + " - " + artistRegimenTable.get(code);
	}
	
	
}
