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

package org.sgs.atbot.http;

public class UserAgentProps {

    private final String platform;
    private final String appId;
    private final String version;
    private final String redditUsername;


    public UserAgentProps(String platform, String appId, String version, String redditUsername) {
        this.platform = platform;
        this.appId = appId;
        this.version = version;
        this.redditUsername = redditUsername;
    }


    public String getPlatform() {
        return platform;
    }


    public String getAppId() {
        return appId;
    }


    public String getVersion() {
        return version;
    }


    public String getRedditUsername() {
        return redditUsername;
    }

}
