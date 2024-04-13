from domain.ServerEntry import ServerEntry

class ServiceEntry:
    
    def __init__(self, name):
        self.name = name
        self.server_entries = []
    
    def add_server_entry(self, target, qualifier):
        if self.get_server_target(target) is not None:      # if the server is already registered, do not add it again
            return -1

        server_entry = ServerEntry(target, qualifier)       # create a new server entry, if not registered
        self.server_entries.append(server_entry)
        return 0
    
    def remove_server_entry(self, target):
        server_entry = self.get_server_target(target)
        if (server_entry) is None:                          # if the server is not registered, cannot remove it
            return -1
        self.server_entries.remove(server_entry)            # remove the server entry, if registered
        return 1
    
    def get_server_entry(self, qualifier):                  # get the server entry for a given qualifier
        for server_entry in self.server_entries:
            if server_entry.qualifier == qualifier:
                return server_entry.target
        return None

    def get_all_server_entries(self):                       # get all the server entries for a given service
        return [[server.get_target(), server.get_qualifier()]for server in self.server_entries]

    def get_server_target(self, target):                    # get the server entry for a given target
        for server_entry in self.server_entries:
            if server_entry.target == target:
                print("Found server to unregister: ", server_entry.target)
                return server_entry
        return None

    def print_service(self):                                # print the service entry
        print("\tServiceEntry => service name:", self.name)
        for entry in self.server_entries:
            entry.print_entry()