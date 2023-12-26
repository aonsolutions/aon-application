package solutions.aon.in.invoice.aws.lambda;

class NoSuchLoadBatchException extends Exception {

    public NoSuchLoadBatchException(String message) {
        super(message);
    }
}