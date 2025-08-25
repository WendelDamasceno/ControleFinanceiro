# Sistema de Controle Financeiro

Sistema desenvolvido para a disciplina de Programação Orientada a Objetos (POO) do IFBA.

## 📋 Descrição

Este é um sistema de controle financeiro pessoal que permite gerenciar receitas, despesas, categorias e orçamentos. O projeto utiliza Java com Maven, banco de dados MySQL e arquitetura MVC.

## 🏗️ Arquitetura

O projeto segue o padrão MVC (Model-View-Controller) com as seguintes camadas:

- **Model**: Classes de entidade (Transacao, Categoria, Orcamento)
- **View**: Interfaces gráficas (a implementar)
- **Controller**: Lógica de negócio (TransacaoController, CategoriaController, etc.)
- **DAO**: Acesso a dados (TransacaoDAO, CategoriaDAO, etc.)

## 🚀 Funcionalidades

### ✅ Implementadas

- **Gestão de Categorias**: CRUD completo para categorias de transações
- **Gestão de Transações**: Registro de receitas e despesas
- **Gestão de Orçamentos**: Definição de limites de gastos por categoria/período
- **Relatórios**: Resumos financeiros e comparações orçamento vs real
- **Validações**: Sistema robusto de validação de dados
- **Exceções**: Tratamento adequado de erros

### 🔄 Em Desenvolvimento

- **Interface Gráfica**: Telas para interação com o usuário
- **Método Main**: Aplicação principal executável

## 🛠️ Tecnologias

- **Java 21**
- **Maven** - Gerenciamento de dependências
- **MySQL** - Banco de dados
- **SLF4J + Logback** - Sistema de logs
- **JUnit** - Testes unitários

## ⚙️ Configuração do Ambiente

### 1. Pré-requisitos

- Java 21 ou superior
- Maven 3.6+
- MySQL 8.0+

### 2. Configuração do Banco de Dados

#### Opção A: Arquivo de Configuração (Desenvolvimento Local)

1. Copie o arquivo template:
```bash
cp src/main/resources/database.properties.template src/main/resources/database.properties
```

2. Edite o arquivo `database.properties` com suas credenciais:
```properties
db.url=jdbc:mysql://localhost:3306/controle_financeiro?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
db.username=root
db.password=SUA_SENHA_AQUI
db.driver=com.mysql.cj.jdbc.Driver
```

#### Opção B: Variáveis de Ambiente (Produção/Segurança)

Configure as seguintes variáveis de ambiente:
```bash
export DB_URL="jdbc:mysql://localhost:3306/controle_financeiro?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
export DB_USERNAME="root"
export DB_PASSWORD="sua_senha_aqui"
export DB_DRIVER="com.mysql.cj.jdbc.Driver"
```

### 3. Criação do Banco

Execute o script SQL para criar o banco de dados:
```bash
mysql -u root -p < script_banco.sql
```

### 4. Compilação e Execução

```bash
# Compilar o projeto
mvn clean compile

# Executar testes automáticos
mvn exec:java -Dexec.mainClass="com.controlfinanceiro.TesteAutomatico"

# Executar aplicação principal (quando implementada)
mvn exec:java -Dexec.mainClass="com.controlfinanceiro.App"
```

## 🔒 Segurança

⚠️ **IMPORTANTE**: O arquivo `database.properties` contém informações sensíveis e está configurado no `.gitignore` para não ser enviado ao repositório.

- Use o arquivo `database.properties.template` como base
- Configure suas credenciais localmente
- Para ambientes de produção, use variáveis de ambiente

## 📦 Estrutura do Projeto

```
src/main/java/com/controlfinanceiro/
├── controller/          # Controllers da aplicação
├── dao/                # Interfaces de acesso a dados
│   └── impl/           # Implementações dos DAOs
├── exception/          # Exceções customizadas
├── model/              # Entidades do domínio
│   └── enums/          # Enumerações
├── util/               # Classes utilitárias
└── view/               # Interfaces gráficas (a implementar)
```

## 🗄️ Banco de Dados

O sistema utiliza 3 tabelas principais:

- **categoria**: Categorias de transações
- **transacao**: Registros de receitas e despesas
- **orcamento**: Limites de gastos por categoria/período

### Como executar o script:
```sql
mysql -u root -p < script_banco.sql
```

## ⚙️ Como Executar

### Pré-requisitos
- Java 21+
- Maven 3.6+
- MySQL 8.0+

### Passos:
1. Clone o repositório
2. Execute o script do banco de dados
3. Configure as credenciais em `database.properties`
4. Compile o projeto:
   ```bash
   mvn clean compile
   ```
5. Execute os testes:
   ```bash
   mvn test
   ```

## 📊 Dados de Exemplo

O script do banco inclui dados de exemplo:
- 11 categorias pré-definidas
- 6 transações de exemplo
- 4 orçamentos para Janeiro/2025

## 🧪 Testes

Execute os testes unitários:
```bash
mvn test
```

## 📝 Logs

Os logs são configurados via `logback.xml` e salvos em:
- Console (nível INFO)
- Arquivo (nível DEBUG)

## 👥 Autor

Wendel Damasceno - Disciplina POO/IFBA

## 📅 Status do Projeto

**Primeira Entrega - Concluída ✅**
- [x] Modelos de dados
- [x] Controllers com validações
- [x] DAOs com operações CRUD
- [x] Sistema de exceções
- [x] Relatórios básicos
- [x] Compilação sem erros

**Próximas Entregas**
- [ ] Interface gráfica
- [ ] Aplicação executável
- [ ] Testes de integração
