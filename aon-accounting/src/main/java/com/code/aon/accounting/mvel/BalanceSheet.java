package com.code.aon.accounting.mvel;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.accounting.enumeration.BalanceType;
import com.code.aon.common.enumeration.IResourceable;

public enum BalanceSheet implements IResourceable{

	BAL_ABR ( 1, BalanceType.CLOSING, "bal", "pgc07abreviado", "xml_report.ftl", "xml_bal_abr.ftl", new BalanceKey[] 

			{BalanceKey.C11000
			,BalanceKey.C11100
			,BalanceKey.C11200
			,BalanceKey.C11300
			,BalanceKey.C11400
			,BalanceKey.C11500
			,BalanceKey.C11600
			,BalanceKey.C11700
			,BalanceKey.C12000
			,BalanceKey.C12100
			,BalanceKey.C12200
			,BalanceKey.C12300
			,BalanceKey.C12380
			,BalanceKey.C12381
			,BalanceKey.C12382
			,BalanceKey.C12370
			,BalanceKey.C12390
			,BalanceKey.C12400
			,BalanceKey.C12500
			,BalanceKey.C12600
			,BalanceKey.C12700
			,BalanceKey.C10000
			,BalanceKey.C20000
			,BalanceKey.C21000
			,BalanceKey.C21100
			,BalanceKey.C21110
			,BalanceKey.C21120
			,BalanceKey.C21200
			,BalanceKey.C21300
			,BalanceKey.C21400
			,BalanceKey.C21500
			,BalanceKey.C21600
			,BalanceKey.C21700
			,BalanceKey.C21800
			,BalanceKey.C21900
			,BalanceKey.C22000
			,BalanceKey.C23000
			,BalanceKey.C31000
			,BalanceKey.C31100
			,BalanceKey.C31200
			,BalanceKey.C31220
			,BalanceKey.C31230
			,BalanceKey.C31290
			,BalanceKey.C31300
			,BalanceKey.C31400
			,BalanceKey.C31500
			,BalanceKey.C31600
			,BalanceKey.C31700
			,BalanceKey.C32000
			,BalanceKey.C32100
			,BalanceKey.C32200
			,BalanceKey.C32300
			,BalanceKey.C32320
			,BalanceKey.C32330
			,BalanceKey.C32390
			,BalanceKey.C32400
			,BalanceKey.C32500
			,BalanceKey.C32580
			,BalanceKey.C32581
			,BalanceKey.C32582
			,BalanceKey.C32590
			,BalanceKey.C32600
			,BalanceKey.C32700
			,BalanceKey.C30000})
		
