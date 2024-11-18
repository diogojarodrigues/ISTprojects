import sys
from sys import stdin

from constants import *
from board import Board

from search import (
    Problem,
    Node,
    astar_search,
    breadth_first_tree_search,
    depth_first_tree_search,
    greedy_search,
    recursive_best_first_search,
    compare_searchers
)

class BimaruState:
    state_id = 0

    def __init__(self, board):
        self.board = board
        self.id = BimaruState.state_id
        BimaruState.state_id += 1
        
        #self.board.print_board(self.id)

    def __lt__(self, other):
        return self.id < other.id


            

class Bimaru(Problem):

    def __init__(self, board: Board):
        """O construtor especifica o estado inicial."""
        state = BimaruState(board)
        super().__init__(state)

    def actions(self, state: BimaruState):
        """Retorna uma lista de ações que podem ser executadas a
        partir do estado passado como argumento.
        
        A primeira ação devolvida é a primeira  a ser executada, a segunda é a segunda
        e etc..
        """
        for boat_size in (range(4, 0, -1)):
            if state.board.boat_count[boat_size - 1] != 0:
                return reversed(state.board.check_available_positions(boat_size))
        return []

    def result(self, state: BimaruState, action):
        """Retorna o estado resultante de executar a 'action' sobre
        'state' passado como argumento. A ação a executar deve ser uma
        das presentes na lista obtida pela execução de
        self.actions(state)."""
        #print(action)
    
        (row, col, boat_size, orientation) = action
        my_board = state.board.copy()

        if (boat_size == 1):
            my_board.putCircle(row, col)
        elif (orientation == HORIZONTAL):
            my_board.putBoatHorizontal(row, col, boat_size)
        elif (orientation == VERTICAL):
            my_board.putBoatVertical(row, col, boat_size)

        my_board.update()


        return BimaruState(my_board)        

    def goal_test(self, state: BimaruState):
        """Retorna True se e só se o estado passado como argumento é
        um estado objetivo. Deve verificar se todas as posições do tabuleiro
        estão preenchidas de acordo com as regras do problema."""

        for r in state.board.rows_ships:
            if r != 0:
                return False
            
        for c in state.board.cols_ships:
            if c != 0:
                return False
            
        if any(count != 0 for count in state.board.boat_count):
            return False
        
        
        return True
        

if __name__ == "__main__":

    board = Board.parse_instance()
    problem = Bimaru(board) 
    goal_node = depth_first_tree_search(problem)
    goal_node.state.board.print_board()
    
    print("\n########################################################################################\n")
    goal_node.state.board.print_board("Solution")
    print("Is goal?", problem.goal_test(goal_node.state))