# Alterações — Krugger 0.2.0-alpha.1

| Subsistema | Mudança |
|---|---|
| Poder | Tag exclusiva `summeraot.power.krugger` persistente e verificada no servidor |
| Administração | `/summeraot krugger grant` e `revoke` com permissão nível 2 |
| Transformação | `/summeraot krugger shift` sem exigir a tag `attack` |
| Registro | Novo tipo `summeraot:krugger_titan` e três hitboxes próprias |
| DAOT original | Retirada a injeção `AttackTitanSpawnMixin` e referência no JSON, preservando o poder Attack original |
| Habilidades | Endurecimento (ID 6) e Berserk (ID 9) bloqueados, braços não endurecem, escalada com mãos endurecidas desativada; catálogo próprio não oferece nenhum dos dois |
| Visual | `krugger_titan.png` é a imagem fornecida pelo usuário sem nenhuma alteração de pixels |
| Bossbar | Exibe `Krugger Titan` |
| Compilação | Gradle 9.2.1, Loom 1.14.10 e Java 21 em ambiente Windows já configurado pelo usuário |

**Atenção:** Não se trata ainda de portabilidade completa da transformação nativa do SpringAOT. Esta alpha introduz uma camada de poder própria do SummerAOT com comando de transformação experimental. Não foram realizados testes de execução com esta nova versão. A compatibilidade de controles, HUD, stamina e escalada durante o gameplay deverá ser verificada.