	// Balance de situación normal
	,BAL_NOR ( 1, BalanceType.CLOSING, "bal","pgc07normal","xml_report.ftl", "xml_bal_nor.ftl", new BalanceKey[] 
			{BalanceKey.B11000
			,BalanceKey.B11100
			,BalanceKey.B11110
			,BalanceKey.B11120
			,BalanceKey.B11130
			,BalanceKey.B11140
			,BalanceKey.B11150
			,BalanceKey.B11160
			,BalanceKey.B11170
			,BalanceKey.B11200
			,BalanceKey.B11210
			,BalanceKey.B11220
			,BalanceKey.B11230
			,BalanceKey.B11300
			,BalanceKey.B11310
			,BalanceKey.B11320
			,BalanceKey.B11400
			,BalanceKey.B11410
			,BalanceKey.B11420
			,BalanceKey.B11430
			,BalanceKey.B11440
			,BalanceKey.B11450
			,BalanceKey.B11460
			,BalanceKey.B11500
			,BalanceKey.B11510
			,BalanceKey.B11520
			,BalanceKey.B11530
			,BalanceKey.B11540
			,BalanceKey.B11550
			,BalanceKey.B11560
			,BalanceKey.B11600
			,BalanceKey.B11700
			,BalanceKey.B12000
			,BalanceKey.B12100
			,BalanceKey.B12200
			,BalanceKey.B12210
			,BalanceKey.B12220
			,BalanceKey.B12230
			,BalanceKey.B12231
			,BalanceKey.B12232
			,BalanceKey.B12240
			,BalanceKey.B12241
			,BalanceKey.B12242
			,BalanceKey.B12250
			,BalanceKey.B12260
			,BalanceKey.B12300
			,BalanceKey.B12310
			,BalanceKey.B12311
			,BalanceKey.B12312
			,BalanceKey.B12320
			,BalanceKey.B12330
			,BalanceKey.B12340
			,BalanceKey.B12350
			,BalanceKey.B12360
			,BalanceKey.B12370
			,BalanceKey.B12400
			,BalanceKey.B12410
			,BalanceKey.B12420
			,BalanceKey.B12430
			,BalanceKey.B12440
			,BalanceKey.B12450
			,BalanceKey.B12460
			,BalanceKey.B12500
			,BalanceKey.B12510
			,BalanceKey.B12520
			,BalanceKey.B12530
			,BalanceKey.B12540
			,BalanceKey.B12550
			,BalanceKey.B12560
			,BalanceKey.B12600
			,BalanceKey.B12700
			,BalanceKey.B12710
			,BalanceKey.B12720
			,BalanceKey.B10000
			,BalanceKey.B20000
			,BalanceKey.B21000
			,BalanceKey.B21100
			,BalanceKey.B21110
			,BalanceKey.B21120
			,BalanceKey.B21200
			,BalanceKey.B21300
			,BalanceKey.B21310
			,BalanceKey.B21320
			,BalanceKey.B21400
			,BalanceKey.B21500
			,BalanceKey.B21510
			,BalanceKey.B21520
			,BalanceKey.B21600
			,BalanceKey.B21700
			,BalanceKey.B21800
			,BalanceKey.B21900
			,BalanceKey.B22000
			,BalanceKey.B22100
			,BalanceKey.B22200
			,BalanceKey.B22300
			,BalanceKey.B22400
			,BalanceKey.B22500
			,BalanceKey.B23000
			,BalanceKey.B31000
			,BalanceKey.B31100
			,BalanceKey.B31110
			,BalanceKey.B31120
			,BalanceKey.B31130
			,BalanceKey.B31140
			,BalanceKey.B31200
			,BalanceKey.B31210
			,BalanceKey.B31220
			,BalanceKey.B31230
			,BalanceKey.B31240
			,BalanceKey.B31250
			,BalanceKey.B31300
			,BalanceKey.B31400
			,BalanceKey.B31500
			,BalanceKey.B31600
			,BalanceKey.B31700
			,BalanceKey.B32000
			,BalanceKey.B32100
			,BalanceKey.B32200
			,BalanceKey.B32300
			,BalanceKey.B32310
			,BalanceKey.B32320
			,BalanceKey.B32330
			,BalanceKey.B32340
			,BalanceKey.B32350
			,BalanceKey.B32400
			,BalanceKey.B32500
			,BalanceKey.B32510
			,BalanceKey.B32511
			,BalanceKey.B32512
			,BalanceKey.B32520
			,BalanceKey.B32530
			,BalanceKey.B32540
			,BalanceKey.B32550
			,BalanceKey.B32560
			,BalanceKey.B32570
			,BalanceKey.B32600
			,BalanceKey.B32700
			,BalanceKey.B30000})
			
