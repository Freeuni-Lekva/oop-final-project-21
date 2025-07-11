document.addEventListener("DOMContentLoaded", () => {
    const { userId, otherUserId } = window.chatConfig;
    const messagesDiv = document.getElementById("messages");
    const form = document.getElementById("message-form");
    const input = document.getElementById("message-input");

    let earliestTimestamp = null;
    let earliestId = null;
    let loading = false;
    let noMoreMessages = false;

    const loadedMessagesMap = new Map();

    async function loadMessages(initial = false) {
        if (loading || noMoreMessages) return;
        loading = true;

        let url = `${window.contextPath}/messages?userId=${otherUserId}`;
        if (!initial && earliestTimestamp && earliestId) {
            url += `&before=${encodeURIComponent(earliestTimestamp)}&beforeId=${earliestId}`;
        }

        console.log(`➡️ Fetching messages before: ${earliestTimestamp} ${earliestId} URL: ${url}`);

        try {
            const resp = await fetch(url);
            if (!resp.ok) throw new Error(`HTTP error! status: ${resp.status}`);

            const messages = await resp.json();

            console.log(`⬅️ Received messages IDs:`, messages.map(m => m.id));

            if (messages.length === 0) {
                noMoreMessages = true;
                loading = false;
                return;
            }

            const orderedMessages = initial ? messages : messages.slice().reverse();

            let addedCount = 0;

            for (const msg of orderedMessages) {
                if (loadedMessagesMap.has(msg.id)) {
                    console.log("Skipping already loaded message ID:", msg.id);
                    continue;
                }

                loadedMessagesMap.set(msg.id, msg.sentAt);

                const div = document.createElement("div");
                div.className = "message " + (msg.senderId === userId ? "outgoing" : "incoming");
                div.textContent = msg.content;

                if (initial) {
                    messagesDiv.appendChild(div);
                } else {
                    messagesDiv.prepend(div);
                }

                addedCount++;
            }

            if (initial) {
                messagesDiv.scrollTop = messagesDiv.scrollHeight;
            }

            if (addedCount === 0) {
                noMoreMessages = true;
                loading = false;
                return;
            }

            let minId = null;
            let minSentAt = null;
            for (const [id, sentAt] of loadedMessagesMap.entries()) {
                if (minSentAt === null || new Date(sentAt) < new Date(minSentAt)) {
                    minSentAt = sentAt;
                    minId = id;
                } else if (new Date(sentAt).getTime() === new Date(minSentAt).getTime() && id < minId) {
                    minId = id;
                }
            }

            earliestTimestamp = minSentAt;
            earliestId = minId;
            console.log(" Updated earliest to:", earliestId, earliestTimestamp);

            loading = false;
        } catch (err) {
            console.error("Failed to load messages", err);
            loading = false;
        }
    }

    loadMessages(true);

    messagesDiv.addEventListener("scroll", () => {
        if (loading || noMoreMessages) return;

        if (messagesDiv.scrollTop < 100) {
            console.log("chat.js: Scroll near top detected, loading older messages");
            loadMessages(false);
        }
    });

    form.addEventListener("submit", async e => {
        e.preventDefault();
        const content = input.value.trim();
        if (content.length === 0) return;

        try {
            const resp = await fetch(`${window.contextPath}/sendMessage`, {
                method: "POST",
                headers: { "Content-Type": "application/x-www-form-urlencoded" },
                body: new URLSearchParams({
                    receiverId: otherUserId,
                    content: content,
                }),
            });

            if (!resp.ok) {
                const errText = await resp.text();
                console.error("Failed to send message:", errText);
                alert("Error sending message: " + errText);
                return;
            }

            const newMsg = await resp.json();

            if (loadedMessagesMap.has(newMsg.id)) {
                console.log("New message already loaded, skipping");
                input.value = "";
                return;
            }

            loadedMessagesMap.set(newMsg.id, newMsg.sentAt);

            const div = document.createElement("div");
            div.className = "message outgoing";
            div.textContent = newMsg.content;
            messagesDiv.appendChild(div);

            messagesDiv.scrollTop = messagesDiv.scrollHeight;

            input.value = "";
        } catch (err) {
            console.error("Error sending message", err);
            alert("Error sending message");
        }
    });
});







