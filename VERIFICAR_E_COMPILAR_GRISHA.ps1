param(
    [switch]$SomenteVerificar
)

$ErrorActionPreference = 'Stop'
$ProjectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $ProjectRoot

$ExpectedGeoSha = '482901968cbfae7c2df7ef13ceb4130c80546e07a7d3d644c16e2e7bf4d38d6a'
$ExpectedPngSha = '32d9f2a4941d2e126b88b0398fb603b55255ad8e4c5dfc5ac6945cba410074d4'
$Geo = 'src/main/resources/assets/summeraot/geo/grisha_bestial_tom_pele_referencia_v14.geo.json'
$Texture = 'src/main/resources/assets/summeraot/textures/entity/grisha_bestial_tom_pele_referencia_v14.png'
$ModelJava = 'src/main/java/dev/summeraot/grisha/client/GrishaTitanModel.java'

function Assert-ExpectedHash($Path, $Expected) {
    if (-not (Test-Path $Path -PathType Leaf)) {
        throw "Arquivo ausente: $Path"
    }
    $Actual = (Get-FileHash -Path $Path -Algorithm SHA256).Hash.ToLowerInvariant()
    if ($Actual -ne $Expected) {
        throw "SHA256 incorreto em: $Path`nObtido: $Actual`nEsperado: $Expected"
    }
    Write-Host "OK - arquivo original validado: $Path"
}

function Get-ZipEntryHash($ZipArchive, $EntryName) {
    $Entry = $ZipArchive.GetEntry($EntryName)
    if ($null -eq $Entry) {
        throw "Entrada ausente no JAR: $EntryName"
    }
    $Stream = $Entry.Open()
    try {
        $Hasher = [System.Security.Cryptography.SHA256]::Create()
        try {
            $Hash = $Hasher.ComputeHash($Stream)
            return ([System.BitConverter]::ToString($Hash) -replace '-', '').ToLowerInvariant()
        }
        finally {
            $Hasher.Dispose()
        }
    }
    finally {
        $Stream.Dispose()
    }
}

Write-Host 'Verificando fontes do Grisha v14...'
Assert-ExpectedHash $Geo $ExpectedGeoSha
Assert-ExpectedHash $Texture $ExpectedPngSha

$ModelText = Get-Content -Path $ModelJava -Raw
if (-not $ModelText.Contains('geo/grisha_bestial_tom_pele_referencia_v14.geo.json') -or
    -not $ModelText.Contains('textures/entity/grisha_bestial_tom_pele_referencia_v14.png')) {
    throw 'GrishaTitanModel.java nao aponta para os novos assets.'
}

$OldAssets = @(
    'src/main/resources/assets/summeraot/geo/attack4titan.geo.json',
    'src/main/resources/assets/summeraot/textures/entity/attack4titan.png',
    'src/main/resources/assets/summeraot/textures/entity/attack4titan2.png',
    'src/main/resources/assets/summeraot/geo/grisha_bestial_cabeca_original_restaurada_v12.geo.json',
    'src/main/resources/assets/summeraot/textures/entity/grisha_bestial_cabeca_original_restaurada_v12.png'
)
foreach ($Old in $OldAssets) {
    if (Test-Path $Old) {
        Remove-Item -LiteralPath $Old -Force
        Write-Host "Asset antigo eliminado: $Old"
    }
}

if (-not $SomenteVerificar) {
    Write-Host 'Compilando a partir desta pasta...'
    & (Join-Path $ProjectRoot 'gradlew.bat') --no-daemon clean build
    if ($LASTEXITCODE -ne 0) { throw "Gradle falhou: codigo $LASTEXITCODE" }
}

$Jar = Join-Path $ProjectRoot 'build/libs/summeraot-0.0.8-alpha.3.jar'
if (-not (Test-Path $Jar -PathType Leaf)) {
    if ($SomenteVerificar) {
        Write-Host 'Fontes corretos. Compile com .\gradlew.bat --no-daemon clean build'
        exit 0
    }
    throw "JAR nao encontrado: $Jar"
}

Add-Type -AssemblyName System.IO.Compression
Add-Type -AssemblyName System.IO.Compression.FileSystem
$Zip = [System.IO.Compression.ZipFile]::OpenRead($Jar)
try {
    $GeoEntry = 'assets/summeraot/geo/grisha_bestial_tom_pele_referencia_v14.geo.json'
    $TextureEntry = 'assets/summeraot/textures/entity/grisha_bestial_tom_pele_referencia_v14.png'
    if ((Get-ZipEntryHash $Zip $GeoEntry) -ne $ExpectedGeoSha) { throw 'Geo incorreto no JAR final.' }
    if ((Get-ZipEntryHash $Zip $TextureEntry) -ne $ExpectedPngSha) { throw 'Textura incorreta no JAR final.' }

    $ClassName = 'dev/summeraot/grisha/client/GrishaTitanModel.class'
    $ClassEntry = $Zip.GetEntry($ClassName)
    if ($null -eq $ClassEntry) { throw 'Classe GrishaTitanModel ausente do JAR.' }
    $Stream = $ClassEntry.Open()
    try {
        $Memory = New-Object System.IO.MemoryStream
        try { $Stream.CopyTo($Memory); $ClassBytes = $Memory.ToArray() }
        finally { $Memory.Dispose() }
    }
    finally { $Stream.Dispose() }
    $ClassString = [System.Text.Encoding]::GetEncoding(28591).GetString($ClassBytes)
    if (-not $ClassString.Contains('grisha_bestial_tom_pele_referencia_v14')) {
        throw 'O bytecode GrishaTitanModel.class nao referencia o modelo v14.'
    }

    foreach ($Old in $OldAssets) {
        $OldEntry = $Old.Substring('src/main/resources/'.Length).Replace('\', '/')
        if ($null -ne $Zip.GetEntry($OldEntry)) { throw "O JAR ainda contem asset antigo: $OldEntry" }
    }
}
finally { $Zip.Dispose() }

Write-Host ''
Write-Host 'OK: JAR compilado com o geo e textura exatos do grisha_novo(1)(1).zip.'
Write-Host "JAR pronto: $Jar"
Write-Host 'Instale o JAR novo no cliente e no servidor e remova JARs anteriores do SummerAOT.'
Write-Host 'No client/latest.log procure SUMMERAOT_CLIENT_VISUAL_V14_READY e SUMMERAOT_GRISHA_VISUAL_V14_ACTIVE.'
