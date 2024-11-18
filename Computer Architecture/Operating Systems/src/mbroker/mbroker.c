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
#include "operations.h"
#include "protocol.h"

#define PIPES_PATH "/tmp/"

box_t* box_list;
long unsigned int box_num = 0;

//Auxiliar functions
int lookup_box(char* box_name) {
    for (int i=0; i<box_num; i++)
        if (!strcmp(box_name, box_list[i].name))
            return i;
    return -1;
}

int send_answer(char* pipe_path_write, char* message, int return_code) {
    answer_t answer;
    answer.return_code = return_code;
    strcpy(answer.message, message);

    int tx = open(pipe_path_write, O_WRONLY);
    if (tx == -1) {
        fprintf(stderr, "[ERR]: open failed: %s\n", strerror(errno));
        exit(EXIT_FAILURE);
    }

    if (write(tx, &answer, ANSWER_SIZE) == -1) {
        fprintf(stderr, "[ERR]: writing answer to pipe");
    }

    close(tx);
    return 1;
}

int create_register_pipe(char* register_pipe) {
    // Remove pipe if it does exist
    if (unlink(register_pipe) != 0 && errno != ENOENT) {
        fprintf(stderr, "[ERR]: unlink(%s) failed: %s\n", register_pipe, strerror(errno));
        exit(EXIT_FAILURE);
    }

    // Create pipe
    if (mkfifo(register_pipe, 0640) != 0) {
        fprintf(stderr, "[ERR]: mkfifo failed: %s\n", strerror(errno));
        exit(EXIT_FAILURE);
    }

    // Open pipe for reading
    // This waits for someone to open it for reading
    int rx = open(register_pipe, O_RDONLY);
    if (rx == -1) {
        fprintf(stderr, "[ERR]: open failed: %s\n", strerror(errno));
        exit(EXIT_FAILURE);
    }

    //Open pipe for writing
    //Never used, created to make the read a blocking wait
    if (open(register_pipe, O_WRONLY) == -1) {
        fprintf(stderr, "[ERR]: open failed: %s\n", strerror(errno));
        exit(EXIT_FAILURE);
    }

    return rx;
}

int delete_register_pipe(char* register_pipe) {
    printf("FALTA IMPLEMENTAR, PIPE: %s", register_pipe);
    return -1;
}


//Auxiliar functions of operations
void read_from_publisher(char* box_name, int register_pipe_fd) {         //Tens de alterar este register pipe para outra pipe
    char message[MESSAGE_SIZE];
    int file;
    ssize_t bytes_read;

    
    char box_path[ANSWER_SIZE] = "/";
    strcat(box_path, box_name);
    
    /*
    // Open the pipe for reading
    pipe = open(register_pipe_path, O_RDONLY);       //Already created
    if (pipe < 0) {
        fprintf(stderr, "Error opening pipe for reading: %s\n", strerror(errno));
        return 1;
    }
    */

    //Open file for writting
    file = tfs_open(box_path, TFS_O_APPEND);
    if (file == -1) {
        fprintf(stderr, "Error opening file for writing: %s\n", strerror(errno));
        return;
    }

    while (true) {
        bytes_read = read(register_pipe_fd, message, MESSAGE_SIZE);
        if (bytes_read < 0) {
            fprintf(stderr, "Error reading from pipe: %s\n", strerror(errno));
            break;
        } else if (strcmp(message, MESSAGE_OVER) == 0) {
            break;
        }
        tfs_write(file, message, MESSAGE_SIZE);
    }
    
    tfs_close(file);
}

void write_to_subscriber(char* client_pipe_path, char* box_name) {
    char message[MESSAGE_SIZE];
    int file, pipe;
    size_t bytes_read;

    char box_path[ANSWER_SIZE] = "/";
    strcat(box_path, box_name);

    file = tfs_open(box_path, TFS_O_CREAT);
    pipe = open(client_pipe_path, O_WRONLY);

    while ((bytes_read = (size_t) tfs_read(file, message, MESSAGE_SIZE)) > 0) {
        if (write(pipe, message, MESSAGE_SIZE) == -1) {
            fprintf(stderr, "Error writing to pipe: %s\n", strerror(errno));
            tfs_close(file);
            close(pipe);
            return;
        }
    }

    if (write(pipe, MESSAGE_OVER, 6) == -1) {
        fprintf(stderr, "Error writing to pipe: %s\n", strerror(errno));
        tfs_close(file);
        close(pipe);
        return;
    }

    close(pipe);
    tfs_close(file);
}


/*
 *  OPERATIONS
 */


