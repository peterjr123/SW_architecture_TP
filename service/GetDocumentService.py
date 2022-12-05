from crawler.LectureNoteDocCrawler import LectureNoteDocCrawler

class GetDocumentService:

    def __init__(self):
        self.id = "id"
        self.pwd = "pwd"
        self.document_crawler = LectureNoteDocCrawler()

    def login(self, id, pw):
        valid = self.document_crawler.validAccount(id,pw)
        if valid:
            return True
        else:
            print("id/pw is not valid")
            return False


    def getDocumentList(self, type):
        if type == "강의자료":
            documentlist = self.document_crawler.getDocumentList()
            return documentlist
        # else type == "과제":


    def getDocument(self, missingDocument):
            path = self.document_crawler.getDocument(missingDocument)
            return path
