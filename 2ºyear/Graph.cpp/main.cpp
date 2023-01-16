#include <iostream>
#include <vector>
#include <algorithm>
using namespace std;

// -----------------------------------------------------
typedef struct set {
	int rank;
	int parent;
}* Set;

vector<Set> sets;

void make_set(int vertex) {
	Set set = new struct set;

	set->rank = 0;
	set->parent = vertex;

	sets[vertex] = set;
}

int find_set(int vertex) {
	Set set = sets[vertex];
	if (vertex != set->parent) {
		set->parent = find_set(set->parent);
	}
	return set->parent;
}

void link(int v1, int v2) {
	Set s1 = sets[v1];
	Set s2 = sets[v2];
	if (s1->rank > s2->rank) {
		s2->parent = v1;
	} else {
		s1->parent = v2;
		if (s1->rank == s2->rank)
			s2->rank++;
	}

}

void union_set(int v1, int v2) {
	link(find_set(v1), find_set(v2));
}
// -----------------------------------------------------

typedef struct {
	int v1;
	int v2;
	int weight;
} Edge;

bool operator<(const Edge& lh_edge, const Edge& rh_edge) {
    return lh_edge.weight > rh_edge.weight;
}


void show_edge(Edge edge) {
	cout << "  " << edge.v1 << " -> " << edge.v2 << " = " << edge.weight << endl;
}

void show_graph(vector<Edge> gr) {
	cout << "\n---All Edges---\n";
	for (auto it = gr.begin(); it != gr.end(); it++)
		show_edge(*it);
	cout << "---------------\n";
}

int main() {
	vector<Edge> edges;
	Edge e;
	int num_v, num_e;

	std::ios::sync_with_stdio(false);
	//Input reading
	cin >> num_v >> num_e;
	edges.reserve(num_e);
	sets.reserve(num_v+1);
	for (int i=0; i<num_e; i++) {
		cin >> e.v1 >> e.v2 >> e.weight;
		edges.emplace_back(e);
	}

	//Kruskal algorithm
	int res = 0;
	for (int i=1; i<=num_v; i++) {
		make_set(i);
	}
	sort(edges.begin(), edges.end());
	for (auto &edge : edges) {
		if (find_set(edge.v1) != find_set(edge.v2)) {
			union_set(edge.v1, edge.v2);
			res += edge.weight;
		}
	}

	cout << res << endl;
	return 0;
}