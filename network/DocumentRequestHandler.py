from DocumentResponseSender import DocumentResponseSender
from service.GetDocumentService import GetDocumentService

import socket


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
        json_documentlist = service.getDocumentList(type)
        if json_documentlist is not None:
            documentlist = self.__documentlist_parser(json_documentlist)
            self.rs.getDocumentList_response(client_socket, documentlist)
        else:
            self.rs.failed_response()

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

        return json_dict

    def getDocument(self, request, client_socket):
        # missing_document = self.__document_parser(request)
        # path, filenames = service.getDocument(missing_document)
        # ","07-JPattV1-Ch5-Partitioning Patterns V03-221011.pdf","Chapter3 Threads.key.pdf"
        path = "C:/Users/82103/Desktop/다운"
        filename = request.split("\r\n\r\n")[1]
        print(filename)
        # filenames = ["03-Architecture-Design Principles(15)-v1.pdf","Chapter3 Threads.key.pdf"]
        data_transferred = 0
        print("파일 %s 전송 시작" % filename)
        file_path = path + "/" + filename
        with open(file_path, 'rb') as f:
            try:
                data = f.read(1024)
                while data:
                    data_transferred += client_socket.send(data)
                    data = f.read(1024)
            except Exception as ex:
                print(ex)
        print("전송완료 %s, 전송량 %d" % (filename, data_transferred))
        # for filename in filenames:
        #     data_transferred = 0
        #     print("파일 %s 전송 시작" % filename)
        #     file_path = path + "/" + filename
        #     with open(file_path, 'rb') as f:
        #         try:
        #             data = f.read(1024)
        #             while data:
        #                 data_transferred += client_socket.send(data)
        #                 data = f.read(1024)
        #         except Exception as ex:
        #             print(ex)
        #     print("전송완료 %s, 전송량 %d" % (filename, data_transferred))

    def listen(self):
        server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        server_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        server_socket.bind(('localhost', 3333))
        server_socket.listen()
        print('server listening...')
        count = 0
        while (True):
            if count == 3:
                break
            client_soc, addr = server_socket.accept()
            print('connected client addr:', addr)

            data = client_soc.recv(10000)
            request = data.decode()

            count += 1
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
    request_handler.listen()
