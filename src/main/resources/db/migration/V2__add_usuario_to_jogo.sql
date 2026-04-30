ALTER TABLE jogo
    ADD COLUMN usuario_id INTEGER;

ALTER TABLE jogo
    ADD CONSTRAINT fk_jogo_usuario
    FOREIGN KEY (usuario_id) REFERENCES usuario(id);
