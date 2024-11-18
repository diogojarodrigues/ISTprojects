# TupleSpaces

Distributed Systems Project 2024

**Group A45**

**Difficulty level: I am Death incarnate!**

### Team Members

| Number | Name            | User                                  | Email                                             |
|--------|-----------------|---------------------------------------|---------------------------------------------------|
| 102848 | Diogo Rodrigues | <https://github.com/diogojarodrigues> | <mailto:diogojarodrigues@tecnico.ulisboa.pt>      |
| 102663  | Pedro Ribeiro   | <https://github.com/dybolo>           | <mailto:pedro.melo.ribeiro@tecnico.ulisboa.pt>    |
| 103525 | Tomaz Silva     | <https://github.com/tomhoq>           | <mailto:tomaz.goncalves-silva@tecnico.ulisboa.pt> |

## Getting Started

The overall system is made up of several modules. The different types of servers are located in _ServerX_ (where X denotes stage 1, 2 or 3).
The clients is in _src/Client_.
The definition of messages and services is in _src/Contract_.
The naming server is in _src/NamingServer_.
The sequencer server is in _src/Sequencer_.


See the [Project Statement](https://github.com/tecnico-distsys/TupleSpaces) for a complete domain and system description.

### Prerequisites

The Project is configured with Java 17 (which is only compatible with Maven >= 3.8), but if you want to use Java 11 you
can too -- just downgrade the version in the POMs.

To confirm that you have them installed and which versions they are, run in the terminal:

```s
javac -version
mvn -version
```

### Installation

To compile and install all modules:

 - First, you need to setup a virtual enviroment
    <br>
    - Windows
    <br>
    
    ```s
    python -m venv .venv
    .venv\Scripts\activate
    python -m pip install grpcio
    python -m pip install grpcio-tools
    ```

    Run this command, to stop the virtual enviroment from running
    ```s 
    deactivate
    ```

    Run this command, to activate the virtual enviroment, if it is not running
    ```s 
    .venv\Scripts\activate
    ```

    <br>

    - Linux/Mac
    
    <br>

    ```s
    python -m venv .venv
    source .venv/bin/activate
    python -m pip install grpcio
    python -m pip install grpcio-tools
    ```

    Run this command, to stop the virtual enviroment from runing
    ```s 
    deactivate
    ```

    Run this command, to activate the virtual enviroment, if it is not running
    ```s 
    source .venv/bin/activate
    ```
    
    <br>
 - To compile all files, open a terminal a run the following commands
    ```s
    cd src
    mvn clean install
    cd Contract
    mvn exec:exec
    ```


### Run Program

 - First, open a terminal and run the DNS server
   ```s
   cd src/NameServer
   python server.py
   ```

 - Second, open second terminal and run the Sequencer server
   ```s
   cd src/Sequencer
   mvn compile exec:java
   ```

 - Third, open a new terminal and run the server (repeated this for 3 times)

   The port value, must be different in all the three terminals created</br>
   The qualifier value, must be different and the values must be one of this A, B, C</br>
   You can use the flag -debug to be able to see the logs from the server
   ```s
   cd src/ServerR3
   mvn compile exec:java -Dexec.args="{port} {qualifier} {-debug}"
   ```

 - Last, open another terminal and run the client (repeated this as many times you like)
   
   ClientId must be a number, and all clients must have different IDs
   ```s
   cd src/Client
   mvn compile exec:java -Dexec.args="{clientId}"
   ```

## Built With

* [Maven](https://maven.apache.org/) - Build and dependency management tool;
* [gRPC](https://grpc.io/) - RPC framework.
