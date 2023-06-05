#PLaces
UNKNOWN = " "           # Desconhecido
PLACEHOLDER = "%"       # Peça de um barco qualquer

#Green
WHITE = "\033[0;37m"
RED = "\033[91m"
BLUE = "\033[0;34m"
YELLOW = "\033[0;33m"
GREEN = "\033[0;32m"
END = "\033[0m"


PLACE_WATER = "."
PLACE_TOP = "t"
PLACE_LEFT = "l"
PLACE_MIDDLE = "m"
PLACE_RIGHT = "r"
PLACE_BOTTOM = "b"
PLACE_CIRCLE = "c" 

#Boat Directions
VERTICAL = 0
HORIZONTAL = 1
NO_DIRECTION = 2

NORTH = (-1, 0, PLACE_BOTTOM)      # Norte
SOUTH = (1, 0, PLACE_TOP)          # Sul
EAST = (0, 1, PLACE_LEFT)          # Este
WEST = (0, -1, PLACE_RIGHT)        # Oeste


#HINTs
WATER_HINT = "W"
TOP_HINT = "T"
LEFT_HINT = "L"
MIDDLE_HINT = "M"
RIGHT_HINT = "R"
BOTTOM_HINT = "B"
CIRCLE_HINT = "C"


#Boats
WATER = (WATER_HINT, PLACE_WATER) # Água
TOP = (TOP_HINT, PLACE_TOP) # Barco com a ponta para cima
LEFT = (LEFT_HINT, PLACE_LEFT) # Barco com a ponta para a esquerda
MIDDLE = (MIDDLE_HINT, PLACE_MIDDLE) # Barco no meio
RIGHT = (RIGHT_HINT, PLACE_RIGHT) # Barco com a ponta para a direita
BOTTOM = (BOTTOM_HINT, PLACE_BOTTOM) # Barco com a ponta para baixo
CIRCLE = (CIRCLE_HINT, PLACE_CIRCLE) # Barco em circulo

BOAT = (
    TOP[0], TOP[1], LEFT[0], LEFT[1], MIDDLE[0], MIDDLE[1], 
    RIGHT[0], RIGHT[1], BOTTOM[0], BOTTOM[1], CIRCLE[0], CIRCLE[1]
    )

FULL_BOAT = (
    TOP[0], TOP[1], LEFT[0], LEFT[1], MIDDLE[0], MIDDLE[1], 
    RIGHT[0], RIGHT[1], BOTTOM[0], BOTTOM[1], CIRCLE[0], CIRCLE[1],
    PLACEHOLDER
)

NOT_PLACED = (UNKNOWN, PLACEHOLDER)
