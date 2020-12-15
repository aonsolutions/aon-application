package solutions.aon.seg.social.exceptions.invalidData;

import solutions.aon.seg.social.exceptions.SegSocialException;

public class InvalidDataException extends SegSocialException{

	public InvalidDataException(){}
	public InvalidDataException(String msg){super(msg);}

	public static void checkCode(Integer statusCode, String msg) throws SegSocialException {
		switch (statusCode) {
		case 221:
		case 2006:
		case 3083:
		case 2148:
		case 3252:
		case 3659:
		case 3408:
		case 9125:
		case 9086:
		case 232:		break;
		case 4860:		throw new NotAllFilledException(msg);
		case 3030:
		case 3260:		throw new WrongRegimeException(msg);
		case 3066:		throw new SyntaxException(msg);
		case 3820:		throw new WrongAffNumber(msg);
		case 3462:		throw new NotAllowedContributionAccount(msg);
		case 4879:		throw new InvalidPrintingMethod(msg);
		case 3001:		throw new UnfilledMandatory(msg);
		case 3543:		throw new NoQueryData(msg);
		case 3823:		throw new invalidCccException(msg);
		case 3037:		throw new NoMoreDataException(msg);
		case 4113:		throw new DataDoesNotExist(msg);
		case 3053:		throw new InvalidDateException(msg);
		case 7:			throw new WrongValueException(msg);
		case 2204:		throw new WrongIdentifierException(msg);
		case 3010:      throw new EmployeeNotRegisteredException("El empleado no está en situación de alta");
		default:		throw new InvalidDataException(msg);
		}
	}
}
