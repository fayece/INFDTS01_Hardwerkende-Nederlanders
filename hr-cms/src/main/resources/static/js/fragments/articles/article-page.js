import { formatLocalTimes } from './time-formatter.js';
import { initCommentForm, initReplyInteractions, initDeleteInteractions } from './comment-manager.js';
import { initCommentLoadMore } from './comment-load-more.js';
import { renderMarkdown } from './render-markdown.js';

document.addEventListener("DOMContentLoaded", () => {
    formatLocalTimes();
    initCommentForm();
    initReplyInteractions();
    initDeleteInteractions();
    initCommentLoadMore();
    renderMarkdown();
});
