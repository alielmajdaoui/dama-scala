# Scala Bootcamp 2022 Course Project at Evolution

The Italian Draughts—`Dama Italiana` written in Scala using functional programming skills taught in the bootcamp.

## Features

- Users play vs. the backend.
- Users registration.
- Top players.

## Technologies

### Backend

- Scala 2
- Cats effect
- http4s
- circe
- If needed, other libs taught in the bootcamp;

### Frontend

- Typescript
- Redux
- socket.io

### Notes

1. I might consider adding logic for multiplayer feature if I could meet the deadline.

## Italian Draughts

Is a board game for two players. It involves diagonal moves of uniform game pieces and compulsory captures by jumping over opponent pieces.

https://en.wikipedia.org/wiki/Italian_draughts

### Game Components

#### The Board

The board consists of 64 squares, 32 black and 32 white.

The rightmost square on both sides is black.

#### The Pieces

The game starts with 24 pieces, 12 black and 12 white.

In the `Dama Italiana` context, we have 2 kind of pieces, regardless to their color, that are:

- Men or `pedine` (singular: `pedina`)
- Kings or `dame` (singular: `dama`)

### Gameplay and Rules

- White always moves first.
- Men move 1 square diagonally.
- When men reach the file farthest from the player to which they belong, they become kings.
- Kings can move forward and back 1 square, again only diagonally.
- When a man is found neighbouring an opposing piece behind that is an empty position, the player is obligated to take this empty position and remove the opposite man from the board.
- Men may only capture diagonally forward, and can capture a maximum of three pieces in a row.
- Kings move, as well as capture, backwards.
- Kings can't be captured by men but only by other kings.
- A player wins when he has succeeded in capturing all of his opponent's pieces.

### Capturing Rules

- If a player is faced with the prospect of choosing which captures to make, the first and foremost rule to obey is to capture the greatest quantity of pieces.
- If a player may capture an equal number of pieces with either a man or king, he must do so with the king.
- If a player may capture an equal number of pieces with a king, in which one or more options contain a number of kings, he must capture the greatest number of kings possible.
- If a player may capture an equal number of pieces (each series containing a king) with a king, he must capture wherever the king occurs first.
- If none of these rules apply to the situation at hand, the player may choose according to his tactical requirements.
