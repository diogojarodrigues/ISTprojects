import sys
sys.path.insert(1, '../Contract/target/generated-sources/protobuf/python')

import NameServer_pb2 as pb2
import NameServer_pb2_grpc as pb2_grpc
import grpc

from concurrent import futures
from domain.NamingServer import NamingServer

class NamingServerServiceImpl(pb2_grpc.NameServerServicer):

    def __init__(self):
        self.server = NamingServer() 
        pass

    def register(self, request, context):   
        print("------------------------------")
        print("Received register request:")
        print(request)

        status = self.server.register(request.service_name, request.qualifier, request.target) 
        if (status == -1):                          # Server could not be registered, port already in use
            print("Could not register")
            context.set_code(grpc.StatusCode.INVALID_ARGUMENT)      # set expection message
            context.set_details('Port already in use') 

        if (status == -2):
            print("Could not register")             # Server could not be registered, qualifier already in use
            context.set_code(grpc.StatusCode.INVALID_ARGUMENT)
            context.set_details('Qualifier already in use') 
        
        self.server.print_server()

        response = pb2.RemoveResponse()
        return response


    def lookup(self, request, context):
            
        def sort_targets(targets):
            
            sorted_list = ["", "", ""] 

            for target in targets:
                qualifier = target[1]  # Extract qualifier from target tuple
                if qualifier == 'A':
                    sorted_list[0] = target[0]
                elif qualifier == 'B':
                    sorted_list[1] = target[0]
                elif qualifier == 'C':
                    sorted_list[2] = target[0]
    
            return sorted_list
        
        print("------------------------------")
        print("Received lookup request:")
        print(request)

        targets = self.server.lookup(request.service_name)  # get all the targets for the service name

        sorted_targets = sort_targets(targets)
        # sort the targets based on the qualifier
        self.server.print_server()

        response = pb2.LookupResponse(targets=sorted_targets)
        return response


    def remove(self, request, context):
        print("------------------------------")
        print("Received remove request:\n", request)

        status = self.server.remove(request.service_name, request.target)
        if (status == -1):                      # Server could not be unregistered
            print("Could not unregister")
            context.set_code(grpc.StatusCode.INVALID_ARGUMENT)      # set excpetion message
            context.set_details('Failed to unregister')  
        else:
            print("Server unregistered")

        self.server.print_server()

        response = pb2.RemoveResponse()
        return response



        