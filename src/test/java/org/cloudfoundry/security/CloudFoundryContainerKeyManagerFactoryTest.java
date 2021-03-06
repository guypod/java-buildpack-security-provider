/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cloudfoundry.security;

import org.junit.Test;

import javax.net.ssl.KeyManager;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import static org.assertj.core.api.Assertions.assertThat;

public final class CloudFoundryContainerKeyManagerFactoryTest {

    @Test
    public void customKeyManager() throws NoSuchProviderException, NoSuchAlgorithmException, CertificateException, KeyStoreException, IOException, UnrecoverableKeyException {
        CloudFoundryContainerKeyManagerFactory.SunX509 factory = new CloudFoundryContainerKeyManagerFactory.SunX509(
            Paths.get("src/test/resources/client-certificates-1.pem"),
            Paths.get("src/test/resources/client-private-key-1.pem"));
        factory.engineInit(getKeyStore(), new char[0]);

        KeyManager keyManager = factory.engineGetKeyManagers()[0];
        assertThat(keyManager).isInstanceOf(DelegatingX509ExtendedKeyManager.class);
        assertThat(((DelegatingX509ExtendedKeyManager) keyManager).size()).isEqualTo(2);
    }

    @Test
    public void defaultKeyManagerForNullCertificatesLocation() throws NoSuchProviderException, NoSuchAlgorithmException, CertificateException, KeyStoreException, IOException,
        UnrecoverableKeyException {
        CloudFoundryContainerKeyManagerFactory.SunX509 factory = new CloudFoundryContainerKeyManagerFactory.SunX509(
            null,
            Paths.get("src/test/resources/client-private-key-1.pem"));
        factory.engineInit(getKeyStore(), new char[0]);


        KeyManager keyManager = factory.engineGetKeyManagers()[0];
        assertThat(keyManager).isInstanceOf(DelegatingX509ExtendedKeyManager.class);
        assertThat(((DelegatingX509ExtendedKeyManager) keyManager).size()).isEqualTo(1);
    }

    @Test
    public void defaultKeyManagerForNullPrivateKeyLocation() throws NoSuchProviderException, NoSuchAlgorithmException, CertificateException, KeyStoreException, IOException, UnrecoverableKeyException {
        CloudFoundryContainerKeyManagerFactory.SunX509 factory = new CloudFoundryContainerKeyManagerFactory.SunX509(
            Paths.get("src/test/resources/client-certificates-1.pem"),
            null);
        factory.engineInit(getKeyStore(), new char[0]);


        KeyManager keyManager = factory.engineGetKeyManagers()[0];
        assertThat(keyManager).isInstanceOf(DelegatingX509ExtendedKeyManager.class);
        assertThat(((DelegatingX509ExtendedKeyManager) keyManager).size()).isEqualTo(1);
    }

    private KeyStore getKeyStore() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null);
        return keyStore;
    }

}