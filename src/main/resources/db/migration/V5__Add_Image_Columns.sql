-- Adiciona suporte a imagem de avatar no usuário
ALTER TABLE users ADD COLUMN avatar_path VARCHAR(255);

-- Adiciona suporte a imagem de capa no post
ALTER TABLE posts ADD COLUMN image_path VARCHAR(255);
