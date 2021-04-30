package net.aonsolutions.aon.api.servlet;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.json.FiscalMatrixParamsJSON;
import com.esferalia.aon.occam.api.json.FiscalModelJSON;
import com.esferalia.aon.occam.api.json.IJsonNames;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.finance.BankAccount;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IFiscalModelTypeVisitor;
import com.esferalia.aon.occam.api.model.fiscal.FiscalMatrixParams;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.impl.jooq.dao.FiscalMenuDAO;
import com.esferalia.aon.occam.impl.jooq.dao.Mod111DAO;
import com.esferalia.aon.occam.impl.jooq.dao.Mod115DAO;
import com.esferalia.aon.occam.impl.jooq.dao.Mod123DAO;
import com.esferalia.aon.occam.impl.jooq.dao.Mod130DAO;
import com.esferalia.aon.occam.impl.jooq.dao.Mod131DAO;
import com.esferalia.aon.occam.impl.jooq.dao.Mod202DAO;
import com.esferalia.aon.occam.impl.jooq.dao.Mod303DAO;
import com.esferalia.aon.watson.util.AonStringUtils;
import net.aonsolutions.aon.api.ewok.AonApiData;

@WebServlet(name = "AonFiscalServlet", urlPatterns = {"/ms/api/fiscal/*"})

public class FiscalServlet extends AonApiHttpServlet{
		
	private static final long serialVersionUID = -8021598700474389724L;
	
