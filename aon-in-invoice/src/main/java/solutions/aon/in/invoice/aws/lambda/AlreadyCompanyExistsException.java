package solutions.aon.in.invoice.aws.lambda;

class AlreadyCompanyExistsException extends IllegalArgumentException {

    public AlreadyCompanyExistsException() {
        super();
    }

    public AlreadyCompanyExistsException(String message) {
        super(message);
    }
}