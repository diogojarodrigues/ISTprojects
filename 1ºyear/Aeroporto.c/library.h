
/*
* Ficheiro: library.h
* Autor: Diogo Rodrigues
* Descricao: Este ficheiro define as constantes e estruturas que serão usadas ao longo do programa,
	bem como a declaracao das variaveis globais e o prototipo de funcoes referentes as estrutas criadas
*/

#ifndef __LIBRARY_H__
#define __LIBRARY_H__


#define MAX_AEROPORTOS	40				/* numero maximo de aeroportos */
#define MAX_VOOS		30000			/* numero maximo de voos */
#define MAX_ID			4				/* dimensao do id do aeroporto */
#define	MAX_PAIS		31				/* dimensao do nome do pais */
#define MAX_CIDADE		51				/* dimensao do nome da cidade */
#define MAX_COD_VOO		7				/* dimensao do codigo de voo */
#define MAX_COD_RESERVA 65536			/* dimensao do codigo de reserva */
#define	MIN_COD_RESERVA	10				/* dimensao minima do codigo de reserva */

#define	MAX_MESES		13				/* numero de meses */
#define INVALIDO 		-1				/* codigo de erro */

/* Compara duas strings alfabeticamente, retorna true se a primeira for menor alfabeticamente */
#define menor(s1, s2) (strcmp(s1, s2) < 0)

/* Data inicial do sistema */
enum DataInicio {DiaInicio = 1, MesInicio = 1, AnoInicio = 2022, HoraInicio = 0, MinutoInicio = 0};
/* Verdadeiro/Falso */
enum Bool {False, True};
/* Modo de como as funcoes sao chamadas */																			
enum Modo {TUDO = -10, AEROPORTO_DE_PARTIDA, AEROPORTO_DE_CHEGADA, INCOMPLETO};
/* Converter datas */					
enum ConversaoDeDatas {MESES_NUM_ANO = 12 , HORAS_NUM_DIA = 24, MINUTOS_NUMA_HORA = 60};

/* Tipos de dados */

typedef struct {	/* Representa um tempo */
	int hora;
	int min;
} Tempo;

typedef struct {	/* Representa uma data */
	int dia;
	int mes;
	int ano;
	Tempo tempo;
} Data;

typedef struct {	/* Representa um aeroporto */
	char id[MAX_ID];
	char pais[MAX_PAIS];
	char cidade[MAX_CIDADE];
	int numeroVoos;
} Aeroporto;

typedef struct {	/* Representa uma reserva */
	char* codReserva;
	int passageiros;
} Reserva;

struct node {		/* Representa um no de uma lista */
	Reserva reserva;
	struct node* next;
};

typedef struct node Node;
typedef struct node* Link;

typedef struct {	/* Representa um voo */
	char codigoVoo[MAX_COD_VOO];
	char idChegada[MAX_ID];
	char idPartida[MAX_ID];
	Data partida;
	Data chegada;
	Tempo duracao;
	int capacidade;
	int totalReservas;
	Link listaReservas;
} Voo;


/* Variáveis globais */

Data dataAtual; 								/* Representa a data atual do sistema */
Aeroporto listaAeroportos[MAX_AEROPORTOS];		/* Lista de todos os aeroportos no sistema */
Voo listaVoos[MAX_VOOS];						/* Lista de todos os voos no sistema */
int diasDosMeses[MAX_MESES];					/* Contem o número de dias de cada mes*/

int numAeroportos;		/* Representa o número de aeroportos no sistema */
int numVoos;			/* Representa o número de voos no sistema */


/* FUNCOES PARA LIBERTAR A MEMORIA */
void eliminaReserva(Link node);
void eliminaReservas(Link reservas);
void destroyReservas();

/* FUNCOES DE LEITURA */
Aeroporto leAeroporto();
Data leData(int modo);
Voo leVoo();
Link leLink();

/* FUNCOES DE ESCRITA */
void escreveAeroporto(Aeroporto aeroporto);
void escreveVoo(Voo voo, int modo);
void escreveData(Data data, int modo);
void escreveReserva(Link link);


#endif