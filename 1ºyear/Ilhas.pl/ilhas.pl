%Diogo Rodrigues - 102848
:-[codigo_comum].

%Funcoes Auxiliares ---------------------------------------

/*
ultimo_elemento(L, Ultimo), em que L eh uma lista, e Ultimo eh uma lista de um elemento, que
corresponde ao ultimo elemento da lista L.
*/
ultimo_elemento([],[]):- !.
ultimo_elemento(L, [Ultimo]):-
    length(L, Comp),
    nth1(Comp, L, Ultimo).

/*
ultimo_elemento(L, Primeiro), em que L eh uma lista, e Primeiro eh uma lista de um elemento,
que corresponde ao primeiro elemento da lista L.
*/
primeiro_elemento([],[]):- !.
primeiro_elemento(L, [Primeiro]):-
    nth1(1, L , Primeiro).

/*
maior(E1, E2, Menor, Maior), E1 e E2 sao dois numeros inteiros, Menor eh o menor entre E1 e E2
e Maior eh o maior entre E1 e E2.
*/
maior(E1, E2, Menor, Maior):-
    E1>=E2,
    Menor = E2,
    Maior = E1.

maior(E1, E2, Menor, Maior):-
    E1<E2,
    Menor = E1,
    Maior = E2.


% Funcoes Principais ------------------------------------

/*
extrai_ilhas_linha(N_lilha, Linha, Ilhas), em que N_lilha eh um inteiro positivo,
correspondente ao numero de uma linha e Linha eh uma lista correspondente a uma linha
de um puzzle, significa que Ilhas eh a lista ordenada (ilhas da esquerda para a direita)
cujos elementos sao as ilhas da linha Linha. 
*/
extrai_ilhas_linha(N_lilha, Linha, Ilhas):-
    findall(ilha(N_ilha, (N_lilha, Indice)),
        (nth1(Indice, Linha, N_ilha),N_ilha>0),
        Ilhas).

/*
ilhas(Puz, Ilhas), em que Puz eh um puzzle, significa que Ilhas eh a lista ordenada
(ilhas da esquerda para a direita e de cima para baixo) cujos elementos sao as ilhas de
Puz.
*/
ilhas(Puz, Ilhas):-
   findall(Ilha,
        (nth1(N_ilha, Puz, Linha), extrai_ilhas_linha(N_ilha, Linha, Ilhas_linha), member(Ilha, Ilhas_linha)),
        Ilhas).

/*
vizinhas(Ilhas, Ilha, Vizinhas), em que Ilhas eh a lista de ilhas de um puzzle
e Ilha eh uma dessas ilhas, significa que Vizinhas eh a lista ordenada (ilhas de cima para
baixo e da esquerda para a direita ) cujos elementos sao as ilhas vizinhas de Ilha.
*/
vizinhas(Ilhas, ilha(_,(X_Ilha,Y_Ilha)), Vizinhas):-

    %Descobre todas as ilha acima da ilha de entrada
    findall(ilha(N_ilha, (X, Y)), (member(ilha(N_ilha, (X,Y)), Ilhas), Y==Y_Ilha, X<X_Ilha), Em_cima), 
    %Seleciona a ultima ilha (a mais proxima da ilha de entrada)
    ultimo_elemento(Em_cima, Cima),

    findall(ilha(N_ilha, (X, Y)), (member(ilha(N_ilha, (X,Y)), Ilhas), Y==Y_Ilha, X>X_Ilha), Em_baixo),
    primeiro_elemento(Em_baixo, Baixo),
     
    findall(ilha(N_ilha, (X, Y)), (member(ilha(N_ilha, (X,Y)), Ilhas), X==X_Ilha, Y<Y_Ilha), A_esquerda),
    ultimo_elemento(A_esquerda, Esquerda),

    findall(ilha(N_ilha, (X, Y)), (member(ilha(N_ilha, (X,Y)), Ilhas), X==X_Ilha, Y>Y_Ilha), A_direita),     
    primeiro_elemento(A_direita, Direita),
    append([Cima, Esquerda, Direita, Baixo], Vizinhas).