void register_publisher(char* client_pipe_path, char* box_name, int register_pipe_fd) {
    int pos = lookup_box(box_name);
    if (pos == -1) {
        send_answer(client_pipe_path, "There is no box with that name!", 0);
        return;
    }
    if (box_list[pos].n_publishers != 0) {
        send_answer(client_pipe_path, "Publisher already assigned to that box!", 0);
        return;
    }

    box_list[pos].n_publishers++;
    fprintf(stdout, "Register publisher!\n");
   
    send_answer(client_pipe_path, "OK", 1);
    read_from_publisher(box_name, register_pipe_fd);

    box_list[lookup_box(box_name)].n_publishers = 0;
    fprintf(stdout, "Publisher endend session!\n");
}

void register_subscriber(char* client_pipe_path, char* box_name) {
    int pos = lookup_box(box_name);
    if (pos == -1) {
        send_answer(client_pipe_path, "There is no box with that name!", 0);
        return;
    }

    box_list[pos].n_subscribers++;
    fprintf(stdout, "Register subscriber!\n");
    
    send_answer(client_pipe_path, "OK", 1);
    write_to_subscriber(client_pipe_path, box_name);

    box_list[pos].n_subscribers--;
    fprintf(stdout, "Unsubscribed!\n");
}



void create_box(char* client_pipe_path, char* box_name) {
    char box_path[ANSWER_SIZE] = "/";
    strcat(box_path, box_name);
    

    if (lookup_box(box_name) != -1) {
        send_answer(client_pipe_path, "[Denied]: Box with that name already exist!", 0);
        return;
    }

    int file = tfs_open(box_path, TFS_O_CREAT);
    if (file == -1) {
        send_answer(client_pipe_path, "[Denied]: Error creating the box", 0);
        return;
    }
    tfs_close(file);

    //Inicializing box
    box_t new;
    new.last = 0;
    new.n_publishers = 0;
    new.n_subscribers = 0;
    new.size = box_num;               //new.size = box_num é temporário motivos de debug
    strcpy(new.name, box_name);

    box_list[box_num++] = new;

    fprintf(stdout, "Create box!\n"); //Temporário

    send_answer(client_pipe_path, "OK", 1);
}

void delete_box(char* client_pipe_path, char* box_name) {
    char box_path[ANSWER_SIZE] = "/";
    strcat(box_path, box_name);
    
    int pos = lookup_box(box_name);
    if (pos == -1) {
        send_answer(client_pipe_path, "No box found with that name", 0);
        return;
    }

    if (tfs_unlink(box_path) == -1) {
        send_answer(client_pipe_path, "Error deleting the box", 0);
        return;
    }
    
    box_list[pos] = box_list[--box_num];
    
    fprintf(stdout, "Delete box!\n");
    send_answer(client_pipe_path, "Ok", 1);
}

void list_boxes(char* client_pipe) {
    int tx = open(client_pipe, O_WRONLY);
    if (tx == -1) {
        fprintf(stderr, "[ERR]: open failed: %s\n", strerror(errno));
        exit(EXIT_FAILURE);
    }

    box_list[box_num].last = 1;
    if (write(tx, box_list, LIST_SIZE * sizeof(box_t)) == -1) {
        fprintf(stderr, "[ERR]: writing answer to pipe");
    }
    box_list[box_num].last = 0;
    
    close(tx);

    fprintf(stdout, "List all boxes!\n");
    return;
}

int main(int argc, char **argv) {
    //Input
    if (argc != 3) {
        fprintf(stderr, "Invalid arguments, operation canceled");
        fprintf(stderr, "usage: pub <register_pipe_name> <max_sessions>\n");
        exit(EXIT_FAILURE);
    }

    //Creating pipe
    char register_pipe_path[PIPE_SIZE] = PIPES_PATH;
    strcat(register_pipe_path, argv[1]);
    int rx = create_register_pipe(register_pipe_path);
    
    box_list = (box_t*) malloc(sizeof(char*) * LIST_SIZE);

    if (tfs_init(NULL) == -1) {
        //Adicionar close de pipes e cenas que abriste
        exit(EXIT_FAILURE);
    }

    //Operation of mbroker
    request_t request;
    ssize_t ret;

    while(true) {
        ret = read(rx, &request, REQUEST_SIZE);                //Lê a operação solicitada pelo cliente
        if (ret == -1) {
            fprintf(stderr, "[ERR]: read failed: %s\n", strerror(errno));
            exit(EXIT_FAILURE);
        }

        switch(request.code) {
            case REG_PUB:
                register_publisher(request.client_pipe, request.box_name, rx);
                break;
            case REG_SUB:
                register_subscriber(request.client_pipe, request.box_name);
                break;
            case CREATE:
                create_box(request.client_pipe, request.box_name);
                break;
            case REMOVE:
                delete_box(request.client_pipe, request.box_name);
                break;
            case LIST:
                list_boxes(request.client_pipe);
                break;
            default:
                fprintf(stdout, "ERROR, Wrong command!\n");
                break;
        }
    }
    
    tfs_destroy();
    delete_register_pipe(register_pipe_path);
    return -1;
}
