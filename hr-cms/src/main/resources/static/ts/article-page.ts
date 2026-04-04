import { formatLocalTimes } from './time-formatter.js';
import { initReplyLoader } from './reply-handler.js';

document.addEventListener("DOMContentLoaded", () => {

    formatLocalTimes();
    initReplyLoader();
});
