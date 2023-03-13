package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod130;

import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.AccountingBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.api.model.type.Mod130Key;
import com.esferalia.aon.occam.impl.jooq.dao.AppParamDAO;

public class Mod130BIZKAIA2015Declaration extends Mod130Declaration {
	
	public static boolean accept(Mod130 mod) {
		return mod.isBizkaia() && mod.getYear() >= 2015; 
	}
	@Override
	IMod130KeyDAO[] getKeys() {
		return Mod130KeyDAO.values();
	}
	@Override
	Double getResult(Mod130 mod130) {
		return mod130.getAmount(Mod130Key.C28);
	}
	@Override
	protected ComplementaryBeahaviour getComplementaryBehaviour(Mod130 mod) {
		return ComplementaryBeahaviour.REPLACEMENT;
	}
	
	private enum Mod130KeyDAO implements IMod130KeyDAO {
		 P3  (Mod130Key.P3 )
		,B01 (Mod130Key.C01)
		,B02 (Mod130Key.C02, null,"C01*5/100")
		,B03 (Mod130Key.C03)
		,B04 (Mod130Key.C04, null,"C02-C03")
		,B05 (Mod130Key.C05)
		,B06 (Mod130Key.C06, null,"C05*2/100")
		,B07 (Mod130Key.C07)
		,B08 (Mod130Key.C08, null,"C06-C07")
		,B09 (Mod130Key.C09)
		,B10 (Mod130Key.C10, null,"C09*20/100")
		,B11 (Mod130Key.C11)
		,B12 (Mod130Key.C12, null,"C10-C11" )
		,B15 (Mod130Key.C15)
		,B16 (Mod130Key.C16, null,"C15*0.5/100")
		,B17 (Mod130Key.C17)
		,B18 (Mod130Key.C18, null,"C16-C17" )
		,B19 (Mod130Key.C19)
		,B20 (Mod130Key.C20, null,"C19*0.25/100")
		,B28 (Mod130Key.C28, null,"C04+C08+C12+C18+C20")
		,TIP (Mod130Key.CT_TIP )
		;
		
		private Mod130Key key;
		private IValueIntializer initializer;
		private IValueAccepter accepter;
		private String expression;
		
		private Mod130KeyDAO(Mod130Key key) {
			this(key,null,null,null);
		}

		private Mod130KeyDAO(Mod130Key key,IValueIntializer initializer,String expression) {
			this(key,initializer,null,expression);
		}
		
		private Mod130KeyDAO(Mod130Key key,IValueIntializer initializer,IValueAccepter accepter,String expression) {
			this.key = key;
			this.initializer = initializer;
			this.accepter= accepter;
			this.expression =  expression;
		}
		
		@Override
		public Mod130Key getKey() {
			return key;
		}
		
		@Override
		public void initialize(AONContext ctx,Mod130 mod) {
			if (initializer != null) {
				initializer.initialize(ctx, mod);
			}
		}
		@Override
		public boolean acceptValue(Mod130 mod,IrpfBreakdown  br) {
			return this.accepter != null && this.accepter.accept(mod, br); 
		}
		@Override
		public String getExpression() {
			return expression;
		}
		@Override
		public String info(AONContext ctx, Mod130 mod) {
			return null;
		}
	}
	
	@Override
	Mod130 initialize(AONContext ctx, Mod130 mod130) {
		mod130.setComplementaryDeclarationAvailable(true);
		mod130.setReplacementDeclarationAvailable(false);
		mod130.putAmount(Mod130Key.P1, 100.0);
		mod130.setRegime(AppParamDAO.getDefaultIRPFRegime(ctx));
		return super.initializeModel(ctx, mod130);
	}

	Mod130 calculate(AONContext ctx, Mod130 mod130) {
		return basicCalculate(ctx,mod130);
	}
	Stream<AccountingBreakdown> getAccountInfoInfo(AONContext ctx, Mod130 mod, IModelScript<Mod130Key> script, IMod130KeyDAO keyDAO) {
		return null;
	}
	@Override
	Mod130MVELContext getMVELcontextForComputeKey(AONContext ctx, Mod130 mod130) {
		return super.getMVELcontext(ctx, mod130);
	}
	
}
