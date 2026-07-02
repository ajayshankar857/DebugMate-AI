// Authentication and API helper utilities for DebugMate AI

const Auth = {
  // Save authentication details
  saveSession(token, user) {
    localStorage.setItem('debugmate_token', token);
    localStorage.setItem('debugmate_user', JSON.stringify(user));
  },

  // Clear authentication details
  clearSession() {
    localStorage.removeItem('debugmate_token');
    localStorage.removeItem('debugmate_user');
  },

  // Get token
  getToken() {
    return localStorage.getItem('debugmate_token');
  },

  // Get user profile metadata
  getUser() {
    const userStr = localStorage.getItem('debugmate_user');
    return userStr ? JSON.parse(userStr) : null;
  },

  // Check if session exists
  isAuthenticated() {
    return !!this.getToken();
  },

  // Log user out
  logout() {
    this.clearSession();
    this.showToast('Logged out successfully.', 'info');
    setTimeout(() => {
      window.location.href = 'login.html';
    }, 1000);
  },

  // Secure API call helper that appends JWT token automatically
  async apiCall(endpoint, options = {}) {
    const token = this.getToken();
    
    // Setup request headers
    const headers = {
      'Content-Type': 'application/json',
      ...options.headers
    };

    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    const config = {
      ...options,
      headers
    };

    try {
      const response = await fetch(endpoint, config);
      
      // Auto logout on 401 Unauthorized
      if (response.status === 401) {
        this.clearSession();
        window.location.href = 'login.html';
        throw new Error('Session expired. Please log in again.');
      }

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.message || `API error (${response.status})`);
      }

      // Check if response has body contents
      const contentType = response.headers.get('content-type');
      if (contentType && contentType.includes('application/json')) {
        return await response.json();
      }
      return null;
    } catch (error) {
      console.error('API call failed:', error);
      throw error;
    }
  },

  // Show a beautifully animated custom toast notification
  showToast(message, type = 'info') {
    let container = document.getElementById('toast-container');
    if (!container) {
      container = document.createElement('div');
      container.id = 'toast-container';
      container.className = 'toast-container';
      document.body.appendChild(container);
    }

    const toast = document.createElement('div');
    toast.className = `custom-toast ${type}`;
    
    let iconClass = 'bi-info-circle';
    if (type === 'success') iconClass = 'bi-check-circle-fill';
    if (type === 'error') iconClass = 'bi-exclamation-triangle-fill';

    toast.innerHTML = `
      <i class="bi ${iconClass}"></i>
      <div class="toast-body">${message}</div>
    `;

    container.appendChild(toast);

    // Remove toast after duration
    setTimeout(() => {
      toast.classList.add('fade-out');
      toast.addEventListener('animationend', () => {
        toast.remove();
      });
    }, 4000);
  },

  // Secure route checks
  guardPage(isProtectedRoute = true) {
    const authenticated = this.isAuthenticated();
    const currentPage = window.location.pathname.split('/').pop();

    if (isProtectedRoute && !authenticated) {
      window.location.href = 'login.html';
    } else if (!isProtectedRoute && authenticated) {
      // If user is logged in, redirect them away from login/register pages
      if (currentPage === 'login.html' || currentPage === 'register.html') {
        window.location.href = 'index.html';
      }
    }
  }
};
