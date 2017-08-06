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
 * StashThisBot - Summon this bot to stash URLs in an archive service.
 * Copyright (C) 2017  S.G. Skinner
 */

package org.sgs.stashbot;

import org.junit.Assert;
import org.junit.Test;
import org.sgs.stashbot.spring.SpringContext;

import net.dean.jraw.http.UserAgent;


public class AppBootstrapTest {

    @Test
    public void testApp() {
        // Spring config smoke test; if pulling the bean from the context blows up, we fail; otherwise pass
        StashThisBot bot = SpringContext.getBean(StashThisBot.class);
        Assert.assertNotNull("Could not instantiate bot!", bot);
    }

    @Test
    public void testOauth() {
        StashThisBot bot = SpringContext.getBean(StashThisBot.class);
        bot.performAuth();
        Assert.assertTrue("Authentication has failed!", bot.isAuthenticated());
    }

    @Test
    public void testUserAgent() {
        UserAgent userAgent = SpringContext.getBean(UserAgent.class);
        Assert.assertNotNull("Could not get bean from context!", userAgent);
        Assert.assertEquals("Val not properly set by Spring!", "desktop:org.sgs.stashbot:0.1.1 (by /u/ArchiveThisBot)", userAgent.toString());
    }

}
