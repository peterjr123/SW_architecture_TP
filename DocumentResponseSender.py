class DocumentResponseSender:

    def setsuccessloginresponse(self):
        response_msg = "HTTP/1.1 200 OK"
        return response_msg

    def setGetDocumentListResponse(self, responsebody):
        response_msg = "HTTP/1.1 200 OK"+'\r\n'
        response_msg +="Content-Length: "+str(len(responsebody))+"\r\n\r\n";
        response_msg += responsebody
        return response_msg

    def setsuccessDocumentresponse(self):
        response_msg = "HTTP/1.1 200 OK"
        return response_msg

    def setFailedResponse(self):
        response_msg = "HTTP/1.1 400 Bad Request"
        return response_msg