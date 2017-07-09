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
 * Copyright (C) 2016  S.G. Skinner
 */

package org.sgs.archivethisbot;

import org.junit.Assert;
import org.junit.Test;
import org.sgs.atbot.ArchiveThisBot;
import org.sgs.atbot.service.RedditService;
import org.sgs.atbot.spring.SpringContext;


public class AppBootstrapTest {

    @Test
    public void testApp() {
        // Spring config smoke test; if new'ing up blows up, we fail, otherwise pass
        new ArchiveThisBot();
        Assert.assertTrue(true);
    }

    @Test
    public void testSpringContext() {
        RedditService service = SpringContext.getService(RedditService.class);
        Assert.assertTrue(service != null);
    }
}
