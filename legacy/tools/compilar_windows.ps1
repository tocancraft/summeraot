#requires -Version 5.1
$ErrorActionPreference = 'Stop'
$Project = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
Set-Location $Project
$Daot = Join-Path $Project 'libs\dannys-aot-2.5.0.jar'
$PreviousDaot = 'C:\MinecraftDev\DAOT_Kruger_Etapa_B_Revisado\libs\dannys-aot-2.5.0.jar'

if (-not (Test-Path $Daot)) {
    if (Test-Path $PreviousDaot) {
        New-Item -ItemType Directory -Force -Path (Join-Path $Project 'libs') | Out-Null
        Copy-Item $PreviousDaot $Daot
        Write-Host 'Dependencia DAOT copiada do projeto anterior.'
    } else {
        throw "Falta o JAR original do DAOT em $Daot. Copie esse arquivo e execute novamente."
    }
}

$WrapperBat = Join-Path $Project 'gradlew.bat'
if (-not (Test-Path $WrapperBat)) {
    $GradleAntigo = 'C:\gradle-8.12-bin\gradle-8.12\bin\gradle.bat'
    if (-not (Test-Path $GradleAntigo)) {
        throw "Nao achei o Gradle 8.12 em $GradleAntigo. Instale Gradle 9.2.1 ou ajuste o caminho no script."
    }
    $Bootstrap = Join-Path $env:TEMP 'SummerAOT-Gradle-Wrapper-Bootstrap'
    New-Item -ItemType Directory -Force -Path $Bootstrap | Out-Null
    Write-Host 'Gerando Gradle Wrapper 9.2.1 a partir do Gradle 8.12 existente...'
    & $GradleAntigo -p $Bootstrap wrapper --gradle-version 9.2.1 --distribution-type bin
    if ($LASTEXITCODE -ne 0) { throw 'Nao foi possivel gerar o Gradle Wrapper.' }
    Copy-Item (Join-Path $Bootstrap 'gradlew.bat') $WrapperBat -Force
    $WrapperTarget = Join-Path $Project 'gradle\wrapper'
    New-Item -ItemType Directory -Force -Path $WrapperTarget | Out-Null
    Copy-Item (Join-Path $Bootstrap 'gradle\wrapper\*') $WrapperTarget -Force
}

Write-Host 'Verificando Gradle Wrapper. A primeira execucao baixa o Gradle 9.2.1 da distribuicao oficial...'
& $WrapperBat --version
if ($LASTEXITCODE -ne 0) { throw 'Nao foi possivel iniciar o Gradle 9.2.1; verifique conexao e certificados.' }

Write-Host 'Compilando SummerAOT...'
& $WrapperBat --no-daemon clean build
if ($LASTEXITCODE -ne 0) { throw 'Compilacao falhou. Copie a mensagem de erro ou gere compilacao-summeraot.log.' }
Write-Host "Build concluido. Confira os JARs em $(Join-Path $Project 'build\libs')"
