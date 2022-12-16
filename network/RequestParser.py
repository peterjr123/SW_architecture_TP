class RequestParser:

    def get_pwid(request):
        request_body = request.split("\r\n\r\n")[1]
        id, pw = request_body.split("/")
        return id, pw

