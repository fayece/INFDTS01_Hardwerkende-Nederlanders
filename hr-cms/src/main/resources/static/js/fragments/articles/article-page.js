import { formatLocalTimes } from './time-formatter.js';
import { initCommentForm, initReplyInteractions } from './comment-manager.js';
import { initCommentLoadMore } from './comment-load-more.js';
import { renderMarkdown } from './render-markdown.js';

document.addEventListener("DOMContentLoaded", () => {
    formatLocalTimes();
    initCommentForm();
    initReplyInteractions();
    initCommentLoadMore();
    renderMarkdown();
});
