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
package voyage.security.verify

import groovy.time.TimeCategory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.validation.annotation.Validated
import voyage.core.sms.AwsSmsService
import voyage.core.sms.SmsMessage
import voyage.security.crypto.CryptoService
import voyage.security.user.PhoneType
import voyage.security.user.User
import voyage.security.user.UserPhone
import voyage.security.user.UserService

import javax.validation.constraints.NotNull

@Service
@Validated
class VerifyService {
    @Value('${app.name}')
    private String appName

    private final UserService userService
    private final CryptoService cryptoService
    private final AwsSmsService smsService
    private final VerifyProperties verifyProperties

    @Autowired
    VerifyService(UserService userService, CryptoService cryptoService, AwsSmsService smsService, VerifyProperties verificationProperties) {
        this.userService = userService
        this.cryptoService = cryptoService
        this.smsService = smsService
        this.verifyProperties = verificationProperties
    }

    boolean verifyCurrentUser(@NotNull String code) {
        User user = userService.currentUser

        UserPhone userPhone = user.phones?.find { phone ->
            cryptoService.hashMatches(code, phone.verifyCode)
        }

        if (!userPhone) {
            throw new InvalidVerificationCodeException()
        }

        if (userPhone.verifyCodeExpired) {
            throw new VerifyCodeExpiredException()
        }

        userPhone.isValidated = true
        userPhone.verifyCode = null
        userPhone.verifyCodeExpiresOn = null
        user.isVerifyRequired = false

        userService.saveDetached(user)

        return true
    }

    void sendVerifyCodeToCurrentUser() {
        User user = userService.currentUser

        Iterable<UserPhone> mobilePhones = user.phones?.findAll {
            it.phoneType == PhoneType.MOBILE
        }

        if (!mobilePhones) {
            throw new InvalidVerificationPhoneNumberException()
        }

        user.isVerifyRequired = true

        // Limit the number of phones that can receive a verification code to 5. This prevents an attacker from overloading
        // the list of phone numbers for a user and spamming an infinite number of phones with security codes.
        int count = (mobilePhones.size() > 5 ? 5 : mobilePhones.size())
        for (int i=0; i < count; i++) {
            sendVerifyCodeToPhoneNumber(mobilePhones[i])
        }

        userService.saveDetached(user)
    }

    private sendVerifyCodeToPhoneNumber(@NotNull UserPhone mobilePhone) {
        String verifyCode = SecurityCode.userVerifyCode
        mobilePhone.verifyCode = cryptoService.hashEncode(verifyCode)
        use(TimeCategory) {
            mobilePhone.verifyCodeExpiresOn = new Date() + verifyProperties.verifyCodeExpiresMinutes.minutes
        }

        SmsMessage smsMessage = new SmsMessage()
        smsMessage.to = mobilePhone.phoneNumber
        smsMessage.text = "Your ${appName} verification code is: ${verifyCode}"
        smsService.send(smsMessage)
    }
}
