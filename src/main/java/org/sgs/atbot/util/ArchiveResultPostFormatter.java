package org.sgs.atbot.util;

import org.sgs.atbot.model.ArchiveResult;
import org.sgs.atbot.model.AtbotUrl;

public class ArchiveResultPostFormatter {
    private static final String FOOTER = "^[FAQ](https://np.reddit.com/r/ArchiveThisBot/wiki/index)&nbsp;| ^[Source&nbsp;Code](https://github.com/sgskinner/ArchiveThisBot)&nbsp;| ^[PM&nbsp;Developer](https://www.reddit.com/message/compose?to=sgskinner&subject=ArchiveThisBot)&nbsp;| ^v0.1.1";
    private static final String LINK_LINE = "1. [Original](%s) --> [Archived](%s) (%s)";
    private static final String LINE = "-----";


    public static String format(ArchiveResult archiveResult) {
        StringBuilder sb = new StringBuilder();
        sb.append("**Archived**:");
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator()); // reddit markdown needs 2 newlines to display one

        for(AtbotUrl atbotUrl : archiveResult.getArchivedUrls()) {
            if (!atbotUrl.isArchived()) {
                continue;
            }

            sb.append(String.format(LINK_LINE, atbotUrl.getOriginalUrl(), atbotUrl.getArchivedUrl(), TimeUtils.formatGmt(atbotUrl.getLastArchived())));
            sb.append(System.lineSeparator());
            sb.append(System.lineSeparator());
        }
        sb.append(LINE);
        sb.append(System.lineSeparator());
        sb.append(FOOTER);

        return sb.toString();
    }

}
