package com.code.aon.csb.fd0.model.MOD190.checks;

import java.util.ArrayList;

import com.code.aon.csb.fd0.model.Fd0Exception;
import com.code.aon.csb.fd0.model.MOD190.data.Receiver;

/**
 * Checks the Receiver objects data
 * 
 * @author Consulting & Development. Iñigo GAyarre - 08/02/2007
 * @since 1.0
 * 
 */
public class CheckReceiver extends Check {

	/**
	 * Parses data
	 * 
	 * @param withHolder
	 *            the object to parse
	 * @param exceptions
	 *            errors founds
	 * @return true if no errors
	 */
	public static boolean parse(Receiver receiver, ArrayList<Exception> exceptions) {
		boolean status = true;
		if (receiver.getCode() == null) {
			exceptions.add(new Fd0Exception(getMessage("ERROR_RECEIVER_1"),
					receiver.toString()));
			status = false;
		}
		if (receiver.getName() == null) {
			exceptions.add(new Fd0Exception(getMessage("ERROR_RECEIVER_2"),
					receiver.toString()));
			status = false;
		}
		if (receiver.getProvince() == null) {
			exceptions.add(new Fd0Exception(getMessage("ERROR_RECEIVER_3"),
					receiver.toString()));
			status = false;
		}
		if (receiver.getKey() == null) {
			exceptions.add(new Fd0Exception(getMessage("ERROR_RECEIVER_4"),
					receiver.toString()));
			status = false;
		} else {
			if (!receiver.getKey().equals("A")
					&& !receiver.getKey().equals("B")
					&& !receiver.getKey().equals("C")
					&& !receiver.getKey().equals("E")
					&& !receiver.getKey().equals("F")
					&& !receiver.getKey().equals("G")
					&& !receiver.getKey().equals("H")
					&& !receiver.getKey().equals("I")
					&& !receiver.getKey().equals("J")
					&& !receiver.getKey().equals("K")
					&& !receiver.getKey().equals("L")
					&& !receiver.getKey().equals("M")) {
				exceptions.add(new Fd0Exception(getMessage("ERROR_RECEIVER_5"),
						receiver.toString()));
				status = false;
			} else {
				if (receiver.getKey().equals("A")
						|| receiver.getKey().equals("C")
						|| receiver.getKey().equals("D")
						|| receiver.getKey().equals("E")
						|| receiver.getKey().equals("J")
						|| receiver.getKey().equals("K")
						|| receiver.getKey().equals("M")) {
					if (receiver.getSubkey() != null) {
						exceptions.add(new Fd0Exception(
								getMessage("ERROR_RECEIVER_6"), receiver
										.toString()));
						status = false;
					}
				} else if (receiver.getKey().equals("B")) {
					if (receiver.getSubkey() == null
							|| (receiver.getSubkey().intValue() != 1
									&& receiver.getSubkey().intValue() != 2 && receiver
									.getSubkey().intValue() != 3)) {
						exceptions.add(new Fd0Exception(
								getMessage("ERROR_RECEIVER_7"), receiver
										.toString()));
						status = false;
					}
				} else if (receiver.getKey().equals("F")) {
					if (receiver.getSubkey() == null
							|| (receiver.getSubkey().intValue() != 1 && receiver
									.getSubkey().intValue() != 2)) {
						exceptions.add(new Fd0Exception(
								getMessage("ERROR_RECEIVER_8"), receiver
										.toString()));
						status = false;
					}
				} else if (receiver.getKey().equals("G")) {
					if (receiver.getSubkey() == null
							|| (receiver.getSubkey().intValue() != 1
									&& receiver.getSubkey().intValue() != 2 && receiver
									.getSubkey().intValue() != 3)) {
						exceptions.add(new Fd0Exception(
								getMessage("ERROR_RECEIVER_9"), receiver
										.toString()));
						status = false;
					}
				} else if (receiver.getKey().equals("H")) {
					if (receiver.getSubkey() == null
							|| (receiver.getSubkey().intValue() != 1
									&& receiver.getSubkey().intValue() != 2 && receiver
									.getSubkey().intValue() != 3)) {
						exceptions.add(new Fd0Exception(
								getMessage("ERROR_RECEIVER_10"), receiver
										.toString()));
						status = false;
					}
				} else if (receiver.getKey().equals("I")) {
					if (receiver.getSubkey() == null
							|| (receiver.getSubkey().intValue() != 1 && receiver
									.getSubkey().intValue() != 2)) {
						exceptions.add(new Fd0Exception(
								getMessage("ERROR_RECEIVER_11"), receiver
										.toString()));
						status = false;
					}
				} else if (receiver.getKey().equals("L")) {
					if (receiver.getSubkey() == null
							|| (receiver.getSubkey().intValue() < 1 || receiver
									.getSubkey().intValue() > 18)) {
						exceptions.add(new Fd0Exception(
								getMessage("ERROR_RECEIVER_12"), receiver
										.toString()));
						status = false;
					}
				}
			}
		}
		if (receiver.getCeuMel() != null) {
			if (receiver.getCeuMel().intValue() != 0
					&& receiver.getCeuMel().intValue() != 1) {
				exceptions.add(new Fd0Exception(
						getMessage("ERROR_RECEIVER_13"), receiver.toString()));
				status = false;
			}
		}
		if (hasAditionalData(receiver) && !hasAditionalDataFilled(receiver)) {
			exceptions.add(new Fd0Exception(getMessage("ERROR_RECEIVER_14"),
					receiver.toString()));
			status = false;
		}
		if (!hasAditionalData(receiver) && hasAditionalDataFilled(receiver)) {
			exceptions.add(new Fd0Exception(getMessage("ERROR_RECEIVER_15"),
					receiver.toString()));
			status = false;
		}
		if (receiver.getDiscapacity() != null) {
			if (receiver.getDiscapacity().intValue() != 0
					&& receiver.getDiscapacity().intValue() != 1
					&& receiver.getDiscapacity().intValue() != 2
					&& receiver.getDiscapacity().intValue() != 3) {
				exceptions.add(new Fd0Exception(
						getMessage("ERROR_RECEIVER_23"), receiver.toString()));
				status = false;
			}
		}
		checkImports(receiver, exceptions);
		return status;
	}

