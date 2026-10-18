# SummerAOT 0.0.5-alpha.1 — Armin Colossal

## Encerramento da 0.0.4
A versao 0.0.4 (Grisha + Krugger) foi aceita pelo usuario apos testes dentro do Minecraft em 14/10/2026. Este pacote parte da implementacao testada. Mantenha backup do JAR 0.0.4 e do mundo.

## O que foi implementado
- Novo shifter independente: `summeraot:armin_colossal_titan`.
- Geometria original do SpringAOT 3.5.2 (`armincolossal.geo.json`, 24 ossos).
- Textura original (`armincolossal.png`) e os 12 clipes de animacao originais; adiciona 1 clipe `arm_drag` do DAOT 2.5.0 para suportar a habilidade nova.
- Mesma base `daot.ColossalTitanEntity` do SpringAOT, mantendo ataques e habilidades de Colossal conforme DAOT 2.5.0.
- Registro próprio de entidade/atributos, renderer, hitboxes animadas de nuca e olho (hitboxes do DAOT).
- Integracao no fluxo de transformacao e nuke do DAOT: troca o objeto criado no STORE da funcao spawnColossalTitan, sem cancelá-la.
- Poder concedido e selecionado via tags `summeraot.power.armin` e `summeraot.selection.armin`.
- Novo card "Armin Colossal" com sprite `armin.png`, que ja esta no OmniShifter incorporado.
- Cooldown nativo de 30 s, raio cosmetico e sincronizacao cliente/servidor.
- Selecao instantanea provisoria no menu com reconciliacao apos retorno do servidor.
- Grisha e Krugger mantidos inalterados fora dos pontos comuns de selecao e rede.

## Aplicacao (patch incremental)
1. Faca backup da pasta `summeraot` (0.0.4 que funcionou) e do mundo.
2. Extraia o ZIP **na raiz da pasta do seu projeto** `summeraot`, mantendo a estrutura `src/main/...` e sobrescrevendo os arquivos.
3. Na raiz do projeto, rode no PowerShell:
   ` .\gradlew.bat --no-daemon clean build`
4. Instale `build/libs/summeraot-0.0.5-alpha.1.jar` no cliente e servidor. Remova o JAR anterior; nao use duas versoes simultaneamente.
5. Mantenha o JAR de Danny's AoT 2.5.0 e GeckoLib como ja configurados.

## Comandos
Com OP: `/daot shifter set <jogador> armin` (concede), `/daot shifter remove <jogador> armin` (remove).
`/summeraot armin status` para consultar, `/summeraot armin select` para selecionar.
Em seguida, transforme-se pela **tecla normal** do DAOT; a transformacao preserva o efeito Colossal, inclusive explosao.
Para testar apenas spawn sem transformar: `/summon summeraot:armin_colossal_titan`.

## Validacao e precaucoes
- Analise sintatica Java 21: 45 arquivos analisados, 0 erros de sintaxe.
- Teste de estado do seletor (Java isolado): PASS.
- Assets: nomes/arquivos e ossos de hitbox encontrados.
- O build Gradle completo NAO foi executado neste ambiente (distribuicao/caches do Gradle ausentes).
- Ainda nao ha teste em jogo do Armin nesta etapa.
- **CUIDADO:** o Colossal nativo possui explosao/nuke que pode destruir construcoes. Teste numa copia do mundo.
- Injeção `spawnColossalTitan` depende da estrutura interna do DAOT 2.5.0. Revalidar se atualizar o mod.
- Cliente e servidor devem usar a MESMA versao do SummerAOT porque o pacote de estado ganhou dois campos booleanos (Armin).

## Licenciamento e referencia
Geometria, textura e animacoes extraidas dos arquivos SpringDAOT 3.5.2 fornecidos. Licenca CC0 (copia LICENSE_springdaot.txt no projeto completo). O SpringDAOT original NAO deve ser instalado ao mesmo tempo (breaks em fabric.mod.json).
