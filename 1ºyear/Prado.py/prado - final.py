'''
13/11/2021
Diogo Rodrigues - 102848
diogojarodrigues@gmail.com

Este programa tem o objetivo de simular um ecossistema entre animais, mais concretamente entre presas e predadores, em que ambos os animais se movimentam, alimentam, reproduzem e morrem.
O programa irá prever como o sistema se desenvolverá ao longo das gerações
'''

#	Funções Auxiliares
def inteiro_maiorQueMinimo(n,minimo):
	'''
	inteiro_maiorQueMinimo: inteiro x inteiro --> booleano
	Esta função serve para validar argumentos.
	Verifica se o 1º parâmetro é do tipo inteiro e se é maior ou igual que o 2º parâmetro
	'''
	return type(n)==int and n>=minimo

'''
TAD posicao: tuplo --> (int,int)

cria_posicao: int × int --> posição
cria_copia_posicao: posicao --> posicao
obter_pos_x : posicao --> int
obter_pos_y : posicao --> int
eh_posicao: universal --> booleano
posicoes_iguais: posicao × posicao --> booleano
posicao_para_str : posicao --> str
'''
def cria_posicao(x,y):
	'''
	cria_posicao: int × int --> posicao
	Esta função recebe dois argumentos, e se estes forem validos, retorna um elemento do tipo posição
	'''
	if inteiro_maiorQueMinimo(x,0) and inteiro_maiorQueMinimo(y,0):
		return (x,y)
	raise ValueError('cria_posicao: argumentos invalidos')

def cria_copia_posicao(p):
	'''
	cria_copia_posicao: posicao --> posicao
	Esta função retorna uma cópia da posição recebida
	'''
	p = cria_posicao(obter_pos_x(p),obter_pos_y(p))
	return p

def obter_pos_x(p):
	'''	
	obter_pos_x : posicao --> int
	A função retorna a abcissa(coluna do prado) da posição
	'''
	return p[0]

def obter_pos_y(p):
	'''	
	obter_pos_y : posicao --> int
	A função retorna a ordenada(linha do prado) da posição
	'''
	return p[1]

def eh_posicao(arg):
	'''
	eh_posicao: universal --> booleano
	A função retorna True se o argumento recebido pertencer ao TAD posicao
	'''
	return type(arg)==tuple and len(arg)==2 and inteiro_maiorQueMinimo(obter_pos_x(arg),0) and inteiro_maiorQueMinimo(obter_pos_y(arg),0)

def posicoes_iguais(p1,p2):
	'''
	posicoes_iguais: posicao × posicao --> booleano
	Esta função retorna true se os dois argumentos corresponderem a uma posição, e se estes forem iguais
	'''
	return eh_posicao(p1) and eh_posicao(p2) and obter_pos_x(p1)==obter_pos_x(p2) and obter_pos_y(p1)==obter_pos_y(p2)

def posicao_para_str(p):
	'''
	posicao_para_str : posicao --> str
	Esta função retorna uma string que representa um elemento do tipo posicao
	'''
	return f'({obter_pos_x(p)}, {obter_pos_y(p)})'

#posicao - Alto Nível 
def obter_posicoes_adjacentes(p):
	'''
	Obter_posicoes_adjacentes: posicao --> tuplo
	A função retorna um tuplo com as posições adjacentes à posição recebida, no sentido do relogio, começando pela posição de cima
	'''
	x = obter_pos_x(p)
	y = obter_pos_y(p)
	t = ()
	if y > 0:				
		t += ( cria_posicao(x, y-1) ,)		#cima

	t+= ( cria_posicao(x + 1, y) ,) 		#direita
	t+= ( cria_posicao(x, y + 1) ,)			#baixo
	if x > 0:				
		t += ( cria_posicao(x - 1, y) ,) 	#esquerda
	
	return t

def ordenar_posicoes(t):
	'''
	ordenar_posicoes: tuplo --> tuplo
	Ordena o tuplo recebido, de acordo com as regras de leitura do prado
	'''
	return tuple(sorted(t, key=lambda x: (obter_pos_y(x),obter_pos_x(x))))


