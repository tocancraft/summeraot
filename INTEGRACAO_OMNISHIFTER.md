# Integração a fazer usando o fonte atual do SummerAOT 0.0.3-alpha.7

O Grisha Titan tem **identidade independente**. Nao usar `attack`, `attack_shifter`, `krugger`, `attack3` como tag do seu poder.

| Campo | Valor |
| --- | --- |
| Chave de poder | `grisha` |
| EntityType | `summeraot:grisha_titan` |
| Canal de selecao sugerido | `summeraot:select_grisha` |
| Icone no OmniShifter | `assets/omnishifter/textures/gui/titan_icons/grisha.png` |
| Geometria / animacoes | `attack4titan` (SpringAOT 3.5.2) |
| Habilidade de endurecimento DAOT | `6` — bloqueada no backend |
| Berserker | **Nao disponibilizar** |

## Reproduzir fluxo do Krugger alpha.7

1. Acrescentar `grisha` nas fontes de poder/seleção/persistência que ja suportam `krugger`, sem alterar o DAOT `attack`.
2. Acrescentar elemento `Grisha Titan` à grade do OmniShifter e fornecer a arte final `grisha.png` no namespace citado (sprite **nao foi incluido** por indisponibilidade do PNG original aprovado).
3. Adicionar pacote C2S para selecao (`summeraot:select_grisha`) e o handler no servidor com a mesma confirmacao/reversao que ja existe para Krugger. O cliente nao e fonte de verdade.
4. Nas fabricas de transformacao, associar `grisha` a `GrishaEntities.GRISHA_TITAN` e as respectivas hitboxes.
5. Adicionar serializacao, sincronizacao, migracao e regras de seringa/atribuição independentes.
6. Ocultar habilidades de endurecimento, escalada dependente de endurecimento e Berserker no menu/servidor (além do bloqueio Java).
7. Reaproveitar procedimentos de animacao/hitbox dos ataques herdados e testar ataque 1, ataque 2, defesa, agarrar e arremessar, sem alterar os poderes de Krugger/Attack.

## Observacao tecnica

O DAOT 2.5.0 referencia `toggleHardening` como metodo privado disparado por `triggerAbility(6)`. O metodo foi interceptado antes de `super.triggerAbility`. `setHardeningMode(...)` e `setTitanClimbing(...)` tambem sao restritos. Nao ha um metodo `triggerBerserk` na classe base inspecionada `daot.AttackTitanEntity`; portanto o veto ao Berserker no nivel do power system depende das classes da versao corrente do SummerAOT.

A renderizacao de hitboxes e inicialmente a mesma estrutura basica de `DAOT_Kruger_Etapa_B` (renderer do DAOT, sem sincronizacao avancada com ossos do Spring). Revisar as posicoes e sincronizacao do projeto atual no proximo passo.
