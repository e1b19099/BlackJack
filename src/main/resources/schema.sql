CREATE TABLE card (
    id IDENTITY,
    suit CHAR NOT NULL,
    number INT NOT NULL
);
CREATE TABLE userinfo (
    user_id IDENTITY,
    username CHAR NOT NULL,
    password CHAR,
    chip INT default(500)
);
CREATE TABLE room (
  room_id IDENTITY,
  room_name CHAR,
  limits INT,
  deck_id INT,
  date date,
  winner INT,
  turn INT default(-1)
);

CREATE TABLE RoomUser (
  room_id INT,
  user_id INT,
  deal_id INT AUTO_INCREMENT,
  use_Chip INT default(0),
  time timestamp,
  PRIMARY KEY (room_id,user_id),
  FOREIGN KEY (room_id) REFERENCES room(room_id),
  FOREIGN KEY (user_id) REFERENCES userinfo(user_id)
);

CREATE TABLE deal (
  deal_id INT NOT NULL,
  deal_number INT,
  id INT,
  PRIMARY KEY (deal_id,deal_number),
  FOREIGN KEY (id) REFERENCES card(id),
  FOREIGN KEY (deal_id) REFERENCES RoomUser(deal_id)
);

CREATE TABLE Deck (
  deck_id INT,
  deal_card INT,
  id INT,
  PRIMARY KEY (deck_id,deal_card),
  FOREIGN KEY (id) REFERENCES card(id)
);
