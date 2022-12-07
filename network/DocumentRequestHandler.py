import socket
import os, sys

path = os.path.dirname(os.path.abspath(os.path.dirname(__file__)))
sys.path.append(path)
sys.path.append(path + "/crawler/")
sys.path.append(path + "/network/")
sys.path.append(path + "/service/")

from DocumentResponseSender import DocumentResponseSender
from service.GetDocumentService import GetDocumentService


class DocumentRequestHandler:
    def __init__(self, port, service):
        self.port = port
        self.service = service
        self.rs = DocumentResponseSender()
        self.host = 'localhost'

    def start(self):
        self.__listen()

    # 로그인
    def login(self, request, client_socket):
        request_body = request.split("\r\n\r\n")[1]
        id, pw = request_body.split("/")
        result = self.service.login(id, pw)
        if result:
            self.rs.success_login_response(client_socket)
        else:
            self.rs.failed_response(client_socket)

    # json string을 dictionary로 받아와서 문자열("course/material")로 변환
    def __documentlist_parser(self, json_documentlist):
        documentlist = ""
        for key, value in json_documentlist.items():
            if isinstance(value, list):
                for v in value:
                    documentlist += key + "/" + v + "\n"
            else:
                documentlist += key + "/" + value + "\n"

        document_list = documentlist[:-1]
        return document_list

    def getDocumentList(self, request, client_socket):
        type = request.split("\r\n\r\n")[1]
        json_documentlist = self.service.getDocumentList(type)
        if json_documentlist is not None:
            documentlist = self.__documentlist_parser(json_documentlist)
            self.rs.getDocumentList_response(client_socket, documentlist)
        else:
            self.rs.failed_response(client_socket)

    # missing_document를 list로 만듦.
    def __document_parser(self, request):
        documentlist = []
        request_body = request.split("\r\n\r\n")[1]
        data_list = request_body.split("\r\n")
        for data in data_list:
            print("data: " + data)
            documentlist.append(data)
        return documentlist

    def getDocument(self, request, client_socket):
        missing_document = self.__document_parser(request)
        documents = self.service.getDocument(missing_document)
        course_name = next(iter(documents))  # 과목명
        documents_path = documents[next(iter(documents))]  # 과목명에 대한 파일 경로 list
        print("파일 다운로드: %s" % documents_path)
        for document_path in documents_path:
            self.rs.getDocument_response(client_socket, document_path)

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
                    self.login(request, client_soc)

                # response for getdocumentlist
                elif "GET /documentlist" in request:
                    self.getDocumentList(request, client_soc)

                # response for getdocument
                elif "GET /document" in request:
                    self.getDocument(request, client_soc)

            client_soc.close()


if __name__ == "__main__":
    port = 3333
    service = GetDocumentService()
    request_handler = DocumentRequestHandler(port, service)
    request_handler.start()
