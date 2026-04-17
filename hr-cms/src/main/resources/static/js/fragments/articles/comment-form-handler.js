import { formatLocalTimes } from './time-formatter.js';
import { renderMarkdown } from "./render-markdown.js";

export function initCommentForm() {
    const form = document.querySelector(".comment-section--create");
    if (!form) return;

    const editor = new window.toastui.Editor({
        el: document.getElementById("comment-editor"),
        height: "auto",
        minHeight: "8rem",
        initialEditType: "wysiwyg",
        hideModeSwitch: true,
        previewStyle: "vertical",
        placeholder: "Leave a comment...",
        toolbarItems: [
            ["bold", "italic", "strike", "image"]
        ],
        hooks: {
            addImageBlobHook: async (blob, callback) => {
                const formData = new FormData();
                formData.append("file", blob);
                const response = await fetch("/media/upload", {
                    method: "POST",
                    body: formData
                });

                const data = await response.json();
                const { url } = data;
                callback(url, "image");
            }
        }
    });

    form.addEventListener("submit", async (event) => {
        event.preventDefault();
        const hiddenInput = document.getElementById("commentBody");
        hiddenInput.value = editor.getMarkdown().trim();
        const formData = new FormData(form);
        const response = await fetch(form.action, {
            method: "POST",
            body: new URLSearchParams(formData),
            headers: { "Content-Type": "application/x-www-form-urlencoded" }
        });

        const html = await response.text();
        const doc = new DOMParser().parseFromString(html, "text/html");
        const errorEl = doc.querySelector(".error-container");

        if (errorEl) {
            const existing = document.querySelector(".error-container");
            if (existing) existing.replaceWith(errorEl);
            else form.insertAdjacentElement("beforebegin", errorEl);
        }
        else {
            editor.setMarkdown("");
            const newComment = doc.querySelector(".comment");
            const list = document.querySelector(".comment-section--list");

            if (newComment && list) {
                const li = document.createElement("li");
                li.appendChild(newComment);
                list.prepend(li);
                formatLocalTimes();
                renderMarkdown(li);

                const loadMoreBtn = document.getElementById("load-more-comments");
                if (loadMoreBtn) {
                    loadMoreBtn.dataset.offset = String(Number(loadMoreBtn.dataset.offset) + 1);
                }
            }
        }
    });
}
