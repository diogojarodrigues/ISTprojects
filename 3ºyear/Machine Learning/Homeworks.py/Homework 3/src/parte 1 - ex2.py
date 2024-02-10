import math
from numpy import linalg as LA
import numpy as np

x0 = np.array([[1], [1], [1], [1]])
x1 = np.array([[1], [0], [0], [-1]])
x01 = np.array([[1,1], [1,0], [1,0], [1,-1]])
z0 = np.array([[-1],[1],[-1]])
z1 = np.array([[1],[-1],[-1]])
l = 0.1

w_1 = np.array([[1,1,1,1],[1,1,2,1],[1,1,1,1]])
b_1 = np.array([[1],[1],[1]])
w_2 = np.array([[1,4,1],[1,1,1]])
b_2 = np.array([[1],[1]])
w_3 = np.array([[1, 1],[3, 1], [1, 1]])
b_3 = np.array([[1], [1], [1]])

def activ(x):
    return np.tanh(0.5*x - 2)

    
def sech(x):
    return (1 / np.cosh(0.5 * x - 2))**2 * 0.5

# Propagation X0 
a_01 = np.matmul(w_1, x0) + b_1  #W.X[0] + b
h_01 = activ(a_01)
a_02 = np.matmul(w_2, h_01) + b_2   #W2.X[1] + b
h_02 = activ(a_02)
a_03 = np.matmul(w_3, h_02) + b_3   #W3.X[2] + b
h_03 = activ(a_03)
prediction_x0 = h_03

# Propagation X1
a_11 = np.matmul(w_1, x1) + b_1  #W.X[0] + b
h_11 = activ(a_11)
a_12 = np.matmul(w_2, h_11) + b_2   #W2.X[1] + b
h_12 = activ(a_12)
a_13 = np.matmul(w_3, h_12) + b_3   #W3.X[2] + b
h_13 = activ(a_13)
prediction_x1 = h_13

#Back propagation x1
dEdh_03 = - ((z0 - prediction_x0))
delta_03 = np.multiply(dEdh_03,0.5*(1-h_03**2))
dE_dw_03 = np.matmul(delta_03, h_02.transpose())

#Back propagation x2
dEdh_13 = - ((z1 - prediction_x1))
delta_13 = np.multiply(dEdh_13,0.5*(1-h_13**2))
dE_dw_13 = np.matmul(delta_13, h_12.transpose())

E3 = dE_dw_13 + dE_dw_03

#Bias 3

b3 = delta_03 + delta_13

#LAYER 2

#Back propagation x1

dEdh_02 = np.matmul(w_3.transpose(), delta_03)

delta_02 = np.multiply(dEdh_02,0.5*(1-h_02**2))
dE_dw_02 = np.matmul(delta_02, h_01.transpose())

#Back propagation x2
dEdh_12 = np.matmul(w_3.transpose(), delta_13)

delta_12 = np.multiply(dEdh_12,0.5*(1-h_12**2))
dE_dw_12 = np.matmul(delta_12, h_11.transpose())

E2 = dE_dw_12 +dE_dw_02

#Bias 2

b2 = delta_02 + delta_12

#LAYER 1

#Back propagation x1

dEdh_01 = np.matmul(w_2.transpose(), delta_02)
delta_01 = np.multiply(dEdh_01,0.5*(1-h_01**2))
dE_dw_01 = np.matmul(delta_01, x0.transpose())

#Back propagation x1

dEdh_11 = np.matmul(w_2.transpose(), delta_12)
delta_11 = np.multiply(dEdh_11,0.5*(1-h_11**2))
dE_dw_11 = np.matmul(delta_11, x0.transpose())

#DE1
E1 = dE_dw_01+dE_dw_11

#Bias 1
b1 = delta_01 + delta_11


#ATUALIZACAO 
w_3 = w_3 - l*E3
b_3 = b_3 - l*b3
# print(b_3)

w_2 = w_2 -  l*E2
b_2 = b_2 - l*b2
# print(b_2)

w_1 = w_1 - l*E1
b_1 = b_1 - l*b1
# print(b_1)