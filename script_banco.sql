DROP DATABASE IF EXISTS controle_financeiro;
CREATE DATABASE controle_financeiro CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE controle_financeiro;

-- Tabela de usuários
CREATE TABLE usuario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    senha VARCHAR(255) NOT NULL,
    ativo BOOLEAN DEFAULT TRUE,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_nome (nome)
);

CREATE TABLE categoria(
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    descricao TEXT,
    ativo BOOLEAN DEFAULT TRUE,
    data_criacao TIMESTAMP DEFAULT  CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Tabela de transações
CREATE TABLE transacao(
    id INT AUTO_INCREMENT PRIMARY KEY,
    descricao VARCHAR(255) NOT NULL,
    valor DECIMAL(10,2) NOT NULL,
    data_transacao DATE NOT NULL,
    tipo ENUM('RECEITA', 'DESPESA') NOT NULL,
    categoria_id INT,
    usuario_id INT NOT NULL,
    observacoes TEXT,
    ativo BOOLEAN DEFAULT TRUE,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (categoria_id) REFERENCES categoria(id),
    FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    INDEX idx_data_transacao (data_transacao),
    INDEX idx_tipo (tipo),
    INDEX idx_categoria (categoria_id),
    INDEX idx_usuario (usuario_id)
);

-- Tabela de orçamento
CREATE TABLE orcamento (
    id INT AUTO_INCREMENT PRIMARY KEY,
    categoria_id INT,
    usuario_id INT NOT NULL,
    valor_limite DECIMAL (10,2) NOT NULL,
    mes INT NOT NULL CHECK ( mes BETWEEN 1 AND 12),
    ano INT NOT NULL CHECK ( ano >= 2020 ),
    descricao TEXT,
    ativo BOOLEAN DEFAULT TRUE,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (categoria_id) REFERENCES categoria(id),
    FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    UNIQUE KEY unique_orcamento (categoria_id, mes, ano, usuario_id),
    INDEX idx_periodo (mes, ano),
    INDEX idx_usuario (usuario_id)
);

-- ============================================================================
-- INSERÇÃO DE DADOS BÁSICOS (SEM TRANSAÇÕES DE EXEMPLO)
-- ============================================================================

-- Inserir apenas as categorias (obrigatórias para o funcionamento do sistema)
INSERT INTO categoria (nome, descricao) VALUES
('Alimentação', 'Gastos com alimentação e supermercado'),
('Transporte', 'Gastos com transporte e combustível'),
('Moradia', 'Gastos com aluguel, financiamento e contas da casa'),
('Saúde', 'Gastos com médicos, medicamentos e planos de saúde'),
('Educação', 'Gastos com cursos, livros e material escolar'),
('Lazer', 'Gastos com entretenimento e diversão'),
('Vestuário', 'Gastos com roupas e calçados'),
('Investimentos', 'Receitas com investimentos e aplicações'),
('Freelance', 'Receitas com trabalhos extras'),
('Outros', 'Outras categorias não especificadas');

-- ============================================================================
-- NENHUM USUÁRIO OU TRANSAÇÃO DE EXEMPLO É INSERIDO AQUI!
-- TODOS OS USUÁRIOS (INCLUINDO WENDEL) COMEÇARÃO COM DASHBOARD COMPLETAMENTE LIMPO
-- ============================================================================

-- Views para relatórios personalizados por usuário
CREATE VIEW vw_resumo_mensal AS
SELECT
    t.usuario_id,
    u.nome as usuario_nome,
    YEAR(data_transacao) as ano,
    MONTH(data_transacao) as mes,
    c.nome as categoria,
    t.tipo,
    SUM(t.valor) as total_valor,
    COUNT(*) as quantidade_transacoes
FROM transacao t
LEFT JOIN categoria c ON t.categoria_id = c.id
LEFT JOIN usuario u ON t.usuario_id = u.id
WHERE t.ativo = TRUE
GROUP BY t.usuario_id, YEAR(data_transacao), MONTH(data_transacao), c.nome, t.tipo
ORDER BY t.usuario_id, ano DESC, mes DESC, c.nome;

CREATE VIEW vw_saldo_atual AS
SELECT
    t.usuario_id,
    u.nome as usuario_nome,
    (SELECT COALESCE(SUM(valor), 0) FROM transacao WHERE tipo = 'RECEITA' AND ativo = TRUE AND usuario_id = t.usuario_id) as total_receitas,
    (SELECT COALESCE(SUM(valor), 0) FROM transacao WHERE tipo = 'DESPESA' AND ativo = TRUE AND usuario_id = t.usuario_id) as total_despesas,
    (SELECT COALESCE(SUM(valor), 0) FROM transacao WHERE tipo = 'RECEITA' AND ativo = TRUE AND usuario_id = t.usuario_id) -
    (SELECT COALESCE(SUM(valor), 0) FROM transacao WHERE tipo = 'DESPESA' AND ativo = TRUE AND usuario_id = t.usuario_id) as saldo_atual
FROM transacao t
LEFT JOIN usuario u ON t.usuario_id = u.id
GROUP BY t.usuario_id;

-- Procedure para relatórios de orçamento vs gasto real por usuário
DELIMITER //
CREATE PROCEDURE sp_relatorio_orcamento_vs_real(
    IN p_usuario_id INT,
    IN p_mes INT,
    IN p_ano INT
)
BEGIN
SELECT
    c.nome as categoria,
    o.valor_limite as orcamento,
    COALESCE(SUM(t.valor), 0) as gasto_real,
    o.valor_limite - COALESCE(SUM(t.valor), 0) as diferenca,
    CASE
        WHEN COALESCE(SUM(t.valor), 0) > o.valor_limite THEN 'ACIMA DO ORÇAMENTO'
        WHEN COALESCE(SUM(t.valor), 0) = o.valor_limite THEN 'NO LIMITE'
        ELSE 'DENTRO DO ORÇAMENTO'
        END as status
FROM orcamento o
LEFT JOIN categoria c ON o.categoria_id = c.id
LEFT JOIN transacao t ON t.categoria_id = o.categoria_id
    AND t.usuario_id = o.usuario_id
    AND MONTH(t.data_transacao) = p_mes
    AND YEAR(t.data_transacao) = p_ano
    AND t.tipo = 'DESPESA'
    AND t.ativo = TRUE
WHERE o.usuario_id = p_usuario_id
    AND o.mes = p_mes
    AND o.ano = p_ano
    AND o.ativo = TRUE
GROUP BY c.nome, o.valor_limite, o.id
ORDER BY diferenca ASC;
END //
DELIMITER ;

-- ============================================================================
-- VERIFICAÇÕES FINAIS
-- ============================================================================

SELECT 'Banco de dados criado com DASHBOARD LIMPO para todos os usuários!' as status;
SELECT COUNT(*) as total_categorias FROM categoria;
SELECT COUNT(*) as total_usuarios FROM usuario;
SELECT COUNT(*) as total_transacoes FROM transacao;
SELECT COUNT(*) as total_orcamentos FROM orcamento;

-- Mensagem final
SELECT '✅ CONFIRMADO: Todos os usuários (incluindo Wendel) começarão com dashboard completamente vazio!' as confirmacao;
