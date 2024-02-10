import numpy as np

from scipy.stats import bernoulli, multivariate_normal
from sklearn.metrics import pairwise_distances

# Corre o código com o comando: python3 Homework\ 4/src/pen\&paper.py > Homework\ 4/src/resultados.txt 

#DADOS

x0 = (1, 0.6, 0.1)                      #x0[0] = y1, x0[1] = y2, x0[2] = y3
x1 = (0, -0.4, 0.8)
x2 = (0, 0.2, 0.5)
x3 = (1, 0.4, -0.1)

dados = [x0, x1, x2, x3]

u1 = np.array([1, 1])
u2 = np.array([0, 0])

cov1 = np.matrix([[2, 0.5], [0.5, 2]])
cov2 = np.matrix([[1.5, 1], [1, 1.5]])

#DISTRIBUIÇÕES

ber1 = bernoulli(0.3)
ber2 = bernoulli(0.7)

mvn1 = multivariate_normal(u1, cov1)
mvn2 = multivariate_normal(u2, cov2)

pi_1 = {"prior": 0.5, "binomial": ber1, "normal": mvn1}
pi_2 = {"prior": 0.5, "binomial": ber2, "normal": mvn2}

#FUNÇÕES

def calcula_posterior(x):

    prob_1 = pi_1["prior"] * pi_1["binomial"].pmf(x[0]) * pi_1["normal"].pdf(x[1:3])
    prob_2 = pi_2["prior"] * pi_2["binomial"].pmf(x[0]) * pi_2["normal"].pdf(x[1:3])

    pi_1_x = prob_1/(prob_1 + prob_2)
    pi_2_x = prob_2/(prob_1 + prob_2)

    return {"pi_1": pi_1_x, "pi_2": pi_2_x}

def update_priors():
    return {"p1_1": N_k["pi_1"]/N, "p1_2": N_k["pi_2"]/N}

def update_mean():

    def aux(cluster, variavel):
        num = 0

        for index, posterior in enumerate(posteriores):
            num += posterior[cluster] * dados[index][variavel]

        return num/N_k[cluster]

    pi_1_mean = {"p_sucesso": aux("pi_1", 0), "mean": [aux("pi_1", 1), aux("pi_1", 2)]}
    pi_2_mean = {"p_sucesso": aux("pi_2", 0), "mean": [aux("pi_2", 1), aux("pi_2", 2)]}

    return {"pi_1": pi_1_mean, "pi_2": pi_2_mean}

def update_variance(mean):
    def aux(cluster):
        num = 0

        for index, posterior in enumerate(posteriores):
            mean = updated_mean[cluster]["mean"]
            x = np.array(dados[index][1:3])

            num += posterior[cluster] * np.outer(x - mean, x - mean)

        return num/N_k[cluster]

    return {"pi_1": aux("pi_1"), "pi_2": aux("pi_2")}

def calculate_silhouette(a, b):
    if (a < b):
        return 1 - a/b
    else:
        return b/a - 1


def calculate_map(x):
    likelihood_1 = pi_1["binomial"].pmf(x[0]) * pi_1["normal"].pdf(x[1:3])
    likelihood_2 = pi_2["binomial"].pmf(x[0]) * pi_2["normal"].pdf(x[1:3])

    return {"pi_1": likelihood_1, "pi_2": likelihood_2}
    

# ALÍNEA A

#E-STEP

posteriores = []
for x in dados:
    posteriores.append(calcula_posterior(x))

print("EXERCÍCIO 1\n")
print("\tPosteriores:")
for index, posterior in enumerate(posteriores):
    print(f"\tx{index}: \t cluster 1 = {round(posterior['pi_1'], 5)} \t cluster 2 = {round(posterior['pi_2'], 5)}")
print()

#M-STEP

N_k = {
    "pi_1": sum(posterior["pi_1"] for posterior in posteriores),
    "pi_2": sum(posterior["pi_2"] for posterior in posteriores)
}
N = N_k["pi_1"] + N_k["pi_2"]

updated_priors = update_priors()
updated_mean = update_mean()
updated_variance = update_variance(update_mean)

print(f"\tUpdated priors: \t cluster 1 = {round(updated_priors['p1_1'], 5)} \t cluster 2 = {round(updated_priors['p1_2'], 5)}\n")
print(f"\tUpdated p_sucesso: \t cluster 1 = {round(updated_mean['pi_1']['p_sucesso'], 5)} \t cluster 2 = {round(updated_mean['pi_2']['p_sucesso'], 5)}\n")
print(f"\tUpdated mean: \t\t cluster 1 = {updated_mean['pi_1']['mean']} \t cluster 2 = {updated_mean['pi_2']['mean']}\n")
print(f"\tUpdated variance: \t cluster 1 = {updated_variance['pi_1'][0]} \t\t\t\t\t\t cluster 2 = {updated_variance['pi_2'][0]}")
print(f"\t\t\t\t\t\t\t\t     {updated_variance['pi_1'][1]}\t\t\t\t\t\t\t\t     {updated_variance['pi_2'][1]}\n")



# ALÍNEA B

print("\nEXERCÍCIO 2\n")

new_x = (1, 0.3, 0.7)

new_prior1 = updated_priors["p1_1"]
new_prior2 = updated_priors["p1_2"]

new_ber1 = bernoulli(updated_mean["pi_1"]["p_sucesso"])
new_ber2 = bernoulli(updated_mean["pi_2"]["p_sucesso"])

new_mvn1 = multivariate_normal(updated_mean["pi_1"]["mean"], updated_variance["pi_1"])
new_mvn2 = multivariate_normal(updated_mean["pi_2"]["mean"], updated_variance["pi_2"])

pi_1 = {"prior": new_prior1, "binomial": new_ber1, "normal": new_mvn1}
pi_2 = {"prior": new_prior2, "binomial": new_ber2, "normal": new_mvn2}

new_posterior = calcula_posterior(new_x)
print(f"\tx_new: \t cluster 1 = {round(new_posterior['pi_1'], 5)} \t cluster 2 = {round(new_posterior['pi_2'], 5)}\n")



# ALÍNEA C

print("\nEXERCÍCIO 3\n")

cluster_1 = []
cluster_2 = []
posteriores = []
for x in dados:
    posteriores.append(calculate_map(x))

print("\tPosteriores:")
for index, posterior in enumerate(posteriores):
    print(f"\tx{index}: \t cluster 1 = {round(posterior['pi_1'], 5)} \t cluster 2 = {round(posterior['pi_2'], 5)}", end="")
    
    if posterior["pi_1"] > posterior["pi_2"]:
        cluster_1.append(dados[index])
        print(" \t pertence ao cluster 1")
    else:
        cluster_2.append(dados[index])
        print(" \t pertence ao cluster 2")
print()

print(f"\tCalculei as silhuetas à mão")

# ALÍNEA D

print("\n\nEXERCÍCIO 4\n")

print("\tA purity é uma medida que nos indica a qualidade do clustering. Quanto mais próximo de 1, melhor é o clustering.")
print("\tUm valor de 0.75 de purity indica que 75% dos dados estão no cluster correto.")
print("\tOu seja, apenas 25% dos dados estão no cluster errado. Como temos um total de 4 amostras, isto significa que apenas 1 amostra está no cluster errado.")
print("\tAssim podemos concluir que existem 3 classes (ground truth), 2 para os clusters previstos e 1 nova para a amostra que está no cluster errado.")