import sys, os
from crawler.LectureNoteDocCrawler import LectureNoteDocCrawler

class GetDocumentService:

    def __init__(self):
        # TODO: Crawler의 id와 pw를 처리하는 방식 단일화 할 것.
        self.id = "id"
        self.password = "password"
        self.document_crawler = LectureNoteDocCrawler(self.id, self.password)

    def login(self, id, password):
        valid = self.document_crawler.validAccount(id, password)
        if valid:
            return True
        else:
            print("id/password is not valid")
            return False


    def getDocumentList(self, type):
        print("type: " + type)
        if type == "강의자료":
            print('get lecture document service start...')
            documentlist = self.document_crawler.getDocumentList()
            return documentlist

        # else type == "과제":


    def getDocument(self, course_name, document_name):
            filenames = self.document_crawler.downloadSingleDocument(course_name, document_name)
            return filenames

