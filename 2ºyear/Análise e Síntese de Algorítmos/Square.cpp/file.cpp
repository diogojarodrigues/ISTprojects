#include <iostream>
#include <stdio.h>
#include <stdlib.h>
#include <unordered_map>
#include <map>
#include <vector>

using namespace std;

unordered_map<long, long> combinacoesCalculadas;

/*
void printEscada(vector<int>* escada) {
	cout << "[";
	for (int i=0; i<(int)escada->size(); i++) {
		cout << i << ", ";
	}
	cout << "]";
	cout << endl;
}
*/

long key(vector<int>* escada);
int retiraQuadrado(vector<int>* escadaOriginal, int largura, int linha, int sq, vector<int>* novaEscada);
long retiraQuadrados(vector<int>* escadaOriginal, int largura, int linha);
long maiorComPossivel(vector<int>* escada, int largura);


int main() {
	vector<int> escada;
	int altura, aux, largura = -1;
	long combinacoes = 0;

	//Leitura de dados
	cin >> altura >> aux;        //aux le o valor da coluna, nao é usado
	for (int i=0; i<altura; i++) {
		cin >> aux;
		if (largura < aux)
			largura = aux;
		escada.push_back(aux);
	}

	//Calculo de combinacoes
	if (altura >= 1 && escada.at(altura-1) >= 1) {
		combinacoes++;
		combinacoes += maiorComPossivel(&escada, largura);
	}	

	cout << combinacoes << endl;
	return 0;
}

long key(vector<int>* escada) {
	long chave=0;

	for(int i=0; i<(int)escada->size(); i++) {
		chave*=10;
		chave+=escada->at(i);
	}

	return chave;
}

int retiraQuadrado(vector<int>* escadaOriginal, int largura, int linha, int sq, vector<int>* novaEscada) {
	int maxColuna = -1;

	//Verificar se pode ser retirado o quadrado
	if(linha+sq > (int)escadaOriginal->size())
		return -1;
	for(int i = 0; i<sq; i++) {
		if (escadaOriginal->at(linha+i) != largura)
			return -1;
	}

	//Alterar a escada
	novaEscada->clear();
	for (int i = 0; i<(int)escadaOriginal->size(); i++) {
		if (i>=linha && i<linha+sq)
			novaEscada->push_back(escadaOriginal->at(i) - sq);
		else
			novaEscada->push_back(escadaOriginal->at(i));

		if (maxColuna < novaEscada->at(i))
			maxColuna = novaEscada->at(i);
	}

	return maxColuna;
}

long retiraQuadrados(vector<int>* escadaOriginal, int largura, int linha) {
	vector<int> novaEscada;
	long combinacoes = 0;
	int novaLargura = 0, sq = 1;
		
	while (sq <= largura && (novaLargura = retiraQuadrado(escadaOriginal, largura, linha, sq, &novaEscada)) != -1) {	
		if (sq>=2)
			combinacoes++;	
		combinacoes += maiorComPossivel(&novaEscada, novaLargura);
		sq++;
	}			

	return combinacoes;
}

long maiorComPossivel(vector<int>* escada, int largura) {
	vector<int> novaEscada;
	long chave = key(escada);
	long combinacoes = 0;

	//Verifica se a combinacao já foi calculada
	if (combinacoesCalculadas.find(chave) != combinacoesCalculadas.end())
		return combinacoesCalculadas[chave];

	//Verifica se ja nao ha combinacoes
	if(largura<=1) {
		return 0;
	}
	
	for (int linha = 0; linha<(int)escada->size(); linha++) {
		if (escada->at(linha) == largura) {
			combinacoes = retiraQuadrados(escada, largura, linha);
			break;
		}
	}

	combinacoesCalculadas[chave] = combinacoes;
	return combinacoes;
}
