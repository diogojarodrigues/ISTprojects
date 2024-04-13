class ServerEntry:
    
    def __init__(self, target, qualifier):
        self.target = target             # ip:address
        self.qualifier = qualifier

    def get_qualifier(self):
        return self.qualifier

    def get_target(self):
        return self.target
    
    def print_entry(self):
        print("\t\tEntry => target:", self.target, ", qualifier;",self.qualifier)
