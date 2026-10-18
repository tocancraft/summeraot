# SummerAOT 0.0.8-alpha.3 — correção do visual do Grisha (v14)

Esta distribuição tem **arquivos de projeto na raiz do ZIP**, sem pasta extra.

## O que está corrigido

- Geo original de `grisha_novo(1)(1).zip` (30 ossos, 131 cubos).
- Textura original `grisha_bestial_tom_pele_referencia_v14.png` (2048x1024 RGBA).
- Modelo GeckoLib `GrishaTitanModel` referenciando **somente** o novo GEO/PNG.
- Remoção dos antigos recursos visuais de Grisha (Attack4 padrão e v12).
- Mantidas animações SpringAOT e alterações de combate da alpha 2 (0,70x velocidade; 1,60x dano).
- Mantido hotfix do mixin `AttackTitanTimersAccessor`.
- Mensagens de diagnóstico no `latest.log` do cliente.

## Instalação sugerida

1. Renomeie a pasta antiga `summeraot` para `summeraot-backup` e **crie uma pasta `summeraot` nova e vazia**.
2. Extraia **o conteúdo deste ZIP diretamente na pasta `summeraot`**. O arquivo `gradlew.bat` deve estar diretamente em `summeraot\gradlew.bat`, NÃO em uma subpasta.
3. Abra o PowerShell nessa pasta e execute:

```powershell
powershell -ExecutionPolicy Bypass -File .\VERIFICAR_E_COMPILAR_GRISHA.ps1
```

Este comando remove arquivos visuais obsoletos *somente dentro deste projeto*, compila e confere no JAR gerado os hashes do GEO/PNG e o modelo Java compilado.

4. Copie `build\libs\summeraot-0.0.8-alpha.3.jar` para a pasta `mods` do **cliente** e do **servidor**. **Remova os JARs anteriores do SummerAOT** de ambas as pastas.
5. Reinicie o Minecraft completamente, em vez de apenas recarregar os recursos.
6. Para confirmar a entidade certa no Minecraft, use:

```mcfunction
/summon summeraot:grisha_titan
```

7. Verifique em `logs/latest.log` do cliente as mensagens:

- `SUMMERAOT_CLIENT_VISUAL_V14_READY`
- `SUMMERAOT_GRISHA_VISUAL_V14_ACTIVE`

Se a primeira estiver ausente, o cliente provavelmente está carregando um JAR antigo. Se a primeira existir mas a segunda não, confirme se está invocando `summeraot:grisha_titan`.

## Observações

Os arquivos de geometria e textura foram preservados exatamente, sem alteração de UV. A correção do empacotamento foi validada. A compilação completa e a validação dentro do Minecraft precisam ser executadas em seu ambiente.