'''
TAD animal: dicionario --> {str:str, str:int, str:int, str:int, str:int}

cria_animal: str × int × int --> animal
cria_copia_animal: animal --> animal
obter_especie: animal --> str
obter_freq_reproducao: animal --> int
obter_freq_alimentacao: animal --> int
obter_idade: animal --> int
obter_fome: animal --> int
aumenta_idade: animal --> animal
reset_idade: animal --> animal
aumenta_fome: animal --> animal
reset_fome: animal --> animal
eh_animal: universal --> booleano
eh_predador: universal --> booleano
eh_presa: universal --> booleano
animais_iguais: animal × animal --> booleano
animal_para_char : animal --> str
animal_para_str : animal --> str
'''
def cria_animal(especie,reprodução,alimentacao):
	'''
	cria_animal: str × int × int --> animal
	Esta função recebe uma string e dois inteiros, valida-os, e cria uma posicao (TAD)
	'''
	if type(especie)==str and especie!='' and inteiro_maiorQueMinimo(reprodução,1) and inteiro_maiorQueMinimo(alimentacao,0):
		return {'especie': especie, 'idade':0, 'reproducao': reprodução, 'fome':0, 'alimentacao': alimentacao}
	raise ValueError('cria_animal: argumentos invalidos')

def cria_copia_animal(ani):
	'''
	cria_copia_animal: animal --> animal
	Cria uma cópia do animal recebido
	'''
	return ani.copy()

def obter_especie(ani):
	'''
	obter_especie: animal --> str
	A função recebe um animal e retorna a sua espécie
	'''
	return ani['especie']

def obter_freq_reproducao(ani):
	'''
	obter_freq_reproducao: animal --> int
	A função recebe um animal e retorna a sua frequência de reprodução
	'''
	return ani['reproducao']

def obter_freq_alimentacao(ani):
	'''
	obter_freq_alimentacao: animal --> int
	A função recebe um animal e retorna a sua frequência de alimentação
	'''
	return ani['alimentacao']

def obter_idade(ani):
	'''
	obter_idade: animal --> int
	A função recebe um animal e retorna a sua idade
	'''
	return ani['idade']

def obter_fome(ani):
	'''
	obter_fome: animal --> int
	A função recebe um animal e retorna a sua fome
	'''
	return ani['fome']

def aumenta_idade(ani):
	'''
	aumenta_idade: animal --> animal
	A função incrementa a idade do animal em uma unidade
	'''
	ani['idade'] += 1
	return ani

def reset_idade(ani):
	'''
	reset_idade: animal --> animal
	A função dá "reset" ao valor da idade, alterando-o para 0
	'''
	ani['idade'] = 0
	return ani

def aumenta_fome(ani):
	'''
	aumenta_fome: animal --> animal
	A função incrementa a idade do fome em uma unidade
	'''
	if eh_predador(ani):
		ani['fome'] += 1
	return ani

def reset_fome(ani):
	'''
	reset_fome: animal --> animal
	A função dá "reset" ao valor da fome, alterando-o para 0
	'''
	if eh_predador(ani):
		ani['fome'] = 0
	return ani

def eh_animal(arg):
	'''
	eh_animal: universal --> booleano
	Esta função recebe um argumento e retorna True se este pertencer ao TAD Animal
	'''

	#podias usar for key in ["alimentacao", "fome", "idade"] para verficar os arguemntos

	return type(arg)==dict and len(arg)== 5 and 'especie' in arg and 'reproducao' in arg and 'alimentacao' in arg  and 'idade' in arg and 'fome' in arg\
	and type(arg['especie'])==str and arg['especie']!='' and type(arg['reproducao'])==int and arg['reproducao'] > 0 and type(arg['alimentacao'])==int and arg['alimentacao'] >= 0\
	and type(arg['idade']) == int and arg['idade'] >= 0 and type(arg['fome']) == int and arg['fome'] >= 0

def eh_predador(arg):
	'''
	eh_predador : universal --> booleano
	Esta função recebe um argumento e retorna True se este pertencer ao TAD Animal e for um predador
	'''
	return eh_animal(arg) and obter_freq_alimentacao(arg)>0

def eh_presa(arg):
	'''
	eh_presa: universal --> booleano
	Esta função recebe um argumento e retorna True se este pertencer ao TAD Animal e for uma presa
	'''
	return eh_animal(arg) and obter_freq_alimentacao(arg) == 0

