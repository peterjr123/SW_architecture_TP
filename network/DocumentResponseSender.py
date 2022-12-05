class DocumentResponseSender:

    def setsuccessloginresponse(self, client_socket):
        response_msg = "HTTP/1.1 200 OK"
        client_socket.sendall(response_msg.encode(encoding="utf-8"))


    def setGetDocumentListResponse(self, client_socket, responsebody):
        response_msg = "HTTP/1.1 200 OK"+'\r\n'
        response_msg +="Content-Length: "+str(len(responsebody))+"\r\n\r\n";
        response_msg += responsebody
        client_socket.sendall(response_msg.encode(encoding="utf-8"))

    def setsuccessDocumentresponse(sel, client_socket, responsebody):
        response_msg = "HTTP/1.1 200 OK" + '\r\n'
        response_msg += "Content-Length: " + str(len(responsebody)) + "\r\n\r\n";
        response_msg += responsebody
        client_socket.sendall(response_msg.encode(encoding="utf-8"))

    def setFailedResponse(self, client_socket):
        response_msg = "HTTP/1.1 400 Bad Request"
        client_socket.sendall(response_msg.encode(encoding="utf-8"))
