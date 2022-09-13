/*
* Ficheiro: main.c
* Autor: Diogo Rodrigues
* Descricao: Este ficheiro contem o codigo principal bem como as principais funcoes. Simula um sistema de voos,
	em que pode adicionar voos e aeroportos e lista-los, para alem de puder alterar a data do sistema
*/

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include "library.h"

/* FUNCOES DE A */

/* Verifica se o limite de aeroportos foi atingido e da print num erro e retorna False nesse caso */
int limiteAeroportos() {
	if (numAeroportos >= MAX_AEROPORTOS) {
		printf("too many airports\n");
		return False;
	}
	
	return True;	
}

/* Verifica se o id e valido e da print num erro e retorna False caso o id seja invalido */
int idValido(char id[]) {
	int i;

	for (i=0; id[i]!='\0'; ++i)
		if ( !(id[i] >= 'A' && id[i] <= 'Z') ) {
			printf("invalid airport ID\n");
			return False;
		}

	return True;
}

/* Verifica se ja existe um aeroporto com o mesmo id e retorna true se nao for esse o caso */
int idNaoDuplicado(char id[]) {
	int i;

	for (i=0; i<numAeroportos; ++i)
		if ( strcmp(id, listaAeroportos[i].id) == 0 ) {
			printf("duplicate airport\n");
			return False;
		}

	return True;
}

/* Verifica as condicoes necessarias para adicionar o novo aeroporto, e caso se estas se confirmem adiciona-o */
void a() {
	Aeroporto novoAeroporto = leAeroporto();

	if ( idValido(novoAeroporto.id) && limiteAeroportos() && idNaoDuplicado(novoAeroporto.id) ) {
		listaAeroportos[numAeroportos++] = novoAeroporto;
		printf("airport %s\n", novoAeroporto.id);
	}
}


/* FUNCOES DE L */

/* Verifica se o Id recebido pertence a base de dados dos aeroportos, se nao da print num erro e retorna False */
int procuraID(char id[]) {
	int i;

	for (i=0; i<numAeroportos; i++)
		if ( strcmp(id, listaAeroportos[i].id) == 0)
			return i;

	printf("%s: no such airport ID\n", id);
	return INVALIDO;
}

/* Le um Id referente ao aeroporto, caso esse Id seja valido da print do aeroporto correspondente */
void escreveID() {
	int posID;
	char id[MAX_ID];

	scanf("%s", id);
	posID = procuraID(id);
	if (posID != INVALIDO)
		escreveAeroporto(listaAeroportos[posID]);
}

/*
	Caso a funcao L, seja marcada com mais parametros, esta funcao ira chamar a funcao escreveID, que ira tratar
	de cada um desses parametros
*/
void obtemIDS() {
	escreveID();
	while (getchar() != '\n')
		escreveID();
}

/* Troca a posicao de dois aeroportos  */
void swapAeroportos(int pos1, int pos2) {
	Aeroporto aux = listaAeroportos[pos1];
	listaAeroportos[pos1] = listaAeroportos[pos2];
	listaAeroportos[pos2] = aux;
}

/* Ordena dois aeroportos */
void ordenaAeroportos() {
	int i, j, ordenado = False;
	for (i=0; i<numAeroportos-1; i++) {
		ordenado = True;
		for (j=0; j<numAeroportos-i-1; j++) {

			if (menor(listaAeroportos[j+1].id, listaAeroportos[j].id)) {
				ordenado = False;
				swapAeroportos(j, j+1);
			}
		}
		if(ordenado == True)
			break;
	}
}

/* Esta funcao, le um caracter e dependendo do caracter lido ira realizar duas acoes distintas */
void l() {
	int i;

	/* Caso o caracter lido seja um espaco, significa que a opcao L foi chamada com vários parametros */
	if (getchar() == ' ')
		obtemIDS();		/* Chama a funcao obtemIDS que ira ler cada parametro, e realizar as devidas funcoes */
	else {

		ordenaAeroportos();
		for  (i=0; i<numAeroportos; i++)
			escreveAeroporto(listaAeroportos[i]);		/* Da print de todos os aeroportos existentes */
	}	
}

/* FUNCOES DE V */

