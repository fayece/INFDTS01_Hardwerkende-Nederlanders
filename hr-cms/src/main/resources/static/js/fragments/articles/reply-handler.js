import { formatLocalTimes } from './time-formatter.js';
import { renderMarkdown } from "./render-markdown.js";

export const initReplyLoader = () => {
    document.addEventListener("click", async (event) => {
        const target = event.target;

        if (!target.classList.contains("load-replies-button")) return;

        const parentId = target.getAttribute("data-parent-id");
        const repliesContainer = document.getElementById(`replies-for-${parentId}`);

        if (!parentId || !repliesContainer) return;

        const button = target;
        const originalText = button.textContent;

        button.textContent = "Loading replies...";
        button.disabled = true;

        try {
            const response = await fetch(`/comment/${parentId}/replies`);

            if (!response.ok) {
                button.textContent = "Error loading replies";
                button.disabled = false;
                return;
            }

            repliesContainer.innerHTML = await response.text();

            target.style.display = "none";

            formatLocalTimes();
            renderMarkdown();

        } catch (error) {
            console.error("Network error:", error);
            button.textContent = "Connection error";
            button.disabled = false;

            setTimeout(() => { button.textContent = originalText; }, 3000);
        }
    });
}
