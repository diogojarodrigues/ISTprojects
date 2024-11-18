#define PIPE_SIZE 256
#define BOX_SIZE 32
#define LIST_SIZE 50
#define REQUEST_SIZE sizeof(request_t)
#define ANSWER_SIZE sizeof(answer_t)
#define MESSAGE_SIZE 128
#define MESSAGE_OVER "#a$-<"

#define PIPES_PATH "/tmp/"

enum operation {
	REG_PUB = 1,
	REG_SUB = 2,
    CREATE = 3,
    REMOVE = 5,
    LIST = 7,
};

typedef struct request {
	int code;
	char client_pipe[PIPE_SIZE];
	char box_name[BOX_SIZE];
} request_t;

typedef struct answer {
	int return_code;
	char message[MESSAGE_SIZE];
} answer_t;

typedef struct box {
	int last;
	char name[BOX_SIZE];
	long unsigned int size;
	long unsigned int n_publishers;
	long unsigned int n_subscribers;
} box_t;

int create_client_pipe(char* pipe_path, char* pipe_name);

int send_request(char* register_pipe_path, int operation, char* client_pipe_path, char* box_name);
int receive_answer(char* client_path_read);

void print_request(request_t request);
