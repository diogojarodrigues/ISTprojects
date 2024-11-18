import numpy as np
import math

dados = np.array([[0.7, -0.3], [0.4, 0.5], [-0.2, 0.8], [-0.4, 0.3]])
output = np.array([0.8, 0.6, 0.3, 0.3])
colunas = np.array([[0.0, 0.0], [1.0, -1.0], [-1.0, 1.0]])
phi_matrix = np.zeros((4, 4))


#Alinea A
def radial_basis_function(arr, col):
    norm = np.linalg.norm(arr - col) ** 2
    return math.exp(-norm/2)

for i in range(4):
    for j in range(4):
        if (j == 0):
            phi_matrix[i][0] = 1
        else:
            res = radial_basis_function(dados[i], colunas[j-1])
            phi_matrix[i][j] = res

phi_matrix_T = phi_matrix.T


xT_x = np.matmul(phi_matrix_T, phi_matrix)
inv_xT_x = np.linalg.inv(xT_x + 0.1 * np.identity(4))

W = np.matmul(np.matmul(inv_xT_x, phi_matrix_T), output)


#Aliena B
z_hat = np.matmul(W, phi_matrix.transpose())

def RMSE(pred, real):
    predicted = np.array(pred)
    target = np.array(real)


    diff = predicted - target
    print(diff)

    diff_2 = diff ** 2

    return np.sqrt(sum(diff_2)/len(diff_2))

print(RMSE(z_hat, output))