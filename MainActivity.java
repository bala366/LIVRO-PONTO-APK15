package com.livroponto.digital;

import android.app.Activity;
import android.print.PrintManager;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class MainActivity extends Activity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        webView = new WebView(this);
        setContentView(webView);

        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setAllowFileAccess(true);
        s.setBuiltInZoomControls(true);
        s.setDisplayZoomControls(false);

        // Mantém o mesmo dashboard desktop, apenas ajustando a escala à tela do celular.
        s.setUseWideViewPort(true);
        s.setLoadWithOverviewMode(true);

        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.addJavascriptInterface(new AppBridge(this), "AndroidBridge");
        webView.loadUrl("file:///android_asset/index.html");
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    public class AppBridge {
        private final Context context;

        AppBridge(Context context) {
            this.context = context;
        }

        @JavascriptInterface
        public String saveBackup(String json) {
            try {
                File f = new File(getFilesDir(), "livro-ponto-backup.json");
                try (FileOutputStream output = new FileOutputStream(f)) {
                    output.write(json.getBytes(StandardCharsets.UTF_8));
                }
                return new JSONObject().put("ok", true).toString();
            } catch (Exception e) {
                return jsonError(e);
            }
        }

        @JavascriptInterface
        public String loadBackup() {
            try {
                File f = new File(getFilesDir(), "livro-ponto-backup.json");
                if (!f.exists()) {
                    return new JSONObject()
                            .put("ok", false)
                            .put("error", "Nenhum backup encontrado")
                            .toString();
                }

                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                try (FileInputStream input = new FileInputStream(f)) {
                    byte[] chunk = new byte[4096];
                    int n;
                    while ((n = input.read(chunk)) != -1) {
                        buffer.write(chunk, 0, n);
                    }
                }

                JSONObject data = new JSONObject(buffer.toString("UTF-8"));
                return new JSONObject().put("ok", true).put("data", data).toString();
            } catch (Exception e) {
                return jsonError(e);
            }
        }

        @JavascriptInterface
        public String exportCSV(String csv) {
            try {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Downloads.DISPLAY_NAME, "relatorio-ponto.csv");
                values.put(MediaStore.Downloads.MIME_TYPE, "text/csv");
                values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/LivroDePonto");

                android.net.Uri uri = getContentResolver()
                        .insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);

                if (uri == null) throw new Exception("Não foi possível criar o arquivo CSV.");

                try (OutputStream output = getContentResolver().openOutputStream(uri)) {
                    if (output == null) throw new Exception("Não foi possível abrir o arquivo CSV.");
                    output.write(csv.getBytes(StandardCharsets.UTF_8));
                }

                runOnUiThread(() ->
                        Toast.makeText(context, "CSV salvo em Downloads/LivroDePonto", Toast.LENGTH_LONG).show());

                return new JSONObject().put("ok", true).toString();
            } catch (Exception e) {
                return jsonError(e);
            }
        }

        @JavascriptInterface
        public void printPage() {
            runOnUiThread(() -> {
                PrintManager manager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
                manager.print(
                        "Livro de Ponto Digital",
                        webView.createPrintDocumentAdapter("Livro de Ponto Digital"),
                        null
                );
            });
        }

        private String jsonError(Exception e) {
            try {
                return new JSONObject()
                        .put("ok", false)
                        .put("error", String.valueOf(e.getMessage()))
                        .toString();
            } catch (Exception ignored) {
                return "{\"ok\":false}";
            }
        }
    }
}
