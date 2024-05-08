function deleteUser(userId) {
    fetch('/admin/deleteUser/' + userId, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json',
            // CSRF token ?
        }
    }).then(response => {
        if (response.ok) {
            alert('User deleted successfully.');
            window.location.reload();
        } else {
            alert('Failed to delete user.');
        }
    }).catch(error => console.error('Error:', error));
}


function editUser(userId) {
    const userName = document.getElementById('userName' + userId).value;
    const userEmail = document.getElementById('userEmail' + userId).value;

    fetch('/admin/editUser/' + userId, {
    method: 'PUT',
    headers: {
    'Content-Type': 'application/json',
    // CSRF token ?
    },
    body: JSON.stringify({ userName, userEmail })
    }).then(response => {
        if (response.ok) {
            alert('User updated successfully.');
            window.location.reload();
        } else {
            alert('Failed to update user.');
        }
    }).catch(error => console.error('Error:', error));
}


function deleteSong(songId) {
    if (confirm('Are you sure you want to delete this song?')) {
        fetch('/admin/deleteSong/' + songId, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
                // CSRF token ?
            }
        }).then(response => {
            if (response.ok) {
                alert('Song deleted successfully.');
                window.location.reload();
            } else {
                alert('Failed to delete song.');
            }
        }).catch(error => console.error('Error:', error));
    }
}

function disableUser(userId) {
    fetch('/admin/disableUser/' + userId, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            // CSRF token ?
        }
    }).then(response => {
        if (response.ok) {
            alert('User disabled successfully.');
            window.location.reload();
        } else {
            alert('Failed to disable user.');
        }
    }).catch(error => console.error('Error:', error));
}

function enableUser(userId) {
    fetch('/admin/enableUser/' + userId, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            // CSRF token ?
        }
    }).then(response => {
        if (response.ok) {
            alert('User enabled successfully.');
            window.location.reload();
        } else {
            alert('Failed to enable user.');
        }
    }).catch(error => console.error('Error:', error));
}

function toggleUserStatus(userId) {
    fetch('/admin/toggleUserStatus/' + userId, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            // CSRF token ?
        }
    }).then(response => {
        if (response.ok) {
            alert('User status updated successfully.');
            window.location.reload();
        } else {
            response.json().then(data => {
                alert('Failed to update user status: ' + data.message);
            });
        }
    }).catch(error => console.error('Error:', error));
}