	/**
	 * If this needs aditional data
	 * 
	 * @param receiver
	 *            the receiver
	 * @return true if yes
	 */
	private static boolean hasAditionalData(Receiver receiver) {
		if (receiver.getKey().equals("A")) {
			return true;
		} else if (receiver.getKey().equals("B")
				&& (receiver.getSubkey().intValue() == 1 || receiver
						.getSubkey().intValue() == 2)) {
			return true;
		} else if (receiver.getKey().equals("C")) {
			return true;
		} else if (receiver.getKey().equals("D")) {
			return true;
		} else if (receiver.getKey().equals("M")) {
			return true;
		}
		return false;
	}

	/**
	 * If this has aditional data filled
	 * 
	 * @param receiver
	 *            the receiver
	 * @return true if yes
	 */
	private static boolean hasAditionalDataFilled(Receiver receiver) {
		if (receiver.getBornYear() != null) {
			return true;
		} else if (receiver.getFamilyStatus() != null) {
			return true;
		} else if (receiver.getMarriageCode() != null) {
			return true;
		} else if (receiver.getDiscapacity() != null) {
			return true;
		} else if (receiver.getRelation() != null) {
			return true;
		} else if (receiver.getLaboralExtension() != null) {
			return true;
		} else if (receiver.getGeoMovility() != null) {
			return true;
		} else if (receiver.getReductions() != null) {
			return true;
		} else if (receiver.getCosts() != null) {
			return true;
		} else if (receiver.getPension() != null) {
			return true;
		} else if (receiver.getFood() != null) {
			return true;
		} else if (receiver.getChildren1() != null) {
			return true;
		} else if (receiver.getChildren2() != null) {
			return true;
		} else if (receiver.getChildren3() != null) {
			return true;
		} else if (receiver.getChildren4() != null) {
			return true;
		} else if (receiver.getChildHandicapped1() != null) {
			return true;
		} else if (receiver.getChildHandicapped2() != null) {
			return true;
		} else if (receiver.getChildHandicapped3() != null) {
			return true;
		} else if (receiver.getChildHandicapped4() != null) {
			return true;
		} else if (receiver.getChildHandicapped5() != null) {
			return true;
		} else if (receiver.getChildHandicapped6() != null) {
			return true;
		} else if (receiver.getParents1() != null) {
			return true;
		} else if (receiver.getParents2() != null) {
			return true;
		} else if (receiver.getParents3() != null) {
			return true;
		} else if (receiver.getParents4() != null) {
			return true;
		} else if (receiver.getParentsHandicapped1() != null) {
			return true;
		} else if (receiver.getParentsHandicapped2() != null) {
			return true;
		} else if (receiver.getParentsHandicapped3() != null) {
			return true;
		} else if (receiver.getParentsHandicapped4() != null) {
			return true;
		} else if (receiver.getParentsHandicapped5() != null) {
			return true;
		} else if (receiver.getParentsHandicapped6() != null) {
			return true;
		}
		return false;
	}

