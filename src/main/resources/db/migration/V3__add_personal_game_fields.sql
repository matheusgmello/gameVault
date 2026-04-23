ALTER TABLE jogo
    ADD COLUMN status varchar(30) NOT NULL DEFAULT 'WISHLIST',
    ADD COLUMN favorito boolean NOT NULL DEFAULT false,
    ADD COLUMN review text,
    ADD COLUMN horas_jogadas integer NOT NULL DEFAULT 0;
