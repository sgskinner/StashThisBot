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
 * ArchiveThisBot - Summon this bot to archive Reddit URLs in archive.is
 * Copyright (C) 2017  S.G. Skinner
 */

package org.sgs.atbot.service.impl;

import org.sgs.atbot.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.oauth.Credentials;
import net.dean.jraw.http.oauth.OAuthData;
import net.dean.jraw.http.oauth.OAuthException;

@Component
public class OauthServiceImpl implements AuthService {

    private final Credentials credentials;


    @Autowired
    public OauthServiceImpl(Credentials credentials) {
        this.credentials = credentials;
    }


    @Override
    public boolean authenticate(RedditClient redditClient) {
        OAuthData oAuthData;
        try {
            oAuthData = redditClient.getOAuthHelper().easyAuth(getCredentials());
        } catch (OAuthException e) {
            throw new RuntimeException(e);
        }

        redditClient.authenticate(oAuthData);

        return isAuthenticated(redditClient);
    }


    @Override
    public boolean isAuthenticated(RedditClient redditClient) {
        return redditClient.isAuthenticated();
    }


    private Credentials getCredentials() {
        return credentials;
    }

}
