# SummerAOT 0.0.3-alpha.1 — OmniShifter integrado

## Arquitetura

- O SummerAOT permanece com o mod ID `summeraot` e depende do Danny's AoT.
- O **OmniShifter original 1.3.0** (metadado interno 1.0.0) fica empacotado como **JAR Fabric aninhado** (CC0).
- O Fabric Loader reconhece internamente o mod ID `omnishifter`: isso é intencional para preservar a persistência, os pacotes de rede, os mixins e a tecla original do OmniShifter.
- **Instale somente o JAR final do SummerAOT; retire o OmniShifter externo da pasta `mods`.** O componente Omni será carregado do interior do SummerAOT.
- O SpringAOT deve permanecer removido para evitar incompatibilidades. É necessário **Fabric Loader 0.19.3 ou superior**, conforme o `fabric.mod.json` do OmniShifter recebido.

## Krugger independente

A posse (`summeraot.power.krugger`) e a seleção (`summeraot.selection.krugger`) são tags independentes. O comando `/daot shifter set @s krugger` continua disponível via ponte do SummerAOT. A tela do Omni recebe um botão extra `Krugger Titan`, que não usa o enum fechado `TitanPowerType` do DAOT.

Para voltar ao poder selecionado no Omni, clique no cartão nativo da tela ou use `/summeraot krugger vanilla`. Comandos:

- `/summeraot krugger select`: ativa Krugger (exige posse).
- `/summeraot krugger vanilla`: devolve a seleção ao OmniShifter.
- `/summeraot krugger status`: exibe posse e seleção.
- `/summeraot krugger shift`: comando alternativo de teste.

**Limitações da primeira Alpha:** Não foi testada em execução. O botão do Krugger é adicional à interface original; não é um cartão nativo de `TitanPowerType`. A UI antiga mantém as demais seleções, cooldowns, sangue, trocas e comandos originais. A seleção ativa de Krugger continua guardada pelo SummerAOT, não na lista interna do Omni. A escolha visual de Krugger ainda não espelha um `activePowerOrdinal` no código Omni. O botão atual mostra texto; a arte `krugger.png` fica preservada no JAR Omni para a evolução da interface.

## Compilar no Windows 10

1. Faça backup do repositório `Desktop\summeraot`.
2. Use o PATCH 0.0.3, que inclui `src`, `embedded`, `build.gradle` e `gradle.properties`. Mescle na raiz do repositório; mantenha `libs/dannys-aot-2.5.0.jar`, `gradlew.bat` e a pasta `gradle` existentes.
3. Execute `./gradlew.bat --no-daemon clean build` no PowerShell.
4. Verifique a existência de `build/libs/summeraot-0.0.3-alpha.1.jar`.
5. Teste em cópia do mundo. Remova as versões antigas do SummerAOT e o OmniShifter externo antes de iniciar.

## Licença e autoria

O OmniShifter embutido declarou licença CC0 1.0 e autor Antigravity. Não alteramos seu bytecode. Os arquivos e recursos específicos do Danny's AoT continuam externos, no JAR do DAOT. O uso dos ícones adicionados pelo usuário pressupõe direitos sobre essas imagens.
