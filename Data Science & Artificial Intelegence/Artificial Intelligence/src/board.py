from sys import stdin

from constants import *

class Board:
    ####################################################
    #        Funções básicas da classe Board           #
    ####################################################
    """Representação interna de um tabuleiro de Bimaru."""
    def __init__(self):
        self.matrix = [[UNKNOWN for j in range(10)] for i in range(10)]
        self.rows_ships = []
        self.cols_ships = []
        self.rows_ships_fixed = []
        self.cols_ships_fixed = []
        self.rows_available_ships = [10 for _ in range(10)]
        self.cols_available_ships = [10 for _ in range(10)]
        self.middle_array = []
        self.placeholders = []
        self.boat_count = [4, 3, 2, 1]  #numero de barcos que faltam colocar de tamanho 1,2,3 e 4 respetivamente 

    def copy(self) -> "Board":
        """Devolve uma cópia do tabuleiro."""
        new_board = Board()
        new_board.matrix = [row[:] for row in self.matrix]

        new_board.rows_ships = self.rows_ships[:]
        new_board.cols_ships = self.cols_ships[:]
        new_board.rows_ships_fixed = self.rows_ships_fixed[:]
        new_board.cols_ships_fixed = self.cols_ships_fixed[:]
        new_board.rows_available_ships = self.rows_available_ships[:]
        new_board.cols_available_ships = self.cols_available_ships[:]

        new_board.boat_count = self.boat_count[:]
        new_board.middle_array = self.middle_array[:]
        new_board.placeholders = self.placeholders[:]
        return new_board

    def print(self):
        for row in range(10):
            for col in range(10):
                print(self.matrix[row][col], end="")
            print("\n", end="")
    
    def print_board(self, name="not specified"):
        """Imprime o tabuleiro no standard output (stdout)."""
        #Auxiliar Functions
        def part_color(value, color = WHITE):
            print(color + str(value) + END + "     ", end="")

        def print_boats_used():
            print("Boats left: ", end="[")
            for i in range(4):
                if (i == 3): print(self.boat_count[i], end="]\n\n")
                else: print(self.boat_count[i], end=", ")

        def print_boat():
            value = self.matrix[row][col]
            if (value in BOAT):
                if (self.rows_ships[row] == 0 or self.cols_ships[col] == 0): part_color(value, YELLOW)
                else: part_color(value, RED)
            else:
                if (self.rows_ships[row] == 0 or self.cols_ships[col] == 0): part_color(value, GREEN)
                else: part_color(value, WHITE)

        def print_row_ships():
            if (self.rows_ships[row] == 0):
                part_color(self.rows_ships[row], GREEN)
            else:
                part_color(self.rows_ships[row], RED)
            part_color(self.rows_ships_fixed[row], BLUE)
            part_color(self.rows_available_ships[row], WHITE)
            print("\n")

        def print_col_ships():
            for col in range(10):
                if (self.cols_ships[col] == 0):
                    part_color(self.cols_ships[col], GREEN)
                else:
                    part_color(self.cols_ships[col], RED)
            print("\n")
            for col in range(10):
                part_color(self.cols_ships_fixed[col], BLUE)
            print("\n")
            for col in range(10):
                part_color(self.cols_available_ships[col], WHITE)
            print("\n") 

        #Main functions
        print("\n*****************************************************************************")
        print("Board:", name)
        print_boats_used()
        for row in range(10):
            for col in range(10):
                print_boat()
            print_row_ships()   
        print_col_ships()
        print("---------------------------------------------------------------------------\n")
    
    def print(self):
        for row in range(10):
            for col in range(10):
                print(self.matrix[row][col], end="")
            print("\n", end="")

    @staticmethod
    def parse_instance():
        def fill_water_initial(board):
            """Preenche o tabuleiro com água, de acordo com as regras do jogo.
            Função corrida só para o inicio!"""
            for i in range(10):
                for j in range(10):
                    if board.rows_ships[i] == 0 or board.cols_ships[j] == 0:
                        if board.matrix[i][j] == UNKNOWN:
                            board.__put_part(i, j, PLACE_WATER)

        """Lê o test do standard input (stdin) que é passado como argumento
        e retorna uma instância da classe Board.

        Por exemplo:
            $ python3 bimaru.py < input_T01
        """
        new_board = Board()

        new_board.rows_ships = [int(element) for element in stdin.readline().split()[1:]]
        new_board.cols_ships = [int(element) for element in stdin.readline().split()[1:]]
        new_board.rows_ships_fixed = new_board.rows_ships[:]
        new_board.cols_ships_fixed = new_board.cols_ships[:]

        fill_water_initial(new_board)                #fills rows and cols with water if they are empty        

        nHints  = int(stdin.readline())
        for i in range(nHints):           
            hint = stdin.readline().split()[1:]
            row = int(hint[0])       
            col = int(hint[1])   
            value = hint[2]     
            #print(row, col, value)       
            new_board.__put_part(row, col, value)       

            if value == CIRCLE_HINT:                        #Retira um barco de tamanho 1
                new_board.boat_count[0] -= 1
            elif value == MIDDLE_HINT:                      #Adiciona a posição à lista de middle
                new_board.middle_array.append((row, col))

            #Colocar placeholders
            if new_board.matrix[row][col] == RIGHT_HINT:
                new_board.__put_part(row, col-1, PLACEHOLDER, EAST)
            elif new_board.matrix[row][col] == LEFT_HINT:
                new_board.__put_part(row, col+1, PLACEHOLDER, WEST)
            elif new_board.matrix[row][col] == TOP_HINT:
                new_board.__put_part(row+1, col, PLACEHOLDER, NORTH)
            elif new_board.matrix[row][col] == BOTTOM_HINT:
                new_board.__put_part(row-1, col, PLACEHOLDER, SOUTH)
            
        #looks for already existing boats
        start_looking_for_boat = False
        count = 0
        for row in range(10):         
            for col in range(10):
                value = new_board.matrix[row][col]
                if  value == LEFT_HINT:
                    start_looking_for_boat = True
                    count= count + 1
                elif start_looking_for_boat == True:
                    #print(row,col,value)
                    if value == MIDDLE_HINT:
                        count= count + 1
                    elif value == RIGHT_HINT: #BUG
                        count= count + 1
                        new_board.boat_count[count-1] -= 1
                        start_looking_for_boat = False
                        continue
                    else:
                        count = 0
                        start_looking_for_boat = False
                
                if value == TOP_HINT:
                    for i in range(1,4):
                        if new_board.matrix[row+i][col] == BOTTOM_HINT:
                            new_board.boat_count[i] -= 1
                            break
                        elif new_board.matrix[row+i][col]== MIDDLE_HINT:
                            continue
                        else:
                            break


        new_board.update()                
        return new_board



    ####################################################
    #                     Getters                      #
    ####################################################
    def get_value(self, row: int, col: int) -> str:
        """Devolve o valor na respetiva posição do tabuleiro."""
        if (0 <= row <= 9 and 0 <= col <= 9):
            return self.matrix[row][col]
        return None

    def adjacent_vertical_values(self, row: int, col: int): #-> (str, str):
        return self.get_value(row-1, col), self.get_value(row+1, col)

    def adjacent_horizontal_values(self, row: int, col: int): #-> (str, str):
        return self.get_value(row, col-1), self.get_value(row, col+1)
    
    def adjacent_top_corners(self, row: int, col: int): #-> (str, str):
        return self.get_value(row-1, col-1), self.get_value(row-1, col+1)

    def adjacent_bottom_corners(self, row: int, col: int): #-> (str, str):
        return self.get_value(row+1, col-1), self.get_value(row+1, col+1)
    
    def adjacent_left_corners(self, row: int, col: int): #-> (str, str):
        return self.get_value(row-1, col-1), self.get_value(row+1, col-1)
    
    def adjacent_right_corners(self, row: int, col: int): #-> (str, str):
        return self.get_value(row-1, col+1), self.get_value(row+1, col+1)



    ####################################################
    #                  Fill the board                  #
    ####################################################
    def __decrement_row(self, row: int):
        self.rows_ships[row] -= 1
        if (self.rows_ships[row] == 0):
            for i in range(10):
                self.__put_part(row, i , PLACE_WATER)
    
    def __decrement_col(self, col: int):
        self.cols_ships[col] -= 1
        if (self.cols_ships[col] == 0):
            for i in range(10):
                self.__put_part(i, col, PLACE_WATER)


    def __put_part(self, row: int, col: int, boat_type: str, direction = None):  

        if (self.matrix[row][col] == PLACE_WATER and boat_type == WATER_HINT): 
            self.matrix[row][col] = WATER_HINT
            return

        if (self.matrix[row][col] in NOT_PLACED):           #Verifica se está algum barco nesta posição (procura por UNKNOWN ou PLACEHOLDER) 
            if (self.matrix[row][col] == PLACEHOLDER):      #É um placeholder     
                if (boat_type in [WATER[1], PLACEHOLDER]):  #Não se pode colocar placeholders e água onde já existe um placeholder
                    return
                self.__remove_placeholder(row, col)
                self.matrix[row][col] = boat_type               #Coloca o barco na posição (row, col)
    
            else:                                           #É um UNKNOWN
                
                self.matrix[row][col] = boat_type
                self.rows_available_ships[row] -= 1
                self.cols_available_ships[col] -= 1

                if (boat_type in WATER):  return
                
                if (boat_type == PLACEHOLDER):              #Se for um placeholder adiciona-o à lista de placeholders
                    self.placeholders.append((row, col, direction))
                
                self.__decrement_row(row)
                self.__decrement_col(col)
                

            self.__mark_water(row, col)                     #Marca a água à volta do barco


    def __remove_placeholder(self, row: int, col: int):
        for placeholder in reversed(self.placeholders):
            if placeholder[0] == row and placeholder[1] == col:
                self.placeholders.remove(placeholder)
                return

    def __mark_water(self, row: int, col: int):
        """Marca todas as posições à volta com água."""
        def mark(row, col):
            if (0 <= row <= 9 and 0 <= col <= 9):
                if self.matrix[row][col] == UNKNOWN:
                    self.__put_part(row, col, PLACE_WATER)
            
        boat_type = self.get_value(row,col)
        if (boat_type in WATER or boat_type == UNKNOWN):
            print("ERROR! __mark_water: ", boat_type, row, col)
            return
        
        mark(row-1, col-1)   #top left
        mark(row-1, col+1)   #top right
        mark(row+1, col-1)   #bottom left
        mark(row+1, col+1)   #bottom right

        if (boat_type in MIDDLE or boat_type == PLACEHOLDER): return
        if (boat_type not in BOTTOM): mark(row-1, col)     #top
        if (boat_type not in RIGHT): mark(row, col-1)      #left
        if (boat_type not in LEFT): mark(row, col+1)       #right
        if (boat_type not in TOP): mark(row+1, col)        #bottom


    def __check_transform_placeholders(self):
        """Verifica se os placeholders atuais foram bloqueados e podem ser transformados em pontas de barcos.
        So chamar esta funcao se Placeholder estiver na ponta"""
        def remove_boat():
            """Remove um barco da lista de barcos que faltam colocar."""
            #print("remove boat: ", row, col, direction)
            for size in range (1,5):
                #gets the value next to placeholder
                value = self.get_value(direction[0]*size + row, direction[1]*size + col)
                if (value == UNKNOWN or value == PLACEHOLDER):
                    #print("not removed")
                    return False
                elif (value == None or value in WATER):
                    self.boat_count[size-1] -= 1
                    #print("removed ", size)
                    return True
                
        def remove_boat_placeholder_in_middle():
            count = 1
            for i in range(1,3):
                #  SMTH % M R  -- Este loop vai percorrer o M e o R
                value = self.get_value(direction[0]*i + row, direction[1]*i + col)
                #print("first:",value,count)
                if (value == UNKNOWN or value == PLACEHOLDER):
                    #print("end")
                    return False
                elif (value == None or value in WATER):
                    break
                count += 1

            for i in range(1,3):
                #  SMTH % M R  -- Este loop vai percorrer o SMTH
                value = self.get_value(-direction[0]*i + row, -direction[1]*i + col)
                #print("last:",value,-direction[0]*i + row, -direction[1]*i + col, count)
                if (value == UNKNOWN or value == PLACEHOLDER):
                    #print("end2")
                    return False
                elif (value == None or value in WATER):
                    break
                count += 1
            self.boat_count[count-1] -= 1
            return
                    
        
                
        updated = False
        if self.placeholders == []: return updated       #Verifica se existem placeholders

        for placeholder in reversed(self.placeholders):
            (row, col, direction) = placeholder
            
            #the value opposite to the placeholder
            value = self.get_value(row-direction[0], col-direction[1])
            #check if it is blocked
            #print(placeholder, value)
            #if its blocked by water then put end of boat
            if value in WATER or value == None:
                self.__put_part(row, col, direction[2])
                remove_boat()
                updated = True
            #if blocked by piece of boat then put middle
            elif value in FULL_BOAT:
                self.__put_part(row, col,PLACE_MIDDLE)
                #print(placeholder)
                remove_boat_placeholder_in_middle()
                updated = True

        return updated

            
    def __check_new_placeholders(self):
        """Verifica se deve criar novos placeholders e marca-os com água."""
        def get_index(row,col):
            for i in range(len(self.middle_array)):
                if self.middle_array[i] == (row, col):
                    return i
            return -1
        
        updated = False
        if self.middle_array == []:
            return updated

        copy_middle_array = self.middle_array[:]

        for (row, col) in copy_middle_array:
            #check if top or bottom is blocked
            if self.adjacent_vertical_values(row, col)[0] in [None, PLACE_WATER, WATER_HINT] or self.adjacent_vertical_values(row,col)[1] in [None, PLACE_WATER, WATER_HINT]:
                self.__put_part(row, col-1, PLACEHOLDER, EAST)
                self.__put_part(row, col+1, PLACEHOLDER, WEST)
                updated = True

                value = get_index(row, col)
                #middle has been procvessed deletes it from unprocessed middle array
                if value !=-1:
                    del self.middle_array[value] 
                else:
                    print("ERROR: middle not found in middle array")

            elif self.adjacent_horizontal_values(row,col)[0] in [None, PLACE_WATER, WATER_HINT] or self.adjacent_horizontal_values(row,col)[1] in [None, PLACE_WATER, WATER_HINT]:
                self.__put_part(row-1, col, PLACEHOLDER, SOUTH)
                self.__put_part(row+1, col, PLACEHOLDER, NORTH)
                updated = True
                
                value = get_index(row, col)
                #middle has been procvessed deletes it from unprocessed middle array
                if value !=-1:
                    del self.middle_array[value]
                else:
                    print("ERROR: middle not found in middle array")
            
        return updated


    ####################################################
    #                Public Functions                  #
    ####################################################         
    def putCircle(self, row, col):
        self.__put_part(row, col, PLACE_CIRCLE)
        self.boat_count[0] -= 1

    def putBoatHorizontal(self, row, col, size):
        for i in range(size):
            if (i == 0): self.__put_part(row, col+i, PLACE_LEFT)
            elif (i == size - 1): self.__put_part(row, col+i, PLACE_RIGHT)
            else: self.__put_part(row, col+i, PLACE_MIDDLE)
            
        self.boat_count[size-1] -= 1
        #print(size, self.boat_count[size-1])

    def putBoatVertical(self, row, col, size):
        for i in range(size):
            if (i == 0): self.__put_part(row, col+i, PLACE_TOP)
            elif (i == size - 1): self.__put_part(row + i, col, PLACE_BOTTOM)
            else: self.__put_part(row + i, col, PLACE_MIDDLE)

        self.boat_count[size-1] -= 1
        #print(size, self.boat_count[size-1])

    #Auxiliar Function
    #Retirei do scope da outra para poder usar para debug (Tranquilo meu bro, essas merdices que eu faço podes apagar a vontade)
    def check_position(self, row: int, col: int, boat_size: int, orientation: int) -> bool:
        """Verifica se é possível colocar um barco de tamanho boat_size
        na posição (row, col) com a orientação orientation."""  
        boat_already_exists = True
        if orientation == VERTICAL:
            if row + boat_size - 1> 9:
                return False
            
            #lets find out if we can place a boat of such size
            blocks_left_to_place = self.cols_ships[col]
            for i in range(row, row + boat_size):
                
                value = self.get_value(i, col)

                #unicos valores q podemos encontrar sao MIDDLE E UNKNOWN E PLACEHOLDER no meio E TOP E BOTTOM E UNKNOWN NAS PONTAS
                if value in WATER or value in LEFT or value in RIGHT or value in CIRCLE:
                    return False
                
                # "places block" if not there
                if value in UNKNOWN:
                    boat_already_exists = False
                    blocks_left_to_place -= 1
                if value == PLACEHOLDER:
                    boat_already_exists = False

                
                #check if position on top of boat and top corners are water   ..www..
                if i == row:
                    if (self.adjacent_vertical_values(i, col)[0] not in [PLACE_WATER, WATER_HINT, UNKNOWN, None] or 
                    not all(element in [PLACE_WATER, WATER_HINT, UNKNOWN, None] for element in self.adjacent_top_corners(i, col))):
                        return False
                
                #checks if horizontal values are all non boats
                if  not all(element in [PLACE_WATER, WATER_HINT, UNKNOWN, None] for element in self.adjacent_horizontal_values(i, col)):
                    return False
                
                #check if position on bottom of boat and bottom corners are water
                if i == row + boat_size-1:
                    if (self.adjacent_vertical_values(i, col)[1] not in [PLACE_WATER, WATER_HINT, UNKNOWN, None] or 
                    not all(element in [PLACE_WATER, WATER_HINT, UNKNOWN, None] for element in self.adjacent_bottom_corners(i, col))):
                        return False
                    #Se barco for de tamanho 2 e a posicao superior for um hint entao posicao debaixo e um placeholder
                    if  value not in [UNKNOWN, BOTTOM_HINT, PLACE_BOTTOM,PLACEHOLDER] or boat_already_exists==True:   
                        return False
                    
                if blocks_left_to_place < 0:
                    return False
                
                
            #end of for loop     

            return True

        elif orientation == HORIZONTAL:
            if col + boat_size - 1 > 9:
                return False

            blocks_left_to_place = self.rows_ships[row]

            for i in range(col, col + boat_size):
                
                value = self.get_value(row, i)
                #print(row,i,value, value==PLACEHOLDER, boat_already_exists)

                #unicos valores q podemos encontrar sao MIDDLE E UNKNOWN E PLACEHOLDER no meio E TOP E BOTTOM E UNKNOWN NAS PONTAS
                if value in WATER or value in TOP or value in BOTTOM or value in CIRCLE:
                    return False
                
                # "places block" if not there already
                if value in UNKNOWN:
                    boat_already_exists = False
                    blocks_left_to_place -= 1
                
                if value == PLACEHOLDER:
                    boat_already_exists = False


                #check if position on left of boat and left corners are water   ..www..
                if i == col:
                    if (self.adjacent_horizontal_values(row, i)[0] not in [PLACE_WATER, WATER_HINT, UNKNOWN, None] or 
                    not all(element in [PLACE_WATER, WATER_HINT, UNKNOWN, None] for element in self.adjacent_left_corners(row, i))):
                        return False

                #check if vertical values are all non boats
                if  not all(element in [PLACE_WATER, WATER_HINT, UNKNOWN, None] for element in self.adjacent_vertical_values(row, i)):
                    return False
                
                #check if position on right of boat and right corners are water
                if i == col + boat_size-1:
                    if (self.adjacent_horizontal_values(row, i)[1] not in [PLACE_WATER, WATER_HINT, UNKNOWN, None] or 
                    not all(element in [PLACE_WATER, WATER_HINT, UNKNOWN, None] for element in self.adjacent_right_corners(row, i))):
                        return False

                    if  value not in [UNKNOWN, RIGHT_HINT, PLACE_RIGHT,PLACEHOLDER] or boat_already_exists==True:
                        return False
                    

                if blocks_left_to_place < 0:
                    return False

        return True
        
    def check_available_positions(self, boat_size: int):
        """Devolve uma lista com as posições disponíveis para colocar um barco de tamanho boat_size."""
        
        positions = []
        #self.print()
        for i in range(10):
            for j in range(10):
                #check if column has space 
                
                if boat_size == 1 and self.matrix[i][j] == UNKNOWN:
                    if self.cols_ships[j] != 0 and  self.rows_ships[i] != 0:
                        positions.append((i, j, boat_size, NO_DIRECTION))
                else:
                    if  self.cols_ships[j] != 0: 
                        
                        #no hint
                        if self.matrix[i][j] == UNKNOWN:
                            if self.check_position(i, j, boat_size, VERTICAL):
                                positions.append((i, j, boat_size, VERTICAL))

                        #hint starter position
                        elif self.matrix[i][j] in TOP:
                            if self.check_position(i, j, boat_size, VERTICAL):
                                positions.append((i, j, boat_size, VERTICAL))

                        elif self.matrix[i][j] == PLACEHOLDER:
                            if self.get_value(i+1,j) in BOTTOM:
                                if boat_size == 2:
                                    positions.append((i, j, boat_size, VERTICAL))
                            else:
                                if self.check_position(i, j, boat_size, VERTICAL):
                                    positions.append((i, j, boat_size, VERTICAL))


                    #check if row has space
                    if self.rows_ships[i] != 0:
                        #no hint
                        
                        if self.matrix[i][j] == UNKNOWN:
                            
                            if self.check_position(i, j, boat_size, HORIZONTAL):
                                positions.append((i, j, boat_size, HORIZONTAL))

                        #hint starter position
                        elif self.matrix[i][j] in LEFT:
                            if self.check_position(i, j, boat_size, HORIZONTAL):
                                positions.append((i, j, boat_size, HORIZONTAL))

                        elif self.matrix[i][j] == PLACEHOLDER:
                            if self.get_value(i,j+1) in RIGHT:
                                if boat_size == 2:
                                    positions.append((i, j, boat_size, HORIZONTAL)) 
                            else:
                                if self.check_position(i, j, boat_size, HORIZONTAL):
                                    positions.append((i, j, boat_size, HORIZONTAL))

        return positions
    
    
    def update(self):
        updated = True
        while (updated):
            up_0 = self.__check_new_placeholders()             #Tem de estar em primeiro lugar
            up_1 = self.fill_with_boats_horizontal()
            up_2 = self.fill_with_boats_vertical()
            up_3 = self.__check_transform_placeholders() 
            updated = up_0 or up_1 or up_2 #or up_3
    
    '''
    def update(self):
        updated = True
        while (updated):
            updated = False
            updated = updated or self.__check_new_placeholders()             #Tem de estar em primeiro lugar
            updated = updated or self.__check_transform_placeholders()
            updated = updated or self.fill_with_boats_horizontal()
            updated = updated or self.fill_with_boats_vertical()
    '''

    def fill_with_boats_horizontal(self):
        updated = False
        for row in range(10):        
            if (self.rows_available_ships[row] == self.rows_ships[row]):
                boat_size = 0
                for col in range(9, -1 , -1):
                    if self.get_value(row, col) == UNKNOWN:
                        boat_size += 1
                    else:
                        if (
                            boat_size == 1
                            and self.boat_count[1] == 0
                            and self.boat_count[2] == 0 
                            and self.boat_count[3] == 0
                            ):
                            self.putCircle(row, col+1)
                            updated = True   
                        elif (2 <= boat_size <= 4):
                            #print("putBoatHorizontal: ", row, col+1, boat_size)
                            self.putBoatHorizontal(row, col+1, boat_size)
                            updated = True
                        boat_size = 0
        
    def fill_with_boats_vertical(self):
        updated = False
        for col in range(10):
            if (self.cols_available_ships[col] == self.cols_ships[col]):
                boat_size = 0
                for row in range(9, -1 , -1):
                    if self.get_value(row, col) == UNKNOWN:
                        boat_size += 1
                    else:
                        if (
                            boat_size == 1
                            and self.boat_count[1] == 0
                            and self.boat_count[2] == 0 
                            and self.boat_count[3] == 0
                            ):
                            #print("putCircle: ", row+1, col)
                            self.putCircle(row+1, col)
                            updated = True
                        elif (2 <= boat_size <= 4):
                            #print("putBoatVertical: ", row+1, col, boat_size)
                            self.putBoatVertical(row+1, col, boat_size)
                            updated = True
                        boat_size = 0
