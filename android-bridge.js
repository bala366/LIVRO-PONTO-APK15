(function () {
  if (!window.crypto) window.crypto = {};
  if (!window.crypto.randomUUID) {
    window.crypto.randomUUID = function () {
      return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
        var r = Math.random() * 16 | 0, v = c === 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
      });
    };
  }

  function androidAvailable() {
    return typeof window.AndroidBridge !== 'undefined';
  }

  window.desktopAPI = {
    saveBackup: async function(data) {
      try {
        if (androidAvailable()) return JSON.parse(AndroidBridge.saveBackup(JSON.stringify(data)));
        localStorage.setItem('livroPontoBackup', JSON.stringify(data));
        return { ok: true };
      } catch (e) { return { ok: false, error: String(e) }; }
    },
    loadBackup: async function() {
      try {
        if (androidAvailable()) return JSON.parse(AndroidBridge.loadBackup());
        var raw = localStorage.getItem('livroPontoBackup');
        return raw ? { ok: true, data: JSON.parse(raw) } : { ok: false, error: 'Nenhum backup encontrado' };
      } catch (e) { return { ok: false, error: String(e) }; }
    },
    exportCSV: async function(csv) {
      try {
        if (androidAvailable()) return JSON.parse(AndroidBridge.exportCSV(csv));
        return { ok: false, error: 'Exportação indisponível' };
      } catch (e) { return { ok: false, error: String(e) }; }
    },
    printPDF: async function() {
      try {
        if (androidAvailable()) AndroidBridge.printPage();
        else window.print();
        return { ok: true };
      } catch (e) { return { ok: false, error: String(e) }; }
    }
  };
})();