	/**
	 * Checks imports
	 * 
	 * @param receiver
	 *            the receiver
	 * 
	 */
	private static boolean checkImports(Receiver receiver, ArrayList<Exception> exceptions) {

		boolean status = true;

		double percepDin = receiver.getReceibedMoney() == null ? 0d : receiver
				.getReceibedMoney().doubleValue();
		double retencDin = receiver.getWithholdedMoney() == null ? 0d
				: receiver.getWithholdedMoney().doubleValue();
		double percepEsp = receiver.getReceibedSpice() == null ? 0d : receiver
				.getReceibedSpice().doubleValue();
		double ingCtaEfe = receiver.getPayEfect() == null ? 0d : receiver
				.getPayEfect().doubleValue();
		double ingCtaRep = receiver.getPayReperc() == null ? 0d : receiver
				.getPayReperc().doubleValue();
		double impNoIRPF = receiver.getReductions() == null ? 0d : receiver
				.getReductions().doubleValue();
		double impGastos = receiver.getCosts() == null ? 0d : receiver
				.getCosts().doubleValue();

		if (percepDin < 0 && retencDin != 0) {
			exceptions.add(new Fd0Exception(getMessage("ERROR_RECEIVER_16"),
					receiver.toString()));
			status = false;
		}
		if (percepDin >= 0 && percepDin < retencDin) {
			exceptions.add(new Fd0Exception(getMessage("ERROR_RECEIVER_17"),
					receiver.toString()));
			status = false;
		}
		if (percepEsp < 0 && ingCtaEfe != 0) {
			exceptions.add(new Fd0Exception(getMessage("ERROR_RECEIVER_18"),
					receiver.toString()));
			status = false;
		}
		if (percepEsp >= 0 && percepEsp < ingCtaEfe) {
			exceptions.add(new Fd0Exception(getMessage("ERROR_RECEIVER_19"),
					receiver.toString()));
			status = false;
		}
		if (ingCtaRep > ingCtaEfe) {
			exceptions.add(new Fd0Exception(getMessage("ERROR_RECEIVER_20"),
					receiver.toString()));
			status = false;
		}
		if (impNoIRPF != 0
				&& !((areEqualDoubles(impNoIRPF,
						(percepDin + percepEsp) * 25 / 100))
						|| (areEqualDoubles(impNoIRPF,
								(percepDin + percepEsp) * 30 / 100))
						|| (areEqualDoubles(impNoIRPF,
								(percepDin + percepEsp) * 40 / 100)) || (areEqualDoubles(
						impNoIRPF, (percepDin + percepEsp) * 50 / 100)))) {
			exceptions.add(new Fd0Exception(getMessage("ERROR_RECEIVER_21"),
					receiver.toString()));
			status = false;
		}
		if (impGastos > (percepDin + percepEsp)) {
			exceptions.add(new Fd0Exception(getMessage("ERROR_RECEIVER_22"),
					receiver.toString()));
			status = false;
		}
		return status;
	}

	private static boolean areEqualDoubles(double param1, double param2) {
		if (safeDouble(param1) == safeDouble(param2)) {
			return true;
		} else {
			return false;
		}
	}

	private static double safeDouble(double param) {
		double i = Math.round(param * 100);
		i = i / 100;
		return i;
	}

}
