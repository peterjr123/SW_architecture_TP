import socket
import os, sys

path = os.path.dirname(os.path.abspath(os.path.dirname(__file__)))
sys.path.append(path)
sys.path.append(path + "/crawler/")
sys.path.append(path + "/network/")
sys.path.append(path + "/service/")

from DocumentResponseSender import DocumentResponseSender
from service.GetDocumentService import GetDocumentService
from RequestParser import RequestParser

class DocumentRequestHandler:
    def __init__(self, port, service):
        self.port = port
        self.service = service
        self.response_sender = DocumentResponseSender()
        self.parser = RequestParser()
        self.host = 'localhost'

    def start(self):
        self.__listen()

    # 로그인
    def __login(self, request, client_socket):
        id, password = self.parser.get_pwid(request)
        result = self.service.login(id, password)
        if result:
            self.response_sender.success_login_response(client_socket)
        else:
            self.response_sender.failed_response(client_socket)

    # Server gets document list from web site
    def __getDocumentList(self, request, client_socket):
        type = self.parser.get_type(request)
        json_documentlist = self.service.getDocumentList(type)
        print("list download complete")
        if json_documentlist is not None:
            documentlist = self.parser.documentlist_parser(json_documentlist)
            self.response_sender.getDocumentList_response(client_socket, documentlist)
        else:
            self.response_sender.failed_response(client_socket)

    # Server gets document from web site
    def __getDocument(self, request, client_socket):
        course_name, document_name = self.parser.document_parser(request)
        documents = self.service.getDocument(course_name, document_name)
        document_path = documents.split("/")[:-1].strip()
        print("파일 다운로드: %s" % document_path)
        self.response_sender.getDocument_response(client_socket, document_path)



    def __listen(self):
        server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        server_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        server_socket.bind(('localhost', 3333))
        server_socket.listen()
        print('server listening...')
        while (True):

            client_soc, addr = server_socket.accept()
            print('connected client addr:', addr)

            data = client_soc.recv(10000)
            request = data.decode()

            if "HTTP/" in request:
                if "POST /login" in request:  # response for login
                    self.__login(request, client_soc)

                # response for getdocumentlist
                elif "GET /documentlist" in request:
                    self.__getDocumentList(request, client_soc)

                # response for getdocument
                elif "GET /document" in request:
                    self.__getDocument(request, client_soc)

            client_soc.close()


if __name__ == "__main__":
    port = 3333
    service = GetDocumentService()
    request_handler = DocumentRequestHandler(port, service)
    request_handler.start()