/* Esta funcao verifica o codigo, e retorna True caso o codigo seja valido */
int codigoVooValido(char codigoVoo[]) {
	int i = 3;

	if (
		codigoVoo[0] < 'A' || codigoVoo[0] > 'Z' ||			/* Primeira letra maiuscula */
		codigoVoo[1] < 'A' || codigoVoo[1] > 'Z' ||			/* Segunda letra maiuscula */
		codigoVoo[2] <= '0' || codigoVoo[2] > '9'		/* Se o primeiro numero for 0 o codigo é invalido */
	)
	{
		printf("invalid flight code\n");
		return False;
	}
	
	while (codigoVoo[i] != '\0') {						/* Restantes numeros */
		if ( codigoVoo[i] < '0' || codigoVoo[i] > '9' ) {
			printf("invalid flight code\n");
			return False;
			}
		i++;
	}

	return True;
}

/* Esta funcao verifica se dois codigos de voo sao iguais, retorna True se os codigos forem iguais*/
int codigoVooIgual(char c1[], char c2[]) {	
	return !strcmp(c1, c2);
}

/* Esta funcao verifica se duas datas correspondem ao mesmo dia, retorna True em caso afirmativo */
int diasIguais(Data d1, Data d2) {
	return d1.dia == d2.dia && d1.mes == d2.mes && d1.ano == d2.ano;
}

/*
	A funcao recebe um codigo de voo e uma data, caso ja exista um voo com a mesma data e o
	mesmo codigo de voo entao a funcao levanta um erro e retorna False
*/
int novoVoo(char codigo[], Data data) {
	int i;
	
	for (i = 0; i<numVoos; i++)
		if ( diasIguais(data, listaVoos[i].partida) && codigoVooIgual(codigo, listaVoos[i].codigoVoo) ) {
				printf("flight already exists\n");
				return False;
		}

	return True;
}

/* A funcao verifica se o numero de voos excede o seu limite, se nao exceder retorna True*/
int voosDisponiveis() {
	if (numVoos < MAX_VOOS)
		return True;
	printf("too many flights\n");
	return False;
}

/* A funcao verifica se a data e valida, se for retorna True */
int dataValida(Data data) {
	if (data.ano == dataAtual.ano)		/* A data tem o mesmo ano da data atual */
		if( (data.mes == dataAtual.mes && data.dia >= dataAtual.dia) || data.mes > dataAtual.mes )
			return True;
	if (data.ano == dataAtual.ano + 1)	/* A data tem mais um ano da data atual */
		if( (data.mes == dataAtual.mes && data.dia <= dataAtual.dia) || data.mes < dataAtual.mes )
			return True;

	printf("invalid date\n");
	return False;
}

/* Verifica se a duracao do voo e valida, ou seja, retorna True caso seja menor que 12 horas */
int duracaoValida(Tempo duracao) {
	if (duracao.hora < 12 || (duracao.hora == 12 && duracao.min == 0))
		return True;
	printf("invalid duration\n");
	return False;
}

/* Verifica se a capacidade do voo e valida, ou seja, retorna True caso a capacidade esteja entre 10 e 100 */
int capacidadeValida(int n) {
	if (n >= 10)
		return True;
	printf("invalid capacity\n");
	return False;
}

/*
	Esta funcao le um voo, verifica se todos os argumentos do voo sao validos,
	caso sejam adiciona o voo a lista de voos
*/
void adicionaVoo() {
	int chegada, partida;
	Voo voo = leVoo();	

	/*Verifica o codigo e se o voo e duplicado */
	if ( codigoVooValido(voo.codigoVoo) && novoVoo(voo.codigoVoo, voo.partida) ) {
		partida = procuraID(voo.idPartida);	/* Verifica o Id */
		chegada = procuraID(voo.idChegada);
	 
		if (

			chegada != INVALIDO && partida != INVALIDO && voosDisponiveis() && dataValida(voo.partida)
			&& duracaoValida(voo.duracao) && capacidadeValida(voo.capacidade)
		)	
		{

			listaVoos[numVoos++] = voo;
			listaAeroportos[partida].numeroVoos++;
		}
	}
}

