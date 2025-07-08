document.addEventListener('DOMContentLoaded', () => {
    const forms = document.querySelectorAll('.friend-request-form');

    forms.forEach(form => {
        form.addEventListener('submit', async (e) => {
            e.preventDefault(); // Prevent page reload

            const receiverId = form.dataset.receiverId;

            try {
                const response = await fetch(`${window.location.origin}${contextPath}/sendFriendshipRequest`,
                    {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: new URLSearchParams({ receiverId })
                });

                if (response.ok) {
                    // Change button text
                    const button = form.querySelector('.friend-request-btn');
                    button.textContent = 'Request Sent';
                    button.disabled = true;
                } else {
                    alert('Failed to send request');
                }
            } catch (err) {
                console.error('Error sending friend request:', err);
                alert('Something went wrong');
            }
        });
    });
});