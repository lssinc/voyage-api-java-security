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
package voyage.security.client

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface ClientRepository  extends CrudRepository<Client, Long> {

    @Query('FROM Client c WHERE c.clientIdentifier = ?1 AND c.isDeleted = false')
    Client findByClientIdentifier(String clientIdentifier)

}
