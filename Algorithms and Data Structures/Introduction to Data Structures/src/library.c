/*
* Ficheiro: main.c
* Autor: Diogo Rodrigues
* Descricao: Este ficheiro inicia as variaveis globais, e tambem implementa funcoes secundarias em relacao
	a leitura e escrita de estruturas
*/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "library.h"

/* INICIALIZACAO DAS VARIAVEIS GLOBAIS */

int diasDosMeses[] = {0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
Data dataAtual = {DiaInicio, MesInicio, AnoInicio, {HoraInicio, MinutoInicio}};
int numAeroportos = 0;
int numVoos = 0;

/* FUNCOES AUXILIARES */

char* strdup(const char *);

/*
  Adiciona a uma data, um tempo, comeca por adicionar os minutos e depois as horas,
  caso o numero de minutos exceda os 60, entao é adicionada uma hora, e retira-se
  60 minutos (correspondetes a hora adicionada) e assim sucessivamente
*/
Data adicionaDuracao(Data d, Tempo t) {

	d.tempo.min += t.min;
	d.tempo.hora += t.hora;

	if (d.tempo.min >= MINUTOS_NUMA_HORA) {
		d.tempo.min -= MINUTOS_NUMA_HORA;
		d.tempo.hora++;
	}
	if (d.tempo.hora >= HORAS_NUM_DIA) {
		d.tempo.hora -= HORAS_NUM_DIA;
		d.dia++;
	}
	if (d.dia > diasDosMeses[d.mes]) {
		d.dia -= diasDosMeses[d.mes];
		d.mes++;
	}
	if (d.mes > MESES_NUM_ANO) {
		d.mes -= MESES_NUM_ANO;
		d.ano++;
	}

	return d;
}

void alocacaoValida(void* ptr) {		
	if (ptr == NULL) {
		printf("No memory\n");
		destroyReservas();
		exit(0);
	}
}

Tempo leTempo() {
	Tempo tempo;
	scanf("%d:%d", &tempo.hora, &tempo.min);
	return tempo;
}

char* leCodigoReserva() {
	char codigoReserva[MAX_COD_RESERVA];
	char* strDuplicada;

	scanf("%s", codigoReserva);
	strDuplicada = strdup(codigoReserva);
	alocacaoValida(strDuplicada);
	
	return strDuplicada;
}

Reserva leReserva() {
	Reserva reserva;

	reserva.codReserva = leCodigoReserva();
	alocacaoValida(reserva.codReserva);
	scanf("%d", &reserva.passageiros);
	
	return reserva;
}


/* FUNCOES PARA LIBERTAR A MEMORIA */

void eliminaReserva(Link node) {	
	free(node->reserva.codReserva);
	free(node);
}

void eliminaReservas(Link reservas) {	
	Link aux;
	while (reservas != NULL) {
		aux = reservas;
		reservas = reservas->next;
		eliminaReserva(aux);
	}

}

void destroyReservas() {				
	int i;

	for (i=0; i<numVoos; i++) {
		eliminaReservas(listaVoos[i].listaReservas);
	}
}


/* FUNCOES DE LEITURA */

Aeroporto leAeroporto() {		
	Aeroporto aeroporto;

	scanf("%s %s ", aeroporto.id, aeroporto.pais);
	fgets(aeroporto.cidade , MAX_CIDADE, stdin);
	aeroporto.cidade[strcspn(aeroporto.cidade, "\n")] = 0;
	aeroporto.numeroVoos = 0;

	return aeroporto;
}

Data leData(int modo) {			
	Data data;
	scanf("%d-%d-%d", &data.dia, &data.mes, &data.ano);
	if (modo == TUDO)			/* Apenas le o tempo referente a data caso a variavel modo seja igual a TUDO*/
		data.tempo = leTempo();
	else {
		data.tempo.hora = 0;
		data.tempo.min = 0;
	}
	return data;
}

Voo leVoo() {				
	Voo voo;

	scanf("%s %s %s ", voo.codigoVoo, voo.idPartida, voo.idChegada);
	voo.partida = leData(TUDO);
	voo.duracao = leTempo();
	voo.chegada = adicionaDuracao(voo.partida, voo.duracao);
	scanf("%d", &voo.capacidade);
	voo.totalReservas = 0;
	voo.listaReservas = NULL;

	return voo;
}

Link leLink() {
	Link link = (Link) malloc(sizeof(Node));
	alocacaoValida(link);
	
	link->reserva = leReserva();
	link->next = NULL;

	return link;
}


/* FUNCOES DE ESCRITA */

void escreveAeroporto(Aeroporto aeroporto) {	
	printf("%s %s %s %d\n", aeroporto.id, aeroporto.cidade, aeroporto.pais, aeroporto.numeroVoos);
}

void escreveData(Data data, int modo) {			
	printf("%02d-%02d-%04d", data.dia, data.mes, data.ano);
	if (modo == TUDO)		/* Apenas imprime o tempo referente a data caso a variavel modo seja igual a TUDO */
		printf(" %02d:%02d", data.tempo.hora, data.tempo.min);
	printf("\n");
}

void escreveVoo(Voo voo, int modo) {			
	printf("%s ", voo.codigoVoo);

	/* Caso se trate de um voo com este aeroporto de partida so imprime o id de chegada e a data de partida */
	if (modo == AEROPORTO_DE_PARTIDA) {	
		printf("%s ", voo.idChegada);
		escreveData(voo.partida, TUDO);
	/* Caso se trate de um voo com este aeroporto de chegada so imprime o id de partida e a data de chegada */
	} else if (modo == AEROPORTO_DE_CHEGADA) {
		printf("%s ", voo.idPartida);
		escreveData(voo.chegada, TUDO);
	/* Caso se queira saber todos os dados do voo */
	} else {
		printf("%s ", voo.idPartida);
		printf("%s ", voo.idChegada);
		escreveData(voo.partida, TUDO);
	}
}

void escreveReserva(Link link) {
	printf("%s %d\n", link->reserva.codReserva, link->reserva.passageiros);
}
