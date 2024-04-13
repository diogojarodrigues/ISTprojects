import threading
from domain.ServiceEntry import ServiceEntry
class NamingServer:
    
    def __init__(self):
        # name -> ServiceEntry
        self.associations = {}
        self.lock = threading.Lock()     
        
        # associations-> (Tuples, ServiceEntry), (servicename, ServiceEntry)
        #      |
        # serviceEntry -> (Tuples, ServerEntry), (servicename, ServerEntry)
        #      |
        # serverEntry -> (localhost:5432, A), (target, qualifier)


    def register(self, service_name, qualifier, target):
        self.lock.acquire()   
        if (target == "localhost:5001"):        # target cannot be the same as the naming server
            self.lock.release()
            return -1
        
        if (service_name in self.associations):     # if the service is already registered, add the server entry to it
            if (self.associations[service_name].get_server_entry(qualifier) is not None ):  # if the qualifier is already registered, do not add it again
                self.lock.release()
                return -2
            
            if (self.associations[service_name].add_server_entry(target, qualifier) == -1): # if the server entry is already registered, do not add it again
                self.lock.release()
                return -1
            
        else:
            #if the service is not already registered create a new one
            print("Creating new service:", service_name)
            self.associations[service_name] = ServiceEntry(service_name)
            if (self.associations[service_name].get_server_entry(qualifier) is not None ):  # if the qualifier is already registered, do not add it again
                self.lock.release()
                return -2
            
            if (self.associations[service_name].add_server_entry(target, qualifier) == -1): # if the server is already registered, do not add it again
                self.lock.release()
                return -1
            
        self.lock.release()
        return 1

    def lookup(self, service_name):
        print("Entered lookup...")
        print("service_name:", service_name)

        self.lock.acquire()   
        service = self.associations.get(service_name)
        if service is None:   
            self.lock.release()          # if the service is not registered, return an empty list
            return []        

        server_entries = self.associations.get(service_name).get_all_server_entries()
        self.lock.release()

        return server_entries

    def remove(self, service_name, target):
        self.lock.acquire()
        service = self.associations.get(service_name)
        
        if(service is None):        # if the service is not registered, return -1
            print("Service not found: ", service_name)
            self.lock.release()
            return -1
        if (service.remove_server_entry(target) != 1):  # if the server could not be removed
            print("Failed to remove server from service: ", service_name)
            self.lock.release()
            return -1
        
        self.lock.release()
        return 1
    
    def print_server(self):
        self.lock.acquire()
        print("NamingServer => associations:")
        for service in self.associations:
            self.associations[service].print_service()
        print("------------------------------\n")
        self.lock.release()