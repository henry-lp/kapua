/*******************************************************************************
 * Copyright (c) 2019 Eurotech and/or its affiliates and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *******************************************************************************/
package org.eclipse.kapua.sso.exception.jwt;

import org.eclipse.kapua.sso.exception.SsoErrorCodes;

public class SsoJwtProcessException extends SsoJwtException {

    /**
     * Constructor.
     *
     * @param cause     The original {@link Throwable}.
     * @param arguments The arguments associated with the {@link Exception}.
     */
    public SsoJwtProcessException(Throwable cause, Object... arguments) {
        super(SsoErrorCodes.JWT_PROCESS_ERROR, cause, arguments);
    }
}
