document.addEventListener('DOMContentLoaded', () => {
    const input = document.getElementById('search-input');
    const typeSelect = document.getElementById('search-type');
    const results = document.getElementById('search-results');

    function clearResults() {
        results.innerHTML = '';
        results.style.display = 'none';
    }

    function renderResults(items) {
        clearResults();
        if (!items || items.length === 0) {
            return;
        }
        items.forEach(item => {
            const li = document.createElement('li');
            if (item.userName) {
                li.innerHTML = `
        <div style="display: flex; align-items: center; gap: 10px;">
            <img src="${item.imageURL || 'https://via.placeholder.com/40'}"
                 alt="profile"
                 style="width: 40px; height: 40px; border-radius: 50%; object-fit: cover;">
            <div>
                <div style="font-weight: bold;">${item.userName}</div>
                <div style="font-size: 12px; color: #666;">${item.firstName} ${item.lastName}</div>
            </div>
        </div>
    `;
            } else if (item.quizTitle) {
                li.textContent = item.quizTitle;
            } else {
                li.textContent = 'No data';
            }

            li.style.padding = '12px 8px';
            li.style.cursor = 'pointer';

            li.addEventListener('click', () => {
                if (item.userName) {
                    window.location.href = `/profile?user=${encodeURIComponent(item.userName)}`;
                }

                else if (item.quizId) {
                    //
                }
            });

            li.addEventListener('mouseover', () => {
                li.style.backgroundColor = '#007bff';
                li.style.color = 'black';
            });
            li.addEventListener('mouseout', () => {
                li.style.backgroundColor = '';
                li.style.color = '';
            });

            results.appendChild(li);
        });
        results.style.display = 'block';
    }

    async function doSearch() {
        const query = input.value.trim();
        const type = typeSelect.value;

        if (!query) {
            clearResults();
            return;
        }

        try {
            const resp = await fetch(`/search?type=${encodeURIComponent(type)}&query=${encodeURIComponent(query)}`);
            if (!resp.ok) {
                clearResults();
                return;
            }
            const data = await resp.json();
            renderResults(data);
        } catch (e) {
            console.error('Search error', e);
            clearResults();
        }
    }

    input.addEventListener('input', () => {
        // you can debounce this call for performance (optional)
        doSearch();
    });

    typeSelect.addEventListener('change', () => {
        if (input.value.trim()) doSearch();
    });

    // hide dropdown if user clicks outside
    document.addEventListener('click', e => {
        if (!results.contains(e.target) && e.target !== input) {
            clearResults();
        }
    });
});
