package net.aonsolutions.aon.bank.nordigen;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.LogData;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccessToken;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccountBalance;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccountTransactions;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAgreement;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenBankAccount;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenBankStatement;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenConfiguration;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenException;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenInstitution;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenRequisition;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenRequisitionStatus;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.impl.jooq.dao.BankStatementDAO;
import com.esferalia.aon.occam.impl.jooq.dao.NordigenDAO;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.bank.nordigen.utils.NordigenAgreementUtils;
import net.aonsolutions.aon.bank.nordigen.utils.NordigenBalancesUtils;
import net.aonsolutions.aon.bank.nordigen.utils.NordigenInstitutionUtils;
import net.aonsolutions.aon.bank.nordigen.utils.NordigenMetadaUtils;
import net.aonsolutions.aon.bank.nordigen.utils.NordigenRequisitionUtils;
import net.aonsolutions.aon.bank.nordigen.utils.NordigenTokenUtils;
import net.aonsolutions.aon.bank.nordigen.utils.NordigenTransactionsUtils;

public class AonNordigen {

	private static final String LINK_REGEX = "^https\\:\\/\\/(?<link>.*?)\\/ms\\/.*$";
	private static final Pattern LINK_PATTERN = Pattern.compile(LINK_REGEX, Pattern.CASE_INSENSITIVE);

	private AonNordigen() {
	}

