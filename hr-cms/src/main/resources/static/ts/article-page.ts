import { formatLocalTimes } from './time-formatter.js';
import { initReplyLoader } from './reply-handler.js';
import { initCommentLoadMore } from './comment-load-more.js';
import { initCommentForm } from './comment-form-handler.js';

document.addEventListener("DOMContentLoaded", () => {

    formatLocalTimes();
    initReplyLoader();
    initCommentLoadMore();
    initCommentForm();
});
