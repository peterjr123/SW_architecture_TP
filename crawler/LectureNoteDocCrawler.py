from DocumentCrawler import DocumentCrawler as Crawler
from abc import *
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from selenium.webdriver import ActionChains
import time


class LectureNoteDocCrawler(Crawler):
    def __init__(self, id, pw):
        self.id = id
        self.pw = pw
        self.driver = None

    def createDocumentListByCrawling(self):
        documentList = []

        # 과목 페이지에서 강의자료 리스트 페이지 접속
        isDocumentListPageExist = False
        submains = self.driver.find_elements(By.CLASS_NAME, 'submain-notice')
        for i in range(len(submains)):
            submainName = submains[i].find_element(By.CLASS_NAME, 'title').text
            if(submainName.find("강의자료") != -1):
                submains[i].find_element(By.XPATH, './/div[1]').click()
                isDocumentListPageExist = True
                break
        
        if(isDocumentListPageExist == False):
            return

        time.sleep(0.2)

        # 강의자료 리스트 페이지에서 각 강의자료 페이지에 접속후, 각 요소들을 가져옴.
        subPages = self.driver.find_elements(By.XPATH, '//td[@class="left"]')
        height = subPages[0].size['height']
        for subPage in subPages:
            self.driver.execute_script(f"window.scrollTo(0, {height});") # scrolling down을 안하면 클릭이 안되는 문제 발생
            height = height*2

            subPage.click()
            
            files = self.driver.find_elements(By.XPATH, '//a[@class="site-link"]')
            # time.sleep(0.2)
            for file in files:
                rawName = file.text
                fileName = rawName[rawName.find('-')+2 : rawName.rfind('(')-1]
                documentList.append(fileName)

            self.driver.back()
        
        return documentList

crawler = LectureNoteDocCrawler("peterjr123", "peterjr123!")
crawler.installChromeDriver()
crawler.getDocumentList()