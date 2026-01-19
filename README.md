# Raspberry Awards API

## Visão Geral
API RESTful desenvolvida em Java com Spring Boot que gerencia dados de filmes indicados ao Raspberry Awards (Pior Filme). A aplicação lê dados de um arquivo CSV, armazena em banco de dados H2 em memória e fornece endpoints para consultar informações sobre produtores que ganharam prêmios consecutivos.

## Características
- **Banco de dados em memória (H2)** - Nenhuma instalação externa necessária
- **Carga automática de dados** - CSV processado na inicialização
- **API REST** - Endpoint para consultar intervalos entre prêmios de produtores
- **Console H2** - Interface web para visualizar dados em tempo real

## Requisitos
- Java 11 ou superior
- Maven 3.6 ou superior

## Instalação e Execução

### 1. Clone o Repositório
```bash
git clone https://github.com/felipejaques/raspberry-awards-back-end.git
cd raspberry-awards-back-end
```

### 2. Compile o Projeto
```bash
mvn clean package
```

### 3. Execute a Aplicação
```bash
mvn spring-boot:run
```

Ou execute o JAR gerado:
```bash
java -jar target/raspberry-awards-1.0-SNAPSHOT.jar
```

A aplicação estará disponível em: `http://localhost:8080`


### Executar Testes
```bash
mvn test
```

### Executar apenas os testes de integração:
```bash
mvn test -Dtest=MovieControllerIntegrationTest
```

## API Endpoints

### Obter Intervalos de Prêmios dos Produtores
Retorna os produtores com maior e menor intervalo entre dois prêmios consecutivos.

**Endpoint:** `GET /api/producers/awards-intervals`

**Resposta de Exemplo:**
```json
{
  "min": [
    {
      "producer": "Joel Silver",
      "interval": 1,
      "previousWin": 1990,
      "followingWin": 1991
    }
  ],
  "max": [
    {
      "producer": "Matthew Vaughn",
      "interval": 13,
      "previousWin": 2002,
      "followingWin": 2015
    }
  ]
}
```

### Console H2
Acesse o console web do H2 em: `http://localhost:8080/h2-console`

**Credenciais:**
- JDBC URL: `jdbc:h2:mem:moviesdb`
- User: `sa`
- Password: *(deixe em branco)*

## Formato do CSV
O arquivo `movielist.csv` deve seguir o formato:
```csv
year;title;studios;producers;winner
1980;Can't Stop the Music;Associated Film Distribution;Allan Carr;yes
1980;Cruising;Lorimar Productions;Jerry Weintraub;
```

**Campos:**
- `year`: Ano do filme
- `title`: Título do filme
- `studios`: Estúdios produtores
- `producers`: Produtores (separados por vírgula ou " and ")
- `winner`: "yes" se ganhou o prêmio, vazio caso contrário

## Tecnologias Utilizadas
- **Spring Boot 2.7.18** - Framework principal
- **Spring Data JPA** - Persistência de dados
- **H2 Database** - Banco de dados em memória
- **Maven** - Gerenciamento de dependências
- **Java 11** - Linguagem de programação

## Configuração
As configurações podem ser ajustadas em `src/main/resources/application.properties`:
- Porta do servidor (padrão: 8080)
- Configurações do banco H2
- Caminho do arquivo CSV

## Observações
- O banco de dados é recriado a cada execução (dados não são persistidos)
- Os dados são carregados automaticamente do CSV na inicialização
- Múltiplos produtores em um mesmo filme são tratados individualmente