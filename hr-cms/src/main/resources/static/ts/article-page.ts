import { formatLocalTimes } from './time-formatter.js';
import { initReplyLoader } from './reply-handler.js';
import { initCommentLoadMore } from './comment-load-more.js';

document.addEventListener("DOMContentLoaded", () => {

    formatLocalTimes();
    initReplyLoader();
    initCommentLoadMore();
});
