export function renderMarkdown(root: Document | Element = document): void {
    root.querySelectorAll(".comment-body[data-markdown]").forEach((el) => {
        const markdown = el.getAttribute("data-markdown")!;
        (window as any).toastui.Editor.factory({
            el: el,
            viewer: true,
            initialValue: markdown,
        });
        el.removeAttribute("data-markdown");
    });
}
