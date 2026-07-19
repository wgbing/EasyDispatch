/* EasyDispatch 通用 JS 工具 */
const API = {
  get: (url) => fetch(url).then(r => r.ok ? r.json() : Promise.reject(r)),
  post: (url, body) => fetch(url, {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify(body)
  }).then(r => r.ok ? r.json() : r.json().then(e => Promise.reject(e))),
  put: (url, body) => fetch(url, {
    method: 'PUT',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify(body)
  }).then(r => r.ok ? r.json() : r.json().then(e => Promise.reject(e))),
  delete: (url) => fetch(url, {method: 'DELETE'})
    .then(r => r.ok ? null : r.json().then(e => Promise.reject(e))),
};

function uploadWithProgress(url, file, onProgress) {
  return new Promise((resolve, reject) => {
    const xhr = new XMLHttpRequest();
    xhr.open('POST', url);
    xhr.upload.onprogress = (e) => {
      if (e.lengthComputable && onProgress) {
        onProgress(Math.round((e.loaded / e.total) * 100));
      }
    };
    xhr.onload = () => {
      if (xhr.status >= 200 && xhr.status < 300) {
        try { resolve(JSON.parse(xhr.responseText)); }
        catch (e) { reject({message: '响应解析失败'}); }
      } else {
        try { reject(JSON.parse(xhr.responseText)); }
        catch (e) { reject({message: '上传失败: HTTP ' + xhr.status}); }
      }
    };
    xhr.onerror = () => reject({message: '网络错误，上传失败'});
    const fd = new FormData();
    fd.append('file', file);
    xhr.send(fd);
  });
}

function showToast(message, type = 'info', duration = 3000) {
  const toast = document.createElement('div');
  toast.className = 'toast ' + type;
  toast.textContent = message;
  document.body.appendChild(toast);
  setTimeout(() => {
    toast.style.opacity = '0';
    toast.style.transition = 'opacity .3s';
    setTimeout(() => toast.remove(), 300);
  }, duration);
}

function humanSize(bytes) {
  if (!bytes && bytes !== 0) return '-';
  if (bytes >= 1024 * 1024 * 1024) return (bytes / 1024 / 1024 / 1024).toFixed(2) + ' GB';
  if (bytes >= 1024 * 1024) return (bytes / 1024 / 1024).toFixed(2) + ' MB';
  if (bytes >= 1024) return (bytes / 1024).toFixed(1) + ' KB';
  return bytes + ' B';
}

function fileIconClass(type) {
  if (!type) return 'type-default';
  const t = type.toLowerCase();
  if (['exe', 'msi', 'dmg', 'app'].includes(t)) return 'type-exe';
  if (['pdf'].includes(t)) return 'type-pdf';
  if (['zip', 'rar', '7z', 'gz', 'tar'].includes(t)) return 'type-zip';
  if (['doc', 'docx', 'txt', 'md'].includes(t)) return 'type-doc';
  return 'type-default';
}

function fileIconEmoji(type) {
  if (!type) return '📄';
  const t = type.toLowerCase();
  if (['exe', 'msi', 'dmg', 'app'].includes(t)) return '⚙️';
  if (['pdf'].includes(t)) return '📕';
  if (['zip', 'rar', '7z', 'gz', 'tar'].includes(t)) return '🗜️';
  if (['doc', 'docx', 'txt', 'md'].includes(t)) return '📝';
  if (['mp4', 'avi', 'mov', 'mkv'].includes(t)) return '🎬';
  if (['mp3', 'wav', 'flac'].includes(t)) return '🎵';
  if (['png', 'jpg', 'jpeg', 'gif', 'bmp'].includes(t)) return '🖼️';
  return '📄';
}

// 检测后端是否在线
async function checkBackendHealth() {
  try {
    await fetch('/api/templates').then(r => {
      if (!r.ok) throw new Error();
      const banner = document.getElementById('offlineBanner');
      if (banner) banner.style.display = 'none';
    });
  } catch (e) {
    const banner = document.getElementById('offlineBanner');
    if (banner) banner.style.display = 'block';
  }
}

// 每隔 30 秒检查一次后端
setInterval(checkBackendHealth, 30000);
