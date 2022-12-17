import sys, os
from crawler.LectureNoteDocCrawler import LectureNoteDocCrawler

class GetDocumentService:

    def __init__(self):
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
            filenames = self.document_crawler.downloadDocument(course_name, document_name)
            return filenames

