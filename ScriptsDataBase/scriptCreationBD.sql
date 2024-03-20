CREATE OR REPLACE FUNCTION createBD()
RETURNS VOID AS $$
BEGIN

    CREATE TABLE Favorite(
        id SERIAL PRIMARY KEY
    );

    CREATE TABLE User(
        id SERIAL PRIMARY KEY,
        name VARCHAR(20) NOT NULL,
        email VARCHAR(40) UNIQUE NOT NULL,
        phone VARCHAR(9) NOT NULL,
        username VARCHAR(20) UNIQUE NOT NULL,
        password VARCHAR(30) NOT NULL,
        idFav INT NOT NULL,

        CONSTRAINT fk_idFav FOREIGN KEY (idFav) REFERENCES Favorite(id)
    );

    CREATE TABLE Collection(
        id SERIAL PRIMARY KEY,
        name VARCHAR(10) UNIQUE NOT NULL,
        description VARCHAR(30) NOT NULL,
        creationDate DATE NOT NULL,
        idUser INT NOT NULL,

        CONSTRAINT fk_user FOREIGN KEY (idUser) REFERENCES User(id)
    );

    CREATE TABLE Recipe(
        id SERIAL PRIMARY KEY,
        name VARCHAR(30) NOT NULL,
        description VARCHAR(30) NOT NULL,
        -- SEE WHAT TO DO ABOUT THIS ATRIBUT MB ASK THE TEACHER ingredientes VARCHAR(40) NOT NULL,
        instructions VARCHAR(100) NOT NULL,
        time TIME NOT NULL,
        type VARCHAR(15) NOT NULL,
        picture VARCHAR(100) NOT NULL
    );

    CREATE TABLE RecipeFavorite(
        idRecipe INT,
        idFav INT,

        CONSTRAINT primKey PRIMARY KEY (idFav, idRecipe),
        CONSTRAINT fk_idfav FOREIGN KEY (idFav) REFERENCES Favorite(id),
        CONSTRAINT fk_idRec FOREIGN KEY (idRecipe) REFERENCES Recipe(id)
    );

    CREATE TABLE RecipeCollection(
        idRecipe INT,
        idColl INT,

        CONSTRAINT primKey PRIMARY KEY (idRecipe, idColl),
        CONSTRAINT fk_Rec FOREIGN KEY (idRecipe) REFERENCES Recipe(id),
        CONSTRAINT fk_coll FOREIGN KEY (idColl) REFERENCES Collection(id)
    );

END;
$$ LANGUAGE plpgsql;
