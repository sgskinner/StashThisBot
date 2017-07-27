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

package org.sgs.atbot.service.impl;

import java.util.Calendar;
import java.util.List;

import org.sgs.atbot.model.ArchiveResult;
import org.sgs.atbot.model.AtbotUrl;
import org.sgs.atbot.service.ArchiveService;
import org.springframework.stereotype.Service;

import net.dean.jraw.models.CommentNode;

@Service
public class ArchiveIsServiceImpl implements ArchiveService {

    @Override
    public ArchiveResult archiveUrls(CommentNode parentCommentNode, CommentNode summoningNode, List<String> extractedUrls) {
        ArchiveResult request = new ArchiveResult(parentCommentNode, summoningNode, extractedUrls);
        for (AtbotUrl atbotUrl : request.getUrlsToArchive()) {
            //TODO: implement archive service here
            atbotUrl.setArchivedUrl("sgs: MOCKED; " + Calendar.getInstance().getTimeInMillis());
            request.setServicedDate(Calendar.getInstance().getTime());
        }

        return request;
    }
}
