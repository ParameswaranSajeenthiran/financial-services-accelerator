package org.wso2.financial.services.accelerator.common.exception;

/**
 * OAuth2ServiceException.
 */
public class OAuth2ServiceException extends Exception {

    public OAuth2ServiceException(String message) {
        super(message);
    }

    public OAuth2ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
