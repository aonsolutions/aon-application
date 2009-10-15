package com.code.aon.finance.invoicing;

import com.code.aon.common.AonException;

public class InvoicingException extends AonException {
	private static final long serialVersionUID = -5386401441718903779L;

    /* (non-Javadoc)
     * @see com.code.aon.common.InvoicingException#InvoicingException()
     */
    public InvoicingException() {
        super();
    }

    /* (non-Javadoc)
     * @see com.code.aon.common.InvoicingException#InvoicingException(java.lang.String)
     */
    public InvoicingException(String message) {
        super(message);
    }

    /* (non-Javadoc)
     * @see com.code.aon.common.InvoicingException#InvoicingException(java.lang.Throwable)
     */
    public InvoicingException(Throwable cause) {
        super(cause);
    }

    /* (non-Javadoc)
     * @see com.code.aon.common.InvoicingException#InvoicingException(java.lang.String, java.lang.Throwable)
     */
    public InvoicingException(String message, Throwable cause) {
        super(message, cause);
    }

}
