# SummerAOT 0.0.6-alpha.2 — IA, bossbars e particulas

Base: 0.0.6-alpha.1 (NPCs, seringas e ovos existentes). Nenhuma identidade de entidade ou item foi alterada.

## Mudancas

- Aldeoes Krugger/Grisha/Armin iniciam a transformacao quando um ataque/dano deixaria sua vida em **70% ou menos** da vida maxima; a espera continua sendo 40 ticks.
- Particulas identicas ao indicador Dust do Danny's AOT 2.5.0: 4 particulas por tick por aldeao no cliente, tamanho 1.0, verde (Attack) para Krugger/Grisha e vermelho (Colossal) para Armin. Funciona para aldeoes criados por ovo, por seringa ou restaurados apos combate.
- Titãs autonomos (invocados por ovo, `/summon`, ou transformados a partir de aldeoes) recebem IA ofensiva: percepcao de jogadores, retaliacao, perseguicao e golpes por `triggerAttack()` do DAOT, conservando animacoes e dano nativos.
- Titãs autonomos ganham bossbar vermelha proporcional a saude. Visibilidade ate 96 blocos (Krugger/Grisha) ou 160 blocos (Armin); a barra e removida ao desaparecer o tita, e gerenciada por jogador e dimensao.
- Titãs com `getShifterUUID() != null` (jogador em forma de tita) NAO recebem IA/bossbar extra.
- O mecanismo de retorno de NPC transformado, seringas e ovos continua presente.

## Como instalar

1. Fazer backup do projeto/servidor.
2. Se optar pelo patch, copiar a estrutura `src/` e `gradle.properties` do ZIP para a **raiz do projeto 0.0.6-alpha.1**, substituindo os arquivos existentes. O pacote completo pode ser extraido como projeto novo.
3. Compilar (Windows): `gradlew.bat --no-daemon clean build`.
4. Instalar o JAR final `build/libs/summeraot-0.0.6-alpha.2.jar` no cliente e servidor, preservando Danny's AOT 2.5.0 e demais dependencias existentes.

## Teste rapido

1. Criar um aldeao portador com um dos 3 ovos. Verificar particulas constantemente ao redor.
2. Com vida maxima de 20 pontos, causar 6 pontos de dano: deve iniciar transformacao, aguardar 40 ticks e surgir o tita.
3. Invocar cada tita com ovo ou `/summon`; aproxime-se em sobrevivencia. Verificar perseguicao, ataques animados e barra vermelha diminuindo conforme dano.
4. Transformar-se como jogador Krugger, Grisha e Armin. Confirmar que a IA nao assume o movimento e que as barras nativas continuam sendo exibidas.
5. Verificar seringas (aplicar, extrair) e retorno de NPC transformado apos combate.

## Validacao e limites

- Analise sintatica Java (52 fontes) sem erros, dados dos efeitos/animacoes cotejados com DAOT via bytecode.
- Gradle **nao compilado** neste ambiente: a distribuicao Gradle requer download e o ambiente nao tem acesso a `services.gradle.org`. Portanto, os testes efetivos in-game sao necessarios.
- A IA usa as animacoes e regras de dano do DAOT; pathfinding dos titas grandes e bloqueios de terreno devem ser conferidos em jogo.
