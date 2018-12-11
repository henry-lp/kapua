/*******************************************************************************
 * Copyright (c) 2017, 2019 Eurotech and/or its affiliates and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *******************************************************************************/
package org.eclipse.kapua.service.certificate.internal;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.commons.util.KapuaFileUtils;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.locator.KapuaProvider;
import org.eclipse.kapua.model.config.metatype.KapuaTad;
import org.eclipse.kapua.model.config.metatype.KapuaTicon;
import org.eclipse.kapua.model.config.metatype.KapuaTocd;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.certificate.CertificateDomains;
import org.eclipse.kapua.service.certificate.CertificateGenerator;
import org.eclipse.kapua.service.certificate.CertificateUsage;
import org.eclipse.kapua.service.certificate.KeyUsage;
import org.eclipse.kapua.service.certificate.KeyUsageSetting;
import org.eclipse.kapua.service.certificate.PrivateCertificate;
import org.eclipse.kapua.service.certificate.PrivateCertificateCreator;
import org.eclipse.kapua.service.certificate.PrivateCertificateFactory;
import org.eclipse.kapua.service.certificate.PrivateCertificateListResult;
import org.eclipse.kapua.service.certificate.PrivateCertificateService;
import org.eclipse.kapua.service.certificate.exception.KapuaCertificateErrorCodes;
import org.eclipse.kapua.service.certificate.exception.KapuaCertificateException;
import org.eclipse.kapua.service.certificate.internal.setting.KapuaCertificateSetting;
import org.eclipse.kapua.service.certificate.internal.setting.KapuaCertificateSettingKeys;
import org.eclipse.kapua.service.certificate.util.CertificateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

@KapuaProvider
public class PrivateCertificateServiceImpl implements PrivateCertificateService {

    private static final Logger LOG = LoggerFactory.getLogger(PrivateCertificateServiceImpl.class);

    private static final KapuaLocator LOCATOR = KapuaLocator.getInstance();

    private static final AuthorizationService AUTHORIZATION_SERVICE = LOCATOR.getService(AuthorizationService.class);
    private static final PermissionFactory PERMISSION_FACTORY = LOCATOR.getFactory(PermissionFactory.class);

    private static final PrivateCertificateFactory CERTIFICATE_FACTORY = LOCATOR.getFactory(PrivateCertificateFactory.class);

    private String certificate;
    private String privateKey;

    /**
     * Constructor
     */
    public PrivateCertificateServiceImpl() throws KapuaException {
        KapuaSecurityUtils.doPrivileged(() -> {
            KapuaCertificateSetting setting = KapuaCertificateSetting.getInstance();

            String privateKeyPath = setting.getString(KapuaCertificateSettingKeys.CERTIFICATE_JWT_PRIVATE_KEY);
            String certificatePath = setting.getString(KapuaCertificateSettingKeys.CERTIFICATE_JWT_CERTIFICATE);

            if (Strings.isNullOrEmpty(privateKeyPath) && Strings.isNullOrEmpty(certificatePath)) {
                LOG.error("No private key and certificate path specified.\nPlease set authentication.session.jwt.private.key and authentication.session.jwt.certificate system properties.");
                throw new KapuaCertificateException(KapuaCertificateErrorCodes.CERTIFICATE_ERROR);
            } else {
                certificate = CertificateUtils.readCertificateAsString(KapuaFileUtils.getAsFile(certificatePath));
                privateKey = CertificateUtils.readPrivateKeyAsString(KapuaFileUtils.getAsFile(privateKeyPath));
            }
        });
    }

