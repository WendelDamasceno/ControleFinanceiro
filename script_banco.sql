DROP DATABASE IF EXISTS controle_financeiro;
CREATE DATABASE controle_financeiro CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE controle_financeiro;

CREATE TABLE categoria(
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    descricao TEXT,
    ativo BOOLEAN DEFAULT TRUE,
    data_criacao TIMESTAMP DEFAULT  CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Tabela de transações
-- REgistar cada movimentação financeira, seja uma entrada (receita) ou uma saída (despsa)
CREATE TABLE transacao(
    id INT AUTO_INCREMENT PRIMARY KEY,
    descricao VARCHAR(255) NOT NULL,
    valor DECIMAL(10,2) NOT NULL,
    data_transacao DATE NOT NULL,
    tipo ENUM('RECEITA', 'DESPESA') NOT NULL,
    categoria_id INT,
    observacoes TEXT,
    ativo BOOLEAN DEFAULT TRUE,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (categoria_id) REFERENCES categoria(id),
    INDEX idx_data_transacao (data_transacao),
    INDEX idx_tipo (tipo),
    INDEX idx_categoria (categoria_id)
);

-- Tabale de orçamento
-- Permite q o usuário defina um limete de gastos
CREATE TABLE orcamento (
    id INT AUTO_INCREMENT PRIMARY KEY,
    categoria_id INT,
    valor_limite DECIMAL (10,2) NOT NULL,
    mes INT NOT NULL CHECK ( mes BETWEEN 1 AND 12),
    ano INT NOT NULL CHECK ( ano >= 2020 ),
    descricao TEXT,
    ativo BOOLEAN DEFAULT TRUE,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (categoria_id) REFERENCES categoria(id),
    UNIQUE KEY unique_orcamento (categoria_id, mes, ano),
    INDEX idx_periodo (mes, ano)
);

INSERT INTO categoria (nome, descricao) VALUES
('Alimentação', 'Gastos com alimentação e supermercado'),
('Transporte', 'Gastos com transporte público, combustível, manutenção de veículos'),
('Moradia', 'Aluguel, financiamento, condomínio, contas de casa'),
('Saúde', 'Medicamentos, consultas médicas, plano de saúde'),
('Educação', 'Cursos, livros, material escolar'),
('Lazer', 'Entretenimento, cinema, viagens, restaurantes'),
('Roupas e Acessórios', 'Vestuário e acessórios pessoais'),
('Salário', 'Salário e benefícios do trabalho'),
('Freelance', 'Trabalhos autônomos e extras'),
('Investimentos', 'Rendimentos de investimentos'),
('Outros Ganhos', 'Outras fontes de receita'),
('Outros Gastos', 'Despesas diversas não categorizadas');


INSERT INTO transacao (descricao, valor, data_transacao, tipo, categoria_id) VALUES
('Salário Janeiro', 5000.00, '2025-01-05', 'RECEITA', 8),
('Supermercado', 350.00, '2025-01-10', 'DESPESA', 1),
('Combustível', 120.00, '2025-01-12', 'DESPESA', 2),
('Aluguel Janeiro', 1200.00, '2025-01-15', 'DESPESA', 3),
('Freelance - Website', 800.00, '2025-01-20', 'RECEITA', 9);

CREATE VIEW vw_resumo_mensal AS
SELECT
        YEAR(data_transacao) as ano,
        MONTH(data_transacao) as mes,
        c.nome as categoria,
        t.tipo,
        SUM(t.valor) as total_valor,
        COUNT(*) as quantidade_transacoes
        FROM transacao t
        LEFT JOIN categoria c ON t.categoria_id = c.id
        WHERE t.ativo = TRUE
        GROUP BY YEAR(data_transacao), MONTH(data_transacao), c.nome, t.tipo
        ORDER BY ano DESC, mes DESC, c.nome;

CREATE VIEW vw_saldo_atual AS
SELECT
    (SELECT COALESCE(SUM(valor), 0) FROM transacao WHERE tipo = 'RECEITA' AND ativo = TRUE) as total_receitas,
    (SELECT COALESCE(SUM(valor), 0) FROM transacao WHERE tipo = 'DESPESA' AND ativo = TRUE) as total_despesas,
    (SELECT COALESCE(SUM(valor), 0) FROM transacao WHERE tipo = 'RECEITA' AND ativo = TRUE) -
    (SELECT COALESCE(SUM(valor), 0) FROM transacao WHERE tipo = 'DESPESA' AND ativo = TRUE) as saldo_atual;


DELIMITER //
CREATE PROCEDURE sp_relatorio_orcamento_vs_real(
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
    AND MONTH(t.data_transacao) = p_mes
    AND YEAR(t.data_transacao) = p_ano
    AND t.tipo = 'DESPESA'
    AND t.ativo = TRUE
WHERE o.mes = p_mes AND o.ano = p_ano AND o.ativo = TRUE
GROUP BY c.nome, o.valor_limite, o.id
ORDER BY diferenca ASC;
END //
DELIMITER ;

SELECT 'Banco de dados criado com sucesso!' as status;
SELECT COUNT(*) as total_categorias FROM categoria;
SELECT COUNT(*) as total_transacoes FROM transacao;
SELECT COUNT(*) as total_orcamentos FROM orcamento;