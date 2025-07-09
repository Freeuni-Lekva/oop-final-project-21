document.addEventListener('DOMContentLoaded', () => {
    const forms = document.querySelectorAll('.unfriend-form');

    forms.forEach(form => {
        form.addEventListener('submit', async (e) => {
            e.preventDefault();

            const otherUserId = form.dataset.userId;

            try {
                const response = await fetch(`${window.contextPath}/removeFriend`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: new URLSearchParams({ profileUserId: otherUserId })
                });

                if (response.ok) {
                    const friendStatusMsg = form.closest('div').querySelector('.friend-status-msg');
                    if (friendStatusMsg) {
                        friendStatusMsg.remove();
                    }

                    form.innerHTML = '<span class="status-message neutral">Friend removed</span>';
                } else {
                    alert('Failed to remove friend. Please try again.');
                }
            } catch (error) {
                console.error('Error removing friend:', error);
                alert('Something went wrong. Please try again.');
            }
        });
    });
});
