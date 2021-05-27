package com.esferalia.aon.gwt.fiscal.shared.mod303;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.*;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum Model3032017PrintBIZKAIAScript implements IModelScript<Mod303Key> {
	
	
	 DVG01 ("IVA DEVENGADO"				,null,TITLE)
	,DVG02 (null						,new Mod303Key[]{BZ_C003	,BZ_X003	,BZ_C004})
	,DVG03 (getDescription(BZ_C005)		,new Mod303Key[]{BZ_C005	,BZ_X005	,BZ_C006})
	,DVG04 (null						,new Mod303Key[]{BZ_C007	,BZ_X007	,BZ_C008})
	,DVG05 (null						,new Mod303Key[]{BZ_C009	,BZ_X009	,BZ_C010})
	,DVG06 (getDescription(BZ_C011)		,new Mod303Key[]{BZ_C011	,BZ_X011	,BZ_C012})
	,DVG07 (null						,new Mod303Key[]{BZ_C013	,BZ_X013	,BZ_C014})
	,DVG08 (null						,new Mod303Key[]{BZ_C015	,BZ_X015	,BZ_C016})
	,DVG09 (getDescription(BZ_C017) 	,new Mod303Key[]{BZ_C017	,null		,BZ_C018})
	,DVG10 (getDescription(BZ_C019)		,new Mod303Key[]{BZ_C019	,null		,BZ_C020})
	,DVG11 (getDescription(BZ_C021)		,new Mod303Key[]{BZ_C021	,null		,BZ_C022})
	,DVG12 (getDescription(BZ_C046)		,new Mod303Key[]{BZ_C046	,null		,BZ_C047})
	,DVG13 (BZ_C023.getDescription()	,new Mod303Key[]{null		,null		,BZ_C023})
	,EMPTY0(null						,null)
	,DED01 ("IVA DEDUCIBLE" 			,null,TITLE)
	,DED02 (BZ_C024.getDescription()	,new Mod303Key[]{null		,null		,BZ_C024})
	,DED03 (BZ_C025.getDescription()	,new Mod303Key[]{null		,null		,BZ_C025})
	,DED04 (BZ_C026.getDescription()	,new Mod303Key[]{null		,null		,BZ_C026})
	,DED05 (BZ_C027.getDescription()	,new Mod303Key[]{null		,null		,BZ_C027})
	,DED06 (BZ_C028.getDescription()	,new Mod303Key[]{null		,null		,BZ_C028})
	,DED07 (BZ_C030.getDescription()	,new Mod303Key[]{null		,null		,BZ_C030})
	,EMPTY1(null						,null)
	,LQ000("RESULTADO" 					,null,TITLE)
	,LQ001(BZ_C031.getDescription()		,new Mod303Key[]{null		,null		,BZ_C031})
	,LQ002(BZ_C045.getDescription()		,new Mod303Key[]{null		,null		,BZ_C045})
	,LQ003(BZ_C032.getDescription()		,new Mod303Key[]{null		,null		,BZ_C032})
	,LQ004(BZ_C033.getDescription()		,new Mod303Key[]{null		,null		,BZ_C033})
	,LQ005(BZ_C034.getDescription()		,new Mod303Key[]{null		,null		,BZ_C034})
	,LQ006(BZ_C035.getDescription()		,new Mod303Key[]{null		,null		,BZ_C035})
	,LQ007(BZ_C036.getDescription()		,new Mod303Key[]{null		,null		,BZ_C036})
	,LQ008(BZ_C038.getDescription()		,new Mod303Key[]{null		,null		,BZ_C038})
	,LQ009(BZ_C039.getDescription()		,new Mod303Key[]{null		,null		,BZ_C039})
	,LQ010(BZ_C040.getDescription()		,new Mod303Key[]{null		,null		,BZ_C040})
	,LQ011(BZ_C041.getDescription()		,new Mod303Key[]{null		,null		,BZ_C041})
	,LQ012(BZ_C042.getDescription()		,new Mod303Key[]{null		,null		,BZ_C042})
	,LQ013(BZ_C043.getDescription()		,new Mod303Key[]{null		,null		,BZ_C043})
	
	,EMPTY2(null						,null)
	,AD000("DATOS ADICIONALES" 			,null,TITLE)
	,AD01 ("Compras de bienes corrientes",null,TITLE)
	,AD02 ("4%"							,new Mod303Key[]{BZ_C050	,BZ_C051	,BZ_C052})
	,AD03 ("10%"						,new Mod303Key[]{BZ_C053	,BZ_C054	,BZ_C055})
	,AD04 ("21%"						,new Mod303Key[]{BZ_C056	,BZ_C057	,BZ_C058})
	,AD05 (null							,new Mod303Key[]{BZ_C059	,BZ_C060	,BZ_C061})
	,AD06 (null							,new Mod303Key[]{BZ_C062	,BZ_C063	,BZ_C064})
	,AD07 ("Total"						,new Mod303Key[]{BZ_C065	,BZ_C066	,BZ_C067})
	,AD08 ("Gastos",null,TITLE)
	,AD09 ("4%"							,new Mod303Key[]{BZ_C068	,BZ_C069	,BZ_C070})
	,AD10 ("10%"						,new Mod303Key[]{BZ_C071	,BZ_C072	,BZ_C073})
	,AD11 ("21%"						,new Mod303Key[]{BZ_C074	,BZ_C075	,BZ_C076})
	,AD13 (null							,new Mod303Key[]{BZ_C077	,BZ_C078	,BZ_C079})
	,AD14 ("Total"						,new Mod303Key[]{BZ_C080	,BZ_C081	,BZ_C082})
	,AD15 ("Bienes de inversi\u00F3n",null,TITLE)
	,AD16 ("4%"							,new Mod303Key[]{BZ_C083	,BZ_C084	,BZ_C085})
	,AD17 ("10%"						,new Mod303Key[]{BZ_C086	,BZ_C087	,BZ_C088})
	,AD18 ("21%"						,new Mod303Key[]{BZ_C089	,BZ_C090	,BZ_C091})
	,AD19 (null							,new Mod303Key[]{BZ_C092	,BZ_C093	,BZ_C094})
	,AD20 ("Total"						,new Mod303Key[]{BZ_C095	,BZ_C096	,BZ_C097})
	,AD21 ("Totales"					,new Mod303Key[]{BZ_C098	,BZ_C099	,BZ_C100})
	
	,ADC00 ("Exclusivamente para sujetos pasivos acogidos al r\u00E9gimen especial del criterio de caja y para "+ 
			"destinatarios/as de operaciones afectadas por el mismo",null,TITLE)
	,ADC01 (getDescription(BZ_C200) 	,new Mod303Key[]{BZ_C200	,null		,BZ_C201})	
	,ADC02 (getDescription(BZ_C202) 	,new Mod303Key[]{BZ_C202	,null		,BZ_C203})
	,EMPTY7(null						,null)
	,OPE00 ("Operaciones espec\u00EDficas",null,TITLE)
	,OPE01 (BZ_C104.getDescription()	,new Mod303Key[]{BZ_C104,null		,null		})
	,OPE02 (BZ_C105.getDescription()	,new Mod303Key[]{BZ_C105,null		,null		})
	,OPE03 (BZ_C106.getDescription()	,new Mod303Key[]{BZ_C106,null		,null		})
	,OPE04 (BZ_C107.getDescription()	,new Mod303Key[]{BZ_C107,null		,null		})
	,OPE05 (BZ_C108.getDescription()	,new Mod303Key[]{BZ_C108,null		,null		})
	
	;
	
	private String label;
	private Mod303Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model3032017PrintBIZKAIAScript(String label, Mod303Key[] keys,FiscalModelKeyInfo ... infoKeys) {
		this.label = label;
		this.keys = keys;
		this.infoKeys = infoKeys;
	}

	private static String getDescription(Mod303Key key) {
		return AonStringUtils.trim(AonStringUtils.substringBefore(key.getDescription(), AonStringUtils.HYPHEN));
	}

	@Override
	public String getLabel() {
		return label;
	}
	@Override
	public Mod303Key[] getKeys() {
		return keys;
	}
	@Override
	public boolean isEnabled() {
		return getInfoKeys()!= null && getInfoKeys().length > 0 && getInfoKeys()[0] != COMPUTE && getInfoKeys()[0] != TITLE;
	}
	@Override
	public boolean isTitle() {
		return getInfoKeys()!= null && getInfoKeys().length > 0 && getInfoKeys()[0] == TITLE;
	}
	@Override
	public FiscalModelKeyInfo[] getInfoKeys() {
		return infoKeys;
	};
	
	@Override
	public boolean hasGraphicParticularity() {
		return false;
	}

	@Override
	public boolean paintHeaderBefore() {
		return (this == AD000);
	};
}