	private static final Logger LOGGER  = Logger.getLogger(FiscalServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API FISCAL SERVLET - GET METHOD");
		try {
			AonApiData api = initialize(req, resp);
			if ( AonStringUtils.endsWith(api.getPath(), "/models") ) {
				response(req, resp, getFiscalModels(api));
			} else if ( AonStringUtils.endsWith(api.getPath(), "/matrix") ) {
				response(req, resp, getFiscalMatrix(api));
			} else {
				throw new Exception("La ruta introducida es incorrecta.");
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API FISCAL SERVLET - POST METHOD");
		try {
			AonApiData api = initialize(req, resp);
			if ( AonStringUtils.endsWith(api.getPath(), "/markAsFinished") ) {
				response(req, resp, markAsFinished(api));
			} else {
				throw new Exception("La ruta introducida es incorrecta.");
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
		
	private JSONArray getFiscalMatrix(AonApiData api) throws ParseException {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin());
			JSONObject jsonParams = api.getParams();
			FiscalMatrixParams params = FiscalMatrixParamsJSON.fromJSON(jsonParams); 
			return FiscalMenuDAO.getDomainsModels(ctx, api.getDomain().getId(), params); 
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	private JSONArray getFiscalModels(AonApiData api) throws ParseException {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin());
			LinkedList<FiscalModel> models = new LinkedList<FiscalModel>();
			models.addAll( Mod303DAO.getMod303s(ctx, api.getDomain().getId()).collect(Collectors.toCollection(LinkedList::new)));
			models.addAll( Mod111DAO.getMod111s(ctx, api.getDomain().getId()).collect(Collectors.toCollection(LinkedList::new)));
			models.addAll( Mod115DAO.getMod115s(ctx, api.getDomain().getId()).collect(Collectors.toCollection(LinkedList::new)));
			models.addAll( Mod123DAO.getMod123s(ctx, api.getDomain().getId()).collect(Collectors.toCollection(LinkedList::new)));
			models.addAll( Mod130DAO.getMod130s(ctx, api.getDomain().getId()).collect(Collectors.toCollection(LinkedList::new)));
			models.addAll( Mod131DAO.getMod131s(ctx, api.getDomain().getId()).collect(Collectors.toCollection(LinkedList::new)));
			models.addAll( Mod202DAO.getMod202s(ctx, api.getDomain().getId()).collect(Collectors.toCollection(LinkedList::new)));
			JSONArray jsonModels = new JSONArray();
			int i = 0;
			for (FiscalModel model : models ) {
				jsonModels.put(i++, FiscalModelJSON.toJSON(model));
			}
			return jsonModels; 
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	private JSONObject markAsFinished(AonApiData api) {
		try ( final AONContext ctx = AONContext.getAONContext(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin())) {
			JSONObject params = api.getData();
			Integer id = JsonUtils.getInteger(params , IJsonNames.ID);
			String iban = JsonUtils.getString(params , IJsonNames.IBAN);
			String bankAlias = JsonUtils.getString(params , IJsonNames.BANK_ALIAS);
			String bankBIC = JsonUtils.getString(params , IJsonNames.BIC);
			FiscalModelType type = FiscalModelType.safeValueOf(JsonUtils.getString(params , IJsonNames.MODEL));
			String typ = JsonUtils.getString(params , IJsonNames.TYPE);
			if (AonStringUtils.isNotBlank(typ)) {
				FiscalModelDeclarationType dec = FiscalModelDeclarationType.valueOf(typ);
				IFiscalModelTypeVisitor visitor = new IFiscalModelTypeVisitor() {

					@Override
					public void visitM111() {
						Mod111 model = Mod111DAO.getMod111(ctx, id);	
						model.setDeclarationType(dec);
						if (AonStringUtils.isNotBlank(iban) && model.getFinance() != null) {
							BankAccount ba = new BankAccount( iban );
							model.getFinance().setBankAccount(ba);
							model.getFinance().setBankAlias(bankAlias);
							model.getFinance().setBic(bankBIC);
						}
						Mod111DAO.markAsFinished(ctx, model); 
					}
					
					@Override
					public void visitM115() {
						Mod115 model = Mod115DAO.getMod115(ctx, id);	
						model.setDeclarationType(dec);
						if (AonStringUtils.isNotBlank(iban) && model.getFinance() != null) {
							BankAccount ba = new BankAccount( iban );
							model.getFinance().setBankAccount(ba);
						}
						Mod115DAO.markAsFinished(ctx, model); 
					}

					@Override
					public void visitM123() {
						Mod123 model = Mod123DAO.getMod123(ctx, id);	
						model.setDeclarationType(dec);
						if (AonStringUtils.isNotBlank(iban) && model.getFinance() != null) {
							BankAccount ba = new BankAccount( iban );
							model.getFinance().setBankAccount(ba);
						}
						Mod123DAO.markAsFinished(ctx, model); 
					}

					@Override
					public void visitM130() {
						Mod130 model = Mod130DAO.getMod130(ctx, id);	
						model.setDeclarationType(dec);
						if (AonStringUtils.isNotBlank(iban) && model.getFinance() != null) {
							BankAccount ba = new BankAccount( iban );
							model.getFinance().setBankAccount(ba);
						}
						Mod130DAO.markAsFinished(ctx, model); 
					}

					@Override
					public void visitM131() {
						Mod131 model = Mod131DAO.getMod131(ctx, id);	
						model.setDeclarationType(dec);
						if (AonStringUtils.isNotBlank(iban) && model.getFinance() != null) {
							BankAccount ba = new BankAccount( iban );
							model.getFinance().setBankAccount(ba);
						}
						Mod131DAO.markAsFinished(ctx, model); 
					}

					@Override
					public void visitM202() {
						Mod202 model = Mod202DAO.getMod202(ctx, id);	
						model.setDeclarationType(dec);
						if (AonStringUtils.isNotBlank(iban) && model.getFinance() != null) {
							BankAccount ba = new BankAccount( iban );
							model.getFinance().setBankAccount(ba);
						}
						Mod202DAO.markAsFinished(ctx, model); 
					}

					@Override
					public void visitM303() {
						Mod303 model = Mod303DAO.getMod303(ctx, id);
						model.setDeclarationType(dec);
						if (AonStringUtils.isNotBlank(iban) && model.getFinance() != null) {
							BankAccount ba = new BankAccount( iban );
							model.getFinance().setBankAccount(ba);
						}
						Mod303DAO.markAsFinishedFromAPI(ctx, model);
					}
					
					@Override public void visitM347() {}
					@Override public void visitM349() {}
					@Override public void visitM390() {}
					@Override public void visitM390HF() {}
					@Override public void visitM180() {}
					@Override public void visitM184() {}
					@Override public void visitM190() {}
					@Override public void visitM193() {}
					@Override public void visitM200() {}
				}; 
				type.visit(visitor);
			}
			return new JSONObject().put("status", "OK"); 
		}
	}
}

