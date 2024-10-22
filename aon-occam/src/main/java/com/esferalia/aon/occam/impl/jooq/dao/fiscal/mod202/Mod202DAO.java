package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod202;

import static com.esferalia.aon.jooq.tables.FsModel200.FS_MODEL200;
import static com.esferalia.aon.jooq.tables.FsModel200Detail.FS_MODEL200_DETAIL;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.mvel2.MVEL;
import org.mvel2.templates.TemplateRuntime;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.Filter.FiscalModelFilter;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.fiscal.AccountingBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelUtils;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATResponse;
import com.esferalia.aon.occam.api.model.type.CNAE2009;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod202Key;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.impl.jooq.dao.AccountEntryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DataResponseDAO;
import com.esferalia.aon.occam.impl.jooq.dao.FinanceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.FiscalModelDAO;
import com.esferalia.aon.occam.impl.jooq.dao.FiscalModelValidation;
import com.esferalia.aon.occam.impl.jooq.dao.IRPFFormatter;
import com.esferalia.aon.occam.server.fiscal.AEATJson;
import com.esferalia.aon.occam.server.fiscal.FiscalUtils;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod202DAO extends FiscalModelDAO {
	
	// Á --> \u00C1 á --> \u00E1 
	// É --> \u00C9 é --> \u00E9 
	// Í --> \u00CD í --> \u00ED 
	// Ó --> \u00D3 ó --> \u00F3 
	// Ú --> \u00DA ú --> \u00FA ... acento
	// Ü --> \u00DC ü --> \u00fc ... diéresis
	// Ñ --> \u00D1 ñ --> \u00F1
	// º --> \u00AA ª --> \u00BA
	// ¿ --> \u00BF

	private static final DateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("ddMMyyyy");
	private static synchronized Date parseDate(String dateStr) throws ParseException {
		return SIMPLE_DATE_FORMAT.parse(dateStr);
	}
	private static synchronized String formatDate(Date date) {
		return SIMPLE_DATE_FORMAT.format(date);
	}
	
	private static final String BN599 = "BN599";
	
	
	@FunctionalInterface
	private static interface IModelInfoProvider {
		String obtain(AONContext ctx, Mod202 mod,IModelScript<Mod202Key> script,Mod202KeyDAO keyDAO);
	}
	private static final String INFO_MSG = "<pre class='aon-fixed-font aon-font-medium aon-margin-bottom'>{0}<pre>";
	private static final String NONE_INFO = "No hay datos";
	private enum Mod202KeyInfoDAO {
		 NONE( ((ctx, mod, script,keyDAO) -> MessageFormat.format(INFO_MSG, NONE_INFO)) )
		,CORPORATE( ((ctx, mod, script,keyDAO) -> getCorporate(ctx,mod, script))) 
		,COMPUTE( ((ctx, mod, script,keyDAO) -> getCompute(ctx,mod, script)))
		,ACT_ACCOUNT ( (ctx, mod, script,keyDAO) -> MessageFormat.format(INFO_MSG, getAccountInfoInfo(ctx, mod, script,keyDAO) ))
		;
		private IModelInfoProvider provider;
		
		private Mod202KeyInfoDAO (IModelInfoProvider provider) {
			this.provider = provider;
		}
		public String getInfo(AONContext ctx, Mod202 mod,IModelScript<Mod202Key> script,Mod202KeyDAO keyDAO) {
			return provider.obtain(ctx, mod, script,keyDAO);
		}
	}
	
	@FunctionalInterface
	private static interface IModelAccepter {
		boolean accept(Mod202 mod);
	}
	@FunctionalInterface
	public static interface IValueIntializer {
		void initialize(AONContext ctx,Mod202 mod);
	}
	
	private enum Mod202KeyDAO {
		// *************************************************************************
		// **************************************************** COMMON TERRITORY ***
		// *************************************************************************
		 P00(Mod202Key.P00,(Mod202::isAEAT),null,null,null)
		,P01(Mod202Key.P01,(Mod202::isAEAT),null,null,null)
		,P02(Mod202Key.P02,(Mod202::isAEAT),null,null,null)
		,P03(Mod202Key.P03,(Mod202::isAEAT)
				,(ctx,mod) -> mod.putDescription(Mod202Key.P03,Mod202DAO.getMainActivityCNAE( ctx, mod ))
				,null,null)
		,X01(Mod202Key.X01,(Mod202::isAEAT),null,null,null)
		,X02(Mod202Key.X02,(Mod202::isAEAT),null,null,null)
		,X03(Mod202Key.X03,(mod -> mod.isAEAT() && mod.getYear() < 2017),null,null,null)
		,X04(Mod202Key.X04,(Mod202::isAEAT),null,null,null)
		,X05(Mod202Key.X05,(Mod202::isAEAT),null,null,null)
		,X06(Mod202Key.X06,(Mod202::isAEAT),null,null,null)
		,X07(Mod202Key.X07,(mod -> mod.isAEAT() && mod.getYear() < 2017),null,null,null)
		,X08(Mod202Key.X08,(Mod202::isAEAT),null,null,null)
		,X09(Mod202Key.X09,(mod -> mod.isAEAT() && mod.getYear() < 2017),null,null,null)
		,X10(Mod202Key.X10,(Mod202::isAEAT),null,null,null)
		,X11(Mod202Key.X11,(Mod202::isAEAT),null,null,null)
		,X12(Mod202Key.X12,(mod -> mod.isAEAT() && mod.getYear() > 2016),null,null,null)
		,X13(Mod202Key.X13,(mod -> mod.isAEAT() && mod.getYear() > 2016),null,null,null)
		,X14(Mod202Key.X14,(mod -> mod.isAEAT() && mod.getYear() > 2016),null,null,null)
		
		,X15(Mod202Key.X15,(mod -> mod.isAEAT() && (mod.getYear() > 2018 || (mod.getYear() == 2018 && mod.getPeriod().ordinal() >= Period.T2.ordinal()))),null,null,null)
		,X16(Mod202Key.X16,(mod -> mod.isAEAT() && (mod.getYear() > 2018 || (mod.getYear() == 2018 && mod.getPeriod().ordinal() >= Period.T2.ordinal()))),null,null,null)
		,X17(Mod202Key.X17,(mod -> mod.isAEAT() && (mod.getYear() > 2018 || (mod.getYear() == 2018 && mod.getPeriod().ordinal() >= Period.T2.ordinal()))),null,null,null)
		,X18(Mod202Key.X18,(mod -> mod.isAEAT() && (mod.getYear() > 2018 || (mod.getYear() == 2018 && mod.getPeriod().ordinal() >= Period.T2.ordinal()))),null,null,null)
		,X19(Mod202Key.X19,(mod -> mod.isAEAT() && (mod.getYear() > 2018 || (mod.getYear() == 2018 && mod.getPeriod().ordinal() >= Period.T2.ordinal()))),null,null,null)
		,X20(Mod202Key.X20,(mod -> mod.isAEAT() && (mod.getYear() >= 2023)),null,null,null)
		
		,X00(Mod202Key.X00,(Mod202::isAEAT),null,null,null)
		,C01(Mod202Key.C01,(Mod202::isAEAT)
			,(ctx,mod) -> mod.putAmount(Mod202Key.C01, getInitialC01(ctx,mod))
			,"isMethodA()?C01:0.0",null)
		,C02(Mod202Key.C02,(Mod202::isAEAT),null,"isMethodA()?C02:0.0",null)
		,C03(Mod202Key.C03,(Mod202::isAEAT),null,"isMethodA()?((C01-C02)*18/100):0.0"
			,"<li>Si la modalidad de c\u00E1lculo es A) :</li>"
			+"<li>18% de @{C01} menos @{C02} igual <b>@{C03}</b></li>"
			)
		,C04(Mod202Key.C04,(Mod202::isAEAT)
			,(ctx,mod) -> mod.putAmount(Mod202Key.C04, getInitialC04(ctx,mod))
			,"isMethodB()?C04:0.0",null)
		,C05(Mod202Key.C05,(Mod202::isAEAT),null,"isMethodB()?C05:0.0",null)
		,C06(Mod202Key.C06,(Mod202::isAEAT),null,"isMethodB()?C06:0.0",null)
		,C36(Mod202Key.C36,(mod -> mod.isAEAT() && mod.getYear() < 2017),null,null,null)
		,C37(Mod202Key.C37,(Mod202::isAEAT),null,"isMethodB()?C37:0.0",null)
		,C07(Mod202Key.C07,(Mod202::isAEAT),null,"isMethodB()?C07:0.0",null)
		,C08(Mod202Key.C08,(Mod202::isAEAT),null,"isMethodB()?C08:0.0",null)
		,C38(Mod202Key.C38,(Mod202::isAEAT),null,"isMethodB()?(C05+C07):0.0"
			,"<li>Si la modalidad de c\u00E1lculo es B.1) ó B.2) :</li>"
			+"<li>@{C05} m\u00E1s @{C07} igual <b>@{C38}</b></li>"
		)
		,C39(Mod202Key.C39,(Mod202::isAEAT),null,"isMethodB()?(C06+C37+C08):0.0"
			,"<li>Si la modalidad de c\u00E1lculo es B.1) ó B.2) :</li>"
			+"<li>@{C06} m\u00E1s @{C37} m\u00E1s @{C08} igual <b>@{C39}</b></li>"
		)
		,C09(Mod202Key.C09,(mod -> mod.isAEAT() && mod.getYear() < 2017),null,null,null)
		,C43(Mod202Key.C43,(mod -> mod.isAEAT() && mod.getYear() < 2017),null,null,null)
		,C13(Mod202Key.C13,(Mod202::isAEAT),null,"isMethodB()?(C04+C38-C39):0.0"
			,"<li>Si la modalidad de c\u00E1lculo es B.1) ó B.2) :</li>"
			+"<li>@{C04} m\u00E1s @{C38} menos @{C39} igual <b>@{C13}</b></li>"
		)
		,C44(Mod202Key.C44,(Mod202::isAEAT),null,"isMethodB()?C44:0.0",null)
		,C14(Mod202Key.C14,(Mod202::isAEAT),null,"isMethodB()?C14:0.0",null)
		,C45(Mod202Key.C45,(Mod202::isAEAT),null,"isMethodB()?C45:0.0",null)
		,C46(Mod202Key.C46,(Mod202::isAEAT),null,"isMethodB()?C46:0.0",null)
		,C16(Mod202Key.C16,(Mod202::isAEAT),null,"isMethodB1()?((C13-C14+C45-C46)>0?(C13-C14+C45-C46):0.0):0.0"
			,"<li>Si la modalidad de c\u00E1lculo es B.1) :</li>"
			 +"@if{ (C13-C14+C45-C46) >= 0}"
			 +	"<li>@{C13} menos @{C14} m\u00E1s @{C45} menos @{C46} igual <b>@{C16}</b></li>"
			 +"@else{}"
			 +	"<li>Al ser la casilla [13]-[14]+[45]-[46] menor que cero, el resultado es <b>cero.</b></li>"
			 +"@end{}"
		)
		
		,C17(Mod202Key.C17,(Mod202::isAEAT),null,"isMethodB1()?computeC17():0.0"
			,"<li>Si la modalidad de c\u00E1lculo es B.1) :</li>"
			+"@if{X04 > 0 }"
			+	"<li>Entidad que aplica el r\u00E9gimen de las entidades navieras en funci\u00F3n del tonelaje: <b>@{C17}</b></li>"
			+"@elseif{X09 > 0 }"
			+	"@code{c17Raw = 19/20*X08;}"
			+	"<li>(19/20) de @{X08}, @{c17Raw}, redondeado al alza, igual <b>@{C17}</b></li>"		
			+"@else{}"
			+	"@code{c17Raw = 5/7*X08;}"
			+	"<li>(5/7) de @{X08}, @{c17Raw} , redondeado a la baja, igual <b>@{C17}</b></li>"
			+"@end{}"
		)
		,C47(Mod202Key.C47,(Mod202::isAEAT),null,"isMethodB1()?C47:0.0",null)
		,C40(Mod202Key.C40,(Mod202::isAEAT),null,"isMethodB1()?C40:0.0",null)
		,C48(Mod202Key.C48,(Mod202::isAEAT),null,"isMethodB1()?C48:0.0",null)
		,C49(Mod202Key.C49,(Mod202::isAEAT),null,"isMethodB1()?C49:0.0",null)
		,C18(Mod202Key.C18,(Mod202::isAEAT),null,"isMethodB1()?((C16*C17/100)+C47-C40+C48-C49):0.0"
			,"<li>Si la modalidad de c\u00E1lculo es B.1) :</li>"
			+"<li>(@{C16} por @{C17} / 100) m\u00E1s @{C47} menos @{C40} m\u00E1s @{C48} menos @{C49}, igual <b>@{C18}</b></li>"		
		)
		,C19(Mod202Key.C19,(Mod202::isAEAT),null,"isMethodB2()?((C13-C44-C14+C45)>0?(C13-C44-C14+C45):0.0):0.0"
			,"<li>Si la modalidad de c\u00E1lculo es B.2) :</li>"
			+"@if{ (C13-C44-C14+C45) >= 0}"
			+	"<li>@{C13} menos @{C44} menos @{C14} m\u00E1s @{C45} igual <b>@{C19}</b></li>"
			+"@else{}"
			+	"<li>Al ser la casilla [13]-[44]-[14]+[45] menor que cero, el resultado es <b>cero.</b></li>"
			+"@end{}"
		)
		,C20(Mod202Key.C20,(Mod202::isAEAT),null,"isMethodB2()?(C20):0.0",null)
		
		,C21(Mod202Key.C21,(Mod202::isAEAT),null,"isMethodB2()?computeC21():0.0"
			,"<li>Si la modalidad de c\u00E1lculo es B.2) :</li>"
			+"@if{X09 > 0 }"
			+	"@code{c21Raw = 19/20*X08_1;}"
			+	"<li>(19/20) de @{X08_1}, @{c21Raw}, redondeado al alza, igual <b>@{C21}</b></li>"		
			+"@else{}"
			+	"@code{c21Raw = 5/7*X08_1;}"
			+	"<li>(5/7) de @{X08_1}, @{c21Raw} , redondeado a la baja, igual <b>@{C21}</b></li>"
			+"@end{}"
		)
		,C22(Mod202Key.C22,(Mod202::isAEAT),null,"isMethodB2()?(C20*C21/100):0.0"
			,"<li>Si la modalidad de c\u00E1lculo es B.2) :</li>"
			+"<li>@{C20} por @{C21} partido 100 igual <b>@{C22}</b></li>"
		)
		,C23(Mod202Key.C23,(Mod202::isAEAT),null,"isMethodB2()?(C19-C20):0.0"
			,"<li>Si la modalidad de c\u00E1lculo es B.2) :</li>"
			+"<li>@{C19} menos @{C20} igual <b>@{C23}</b></li>"
		)
		,C24(Mod202Key.C24,(Mod202::isAEAT),null,"isMethodB2()?computeC24():0.0"
			,"<li>Si la modalidad de c\u00E1lculo es B.2) :</li>"
			+"@if{X09 > 0 }"
			+	"@code{c24Raw = 19/20*X08_2;}"
			+	"<li>(19/20) de @{X08_2}, @{c24Raw}, redondeado al alza, igual <b>@{C24}</b></li>"		
			+"@else{}"
			+	"@code{c21Raw = 5/7*X08_2;}"
			+	"<li>(5/7) de @{X08_2}, @{c24Raw} , redondeado a la baja, igual <b>@{C24}</b></li>"
			+"@end{}"
		)
		,C25(Mod202Key.C25,(Mod202::isAEAT),null,"isMethodB2()?(C23*C24/100):0.0"
			,"<li>Si la modalidad de c\u00E1lculo es B.2) :</li>"
			+"<li>@{C23} por @{C24} partido 100 igual <b>@{C25}</b></li>"
		)
		,C50(Mod202Key.C50,(Mod202::isAEAT),null,"isMethodB2()?C50:0.0",null)
		,C42(Mod202Key.C42,(Mod202::isAEAT),null,"isMethodB2()?C42:0.0",null)
		,C51(Mod202Key.C51,(Mod202::isAEAT),null,"isMethodB2()?C51:0.0",null)
		,C52(Mod202Key.C52,(Mod202::isAEAT),null,"isMethodB2()?C52:0.0",null)
		,C26(Mod202Key.C26,(Mod202::isAEAT),null,"isMethodB2()?(C22+C25+C50-C42+C51-C52):0.0"
			,"<li>Si la modalidad de c\u00E1lculo es B.2) :</li>"
			+"<li>@{C22} m\u00E1s @{C25} m\u00E1s @{C50} menos @{C42} m\u00E1s @{C51} menos @{C52} igual <b>@{C26}</b></li>"
		)
		,C27(Mod202Key.C27,(Mod202::isAEAT),null,"isMethodB()?C27:0.0",null)
		,C28(Mod202Key.C28,(Mod202::isAEAT),null,"isMethodB()?C28:0.0",null)
		,C29(Mod202Key.C29,(Mod202::isAEAT),null,"isMethodB()?C29:0.0",null)
		,C30(Mod202Key.C30,(Mod202::isAEAT),null,"isMethodB()?C30:0.0",null)
		,C31(Mod202Key.C31,(Mod202::isAEAT),null,"isMethodB()?C31:0.0",null)
		,C32(Mod202Key.C32,(Mod202::isAEAT),null,"((((isMethodB1()?C18:(isMethodB2()?C26:0.0))-C27-C28)*C29/100)-C30-C31)<0?0.0:((((isMethodB1()?C18:(isMethodB2()?C26:0.0))-C27-C28)*C29/100)-C30-C31)"
			,"@if{X00 == 0 }"
			+	"<li>Si la modalidad de c\u00E1lculo es A) :</li>"
			+	"<li>Cero,<b>@{C32}</b></li>"
			+"@elseif{X00 == 1 }"
			+	"<li>Si la modalidad de c\u00E1lculo es B.1) :</li>"
			+	"<li>((@{C18} menos @{C27} menos @{C28}) por @{C29} partido 100) menos @{C30} menos @{C31}, igual <b>@{C32}</b></li>"
			+"@else{}"
			+	"<li>Si la modalidad de c\u00E1lculo es B.2) :</li>"
			+	"<li>((@{C26} menos @{C27} menos @{C28}) por @{C29} partido 100) menos @{C30} menos @{C31}, igual <b>@{C32}</b></li>"
			+"@end{}"
		)
		,C33(Mod202Key.C33,(Mod202::isAEAT),null,"isMethodB()?C33:0.0",null)
		,C34(Mod202Key.C34,(Mod202::isAEAT),null,"isMethodB()?(C32>C33?C32:C33):0.0"
			,"<li>Si la modalidad de c\u00E1lculo es B.1) o B.2) :</li>"
			+"<li>La cantidad mayor entre [032] y  [033], igual <b>@{C34}</b></li>"
		)
		,A01(Mod202Key.A01,(Mod202::isAEAT),null,null,null)
		,A02(Mod202Key.A02,(Mod202::isAEAT),null,null,null)
		,A03(Mod202Key.A03,(Mod202::isAEAT),null,null,null)
		,A04(Mod202Key.A04,(Mod202::isAEAT),null,null,null)
		,A05(Mod202Key.A05,(Mod202::isAEAT),null,null,null)
		,A06(Mod202Key.A06,(Mod202::isAEAT),null,null,null)
		,A07(Mod202Key.A07,(Mod202::isAEAT),null,null,null)
		,A08(Mod202Key.A08,(Mod202::isAEAT),null,null,null)
		,A09(Mod202Key.A09,(Mod202::isAEAT),null,null,null)
		,A10(Mod202Key.A10,(Mod202::isAEAT),null,null,null)
		,A11(Mod202Key.A11,(Mod202::isAEAT),null,null,null)
		,A12(Mod202Key.A12,(Mod202::isAEAT),null,null,null)
		;
		
		private Mod202Key key;
		private IModelAccepter acceptModel;
		private IValueIntializer initializer;
		private String expression;
		private String template;

		private Mod202KeyDAO(Mod202Key key
				, IModelAccepter acceptModel
				, IValueIntializer initializer
				, String expression
				, String template) {
			this.key = key;
			this.acceptModel =  acceptModel;
			this.initializer = initializer;
			this.expression =  expression;
			this.template =  template;
		}
		public Mod202Key getKey() {
			return key;
		}
		public boolean acceptModel(Mod202 mod) {
			return  (acceptModel.accept(mod));
		}
		public void initialize(AONContext ctx,Mod202 mod) {
			if (initializer != null) {
				initializer.initialize(ctx, mod);
			}
		}
		public String getExpression() {
			return expression;
		}
		public String getTemplate() {
			return template;
		}
		public static Mod202KeyDAO safeValueOf(Mod202 mod, String key) {
			if (AonStringUtils.isBlank(key)) return null; 
			for (Mod202KeyDAO keyDAO : Mod202KeyDAO.values()) {	
				if (keyDAO.acceptModel(mod) && keyDAO.getKey().getValue().equals(key) ) {
					return keyDAO;
				}
			}
			return null;
		}
	}

	public static Stream<Mod202> getMod202s(AONContext ctx,int domain, FiscalModelFilter filter) {
		return getModelRecords(ctx, domain,FiscalModelType.M202,filter)
				.map( rec -> map202(new Mod202(),rec))
				.peek(fm -> getModelDetails(ctx,fm).forEach( detail -> fm.put( detail)))
				;
	}
	public static Stream<Mod202> getMod202s(AONContext ctx,int domain) {
		return getModelRecords(ctx, domain,FiscalModelType.M202)
			.map( rec -> map202(new Mod202(),rec))
			.peek(fm -> getModelDetails(ctx,fm).forEach( detail -> fm.put( detail)));
	}

	public static Mod202 getMod202(AONContext ctx,int id) {
		ctx.checkRead();
		final Mod202 mod202 = getModelRecord(ctx, id)
				.map( rec -> map202(new Mod202(),rec));
		if (mod202 != null) {
			getModelDetails(ctx,mod202).forEach( mod202::put);	
		}
		onFillFiscalModel(mod202);
		return mod202;
	}
	
	
	public static Mod202 saveMod202(AONContext ctx, Mod202 mod202) {
		ensureDetail(mod202);
		calculateMod202(ctx, mod202);
		FiscalModel fm = save(ctx, mod202);
		return getMod202(ctx, fm.getId());
	}
	
	public static Mod202 saveCommentsMod202(AONContext ctx, Mod202 mod202) {
		saveComments(ctx, mod202);
		return mod202;
	}

	private static Mod202MVELContext getMVELcontext(AONContext ctx,Mod202 mod202) {
		Mod202MVELContext mvelCtx = new Mod202MVELContext();
		mvelCtx.year = mod202.getYear();
		for (String key : mod202.getMap().keySet()) {
			Mod202Key mod202Key = Mod202Key.getKey(key);
			if (mod202Key != null) {
				FiscalModelDetail detail = mod202.getMap().get(key);
				double amount;
				if (mod202Key == Mod202Key.X08) {
					String x08 = detail.getDescription();
					amount = AonNumberUtils.todouble(x08);
					double x08_1 = AonNumberUtils.todouble(AonStringUtils.substringBefore(x08, "/"));
					mvelCtx.put(Mod202MVELContext.X08_1, x08_1);
					double x08_2 = AonNumberUtils.todouble(AonStringUtils.substringAfter(x08, "/"));
					mvelCtx.put(Mod202MVELContext.X08_2, x08_2);
				} else {
					amount = detail==null?0.0:detail.getAmount();
				}
				mvelCtx.put(mod202Key.toString(), amount);
			}
		}
		return mvelCtx;
	}
	public static Mod202 calculateMod202(AONContext ctx, Mod202 mod202) {
		Mod202MVELContext mvelCtx = getMVELcontext(ctx,mod202);
		for (Mod202KeyDAO key : Mod202KeyDAO.values()) {
			if (AonStringUtils.isNotEmpty( key.getExpression()) && key.acceptModel(mod202)) {
				Object ret =  MVEL.eval( key.getExpression() , mvelCtx , mvelCtx);
				Double amount = (Double) ret;
				mvelCtx.put(key.getKey().toString(), amount);
				mod202.ensureDetail(key.getKey()).setAmount(AonMathUtils.round( amount) );
			}
		}
		return mod202; 
	}
	
	public static Mod202 initializeMod202(AONContext ctx,Mod202 mod202) {
		if (mod202 == null) {
			mod202 = new Mod202();
			mod202.setDomain(ctx.getDomainId());
		}
		initializeFiscalModel(ctx, mod202);
		if (mod202.getPeriod() == Period.T2) {
			mod202.setPeriod(Period.T1);
		}
		if (mod202.getPeriod() == Period.T3) {
			mod202.setPeriod(Period.T2);
		}
		if (mod202.getPeriod() == Period.T4) {
			mod202.setPeriod(Period.T3);
		}
		return mod202;
	}
	
	public static Mod202 resetMod202(AONContext ctx,Mod202 mod202) {
		mod202.setMap(null);
		initializeIdentificationData(ctx, mod202);
		createMod202(ctx,mod202);
		return mod202;
	}
	
	public static Mod202 createMod202(AONContext ctx,Mod202 mod202) {
		for (Mod202KeyDAO key : Mod202KeyDAO.values()) {
			if (key.acceptModel(mod202)) {
				FiscalModelDetail detail = mod202.ensureDetail(key.getKey());
				detail.setExpression(key.getExpression());
			}
		}
		for (Mod202KeyDAO key : Mod202KeyDAO.values()) {
			if (key.acceptModel(mod202)) {
				key.initialize(ctx, mod202);
			};
		}
		return calculateMod202(ctx, mod202);
	}
	public static String getMod202Info(AONContext ctx, Mod202 mod202, IModelScript<Mod202Key> script, FiscalModelKeyInfo infoKey) {
		Mod202KeyInfoDAO k = Mod202KeyInfoDAO.valueOf(infoKey.toString());
		for (Mod202KeyDAO keyDAO : Mod202KeyDAO.values()) {
			if (keyDAO.getKey() == script.getKeys()[0]) {
				return k.getInfo(ctx, mod202, script, keyDAO);
			}
		}
		return null; 
	}

	private static String getAccountInfoInfo(AONContext ctx, Mod202 mod, IModelScript<Mod202Key> script,
			Mod202KeyDAO keyDAO) {
		String title = "SALDOS DE CUENTAS QUE AFECTAN A LA CONFECCI\u00D3N DEL MODELO " 
				+ FiscalModelUtils.getModelName(mod) 
				+ " DEL " + mod.getPeriod().getDescription()
				+ " DE " + mod.getYear();
		
		return getC04Info(ctx, mod, script,title);
	}
	
	private static double getRawC04(AONContext ctx, final Mod202 mod) {
		return getInitialBaseC04(ctx, mod)
				.mapToDouble(br -> br.getCreditBalance())
				.sum();  
	}
	
	private static double getInitialC04(AONContext ctx, final Mod202 mod) {
		return getRawC04(ctx, mod); 
	}
	
	private static String getC04Info(AONContext ctx, final Mod202 mod202
			, final IModelScript<Mod202Key> script,String title) {
		return IRPFFormatter.formatAccountingBreakdown(title, script.getLabel() 
			,getInitialBaseC04(ctx, mod202).collect(Collectors.toCollection(LinkedList::new))
		);
	}
	private static Stream<AccountingBreakdown> getInitialBaseC04(AONContext ctx, final Mod202 mod) {
		Date startDate = AonDateUtils.getYearFirstDay(mod.getYear());
		Date end = null;
		if (mod.getPeriod() == Period.T1) {
			end = FiscalUtils.getPeriodEnd(mod.getYear(),Period.T1); // Hasta el 31 de marzo
		}
		if (mod.getPeriod() == Period.T2) {
			end = FiscalUtils.getPeriodEnd(mod.getYear(),Period.T3); // Hasta el 30 de septiembre
		}
		if (mod.getPeriod() == Period.T3) {
			end = FiscalUtils.getPeriodEnd(mod.getYear(),Period.M11); // Hasta el 30 de Noviembre.
		}
		final Date endDate = end;
		return AccountEntryDAO.getAccountingBreakdown(ctx,
				p -> p.getDomainProperty().eq(ctx.getDomainId())
					.and(p.getEntryDateProperty().ge( startDate ))
					.and(p.getEntryDateProperty().le( endDate ))
					.and(
							p.getAccountCodeProperty().like("6%")
							.or(p.getAccountCodeProperty().like("7%"))
						)
					)
			.filter( br -> (!br.hasActivity() || (!br.isFarmer() && (br.isNormalRegime() || br.isSimplifiedRegime())) ));
	}
	
	
	private static String getCorporate(AONContext ctx, Mod202 mod202, IModelScript<Mod202Key> script) {
		StringBuilder buf = new StringBuilder();
		buf.append("<pre style=\"font-family: Fixed, monospace;font-size: 0.9em; margin-bottom: 1em; padding: 1em;\">");
		for (Mod202Key key : script.getKeys() ) {
			Mod202KeyDAO keyDAO = Mod202KeyDAO.safeValueOf(mod202, key.getValue());
			if (keyDAO != null) {
				String box = " [" + AonStringUtils.leftPad(Integer.toString(keyDAO.getKey().getBox()), 3, '0')+"] ";
				buf.append(AonStringUtils.CR_LF);
				buf.append("<b>DETALLE DEL C\u00C1LCULO DE LA CASILLA: " + box + " - " + script.getLabel() + "</b>");
				buf.append(AonStringUtils.CR_LF);
				buf.append(AonStringUtils.CR_LF);
				buf.append("<ul style=\"padding-left: 20px;\">");
				double x00 = mod202.getAmount(Mod202Key.X00);
				if (x00 == 0) {
					buf.append("<li>Tipo Cálculo: <b>A) Art\u00EDculo LIS 40.2 LIS</b> </li>" );
					int year = mod202.getYear()- ((mod202.getPeriod() == Period.T1)?2:1); 
					buf.append("<li>Valor de la casilla 599 del I.S. ej. "+year+": <b>"+ getInitialC01(ctx, mod202) +"</b></li>" );
				} else {
					buf.append("<li>Tipo Cálculo: no es <b>A) Art\u00EDculo LIS 40.2 LIS</b> </li>" );
					buf.append("<li>Valor <b>0.0</b></li>" );
				}
				buf.append("</ul>");
			}
		}
		buf.append("</pre>");
		return buf.toString();
	}

	private static String getCompute(AONContext ctx, Mod202 mod202, IModelScript<Mod202Key> script) {
		return getCompute(ctx, mod202, script, getMVELcontext(ctx,mod202));
	}

	private static String getCompute(AONContext ctx, Mod202 mod202, IModelScript<Mod202Key> script,Mod202MVELContext mvelCtx) {
		StringBuilder buf = new StringBuilder();
		buf.append("<pre style=\"font-family: Fixed, monospace;font-size: 0.9em; margin-bottom: 1em; padding: 1em;\">");
		for (Mod202Key key : script.getKeys() ) {
			Mod202KeyDAO keyDAO = Mod202KeyDAO.safeValueOf(mod202, key.getValue());
			if (keyDAO != null) {
				String box = " [" + AonStringUtils.leftPad(Integer.toString(keyDAO.getKey().getBox()), 3, '0')+"] ";
				buf.append(AonStringUtils.CR_LF);
				buf.append("<b>DETALLE DEL C\u00C1LCULO DE LA CASILLA: " + box + " - " + script.getLabel() + "</b>");
				buf.append(AonStringUtils.CR_LF);
				buf.append(AonStringUtils.CR_LF);
				buf.append("<ul style=\"padding-left: 20px;\">");
				if (AonStringUtils.isNotBlank( keyDAO.getExpression())) {
					buf.append("<li><b>F\u00F3rmula:</b> " + keyDAO.getExpression() + "</li>" );
				}
				String template = keyDAO.getTemplate();
				if (AonStringUtils.isNotBlank( template )) {
					Object result = TemplateRuntime.eval(template, mvelCtx);
					buf.append(result != null ? result.toString() : null);
				}
				buf.append("</ul>");
			}
		}
		buf.append("</pre>");
		return buf.toString();
	}
/*	
	private static String getExpression(Mod202 mod202
			, IModelScript<Mod202Key> script, Mod202KeyDAO keyDAO0) {
		StringBuilder buf = new StringBuilder();
		int headerLength = 100;
		buf.append(MessageFormat.format(IRPFFormatter.DIV_MSG,AonStringUtils.repeat(" ", headerLength)));
		buf.append(MessageFormat.format(IRPFFormatter.DIV_MSG_BOLD,AonStringUtils.center("DETALLE DEL C\u00C1LCULO", headerLength)));
		buf.append(MessageFormat.format(IRPFFormatter.DIV_MSG_BOLD,AonStringUtils.repeat("-", headerLength)));

		final StringBuilder expr = new StringBuilder();
		final StringBuilder resu = new StringBuilder();

		LinkedHashMap<String, Object> mvelCtx = new LinkedHashMap<String, Object>() {
			private static final long serialVersionUID = -4910560506222174407L;
			@Override
			public Object get(Object key) {
				Mod202KeyDAO keyDAO = Mod202KeyDAO.valueOf(key.toString());
				String box = " [" + AonStringUtils.leftPad(Integer.toString(keyDAO.getKey().getBox()), 3, '0')+"] ";
				String exprCopy = expr.toString();
				expr.delete(0, expr.length());
				expr.append(AonStringUtils.replace(exprCopy
						, key.toString()
						, box));
				Object value = super.get(key);
				exprCopy = resu.toString();
				resu.delete(0, resu.length());
				resu.append(AonStringUtils.replace(exprCopy
						, key.toString()
						," " + value.toString() + " "
						));
				return value;
			}
		};

		for (String keyValue : mod202.getMap().keySet()) {
			Mod202Key mod202Key = Mod202Key.getKey(keyValue);
			if (mod202Key != null) {
				FiscalModelDetail detail = mod202.getMap().get(keyValue);
				mvelCtx.put(mod202Key.toString(), detail==null?0.0:detail.getAmount());
			}
		}


		for (Mod202Key key : script.getKeys() ) {
			Mod202KeyDAO keyDAO = Mod202KeyDAO.safeValueOf(mod202, key.getValue());
			if (keyDAO != null) {
				resu.delete(0, resu.length());
				resu.append(keyDAO.getExpression());
				expr.delete(0, expr.length());
				expr.append(keyDAO.getExpression());
				
				String box = " [" + AonStringUtils.leftPad(Integer.toString(keyDAO.getKey().getBox()), 3, '0')+"] ";
				buf.append(MessageFormat.format(IRPFFormatter.DIV_MSG,AonStringUtils.repeat(" ", headerLength)));
				buf.append(MessageFormat.format(IRPFFormatter.DIV_MSG_BOLD,AonStringUtils.center("Casilla: " + box + " - " + script.getLabel(), headerLength)));
				Object ret = MVEL.eval(keyDAO.getExpression(), mvelCtx, mvelCtx);
				resu.append(" = ");
				resu.append(AonMathUtils.round((Double) ret));
				expr.append(" = ");
				expr.append(box);
				buf.append(MessageFormat.format(IRPFFormatter.DIV_MSG,"<b>F\u00F3rmula:</b> " + expr.toString()));		
				buf.append(MessageFormat.format(IRPFFormatter.DIV_MSG_BLUE_BORDER_BOTTOM,"<b>Resultado:</b> " + resu.toString()));
			}
			
		}
		return buf.toString();
	}
*/
	// -------------------------------------------------------------------- UTIL
	public static Mod202 markAsFinished(AONContext ctx,Mod202 mod202) {
		mod202 = FiscalModelDAO.finish(ctx, mod202);
		return saveMod202(ctx, mod202);
	}
	public static Mod202 markAsSent(AONContext ctx,Mod202 mod202) {
		mod202.setStatus(FiscalStatus.SENT);
		mod202= saveMod202(ctx, mod202);
		return mod202;
	}
	public static Mod202 markAsCustomerCheck(AONContext ctx,Mod202 mod202) {
		mod202 = FiscalModelDAO.finish(ctx, mod202);
		mod202.setStatus(FiscalStatus.CUSTOMER_CHECK);
		mod202= saveMod202(ctx, mod202);
		return mod202;
	}
	
	public static Mod202 markAsPending(AONContext ctx,Mod202 mod202) {
		mod202.setDeclarationType( (String) null);
		mod202.setStatus(FiscalStatus.PENDING);
		Finance finance = mod202.getFinance();
		mod202.setFinance(null);
		mod202 = saveMod202(ctx, mod202);
		if (finance != null) {
			FinanceDAO.delete(ctx, finance.getId());
		}
		return mod202;
	}

	protected static void onFillFiscalModel(Mod202 mod202) {
		for (Mod202Key key : Mod202Key.values()) {
			if (key == Mod202Key.P02) {
				String c = mod202.getDescription(Mod202Key.P02);
				Date initialDate = null;
				if (AonStringUtils.isNotEmpty( c )) {
					try {
						initialDate = parseDate(c);
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
				mod202.setInitialDate(initialDate);
			} else if (key == Mod202Key.P03) {
				String c = mod202.getDescription(Mod202Key.P03);
				if (AonStringUtils.isNotEmpty( c )) {
					CNAE2009 cnae = CNAE2009.valueOfCode(c); 
					mod202.setCnae(cnae);
				} else {
					mod202.setCnae(null);
				}
			}
		}
	}
	protected static void ensureDetail(Mod202 mod202) {
		for (Mod202Key key : Mod202Key.values()) {
			if (key == Mod202Key.P02) {
				if (mod202.getInitialDate() != null) {
					try {
						String date = formatDate(mod202.getInitialDate());
						mod202.putDescription(Mod202Key.P02,date);
					} catch (NumberFormatException e) {
						mod202.putDescription(Mod202Key.P02,null);
					}
				} else {
					mod202.putDescription(Mod202Key.P02,null);
				}
			} else if (key == Mod202Key.P03) {
				if (mod202.getCnae() != null ) {
					try {
						mod202.putDescription(Mod202Key.P03, mod202.getCnae().getCode());
					} catch (NumberFormatException e) {
						mod202.putDescription(Mod202Key.P03,null);
					}
				} else {
					mod202.putDescription(Mod202Key.P03,null);
				}
			}
		}
	}
	
	private static String getMainActivityCNAE(AONContext ctx,Mod202 mod) {
		Date atDate = AonDateUtils.getDate(mod.getYear(), mod.getPeriod().getStartMonth(),1);
		EnterpriseActivity activity  = CompanyDAO.getEnterpriseActivities(ctx, ctx.getDomainId(), atDate)
			.filter( ea -> ea.isPrincipal() )
			.findFirst()
			.orElse(null);
		String cnaeCode = activity==null?null:activity.getCnaeCode();
		cnaeCode = AonStringUtils.substring(cnaeCode,0,2) + "." + AonStringUtils.substring(cnaeCode,2,4);
 		CNAE2009 cnae = CNAE2009.valueOfCode(cnaeCode);
		mod.setCnae(cnae);
		return cnaeCode;
	}
	
	public static Mod202 aeatPresentation(AONContext ctx, Mod202 mod202, String aeatResponse) {
		if (AonStringUtils.isNotBlank(aeatResponse)) {
			DataResponseDAO.insertAEATResponse(ctx, mod202, aeatResponse);
			AEATResponse response = AEATJson.toJSON(aeatResponse.getBytes());
			Mod202 changed = getMod202(ctx, mod202.getId());
			if (changed != null) {
				changed.setNumber(response.getJustificante());
				return markAsSent(ctx, changed);
			}
		}
		return mod202;
	}
	
	private static double getInitialC01(AONContext ctx,Mod202 mod) {
		double x00 = mod.getAmount(Mod202Key.X00);
		if (x00 == 0) {
			int year = mod.getYear() - ((mod.getPeriod() == Period.T1)?2:1);
			return ctx.getDslContext().select(FS_MODEL200_DETAIL.VALUE)
				.from(FS_MODEL200)
				.innerJoin(FS_MODEL200_DETAIL).on(FS_MODEL200.ID.eq(FS_MODEL200_DETAIL.FS_MODEL200))
				.where(FS_MODEL200.DOMAIN.eq(ctx.getDomainId()))
				.and(FS_MODEL200.YEAR.eq(year))
				.and(FS_MODEL200_DETAIL.KEY.eq(BN599))
				.orderBy(FS_MODEL200.ID)
				.stream()
				.map(r -> r.getValue(FS_MODEL200_DETAIL.VALUE))
				.findFirst()
				.orElse(0.0)
			;
		}
		return 0.0;
	}
	
	public static Mod202 markAsCustomerRejected(AONContext ctx, Mod202 mod202, String reason) {
		FiscalModelValidation.statusChange(mod202, FiscalStatus.CUSTOMER_REJECTED);
		mod202.setStatus(FiscalStatus.CUSTOMER_REJECTED);
		if (AonStringUtils.isNotBlank(reason)) {
			String comments = mod202.getComments();
			if (AonStringUtils.isNotBlank(comments)) {
				comments = AonStringUtils.join(comments, "\n", reason);
			} else {
				comments = reason; 
			}
			mod202.setComments( comments );
		}
		mod202 = saveMod202(ctx, mod202);
		return mod202;
	}
	
}
