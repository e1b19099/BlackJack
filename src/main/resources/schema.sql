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
  date date,
  isCPU boolean,
  winner INT
);

CREATE TABLE RoomUser (
  room_id INT,
  user_id INT,
  deal INT,
  DealCardID INT,
  total INT default (0),
  PRIMARY KEY(room_id,user_id),
  FOREIGN KEY (room_id) REFERENCES room(room_id),
  FOREIGN KEY (user_id) REFERENCES userinfo(user_id)
);
