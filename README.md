# Sistema de Controle Financeiro

## 📋 Descrição
Sistema de controle financeiro pessoal desenvolvido em Java como projeto da disciplina de **Programação Orientada a Objetos (POO)** do **IFBA**. Esta é a **primeira parte do projeto**, focando na estrutura backend e modelagem de dados.

## 🎯 Objetivos da Disciplina
- Aplicar conceitos de Programação Orientada a Objetos
- Implementar padrões de projeto (DAO, MVC)
- Trabalhar com persistência de dados usando JDBC

## 🏗️ Arquitetura do Projeto

### Padrões Implementados
- **MVC (Model-View-Controller)**: Separação clara de responsabilidades
- **DAO (Data Access Object)**: Abstração da camada de acesso a dados
- **Factory Pattern**: Para conexões com banco de dados

### Estrutura de Pacotes
```
com.controlfinanceiro/
├── model/          # Entidades e enums
├── dao/            # Interfaces e implementações DAO
├── controller/     # Controladores da aplicação
├── view/           # Interface gráfica (a ser implementada)
├── util/           # Classes utilitárias
└── exception/      # Exceções customizadas
```

## 📊 Modelo de Dados

### Entidades Principais
- **Categoria**: Classificação das transações
- **Transacao**: Registro de receitas e despesas
- **Orcamento**: Controle de limites de gastos por categoria

### Banco de Dados
- **SGBD**: MySQL 8.0+
- **Encoding**: UTF-8 (utf8mb4)
- **Script**: `script_banco.sql` com estrutura completa

## 🛠️ Tecnologias Utilizadas

### Backend
- **Java 21** - Linguagem principal
- **Maven** - Gerenciamento de dependências
- **MySQL Connector** - Conexão com banco de dados
- **Apache Commons Lang** - Utilitários

### Interface (Planejada)
- **Java Swing** - Interface gráfica desktop


### Logging e Serialização
- **Logback** - Sistema de logs
- **Jackson** - Processamento JSON (futuras integrações)

## 📁 Funcionalidades Implementadas (Backend)

### ✅ Estrutura Completa
- [x] Modelos de dados com validações
- [x] DAOs com operações CRUD
- [x] Controladores com regras de negócio
- [x] Sistema de exceções customizadas
- [x] Utilitários para formatação e conexão
- [x] Configuração Maven completa
- [x] Script de banco com dados de exemplo

### 🔄 Em Desenvolvimento
- [ ] Interface gráfica (View layer)
- [ ] Classe principal funcional
- [ ] Integração completa das camadas

## 🗃️ Estrutura do Banco de Dados

### Tabelas
1. **categoria** - Categorias das transações
2. **transacao** - Registro de movimentações financeiras
3. **orcamento** - Limites de gastos por categoria/mês

## 🚀 Como Executar

### Pré-requisitos
- Java 21+
- Maven 3.8+
- MySQL 8.0+

## 📋 Status do Projeto

### Primeira Entrega (Atual)
- ✅ Modelagem completa das entidades
- ✅ Implementação da camada DAO
- ✅ Controladores com regras de negócio
- ✅ Sistema de exceções
- ✅ Estrutura de banco de dados
- ✅ Configuração Maven

### Próximas Etapas
- 🔄 Desenvolvimento da interface gráfica
- 🔄 Integração View-Controller
- 🔄 Implementação de relatórios
- 🔄 Validações de formulário
- 🔄 Sistema de backup/restore

**Nota**: Esta é a primeira versão do projeto, com foco na estrutura backend. A interface gráfica será implementada nas próximas etapas.
