#!/bin/bash
# Script Bash para iniciar o ambiente de desenvolvimento
# Uso: ./scripts/start-dev.sh

echo "========================================"
echo "  Gestão RH e Financeiro - Call Center"
echo "  Iniciando ambiente de desenvolvimento"
echo "========================================"
echo ""

# Verificar se Docker está instalado
echo "Verificando Docker..."
if ! command -v docker &> /dev/null; then
    echo "ERRO: Docker não está instalado"
    exit 1
fi

# Verificar se Docker Compose está instalado
if ! command -v docker-compose &> /dev/null; then
    echo "ERRO: Docker Compose não está instalado"
    exit 1
fi

# Verificar se Java está instalado
echo "Verificando Java..."
if ! command -v java &> /dev/null; then
    echo "ERRO: Java não está instalado"
    exit 1
fi

java -version

# Verificar se Maven está instalado
echo "Verificando Maven..."
if ! command -v mvn &> /dev/null; then
    echo "ERRO: Maven não está instalado"
    exit 1
fi

mvn -version

echo ""
echo "Iniciando serviços com Docker Compose..."
docker-compose up -d

echo ""
echo "Aguardando serviços iniciarem (30 segundos)..."
sleep 30

echo ""
echo "Verificando saúde dos serviços..."

# Verificar PostgreSQL
if docker ps --filter "name=gestao-rh-postgres" --format "{{.Status}}" | grep -q "Up"; then
    echo "✓ PostgreSQL está rodando"
else
    echo "✗ PostgreSQL não está rodando"
fi

# Verificar Redis
if docker ps --filter "name=gestao-rh-redis" --format "{{.Status}}" | grep -q "Up"; then
    echo "✓ Redis está rodando"
else
    echo "✗ Redis não está rodando"
fi

# Verificar RabbitMQ
if docker ps --filter "name=gestao-rh-rabbitmq" --format "{{.Status}}" | grep -q "Up"; then
    echo "✓ RabbitMQ está rodando"
else
    echo "✗ RabbitMQ não está rodando"
fi

echo ""
echo "========================================"
echo "  Ambiente pronto!"
echo "========================================"
echo ""
echo "Serviços disponíveis:"
echo "  - PostgreSQL: localhost:5432"
echo "  - Redis: localhost:6379"
echo "  - RabbitMQ: localhost:5672"
echo "  - RabbitMQ Management: http://localhost:15672"
echo ""
echo "Para iniciar a aplicação, execute:"
echo "  mvn spring-boot:run"
echo ""
