
| Action | REST Call using client | RPC Call at server|
|--|--|--|
| purchaseTicket | POST `/ticket/purchase` |PurchaseTicket |
| getReceipt | GET `/user/{email}` | GetReceipt |
| getSeatsBySection | GET `/user/seats` | GetUsersBySection |
| removeUser | DELETE `/user/{email}` | RemoveUser |
| modifySeatOfUser | PUT `/user/modify` | ModifyUserSeat |
---
# How this application is structured
This application is written in Java 17, GRPC Spring Boot and Gradle. It has three parts:
* common project
* server project
* client project

## The common project
The common project has the following highlights:
* Two `*.proto` files corresponding to user and train actions
* `AppContext` class that is used by the *client project* to get and set `requestId` to each request
* `GrpcConstants` class that is used to add `requestId` metadata to the incoming requests
* `train.proto` file defines a `TrainTicketService` which lists 5 `rpc` calls
    * `PurchaseTicket`
    * `GetReceipt`
    * `GetUsersBySection`
    * `RemoveUser`
    * `ModifyUserSeat`
* There is another `user.proto` file that defines a `UserService` which was used while development to debug some scenarios and is left as is for the evaluator's reference

## The server project
The server project has the following highlights:
* Two services
    * `TrainTicketService` and
    * `UserService `
* `GrpcServerInterceptor` that calls the custom error handler for global exception handling
* `GrpcErrorAdvice` for handling all errors globally
* Three Entities and DAO classes for accessing the data
    * `SeatDAO`
    * `TicketDAO`
    * `UserDAO`
* This project also has couple of custom exceptions and unit tests
* The code coverage of this project stands at 50%

## The client project
The client project has the following highlights:
* The client is a REST service written using Spring Boot that uses GRPC to internally calls the server project
* `GrpcChannelManager` which creates a `ManagedChannel` with some default config
* A global exception handler using `@ControllerAdvice`
* A `dto` package that contains separate DTOs for input and output
* A `GrpcClientInterceptor` that intercepts outgoing calls before they are dispatched by a channel
* There is a `ClientController` which lists some actions that can be performed

## How to test this project
There is a postman collection that is capable of executing all requests using REST and it will internally execute the GRPC calls
1. Build the project (common, server and client)
2. Run Server
3. Run Client
4. Import `GRPC-Example-Client.postman_collection` into postman
5. Execute requests in sequence
    * Most of the request use postman globals to carry data points from one request to another
    * For other requests there are some random values configured using postman variables
    * In remaining cases, its required for the user to manually copy data like `email` from one request to another in order to get the proper response



