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

const extractHtmlFragment = (htmlString, selector) => {
    return new DOMParser().parseFromString(htmlString, "text/html").querySelector(selector);
};

const isFullPageHtml = (html) => {
    const lower = html.toLowerCase();
    return lower.startsWith("<!doctype html>") || lower.includes("<body");
};

const setButtonLoading = (button, loadingText) => {
    if (!button) return () => {};
    const originalText = button.textContent;
    button.textContent = loadingText;
    button.disabled = true;
    return () => {
        button.textContent = originalText;
        button.disabled = false;
    };
};

const postUrlEncoded = (url, body) => fetch(url, {
    method: "POST",
    headers: {"Content-Type": "application/x-www-form-urlencoded"},
    body
});

const displayServerError = (html, target) => {
    const errorEl = extractHtmlFragment(html, ".error-container");
    if (!errorEl) return false;

    let existing = target.firstElementChild?.matches(".error-container")
        ? target.firstElementChild
        : null;

    if (!existing) {
        existing = document.createElement("div");
        existing.className = "error-container";
        target.insertAdjacentElement("afterbegin", existing);
    }

    existing.innerHTML = errorEl.innerHTML;
    return true;
};

const createEditor = (elementId, placeholder, minHeight = "auto") => new window.toastui.Editor({
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
            const res = await fetch("/media/upload", { method: "POST", body: formData }).catch(() => null);
            if (!res || !res.ok) return alert("Failed to upload image.");
            callback((await res.json()).url, "image");
        }
    }
});

export const initCommentForm = () => {
    const form = document.querySelector(".comment-section--create");
    if (!form) return;

    const editor = createEditor("comment-editor", "Leave a comment...", "8rem");

    form.addEventListener("submit", async (event) => {
        event.preventDefault();

        const markdown = editor.getMarkdown().trim();
        if (!markdown) return;

        form.commentBody.value = markdown;

        const submitBtn = form.querySelector('button[type="submit"]');
        const restoreButton = setButtonLoading(submitBtn, "Posting...");
        const response = await postUrlEncoded(form.action, new URLSearchParams(new FormData(form))).catch(() => null);

        if (!response) {
            alert("Network error. Please try again later.");
            return restoreButton();
        }

        const html = await response.text();

        if (displayServerError(html, form)) return restoreButton();

        if (!response.ok) {
            alert("Failed to post comment. Please try again.");
            return restoreButton();
        }

        if (isFullPageHtml(html)) {
            alert("Your session may have expired or an error occurred. Please copy your text and refresh the page.");
            return restoreButton();
        }

        editor.setMarkdown("");
        const newComment = extractHtmlFragment(html, ".comment");
        const list = document.querySelector(".comment-section--list");

        if (newComment && list) {
            const li = document.createElement("li");
            li.appendChild(newComment);
            list.prepend(li);
            initializeNewContent(li);
        }

        const loadMoreBtn = document.getElementById("load-more-comments");
        if (loadMoreBtn) loadMoreBtn.dataset.offset = String(Number(loadMoreBtn.dataset.offset) + 1);
    });
};

export const initDeleteInteractions = () => {
    const modal = document.querySelector(".confirm-modal");
    if (!modal) return;

    document.addEventListener("click", (event) => {
        const deleteBtn = event.target.closest(".delete-button");
        if (deleteBtn) {
            modal.dataset.pendingId = deleteBtn.dataset.commentId;
            modal.showModal();
            return;
        }

        if (event.target.closest(".cancel-action")) {
            modal.dataset.pendingId = "";
            modal.close();
            return;
        }

        if (event.target.closest(".confirm-action")) return handleConfirmDelete(modal);
    });
};

const handleConfirmDelete = async (modal) => {
    const commentId = modal.dataset.pendingId;
    if (!commentId) return;

    const commentEl = document.querySelector(`.delete-button[data-comment-id="${commentId}"]`)?.closest(".comment");

    const restoreButton = setButtonLoading(modal.querySelector(".confirm-action"), "Deleting...");
    const response = await fetch(`/comment/${commentId}`, { method: "DELETE" }).catch(() => null);

    modal.dataset.pendingId = "";
    modal.close();
    restoreButton();

    if (!response || !response.ok) {
        if (commentEl) {
            let errorEl = commentEl.querySelector(".delete-error");
            if (!errorEl) {
                errorEl = document.createElement("p");
                errorEl.className = "delete-error error-container";
                commentEl.appendChild(errorEl);
            }
            errorEl.textContent = "Failed to delete comment. Please try again.";
        }
        return;
    }

    const updatedComment = extractHtmlFragment(await response.text(), ".comment");
    if (commentEl && updatedComment) {
        commentEl.replaceWith(updatedComment);
        initializeNewContent(updatedComment);
    }
};

