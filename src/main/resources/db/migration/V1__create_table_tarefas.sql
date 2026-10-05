CREATE TABLE IF NOT EXISTS tb_tarefas (
                                          id BIGSERIAL PRIMARY KEY,
                                          titulo VARCHAR(255) NOT NULL,
    descricao VARCHAR(255),
    concluida BOOLEAN NOT NULL DEFAULT FALSE
    );