/*
estado(Ilhas, Estado), em que Ilhas eh a lista de ilhas de um puzzle, significa que
Estado eh a lista ordenada cujos elementos sao as entradas referentes a cada uma das
ilhas de Ilhas.
*/
estado(Ilhas, Estado):-
    findall([Ilha, Vizinhas, []], (member(Ilha, Ilhas), vizinhas(Ilhas, Ilha, Vizinhas)), Estado).

/*
posicoes_entre(Pos1, Pos2, Lista_pos), em que Pos1 e Pos2 sao posicoes, significa que
Lista_pos eh a lista ordenada de posicoes entre Pos1 e Pos2 (excluindo Pos1 e Pos2). 
Se Pos1 e Pos2 nao pertencerem a mesma linha ou a mesma coluna, o resultado eh false.
*/
posicoes_entre((X1, Y1), (X2, Y2), Lista_pos):-
    X1==X2,    %mesma coluna
    maior(Y1, Y2, Menor, Maior),
    findall((X1, Y), (between(Menor, Maior, Y), Y\==Y1, Y\==Y2), Lista_pos).

posicoes_entre((X1, Y1), (X2, Y2), Lista_pos):-
    Y1==Y2,    %mesma linha
    maior(X1, X2, Menor, Maior),
    findall((X, Y1), (between(Menor, Maior, X), X\==X1, X\==X2), Lista_pos).

/*
cria_ponte(Pos1, Pos2, Ponte), em que Pos1 e Pos2 sao 2 posicoes, significa
que Ponte eh uma ponte entre essas 2 posicoes.
*/
cria_ponte((X1, Y1), (X2, Y2), ponte((X1, Y1),(X2, Y2))):-X1 < X2, !.    %posicao por ordem
cria_ponte((X1, Y1), (X2, Y2), ponte((X1, Y1),(X2, Y2))):-X1 == X2, Y1=<Y2, !.
cria_ponte((X1, Y1), (X2, Y2), ponte((X2, Y2),(X1, Y1))):-X1 > X2, !.    %posicao trocada
cria_ponte((X1, Y1), (X2, Y2), ponte((X2, Y2),(X1, Y1))):-X1 == X2, Y1>Y2, !.

/*
caminho_livre(Pos1, Pos2, Posicoes, Iilha, Vizinha), em que Pos1 e Pos2 sao posicoes,
Posicoes eh a lista ordenada de posicoes entre Pos1 e Pos2, Ilha eh uma ilha, e Vizinha eh
uma das suas vizinhas, significa que a adicao da ponte ponte(Pos1, Pos2) nao faz
com que Ilha e Vizinha deixem de ser vizinhas.
*/
%Verifica se as posicoes correspondem a Ilha ou a ilha Vizinha 
caminho_livre(Pos1, _, _, ilha(_, Pos_ilha), _):- Pos1==Pos_ilha, !. 
caminho_livre(Pos1, _, _, _, ilha(_, Pos_vizinha)):- Pos1==Pos_vizinha, !.
caminho_livre(_, Pos2, _, ilha(_, Pos_ilha), _):- Pos2==Pos_ilha, !. 
caminho_livre(_, Pos2, _, _, ilha(_, Pos_vizinha)):- Pos2==Pos_vizinha, !.

caminho_livre(_, _, Posicoes, ilha(_, Pos_ilha), ilha(_, Pos_vizinha)):-
    posicoes_entre(Pos_ilha, Pos_vizinha, Pos_entre_ilhas),
    \+ (member(Pos, Posicoes), member(Pos, Pos_entre_ilhas)).

