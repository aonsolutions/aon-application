package solutions.aon.in.invoice.aws.lambda;

class NoSuchCompanyException extends IllegalArgumentException {

    public NoSuchCompanyException(String message) {
        super(message);
    }
}