import numpy as np
from scipy.stats import multivariate_normal

y1 = np.array([0.24, 0.16, 0.32, 0.54, 0.66, 0.76, 0.41])
y2 = np.array([0.36, 0.48, 0.72, 0.11, 0.39, 0.28, 0.53])
y3 = np.array([1, 1, 0, 0, 0, 1, 0])
y4 = np.array([1, 0, 1, 0, 0, 0, 1])
y5 = np.array([0, 1, 2, 1, 0, 2, 1])
y6 = np.array(["A", "A", "A", "B", "B", "B", "B"])

dados = {"y1": y1, "y2": y2, "y3": y3, "y4": y4, "y5": y5, "y6": y6}

def prob_1(y5, y6):
    num = 0
    den = 0

    for i in range(7):
        if (y6 == dados["y6"][i]):
            den += 1
            if (y5 == dados["y5"][i]):
                num += 1
    return (num / den)

def prob_2(y3, y4, y6):
    num = 0
    den = 0

    for i in range(7):
        if (y6 == dados["y6"][i]):
            den += 1
            if (y3 == dados["y3"][i] and y4 == dados["y4"][i]):
                num += 1
    return (num / den)

dados_a = {"y1": [], "y2": [], "y3": [], "y4": [], "y5": [], "y6": []}
dados_b = {"y1": [], "y2": [], "y3": [], "y4": [], "y5": [], "y6": []}

for i in range(7):
    if dados["y6"][i] == "A":
        for key in dados_a.keys():
            dados_a[key].append(dados[key][i])
    elif dados["y6"][i] == "B":
        for key in dados_b.keys():
            dados_b[key].append(dados[key][i])

#Covariance and mean matrix for A
cov_matrix_a = np.cov([dados_a["y1"], dados_a["y2"]], ddof=1)
mean_a = np.array([np.mean(dados_a["y1"]), np.mean(dados_a["y2"])])

#Covariance and mean matrix for B
cov_matrix_b = np.cov([dados_b["y1"], dados_b["y2"]])
mean_b = np.array([np.mean(dados_b["y1"]), np.mean(dados_b["y2"])])

x8 = np.array([0.38, 0.52, 0, 1, 0, "A"])
x9 = np.array([0.42, 0.59, 0, 1, 1, "B"])

# Create a multivariate normal distribution object
mvn = multivariate_normal(mean=mean_b, cov=cov_matrix_b)

# Define the point at which you want to calculate the probability
point = np.array([x8[0], x8[1]])

# Calculate the probability density at the given point
probability = mvn.pdf(point)

print(f"Probability Density at {point}: {probability}")
