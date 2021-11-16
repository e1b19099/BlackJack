CREATE TABLE card (
    id IDENTITY,
    suit CHAR NOT NULL,
    number INT NOT NULL
);
CREATE TABLE userinfo (
    id INT NOT NULL PRIMARY KEY,
    username CHAR NOT NULL,
    password CHAR,
    chip INT NOT NULL
);
CREATE TABLE room (
  date date,
  winner INT
);

CREATE TABLE RoomUser (
  id INT,
  MatchID INT,
  deal INT,
  DealCardID INT
);
