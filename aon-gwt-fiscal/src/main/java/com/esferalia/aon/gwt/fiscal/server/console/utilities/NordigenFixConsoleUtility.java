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
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenBankAccount;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenConfiguration;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenRequisition;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenRequisitionStatus;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.impl.jooq.console.ConsoleMessageUtils;
import com.esferalia.aon.occam.impl.jooq.console.ConsoleParams;
import com.esferalia.aon.occam.impl.jooq.dao.BankStatementDAO;
import com.esferalia.aon.watson.util.AonStringUtils;

class NordigenFixConsoleUtility extends AbstractConsoleUtility {

	@Override
	protected String getTitle() {
		return "Arreglo NORDIGEN";
	}
	
	@Override
	protected void doUtility(String processId, ConsoleParams params, DomainParams domainParams) {
		ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.ok(processId, "Inicio del proceso."));
		AONContext ctx = params.getFromConnection().getAONContext();
		NordigenServiceImpl nordigen = new NordigenServiceImpl();
		ctx.getDslContext()
			.select(DOMAIN.ID, DOMAIN.NAME, DOMAIN.DESCRIPTION)
			.from(DOMAIN)
			.where( DOMAIN.ID.eq(0))
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
					if(requisition.getStatus() == NordigenRequisitionStatus.LINKED) {
						List<String> accIds = requisition.getAccounts();
						if(!(accIds == null || accIds.isEmpty() || accIds.size() == 1)) {
							String account = nordigen.getAccountIdByIban(config.getToken(), bank.getRequisition(), bank);
							List<String> wrongAccounts = accIds.stream()
									.filter(id -> !id.equals(account))
									.collect(Collectors.toList());
							List<BankStatement> wrongMovements = nordigen.checkIncorrectMovements(occam, config.getToken(), wrongAccounts, bank);
							List<Integer> wrongMovementsIds = wrongMovements.stream().map(r -> r.getId()).toList();
							for(Integer movement : wrongMovementsIds){
								ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.message(processId, "Deleting bank movement ID(BANK_STATEMENT): " + movement + 
										", Bank account: " + bank.getBankAccount().getIban() + ", Requisition: " + bank.getRequisition()));
							}
                            try {
                                int deletedRows = ctx.getDslContext().delete(BANK_STATEMENT).where(BANK_STATEMENT.ID.in(wrongMovementsIds)).execute();
                                ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.ok(processId, "Filas eliminadas: " + deletedRows));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
							NordigenBankAccount nordigenAccount = new NordigenBankAccount()
									.setRbank(bank)
									.setIban(bank != null && bank.getBankAccount() != null ? bank.getBankAccount().getIban() : null)
									.setBankAlias(bank != null ? bank.getAlias() : null)
									.setLinked(AonStringUtils.isNotBlank(bank.getRequisition()))
									.setRequisitionId(bank.getRequisition())
									.setLastMovementDate(BankStatementDAO.getLastMovementDate(ctx, bank.getId()));
							nordigen.insertCorrectMovements(occam, nordigenAccount);
						}
					} else {
						ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.message(processId, "No se puede comprobar el banco -> "
							+ "domain: "
							+ bank.getDomain()
							+ "id:" 
							+ bank.getId()
							+ " IBAN: "
							+ bank.getBankAccount().getIban()
							+ ", se debe actualizar el agreement (acuerdo del usuario.)"
						));
					}
				}
			});
		ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.ok(processId, "Final del proceso."));
	}
}
