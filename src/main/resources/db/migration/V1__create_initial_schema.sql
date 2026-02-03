CREATE TABLE genero (
    id serial PRIMARY KEY,
    nome varchar(100) NOT NULL
);

CREATE TABLE plataforma (
    id serial PRIMARY KEY,
    nome varchar(100) NOT NULL
);

CREATE TABLE jogo (
  id serial PRIMARY KEY,
  titulo varchar(255) NOT NULL,
  descricao text,
  data_lancamento date,
  nota numeric,
  criado_em timestamp,
  atualizado_em timestamp
);

CREATE TABLE jogo_genero (
     jogo_id INTEGER,
     genero_id INTEGER,
     CONSTRAINT fk_jogo_genero_jogo FOREIGN KEY(jogo_id) REFERENCES jogo(id),
     CONSTRAINT fk_jogo_genero_genero FOREIGN KEY(genero_id) REFERENCES genero(id)
);

CREATE TABLE jogo_plataforma (
    jogo_id INTEGER,
    plataforma_id INTEGER,
    CONSTRAINT fk_jogo_plataforma_jogo FOREIGN KEY(jogo_id) REFERENCES jogo(id),
    CONSTRAINT fk_jogo_plataforma_plataforma FOREIGN KEY(plataforma_id) REFERENCES plataforma(id)
);

CREATE TABLE usuario (
 id serial PRIMARY KEY,
 nome varchar(255) NOT NULL,
 email varchar(255) NOT NULL UNIQUE,
 senha varchar(255) NOT NULL
);