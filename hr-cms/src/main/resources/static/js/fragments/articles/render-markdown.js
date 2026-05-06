export function renderMarkdown(root = document) {
    root.querySelectorAll(".comment-body[data-markdown]").forEach((el) => {
        const markdown = el.getAttribute("data-markdown");
        window.toastui.Editor.factory({
            el: el,
            viewer: true,
            initialValue: markdown,
        });
        el.removeAttribute("data-markdown");
    });
}
