#include "logging.h"
#include <errno.h>
#include <assert.h>
#include <fcntl.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <unistd.h>
#include "protocol.h"

void verify_arguments(int argc, char **argv, char* register_pipe_path, char* client_pipe_path, char* box_name) {
    if (argc != 4) {
        fprintf(stderr, "Invalid arguments, operation canceled");
        fprintf(stderr, "usage: pub <register_pipe_name> <pipe_name> <box_name>\n");
        exit(EXIT_FAILURE);
    }

    //Register pipe
    strcpy(register_pipe_path, PIPES_PATH);
    strcat(register_pipe_path, argv[1]);

    //Client pipe
    create_client_pipe(client_pipe_path, argv[2]);

    //Box name
    strcpy(box_name, argv[3]);
}

void write_to_box(char* register_pipe_path) {
    char message[MESSAGE_SIZE];

    int fd = open(register_pipe_path, O_WRONLY);
    if (fd < 0) {
        fprintf(stderr, "Error opening pipe for writing: %s\n", strerror(errno));
        return;
    }

    while (fgets(message, MESSAGE_SIZE, stdin) != NULL) {
        // Write the input to the pipe
        if (write(fd, message, MESSAGE_SIZE) < 0) {
            fprintf(stderr, "Error writing to pipe: %s\n", strerror(errno));
            close(fd);
            return;
        }
    }
    if (write(fd, MESSAGE_OVER, 6) == -1) {
        fprintf(stderr, "[send_message_to_server]: open failed: %s\n", strerror(errno));
        close(fd);
        return;
    }


    close(fd);
    return;
}

int main(int argc, char **argv) {
    char* register_pipe_path = (char*) malloc(sizeof(char) * PIPE_SIZE);
    char* client_pipe_path = (char*) malloc(sizeof(char) * PIPE_SIZE);
    char* box_name = (char*) malloc(sizeof(char) * BOX_SIZE);
    verify_arguments(argc, argv, register_pipe_path, client_pipe_path, box_name);

    send_request(register_pipe_path, REG_PUB, client_pipe_path, box_name);
    if (receive_answer(client_pipe_path)) {
        write_to_box(register_pipe_path);
    }
    
    return 0;
}
