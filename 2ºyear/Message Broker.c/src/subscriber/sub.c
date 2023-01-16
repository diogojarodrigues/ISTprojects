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
        fprintf(stderr, "Invalid arguments, operation canceled\n");
        fprintf(stderr, "usage: sub <register_pipe_name> <pipe_name> <box_name>\n");
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

void read_from_box(char* client_pipe_path) {
    char message[MESSAGE_SIZE];
    ssize_t bytes_read;
    int pd, n_msg = 0;

    pd = open(client_pipe_path, O_RDONLY);
    if (pd == -1) {
        fprintf(stderr, "[send_message_to_server]: open failed: %s\n", strerror(errno));
        return;
    }
    while ((bytes_read = read(pd, message, MESSAGE_SIZE)) >= 0) {
        if (strcmp(message, MESSAGE_OVER) == 0)
            break;
        message[bytes_read] = '\0';
        printf("%s", message);
        n_msg++;
    }
    
    close(pd);
    fprintf(stdout, "Were read %d! message(s)\n", n_msg);
}

int main(int argc, char **argv) {
    char* register_pipe_path = (char*) malloc(sizeof(char) * PIPE_SIZE);
    char* client_pipe_path = (char*) malloc(sizeof(char) * PIPE_SIZE);
    char* box_name = (char*) malloc(sizeof(char) * BOX_SIZE);
    verify_arguments(argc, argv, register_pipe_path, client_pipe_path, box_name);

    send_request(register_pipe_path, REG_SUB, client_pipe_path, box_name);
    if (receive_answer(client_pipe_path)) {
        read_from_box(client_pipe_path);
    }
    
    return 0;
}