	// Cuenta de Pérdidas y Ganancias Abreviada
	,PYG_ABR ( 1, BalanceType.OPERATING , "pyg","pgc07abreviado","xml_report.ftl", "xml_pyg_abr.ftl", new BalanceKey[] 
			{BalanceKey.C40100
			,BalanceKey.C40200
			,BalanceKey.C40300
			,BalanceKey.C40400
			,BalanceKey.C40500
			,BalanceKey.C40600
			,BalanceKey.C40700
			,BalanceKey.C40800
			,BalanceKey.C40900
			,BalanceKey.C41000
			,BalanceKey.C41100
			,BalanceKey.C41200
			,BalanceKey.C41300
			,BalanceKey.C49100
			,BalanceKey.C41400
			,BalanceKey.C41430
			,BalanceKey.C41490
			,BalanceKey.C41500
			,BalanceKey.C41600
			,BalanceKey.C41700
			,BalanceKey.C41800
			,BalanceKey.C42100
			,BalanceKey.C42110
			,BalanceKey.C42120
			,BalanceKey.C42130
			,BalanceKey.C49200
			,BalanceKey.C49300
			,BalanceKey.C41900
			,BalanceKey.C49500})
			
	// Cuenta de Pérdidas y Ganancias Normal
	,PYG_NOR ( 1, BalanceType.OPERATING , "pyg","pgc07normal","xml_report.ftl","xml_pyg_nor.ftl", new BalanceKey[] 
			{BalanceKey.B40100
			,BalanceKey.B40110
			,BalanceKey.B40120
			,BalanceKey.B40200
			,BalanceKey.B40300
			,BalanceKey.B40400
			,BalanceKey.B40410
			,BalanceKey.B40420
			,BalanceKey.B40430
			,BalanceKey.B40440
			,BalanceKey.B40500
			,BalanceKey.B40510
			,BalanceKey.B40520
			,BalanceKey.B40600
			,BalanceKey.B40610
			,BalanceKey.B40620
			,BalanceKey.B40630
			,BalanceKey.B40700
			,BalanceKey.B40710
			,BalanceKey.B40720
			,BalanceKey.B40730
			,BalanceKey.B40740
			,BalanceKey.B40800
			,BalanceKey.B40900
			,BalanceKey.B41000
			,BalanceKey.B41100
			,BalanceKey.B41110
			,BalanceKey.B41120
			,BalanceKey.B41200
			,BalanceKey.B41300
			,BalanceKey.B49100
			,BalanceKey.B41400
			,BalanceKey.B41410
			,BalanceKey.B41411
			,BalanceKey.B41412
			,BalanceKey.B41420
			,BalanceKey.B41421
			,BalanceKey.B41422
			,BalanceKey.B41430
			,BalanceKey.B41500
			,BalanceKey.B41510
			,BalanceKey.B41520
			,BalanceKey.B41530
			,BalanceKey.B41600
			,BalanceKey.B41610
			,BalanceKey.B41620
			,BalanceKey.B41700
			,BalanceKey.B41800
			,BalanceKey.B41810
			,BalanceKey.B41820
			,BalanceKey.B42100
			,BalanceKey.B42110
			,BalanceKey.B42120
			,BalanceKey.B42130
			,BalanceKey.B49200
			,BalanceKey.B49300
			,BalanceKey.B41900
			,BalanceKey.B49400
			,BalanceKey.B42000
			,BalanceKey.B49500})
	;
	
	private int id;
	private BalanceType type;
	private String module;
	private String moduleId;
	private String mainTemplate;
	private String template;
	private BalanceKey[] keys;
	
    private static final String MSG_KEY_PREFIX = "aon_enum_balance_sheet_";

	private BalanceSheet(int id, BalanceType type, String module,String moduleId, String mainTemplate, String template,BalanceKey[] keys) {
		this.id = id;
		this.type = type;
		this.module = module;
		this.moduleId = moduleId;
		this.mainTemplate = mainTemplate; 
		this.template = template;
		this.keys = keys;
	}
	
    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }

	public int getId() {
		return id;
	}
	public BalanceType getType() {
		return type;
	}
	public String getModule() {
		return module;
	}
	public String getModuleId() {
		return moduleId;
	}
	public String getTemplate() {
		return template;
	}
	public BalanceKey[] getKeys() {
		return keys;
	}

	public String getMainTemplate() {
		return mainTemplate;
	}
	
}
