from DocumentCrawler import DocumentCrawler as Crawler
from abc import *
from selenium.webdriver.common.by import By

class LectureNoteDocCrawler(Crawler):
    def __init__(self, id, pw):
        self.id = id
        self.pw = pw
        self.driver = None

    def createDocumentListByCrawling(self):
        submains = self.driver.find_elements(By.CLASS_NAME, 'submain-notice')
        for i in range(len(submains)):
            submainName = submains[i].find_element(By.CLASS_NAME, 'title').text
            if(submainName.find("강의자료")):
                print(submains[i])
                # submains[i].click()
                break
        return

crawler = LectureNoteDocCrawler("peterjr123", "peterjr123!")
crawler.installChromeDriver()
crawler.getDocumentList()