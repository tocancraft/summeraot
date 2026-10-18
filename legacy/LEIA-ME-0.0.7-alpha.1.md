# SummerAOT 0.0.7-alpha.1 — NPCs dos titãs originais

Base: SummerAOT 0.0.6-alpha.3 (Minecraft 1.21.1 Fabric, Danny's AOT 2.5.0)

## Inclusões

- 7 aldeões com aparência humana no formato de skin Minecraft 64×64, preservando trocas e profissão do aldeão.
- 7 ovos correspondentes aos aldeões com poder, e 7 ovos que geram as entidades originais do DAOT.
- Transformação dos aldeões ao atingir 70% de vida; espera de 40 ticks; raio, onda de choque e eventos de surgimento.
- Partículas com as cores nativas do DAOT, IA básica e barra de vida para as entidades geradas **pela transformação desses aldeões**.
- Retorno do NPC à forma humana após aproximadamente 80 segundos.
- Substituição **somente** da textura humana do aldeão Armin pela nova `armin_human.png`.

As entidades dos titãs originais usadas pelos ovos permanecem as mesmas do Danny's AOT. Ovos de titã invocados diretamente usam os comportamentos nativos do DAOT; a IA adicional do SummerAOT fica restrita aos titãs que vieram de aldeões com os novos ovos. Não substituímos as transformações dos jogadores, seus poderes ou os ovos já registrados pelo DAOT.

## IDs novos

| Forma | Aldeão | Ovo do aldeão | Ovo do titã (entidade DAOT) |
|---|---|---|---|
| Ataque | `summeraot:attack_villager` | `summeraot:attack_villager_spawn_egg` | `summeraot:attack_titan_spawn_egg` |
| Colossal | `summeraot:colossal_villager` | `summeraot:colossal_villager_spawn_egg` | `summeraot:colossal_titan_spawn_egg` |
| Blindado | `summeraot:armored_villager` | `summeraot:armored_villager_spawn_egg` | `summeraot:armored_titan_spawn_egg` |
| Fêmea/Annie | `summeraot:annie_villager` | `summeraot:annie_villager_spawn_egg` | `summeraot:annie_titan_spawn_egg` |
| Martelo de Guerra | `summeraot:warhammer_villager` | `summeraot:warhammer_villager_spawn_egg` | `summeraot:warhammer_titan_spawn_egg` |
| Mandíbula | `summeraot:jaw_villager` | `summeraot:jaw_villager_spawn_egg` | `summeraot:jaw_titan_spawn_egg` |
| Quadrúpede | `summeraot:cart_villager` | `summeraot:cart_villager_spawn_egg` | `summeraot:cart_titan_spawn_egg` |

No DAOT 2.5.0, o Mandíbula é `DannysAot.TEST_SHIFTER_TITAN`; o Quadrúpede é `DannysAot.CART_SHIFTER_TITAN`. A textura não enviada do Beast permanece inalterada: não foi inventada uma skin para essa forma.

## Instalação

Faça backup. Extraia o PATCH sobre a raiz dos fontes SummerAOT 0.0.6-alpha.3, confirmando a mesclagem de `src` e substituindo os arquivos modificados. Alternativamente, use o projeto completo.

No PowerShell (Windows):

```powershell
.\gradlew.bat --no-daemon clean build
```

O JAR gerado deve ir em `build/libs/summeraot-0.0.7-alpha.1.jar`. Lembre-se de remover o JAR antigo da pasta `mods` e atualizar **cliente e servidor**.

## Testes

1. Criar os sete aldeões com seus ovos, verificar cada skin 64×64 e as partículas.
2. Confirmar que 6 pontos de dano num aldeão de 20 pontos de vida iniciam a transformação.
3. Conferir raio, explosão, modelo correspondente, bossbar, ataques e retorno à forma humana.
4. Usar os sete ovos de titã e verificar que geram os titãs originais, sem alterar os originais do DAOT.
5. Verificar que as skins de Krugger e Grisha não mudaram e que o aldeão Armin usa a nova textura.
6. Confirmar que os jogadores com poderes DAOT/OmniShifter continuam funcionando.

**Estado:** fontes preparados e recursos verificados estaticamente. Não houve compilação Gradle completa nem teste de execução dentro do Minecraft. As formas nativas podem ter restrições de IA próprias do DAOT; esta integração não modifica essas classes.
