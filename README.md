# LIVRO DE PONTO DIGITAL V15 — ANDROID APK

Versão Android baseada diretamente no dashboard Windows que já funcionou.

## O dashboard foi preservado
- `index.html`, `styles.css` e `app.js` vieram da versão Windows aprovada.
- O Android usa WebView em modo de visão ampla para manter o mesmo dashboard.
- `android-bridge.js` apenas conecta backup, restauração, CSV e impressão/PDF ao Android.

## Estrutura robusta para GitHub
O projeto contém a estrutura Android completa em `app/`, mas o workflow também recria
automaticamente a pasta `app` usando os arquivos visíveis da raiz. Assim, se o upload pelo
navegador não levar uma pasta, a compilação ainda pode reconstruí-la.

## Workflow
`.github/workflows/compilar-android-apk.yml`

Na aba Actions:
`COMPILAR APK ANDROID`

Artefato:
`LIVRO-DE-PONTO-DIGITAL-ANDROID-APK`

Arquivo:
`app-debug.apk`
