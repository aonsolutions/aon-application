
package com.esferalia.aon.occam.jooq.test;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.accounting.AccMiningParameters;
import com.esferalia.aon.occam.api.model.accounting.IAccMiningKeyAccept;
import com.esferalia.aon.occam.server.accounting.AccMiningMVELContext;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AccMiningMVELTest  {
	public enum BalanceKey {
		// BALANCE: ACTIVO
		 BA101(101)
		,BA102(102)
		,BA103(103)
		,BA104(104)
		,BA105(105)
		,BA106(106)
		,BA107(107)
		,BA108(108)
		,BA700(700)
		,BA701(701)
		,BA109(109)
		,BA110(110)
		,BA111(111)
		,BA112(112)
		,BA113(113)
		,BA114(114)
		,BA115(115)
		,BA116(116)
		,BA117(117)
		,BA118(118)
		,BA119(119)
		,BA120(120)
		,BA121(121)
		,BA122(122)
		,BA123(123)
		,BA124(124)
		,BA125(125)
		,BA126(126)
		,BA127(127)
		,BA128(128)
		,BA129(129)
		,BA130(130)
		,BA131(131)
		,BA132(132)
		,BA133(133)
		,BA134(134)
		,BA135(135)
		,BA136(136)
		,BA137(137)
		,BA138(138)
		,BA139(139)
		,BA140(140)
		,BA141(141)
		,BA142(142)
		,BA143(143)
		,BA144(144)
		,BA145(145)
		,BA146(146)
		,BA147(147)
		,BA148(148)
		,BA149(149)
		,BA150(150)
		,BA151(151)
		,BA152(152)
		,BA153(153)
		,BA154(154)
		,BA155(155)
		,BA156(156)
		,BA157(157)
		,BA158(158)
		,BA159(159)
		,BA160(160)
		,BA161(161)
		,BA162(162)
		,BA163(163)
		,BA164(164)
		,BA165(165)
		,BA166(166)
		,BA167(167)
		,BA168(168)
		,BA169(169)
		,BA170(170)
		,BA171(171)
		,BA172(172)
		,BA173(173)
		,BA174(174)
		,BA175(175)
		,BA176(176)
		,BA177(177)
		,BA178(178)
		,BA179(179)
		,BA180(180)
		// PATRIMONIO NETO Y PASIVO
		,BP185(185)
		,BP186(186)
		,BP187(187)
		,BP188(188)
		,BP189(189)
		,BP190(190)
		,BP191(191)
		,BP192(192)
		,BP193(193)
		,BP702(702)
		,BP194(194)
		,BP195(195)
		,BP196(196)
		,BP197(197)
		,BP198(198)
		,BP199(199)
		,BP200(200)
		,BP201(201)
		,BP202(202)
		,BP203(203)
		,BP204(204)
		,BP205(205)
		,BP206(206)
		,BP207(207)
		,BP208(208)
		,BP209(209)
		,BP210(210)
		,BP211(211)
		,BP212(212)
		,BP213(213)
		,BP214(214)
		,BP215(215)
		,BP216(216)
		,BP217(217)
		,BP218(218)
		,BP219(219)
		,BP220(220)
		,BP221(221)
		,BP222(222)
		,BP223(223)
		,BP224(224)
		,BP225(225)
		,BP226(226)
		,BP227(227)
		,BP228(228)
		,BP229(229)
		,BP230(230)
		,BP703(703)
		,BP704(704)
		,BP231(231)
		,BP232(232)
		,BP233(233)
		,BP234(234)
		,BP235(235)
		,BP236(236)
		,BP237(237)
		,BP238(238)
		,BP239(239)
		,BP240(240)
		,BP241(241)
		,BP242(242)
		,BP243(243)
		,BP244(244)
		,BP245(245)
		,BP246(246)
		,BP247(247)
		,BP248(248)
		,BP249(249)
		,BP250(250)
		,BP251(251)
		,BP252(252)
		// PERDIDAS Y GANANCIAS
		,PG255(255)
		,PG256(256)
		,PG257(257)
		,PG705(705)
		,PG706(706)
		,PG707(707)
		,PG708(708)
		,PG258(258)
		,PG259(259)
		,PG260(260)
		,PG261(261)
		,PG262(262)
		,PG263(263)
		,PG264(264)
		,PG265(265)
		,PG266(266)
		,PG267(267)
		,PG268(268)
		,PG269(269)
		,PG270(270)
		,PG271(271)
		,PG273(273)
		,PG274(274)
		,PG275(275)
		,PG276(276)
		,PG277(277)
		,PG278(278)
		,PG279(279)
		,PG280(280)
		,PG281(281)
		,PG282(282)
		,PG283(283)
		,PG709(709)
		,PG284(284)
		,PG285(285)
		,PG286(286)
		,PG287(287)
		,PG288(288)
		,PG289(289)
		,PG290(290)
		,PG291(291)
		,PG292(292)
		,PG293(293)
		,PG710(710)
		,PG294(294)
		,PG295(295)
		,PG296(296)
		,PG297(297)
		,PG298(298)
		,PG299(299)
		,PG300(300)
		,PG301(301)
		,PG302(302)
		,PG303(303)
		,PG304(304)
		,PG305(305)
		,PG306(306)
		,PG307(307)
		,PG308(308)
		,PG309(309)
		,PG310(310)
		,PG311(311)
		,PG312(312)
		,PG313(313)
		,PG314(314)
		,PG315(315)
		,PG316(316)
		,PG317(317)
		,PG318(318)
		,PG319(319)
		,PG320(320)
		,PG321(321)
		,PG322(322)
		,PG323(323)
		,PG329(329)
		,PG330(330)
		,PG331(331)
		,PG332(332)
		,PG324(324)
		,PG325(325)
		,PG326(326)
		,PG327(327)
		,PG328(328)
		,PG500(500)
		// ESTADO DE CAMBIOS EN EL PATRIMONIO NETO. ESTADO DE INGRESOS Y GASTOS RECONOCIDOS EN EL EJERCICIO
		,T0500(500)
		,T0336(336)
		,T0337(337)
		,T0338(338)
		,T0339(339)
		,T0340(340)
		,T0341(341)
		,T0342(342)
		,T0343(343)
		,T0344(344)
		,T0345(345)
		,T0346(346)
		,T0347(347)
		,T0348(348)
		,T0349(349)
		,T0350(350)
		,T0351(351)
		,T0352(352)
		,T0353(353)
		,T0354(354)
		,T0355(355)
		;
		

		private int code;
		
		private BalanceKey(int code) {
			this.code = code;
		}

		public int getCode() {
			return code;
		}
	}
	
	public static Map<String,String> INITIALIZE_EXPRESSION_MAP = new LinkedHashMap<String,String>();
	
	static { // BALANCE: ACTIVO
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BA103.toString(),"NORMAL?sdb({201})-sab({2801,2901}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BA104.toString(),"NORMAL?sdb({202})-sab({2802,2902}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BA105.toString(),"NORMAL?sdb({203})-sab({2803,2903}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BA106.toString(),"sdb({204})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BA107.toString(),"NORMAL?sdb({206})-sab({2806,2906}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BA108.toString(),"NORMAL?sdb({200})-sab({2800}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BA109.toString(),"NORMAL?sdb({205,209})-sab({2805,2905}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BA110.toString(),"NORMAL?0.0:sdb({201,202,203,205,206,207,208,209})-sab({280,290})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BA111.toString(),"NORMAL?0.0:sdb({210,211,212,213,214,215,216,217,218,219,23})-sab({2812,2813,2814,2815,2816,2817,2818,2819,2912,2913,2914,2915,2916,2917,2918,2919,2811,2910,2911})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BA112.toString(),"NORMAL?sdb({210,211})-sab({2811,2910,2911}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BA113.toString(),"NORMAL?sdb({212,213,214,215,216,217,218,219})-sab({2812,2813,2814,2815,2816,2817,2818,2819,2912,2913,2914,2915,2916,2917,2918,2919}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BA114.toString(),"NORMAL?sdb({23}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BA115.toString(),"NORMAL?0.0:sdb({22})-sab({282,292})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BA116.toString(),"NORMAL?sdb({220})-sab({2920}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BA117.toString(),"NORMAL?sdb({221})-sab({282,2921}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BA119.toString(),"sdb({2403,2404})-sab({2493,2494,293})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BA120.toString(),"NORMAL?sdb({2423,2424})-sab({2953,2954}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BA121.toString(),"NORMAL?sdb({2413,2414})-sab({2943,2944}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BA125.toString(),"NORMAL?0.0:sdb({2423,2424,2413,2414})-sab({2953,2954,2943,2944})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BA127.toString(),"sdb({2405,250})-sab({2495,259}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BA128.toString(),"NORMAL?sdb({2425,252,253,254})-sab({2955,298}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BA129.toString(),"NORMAL?sdb({2415,251})-sab({2945,297}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BA130.toString(),"NORMAL?sdb({255}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BA131.toString(),"NORMAL?sdb({258,26}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BA132.toString(),"NORMAL?sdb({257}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BA133.toString(),"NORMAL?0.0:sdb({2425,252,253,254,2415,251,258,26,257,255})-sab({2955,298,2945,297})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BA134.toString(),"sdb({474})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BA137.toString(),"(NORMAL || ABREVIADO)?0.0:sdb({580,581,582,583,584}) - sab({599})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BA138.toString(),"NORMAL?0.0:sdb({30,31,32,33,34,35,36,407})-sab({(39)})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BA139.toString(),"NORMAL?sdb({30})-sab({390}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BA140.toString(),"NORMAL?sdb({31,32})-sab({391,392}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BA143.toString(),"NORMAL?sdb({33,34})-sab({393,394}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BA146.toString(),"NORMAL?sdb({35})-sab({395}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BA147.toString(),"NORMAL?sdb({36})-sab({396}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BA148.toString(),"NORMAL?sdb({407}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BA151.toString(),"sdb({430,431,432,435,436})-sab({437,490,4935})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BA153.toString(),"NORMAL?sdb({433,434})-sab({4933,4934}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BA154.toString(),"NORMAL?sdb({44}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BA155.toString(),"NORMAL?sdb({460,544}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BA156.toString(),"NORMAL?sdb({4709}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BA157.toString(),"NORMAL?sdb({4700,4708,471,472}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BA158.toString(),"sdb({5580})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BA159.toString(),"NORMAL?0.0:sdb({44,460,470,471,472,544})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BA161.toString(),"sdb({5303,5304})-sab({5393,5394,593})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BA162.toString(),"NORMAL?sdb({5323,5324,5343,5344})-sab({5953,5954}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BA163.toString(),"NORMAL?sdb({5313,5314,5333,5334})-sab({5943,5944}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BA165.toString(),"NORMAL?sdb({5353,5354,5524})+sdbPositivo({5523}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BA167.toString(),"NORMAL?0.0:sdb({5313,5314, 5323, 5324,5333, 5334, 5343,5344, 5353, 5354,5524})+sdbPositivo({5523})-sab({5943,5944,5953,5954})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BA169.toString(),"sdb({5305,540})-sab({5395,549})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BA170.toString(),"NORMAL?sdb({5325,5345,542,543,547})-sab({5955,598}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BA171.toString(),"NORMAL?sdb({5315,5335,541,546})-sab({5945,597}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BA172.toString(),"NORMAL?sdb({5590,5593}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BA173.toString(),"NORMAL?sdb({5355,545,548,5525,565,566})+sdbPositivo({551}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BA175.toString(),"NORMAL?0.0:sdb({5325,5345,542,543,547,5315,5335,541,546,5590,5593,5355,545,548,5525,565,566})-sab({5955,598,5945,597})+sdbPositivo({551})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BA176.toString(),"sdb({480,567})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BA177.toString(),"NORMAL?0.0:sdb({570,571,572,573,574,575,576}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BA178.toString(),"NORMAL?sdb({570,571,572,573,574,575}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BA179.toString(),"NORMAL?sdb({576}):0.0");
	}
	
	static { // BALANCE: PATRIMONIO NETO Y PASIVO
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BP188.toString(),"sab({100,101,102})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BP189.toString(),"sdb({1030,1040})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BP190.toString(),"sab({110})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BP191.toString(),"NORMAL?0.0:sab({112,113,114,115,119})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BP192.toString(),"NORMAL?sab({112,1141}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BP193.toString(),"NORMAL?sab({113,1140,1142,1143,1144,115,119}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BP194.toString(),"sdb({108,109})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BP195.toString(),"NORMAL?0.0:sab({120})-sdb({121})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BP196.toString(),"NORMAL?sab({120}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BP197.toString(),"NORMAL?sab({121}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BP198.toString(),"sab({118})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BP199.toString(),"sab({129})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BP200.toString(),"sdb({557})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BP201.toString(),"sab({111})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BP202.toString(),"NORMAL?0.0:sab({1340,136,135,137})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BP203.toString(),"NORMAL?sab({133}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BP204.toString(),"NORMAL?sab({1340}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BP205.toString(),"NORMAL?sab({136}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BP206.toString(),"NORMAL?sab({135}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BP207.toString(),"NORMAL?sab({137}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BP209.toString(),"sab({130,131,132}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BP211.toString(),"NORMAL?0.0:sab({14})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BP212.toString(),"NORMAL?sab({140}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BP213.toString(),"NORMAL?sab({145}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BP214.toString(),"NORMAL?sab({146}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BP215.toString(),"NORMAL?sab({141,142,143,147}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BP217.toString(),"NORMAL?sab({177,178,179}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BP218.toString(),"sab({1605,170})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BP219.toString(),"sab({1625,174})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BP220.toString(),"NORMAL?sab({176}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BP221.toString(),"NORMAL?sab({1615,1635,171,172,173,175,180,185,189}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BP222.toString(),"NORMAL?0.0:sab({1615,1635,171,172,173,175,176,177,178,179,180,185,189})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BP223.toString(),"sab({1603,1604,1613,1614,1623,1624,1633,1634})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BP224.toString(),"sab({479})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BP225.toString(),"sab({181})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BP227.toString(),"sab({15})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BP229.toString(),"NORMAL?sab({585,586,587,588,589}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BP230.toString(),"NORMAL?0.0:sab({499,529})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BP704.toString(),"NORMAL?sab({499,529}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BP232.toString(),"NORMAL?sab({500,501,505,506}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BP233.toString(),"sab({5105,520,527})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BP234.toString(),"sab({5125,524})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BP235.toString(),"NORMAL?sab({5595,5598}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BP236.toString(),"NORMAL?sab({194,509,5115,5135,5145,521,522,523,525,526,528,5525,555,5565,5566,560,561,569})+sabPositivo({551})-sdb({1034,1044,190,192}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BP237.toString(),"NORMAL?0.0:sab({194,500,501,505,506,509,5115,5135,5145,521,522,523,525,526,528,525,555,5565,5566,5595,5598,560,561,569})+sabPositivo({551})-sdb({1034,1044,190,192})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BP238.toString(),"sab({5103,5104,5113,5114,5123,5124,5133,5134,5143,5144,5524,5563,5564})+sabPositivo({5523})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BP242.toString(),"sab({400,401,405})-sdb({406})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BP243.toString(),"NORMAL?sab({403,404}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BP244.toString(),"NORMAL?sab({41}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BP245.toString(),"NORMAL?sab({465,466}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BP246.toString(),"NORMAL?sab({4752}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BP247.toString(),"NORMAL?sab({4750,4751,4758,476,477}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BP248.toString(),"NORMAL?sab({438}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BP249.toString(),"NORMAL?0.0:sab({41,438,465,466,475,476,477})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BP250.toString(),"sab({485,568})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.BP251.toString(),"sab({502,507})");
	}
	static { // BALANCE DE PERDIDAS Y GANANCIAS 
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.PG255.toString(),"PYMES?0.0:sap({700,701,702,703,704,705})-sdp({706,708,709})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.PG256.toString(),"PYMES?sap({700,701,702,703,704})-sdp({706,708,709}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.PG257.toString(),"PYMES?sap({705}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.PG258.toString(),"sap({71,7930})-sdp({6930})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.PG259.toString(),"sap({73})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.PG260.toString(),"PYMES?0.0"
				+ ":sap({606,608,609,61,7931,7932,7933})-sdp({600,601,602,607,6931,6932,6933})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.PG261.toString(),"PYMES?sap({6060,6080,6090,610})-sdp({600}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.PG262.toString(),"PYMES?sap({6061,6062,6081,6082,6091,6092,611,612})-sdp({601,602}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.PG263.toString(),"PYMES?sap({607}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.PG264.toString(),"PYMES?sap({7931,7932,7933})-sdp({6931,6932,6933}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.PG267.toString(),"sap({752})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.PG268.toString(),"sap({751,753,754,755,756,757,758,759})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.PG269.toString(),"sap({740,747})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.PG271.toString(),"sap({640})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.PG273.toString(),"sap({641})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.PG274.toString(),"sap({642})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.PG275.toString(),"sap({643})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.PG276.toString(),"sap({6450})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.PG277.toString(),"sap({649})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.PG278.toString(),"sap({7950,7957})-sdp({644,6457})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.PG279.toString(),"PYMES?0.0:sap({636,639,794,7954})-sdp({62,631,634,65,694,695})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.PG280.toString(),"PYMES?sap({62}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.PG281.toString(),"PYMES?sap({636,639})-sdp({631,634}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.PG282.toString(),"PYMES?sap({794,7954})-sdp({650,694,695}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.PG283.toString(),"PYMES?sap({651,659}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.PG284.toString(),"sap({68})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.PG285.toString(),"sap({746})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.PG286.toString(),"sap({7951,7952,7955,7956})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.PG289.toString(),"sap({690,691,692})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.PG290.toString(),"sap({790,791,792})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.PG292.toString(),"sap({770,771,772})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.PG293.toString(),"sap({670,671,672})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.PG294.toString(),"PYG_PYMES?0.0:sap({774})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.PG295.toString(),"sap({778})-sdp({678})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.PG299.toString(),"sap({7600,7601})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.PG300.toString(),"sap({7602,7603})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.PG302.toString(),"sap({7610,7611,76200,76201,76210,76211})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.PG303.toString(),"sap({7612,7613,76202,76203,76212,76213,767,769})");
//	 		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.PG304.toString(),"sap({746})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.PG306.toString(),"sap({6610,6611,6615,6616,6620,6621,6640,6641,6650,6651,6654,6655})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.PG307.toString(),"sap({6612,6613,6617,6618,6622,6623,6624,6642,6643,6652,6653,6656,6657,669})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.PG308.toString(),"sap({660})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.PG309.toString(),"PYMES?0.0:sap({763})-sdp({663})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.PG310.toString(),"PYMES?sap({7630,7631,7633})-sdp({6630,6631,6633}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.PG311.toString(),"PYMES?sap({7632})-sdp({6632}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.PG312.toString(),"sap({768})-sdp({668})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.PG316.toString(),"sap({696,697,698,699})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.PG318.toString(),"sap({796,797,798,799})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.PG321.toString(),"sap({766,773,775})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.PG323.toString(),"sap({666,667,673,675})");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.PG326.toString(),"sap({6301,638})-sdp({6300,633})");
	}
	
	static { //ESTADO DE CAMBIOS EN EL PATRIMONIO NETO. ESTADO DE INGRESOS Y GASTOS RECONOCIDOS EN EL EJERCICIO
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.T0336.toString(),"ABREVIADO?sab({900,991,992})+sdb({800,89}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.T0337.toString(),"NORMAL?sab({900,991,992})+sdb({800,89}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.T0339.toString(),"NORMAL || ABREVIADO?sab({910})+sdb({810}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.T0340.toString(),"NORMAL || ABREVIADO?sab({94}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.T0341.toString(),"NORMAL || ABREVIADO?sab({95})+sdb({85}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.T0342.toString(),"NORMAL || ABREVIADO?sab({900})+sdb({860}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.T0343.toString(),"NORMAL || ABREVIADO?sab({920})+sdb({820}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.T0344.toString(),"NORMAL || ABREVIADO?sab({8301,834,835,838})+sdb({8300,833}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.T0346.toString(),"ABREVIADO?sab({902,993,994})+sdb({802}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.T0347.toString(),"NORMAL?sab({902,993,994})+sdb({802}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.T0349.toString(),"NORMAL || ABREVIADO?sab({912})+sdb({812}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.T0350.toString(),"NORMAL || ABREVIADO?sdb({84}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.T0351.toString(),"NORMAL || ABREVIADO?sab({902})+sdb({862}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.T0352.toString(),"NORMAL || ABREVIADO?sab({921})+sdb({821}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(BalanceKey.T0353.toString(),"NORMAL || ABREVIADO?sab({8301})+sdb({836,837}):0.0");
	}
	
	public static Map<String,String> COMPUTE_EXPRESSION_MAP = new LinkedHashMap<String,String>();

	static {	// BALANCE: ACTIVO
		COMPUTE_EXPRESSION_MAP.put(BalanceKey.BA101.toString(),"BA102+BA111+BA115+BA118+BA126+BA134+BA135");
		COMPUTE_EXPRESSION_MAP.put(BalanceKey.BA102.toString(),"NORMAL"
				+ "?BA103+BA104+BA105+BA106+BA107+BA108+BA700+BA701+BA109"
				+ ":BA106+BA110");
		COMPUTE_EXPRESSION_MAP.put(BalanceKey.BA111.toString(),"NORMAL?(BA112+BA113+BA114):BA111");
		COMPUTE_EXPRESSION_MAP.put(BalanceKey.BA115.toString(),"NORMAL?(BA116+BA117):BA115");
		COMPUTE_EXPRESSION_MAP.put(BalanceKey.BA118.toString(),"NORMAL? BA119+BA120+BA121+BA122+BA123+BA124 : BA119+BA125");
		COMPUTE_EXPRESSION_MAP.put(BalanceKey.BA126.toString(),"NORMAL?BA127+BA128+BA129+BA130+BA131+BA132+BA133 : BA127+BA133");
		
		COMPUTE_EXPRESSION_MAP.put(BalanceKey.BA136.toString(),"BA137+BA138+BA149+BA160+BA168+BA176+BA177");
		COMPUTE_EXPRESSION_MAP.put(BalanceKey.BA138.toString(),"NORMAL?BA139+BA140+BA141+BA144+BA147+BA148:BA138");
		COMPUTE_EXPRESSION_MAP.put(BalanceKey.BA141.toString(),"NORMAL?BA142+BA143:0.0");
		COMPUTE_EXPRESSION_MAP.put(BalanceKey.BA144.toString(),"NORMAL?BA145+BA146:0.0");
		COMPUTE_EXPRESSION_MAP.put(BalanceKey.BA149.toString(),"NORMAL"
				+ "?BA150+BA153+BA154+BA155+BA156+BA157+BA158"
				+ ":BA150+BA158+BA159");
		COMPUTE_EXPRESSION_MAP.put(BalanceKey.BA150.toString(),"BA151+BA152");
		COMPUTE_EXPRESSION_MAP.put(BalanceKey.BA160.toString(),"NORMAL"
				+ "?BA161+BA162+BA163+BA164+BA165+BA166"
				+ ":BA161+BA167");
		COMPUTE_EXPRESSION_MAP.put(BalanceKey.BA168.toString(),"NORMAL"
				+ "?BA169+BA170+BA171+BA172+BA173+BA174"
				+ ":BA169+BA175");
		COMPUTE_EXPRESSION_MAP.put(BalanceKey.BA177.toString(),"NORMAL?BA178+BA179:BA177");
		COMPUTE_EXPRESSION_MAP.put(BalanceKey.BA180.toString(),"BA101+BA136");
	}
	
	static { // BALANCE: PATRIMONIO NETO Y PASIVO
		COMPUTE_EXPRESSION_MAP.put(BalanceKey.BP185.toString(),"PYMES?BP186+BP209+BP208:BP186+BP209+BP202");
		COMPUTE_EXPRESSION_MAP.put(BalanceKey.BP186.toString(),"PYMES"
				+ "?BP187+BP190+BP191+BP194+BP195+BP198+BP199+BP200"
				+ ":BP187+BP190+BP191+BP194+BP195+BP198+BP199+BP200+BP201");
		COMPUTE_EXPRESSION_MAP.put(BalanceKey.BP187.toString(),"BP188+BP189");
		COMPUTE_EXPRESSION_MAP.put(BalanceKey.BP191.toString(),"NORMAL?BP192+BP193+BP702:BP191");
		COMPUTE_EXPRESSION_MAP.put(BalanceKey.BP195.toString(),"NORMAL?BP196+BP197:BP195");
		COMPUTE_EXPRESSION_MAP.put(BalanceKey.BP202.toString(),"NORMAL?BP203+BP204+BP205+BP206+BP207:BP202");
		
		COMPUTE_EXPRESSION_MAP.put(BalanceKey.BP210.toString(),"BP211+BP216+BP223+BP224+BP225+BP226+BP227");
		COMPUTE_EXPRESSION_MAP.put(BalanceKey.BP211.toString(),"NORMAL?BP212+BP213+BP214+BP215:BP211");
		COMPUTE_EXPRESSION_MAP.put(BalanceKey.BP216.toString(),"NORMAL?BP217+BP218+BP219+BP220+BP221:BP218+BP219+BP222");
		COMPUTE_EXPRESSION_MAP.put(BalanceKey.BP228.toString(),"NORMAL"
				+ "?BP229+BP230+BP231+BP238+BP239+BP250+BP251"
				+ ":BP230+BP231+BP238+BP239+BP250+BP251");
		COMPUTE_EXPRESSION_MAP.put(BalanceKey.BP230.toString(),"NORMAL?BP703+BP704:BP230");
		COMPUTE_EXPRESSION_MAP.put(BalanceKey.BP231.toString(),"NORMAL"
				+ "?BP232+BP233+BP234+BP235+BP236"
				+ ":BP233+BP234+BP237");
		COMPUTE_EXPRESSION_MAP.put(BalanceKey.BP239.toString(),"NORMAL"
				+ "?BP240+BP243+BP244+BP245+BP246+BP247+BP248"
				+ ":BP240+BP249");
		COMPUTE_EXPRESSION_MAP.put(BalanceKey.BP240.toString(),"BP241+BP242:BP240");
		COMPUTE_EXPRESSION_MAP.put(BalanceKey.BP240.toString(),"BP241+BP242");
		COMPUTE_EXPRESSION_MAP.put(BalanceKey.BP252.toString(),"BP185+BP210+BP228");
	}
	
	static { // BALANCE DE PERDIDAS Y GANANCIAS
		COMPUTE_EXPRESSION_MAP.put(BalanceKey.PG255.toString(),"PYG_NORMAL?PG256+PG257+PG705:PG255");
		COMPUTE_EXPRESSION_MAP.put(BalanceKey.PG705.toString(),"PYG_NORMAL?PG706+PG707+PG708:0.0");
		COMPUTE_EXPRESSION_MAP.put(BalanceKey.PG260.toString(),"PYG_NORMAL?PG261+PG262+PG263+PG264:PG260");
		COMPUTE_EXPRESSION_MAP.put(BalanceKey.PG265.toString(),"PG266+PG269");
		COMPUTE_EXPRESSION_MAP.put(BalanceKey.PG266.toString(),"PG267+PG268");
		COMPUTE_EXPRESSION_MAP.put(BalanceKey.PG270.toString(),"PG271+PG273+PG274+PG275+PG276+PG277+PG278");
		COMPUTE_EXPRESSION_MAP.put(BalanceKey.PG279.toString(),"PYG_NORMAL?PG280+PG281+PG282+PG283+PG709:PG279");
		COMPUTE_EXPRESSION_MAP.put(BalanceKey.PG287.toString(),"PG288+PG291+PG710");
		COMPUTE_EXPRESSION_MAP.put(BalanceKey.PG288.toString(),"PG289+PG290");
		COMPUTE_EXPRESSION_MAP.put(BalanceKey.PG291.toString(),"PG292+PG293");
		COMPUTE_EXPRESSION_MAP.put(BalanceKey.PG296.toString(),"PYG_PYMES"
			+ "?PG255+PG258+PG259+PG260+PG265+PG270+PG279+PG284+PG285+PG286+PG287+PG295"
			+ ":PG255+PG258+PG259+PG260+PG265+PG270+PG279+PG284+PG285+PG286+PG287+PG294+PG295");
		COMPUTE_EXPRESSION_MAP.put(BalanceKey.PG297.toString(),"PG298+PG301+PG304");
		COMPUTE_EXPRESSION_MAP.put(BalanceKey.PG298.toString(),"PG299+PG300");
		COMPUTE_EXPRESSION_MAP.put(BalanceKey.PG301.toString(),"PG302+PG303");
		COMPUTE_EXPRESSION_MAP.put(BalanceKey.PG305.toString(),"PG306+PG307+PG308");
		COMPUTE_EXPRESSION_MAP.put(BalanceKey.PG309.toString(),"PYG_NORMAL?PG310+PG311:PG309");
		COMPUTE_EXPRESSION_MAP.put(BalanceKey.PG313.toString(),"PG314+PG319");
		COMPUTE_EXPRESSION_MAP.put(BalanceKey.PG314.toString(),"PG315+PG316+PG317+PG318");
		COMPUTE_EXPRESSION_MAP.put(BalanceKey.PG319.toString(),"PG320+PG321+PG322+PG323");
		COMPUTE_EXPRESSION_MAP.put(BalanceKey.PG329.toString(),"PG330+PG331+PG332");
		COMPUTE_EXPRESSION_MAP.put(BalanceKey.PG324.toString(),"PG297+PG305+PG309+PG312+PG313+PG329");
		COMPUTE_EXPRESSION_MAP.put(BalanceKey.PG325.toString(),"PG296+PG324");
		COMPUTE_EXPRESSION_MAP.put(BalanceKey.PG327.toString(),"PG325+PG326");
		COMPUTE_EXPRESSION_MAP.put(BalanceKey.PG500.toString(),"PYG_NORMAL?PG327+PG328:PG327");
	}
	static { //ESTADO DE CAMBIOS EN EL PATRIMONIO NETO. ESTADO DE INGRESOS Y GASTOS RECONOCIDOS EN EL EJERCICIO
		COMPUTE_EXPRESSION_MAP.put(BalanceKey.T0500.toString(),"PYMES?0.0:PG500");
		COMPUTE_EXPRESSION_MAP.put(BalanceKey.T0336.toString(),"NORMAL?T0337+T0338:T0336");
		COMPUTE_EXPRESSION_MAP.put(BalanceKey.T0345.toString(),"PYMES?0.0:T0336+T0339+T0340+T0341+T0342+T0343+T0344");
		COMPUTE_EXPRESSION_MAP.put(BalanceKey.T0346.toString(),"NORMAL?T0347+T0348:T0346");
		COMPUTE_EXPRESSION_MAP.put(BalanceKey.T0354.toString(),"PYMES?0.0:T0346+T0349+T0350+T0351+T0352+T0353");
		COMPUTE_EXPRESSION_MAP.put(BalanceKey.T0355.toString(),"PYMES?0.0:T0500+T0345+T0354");
	}

	@Test
	public void testBalance() {

		String domainName = "mac.ecastellano.dev";
		int domainId = 526;
		String user = "mac";
		int year = 2014;
		
		AccMiningParameters params = new AccMiningParameters();
		params.setDomain(domainId);
		params.setYear(year);
		params.setStartDate( AonDateUtils.getYearFirstDay(year));
		params.setEndDate( AonDateUtils.getYearLastDay(year) );

		AONContext context = AONContext.getAONContext(domainName, domainId, user);
		
		Date now = new Date();
		DecimalFormat nf = new DecimalFormat("#,##0.0#;(#,##0.0#)"); 
		AccMiningMVELContext ctx = new AccMiningMVELContext( new IAccMiningKeyAccept() {
			
			@Override
			public boolean acceptKey(Object key) {
				try {
					return (BalanceKey.valueOf((String) key) != null);	
				} catch (IllegalArgumentException e) {
					return false;
				}
			}
		});
		ctx.setAccounts( ACCOUNTING.getAccountBalances(context, params) );
		ctx.setExpressionMap(INITIALIZE_EXPRESSION_MAP);
		
		ctx.put("NORMAL",false);
		ctx.put("ABREVIADO",true);
		ctx.put("PYMES",false);
		
		ctx.put("PYG_NORMAL",true);
		ctx.put("PYG_ABREVIADO",false);
		ctx.put("PYG_PYMES",false);
		for (BalanceKey key : BalanceKey.values()) {
			ctx.put(key.toString(), 0.0 );
		}
		for (String stringKey : INITIALIZE_EXPRESSION_MAP.keySet()) {
			String expression = INITIALIZE_EXPRESSION_MAP.get(stringKey);
			Double d = (Double) ctx.evaluateExpression(stringKey,expression);
			ctx.put(stringKey, d );
		}
		
		ctx.setExpressionMap(COMPUTE_EXPRESSION_MAP);
		for (String stringKey : COMPUTE_EXPRESSION_MAP.keySet()) {
			String expression = COMPUTE_EXPRESSION_MAP.get(stringKey);
			Double d = (Double) ctx.evaluateExpression(stringKey,expression);
			ctx.put(stringKey, d );
		}
		for (BalanceKey key : BalanceKey.values()) {
			Double d = (Double) ctx.get(key.toString());
			System.out.println( key.toString() 
					+ "\t" + AonStringUtils.leftPad(nf.format(AonMathUtils.round( d )),20));
		}
		System.out.println("[END] " + ((new Date()).getTime() - now.getTime()) + "Ms.");
	}
}
