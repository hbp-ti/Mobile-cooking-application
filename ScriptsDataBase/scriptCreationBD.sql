CREATE TABLE Users(
    id SERIAL PRIMARY KEY,
    name VARCHAR(40) NOT NULL,
    email VARCHAR(40) UNIQUE NOT NULL,
    username VARCHAR(20) UNIQUE NOT NULL,
    password VARCHAR(300) NOT NULL
);

CREATE TABLE Recipe(
    id SERIAL PRIMARY KEY,
    name VARCHAR(30) NOT NULL,
    preparation VARCHAR(600) NOT NULL,
    prepTime Int NOT NULL,
    type VARCHAR(15) NOT NULL,
    picture text NOT NULL,
    ingredients VARCHAR(300) NOT NULL
);

CREATE TABLE SavedRecipe(
    id SERIAL PRIMARY KEY,
    name VARCHAR(30) NOT NULL,
    preparation VARCHAR(600) NOT NULL,
    prepTime Int NOT NULL,
    type VARCHAR(15) NOT NULL,
    picture text NOT NULL,
    ingredients VARCHAR(300) NOT NULL,
    idUser INT NOT NULL,
    idRec INT NOT NULL,

    CONSTRAINT fk_recipeSaved FOREIGN KEY (idRec) REFERENCES Recipe(id),
    CONSTRAINT fk_user FOREIGN KEY (idUser) REFERENCES Users(id)
);


