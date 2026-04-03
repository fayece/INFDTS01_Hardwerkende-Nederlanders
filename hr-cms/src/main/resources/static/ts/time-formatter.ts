export const formatLocalTimes= (): void => {
    const timeElements = document.querySelectorAll(".local-time");

    timeElements.forEach((element) => {
        const timestamp = element.getAttribute("data-timestamp");

        if (timestamp) {
            const localDate = new Date(timestamp);

            element.textContent = localDate.toLocaleString(undefined, {
                day: "2-digit",
                month: "2-digit",
                year: "numeric",
                hour: "2-digit",
                minute: "2-digit",
            }).replace(',', '');
        }
    });
};
