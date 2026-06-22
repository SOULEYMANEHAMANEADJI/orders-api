# Charge les variables depuis .env et lance l'application Spring Boot en profil local
# Usage: PowerShell.exe -ExecutionPolicy Bypass -File .\run-local.ps1

param(
    [string]$JarPath = "target\rest-api-orders-0.0.1-SNAPSHOT.jar",
    [int]$TimeoutSeconds = 60
)

function Load-DotEnv {
    $envFile = Join-Path (Get-Location) '.env'
    if (-Not (Test-Path $envFile)) {
        Write-Host ".env introuvable à la racine du projet. Créez .env à partir de .env.example" -ForegroundColor Yellow
        return
    }
    Get-Content $envFile | Where-Object { $_ -and -not $_.TrimStart().StartsWith('#') } | ForEach-Object {
        $parts = $_ -split '=',2
        if ($parts.Count -eq 2) {
            $name = $parts[0].Trim()
            $value = $parts[1].Trim()
            Set-Item -Path env:$name -Value $value
        }
    }
    Write-Host "Variables d'environnement chargées depuis .env" -ForegroundColor Green
}

function Test-DbPort {
    param($dbHost, $dbPort)
    Write-Host "Test de connexion TCP vers ${dbHost}:${dbPort} ..."
    $res = Test-NetConnection -ComputerName $dbHost -Port $dbPort -WarningAction SilentlyContinue
    if ($res -and $res.TcpTestSucceeded) {
        Write-Host "TCP OK: ${dbHost}:${dbPort}" -ForegroundColor Green
        return $true
    }
    else {
        Write-Host "TCP NOK: ${dbHost}:${dbPort}" -ForegroundColor Red
        return $false
    }
}

# Charger .env
Load-DotEnv

$dbHost = $env:DB_HOST
$dbPort = $env:DB_PORT
$dbName = $env:DB_NAME
$dbUser = $env:DB_USER
$dbPassword = $env:DB_PASSWORD
if (-Not $dbHost) { $dbHost = '127.0.0.1' }
if (-Not $dbPort) { $dbPort = '3307' }
if (-Not $dbName) { $dbName = 'rest_api_orders_db' }
if (-Not $dbUser) { $dbUser = 'root' }
if (-Not $dbPassword) { $dbPassword = 'Admin$123' }

# Tester la DB
$ok = Test-DbPort -dbHost $dbHost -dbPort $dbPort
if (-Not $ok) {
    Write-Host "Impossible de joindre la base de données sur ${dbHost}:${dbPort}. Vérifiez que MySQL écoute et que le pare-feu ne bloque pas." -ForegroundColor Red
    Write-Host "Si vous voulez forcer un autre port, modifiez .env ou passez --spring.datasource.url sur la ligne de commande." -ForegroundColor Yellow
    exit 1
}

# Lancer l'application
if (-Not (Test-Path $JarPath)) {
    Write-Host "Fichier jar introuvable : $JarPath. Construisez le projet (mvn package) d'abord." -ForegroundColor Red
    exit 1
}

$jdbcUrl = "jdbc:mysql://${dbHost}:${dbPort}/${dbName}?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
$cmd = "java -jar $JarPath --spring.profiles.active=local --spring.datasource.url=$jdbcUrl --spring.datasource.username=$dbUser --spring.datasource.password=********"
Write-Host "Lancement : $cmd" -ForegroundColor Cyan
& java -jar $JarPath `
  --spring.profiles.active=local `
  --spring.datasource.url=$jdbcUrl `
  --spring.datasource.username=$dbUser `
  --spring.datasource.password=$dbPassword `
  --spring.jpa.hibernate.ddl-auto=update `
  --spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect `
  --spring.flyway.enabled=false



