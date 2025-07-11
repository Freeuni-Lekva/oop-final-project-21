document.addEventListener('DOMContentLoaded', () => {
    const forms = document.querySelectorAll('.friend-response-form');

    forms.forEach(form => {
        form.addEventListener('submit', async (e) => {
            e.preventDefault();

            const senderId = form.dataset.senderId;
            const requestId = form.dataset.requestId;
            const action = e.submitter.value;

            try {
                const response = await fetch(`${window.contextPath}/respondToFriendRequest`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                    body: new URLSearchParams({ senderId, requestId, action })
                });

                if (response.ok) {
                    window.location.reload();
                    // form.innerHTML = action === 'accept'
                    //     ? '<span class="status-message success">You are now friends ✓</span>'
                    //     : '<span class="status-message neutral">Request declined</span>';
                } else {
                    alert('Action failed. Please try again.');
                }
            } catch (err) {
                console.error('Error:', err);
                alert('Something went wrong.');
            }
        });
    });
});