/* 
	Esta funcao le um caracter e caso este seja igual a um espaco, significa que a funcao v e chamada 
	com varios parametros, e consoante a leitura ira realizar duas acoes distintas
*/
void v() {
	int i;

	if (getchar() == ' ')
		adicionaVoo();					/* Adiciona um voo */
	else		
		for (i=0; i<numVoos; i++)		/* Da print de todos os voos existentes */
			escreveVoo(listaVoos[i], TUDO);
}

/* FUNCOES DE P & C */

/* A funcao recebe e ordena duas datas, incluindo horas */
int dataNaoOrdenada(Data d1, Data d2) {
	if (d1.ano == d2.ano) {
		if (d1.mes == d2.mes) {
			if (d1.dia == d2.dia) {
				if (d1.tempo.hora == d2.tempo.hora)
					return d1.tempo.min > d2.tempo.min;
				return d1.tempo.hora > d2.tempo.hora;
			}
			return d1.dia > d2.dia;
		}
		return d1.mes > d2.mes;
	}
	return d1.ano > d2.ano;
}

/* 
	A funcao recebe um voo, e insere-o ordenadamente, num vetor que contem todos os voos com o mesmo
	aeroporto de chegada ou o mesmo aeroporto de partida, dependendo do modo
*/
int ordenaVoos(Voo listaDeVoosDumAeroporto[], int tamanho, int modo) {
	Voo vooTemporario;
	int i, ordenado = True;

	for (i=0; i < tamanho-1; i++)

		if (
			/* Caso seja um aeroporto de partida, ira criar uma lista ordenada pela data de partida */
			( dataNaoOrdenada(listaDeVoosDumAeroporto[i].partida, listaDeVoosDumAeroporto[i+1].partida)
			&& modo == AEROPORTO_DE_PARTIDA ) ||
			/* Caso seja um aeroporto de chegada, ira criar uma lista ordenada pela data de chegada */
			( dataNaoOrdenada(listaDeVoosDumAeroporto[i].chegada, listaDeVoosDumAeroporto[i+1].chegada)
			&& modo == AEROPORTO_DE_CHEGADA )

		)
		{
			vooTemporario = listaDeVoosDumAeroporto[i];
			listaDeVoosDumAeroporto[i] = listaDeVoosDumAeroporto[i+1];
			listaDeVoosDumAeroporto[i+1] = vooTemporario;
		 	ordenado = False;
		}

	return ordenado;
}

/* 
	Esta funcao cria uma lista com os voos que satisfacam as condicoes,
	caso seja um aeroporto de partida, apenas ira listar os voos em que o Id de partida
	conicide com o Id recebido,
	caso seja um aeroporto de chegada, apenas ira listar os voos em que o Id de chegada
	conicide com o Id recebido
 */
int criarListaVoosDumAeroporto(Voo listaDeVoosDumAeroporto[], char id[MAX_ID], int modo) {
	int i, tamanho = 0;

	for (i = 0; i<numVoos; i++) 
		if ( 
				( !strcmp(id, listaVoos[i].idPartida) && modo == AEROPORTO_DE_PARTIDA ) ||
				( !strcmp(id, listaVoos[i].idChegada) && modo == AEROPORTO_DE_CHEGADA)
			)
			listaDeVoosDumAeroporto[tamanho++] = listaVoos[i];
	return tamanho;
}

/*
	Esta funcao le um Id, valida-o e caso este seja valido executa varias funcoes, estas funcoes tem ligeiras
	alteracoes dependendo do modo com que a funcao e chamada, caso o modo seja igual a AEROPORTO_DE_PARTIDA, as
	funcoes terao comportamentos diferentes do que se o modo fosse igual a AEROPORTO_DE_CHEGADA, por exemplo
	a funcao escreveVoo no modo AEROPORTO_DE_PARTIDA apenas imprime os Ids de chegada
*/
void voosDumAeroporto(int modo) {
	Voo listaDeVoosDumAeroporto[MAX_VOOS];
	char id[MAX_ID];
	int i, tamanho;

	scanf("%s", id);	
	if (procuraID(id) != INVALIDO) {
		tamanho = criarListaVoosDumAeroporto(listaDeVoosDumAeroporto, id, modo);
		while(!ordenaVoos(listaDeVoosDumAeroporto, tamanho, modo));
		/* Escreve todos os Voos em que o Id de chegada/partida(dependendo do modo) coincidem com o id lido*/
		for (i = 0; i<tamanho; i++)	
			escreveVoo(listaDeVoosDumAeroporto[i], modo);
	}
}

