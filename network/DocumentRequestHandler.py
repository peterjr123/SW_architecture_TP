from DocumentResponseSender import DocumentResponseSender
from service.GetDocumentService import GetDocumentService

import socket
import json


class DocumentRequestHandler:
    def __init__(self, port, service):
        self.port = port
        self.service = service
        self.rs = DocumentResponseSender()
        self.host = 'localhost'

    # 로그인
    def login(self, request, client_socket):
        request_body = request.split("\r\n\r\n")[1]
        id, pw = request_body.split("/")
        result = service.login(id, pw)
        if result:
            self.rs.setsuccessloginresponse(client_socket)
        else:
            self.rs.setFailedResponse(client_socket)

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
        json_documentlist = service.getDocumentList(type)
        if json_documentlist is not None:
            documentlist = self.__documentlist_parser(json_documentlist)
            self.rs.setGetDocumentListResponse(client_socket, documentlist)
        else:
            self.rs.setFailedResponse()

    # missing_document를 dictionary로 만듦.
    def __document_parser(self, request):
        json_dict = {}
        request_body = request.split("\r\n\r\n")[1]
        data_list = request_body.split("\r\n")
        for data in data_list:
            course, material = data.split("/")
            if course not in json_dict:
                json_dict[course] = material
            else:
                value = json_dict[course]
                if isinstance(value, list):
                    value.append(material)
                else:
                    values = [value, material]
                    json_dict[course] = values

        #json_string = json.dumps(json_dict)

        return json_dict

    def getDocument(self, request, client_socket):
        missing_document = self.__document_parser(request)
        path = service.getDocument(missing_document)
        if path is not None:
            response_msg = self.rs.setsuccessDocumentresponse(client_socket, path)
            client_socket.sendall(response_msg.encode(encoding="utf-8"))
        else:
            self.rs.setFailedResponse(client_socket)

    def listen(self):
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
                    break

            client_soc.close()


if __name__ == "__main__":
    port = 80
    service = GetDocumentService()
    request_handler = DocumentRequestHandler(port, service)
    request_handler.listen()
