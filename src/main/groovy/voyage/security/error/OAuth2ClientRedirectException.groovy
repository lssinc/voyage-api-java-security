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
package voyage.security.error

import org.springframework.http.HttpStatus

/**
 * This exception will be thrown when a client provides an invalid redirect URL during an implicit authorization request.
 */
class OAuth2ClientRedirectException extends AppOAuth2Exception {
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST
    private static final String DEFAULT_MESSAGE = 'The client redirect URL was not valid. To resolve this issue please use a valid redirect url.'

    OAuth2ClientRedirectException() {
        this(DEFAULT_MESSAGE)
    }

    OAuth2ClientRedirectException(String message) {
        super(HTTP_STATUS, message)
    }
}