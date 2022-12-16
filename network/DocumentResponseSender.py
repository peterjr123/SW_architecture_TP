class DocumentResponseSender:

    def success_login_response(self, client_socket):
        response_msg = "HTTP/1.1 200 OK"
        client_socket.sendall(response_msg.encode(encoding="utf-8"))
        print('success login message send!!')

    def getDocumentList_response(self, client_socket, responsebody):
        response_msg = "HTTP/1.1 200 OK"+'\r\n'
        response_msg +="Content-Length: "+str(len(responsebody))+"\r\n\r\n"
        response_msg += responsebody
        client_socket.sendall(response_msg.encode(encoding="utf-8"))

    def getDocument_response(self, client_socket, document_path):
        with open(document_path, 'rb') as f:
            try:
                data = f.read(1024)
                while data:
                    client_socket.send(data)
                    data = f.read(1024)
                print("파일 전송 완료: %s" % document_path)

            except Exception as ex:
                print(ex)

    def failed_response(self, client_socket):
        response_msg = "HTTP/1.1 400 Bad Request"
        client_socket.sendall(response_msg.encode(encoding="utf-8"))

