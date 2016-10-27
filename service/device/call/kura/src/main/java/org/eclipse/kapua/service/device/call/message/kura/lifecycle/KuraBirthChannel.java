/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.kapua.service.device.call.message.kura.lifecycle;

import org.eclipse.kapua.service.device.call.message.kura.KuraChannel;
import org.eclipse.kapua.service.device.call.message.kura.setting.DeviceCallSetting;
import org.eclipse.kapua.service.device.call.message.kura.setting.DeviceCallSettingKeys;

/**
 * Kura device birth message channel implementation.
 * 
 * @since 1.0
 *
 */
public class KuraBirthChannel extends KuraChannel
{
    protected static final String DESTINATION_CONTROL_PREFIX = DeviceCallSetting.getInstance().getString(DeviceCallSettingKeys.DESTINATION_MESSAGE_CLASSIFIER);

    /**
     * Constructor
     */
    public KuraBirthChannel()
    {
    }

    /**
     * Constructor
     * 
     * @param scopeNamespace
     * @param clientId
     */
    public KuraBirthChannel(String scopeNamespace, String clientId)
    {
        this(null, scopeNamespace, clientId);
    }

    /**
     * Constructor
     * 
     * @param messageClassification
     * @param scopeNamespace
     * @param clientId
     */
    public KuraBirthChannel(String messageClassification, String scopeNamespace, String clientId)
    {
        this.messageClassification = messageClassification;
        this.scopeNamespace = scopeNamespace;
        this.clientId = clientId;
    }

}
