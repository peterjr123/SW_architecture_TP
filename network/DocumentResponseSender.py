class DocumentResponseSender:

    def success_login_response(self, client_socket):
        response_msg = "HTTP/1.1 200 OK"
        client_socket.sendall(response_msg.encode(encoding="utf-8"))


    def getDocumentList_response(self, client_socket, responsebody):
        response_msg = "HTTP/1.1 200 OK"+'\r\n'
        response_msg +="Content-Length: "+str(len(responsebody))+"\r\n\r\n"
        response_msg += responsebody
        client_socket.sendall(response_msg.encode(encoding="utf-8"))

    def documentName_response(sel, client_socket, documentname):
        response_msg = documentname + '\r\n'
        client_socket.sendall(response_msg.encode(encoding="utf-8"))

    def failed_response(self, client_socket):
        response_msg = "HTTP/1.1 400 Bad Request"
        client_socket.sendall(response_msg.encode(encoding="utf-8"))