def animais_iguais(a1,a2):
	'''
	animais_iguais: animal × animal --> booleano
	A função recebe dois argumentos, e caso estes sejam ambos animais e forem iguais retorna True
	'''
	return eh_animal(a1) and eh_animal(a2) and a1==a2

def animal_para_char(ani):
	'''
	animal_para_char : animal --> str
	Esta função recebe um animal e retorna um char que o representa
	'''
	if obter_freq_alimentacao(ani)==0:
		return obter_especie(ani)[0].lower()
	return obter_especie(ani)[0].upper()

def animal_para_str(ani):
	'''
	animal_para_str : animal --> str
	Esta função recebe um animal e retorna uma string que o representa
	'''
	if eh_predador(ani):
		return f"{obter_especie(ani)} [{obter_idade(ani)}/{obter_freq_reproducao(ani)};{obter_fome(ani)}/{obter_freq_alimentacao(ani)}]"
	return f"{obter_especie(ani)} [{obter_idade(ani)}/{obter_freq_reproducao(ani)}]"

#animal - Alto Nível
def eh_animal_fertil(ani):
	'''
	eh_animal_fertil: animal --> booleano
	Recebe um animal e retorna True se estiver em idade fértil (idade == frequência de reprodução)
	'''
	return obter_idade(ani)>=obter_freq_reproducao(ani)

def eh_animal_faminto(ani):
	'''
	eh_animal_faminto: animal --> booleano
	Recebe um animal e retorna True se estiver faminto (fome == frequência de alimentação)
	'''
	if eh_predador(ani) and obter_fome(ani)>=obter_freq_alimentacao(ani):
		return True
	return False

def reproduz_animal(ani):
	'''
	reproduz_animal: animal --> animal
	Recebe um animal e retorna uma cópia do original, em que a idade em ambos é igualada a zero e a fome da cópia também
	'''
	ani = reset_idade(ani)
	copia = cria_copia_animal(ani)
	copia = reset_fome(copia)
	return copia


'''
TAD Prado: tuplo --> (int,int,tuplo,dicionario)

cria_prado: posicao × tuplo × tuplo × tuplo --> prado
cria_copia_prado: prado --> prado
obter_tamanho_x: prado --> int
obter_tamanho_y: prado --> int
obter_numero_predadores: prado --> int
obter_numero_presas: prado --> int
obter_posicao_animais: prado --> tuplo posicoes
obter_animal: prado × posicao --> animal
eliminar_animal: prado × posicao --> prado
mover_animal: prado × posicao × posicao --> prado
inserir_animal: prado × animal × posicao --> prado
eh_prado: universal --> booleano
eh_posicao_animal: prado × posicao --> booleano
eh_posicao_obstaculo: prado × posicao --> booleano
eh_posicao_livre: prado × posicao --> booleano
prados_iguais: prado × prado --> booleano
prado_para_str: prado --> str
'''
#prado - Funções Auxiliares Validação
def validacao_tuplo(arg,pred):
	'''
	validacao_tuplo: universal x funcao --> booleano
	Esta função serve para validar um argumento. Se o argumento recebido for do tipo tuple e todos os seus elementos\
	obedecerem à condição(predicado) então a função retorna True
	'''
	if type(arg) != tuple:
		return False
	for elemento in arg:
		if not pred(elemento):
			return False
	return True

def pos_dentro_limites(t_pos,largura,comprimento):
	'''
	pos_dentro_limites: tuplo x inteiro x inteiro --> booleano
	Esta função verifica se os elementos do tuplo recebido (posições) estão dentro dos limites do prado
	'''
	for pos in t_pos:
		if not (0<obter_pos_x(pos)<largura and 0<obter_pos_y(pos)<comprimento):
			return False
	return True

