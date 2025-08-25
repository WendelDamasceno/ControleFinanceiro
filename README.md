# 💰 Sistema de Controle Financeiro

Sistema completo de controle financeiro pessoal desenvolvido para a disciplina de **Programação Orientada a Objetos (POO)** do **Instituto Federal da Bahia (IFBA)**.

## 📋 Descrição

Sistema de gestão financeira pessoal com interface gráfica moderna, permitindo controle completo de receitas, despesas, usuários e geração de relatórios detalhados. O projeto utiliza Java com Maven, MySQL e arquitetura MVC com interface Swing.

## ✨ Funcionalidades Implementadas

### 🔐 Sistema de Usuários
- **Login/Logout**: Autenticação segura de usuários
- **Cadastro de Usuários**: Tela moderna para registro de novos usuários
- **Sessão de Usuário**: Isolamento de dados por usuário logado
- **Validações**: Sistema robusto de validação de dados

### 🏠 Dashboard Interativo
- **Visão Geral**: Cards com resumo financeiro em tempo real
- **Saldo do Mês**: Cálculo automático de receitas - despesas
- **Patrimônio Total**: Saldo acumulado de todas as transações
- **Transações Recentes**: Lista das últimas 5 movimentações
- **Guia para Novos Usuários**: Interface especial para primeiros passos
- **Resumo Rápido**: Widget lateral com saldo e estatísticas

### 💰 Gestão de Receitas
- **Cadastro Completo**: Formulário moderno para receitas
- **Salário Mensal**: Modo especial para receitas recorrentes
- **Categorização**: Organização por categorias
- **Histórico**: Tabela com todas as receitas cadastradas
- **Edição/Exclusão**: CRUD completo com validações

### 💸 Gestão de Despesas
- **Registro de Gastos**: Interface intuitiva para despesas
- **Despesas Fixas**: Controle de gastos recorrentes
- **Categorias**: Classificação por tipo de despesa
- **Observações**: Campo para detalhes adicionais
- **Controle Temporal**: Filtros por data e período

### 📊 Relatórios e Análises
- **Relatórios por Período**: Análises mensais e anuais
- **Comparativos**: Receitas vs Despesas
- **Análise por Categoria**: Detalhamento de gastos
- **Exportação**: Geração de relatórios para arquivo
- **Gráficos**: Visualizações dos dados financeiros

### 🎨 Interface Gráfica Moderna
- **Design Profissional**: Tema azul moderno e limpo
- **Navegação Intuitiva**: Menu lateral com indicadores visuais
- **Responsividade**: Layout adaptável e componentes organizados
- **Feedback Visual**: Atualizações em tempo real
- **UX Aprimorada**: Experiência de usuário otimizada

## 🏗️ Arquitetura

O projeto segue rigorosamente o padrão **MVC (Model-View-Controller)**:

### Model (Modelos)
- `Usuario.java` - Dados dos usuários do sistema
- `Transacao.java` - Receitas e despesas
- `Categoria.java` - Categorias de transações
- `Orcamento.java` - Planejamento financeiro

### View (Interface Gráfica)
- `TelaLogin.java` - Tela de autenticação
- `TelaCadastroUsuario.java` - Registro de novos usuários
- `TelaPrincipal.java` - Dashboard principal com navegação
- `CadastroReceita.java` - Gestão de receitas
- `CadastroDespesa.java` - Gestão de despesas
- `GerarRelatorio.java` - Interface de relatórios

### Controller (Lógica de Negócio)
- `UsuarioController.java` - Autenticação e cadastro
- `TransacaoController.java` - Gestão de receitas/despesas
- `DashboardController.java` - Estatísticas e resumos
- `CategoriaController.java` - Gestão de categorias
- `RelatorioController.java` - Geração de relatórios

### DAO (Acesso a Dados)
- Interfaces: `UsuarioDAO`, `TransacaoDAO`, `CategoriaDAO`
- Implementações: Todas as classes `*DAOImpl.java`
- Padrão Repository com CRUD completo

### Utilitários
- `SessaoUsuario.java` - Gerenciamento de sessão
- `ConnectionFactory.java` - Conexão com banco
- `FormatUtils.java` - Formatação de valores
- `FocusManager.java` - Controle de foco da interface

## 🛠️ Tecnologias

- **Java 21** - Linguagem principal
- **Maven** - Gerenciamento de dependências e build
- **MySQL 8.0+** - Banco de dados relacional
- **Swing** - Interface gráfica nativa
- **JDBC** - Conectividade com banco
- **SLF4J + Logback** - Sistema de logs profissional
- **JUnit** - Testes unitários

## ⚙️ Configuração e Instalação

### 1. Pré-requisitos
- **Java 21** ou superior
- **Maven 3.6+**
- **MySQL 8.0+**
- **Git** (para clonar o repositório)

### 2. Instalação
```bash
# 1. Clone o repositório
git clone https://github.com/seu-usuario/ControleFinanceiro.git
cd ControleFinanceiro

# 2. Configure o banco de dados
mysql -u root -p < script_banco.sql

# 3. Configure as credenciais
cp src/main/resources/database.properties.template src/main/resources/database.properties
# Edite o arquivo com suas credenciais MySQL

# 4. Compile e execute
mvn clean compile
mvn exec:java -Dexec.mainClass="com.controlfinanceiro.App"
```