	// Esta en NordigenServiceImpl
	public static NordigenConfiguration getConfiguration(Occam occam) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return new NordigenConfiguration().setConfiguration(AON.getConfiguration(ctx))
					.setToken(NordigenTokenUtils.getNewAccessToken()).setAccounts(NordigenDAO.getAllAccounts(ctx));
		} catch (AonCoreException ex) {
			AON_SOLUTIONS.insertLogData(occam, new LogData(occam.getDomain(), ex));
			throw new NordigenException(ex.getMessage());
		}
	}

	// Esta en NordigenServiceImpl
	public static List<NordigenInstitution> getInstitutions(NordigenAccessToken token, Country country,
			Boolean paymentsEnabled) {
		return NordigenAPI.getInstitutions(token.getAccess(), country, paymentsEnabled);
	}

	// Esta en NordigenServiceImpl
	public static List<NordigenInstitution> getInstitutionsByBic(NordigenAccessToken token, String bic) {
		if (AonStringUtils.isNotBlank(bic)) {
			List<NordigenInstitution> matchedInstitutions = AonCollectionUtils
					.stream(AonNordigen.getInstitutions(token, null, null))
					.filter(inst -> AonStringUtils.containsIgnoreCase(inst.getBic(), bic)).collect(Collectors.toList());
			if (AonCollectionUtils.isNotEmpty(matchedInstitutions)) {
				return matchedInstitutions;
			}
		}
		throw new NordigenException("No available institutions");
	}
	
	public static NordigenInstitution getInstitutionByBic(NordigenAccessToken token, String bic) {
	    if (AonStringUtils.isNotBlank(bic) && bicExist(token, bic)) {
	        Optional<NordigenInstitution> matchedInstitution = AonCollectionUtils
	                .stream(AonNordigen.getInstitutions(token, null, null))
	                .filter(inst -> AonStringUtils.containsIgnoreCase(inst.getBic(), bic))
	                .findFirst(); 
	        if (matchedInstitution.isPresent()) {
	            return matchedInstitution.get();
	        }
	    }

	    throw new NordigenException("No institution found for the provided BIC: " + bic);
	}

	
	public static boolean bicExist(NordigenAccessToken token, String bic) {
		if (AonStringUtils.isNotBlank(bic)) {
			
			if (bic.length() != 8 && bic.length() != 11) {
				throw new NordigenException("Invalid BIC length");
			}

			if (!bic.matches("^[A-Z]{4}[A-Z]{2}[A-Z0-9]{2}(?:[A-Z0-9]{3})?$")) {
				throw new NordigenException("Invalid BIC format");
			}
			
	        return AonCollectionUtils
	                .stream(AonNordigen.getInstitutions(token, null, null))
	                .anyMatch(inst -> AonStringUtils.containsIgnoreCase(inst.getBic(), bic)); 
	    } 
	    return false; 
	}

	// Esta en NordigenServiceImpl
	public static NordigenRequisition getRequisition(NordigenAccessToken token, String requisitionId) {
		return NordigenAPI.getRequisition(token.getAccess(), requisitionId);
	}

	// Esta en NordigenServiceImpl , mirar esta funcion , cambia un requisition de un banco vinculado como vacio
	public static List<NordigenRequisition> getDomainRequisitions(NordigenAccessToken token, String domainName) {
		LinkedList<NordigenRequisition> list = new LinkedList<>();
		AonCollectionUtils.stream(NordigenRequisitionUtils.getAllRequisitions(token)).filter(req -> {
			Matcher matcher = LINK_PATTERN.matcher(req.getRedirect());
			if (matcher.matches()) {
				String link = matcher.group("link");
				return link != null && AonStringUtils.equalsIgnoreCase(domainName, link);
			}
			return false;
		}).forEach(list::add);
		for (int i = 0; i < list.size(); i++) {
			System.out.println(list.get(i).getId());	
		}
		
		return list;
	}

	/// ************************************************************************************
	/// ************************************************************************************
	/// ************************************************************************************
	/// ************************************************************************************
	/// ************************************************************************************
	/// ************************************************************************************
	/// ************************************************************************************
	/// ************************************************************************************
	/// ************************************************************************************
	/// ************************************************************************************
	/// ************************************************************************************
	/// ************************************************************************************

	// ----------------------------------------------------------------
	// -------------------------------------------------------- [PUBLIC]
	// ----------------------------------------------------------------

	
	public static NordigenBankAccount setBankAccountValues(Occam occam, NordigenAccessToken token, NordigenBankAccount account) {
	    try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
	        RegistryBank rbank = account.getRbank();
	        if (rbank.getRequisition() != null) {
	            String reqId = rbank.getRequisition();
	            if (AonStringUtils.isNotBlank(reqId)) {
	                account.setLinked(true);
	                account.setRequisition(getRequisition(token, reqId));
//	                if (!account.getRequisition().getStatus().equals(NordigenRequisitionStatus.LINKED)) {
//						System.out.println("entra aki");
//						deleteRequisitionById(token, occam, reqId);
//					}
	                // ExecutorService para manejo de hilos
	                ExecutorService executor = Executors.newSingleThreadExecutor();
	                Future<Void> metadataFuture = executor.submit(() -> {
	                    int retries = 5;
	                    int delay = 500; // tiempo de espera en ms entre intentos
	                    for (int i = 0; i < retries; i++) {
	                        account.setMetadata(NordigenMetadaUtils.getNordigenAccountMetadata(token, account.getRequisition(), rbank));
	                        if (account.getMetadata() != null) {
	                            System.out.println("Metadata cargado exitosamente: " + account.getMetadata());
	                            return null; // si la metadata se carga correctamente, salir del bucle
	                        }
	                        System.out.println("Reintentando cargar metadata... Intento " + (i + 1));
	                        Thread.sleep(delay);
	                    }
	                    // Si después de los reintentos no se carga metadata, se deja null
	                    System.out.println("Error: No se pudo cargar metadata después de varios intentos.");
	                    account.addLog("Error al cargar metadata.");
	                    return null;
	                });

	                // Esperar a que se complete la carga de metadata
	                metadataFuture.get();  // Espera a que el Future termine

	                // Verificación de metadata antes de proceder
	                if (account.getMetadata() == null) {
	                    System.out.println("Error: Metadata no se cargó correctamente.");
	                    executor.shutdown();
	                    return account; // Devolvemos el objeto incompleto en caso de error
	                }

	                // Continuar con el resto de los hilos dependientes
	                String accountId = account.getMetadata().getId();
	                account.setLastMovementDate(BankStatementDAO.getLastMovementDate(ctx, account.getRbank().getId()));

	                // Hilo para obtener Institution
	                Thread institutionThread = new Thread(() -> {
	                    try {
	                        if (account.getRequisition().getInstitutionId() != null) {
	                            account.setInstitution(NordigenInstitutionUtils.getInstitution(token, account.getRequisition().getInstitutionId()));
	                        }
	                    } catch (Exception e) {
	                        e.printStackTrace();
	                        account.addLog(e.getMessage());
	                    }
	                });
	                institutionThread.start();

	                // Hilo para obtener Balances y Movimientos
	                Thread balancesThread = new Thread(() -> {
	                    try {
	                        account.setBalances(NordigenBalancesUtils.getAccountBalances(token, accountId));
	                        account.setNotInsertedMovements(NordigenTransactionsUtils.getNotInsertedTransactions(token, account));
	                    } catch (Exception e) {
	                        e.printStackTrace();
	                        AON_SOLUTIONS.insertLogData(occam, new LogData(occam.getDomain(), e));
	                        account.addLog(e.getMessage());
	                    }
	                });
	                balancesThread.start();

	                // Espera a que todos los hilos finalicen
	                institutionThread.join();
	                balancesThread.join();
	                
	                // Cerrar el executor
	                executor.shutdown();
	            }
	        }
	        return NordigenDAO.updateRegistryBank(ctx, account);
	    } catch (InterruptedException | ExecutionException e) {
	        Thread.currentThread().interrupt();
	        AON_SOLUTIONS.insertLogData(occam, new LogData(occam.getDomain(), e));
	        throw new AonCoreException(e);
	    }
	}
	
	
	

	// Esta en NordigenServiceImpl
	public static boolean deleteRequisitionById(NordigenAccessToken token, Occam occam, String requisitionId) {
		NordigenRequisition requisition = AonNordigen.getRequisition(token, requisitionId);
		List<RegistryBank> rbanks = NordigenRequisitionUtils.getRbanksByRequisition(occam, requisitionId);
		boolean requisitionDeleted = true;
		if (requisition != null) {
			try {
				NordigenRequisitionUtils.deleteRequisition(token, requisition);
			} catch (Exception e) {
				AON_SOLUTIONS.insertLogData(occam, new LogData(occam.getDomain(), e));
				requisitionDeleted = false;
			}
		}
		if (rbanks != null) {
			for (RegistryBank rbank : rbanks) {
				rbank.setRequisition(null);
				NordigenUtils.updateRbank(occam, rbank);
			}
		}
		return requisitionDeleted;
	}

	// Esta en NordigenServiceImpl
	public static void deleteRequisitionByRbank(NordigenAccessToken token, Occam occam, Integer rbankId) {
		if (rbankId == null)
			return;
		RegistryBank rbank = AON.getRegistryBank(occam, f -> f.getIdProperty().eq(rbankId));
		if (rbank == null)
			return;

		String requisition = rbank.getRequisition();
		rbank.setRequisition(null);
		AON.saveRegistryBank(occam, rbank);
		if (requisition != null) {
			NordigenRequisitionUtils.deleteRequisition(token, requisition);
		}
	}

	// Esta en NordigenServiceImpl
	public static int insertStatements(Occam occam, NordigenBankAccount account) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return NordigenDAO.insertStatements(ctx, account);
		}
	}

	// Esta en NordigenServiceImpl
	public static List<NordigenBankStatement> getMovements(NordigenAccessToken token, Occam occam,
			NordigenBankAccount nordigenBankAccount, Date endDate, boolean online) {
		System.out.println("Pasa por getMovementes");
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			List<NordigenBankStatement> list = new LinkedList<>();
			if (nordigenBankAccount != null && nordigenBankAccount.getMetadata() != null) {
				String id = nordigenBankAccount.getMetadata().getId();
				if (online) {
					NordigenAccountTransactions transactions = NordigenTransactionsUtils.getTransactions(token, id,
							endDate);
					if (transactions != null) {	
					list.addAll(
							NordigenTransactionsUtils.getPendingAccountTransactions(nordigenBankAccount, transactions));
					list.addAll(NordigenTransactionsUtils.getBookedAccountTransactions(nordigenBankAccount,
							transactions, endDate));
					}
//					if (transactions != null) {
//						AonCollectionUtils.stream(AonNordigen.getPendingAccountTransactions(transactions))
//						.map(tr -> nordigenBankAccount.toBankStatement(tr)).forEach(bs -> {
//							bs.setPending(true);
//							list.add(bs);
//						});
//						AonCollectionUtils.stream(AonNordigen.getBookedAccountTransactions(transactions,endDate))
//						.map(tr -> nordigenBankAccount.toBankStatement(tr)).forEach(bs -> {
//							bs.setPending(false);
//							list.add(bs);
//						});
//					}
				} else {
					list.addAll(nordigenBankAccount.getNotInsertedMovements());
					list.addAll(NordigenTransactionsUtils.getStoredBankStatements(occam, nordigenBankAccount.getRbank(),
							endDate));
				}
			}
			List<NordigenBankStatement> orderedList = list.stream().filter(Objects::nonNull)
					.collect(Collectors.toList());
			
			List<NordigenAccountBalance> balances = nordigenBankAccount.getBalances();
			NordigenAccountBalance consolidado = NordigenBalancesUtils.filterConsolidado(balances);
			NordigenAccountBalance real = NordigenBalancesUtils.filterReal(balances);
			NordigenAccountBalance balance = consolidado != null ? consolidado : real;
			Double remaining = (balance != null && balance.getBalanceAmount() != null)
					? balance.getBalanceAmount().getAmount()
					: 0;
			for (NordigenBankStatement bs : orderedList) {
				bs.setCurrentBalance(remaining);
				remaining += (bs.getAmount() * (bs.isPayment() ? 1 : -1));
			}
			return orderedList;
		} catch (NordigenException e) {
			AON_SOLUTIONS.insertLogData(occam, new LogData(occam.getDomain(), e));
			throw e;
		} catch (Exception e) {
			AON_SOLUTIONS.insertLogData(occam, new LogData(occam.getDomain(), e));
			throw mapException(e);
		}
	}

	// Esta en NordigenServiceImpl
