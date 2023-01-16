#for i in range(1, 11, 1):
#	print(f"./dgg {2**i} > dense_graph_{i}.in")

for i in range(1, 11, 1):
	print(f"./d2g {(2**i)+1} {(2**i) + 2*i} {0.5} > delaunay_graph_{i}.in")
