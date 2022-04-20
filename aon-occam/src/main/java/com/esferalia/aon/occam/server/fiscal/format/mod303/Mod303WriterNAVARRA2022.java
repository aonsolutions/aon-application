package com.esferalia.aon.occam.server.fiscal.format.mod303;

import java.io.IOException;
import java.io.Writer;

import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.server.fiscal.format.AonFiscalFileUtils;
import com.esferalia.aon.occam.server.fiscal.format.mod303.Mod303Writer.IMod303Writer;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.Pair;

public class Mod303WriterNAVARRA2022 implements IMod303Writer{

	private static final Mod303Key[] KEYS = new Mod303Key[] {
		 Mod303Key.NF_010		,Mod303Key.NF_001		,Mod303Key.NF_002		,Mod303Key.NF_194
		,Mod303Key.NF_171		,Mod303Key.NF_003		,Mod303Key.NF_013		,Mod303Key.NF_004
		,Mod303Key.NF_014		,Mod303Key.NF_005		,Mod303Key.NF_015		,Mod303Key.NF_006
		,Mod303Key.NF_016		,Mod303Key.NF_051		,Mod303Key.NF_052		,Mod303Key.NF_007
		,Mod303Key.NF_017		,Mod303Key.NF_008		,Mod303Key.NF_018		,Mod303Key.NF_009
		,Mod303Key.NF_019		,Mod303Key.NF_172		,Mod303Key.NF_173		,Mod303Key.NF_176
		,Mod303Key.NF_177		,Mod303Key.NF_020
		
		,Mod303Key.NF_031		,Mod303Key.NF_041		,Mod303Key.NF_131		,Mod303Key.NF_141
		,Mod303Key.NF_032		,Mod303Key.NF_042		,Mod303Key.NF_039		,Mod303Key.NF_049
		,Mod303Key.NF_170		,Mod303Key.NF_043		,Mod303Key.NF_174		,Mod303Key.NF_175
		,Mod303Key.NF_178		,Mod303Key.NF_179		,Mod303Key.NF_045		,Mod303Key.NF_450
		,Mod303Key.NF_055		,Mod303Key.NF_061		,Mod303Key.NF_062		,Mod303Key.NF_069
		,Mod303Key.NF_063
	};	

	public void fillWriter(Mod303 mod, Writer wr) throws IOException {
		Pair<Integer,String> data = getData(mod);
		
		wr.append("0")
			.append("F69")
			.append(AonFiscalFileUtils.unsigned(mod.getYear(), 4,0))
			.append(AonFiscalFileUtils.text(AonStringUtils.defaultString(mod.getDescription(Mod303Key.NF_I00), mod.getDocument()),9))
			.append(AonFiscalFileUtils.text(AonStringUtils.defaultString(mod.getDescription(Mod303Key.NF_I01), mod.getName()),40))
			.append(AonFiscalFileUtils.spaces(52))
			.append(AonFiscalFileUtils.unsigned(1, 8))				// Registro tipo 1
			.append(AonFiscalFileUtils.unsigned(data.getLeft(), 8))	// Registro tipo 2
			.append(AonFiscalFileUtils.unsigned(0, 8))				// Registro tipo 3
			.append(AonFiscalFileUtils.unsigned(0, 8))				// Registro tipo 4
			.append(AonFiscalFileUtils.unsigned(0, 8))				// Registro tipo 5
			.append(AonFiscalFileUtils.unsigned(0, 8))				// Registro tipo 6
			.append("T")
			.append(AonFiscalFileUtils.text(mod.getContactPhone(),9))
			.append(AonFiscalFileUtils.text(mod.getContactPerson(),40))
			.append(AonFiscalFileUtils.spaces(30))
			.append(AonFiscalFileUtils.spaces(13))
			.append(AonStringUtils.CR_LF);
		 wr.append("1")
			.append("F69")
			.append(AonFiscalFileUtils.unsigned(mod.getYear(), 4,0))
			.append(AonFiscalFileUtils.text(mod.getPeriod().getFormatName( mod.getAdministration() ), 1))
			.append("00")
			.append(AonFiscalFileUtils.zeros(6))
			.append(AonFiscalFileUtils.text(mod.getDocument(),9))
			.append(AonFiscalFileUtils.text(mod.getName(),40))
			.append(AonFiscalFileUtils.zeros(14))
			.append("T")
			.append(AonFiscalFileUtils.text(mod.getContactPhone(),9))
			.append(AonFiscalFileUtils.text(mod.getContactPerson(),40))
			.append(AonFiscalFileUtils.spaces(13))
			.append(" ")
			.append(AonFiscalFileUtils.text(mod.isReplacement()?"S":" ",1))
			.append(mod.isReplacement()
	  			?AonFiscalFileUtils.unsigned(mod.getReplacedNumber(), 13,0)
	  			:AonFiscalFileUtils.zeros(13))
			.append(AonFiscalFileUtils.number(mod.getFinanceCCC(),20))
			.append((mod.getDeclarationResultType() == FiscalModelDeclarationType.BANK)?"7":"0")
			.append(AonFiscalFileUtils.spaces(22))
			.append(AonFiscalFileUtils.zeros(8))
			.append(AonFiscalFileUtils.spaces(28))
			.append(AonFiscalFileUtils.spaces(13))
			.append(AonStringUtils.CR_LF);
		 
		 wr.append(data.getRight());

		
	}
	
	private static void writeRecord(Mod303 mod,StringBuilder wr ) {
		wr.append("2")
			.append("F69")
			.append(AonFiscalFileUtils.unsigned(mod.getYear(), 4,0))
			.append(AonFiscalFileUtils.text(mod.getPeriod().getFormatName( mod.getAdministration() ), 1))
			.append("00")
			.append(AonFiscalFileUtils.zeros(6))
			.append(AonFiscalFileUtils.text(mod.getDocument(),9));
	}
	
	private static void writeKey(Mod303 mod,Mod303Key key,StringBuilder wr) {
		writeKey((key==null?"0":key.getBoxAsString())
				,(key==null?0.0:mod.getAmount(key))
				,wr);
	}
	
	private static void writeKey(String box, Double value,StringBuilder wr) {
		wr.append(AonFiscalFileUtils.number( box, 4))
		  .append(AonFiscalFileUtils.signedSpace( value,16,2));
	}

	private static Pair<Integer,String> getData(Mod303 mod) {
		StringBuilder wr = new StringBuilder();
		int records = 0;
		int i = 0;
		writeRecord(mod, wr);
		records++;
		
		writeKey("129", 0.0, wr);
		i++;
		writeKey("93", (mod.isWithoutActivity()?1.0:0.0), wr);		
		i++;
		writeKey("264", 0.0, wr);
		i++;
		writeKey("265", 1.0, wr);
		i++;
		writeKey("266", 0.0, wr);
		i++;
		writeKey("267", 1.0, wr);
		i++;
		
		for (Mod303Key key : KEYS ) {
			if (AonMathUtils.isNotZero(mod.getAmount(key))) {
				if (i == 0) {
					writeRecord(mod, wr);
					records++;
				}
				writeKey(mod, key, wr);
				i++;
				if (i==10) {
					wr.append(AonFiscalFileUtils.spaces(24))
						.append(AonStringUtils.CR_LF);
					i = 0;
				}
			}
		}
		if (i > 0) {
			for (;i<10;i++) {
				writeKey(mod, null, wr);
			}
			wr.append(AonFiscalFileUtils.spaces(24))
				.append(AonStringUtils.CR_LF);
		}
		return new Pair<>(records, wr.toString());
	}
	
}

