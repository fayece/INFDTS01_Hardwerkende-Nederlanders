import { formatLocalTimes } from './time-formatter.js';
import { renderMarkdown } from "./render-markdown.js";

/**
 * @typedef {Object} ToastUIEditorInstance
 * @property {function(): string} getMarkdown
 * @property {function(string): void} setMarkdown
 */

/** @type {Object.<string, ToastUIEditorInstance>} */
const activeEditors = {};

const initializeNewContent = (targetElement) => {
    formatLocalTimes();
    renderMarkdown(targetElement);
};

const createEditor = (elementId, placeholder, minHeight = "auto") => {
    return new window.toastui.Editor({
        el: document.getElementById(elementId),
        height: "auto",
        minHeight: minHeight,
        initialEditType: "wysiwyg",
        hideModeSwitch: true,
        previewStyle: "vertical",
        placeholder: placeholder,
        toolbarItems: [["bold", "italic", "strike", "image"]],
        hooks: {
            addImageBlobHook: async (blob, callback) => {
                const formData = new FormData();
                formData.append("file", blob);
                try {
                    const response = await fetch("/media/upload", {
                        method: "POST",
                        body: formData
                    });
                    const data = await response.json();
                    callback(data.url, "image");
                } catch (error) {
                    console.error("Image upload failed", error);
                    alert("Failed to upload image.");
                }
            }
        }
    });
};

const extractHtmlFragment = (htmlString, selector) => {
    const doc = new DOMParser().parseFromString(htmlString, "text/html");
    return doc.querySelector(selector);
};

export const initCommentForm = () => {
    const form = document.querySelector(".comment-section--create");
    if (!form) return;

    const editor = createEditor("comment-editor", "Leave a comment...", "8rem");

    form.addEventListener("submit", async (event) => {
        event.preventDefault();

        const markdown = editor.getMarkdown().trim();
        if (!markdown) return;

        const hiddenInput = document.getElementById("commentBody");
        hiddenInput.value = markdown;

        try {
            const response = await fetch(form.action, {
                method: "POST",
                body: new URLSearchParams(new FormData(form)),
                headers: { "Content-Type": "application/x-www-form-urlencoded" }
            });

            const html = await response.text();
            const errorEl = extractHtmlFragment(html, ".error-container");

            if (errorEl) {
                const existing = document.querySelector(".error-container");
                if (existing) existing.replaceWith(errorEl);
                else form.insertAdjacentElement("beforebegin", errorEl);
                return;
            }

            editor.setMarkdown("");
            const newComment = extractHtmlFragment(html, ".comment");
            const list = document.querySelector(".comment-section--list");

            if (newComment && list) {
                const li = document.createElement("li");
                li.appendChild(newComment);
                list.prepend(li);

                initializeNewContent(li);

                const loadMoreBtn = document.getElementById("load-more-comments");
                if (loadMoreBtn) {
                    loadMoreBtn.dataset.offset = String(Number(loadMoreBtn.dataset.offset) + 1);
                }
            }
        } catch (error) {
            console.error("Error submitting comment:", error);
        }
    });
};

export const initReplyInteractions = () => {
    document.addEventListener("click", async (event) => {
        const target = event.target;

        const loadBtn = target.closest(".load-replies-button");
        if (loadBtn) {
            await handleLoadReplies(loadBtn);
            return;
        }

        const replyBtn = target.closest(".reply-button");
        if (replyBtn) {
            handleShowReplyForm(replyBtn);
            return;
        }

        const cancelBtn = target.closest(".cancel-reply");
        if (cancelBtn) {
            const commentId = cancelBtn.dataset.parentId;
            cancelBtn.closest(".reply-form-container").remove();
            if (commentId) delete activeEditors[commentId];
            return;
        }

        const submitBtn = target.closest(".submit-reply");
        if (submitBtn) { await handleSubmitReply(submitBtn); }
    });
};

const handleLoadReplies = async (button) => {
    const parentId = button.dataset.parentId;
    const repliesContainer = document.getElementById(`replies-for-${parentId}`);
    if (!parentId || !repliesContainer) return;

    const originalText = button.textContent;
    button.textContent = "Loading replies...";
    button.disabled = true;

    try {
        const response = await fetch(`/comment/${parentId}/replies`);
        if (!response.ok) {
            button.textContent = "Error loading replies";
            button.disabled = false;
            setTimeout(() => { button.textContent = originalText; }, 3000);
            return;
        }

        repliesContainer.innerHTML = await response.text();
        button.style.display = "none";

        initializeNewContent(repliesContainer);
    } catch (error) {
        button.textContent = "Network error. Please try again later.";
        button.disabled = false;
        setTimeout(() => { button.textContent = originalText; }, 3000);
    }
};

const handleShowReplyForm = (button) => {
    const commentId = button.dataset.commentId;
    const articleElement = button.closest('[data-article-id]') || document.querySelector('[data-article-id]');
    const articleId = articleElement ? articleElement.dataset.articleId : null;

    if (!articleId) return alert("An error occurred. Please refresh the page.");

    const container = document.getElementById(`replies-for-${commentId}`);
    if (container.querySelector(".reply-form-container")) return;

    container.insertAdjacentHTML('afterbegin', `
        <div class="reply-form-container">
            <div id="reply-editor-${commentId}"></div>
            <div>
                <button type="button" class="submit-reply" data-parent-id="${commentId}" data-article-id="${articleId}">Post Reply</button>
                <button type="button" class="cancel-reply" data-parent-id="${commentId}">Cancel</button>
            </div>
        </div>
    `);

    activeEditors[commentId] = createEditor(`reply-editor-${commentId}`, "Write your reply...");
};

const handleSubmitReply = async (button) => {
    const commentId = button.dataset.parentId;
    const articleId = button.dataset.articleId;
    const editor = activeEditors[commentId];

    if (!editor) return;
    const markdown = editor.getMarkdown().trim();
    if (!markdown) return;

    const originalText = button.textContent;
    button.textContent = "Posting...";
    button.disabled = true;

    try {
        const response = await fetch(`/comment/article/${articleId}/new`, {
            method: "POST",
            headers: {"Content-Type": "application/x-www-form-urlencoded"},
            body: new URLSearchParams({ "commentBody": markdown, "parentCommentId": commentId })
        });

        if (response.ok) {
            const html = await response.text();
            const newReply = extractHtmlFragment(html, ".comment"); // Use shared DOM parser!

            if (newReply) {
                const container = document.getElementById(`replies-for-${commentId}`);
                container.querySelector(".reply-form-container").remove();
                container.appendChild(newReply);

                delete activeEditors[commentId];
                initializeNewContent(newReply);
            }
        } else {
            alert("Failed to post reply. Please try again.");
            button.textContent = originalText;
            button.disabled = false;
        }
    } catch (error) {
        alert("Network error. Please check your connection.");
        button.textContent = originalText;
        button.disabled = false;
    }
};