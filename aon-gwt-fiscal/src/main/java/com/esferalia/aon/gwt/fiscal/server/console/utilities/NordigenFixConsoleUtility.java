package com.esferalia.aon.gwt.fiscal.server.console.utilities;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.BankStatement.BANK_STATEMENT;

import java.util.List;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.finance.server.NordigenServiceImpl;
import com.esferalia.aon.jooq.tables.User;
import com.esferalia.aon.jooq.tables.UserScope;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.DomainParams;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.BankStatement;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenConfiguration;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenRequisition;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.impl.jooq.console.ConsoleMessageUtils;
import com.esferalia.aon.occam.impl.jooq.console.ConsoleParams;
import com.esferalia.aon.occam.impl.jooq.dao.console.ConsoleDAO;

class NordigenFixConsoleUtility extends AbstractConsoleUtility {

	protected void doUtility(String processId, ConsoleParams params, DomainParams domainParams) {	
		ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.ok(processId, "Inicio del proceso."));
		AONContext ctx = params.getFromConnection().getAONContext();
		NordigenServiceImpl nordigen = new NordigenServiceImpl();
		ctx.getDslContext()
			.select(DOMAIN.ID, DOMAIN.NAME,DOMAIN.DESCRIPTION)
			.from(DOMAIN)
			.where( ConsoleDAO.getFilter( domainParams ) )
			.fetch()
			.stream()
			.forEach( result -> {
				String login = ctx.getDslContext()
						.select(User.USER.LOGIN)
						.from(User.USER)
						.join(UserScope.USER_SCOPE)
						.on(User.USER.ID.eq(UserScope.USER_SCOPE.USER_ID))
						.where(User.USER.DOMAIN.eq(result.getValue(DOMAIN.ID)))
						.fetch().stream().findFirst().get().component1();
				Occam occam = new Occam()
						.setDomain(result.getValue(DOMAIN.ID))
						.setDomainName(result.getValue(DOMAIN.NAME))
						.setUser(login);
				List<RegistryBank> banks = nordigen.getByRequisitionIsNotNull(occam);
				NordigenConfiguration config = nordigen.getConfiguration(occam);
				for(RegistryBank bank : banks) {
					NordigenRequisition requisition = nordigen.getRequisition(config.getToken(), bank.getRequisition());
					List<String> accIds = requisition.getAccounts();
					if(!(accIds == null || accIds.isEmpty() || accIds.size() == 1)) {
						String account = nordigen.getAccountIdByIban(config.getToken(), bank.getRequisition(), bank);
						List<String> wrongAccounts = accIds.stream()
                                .filter(id -> !id.equals(account))
                                .collect(Collectors.toList());
						List<BankStatement> wrongMovements = nordigen.checkIncorrectMovements(occam, config.getToken(), wrongAccounts, bank);
						List<Integer> wrongMovementsIds = wrongMovements.stream().map(r -> r.getId()).toList();
						ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.ok(processId, "moves: " + wrongMovementsIds.toString()));
//						ctx.getDslContext().delete(BANK_STATEMENT).where(BANK_STATEMENT.ID.in(wrongMovements)).execute();
                    }
				}
			});
		ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.ok(processId, "Final del proceso."));
	}
	
}
