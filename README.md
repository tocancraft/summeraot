# SummerAOT — Krugger Titan 0.2.0-alpha.1

Addon Fabric para Minecraft 1.21.1 com o **Krugger Titan** como poder independente do Attack Titan original.

## Estrutura e funcionalidade

- `summeraot:krugger_titan` é uma nova entidade shifter; o Attack Titan do DAOT continua registrado e utilizável separadamente.
- O poder pertence ao SummerAOT e é persistido na tag de jogador `summeraot.power.krugger`; não depende de `attack`, `attack_3`, nem concede essas tags.
- Usa classes de ataque, hitboxes e montagem do Danny's AoT como **dependência externa**, sem reempacotar suas classes.
- Usa o modelo e as 37 animações originais de Kruger do SpringAOT, e a **textura exata fornecida pelo usuário** em `textures/entity/krugger_titan.png`.
- Bloqueia endurecimento (ID 6) e Berserk (ID 9), desativa endurecimento e escalada por mãos endurecidas. A política de habilidades própria expõe somente `ground_stomp`, `attack_kick`, `attack_protect`, `heavy_punch` e `grab` como habilidades elegíveis para uma futura árvore de skills do SummerAOT.
- Os comandos `grant`/`revoke` requerem operador. A transformação usa `/summeraot krugger shift` como ponto de entrada independente nesta alpha.

## Compilação no Windows 10

1. Faça cópia de segurança do diretório do SummerAOT e de qualquer mundo de testes.
2. Copie os arquivos deste projeto para `C:\Users\Matheus Grossi\Desktop\summeraot`, mesclando as pastas `src` e substituindo os arquivos antigos.
3. Mantenha `libs/dannys-aot-2.5.0.jar` em seu local: **não** está incluído neste ZIP.
4. Abra PowerShell na raiz do repositório e execute `./gradlew.bat --no-daemon clean build` (o Gradle Wrapper do repositório anterior pode ser mantido).
5. O JAR gerado estará em `build/libs/summeraot-0.2.0-alpha.1.jar`.
6. Use uma instância isolada Minecraft 1.21.1 Fabric com DAOT, GeckoLib, Fabric API e as demais dependências dele. NÃO instale simultaneamente o SpringAOT.

## Comandos

```mcfunction
/summeraot krugger grant <jogador>  # operador, poder independente
/summeraot krugger revoke <jogador> # operador
/summeraot krugger status          # consulta o próprio poder
/summeraot krugger shift           # transforma quem possui o poder
/summeraot krugger summon          # operador, invoca entidade de teste
```

## Limitações conhecidas da alpha

- Compilação e execução no Minecraft **ainda não verificadas para esta versão**. A versão anterior (`0.1.0-alpha.1`) foi compilada e carregou em jogo.
- O comando de transformação é novo e experimental: reutiliza `AttackTitanEntity.onPlayerShift` e não equivale ao pipeline integral de ascensão, cooldown, stamina, HUD e desbloqueios do DAOT.
- Como a entidade Java herda `AttackTitanEntity`, o HUD original pode mostrar ícones ou rótulos de Attack, inclusive endurecimento, **mesmo com a habilidade bloqueada**. Um HUD inteiramente próprio ainda será necessário.
- Embora o catálogo próprio já exclua Berserk e endurecimento, o HUD herdado do DAOT pode mostrar opções antigas. O HUD próprio ainda não foi implementado.
- O soco pesado está reservado no catálogo, mas a execução especial do SpringAOT ainda não foi portada.
- Esta versão ainda não contém seringa funcional, concessão automática de poder, integração com tecla padrão de transformação nem o soco pesado adicional do SpringAOT.
- O ID da entidade mudou de `summeraot:attack_3_titan` para `summeraot:krugger_titan`. Mundos antigos podem perder entidades da alpha anterior; faça backup e teste em mundo novo.
- Modificações futuras no DAOT podem exigir atualização do addon.

## Origem dos recursos e licenças

- DAOT: depende de JAR instalado separadamente; não copia as classes do Danny.
- SpringAOT 3.5.2: modelos e animações preservados no namespace `summeraot`; licença SpringAOT incluída para referência.
- Textura principal do Krugger: fornecida pelo usuário, copiada integralmente sem edição.

