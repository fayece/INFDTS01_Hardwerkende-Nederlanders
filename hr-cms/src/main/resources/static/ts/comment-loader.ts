import { formatLocalTimes } from './time-formatter.js';

const loadComments = async (): Promise<void> => {

    const container = document.getElementById("comment-section-container") as HTMLElement | null;
    if (!container) return;

    const articleId: string | null = container.getAttribute("data-article-id");

    if (!articleId) {
        console.error("No article ID found on the comment container.");
        return;
    }

    try {
        const response = await fetch(`/comment/article/${articleId}?page=1`);

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        container.innerHTML = await response.text();

        formatLocalTimes();

    } catch (error) {

        container.innerHTML = "<p class='error-msg'>Could not load comments at this time.</p>";
        console.error("Failed to fetch comments:", error);
    }
};

document.addEventListener("DOMContentLoaded", loadComments);


document.addEventListener("click", async (event: MouseEvent) => {
    const target = event.target as HTMLElement;

    if (target.classList.contains("load-replies-button")) {
        const parentId = target.getAttribute("data-parent-id");
        const repliesContainer = document.getElementById(`replies-for-${parentId}`);

        if (!parentId || !repliesContainer) return;

        target.textContent = "Loading replies...";
        target.setAttribute("disabled", "true");

        try {
            const response = await fetch(`/comment/${parentId}/replies`);

            if (!response.ok) throw new Error("Failed to load replies");

            repliesContainer.innerHTML = await response.text();

            target.style.display = "none";

            formatLocalTimes();
        } catch (error) {
            console.error(error);
            target.textContent = "Error loading replies";
            target.removeAttribute("disabled");
        }
    }
});
