# Win10 — primeira etapa (Android)

Projeto inicial em Kotlin + Jetpack Compose para abrir no Android Studio.

## O que já está incluído
- Tela de abertura com boneco vermelho minimalista e animação suave.
- Transição para menu principal branco.
- Nome Win10 no canto inferior esquerdo.
- Botão ON/OFF com estado visual e texto de status.
- Solicitação de permissões quando o usuário toca em ATIVAR.

## Permissões
A primeira etapa solicita câmera, microfone e acesso à mídia/arquivos quando o usuário ativa o recurso. O botão **não grava, fotografa, envia arquivos nem executa em segundo plano**: ele apenas muda o estado local do aplicativo. O armazenamento interno privado do próprio app normalmente não exige permissão no Android; as permissões de mídia são para arquivos compartilhados.

## Como abrir e gerar APK
1. Instale o Android Studio e um JDK 17.
2. Abra a pasta `Win10_Android_Project` no Android Studio.
3. Aguarde a sincronização do Gradle.
4. Use **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
5. O APK de debug geralmente fica em `app/build/outputs/apk/debug/app-debug.apk`.

## Nota
Este ambiente não possui uma compilação Android verificada, então o ZIP contém o código-fonte do projeto, não um APK já compilado. A compilação pode exigir baixar Gradle e dependências pelo Android Studio.