def cria_prado(d,r,a,p):
	'''
	cria_prado: posicao × tuplo × tuplo × tuplo --> prado
	Esta função recebe 4 argumentos, valida-os e caso estes sejam verdadeiros cria um elemento do tipo prado (TAD) 
	'''	
	def cria_animais(a,p):
		def pos_para_tuplo(pos):
			return (obter_pos_x(pos),obter_pos_y(pos))
		'''
		cria_animais: tuple x tuple --> dicionario
		Esta função recebe um tuplo com n animais, e outro tuplo com n posições referentes aos animais
		Cria um dicionário com tamanho n, em que as chaves são as posições e os valores são os animais
		'''
		for pos in p:
			for roc in r:
				if posicoes_iguais(pos,roc):
					raise ValueError('cria_prado: argumentos invalidos')

		animais = {}
		for i in range(len(a)):
			animais[pos_para_tuplo(p[i])]=a[i]
		return animais
	
	if eh_posicao(d) and inteiro_maiorQueMinimo(obter_pos_x(d),1) and inteiro_maiorQueMinimo(obter_pos_y(d),1):
		largura = obter_pos_x(d)
		comprimento = obter_pos_y(d)

	if validacao_tuplo(r,eh_posicao) and validacao_tuplo(a,eh_animal) and validacao_tuplo(p,eh_posicao) and len(a) > 0\
	and len(a)==len(p) and pos_dentro_limites(r,largura,comprimento) and pos_dentro_limites(p,largura,comprimento):

		animais = cria_animais(a,p)
		return (largura, comprimento,r, animais)

	raise ValueError('cria_prado: argumentos invalidos')

def cria_copia_prado(prado):
	'''
	cria_copia_prado: prado --> prado
	Esta função recebe um prado e retorna uma cópia do prado original
	'''	
	animais_copia = {}
	for pos,ani in prado[3].items():
		animais_copia[pos] = ani
	return (prado[0],prado[1],prado[2], animais_copia)

def obter_tamanho_x(prado):
	'''
	obter_tamanho_x: prado --> int
	Devolve o valor inteiro que corresponde à dimensão Nx (nº de colunas) do prado
	'''	
	return prado[0] + 1

def obter_tamanho_y(prado):
	'''
	obter_tamanho_y: prado --> int
	Devolve o valor inteiro que corresponde à dimensão Ny (nº de linhas) do prado
	'''	
	return prado[1] + 1

def obter_numero_predadores(prado):
	'''
	obter_numero_predadores: prado --> int
	Devolve o valor inteiro que correponde ao número de predadores
	'''	
	cont = 0
	for ani in prado[3].values():
		if eh_predador(ani):
			cont += 1
	return cont

def obter_numero_presas(prado):
	'''
	obter_numero_presas: prado --> int
	Devolve o valor inteiro que correponde ao número de presas
	'''	
	cont = 0
	for ani in prado[3].values():
		if eh_presa(ani):
			cont += 1
	return cont

def obter_posicao_animais(prado):
	'''
	obter_posicao_animais: prado --> tuplo posicoes
	Esta função recebe um prado e retorna todas as posições em que existem animais, ordenadamente
	'''	
	lista = []
	for pos in prado[3]:
		lista.append(cria_posicao(pos[0],pos[1]))

	return tuple(ordenar_posicoes(lista))

def obter_animal(prado,pos):
	'''
	obter_animal: prado × posicao --> animal
	A função recebe um prado e uma posição e retorna o animal, que corresponde à posição no prado
	'''	
	return prado[3][(obter_pos_x(pos),obter_pos_y(pos))]

def eliminar_animal(prado,pos):
	'''
	eliminar_animal: prado × posicao --> prado
	Esta função elimina o animal que se encontra nesta posição do prado
	'''	
	del prado[3][(obter_pos_x(pos),obter_pos_y(pos))]
	return prado

def mover_animal(prado,p1,p2):
	'''
	mover_animal: prado × posicao × posicao --> prado
	Esta função move o animal de uma posição(p1) para a outra(p2)
	'''	
	prado[3][(obter_pos_x(p2),obter_pos_y(p2))] = prado[3].pop((obter_pos_x(p1),obter_pos_y(p1)))
	return prado

def inserir_animal(prado,ani,pos):
	'''
	inserir_animal: prado × animal × posicao --> prado
	Esta função insere o animal que se encontra nesta posição do prado
	'''	
	prado[3][(obter_pos_x(pos),obter_pos_y(pos))]=ani
	return prado

