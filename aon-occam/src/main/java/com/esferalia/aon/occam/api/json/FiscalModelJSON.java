package com.esferalia.aon.occam.api.json;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IFiscalModelTypeVisitor;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.util.AonNumberUtils;

public enum FiscalModelJSON {

	ID {
		@Override
		public FiscalModel from(FiscalModel model, JSONObject json) {
			return model.setId(AonNumberUtils.toInteger(  json.optNumber(IJsonNames.ID, null) ));
		}
		@Override
		public JSONObject to(FiscalModel model, JSONObject json) {
			return json.put(IJsonNames.ID, model.getId());
		}
	},
	DOMAIN{
		@Override
		public FiscalModel from(FiscalModel model, JSONObject json) {
			return model.setDomain(AonNumberUtils.toInteger(  json.optNumber(IJsonNames.DOMAIN, null) ));
		}
		@Override
		public JSONObject to(FiscalModel model, JSONObject json) {
			return json.put(IJsonNames.DOMAIN, model.getDomain());
		}
	},
	ADMINISTRATION{
		@Override
		public FiscalModel from(FiscalModel model, JSONObject json) {
			return model.setAdministration(Administration.safeValueOf( json.optString(IJsonNames.ADMINISTRATION,null) ));
		}
		@Override
		public JSONObject to(FiscalModel model, JSONObject json) {
			return json.put(IJsonNames.ADMINISTRATION, model.getAdministration().toString());
		}
	},
	MODEL{
		@Override
		public FiscalModel from(FiscalModel model, JSONObject json) {
			return model.setModel(FiscalModelType.safeValueOf( json.optString(IJsonNames.MODEL,null) ));
		}
		@Override
		public JSONObject to(FiscalModel model, JSONObject json) {
			return json.put(IJsonNames.MODEL, model.getModel().getValue());
		}
	},
	YEAR{
		@Override
		public FiscalModel from(FiscalModel model, JSONObject json) {
			return model.setYear(AonNumberUtils.toint(  json.optNumber(IJsonNames.YEAR, null) ));
		}
		@Override
		public JSONObject to(FiscalModel model, JSONObject json) {
			return json.put(IJsonNames.YEAR, model.getYear());
		}
	},
	PERIOD{
		@Override
		public FiscalModel from(FiscalModel model, JSONObject json) {
			return model.setPeriod(Period.safeValueOf( json.optString(IJsonNames.PERIOD,null) ));
		}
		@Override
		public JSONObject to(FiscalModel model, JSONObject json) {
			return json.put(IJsonNames.PERIOD, model.getPeriod().toString());
		}
	},
	STATUS{
		@Override
		public FiscalModel from(FiscalModel model, JSONObject json) {
			return model.setStatus(FiscalStatus.safeValueOf( json.optString(IJsonNames.STATUS,null) ));
		}
		@Override
		public JSONObject to(FiscalModel model, JSONObject json) {
			return json.put(IJsonNames.STATUS, model.getStatus().toString());
		}
	},
	COMPLEMENTARY{
		@Override
		public FiscalModel from(FiscalModel model, JSONObject json) {
			return model.setComplementary(json.optBoolean(IJsonNames.COMPLEMENTARY));
		}
		@Override
		public JSONObject to(FiscalModel model, JSONObject json) {
			return json.put(IJsonNames.COMPLEMENTARY, model.isComplementary());
		}
	},
	REPLACEMENT{
		@Override
		public FiscalModel from(FiscalModel model, JSONObject json) {
			return model.setReplacement(json.optBoolean(IJsonNames.REPLACEMENT));
		}
		@Override
		public JSONObject to(FiscalModel model, JSONObject json) {
			return json.put(IJsonNames.REPLACEMENT, model.isReplacement());
		}
	},
	DOCUMENT{
		@Override
		public FiscalModel from(FiscalModel model, JSONObject json) {
			return model.setDocument(json.optString(IJsonNames.DOCUMENT,null));
		}
		@Override
		public JSONObject to(FiscalModel model, JSONObject json) {
			return json.put(IJsonNames.DOCUMENT, model.getDocument());
		}
	},
	NAME{
		@Override
		public FiscalModel from(FiscalModel model, JSONObject json) {
			return model.setName(json.optString(IJsonNames.NAME,null));
		}
		@Override
		public JSONObject to(FiscalModel model, JSONObject json) {
			return json.put(IJsonNames.NAME, model.getName());
		}
	},
	SURNAME{
		@Override
		public FiscalModel from(FiscalModel model, JSONObject json) {
			return model.setName(json.optString(IJsonNames.SURNAME,null));
		}
		@Override
		public JSONObject to(FiscalModel model, JSONObject json) {
			return json.put(IJsonNames.SURNAME, model.getName());
		}
	},
	IBAN{
		@Override
		public FiscalModel from(FiscalModel model, JSONObject json) {
			model.setIban(json.optString(IJsonNames.IBAN,null));
			return model; 
		}
		@Override
		public JSONObject to(FiscalModel model, JSONObject json) {
			return json.put(IJsonNames.IBAN, (model.getFinance() != null && model.getFinance().getBankAccount() != null)?model.getFinance().getBankAccount().getIban():"");
		}
	},
	RESULT{
		@Override
		public FiscalModel from(FiscalModel model, JSONObject json) {
			return model;
		}
		@Override
		public JSONObject to(FiscalModel model, JSONObject json) {
			model.getModel().visit(new IFiscalModelTypeVisitor() {
				
				private void visitEmpty() {
					json.put(IJsonNames.RESULT, model.getResult());	
				}
				private void visitResult() {
					json.put(IJsonNames.RESULT, model.getDeclarationResult());
				}
				@Override public void visitM349()  { visitEmpty();}
				@Override public void visitM347()  { visitEmpty();}
				@Override public void visitM200()  { visitEmpty();}
				@Override public void visitM193()  { visitEmpty();}
				@Override public void visitM190()  { visitEmpty();}
				@Override public void visitM184()  { visitEmpty();}
				@Override public void visitM180()  { visitEmpty();}

				@Override public void visitM390()  { visitResult();}
				@Override public void visitM131()  { visitResult();}
				@Override public void visitM202()  { visitResult();}
				@Override public void visitM130()  { visitResult();}
				@Override public void visitM303()  { visitResult();}
				@Override public void visitM123()  { visitResult();}
				@Override public void visitM115()  { visitResult();}
				@Override public void visitM111()  { visitResult();}
				@Override public void visitM390HF(){ visitResult();}
				@Override public void visitM369()  { visitResult();}
				@Override public void visitM421()  { visitResult();}
			});
			return json;
		}
	},
	DECLARATION_TYPE{
		@Override
		public FiscalModel from(FiscalModel model, JSONObject json) {
			return model;
		}
		@Override
		public JSONObject to(FiscalModel model, JSONObject json) {
			model.getModel().visit(new IFiscalModelTypeVisitor() {
				
				private void visitOld() {
					json.put(IJsonNames.TYPE, model.getDeclarationType());	
				}
				private void visitDeclarationResultType() {
					json.put(IJsonNames.TYPE, model.getDeclarationResultType());
				}
				
				@Override public void visitM349()  { visitOld();}
				@Override public void visitM347()  { visitOld();}
				@Override public void visitM200()  { visitOld();}
				@Override public void visitM193()  { visitOld();}
				@Override public void visitM190()  { visitOld();}
				@Override public void visitM184()  { visitOld();}
				@Override public void visitM180()  { visitOld();}

				@Override public void visitM202()  { visitDeclarationResultType();}
				@Override public void visitM131()  { visitDeclarationResultType();}
				@Override public void visitM130()  { visitDeclarationResultType();}
				@Override public void visitM390()  { visitDeclarationResultType();}
				@Override public void visitM390HF(){ visitDeclarationResultType();}
				@Override public void visitM303()  { visitDeclarationResultType();}
				@Override public void visitM123()  { visitDeclarationResultType();}
				@Override public void visitM115()  { visitDeclarationResultType();}
				@Override public void visitM111()  { visitDeclarationResultType();}
				@Override public void visitM369()  { visitDeclarationResultType();}
				@Override public void visitM421()  { visitDeclarationResultType();}
			});
			return json;
		}
	},
	;

	public abstract FiscalModel from(FiscalModel model, JSONObject json);
	public abstract JSONObject to(FiscalModel model, JSONObject json);
	
	public static JSONObject toJSON(FiscalModel model) {
		if (model != null) {
			JSONObject json = new JSONObject();
			for (FiscalModelJSON p : FiscalModelJSON.values()) {
				p.to(model, json);
			}
			return json;
		}
		return null;
	}
	
	public static FiscalModel fromString(String text) {
		JSONObject json = new JSONObject(text);
		return fromJSON(json); 
	}

	public static FiscalModel fromJSON(JSONObject json) {
		if (json != null) {
			FiscalModel fiscalModel = new FiscalModel();
			for (FiscalModelJSON p : FiscalModelJSON.values()) {
				p.from(fiscalModel, json);
			}
			return fiscalModel;
		}
		return null;
	}

}