/* Chama a funcao voosDumAeroporto que ira escrever todos os voos que partem do aeroproto do Id lido */
void p() {
	voosDumAeroporto(AEROPORTO_DE_PARTIDA);
}

/* Chama a funcao voosDumAeroporto que ira escrever todos os voos que chegam ao aeroporto do Id lido */
void c() {
	voosDumAeroporto(AEROPORTO_DE_CHEGADA);
}

/* FUNCAO T */

/* Recebe uma data e caso essa data seja valida, atualiza a data atual sistema */
void t(){
	Data novaData = leData(INCOMPLETO);
	if (dataValida(novaData)) {
		dataAtual.dia = novaData.dia;
		dataAtual.mes = novaData.mes;
		dataAtual.ano = novaData.ano;

		escreveData(dataAtual, INCOMPLETO);
	}
}

/* FUNCAO R */

/* Procura o voo, na lista de voos e retorna o seu indice */
int encontraVoo(char codigoVoo[MAX_COD_VOO], Data data) {
	int i;
	
	for (i=0; i<numVoos; i++)
		if ( (diasIguais(data, listaVoos[i].partida) && codigoVooIgual(codigoVoo, listaVoos[i].codigoVoo)) ) {
			return i;
		}

	return INVALIDO;
}

/* Verifica se o codigo de reserva e valido */
int validaCodReserva(char codigoReserva[]) {
	int i;

	for (i=0; codigoReserva[i] != '\0'; i++)
		if ( !( (codigoReserva[i] >= 'A' && codigoReserva[i] <= 'Z') || (codigoReserva[i] >= '0' && codigoReserva[i] <= '9') ) ) {
			return False;
		}

	if (i<MIN_COD_RESERVA) {
		return False;
	}

	return True;
}

/* Verifica se ja existe uma reserva com o mesmo codigo */
int novaReserva(char codigoReserva[]) {
	int i;
	Link aux;

	for (i=0; i<numVoos; i++) {
		aux = listaVoos[i].listaReservas;
		
		while (aux != NULL) {
			if ( !strcmp(aux->reserva.codReserva, codigoReserva) )
				return False;
			aux = aux->next;	
		}
	}
	return True;
}

/* Verifica se o numero de reservas nao excede a capacidade do voo*/
int limiteValido(int passageiros, int posicao) {
	return (listaVoos[posicao].totalReservas + passageiros) <= listaVoos[posicao].capacidade;
}

/* Verifica se a reserva e valida */
int reservaValida(Link node, int posicao, char codigoVoo[MAX_COD_VOO], Data data) {
	if (validaCodReserva(node->reserva.codReserva) == False)
		printf("invalid reservation code\n");
	else if (posicao == INVALIDO)
		printf("%s: flight does not exist\n", codigoVoo);
	else if (novaReserva(node->reserva.codReserva) == False)
		printf("%s: flight reservation already used\n", node->reserva.codReserva);
	else if (limiteValido(node->reserva.passageiros, posicao) == False)
		printf("too many reservations\n");
	else if (dataValida(data) == False);
	else if (node->reserva.passageiros <= 0)
		printf("invalid passenger number\n");
	else 
		return True;
	return False;
}

/* Adiciona uma reserva ao voo */
void adicionaReserva(int posicao, Link new) {
	listaVoos[posicao].totalReservas += new->reserva.passageiros;	
	new->next = listaVoos[posicao].listaReservas;
	listaVoos[posicao].listaReservas = new;
}	

/* Le uma reserva, valida-a e adiciona a reserva ao voo correspondente */
void criaReservas(char codigoVoo[MAX_COD_VOO], Data data) {
	Link node = leLink();
	int posicao = encontraVoo(codigoVoo, data);

	if (reservaValida(node, posicao, codigoVoo, data))
		adicionaReserva(posicao, node);
	else
		eliminaReserva(node);
}

/* Troca a posicao de duas reservas */
void swapReserva(Link l1, Link l2) {
	Reserva aux = l1->reserva;
	l1->reserva = l2->reserva;
	l2->reserva = aux;
}

