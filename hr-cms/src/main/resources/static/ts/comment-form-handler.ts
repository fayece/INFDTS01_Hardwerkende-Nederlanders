import { formatLocalTimes } from './time-formatter.js';

export function initCommentForm(): void {
    const form = document.querySelector(".comment-section--create") as HTMLFormElement;
    if (!form) return;

    form.addEventListener("submit", async (event) => {
        event.preventDefault();

        const formData = new FormData(form);
        const response = await fetch(form.action, {
            method: "POST",
            body: new URLSearchParams(formData as any),
            headers: { "Content-Type": "application/x-www-form-urlencoded" }
        });

        const html = await response.text();
        const doc = new DOMParser().parseFromString(html, "text/html");

        const errorEl = doc.querySelector(".error-container");
        if (errorEl) {
            const existing = document.querySelector(".error-container");
            if (existing) {
                existing.replaceWith(errorEl);
            } else {
                form.insertAdjacentElement("beforebegin", errorEl);
            }
        } else {
            (form.querySelector("textarea") as HTMLTextAreaElement).value = "";
            const newList = doc.querySelector(".comment-section--list");
            document.querySelector(".comment-section--list")?.replaceWith(newList!);
            formatLocalTimes();
        }
    });
}