//	public static NordigenRequisition addAccount(NordigenAccessToken token, Occam occam,
//			NordigenBankAccount nordigenBankAccount) {
//		RegistryBank rbank = nordigenBankAccount.getRbank();
//		if (rbank.getBankAccount().getIban().equals("GL8262400000062409")
//				|| rbank.getBankAccount().getIban().equals("GL4076010000076016")) {
//			NordigenAgreement agreement = NordigenAgreementUtils.createAgreement(token, "SANDBOXFINANCE_SFIN0000");
//			String redirect = "https://www.youtube.com/watch?v=dQw4w9WgXcQ";
//
//			NordigenRequisition requisition = AonNordigen.createRequisition(token, agreement, redirect);
//			NordigenRequisitionUtils.updateRequisitionId(occam, requisition, rbank.getId());
//			return requisition;
//		}
//		Pattern bicPattern = Pattern.compile("^(?<bic>.*?)X*$", Pattern.CASE_INSENSITIVE);
//		Matcher bicMatcher = bicPattern.matcher(AonStringUtils.trimToEmpty(rbank.getBic()));
//		StringBuilder bicBuilder = new StringBuilder();
//		if (bicMatcher.matches()) {
//			bicBuilder.append(AonStringUtils.trimToEmpty(bicMatcher.group("bic")));
//		}
//		final String bic = bicBuilder.toString();
//
//
//		List<NordigenInstitution> instList = AonNordigen.getInstitutions(token, null, null).stream()
//				.filter(inst -> AonStringUtils.equalsIgnoreCase(inst.getBic(), bic)).toList();
//
//		if (instList.isEmpty()) {
//			throw new NordigenException("No institution found for the provided BIC: " + bic);
//		}
//
//		Optional<NordigenInstitution> optInstitution = Optional
//				.ofNullable(instList.size() == 1 ? instList.get(0) : null);
//		StringBuilder instIdBuilder = new StringBuilder();
//		if (optInstitution.isPresent()) {
//			instIdBuilder.append(AonStringUtils.trimToEmpty(optInstitution.get().getId()));
//		}
//		final String institutionId = instIdBuilder.toString();
//
//		// EN CASO DE QUE HAYA PROBLEMAS CON EL BIC:
//		NordigenInstitution inst = nordigenBankAccount.getInstitution();
//
//		if (inst == null && instList.size() > 1) {
//			throw new NordigenException("Too much institutions");
//		}
//
//		NordigenAgreement agreement = NordigenAgreementUtils.createAgreement(token,
//				inst != null ? inst.getId() : institutionId);
//		NordigenRequisition requisition = AonNordigen.createRequisition(token, agreement,
//				"https://" + occam.getDomainName() + "/ms/api/task-evaluation/rbank?rbank="
//						+ (rbank != null ? "" + rbank.getId() : ""));
//
//		NordigenRequisitionUtils.updateRequisitionId(occam, requisition, rbank.getId());
//
//		String msg = "El usuario" + occam.getUser() + "del dominio " + occam.getDomainName()
//				+ " vinculo el banco con id : " + nordigenBankAccount.getRbank().getId() + " y su requisition es: "
//				+ requisition.getId();
//
//		LogData data = new LogData(occam.getDomain(), msg);
//
//		AON_SOLUTIONS.insertLogData(occam.getDomainName(), occam.getDomain(), occam.getUser(), data);
//
//		return requisition;
//	}
	
	public static NordigenRequisition addAccount(NordigenAccessToken token, Occam occam, NordigenBankAccount nordigenBankAccount) {
	    RegistryBank rbank = nordigenBankAccount.getRbank();
	    
	    // Verificar si es una cuenta IBAN de sandbox
	    if (isSandboxAccount(rbank.getBankAccount().getIban())) {
	        return handleSandboxAccount(token, occam, nordigenBankAccount);
	    }

	    // Procesar BIC y obtener la institución asociada
	    String bic = extractBic(rbank.getBic());
	    
	    if (!bicExist(token, bic)) {
	        throw new NordigenException("No institution found for the provided BIC: " + bic);
	    }

	    NordigenInstitution institution = getInstitutionByBic(token, bic);
	    NordigenInstitution inst = nordigenBankAccount.getInstitution() != null ? nordigenBankAccount.getInstitution() : institution;

	    // Crear el acuerdo y la requisition para la cuenta bancaria
	    NordigenAgreement agreement = NordigenAgreementUtils.createAgreement(token, inst.getId());
	    String redirectUrl = generateRedirectUrl(occam, rbank.getId());

	    NordigenRequisition requisition = AonNordigen.createRequisition(token, agreement, redirectUrl);
	    NordigenRequisitionUtils.updateRequisitionId(occam, requisition, rbank.getId());

	    logRequisition(occam, nordigenBankAccount, requisition);

	    return requisition;
	}

	//////////////////////
	//DESGLOSE addAccount
	private static boolean isSandboxAccount(String iban) {
	    return iban.equals("GL8262400000062409") || iban.equals("GL4076010000076016");
	}

	private static NordigenRequisition handleSandboxAccount(NordigenAccessToken token, Occam occam, NordigenBankAccount nordigenBankAccount) {
	    NordigenAgreement agreement = NordigenAgreementUtils.createAgreement(token, "SANDBOXFINANCE_SFIN0000");
	    String redirect = "https://www.youtube.com/watch?v=dQw4w9WgXcQ";
//	    RegistryBank rbank = nordigenBankAccount.getRbank();


	    NordigenRequisition requisition = AonNordigen.createRequisition(token, agreement, redirect);
//	    String redirectUrl = generateRedirectUrl(occam, rbank.getId());

//	    NordigenRequisition requisition = AonNordigen.createRequisition(token, agreement, redirectUrl);

	    NordigenRequisitionUtils.updateRequisitionId(occam, requisition, nordigenBankAccount.getRbank().getId());

	    logRequisition(occam, nordigenBankAccount, requisition);

	    return requisition;
	}

	private static String extractBic(String bicInput) {
	    Pattern bicPattern = Pattern.compile("^(?<bic>.*?)X*$", Pattern.CASE_INSENSITIVE);
	    Matcher bicMatcher = bicPattern.matcher(AonStringUtils.trimToEmpty(bicInput));

	    if (bicMatcher.matches()) {
	        return AonStringUtils.trimToEmpty(bicMatcher.group("bic"));
	    }
	    throw new NordigenException("Invalid BIC format.");
	}

	private static String generateRedirectUrl(Occam occam, int rbankId) {
	    return "https://" + occam.getDomainName() + "/ms/api/task-evaluation/rbank?rbank=" + rbankId;
	}

	private static void logRequisition(Occam occam, NordigenBankAccount nordigenBankAccount, NordigenRequisition requisition) {
	    String msg = "El usuario " + occam.getUser() + " del dominio " + occam.getDomainName()
	            + " vinculó el banco con id: " + nordigenBankAccount.getRbank().getId() + " y su requisition es: "
	            + requisition.getId();
	    
	    LogData data = new LogData(occam.getDomain(), msg);
	    AON_SOLUTIONS.insertLogData(occam.getDomainName(), occam.getDomain(), occam.getUser(), data);
	}
	//////////////////////


	// -------------------------- Métodos con tests y excepcion.

	private static NordigenException mapException(Exception exception) throws IllegalArgumentException {
		exception.printStackTrace();
		String err = null;
		try {
			JSONObject errJson = new JSONObject(exception.getMessage());
			err = errJson.getString("result");
		} catch (Exception e) {
			err = exception.getMessage();
		}
		return new NordigenException(err);
	}

	static void deleteAgreement(NordigenAccessToken token, NordigenAgreement agreement) {
		NordigenAPI.deleteEndUserAgreement(token.getAccess(), agreement != null ? agreement.getId() : null);
	}

	static NordigenRequisition createRequisition(NordigenAccessToken token, NordigenAgreement agreement,
			String redirect) {
		return NordigenRequisitionUtils.createRequisition(token, agreement != null ? agreement.getId() : null,
				agreement != null ? agreement.getInstitutionId() : null, redirect);
	}
}