/* Ordena as reservas de um dado voo */
void escreveReservasOrdenadas(Link head) {
	Link i, j, min;

	for (i=head; i!=NULL; i=i->next) {
		min = i;
		for (j=i->next; j!=NULL; j=j->next) 
			if (menor(j->reserva.codReserva, min->reserva.codReserva))
				min = j;
		swapReserva(min, i);
	}

	for (; head != NULL; head = head->next) {
		escreveReserva(head);
	}

}

/* Escreve as reservas, de forma ordenada de um dado voo */
void escreveReservas(char codigoVoo[MAX_COD_VOO], Data data) {
	int numVoo; 

	numVoo = encontraVoo(codigoVoo, data);
	if (numVoo == INVALIDO) {
		printf("%s: flight does not exist\n", codigoVoo);
		return;
	}
	if (!dataValida(data)){
		return;
	}
	escreveReservasOrdenadas(listaVoos[numVoo].listaReservas);
}

/* Verifica se o comando e para adicionar ou ler reservas */
void r() {
	char codigoVoo[MAX_COD_VOO];
	Data data;

	scanf("%s", codigoVoo);
	data = leData(INCOMPLETO);

	if (getchar() != '\n')
		criaReservas(codigoVoo, data);
	else
		escreveReservas(codigoVoo, data);
}

/* FUNCAO E */

/* Elimina uma reserva */
void removeElemento(Link anterior, Link reserva, int i) {
	if (anterior == NULL)					/* remocao do 1 elemento */
		listaVoos[i].listaReservas = reserva->next;
	else if(reserva->next == NULL)			/* remocao do ultimo elemento */
		anterior->next = NULL;
	else									/* remocao de elementos intermediarios */
		anterior->next = reserva->next;

}

/* Esta funcao procura por todos os voos, se a reserva existe, e depois elimina-a */
void eliminaReservaVoo(char codigo[]) {
	int i;
	Link reserva, anterior;

	for (i=0; i<numVoos; i++) {
		anterior = NULL;
		reserva = listaVoos[i].listaReservas;
		
		while (reserva != NULL) {
			if ( !strcmp(reserva->reserva.codReserva, codigo) ) {
				removeElemento(anterior, reserva, i);
				listaVoos[i].totalReservas -= reserva->reserva.passageiros;
				eliminaReserva(reserva);
				return;
			}
			anterior = reserva;
			reserva = reserva->next;
		}
	}
	printf("not found\n");
}

/* Decremente o numero de voos de um aeroporto */
void eliminaVooAeroporto(char codigo[MAX_ID]) {
	int i;

	for (i=0; i<numAeroportos; i++) {
		if ( !strcmp(codigo, listaAeroportos[i].id) ) {
			listaAeroportos[i].numeroVoos--;
		}
	}
}

/* Troca a posicao de dois voos */
void swapVoos(int p1, int p2) {
	Voo aux = listaVoos[p1];
	listaVoos[p1] = listaVoos[p2];
	listaVoos[p2] = aux;
}

/* Elimina um voo */
void eliminaVoo(char codigo[MAX_COD_VOO]) {
	int i = 0, codigoValido = False;

	while (i < numVoos) {
		if (!strcmp(codigo, listaVoos[i].codigoVoo)) {
			eliminaReservas(listaVoos[i].listaReservas);
			eliminaVooAeroporto(listaVoos[i].idPartida);
			swapVoos(i, --numVoos);
			codigoValido = True;
		} else
			i++;
	}
	if (codigoValido == False)
		printf("not found\n");
}

/* Verifica se o tipo de dado a eliminar e um voo ou um codigo de reserva */
void e(){
	char codigo[MAX_COD_RESERVA];
	scanf("%s", codigo);

	if (strlen(codigo) > MAX_COD_VOO)
		eliminaReservaVoo(codigo);
	else 
		eliminaVoo(codigo);	
}

/* Funcao main, recebe a opcao do utlizador e chama a funcao correspondente */
int main() {
	char comando;

	while ( (comando=getchar()) != 'q')
		switch(comando) {
		case 'a':
			a();
			break;
		case 'l':
			l();
			break;
		case 'v':
			v();
			break;
		case 'p':
			p();
			break;
		case 'c':
			c();
			break;
		case 't':
			t();
			break;
		case 'r':
			r();
			break;
		case 'e':
			e();
			break;
	}
	
	destroyReservas();
	return 0;
}
