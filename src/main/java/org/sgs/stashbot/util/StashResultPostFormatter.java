package org.sgs.stashbot.util;

import org.sgs.stashbot.model.StashResult;
import org.sgs.stashbot.model.StashUrl;

public class StashResultPostFormatter {
    private static final String FOOTER = "^[FAQ](https://np.reddit.com/r/StashThis/wiki/index)&nbsp;| ^[Source&nbsp;Code](https://github.com/sgskinner/StashThisBot)&nbsp;| ^[PM&nbsp;Developer](https://www.reddit.com/message/compose?to=sgskinner&subject=StashThisBot)&nbsp;| ^v0.1.1";
    private static final String LINK_LINE_SUCCESS = "1. [Original](%s) --> [Stashed](%s) (%s)";
    private static final String LINK_LINE_FAILURE = "1. [Original](%s) --> [Stash Failed!](%s)";
    private static final String FAILURE_HELP_NOTE = "(Please see the wiki [here](https://www.reddit.com/r/StashThis/wiki/index#wiki_failures) about stash failures, and/or reproduce the failure by clicking the 'Stash Failed!' link.)";
    private static final String LINE = "-----";


    public static String format(StashResult stashResult) {
        StringBuilder sb = new StringBuilder();
        sb.append("**Stashed:**");
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator()); // reddit markdown needs 2 newlines to display one

        boolean atLeastOneFailed = false;
        for(StashUrl stashUrl : stashResult.getStashUrls()) {

            if (stashUrl.isStashed()) {
                sb.append(String.format(LINK_LINE_SUCCESS, stashUrl.getOriginalUrl(), stashUrl.getStashedUrl(), TimeUtils.formatGmt(stashUrl.getLastStashed())));
            } else {
                // On failed StashUrl's, the archiveUrl is set to the save-request URL, which
                // the user can click and see for themselves why the archive failed
                sb.append(String.format(LINK_LINE_FAILURE, stashUrl.getOriginalUrl(), stashUrl.getStashedUrl()));
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
