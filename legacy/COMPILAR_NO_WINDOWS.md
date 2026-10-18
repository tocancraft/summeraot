# Compilar SummerAOT Krugger 0.2.0-alpha.1 no Windows 10

1. Feche o Minecraft, faça backup da pasta `C:\Users\Matheus Grossi\Desktop\summeraot`.
2. Copie o conteúdo deste ZIP para a pasta `summeraot`, **mesclando** as pastas `src` e `tools` e substituindo os arquivos antigos. Não crie uma pasta `src` dentro de outra `src`.
3. **Preserve** os arquivos existentes `gradlew.bat`, `gradlew`, a pasta `gradle/wrapper/` e a dependência `libs/dannys-aot-2.5.0.jar` que já funcionavam na versão 0.1.0.
4. Abra PowerShell dentro de `summeraot` e execute **uma linha apenas**:

```powershell
.\gradlew.bat --no-daemon clean build
```

5. O resultado esperado é `build/libs/summeraot-0.2.0-alpha.1.jar`. Ignore `-sources.jar`.
6. Teste numa instância separada com Minecraft 1.21.1 Fabric, DAOT e suas dependências. Não instale o SpringAOT junto.

## Testar o poder (operador e jogador no mundo)

```mcfunction
/summeraot krugger grant <jogador>
/summeraot krugger status
/summeraot krugger shift
```

Os ataques Berserk (ID 9) e endurecimento (ID 6) não devem responder. O HUD herdado do DAOT poderá continuar mostrando botões antigos até termos um HUD próprio. O soco pesado especial do Spring ainda não foi integrado funcionalmente.

**Status:** este código ainda não passou por compilação ou teste dentro do Minecraft. A versão 0.1.0-alpha.1 anterior foi compilada e testada com sucesso.
