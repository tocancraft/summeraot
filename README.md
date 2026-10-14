# SummerAOT — Kruger 0.1.0-alpha.1

**Addon Fabric de Minecraft 1.21.1 (Java 21) que seleciona Kruger como variante do Attack Titan do Danny's AoT.**

O SpringAOT **não precisa ser instalado**. A estrutura visual de Kruger veio do SpringAOT 3.5.2, enquanto controles, ataques-base, stamina, hardening, berserk, hitboxes e ciclo de shift são reutilizados do **DAOT como dependência externa**.

## Status verificável

- Código-fonte preparado. **Não compilado nem testado no Minecraft** neste ambiente.
- Entidade `summeraot:attack_3_titan` e três hitboxes (`_nape`, `_eye`, `_grab`).
- Modelo de 21 bones, 37 animações e texturas originais, incluindo variação alternativa.
- Mixin de substituição **pontual do objeto AttackTitanEntity criado na rotina de shift do DAOT**, permitindo que a rotina DAOT permaneça responsável pelos seus controles, raios, explosão, efeitos e verificações. **Este ponto é sensível a versões do DAOT.**
- Seleção `attack_3` apenas quando o jogador já possui a tag `attack` reconhecida pelo DAOT.
- Três hitboxes utilizam lógica original do DAOT para dano/grab; reprodução *idêntica* dos parâmetros da versão Spring ainda requer testes e ajustes.
- Sincronização client-side com bones feita com TitanBoneCache do DAOT; pacote específico de sincronização do Spring **não foi portado**.
- Seringa, heavy punch especial, rock pickup e menu de habilidades Spring: **arquivos ou referências preparados, lógica ainda pendente**. Não chamar isto de port completo.

## Pré-requisitos

- Minecraft 1.21.1 + Fabric Loader, Fabric API, GeckoLib 4.8.3, Danny's AoT com dependências próprias.
- Java JDK 21.
- Gradle **9.2.1** (Fabric Loom **1.14.10**). O Gradle 8.12 não é compatível com este projeto.
- JAR **original** do DAOT em `libs/dannys-aot-2.5.0.jar` (nome externo; o mod declara versão interna 2.4.1).

Não incluir JAR de terceiros no produto distribuído. Use uma cópia local do original adquirido legitimamente.

## Windows PowerShell

```powershell
cd "C:\MinecraftDev\SummerAOT_Kruger_0.1.0_Source"
New-Item -ItemType Directory -Path .\libs -Force
Copy-Item "C:\Caminho\do\dannys-aot-2.5.0.jar" .\libs\dannys-aot-2.5.0.jar
& "C:\MinecraftDev\tools\gradle-9.2.1\bin\gradle.bat" --no-daemon clean build
```

Se Gradle estiver no PATH, use `gradle --no-daemon clean build`. JAR compilado (se o build passar) em `build/libs/summeraot-0.1.0-alpha.1.jar`.

## Testes no jogo

Com DAOT e SummerAOT instalados, **não instale SpringAOT ao mesmo tempo**. Em mundo de desenvolvimento:

- `/summeraot kruger summon` (operador) invoca entidade para inspeção visual; não concede poder.
- Jogador com poder Attack normal do Danny: `/summeraot kruger select` seleciona variante Kruger.
- Transforme-se pelo **mecanismo normal do Danny**. A classe deve ser substituída por Kruger durante a criação.
- `/summeraot kruger vanilla` remove o seletor da variante, mantendo o poder do DAOT.

Não atribuímos o poder Attack automaticamente nem modificamos o verificador de integridade do DAOT.

Se o Mixin não encontrar seu ponto de injeção, o Fabric deverá interromper a inicialização. **Não ignore erros de Mixin** — significa que a versão do DAOT não é compatível e precisamos adaptar o ponto de integração.

## Proveniência e licença

SpringAOT 3.5.2 (`CC0-1.0` informado no seu `fabric.mod.json`). O DAOT declara `ARR`; suas classes são dependência externa, sem cópia no SummerAOT. Verifique permissões de todos os ativos antes de publicar ou redistribuir. Referências do Spring não garantem propriedade sobre ativos de terceiros.

Veja `PORTABILIDADE_KRUGER.md` e execute `python tools/validar_recursos.py` para verificação dos recursos.
