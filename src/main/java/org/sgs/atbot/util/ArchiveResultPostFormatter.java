package org.sgs.atbot.util;

import org.sgs.atbot.model.ArchiveResult;
import org.sgs.atbot.model.AtbotUrl;

public class ArchiveResultPostFormatter {
    private static final String FOOTER = "^[FAQ](https://np.reddit.com/r/ArchiveThisBot/wiki/index)&nbsp;| ^[Source&nbsp;Code](https://github.com/sgskinner/ArchiveThisBot)&nbsp;| ^[PM&nbsp;Developer](https://www.reddit.com/message/compose?to=sgskinner&subject=ArchiveThisBot)&nbsp;| ^v0.1.1";
    private static final String LINK_LINE_SUCCESS = "1. [Original](%s) --> [Archived](%s) (%s)";
    private static final String LINK_LINE_FAILURE = "1. [Original](%s) --> [Archive Failed!](%s)";
    private static final String FAILURE_HELP_NOTE = "(Please see the wiki [here](https://www.reddit.com/r/ArchiveThisBot/wiki/faq/failures) about archive failures, and/or reproduce the failure by clicking the 'Archive Failed!' link.)";
    private static final String LINE = "-----";


    public static String format(ArchiveResult archiveResult) {
        StringBuilder sb = new StringBuilder();
        sb.append("**Archived**:");
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator()); // reddit markdown needs 2 newlines to display one

        boolean atLeastOneFailed = false;
        for(AtbotUrl atbotUrl : archiveResult.getArchivedUrls()) {

            if (atbotUrl.isArchived()) {
                sb.append(String.format(LINK_LINE_SUCCESS, atbotUrl.getOriginalUrl(), atbotUrl.getArchivedUrl(), TimeUtils.formatGmt(atbotUrl.getLastArchived())));
            } else {
                // On failed AtbotUrl's, the archiveUrl is set to the save-request URL, which
                // the user can click and see for themselves why the archive failed
                sb.append(String.format(LINK_LINE_FAILURE, atbotUrl.getOriginalUrl(), atbotUrl.getArchivedUrl()));
                atLeastOneFailed = true;
            }

            sb.append(System.lineSeparator());
            sb.append(System.lineSeparator());
        }

        if (atLeastOneFailed) {
            sb.append(FAILURE_HELP_NOTE);
            sb.append(System.lineSeparator());
            sb.append(System.lineSeparator());
        }

        sb.append(LINE);
        sb.append(System.lineSeparator());
        sb.append(FOOTER);

        return sb.toString();
    }

}
