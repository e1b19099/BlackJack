CREATE TABLE card (
    id IDENTITY,
    suit CHAR NOT NULL,
    number INT NOT NULL
);
CREATE TABLE userinfo (
    id INT NOT NULL PRIMARY KEY,
    user CHAR NOT NULL ,
    chip INT NOT NULL
);
