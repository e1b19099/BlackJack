CREATE TABLE card (
    id IDENTITY,
    suit CHAR NOT NULL,
    number INT NOT NULL
);
CREATE TABLE userinfo (
    user_id IDENTITY,
    username CHAR NOT NULL,
    password CHAR,
    chip INT NOT NULL
);
CREATE TABLE room (
  room_id IDENTITY,
  room_name CHAR,
  deck_id INT,
  date date,
  winner INT
);

CREATE TABLE deal (
  room_id INT,
  user_id INT,
  deal_id INT NOT NULL,
  id INT,
  PRIMARY KEY (room_id,user_id,deal_id),
  FOREIGN KEY (id) REFERENCES card(id)
);

CREATE TABLE RoomUser (
  room_id INT,
  user_id INT,
  deal_id INT,
  PRIMARY KEY (room_id,user_id),
  FOREIGN KEY (room_id) REFERENCES room(room_id),
  FOREIGN KEY (user_id) REFERENCES userinfo(user_id),
  FOREIGN KEY (deal_id) REFERENCES deal(deal_id)
);

CREATE TABLE Deck (
  deck_id INT,
  deal_card INT,
  id INT,
  PRIMARY KEY (deck_id,deal_card),
  FOREIGN KEY (id) REFERENCES card(id)
);
