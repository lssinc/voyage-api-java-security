/*
 * Copyright (c) 2017 Lighthouse Software, Inc.   http://www.LighthouseSoftware.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package voyage.security.crypto

import org.springframework.beans.factory.annotation.Autowired
import voyage.security.AuthenticatedIntegrationTest

import java.security.KeyPair

class KeyStoreServiceIntegrationSpec extends AuthenticatedIntegrationTest {
    @Autowired
    KeyStoreService keyStoreService

    def 'getRsaKeyPair returns a KeyPair from the keystore'() {
        when:
            KeyPair keyPair = keyStoreService.getRsaKeyPair('asymmetric', 'changeme'.toCharArray())

        then:
            keyPair.public
            keyPair.private
    }

    def 'KeyStoreService throws exception when the keystore cannot be loaded'() {
        given:
            CryptoProperties properties = new CryptoProperties()
            properties.keyStoreFileName = 'keystore.jks'
            properties.keyStorePassword = 'wrong-password'

        when:
            KeyStoreService service = new KeyStoreService(properties)

        then:
            !service
            IOException ex = thrown()
            ex != null
    }
}
