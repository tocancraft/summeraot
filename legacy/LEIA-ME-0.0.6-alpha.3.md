# SummerAOT 0.0.6-alpha.3 — Colossal NPC e explosão das transformações

Base: SummerAOT 0.0.6-alpha.2, testada pelo usuário no Minecraft 1.21.1 Fabric + Danny's AOT 2.5.0.

## Alterações

1. **Armin Colossal autônomo (ovo, /summon ou aldeão)**:
   - `ActiveTargetGoal` do Colossal deixa de exigir linha de visão a partir dos olhos (60 blocos acima do solo).
   - Busca adicional de jogadores próximos em modo sobrevivência caso o alvo não seja identificado pela IA vanilla.
   - Movimento horizontal controlado por velocidade, sem depender do pathfinder vanilla incapaz de planejar rotas para a hitbox de 16 × 60 blocos.
   - Direção do corpo, cabeça e ataques voltada para o alvo; acionamento de `triggerAttack()` e `triggerKickAbility()` nativos, respeitando cooldowns e animações DAOT.
   - IA e novos controles só se aplicam aos titãs **não associados a um jogador** (`getShifterUUID() == null`).

2. **Transformações dos três aldeões**:
   - O gatilho permanece em **70% de vida** e a espera permanece em 40 ticks.
   - O raio foi movido para o instante em que o aldeão efetivamente se transforma, sincronizado com explosão e surgimento do titã.
   - Envia `TitanSpawnPayload` nativo do DAOT aos jogadores da dimensão (até 512 blocos), acionando efeitos e som da transformação, incluindo o atraso sonoro pela distância.
   - Chama a rotina **original** `ModNetworking.triggerTransformationExplosion` por mixin invoker, replicando os valores do mod: Attack 10 blocos, 20 de dano e impulso 2; Colossal 20 blocos, 30 de dano e impulso 3.
   - Adiciona `EXPLOSION_EMITTER` e fumaça ao efeito visual da explosão.
   - Envia ao cliente um evento próprio do SummerAOT apenas para Armin NPC, executando `ShiftParticleHelper.spawnColossalNukeParticle()` — mesmo efeito nuclear Effekseer do DAOT, sem dependência de um jogador montado.
   - Para Armin NPC, ativa também as **20 fases nucleares** nativas do Colossal DAOT (sem montar um jogador na entidade).
   - A onda de choque executa **uma só vez**, depois que o aldeão original já foi removido. Não usa `World.createExplosion` duplicadamente.

3. Bossbars, partículas de aldeões, skins, seringas, ovos, modelos, IDs e poderes dos jogadores permanecem inalterados.

## Instalação

- **Patch**: extraia as pastas/arquivos do patch sobre a raiz do projeto `summeraot` na `0.0.6-alpha.2`, substituindo os originais.
- **Projeto completo**: extraia em uma pasta nova.
- Compilação: `gradlew.bat --no-daemon clean build`.
- Esperado: `build/libs/summeraot-0.0.6-alpha.3.jar`.
- Instale cliente e servidor com a mesma versão; mantenha Danny's AOT 2.5.0.

## Testes em mundo de cópia (importante)

1. Em **terreno aberto e longe de construções**, invoque Armin por ovo ou `/summon`, aproxime-se em sobrevivência; aguarde o término da transformação (80 ticks quando vem de um aldeão). Verifique orientação, deslocamento, golpes e bossbar.
2. Teste um aldeão Krugger/Grisha a partir de 20 HP: 6 pontos de dano -> 14 HP -> 40 ticks -> raio + explosão de impacto/som + titã.
3. Teste aldeão Armin **muito longe da base**, pois a transformação ativa a sequência nuclear do Colossal DAOT, que pode destruir grandes áreas e causar dano severo. Use uma cópia do mundo.
4. Verifique novamente as transformações **controladas por jogador** para Krugger/Grisha/Armin; nenhuma delas deve receber IA autônoma.
5. Confirme partículas de aldeões, bossbars e seringas como na Alpha 2.

## Validação e limites

- Verificação sintática com `javac`/JavacTask: 56 fontes Java analisados, zero erros de parse.
- 25 JSONs de recursos validados.
- Rotinas nativas de ataque, explosão e partículas cotejadas por `javap` no DAOT 2.5.0.
- **Não houve compilação Gradle neste ambiente** (a distribuição Gradle requerida não está disponível offline). Executar o build e testes de execução dentro do jogo antes de promover a versão a estável.
- Não testado em servidor dedicado. A performance da sequência nuclear nativa e a navegação do Colossal por biomas estreitos precisam ser verificadas.
