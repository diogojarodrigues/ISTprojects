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

int create_client_pipe(char* pipe_path, char* pipe_name) {
    char str[20];
    sprintf(str, "%d", getpid());

    strcpy(pipe_path, PIPES_PATH);
    strcat(pipe_path, pipe_name);
    strcat(pipe_path, str);

    // Remove pipe if it does exist
    if (unlink(pipe_path) != 0 && errno != ENOENT) {
        fprintf(stderr, "[ERR]: unlink(%s) failed: %s\n", pipe_path, strerror(errno));
        exit(EXIT_FAILURE);
    }

    // Create pipe
    if (mkfifo(pipe_path, 0640) != 0) {
        fprintf(stderr, "[ERR]: mkfifo failed: %s\n", strerror(errno));
        exit(EXIT_FAILURE);
    }

    return 0;
}

//Send request from client to server (does't work the other way around)
int send_request(char* register_pipe_path, int operation, char* client_pipe_path, char* box_name) {
    request_t request;
    request.code = operation;
    strcpy(request.client_pipe, client_pipe_path);
    strcpy(request.box_name, box_name);

	//Open pipe for writing
    int tx = open(register_pipe_path, O_WRONLY);
    if (tx == -1) {
        fprintf(stderr, "[send_message_to_server]: open failed: %s\n", strerror(errno));
        exit(EXIT_FAILURE);
    }

	ssize_t ret = write(tx, &request, REQUEST_SIZE);
	if (ret == -1)
        fprintf(stderr, "[send_message_to_server]: error writing in the file\n");
        
        
    while(close(tx) == -1) {
        fprintf(stderr, "[send_message_to_server]: error closing the file\n");
    } 

	return (int) ret; 
}

int receive_answer(char* client_path_read) {
    answer_t answer;
    int rx = open(client_path_read, O_RDONLY);
    if (rx == -1) {
        fprintf(stderr, "[ERR]: open failed: %s\n", strerror(errno));
        exit(EXIT_FAILURE);
    }

    if (read(rx, &answer, ANSWER_SIZE) == -1) {
        close(rx);
        return -1;
    }
    fprintf(stdout, "%s\n", answer.message);

    close(rx);
    return answer.return_code;
}

void print_request(request_t message) {
    fprintf(stdout, "code:%d | client pipe:%s | box name:%s \n", message.code, message.client_pipe, message.box_name);
}