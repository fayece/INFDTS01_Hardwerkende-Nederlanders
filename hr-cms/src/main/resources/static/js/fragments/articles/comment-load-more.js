import { formatLocalTimes } from "./time-formatter.js";
import { renderMarkdown } from "./render-markdown.js";

async function loadMore(btn) {
    const articleId = btn.dataset.articleId;
    const offset = btn.dataset.offset;

    const response = await fetch(`/comment/article/${articleId}?offset=${offset}`);
    const html = await response.text();
    const doc = new DOMParser().parseFromString(html, "text/html");

    const newComments = doc.querySelectorAll(".comment-section--list li");
    const list = document.querySelector(".comment-section--list");
    newComments.forEach(comment => list.appendChild(comment));
    formatLocalTimes();
    renderMarkdown();

    const nextButton = doc.getElementById("load-more-comments");
    if (nextButton instanceof HTMLButtonElement) btn.dataset.offset = nextButton.dataset.offset;
    else btn.remove();
}

export function initCommentLoadMore() {
    const button = document.getElementById("load-more-comments");
    if (button instanceof HTMLButtonElement) {
        button.addEventListener("click", () => loadMore(button));
    }
}
