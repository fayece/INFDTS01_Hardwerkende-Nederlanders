import { formatLocalTimes } from './time-formatter.js';
import { renderMarkdown } from "./render-markdown.js";

export const initReplyForm = () => {
    document.addEventListener("click", async (event) => {
        const btn = event.target.closest(".reply-button");
        if (!btn) return;

        const commentId = btn.dataset.commentId;
        const articleId = btn.dataset.articleId;
        const container = document.getElementById(`replies-for-${commentId}`);

        if (container.querySelector(".reply-form-container")) return;

        const formHtml = `
            <div class="reply-form-container">
                <div id="reply-editor-${commentId}"></div>
                <div>
                    <button class="submit-reply" data-parent-id="${commentId}">Post Reply</button>
                    <button class="cancel-reply">Cancel</button>
                </div>
        </div>
        `;

        container.insertAdjacentHTML('afterbegin', formHtml);

        const editor = new window.toastui.Editor({
            el: document.getElementById(`reply-editor-${commentId}`),
            height: "auto",
            initialEditType: "wysiwyg",
            hideModeSwitch: true,
            placeholder: "Write your reply...",
            toolbarItems: [["bold", "italic", "image"]]
        });

        container.querySelector(".cancel-reply").onclick = () => {
            container.querySelector(".reply-form-container").remove();
        };

        container.querySelector(".submit-reply").onclick = async (e) => {
            const markdown = editor.getMarkdown().trim();
            if (!markdown) return;

            const response = await fetch(`/comment/article/${articleId}/new`, {
                method: "POST",
                headers: {"Content-Type": "application/x-www-form-urlencoded"},
                body: new URLSearchParams({
                    "commentBody": markdown,
                    "parentCommentId": commentId
                })
            });

            if (response.ok) {
                const html = await response.text();
                const doc = new DOMParser().parseFromString(html, "text/html");
                const newReply = doc.querySelector(".comment");

                container.querySelector(".reply-form-container").remove();
                container.appendChild(newReply);

                formatLocalTimes();
                renderMarkdown(newReply);
            }
        };
    })
}