def eh_prado(arg):
	'''
	eh_prado: universal --> booleano
	A função recebe um elemento e valida-o, se este argumento representar um prado, a função retorna True
	'''	
	return type(arg)==tuple and len(arg)==4 and inteiro_maiorQueMinimo(arg[0],1) and inteiro_maiorQueMinimo(arg[1],1)\
	and type(arg[2])==tuple and validacao_tuplo(arg[2],eh_posicao) and pos_dentro_limites(arg[2],arg[0],arg[1])\
	and type(arg[3])==dict and len(arg[3])>0 and validacao_tuplo(tuple(arg[3].keys()),eh_posicao)\
	and pos_dentro_limites(tuple(arg[3].keys()),arg[0],arg[1]) and validacao_tuplo(tuple(arg[3].values()),eh_animal)

def eh_posicao_animal(prado,pos):
	'''
	eh_posicao_animal: prado × posicao --> booleano
	Esta função retorna True se receber um prado e na posição recebida existir um animal
	'''	
	posicoes_animais = obter_posicao_animais(prado)
	for p in posicoes_animais:
		if posicoes_iguais(p,pos):
			return True
	return False

def eh_posicao_obstaculo(prado,pos):
	'''
	eh_posicao_obstaculo: prado × posicao --> booleano
	Esta função retorna True se receber um prado e na posição recebida existir um obstáculo
	'''	
	if not (eh_prado(prado) and eh_posicao(pos)):
		return False
	if obter_pos_x(pos) == 0 or obter_pos_x(pos) == prado[0] or obter_pos_y(pos) == 0 or obter_pos_y(pos) == prado[1]:
		return True
	for p in prado[2]:
		if posicoes_iguais(p,pos):
			return True
	return False

def eh_posicao_livre(prado,pos):
	'''
	eh_posicao_livre: prado × posicao --> booleano
	Esta função retorna True se receber um prado e na posição recebida existir um espaço livre no prado
	'''	
	return not( eh_posicao_animal(prado,(obter_pos_x(pos),obter_pos_y(pos))) or eh_posicao_obstaculo(prado,(obter_pos_x(pos),obter_pos_y(pos))))

def prados_iguais(p1,p2):
	'''
	prados_iguais: prado × prado --> booleano
	Esta função recebe dois argumentos, e retorna True se estes forem iguais e se ambos forem prados
	'''	
	return eh_prado(p1) and eh_prado(p2) and p1==p2

def prado_para_str(prado):
	'''
	prado_para_str : prado --> str
	A função devolve um string que representa um prado
	'''	
	prado_str = ''
	largura = obter_tamanho_x(prado)
	comprimento = obter_tamanho_y(prado)
	for y in range(comprimento):
		for x in range(largura):
			pos = cria_posicao(x,y)    #Estes dois ciclos permitem correr todas as posições do prado(incluindo as livres)
			if (x == 0 or x == largura-1) and (y == 0 or y == comprimento-1): #É um canto
				prado_str+='+'					
			elif x == 0 or x == largura-1:		#É um limite lateral
				prado_str+='|'
			elif y == 0 or y == comprimento-1:	#É um limite frontal
				prado_str+='-'
			elif eh_posicao_obstaculo(prado,pos):	#É uma rocha
				prado_str+='@'
			elif eh_posicao_animal(prado,pos):	#É um animal	
				prado_str+= f'{animal_para_char(obter_animal(prado,pos))}'
			else:
				prado_str+='.'				#É um espaço vazio	
		if x!=largura-1 or y!=comprimento-1:
			prado_str+='\n'   #Fim de uma linha do prado
	return prado_str

def obter_valor_numerico(prado,pos):
	'''
	obter_valor_numerico: prado × posicao --> int
	Devolve o valor numérico da posição pos correspondente à ordem de leitura no prado
	'''	
	return obter_tamanho_x(prado)*(obter_pos_y(pos)) +  obter_pos_x(pos)

