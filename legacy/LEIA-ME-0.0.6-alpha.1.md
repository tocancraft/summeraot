# SummerAOT 0.0.6-alpha.1 — Aldeões Shifters, seringas e ovos

Base: fontes do SummerAOT 0.0.5-alpha.1 validados pelo usuário em jogo (Krugger, Grisha, Armin).
**Aviso:** este pacote foi verificado por análise sintática Java e integridade de recursos; não houve build Gradle nem teste in-game neste ambiente.

## Conteúdo

- Três **novos tipos de aldeão**, derivados de `VillagerEntity`, com skins 64×64 fornecidas pelo usuário: `summeraot:krugger_villager`, `summeraot:grisha_villager`, `summeraot:armin_villager`.
- Renderizador com geometria de jogador para preservar corretamente as texturas UV das skins (a geometria de aldeão vanilla não usa as mesmas coordenadas UV).
- Seis ovos de spawn em *Spawn Eggs*: três aldeões (`*_villager_spawn_egg`) e três titãs (Krugger, Grisha e Armin Colossal).
- Seríngas preenchidas: `summeraot:krugger_syringe`, `summeraot:grisha_syringe`, `summeraot:armin_syringe`. Seringa vazia adicional `summeraot:empty_power_syringe`.
- **Compatibilidade de extração com a seringa vazia nativa** `dannys-aot:empty_syringe`. O código do DAOT trata apenas titãs puros/aldeões no TitanPowerData; os três tipos extras são interceptados pelo SummerAOT.
- Sobre aldeão comum, use uma seringa preenchida para torná-lo portador daquele poder. Sobre um aldeão portador, use seringa vazia para extrair o poder e retorná-lo a aldeão normal.
- Sobre o titã SummerAOT, a seringa vazia extrai fluido uma vez por instância do titã. Se for um NPC transformado, retorna a aldeão normal (poder removido). Ovos/summon comuns geram titãs não controlados por jogador.
- Sobre si mesmo, clicar com o botão direito na seringa preenchida concede o poder independente ao personagem (Eldian/Royal, exceto Creative); a seleção é feita depois via OmniShifter.
- Aldeões portadores mantêm a IA vanilla e profissões. Ao serem atacados por um ser vivo e baixarem de 25% de vida, iniciam transformação de 40 ticks. Em forma titânica, perseguem agressor ou hostis próximos, atacam e voltam à forma humana após o combate ou até 80 s.

## IDs — ovos de NPC

`summeraot:krugger_villager_spawn_egg`
`summeraot:grisha_villager_spawn_egg`
`summeraot:armin_villager_spawn_egg`

## IDs — ovos de titã

`summeraot:krugger_titan_spawn_egg`
`summeraot:grisha_titan_spawn_egg`
`summeraot:armin_colossal_titan_spawn_egg`

## IDs — seringas

`summeraot:empty_power_syringe`
`summeraot:krugger_syringe`
`summeraot:grisha_syringe`
`summeraot:armin_syringe`

A seringa vazia SummerAOT pode ser obtida trocando uma seringa vazia nativa em um craft sem formato. Também funciona sem craft no criativo.

## Aplicação

1. Faça backup do mundo e da pasta atual do SummerAOT 0.0.5.
2. **Patch**: extraia o ZIP incremental na raiz do projeto SummerAOT 0.0.5, substituindo arquivos coincidentes. Não misture os arquivos com o JAR já compilado.
3. **Projeto completo**: alternativa para compilar de uma pasta nova, sem depender do seu projeto antigo.
4. Windows: `gradlew.bat --no-daemon clean build` (somente se já possuir o Gradle wrapper configurado). O projeto contém `gradlew.bat`, mas o ambiente desta sessão não tinha distribuição do Gradle ou cache de dependências para validar a compilação.
5. Coloque apenas o JAR gerado `summeraot-0.0.6-alpha.1.jar` nos mods do cliente e servidor. Remova o JAR antigo. Dependências: Fabric 1.21.1, DAOT 2.5.0, GeckoLib, Fabric API.

## Testes recomendados

- `/give @p summeraot:krugger_villager_spawn_egg` e spawn; repetir para Grisha e Armin; conferir textura, profissão e comércio.
- `/give @p summeraot:empty_power_syringe` e extrair de um aldeão portador.
- `/give @p dannys-aot:empty_syringe` e confirmar compatibilidade com uma entidade SummerAOT.
- `/give @p summeraot:armin_colossal_titan_spawn_egg` e verificar comportamento igual ao `/summon summeraot:armin_colossal_titan`.
- Atacar o NPC em campo aberto até ele chegar a 25% de vida; observar transformação, luta e retorno. Testar desconexão, carregamento de chunk e restart.

## Limites desta alpha

- **Não é uma cópia byte-a-byte do sistema de aldeões do DAOT/SpringAOT.** `TitanPowerData` só reconhece os oito tipos originais: estes três NPCs têm registro próprio por identidade de entidade.
- A IA do titã NPC, incluindo perseguição e golpes, é básica, e a nuke do Colossal controlado por jogador não foi implementada para a transformação do aldeão. Precisa de testes em jogo.
- Profissão e nível são preservados ao transformar/reverter. Estoque de trocas, fofocas (gossip), XP interno e outras informações do aldeão não são restaurados integralmente após virar titã. Em conversões via seringa, a identidade de aldeão também é recriada.
- Não foi implementada a herança de poder via devorar um aldeão/titã NPC por titã puro; a aquisição pela seringa e OmniShifter funciona de forma independente.
- As seringas novas não modificam o enum nativo do DAOT nem sobrescrevem as seringas originais.
