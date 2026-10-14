# Como compilar o SummerAOT Kruger no Windows 10

O projeto ainda não passou por build e teste no jogo. Este pacote contém o mesmo código da versão 0.1.0, acrescido do instalador local de Gradle Wrapper; não inclui o JAR do DAOT nem uma distribuição do Gradle.

## Pré-requisitos

- Java 21 instalado (já confirmado no ambiente do usuário).
- Gradle 8.12 instalado em `C:\gradle-8.12-bin\gradle-8.12\bin\gradle.bat` (já confirmado).
- Internet para baixar Gradle 9.2.1, Fabric Loom, Minecraft, Fabric API e GeckoLib.
- JAR original do DAOT em `libs/dannys-aot-2.5.0.jar` (o script tenta copiá-lo automaticamente do projeto anterior).

## Execução no PowerShell

Extraia o ZIP em `C:\MinecraftDev\SummerAOT_Kruger_0.1.0_Source` e execute:

```powershell
cd 'C:\MinecraftDev\SummerAOT_Kruger_0.1.0_Source'
powershell -NoProfile -ExecutionPolicy Bypass -File .\tools\compilar_windows.ps1
```

O script usa o Gradle 8.12 em uma **pasta temporária vazia**, gera os arquivos `gradlew.bat` e `gradle/wrapper` apontando para o Gradle 9.2.1, e executa `gradlew.bat --no-daemon clean build` na raiz do SummerAOT. Assim não é preciso instalar manualmente o Gradle 9.2.1 ou alterar o PATH. O Wrapper *ainda baixa* o Gradle 9.2.1 pela internet na primeira execução.

Build bem-sucedido: `build/libs/summeraot-0.1.0-alpha.1.jar`. Não instale `-sources.jar`.

Se falhar, gere o log manualmente:

```powershell
.\gradlew.bat --no-daemon clean build --stacktrace *> compilacao-summeraot.log
```

O projeto é Fabric 1.21.1, não NeoForge; não instale no Gurupicraft NeoForge.
