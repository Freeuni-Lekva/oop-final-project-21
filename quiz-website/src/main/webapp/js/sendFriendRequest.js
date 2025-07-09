document.addEventListener('DOMContentLoaded', () => {
    const forms = document.querySelectorAll('.friend-request-form');

    forms.forEach(form => {
        form.addEventListener('submit', async (e) => {
            e.preventDefault();

            const receiverId = form.dataset.receiverId;

            try {
                const response = await fetch(`${window.contextPath}/sendFriendshipRequest`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                    body: new URLSearchParams({ receiverId })
                });

                if (response.ok) {
                    const button = form.querySelector('.btn-send');
                    button.textContent = 'Request Sent ✓';
                    button.disabled = true;
                    button.classList.remove('btn-send');
                    button.classList.add('btn-disabled');
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