/*
actualiza_vizinhas_entrada(Pos1, Pos2, Posicoes, Entrada,Nova_entrada), em que Pos1 e Pos2 
sao as posicoes entre as quais ira ser adicionada uma ponte, Posicoes eh a lista ordenada
de posicoes entre Pos1 e Pos2, e Entrada eh uma entrada, significa que Nova_entrada eh igual a 
Entrada, excepto no que diz respeito a lista de ilhas vizinhas; esta deve ser actualizada,
removendo as ilhas que deixaram de ser vizinhas, apos a adicao da ponte.
*/
actualiza_vizinhas_entrada(Pos1, Pos2, Posicoes, [Ilha, Vizinhas, Pontes], [Ilha, Ilhas_livres, Pontes]):-
    include(caminho_livre(Pos1, Pos2, Posicoes, Ilha), Vizinhas, Ilhas_livres).

/*
actualiza_vizinhas_apos_pontes(Estado, Pos1, Pos2, Novo_estado) ,
em que Estado eh um estado, Pos1 e Pos2 sao as posicoes entre as
quais foi adicionada uma ponte, significa que Novo_estado eh o estado que se obtem de
Estado apos a atualizacao das ilhas vizinhas de cada uma das suas entradas
*/
actualiza_vizinhas_apos_pontes(Estado, Pos1, Pos2, Novo_Estado):-
    posicoes_entre(Pos1, Pos2, Posicoes),
    maplist(actualiza_vizinhas_entrada(Pos1, Pos2, Posicoes) , Estado, Novo_Estado).

/*
ilhas_terminadas(Estado, Ilhas_term), em que Estado eh um estado, significa que Ilhas_term eh
a lista de ilhas que ja tem todas as pontes associadas, designadas por ilhas terminadas. 
Se a entrada referente a uma ilha for [ilha(N_pontes,Pos), Vizinhas, Pontes], esta ilha esta 
terminada se N_pontes for diferente de 'X' (a razao para esta condicao ficara aparente mais 
a frente) e o comprimento da lista Pontes for N_pontes.
*/
ilhas_terminadas(Estado, Ilhas_term):-
    findall(ilha(N_ilha, Coordenadas),

        (member(Entrada, Estado),
         nth1(1, Entrada, ilha(N_ilha, Coordenadas)),
         nth1(3, Entrada, Pontes),
         N_ilha \== 'X',
         length(Pontes, N_ilha)),

    Ilhas_term).

/*
tira_ilhas_terminadas_entrada(Ilhas_term, Entrada, Nova_entrada),
em que Ilhas_term eh uma lista de ilhas terminadas e Entrada eh uma entrada, significa que 
Nova_entrada eh a entrada resultante de remover as ilhas de Ilhas_term, da lista de ilhas 
vizinhas de entrada.
*/
tira_ilhas_terminadas_entrada(Ilhas_term, [Ilha, Vizinhas, Pontes], [Ilha, Ilhas_nao_terminadas, Pontes]):-
    findall(Vizinha, (member(Vizinha, Vizinhas), \+member(Vizinha, Ilhas_term)), Ilhas_nao_terminadas).

/*
tira_ilhas_terminadas(Estado, Ilhas_term, Novo_estado), em que Estado eh um estado e 
Ilhas_term eh uma lista de ilhas terminadas, significa que Novo_estado eh o estado resultante 
de aplicar o predicado tira_ilhas_terminadas_entrada a cada uma das entradas de Estado.
*/    
tira_ilhas_terminadas(Estado, Ilhas_term, Novo_Estado):-
    maplist(tira_ilhas_terminadas_entrada(Ilhas_term), Estado, Novo_Estado).

/*
marca_ilhas_terminadas_entrada(Ilhas_term, Entrada,Nova_entrada), 
em que Ilhas_term eh uma lista de ilhas terminadas e Entrada
eh uma entrada, significa que Nova_entrada eh a entrada obtida de
Entrada da seguinte forma: se a ilha de Entrada pertencer a Ilhas_term, o numero
de pontes desta eh substituido por 'X', em caso contrario Nova_entrada eh igual a Entrada
*/
marca_ilhas_terminadas_entrada(Ilhas_term, [ilha(N_ilha, Coordenadas), Vizinhas, Pontes], [ilha('X', Coordenadas), Vizinhas, Pontes]):-
    member(ilha(N_ilha, Coordenadas), Ilhas_term), !.