def obter_movimento(prado,pos):
	'''
	obter_movimento: prado × posicao --> posicao
	Esta função retorna a posição para qual o animal se irá deslocar
	Caso todas as posições adjacentes estejam ocupadas então ele permanecerá na mesma posição
	'''	
	def movimento(prado,func):
		posicao_possiveis = []
		for p in posicoes_adjacentes:
			if func(prado,p):	#se a posição p obdecer as regras(função) então esta passa a ser uma posição possível
				posicao_possiveis.append(p)

		if len(posicao_possiveis) > 0:		#se nenhuma posição for possivel então ele permanece na mesma
			return posicao_possiveis[obter_valor_numerico(prado,pos)%len(posicao_possiveis)]
		return pos
			
	posicoes_adjacentes = obter_posicoes_adjacentes(pos)
	posicao_animais = obter_posicao_animais(prado)

	if eh_predador(obter_animal(prado,pos)):	#o predador primeiro vê-se pode deslocar para uma posição onde existe uma presa
		proxima_posicao = movimento(prado,lambda x,y: y in posicao_animais and eh_presa(obter_animal(x,y)))  
		if proxima_posicao != pos:
			return proxima_posicao
		
	return movimento(prado, lambda x,y: eh_posicao_livre(x,y))  #posição para qual a presa, ou caso o predador não tenha consiguido "comer" uma presa, se irá deslocar

def geracao(prado):
	'''
	geracao: prado --> prado
	Esta função recebe um prado, e faz a simulação dos acontecimentos que ocorrem no prado durante uma geração
	Nomeadamente a morte, reprodução, alimentação e movimentação dos animais
	'''
	posicao_animais = obter_posicao_animais(prado)
	ja_moveu = {x:False for x in posicao_animais}

	for p in posicao_animais:
		if not ja_moveu[p]:
			ja_moveu[p] = True
			ani = obter_animal(prado,p)
			p_seguinte = obter_movimento(prado,p)
			ani = aumenta_fome(ani)
			ani = aumenta_idade(ani)

			if eh_posicao_animal(prado,p_seguinte):
				ani = reset_fome(ani)

			prado = mover_animal(prado,p,p_seguinte)
			if p!=p_seguinte:
				ja_moveu[p_seguinte] = True
				if eh_animal_fertil(ani):
					prado = inserir_animal(prado,reproduz_animal(ani),p)
					ani = reset_idade(ani)
			
			if eh_animal_faminto(ani):
				prado = eliminar_animal(prado,p_seguinte)
	return prado
	
def simula_ecossistema(ficheiro,n_geracao,modo):
	'''
	simula_ecossistema: str × int × booleano --> tuplo
	Esta função recebe uma string, que representa um documento de texto com as informações do prado, um inteiro\
	que representa o número de gerações a simular e um booleano v, que representa o modo\
	como se deve mostrar as simulações ao utilizador
	'''	
	def mostra(prado,P,p,g):
		print(f'Predadores: {P} vs Presas: {p} (Gen. {g})')
		print(prado_para_str(prado))

	with open(ficheiro,'r') as config: #Extração dos dados do documento de texto
		tamanho_txt = eval(config.readline())		
		rochas_txt = eval(config.readline())		
		linhas = (config.readlines())
	animais_txt = []
	posicoes_txt = []	
	for l in linhas:			#Manipulação dos dados			
		animais_txt += [eval(l)[0:3]]		
		posicoes_txt += [eval(l)[3]]		

	tamanho = cria_posicao(tamanho_txt[0],tamanho_txt[1])						#posicao que representa o canto inferior direito
	rochas = tuple([cria_posicao(x[0],x[1]) for x in rochas_txt])				#tuplo que representa as posições das rochas
	animais = tuple([cria_animal(x[0],x[1],x[2]) for x in animais_txt])			#tuplo que representa os animais
	posicoes_animais = tuple([cria_posicao(x[0],x[1]) for x in posicoes_txt])	#tuplo que representa as posições dos animais
	prado = cria_prado(tamanho,rochas,animais,posicoes_animais)

	mostra(prado, obter_numero_predadores(prado), obter_numero_presas(prado),0) #Geração 0

	for i in range(1,n_geracao+1):

		prado = geracao(prado)
		Predadores = obter_numero_predadores(prado)
		presas = obter_numero_presas(prado)

		if modo:		#Se o modo verboso estiver ativado mostra todas as gerações em que houve alterações nos números dos animais
			if i!=1 and numero_prado!=(Predadores,presas):
				mostra(prado, Predadores, presas,i)
		elif i==n_geracao:	#Se o modo verboso estiver desligado, mostra a última geração
			mostra(prado, Predadores, presas,i)
		numero_prado = (Predadores,presas)

	return numero_prado
	