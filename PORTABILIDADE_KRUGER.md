# Matriz técnica — Kruger SpringAOT → SummerAOT

| Componente Spring | Implementação SummerAOT | Estado |
|---|---|---|
| `Attack3TitanEntity extends AttackTitanEntity` | `KrugerTitanEntity extends AttackTitanEntity` | Fonte criada, sem build |
| `attack3titan.geo.json` | `assets/summeraot/geo/attack3titan.geo.json` | Copiado byte a byte |
| `attack3titan.animation.json` | `assets/summeraot/animations/attack3titan.animation.json` | Copiado byte a byte, 37 clips |
| `attack3titan.png` e `attack3titan2.png` | `assets/summeraot/textures/entity/` | Copiados byte a byte |
| `Attack3TitanNape/Eye/Grab` | `KrugerNape/Eye/Grab extends` respectivas entidades DAOT | Reuso dos comportamentos DAOT, não cópia integral Spring |
| Hitboxes com bone sync | `KrugerTitanRenderer` + `TitanBoneCache` DAOT | Client-side; falta pacote C2S e checagem in-game |
| `ModNetworkingMixin` do Spring | `AttackTitanSpawnMixin` (`@ModifyVariable`) | Reutiliza restante do pipeline DAOT; necessita build/teste |
| `attack_3` | Tag de seleção para titular nativo do poder `attack` | Seleção parcial; não é poder independente completo |
| 7 habilidades Attack | `AttackTitanEntity` DAOT | Herdadas; diferenças de tiers e UI Spring pendentes |
| `AttackTitanHeavyPunchMixin` | — | Pendente (Spring modifica toda família Attack) |
| `AttackTitanRockPickupMixin` | Recursos de pedra guardados | Mecânica pendente |
| Seringa `custom_model_data=3` | JSON e textura no namespace SummerAOT | Visual pronto; não registrada/interceptada |
| HUD / inventário / persistência Spring | — | Pendente |

## Decisão de arquitetura

Priorizar recursos e herança do DAOT para evitar copiar implementações inteiras do Danny e perder compatibilidade com futuras atualizações. A substituição da instância dentro de `spawnAttackTitan` só ocorre para jogadores já aprovados como Attack Shifters pelo DAOT **e** com a tag `attack_3` selecionada. Isso é mais conservador do que interceptar todo o protocolo de transformação e substituí-lo por uma rotina paralela.

A injeção só foi inspecionada no DAOT fornecido, que declara internamente 2.4.1. Não presumir compatibilidade com atualizações futuras do DAOT.

**Itens não portados neste pacote não devem ser considerados implementados.**