marca_ilhas_terminadas_entrada(_, Estado, Estado).

/*
marca_ilhas_terminadas(Estado, Ilhas_term, Novo_estado), em que
Estado eh um estado e Ilhas_term eh uma lista de ilhas terminadas, significa que Novo_estado 
eh o estado resultante de aplicar o predicado marca_ilhas_terminadas_entrada a cada uma
das entradas de Estado.
*/
marca_ilhas_terminadas(Estado, Ilhas_term, Novo_Estado):-
    maplist(marca_ilhas_terminadas_entrada(Ilhas_term), Estado, Novo_Estado).

/*
trata_ilhas_terminadas(Estado, Novo_estado), em que Estado eh um estado, significa que
Novo_estado eh o estado resultante de aplicar os predicados 
tira_ilhas_terminadas e marca_ilhas_terminadas a Estado.
*/
trata_ilhas_terminadas(Estado, Novo_Estado):-
    ilhas_terminadas(Estado, Ilhas_term),
    tira_ilhas_terminadas(Estado, Ilhas_term, Estado_intermedio),
    marca_ilhas_terminadas(Estado_intermedio, Ilhas_term, Novo_Estado).

/*
junta_pontes(Estado, Num_pontes, Ilha1, Ilha2, Novo_estado), em
que Estado eh um estado e Ilha1 e Ilha2 sao 2 ilhas, significa que Novo_estado eh
o estado que se obtem de Estado por adicao de Num_pontes pontes entre Ilha1 e
Ilha2 .
*/
junta_pontes(Estado, Num_Pontes, ilha(_, Pos1), ilha(_, Pos2), Estado_final):-
    cria_ponte(Pos1, Pos2, Ponte),
    length(Lista_pontes, Num_Pontes),
    findall(Ponte, member(_, Lista_pontes), Pontes),
    %Pontes eh uma lista de Num_Pontes elementos, em que todos os elementos sao iguais a ponte criada

    findall(Nova_entrada,
        (member(Entrada, Estado),
        junta_pontes_entrada(Pos1, Pos2, Pontes, Entrada, Nova_entrada)),   %adiciona as Pontes as ilhas correspondentes
    Estado_apos_pontes),
    
    actualiza_vizinhas_apos_pontes(Estado_apos_pontes, Pos1, Pos2, Estado_intermedio),
    trata_ilhas_terminadas(Estado_intermedio, Estado_final).

/*
junta_pontes_entrada(Pos1, Pos2, Pontes, Entrada, Nova_entrada), em que Pos1 e Pos2 sao duas posicoes, Pontes eh a lista
que contem as pontes criadas, Entrada corresponde a uma entrada do estado, e Nova_entrada corresponde a adicao de Pontes,
as pontes ja existentes na Entrada, caso as posicoes das ilhas coincidirem com uma extremidade da ponte
*/  
junta_pontes_entrada(Pos1, _, Pontes, [ilha(N, Pos), Vizinhas, Pontes_antigas], [ilha(N, Pos), Vizinhas, Pontes_novas]):-
    Pos == Pos1, !,         %A ponte tem uma extremidade nesta posicao
    append(Pontes_antigas, Pontes, Pontes_novas).

junta_pontes_entrada(_, Pos2, Pontes, [ilha(N, Pos), Vizinhas, Pontes_antigas], [ilha(N, Pos), Vizinhas, Pontes_novas]):-
    Pos == Pos2, !,         %A ponte tem uma extremidade nesta posicao
    append(Pontes_antigas, Pontes, Pontes_novas).

junta_pontes_entrada(_, _, _, Entrada, Entrada).   %A ponte nao passa nesta posicao, a entrada mantem-se