### 3. Configuração do Banco

#### Arquivo de Configuração (Desenvolvimento)
```properties
db.url=jdbc:mysql://localhost:3306/controle_financeiro?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
db.username=root
db.password=SUA_SENHA_AQUI
db.driver=com.mysql.cj.jdbc.Driver
```

#### Variáveis de Ambiente (Produção)
```bash
export DB_URL="jdbc:mysql://localhost:3306/controle_financeiro?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
export DB_USERNAME="root"
export DB_PASSWORD="sua_senha_aqui"
```

## 🗄️ Estrutura do Banco

### Tabelas Principais
- **usuarios**: Dados de autenticação e perfil
- **categorias**: Tipos de transações (Alimentação, Transporte, etc.)
- **transacoes**: Registros de receitas e despesas
- **orcamentos**: Planejamento e limites de gastos

### Dados Iniciais
O sistema inclui:
- 11 categorias pré-definidas
- Dados de exemplo para demonstração
- Estrutura completa para multi-usuário

## 🚀 Como Usar

### Primeiro Acesso
1. **Execute a aplicação**: `mvn exec:java -Dexec.mainClass="com.controlfinanceiro.App"`
2. **Cadastre-se**: Clique em "Cadastrar" na tela de login
3. **Preencha os dados**: Nome, email e senha
4. **Faça login**: Use suas credenciais criadas

### Fluxo de Uso
1. **Dashboard**: Visualize resumo financeiro
2. **Receitas**: Cadastre salários e outras rendas
3. **Despesas**: Registre todos os gastos
4. **Relatórios**: Analise seu desempenho financeiro

### Funcionalidades Especiais
- **Atualização Automática**: Saldos atualizados em tempo real
- **Categorização Inteligente**: Organize por tipos de gasto
- **Multi-usuário**: Cada pessoa tem dados isolados
- **Interface Responsiva**: Layout adaptável

## 📊 Capturas de Tela

### Tela de Login
Interface moderna com campos de autenticação e opção de cadastro.

### Dashboard Principal
Visão geral com cards informativos, resumo lateral e transações recentes.

### Gestão de Transações
Formulários completos para receitas e despesas com validações.

### Sistema de Relatórios
Análises detalhadas com filtros e exportação de dados.

## 🔒 Segurança

- **Autenticação**: Sistema seguro de login/logout
- **Isolamento**: Dados separados por usuário
- **Validações**: Entrada de dados rigorosamente validada
- **Configuração Segura**: Credenciais não versionadas (`.gitignore`)

## 🧪 Testes e Qualidade

```bash
# Executar testes unitários
mvn test

# Compilar sem erros
mvn clean compile

# Executar aplicação
mvn exec:java -Dexec.mainClass="com.controlfinanceiro.App"
```

### Cobertura de Testes
- Testes de controllers
- Validação de DAOs
- Testes de integração com banco
- Verificação de regras de negócio

## 📁 Estrutura Completa

```
src/main/java/com/controlfinanceiro/
├── App.java                    # Classe principal
├── controller/                 # Camada de controle
│   ├── UsuarioController.java  # Autenticação
│   ├── TransacaoController.java # Receitas/Despesas
│   ├── DashboardController.java # Estatísticas
│   └── ...
├── dao/                        # Acesso a dados
│   ├── UsuarioDAO.java
│   ├── TransacaoDAO.java
│   └── impl/                   # Implementações
├── model/                      # Entidades
│   ├── Usuario.java
│   ├── Transacao.java
│   └── enums/                  # Enumerações
├── view/                       # Interface gráfica
│   ├── TelaLogin.java          # Login
│   ├── TelaPrincipal.java      # Dashboard
│   ├── CadastroReceita.java    # Receitas
│   ├── CadastroDespesa.java    # Despesas
│   └── GerarRelatorio.java     # Relatórios
├── util/                       # Utilitários
│   ├── SessaoUsuario.java      # Sessão
│   ├── ConnectionFactory.java  # Banco
│   └── FormatUtils.java        # Formatação
└── exception/                  # Exceções customizadas
```

## 📈 Status do Projeto

### ✅ CONCLUÍDO
- [x] **Sistema de Usuários** - Login, cadastro, sessão
- [x] **Interface Gráfica Completa** - Todas as telas implementadas
- [x] **Dashboard Interativo** - Cards, resumos, estatísticas
- [x] **Gestão de Receitas** - CRUD completo
- [x] **Gestão de Despesas** - CRUD completo
- [x] **Sistema de Relatórios** - Análises e exportação
- [x] **Banco de Dados** - Estrutura completa
- [x] **Validações** - Sistema robusto
- [x] **Arquitetura MVC** - Implementação completa
- [x] **Sistema de Logs** - Logback configurado
- [x] **Aplicação Executável** - Totalmente funcional

### 🎯 Objetivos Pedagógicos Alcançados
- ✅ Programação Orientada a Objetos
- ✅ Padrão MVC (Model-View-Controller)
- ✅ Interface gráfica com Swing
- ✅ Persistência com JDBC/MySQL
- ✅ Tratamento de exceções
- ✅ Validação de dados
- ✅ Arquitetura em camadas
- ✅ Boas práticas Java

