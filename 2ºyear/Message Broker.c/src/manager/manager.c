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

int verify_arguments(int argc, char **argv, char* register_pipe_path, char* client_pipe_path, char* box_name) {
    if (
        (argc < 4 || argc > 5) || (
        strcmp(argv[3], "create") &&
        strcmp(argv[3], "remove") &&
        strcmp(argv[3], "list"))
    ) {
        fprintf(stderr, "Invalid arguments, operation canceled\n");
        fprintf(stderr, "usage: \n"
                    "   manager <register_pipe_name> <pipe_name> create <box_name>\n"
                    "   manager <register_pipe_name> <pipe_name> remove <box_name>\n"
                    "   manager <register_pipe_name> <pipe_name> list\n");
        exit(EXIT_FAILURE);
    }

    //Register pipe
    strcpy(register_pipe_path, PIPES_PATH);
    strcat(register_pipe_path, argv[1]);

    //Client pipe
    create_client_pipe(client_pipe_path, argv[2]);

    switch (argv[3][0]) {
    case 'c':
        strcpy(box_name, argv[4]);
        return CREATE;
    case 'r':
        strcpy(box_name, argv[4]);
        return REMOVE;
    case 'l':
        strcpy(box_name, "");
        return LIST;

    default:
        fprintf(stderr, "Invalid Operation\n");
        return -1;
    }
    
    return -1;
}

void receive_boxes(char* client_pipe_path) {
    box_t* box_list = (box_t*) malloc(sizeof(box_t) * LIST_SIZE);
    int rx = open(client_pipe_path, O_RDONLY);           // Open pipe for reading
    if (rx == -1) {
        fprintf(stderr, "[ERR]: open failed: %s\n", strerror(errno));
        exit(EXIT_FAILURE);
    }

    if (read(rx, box_list, sizeof(box_t) * LIST_SIZE) == -1)
        exit(EXIT_FAILURE);
    
    for (int i = 0; i<LIST_SIZE; i++) {
        if (box_list[i].last == 1) {
            if (i==0)
                fprintf(stdout, "NO BOXES FOUND\n");
            break;
        }
            
        fprintf(stdout, "%s %zu %zu %zu\n", box_list[i].name, box_list[i].size, box_list[i].n_publishers, box_list[i].n_subscribers);
    }

    close(rx);
}

int main(int argc, char **argv) {
    char* register_pipe_path = (char*) malloc(sizeof(char) * PIPE_SIZE);
    char* client_pipe_path = (char*) malloc(sizeof(char) * PIPE_SIZE);
    char* box_name = (char*) malloc(sizeof(char) * BOX_SIZE);
    int operation = verify_arguments(argc, argv, register_pipe_path, client_pipe_path, box_name);
    
    //Send the request to the broker and receive the awnser

    if (operation == LIST) {
        if (send_request(register_pipe_path, operation, client_pipe_path, box_name) == -1) {
            fprintf(stderr, "[ERR]: Error sending the request\n");
        }
        receive_boxes(client_pipe_path);
    } else {
        if (send_request(register_pipe_path, operation, client_pipe_path, box_name) == -1) {
            fprintf(stderr, "[ERR]: Error sending the request\n");
        }
        receive_answer(client_pipe_path);
    }

    return 0;
}
