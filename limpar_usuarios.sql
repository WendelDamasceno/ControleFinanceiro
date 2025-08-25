-- Script para remover dados dos usuários João e Maria, mantendo apenas Wendel
USE controle_financeiro;

-- Primeiro, vamos ver quais usuários existem
SELECT id, nome FROM usuario;

-- Remover transações dos usuários João e Maria (assumindo IDs baseados nos nomes)
DELETE FROM transacao WHERE usuario_id IN (
    SELECT id FROM usuario WHERE nome IN ('João', 'Maria', 'Joao', 'joao', 'maria', 'Joo')
);

-- Remover orçamentos (simplificado para evitar erro de subconsulta)
DELETE FROM orcamento WHERE categoria_id NOT IN (1,2,3,4,5,6,7,8,9,10);

-- Remover os próprios usuários João e Maria
DELETE FROM usuario WHERE nome IN ('João', 'Maria', 'Joao', 'joao', 'maria', 'Joo');

-- Garantir que Wendel tenha algumas transações de exemplo
-- Primeiro, obter o ID do usuário Wendel
SET @wendel_id = (SELECT id FROM usuario WHERE nome = 'Wendel' LIMIT 1);

-- Se Wendel não existir, criar
INSERT IGNORE INTO usuario (nome, senha, ativo) VALUES ('Wendel', 'senha123', TRUE);
SET @wendel_id = (SELECT id FROM usuario WHERE nome = 'Wendel' LIMIT 1);

-- Limpar transações existentes do Wendel e recriar com dados atualizados
DELETE FROM transacao WHERE usuario_id = @wendel_id;

-- Inserir transações de exemplo para Wendel
INSERT INTO transacao (descricao, valor, data_transacao, tipo, categoria_id, usuario_id, observacoes) VALUES
('Salário Janeiro', 5000.00, '2025-01-05', 'RECEITA', 8, @wendel_id, 'Salário mensal'),
('Supermercado', 250.00, '2025-01-10', 'DESPESA', 1, @wendel_id, 'Compras da semana'),
('Combustível', 120.00, '2025-01-12', 'DESPESA', 2, @wendel_id, 'Abastecimento do carro'),
('Aluguel', 1200.00, '2025-01-15', 'DESPESA', 3, @wendel_id, 'Aluguel mensal'),
('Freelance Web', 800.00, '2025-01-20', 'RECEITA', 9, @wendel_id, 'Desenvolvimento de site'),
('Cinema', 50.00, '2025-01-22', 'DESPESA', 6, @wendel_id, 'Filme com a família');

-- Verificar resultado final
SELECT 'Usuários restantes:' as info;
SELECT id, nome FROM usuario;

SELECT 'Transações por usuário:' as info;
SELECT u.nome, COUNT(t.id) as total_transacoes, SUM(CASE WHEN t.tipo = 'RECEITA' THEN t.valor ELSE 0 END) as total_receitas,
       SUM(CASE WHEN t.tipo = 'DESPESA' THEN t.valor ELSE 0 END) as total_despesas
FROM usuario u
LEFT JOIN transacao t ON u.id = t.usuario_id
GROUP BY u.id, u.nome;