    @Override
    public PrivateCertificate create(PrivateCertificateCreator creator) throws KapuaException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PrivateCertificate find(KapuaId scopeId, KapuaId entityId) throws KapuaException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PrivateCertificateListResult query(KapuaQuery<PrivateCertificate> query) throws KapuaException {
        //
        // Argument Validation
        ArgumentValidator.notNull(query, "query");

        //
        // Check Access
        AUTHORIZATION_SERVICE.checkPermission(PERMISSION_FACTORY.newPermission(CertificateDomains.CERTIFICATE_DOMAIN, Actions.write, query.getScopeId()));

        //
        // Create the default certificate
        CertificateUsage jwtCertificateUsage = new CertificateUsageImpl("JWT");
        Set<CertificateUsage> certificateUsages = Sets.newHashSet(jwtCertificateUsage);

        KeyUsageSetting keyUsageSetting = new KeyUsageSettingImpl();
        keyUsageSetting.setKeyUsage(KeyUsage.DIGITAL_SIGNATURE);
        keyUsageSetting.setAllowed(true);
        keyUsageSetting.setKapuaAllowed(true);

        KapuaCertificateSetting setting = KapuaCertificateSetting.getInstance();

        PrivateCertificate kapuaPrivateCertificate = new PrivateCertificateImpl(KapuaId.ONE);
        kapuaPrivateCertificate.setPrivateKey(privateKey);
        kapuaPrivateCertificate.setCertificate(certificate);
        kapuaPrivateCertificate.getKeyUsageSettings().add(keyUsageSetting);
        kapuaPrivateCertificate.setCertificateUsages(certificateUsages);
        kapuaPrivateCertificate.setPassword(setting.getString(KapuaCertificateSettingKeys.CERTIFICATE_JWT_PRIVATE_KEY_PASSWORD));

        PrivateCertificateListResult result = CERTIFICATE_FACTORY.newListResult();
        result.addItems(Lists.newArrayList(kapuaPrivateCertificate));

        return result;
    }

    @Override
    public long count(KapuaQuery<PrivateCertificate> query) {
        return 1L;
    }

    @Override
    public void delete(KapuaId scopeId, KapuaId entityId) throws KapuaException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PrivateCertificate findByName(String name) throws KapuaException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PrivateCertificate update(PrivateCertificate entity) throws KapuaException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PrivateCertificate generate(CertificateGenerator generator) throws KapuaException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<PrivateCertificate> findAncestorsCertificates(KapuaId scopeId, CertificateUsage usage) {
        throw new UnsupportedOperationException();
    }

    @Override
    public KapuaTocd getConfigMetadata(KapuaId scopeId) throws KapuaException {
        return EmptyTocd.getInstance();
    }

    @Override
    public Map<String, Object> getConfigValues(KapuaId scopeId) throws KapuaException {
        return Collections.emptyMap();
    }

    @Override
    public void setConfigValues(KapuaId scopeId, KapuaId parentId, Map<String, Object> values) throws KapuaException {
        throw new UnsupportedOperationException();
    }

    public static class EmptyTocd implements KapuaTocd {

        private static final EmptyTocd INSTANCE = new EmptyTocd();

        public static EmptyTocd getInstance() {
            return INSTANCE;
        }

        private EmptyTocd() {
        }

        @Override
        public void setOtherAttributes(Map<QName, String> otherAttributes) {
            // This is a Empty TOCD implementation
        }

        @Override
        public void setName(String value) {
            // This is a Empty TOCD implementation
        }

        @Override
        public void setId(String value) {
            // This is a Empty TOCD implementation
        }

        @Override
        public void setIcon(List<? extends KapuaTicon> icon) {
            // This is a Empty TOCD implementation
        }

        @Override
        public void setDescription(String value) {
            // This is a Empty TOCD implementation
        }

        @Override
        public void setAny(List<Object> any) {
            // This is a Empty TOCD implementation
        }

        @Override
        public void setAD(List<? extends KapuaTad> icon) {
            // This is a Empty TOCD implementation
        }

        @Override
        public Map<QName, String> getOtherAttributes() {
            return Collections.emptyMap();
        }

        @Override
        public String getName() {
            return PrivateCertificateService.class.getSimpleName();
        }

        @Override
        public String getId() {
            return PrivateCertificateService.class.getName();
        }

        @Override
        public List<KapuaTicon> getIcon() {
            return Collections.emptyList();
        }

        @Override
        public String getDescription() {
            return null;
        }

        @Override
        public List<Object> getAny() {
            return Collections.emptyList();
        }

        @Override
        public List<KapuaTad> getAD() {
            return Collections.emptyList();
        }
    }
}
