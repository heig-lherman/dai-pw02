# Practical Work 2 - Chess server

This is a CLI that allows you to launch a chess server and a chess client.

## Dependencies

### Lombok

This project uses [Lombok](https://projectlombok.org/) to generate boilerplate code. 
When opening this project in IntelliJ, annotation processing is recommended.
Maven will compile the project without any additional configuration.

## Build the JAR

To build the JAR, run the following command from the root of the repository:

```shell
./mvnw clean package
```

## Running

To run the JAR, run the following command from the root of the repository:

```shell
java -jar target/pw-mp-chess-1.0.0-SNAPSHOT.jar <command>
```

## Usage

The CLI methods are documented in the form of usage messages, given using the `--help` flag.

```shell
java -jar target/pw-mp-chess-1.0.0-SNAPSHOT.jar --help
```

### Commands

The CLI supports the following commands:

- `server`: Creates a server instance, ready to accept connections.
- `client`: Creates a client instance, expects the server to be available first.

#### Server

The server will only handle one game at a time in this implementation of the protocol.
If two clients connect, they will be able to play together.

The server command has one argument, the port to listen on. 
By default, it is the protocol's standard port, which is 6343.

The server will output the state of the game in its standard output.

#### Client

The client command has two arguments, the host and the port to connect to.
The host is defaulted to localhost for convenience, but can be changed to connect to a remote server.
The port is defaulted to the protocol's standard port, which is 6343.

The client starts a GUI that allows the user to play the game.

### Example

Let's create a server instance on the default port.

```shell
java -jar target/pw-mp-chess-1.0.0-SNAPSHOT.jar server
```

The server will add a log line when it is ready to accept connections.

Now we need to start two clients, one for each player.

```shell
java -jar target/pw-mp-chess-1.0.0-SNAPSHOT.jar client &
java -jar target/pw-mp-chess-1.0.0-SNAPSHOT.jar client &
```

The clients will connect to the server and start a game.
Two GUIs will open, one for each player.

In this basic implementation, the server will shut down when the game is over.

## Protocol

> [!NOTE]
> The complete version of the protocol is available in [this document](./doc/protocol.pdf).

### Server to client

* ```COLOR <color>``` :  The server sends the color of the player to the client when there are two players connected.
    * ```<color>``` : WHITE or BLACK
* ```MOVE <fromX> <fromY> <toX> <toY>``` : The server sends the move of the opponent to the client. All coordinates must
be between 0 and 7.
    * ```<fromX>``` : The X coordinate of the piece to move.
    * ```<fromY>``` : The Y coordinate of the piece to move.
    * ```<toX>``` : The X coordinate of the destination.
    * ```<toY>``` : The Y coordinate of the destination.
* ```PROMOTION <ordinalPieceType> <toX> <toY>``` : The server sends the promotion of the opponent to the client. All
coordinates must be between 0 and 7. Will only be sent to the server if there is a move before.
    * ```<ordinalPieceType>``` : The ordinal of the piece type.
      *  ```1``` : Rook
      *  ```2``` : Knight
      *  ```3``` : Bishop
      *  ```4``` : Queen
    * ```<toX>``` : The X coordinate of the destination.
    * ```<toY>``` : The Y coordinate of the destination.
* ```REPLAY <answer>``` : The server sends "Yes" to the client if both players want to play again, otherwise "No".
    * ```<answer>``` : Yes or No
* ```ERROR <errorOrdinal>``` : The client will create an error message with the error type. The received message will
be replaced by the error message. The message will not be sent to the server.
    * ```<errorOrdinal>``` : The ordinal of the error type.
      *  ```1``` : INVALID_MESSAGE - Shown if the message is not the spectated one or if the message is not in the list.
      *  ```2``` : INVALID_NBR_ARGUMENTS - Shown if the number of arguments is not the expected one.
      *  ```3``` : INVALID_MOVE - Shown if the move is not valid.
      *  ```4``` : INVALID_PROMOTION - Shown if the piece type is not valid.
      *  ```5``` : INVALID_REPLAY - Shown if the replay answer is not valid.
      *  ```6``` : INVALID_COLOR - Shown if the color is not valid.

### Client to server
* ```MOVE <fromX> <fromY> <toX> <toY>``` : The client sends the that he has done to the server. All coordinates must be
between 0 and 7.
    * ```<fromX>``` : The X coordinate of the piece to move.
    * ```<fromY>``` : The Y coordinate of the piece to move.
    * ```<toX>``` : The X coordinate of the destination.
    * ```<toY>``` : The Y coordinate of the destination.
* ```PROMOTION <ordinalPieceType> <toX> <toY>``` : The client sends the promotion that he has done to the server.
    * ```<ordinalPieceType>``` : The ordinal of the piece type.
        *  ```1``` : Rook
        *  ```2``` : Knight
        *  ```3``` : Bishop
        *  ```4``` : Queen
* ```REPLAY <answer>``` : The client sends "Yes" to the server if he wants to play again, otherwise "No".
    * ```<answer>``` : Yes or No

## Messages examples

```mermaid
sequenceDiagram
    actor White as White
    participant Server as Server
    actor Black as Black

    par
    White -->> Server: Establish connection
    Black -->> Server: Establish connection
    Server ->> White: COLOR WHITE
    Server ->> Black: COLOR BLACK
    end

    loop eachTurn
    alt moveValid
        White ->> Server: MOVE fromX fromY toX toY
        opt if promotion
            White ->> Server: PROMOTION PieceType toX toY
        end
        Server ->> Black: MOVE fromX fromY toX toY
        opt if promotion
            Server ->> Black: PROMOTION PieceType toX toY
        end
        else moveNotValid
        White ->> White: print(Move invalid)
    end
    Note over White,Black: The same will happen from Black to White until EndGame.
        alt PLAY AGAIN
    par
        White ->> Server: REPLAY Yes
        Black ->> Server: REPLAY Yes
        Server -->> White: REPLAY Yes
        Server -->> Black: REPLAY Yes
            Note over White,Black: Another Game begins.
    end
        else noAgreement
        par
        White ->> Server: REPLAY No
        White ->> White: Exit()
        Black ->> Server: REPLAY Yes
        Server -->> White: REPLAY No
        Server -->> Black: REPLAY No
        Black ->> Black: Exit()
        end
    end
    end
```
