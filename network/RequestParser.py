class RequestParser:

    def get_pwid(self, request):
        request_body = request.split("\r\n\r\n")[1]
        id, pw = request_body.split("/")
        return id, pw

    def get_type(self, request):
        type = request.split("\r\n\r\n")[1]
        return type

    # json을 dictionary로 받아와서 문자열("course/material")로 변환
    def documentlist_parser(self, json_documentlist):
            documentlist = ""
            for key, value in json_documentlist.items():
                if isinstance(value, list):
                    for v in value:
                        documentlist += key.strip() + "/" + v + "\n"
                else:
                    documentlist += key.strip() + "/" + value + "\n"

            document_list = documentlist[:-1]
            return document_list

    # missing_document를 list로 만듦.
    def document_parser(self, request):
        documentlist = []
        request_body = request.split("\r\n\r\n")[1]
        data_list = request_body.split("\r\n")
        for data in data_list:
            documentlist.append(data)
        return documentlist