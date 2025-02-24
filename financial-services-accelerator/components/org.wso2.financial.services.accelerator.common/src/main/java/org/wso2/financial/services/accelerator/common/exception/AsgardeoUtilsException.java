package org.wso2.financial.services.accelerator.common.exception;

/**
 * AsgardeoUtilsException.
 */
public class AsgardeoUtilsException extends  Exception {

    public AsgardeoUtilsException(String message) {
        super(message);
    }

    public AsgardeoUtilsException(String message, Throwable e) {
        super(message, e);
    }
}
