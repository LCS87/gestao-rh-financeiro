# Script PowerShell para iniciar o ambiente de desenvolvimento
# Uso: .\scripts\start-dev.ps1

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Gestão RH e Financeiro - Call Center" -ForegroundColor Cyan
Write-Host "  Iniciando ambiente de desenvolvimento" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Verificar se Docker está instalado
Write-Host "Verificando Docker..." -ForegroundColor Yellow
if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
    Write-Host "ERRO: Docker não está instalado ou não está no PATH" -ForegroundColor Red
    exit 1
}

# Verificar se Docker Compose está instalado
if (-not (Get-Command docker-compose -ErrorAction SilentlyContinue)) {
    Write-Host "ERRO: Docker Compose não está instalado ou não está no PATH" -ForegroundColor Red
    exit 1
}

# Verificar se Java está instalado
Write-Host "Verificando Java..." -ForegroundColor Yellow
if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
    Write-Host "ERRO: Java não está instalado ou não está no PATH" -ForegroundColor Red
    exit 1
}

$javaVersion = java -version 2>&1 | Select-String -Pattern 'version'
Write-Host "Java encontrado: $javaVersion" -ForegroundColor Green

# Verificar se Maven está instalado
Write-Host "Verificando Maven..." -ForegroundColor Yellow
if (-not (Get-Command mvn -ErrorAction SilentlyContinue)) {
    Write-Host "ERRO: Maven não está instalado ou não está no PATH" -ForegroundColor Red
    exit 1
}

$mavenVersion = mvn -version | Select-String -Pattern 'Apache Maven'
Write-Host "Maven encontrado: $mavenVersion" -ForegroundColor Green

Write-Host ""
Write-Host "Iniciando serviços com Docker Compose..." -ForegroundColor Yellow
docker-compose up -d

Write-Host ""
Write-Host "Aguardando serviços iniciarem (30 segundos)..." -ForegroundColor Yellow
Start-Sleep -Seconds 30

Write-Host ""
Write-Host "Verificando saúde dos serviços..." -ForegroundColor Yellow

# Verificar PostgreSQL
$pgStatus = docker ps --filter "name=gestao-rh-postgres" --format "{{.Status}}"
if ($pgStatus -like "*Up*") {
    Write-Host "✓ PostgreSQL está rodando" -ForegroundColor Green
} else {
    Write-Host "✗ PostgreSQL não está rodando" -ForegroundColor Red
}

# Verificar Redis
$redisStatus = docker ps --filter "name=gestao-rh-redis" --format "{{.Status}}"
if ($redisStatus -like "*Up*") {
    Write-Host "✓ Redis está rodando" -ForegroundColor Green
} else {
    Write-Host "✗ Redis não está rodando" -ForegroundColor Red
}

# Verificar RabbitMQ

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Ambiente pronto!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Serviços disponíveis:" -ForegroundColor Yellow
Write-Host "  - PostgreSQL: localhost:5432" -ForegroundColor White
Write-Host "  - Redis: localhost:6379" -ForegroundColor White
Write-Host "  - RabbitMQ: localhost:5672" -ForegroundColor White
Write-Host "  - RabbitMQ Management: http://localhost:15672" -ForegroundColor White
Write-Host ""
Write-Host "Para iniciar a aplicação, execute:" -ForegroundColor Yellow
Write-Host "  mvn spring-boot:run" -ForegroundColor White
Write-Host ""
