# SummerAOT 0.0.8-alpha.3 — pasta completa ajustada

Este pacote e o projeto **inteiro** da 0.0.8-alpha.3 (nao e um patch).

O Grisha esta configurado para usar EXATAMENTE os arquivos do ultimo
`grisha_novo(1)(1).zip`, sem modificacoes no conteudo original:
- Geometria: `src/main/resources/assets/summeraot/geo/grisha_bestial_tom_pele_referencia_v14.geo.json`
- Textura: `src/main/resources/assets/summeraot/textures/entity/grisha_bestial_tom_pele_referencia_v14.png`
- Animacoes mantidas: `src/main/resources/assets/summeraot/animations/attack4titan.animation.json`

SHA-256 do arquivo GEO: `482901968cbfae7c2df7ef13ceb4130c80546e07a7d3d644c16e2e7bf4d38d6a`
SHA-256 da TEXTURA: `32d9f2a4941d2e126b88b0398fb603b55255ad8e4c5dfc5ac6945cba410074d4`

O pacote tambem inclui os ajustes de combate da alpha.2:
- Dano ofensivo 60% maior (multiplicador 1.60x)
- Ataques 30% mais lentos (velocidade 0.70x)
- Hotfix de compilacao no cast `AttackTitanTimersAccessor`
- Import corrigido de `ServerEntityEvents`

Os assets anteriores de Grisha (attack4titan.geo.json, attack4titan.png,
attack4titan2.png e arquivos visuais da v12) foram removidos.

## Uso
1. Extraia o ZIP em uma pasta nova. O diretório `SummerAOT_0.0.8-alpha.3` e a raiz do projeto.
2. Abra um terminal nesta pasta.
3. Execute no Windows:

```powershell
.\gradlew.bat --no-daemon clean build
```

4. Utilize o JAR produzido em `build/libs`, substituindo a versao anterior.
5. Mantenha so uma versao do SummerAOT na pasta `mods` do cliente e servidor.

Nao foi executada uma compilacao Gradle neste ambiente. Os arquivos do novo
GEO/textura e os caminhos de referencia foram verificados.
