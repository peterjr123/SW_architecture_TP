import sys, os
from network.DocumentRequestHandler import DocumentRequestHandler



from crawler.LectureNoteDocCrawler import LectureNoteDocCrawler
class GetDocumentService:

    def __init__(self):
        self.id = "id"
        self.pwd = "pwd"
        self.document_crawler = LectureNoteDocCrawler(self.id, self.pwd)

    def login(self, id, pw):
        valid = self.document_crawler.validAccount(id, pw)
        if valid:
            return True
        else:
            print("id/pw is not valid")
            return False


    def getDocumentList(self, type):
        print("type: " + type)
        if type == "강의자료":
            print('get lecture document service start...')
            documentlist = self.document_crawler.getDocumentList()
            return documentlist

        # else type == "과제":


    def getDocument(self, missingDocument):
            path, filenames = self.document_crawler.getDocument(missingDocument)
            return path, filenames


if __name__ == "__main__":
    port = 3333
    service = GetDocumentService()
    request_handler = DocumentRequestHandler(port, service)
    request_handler.start()
    print(sys.path)

