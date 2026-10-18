# SummerAOT 0.0.4 — patch inicial do Grisha Titan

Este pacote contem **codigo Java e assets reais extraidos do SpringAOT 3.5.2**. Foi construido usando a metodologia de registro independente adotada para o Krugger. **Nao e um JAR pronto e nao contem o codigo completo do SummerAOT Alpha 7.**

## Aplicar ao projeto ja existente (Windows / PowerShell)

1. Extraia este arquivo ZIP em uma pasta de trabalho.
2. Abra o PowerShell nessa pasta e execute:

```powershell
py -3 install_grisha.py "C:\Users\Matheus Grossi\Desktop\summeraot"
```

O instalador verifica o `fabric.mod.json`, salva um backup dos dois arquivos de metadados editados, adiciona classes e assets, faz registro pelos dois entrypoints extras (`GrishaBootstrap` e `GrishaClient`), e atualiza `mod_version` para `0.0.4-alpha.1`. Ele **recusa sobrescrever arquivos Java ou PNG ja existentes** (para evitar perder customizacoes).

Depois da instalacao, compile com o Gradle wrapper do projeto:

```powershell
cd "C:\Users\Matheus Grossi\Desktop\summeraot"
.\gradlew.bat --no-daemon clean build
```

## Teste inicial (sem interface de selecao)

```
/summon summeraot:grisha_titan
```

Confirme geometria, textura, caminhada e ataques. Isso testa o registro e a renderizacao; **nao testa ainda a transformacao via OmniShifter**.

## Integracao funcional pendente

Este pacote ainda **nao altera** o codigo de controle de shifters, o menu OmniShifter, as permissoes/seringas, os packets C2S/S2C, nem a persistencia que foram atualizados entre 0.0.3-alpha.1 e 0.0.3-alpha.7. Sem o fonte atual da 0.0.3-alpha.7 nao se pode aplicar o mesmo percurso de selecao/confirmacao/reversao do Krugger sem risco de sobrescrever funcionalidade. Consulte `INTEGRACAO_OMNISHIFTER.md`.

> O arquivo `attack4titan2.png` e alternativo; o renderer carrega `attack4titan.png` por padrao.
