/*
 * GNU GENERAL PUBLIC LICENSE
 * Version 3, 29 June 2007
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * StashThisService - Summon this bot to archive URLs in an archive service.
 * Copyright (C) 2017  S.G. Skinner
 */

package org.sgs.stashbot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.AuthenticationMethod;
import net.dean.jraw.http.oauth.Credentials;
import net.dean.jraw.http.oauth.OAuthData;


@Service
public class AuthService {
    private static final Logger LOG = LoggerFactory.getLogger(AuthService.class);
    private Environment env;


    public boolean authenticate(RedditClient redditClient) {
        try {
            OAuthData oAuthData = redditClient
                    .getOAuthHelper()
                    .easyAuth(getCredentials());
            redditClient.authenticate(oAuthData);
        } catch (Exception e) {
            LOG.error("Could not authenticate: {}", e.getMessage());
            return false;
        }

        return redditClient.isAuthenticated();
    }


    private Credentials getCredentials() {
        return new Credentials(AuthenticationMethod.SCRIPT,
                env.getRequiredProperty("reddit.username"),
                env.getRequiredProperty("reddit.password"),
                env.getRequiredProperty("reddit.clientId"),
                env.getRequiredProperty("reddit.clientSecret"),
                null, //deviceId, not used by us
                env.getRequiredProperty("reddit.redirectUrl"));
    }


    @Autowired
    public void setEnv(Environment env) {
        this.env = env;
    }

}