export const initReplyInteractions = () => {
    document.addEventListener("click", async (event) => {
        const target = event.target;

        const loadBtn = target.closest(".load-replies-button");
        if (loadBtn) return handleLoadReplies(loadBtn);

        const replyBtn = target.closest(".reply-button");
        if (replyBtn) return handleShowReplyForm(replyBtn);

        const cancelBtn = target.closest(".cancel-reply");
        if (cancelBtn) {
            const commentId = cancelBtn.dataset.parentId;
            cancelBtn.closest(".reply-form-container").remove();

            if (commentId) delete activeEditors[commentId];
        }

        const submitBtn = target.closest(".submit-reply");
        if (submitBtn) return handleSubmitReply(submitBtn);
    });
};

const handleLoadReplies = async (button) => {
    const parentId = button.dataset.parentId;
    const container = document.getElementById(`replies-for-${parentId}`);
    if (!parentId || !container) return;

    const restoreButton = setButtonLoading(button, "Loading replies...");
    const response = await fetch(`/comment/${parentId}/replies`).catch(() => null);

    if (!response || !response.ok) {
        button.textContent = "Error loading replies";
        return setTimeout(restoreButton, 3000);
    }

    [...container.children].forEach(c => c.matches(".reply-form-container") || c.remove());

    container.insertAdjacentHTML('beforeend', await response.text());
    button.style.display = "none";
    initializeNewContent(container);
};

const handleShowReplyForm = (button) => {
    const commentId = button.dataset.commentId;
    const articleId = button.closest('[data-article-id]')?.dataset.articleId
        || document.querySelector('[data-article-id]')?.dataset.articleId;

    if (!articleId) return alert("An error occurred. Please refresh the page.");

    const container = document.getElementById(`replies-for-${commentId}`);
    if (container.querySelector(".reply-form-container")) return;

    const template = document.getElementById("reply-form-template");
    const clone = template.content.cloneNode(true);

    clone.querySelector(".reply-editor-target").id = `reply-editor-${commentId}`;

    const submitBtn = clone.querySelector(".submit-reply");
    submitBtn.dataset.parentId = commentId;
    submitBtn.dataset.articleId = articleId;

    const cancelBtn = clone.querySelector(".cancel-reply");
    cancelBtn.dataset.parentId = commentId;

    container.prepend(clone);

    activeEditors[commentId] = createEditor(`reply-editor-${commentId}`, "Write your reply...");
};

const handleSubmitReply = async (button) => {
    const commentId = button.dataset.parentId;
    const editor = activeEditors[commentId];
    if (!editor) return;

    const markdown = editor.getMarkdown().trim();
    if (!markdown) return;

    const restoreButton = setButtonLoading(button, "Posting...");
    const payload = new URLSearchParams({ commentBody: markdown, parentCommentId: commentId });
    const response = await postUrlEncoded(`/comment/article/${button.dataset.articleId}/new`, payload).catch(() => null);

    if (!response) {
        alert("Network error. Please check your connection.");
        return restoreButton();
    }

    const html = await response.text();
    const container = document.getElementById(`replies-for-${commentId}`);
    const replyFormContainer = container.querySelector(".reply-form-container");

    if (displayServerError(html, replyFormContainer)) return restoreButton();

    if (!response.ok) {
        alert("Failed to post reply. Please try again.");
        return restoreButton();
    }

    if (isFullPageHtml(html)) {
        alert("Your session may have expired. Please copy your text and refresh.");
        return restoreButton();
    }

    const newReply = extractHtmlFragment(html, ".comment");
    if (newReply) {
        container.querySelector(".reply-form-container").remove();
        container.appendChild(newReply);
        delete activeEditors[commentId];
        initializeNewContent(newReply);
    } else {
        alert("Failed to load the new reply. Please refresh the page.");
        restoreButton();
    }
};
