CREATE TABLE User(
    id SERIAL PRIMARY KEY,
    name VARCHAR(20) NOT NULL,
    email VARCHAR(40) UNIQUE NOT NULL,
    phone VARCHAR(9) UNIQUE NOT NULL,
    username VARCHAR(20) UNIQUE NOT NULL,
    password VARCHAR(30) NOT NULL
);

CREATE TABLE Recipe(
    id SERIAL PRIMARY KEY,
    name VARCHAR(30) NOT NULL,
    preparation VARCHAR(100) NOT NULL,
    time TIME NOT NULL,
    type VARCHAR(15) NOT NULL,
    picture VARCHAR(100) NOT NULL,
    idUser INT NOT NULL,
    CONSTRAINT fk_user FOREIGN KEY (idUser) REFERENCES User(id)
);

CREATE TABLE Ingredient(
    id SERIAL PRIMARY KEY,
    name VARCHAR(20)
);

CREATE TABLE RecipeIngredient(
    idIng INT,
    idRec INT,
    CONSTRAINT pk_id PRIMARY KEY (idIng, idRec),
    CONSTRAINT fk_ing FOREIGN KEY (idIng) REFERENCES Ingredient(id),
    CONSTRAINT fk_rec FOREIGN KEY (idRec) REFERENCES Recipe(id)
);