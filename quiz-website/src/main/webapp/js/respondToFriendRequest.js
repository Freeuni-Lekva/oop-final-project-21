document.addEventListener('DOMContentLoaded', () => {
    const forms = document.querySelectorAll('.friend-response-form');

    forms.forEach(form => {
        form.addEventListener('submit', async (e) => {
            e.preventDefault();

            const senderId = form.dataset.senderId;
            const requestId = form.dataset.requestId;
            const action = e.submitter.value;  // "accept" or "decline"

            try {
                const response = await fetch(`${window.contextPath}/respondToFriendRequest`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: new URLSearchParams({
                        senderId,
                        requestId,
                        action
                    })
                });

                if (response.ok) {
                    form.innerHTML = action === 'accept'
                        ? '<span style="color: green;">You are now friends :)</span>'
                        : '<span style="color: gray;">Request declined :(</span>';
                } else {
                    alert('Action failed. Please try again.');
                }
            } catch (err) {
                console.error('Error:', err);
                alert('Something went wrong. lalalala');
            }
        });
    });
});

